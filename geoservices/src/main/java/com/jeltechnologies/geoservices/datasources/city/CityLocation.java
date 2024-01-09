package com.jeltechnologies.geoservices.datasources.city;

import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.datasources.Location;

public class CityLocation extends Location {
    private final String geonameID;
    private final String name;
    private final String population;
    private final String timezone;
    private final String countryCode;

    public CityLocation(Coordinates coordinate, String geonameID, String city, String population, String timezone, String coordinatesString,
	    String countryCode) {
	super(coordinate);
	this.geonameID = geonameID;
	this.name = city;
	this.population = population;
	this.timezone = timezone;
	this.countryCode = countryCode;
    }

    public String getGeonameID() {
        return geonameID;
    }

    public String getName() {
        return name;
    }

    public String getPopulation() {
        return population;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
	return "CityLocation [geonameID=" + geonameID + ", name=" + name + ", population=" + population + ", timezone=" + timezone + ", countryCode="
		+ countryCode + "]";
    }
}
