package com.jeltechnologies.sheetmusic.servlet;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings("rawtypes")
public class CachedBean implements Serializable {
    private static final long serialVersionUID = 5208052759323555753L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedBean.class);

    private Serializable bean;

    private final LocalDateTime createdTime;

    private LocalDateTime expiryTime;

    private static final int EXPIRY_TIME_MINUTES = 5;

    public CachedBean() {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Created new instance");
	}
	this.createdTime = LocalDateTime.now();
	this.expiryTime = createdTime.plusMinutes(EXPIRY_TIME_MINUTES);
    }
    
    public void flush() {
	this.bean = null;
    }

    public static Object get(HttpSession session, String name, Class clazz) {
	return get(session.getServletContext(), name, clazz);
    }

    public boolean expired() {
	return LocalDateTime.now().isAfter(this.expiryTime);
    }
    
    public static void flush(ServletContext context, String name) {
	CachedBean cache = (CachedBean) context.getAttribute(name);
	if (cache != null) {
	    cache.flush();
	}
    }

    @SuppressWarnings("unchecked")
    public static Serializable get(ServletContext context, String name, Class clazz) {
	CachedBean cache = (CachedBean) context.getAttribute(name);
	if (cache == null) {
	    try {
		cache = new CachedBean();
		context.setAttribute(name, cache);
	    } catch (Exception e) {
		LOGGER.error("Cannot create a new instance of " + clazz, e);
	    }
	}
	Serializable bean = null;
	if (cache != null) {
	    if (cache.expired()) {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Cache expired for " + name);
		}
	    } else {
		bean = cache.bean;
	    }
	    if (bean == null) {
		try {
		    bean = (Serializable) clazz.getDeclaredConstructor().newInstance();
		    cache.bean = bean;
		} catch (Exception e) {
		    LOGGER.error("Cannot create new instance of " + clazz, e);
		}
	    }
	}
	return bean;
    }
}
