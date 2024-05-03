package com.jeltechnologies.sheetmusic.booksview;

import java.io.IOException;
import java.util.List;

import com.jeltechnologies.sheetmusic.servlet.BaseServlet;
import com.jeltechnologies.utils.JsonUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/books-in-view/view")
public class BooksInViewServlet extends BaseServlet {
    private static final long serialVersionUID = -997307635709859960L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String json = getBody(request);
	@SuppressWarnings("unchecked")
	List<String> bookViews = (List<String>) new JsonUtils().fromJSON(json, List.class);
	BooksInViewSessionBean bean = BooksInViewSessionBean.getBooksView(request.getSession());
	bean.setBooksInView(bookViews);
    }
}
