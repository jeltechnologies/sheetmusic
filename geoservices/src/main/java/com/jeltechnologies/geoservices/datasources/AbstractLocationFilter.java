package com.jeltechnologies.geoservices.datasources;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.geoservices.datamodel.AddressRequest;
import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.datamodel.Distance;
import com.jeltechnologies.geoservices.service.CountryMap;
import com.jeltechnologies.geoservices.utils.StringUtils;

public abstract class AbstractLocationFilter implements LocationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLocationFilter.class);
    
    protected List<Location> locations = new ArrayList<Location>();
    
    protected final CountryMap countries;
    
    protected AbstractLocationFilter(CountryMap countries) {
	this.countries = countries;
    }
    
    @Override
    public final void updateLocation(AddressRequest request) {
	String before = null;
	if (LOGGER.isDebugEnabled()) {
	    before = request.toString();
	}
	makeUpdates(request);
	if (LOGGER.isDebugEnabled()) {
	    String after = request.toString();
	    String logLine = "Updated by " + getDescription();
	    boolean sameSame = before.equals(after);
	    if (sameSame) {
		logLine = logLine + " no changes";
	    }
	    LOGGER.debug(logLine);
	    if (!sameSame) {
		LOGGER.debug("  Before: " + before);
		LOGGER.debug("  After : " + after);
	    }
	}
    }
    
    /**
     * Implemenation specific logic to update address information in the GeoLocation
     * 
     * @param geoLocation
     */
    protected abstract void makeUpdates(AddressRequest request);
    
    protected String getDescription() {
	return this.getClass().getSimpleName();
    }
    
    /**
     * Get nearest known location for specific coordinates
     * 
     * @param c
     * @return
     */
    protected NearestLocation getNearestLocation(Coordinates c) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getNearestLocation " + c);
	}
	Location closestLocation = null;
	NearestLocation nearestLocation = null;
	Coordinates closest = null;
	double distancetoClosest = 0;
	if (!locations.isEmpty()) {
	    for (int i = 0; i < locations.size(); i++) {
		Location currentLocation = locations.get(i);
		if (i == 0) {
		    closestLocation = currentLocation;
		    closest = closestLocation.getCoordinates();
		    distancetoClosest = c.getDistanceFrom(closest);
		} else {
		    Coordinates current = currentLocation.getCoordinates();
		   double distancetoCurrent = c.getDistanceFrom(current);
		    if (distancetoCurrent < distancetoClosest) {
			closestLocation = currentLocation;
			closest = closestLocation.getCoordinates();
			distancetoClosest = c.getDistanceFrom(closest);
		    }
		}
	    }
	    nearestLocation = new NearestLocation(closestLocation, new Distance(distancetoClosest));
	}
	return nearestLocation;
    }
    
    @Override
    public String sizeFormatted() {
	return StringUtils.formatNumber(locations.size());
    }
    
    @Override
    public int size() {
	return locations.size();
    }
}
