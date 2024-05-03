package com.jeltechnologies.sheetmusic.jsonpayloads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.Category;

public class CategoryAndBooks {
    private Category category;

    private List<Book> books = new ArrayList<Book>();
    
    public void add(Book book) {
	this.books.add(book);
    }

    public Category getCategory() {
	return category;
    }

    public void setCategory(Category category) {
	this.category = category;
    }

    public List<Book> getBooks() {
	return books;
    }

    public void setBooks(List<Book> books) {
	this.books = books;
    }
    
    public void sort() {
	Collections.sort(books);
    }
}
