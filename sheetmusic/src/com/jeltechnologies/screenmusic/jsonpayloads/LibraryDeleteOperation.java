package com.jeltechnologies.screenmusic.jsonpayloads;

public class LibraryDeleteOperation {
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("LibraryDeleteOperation [file=");
	builder.append(file);
	builder.append("]");
	return builder.toString();
    }
}
