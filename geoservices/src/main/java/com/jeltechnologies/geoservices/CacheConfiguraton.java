package com.jeltechnologies.geoservices;

public record CacheConfiguraton (boolean useCache, int maxCacheSize, int expiryTimeMinutes, int scheduleCacheCleanMinutes) 
{
}
