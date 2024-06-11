package com.jeltechnologies.screenmusic;

import java.io.File;

import com.jeltechnologies.utils.StringUtils;

public record User(String name, File sheetMusicFolderName) {
    
    public File getFile(String relativeFileName) {
	String filePath =  getSheetMusicFolder() + "/" + relativeFileName;
	return new File(filePath);
    }

    public String getRelativeFileName(File file) {
	String absoluteFileName = file.getAbsolutePath();
	String baseFolderFileName =  getSheetMusicFolder().getAbsolutePath();
	String relativeFileName = StringUtils.stripBefore(absoluteFileName, baseFolderFileName);
	relativeFileName = StringUtils.replaceAll(relativeFileName, '\\', '/');
	if (relativeFileName.startsWith("/")) {
	    relativeFileName = relativeFileName.substring(1);
	}
	return relativeFileName;
    }
    
    public File getSheetMusicFolder() {
	return sheetMusicFolderName;
    }
    
}
