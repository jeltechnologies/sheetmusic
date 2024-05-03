package com.jeltechnologies.sheetmusic.maintenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaintenanceThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceThread.class);

    @Override
    public void run() {
	LOGGER.info("Maintenance thread started");
	LOGGER.info("Maintenance thread ended");
    }


}
