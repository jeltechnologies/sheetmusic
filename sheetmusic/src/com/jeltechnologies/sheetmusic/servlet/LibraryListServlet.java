package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;
import java.util.List;

import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.Folder;
import com.jeltechnologies.sheetmusic.library.Library;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/list")
public class LibraryListServlet extends BaseServlet {
    private static final long serialVersionUID = 1739283592682873401L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String relativeFolderName = request.getParameter("folder");
	if (relativeFolderName == null || relativeFolderName.isEmpty()) {
	    relativeFolderName = "/";
	}
	Library library = new Library(getUser(request), new SheetMusicContext(request));
	LibraryList list = new LibraryList();
	List<Book> books = library.getBooksFromFolder(relativeFolderName);
	List<Folder> folders = library.getFoldersInFolder(relativeFolderName);
	list.setBooks(books);
	list.setFolders(folders);
	respondJson(response, list);
    }
}
