package com.jeltechnologies.sheetmusic.servlet;

import com.jeltechnologies.sheetmusic.extractedfilestorage.ThumbnailsQueue;
import com.jeltechnologies.utils.JMXUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public class SheetMusicContext {
    
    private final ServletContext context;
    
    public SheetMusicContext(ServletContext context) {
	this.context = context;
    }
    
    public SheetMusicContext(HttpServletRequest request) {
	this.context = request.getServletContext();
    }
    
    public JMXUtils getJmx() {
	return new ContextObjectStore<JMXUtils>(JMXUtils.class).get(context);
    }
    
    public ThreadService getThreadService() {
	return new ContextObjectStore<ThreadService>(ThreadService.class).get(context);
    }
    
    public ThumbnailsQueue getThumbnailsQueue() {
	return new ContextObjectStore<ThumbnailsQueue>(ThumbnailsQueue.class).get(context);
    }
}
