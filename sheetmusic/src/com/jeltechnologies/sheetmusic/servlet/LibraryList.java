package com.jeltechnologies.sheetmusic.servlet;

import java.io.Serializable;
import java.util.List;

import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.Folder;

public class LibraryList implements Serializable {
    private static final long serialVersionUID = 5558591689575716809L;

    private List<Book> books;

    private List<Folder> folders;

    public List<Book> getBooks() {
	return books;
    }

    public void setBooks(List<Book> books) {
	this.books = books;
    }

    public List<Folder> getFolders() {
	return folders;
    }

    public void setFolders(List<Folder> folders) {
	this.folders = folders;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("LibraryList [books=");
	builder.append(books);
	builder.append(", folders=");
	builder.append(folders);
	builder.append("]");
	return builder.toString();
    }

}
