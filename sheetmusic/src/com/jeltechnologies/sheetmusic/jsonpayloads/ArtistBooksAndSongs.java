package com.jeltechnologies.sheetmusic.jsonpayloads;

import java.util.ArrayList;
import java.util.List;

import com.jeltechnologies.sheetmusic.library.Book;

public class ArtistBooksAndSongs {
    private String artist;
    private String artistSortField;
    private List<Book> books = new ArrayList<>();;
    private List<Song> songs = new ArrayList<Song>();
    
    public void add(Book book) {
	books.add(book);
    }
    
    public void add(Song song) {
	songs.add(song);
    }

    public String getArtist() {
	return artist;
    }

    public void setArtist(String artist) {
	this.artist = artist;
	artistSortField = artist;
	if (artist != null) {
	    String artistLower = artist.toLowerCase();
	    if (artistLower.startsWith("the ")) {
		artistSortField = artist.substring(4);
	    }
	}
    }
    
    public String getArtistSortField() {
	return artistSortField;
    }

    public List<Book> getBooks() {
	return books;
    }

    public void setBooks(List<Book> books) {
	this.books = books;
    }

    public List<Song> getSongs() {
	return songs;
    }

    public void setSongs(List<Song> songs) {
	this.songs = songs;
    }
}
