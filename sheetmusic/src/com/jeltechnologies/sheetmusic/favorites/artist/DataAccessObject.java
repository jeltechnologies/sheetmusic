package com.jeltechnologies.sheetmusic.favorites.artist;

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

public class DataAccessObject extends DBCrud {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessObject.class);

    private static String FAV_QUERY = "SELECT artist, position FROM FavoriteArtists WHERE username=? ";

    public List<FavoriteArtist> getAll(String userName) throws SQLException {
	ResultSetIterator rs = null;
	List<FavoriteArtist> favorites = new ArrayList<FavoriteArtist>();
	try {
	    String sql = "SELECT f.position, f.artist, b.checksum, b.title FROM FavoriteArtists f, Books b WHERE username=? AND f.artist = b.artist ORDER BY position, artist";
	    LOGGER.debug("User: " + userName + " => " + sql);
	    PreparedStatement st = getStatement(sql);
	    st.setString(1, userName);
	    rs = new ResultSetIterator(st);
	    String lastArtist = null;
	    FavoriteArtist f = null;
	    List<String> checksums = null;
	    while (rs.next()) {
		int position = rs.getNextInt();
		String artist = rs.getNextString();
		String checksum = rs.getNextString();
		if (!artist.equals(lastArtist)) {
		    f = new FavoriteArtist();
		    f.setArtist(artist);
		    f.setPosition(position);
		    checksums = new ArrayList<String>();
		    f.setBookIds(checksums);
		    favorites.add(f);
		    lastArtist = artist;
		}
		checksums.add(checksum);
	    }
	    return favorites;
	} finally {
	    close(rs);
	}
    }

    public FavoriteArtist getFavorite(String userName, String artist) throws SQLException {
	ResultSetIterator rs = null;
	FavoriteArtist f = null;
	try {
	    String sql = FAV_QUERY + "AND artist=? ORDER BY position";
	    LOGGER.debug("User: " + userName + " => " + sql);
	    PreparedStatement st = getStatement(sql);
	    st.setString(1, userName);
	    st.setString(2, artist);
	    rs = new ResultSetIterator(st);
	    if (rs.next()) {
		f = new FavoriteArtist();
		f.setArtist(rs.getNextString());
		f.setPosition(rs.getNextInt());
		f.setFavorite(true);
	    }
	    return f;
	} finally {
	    close(rs);
	}
    }

    public void postFavorite(String userName, FavoriteArtist favoriteArtist) throws SQLException {
	ResultSet rh = null;
	try {
	    FavoriteArtist existing = getFavorite(userName, favoriteArtist.getArtist());
	    if (existing != null) {
		deleteFavorite(userName, favoriteArtist.getArtist());
	    }
	    int newPosition;
	    if (existing != null) {
		newPosition = existing.getPosition();
	    } else {
		PreparedStatement getHighestFavoritePosition = getStatement("SELECT MAX(position) FROM FavoriteArtists WHERE username=?");
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
		    "INSERT INTO FavoriteArtists (username, position, artist) VALUES (?,?,?);");
	    insertFavoriteStatement.clearParameters();
	    insertFavoriteStatement.setString(1, userName);
	    insertFavoriteStatement.setInt(2, newPosition);
	    insertFavoriteStatement.setString(3, favoriteArtist.getArtist());
	    int rows = insertFavoriteStatement.executeUpdate();
	    if (rows != 1) {
		LOGGER.warn("Received " + rows + " instead of expected 1 when adding favorite");
	    }
	} finally {
	    DBUtils.close(rh);
	}
    }

    public void deleteFavorite(String userName, String artist) throws SQLException {
	PreparedStatement deleteFavoriteStatement = getStatement("DELETE FROM FavoriteArtists WHERE userName=? AND artist=?;");
	deleteFavoriteStatement.clearParameters();
	deleteFavoriteStatement.setString(1, userName);
	deleteFavoriteStatement.setString(2, artist);
	int rows = deleteFavoriteStatement.executeUpdate();
	if (rows > 1) {
	    LOGGER.warn("Received " + rows + " instead of expected 1 when deleting favorite");
	}
    }

}
