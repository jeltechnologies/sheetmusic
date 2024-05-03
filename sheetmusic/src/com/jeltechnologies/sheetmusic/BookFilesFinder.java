package com.jeltechnologies.sheetmusic;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.utils.StringUtils;

public class BookFilesFinder {
    private final static Logger LOGGER = LoggerFactory.getLogger(BookFilesFinder.class);

    private List<File> files;

    private final String extension;
    
    private final File sheetFolder;
    
    //private static final String CACHE_FOLDER_PATH = Environment.getInstance().getConfiguration().storage().getCacheFolder().getAbsolutePath();
    
    /**
     * Default finding all pdf, png and jpg
     */
    public BookFilesFinder(Path path) {
	this.extension = null;
	this.sheetFolder = path.toFile();
    }

    public BookFilesFinder(Path path, String extension) {
	this.sheetFolder = path.toFile();
	this.extension = extension.toLowerCase();
    }

    public List<File> getFilesRecursive(String startFolder) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getFilesRecursive " + startFolder);
	}
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
    
    private void foundFile(File file) {
	files.add(file);
//	boolean ok;
////	if (isInExtractedImageDirectory(file)) {
////	    ok = true;
////	    files.add(file);
////	} else {
////	    ok = true;
////	}
////	if (LOGGER.isTraceEnabled()) {
////	    LOGGER.trace(file + " => " + ok);
////	}
    }
    
//    private boolean isInExtractedImageDirectory(File file) {
//	String absoluteName = file.getAbsolutePath();
//	return absoluteName.startsWith(CACHE_FOLDER_PATH);
//    }

    private void foundFolder(File folder) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Found folder " + folder);
	}
	File[] files = folder.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		String lowerName = name.toLowerCase();
		boolean accept;
		if (extension == null) {
		    accept = lowerName.endsWith(".pdf") || lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
		} else {
		    accept = lowerName.endsWith(extension);
		}
		return accept;
	    }
	});

	if (files != null) {
	    for (File file : files) {
		foundFile(file);
	    }
	    File[] folders = folder.listFiles(new FileFilter() {
		@Override
		public boolean accept(File pathname) {
		    return pathname.isDirectory();
		}
	    });
	    for (File childFolder : folders) {
		//if (!isInExtractedImageDirectory(childFolder)) {
		    foundFolder(childFolder);
		//}
	    }
	}
    }

    public static boolean isPdfFile(File file) {
	String name = file.getName().toLowerCase();
	return name.endsWith(".pdf");
    }

    public List<File> getAllBookFiles() {
	List<File> files = getFilesRecursive(sheetFolder);
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(StringUtils.formatNumber(files.size()) + " files");
	}
	return files;
    }
    

}
