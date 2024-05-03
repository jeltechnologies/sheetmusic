package com.jeltechnologies.sheetmusic.extractedfilestorage;

public interface ThumbnailsTaskConsumerMBean {
    boolean isBusy();
    String getBook();
    int getThumbsToExtract();
    int getThumbsExtracted();
    int getThumbsRemaining();
}
