package com.jeltechnologies.sheetmusic;

import java.io.File;
import java.nio.file.Path;

import com.jeltechnologies.utils.StringUtils;

public record User(String name, Path sheetMusicFolder) {
    
    public File getFile(String relativeFileName) {
	Path path = Path.of(sheetMusicFolder.toString(), relativeFileName);
	return path.toFile();
    }

    public String getRelativeFileName(File file) {
	String absoluteFileName = file.getAbsolutePath();
	String baseFolderFileName = sheetMusicFolder.toFile().getAbsolutePath();
	String relativeFileName = StringUtils.stripBefore(absoluteFileName, baseFolderFileName);
	relativeFileName = StringUtils.replaceAll(relativeFileName, '\\', '/');
	if (relativeFileName.startsWith("/")) {
	    relativeFileName = relativeFileName.substring(1);
	}
	return relativeFileName;
    }
    
}
