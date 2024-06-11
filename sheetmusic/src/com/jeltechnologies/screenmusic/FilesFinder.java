package com.jeltechnologies.screenmusic;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesFinder {
    private final static Logger LOGGER = LoggerFactory.getLogger(FilesFinder.class);

    private List<File> files;

    private final String extension;

    public FilesFinder(String extension) {
	this.extension = extension;
    }

    public List<File> getFilesRecursive(String startFolder) {
	files = new ArrayList<File>();
	File folder = new File(startFolder);
	if (folder.isDirectory()) {
	    foundFolder(folder);
	}
	return files;
    }
    
    public List<File> getFilesRecursive(File startFolder) {
	return getFilesRecursive(startFolder.getAbsolutePath());
    }

    private void foundFile(File pdf) {
	files.add(pdf);
    }

    private void foundFolder(File folder) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Found folder " + folder);
	}
	File[] pdfs = folder.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	    }
	});

	for (File pdf : pdfs) {
	    foundFile(pdf);
	}
	File[] folders = folder.listFiles(new FileFilter() {
	    @Override
	    public boolean accept(File pathname) {
		return pathname.isDirectory();
	    }
	});
	for (File childFolder : folders) {
	    foundFolder(childFolder);
	}
    }

}
