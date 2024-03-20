package com.jeltechnologies.geoservices.datasources.postalcode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.geoservices.datamodel.AddressRequest;
import com.jeltechnologies.geoservices.datamodel.Coordinates;
import com.jeltechnologies.geoservices.datamodel.Country;
import com.jeltechnologies.geoservices.datasources.AbstractLocationFilter;
import com.jeltechnologies.geoservices.datasources.Location;
import com.jeltechnologies.geoservices.datasources.NearestLocation;
import com.jeltechnologies.geoservices.service.CountryMap;
import com.jeltechnologies.geoservices.utils.StringUtils;

/**
 * Find postal code and correct cityname of a location using OpenDataSoft
 * <p>
 * This is done after finding the house, because data about houses often only contains municipality name and not city name.
 * <p>
 * 
 * @see <a href="https://public.opendatasoft.com/explore/dataset/geonames-postal-code/table/">All Postal Code - All countries (Geonames)</a>
 * 
 */
public class PostalCodesLocationFilter extends AbstractLocationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostalCodesLocationFilter.class);

    private Country NLD = new Country("NL", "The Netherlands");

    // For Sweden we should integrate https://www.snabbgrus.se/stockholm-1/
    @Override
    protected void makeUpdates(AddressRequest request) {
	String postCodeInAddress = request.answer().getAddress().getPostalCode();
	String countryCodeInAddress = request.answer().getAddress().getCountry().code();
	if (countryCodeInAddress.equals(NLD.code())) {
	    PostalCodeLocation foundPostalCode = null;
	    Iterator<Location> iterator = this.locations.iterator();
	    while (iterator.hasNext() && foundPostalCode == null) {
		PostalCodeLocation current = (PostalCodeLocation) iterator.next();
		if (current.getCountrycode().equals(countryCodeInAddress)) {
		    if (current.getPostalcode().equals(postCodeInAddress)) {
			foundPostalCode = current;
		    }
		}
	    }
	    if (foundPostalCode != null) {
		request.setPlace(foundPostalCode.getPlacename(), foundPostalCode.getCoordinates());
	    } else {
		NearestLocation location = getNearestLocation(request.query().coordinates());
		if (location != null) {
		    PostalCodeLocation nearest = (PostalCodeLocation) location.location();
		    if (nearest != null) {
			request.setPlace(nearest.getPlacename(), nearest.getCoordinates());
		    }
		}
	    }
	    if (postCodeInAddress.length() == 6) {
		String nr = postCodeInAddress.substring(0, 4);
		String ch = postCodeInAddress.substring(4);
		String postCode = nr + " " + ch;
		request.setPostcalCode(postCode, request.answer().getClosestCoordinate());
	    }
	}
    }

    public PostalCodesLocationFilter(InputStream postalCodeStream, CountryMap countries) throws IOException {
	super(countries);
	loadPostcalCodes(postalCodeStream);
    }

    private void loadPostcalCodes(InputStream in) throws IOException {
	Scanner scanner = null;
	try {
	    scanner = new Scanner(in, "UTF-8");
	    int i = 1;
	    while (scanner.hasNext()) {
		try {
		    String line = scanner.nextLine();
		    if (!line.startsWith("﻿Geoname ID")) {
			parsePostalCodeLine(i, line);
		    }
		} catch (Exception e) {
		    LOGGER.trace(e.getMessage() + " in line " + i);
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

    private void parsePostalCodeLine(int i, String line) {
	if (!line.startsWith("country")) {
	    String[] parts = line.split(";");
	    if (parts.length > 12) {
		String countrycode = parts[0];
		String postalcode = parts[1];
		String placename = parts[2];
		String latitude = parts[9];
		String longitude = parts[10];
		String accuracy = parts[11];
		Coordinates coordinates = new Coordinates(latitude, longitude);
		PostalCodeLocation location = new PostalCodeLocation(coordinates, countrycode, postalcode, placename, accuracy);
		locations.add(location);
	    }
	}
    }

    @Override
    public String toString() {
	return StringUtils.formatNumber(super.locations.size()) + " postal codes";
    }

}
