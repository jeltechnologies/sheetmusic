package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class JobQueue implements JobQueueMBean {
    private BlockingQueue<Job> queue = new LinkedBlockingQueue<Job>();

    private static final JobQueue INSTANCE = new JobQueue();
    
    private JobQueue() {
    }

    public static JobQueue getInstance() {
	return INSTANCE;
    }
    
    public void add(Job job) {
	queue.add(job);
    }

    public Job poll() throws InterruptedException {
	return queue.poll(60, TimeUnit.MINUTES);
    }

    public int getSize() {
	return queue.size();
    }

}
