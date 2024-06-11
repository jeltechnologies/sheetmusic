package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.config.Configuration;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.servlet.ThreadService;
import com.jeltechnologies.utils.FileUtils;

public class BookImageCache {
    private final static Logger LOGGER = LoggerFactory.getLogger(BookImageCache.class);
    
    private final ThreadService threadService;
    
    public BookImageCache(ThreadService threadService) {
	this.threadService = threadService;
    }
    
    public void delete(String checksum) {
	File cacheFolderExtracted = Configuration.getInstance().storage().getCacheFolderExtracted();
	String absoluteFolderName = cacheFolderExtracted.getAbsolutePath() + "/" + checksum;
	File folder = new File(absoluteFolderName);
	File large = new File(folder.getAbsolutePath() + "/large");
	tryToDeleteFolder(large);
	File medium = new File(folder.getAbsolutePath() + "/medium");
	tryToDeleteFolder(medium);
	File small = new File(folder.getAbsolutePath() + "/small");
	tryToDeleteFolder(small);
    }

    private void tryToDeleteFolder(File folder) {
	if (folder.isDirectory()) {
	    File[] filesInFolder = folder.listFiles();
	    boolean ok = true;

	    for (File file : filesInFolder) {
		if (LOGGER.isTraceEnabled()) {
		    LOGGER.trace("Deleting file: " + file);
		}
		ok = file.delete();
		if (!ok) {
		    LOGGER.error("Cannot delete file " + file);
		}
	    }
	    if (ok) {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Deleting folder: " + folder);
		}
		ok = folder.delete();
		if (!ok) {
		    LOGGER.error("Cannot delete folder " + folder);
		}
	    }
	}
    }

    public void updateChecksum(Book book, String newchecksum) throws IOException {
	File cacheFolderExtracted = Configuration.getInstance().storage().getCacheFolderExtracted();
	String oldFolder = cacheFolderExtracted.getAbsolutePath() + "/" + book.getFileChecksum();
	String newFolder = cacheFolderExtracted.getAbsolutePath() + "/" + newchecksum;
	File from = new File(oldFolder);
	File to = new File(newFolder);
	FileUtils.copyDirectory(from, to, true);

	threadService.schedule(new Runnable() {
	    @Override
	    public void run() {
		File[] files = from.listFiles();
		for (File file : files) {
		    boolean fileDeleted = file.delete();
		    if (!fileDeleted) {
			LOGGER.warn("Could not delete file " + file.getAbsolutePath());
		    }
		}
		boolean deleteFolderOk = from.delete();
		if (!deleteFolderOk) {
		    LOGGER.warn("Could not delete original cache folder " + from);
		} else {
		    if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Removed cache folder " + from + " because the PDF file was changed");
		    }
		}
	    }
	}, 5, TimeUnit.MINUTES);

    }

}
