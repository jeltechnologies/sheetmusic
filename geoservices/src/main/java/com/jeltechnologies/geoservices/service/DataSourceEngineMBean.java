package com.jeltechnologies.geoservices.service;

public interface DataSourceEngineMBean {
    boolean isReadyForService();
    int getNrOfCities();
    int getNrOfHouses();
    int getNrOfPostalCodes();
    long getHandledRequests();
}
