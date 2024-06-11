package com.jeltechnologies.screenmusic;

import com.jeltechnologies.screenmusic.jsonpayloads.AutoCompleteResponse;
import com.jeltechnologies.screenmusic.search.SearchResults;

public interface BookFinder {
    SearchResults search(String word) throws InterruptedException;
    AutoCompleteResponse findSuggestions(String word);
}
