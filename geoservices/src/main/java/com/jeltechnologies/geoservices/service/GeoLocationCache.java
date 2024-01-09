package com.jeltechnologies.geoservices.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.geoservices.config.CacheConfiguraton;
import com.jeltechnologies.geoservices.datamodel.AddressRequest;
import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.utils.JMXUtils;

public class GeoLocationCache implements GeoLocationCacheMBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoLocationCache.class);
    private List<CachedGeoLocation> cache = new CopyOnWriteArrayList<CachedGeoLocation>();
    private final ScheduledExecutorService executor;
    private final CacheConfiguraton config;
    private long hits;
    private long misses;
    
    public GeoLocationCache(CacheConfiguraton config, ScheduledExecutorService executor) {
	this.config = config;
	this.executor = executor;
	JMXUtils.getInstance().registerMBean("cache", "Address", this);
	scheduleCleanThread();
    }
    
    public int getSize() {
	return cache.size();
    }

    public void add(AddressRequest geoLocation) {
	if (cache.size() < config.maxCacheSize()) {
	    CachedGeoLocation cachedLocation = new CachedGeoLocation(geoLocation, config.expiryTimeMinutes());
	    cache.add(cachedLocation);
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace("Added to cache: " + cachedLocation); 
	    }
	} else {
	    LOGGER.warn("GeoLocation cannot be cached, cache is max size (" + config.maxCacheSize() + ")");
	}
    }

    private void scheduleCleanThread() {
	executor.scheduleAtFixedRate(new Runnable() {
	    @Override
	    public void run() {
		cleanCache();

	    }
	}, 1, config.scheduleCacheCleanMinutes(), TimeUnit.MINUTES);
    }

    private void cleanCache() {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Cleaning cache. The cache contains " + cache.size() + " locations.");
	}
	LocalDateTime now = LocalDateTime.now();
	List<CachedGeoLocation> outdatedLocations = new ArrayList<CachedGeoLocation>();
	for (int i = 0; i < cache.size(); i++) {
	    CachedGeoLocation location = cache.get(i);
	    if (now.isAfter(location.getExpiryTime())) {
		outdatedLocations.add(location);
	    }
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Found " + outdatedLocations.size() + " items that are expired");
	}
	for (CachedGeoLocation outdatedLocation : outdatedLocations) {
	    cache.remove(outdatedLocation);
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace("Removed " + outdatedLocation);
	    }
	}
    }

    public AddressRequest fetch(Coordinates coordinates) {
	CachedGeoLocation found = null;
	for (int i = cache.size() - 1; found == null && i >= 0; i--) {
	    CachedGeoLocation location = cache.get(i);
	    if (location.isHit(coordinates)) {
		found = location;
	    }
	}
	AddressRequest result = null;
	if (found != null) {
	    result = found.getLocation();
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Returned result from cache");
	    }
	    cache.remove(found);
	    cache.add(new CachedGeoLocation(result, config.expiryTimeMinutes()));
	    hits++;
	} else {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Location was not found in the cache");
	    }
	    misses++;
	}
	return result;
    }

    @Override
    public long getHits() {
	return hits;
    }

    @Override
    public long getMisses() {
	return misses;
    }

}
