package com.jeltechnologies.sheetmusic.jsonpayloads;

import java.io.Serializable;
import java.util.Objects;

public class RatedSuggestion implements Serializable, Comparable<RatedSuggestion> {
    private static final long serialVersionUID = -3294187192416838720L;
    private int rate;
    private String suggestion;

    public RatedSuggestion() {
    }

    public RatedSuggestion(int rate, String suggestion) {
	this.rate = rate;
	this.suggestion = suggestion;
    }

    public int getRate() {
	return rate;
    }

    public void setRate(int rate) {
	this.rate = rate;
    }

    public String getSuggestion() {
	return suggestion;
    }

    public void setSuggestion(String suggestion) {
	this.suggestion = suggestion;
    }

    @Override
    public String toString() {
	return "RatedSuggestion [rate=" + rate + ", suggestion=" + suggestion + "]";
    }

    @Override
    public int compareTo(RatedSuggestion o) {
	return this.rate - o.rate;
    }

    @Override
    public int hashCode() {
	return Objects.hash(rate, suggestion);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RatedSuggestion other = (RatedSuggestion) obj;
	return rate == other.rate && Objects.equals(suggestion, other.suggestion);
    }
    
    
}