package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.servlet.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download-job")
public class DownloadExportJobServlet extends BaseServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadExportJobServlet.class);
    private static final long serialVersionUID = -7245492485966392108L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	User user = getUser(request);
	String idParameter = request.getParameter("id");
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("doGet: user:" + user.name() + ", id: " + idParameter);
	}
	JobList list = JobList.getInstance();
	Job job = list.searchById(user.name(), idParameter);
	if (job == null) {
	    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	} else {
	    File file = job.getOutputFile();
	    respondBinaryFile(response, request.getServletContext(), file, true);
	}
    }

}
