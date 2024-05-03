package com.jeltechnologies.sheetmusic;

import java.io.Serializable;

public class PageTitle implements Serializable, Comparable<PageTitle> {
    private static final long serialVersionUID = -6461717639827562969L;
    private int page;
    private String title;

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    @Override
    public String toString() {
	return "PageTitle [page=" + page + ", title=" + title + "]";
    }
    
    @Override
    public int compareTo(PageTitle o) {
	return title.compareTo(o.title);
    }
}
