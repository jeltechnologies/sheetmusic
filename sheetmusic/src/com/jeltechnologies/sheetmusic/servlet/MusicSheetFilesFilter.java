package com.jeltechnologies.sheetmusic.servlet;

import java.io.File;
import java.io.FilenameFilter;

public class MusicSheetFilesFilter implements FilenameFilter{

    @Override
    public boolean accept(File dir, String name) {
	return name.endsWith(".pdf") || name.endsWith(".jpg") || name.endsWith(".png");
    }

}
