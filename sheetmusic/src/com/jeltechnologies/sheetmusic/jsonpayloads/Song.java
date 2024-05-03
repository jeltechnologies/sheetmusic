package com.jeltechnologies.sheetmusic.jsonpayloads;

public class Song implements Comparable<Song> {
    private String title;
    private String artist;
    private int pageNr;
    private String relativeFileName;
    private String fileChecksum;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getArtist() {
	return artist;
    }

    public void setArtist(String artist) {
	this.artist = artist;
    }

    public int getPageNr() {
	return pageNr;
    }

    public void setPageNr(int pageNr) {
	this.pageNr = pageNr;
    }

    public String getRelativeFileName() {
	return relativeFileName;
    }

    public void setRelativeFileName(String relativeFileName) {
	this.relativeFileName = relativeFileName;
    }

    public String getFileChecksum() {
	return fileChecksum;
    }

    public void setFileChecksum(String fileChecksum) {
	this.fileChecksum = fileChecksum;
    }

    @Override
    public int compareTo(Song o) {
	if (title == null) {
	    return 0;
	} else {
	    return title.compareTo(o.title);
	}
    }

}
