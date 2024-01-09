package com.jeltechnologies.geoservices.utils;

public interface JSONUtils {
    public String toJSON(Object object) throws Exception;
    public Object fromJSON(String json, Class<?> clazz) throws Exception;
}
