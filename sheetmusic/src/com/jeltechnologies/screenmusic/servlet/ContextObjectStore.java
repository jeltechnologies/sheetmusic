package com.jeltechnologies.screenmusic.servlet;

import jakarta.servlet.ServletContext;

public class ContextObjectStore<T> {
    private final String name;
    
    public ContextObjectStore(Class<T> type) {
	this.name = type.getName();
    }
    
    public T get(ServletContext context) {
	return getAttribute(name, context, true);
    }
    
    @SuppressWarnings("unchecked")
    private T getAttribute(String name, ServletContext context, boolean crashOnError) {
	Object object = context.getAttribute(name);
	if (crashOnError && object == null) {
	    throw new IllegalStateException ("Cannot find Object " + name + " in ServletContext");
	}
	return (T) object;
    }
    
    public void set(T object, ServletContext context) {
	T existing = getAttribute(name, context, false);
	if (existing != null) {
	    throw new IllegalStateException ("Context variable " + name + " is already set. Can only set once to avoid race conditions");
	}
	context.setAttribute(name, object);
    }
}
