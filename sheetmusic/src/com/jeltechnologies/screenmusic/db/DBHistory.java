package com.jeltechnologies.screenmusic.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.jeltechnologies.screenmusic.history.BookViews;
import com.jeltechnologies.screenmusic.history.PageView;
import com.jeltechnologies.screenmusic.history.PageViews;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.BookPage;

public class DBHistory extends DBCrud {

    public List<PageView> getHistory(String user, int limit) throws SQLException {
	ResultSet rs = null;
	try {
	    StringBuilder sql = new StringBuilder();

	    sql.append(
		    "SELECT h.moment, h.page, h.books_checksum, b.title as booktitle, b.artist as bookartist, p.title, p.artist,p.title2, p.artist2,p.description,p.text,");
	    sql.append(" (SELECT relativeFileName FROM files f WHERE f.books_checksum = h.books_checksum LIMIT 1) AS relativeFileName");
	    sql.append(
		    " FROM history h INNER JOIN books b ON b.checksum = h.books_checksum LEFT OUTER JOIN pages p ON p.books_checksum = b.checksum AND h.page = p.pagenumber");
	    sql.append(" WHERE username=?");
	    sql.append(" ORDER BY h.moment DESC");
	    sql.append(" LIMIT ?;");

	    PreparedStatement st = getStatement(sql.toString());
	    st.clearParameters();
	    st.setString(1, user);
	    st.setInt(2, limit);
	    rs = st.executeQuery();
	    List<PageView> views = new ArrayList<PageView>();
	    while (rs.next()) {
		String relativeFileName = rs.getString(12);
		if (relativeFileName != null) {
		    LocalDateTime moment = DBUtils.getLocalDateTime(rs, 1);
		    int pageNr = rs.getInt(2);
		    String checksum = rs.getString(3);
		    String bookTitle = rs.getString(4);
		    String bookArtist = rs.getString(5);
		    String pageTitle = rs.getString(6);
		    String pageArtist = rs.getString(7);
		    String pageTitle2 = rs.getString(8);
		    String pageArtist2 = rs.getString(9);
		    String pageDescription = rs.getString(10);
		    String pageText = rs.getString(11);
		    BookPage page = new BookPage();
		    page.setBookFileChecksum(checksum);
		    page.setNr(pageNr);
		    page.setTitle(pageTitle);
		    page.setArtist(pageArtist);
		    page.setTitle2(pageTitle2);
		    page.setArtist2(pageArtist2);
		    page.setDescription(pageDescription);
		    page.setText(pageText);
		    Book book = new Book();
		    book.setFileChecksum(checksum);
		    book.setRelativeFileName(relativeFileName);
		    book.setTitle(bookTitle);
		    book.setArtist(bookArtist);
		    PageView view = new PageView(book, page, moment);
		    views.add(view);
		}
	    }
	    return views;
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }

    public List<BookViews> getMostPopularBooks(String user, int top) throws SQLException {
	ResultSet rs = null;
	List<BookViews> result = new ArrayList<BookViews>();
	try {
	    String sql = "SELECT books_checksum, COUNT(h.moment) AS plays FROM history h WHERE h.username = ? GROUP BY books_checksum ORDER BY plays DESC LIMIT ?;";
	    PreparedStatement ps = getStatement(sql);
	    ps.clearParameters();
	    ps.setString(1, user);
	    ps.setInt(2, top);
	    rs = ps.executeQuery();
	    while (rs.next()) {
		String checksum = rs.getString(1);
		int plays = rs.getInt(2);
		BookViews bookViews = new BookViews();
		Book book = getBook(checksum);
		if (book != null) {
		    bookViews.setBook(book);
		    bookViews.setViews(plays);
		    result.add(bookViews);
		}
	    }
	    return result;
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }

    public List<PageViews> getMostPopularPages(String user, int top) throws SQLException {
	ResultSet rs = null;
	List<PageViews> result = new ArrayList<PageViews>();
	try {
	    String sql = "SELECT books_checksum, h.page, COUNT(h.moment) AS plays FROM history h WHERE h.username = ? GROUP BY books_checksum, h.page ORDER BY plays DESC LIMIT ?;";
	    PreparedStatement ps = getStatement(sql);
	    ps.clearParameters();
	    ps.setString(1, user);
	    ps.setInt(2, top);
	    rs = ps.executeQuery();
	    while (rs.next()) {
		String checksum = rs.getString(1);
		int page = rs.getInt(2);
		int plays = rs.getInt(3);
		PageViews pageViews = new PageViews();
		Book book = getBook(checksum);
		if (book != null) {
		    pageViews.setBook(book);
		    pageViews.setPage(page);
		    pageViews.setViews(plays);
		    result.add(pageViews);
		}
	    }
	    return result;
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }

}
