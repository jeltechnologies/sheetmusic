package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobList implements JobListMBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobList.class);
    private static final JobList INSTANCE = new JobList();

    private final List<Job> jobs = new ArrayList<Job>();

    private JobList() {
    }

    public static JobList getInstance() {
	return INSTANCE;
    }

    public void add(Job job) {
	this.jobs.add(job);
    }

    public boolean remove(Job job) {
	job.cancel();
	cleanFiles(job);
	return jobs.remove(job);
    }

    private void cleanFiles(Job job) {
	File input = job.getInputFile();
	if (input != null && input.isFile()) {
	    boolean ok = input.delete();
	    if (!ok) {
		LOGGER.trace("Cannot delete input file " + input);
	    }
	}
	File folder = job.getOutputFolder();
	if (folder != null) {
	    for (File file : folder.listFiles()) {
		boolean ok = file.delete();
		if (!ok) {
		    LOGGER.trace("Cannot delete generated OCR file " + file);
		}
	    }
	    boolean ok = folder.delete();
	    if (!ok) {
		LOGGER.trace("Cannot delete temp folder " + folder);
	    }
	}
    }

    public Job searchById(String user, String id) {
	Job found = null;
	Iterator<Job> iterator = jobs.iterator();
	while (found == null && iterator.hasNext()) {
	    Job current = iterator.next();
	    if (current.getId().equals(id)) {
		found = current;
	    }
	}
	if (found != null) {
	    if (!found.getUserName().equals(user)) {
		found = null;
	    }
	}
	return found;
    }

    public List<Job> getAllForUser(String userName) {
	List<Job> result = new ArrayList<>();
	for (Job job : jobs) {
	    if (job.getUserName().equals(userName)) {
		result.add(job);
	    }
	}
	Collections.sort(result, new Comparator<Job>() {
	    @Override
	    public int compare(Job o1, Job o2) {
		return o2.getStartTime().compareTo(o1.getStartTime());
	    }
	});
	return result;
    }

    @Override
    public int getSize() {
	return jobs.size();
    }

}
