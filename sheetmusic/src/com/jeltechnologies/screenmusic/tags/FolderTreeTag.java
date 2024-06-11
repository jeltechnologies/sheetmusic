package com.jeltechnologies.screenmusic.tags;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.utils.StringUtils;

public class FolderTreeTag extends BaseTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderTreeTag.class);

    private List<String> ids;

    private String defaultNodeID;

    private String defaultfolder = "";

    public String getDefaultfolder() {
	return defaultfolder;
    }

    public void setDefaultfolder(String defaultFolder) {
	this.defaultfolder = defaultFolder;
    }

    
    @Override
    public void processTag() throws Exception {
	String defaultFromRequest = getRequestParameter("defaultFolder");
	LOGGER.info("Default from request: " + defaultFromRequest);
	
	if (defaultFromRequest != null) {
//	    Environment env = Environment.INSTANCE;
//	    String unc = env.getRelativeRootUncategorized();
//	    String afterRoot = StringUtils.stripBefore(defaultFromRequest, unc);
//	    LOGGER.info(afterRoot);
	    setDefaultfolder(defaultFromRequest);
	}
	
	defaultNodeID = null;
	// defaultFoldersRoot = null;
	ids = new ArrayList<String>();

	addLine("<div id=\"" + id + "\">");
	
	File root = getUser().getSheetMusicFolder();
	addFolder(root);
	addLine("</div>");

	addLine("<script>");
	StringBuilder b = new StringBuilder();
	b.append("var albumTreeIds = [ ");
	for (int i = 0; i < ids.size(); i++) {
	    String id = ids.get(i);
	    if (i > 0) {
		b.append(", ");
	    }
	    b.append("\"").append(id).append("\"");
	}

	b.append(" ];");
	addLine(b.toString());

	if (defaultNodeID != null) {
	    StringBuilder openNode = new StringBuilder();
	    openNode.append("var albumTreeOpenNodeID = \"").append(defaultNodeID).append("\";");
	    addLine(openNode.toString());
	}

	addLine("</script>");
    }

    private void addFolder(File folder) throws Exception {
	if (folder.isDirectory()) {

//	    String relativeFolder = ENV.getRelativePhotoFileName(folder);
//	    boolean defaultExpected = relativeFolder.equals(defaultFoldersRoot);
	    
	    boolean defaultExpected = false;
	    
	    String relativeFolder = "WEGWEG";

	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace(relativeFolder + " defaultExpected: " + defaultExpected);
	    }

	    int idCounter = ids.size();
	    String nodeID = "node-" + idCounter;

	    if (relativeFolder.equals(this.defaultfolder)) {
		defaultNodeID = nodeID;
	    }

	    ids.add(relativeFolder);

	    addLine("<ul>");
	    add("<li id=\"" + nodeID + "\">");
	    add(folder.getName());
	    File[] children = new File[0]; //  folder.listFiles(new FolderFilter());
	    for (File child : children) {
		addFolder(child);
	    }

	    if (defaultExpected && defaultNodeID == null) {
		idCounter = ids.size();
		nodeID = "node-" + idCounter;
		ids.add(defaultfolder);
		defaultNodeID = nodeID;
		add("<ul><li id=\"" + nodeID + "\">");
		String name = StringUtils.stripBeforeLast(defaultfolder, "/");
		add(name);
		add("</li></ul>");
	    }

	    addLine("</li>");
	    addLine("</ul>");
	}
    }


}
