package com.jeltechnologies.sheetmusic.tags;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;
import com.jeltechnologies.utils.StringUtils;

public class TitleTag extends BaseTag {

    @Override
    public void processTag() throws Exception {
	String title;
	String folder = getRequestParameter("folder");
	if (folder != null && !folder.isBlank()) {
	    title = folder;
	} else {
	    String id  = getRequestParameter("id");
	    Book book = null;
	    User user = getUser();
	    SheetMusicContext context = new SheetMusicContext(this.getPageContext().getServletContext());
	    Library library = new Library(user, context);
	    if (id != null && !id.isBlank()) {
		book = library.getBook(id);
	    } else {
		String file = getRequestParameter("file");
		if (file != null && !file.isBlank()) {
		    book = library.getBookByFileName(file);
		}
	    }
	    if (book == null) {
		title = "";
	    } else {
		title = book.getLabel() + " - ";
	    }
	}
	title = title + "Sheet music";
	String titlEncoded= StringUtils.encodeHtml(title);
	add("<title>" + titlEncoded + "</title>");
    }
}
