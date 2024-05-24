package com.jeltechnologies.sheetmusic.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeltechnologies.sheetmusic.PageTitle;

public class Book implements Serializable, Comparable<Book> {
    private static final long serialVersionUID = 3970744063509548204L;
    private String relativeFileName;
    private int nrOfPages;
    private String title;
    private String artist;
    private String description;
    private String series;
    @JsonIgnore
    private long fileSize;
    private String fileChecksum;
    @JsonIgnore
    private Date fileLastModified;
    private List<BookPage> pages = new ArrayList<BookPage>();
    private List<Category> categories = new ArrayList<Category>();

    public Book() {
    }

    public String getLabel() {
	StringBuilder s = new StringBuilder();
	boolean hasLabel = false;
	if (artist != null && !artist.isEmpty()) {
	    s.append(artist);
	    hasLabel = true;
	}
	if (title != null && !title.isEmpty()) {
	    if (hasLabel) {
		s.append(" - ");
	    }
	    s.append(title);
	}
	
	return s.toString();
    }

    public List<PageTitle> getContents() {
	List<PageTitle> titles = new ArrayList<PageTitle>();
	for (BookPage page : pages) {
	    addTitleIfNotEmpty(titles, page.getNr(), page.getTitle());
	    addTitleIfNotEmpty(titles, page.getNr(), page.getTitle2());
	}
	Collections.sort(titles);
	return titles;
    }
    
    private void addTitleIfNotEmpty(List<PageTitle> titles, int pageNr, String title) {
	if (title != null && !title.isBlank()) {
	    PageTitle pageTitle = new PageTitle();
	    pageTitle.setPage(pageNr);
	    pageTitle.setTitle(title);
	    titles.add(pageTitle);
	}
    }

    public List<BookPage> getPages() {
	return pages;
    }

    public void setPages(List<BookPage> pages) {
	this.pages = pages;
    }

    public long getFileSize() {
	return fileSize;
    }

    public void setFileSize(long fileSize) {
	this.fileSize = fileSize;
    }

    public String getArtist() {
	return artist;
    }

    public void setArtist(String artist) {
	this.artist = artist;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getFileChecksum() {
	return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
	this.fileChecksum = fileChecksum;
    }

    public Date getFileLastModified() {
	return fileLastModified;
    }

    public void setFileLastModified(Date fileLastModified) {
	this.fileLastModified = fileLastModified;
    }

    public String getRelativeFileName() {
	return relativeFileName;
    }

    public void setRelativeFileName(String relativeFileName) {
	this.relativeFileName = relativeFileName;
    }

    public int getNrOfPages() {
	return nrOfPages;
    }

    public void setNrOfPages(int nrOfPages) {
	this.nrOfPages = nrOfPages;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    @Override
    public int hashCode() {
	return Objects.hash(nrOfPages, relativeFileName, title);
    }
    
    public void trim() {
	artist = trim(artist);
	title = trim(title);
	for (BookPage page : pages) {
	    page.trim();
	}
    }
    
    private String trim(String field) {
	if (field == null) {
	    return null;
	} else {
	    return field.trim();
	}
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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
	Book other = (Book) obj;
	return nrOfPages == other.nrOfPages && Objects.equals(relativeFileName, other.relativeFileName) && Objects.equals(title, other.title);
    }


    @Override
    public String toString() {
	return "Book [relativeFileName=" + relativeFileName + ", nrOfPages=" + nrOfPages + ", title=" + title + ", fileChecksum=" + fileChecksum + "]";
    }

    @Override
    public int compareTo(Book o) {
	int compare = getLabel().compareToIgnoreCase(o.getLabel());
	if (compare == 0) {
	    compare = relativeFileName.compareToIgnoreCase(o.relativeFileName);
	}
	return compare;
    }
}
