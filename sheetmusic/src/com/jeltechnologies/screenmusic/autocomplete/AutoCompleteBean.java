package com.jeltechnologies.screenmusic.autocomplete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.jsonpayloads.AutoCompleteResponse;
import com.jeltechnologies.screenmusic.jsonpayloads.RatedSuggestion;

public abstract class AutoCompleteBean implements Serializable {
    private static final long serialVersionUID = -1568350860035146522L;
    private List<String> lines = new ArrayList<String>();
    private List<String> lowerLines = new ArrayList<String>();
    protected final User user;

    public AutoCompleteBean(User user) {
	this.user = user;
    }

    protected void add(List<String> lines) {
	for (String line : lines) {
	    add(line);
	}
    }

    protected void add(String line) {
	this.lines.add(line);
	this.lowerLines.add(line.toLowerCase());
    }

    public AutoCompleteResponse getAutoCompleteSuggestions(String query) {
	String queryLower = query.toLowerCase();
	Map<String, RatedSuggestion> ratedSuggestionMap = new HashMap<String, RatedSuggestion>();
	for (int i = 0; i < lines.size(); i++) {
	    String line = lines.get(i);
	    String lowerLine = lowerLines.get(i);
	    int position = lowerLine.indexOf(queryLower);
	    if (position > -1) {
		RatedSuggestion ratedSuggestion = ratedSuggestionMap.get(line);
		if (ratedSuggestion == null) {
		    ratedSuggestion = new RatedSuggestion(position, line);
		    ratedSuggestionMap.put(line, ratedSuggestion);
		} else {
		    int existingRating = ratedSuggestion.getRate();
		    if (existingRating < position) {
			ratedSuggestion.setRate(position);
		    }
		}
	    }
	}
	List<RatedSuggestion> sortedSuggestions = new ArrayList<RatedSuggestion>();
	for (String word : ratedSuggestionMap.keySet()) {
	    RatedSuggestion suggestion = ratedSuggestionMap.get(word);
	    sortedSuggestions.add(suggestion);
	}
	Collections.sort(sortedSuggestions);
	AutoCompleteResponse response = new AutoCompleteResponse();
	response.setQuery(query);
	List<String> suggestionsInResponse = new ArrayList<String>();
	response.setSuggestions(suggestionsInResponse);
	for (RatedSuggestion ratedSuggestion : sortedSuggestions) {
	    suggestionsInResponse.add(ratedSuggestion.getSuggestion());
	}
	return response;
    }

}
