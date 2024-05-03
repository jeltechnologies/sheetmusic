package com.jeltechnologies.sheetmusic.statistics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.db.DBCrud;
import com.jeltechnologies.sheetmusic.history.BookViews;
import com.jeltechnologies.sheetmusic.history.History;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.tags.BaseTag;

public class MostPopularBooksTag extends BaseTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(MostPopularBooksTag.class);
    
    private int top;

    @Override
    public void processTag() throws Exception {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("processTag");
	}

	addStartListHtml();
	DBCrud db = null;
	try {
	    String user = getUser().name();
	    db = new DBCrud();
	    List<BookViews> mostPopular = new History(user).getMostPopularBooks(top);
	    for (BookViews bookViews : mostPopular) {
		Book book = bookViews.getBook();
		String title = book.getLabel() + " (" + bookViews.getViews() + " views)";
		String link = "book.jsp?id=" + book.getFileChecksum();
		String thumbHtml = createThumb(title, link, 0, book.getFileChecksum(), 1);
		addLine(thumbHtml);
	    }

	} finally {
	    if (db != null) {
		db.close();
	    }
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
