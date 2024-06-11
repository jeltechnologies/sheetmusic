package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;
import com.jeltechnologies.utils.StringUtils;

public class RefreshBookThread implements Runnable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshBookThread.class);
    
    private final String relativeFilename;
    
    private final User user;
    
    private ScreenMusicContext context;
    
    public RefreshBookThread(User user, ScreenMusicContext context, String relativeFilename) {
	this.relativeFilename = relativeFilename;
	this.user = user;
	this.context = context;
    }

    @Override
    public void run() {
	String threadName = RefreshBookThread.class.getSimpleName() + " for [" + StringUtils.stripSpaces(relativeFilename) + "]";
	Thread.currentThread().setName(threadName);
	LOGGER.info(threadName + " started");
	try {
	    Library library = new Library(user, context);
	    Book book = library.getBookByFileName(relativeFilename);
	    BookImageCache cache = new BookImageCache(context.getThreadService());
	    cache.delete(book.getFileChecksum());
	    
	    File file = user.getFile(relativeFilename);
	    IndexProducer producer = new IndexProducer(user, context);
	    producer.handleFile(file);
	    
	} catch (Exception e) {
	    LOGGER.error("Cannot refresh book " + relativeFilename, e);
	}
	LOGGER.info(threadName + " ended");
    }

}
