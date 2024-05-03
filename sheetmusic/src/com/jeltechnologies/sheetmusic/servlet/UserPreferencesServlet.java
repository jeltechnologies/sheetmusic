package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.db.DBUsers;
import com.jeltechnologies.sheetmusic.jsonpayloads.UserPreferences;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/users/preferences")
public class UserPreferencesServlet extends BaseServlet {
    private static final long serialVersionUID = -4114896654302681749L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String user = ServletUtils.getUserName(request);
	DBUsers db = null;
	Object responseJSON;
	try {
	    db = new DBUsers();
	    responseJSON = db.getPreferences(user);
	    if (responseJSON == null) {
		responseJSON = new UserPreferences();
	    }
	    respondJson(response, responseJSON);
	} catch (Exception e) {
	    throw new ServletException("Cannot get user preferences", e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String userName = ServletUtils.getUserName(request);
	DBUsers db = null;
	try {
	    db = new DBUsers();
	    UserPreferences preferences = (UserPreferences) getJsonFromBody(request, UserPreferences.class);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("userName " + userName + ", preferences: " + preferences);
	    }

	    UserPreferences existingPreferences = db.getPreferences(userName);
	    if (existingPreferences == null) {
		existingPreferences = new UserPreferences();
	    }
	    if (!preferences.getCategorySelected().isBlank()) {
		existingPreferences.setCategorySelected(preferences.getCategorySelected());
	    }
	    if (!preferences.getSeriesSelected().isBlank()) {
		existingPreferences.setSeriesSelected(preferences.getSeriesSelected());
	    }
	    if (preferences.getSongBooksSorting() != null) {
		existingPreferences.setSongBooksSorting(preferences.getSongBooksSorting());
	    }
	    
	    db.addPreferences(userName, existingPreferences);
	} catch (Exception e) {
	    throw new ServletException("Cannot store user preferences", e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

}
