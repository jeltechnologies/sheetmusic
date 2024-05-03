package com.jeltechnologies.sheetmusic.servlet;

import java.io.Serializable;

import com.jeltechnologies.sheetmusic.library.Book;

public class LibraryOperation implements Serializable {
    private static final long serialVersionUID = -8786185498008819502L;
    private String operation;
    private Book book;

    public String getOperation() {
	return operation;
    }

    public void setOperation(String operation) {
	this.operation = operation;
    }

    public Book getBook() {
	return book;
    }

    public void setBook(Book book) {
	this.book = book;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("LibraryOperation [operation=");
	builder.append(operation);
	builder.append(", book=");
	builder.append(book);
	builder.append("]");
	return builder.toString();
    }
}
