package com.jeltechnologies.sheetmusic.statistics;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.history.History;
import com.jeltechnologies.sheetmusic.history.PageViews;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.BookPage;
import com.jeltechnologies.sheetmusic.tags.BaseTag;

public class MostPopularPagesTag extends BaseTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(MostPopularPagesTag.class);
    private int top;

    @Override
    public void processTag() throws Exception {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("processTag");
	}

	addStartListHtml();
	History history = new History(getUser().name());
	List<PageViews> mostPopular = history.getMostPopularPages(top);

	for (PageViews pageViews : mostPopular) {
	    Book book = pageViews.getBook();
	    int pageNr = pageViews.getPage();
	    BookPage page = null;
	    Iterator<BookPage> i = book.getPages().iterator();
	    while (page == null && i.hasNext()) {
		BookPage current = i.next();
		if (current.getNr() == pageNr) {
		    page = current;
		}
	    }
	   
	    String label;
	    if (page != null) {
		label = page.getLabel();
	    }
	    else {
		label = "Page " + pageNr;
	    }
	    String title = label +  " (" + pageViews.getViews() + " views)";
	    
	    String link = "page.jsp?id=" + book.getFileChecksum() + "&page=" + pageNr;
	    String thumbHtml = createThumb(title, link, 0, book.getFileChecksum(), pageNr);
	    addLine(thumbHtml);
	}

	addEndListHtml();
    }

    public String getTop() {
	return String.valueOf(top);
    }

    public void setTop(String top) {
	this.top = Integer.parseInt(top);
    }

}
