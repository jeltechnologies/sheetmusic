package com.jeltechnologies.screenmusic.maintenance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateChecksumThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecksumThread.class);

    @Override
    public void run() {
	LOGGER.info("Updating checksums thread started");
	Thread.currentThread().setName(this.getClass().getSimpleName());
	try {
	    //doIt();

	} catch (Exception e) {
	    LOGGER.error("Error updating checksums", e);
	}
	LOGGER.info("Updating checksums thread ended");
    }

//    private void doIt() throws Exception {
//	DBCrud db = null;
//	try {
//	    db = new DBCrud();
//	    List<String> allRelativeFilesNames = db.getAllFiles();
//	    for (String relativeFileName : allRelativeFilesNames) {
//
//		if (Thread.interrupted()) {
//		    throw new InterruptedException("Interrupted");
//		}
//		Book book = db.getBookByFileName(relativeFileName);
//
//		if (book != null) {
//		    File file = Environment.getFile(relativeFileName);
//
//		    if (file.isFile()) {
//			String checksum = OldIndexConsumer.createChecksum(file);
//			if (!checksum.equals(book.getFileChecksum())) {
//			    LOGGER.trace("Updating checksum for " + relativeFileName + " to " + checksum);
//			    try {
//				db.updateChecksum(book.getFileChecksum(), checksum);
//			    } catch (SQLException e) {
//				LOGGER.warn("Error updating checksum: " + e.getMessage(), e);
//			    }
//			}
//		    } else {
//			db.deleteFile(relativeFileName);
//		    }
//		}
//	    }
//	    LOGGER.info("Committing");
//	    db.commit();
//	} catch (Exception e) {
//	    LOGGER.info("Rolling back");
//	    db.rollback();
//	    throw e;
//	} finally {
//	    if (db != null) {
//		db.close();
//	    }
//	}
//
//    }

}
