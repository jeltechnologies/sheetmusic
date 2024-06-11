package com.jeltechnologies.screenmusic.tags;

import java.io.IOException;

import com.jeltechnologies.icons.IconTag;
import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.servlet.BaseServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

public abstract class BaseTag extends SimpleTagSupport {

    public static final String ICON_FOLDER = new IconTag("folder", 64).toString();

    protected static final int MAX_TITLE_WIDTH = 65;

    protected boolean addComments = true;

    protected String id;

    protected String cssClass;
    
    public abstract void processTag() throws Exception;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCssClass() {
	return cssClass;
    }

    public void setCssClass(String cssClass) {
	this.cssClass = cssClass;
    }
    
    protected PageContext getPageContext() {
	return (PageContext) getJspContext();
    }
    
    protected HttpSession getSession() {
	return (HttpSession) getPageContext().getSession();
    }
    
    protected HttpServletRequest getRequest() {
	return (HttpServletRequest) getPageContext().getRequest();
    }

    @Override
    public void doTag() throws JspException {
	try {
	    if (addComments) {
		addLine();
		addLine("<!-- Start " + this.getClass().getSimpleName() + " -->");
	    }
	    processTag();
	    if (addComments) {
		addLine();
		addLine("<!-- End " + this.getClass().getSimpleName() + " -->");
	    }
	} catch (Exception exception) {
	    throw new JspException("Error while processing tag " + exception.getMessage(), exception);
	}
    }

    protected void add(Object object) throws IOException {
	String line;
	if (object != null) {
	    line = object.toString();

	} else {
	    line = "null";
	}
	getJspContext().getOut().write(line);
    }
    
    protected void addLine() throws IOException {
	getJspContext().getOut().write("\r\n");
    }

    protected void addLine(String line) throws IOException {
	getJspContext().getOut().write(line + "\r\n");
    }

    protected void addParargraph(String text) throws IOException {
	add("<p>" + text + "</p>");
    }

    protected User getUser() {
	return BaseServlet.getUser(getPageContext().getRequest());
    }

    protected String getRequestParameter(String name) {
	return (String) getPageContext().getRequest().getParameter(name);
    }
    
    protected String createThumb(String title, String link, int itemsInFolder, String bookId, int page) {
	StringBuilder b = new StringBuilder();
	b.append("<a href=\"").append(link).append("\">");
	b.append("<li class=\"card-list\">");

	if (bookId != null) {
	    b.append("<img src=\"page?checksum=");
	    b.append(bookId).append("&page=").append(page).append("&size=small\"");
	    b.append(" class=\"img-card-list\">");
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
		b.append("<span class=\"card-title\">");
		b.append(description);
		b.append("</span>");
	    }
	}

	b.append("</li></a>");
	return b.toString();
    }

    protected String createBookPageLink(Book book, int pageNumber) {
	return createBookPageLink(book.getFileChecksum(), pageNumber);
    }
    
    protected String createBookPageLink(String checksum, int pageNumber) {
	StringBuilder link = new StringBuilder("page.jsp?id=").append(checksum).append("&page=").append(pageNumber);
	return link.toString();
    }

    protected void addStartListHtml() throws IOException {
	add("<ul class=\"card-list\">");
    }

    protected void addEndListHtml() throws IOException {
	add("</ul>");
    }
}
