package com.jeltechnologies.sheetmusic.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class ResultSetIterator {
    private final ResultSet rs;
    private int index = 1;

    public ResultSetIterator(ResultSet rs) {
	this.rs = rs;
    }

    public ResultSetIterator(PreparedStatement st) throws SQLException {
	this.rs = st.executeQuery();
    }
    
    public boolean next() throws SQLException {
	index = 1;
	return rs.next();
    }
    
    public void close() throws SQLException {
	rs.close();
    }

    public String getNextString() throws SQLException {
	String result = rs.getString(index);
	index++;
	return result;
    }

    public int getNextInt() throws SQLException {
	int result = rs.getInt(index);
	index++;
	return result;
    }

    public boolean getNextBoolean() throws SQLException {
	boolean result = rs.getBoolean(index);
	index++;
	return result;
    }

    public Date getNextDate(ResultSet rs) throws SQLException {
	java.sql.Timestamp sqlDate = rs.getTimestamp(index);
	java.util.Date utilDate;
	if (sqlDate != null) {
	    utilDate = new java.util.Date(sqlDate.getTime());
	} else {
	    utilDate = null;
	}
	return utilDate;
    }

    public LocalDateTime getNextLocalDateTime(ResultSet rs) throws SQLException {
	java.sql.Timestamp sqlDate = rs.getTimestamp(index);
	LocalDateTime utilDate;
	if (sqlDate != null) {
	    utilDate = sqlDate.toLocalDateTime();
	} else {
	    utilDate = null;
	}
	return utilDate;
    }
    

}
