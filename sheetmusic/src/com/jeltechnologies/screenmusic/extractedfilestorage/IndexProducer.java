package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.BookFilesFinder;
import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.config.Configuration;
import com.jeltechnologies.screenmusic.db.DBCrud;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.library.SheetMusicFileParser;
import com.jeltechnologies.screenmusic.library.SheetMusicPdfFileParser;
import com.jeltechnologies.screenmusic.library.SheetMusicPngJpgParser;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;
import com.jeltechnologies.utils.FileUtils;

public class IndexProducer implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexProducer.class);

    private List<File> filesToIndex = null;

    private boolean tryToRedoBlankThumbs = false;
    
    private final User user;
    
    private final ScreenMusicContext context;

    public IndexProducer(User user, ScreenMusicContext context) {
	this.user = user;
	this.context = context;
    }

    public void setFilesToIndex(List<File> filesToIndex) {
	this.filesToIndex = filesToIndex;
    }
    
    public boolean isTryToRedoBlankThumbs() {
        return tryToRedoBlankThumbs;
    }

    /**
     * Apply fix for classpath Error in ImageIO classloading that caused blank images
     */
    public void setTryToRedoBlankThumbs(boolean tryToRedoBlankThumbs) {
        this.tryToRedoBlankThumbs = tryToRedoBlankThumbs;
    }

    public void run() {
	String threadName = this.getClass().getSimpleName();
	Thread.currentThread().setName(threadName);
	LOGGER.info("Started thread " + threadName);
	try {
	    produceAllPdfFiles();
	} catch (InterruptedException e) {
	    LOGGER.debug("Interrupted");
	} catch (Throwable t) {
	    LOGGER.error("Error when producing");
	}
	LOGGER.info("Ended thread " + threadName);
    }

    private void produceAllPdfFiles() throws InterruptedException {
	BlockList blacklist = new BlockList(user);
	List<File> allFiles;

	if (filesToIndex == null) {
	    allFiles = new BookFilesFinder(user.getSheetMusicFolder()).getAllBookFiles();
	} else {
	    allFiles = filesToIndex;
	}

	for (File file : allFiles) {
	    if (!blacklist.contains(file) && file.isFile()) {
		if (Thread.interrupted()) {
		    throw new InterruptedException();
		}
		try {
		    handleFile(file);
		} catch (Throwable t) {
		    LOGGER.error("Error " + t.getMessage() + "with file [" + file + "] now adding to blacklist");
		    blacklist.add(file);
		}
	    }
	}
    }

    public void handleFile(File file) throws IOException, InterruptedException {
	Library library = new Library(user, context);
	Book book = library.getBookByFile(file);

	if (book == null) {
	    SheetMusicFileParser parser = null;
	    try {
		if (BookFilesFinder.isPdfFile(file)) {
		    parser = new SheetMusicPdfFileParser(file);
		} else {
		    parser = new SheetMusicPngJpgParser(user, file);
		}
		book = parser.getBook();
		book.setRelativeFileName(user.getRelativeFileName(file));
	    } finally {
		if (parser != null) {
		    parser.close();
		}
	    }
	    book.trim();
	    if (book != null) {
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("Adding book to library: " + book.getRelativeFileName());
		}
		book.setFileLastModified(new Date(file.lastModified()));
		String checksum = FileUtils.createMD5Checksum(file);
		book.setFileChecksum(checksum);
		addBook(book);
	    }
	} else {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Book " + book.getLabel() + " was found already in db");
	    }
	}
	if (book != null) {
	    File cacheFolderExtracted = Configuration.getInstance().storage().getCacheFolderExtracted();
	    List<Thumbnail> thumbnails = ThumbnailsFactory.getExpectedThumbs(book, cacheFolderExtracted);
	    List<Thumbnail> missing = new ArrayList<Thumbnail>();
	    boolean bookContainsBlankImages = false;
	    for (Thumbnail thumb : thumbnails) {
		File cachedFile = thumb.getCachedFile();
		if (!cachedFile.exists()) {
		    missing.add(thumb);
		} else {
		    if (tryToRedoBlankThumbs) {
			long s = cachedFile.length();
			boolean blankImage = false;
			switch (thumb.getSize()) {
			    case LARGE: {
				blankImage = s < 56000;
				break;
			    }
			    case MEDIUM: {
				blankImage = s < 16000;
				break;
			    }
			    case SMALL: {
				blankImage = s < 2000;
				break;
			    }
			}
			if (blankImage) {
			    missing.add(thumb);
			    bookContainsBlankImages = true;
			}
		    }
		}
	    }
	    if (bookContainsBlankImages) {
		LOGGER.warn("Blank thumbs found in [" + book.getLabel() + "] => Will do a new attempt to recreate these thumbs");
	    }
	    if (!missing.isEmpty()) {
		ThumbnailsExtractTask task = new ThumbnailsExtractTask(book, missing);
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("Missing thumbnails: " + task);
		}
		context.getThumbnailsQueue().add(task);
	    }
	}
    }

    private static void addBook(Book book) throws InterruptedException {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Adding book " + book);
	}
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    boolean existingBook = db.isExistingBookId(book.getFileChecksum());
	    if (!existingBook) {
		db.addBook(book);
	    } else {
		LOGGER.info("Found a copy of an earlier added book " + book.getTitle());
		db.updateBookTitlesAndDescriptions(book);
	    }
	    db.addFile(book);
	} catch (SQLException e) {
	    LOGGER.error("Cannot add book to database: ", e);
	    throw new IllegalStateException("Cannot add book to database: " + book, e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Adding book completed" + book);
	}
    }

}
