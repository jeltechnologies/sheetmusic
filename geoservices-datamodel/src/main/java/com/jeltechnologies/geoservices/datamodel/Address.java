package com.jeltechnologies.geoservices.datamodel;

import java.util.Objects;

public class Address {
    private String place;
    private String postalCode;
    private String street;
    private String nr;
    private Country country;
    
    public Address() {
    }
    
    public Address(String place, String postalCode, String street, String nr, Country country) {
	this.place = place;
	this.postalCode = postalCode;
	this.street = street;
	this.nr = nr;
	this.country = country;
    }

    public String getPlace() {
	return place;
    }

    protected void setPlace(String place) {
	this.place = place;
    }

    public String getPostalCode() {
	return postalCode;
    }

    protected void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getStreet() {
	return street;
    }

    protected void setStreet(String street) {
	this.street = street;
    }

    public String getNr() {
	return nr;
    }

    protected void setNr(String nr) {
	this.nr = nr;
    }

    public Country getCountry() {
	return country;
    }

    protected void setCountry(Country country) {
	this.country = country;
    }

    @Override
    public String toString() {
	return "Address [place=" + place + ", postalCode=" + postalCode + ", street=" + street + ", nr=" + nr + ", country=" + country + "]";
    }

    @Override
    public int hashCode() {
	return Objects.hash(country, nr, place, postalCode, street);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	Address other = (Address) obj;
	return Objects.equals(country, other.country) && Objects.equals(nr, other.nr) && Objects.equals(place, other.place)
		&& Objects.equals(postalCode, other.postalCode) && Objects.equals(street, other.street);
    }
}
