package com.jeltechnologies.geoservices.database;

import com.jeltechnologies.geoservices.config.Configuration;

import jakarta.servlet.ServletContext;

public class HouseDataSourceFactory {
    private static final String ATTRIBUTE_NAME = HouseDataSourceFactory.class.getName();
    
    private HouseDataSource inMemorySource;
    
    private boolean useDatabase;
    
    public static HouseDataSourceFactory getInstance(ServletContext context) {
	HouseDataSourceFactory factory = (HouseDataSourceFactory) context.getAttribute(ATTRIBUTE_NAME);
	if (factory == null) {
	    factory = new HouseDataSourceFactory();
	    context.setAttribute(ATTRIBUTE_NAME, factory);
	}
	return factory;
    }
    
    public void init(Configuration configuration) {
	useDatabase = configuration.useDatabase();
	if (!useDatabase) {
	    inMemorySource = new InMemoryHouseDatasource();
	}
    }
    
    public HouseDataSource get() {
	if (useDatabase) {
	    return new Database();
	} else {
	    return inMemorySource;
	}
    }
}
