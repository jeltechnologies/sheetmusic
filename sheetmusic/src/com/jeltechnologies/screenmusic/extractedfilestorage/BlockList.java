package com.jeltechnologies.screenmusic.extractedfilestorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.utils.FileUtils;

public class BlockList {
    private List<String> blockList = null;
    private final static Logger LOGGER = LoggerFactory.getLogger(BlockList.class);
    private final User user;
    private final String blockListFile;

    public BlockList(User user) {
	this.user = user;
	this.blockListFile = user.getSheetMusicFolder().getAbsolutePath() + "/not-working-files.txt";
	readBlockList();
    }

    private void readBlockList() {
	try {
	    List<String> fileNames = FileUtils.readTextFileLines(blockListFile, false);
	    blockList = new ArrayList<String>(fileNames.size());
	    for (String relativeFileName : fileNames) {
		blockList.add(relativeFileName);
	    }
	} catch (Exception e) {
	    LOGGER.trace("Could not load blacklist file because " + e.getMessage());
	}
	if (blockList == null) {
	    blockList = new ArrayList<String>();
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Blacklist file contains " + blockList.size() + " PDFs that are not working");
	}
    }

    public boolean contains(File file) {
	String fileRelativeName = user.getRelativeFileName(file);
	boolean blackListed = false;
	synchronized (this) {
	    Iterator<String> iterator = blockList.iterator();
	    while (iterator.hasNext() && !blackListed) {
		String currentRelativeFileName = iterator.next();
		if (currentRelativeFileName.equalsIgnoreCase(fileRelativeName)) {
		    blackListed = true;
		}
	    }
	}
	return blackListed;
    }

    public void add(File file) {
	if (file != null && !contains(file)) {
	    String fileRelativeName = user.getRelativeFileName(file);
	    LOGGER.info("Adding to blacklist: " + fileRelativeName);
	    synchronized (this) {
		blockList.add(fileRelativeName);
	    }
	    try {
		List<String> lines = new ArrayList<String>(blockList.size());
		for (String blacklisted : blockList) {
		    lines.add(blacklisted);
		}
		FileUtils.writeTextFile(blockListFile, lines);
	    } catch (IOException e) {
		LOGGER.warn("Cannot write blacklist file", e);
	    }
	}
    }
}
