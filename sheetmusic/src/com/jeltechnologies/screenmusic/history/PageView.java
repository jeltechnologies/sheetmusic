package com.jeltechnologies.screenmusic.history;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.BookPage;

public class PageView implements Serializable {
    private static final long serialVersionUID = 1406523858658115934L;
    private Book book;
    private BookPage page;
    @JsonIgnore
    private LocalDateTime moment;

    public PageView(Book book, BookPage page, LocalDateTime moment) {
	super();
	this.book = book;
	this.page = page;
	this.moment = moment;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setPage(BookPage page) {
        this.page = page;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }

    public Book getBook() {
	return book;
    }

    public BookPage getPage() {
	return page;
    }

    public LocalDateTime getMoment() {
	return moment;
    }
    
    public String getMomentISO() {
	return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(moment);
    }

    @Override
    public String toString() {
	return "PageView [book=" + book + ", page=" + page + ", moment=" + moment + "]";
    }

}
