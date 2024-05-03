package com.jeltechnologies.utils;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final int MAX_BINARY_FILE_IN_MEMORY = 10485760; // 10 MB

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    private static final int BINARY_READ_CHUNK_LENGTH = 4096;

    private static final int MAX_IMAGE_SIZE = 10 * 1048576; // 10 MB preventing
							    // memory leak

    public static final String NEWLINE = System.getProperty("line.separator");

    private final static String FILE_SEPERATOR = System.getProperty("file.separator");

    private static void makeFolderToStoreFileIfNeeded(String fileName) throws IOException {
	int lastSeperator = fileName.lastIndexOf(FILE_SEPERATOR);
	if (lastSeperator > 0) {
	    String folderName = fileName.substring(0, lastSeperator);
	    File folder = new File(folderName);
	    if (folder.isFile()) {
		throw new IOException("Cannot create folder " + folderName + " to store " + fileName + " because this is an existing file");
	    } else {
		if (!folder.exists()) {
		    boolean ok = folder.mkdirs();
		    if (!ok) {
			throw new IOException("Cannot create folder " + folderName + " to store " + fileName);
		    } else {
			if (LOGGER.isDebugEnabled()) {
			    LOGGER.debug("Created folder " + folderName);
			}
		    }
		}
	    }
	}
    }

    private static void removeFolderThatContainedFileIfEmpty(String fileName) throws IOException {
	int lastSeperator = fileName.lastIndexOf(FILE_SEPERATOR);
	if (lastSeperator > 0) {
	    String folderName = fileName.substring(0, lastSeperator);
	    File folder = new File(folderName);
	    if (folder.isDirectory()) {
		String[] children = folder.list();
		if (children.length == 0) {
		    folder.delete();
		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Successfully deleted folder " + folderName);
		    }
		}
	    }
	}
    }

    public static void moveFile(File from, File to, boolean overwrite) throws IOException {
	moveFile(from.getAbsolutePath(), to.getAbsolutePath(), overwrite, false);
    }

    public static void moveFile(String fromAbsoluteFilePath, String toAbsoluteFilePath, boolean overwrite) throws IOException {
	moveFile(fromAbsoluteFilePath, toAbsoluteFilePath, overwrite, false);
    }

    public static void moveFile(String fromAbsoluteFilePath, String toAbsoluteFilePath, boolean overwrite, boolean deleteEmptyFolder) throws IOException {
	makeFolderToStoreFileIfNeeded(toAbsoluteFilePath);
	Path source = Paths.get(fromAbsoluteFilePath);
	Path destination = Paths.get(toAbsoluteFilePath);
	if (overwrite) {
	    Files.move(source, destination, REPLACE_EXISTING);
	} else {
	    Files.move(source, destination);
	}
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Successfully moved [" + fromAbsoluteFilePath + "] to [" + toAbsoluteFilePath + "]");
	}
	if (deleteEmptyFolder) {
	    removeFolderThatContainedFileIfEmpty(fromAbsoluteFilePath);
	}
    }

    public static String readTextFile(String filePath) throws IOException {
	return readTextFile(filePath, null);
    }

    public static String readTextFile(String filePath, String charSetName) throws IOException {
	return readTextFile(filePath, true, charSetName);
    }

    public static String readTextFile(String filePath, boolean inClassPath, String charSetName) throws IOException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("readFile " + filePath);
	}
	StringBuilder builder = new StringBuilder();
	Scanner scanner = null;
	InputStream in;
	try {
	    if (inClassPath) {
		in = FileUtils.class.getResourceAsStream("/" + filePath);
	    } else {
		in = new FileInputStream(new File(filePath));
	    }
	    if (charSetName != null) {
		scanner = new Scanner(in, charSetName);
	    } else {
		scanner = new Scanner(in);
	    }
	    while (scanner.hasNext()) {
		builder.append(scanner.nextLine());
	    }
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	}
	return builder.toString();
    }

    public static List<String> readTextFileLines(String filePath) throws IOException {
	return readTextFileLines(filePath, true, null);
    }

    public static List<String> readTextFileLines(String filePath, boolean inClassPath) throws IOException {
	return readTextFileLines(filePath, inClassPath, null);
    }

    public static List<String> readTextFileLines(String filePath, String charSetName) throws IOException {
	return readTextFileLines(filePath, true, charSetName);
    }

    public static List<String> readTextFileLines(String filePath, boolean inClassPath, String charSetName) throws IOException {
	List<String> result = new ArrayList<String>();
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("readTextFileLines " + filePath);
	}
	Scanner scanner = null;
	InputStream in;
	try {
	    if (inClassPath) {
		in = FileUtils.class.getResourceAsStream("/" + filePath);
	    } else {
		in = new FileInputStream(new File(filePath));
	    }
	    if (charSetName != null) {
		scanner = new Scanner(in, charSetName);
	    } else {
		scanner = new Scanner(in);
	    }
	    while (scanner.hasNext()) {
		result.add(scanner.nextLine());
	    }
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	}
	return result;
    }

    public static void writeTextFile(String filePath, String text) throws IOException {
	writeTextFile(filePath, text, Charset.defaultCharset());
    }

    public static void writeTextFile(String filePath, List<String> lines) throws IOException {
	writeTextFile(filePath, lines, Charset.defaultCharset());
    }

    public static void writeTextFile(String filePath, String text, Charset charset) throws IOException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("writeFile " + filePath);
	}
	makeFolderToStoreFileIfNeeded(filePath);
	Writer output = null;
	try {
	    output = new OutputStreamWriter(new FileOutputStream(filePath), charset);
	    output.write(text);
	} finally {
	    if (output != null) {
		output.close();
	    }
	}
    }

    public static void writeTextFile(String filePath, List<String> lines, Charset charset) throws IOException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("writeFile " + filePath);
	}
	makeFolderToStoreFileIfNeeded(filePath);
	Writer output = null;
	try {
	    output = new OutputStreamWriter(new FileOutputStream(filePath), charset);
	    for (int i = 0; i < lines.size(); i++) {
		if (i > 0) {
		    output.write(NEWLINE);
		}
		output.write(lines.get(i));
	    }
	} finally {
	    if (output != null) {
		output.close();
	    }
	}
    }

    public static void writeBinaryFile(String filePath, byte[] data) throws IOException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("writeBinaryFile " + filePath);
	}
	File fileTo = new File(filePath);
	if (fileTo.isFile()) {
	    LOGGER.info("File already exist on disk, writing is skipped for file [" + filePath + "]");
	} else {
	    makeFolderToStoreFileIfNeeded(filePath);
	    FileOutputStream out = null;
	    try {
		out = new FileOutputStream(fileTo);
		out.write(data);
	    } finally {
		if (out != null) {
		    out.close();
		}
	    }
	}
    }

    public static byte[] readBinaryFile(String filePath) throws IOException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("readBinaryFile " + filePath);
	}
	File file = new File(filePath);
	if (file.length() > MAX_BINARY_FILE_IN_MEMORY) {
	    throw new IOException("Cannot read binary files larger than " + MAX_BINARY_FILE_IN_MEMORY + " bytes");
	}
	byte[] fileData = new byte[(int) file.length()];
	DataInputStream dis = null;
	try {
	    dis = new DataInputStream(new FileInputStream(file));
	    dis.readFully(fileData);
	    return fileData;
	} finally {
	    if (dis != null) {
		dis.close();
	    }
	}
    }

    public static byte[] readImageFromClasspath(String localPath) throws IOException {
	ByteArrayOutputStream bais = new ByteArrayOutputStream();
	InputStream is = null;
	is = FileUtils.class.getResourceAsStream(localPath);
	if (is == null) {
	    throw new FileNotFoundException("Cannot find " + localPath);
	}
	byte[] byteChunk = new byte[BINARY_READ_CHUNK_LENGTH];
	int n;
	int totalBytesRead = 0;
	while ((n = is.read(byteChunk)) > 0) {

	    bais.write(byteChunk, 0, n);
	    totalBytesRead = totalBytesRead + n;
	    if (totalBytesRead > MAX_IMAGE_SIZE) {
		throw new IOException("Maximum image size is " + MAX_IMAGE_SIZE + " bytes");
	    }
	}
	return bais.toByteArray();
    }

    public static void writeObject(String fileName, Object object) throws IOException {
	FileOutputStream fos = null;
	ObjectOutputStream outputStream = null;
	try {
	    fos = new FileOutputStream(fileName);
	    outputStream = new ObjectOutputStream(fos);
	    outputStream.writeObject(object);
	} finally {
	    if (outputStream != null) {
		outputStream.close();
	    }
	    if (fos != null) {
		fos.close();
	    }
	}
    }

    public static Object readObject(String fileName) throws IOException, ClassNotFoundException {
	FileInputStream fis = null;
	ObjectInputStream inputStream = null;
	try {
	    fis = new FileInputStream(fileName);
	    inputStream = new ObjectInputStream(fis);
	    return inputStream.readObject();
	} finally {
	    if (inputStream != null) {
		inputStream.close();
	    }
	    if (fis != null) {
		fis.close();
	    }
	}
    }

    public static void copyFile(String source, String destination) throws IOException {
	copyFile(source, destination, true);
    }

    public static void copyFile(String source, String destination, boolean overwrite) throws IOException {
	Path sourceFile = Paths.get(source);
	Path targetFile = Paths.get(destination);
	if (overwrite) {
	    Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
	} else {
	    Files.copy(sourceFile, targetFile);
	}
    }

    public static void copyFile(File source, File destination, boolean overwrite) throws IOException {
	Path sourceFile = source.toPath();
	Path targetFile = destination.toPath();
	if (overwrite) {
	    Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
	} else {
	    Files.copy(sourceFile, targetFile);
	}
    }

    public static void copyDirectory(File from, File to, boolean overwrite) throws IOException {
	try {
	    copyDirectoryJavaNIO(from.toPath(), to.toPath(), overwrite);
	} catch (UncheckedIOException e) {
	    throw e.getCause();
	}
    }

    private static void copyDirectoryJavaNIO(Path source, Path target, boolean overwrite) throws IOException {
	// From https://mkyong.com/java/how-to-copy-directory-in-java/
	// is this a directory?
	if (Files.isDirectory(source)) {
	    // if target directory exist?
	    if (Files.notExists(target)) {
		// create it
		Files.createDirectories(target);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Directory created : " + target);
		}
	    }

	    // list all files or folders from the source, Java 1.8, returns a stream
	    // doc said need try-with-resources, auto-close stream
	    try (Stream<Path> paths = Files.list(source)) {
		// recursive loop
		paths.forEach(p -> copyDirectoryJavaNIOWrapper(p, target.resolve(source.relativize(p)), overwrite));
	    }

	} else {
	    // if file exists, replace it
	    if (overwrite) {
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	    } else {
		Files.copy(source, target);
	    }
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format("Copy File from \t'%s' to \t'%s'", source, target));
	    }
	}
    }

    // extract method to handle exception in lambda
    private static void copyDirectoryJavaNIOWrapper(Path source, Path target, boolean overwrite) {
	try {
	    copyDirectoryJavaNIO(source, target, overwrite);
	} catch (IOException e) {
	    throw new UncheckedIOException(e);
	}
    }

    public static String createMD5Checksum(File file) throws IOException, InterruptedException {
	MessageDigest md;
	try {
	    md = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e) {
	    throw new IllegalStateException("Cannot find messagedigest");
	}
	try (InputStream fis = new FileInputStream(file)) {
	    byte[] buffer = new byte[1024];
	    int nread;
	    while ((nread = fis.read(buffer)) != -1) {
		md.update(buffer, 0, nread);
	    }
	}

	// bytes to hex
	StringBuilder result = new StringBuilder();
	for (byte b : md.digest()) {
	    result.append(String.format("%02x", b));
	}
	return result.toString();
    }

    public static void saveImage(File file, BufferedImage image, String imageExtension, float compressionQuality) throws IOException {
	File folder = file.getParentFile();
	if (!folder.isDirectory()) {
	    folder.mkdirs();
	}
	try {
	    ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
	    ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
	    jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	    jpgWriteParam.setCompressionQuality(compressionQuality);
	    if (imageExtension.endsWith("png")) {
		ImageIO.write(image, imageExtension, file);
	    } else {
		ImageOutputStream outputStream = null;
		try {
		    // make sure it overwrites
		    if (file.isFile()) {
			file.delete();
		    }
		    outputStream = new FileImageOutputStream(file);
		    jpgWriter.setOutput(outputStream);
		    IIOImage outputImage = new IIOImage(image, null, null);
		    jpgWriter.write(null, outputImage, jpgWriteParam);
		} finally {
		    jpgWriter.dispose();
		    if (outputStream != null) {
			outputStream.close();
		    }
		}
	    }
	} catch (IOException e) {
	    LOGGER.error("Cannot write image to " + file, e);
	}
    }

}
