package com.jeltechnologies.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.utils.OperatingSystemCommandArgument.Quotes;

public class OperatingSystemCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatingSystemCommand.class);
    private final File exe;
    private final String username;
    private final String password;
    private final File executor;
    private List<OperatingSystemCommandArgument> arguments = new ArrayList<>();
    private List<String> responseLines;
    private String description;
    private int exitValue = -1;
    private final List<OperatingSystemCommandListener> listeners = new ArrayList<OperatingSystemCommandListener>();
    private Map<String, String> environmentVariables = new HashMap<String, String>();
    private File folder = null;

    public OperatingSystemCommand(File exe) {
	this.exe = exe;
	this.username = null;
	this.password = null;
	this.executor = null;
    }

    public OperatingSystemCommand(File exe, String username, String password, File executor) {
	this.exe = exe;
	this.username = username;
	this.password = password;
	this.executor = executor;
    }

    public void addArgument(String argument) {
	this.arguments.add(new OperatingSystemCommandArgument(argument));
    }

    public void addArgument(String argument, Quotes quotes) {
	this.arguments.add(new OperatingSystemCommandArgument(argument, quotes));
    }

    public void addListener(OperatingSystemCommandListener listener) {
	this.listeners.add(listener);
    }

    public void setEnvironmentVariable(String variable, String value) {
	environmentVariables.put(variable, value);
    }

    public void setFolder(File folder) {
	this.folder = folder;
    }

    public void execute() throws IOException, InterruptedException {
	responseLines = new ArrayList<>();
	ProcessBuilder processBuilder = new ProcessBuilder();

	List<String> commands = new ArrayList<String>();
	String exePath = exe.getAbsolutePath();
	
	boolean runAsSpecialUser = username != null && password != null;

	if (runAsSpecialUser) {
	    // https://stackoverflow.com/questions/1409852/invoking-an-external-process-with-a-different-user-in-java
	    commands.add(executor.getAbsolutePath());
	    commands.add("-i");
	    commands.add("-u");
	    commands.add(username);
	    commands.add("-p");
	    commands.add(password);
	}

	commands.add(exePath);

	for (OperatingSystemCommandArgument argument : arguments) {
	    commands.add(argument.toString());
	}

	StringBuilder b = new StringBuilder();
	for (int i = 0; i < commands.size(); i++) {
	    if (i > 0) {
		b.append(" ");
	    }
	    b.append(commands.get(i));
	}
	description = b.toString();

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Executing " + description);
	}

	if (environmentVariables != null && !environmentVariables.isEmpty()) {
	    Map<String, String> environment = processBuilder.environment();
	    for (String name : environmentVariables.keySet()) {
		String value = environmentVariables.get(name);
		LOGGER.debug("Setting environment variable " + name + "=" + value);
		environment.put(name, value);
	    }
	}

	if (folder != null) {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Executing in folder " + folder);
	    }
	    processBuilder.directory(folder);
	}

	processBuilder.command(commands);
	processBuilder.redirectErrorStream(true);

	Process process;
	process = processBuilder.start();
	BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	String line;
	while ((line = reader.readLine()) != null) {
	    if (Thread.interrupted()) {
		throw new InterruptedException();
	    }
	    if (LOGGER.isTraceEnabled()) {
		LOGGER.trace(line);
	    }
	    for (OperatingSystemCommandListener listener : listeners) {
		listener.receivedLine(line);
	    }
	    responseLines.add(line);
	}
	exitValue = process.waitFor();

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Exit value: " + exitValue);
	}

	if (exitValue != 0) {
	    throw new IOException(description + " => exit value: " + exitValue);
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Successfully executed " + description);
	}
    }

    public List<String> getOutput() {
	return responseLines;
    }

    public String getDescription() {
	return description;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("OperatingSystemCommand [description=");
	builder.append(description);
	if (exitValue != -1) {
	    builder.append(", exitValue=");
	    builder.append(exitValue);
	}
	builder.append("]");
	return builder.toString();
    }

}
