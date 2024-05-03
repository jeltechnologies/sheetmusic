package com.jeltechnologies.sheetmusic.tags;

import java.io.IOException;
import java.util.List;

import com.jeltechnologies.utils.StringUtils;

public abstract class AbstractSelectTag extends BaseTag {
    protected abstract List<String> getOptions();
    
    protected abstract String getSelected();
    
    protected String containerid;
    
    protected String containerclass = "sheetmusic-select";
    
    protected String label;
    
    protected String onclick;
    
    protected String onchange;
    
    public String getContainerclass() {
        return containerclass;
    }

    public void setContainerclass(String containerclass) {
        this.containerclass = containerclass;
    }

    public String getOnchange() {
        return onchange;
    }
    
    public String getContainerid() {
        return containerid;
    }

    public void setContainerid(String containerid) {
        this.containerid = containerid;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public String getOnClick() {
        return getOnclick();
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnClick(String onClick) {
        setOnclick(onClick);
    }

    public void setOnclick(String onClick) {
        this.onclick = onClick;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void processTag() throws Exception {
	openTag();
	addSelect();
	closeTag();
    }
    
    private void openTag() throws IOException {
	StringBuilder b = new StringBuilder();
	b.append("<div");
	if (containerid != null) {
	    b.append(" id=\"").append(containerid).append("\"");
	}
	b.append(" class=\"").append(containerclass).append("\">");
	addLine(b.toString());
	if (label != null) {
	    b = new StringBuilder();
	    b.append("  <label for=\"").append(id).append("\">").append(label).append("</label>");
	    addLine(b.toString());
	}
    }
    
    private void addSelect() throws IOException {
	StringBuilder s = new StringBuilder();
	s.append("  <select id=\"").append(id).append("\" class=\"filter-sort-select\"");
	if (onclick != null) {
	    s.append(" onclick=\"").append(onclick).append("\"");
	}
	if (onchange != null) {
	    s.append(" onchange=\"").append(onchange).append("\"");
	}
	s.append(">");
	addLine(s.toString());
	List<String> options = getOptions();
	String selected = getSelected();
	for (String option : options) {
	    StringBuilder b = new StringBuilder();
	    b.append("    <option value=\"").append(option).append("\"");
	    if (option.equals(selected)) {
		b.append(" selected");
	    }
	    String htmlOption = StringUtils.encodeHtml(option);
	    b.append(">").append(htmlOption).append("</option>");
	    addLine(b.toString());
	}
	addLine("  </select>");
    }
    
    private void closeTag() throws IOException {
	addLine("</div>");
    }
    
}
