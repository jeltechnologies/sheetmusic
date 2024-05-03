package com.jeltechnologies.sheetmusic.extractedfilestorage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThumbnailsQueue implements ThumbnailsQueueMBean {
    private BlockingQueue<ThumbnailsExtractTask> queue = new LinkedBlockingQueue<ThumbnailsExtractTask>();

    private AtomicInteger thumbsInQueue = new AtomicInteger();

    public ThumbnailsQueue() {
    }

    public void add(ThumbnailsExtractTask t) {
	int delta = t.getThumbsToExtract().size();
	thumbsInQueue.addAndGet(delta);
	queue.add(t);
    }

    public ThumbnailsExtractTask take() throws InterruptedException {
	ThumbnailsExtractTask task = queue.take();
	int delta = -1 * task.getThumbsToExtract().size();
	thumbsInQueue.addAndGet(delta);
	return task;
    }

    @Override
    public int getBooks() {
	return queue.size();
    }

    @Override
    public int getThumbnails() {
	return thumbsInQueue.intValue();
    }
}
