package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;

import com.jeltechnologies.sheetmusic.library.Library;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/categories/books")
public class CategoriesBooksServlet extends BaseServlet {

    private static final long serialVersionUID = 5544615584428046199L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	Object responseJSON;
	try {
	    responseJSON = new Library(getUser(request), new SheetMusicContext(request)).getAllCategoriesAndBooks();
	    respondJson(response, responseJSON);
	} catch (Exception e) {
	    throw new ServletException("Cannot get categories and books", e);
	}
    }

}
