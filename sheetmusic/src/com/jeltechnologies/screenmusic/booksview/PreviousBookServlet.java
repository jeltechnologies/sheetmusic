package com.jeltechnologies.screenmusic.booksview;

import java.io.IOException;

import com.jeltechnologies.screenmusic.servlet.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/books-in-view/previous")
public class PreviousBookServlet extends BaseServlet {
    private static final long serialVersionUID = 606394163722380508L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	BooksInViewSessionBean bean = BooksInViewSessionBean.getBooksView(request.getSession());
	String next = bean.getPrevious();
	respondJson(response, next);
    }


}
