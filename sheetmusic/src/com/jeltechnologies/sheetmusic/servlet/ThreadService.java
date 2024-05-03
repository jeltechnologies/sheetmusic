package com.jeltechnologies.sheetmusic.servlet;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.config.Configuration;

public class ThreadService implements Serializable {
    private static final long serialVersionUID = -7437073147064868879L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadService.class);
    private static final int THREAD_POOL_SIZE_SCHEDULE = 20;
    private static final int THREAD_POOL_SIZE_SESSIONS = 15;
    private ScheduledExecutorService scheduler = null;
    private ExecutorService executor = null;

    public ThreadService() {
	int consumeThreads = Configuration.getInstance().indexConsumeThreads();
	int scheduleThreads = consumeThreads + THREAD_POOL_SIZE_SCHEDULE;
	int sessionThreads = consumeThreads + THREAD_POOL_SIZE_SESSIONS;
	scheduler = Executors.newScheduledThreadPool(scheduleThreads);
	executor = Executors.newFixedThreadPool(sessionThreads);
    }

    public synchronized void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
	scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
    
    public synchronized void schedule(Runnable command, long initialDelay, TimeUnit unit) {
	scheduler.schedule(command, initialDelay, unit);
    }

    public synchronized void scheduleDailyAt(Runnable command, int hour, int minute) {
	ZonedDateTime now = ZonedDateTime.now();
	ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0);
	if (now.compareTo(nextRun) > 0) {
	    nextRun = nextRun.plusDays(1);
	}
	Duration duration = Duration.between(now, nextRun);
	long initalDelay = duration.getSeconds();
	scheduler.scheduleAtFixedRate(command, initalDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public synchronized void execute(Runnable command) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Execute " + command.getClass().getSimpleName());
	}
	executor.execute(command);
    }

    @SuppressWarnings("rawtypes")
    public synchronized Future submit(Runnable command) {
	return executor.submit(command);
    }

    public synchronized void shutdown() {
	LOGGER.info(this.getClass().getSimpleName() + " will initiate shutdown.....");
	if (executor != null) {
	    executor.shutdownNow();
	}
	if (scheduler != null) {
	    scheduler.shutdownNow();
	}
	LOGGER.info(this.getClass().getSimpleName() + " ..... shutdown completed");
    }
}
