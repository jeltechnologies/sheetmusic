package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.BookFilesFinder;
import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.SheetMusicFileParser;
import com.jeltechnologies.screenmusic.library.SheetMusicPdfFileParser;
import com.jeltechnologies.screenmusic.library.SheetMusicPngJpgParser;

public class ThumbnailsTaskConsumer implements Runnable, ThumbnailsTaskConsumerMBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThumbnailsTaskConsumer.class);

    private final int threadNumber;

    private final ThumbnailsQueue queue;

    private ThumbnailsExtractTask extractTask;

    private boolean busy;

    private SheetMusicFileParser parser = null;
    
    private final User user;

    public ThumbnailsTaskConsumer(User user, int threadNumber, ThumbnailsQueue queue) {
	this.threadNumber = threadNumber;
	this.queue = queue;
	this.user = user;
    }

    @Override
    public void run() {
	busy = false;
	String threadName = this.getClass().getSimpleName() + "-" + threadNumber;
	Thread.currentThread().setName(threadName);
	LOGGER.info("Thread " + threadName + " started");
	boolean interrupted = false;
	while (!interrupted) {
	    File file = null;
	    try {
		if (LOGGER.isDebugEnabled()) {
		    int nr = queue.getBooks();
		    LOGGER.debug("Queue size is " + nr + " - now taking a file from the queue or waiting...");
		}
		extractTask = queue.take();
		busy = true;
		extractThumbnails();
		busy = false;
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Processing " + extractTask);
		}
	    } catch (InterruptedException e) {
		interrupted = true;
		LOGGER.info("Thread " + threadName + " interrupted");
	    } catch (Throwable t) {
		String errorMessage = threadName + " - Cannot extract image from file " + file + " because " + t.getMessage();
		if (extractTask != null) {
		    LOGGER.warn("Blacklisting: " + extractTask.getBook().getLabel());
		    String bookRelativeFileName = extractTask.getBook().getRelativeFileName();
		    File bookFile = user.getFile(bookRelativeFileName);
		    new BlockList(user).add(bookFile);
		}
		LOGGER.error(errorMessage, t);
	    }
	}
	busy = false;
	LOGGER.info("Thread " + threadName + " ended");
    }

    private void extractThumbnails() throws IOException, InterruptedException {
	Book book = extractTask.getBook();
	File file = user.getFile(book.getRelativeFileName());
	try {
	    if (BookFilesFinder.isPdfFile(file)) {
		parser = new SheetMusicPdfFileParser(file);
	    } else {
		parser = new SheetMusicPngJpgParser(user, file);
	    }
	    parser.createThumbs(extractTask.getThumbsToExtract());
	} finally {
	    if (parser != null) {
		parser.close();
	    }
	}
	parser = null;
    }

    @Override
    public String getBook() {
	String result = null;
	if (busy && extractTask != null) {
	    result = extractTask.getBook().getRelativeFileName();
	}
	return result;
    }

    @Override
    public int getThumbsToExtract() {
	int result = 0;
	if (busy && extractTask != null) {
	    result = extractTask.getThumbsToExtract().size();
	}
	return result;
    }

    @Override
    public int getThumbsExtracted() {
	int result = 0;
	if (busy && parser != null) {
	    result = parser.getImagesCreated();
	}
	return result;
    }

    @Override
    public int getThumbsRemaining() {
	int result = 0;
	if (busy) {
	    result = getThumbsToExtract() - getThumbsExtracted();
	}
	return result;
    }

    @Override
    public boolean isBusy() {
	return busy;
    }
}
