package com.jeltechnologies.sheetmusic.favorites.pages;

import com.jeltechnologies.sheetmusic.favorites.Favorite;
import com.jeltechnologies.sheetmusic.library.BookPage;

public class FavoritePage extends Favorite {
    private static final long serialVersionUID = -1088382243821538593L;
    private String bookId;
    private int pageNumber;
    private String userLabel;
    private BookPage bookPage;

    public BookPage getBookPage() {
        return bookPage;
    }

    public void setBookPage(BookPage bookPage) {
        this.bookPage = bookPage;
    }

    public String getBookId() {
	return bookId;
    }

    public void setBookId(String bookId) {
	this.bookId = bookId;
    }

    public int getPageNumber() {
	return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
	this.pageNumber = pageNumber;
    }

    public String getUserLabel() {
	return userLabel;
    }

    public void setUserLabel(String userLabel) {
	this.userLabel = userLabel;
    }

    @Override
    public String toString() {
	return "FavoritePage [bookId=" + bookId + ", pageNumber=" + pageNumber + ", userLabel=" + userLabel + "]";
    }
}
