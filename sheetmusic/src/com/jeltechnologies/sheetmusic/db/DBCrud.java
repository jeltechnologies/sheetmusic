package com.jeltechnologies.sheetmusic.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.BookPage;
import com.jeltechnologies.sheetmusic.library.Category;

public class DBCrud extends Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBCrud.class);

    static String BOOK_PAGE_JOIN;

    static {
	StringBuilder s = new StringBuilder();
	s.append("SELECT");
	s.append(" checksum,");
	s.append(" nrOfPages,");
	s.append(" books.title,");
	s.append(" books.artist,");
	s.append(" books.description,");
	s.append(" books.series,");
	s.append(" pagenumber,");
	s.append(" pages.artist,");
	s.append(" pages.title,");
	s.append(" pages.artist2,");
	s.append(" pages.title2,");
	s.append(" pages.description,");
	s.append(" text ");
	s.append("FROM Books LEFT OUTER JOIN Pages ON Books.checksum = Pages.books_checksum");
	BOOK_PAGE_JOIN = s.toString();
    }

    protected static final String BOOK_PAGE_FILE_JOIN;

    static {
	StringBuilder s = new StringBuilder();
	s.append("SELECT ");
	s.append("  checksum,");
	s.append("  relativeFileName,");
	s.append("  nrOfPages,");
	s.append("  books.title,");
	s.append("  books.artist,");
	s.append("  books.description,");
	s.append("  books.series,");
	s.append("  dateModified,");
	s.append("  size,");
	s.append("  pagenumber,");
	s.append("  pages.artist,");
	s.append("  pages.title,");
	s.append("  pages.artist2,");
	s.append("  pages.title2,");
	s.append("  pages.description,");
	s.append("  text ");
	s.append("FROM");
	s.append("  Books");
	s.append("  LEFT OUTER JOIN Files on");
	s.append("    Books.checksum = Files.books_checksum");
	s.append("  LEFT OUTER JOIN Pages on");
	s.append("    Books.checksum = Pages.books_checksum ");
	BOOK_PAGE_FILE_JOIN = s.toString();
    }

    protected static final String BOOK_PAGES_FILES_JOIN = BOOK_PAGE_JOIN + "LEFT OUTER JOIN Files on Books.checksum = Files.books_checksum";

    public void addBook(Book book) throws SQLException {
	PreparedStatement addBookStatement = getStatement("INSERT INTO Books (checksum, nrOfPages, title, artist, description,series) VALUES (?,?,?,?,?,?)");

	addBookStatement.clearParameters();
	addBookStatement.setString(1, book.getFileChecksum());
	addBookStatement.setInt(2, book.getNrOfPages());
	addBookStatement.setString(3, book.getTitle());
	addBookStatement.setString(4, book.getArtist());
	addBookStatement.setString(5, book.getDescription());
	addBookStatement.setString(6, book.getSeries());

	int rows = addBookStatement.executeUpdate();
	if (rows != 1) {
	    LOGGER.warn("Add book resulted in " + rows + " rows but should be 1");
	} else {
	    updateBookTitlesAndDescriptions(book);
	}

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Added book to database: " + book.getFileChecksum());
	}
    }

    public void updatCategoriesInBook(Book book) throws SQLException {
	String checksum = book.getFileChecksum();
	PreparedStatement s = getStatement("SELECT categories_name FROM bookscategories WHERE books_checksum=? ORDER BY categories_name");
	ResultSet r = null;
	try {
	    s.clearParameters();
	    s.setString(1, checksum);
	    r = s.executeQuery();
	    List<Category> categories = new ArrayList<Category>();
	    while (r.next()) {
		Category category = new Category();
		category.setName(r.getString(1));
		categories.add(category);
	    }
	    book.setCategories(categories);
	} finally {
	    close(r);
	}
    }

    public List<Category> getAllCategories() throws SQLException {
	List<Category> result = new ArrayList<Category>();
	ResultSet r = null;
	try {
	    PreparedStatement s = getStatement("SELECT name FROM categories ORDER BY name");
	    s.clearParameters();
	    r = s.executeQuery();
	    while (r.next()) {
		Category category = new Category();
		category.setName(r.getString(1));
		result.add(category);
	    }
	    return result;
	} finally {
	    close(r);
	}
    }

    public void addFile(Book book) throws SQLException {
	deleteFile(book.getRelativeFileName());

	PreparedStatement addFileStatement = getStatement("INSERT INTO Files (relativeFileName, books_checksum,  dateModified, size) VALUES (?,?,?,?);");
	addFileStatement.clearParameters();
	addFileStatement.setString(1, book.getRelativeFileName());
	addFileStatement.setString(2, book.getFileChecksum());
	DBUtils.setTimestamp(addFileStatement, 3, book.getFileLastModified());
	addFileStatement.setLong(4, book.getFileSize());
	int rowsFile = addFileStatement.executeUpdate();
	if (rowsFile != 1) {
	    LOGGER.warn("Add files resulted in " + rowsFile + " rows but should be 1");
	}

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Added book file to database: " + book.getRelativeFileName());
	}
    }

    public List<String> getAllFiles() throws SQLException {
	List<String> names = new ArrayList<String>();
	PreparedStatement getRelativeFileNamesStatement = getStatement("SELECT relativeFileName FROM Files;");
	ResultSet r = null;
	try {
	    r = getRelativeFileNamesStatement.executeQuery();
	    while (r.next()) {
		names.add(r.getString(1));
	    }
	    return names;
	} finally {
	    DBUtils.close(r);
	}
    }

    private void setBookPages(Book book, Map<Integer, BookPage> pages) {
	List<BookPage> bookPages = new ArrayList<BookPage>();
	for (int page = 1; page <= book.getNrOfPages(); page++) {
	    BookPage bookPage = pages.get(page);
	    if (bookPage == null) {
		bookPage = new BookPage();
		bookPage.setNr(page);
	    }
	    bookPages.add(bookPage);
	}
	book.setPages(bookPages);
    }

    public boolean isExistingBookId(String id) throws SQLException {
	String sql = "SELECT checksum FROM books WHERE checksum=?";
	PreparedStatement st = getStatement(sql);
	st.clearParameters();
	st.setString(1, id);
	ResultSet r = null;
	try {
	    r = st.executeQuery();
	    return r.next();
	} finally {
	    close(r);
	}
    }

    public Book getBook(String id) throws SQLException {
	String sql = BOOK_PAGE_JOIN + " WHERE checksum=? AND checksum IN (SELECT books_checksum FROM files WHERE books_checksum=?)";
	PreparedStatement st = getStatement(sql);
	st.clearParameters();
	st.setString(1, id);
	st.setString(2, id);
	ResultSet rs = null;
	Book book = null;
	try {
	    rs = st.executeQuery();
	    Map<Integer, BookPage> pages = new HashMap<Integer, BookPage>();
	    while (rs.next()) {
		int c = 1;
		if (book == null) {
		    book = new Book();
		    book.setFileChecksum(rs.getString(c));
		    c++;
		    int nrOfPages = rs.getInt(c);
		    book.setNrOfPages(nrOfPages);
		    c++;
		    book.setTitle(rs.getString(c));
		    c++;
		    book.setArtist(rs.getString(c));
		    c++;
		    book.setDescription(rs.getString(c));
		    c++;
		    book.setSeries(rs.getString(c));
		    c++;
		} else {
		    c = 7;
		}
		BookPage page = new BookPage();
		int nr = rs.getInt(c);
		pages.put(nr, page);
		page.setNr(nr);
		c++;
		page.setArtist(rs.getString(c));
		c++;
		page.setTitle(rs.getString(c));
		c++;
		page.setArtist2(rs.getString(c));
		c++;
		page.setTitle2(rs.getString(c));
		c++;
		page.setDescription(rs.getString(c));
		c++;
		page.setText(rs.getString(c));
		c++;
	    }
	    if (book != null) {
		setBookPages(book, pages);
	    }
	    return book;
	} finally {
	    close(rs);
	}
    }

    public List<String> getBookFiles(String bookId) throws SQLException {
	String sql = "SELECT relativefilename FROM files WHERE books_checksum=?";
	PreparedStatement st = getStatement(sql);
	st.clearParameters();
	st.setString(1, bookId);
	ResultSet rs = null;
	List<String> files = new ArrayList<>();
	try {
	    rs = st.executeQuery();
	    while (rs.next()) {
		files.add(rs.getString(1));
	    }
	} finally {
	    close(rs);
	}
	return files;
    }

    public Book getBookByFileName(String relativeFileName) throws SQLException {
	String sql = BOOK_PAGE_FILE_JOIN + " WHERE relativeFileName = ?;";
	PreparedStatement s = getStatement(sql);
	ResultSet r = null;
	try {
	    s.clearParameters();
	    s.setString(1, relativeFileName);
	    r = s.executeQuery();
	    Book book = null;
	    Map<Integer, BookPage> pages = new HashMap<Integer, BookPage>();
	    boolean firstRow = true;
	    while (r.next()) {
		if (firstRow) {
		    firstRow = false;
		    book = new Book();
		    book.setFileChecksum(r.getString(1));
		    book.setRelativeFileName(r.getString(2));
		    book.setNrOfPages(r.getInt(3));
		    book.setTitle(r.getString(4));
		    book.setArtist(r.getString(5));
		    book.setDescription(r.getString(6));
		    book.setSeries(r.getString(7));
		    book.setFileLastModified(DBUtils.getDateTime(r, 8));
		    book.setFileSize(r.getLong(9));
		}
		BookPage page = new BookPage();
		int pageNumber = r.getInt(10);

		if (pageNumber > 0) {
		    page.setBookFileChecksum(book.getFileChecksum());
		    page.setNr(pageNumber);
		    page.setArtist(r.getString(11));
		    page.setTitle(r.getString(12));
		    page.setArtist2(r.getString(13));
		    page.setTitle2(r.getString(14));
		    page.setDescription(r.getString(15));
		    page.setText(r.getString(16));
		    pages.put(pageNumber, page);
		}
	    }
	    if (book != null) {
		setBookPages(book, pages);
	    }
	    return book;
	} finally {
	    close(r);
	}
    }

    public List<Book> getFilesWithChecksum(String checksum) throws SQLException {
	ResultSet r = null;
	List<Book> books = new ArrayList<Book>();
	try {
	    PreparedStatement getBookCopiesByChecksumStatement = getStatement(
		    "SELECT checksum, relativeFileName, nrOfPages, title, artist ,description, dateModified, size, series FROM Books LEFT OUTER JOIN Files ON Books.checksum = Files.books_checksum WHERE checksum = ?");
	    getBookCopiesByChecksumStatement.clearParameters();
	    getBookCopiesByChecksumStatement.setString(1, checksum);
	    r = getBookCopiesByChecksumStatement.executeQuery();
	    while (r.next()) {
		Book book = new Book();
		books.add(book);
		book.setFileChecksum(r.getString(1));
		book.setRelativeFileName(r.getString(2));
		book.setNrOfPages(r.getInt(3));
		book.setTitle(r.getString(4));
		book.setArtist(r.getString(5));
		book.setDescription(r.getString(6));
		book.setFileLastModified(DBUtils.getDateTime(r, 7));
		book.setFileSize(r.getLong(8));
		book.setSeries(r.getString(9));
	    }
	    return books;
	} finally {
	    DBUtils.close(r);
	}
    }

    public void deleteFile(String relativeFileName) throws SQLException {
	PreparedStatement deleteFileStatement = getStatement("DELETE FROM Files WHERE relativeFileName = ?");
	deleteFileStatement.clearParameters();
	deleteFileStatement.setString(1, relativeFileName);
	deleteFileStatement.executeUpdate();
    }

    public List<String> getRelativeFilesForBook(String checksum) throws SQLException {
	List<String> files = new ArrayList<String>();
	ResultSet r = null;
	try {
	    PreparedStatement getFavoritesStatement = getStatement("SELECT relativeFileName FROM Files WHERE books_checksum=?");
	    getFavoritesStatement.clearParameters();
	    getFavoritesStatement.setString(1, checksum);
	    r = getFavoritesStatement.executeQuery();
	    while (r.next()) {
		files.add(r.getString(1));
	    }
	} finally {
	    DBUtils.close(r);
	}
	return files;
    }

    
    public LocalDateTime getLastStoredHistory(String userName) throws SQLException {
	ResultSet rs = null;
	try {
	    String sql = "SELECT MAX(moment) FROM History WHERE username=?";
	    PreparedStatement st = getStatement(sql);
	    st.clearParameters();
	    st.setString(1, userName);
	    rs = st.executeQuery();
	    LocalDateTime result;
	    if (rs.next()) {
		result = DBUtils.getLocalDateTime(rs, 1);
	    } else {
		result = null;
	    }
	    return result;
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }

    public void addHistory(String userName, String id, int page) throws SQLException {
	PreparedStatement insertHistoryStatement = getStatement("INSERT INTO History (username, moment, books_checksum, page) VALUES (?,?,?,?);");
	Book book = getBook(id);
	if (book == null) {
	    LOGGER.error("Cannot add history, because no book not found for id " + id);
	} else {
	    Date moment = new Date();
	    insertHistoryStatement.clearParameters();
	    insertHistoryStatement.setString(1, userName);
	    DBUtils.setTimestamp(insertHistoryStatement, 2, moment);
	    insertHistoryStatement.setString(3, book.getFileChecksum());
	    insertHistoryStatement.setInt(4, page);
	    insertHistoryStatement.execute();
	    commit();
	}
    }

   
    public void updateBookTitlesAndDescriptions(Book book) throws SQLException {
	PreparedStatement updateBookTitle = getStatement("UPDATE Books SET title=?, artist=?, description=?, series=? WHERE checksum=?");
	updateBookTitle.clearParameters();
	updateBookTitle.setString(1, book.getTitle());
	updateBookTitle.setString(2, book.getArtist());
	updateBookTitle.setString(3, book.getDescription());
	updateBookTitle.setString(4, book.getSeries());
	updateBookTitle.setString(5, book.getFileChecksum());
	updateBookTitle.executeUpdate();

	List<BookPage> pages = book.getPages();
	if (pages != null) {
	    PreparedStatement deleteBookPages = getStatement("DELETE FROM Pages WHERE books_checksum=?");
	    deleteBookPages.clearParameters();
	    deleteBookPages.setString(1, book.getFileChecksum());
	    int deletedPages = deleteBookPages.executeUpdate();
	    if (LOGGER.isDebugEnabled() && deletedPages > 0) {
		LOGGER.debug("Deleted " + deletedPages + " pages");
	    }

	    for (BookPage page : pages) {
		boolean contents;
		contents = page.getDescription() != null && !page.getDescription().trim().isEmpty();
		if (!contents) {
		    contents = page.getText() != null && !page.getText().trim().isEmpty();
		}
		if (!contents) {
		    contents = page.getTitle() != null && !page.getTitle().trim().isEmpty();
		}

		PreparedStatement addBookPage = null;
		if (contents) {
		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inserting into database: " + page.toString());
		    }
		    if (addBookPage == null) {
			addBookPage = getStatement(
				"INSERT INTO Pages(books_checksum, pagenumber, artist, title, artist2, title2, description, text) VALUES (?,?,?,?,?,?,?,?)");
		    }
		    addBookPage.clearParameters();
		    addBookPage.setString(1, book.getFileChecksum());
		    addBookPage.setInt(2, page.getNr());
		    addBookPage.setString(3, page.getArtist());
		    addBookPage.setString(4, page.getTitle());
		    addBookPage.setString(5, page.getArtist2());
		    addBookPage.setString(6, page.getTitle2());
		    addBookPage.setString(7, page.getDescription());
		    addBookPage.setString(8, page.getText());
		    addBookPage.executeUpdate();
		}
	    }
	    updateBookCategories(book);
	}
    }

    private void updateBookCategories(Book book) throws SQLException {
	try {
	    PreparedStatement delOldCatPs = getStatement("DELETE FROM bookscategories WHERE books_checksum = ?");
	    delOldCatPs.clearParameters();
	    delOldCatPs.setString(1, book.getFileChecksum());
	    delOldCatPs.executeUpdate();

	    List<Category> categories = book.getCategories();
	    if (categories != null && !categories.isEmpty()) {

		List<Category> allCategories = getAllCategories();
		List<Category> newCats = new ArrayList<Category>();
		for (Category cat : categories) {
		    Category found = null;
		    Iterator<Category> iterator = allCategories.iterator();
		    while (iterator.hasNext() && found == null) {
			Category current = iterator.next();
			if (current.getName().equalsIgnoreCase(cat.getName())) {
			    found = current;
			}
		    }
		    if (found == null) {
			newCats.add(cat);
		    }
		}

		if (!newCats.isEmpty()) {
		    PreparedStatement st = getStatement("INSERT INTO categories(name) VALUES (?)");
		    for (Category newCat : newCats) {
			st.clearParameters();
			st.setString(1, newCat.getName());
			st.executeUpdate();
		    }
		}

		PreparedStatement stAddCatToBook = getStatement("INSERT INTO bookscategories (categories_name, books_checksum) VALUES (?, ?)");
		for (Category bookCategory : categories) {
		    stAddCatToBook.clearParameters();
		    stAddCatToBook.setString(1, bookCategory.getName());
		    stAddCatToBook.setString(2, book.getFileChecksum());
		    stAddCatToBook.executeUpdate();
		}
	    }
	} catch (SQLException e) {
	    LOGGER.warn("Cannot update book categories", e);
	    rollback();
	    throw e;
	}
    }

    public void deleteBookFile(String relativeFileName) throws SQLException {
	String sql = "DELETE FROM Files WHERE relativeFileName=?;";
	PreparedStatement st = getStatement(sql);
	st.clearParameters();
	st.setString(1, relativeFileName);
	int rows = st.executeUpdate();
	if (rows != 1) {
	    LOGGER.warn("Unexpected rows " + rows + " when deleting file " + relativeFileName);
	}
    }

    public void updateChecksum(String oldChecksum, String newChecksum) throws SQLException {
	String sql = "UPDATE BOOKS SET checksum=? WHERE checksum=?;";
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("UPDATE BOOKS SET checksum=" + newChecksum + " WHERE checksum=" + oldChecksum + ";");
	}

	PreparedStatement st = getStatement(sql);
	st.clearParameters();
	st.setString(1, newChecksum);
	st.setString(2, oldChecksum);
	int rows = st.executeUpdate();
	if (rows != 1) {
	    LOGGER.warn("Unexpected rows " + rows + " when updating checksum of book with checksum " + oldChecksum);
	}
    }

    public List<Book> getAllBooks() throws SQLException {
	List<Book> books = new ArrayList<Book>();
	ResultSet r = null;
	try {
	    PreparedStatement ps = getStatement(
		    "SELECT checksum, relativeFileName, nrOfPages, title, artist ,description, dateModified, size, series FROM Books INNER JOIN Files ON Books.checksum = Files.books_checksum");
	    ps.clearParameters();
	    r = ps.executeQuery();
	    while (r.next()) {
		Book book = new Book();
		books.add(book);
		book.setFileChecksum(r.getString(1));
		book.setRelativeFileName(r.getString(2));
		book.setNrOfPages(r.getInt(3));
		book.setTitle(r.getString(4));
		book.setArtist(r.getString(5));
		book.setDescription(r.getString(6));
		book.setFileLastModified(DBUtils.getDateTime(r, 7));
		book.setFileSize(r.getLong(8));
		book.setSeries(r.getString(9));
	    }
	    return books;
	} finally {
	    DBUtils.close(r);
	}
    }

}
