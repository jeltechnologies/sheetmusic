package com.jeltechnologies.geoservices;

public record Configuration(
	String dataFolder,
	int threadPool,
	boolean useCache,
	boolean searchAllHouses,
	boolean refreshOpenStreetDataCSV,
	CacheConfiguraton cache) {
}
