package com.jeltechnologies.sheetmusic.search;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.BaseServlet;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/search")
public class SearchServlet extends BaseServlet {
    private static final long serialVersionUID = 8255855420060016539L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServlet.class);
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	User user = getUser(request);
	String query = request.getParameter("q");
	try {
	    SearchResults results = new Library(user, new SheetMusicContext(request)).search(query);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Q: " + query);
		for (SearchResult result : results.getResults()) {
		    LOGGER.debug("  " + result.toString());
		}
	    }
	    respondJson(response, results);
	} catch (InterruptedException e) {
	    throw new ServletException("Interrupted");
	}
    }
}
