package com.jeltechnologies.geoservices.datasources;

import com.jeltechnologies.geoservices.datamodel.Coordinates;

public class Location {
    protected final Coordinates coordinates;

    public Location(Coordinates coordinates) {
	this.coordinates = coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
	return "[coordinates=" + coordinates + "]";
    }
    
    
}
