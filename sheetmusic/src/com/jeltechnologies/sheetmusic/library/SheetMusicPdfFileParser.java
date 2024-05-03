package com.jeltechnologies.sheetmusic.library;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.extractedfilestorage.Thumbnail;
import com.jeltechnologies.sheetmusic.pdf.PdfManager;
import com.jeltechnologies.sheetmusic.pdf.PdfPage;
import com.jeltechnologies.utils.FileUtils;
import com.jeltechnologies.utils.ImageUtils;
import com.jeltechnologies.utils.StringUtils;

public class SheetMusicPdfFileParser implements SheetMusicFileParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(SheetMusicPdfFileParser.class);
    
    private static final int MAX_ALSO_KNOWN_AS_CHECKSUMS = 200;

    private static final String CUSTOM_FIELD_SAVED_TIMESTAMP = "saved";
    
    private static final char CATEGORY_SPLITER = '$';

    private PdfManager reader;

    private final File pdfFile;

    private int imagesCreated;

    public SheetMusicPdfFileParser(File pdfFile) throws IOException {
	this.pdfFile = pdfFile;
	if (!pdfFile.isFile()) {
	    throw new FileNotFoundException("Cannot find PDF file" + pdfFile.getAbsolutePath());
	}
    }

    private PdfManager getPdfManager() throws IOException {
	if (reader == null) {
	    reader = new PdfManager(pdfFile);
	}
	return reader;
    }

    private String getCustomData(String name) throws IOException {
	String data = getPdfManager().getCustomData(name);
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("getCustomData('" + name + "') => " + data);
	}
	return data;
    }

    private void setCustomData(String name, String value) throws IOException {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("setCustomData name:" + name + "=" + value);
	}
	getPdfManager().setCustomData(name, value);
    }

    public Book getBook() throws IOException {
	Book book = new Book();
	PdfManager pdfManager = getPdfManager();
	boolean savedBySheetServer = isSavedByMe();

	if (savedBySheetServer) {
	    book.setTitle(getCustomData("title"));
	    book.setArtist(getCustomData("artist"));
	    book.setSeries(getCustomData("series"));
	    String categories = getCustomData("categories");
	    if (categories != null && !categories.isBlank()) {
		List<String> parts = StringUtils.split(categories, CATEGORY_SPLITER);
		for (String part : parts) {
		    Category cat = new Category();
		    cat.setName(part);
		    book.getCategories().add(cat);
		}
	    }
	} else {
	    book.setTitle(StringUtils.stripAfterLast(pdfFile.getName(), "."));
	}
	int nrOfPages = pdfManager.getNrOfPages();
	book.setNrOfPages(nrOfPages);
	book.setFileLastModified(new Date(pdfFile.lastModified()));
	book.setFileSize(pdfFile.length());
	List<BookPage> bookPages = new ArrayList<BookPage>();
	book.setPages(bookPages);

	if (savedBySheetServer) {
	    for (int pageNr = 1; pageNr <= nrOfPages; pageNr++) {
		String prefix = "p" + pageNr + "-";
		String pageTitle = getCustomData(prefix + "title");
		String pageTitle2 = getCustomData(prefix + "title2");
		String pageArtist = getCustomData(prefix + "artist");
		String pageArtist2 = getCustomData(prefix + "artist2");
		String pageDescription = getCustomData(prefix + "description");
		String pageText = getCustomData(prefix + "text");

		BookPage bookPage = new BookPage();
		bookPage.setNr(pageNr);
		boolean add = false;
		if (isValue(pageTitle)) {
		    bookPage.setTitle(pageTitle);
		    add = true;
		}
		if (isValue(pageTitle2)) {
		    bookPage.setTitle2(pageTitle2);
		    add = true;
		}
		if (isValue(pageArtist)) {
		    bookPage.setArtist(pageArtist);
		    add = true;
		}
		if (isValue(pageArtist2)) {
		    bookPage.setArtist2(pageArtist2);
		    add = true;
		}
		if (isValue(pageDescription)) {
		    bookPage.setDescription(pageDescription);
		    add = true;
		}
		if (isValue(pageText)) {
		    bookPage.setText(pageText);
		    add = true;
		}
		if (add) {
		    bookPages.add(bookPage);
		}
	    }
	} else {
	    // Use the custom properties saved from sheetmusic
	    List<PdfPage> pages = pdfManager.getPagesInfo();
	    if (pages != null && !pages.isEmpty()) {
		for (PdfPage page : pages) {
		    BookPage bookPage = new BookPage();
		    bookPages.add(bookPage);
		    bookPage.setNr(page.getPageNumber());
		    bookPage.setTitle(page.getTitle());
		}
	    }
	}

	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("Parsed book savedBySheetServer: " + savedBySheetServer + ", book: " + book);
	}
	return book;
    }

    public void close() {
	try {
	    getPdfManager().close();
	} catch (IOException e) {
	    LOGGER.warn("Cannot close PDF manager for " + this.pdfFile, e);
	}
    }

    private boolean isValue(String value) {
	boolean result = value != null && !value.isBlank();
	return result;
    }

    @Override
    public void createThumbs(List<Thumbnail> thumbs) throws IOException, InterruptedException {
	imagesCreated = 0;
	PdfManager manager = getPdfManager();
	Map<Integer, List<Thumbnail>> pageMap = new HashMap<Integer, List<Thumbnail>>();
	for (Thumbnail thumbnail : thumbs) {
	    Integer page = thumbnail.getPage();
	    List<Thumbnail> thumbsOnPage = pageMap.get(page);
	    if (thumbsOnPage == null) {
		thumbsOnPage = new ArrayList<Thumbnail>();
		pageMap.put(page, thumbsOnPage);
	    }
	    thumbsOnPage.add(thumbnail);
	}
	for (Integer page : pageMap.keySet()) {
	    List<Thumbnail> thumbsOnPage = pageMap.get(page);
	    BufferedImage originalImage = manager.extractPage(page);
	    for (Thumbnail thumbnail : thumbsOnPage) {
		if (Thread.interrupted()) {
		    throw new InterruptedException();
		}
		int minWidth = thumbnail.getSize().getWidth();
		int minHeight = thumbnail.getSize().getHeight();
		BufferedImage thumbImage = ImageUtils.createMaximizedThumb(originalImage, minWidth, minHeight);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Saving " + thumbnail.getCachedFile().getAbsolutePath());
		}
		FileUtils.saveImage(thumbnail.getCachedFile(), thumbImage, Thumbnail.IMAGE_EXTENSION, Thumbnail.COMPRESSION_QUALITY);
		imagesCreated++;
	    }
	}
    }

    @Override
    public int getImagesCreated() {
	return imagesCreated;
    }

    public void save(File toFile, Book book) throws IOException {
	setCustomData("title", book.getTitle());
	setCustomData("artist", book.getArtist());
	setCustomData("series", book.getSeries());
	List<Category> categories = book.getCategories();
	if (categories != null && !categories.isEmpty()) {
	    StringBuilder b = new StringBuilder();
	    for (int i = 0; i < categories.size(); i++) {
		if (i > 0) {
		    b.append(CATEGORY_SPLITER);
		}
		b.append(categories.get(i).getName());
	    }
	    setCustomData("categories", b.toString());
	}
	List<BookPage> pages = book.getPages();
	for (BookPage page : pages) {
	    String prefix = "p" + page.getNr() + "-";
	    setCustomData(prefix + "title", page.getTitle());
	    setCustomData(prefix + "title2", page.getTitle2());
	    setCustomData(prefix + "artist", page.getArtist());
	    setCustomData(prefix + "artist2", page.getArtist2());
	    setCustomData(prefix + "description", page.getDescription());
	    setCustomData(prefix + "text", page.getText());
	}

	PdfManager pdf = getPdfManager();
	pdf.addBookmarks(book.getPages());
	String creator = "Sheetmusic";
	String subject = "Sheetmusic";
	String artist = book.getArtist();
	String title = book.getTitle();
	pdf.setGenericProperties(creator, subject, artist, title);

	String now = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ISO_DATE_TIME);
	setCustomData(CUSTOM_FIELD_SAVED_TIMESTAMP, now);

	addAlsoKnownAsChecksum(book.getFileChecksum());

	pdf.save(toFile);
    }

    public void addAlsoKnownAsChecksum(String checksum) throws IOException {
	List<String> checksums = getAlsoKnownAsChecksums();
	if (checksums.size() < MAX_ALSO_KNOWN_AS_CHECKSUMS) {
	    int slot = checksums.size() + 1;
	    String name = "aka" + slot;
	    String value = checksum;
	    setCustomData(name, value);
	} else {
	    LOGGER.warn("No more space to store updated AKA checksum for " + pdfFile);
	}
    }

    public List<String> getAlsoKnownAsChecksums() throws IOException {
	List<String> checksums = new ArrayList<>();
	boolean continueSearching = true;
	for (int i = 1; continueSearching && i < Integer.MAX_VALUE; i++) {
	    String akaName = "aka" + i;
	    String value = getCustomData(akaName);
	    if (value != null && !value.isBlank()) {
		checksums.add(value);
	    } else {
		continueSearching = false;
	    }
	}
	return checksums;
    }

    public boolean isSavedByMe() throws IOException {
	String savedDate = getCustomData(CUSTOM_FIELD_SAVED_TIMESTAMP);
	return savedDate != null && !savedDate.isBlank();
    }

}
