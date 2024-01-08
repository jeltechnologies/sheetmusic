package com.jeltechnologies.geoservices.datamodel;

import java.math.BigDecimal;

public record Coordinates(double latitude, double longitude) 
{
    public Coordinates(String lat, String lng) {
	this(Double.parseDouble(lat), Double.parseDouble(lng));
    }
    
    public Coordinates(BigDecimal lat, BigDecimal lng) {
	this(lat.doubleValue(), lng.doubleValue());
    }
 
    public double getDistanceFrom(Coordinates other) {
	return calculateDistance(latitude, longitude, other.latitude(), other.longitude());
    }
    
    private double haversine(double val) {
	return Math.pow(Math.sin(val / 2), 2);
    }

    private double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
	double dLat = Math.toRadians((endLat - startLat));
	double dLong = Math.toRadians((endLong - startLong));
	startLat = Math.toRadians(startLat);
	endLat = Math.toRadians(endLat);
	double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	final int EARTH_RADIUS = 6371;
	return EARTH_RADIUS * c;
    }
    
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("[ ").append(latitude);
	builder.append(", ");
	builder.append(longitude).append(" ]");
	return builder.toString();
    }
}
