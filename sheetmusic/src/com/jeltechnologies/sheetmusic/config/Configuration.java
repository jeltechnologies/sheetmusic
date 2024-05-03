package com.jeltechnologies.sheetmusic.config;

public record Configuration(int indexConsumeThreads, Admin admin, StorageConfiguration storage, OpticalMusicRecognizationConfiguration opticalmusicrecognition) {
    private static Configuration instance;
    
    public Configuration(int indexConsumeThreads, Admin admin, StorageConfiguration storage, OpticalMusicRecognizationConfiguration opticalmusicrecognition) {
	this.storage = storage;
	this.admin = admin;
	this.opticalmusicrecognition = opticalmusicrecognition;
	if (indexConsumeThreads < 1) {
	    this.indexConsumeThreads = 15;
	} else {
	    this.indexConsumeThreads = indexConsumeThreads;
	}
	instance = this;
    }
    
    public static Configuration getInstance() {
	return instance;
    }
}
