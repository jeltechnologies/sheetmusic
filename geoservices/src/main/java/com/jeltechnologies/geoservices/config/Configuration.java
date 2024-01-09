package com.jeltechnologies.geoservices.config;

public record Configuration(
	String dataFolder,
	int threadPool,
	Boolean searchAllHouses,
	Boolean refreshOpenStreetDataCSV,
	CacheConfiguraton cache) {

    public Configuration {
	if (dataFolder == null) {
	    throw new IllegalArgumentException("dataFolder must be set");
	}
	final boolean USE_CACHE = true;
	final boolean SEARCH_ALL_HOUSES = false;
	final int MAX_CACHE_SIZE = 100000;
	final int EXPIRY_TIME_MINUTES = 1;
	final int SCHEDULE_CACHE_CLEAN_MINUTES = 2;
	final boolean REFRESH_OPENSTREETDATA_CSV = false;
	final int THREADPOOL = 15;

	if (searchAllHouses == null) {
	    searchAllHouses = SEARCH_ALL_HOUSES;
	}
	if (refreshOpenStreetDataCSV == null) {
	    refreshOpenStreetDataCSV = REFRESH_OPENSTREETDATA_CSV;
	}
	if (threadPool < 1) {
	    threadPool = THREADPOOL;
	}
	if (cache == null) {
	    cache = new CacheConfiguraton(USE_CACHE, MAX_CACHE_SIZE, EXPIRY_TIME_MINUTES, SCHEDULE_CACHE_CLEAN_MINUTES);
	}
    }
}
