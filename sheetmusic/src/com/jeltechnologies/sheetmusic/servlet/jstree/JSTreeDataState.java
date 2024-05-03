package com.jeltechnologies.sheetmusic.servlet.jstree;

import java.io.Serializable;

public class JSTreeDataState implements Serializable {
    private static final long serialVersionUID = 6169418809421919005L;
    private boolean selected;
    private boolean opened;
    private boolean disabled;

    public boolean isSelected() {
	return selected;
    }

    public void setSelected(boolean selected) {
	this.selected = selected;
    }

    public boolean isOpened() {
	return opened;
    }

    public void setOpened(boolean opened) {
	this.opened = opened;
    }

    public boolean isDisabled() {
	return disabled;
    }

    public void setDisabled(boolean disabled) {
	this.disabled = disabled;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("JSTreeDataState [selected=");
	builder.append(selected);
	builder.append(", opened=");
	builder.append(opened);
	builder.append(", disabled=");
	builder.append(disabled);
	builder.append("]");
	return builder.toString();
    }

}
