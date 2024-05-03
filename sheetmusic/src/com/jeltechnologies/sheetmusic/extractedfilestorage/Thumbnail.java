package com.jeltechnologies.sheetmusic.extractedfilestorage;

import java.io.File;

public class Thumbnail {
    private final int page;
    private final Size size;
    public final String checksum;
    private final File cachedFile;
    public static final String IMAGE_EXTENSION = "jpg";
    public static final float COMPRESSION_QUALITY = 0.7f;

    public enum Size {
	SMALL("small", 200, 300), MEDIUM("medium", 960, 1080), LARGE("large", 1920, 2160);

	private String description;
	private int width;
	private int height;

	private Size(String description, int width, int height) {
	    this.description = description;
	    this.width = width;
	    this.height = height;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public int getWidth() {
	    return width;
	}

	public void setWidth(int width) {
	    this.width = width;
	}

	public int getHeight() {
	    return height;
	}

	public void setHeight(int height) {
	    this.height = height;
	}

	@Override
	public String toString() {
	    StringBuilder b = new StringBuilder();
	    b.append(description).append(" (").append(width).append("x").append(height).append(")");
	    return b.toString();
	}
    }
    
    public Thumbnail(String checksum, int page, Size size, File cacheFolder) {
	this.checksum = checksum;
	this.page = page;
	this.size = size;
	StringBuilder relName = new StringBuilder();
	relName.append(checksum).append("/").append(size.description).append("/img-");
	relName.append(page).append("-").append(size.description);
	relName.append(".").append(IMAGE_EXTENSION);
	String relativeFileName = relName.toString();
	String absoluteFileName = cacheFolder.getAbsolutePath() + "/" + relativeFileName;
	this.cachedFile = new File(absoluteFileName);
    }

    public int getPage() {
        return page;
    }

    public Size getSize() {
        return size;
    }

    public String getChecksum() {
	return checksum;
    }
    
    public File getCachedFile() {
	return cachedFile;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Thumbnail [page=");
	builder.append(page);
	builder.append(", size=");
	builder.append(size);
	builder.append(", checksum=");
	builder.append(checksum);
	builder.append("]");
	return builder.toString();
    }

}
