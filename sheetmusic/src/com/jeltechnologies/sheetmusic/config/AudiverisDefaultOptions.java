package com.jeltechnologies.sheetmusic.config;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.utils.FileUtils;
import com.jeltechnologies.utils.StringUtils;

public class AudiverisDefaultOptions implements Serializable {
    private static final long serialVersionUID = 1228992920352192043L;

    private final static Logger LOGGER = LoggerFactory.getLogger(AudiverisDefaultOptions.class);

    private List<AudiverisOption> options = new ArrayList<AudiverisOption>();

    public AudiverisDefaultOptions() {
	try {
	    init();
	    if (LOGGER.isTraceEnabled()) {
		for (AudiverisOption option : options) {
		    logOption(System.out, option);
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Cannot load Audiveris dumped options", e);
	}
    }

    public AudiverisOption getOption(String name) {
	AudiverisOption found = null;
	Iterator<AudiverisOption> i = options.iterator();
	while (found == null && i.hasNext()) {
	    AudiverisOption current = i.next();
	    if (current.name().equals(name)) {
		found = current;
	    }
	}
	return found;
    }

    private void logOption(PrintStream stream, AudiverisOption option) {
	stream.println("// " + option.description() + " (" + option.dataType() + ")");
	stream.println(option.name() + " = " + option.value());
	stream.println();
    }

    private void init() throws IOException {
	List<String> lines = FileUtils.readTextFileLines("/audiveris_default_options_dumped.txt", true);
	String currentClassName = null;
	for (int i = 0; i < lines.size(); i++) {
	    String rawLine = lines.get(i);
	    String line = StringUtils.stripControlChars(rawLine).trim();
	    if (!line.isBlank()) {
		if (line.startsWith("[")) {
		    currentClassName = StringUtils.findBetween(line, "[", "]");
		} else {
		    if (currentClassName != null) {
			addOption(currentClassName, line);
		    }
		}
	    }
	}
    }

    private void addOption(String className, String line) {
	int equalsSign = line.indexOf("=");
	if (equalsSign > -1) {
	    String namePart = line.substring(0, equalsSign).trim();
	    String valuePart = line.substring(equalsSign + 1).trim();
	    String name = StringUtils.stripAfter(namePart, " ").trim();
	    String dataType = StringUtils.findAfter(namePart, " ").trim();
	    String value = StringUtils.stripAfter(valuePart, " ").trim();
	    String description = StringUtils.findAfter(valuePart, " ").trim();
	    AudiverisOption option = new AudiverisOption(className + "." + name, dataType, value, description);
	    options.add(option);
	}
    }

    @Override
    public String toString() {
	return "AudiverisDefaultOptions [options=" + options + "]";
    }
    
    public List<AudiverisOption> getOptions() {
	return options;
    }
}
