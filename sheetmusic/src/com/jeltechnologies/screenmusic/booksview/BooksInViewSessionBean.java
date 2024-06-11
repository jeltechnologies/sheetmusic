package com.jeltechnologies.screenmusic.booksview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;

public class BooksInViewSessionBean implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooksInViewSessionBean.class);
    
    private static final String SESSION_KEY = BooksInViewSessionBean.class.getName();

    private static final long serialVersionUID = -4125978990946731636L;

    private List<String> booksInView = new ArrayList<String>();

    private int currentBookView = -1;

    public static BooksInViewSessionBean getBooksView(HttpSession session) {
	BooksInViewSessionBean bean = (BooksInViewSessionBean) session.getAttribute(SESSION_KEY);
	if (bean == null) {
	    bean = new BooksInViewSessionBean();
	    session.setAttribute(SESSION_KEY, bean);
	}
	return bean;
    }
    
    public void setBooksInView(List<String> bookViews) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("setBooksInView " + bookViews);
	}
	this.booksInView = bookViews;
	currentBookView = -1;
    }
    
    public void setCurrentBookInView(String bookId) {
	int foundIndex = -1;
	for (int i = 0; i < booksInView.size() && foundIndex == -1; i++) {
	    String current = booksInView.get(i);
	    if (current.equals(bookId)) {
		foundIndex = i;
	    }
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("setCurrentBookView (" + bookId + ") => " + foundIndex);
	}
	currentBookView = foundIndex;
    }
    
    public String getNext() {
	String result = null;
	if (currentBookView > -1) {
	    int nextIndex = currentBookView + 1;
	    if (nextIndex < booksInView.size()) {
		result = booksInView.get(nextIndex);
	    }
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("getNext => " + result);
	}
	return result;
    }
    
    public String getPrevious() {
	String result = null;
	if (currentBookView > -1) {
	    int previousIndex = currentBookView - 1;
	    if (previousIndex >= 0) {
		result = booksInView.get(previousIndex);
	    }
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("getPrevious => " + result);
	}
	return result;
    }

}
