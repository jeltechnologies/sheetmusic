package com.jeltechnologies.screenmusic.favorites.books;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jeltechnologies.screenmusic.favorites.Favorite;
import com.jeltechnologies.screenmusic.library.Book;

@JsonInclude(Include.NON_NULL)
public class FavoriteBook extends Favorite {
    private static final long serialVersionUID = 4985337786693987141L;
    
    @JsonInclude(Include.NON_NULL)
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
	return "FavoriteBook [book=" + book + ", getPosition()=" + getPosition() + ", isFavorite()=" + isFavorite() + "]";
    }


}
