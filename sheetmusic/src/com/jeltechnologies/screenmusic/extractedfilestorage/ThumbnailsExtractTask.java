package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.util.List;

import com.jeltechnologies.screenmusic.library.Book;

public class ThumbnailsExtractTask {
    private final Book book;
    
    private final List<Thumbnail> thumbsToExtract;
    
    public ThumbnailsExtractTask(Book book, List<Thumbnail> thumbsToExtract) {
	this.book = book;
	this.thumbsToExtract = thumbsToExtract;
    }

    public Book getBook() {
        return book;
    }

    public List<Thumbnail> getThumbsToExtract() {
        return thumbsToExtract;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("ThumbnailsExtractTask [book=");
	builder.append(book.getRelativeFileName());
	builder.append(", thumbsToExtract=");
	builder.append(thumbsToExtract.size());
	builder.append("]");
	return builder.toString();
    }

}
