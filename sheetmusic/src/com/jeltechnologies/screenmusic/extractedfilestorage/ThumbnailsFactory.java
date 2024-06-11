package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jeltechnologies.screenmusic.extractedfilestorage.Thumbnail.Size;
import com.jeltechnologies.screenmusic.library.Book;

public class ThumbnailsFactory {

    public static List<Thumbnail> getExpectedThumbs(Book book, File cacheFolderExtracted) {
	List<Thumbnail> images = new ArrayList<Thumbnail>();
	for (int index = 0; index < book.getNrOfPages(); index++) {
	    int page = index + 1;
	    images.add(new Thumbnail(book.getFileChecksum(), page, Size.SMALL, cacheFolderExtracted));
	    images.add(new Thumbnail(book.getFileChecksum(), page, Size.MEDIUM, cacheFolderExtracted));
	    images.add(new Thumbnail(book.getFileChecksum(), page, Size.LARGE,cacheFolderExtracted));
	}
	return images;
    }

}
