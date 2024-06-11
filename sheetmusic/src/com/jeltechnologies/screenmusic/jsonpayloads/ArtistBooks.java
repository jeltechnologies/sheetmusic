package com.jeltechnologies.screenmusic.jsonpayloads;

import java.util.ArrayList;
import java.util.List;

import com.jeltechnologies.screenmusic.library.Book;

public class ArtistBooks implements Comparable<ArtistBooks> {
    private String artist;
    private List<Song> songs = new ArrayList<Song>();
    private List<Book> books = new ArrayList<Book>();
    private int totalSongs = 0;

    public void add(String bookChecksum) {
	Book b = new Book();
	b.setFileChecksum(bookChecksum);
	books.add(b);
    }

    public void add(Song song) {
	songs.add(song);
	totalSongs = totalSongs + 1;
    }

    public String getArtist() {
	return artist;
    }

    public List<Song> getSongs() {
	return songs;
    }

    public List<Book> getBooks() {
	return books;
    }

    public void setArtist(String artist) {
	this.artist = artist;
    }

    public void add(int songs) {
	totalSongs = totalSongs + songs;
    }
    
    public int getTotalSongs() {
        return totalSongs;
    }

    @Override
    public int compareTo(ArtistBooks o) {
	return artist.compareTo(o.artist);
    }

}
