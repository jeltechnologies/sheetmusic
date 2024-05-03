package com.jeltechnologies.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class InterfaceFinder<T> {
    private static final char FILE_SEPERATOR = File.separatorChar;
    private static final String FILE_ENCODING = "UTF-8";

    public List<T> getObjects(Class<T> anInterface, String packageName) throws ClassNotFoundException {
	List<T> objects = new ArrayList<T>();
	List<Class<T>> allClasses;
	allClasses = getClassesImplentingInterface(anInterface, packageName);
	for (Class<T> clazz : allClasses) {
	    T object;
	    try {
		object = (T) clazz.getDeclaredConstructor().newInstance();
		objects.add(object);
	    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
		    | SecurityException e) {
		throw new ClassNotFoundException("Cannot instantiate " + clazz.getName() + ". Does it have a default constructor?");
	    }
	}
	return objects;
    }

    @SuppressWarnings("unchecked")
    private List<Class<T>> getClassesImplentingInterface(Class<T> anInterface, String packageName) throws ClassNotFoundException {
	// This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
	ArrayList<File> directories = new ArrayList<File>();
	String packageToPath = packageName.replace('.', FILE_SEPERATOR);
	try {
	    ClassLoader cld = Thread.currentThread().getContextClassLoader();
	    if (cld == null) {
		throw new ClassNotFoundException("Can't get class loader.");
	    }
	    // Ask for all resources for the packageToPath
	    Enumeration<URL> resources = cld.getResources(packageToPath);
	    while (resources.hasMoreElements()) {
		directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), FILE_ENCODING)));
	    }
	} catch (NullPointerException x) {
	    throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Null pointer exception)");
	} catch (UnsupportedEncodingException encex) {
	    throw new ClassNotFoundException(packageName + " does not appear to be a valid package (Unsupported encoding)");
	} catch (IOException ioex) {
	    throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + packageName);
	}

	List<Class<T>> classes = new ArrayList<Class<T>>();
	// For every directoryFile identified capture all the .class files
	while (!directories.isEmpty()) {
	    File directoryFile = directories.remove(0);
	    if (directoryFile.exists()) {
		// Get the list of the files contained in the package
		File[] files = directoryFile.listFiles();
		for (File file : files) {
		    // we are only interested in .class files
		    if ((file.getName().endsWith(".class")) && (!file.getName().contains("$"))) {
			// removes the .class extension
			int index = directoryFile.getPath().indexOf(packageToPath);
			String packagePrefix = directoryFile.getPath().substring(index).replace(FILE_SEPERATOR, '.');
			try {
			    String className = packagePrefix + '.' + file.getName().substring(0, file.getName().length() - 6);
			    Class<?> clazz = Class.forName(className);
			    List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
			    if (interfaces.contains(anInterface)) {
				classes.add((Class<T>) clazz);
			    }
			} catch (NoClassDefFoundError e) {
			    // do nothing. this class hasn't been found by the loader, and we don't care.
			}
		    } else if (file.isDirectory()) { // If we got to a subdirectory
			directories.add(new File(file.getPath()));
		    }
		}
	    } else {
		throw new ClassNotFoundException(packageName + " (" + directoryFile.getPath() + ") does not appear to be a valid package");
	    }
	}
	return classes;
    }

}
