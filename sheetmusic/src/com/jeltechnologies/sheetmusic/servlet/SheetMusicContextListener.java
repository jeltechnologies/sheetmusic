package com.jeltechnologies.sheetmusic.servlet;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.config.Admin;
import com.jeltechnologies.sheetmusic.config.Configuration;
import com.jeltechnologies.sheetmusic.config.ConfigurationLoader;
import com.jeltechnologies.sheetmusic.db.DBCrud;
import com.jeltechnologies.sheetmusic.db.DBUsers;
import com.jeltechnologies.sheetmusic.extractedfilestorage.IndexProducer;
import com.jeltechnologies.sheetmusic.extractedfilestorage.ThumbnailsQueue;
import com.jeltechnologies.sheetmusic.extractedfilestorage.ThumbnailsTaskConsumer;
import com.jeltechnologies.sheetmusic.maintenance.MaintenanceThread;
import com.jeltechnologies.sheetmusic.opticalmusicrecognition.Consumer;
import com.jeltechnologies.sheetmusic.opticalmusicrecognition.JobList;
import com.jeltechnologies.sheetmusic.opticalmusicrecognition.JobQueue;
import com.jeltechnologies.utils.JMXUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class SheetMusicContextListener implements ServletContextListener {
    private static final String MBEAN_INDEX_SERVICE_TYPE = "Index Service";
    private static final String MBEAN_OMR_SERVICE_TYPE = "Optical Music Recognition Service";
    private static final Logger LOGGER = LoggerFactory.getLogger(SheetMusicContextListener.class);
    private static final String CONFIG_ENVIRONMENT_VARIABLE = "SHEETMUSIC_CONFIG";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
	LOGGER.info("===============================================================================================================");
	LOGGER.info("Sheet Music - Starting on " + System.getProperty("os.name"));
	System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");
	DBUsers dbUsers = null;
	try {
	    loadConfiguration();
	    ServletContext context = servletContextEvent.getServletContext();
	    addContextScopeObjects(context);
	    prepareDatabase(context);
	    dbUsers = new DBUsers();
	    List<User> users = dbUsers.getUsers();
	    runEssentialTasksImmediatialy(users, context);
	    startScheduledTasks(users, context);
	    // runMaintenance(context);
	} catch (Throwable e) {
	    LOGGER.error("Error starting sheetmusic web application: " + e.getMessage(), e);
	} finally {
	    if (dbUsers != null) {
		dbUsers.close();
	    }
	}
	LOGGER.info("===============================================================================================================");
    }

    /**
     * Adds objects to the instance scope
     * 
     * This must be called once, from a single thread during startup
     * 
     * @param context
     * @throws IOException
     */
    private void addContextScopeObjects(ServletContext context) throws IOException {
	JMXUtils jmx = new JMXUtils();
	new ContextObjectStore<JMXUtils>(JMXUtils.class).set(jmx, context);
	
	ThreadService threadService = new ThreadService();
	new ContextObjectStore<ThreadService>(ThreadService.class).set(threadService, context);
	
	ThumbnailsQueue thumbnailsQueue = new ThumbnailsQueue();
	new ContextObjectStore<ThumbnailsQueue>(ThumbnailsQueue.class).set(thumbnailsQueue, context);
    }
    
    private void loadConfiguration() throws IOException {
	String environmentName = CONFIG_ENVIRONMENT_VARIABLE;
	String yamlFileName = System.getProperty(environmentName);
	if (yamlFileName == null || yamlFileName.isEmpty()) {
	    yamlFileName = System.getenv(environmentName);
	}
	if (yamlFileName != null) {
	    new ConfigurationLoader().load(Path.of(yamlFileName));
	} else {
	    throw new IOException(
		    "Cannot load configuration. Set environment variable " + CONFIG_ENVIRONMENT_VARIABLE + " to the yaml file with configuration");
	}
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
	LOGGER.info("Sheet Music - Shutting down......");
	SheetMusicContext context = new SheetMusicContext(servletContextEvent.getServletContext());
	ThreadService threadService = context.getThreadService();
	threadService.shutdown();
	JMXUtils jmx = context.getJmx();
	jmx.unregisterAllMBeans();
	LOGGER.info("Sheet Music - Shutdown completed");
    }

    private void prepareDatabase(ServletContext context) {
	DBCrud crud = null;
	try {
	    crud = new DBCrud();
	    Admin admin = Configuration.getInstance().admin();
	    if (admin == null) {
		LOGGER.warn("Admin not set");
	    } else {
		crud.prepareDatabase(admin);
	    }
	} catch (SQLException sqlException) {
	    throw new IllegalStateException("Cannot connect and prepare database", sqlException);
	} finally {
	    if (crud != null) {
		crud.close();
	    }
	}
    }

    @SuppressWarnings("unused")
    private void runMaintenance(ServletContext context) {
	ExecutorService executor = Executors.newSingleThreadExecutor();
	// executor.execute(new HouseKeepingThread(true));
	executor.execute(new MaintenanceThread());
	executor.shutdown();
    }

    /**
     * Make sure this is done in startup single thread to prevent race condition
     * 
     * @param users
     * @param context
     */
    private void runEssentialTasksImmediatialy(List<User> users, ServletContext context) {
	Configuration config = Configuration.getInstance();
	SheetMusicContext sheetMusicContext = new SheetMusicContext(context);
	ThreadService executor = sheetMusicContext.getThreadService();
	ThumbnailsQueue queue = sheetMusicContext.getThumbnailsQueue();
	JMXUtils jmx = sheetMusicContext.getJmx();
	jmx.registerMBean(ThumbnailsQueue.class.getSimpleName(), MBEAN_INDEX_SERVICE_TYPE, queue);
	int threads = config.indexConsumeThreads();
	for (User user : users) {
	    for (int i = 0; i < threads; i++) {
		int threadNumber = i + 1;
		ThumbnailsTaskConsumer consumer = new ThumbnailsTaskConsumer(user, threadNumber, queue);
		jmx.registerMBean(ThumbnailsTaskConsumer.class.getSimpleName() + " " + threadNumber, MBEAN_INDEX_SERVICE_TYPE, consumer);
		executor.execute(consumer);
	    }
	}
	JobQueue omrQueue = JobQueue.getInstance();
	jmx.registerMBean(JobQueue.class.getSimpleName(), MBEAN_OMR_SERVICE_TYPE, omrQueue);
	int ocrThreads = 1;
	for (User user : users) {
	    for (int i = 0; i < ocrThreads; i++) {
		int threadNumber = i + 1;
		Consumer consumer = new Consumer(user, sheetMusicContext, config.opticalmusicrecognition().audiveris(), omrQueue, threadNumber);
		jmx.registerMBean(Consumer.class.getSimpleName() + " " + threadNumber, MBEAN_OMR_SERVICE_TYPE, consumer);
		executor.execute(consumer);
	    }
	}
	JobList jobList = JobList.getInstance();
	jmx.registerMBean(JobList.class.getSimpleName(), MBEAN_OMR_SERVICE_TYPE, jobList);
    }

    private void startScheduledTasks(List<User> users, ServletContext context) {
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("Starting scheduled tasks");
	}
	ThreadService threadService = new SheetMusicContext(context).getThreadService();
	SheetMusicContext sheetMusicContext = new SheetMusicContext(context);
	for (User user : users) {
	    threadService.scheduleAtFixedRate(new IndexProducer(user, sheetMusicContext), 0, 24, TimeUnit.HOURS);
	}
    }

}
