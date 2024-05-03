package com.jeltechnologies.utils.datatypes;

import java.io.Serializable;
import java.time.LocalDate;

public class WeekYear implements Serializable, Comparable<WeekYear> {
    private static final long serialVersionUID = -1874477247360493559L;
    private final int week;
    private final int year;
    private final LocalDate lastDayOfWeek;

    public WeekYear(int week, int year, LocalDate lastDateOfWeek) {
	this.week = week;
	this.year = year;
	this.lastDayOfWeek = lastDateOfWeek;
    }

    public int getWeek() {
	return week;
    }

    public int getYear() {
	return year;
    }

    @Override
    public int compareTo(WeekYear o) {
	return this.lastDayOfWeek.compareTo(o.lastDayOfWeek);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("WeekYear [week=");
	builder.append(week);
	builder.append(", year=");
	builder.append(year);
	builder.append(", lastDayOfWeek=");
	builder.append(lastDayOfWeek);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((lastDayOfWeek == null) ? 0 : lastDayOfWeek.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	WeekYear other = (WeekYear) obj;
	if (lastDayOfWeek == null) {
	    if (other.lastDayOfWeek != null)
		return false;
	} else if (!lastDayOfWeek.equals(other.lastDayOfWeek))
	    return false;
	return true;
    }

    public LocalDate getLastDayOfWeek() {
        return lastDayOfWeek;
    }
    
}
