package com.jeltechnologies.geoservices.datasources.postalcode;

import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.datasources.Location;

public class PostalCodeLocation extends Location {
    private final String countrycode;
    private final String postalcode;
    private final String placename;
    private final String accuracy;

    public PostalCodeLocation(Coordinates coordinates, String countrycode, String postalcode, String placename, String accuracy) {
	super(coordinates);
	this.countrycode = countrycode;
	this.postalcode = postalcode;
	this.placename = placename;
	this.accuracy = accuracy;
    }

    public Coordinates getCoordinates() {
	return coordinates;
    }

    public String getCountrycode() {
	return countrycode;
    }

    public String getPostalcode() {
	return postalcode;
    }

    public String getPlacename() {
	return placename;
    }

    public String getAccuracy() {
	return accuracy;
    }

    @Override
    public String toString() {
	return "PostalCodeLocation [countrycode=" + countrycode + ", postalcode=" + postalcode + ", placename=" + placename + ", accuracy=" + accuracy + "]";
    }
    
    

}
