package com.jeltechnologies.geoservices.datasources.house;

import com.jeltechnologies.geoservices.datamodel.Country;

public record GeoFile(int id, String filename, Country country) {}
