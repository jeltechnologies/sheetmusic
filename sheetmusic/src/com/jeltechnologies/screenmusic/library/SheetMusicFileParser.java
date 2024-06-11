package com.jeltechnologies.screenmusic.library;

import java.io.IOException;
import java.util.List;

import com.jeltechnologies.screenmusic.extractedfilestorage.Thumbnail;

public interface SheetMusicFileParser {
    public Book getBook() throws IOException;
    
    public void close();
    
    public void createThumbs(List<Thumbnail> thumbs) throws InterruptedException, IOException;

    public int getImagesCreated();
}
