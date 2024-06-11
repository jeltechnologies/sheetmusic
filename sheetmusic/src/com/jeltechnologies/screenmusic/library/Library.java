package com.jeltechnologies.screenmusic.library;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.config.Configuration;
import com.jeltechnologies.screenmusic.db.DBCrud;
import com.jeltechnologies.screenmusic.db.DBSearch;
import com.jeltechnologies.screenmusic.extractedfilestorage.BlockList;
import com.jeltechnologies.screenmusic.extractedfilestorage.BookImageCache;
import com.jeltechnologies.screenmusic.extractedfilestorage.IndexProducer;
import com.jeltechnologies.screenmusic.jsonpayloads.ArtistBooksAndSongs;
import com.jeltechnologies.screenmusic.jsonpayloads.ArtistsInfoList;
import com.jeltechnologies.screenmusic.jsonpayloads.CategoriesAndBooks;
import com.jeltechnologies.screenmusic.jsonpayloads.SortType;
import com.jeltechnologies.screenmusic.search.SearchResults;
import com.jeltechnologies.screenmusic.servlet.MusicFoldersFileFilter;
import com.jeltechnologies.screenmusic.servlet.MusicSheetFilesFilter;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;
import com.jeltechnologies.screenmusic.statistics.LibraryStatistics;
import com.jeltechnologies.utils.FileUtils;
import com.jeltechnologies.utils.StringUtils;

public class Library {
    private static final Logger LOGGER = LoggerFactory.getLogger(Library.class);
    private final BlockList blacklist;
    private static final int MAX_RANDOM_BOOKS = 255;
    private static final int SONGBOOKS_MINIMUM_PAGES = 10;
    private final User user;
    private final ScreenMusicContext context;
    //private final static File CACHE_FOLDER_DELETED = Environment.getInstance().getConfiguration().storage().getCacheFolderDeleted();

    public Library(User user, ScreenMusicContext context) {
	this.user = user;
	this.context = context;
	blacklist = new BlockList(user);
    }

    public Book getBook(String checksum) {
	DBCrud db = null;
	Book book = null;
	try {
	    db = new DBCrud();
	    book = db.getBook(checksum);
	} catch (SQLException sqlException) {
	    LOGGER.error("Cannot get book with checksum " + checksum, sqlException);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return book;
    }

    public Book getBookWithFileName(String checksum) {
	DBCrud db = null;
	Book book = null;
	try {
	    db = new DBCrud();
	    book = db.getBook(checksum);
	    if (book != null) {
		List<String> files = getBookFiles(book);
		if (files != null && !files.isEmpty()) {
		    book.setRelativeFileName(files.get(0));
		} else {
		    book = null;
		}
	    }
	} catch (SQLException sqlException) {
	    LOGGER.error("Cannot get book with checksum " + checksum, sqlException);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return book;
    }

    public Book getBookWithCategories(String checksum) {
	DBCrud db = null;
	Book book = null;
	try {
	    db = new DBCrud();
	    book = db.getBook(checksum);
	    if (book != null) {
		if (book != null) {
		    db.updatCategoriesInBook(book);
		}
	    }
	} catch (SQLException sqlException) {
	    LOGGER.error("Cannot get book with checksum " + checksum, sqlException);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return book;
    }

    public Book getBookByFile(File file) {
	return getBookByFile(file, false);
    }

    public List<String> getBookFiles(Book book) {
	DBCrud db = null;
	List<String> files = null;
	try {
	    db = new DBCrud();
	    files = db.getBookFiles(book.getFileChecksum());
	} catch (SQLException sqlException) {
	    LOGGER.error("Cannot get files with id " + book.getFileChecksum(), sqlException);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return files;
    }

    public Book getBookByFile(File file, boolean includeCategories) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getBook('" + file.getAbsolutePath() + "'");
	}
	Book book = null;
	if (!blacklist.contains(file)) {
	    book = getBookFromDatabase(file, includeCategories);
	} else {
	    LOGGER.warn("Book was blacklisted: " + file);
	}
	return book;
    }

    public Book getBookByFileName(String relativeFileName) {
	return getBookByFileName(relativeFileName, false);
    }

    public Book getBookByFileName(String relativeFileName, boolean includeCategories) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getBook('" + relativeFileName + "'");
	}
	File file = user.getFile(relativeFileName);
	return getBookByFile(file, includeCategories);
    }

    public String getPageLabel(Book book, int pageFrom, int pageTo) {
	Book bookAndPages;
	if (book.getFileChecksum() != null && !book.getFileChecksum().isBlank()) {
	    bookAndPages = getBook(book.getFileChecksum());
	} else {
	    bookAndPages = getBookByFileName(book.getRelativeFileName());
	}
	BookPage found = null;
	Iterator<BookPage> pagesIterator = bookAndPages.getPages().iterator();
	while (found == null && pagesIterator.hasNext()) {
	    BookPage current = pagesIterator.next();
	    if (current.getNr() == pageFrom) {
		found = current;
	    }
	}
	String result;
	if (found != null && !found.getLabel().trim().isEmpty()) {
	    result = found.getLabel();
	} else {
	    StringBuilder b = new StringBuilder(book.getLabel());
	    if (pageFrom > 0) {
		b.append(" - ").append(pageFrom);
		if (pageTo > 0) {
		    b.append(" - ").append(pageTo);
		}
	    }
	    result = b.toString();
	}
	return result;
    }

    public boolean containsBook(String checksum) {
	boolean result;
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    Book book = db.getBook(checksum);
	    result = book != null;
	} catch (SQLException e) {
	    LOGGER.error("Cannot get books with checksum " + checksum, e);
	    result = false;
	} finally {
	    close(db);
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("containsBook('" + checksum + "') => " + result);
	}
	return result;
    }

    private void close(DBCrud db) {
	if (db != null) {
	    db.close();
	}
    }

    private Book getBookFromDatabase(File file, boolean includeCategories) {
	String relativeFileName = user.getRelativeFileName(file);
	Book book = null;
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    book = db.getBookByFileName(relativeFileName);
	    if (book != null && (file.length() != book.getFileSize())) {
		// the user replaced the file with a different file, we return null because it
		// is a different book
		LOGGER.info("Book file " + relativeFileName + " changed, will now update the database");
		book = null;
	    } else {
		if (includeCategories) {
		    if (book != null) {
			db.updatCategoriesInBook(book);
		    }
		}
	    }
	} catch (SQLException exception) {
	    LOGGER.warn("Cannot get book with relativeFileName " + relativeFileName, exception);
	} finally {
	    close(db);
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("getBookFromDatabase (" + relativeFileName + ") => " + book);
	}
	return book;
    }

    public List<String> getAllRelativeFiles() {
	List<String> result;
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    result = db.getAllFiles();
	} catch (SQLException exception) {
	    LOGGER.error("Cannot get all relative files", exception);
	    result = new ArrayList<String>();
	} finally {
	    close(db);
	}
	return result;
    }

    public void deleteFile(String relativeFileName) {
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    db.deleteFile(relativeFileName);
	    File file = user.getFile(relativeFileName);
	    if (file.exists()) {
		File cacheFolderDeleted = Configuration.getInstance().storage().getCacheFolderDeleted();
		String toName = cacheFolderDeleted.getAbsolutePath() + "/" + file.getName();
		File to = new File(toName);
		FileUtils.moveFile(file, to, true);
	    }
	} catch (SQLException exception) {
	    LOGGER.error("Cannot deleteFile " + relativeFileName, exception);
	} catch (IOException e) {
	    LOGGER.warn("Could not move file to recycle bin: " + relativeFileName);
	} finally {
	    close(db);
	}
    }

    public SearchResults search(String word) throws InterruptedException {
	SearchResults results;
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    results = db.search(word);
	} catch (SQLException exception) {
	    LOGGER.error("Cannot search in database for " + word, exception);
	    results = new SearchResults(word);
	} finally {
	    close(db);
	}
	return results;
    }

    public LibraryStatistics getLibraryStatistics() {
	DBSearch db = null;
	int books;
	int pages;
	try {
	    db = new DBSearch();
	    books = db.getNrOfBooks();
	    pages = db.getNrOfPages();
	} catch (SQLException e) {
	    LOGGER.error("Cannot get library statstics", e);
	    books = -1;
	    pages = -1;
	} finally {
	    close(db);
	}
	return new LibraryStatistics(books, pages);
    }

    public List<Book> getBooksFromFolder(String relativeFolderName) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getBooksFromFolder " + relativeFolderName);
	}
	List<Book> booksInFolder = new ArrayList<>();
	File folder = user.getFile(relativeFolderName);

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("folder: " + folder.getAbsolutePath());
	}
	File[] sheetFiles = folder.listFiles(new MusicSheetFilesFilter());

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("sheetFiles: " + sheetFiles.length);
	}
	booksInFolder = new ArrayList<Book>(sheetFiles.length);
	List<File> missingFiles = new ArrayList<>();
	for (File sheetFile : sheetFiles) {
	    Book book = getBookByFile(sheetFile);
	    if (book != null) {
		booksInFolder.add(book);
	    } else {
		missingFiles.add(sheetFile);
	    }
	}
	if (!missingFiles.isEmpty()) {
	    IndexProducer producer = new IndexProducer(user, context);
	    producer.setFilesToIndex(missingFiles);
	    context.getThreadService().execute(producer);
	}
	Collections.sort(booksInFolder);
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(booksInFolder.toString());
	}
	return booksInFolder;
    }

    public List<Folder> getFoldersInFolder(String relativeFolderName) {
	File folder = user.getFile(relativeFolderName);
	File[] foldersInFolder = folder.listFiles(new MusicFoldersFileFilter());
	List<Folder> folders = new ArrayList<Folder>(foldersInFolder.length);
	for (File folderInFolder : foldersInFolder) {
	    Folder f = new Folder();
	    f.setTitle(folderInFolder.getName());
	    f.setPath(user.getRelativeFileName(folderInFolder));
	    File[] sheetMusicFiles = folderInFolder.listFiles(new MusicSheetFilesFilter());
	    f.setFiles(sheetMusicFiles.length);
	    folders.add(f);
	}
	return folders;
    }

    public List<String> getAllSearchableText() {
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    return db.getAllSearchableText();
	} catch (SQLException e) {
	    LOGGER.warn("Cannot get searchable index", e);
	    return new ArrayList<String>();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}

    }

    public void moveBook(Book book, String toRelativeFolderName) throws IOException {
	String relativeFilename = book.getRelativeFileName();
	String localName;
	if (relativeFilename.indexOf("/") > -1) {
	    localName = StringUtils.stripBeforeLast(relativeFilename, "/");
	} else {
	    localName = relativeFilename;
	}
	File destination = user.getFile(toRelativeFolderName + "/" + localName);

	DBCrud db = null;
	try {
	    File from = user.getFile(book.getRelativeFileName());
	    LOGGER.info("Moving " + from.getAbsolutePath() + " to " + destination.getAbsolutePath());
	    FileUtils.moveFile(from.getAbsolutePath(), destination.getAbsolutePath(), true, false);
	    db = new DBCrud();
	    db.deleteFile(book.getRelativeFileName());
	    String destinationRelativeFileName = user.getRelativeFileName(destination);
	    book.setRelativeFileName(destinationRelativeFileName);
	    db.addFile(book);
	} catch (SQLException e) {
	    LOGGER.warn("Cannot move book", e);
	    throw new IOException("Cannot move book " + book.getRelativeFileName() + " to " + toRelativeFolderName, e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    public String updateBook(Book book) throws IOException {
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    db.updateBookTitlesAndDescriptions(book);
	    String relFile = db.getBookFiles(book.getFileChecksum()).get(0);
	    String id = book.getFileChecksum();
	    if (relFile.toLowerCase().endsWith(".pdf")) {
		List<Book> bookCopies = db.getFilesWithChecksum(book.getFileChecksum());
		List<File> fileCopies = new ArrayList<File>(bookCopies.size());
		for (Book copy : bookCopies) {
		    fileCopies.add(user.getFile(copy.getRelativeFileName()));
		}
		id = updateBookGetChecksum(db, book, fileCopies);
	    }
	    return id;
	} catch (SQLException | InterruptedException e) {
	    try {
		db.rollback();
	    } catch (SQLException e1) {
		LOGGER.warn("Cannot rollback transaction");
	    }
	    throw new IOException("Cannot update book " + book.getRelativeFileName() + " because " + e.getMessage(), e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    private String updateBookGetChecksum(DBCrud db, Book book, List<File> fileCopies) throws IOException, InterruptedException, SQLException {
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("Updating book " + book.getLabel());
	}
	SheetMusicPdfFileParser pdfParser = null;
	File tmpFile;
	String oldChecksum = book.getFileChecksum();
	String newChecksum;
	File pdfFile = fileCopies.get(0);
	try {
	    pdfParser = new SheetMusicPdfFileParser(pdfFile);

	    String tempDir = System.getProperty("java.io.tmpdir");
	    tmpFile = new File(tempDir + "/" + UUID.randomUUID().toString() + ".pdf");
	    pdfParser.save(tmpFile, book);

	    newChecksum = FileUtils.createMD5Checksum(tmpFile);
	    for (File fileCopy : fileCopies) {
		FileUtils.copyFile(tmpFile, fileCopy, true);
	    }

	    boolean deleted = tmpFile.delete();
	    if (!deleted) {
		LOGGER.warn("Could not delete temp file " + tmpFile.getAbsolutePath());
	    }
	    db.updateChecksum(oldChecksum, newChecksum);
	    db.commit();

	    new BookImageCache(context.getThreadService()).updateChecksum(book, newChecksum);
	    if (!fileCopies.isEmpty()) {
		IndexProducer producer = new IndexProducer(user, context);
		for (File fileCopy : fileCopies) {
		    producer.handleFile(fileCopy);
		}
	    }
	    return newChecksum;
	} catch (IOException e) {
	    db.rollback();
	    throw new IOException("Error saving " + pdfFile.getAbsolutePath() + ", storing book " + book, e);
	} finally {
	    if (pdfParser != null) {
		pdfParser.close();
	    }
	}
    }

    public List<Category> getAllCategories() {
	List<Category> categories = new ArrayList<>();
	DBCrud db = null;
	try {
	    db = new DBCrud();
	    categories = db.getAllCategories();
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all categories: " + e.getMessage());
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return categories;
    }

    public CategoriesAndBooks getAllCategoriesAndBooks() {
	CategoriesAndBooks categories;
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    categories = db.getCategoriesAndBooks();
	    categories.sort();
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all categories and books: " + e.getMessage());
	    categories = new CategoriesAndBooks();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return categories;
    }

    public ArtistsInfoList getArtistsBooksAndSongs() {
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    return db.getArtistsBooksAndSongs();
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all artists books and songs: " + e.getMessage());
	    return new ArtistsInfoList();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    public ArtistBooksAndSongs getArtistBooksAndSongs(String name) {
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    return db.getArtistBooksAndSongs(name);
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all artists books and songs for " + name + ". " + e.getMessage());
	    return new ArtistBooksAndSongs();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    public List<Book> getAllSongBooks(SortType sortType) {
	List<Book> books;
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    books = db.getAllSongBooks(SONGBOOKS_MINIMUM_PAGES);
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all artists books and songs: " + e.getMessage());
	    books = new ArrayList<Book>();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	books = sortBooks(books, sortType);
	return books;
    }

    private List<Book> sortBooks(List<Book> books, SortType sortType) {
	if (sortType == SortType.RANDOM) {
	    Random random = new Random();
	    List<Book> randomBooks = new ArrayList<Book>();
	    int expectedLoops = books.size();
	    for (int loop = 0; loop < expectedLoops && randomBooks.size() < MAX_RANDOM_BOOKS && !books.isEmpty(); loop++) {
		int nextRandom = random.nextInt(books.size());
		Book book = books.get(nextRandom);
		books.remove(nextRandom);
		randomBooks.add(book);
	    }
	    books = randomBooks;
	} else {
	    Collections.sort(books, new Comparator<Book>() {
		@Override
		public int compare(Book o1, Book o2) {
		    int result;
		    switch (sortType) {
			case A_Z:
			    result = o1.getLabel().compareTo(o2.getLabel());
			    break;
			case Z_A:
			    result = o2.getLabel().compareTo(o1.getLabel());
			    break;
			default:
			    result = 0;
			    break;
		    }
		    return result;
		}
	    });
	}
	return books;
    }

    public List<String> getSeries() {
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    return db.getSeries();
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all series. " + e.getMessage());
	    return new ArrayList<String>();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    public List<Book> getBooksInSeries(String series) {
	DBSearch db = null;
	try {
	    db = new DBSearch();
	    List<Book> books = db.getBooksInSeries(series);
	    Collections.sort(books, new Comparator<Book>() {
		@Override
		public int compare(Book o1, Book o2) {
		    return o1.getLabel().compareTo(o2.getLabel());
		}
	    });
	    return books;
	} catch (SQLException e) {
	    LOGGER.warn("Could not get all books in series " + series + ". " + e.getMessage());
	    return new ArrayList<Book>();
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

}
