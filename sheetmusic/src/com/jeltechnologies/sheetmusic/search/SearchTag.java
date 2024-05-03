package com.jeltechnologies.sheetmusic.search;

import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;
import com.jeltechnologies.sheetmusic.tags.BaseTag;
import com.jeltechnologies.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchTag extends BaseTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchTag.class);
    
    @Override
    public void processTag() throws Exception {
	String query = getRequestParameter("q");
	SearchResults results = new Library(getUser(),new SheetMusicContext(getRequest())).search(query);
	addStartListHtml();
	String searchEncoded = StringUtils.encodeURL(query);
	for (SearchResult result : results.getResults()) {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Query: " + query + ". Result: " + result.getLabel() + ". Ranking: " + result.getRanking());
	    }
	    String link = createBookPageLink(result.getChecksum(), result.getPage()) + "&search=" + searchEncoded;
	    String html = createThumb(result.getLabel(), link, 0, result.getChecksum(), result.getPage());
	    addLine(html);
	}
	addEndListHtml();
    }
}
