package com.jeltechnologies.sheetmusic.favorites.books;

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

import jakarta.ws.rs.NotFoundException;

public class DataAccessObject extends DBCrud {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessObject.class);

    private static String FAV_BOOK_JOIN = "SELECT b.checksum, b.title, b.artist, b.nrofpages, b.series, b.description, f.position FROM favoritebooks f INNER JOIN books b ON f.books_checksum = b.checksum WHERE username=? ";

    public List<FavoriteBook> getFavoriteBooks(String userName) throws SQLException {
	ResultSetIterator rs = null;
	List<FavoriteBook> favorites = new ArrayList<FavoriteBook>();
	try {
	    String sql = FAV_BOOK_JOIN + "ORDER BY position";
	    LOGGER.debug("User: " + userName + " => " + sql);
	    PreparedStatement st = getStatement(sql);
	    st.setString(1, userName);
	    rs = new ResultSetIterator(st);
	    while (rs.next()) {
		FavoriteBook fb = new FavoriteBook();
		favorites.add(fb);
		parseFavoriteBook(fb, rs);
	    }
	    return favorites;
	} finally {
	    close(rs);
	}
    }

    public FavoriteBook getFavoriteBook(String userName, String bookId) throws SQLException {
	FavoriteBook favoriteBook = new FavoriteBook();
	ResultSetIterator rs = null;
	try {
	    String sql = FAV_BOOK_JOIN + "AND books_checksum=? ORDER BY position";
	    PreparedStatement st = getStatement(sql);
	    st.setString(1, userName);
	    st.setString(2, bookId);
	    rs = new ResultSetIterator(st);
	    if (rs.next()) {
		parseFavoriteBook(favoriteBook, rs);
	    } else {
		favoriteBook.setFavorite(false);
	    }
	} finally {
	    close(rs);
	}
	return favoriteBook;
    }

    private void parseFavoriteBook(FavoriteBook favoriteBook, ResultSetIterator rs) throws SQLException {
	favoriteBook.setBook(parseBook(rs));
	favoriteBook.setPosition(rs.getNextInt());
	favoriteBook.setFavorite(true);
    }

    private Book parseBook(ResultSetIterator rs) throws SQLException {
	Book book = new Book();
	// 1           2        3         4            5         6              7
	// b.checksum, b.title, b.artist, b.nrofpages, b.series, b.description, f.position
	book.setFileChecksum(rs.getNextString());
	book.setTitle(rs.getNextString());
	book.setArtist(rs.getNextString());
	book.setNrOfPages(rs.getNextInt());
	book.setSeries(rs.getNextString());
	book.setDescription(rs.getNextString());
	return book;
    }

    public void postFavorite(String userName, FavoriteBook fav) throws SQLException {
	ResultSet rh = null;
	try {
	    String bookId = fav.getBook().getFileChecksum();
	    Book book = getBook(bookId);
	    if (book == null) {
		throw new NotFoundException();
	    }
	    FavoriteBook existing = getFavoriteBook(userName, bookId);
	    if (existing == null) {
		deleteFavorite(userName, bookId);
	    }

	    int newPosition;
	    if (existing != null) {
		newPosition = existing.getPosition();
	    } else {
		PreparedStatement getHighestFavoritePosition = getStatement("SELECT MAX(position) FROM favoritebooks WHERE username=?");
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
		    "INSERT INTO FavoriteBooks (username, position, books_checksum) VALUES (?,?,?);");
	    insertFavoriteStatement.clearParameters();
	    insertFavoriteStatement.setString(1, userName);
	    insertFavoriteStatement.setInt(2, newPosition);
	    insertFavoriteStatement.setString(3, book.getFileChecksum());
	    int rows = insertFavoriteStatement.executeUpdate();
	    if (rows != 1) {
		LOGGER.warn("Received " + rows + " instead of expected 1 when adding favorite");
	    }
	} finally {
	    DBUtils.close(rh);
	}
    }

    public void deleteFavorite(String userName, String id) throws SQLException {
	PreparedStatement deleteFavoriteStatement = getStatement("DELETE FROM FavoriteBooks WHERE userName=? AND books_checksum=?;");
	deleteFavoriteStatement.clearParameters();
	deleteFavoriteStatement.setString(1, userName);
	deleteFavoriteStatement.setString(2, id);
	int rows = deleteFavoriteStatement.executeUpdate();
	if (rows > 1) {
	    LOGGER.warn("Received " + rows + " instead of expected 1 when deleting favorite");
	}
    }

}
