package com.jeltechnologies.geoservices.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.geoservices.Configuration;
import com.jeltechnologies.geoservices.Environment;
import com.jeltechnologies.geoservices.database.Database;
import com.jeltechnologies.geoservices.utils.JMXUtils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
	try {
	    Configuration config = Environment.getConfiguration();
	    LOGGER.info(config.toString());
	    Database database = new Database();
	    database.initDatabase(config.refreshOpenStreetDataCSV());
	    database.close();
	    new DataSourceEngine(config, servletContextEvent.getServletContext());
	    LOGGER.info("Service deployed");
	} catch (Exception e) {
	    throw new IllegalStateException("Cannot start web app because " + e.getMessage(), e);
	}
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
	DataSourceEngine.getDataSourceEngine(servletContextEvent.getServletContext()).shutdown();
	JMXUtils.getInstance().unregisterAllMBeans();
	LOGGER.info("Service undeployed");
    }
}
