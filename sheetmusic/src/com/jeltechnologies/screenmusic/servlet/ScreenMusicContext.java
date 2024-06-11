package com.jeltechnologies.screenmusic.servlet;

import com.jeltechnologies.screenmusic.extractedfilestorage.ThumbnailsQueue;
import com.jeltechnologies.utils.JMXUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public class ScreenMusicContext {
    
    private final ServletContext context;
    
    public ScreenMusicContext(ServletContext context) {
	this.context = context;
    }
    
    public ScreenMusicContext(HttpServletRequest request) {
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
