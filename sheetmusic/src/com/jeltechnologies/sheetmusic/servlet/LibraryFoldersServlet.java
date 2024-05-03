package com.jeltechnologies.sheetmusic.servlet;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.servlet.jstree.JSTreeData;
import com.jeltechnologies.sheetmusic.servlet.jstree.JSTreeFactory;
import com.jeltechnologies.utils.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/folders")
public class LibraryFoldersServlet extends BaseServlet {
    private static final long serialVersionUID = 8384652663226560067L;
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryFoldersServlet.class);
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	User user = getUser(request);
	String relativeFileName = request.getParameter("file");
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("doGet file: " + relativeFileName);
	}
	if (relativeFileName == null || relativeFileName.isEmpty()) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	else {
	    String folderName = StringUtils.stripAfterLast(relativeFileName, "/");
	    File selectedFolder = user.getFile(folderName);
	    JSTreeData treeData = new JSTreeFactory(user).getFolders(selectedFolder);
	    respondJson(response, treeData);
	}
    }

}
