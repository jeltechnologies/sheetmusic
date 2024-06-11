package com.jeltechnologies.screenmusic.library;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.BookFilesFinder;
import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.config.Configuration;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;

public class HouseKeepingThread implements Runnable, HouseKeepingThreadMBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(HouseKeepingThread.class);
    private String threadName;
    private String status = "";
    private final User user;
    private final ScreenMusicContext context;
    private final boolean deleteMissingFilesFromDatabase;

    public HouseKeepingThread(User user, boolean deleteMissingFilesFromDatabase, ScreenMusicContext context) {
	this.deleteMissingFilesFromDatabase = deleteMissingFilesFromDatabase;
	this.user = user;
	this.context = context;
    }

    @Override
    public void run() {
	threadName = this.getClass().getSimpleName();
	context.getJmx().registerMBean(this.getClass().getSimpleName(), "HouseKeeping", this);
	Thread.currentThread().setName(threadName);
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info(threadName + " started");
	}

	boolean interrupted = false;

	List<File> pdfFiles = new BookFilesFinder(user.getSheetMusicFolder()).getAllBookFiles();
	if (Thread.interrupted()) {
	    interrupted = true;
	}
	if (!interrupted) {
	    interrupted = extractMetaData(pdfFiles);
	}

	if (!interrupted) {
	    try {
		status = "Removing books not found on disk from database";
		updateAllFoundBooks(pdfFiles);
	    } catch (InterruptedException e) {
		interrupted = true;
	    }
	}

	if (!interrupted) {
	    try {
		cleanCache();
	    } catch (InterruptedException e) {
		interrupted = true;
	    }
	}

	if (!interrupted) {
	    status = "Thread ended normally";
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info(threadName + " ended normally");
	    }
	} else {
	    if (LOGGER.isInfoEnabled()) {
		status = "Thread was interrupted";
		LOGGER.info(threadName + " interrupted");
	    }
	}
	context.getJmx().unregisterMBean(this.getClass().getSimpleName());
    }

    private void updateAllFoundBooks(List<File> foundBookFiles) throws InterruptedException {
	Library library = new Library(user, context);
	List<String> allRelativeFileNamesInDB = library.getAllRelativeFiles();

	if (Thread.interrupted()) {
	    throw new InterruptedException();
	}

	List<String> allFilesOnDisk = new ArrayList<String>(foundBookFiles.size());
	List<String> filesNotOnDisk = new ArrayList<String>();
	for (File file : foundBookFiles) {
	    allFilesOnDisk.add(user.getRelativeFileName(file));
	}

	for (String fileInDB : allRelativeFileNamesInDB) {
	    if (!allFilesOnDisk.contains(fileInDB)) {
		filesNotOnDisk.add(fileInDB);
	    }
	}

	if (deleteMissingFilesFromDatabase) {
	    for (String fileNotOnDisk : filesNotOnDisk) {
		if (Thread.interrupted()) {
		    throw new InterruptedException();
		}
		LOGGER.info("Book file was removed: [" + fileNotOnDisk + "]");
		library.deleteFile(fileNotOnDisk);
	    }
	}
    }

    private boolean extractMetaData(List<File> pdfFiles) {
	Iterator<File> iterator = pdfFiles.iterator();
	boolean interrupted = false;
	while (iterator.hasNext() && !interrupted) {
	    File file = iterator.next();
	    try {
		collectMetaData(file);
	    } catch (InterruptedException interruptedException) {
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info(threadName + " interrupted");
		}
		interrupted = true;
	    } catch (Throwable t) {
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("LibraryBinaryFileImpl collection found read problem " + file.getName() + " because " + t.getMessage());
		}
	    }
	}
	return interrupted;
    }

    private void collectMetaData(File file) throws InterruptedException {
	if (Thread.interrupted()) {
	    throw new InterruptedException();
	}
	status = "Collecting metadata for " + user.getRelativeFileName(file);
	new Library(user, context).getBookByFile(file);
	status = "Idle";
    }

    private void cleanCache() throws InterruptedException {
	LOGGER.info("Cleaning cache");
	status = "Housekeeping cache folders.. getting folders";
	List<String> checksums = getAllCacheFolders();
	Library library = new Library(user, context);
	for (String checksum : checksums) {
	    boolean inLibrary = library.containsBook(checksum);
	    if (!inLibrary) {
		moveCacheFolderToDeleted(checksum);
	    }
	}
	status = "Done";
    }

    private List<String> getAllCacheFolders() throws InterruptedException {
	File[] folders = Configuration.getInstance().storage().getCacheFolderExtracted().listFiles(new FileFilter() {
	    @Override
	    public boolean accept(File pathname) {
		return pathname.isDirectory();
	    }
	});
	List<String> checksums = new ArrayList<String>(folders.length);
	for (File folder : folders) {
	    if (Thread.interrupted()) {
		throw new InterruptedException();
	    }
	    checksums.add(folder.getName());
	}
	return checksums;
    }

    private void moveCacheFolderToDeleted(String checksum) {
	status = "Cleaning " + checksum;
	Configuration configuration = Configuration.getInstance();
	File cacheFolderExtracted = configuration.storage().getCacheFolderExtracted();
	File from = new File(cacheFolderExtracted.getAbsolutePath() + "/" + checksum);
	File cacheFolderDeleted = configuration.storage().getCacheFolderDeleted();
	File to = new File(cacheFolderDeleted.getAbsolutePath() + "/" + checksum);

	if (!from.isDirectory()) {
	    throw new IllegalStateException("Internal error " + from + " is not a directory");
	}

	if (to.isDirectory()) {
	    deleteCacheFolder(to);
	}

	try {
	    Path fromPath = from.toPath();
	    Path toPath = to.toPath();
	    Files.move(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
	    LOGGER.info("Moved directory " + fromPath + " to " + toPath);
	} catch (IOException ex) {
	    LOGGER.error("Cannot move folder " + from.getAbsolutePath() + " to " + to.getAbsolutePath(), ex);
	}
    }

    private void deleteCacheFolder(File folder) {
	if (folder.isDirectory()) {
	    boolean ok = true;
	    File[] cachedFiles = folder.listFiles();
	    for (int i = 0; i < cachedFiles.length && ok; i++) {
		ok = cachedFiles[i].delete();
		if (!ok) {
		    LOGGER.warn("Cannot deleted cached file " + cachedFiles[i].getAbsolutePath());
		}
	    }
	    if (ok) {
		ok = folder.delete();
		if (!ok) {
		    LOGGER.warn("Cannot deleted cache folder " + folder.getAbsolutePath());
		}
	    }
	    if (ok) {
		LOGGER.info("Removed folder '" + folder.getAbsolutePath() + "'");
	    }
	}
    }

    @Override
    public String getStatus() {
	return status;
    }

}
