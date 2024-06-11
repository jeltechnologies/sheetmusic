package com.jeltechnologies.screenmusic.servlet;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.pdf.PdfExtractor;
import com.jeltechnologies.utils.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download/*")
public class DownloadServlet extends BaseServlet {
    private static final long serialVersionUID = 714410171391346350L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String requestURI = request.getRequestURI();
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("doGet " + requestURI);
	}
	String downloadArtifact = StringUtils.findAfterIfNotFoundReturnIn(requestURI, "/download/");
	String bookId = downloadArtifact;

	String fromParam = request.getParameter("from");
	String toParam = request.getParameter("to");

	if (bookId == null || bookId.isEmpty()) {
	    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
	} else {
	    User user = getUser(request);
	    Library lib = new Library(user, new ScreenMusicContext(request));
	    Book book = lib.getBookWithFileName(bookId);
	    if (book == null) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    } else {
		if (fromParam == null || fromParam.isEmpty()) {
		    File bookFile = user.getFile(book.getRelativeFileName());
		    int status = respondBinaryFile(response, request.getServletContext(), bookFile, true);
		    response.setStatus(status);
		} else {
		    try {
			int from = Integer.parseInt(fromParam);
			int to = Integer.parseInt(toParam);
			serveBook(request, response, lib, user, book, from, to);
		    } catch (NumberFormatException nfe) {
			LOGGER.warn("Syntax error in from or to");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    }
		}
	    }
	}
    }

    private void serveBook(HttpServletRequest request, HttpServletResponse response, Library library, User user, Book book, int from, int to) throws ServletException, IOException {
	PdfExtractor extractor = new PdfExtractor();
	try {
	    String outFileName = library.getPageLabel(book, from, to);
	    File tempFile = extractor.extractPdf(user, outFileName, book, from, to);
	    int status = respondBinaryFile(response, request.getServletContext(), tempFile, true);
	    response.setStatus(status);
	    tempFile.delete();
	} catch (Exception e) {
	    LOGGER.warn("Cannot extract PDF " + book.getRelativeFileName() + ", " + from + ", " + to);
	}
    }
    
   

}
