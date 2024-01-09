package com.jeltechnologies.geoservices.service;

public interface GeoLocationCacheMBean {
    int getSize();
    
    long getHits();
    
    long getMisses();
}
