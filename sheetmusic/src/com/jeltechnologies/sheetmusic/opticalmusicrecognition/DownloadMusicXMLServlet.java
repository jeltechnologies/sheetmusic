package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.db.DBUsers;
import com.jeltechnologies.sheetmusic.jsonpayloads.UserPreferences;
import com.jeltechnologies.sheetmusic.library.Book;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.BaseServlet;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/download-musicxml")
public class DownloadMusicXMLServlet extends BaseServlet {

    private static final long serialVersionUID = 4141018645883829065L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadMusicXMLServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	User user = getUser(request);
	JobData jobData;
	try {
	    jobData = (JobData) getJsonFromBody(request, JobData.class);
	    LOGGER.info(jobData.toString());
	} catch (Exception e) {
	    throw new ServletException(e.getMessage());
	}
	jobData.setUserName(user.name());
	storeUserPreferences(jobData);
	Book book = new Library(user, new SheetMusicContext(request)).getBookWithFileName(jobData.getBookId());
	if (book == null) {
	    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	} else {
	    jobData.setBook(book);
	    Job job = new Job(jobData);
	    JobList.getInstance().add(job);
	    JobQueue.getInstance().add(job);
	    respondText(response, job.getId());
	}
    }

    private void storeUserPreferences(JobData jobData) {
	DBUsers db = null;
	try {
	    db = new DBUsers();
	    String user = jobData.getUserName();
	    UserPreferences preferences = db.getPreferences(user);
	    if (preferences == null) {
		preferences = new UserPreferences();
	    }
	    preferences.getOcr().setPreferences(jobData.getOptions());
	    db.addPreferences(user, preferences);
	} catch (Exception e) {
	    LOGGER.warn("Could not store the user preferences because " + e.getMessage(), e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

}
