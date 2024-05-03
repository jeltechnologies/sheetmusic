package com.jeltechnologies.sheetmusic.history;

import com.jeltechnologies.sheetmusic.library.Book;

public class BookViews implements Comparable<BookViews> {
    private Book book;
    private int views;

    public Book getBook() {
	return book;
    }

    public void setBook(Book book) {
	this.book = book;
    }

    public int getViews() {
	return views;
    }

    public void setViews(int views) {
	this.views = views;
    }

    @Override
    public int compareTo(BookViews other) {
	return other.getViews() - this.views;
    }

    @Override
    public String toString() {
	return "BookViews [views=" + views + ", book=" + book.getTitle() + "]";
    }

    
}
