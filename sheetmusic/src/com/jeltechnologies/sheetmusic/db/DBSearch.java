package com.jeltechnologies.sheetmusic.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.jsonpayloads.ArtistBooks;
import com.jeltechnologies.sheetmusic.jsonpayloads.ArtistBooksAndSongs;
import com.jeltechnologies.sheetmusic.jsonpayloads.ArtistsInfoList;
import com.jeltechnologies.sheetmusic.jsonpayloads.CategoriesAndBooks;
import com.jeltechnologies.sheetmusic.jsonpayloads.Song;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.BookPage;
import com.jeltechnologies.sheetmusic.search.SearchResult;
import com.jeltechnologies.sheetmusic.search.SearchResults;

public class DBSearch extends DBCrud {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBSearch.class);

    public SearchResults search(String word) throws SQLException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Search: " + word);
	}
	SearchResults results = new SearchResults(word);
	ResultSet rsPages = null;
	ResultSet rsBooks = null;

	try {
	    StringBuilder s = new StringBuilder();
	    s.append("select p.pagenumber, p.title, p.artist, p.title2, p.artist2, p.books_checksum,b.title, b.artist, b.nrofpages ");
	    s.append("from");
	    s.append(" pages p inner join files f on p.books_checksum = f.books_checksum inner join books b on f.books_checksum = b.checksum ");
	    s.append("where");
	    s.append(" p.title ilike ?");
	    s.append(" or p.artist ilike ?");
	    s.append(" or p.artist2 ilike ?");
	    s.append(" or p.title2 ilike ? ");
	    s.append("order by b.checksum ");
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(s.toString());
	    }
	    PreparedStatement searchInPagesStatement = getStatement(s.toString());
	    searchInPagesStatement.clearParameters();
	    String searchLike = "%" + word + "%";
	    for (int p = 1; p <= 4; p++) {
		searchInPagesStatement.setString(p, searchLike);
	    }

	    rsPages = searchInPagesStatement.executeQuery();
	    int rows = 0;
	    while (rsPages.next()) {
		rows++;
		int pageNumber = rsPages.getInt(1);
		String pageTitle = rsPages.getString(2);
		String pageArtist = rsPages.getString(3);
		String pageTitle2 = rsPages.getString(4);
		String pageArtist2 = rsPages.getString(5);
		String checksum = rsPages.getString(6);
		BookPage page = new BookPage();
		page.setBookFileChecksum(checksum);
		page.setNr(pageNumber);
		page.setTitle(pageTitle);
		page.setArtist(pageArtist);
		page.setTitle2(pageTitle2);
		page.setArtist2(pageArtist2);
		if (pageNumber > 0) {
		    results.add(new SearchResult(word, page.getLabel(), checksum, pageNumber));
		}
	    }

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(" => Found rows: " + rows + ", books in pages: " + results.size());
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append("select b.title, b.artist, b.checksum, b.nrofpages ");
	    sb.append("from books b inner join files f on b.checksum = f.books_checksum ");
	    sb.append("where b.title ilike ? ");
	    sb.append(" or b.artist  ilike ? ");
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(sb.toString());
	    }
	    PreparedStatement searchInBooksSatement = getStatement(sb.toString());
	    searchInBooksSatement.clearParameters();
	    for (int p = 1; p <= 2; p++) {
		searchInBooksSatement.setString(p, searchLike);
	    }
	    rsBooks = searchInBooksSatement.executeQuery();
	    int nrOfBooksFound = 0;
	    final int FIRST_PAGE = 1;
	    while (rsBooks.next()) {
		nrOfBooksFound++;
		String title = rsBooks.getString(1);
		String artist = rsBooks.getString(2);
		String checksum = rsBooks.getString(3);
		int nrOfPages = rsBooks.getInt(4);

		if (!results.containsBook(checksum)) {
		    Book book = new Book();
		    book.setFileChecksum(checksum);
		    book.setArtist(artist);
		    book.setTitle(title);
		    book.setNrOfPages(nrOfPages);
		    results.add(new SearchResult(word, book.getLabel(), checksum, FIRST_PAGE));
		}
	    }
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(" => Found books in filenames : " + nrOfBooksFound);
	    }

	} finally {
	    DBUtils.close(rsPages);
	    DBUtils.close(rsBooks);
	}
	return results;
    }

    public List<ArtistBooks> getArtistBooks() throws SQLException {
	List<ArtistBooks> result = new ArrayList<ArtistBooks>();
	Map<String, ArtistBooks> map = new HashMap<String, ArtistBooks>();

	StringBuilder b = new StringBuilder();

	b.append("SELECT b.artist, b.checksum, COUNT(p.books_checksum) AS songs ");
	b.append("FROM books b INNER JOIN pages p ON b.checksum = p.books_checksum ");
	b.append("WHERE");
	b.append(" b.artist IS NOT NULL AND b.artist <> '' AND");
	b.append(" b.checksum IN (SELECT books_checksum FROM files) ");
	b.append("GROUP BY b.artist,b.checksum,b.nrofpages ");
	b.append("ORDER by artist");

	ResultSet rs = null;
	ResultSet rsPages = null;
	try {
	    PreparedStatement st = getStatement(b.toString());
	    st.clearParameters();
	    rs = st.executeQuery();
	    while (rs.next()) {
		String artist = rs.getString(1);
		String checksum = rs.getString(2);
		int songs = rs.getInt(3);
		String artistLower = artist.toLowerCase();
		ArtistBooks artistBooks = map.get(artistLower);
		if (artistBooks == null) {
		    artistBooks = new ArtistBooks();
		    artistBooks.setArtist(artist);
		    result.add(artistBooks);
		    map.put(artistLower, artistBooks);
		}
		artistBooks.add(checksum);
		artistBooks.add(songs);
	    }

	    StringBuilder bp = new StringBuilder();
	    bp.append("(SELECT p1.artist, p1.pagenumber, p1.books_checksum");
	    bp.append(" FROM pages p1 INNER JOIN files f1 on f1.books_checksum = p1.books_checksum");
	    bp.append(" WHERE p1.artist IS NOT NULL AND p1.artist<> '')");
	    bp.append(" UNION ");
	    bp.append("(SELECT p2.artist2, p2.pagenumber, p2.books_checksum ");
	    bp.append("FROM pages p2 INNER JOIN files f2 on f2.books_checksum = p2.books_checksum ");
	    bp.append("WHERE p2.artist2 IS NOT NULL AND p2.artist2<> '' )");

	    PreparedStatement stBp = getStatement(bp.toString());
	    rsPages = stBp.executeQuery();

	    while (rsPages.next()) {
		String artist = rsPages.getString(1);
		int page = rsPages.getInt(2);
		String checksum = rsPages.getString(3);

		String artistLower = artist.toLowerCase();
		ArtistBooks artistBooks = map.get(artistLower);
		if (artistBooks == null) {
		    artistBooks = new ArtistBooks();
		    artistBooks.setArtist(artist);
		    result.add(artistBooks);
		    map.put(artistLower, artistBooks);
		}

		Song song = new Song();
		song.setFileChecksum(checksum);
		song.setPageNr(page);
		artistBooks.add(song);
	    }

	    return result;

	} finally {
	    close(rs);
	    close(rsPages);
	}
    }

    public ArtistsInfoList getArtistsBooksAndSongs() throws SQLException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getArtistsBooksAndSongs");
	}

	ResultSet rs = null;
	ResultSet rsSongs = null;

	StringBuilder s = new StringBuilder();
	s.append("SELECT artist, title, nrofpages,checksum ");
	s.append("FROM books b INNER JOIN files f ON b.checksum = f.books_checksum ");
	s.append("WHERE artist IS NOT NULL AND artist <> '' ");
	s.append("ORDER by artist");

	try {
	    PreparedStatement ps = getStatement(s.toString());
	    rs = ps.executeQuery();
	    Map<String, ArtistBooksAndSongs> map = new HashMap<String, ArtistBooksAndSongs>();
	    while (rs.next()) {
		String artist = rs.getString(1);
		String title = rs.getString(2);
		int nrOfPages = rs.getInt(3);
		String checksum = rs.getString(4);
		Book b = new Book();
		b.setArtist(artist);
		b.setTitle(title);
		b.setFileChecksum(checksum);
		b.setNrOfPages(nrOfPages);
		String artistLower = artist.toLowerCase();
		ArtistBooksAndSongs abs = map.get(artistLower);
		if (abs == null) {
		    abs = new ArtistBooksAndSongs();
		    abs.setArtist(artist);
		    map.put(artistLower, abs);
		}
		abs.add(b);
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append("SELECT p1.books_checksum,p1.pagenumber,p1.artist, p1.title ");
	    sb.append("FROM pages p1 INNER JOIN files f1 ON p1.books_checksum = f1.books_checksum ");
	    sb.append("WHERE p1.artist IS NOT NULL AND p1.artist<>'' ");
	    sb.append("UNION ");
	    sb.append("SELECT p2.books_checksum,p2.pagenumber,p2.artist, p2.title2 ");
	    sb.append("FROM pages p2 INNER JOIN files f2 ON p2.books_checksum = f2.books_checksum ");
	    sb.append("WHERE p2.artist2 IS NOT NULL AND p2.artist2<>''");

	    PreparedStatement psSongs = getStatement(sb.toString());
	    rsSongs = psSongs.executeQuery();
	    while (rsSongs.next()) {
		String checksum = rsSongs.getString(1);
		int pageNr = rsSongs.getInt(2);
		String artist = rsSongs.getString(3);
		if (artist != null && !artist.isBlank()) {
		    String title = rsSongs.getString(4);
		    String artistLower = artist.toLowerCase();
		    // System.out.println(checksum);
		    ArtistBooksAndSongs abs = map.get(artistLower);
		    if (abs == null) {
			abs = new ArtistBooksAndSongs();
			abs.setArtist(artist);
			map.put(artistLower, abs);
		    }
		    Song song = new Song();
		    song.setFileChecksum(checksum);
		    song.setPageNr(pageNr);
		    song.setTitle(title);
		    abs.add(song);
		}
	    }

	    ArtistsInfoList result = new ArtistsInfoList();

	    for (String artist : map.keySet()) {
		ArtistBooksAndSongs abs = map.get(artist);
		result.getArtists().add(abs);
	    }

	    return result;

	} finally {
	    close(rs);
	    close(rsSongs);
	}
    }

    public ArtistBooksAndSongs getArtistBooksAndSongs(String artist) throws SQLException {
	ArtistBooksAndSongs result = new ArtistBooksAndSongs();
	result.setArtist(artist);
	String artistLower = artist.toLowerCase();

	ResultSet rsChecksum = null;
	ResultSet rsPages = null;
	try {
	    StringBuilder s = new StringBuilder();
	    s.append("SELECT checksum ");
	    s.append("FROM books b ");
	    s.append("WHERE LOWER(artist) =?");
	    s.append(" AND b.checksum IN (SELECT books_checksum FROM files) ");
	    PreparedStatement checksumStatement = getStatement(s.toString());
	    checksumStatement.clearParameters();
	    checksumStatement.setString(1, artistLower);
	    rsChecksum = checksumStatement.executeQuery();
	    while (rsChecksum.next()) {
		String checksum = rsChecksum.getString(1);
		Book book = getBook(checksum);
		if (book != null) {
		    result.add(book);
		}
	    }

	    StringBuilder pb = new StringBuilder();
	    pb.append("SELECT p.books_checksum, p.pagenumber, p.artist, p.title, p.artist2, p.title2, p.description, f.relativeFileName ");
	    pb.append("FROM Pages p INNER JOIN books b ON p.books_checksum = b.checksum");
	    pb.append(" INNER JOIN Files f ON b.checksum = f.books_checksum ");
	    pb.append("WHERE (b.artist IS NULL OR b.artist = '')");
	    pb.append(" AND (LOWER(p.artist) = ? OR LOWER(p.artist2) = ?)");
	    PreparedStatement stPages = getStatement(pb.toString());
	    stPages.clearParameters();
	    stPages.setString(1, artistLower);
	    stPages.setString(2, artistLower);

	    rsPages = stPages.executeQuery();
	    while (rsPages.next()) {
		String checksum = rsPages.getString(1);
		int pageNumber = rsPages.getInt(2);
		String pageArtist = rsPages.getString(3);
		String title = rsPages.getString(4);
		String pageArtist2 = rsPages.getString(5);
		String title2 = rsPages.getString(6);
		String description = rsPages.getString(7);
		String relativeFileName = rsPages.getString(8);

		BookPage page = new BookPage();
		page.setBookFileChecksum(checksum);
		page.setNr(pageNumber);
		page.setArtist(pageArtist);
		page.setArtist2(pageArtist2);
		page.setTitle(title);
		page.setTitle2(title2);
		page.setDescription(description);

		if (!page.isBlank()) {
		    Song song = new Song();
		    result.add(song);
		    song.setFileChecksum(checksum);
		    song.setRelativeFileName(relativeFileName);
		    song.setPageNr(pageNumber);
		    if (pageArtist != null && pageArtist.equalsIgnoreCase(artist)) {
			song.setArtist(pageArtist);
			song.setTitle(title);
		    } else {
			if (pageArtist2 != null && pageArtist2.equalsIgnoreCase(artist)) {
			    song.setArtist(pageArtist2);
			    song.setTitle(title2);
			}
		    }
		    if (song.getTitle() == null || song.getTitle().isBlank()) {
			song.setTitle(title);
		    }
		    if (song.getTitle() == null || song.getTitle().isBlank()) {
			song.setTitle(title2);
		    }
		}
	    }
	} finally {
	    close(rsChecksum);
	    close(rsPages);

	}
	return result;
    }

    public CategoriesAndBooks getCategoriesAndBooks() throws SQLException {
	ResultSet rs = null;
	try {
	    StringBuilder s = new StringBuilder();

	    s.append("SELECT bc.categories_name, bc.books_checksum, b.artist, b.title, b.nrofpages, ");
	    s.append("(SELECT f.relativefilename FROM files f WHERE f.books_checksum = b.checksum LIMIT 1) ");
	    s.append("FROM bookscategories bc ");
	    s.append("INNER JOIN books b ON bc.books_checksum = b.checksum ");
	    s.append("INNER JOIN files f2 ON f2.books_checksum = b.checksum ");
	    s.append("ORDER BY bc.categories_name,b.artist, b.title ");

	    PreparedStatement st = getStatement(s.toString());
	    st.clearParameters();
	    rs = st.executeQuery();
	    CategoriesAndBooks result = new CategoriesAndBooks();
	    while (rs.next()) {
		String checksum = rs.getString(2);
		String categoryName = rs.getString(1);
		String artist = rs.getString(3);
		String title = rs.getString(4);
		int nrOfPages = rs.getInt(5);
		String relativeFileName = rs.getString(6);
		Book b = new Book();
		b.setFileChecksum(checksum);
		b.setTitle(title);
		b.setArtist(artist);
		b.setNrOfPages(nrOfPages);
		b.setRelativeFileName(relativeFileName);
		if (LOGGER.isTraceEnabled()) {
		    LOGGER.trace("Category: " + categoryName + ": title: " + title);
		}
		result.add(categoryName, b);
	    }
	    return result;
	} finally {
	    close(rs);
	}
    }

    public List<String> getSeries() throws SQLException {
	List<String> series = new ArrayList<String>();
	ResultSet rs = null;
	try {
	    String sql = "SELECT DISTINCT series FROM books WHERE series IS NOT null AND series <> '' AND checksum IN (SELECT books_checksum FROM files) ORDER BY series";
	    PreparedStatement st = getStatement(sql);
	    rs = st.executeQuery();
	    while (rs.next()) {
		String name = rs.getString(1);
		series.add(name);
	    }
	} finally {
	    close(rs);
	}
	return series;
    }

    public List<Book> getBooksInSeries(String series) throws SQLException {
	List<Book> books = new ArrayList<Book>();
	ResultSet rs = null;
	try {
	    StringBuilder b = new StringBuilder();
	    b.append("SELECT b.checksum, b.artist, b.title, b.nrofpages, ");
	    b.append(" (SELECT f.relativefilename FROM files f WHERE f.books_checksum = b.checksum LIMIT 1) ");
	    b.append("FROM books b INNER JOIN files f2 ON f2.books_checksum = b.checksum ");
	    b.append("WHERE b.series = ?;");
	    PreparedStatement st = getStatement(b.toString());
	    st.clearParameters();
	    st.setString(1, series);
	    rs = st.executeQuery();
	    while (rs.next()) {
		String checksum = rs.getString(1);
		String artist = rs.getString(2);
		String title = rs.getString(3);
		int nrOfPages = rs.getInt(4);
		String relativeFile = rs.getString(5);
		Book book = new Book();
		book.setFileChecksum(checksum);
		book.setArtist(artist);
		book.setTitle(title);
		book.setNrOfPages(nrOfPages);
		book.setRelativeFileName(relativeFile);
		books.add(book);
	    }
	    return books;
	} finally {
	    close(rs);
	}
    }

    public List<String> getAllSearchableText() throws SQLException {
	int nrOfColumnsInSelect = 9;
	StringBuilder s = new StringBuilder();
	s.append("select");
	s.append(" b.title as booktitle,");
	s.append(" b.artist as bookartist,");
	s.append(" b.description as bookdescription,");
	s.append(" b.series as bookseries,");
	s.append(" p.title as pagetitle,");
	s.append(" p.artist as pageartist,");
	s.append(" p.title2 as pagetitle2,");
	s.append(" p.artist2 as pageartist2,");
	s.append(" p.description as pagedescription");
	s.append(" from books b");
	s.append(" inner join files f on b.checksum  = f.books_checksum");
	s.append(" left outer join pages p on b.checksum  = p.books_checksum");
	String sql = s.toString();
	PreparedStatement getAllBooks = getStatement(sql);
	getAllBooks.clearParameters();
	ResultSet r = null;
	try {
	    r = getAllBooks.executeQuery();
	    Set<String> wordSetLowerCase = new HashSet<String>();
	    List<String> wordsNormalCase = new ArrayList<String>();
	    while (r.next()) {
		for (int columnIndex = 1; columnIndex <= nrOfColumnsInSelect; columnIndex++) {
		    String word = r.getString(columnIndex);
		    if (word != null && !word.equals("")) {
			String wordLower = word.toLowerCase();
			if (!wordSetLowerCase.contains(wordLower)) {
			    wordSetLowerCase.add(wordLower);
			    wordsNormalCase.add(word);
			}
		    }
		}
	    }
	    Collections.sort(wordsNormalCase);
	    return wordsNormalCase;
	} finally {
	    if (r != null) {
		r.close();
	    }
	}
    }

    public List<Book> getAllSongBooks(int minimumPages) throws SQLException {
	List<Book> books = new ArrayList<Book>();
	boolean hasMinimumPages = minimumPages > 0;
	ResultSet rs = null;
	try {
	    StringBuilder b = new StringBuilder();
	    b.append("SELECT b.checksum, b.artist, b.title, b.nrofpages, ");
	    b.append(" (SELECT f.relativefilename FROM files f WHERE f.books_checksum = b.checksum LIMIT 1) ");
	    b.append("FROM books b INNER JOIN files f2 ON f2.books_checksum = b.checksum ");
	    b.append("WHERE b.artist <> '' ");
	    if (hasMinimumPages) {
		b.append("AND b.nrofpages >= ? ");
	    }
	    PreparedStatement st = getStatement(b.toString());
	    st.clearParameters();
	    if (hasMinimumPages) {
		st.setInt(1, minimumPages);
	    }
	    rs = st.executeQuery();
	    while (rs.next()) {
		String checksum = rs.getString(1);
		String artist = rs.getString(2);
		String title = rs.getString(3);
		int nrOfPages = rs.getInt(4);
		String relativeFile = rs.getString(5);
		Book book = new Book();
		book.setFileChecksum(checksum);
		book.setArtist(artist);
		book.setTitle(title);
		book.setNrOfPages(nrOfPages);
		book.setRelativeFileName(relativeFile);
		books.add(book);
	    }
	    return books;
	} finally {
	    close(rs);
	}
    }
    
    public int getNrOfBooks() throws SQLException {
	int result = -1;
	String sqlBooks = "SELECT COUNT(DISTINCT(books_checksum)) FROM files f";
	PreparedStatement st = getStatement(sqlBooks);
	ResultSet rs = null;
	try {
	    rs = st.executeQuery();
	    if (rs.next()) {
		result = rs.getInt(1);
	    }
	    return result;
	}
	finally {
	    close(rs);
	}
    }
    
    public int getNrOfPages() throws SQLException {
	int result = -1;
	String sql = """
		SELECT sum(b.nrofpages)
	        FROM books b WHERE b.checksum IN 
		  (SELECT books_checksum FROM Files f);
		  """;
	PreparedStatement st = getStatement(sql);
	ResultSet rs = null;
	try {
	    rs = st.executeQuery();
	    if (rs.next()) {
		result = rs.getInt(1);
	    }
	    return result;
	}
	finally {
	    close(rs);
	}
    }
    
    

}
