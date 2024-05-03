package com.jeltechnologies.sheetmusic.search;

import java.io.Serializable;

public class SearchResult implements Serializable, Comparable<SearchResult> {
    private static final long serialVersionUID = -8001739790471310256L;
    private final String label;
    private final String checksum;
    private final int page;
    private final int ranking;
    private final static int RANKED_AS_PART_OF_A_LABEL = 1000;
    
    public SearchResult(String searchFor, String label, String checksum, int page) {
	this.label = label;
	this.checksum = checksum;
	this.page = page;
	this.ranking = calculateRanking(searchFor);
    }
    
    public SearchResult(SearchResult copyOf) {
	this.label = copyOf.label;
	this.checksum = copyOf.checksum;
	this.page = copyOf.page;
	this.ranking = copyOf.ranking;
    }

    public int getRanking() {
        return ranking;
    }

    public String getLabel() {
        return label;
    }

    public String getChecksum() {
        return checksum;
    }

    public int getPage() {
        return page;
    }
    
    private int calculateRanking(String searchFor) {
	String word = searchFor + " ";
	int index = label.toLowerCase().indexOf(word.toLowerCase());
	if (index < 0) {
	    index = label.toLowerCase().indexOf(searchFor.toLowerCase()) + RANKED_AS_PART_OF_A_LABEL;
	}
	return index;
    }

    @Override
    public int compareTo(SearchResult o) {
	return ranking - o.ranking;
    }

    @Override
    public String toString() {
	return "SearchResult [label=" + label + ", checksum=" + checksum + ", page=" + page + ", ranking=" + ranking + ", getRanking()=" + getRanking()
		+ ", getLabel()=" + getLabel() + ", getChecksum()=" + getChecksum() + ", getPage()=" + getPage() + ", getClass()=" + getClass()
		+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }    
}
