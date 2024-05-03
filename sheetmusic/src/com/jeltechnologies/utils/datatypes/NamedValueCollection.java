package com.jeltechnologies.utils.datatypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A hybrid List and Map collection of NamesValue objects, that are either
 * compared case sensitive or case insentive.
 * 
 * @author Jelte
 *
 */
public class NamedValueCollection implements Iterable<NamedValue> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NamedValueCollection.class);

    private final List<NamedValue> list;
    private final boolean caseSensitve;

    public NamedValueCollection() {
	list = new ArrayList<NamedValue>();
	this.caseSensitve = false;
    }

    public NamedValueCollection(int size) {
	list = new ArrayList<NamedValue>(size);
	this.caseSensitve = false;
    }

    public NamedValueCollection(boolean caseSensitive) {
	list = new ArrayList<NamedValue>();
	this.caseSensitve = caseSensitive;
    }

    public NamedValueCollection(int size, boolean caseSensitive) {
	list = new ArrayList<NamedValue>(size);
	this.caseSensitve = caseSensitive;
    }
    
    private NamedValue getFirstNamedValue(String name) {
	NamedValue found = null;
	Iterator<NamedValue> iterator = list.iterator();
	while (found == null && iterator.hasNext()) {
	    NamedValue current = iterator.next();
	    if (this.caseSensitve) {
		if (current.getName().equals(name)) {
		    found = current;
		}
	    } else {
		if (current.getName().equalsIgnoreCase(name)) {
		    found = current;
		}
	    }
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getFirstNamedValue (" + name + ") => " + found);
	}
	return found;
    }

    /**
     * Adds a new name value pair, or replaces an existing value if the name
     * already exists in the List
     * 
     * @param name
     * @param value
     */
    public void put(String name, String value) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("put (name='" + name + "', value='" + value + "')");
	}

	String notNullValue;
	if (value == null) {
	    notNullValue = "";
	} else {
	    notNullValue = value;
	}
	NamedValue found = getFirstNamedValue(name);
	if (found != null) {
	    found.setValue(notNullValue);
	} else {
	    list.add(new NamedValue(name, notNullValue));
	}

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Now list is: " + list);
	}
    }

    /**
     * Adds a new name value pair
     * 
     * @param name
     * @param value
     *            if it is null it will be changed to empty string to prevent
     *            NullPointerExceptions
     */
    public void add(String name, String value) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("add (name='" + name + "', value='" + value + "')");
	}

	String notNullValue;
	if (value == null) {
	    notNullValue = "";
	} else {
	    notNullValue = value;
	}
	list.add(new NamedValue(name, notNullValue));
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Now list is: " + list);
	}
    }
    
    /**
     * Adds a new name value pair
     * 
     * @param name
     * @param value
     *            if it is null it will be changed to empty string to prevent
     *            NullPointerExceptions
     */
    public void add(String name, int value) {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("add (name='" + name + "', value='" + value + "')");
	}
	add(name, String.valueOf(value));
    }

    /**
     * Returns the value for a name in the Map, or an empty string in case there
     * is no value for a name. It will never return a null value to prevent
     * NullPointerExceptions
     * 
     * @param name
     * @return
     */
    public String getFirst(String name) {
	NamedValue found = getFirstNamedValue(name);
	String result;
	if (found == null) {
	    result = "";
	} else {
	    result = found.getValue();
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getFirst (" + name + ") => '" + result + "'");
	}
	return result;
    }

    public List<String> getAll(String name) {
	List<String> values = new ArrayList<String>();
	for (NamedValue namedValue : this.list) {
	    NamedValue found = null;
	    if (this.caseSensitve) {
		if (name.equals(namedValue.getName())) {
		    found = namedValue;
		}
	    } else {
		if (name.equalsIgnoreCase(namedValue.getName())) {
		    found = namedValue;
		}
	    }
	    if (found != null) {
		values.add(found.getValue());
	    }
	}
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getAll (" + name + ") => " + values);
	}
	return values;
    }

    @Override
    public Iterator<NamedValue> iterator() {
	return list.iterator();
    }

    /**
     * Does the Map already contain a value for a name?
     * 
     * @param name
     * @return
     */
    public boolean containsName(String name) {
	NamedValue found = getFirstNamedValue(name);
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("contansName(" + name + ") => " + (found != null));
	}
	return found != null;
    }

    public boolean isEmpty() {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("isEmpty() => " + (list.isEmpty()));
	}
	return list.isEmpty();
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(list);
	return builder.toString();
    }

}
