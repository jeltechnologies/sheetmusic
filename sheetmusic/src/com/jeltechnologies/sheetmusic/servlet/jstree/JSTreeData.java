package com.jeltechnologies.sheetmusic.servlet.jstree;

import java.io.Serializable;
import java.util.List;

/**
 * https://github.com/vakata/jstree/wiki
 */
public class JSTreeData implements Serializable {
    private static final long serialVersionUID = -3236841595352397681L;
    private String id;
    private String text;
    private JSTreeDataState state;
    private String icon;
    private String type = "default";
    private List<JSTreeData> children;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public JSTreeDataState getState() {
	return state;
    }

    public void setState(JSTreeDataState state) {
	this.state = state;
    }

    public String getIcon() {
	return icon;
    }

    public void setIcon(String icon) {
	this.icon = icon;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public List<JSTreeData> getChildren() {
	return children;
    }

    public void setChildren(List<JSTreeData> childen) {
	this.children = childen;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("JSTreeData [id=");
	builder.append(id);
	builder.append(", text=");
	builder.append(text);
	builder.append(", state=");
	builder.append(state);
	builder.append(", icon=");
	builder.append(icon);
	builder.append(", type=");
	builder.append(type);
	builder.append(", children=");
	builder.append(children);
	builder.append("]");
	return builder.toString();
    }
}
