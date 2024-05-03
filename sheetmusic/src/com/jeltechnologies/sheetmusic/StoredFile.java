package com.jeltechnologies.sheetmusic;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StoredFile implements Serializable {
    private static final long serialVersionUID = 3894501775707364975L;
    private final List<String> folderNames;
    private final String fileName;

    public StoredFile(String absoluteFileName) {
	String[] folderParts = absoluteFileName.split("/");
	int fileNamePart = folderParts.length - 1;
	fileName = folderParts[fileNamePart];
	folderNames = new ArrayList<String>();
	for (int i = 0; i < fileNamePart; i++) {
	    folderNames.add(folderParts[i]);
	}
    }

    public String getFolderName() {
	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < folderNames.size(); i++) {
	    builder.append(folderNames.get(i)).append("/");
	}
	return builder.toString();
    }

    public List<String> getFolderNames() {
        return folderNames;
    }

    public String getFileName() {
        return fileName;
    }
    
    public File toFile() {
	return new File(getFolderName() + "/"  + fileName);
    }
}
