package com.jeltechnologies.screenmusic.library;

import java.io.Serializable;

public class BookPage implements Serializable {
    private static final long serialVersionUID = -9088785524407706385L;
    
    private String bookFileChecksum; 
    
    private int nr;

    private String artist = "";

    private String artist2 = "";

    private String title = "";

    private String title2 = "";

    private String description = "";

    private String text = "";

    public BookPage() {
    }

    public int getNr() {
	return nr;
    }

    public String getArtist() {
	return artist;
    }

    public void setArtist(String artist) {
	this.artist = artist;
    }

    public String getArtist2() {
	return artist2;
    }

    public void setArtist2(String artist2) {
	this.artist2 = artist2;
    }

    public String getTitle2() {
	return title2;
    }

    public void setTitle2(String title2) {
	this.title2 = title2;
    }

    public void setNr(int pageNumber) {
	this.nr = pageNumber;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }
    
    public String getBookFileChecksum() {
        return bookFileChecksum;
    }

    public void setBookFileChecksum(String bookFileChecksum) {
        this.bookFileChecksum = bookFileChecksum;
    }
    
    public String getLabel() {
	StringBuilder s = new StringBuilder();
	String label1 = getTitleArtist(title, artist);
	String label2 = getTitleArtist(title2, artist2);
	s.append(label1);
	if (!label2.isEmpty()) {
	    if (!label1.isEmpty()) {
		s.append(" / ");
	    }
	    s.append(label2);
	}
	return s.toString();
    }

    private String getTitleArtist(String title, String artist) {
	boolean empty = true;
	StringBuilder s = new StringBuilder();
	if (artist != null && !artist.isEmpty()) {
	    s.append(artist);
	    empty = false;
	}
	if (title != null && !title.isEmpty()) {
	    if (!empty) {
		s.append(" - ");
	    }
	    s.append(title);
	}
	return s.toString();
    }

    public void trim() {
	this.artist = trim(artist);
	this.artist2 = trim(artist2);
	this.title = trim(title);
	this.title2 = trim(title2);
	this.description = trim(description);
	this.text = trim(text);
    }
    
    private String trim(String field) {
	if (field == null) {
	    return null;
	} else {
	    return field.trim();
	}
    }

    private boolean isBlank(String value) {
	return value == null || value.isBlank();
    }

    public boolean isBlank() {
	boolean blank = isBlank(title) && isBlank(title2) && isBlank(artist) && isBlank(artist2) && isBlank(description) && isBlank(text);
	return blank;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("BookPage [bookFileChecksum=");
	builder.append(bookFileChecksum);
	builder.append(", nr=");
	builder.append(nr);
	builder.append(", title=");
	builder.append(title);
	builder.append(", artist=");
	builder.append(artist);
	builder.append(", title2=");
	builder.append(title2);
	builder.append(", artist2=");
	builder.append(artist2);
	builder.append(", description=");
	builder.append(description);
	builder.append(", text=");
	builder.append(text);
	builder.append("]");
	return builder.toString();
    }


}
