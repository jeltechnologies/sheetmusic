package com.jeltechnologies.sheetmusic.config;

import java.util.List;

public record AudiverisConfiguration(
	String audiveris_lib,
	String tessdata_prefix,
	List<AudiverisOption> default_options, 
	List<TessdataLanguage> languages) {
    
    public AudiverisConfiguration(String audiveris_lib, String tessdata_prefix, List<AudiverisOption> default_options, List<TessdataLanguage> languages) {
	this.audiveris_lib = audiveris_lib;
	this.tessdata_prefix= tessdata_prefix;
	this.default_options = new AudiverisDefaultOptions().getOptions();
	this.languages = new TessdataLanguages(tessdata_prefix).getLanguages();
    }

    @Override
    public String toString() {
	return "AudiverisConfiguration [audiveris_lib=" + audiveris_lib + ", tessdata_prefix=" + tessdata_prefix + ", default_options=" + default_options.size()
		+ ", languages=" + languages.size() + "]";
    }
    
    
}