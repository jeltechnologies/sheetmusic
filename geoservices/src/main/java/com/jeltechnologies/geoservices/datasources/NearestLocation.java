package com.jeltechnologies.geoservices.datasources;

import com.jeltechnologies.geoservices.datamodel.Distance;

public record NearestLocation(Location location, Distance distance)
{
    public boolean isCloserThan(Distance otherDistance) {
	boolean result = false;
	if (distance != null && otherDistance != null) {
	    result = otherDistance.exact() > distance.exact();
	}
	return result;
    }
}
