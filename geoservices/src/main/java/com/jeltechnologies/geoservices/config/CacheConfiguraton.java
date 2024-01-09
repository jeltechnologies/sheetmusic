package com.jeltechnologies.geoservices.config;

public record CacheConfiguraton(boolean useCache, int maxCacheSize, int expiryTimeMinutes, int scheduleCacheCleanMinutes) {
}
