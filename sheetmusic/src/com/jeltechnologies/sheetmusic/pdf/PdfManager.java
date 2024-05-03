package com.jeltechnologies.sheetmusic.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.library.BookPage;
import com.jeltechnologies.utils.ImageUtils;
import com.jeltechnologies.utils.StringUtils;

public class PdfManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfManager.class);
    private final File pdfFile;
    private final List<File> tempFiles = new ArrayList<File>();
    private final PDDocument pdf;
    private static final int DOTS_PER_INCH = PdfExtractor.DOTS_PER_INCH;
    private static final String CUSTOM_FIELD_PREFIX = "sheetmusic-";

    public PdfManager(File file) throws IOException {
	this.pdfFile = file;
	pdf = openPdf(this.pdfFile);
    }

    protected static PDDocument openPdf(File file) throws IOException {
	PDDocument pdDocument;
	pdDocument = Loader.loadPDF(file);
	pdDocument.setAllSecurityToBeRemoved(true);
	return pdDocument;
    }

    public BufferedImage createSingleImage(List<Integer> pages, int maxWidth, int maxHeight, int getNrOfPagesInSingleImage)
	    throws IOException, OutOfMemoryError {
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("createSingleImage [" + pdfFile.getAbsolutePath() + "] , maxWidth=" + maxWidth + ", maxHeight=" + maxHeight + " pages: " + pages
		    + ", norOfPagesInSingleImage: " + getNrOfPagesInSingleImage);
	}
	PDFRenderer renderer = new PDFRenderer(pdf);
	List<BufferedImage> images = new ArrayList<BufferedImage>(pages.size());

	BufferedImage lastImage = null;
	for (int page : pages) {
	    BufferedImage image = null;
	    if (page <= pdf.getNumberOfPages()) {
		image = renderer.renderImageWithDPI(page - 1, DOTS_PER_INCH, ImageType.RGB);
		images.add(image);
		lastImage = image;
	    }
	}

	int createdImages = images.size();

	for (int emptyPageCounter = createdImages; emptyPageCounter < getNrOfPagesInSingleImage; emptyPageCounter++) {
	    LOGGER.trace("Adding empty image");
	    if (lastImage != null) {
		int height = lastImage.getHeight();
		int width = lastImage.getWidth();
		BufferedImage emptyPage = ImageUtils.getWhiteImage(width, height);
		images.add(emptyPage);
	    }
	}

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Number of images to concat " + images.size());
	}

	BufferedImage together = ImageUtils.concatImagesHorizontally(images);
	together = ImageUtils.createMaximizedThumb(together, maxWidth, maxHeight);
	return together;
    }

    public BufferedImage extractPage(int page) throws IOException, OutOfMemoryError {
	PDFRenderer renderer = new PDFRenderer(pdf);
	BufferedImage image = renderer.renderImageWithDPI(page - 1, DOTS_PER_INCH, ImageType.RGB);
	return image;
    }

    public void addBookmarks(List<BookPage> pages) {
	boolean hasContents = false;
	Iterator<BookPage> iterator = pages.iterator();
	while (!hasContents && iterator.hasNext()) {
	    BookPage p = iterator.next();
	    hasContents = !p.isBlank();
	}
	if (hasContents) {
	    PDDocumentOutline documentOutline;
	    documentOutline = new PDDocumentOutline();
	    pdf.getDocumentCatalog().setDocumentOutline(documentOutline);
	    for (BookPage bookPage : pages) {
		if (!bookPage.isBlank()) {
		    PDPageDestination pageDestination = new PDPageFitWidthDestination();
		    pageDestination.setPage(pdf.getPage(bookPage.getNr() - 1));
		    PDOutlineItem bookmark = new PDOutlineItem();
		    bookmark.setDestination(pageDestination);
		    bookmark.setTitle(bookPage.getLabel());
		    documentOutline.addLast(bookmark);
		}
	    }
	    pdf.getDocumentCatalog().setPageMode(PageMode.USE_OUTLINES);
	}
    }

    public void close() {
	if (pdf != null) {
	    try {
		pdf.close();
	    } catch (IOException e) {
		LOGGER.warn("Cannot close PDF document " + pdfFile.getName(), e);
	    }
	}
	for (File tempFile : tempFiles) {
	    tempFile.delete();
	}
    }

    public int getNrOfPages() {
	return pdf.getNumberOfPages();
    }

    public String getTitle() {
	String title = null;
	PDDocumentInformation info = pdf.getDocumentInformation();
	if (info != null) {
	    title = info.getTitle();
	}
	if (title == null) {
	    title = pdfFile.getName();
	}
	return title;
    }

    private void addPageTitles(List<PdfPage> pages, PDOutlineNode bookmark) throws IOException {
	PDOutlineItem current = bookmark.getFirstChild();
	while (current != null) {
	    int pageNumber = -1;

	    if (current.getDestination() instanceof PDPageDestination) {
		PDPageDestination pd = (PDPageDestination) current.getDestination();
		pageNumber = pd.retrievePageNumber() + 1;
		// System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
	    } else if (current.getDestination() instanceof PDNamedDestination) {
		PDPageDestination pd = pdf.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) current.getDestination());
		if (pd != null) {
		    pageNumber = pd.retrievePageNumber() + 1;
		}
	    } else if (current.getDestination() != null) {
		// System.out.println(indentation + "Destination class: " + current.getDestination().getClass().getSimpleName());
	    }

	    if (current.getAction() instanceof PDActionGoTo) {
		PDActionGoTo gta = (PDActionGoTo) current.getAction();
		if (gta.getDestination() instanceof PDPageDestination) {
		    PDPageDestination pd = (PDPageDestination) gta.getDestination();
		    pageNumber = pd.retrievePageNumber() + 1;
		    // System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
		} else if (gta.getDestination() instanceof PDNamedDestination) {
		    PDPageDestination pd = pdf.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) gta.getDestination());
		    if (pd != null) {
			pageNumber = pd.retrievePageNumber() + 1;
			// System.out.println(indentation + "Destination page: " + (pd.retrievePageNumber() + 1));
		    }
		} else {
		    // System.out.println(indentation + "Destination class: " + gta.getDestination().getClass().getSimpleName());
		}
	    } else if (current.getAction() != null) {
		// System.out.println(indentation + "Action class: " + current.getAction().getClass().getSimpleName());
	    }

	    if (pageNumber > -1) {
		PdfPage pageWithTheSameNumber = null;
		Iterator<PdfPage> pagesIterator = pages.iterator();
		while (pageWithTheSameNumber == null && pagesIterator.hasNext()) {
		    PdfPage currentPage = pagesIterator.next();
		    if (currentPage.getPageNumber() == pageNumber) {
			pageWithTheSameNumber = currentPage;
		    }
		}
		if (pageWithTheSameNumber == null) {
		    PdfPage page = new PdfPage();
		    page.setPageNumber(pageNumber);
		    String title = StringUtils.stripControlChars(current.getTitle());
		    page.setTitle(title);
		    pages.add(page);
		}
	    }
	    addPageTitles(pages, current);
	    current = current.getNextSibling();
	}
    }

    public List<PdfPage> getPagesInfo() {
	List<PdfPage> pages = new ArrayList<>();
	PDDocumentOutline outline = pdf.getDocumentCatalog().getDocumentOutline();
	if (outline != null) {
	    try {
		addPageTitles(pages, outline);
	    } catch (IOException e) {
		LOGGER.error("Could not get PDF pages info: " + e.getMessage());
	    }
	}
	return pages;
    }

    public void setCustomData(String shortName, String value) {
	String name = CUSTOM_FIELD_PREFIX + shortName;
	PDDocumentInformation docInfo = pdf.getDocumentInformation();
	if (value == null || value.isBlank()) {
	    docInfo.setCustomMetadataValue(name, null);
	} else {
	    docInfo.setCustomMetadataValue(name, value);
	}
    }

    public String getCustomData(String shortName) {
	String name = CUSTOM_FIELD_PREFIX + shortName;
	PDDocumentInformation docInfo = pdf.getDocumentInformation();
	return docInfo.getCustomMetadataValue(name);
    }

    public void setGenericProperties(String creator, String subject, String artist, String title) {
	PDDocumentInformation info = pdf.getDocumentInformation();
	if (info != null) {
	    info.setTitle(title);
	    if (artist != null && !artist.isBlank()) {
		info.setAuthor(artist);
	    }
	    info.setCreator(creator);
	    info.setSubject(subject);
	}
    }

    public void save(File toFile) throws IOException {
	pdf.save(toFile);
    }

}
