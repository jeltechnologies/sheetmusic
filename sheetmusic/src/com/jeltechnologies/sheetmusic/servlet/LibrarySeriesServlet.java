package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;

import com.jeltechnologies.sheetmusic.library.Library;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/series")
public class LibrarySeriesServlet extends BaseServlet {
    private static final long serialVersionUID = 7293222449959243344L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	Object responseJSON;
	try {
	    
	    String series = request.getParameter("series");
	    if (series != null && !series.isBlank()) {
		responseJSON = new Library(getUser(request), new SheetMusicContext(request)).getBooksInSeries(series);
	    } else {
		responseJSON = new Library(getUser(request), new SheetMusicContext(request)).getSeries();
	    }
	    
	    respondJson(response, responseJSON);

	} catch (Exception e) {
	    throw new ServletException("Cannot get categories", e);
	}
    }
}
