package com.jeltechnologies.screenmusic.history;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.db.DBCrud;
import com.jeltechnologies.screenmusic.db.DBHistory;

public class History {

    private static final Logger LOGGER = LoggerFactory.getLogger(History.class);

    private final static int USER_PLAYED_FROM_PAGE_SECONDS = 5;

    private static final int LIMIT_HISTORY = 500;

    private final String userName;

    public History(String userName) {
	this.userName = userName;
    }

    public void addToHistory(String id, int pageNumber) {
	DBCrud database = null;
	try {
	    database = new DBCrud();
	    LocalDateTime now = LocalDateTime.now();

	    boolean isNewPageView;
	    LocalDateTime lastUpdate = database.getLastStoredHistory(userName);
	    if (lastUpdate == null) {
		isNewPageView = true;
	    } else {
		long viewTime = ChronoUnit.SECONDS.between(lastUpdate, now);
		if (viewTime >= USER_PLAYED_FROM_PAGE_SECONDS) {
		    isNewPageView = true;
		} else {
		    isNewPageView = false;
		}
	    }
	    if (isNewPageView) {
		database.addHistory(userName, id, pageNumber);
	    }
	    database.commit();
	} catch (SQLException e) {
	    LOGGER.warn("Cannot store history because " + e.getMessage(), e);
	} finally {
	    if (database != null) {
		database.close();
	    }
	}

    }

    public List<PageView> getPageViews() throws SQLException {
	DBHistory database = null;
	try {
	    database = new DBHistory();
	    List<PageView> allViews = database.getHistory(userName, LIMIT_HISTORY);
	    return allViews;
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
    }

    public List<BookViews> getMostPopularBooks(int top) throws SQLException {
	DBHistory database = null;
	try {
	    database = new DBHistory();
	    return database.getMostPopularBooks(userName, top);
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
    }

    public List<PageViews> getMostPopularPages(int top) throws SQLException {
	DBHistory database = null;
	try {
	    database = new DBHistory();
	    return database.getMostPopularPages(userName, top);
	} finally {
	    if (database != null) {
		database.close();
	    }
	}
    }

}
