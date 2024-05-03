package com.jeltechnologies.sheetmusic.pdf;

import java.io.Serializable;

public class PdfPage implements Serializable {
    private static final long serialVersionUID = -6683853951222767483L;
    private int pageNumber;
    private String title;

    public int getPageNumber() {
	return pageNumber;
    }

    public String getTitle() {
	return title;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("PdfPage [pageNumber=");
	builder.append(pageNumber);
	builder.append(", title=");
	builder.append(title);
	builder.append("]");
	return builder.toString();
    }

}
