package com.jeltechnologies.utils.datatypes;

import java.io.Serializable;

/**
 * The classic named value combination, allowing lists with sequential order
 * instead of Maps
 * 
 * @author Jelte
 */
public class NamedValue implements Serializable {
    private static final long serialVersionUID = 8409282139968513134L;

    private final String name;

    private String value;

    public NamedValue(String name, String value) {
	super();
	this.name = name;
	this.value = value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getName() {
	return name;
    }

    public String getValue() {
	return value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	NamedValue other = (NamedValue) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(name);
	builder.append("='");
	builder.append(value);
	builder.append("'");
	return builder.toString();
    }

}
