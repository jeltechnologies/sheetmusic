package com.jeltechnologies.sheetmusic;

import com.jeltechnologies.sheetmusic.jsonpayloads.AutoCompleteResponse;
import com.jeltechnologies.sheetmusic.search.SearchResults;

public interface BookFinder {
    SearchResults search(String word) throws InterruptedException;
    AutoCompleteResponse findSuggestions(String word);
}
