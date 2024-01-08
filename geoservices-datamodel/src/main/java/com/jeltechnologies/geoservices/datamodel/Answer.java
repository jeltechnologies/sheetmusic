package com.jeltechnologies.geoservices.datamodel;

public class Answer {
    private Address address = new Address();
    private Distance distanceFromQuery;
    private Coordinates closestCoordinate;

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    public Distance getDistanceFromQuery() {
	return distanceFromQuery;
    }

    public void setDistanceFromQuery(Distance distanceFromQuery) {
	this.distanceFromQuery = distanceFromQuery;
    }
    
    public void setClosestCoordinate(Coordinates c) {
	this.closestCoordinate = c;
    }

    public Coordinates getClosestCoordinate() {
	return closestCoordinate;
    }

    @Override
    public String toString() {
	return "Answer [address=" + address + ", distanceFromQuery=" + distanceFromQuery + ", closestCoordinate=" + closestCoordinate + "]";
    }
}
