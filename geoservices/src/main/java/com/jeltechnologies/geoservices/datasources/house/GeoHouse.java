package com.jeltechnologies.geoservices.datasources.house;

import com.jeltechnologies.geoservices.datamodel.Coordinates;

public record GeoHouse(
	int id,
	int fileId,
	Coordinates coordinates,
	String postalCode,
	String city,
	String street,
	String houseNumber,
	String countryCode
) 
{
}
