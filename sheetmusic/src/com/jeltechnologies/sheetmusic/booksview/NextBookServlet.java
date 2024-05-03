package com.jeltechnologies.sheetmusic.booksview;

import java.io.IOException;

import com.jeltechnologies.sheetmusic.servlet.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/books-in-view/next")
public class NextBookServlet extends BaseServlet {
    private static final long serialVersionUID = -7058098698900023305L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	BooksInViewSessionBean bean = BooksInViewSessionBean.getBooksView(request.getSession());
	String next = bean.getNext();
	respondJson(response, next);
    }

}
