package com.jeltechnologies.screenmusic.config;

import java.io.Serializable;

public class TessdataLanguage implements Serializable, Comparable<TessdataLanguage> {
    private static final long serialVersionUID = 3714781546825333777L;
    private final String code;
    private final String language;

    public TessdataLanguage(String code, String language) {
	this.code = code;
	this.language = language;
    }

    public String getCode() {
	return code;
    }

    public String getLanguage() {
	return language;
    }

    @Override
    public String toString() {
	return "TessdataLanguage [" + code + " = " + language + "]";
    }

    @Override
    public int compareTo(TessdataLanguage o) {
	return language.compareToIgnoreCase(o.language);
    }
}
