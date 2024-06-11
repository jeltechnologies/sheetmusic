package com.jeltechnologies.screenmusic.tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.booksview.BooksInViewSessionBean;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.Folder;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;
import com.jeltechnologies.utils.StringUtils;

public class LibraryTag extends BaseTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryTag.class);

    private String relativeFolderName;
    private List<Folder> foldersInFolder;
    private List<Book> booksInFolder;

    @Override
    public void processTag() throws Exception {
	relativeFolderName = getRequestParameter("folder");
	if (relativeFolderName == null || relativeFolderName.equals("")) {
	    relativeFolderName = "/";
	}
	LOGGER.trace("relativeFolderName: " + relativeFolderName);
	User user = getUser();
	LOGGER.trace("User: " + user);
	Library library = new Library(user, new ScreenMusicContext(getRequest()));
	foldersInFolder = library.getFoldersInFolder(relativeFolderName);
	Collections.sort(foldersInFolder);
	LOGGER.trace("foldersInFolder " + foldersInFolder);
	booksInFolder = library.getBooksFromFolder(relativeFolderName);
	LOGGER.trace("booksInFolder " + booksInFolder);
	addTitle();
	processFolder();
	storeBooksInView();
    }

    private void addTitle() throws Exception {
	List<String> parts = new ArrayList<String>();
	parts.add("/");
	List<String> pathParts = StringUtils.split(relativeFolderName, '/');
	for (String part : pathParts) {
	    parts.add(part);
	}
	StringBuilder s = new StringBuilder();
	String relativeFolderName = "/";
	boolean firstPart = true;
	for (String part : parts) {
	    if (firstPart) {
		relativeFolderName = "/";
		firstPart = false;
	    } else {
		relativeFolderName = relativeFolderName + "/" + part;
		s.append(" / ");
	    }
	    String title;
	    if (part.equals("/")) {
		title = "Folders";
	    } else {
		title = part;
	    }
	    s.append("<a href=\"folders.jsp");
	    if (!relativeFolderName.equals("/")) {
		s.append("?folder=").append(StringUtils.encodeURL(relativeFolderName));
	    }
	    s.append("\">").append(title).append("</a>");
	}
	String breadCrumbs = s.toString();
	add("<div class=\"pageTitle\">");
	add(breadCrumbs);
	addLine("</div>");
    }

    private void processFolder() throws Exception {
	addStartListHtml();
	for (Folder folderInFolder : foldersInFolder) {
	    addLine(folderToHtml(folderInFolder));
	}
	if (booksInFolder != null) {
	    for (Book book : booksInFolder) {
		addLine(bookToHtml(book));
	    }
	}
	addEndListHtml();
    }
    
    private void storeBooksInView() throws Exception {
	List<String> booksInView;
	if (booksInFolder == null) {
	    booksInView = new ArrayList<String>();
	} else {
	    booksInView = new ArrayList<String>(booksInFolder.size());
	    for (Book book : booksInFolder) {
		booksInView.add(book.getFileChecksum());
	    }
	}
	BooksInViewSessionBean viewBean = BooksInViewSessionBean.getBooksView(getSession());
	viewBean.setBooksInView(booksInView);
    }
    
    private String folderToHtml(Folder folder) throws Exception {
	String relativeFolderName = folder.getPath();
	String title = folder.getTitle();
	int size = folder.getFiles();
	String link = "folders.jsp?folder=" + StringUtils.encodeURL(relativeFolderName);
	return createThumb(title, link, size, null, -1);
    }

    private String bookToHtml(Book book) {
	String title = book.getLabel();
	int size = book.getNrOfPages();
	String link = "book.jsp?id=" + book.getFileChecksum();
	return createThumb(title, link, size, book.getFileChecksum(), 1);
    }
}
