package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeltechnologies.sheetmusic.autocomplete.AutoCompleteSearchServlet;
import com.jeltechnologies.sheetmusic.autocomplete.AutoCompleteSeriesServlet;
import com.jeltechnologies.sheetmusic.extractedfilestorage.RefreshBookThread;
import com.jeltechnologies.sheetmusic.jsonpayloads.LibraryDeleteOperation;
import com.jeltechnologies.sheetmusic.jsonpayloads.LibraryMoveOperation;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.BookPage;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.utils.JsonUtils;
import com.jeltechnologies.utils.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/book")
public class LibraryBookServlet extends BaseServlet {
    private static final long serialVersionUID = 1739283592682873401L;
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryBookServlet.class);
    private final static boolean INCLUDE_CATEGORIES = true;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String queryString = request.getQueryString();
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("LibraryBinaryFileImpl Servlet doGet queryString: [" + queryString + "]");
	}

	Book book = null;
	String id = request.getParameter("id");

	if (id != null && !id.isBlank()) {
	    book = new Library(getUser(request), new SheetMusicContext(request)).getBookWithCategories(id);
	} else {
	    String origFileName = request.getParameter("file");
	    if (origFileName != null && !origFileName.isBlank()) {
		String relativeFileName = null;
		if (origFileName != null && !origFileName.isEmpty()) {
		    relativeFileName = StringUtils.decodeURL(origFileName);
		    if (!relativeFileName.startsWith("/")) {
			relativeFileName = "/" + relativeFileName;
		    }
		}
		if (relativeFileName != null && !relativeFileName.isEmpty()) {
		    book = new Library(getUser(request), new SheetMusicContext(request)).getBookByFileName(relativeFileName, INCLUDE_CATEGORIES);
		}
	    }
	}

	if (book != null) {
	    respondJson(response, book);
	} else {
	    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String body = getBody(request);
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("doPost with body : " + body);
	}
	LibraryOperation operation = null;
	try {
	    operation = getOperationFromPostBody(body);
	} catch (Exception e) {
	    LOGGER.warn("Cannot parse body for post because " + e.getMessage());
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	if (operation != null) {
	    Book book = operation.getBook();
	    book.trim();

	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("doPost parsed book: " + book.toString());
		for (BookPage page : book.getPages()) {
		    LOGGER.debug(page.toString());
		}
	    }

	    String id = book.getFileChecksum();
	    if (operation.getOperation().equalsIgnoreCase("update")) {
		id = new Library(getUser(request), new SheetMusicContext(request)).updateBook(book);
	    } else {
		if (operation.getOperation().equalsIgnoreCase("refresh")) {
		    refreshBook(request, book);
		}
	    }
	    invalidateCache(request);
	    Book responseBook = new Book();
	    responseBook.setFileChecksum(id);
	    respondJson(response, responseBook);
	}
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	LOGGER.info("doPut");
	String body = getBody(request);
	ObjectMapper mapper = new ObjectMapper();
	LibraryMoveOperation operation = mapper.readValue(body, LibraryMoveOperation.class);
	if (operation == null) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	} else {
	    Library library = new Library(getUser(request), new SheetMusicContext(request));
	    Book book = library.getBookByFileName(operation.getFile());
	    if (book == null) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    } else {
		library.moveBook(book, operation.getTofolder());
		invalidateCache(request);
	    }
	}
	invalidateCache(request);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	LOGGER.info("doDelete");
	String body = getBody(request);
	ObjectMapper mapper = new ObjectMapper();
	LibraryDeleteOperation operation = mapper.readValue(body, LibraryDeleteOperation.class);
	if (operation == null) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	} else {
	    Library library = new Library(getUser(request), new SheetMusicContext(request));
	    Book book = library.getBookByFileName(operation.getFile());
	    if (book == null) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    } else {
		library.deleteFile(operation.getFile());
		invalidateCache(request);
	    }
	}
    }

    private void refreshBook(HttpServletRequest request, Book book) {
	String relativeFileName = book.getRelativeFileName();
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("Refresh " + relativeFileName);
	}
	Runnable task = new RefreshBookThread(getUser(request), new SheetMusicContext(request), relativeFileName);
	new SheetMusicContext(request.getServletContext()).getThreadService().execute(task);
	invalidateCache(request);
    }

    private LibraryOperation getOperationFromPostBody(String json) throws Exception {
	LibraryOperation operation = (LibraryOperation) new JsonUtils().fromJSON(json, LibraryOperation.class);
	return operation;
    }

    private void invalidateCache(HttpServletRequest request) {
	CachedBean.flush(request.getServletContext(), AutoCompleteSearchServlet.CACHE_BEAN);
	CachedBean.flush(request.getServletContext(), AutoCompleteSeriesServlet.CACHE_BEAN);
    }

}
