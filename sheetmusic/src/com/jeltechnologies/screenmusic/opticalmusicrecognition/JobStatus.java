package com.jeltechnologies.screenmusic.opticalmusicrecognition;

public enum JobStatus {
    QUEUED {
	public String toString() {
	    return "Queued";
	}
    },
    PROCESSING{
	public String toString() {
	    return "In progress";
	}
    }, READY {
	public String toString() {
	    return "Ready for download";
	}
    }, ERROR {
	public String toString() {
	    return "Error";
	}
    }
}
