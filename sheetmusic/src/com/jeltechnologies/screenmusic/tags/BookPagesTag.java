package com.jeltechnologies.screenmusic.tags;

import java.util.Iterator;
import java.util.List;

import com.jeltechnologies.screenmusic.PageTitle;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.BookPage;
import com.jeltechnologies.utils.StringUtils;

import jakarta.servlet.jsp.PageContext;

public class BookPagesTag extends BaseTag {
    private final int MAX_LENGTH_LABEL = 25;

    private final int CONTENTS_PER_PAGE = 10;
    
    @Override
    public void processTag() throws Exception {
	Book book = (Book) getJspContext().getAttribute("book", PageContext.REQUEST_SCOPE);
	//String fileEncoded = StringUtils.encodeURL(book.getRelativeFileName());
	
	addLine("<ul class=\"container float\">");

	boolean tableOfContentsInserted = false;
	int nrOfPages = book.getNrOfPages();
	for (int pageNumber = 1; pageNumber <= nrOfPages; pageNumber++) {
	    BookPage found = null;
	    Iterator<BookPage> iterator = book.getPages().iterator();
	    while (found == null && iterator.hasNext()) {
		BookPage current = iterator.next();
		if (current.getNr() == pageNumber) {
		    found = current;
		}
	    }
	    String link = createBookPageLink(book, pageNumber);
	    String html;
	    String label = null;
	    if (found != null) {
		label = StringUtils.encodeHtml(found.getLabel());
		html = createThumb(label, link, 0, book.getFileChecksum(), pageNumber);
	    } else {
		html = createThumb(null, link, 0, book.getFileChecksum(), pageNumber);
	    }

	    if (pageNumber == 1 && label != null && !label.isBlank()) {
		addLine(getTableOfContents(book));
		tableOfContentsInserted = true;
	    } else {

		if (!tableOfContentsInserted && pageNumber == 2) {
		    addLine(getTableOfContents(book));
		    tableOfContentsInserted = true;
		}
	    }

	    addLine(html);
	}

	addLine("</ul>");
    }
    
    @Override
    protected String createThumb(String title, String link, int itemsInFolder, String thumbImageUrl, int page) {
  	StringBuilder b = new StringBuilder();
  	b.append("<li class=\"item float-item\">");
  	b.append("<a href=\"").append(link).append("\">");

  	if (thumbImageUrl != null) {
  	    b.append("<img loading=\"lazy\" class=\"item-image\" src=\"page?checksum=");
  	    b.append(thumbImageUrl).append("&page=").append(page).append("&size=small\">");
  	} else {
  	    b.append("<span class=\"foldericon\">").append(ICON_FOLDER).append("</span>");
  	}

  	if (title != null) {
  	    if (title.length() > MAX_TITLE_WIDTH) {
  		title = title.substring(0, MAX_TITLE_WIDTH) + "...";
  	    }

  	    if (!title.equals("")) {
  		String description = title;
  		if (itemsInFolder > 0) {
  		    description = description + " (" + itemsInFolder + ")";
  		}
  		b.append("<span class=\"title\">");
  		b.append(description);
  		b.append("</span>");
  	    }
  	}
  	b.append("</a></li>");
  	return b.toString();
      }

   private String getTableOfContents(Book book) {
	StringBuilder contents = new StringBuilder();
	startLiAddTitle(contents);
	int linksOnTile = -1;
	List<PageTitle> titles = book.getContents();
	for (PageTitle title : titles) {
	    linksOnTile++;
	    if (linksOnTile == CONTENTS_PER_PAGE) {
		startLiAddTitle(contents);
		linksOnTile = 0;
	    }
	    String link = createBookPageLink(book, title.getPage());
	    StringBuilder h = new StringBuilder();
	    h.append("<div class=\"table-contents-link\">");
	    String label = title.getTitle();
	    if (label.length() > MAX_LENGTH_LABEL) {
		label = label.substring(0, MAX_LENGTH_LABEL) + "...";
	    }
	    h.append("<a href=\"").append(link).append("\">").append(label).append("</a>");
	    h.append("</div>");
	    contents.append(h);
	}
	contents.append("</li>");
	String result;
	if (titles.isEmpty()) {
	    result = "";
	} else {
	    result = contents.toString();
	}
	return result;
    }

    private void startLiAddTitle(StringBuilder contents) {
	contents.append("<li class=\"item float-item\">");
	contents.append("<div class=\"table-contents-header\">Contents</div>");
    }

}
