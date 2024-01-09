package com.jeltechnologies.geoservices.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.jeltechnologies.geoservices.datamodel.AddressRequest;
import com.jeltechnologies.geoservices.datamodel.Coordinates;

public class CachedGeoLocation {
    private static final int PRECISION_BEHIND_COMMA = 4;
    private static final BigDecimal PRECISION_MULTIPLIER = new BigDecimal(10).pow(PRECISION_BEHIND_COMMA);
    private final AddressRequest location;
    private final LocalDateTime cacheTime;
    private final LocalDateTime expiryTime;
    private final int nearLatitude;
    private final int nearLongitude;
    
    public CachedGeoLocation(AddressRequest location, int expiryTimeMinutes) {
	this.location = location;
	this.cacheTime = LocalDateTime.now();
	this.expiryTime = cacheTime.plusMinutes(expiryTimeMinutes);
	Coordinates c = location.query().coordinates();
	this.nearLatitude = getSimularCoordinate(c.latitude());
	this.nearLongitude = getSimularCoordinate(c.longitude());
    }
    
    private int getSimularCoordinate(double original) {
	return new BigDecimal(original).multiply(PRECISION_MULTIPLIER).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public AddressRequest getLocation() {
        return location;
    }

    public LocalDateTime getCacheTime() {
        return cacheTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
    
    public boolean isHit(Coordinates c) {
	boolean result = false;
	if (c != null) {
	    int otherLat = getSimularCoordinate(c.latitude());
	    if (otherLat == nearLatitude) {
		int otherLon = getSimularCoordinate(c.longitude());
		result = otherLon == nearLongitude;
	    }
	}
	return result;
    }

    @Override
    public String toString() {
	return "CachedGeoLocation [cacheTime=" + cacheTime + ", expiryTime=" + expiryTime + ", nearLatitude=" + nearLatitude + ", nearLongitude="
		+ nearLongitude + ", location=" + location + "]";
    }
    
    
}
