package com.jeltechnologies.sheetmusic.library;

import java.io.Serializable;

public class Category implements Serializable, Comparable<Category>{
    private static final long serialVersionUID = 5660641775188257339L;
    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Category o) {
	return name.compareTo(o.name);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Category [name=");
	builder.append(name);
	builder.append("]");
	return builder.toString();
    }
}
