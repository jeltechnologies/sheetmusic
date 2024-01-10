package com.jeltechnologies.geoservices.datasources.house;

import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.datasources.Location;

public class GeoCoordinates extends Location {
    private final int id;

    public GeoCoordinates(int id, double lat, double lon) {
	super(new Coordinates(lat, lon));
	this.id = id;
    }

    public int getId() {
	return id;
    }

    @Override
    public String toString() {
	return "GeoCoordinates [id=" + id + ", coordinates=" + coordinates + "]";
    }
    
}
