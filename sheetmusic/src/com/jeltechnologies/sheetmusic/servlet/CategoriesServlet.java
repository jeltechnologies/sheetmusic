package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;

import com.jeltechnologies.sheetmusic.library.Library;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/categories/list")
public class CategoriesServlet extends BaseServlet {
    private static final long serialVersionUID = 3649811767196573694L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	Object responseJSON;
	try {
	    responseJSON = new Library(getUser(request), new SheetMusicContext(request)).getAllCategories();
	    respondJson(response, responseJSON);
	} catch (Exception e) {
	    throw new ServletException("Cannot get categories", e);
	}
    }
}

