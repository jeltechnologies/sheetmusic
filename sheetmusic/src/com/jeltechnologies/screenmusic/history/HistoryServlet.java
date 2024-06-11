package com.jeltechnologies.screenmusic.history;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.servlet.BaseServlet;
import com.jeltechnologies.screenmusic.servlet.ServletUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/history/*")
public class HistoryServlet extends BaseServlet {

    private final static Logger LOGGER = LoggerFactory.getLogger(HistoryServlet.class);
    private static final long serialVersionUID = 4432433535454692675L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {
	    History history = new History(ServletUtils.getUserName(request));
	    List<PageView> views = history.getPageViews();
	    respondJson(response, views);
	} catch (Exception e) {
	    LOGGER.error("Error getting history", e);
	    throw new ServletException("Cannot get history");
	}
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {
	    StoreHistoryPayload payload = (StoreHistoryPayload) getJsonFromBody(request, StoreHistoryPayload.class);
	    History history = new History(ServletUtils.getUserName(request));
	    history.addToHistory(payload.getId(), payload.getPage());
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Added history: " + payload);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error getting history", e);
	    throw new ServletException("Cannot get history");
	}

    }

}
