package com.jeltechnologies.screenmusic.extractedfilestorage;

public interface ThumbnailsTaskConsumerMBean {
    boolean isBusy();
    String getBook();
    int getThumbsToExtract();
    int getThumbsExtracted();
    int getThumbsRemaining();
}
