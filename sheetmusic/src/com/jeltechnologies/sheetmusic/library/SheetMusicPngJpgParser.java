package com.jeltechnologies.sheetmusic.library;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.extractedfilestorage.Thumbnail;
import com.jeltechnologies.utils.FileUtils;
import com.jeltechnologies.utils.ImageUtils;
import com.jeltechnologies.utils.StringUtils;

public class SheetMusicPngJpgParser implements SheetMusicFileParser {
    private final File imageFile;
    private final User user;
    
    public SheetMusicPngJpgParser(User user, File file) {
	this.imageFile = file;
	this.user = user;
    }

    @Override
    public Book getBook() throws IOException {
	Book book = new Book();
	book.setNrOfPages(1);
	String name = imageFile.getName();
	book.setTitle(StringUtils.stripAfterLast(name, "."));
	book.setFileSize(imageFile.length());
	return book;
    }
    
    @Override
    public void close() {
    }
    
    private int imagesCreated;

    @Override
    public void createThumbs(List<Thumbnail> thumbs) throws IOException, InterruptedException {
	imagesCreated = 0;
	Book book = getBook();
	
	File bookFileImage = user.getFile(book.getRelativeFileName());
	for (Thumbnail thumb : thumbs) {
	    if (Thread.interrupted()) {
		throw new InterruptedException();
	    }
	    BufferedImage image = ImageUtils.createMaximizedThumb(bookFileImage, thumb.getSize().getWidth(), thumb.getSize().getHeight());
	    FileUtils.saveImage(thumb.getCachedFile(), image, Thumbnail.IMAGE_EXTENSION, Thumbnail.COMPRESSION_QUALITY);
	    imagesCreated++;
	}
    }

    @Override
    public int getImagesCreated() {
	return imagesCreated;
    }
    
}
