package com.jeltechnologies.sheetmusic.favorites.pages;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.db.DBCrud;
import com.jeltechnologies.sheetmusic.db.DBUtils;
import com.jeltechnologies.sheetmusic.db.ResultSetIterator;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.BookPage;

import jakarta.ws.rs.NotFoundException;

public class DataAccessObject extends DBCrud {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessObject.class);

    private static String FAV_QUERY = "SELECT f.username, f.userlabel, f.position, f.page, f.books_checksum, p.artist, p.title, p.artist2, p.title2, p.description, p.text FROM favoritepages f LEFT OUTER JOIN pages p ON f.page = p.pagenumber AND p.books_checksum=f.books_checksum WHERE f.username=? ";

    public List<FavoritePage> getAll(String userName) throws SQLException {
	ResultSetIterator rs = null;
	List<FavoritePage> favorites = new ArrayList<FavoritePage>();
	try {
	    String sql = FAV_QUERY + "ORDER BY position";
	    LOGGER.debug("User: " + userName + " => " + sql);
	    PreparedStatement st = getStatement(sql);
	    st.setString(1, userName);
	    rs = new ResultSetIterator(st);
	    while (rs.next()) {
		FavoritePage f = parseFavorite(rs);
		favorites.add(f);
	    }
	    return favorites;
	} finally {
	    close(rs);
	}
    }

    public FavoritePage getFavorite(String userName, String bookId, int pageNumber) throws SQLException {
	ResultSetIterator rs = null;
	FavoritePage f = null;
	try {
	    String sql = FAV_QUERY + "AND f.books_checksum=? AND f.page=? ORDER BY position";
	    LOGGER.debug("User: " + userName + " => " + sql);
	    PreparedStatement st = getStatement(sql);
	    st.setString(1, userName);
	    st.setString(2, bookId);
	    st.setInt(3, pageNumber);
	    rs = new ResultSetIterator(st);
	    if (rs.next()) {
		f = parseFavorite(rs);
	    }
	    return f;
	} finally {
	    close(rs);
	}
    }
    
    private FavoritePage parseFavorite(ResultSetIterator rs) throws SQLException {
	FavoritePage f = new FavoritePage();
	f.setBookId(rs.getNextString());
	f.setUserLabel(rs.getNextString());
	f.setPosition(rs.getNextInt());
	f.setPageNumber(rs.getNextInt());
	f.setFavorite(true);
	BookPage page = new BookPage();
	f.setBookPage(page);
	
	// p.books_checksum, 
	page.setBookFileChecksum(rs.getNextString());
	
	// p.artist, 
	page.setArtist(rs.getNextString());
	
	// p.title, 
	page.setTitle(rs.getNextString());
	
	// p.artist2, 
	page.setArtist2(rs.getNextString());
	
	// p.title2, 
	page.setTitle2(rs.getNextString());
	
	// p.description, 
	page.setDescription(rs.getNextString());
	
	// p.text 
	page.setText(rs.getNextString());

	return f;
    }

    public void postFavorite(String userName, String bookId, int page, FavoritePage fav) throws SQLException {
	ResultSet rh = null;
	try {
	    Book book = getBook(bookId);
	    if (book == null) {
		throw new NotFoundException();
	    }
	    FavoritePage existing = getFavorite(userName, bookId, page);
	    if (existing != null) {
		deleteFavorite(userName, bookId, page);
	    }
	    int newPosition;
	    if (existing != null) {
		newPosition = existing.getPosition();
	    } else {
		PreparedStatement getHighestFavoritePosition = getStatement("SELECT MAX(position) FROM favoritepages WHERE username=?");
		getHighestFavoritePosition.clearParameters();
		getHighestFavoritePosition.setString(1, userName);
		rh = getHighestFavoritePosition.executeQuery();
		int highestPosition;
		if (rh.next()) {
		    highestPosition = rh.getInt(1);
		} else {
		    highestPosition = 0;
		}
		newPosition = highestPosition + 1;
	    }
	    PreparedStatement insertFavoriteStatement = getStatement(
		    "INSERT INTO FavoritePages (username, position, userlabel, books_checksum, page) VALUES (?,?,?,?,?);");
	    insertFavoriteStatement.clearParameters();
	    insertFavoriteStatement.setString(1, userName);
	    insertFavoriteStatement.setInt(2, newPosition);
	    insertFavoriteStatement.setString(3, fav.getUserLabel());
	    insertFavoriteStatement.setString(4, book.getFileChecksum());
	    insertFavoriteStatement.setInt(5, page);
	    int rows = insertFavoriteStatement.executeUpdate();
	    if (rows != 1) {
		LOGGER.warn("Received " + rows + " instead of expected 1 when adding favorite");
	    }
	} finally {
	    DBUtils.close(rh);
	}
    }

    public void deleteFavorite(String userName, String bookId, int page) throws SQLException {
	PreparedStatement deleteFavoriteStatement = getStatement("DELETE FROM FavoritePages WHERE userName=? AND books_checksum=? AND page=?;");
	deleteFavoriteStatement.clearParameters();
	deleteFavoriteStatement.setString(1, userName);
	deleteFavoriteStatement.setString(2, bookId);
	deleteFavoriteStatement.setInt(3, page);
	int rows = deleteFavoriteStatement.executeUpdate();
	if (rows > 1) {
	    LOGGER.warn("Received " + rows + " instead of expected 1 when deleting favorite");
	}
    }

}
