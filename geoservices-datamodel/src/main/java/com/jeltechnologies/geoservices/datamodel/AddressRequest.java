package com.jeltechnologies.geoservices.datamodel;

public record AddressRequest(Query query, Answer answer) {

    public void setPlace(String place, Coordinates coordinates) {
	answer.getAddress().setPlace(place);
	updateCoordinatesAndDistance(coordinates);
    }

    public void setPostcalCode(String postalcode, Coordinates coordinates) {
	answer.getAddress().setPostalCode(postalcode);
	updateCoordinatesAndDistance(coordinates);
    }

    public void setStreet(String street, Coordinates coordinates) {
	answer.getAddress().setStreet(street);
	updateCoordinatesAndDistance(coordinates);
    }

    public void setNr(String nr, Coordinates coordinates) {
	answer.getAddress().setNr(nr);
	updateCoordinatesAndDistance(coordinates);
    }

    public void setCountry(Country country, Coordinates coordinates) {
	answer.getAddress().setCountry(country);
	updateCoordinatesAndDistance(coordinates);
    }

    private void updateCoordinatesAndDistance(Coordinates newCoordinate) {
	boolean updateCoordinates = false;
	Coordinates old = answer.getClosestCoordinate();
	if (old == null) {
	    updateCoordinates = true;
	} else {
	    double newDistance = newCoordinate.getDistanceFrom(query.coordinates());
	    if (newDistance < answer.getDistanceFromQuery().exact()) {
		updateCoordinates = true;
	    }
	}
	if (updateCoordinates) {
	    double newDistance = newCoordinate.getDistanceFrom(query.coordinates());
	    answer.setClosestCoordinate(newCoordinate);
	    answer.setDistanceFromQuery(new Distance(newDistance));
	}
    }
}
