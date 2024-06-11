package com.jeltechnologies.screenmusic.history;

import java.io.Serializable;

import com.jeltechnologies.screenmusic.library.Book;

public class PageViews implements Serializable {
    private static final long serialVersionUID = -2043535415588963385L;
    private Book book;
    private int page;
    private int views;

    public Book getBook() {
	return book;
    }

    public void setBook(Book book) {
	this.book = book;
    }

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public int getViews() {
	return views;
    }

    public void setViews(int views) {
	this.views = views;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("PageViews [book=");
	builder.append(book);
	builder.append(", page=");
	builder.append(page);
	builder.append(", views=");
	builder.append(views);
	builder.append("]");
	return builder.toString();
    }

}
