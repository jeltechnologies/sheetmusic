package com.jeltechnologies.geoservices.config;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Environment {

    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    private static final String CONFIG_ENVIRONMENT_VARIABLE = "GEOSERVICES_CONFIG";

    private static Configuration config = loadConfiguration();

    public static final Configuration getConfiguration() {
	return config;
    }

    private static Configuration loadConfiguration() {
	String environmentName = CONFIG_ENVIRONMENT_VARIABLE;
	String value = System.getProperty(environmentName);
	if (value == null || value.isEmpty()) {
	    value = System.getenv(environmentName);
	}
	if (value == null) {
	    throw new IllegalStateException("Cannot load configuration. Please set the file name of the configuration YAML file in environment variable "
		    + CONFIG_ENVIRONMENT_VARIABLE);
	}
	File file = new File(value);
	if (!file.isFile()) {
	    throw new IllegalStateException("Cannot load configuration. Cannot find file " + value);
	}
	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	mapper.findAndRegisterModules();

	Configuration config;
	try {
	    config = (Configuration) mapper.readValue(file, Configuration.class);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("Using configuration: " + config);
	    }
	    String error = validateLoad(config);
	    if (error != null) {
		throw new IllegalStateException(error);
	    }
	    return config;
	} catch (IOException e) {
	    throw new IllegalStateException("Cannot load configuration. " + e.getMessage(), e);
	}
    }

    private static String validateLoad(Configuration config) {
	String error = null;
	File dataFolder = new File(config.dataFolder());
	if (dataFolder == null || !dataFolder.isDirectory()) {
	    error =  dataFolder + " is not a directory";
	}
	return error;
    }

}
