package com.jeltechnologies.screenmusic.booksview;

import java.io.IOException;

import com.jeltechnologies.screenmusic.servlet.BaseServlet;
import com.jeltechnologies.utils.JsonUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/books-in-view/opened-book")
public class OpenedBookServlet extends BaseServlet {
    private static final long serialVersionUID = 4728699747390751582L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String json = getBody(request);
	String bookInView = (String) new JsonUtils().fromJSON(json, String.class);
	BooksInViewSessionBean bean = BooksInViewSessionBean.getBooksView(request.getSession());
	bean.setCurrentBookInView(bookInView);
    }

}
