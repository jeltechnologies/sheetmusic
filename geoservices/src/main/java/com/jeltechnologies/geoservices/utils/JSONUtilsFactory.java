package com.jeltechnologies.geoservices.utils;

public class JSONUtilsFactory {
    
    public static JSONUtils getInstance() {
	return new JSONUtilsJackson();
    }

}
