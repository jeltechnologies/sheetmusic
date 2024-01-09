package com.jeltechnologies.geoservices;

public class Environment {
    private static final boolean USE_CACHE = true;
    private static final boolean SEARCH_ALL_HOUSES = false;
    private static final int MAX_CACHE_SIZE = 100000;
    private static final int EXPIRY_TIME_MINUTES = 1;
    private static final int SCHEDULE_CACHE_CLEAN_MINUTES = 2;
    private static final boolean REFRESH_OPENSTREETDATA_CSV = false;
    private static final String CSVFILES = "C:\\Projects\\Tools\\geo-services";
    private static final int THREADPOOL = 15;
    
    public static final Configuration getConfiguration() {
	CacheConfiguraton cacheConfiguraton = new CacheConfiguraton(USE_CACHE, MAX_CACHE_SIZE, EXPIRY_TIME_MINUTES, SCHEDULE_CACHE_CLEAN_MINUTES);
	Configuration c = new Configuration(CSVFILES, THREADPOOL, USE_CACHE, SEARCH_ALL_HOUSES, REFRESH_OPENSTREETDATA_CSV, cacheConfiguraton);
	return c;
    }

}
