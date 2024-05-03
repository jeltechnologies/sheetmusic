package com.jeltechnologies.sheetmusic.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBUtils.class);

    public static final Connection getConnection() throws SQLException {
	try {
	    Context ctx = new InitialContext();
	    DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/database");
	    Connection connection = ds.getConnection();
	    connection.setAutoCommit(false);
	    return connection;
	} catch (Exception e) {
	    throw new IllegalStateException("Cannot connect to database", e);
	}
    }

    public static void executeSQL(Connection connection, String sql) throws SQLException {
	Statement statement = null;
	try {
	    statement = connection.createStatement();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info(sql);
	    }
	    statement.execute(sql);
	} finally {
	    DBUtils.close(statement);
	}
    }

    public static void close(Statement statement) {
	if (statement != null) {
	    try {
		statement.close();
	    } catch (SQLException e) {
		LOGGER.error("Cannot close Statemement", e);
	    }
	}
    }

    public static void close(ResultSet rs) {
	if (rs != null) {
	    try {
		rs.close();
	    } catch (SQLException e) {
		LOGGER.error("Cannot close ResultSet", e);
	    }
	}
    }

    public static void close(Connection connection) {
	if (connection != null) {
	    try {
		connection.close();
	    } catch (SQLException e) {
		LOGGER.error("Cannot close connection", e);
	    }
	}
    }

    public static Date getDateTime(ResultSet rs, int columnIndex) throws SQLException {
	java.sql.Timestamp sqlDate = rs.getTimestamp(columnIndex);
	java.util.Date utilDate;
	if (sqlDate != null) {
	    utilDate = new java.util.Date(sqlDate.getTime());
	} else {
	    utilDate = null;
	}
	return utilDate;
    }
    
    public static LocalDateTime getLocalDateTime(ResultSet rs, int columnIndex) throws SQLException {
	java.sql.Timestamp sqlDate = rs.getTimestamp(columnIndex);
	LocalDateTime utilDate;
	if (sqlDate != null) {
	    utilDate = sqlDate.toLocalDateTime();
	} else {
	    utilDate = null;
	}
	return utilDate;
    }

    public static Date getDate(ResultSet rs, int columnIndex) throws SQLException {
	java.sql.Date sqlDate = rs.getDate(columnIndex);
	java.util.Date utilDate;
	if (sqlDate != null) {
	    utilDate = new java.util.Date(sqlDate.getTime());
	} else {
	    utilDate = null;
	}
	return utilDate;
    }

    public static void setTimestamp(PreparedStatement st, int parameterIndex, Date date) throws SQLException {
	if (date != null) {
	    Timestamp timeStamp = new Timestamp(date.getTime());
	    st.setTimestamp(parameterIndex, timeStamp);
	} else {
	    st.setNull(parameterIndex, Types.TIMESTAMP);
	}
    }


}
