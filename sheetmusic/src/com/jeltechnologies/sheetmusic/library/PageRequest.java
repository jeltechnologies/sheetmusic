package com.jeltechnologies.sheetmusic.library;

import java.io.Serializable;
import java.util.Objects;

public class PageRequest implements Serializable, Cloneable, Comparable<PageRequest> {
    private static final long serialVersionUID = -6078717682556554012L;
    private String title;
    private int maxWidth;
    private int maxHeight;
    private int startPage;
    private int nrOfPagesInSingleImage;
    private String id;

    public PageRequest() {
    }
    
    public PageRequest(int maxWidth, int maxHeight, int startPage, int nrOfPagesInSingleImage, String id, String title) {
	this.maxHeight = maxHeight;
	this.maxWidth = maxWidth;
	this.nrOfPagesInSingleImage = nrOfPagesInSingleImage;
	this.startPage = startPage;
	this.title = title;
	this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
	return title;
    }

    public int getStartPage() {
	return startPage;
    }

    public int getMaxWidth() {
	return maxWidth;
    }

    public int getMaxHeight() {
	return maxHeight;
    }

    public int getNrOfPagesInSingleImage() {
	return nrOfPagesInSingleImage;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public void setMaxWidth(int maxWidth) {
	this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
	this.maxHeight = maxHeight;
    }

    public void setStartPage(int startPage) {
	this.startPage = startPage;
    }

    public void setNrOfPagesInSingleImage(int nrOfPagesInSingleImage) {
	this.nrOfPagesInSingleImage = nrOfPagesInSingleImage;
    }

    public Object clone() {
	PageRequest clone = new PageRequest(maxWidth, maxHeight, startPage, nrOfPagesInSingleImage, id, title);
	return clone;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("PageRequest [title=");
	builder.append(title);
	builder.append(", maxWidth=");
	builder.append(maxWidth);
	builder.append(", maxHeight=");
	builder.append(maxHeight);
	builder.append(", startPage=");
	builder.append(startPage);
	builder.append(", nrOfPagesInSingleImage=");
	builder.append(nrOfPagesInSingleImage);
	builder.append(", id=");
	builder.append(id);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int compareTo(PageRequest o) {
	return this.title.compareTo(o.title);
    }

    @Override
    public int hashCode() {
	return Objects.hash(id, maxHeight, maxWidth, nrOfPagesInSingleImage, startPage, title);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	PageRequest other = (PageRequest) obj;
	return Objects.equals(id, other.id) && maxHeight == other.maxHeight && maxWidth == other.maxWidth && nrOfPagesInSingleImage == other.nrOfPagesInSingleImage
		&& startPage == other.startPage && Objects.equals(title, other.title);
    }

}
