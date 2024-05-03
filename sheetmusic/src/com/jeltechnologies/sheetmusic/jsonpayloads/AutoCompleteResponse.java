package com.jeltechnologies.sheetmusic.jsonpayloads;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object for JSON response for JQuery-Automcomplete.
 * 
 * @see https://github.com/devbridge/jQuery-Autocomplete
 */
public class AutoCompleteResponse implements Serializable {
    private static final long serialVersionUID = -2693225371673475472L;
    private String query;
    
    private List<String> suggestions = new ArrayList<String>();
    
    public AutoCompleteResponse() {
    }

    public String getQuery() {
	return query;
    }

    public void setQuery(String query) {
	this.query = query;
    }

    public List<String> getSuggestions() {
	return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
	this.suggestions = suggestions;
    }

    @Override
    public String toString() {
	return "AutoCompleteResponse [query=" + query + ", suggestions=" + suggestions + "]";
    }
}
