package com.jeltechnologies.sheetmusic.db;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.jsonpayloads.UserPreferences;
import com.jeltechnologies.utils.JsonUtils;

public class DBUsers extends DBCrud {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBUsers.class);

    public User getUser(String name) throws SQLException {
	ResultSet rs = null;
	try {
	    PreparedStatement st = getStatement("SELECT name, sheetfolder FROM Users WHERE name=?");
	    st.setString(1, name);
	    rs = st.executeQuery();
	    User user = null;
	    if (rs.next()) {
		Path path = Paths.get(rs.getString(2));
		user = new User(rs.getString(1), path);
	    }
	    if (user == null) {
		LOGGER.warn("No such user: " + name);
	    }
	    return user;
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }

    public List<User> getUsers() throws SQLException {
	ResultSet rs = null;
	try {
	    PreparedStatement st = getStatement("SELECT name, sheetfolder FROM Users");
	    rs = st.executeQuery();
	    List<User> users = new ArrayList<User>();
	    while (rs.next()) {
		String name = rs.getString(1);
		Path path = Paths.get(rs.getString(2));
		User user = new User(name, path);
		users.add(user);
	    }
	    return users;
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	}
    }

    public void addPreferences(String userName, UserPreferences preferences) throws SQLException {
	String json = new JsonUtils().toJSON(preferences);
	String sqlUpdate = "UPDATE users SET preferences=? WHERE name=?";
	PreparedStatement st = getStatement(sqlUpdate);
	st.setString(1, json);
	st.setString(2, userName);
	st.executeUpdate();
    }

    public UserPreferences getPreferences(String userName) throws SQLException {
	ResultSet rs = null;
	try {
	    String sql = "SELECT preferences FROM Users WHERE name = ?";
	    PreparedStatement st = getStatement(sql);
	    st.clearParameters();
	    st.setString(1, userName);
	    rs = st.executeQuery();
	    UserPreferences preferences = null;
	    if (rs.next()) {
		String json = rs.getString(1);
		try {
		    preferences = (UserPreferences) new JsonUtils().fromJSON(json, UserPreferences.class);
		} catch (Exception e) {
		    LOGGER.warn("Cannot convert to userpreferences: " + preferences);
		}
	    }
	    if (preferences == null) {
		// TODO FIX
		preferences = new UserPreferences();
	    }
	    return preferences;
	} finally {
	    close(rs);
	}
    }
}
