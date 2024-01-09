package com.jeltechnologies.geoservices.datasources;

import com.jeltechnologies.geoservices.datamodel.AddressRequest;

/**
 * Updates the geolocation with implementation specific address information
 * <p>
 */
public interface LocationFilter {

    /**
     * Update address information, based on stored coordnates and implementation-specific information
     * 
     * @param geoLocation coordinates and address information for incoming address requests
     */
    void updateLocation(AddressRequest request);

    /**
     * The number of coordinates stored to update address information
     * <p>
     * For logging and debugging purposes
     * @return
     */
    String sizeFormatted();

    /**
     * The number of coordinates stored to update the address information
     * <p>
     * For logging and debugging purposes
     * @return
     */
    int size();

}