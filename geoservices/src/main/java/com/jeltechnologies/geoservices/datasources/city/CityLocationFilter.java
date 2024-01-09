package com.jeltechnologies.geoservices.datasources.city;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.geoservices.datamodel.AddressRequest;
import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.datamodel.Country;
import com.jeltechnologies.geoservices.datasources.AbstractLocationFilter;
import com.jeltechnologies.geoservices.datasources.NearestLocation;
import com.jeltechnologies.geoservices.service.CountryMap;

/**
 * Get country and city name, which works globally.
 * <p>
 * This is typically the first step in finding an address. The country is important for finding the streeet.
 * </p>
 * This uses data from OpenDataSoft.
 * @see <a href="https://public.opendatasoft.com/explore/dataset/geonames-all-cities-with-a-population-1000/table/?disjunctive.cou_name_en&sort=name">Geonames - All Cities with a population > 1000</a>
 * @return
 */
public class CityLocationFilter extends AbstractLocationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CityLocationFilter.class);

    public CityLocationFilter(InputStream inputFile, CountryMap countryMap) throws IOException {
	super(countryMap);
	LOGGER.info("Loading global cities information");
	init(inputFile);
	LOGGER.info("  Finished Loading " + sizeFormatted() + " global cities");
    }

    private void init(InputStream in) throws IOException {
	Scanner scanner = null;
	try {
	    scanner = new Scanner(in, "UTF-8");
	    int i = 1;
	    while (scanner.hasNext()) {
		if (i > 1) {
		    try {
			String line = scanner.nextLine();
			if (!line.startsWith("﻿Geoname ID")) {
			    parseLine(i, line);
			}
		    } catch (Exception e) {
			LOGGER.warn(e.getMessage() + " in line " + i, e);
		    }
		}
		i++;
	    }
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	    if (in != null) {
		in.close();
	    }
	}
    }

    private void parseLine(int lineNumber, String line) {
	String[] parts = line.split(";");
	String geonameID = parts[0];
	String name = parts[1];
	String population = parts[13];
	String timezone = parts[16];
	String coordinatesString = parts[19];
	String countryCode = parts[6];

	if (coordinatesString == null || coordinatesString.isBlank()) {
	    throw new IllegalArgumentException("No coordinates found for line " + lineNumber);
	}
	String[] coordinateParts = coordinatesString.split(",");
	if (coordinateParts.length != 2) {
	    LOGGER.trace("Parse error in for city " + name + " at line " + lineNumber + ". Invalid coordination: " + coordinatesString);
	} else {
	    Coordinates coordinates = new Coordinates(coordinateParts[0].trim(), coordinateParts[1].trim());
	    CityLocation location = new CityLocation(coordinates, geonameID, name, population, timezone, coordinatesString, countryCode); locations.add(location);
	}
    }

    @Override
    protected void makeUpdates(AddressRequest request) {
	NearestLocation nearest = this.getNearestLocation(request.query().coordinates());
	if (nearest == null) {
	    LOGGER.warn("No nearest CityLocation found for " + request.query());
	} else {
	    CityLocation location = (CityLocation) nearest.location();
	    Coordinates coordinates = location.getCoordinates();
	    Country country = countries.getCountry(location.getCountryCode());
	    request.setCountry(country, coordinates);
	    request.setPlace(location.getName(), coordinates);
	}
    }
    
    @Override
    public String toString() {
	return sizeFormatted() + " cities";
    }

}
