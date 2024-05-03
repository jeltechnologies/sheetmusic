package com.jeltechnologies.sheetmusic.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.utils.FileUtils;
import com.jeltechnologies.utils.StringUtils;

public class TessdataLanguages implements Serializable {
    private static final long serialVersionUID = -3717102055810746700L;

    private final static Logger LOGGER = LoggerFactory.getLogger(TessdataLanguages.class);

    private List<TessdataLanguage> languages = new ArrayList<TessdataLanguage>();

    public TessdataLanguages(String tessdataPrefixFolderName) {
	if (tessdataPrefixFolderName != null) {
	    try {
		init(new File(tessdataPrefixFolderName));
		LOGGER.info(this.toString());
	    } catch (Exception e) {
		LOGGER.warn("Cannot load TESS language files", e);
	    }
	}
    }

    private void init(File tessdataFolder) throws Exception {
	Map<String, TessdataLanguage> languageMap = new HashMap<String, TessdataLanguage>();
	List<String> lines = FileUtils.readTextFileLines("/tessdata_languages.txt", true);
	for (String lineRaw : lines) {
	    String line = StringUtils.stripControlChars(lineRaw);
	    line = StringUtils.stripDoubleSpaces(line).trim();
	    if (!line.startsWith("#")) {
		String[] parts = line.split(" ");
		if (parts.length > 2) {
		    String code = parts[0];
		    StringBuilder nameBuilder = new StringBuilder();
		    for (int i = 1; i < parts.length; i++) {
			String name = parts[i];
			if (!name.equals("x")) {
			    if (!nameBuilder.isEmpty()) {
				nameBuilder.append(" ");
			    }
			    nameBuilder.append(name);
			}
		    }
		    String name = nameBuilder.toString();
		    TessdataLanguage language = new TessdataLanguage(code, name);
		    languageMap.put(code, language);
		}
	    }
	}

	File[] trainedFiles = tessdataFolder.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return name.toLowerCase().endsWith(".traineddata");
	    }
	});
	for (File trainedFile : trainedFiles) {
	    String code = StringUtils.stripAfter(trainedFile.getName(), ".");
	    TessdataLanguage language = languageMap.get(code);
	    if (language == null) {
		String strippedCode = StringUtils.stripAfter(code, "_vert");
		language = languageMap.get(strippedCode);
		if (language != null) {
		    language = new TessdataLanguage(code, language.getLanguage() + " (vertical)");
		}
	    }
	    if (language != null) {
		languages.add(language);
	    }
	}
	Collections.sort(languages);
    }

    public List<TessdataLanguage> getLanguages() {
	return languages;
    }

    @Override
    public String toString() {
	return "TessdataLanguages [languages=" + languages + "]";
    }

}
