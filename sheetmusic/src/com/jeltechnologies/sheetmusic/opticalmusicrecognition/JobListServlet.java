package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.servlet.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tasks-musicxml")
public class JobListServlet extends BaseServlet {
    private static final long serialVersionUID = -3259131159661406737L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobListServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	User user = getUser(request);
	String idParameter = request.getParameter("id");
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("doGet: user:" + user.name() + ", id: " + idParameter);
	}
	JobList list = JobList.getInstance();
	if (idParameter == null) {
	    List<Job> jobs = list.getAllForUser(user.name());
	    respondJson(response, jobs);
	} else {
	    Job job = list.searchById(user.name(), idParameter);
	    if (job != null) {
		respondJson(response, job);
	    } else {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    }
	}
    }
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	User user = getUser(request);
	String idParameter = request.getParameter("id");
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("doDelete: user:" + user.name() + ", id: " + idParameter);
	}
	JobList list = JobList.getInstance();
	Job job = list.searchById(user.name(), idParameter);
	if (job == null) {
	    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	} else {
	    list.remove(job);
	}
    }
    
}
