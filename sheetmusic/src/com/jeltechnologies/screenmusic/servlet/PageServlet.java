package com.jeltechnologies.screenmusic.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.config.Configuration;
import com.jeltechnologies.screenmusic.extractedfilestorage.Thumbnail;
import com.jeltechnologies.screenmusic.extractedfilestorage.ThumbnailsExtractTask;
import com.jeltechnologies.screenmusic.extractedfilestorage.ThumbnailsQueue;
import com.jeltechnologies.screenmusic.extractedfilestorage.Thumbnail.Size;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.Library;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/page/*")
public class PageServlet extends BaseServlet {
    private static final long serialVersionUID = 1770728183037297254L;
    private final static Logger LOGGER = LoggerFactory.getLogger(PageServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug(request.getQueryString());
	}

	String file = request.getParameter("file");
	String pageString = request.getParameter("page");
	String askedSize = request.getParameter("size");
	String checksum = request.getParameter("checksum");

	if (pageString == null) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	} else {
	    if (checksum == null && file == null) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    } else {
		if (checksum == null) {
		    Library library = new Library(getUser(request), new ScreenMusicContext(request));
		    Book book = library.getBookByFileName(file);
		    if (book == null) {
			LOGGER.warn("Cannot find book file: " + file + ", checksum: " + checksum);
		    } else {
			checksum = book.getFileChecksum();
		    }
		}
		if (checksum == null) {
		    response.setStatus(404);
		} else {
		    Size size;
		    if (askedSize == null) {
			size = Size.SMALL;
		    } else {
			if (askedSize.equalsIgnoreCase("small")) {
			    size = Size.SMALL;
			} else {
			    if (askedSize.equalsIgnoreCase("medium")) {
				size = Size.MEDIUM;
			    } else {
				if (askedSize.equalsIgnoreCase("large")) {
				    size = Size.LARGE;
				} else {
				    size = Size.MEDIUM;
				}
			    }
			}
		    }
		    int page = Integer.parseInt(pageString);
		    File cacheFolderExtracted = Configuration.getInstance().storage().getCacheFolderExtracted();
		    Thumbnail thumb = new Thumbnail(checksum, page, size, cacheFolderExtracted);
		    File cachedFile = thumb.getCachedFile();
		    if (!cachedFile.exists()) {
			Library library = new Library(getUser(request), new ScreenMusicContext(request));
			Book book = library.getBookWithFileName(checksum);
			if (book != null) {
			    tryToExtractMissingThumb(request.getServletContext(), book, thumb);
			}
		    }
		    if (!cachedFile.exists()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    } else {
			respondBinaryFile(response, request.getServletContext(), cachedFile, false);
		    }
		}
	    }
	}
    }

    private void tryToExtractMissingThumb(ServletContext context, Book book, Thumbnail thumbnail) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("tryToExtractMissingThumb for " + thumbnail + " and book " + book);
	}
	List<Thumbnail> thumbs = new ArrayList<>(1);
	thumbs.add(thumbnail);
	ThumbnailsExtractTask task = new ThumbnailsExtractTask(book, thumbs);
	ThumbnailsQueue queue = new ScreenMusicContext(context).getThumbnailsQueue();
	queue.add(task);
	File file = thumbnail.getCachedFile();
	int maxAttempts = 10;
	int attempt = 0;
	try {
	    do {
		Thread.sleep(1000);
		attempt++;
	    } while (!file.isFile() && attempt < maxAttempts);
	} catch (InterruptedException ie) {
	    LOGGER.info("GetPage interrupted");
	}
	if (LOGGER.isDebugEnabled()) {
	    boolean found = file.isFile();
	    LOGGER.debug("tryToExtractMissingThumb for " + thumbnail + ". Found: " + found);
	}

    }

}
