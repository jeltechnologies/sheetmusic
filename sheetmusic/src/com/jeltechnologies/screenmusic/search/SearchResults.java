package com.jeltechnologies.screenmusic.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchResults implements Serializable {
    private static final long serialVersionUID = -6530953170241827427L;
    private final String q;
    private final List<SearchResult> results = new ArrayList<SearchResult>();
    private List<SearchResult> sortedResults;
    private final static int MAX_RESULTS = 100;

    public SearchResults(String searchFor) {
	this.q = searchFor;
    }

    public String getQ() {
	return q;
    }

    public void add(SearchResult result) {
	this.results.add(result);
    }

    public List<SearchResult> getResults() {
	if (sortedResults == null) {
	    Collections.sort(results);
	    int size = results.size();
	    if (size > MAX_RESULTS) {
		size = MAX_RESULTS;
	    }
	    sortedResults = new ArrayList<SearchResult>(size);
	    for (int i=0;i<results.size() && i < MAX_RESULTS;i++) {
		SearchResult clone = new SearchResult(results.get(i));
		sortedResults.add(clone);
	    }
	}
	return sortedResults;
    }

    public int size() {
	return results.size();
    }

    public boolean containsBook(String checksum) {
	boolean found = false;
	for (int i = 0; i < results.size() && !found; i++) {
	    if (results.get(i).getChecksum().equals(checksum)) {
		found = true;
	    }
	}
	return found;
    }
    
    public void sort() {
	Collections.sort(results);
    }

    @Override
    public String toString() {
	return "SearchResults [q=" + q + ", results=" + getResults() + "]";
    }
    
}
