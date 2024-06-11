package com.jeltechnologies.screenmusic.servlet;

import java.io.File;
import java.io.FileFilter;

public class MusicFoldersFileFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
	return pathname.isDirectory();
    }

}
