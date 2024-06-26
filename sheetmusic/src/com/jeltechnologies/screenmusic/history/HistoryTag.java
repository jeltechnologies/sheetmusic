package com.jeltechnologies.screenmusic.history;

import java.util.List;

import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.BookPage;
import com.jeltechnologies.screenmusic.tags.BaseTag;

public class HistoryTag extends BaseTag {
    @Override
    public void processTag() throws Exception {
	addStartListHtml();
	List<PageView> views = new History(getUser().name()).getPageViews();
	
	for (PageView view : views) {
	    Book book = view.getBook();
	    BookPage page = view.getPage();
	    int pageNr = page.getNr();
	    String title = page.getLabel();

	    String link = "page.jsp?id=" + book.getFileChecksum() + "&page=" + pageNr;
	    
	    String thumbHtml = createThumb(title, link, 0, book.getFileChecksum(), pageNr);
	    addLine(thumbHtml);
	}
	addEndListHtml();
    }
}
