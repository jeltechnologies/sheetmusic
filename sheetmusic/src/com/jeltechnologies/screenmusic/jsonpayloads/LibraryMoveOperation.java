package com.jeltechnologies.screenmusic.jsonpayloads;

public class LibraryMoveOperation {
    private String file;
    private String tofolder;

    public String getFile() {
	return file;
    }

    public void setFile(String file) {
	this.file = file;
    }

    public String getTofolder() {
	return tofolder;
    }

    // because of limitation in jstree
    public void setTofolder(String[] tofolder) {
	this.tofolder = tofolder[0];
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("LibraryMoveOperation [file=");
	builder.append(file);
	builder.append(", tofolder=");
	builder.append(tofolder);
	builder.append("]");
	return builder.toString();
    }
}
