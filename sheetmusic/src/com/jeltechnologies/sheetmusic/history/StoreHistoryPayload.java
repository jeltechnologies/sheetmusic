package com.jeltechnologies.sheetmusic.history;

import java.io.Serializable;

public class StoreHistoryPayload implements Serializable {
    private static final long serialVersionUID = -8864866898553826251L;
    private String id;
    private int page;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    @Override
    public String toString() {
	return "StoreHistoryPayload [id=" + id + ", page=" + page + "]";
    }
    
}
