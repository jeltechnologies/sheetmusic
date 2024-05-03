package com.jeltechnologies.sheetmusic.library;

import java.io.Serializable;

public class Folder implements Serializable {
    private static final long serialVersionUID = 160568644792584173L;
    private String path;
    private String title;
    private int files;

    public String getPath() {
	return path;
    }

    public void setPath(String relativeFileName) {
	this.path = relativeFileName;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public int getFiles() {
	return files;
    }

    public void setFiles(int sheetmusicFiles) {
	this.files = sheetmusicFiles;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Folder [path=");
	builder.append(path);
	builder.append(", title=");
	builder.append(title);
	builder.append(", files=");
	builder.append(files);
	builder.append("]");
	return builder.toString();
    }

}
