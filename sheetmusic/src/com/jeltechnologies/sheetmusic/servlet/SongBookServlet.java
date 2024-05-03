package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;
import java.util.List;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.jsonpayloads.SortType;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.Library;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/songbooks/*")
public class SongBookServlet extends BaseServlet {
    private static final long serialVersionUID = -385660033964787875L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {
	    SortType sortType = getSortType(request);
	    Object responseJSON = getSongBooks(getUser(request), new SheetMusicContext(request), sortType);
	    respondJson(response, responseJSON);
	} catch (Exception e) {
	    throw new ServletException("Cannot get songbooks", e);
	}
    }
    
    private List<Book> getSongBooks(User user, SheetMusicContext context, SortType sortType) {
	List<Book> songBooks = new Library(user, context).getAllSongBooks(sortType);
	return songBooks;
    }
    
    private SortType getSortType(HttpServletRequest request) {
	String sortParam = request.getParameter("sort");
	SortType sortType = null;
	for (int i = 0; i < SortType.values().length && sortType == null; i++) {
	    SortType current = SortType.values()[i];
	    if (current.toString().equalsIgnoreCase(sortParam)) {
		sortType = current;
	    }
	}
	if (sortType == null) {
	    sortType = SortType.RANDOM;
	}
	return sortType;
    }

}
