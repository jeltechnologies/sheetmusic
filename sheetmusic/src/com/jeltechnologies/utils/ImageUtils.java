package com.jeltechnologies.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaxt.io.Image;

public class ImageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);
    
    public static BufferedImage createMaximizedThumb(File sourceFile, int minWidth, int minHeight) throws IOException {
	return createMaximizedThumb(ImageIO.read(sourceFile), minWidth, minHeight);
    }

    public static BufferedImage createThumb(BufferedImage bufferedImage, int picWidth, int picHeight) throws IOException {
        return createThumb(new Image(bufferedImage), picWidth, picHeight);
    }
    
    public static BufferedImage createThumb(File imageFile, int picWidth, int picHeight) throws IOException {
        return createThumb(new Image(imageFile), picWidth, picHeight);
    }
    
    public static BufferedImage createThumb(Image image, int picWidth, int picHeight) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Max width is: " + picWidth + ", height is: " + picHeight + " # Original width is: " + imageWidth + ", height: " + imageHeight);
	}

	double thumbRatio = (double) picWidth / (double) picHeight;
        double imageRatio = (double) imageWidth / (double) imageHeight;
 
        if (thumbRatio < imageRatio) {
            picHeight = (int) (picWidth / imageRatio);
        } else {
            picWidth = (int) (picHeight * imageRatio);
        }
 
        if ((imageWidth < picWidth) && (imageHeight < picHeight)) {
            picWidth = imageWidth;
            picHeight = imageHeight;
        } else if (imageWidth < picWidth) {
            picWidth = imageWidth;
        } else if (imageHeight < picHeight) {
            picHeight = imageHeight;
        }
        
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace(".. Resized width is: " + picWidth + ", height is: " + picHeight);
	}
	
	BufferedImage thumb = image.getBufferedImage(picWidth, picHeight, false);
   	return thumb;
    }
    
    
    public static byte[] createThumb(byte[] payload, int picWidth, int picHeight) throws IOException {
	Image image = new Image(payload);
	BufferedImage thumb = createThumb(image.getBufferedImage(), picWidth, picHeight);
	Image thumbImage = new Image(thumb);
	return thumbImage.getByteArray();
    }
    
    public static BufferedImage createMaximizedThumb(BufferedImage source, int minWidth, int minHeight) throws IOException {
	int originalWidth = source.getWidth();
	int originalHeight = source.getHeight();

	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Min width is: " + minWidth + ", height is: " + minHeight + " # Original width is: " + originalWidth + ", height: " + originalHeight);
	}

	int thumbWidth;
	int thumbHeight;
	float percentage;
	if (originalWidth > originalHeight) {
	    thumbWidth = minWidth;
	    percentage = ((float) minWidth / originalWidth);
	    thumbHeight = (int) (originalHeight * percentage);
	} else {
	    thumbHeight = minHeight;
	    percentage = ((float) minHeight / originalHeight);
	    thumbWidth = (int) (originalWidth * percentage);
	}
	if (thumbHeight > minHeight) {
	    thumbHeight = minHeight;
	    percentage = ((float) minHeight / originalHeight);
	    thumbWidth = (int) (originalWidth * percentage);
	}
	
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("Resized thumb width is: " + thumbWidth + ", height: " + thumbHeight + ", percentage: " + percentage);
	}
	
	BufferedImage thumb = new Image(source).getBufferedImage(thumbWidth, thumbHeight, false);
	return thumb;
    }
    

    public static BufferedImage concatImagesHorizontally(List<BufferedImage> images) throws IOException {
	BufferedImage resultImage = null;
	if (!images.isEmpty()) {
	    int resultWidth = 0;
	    int height = images.get(0).getHeight();
	    for (BufferedImage image : images) {
		resultWidth = resultWidth + image.getWidth();
	    }
	    resultImage = new BufferedImage(resultWidth, height, BufferedImage.TYPE_INT_RGB);
	    int x = 0;
	    for (int index = 0; index < images.size(); index++) {
		BufferedImage currentImage = images.get(index);
		resultImage.createGraphics().drawImage(currentImage, x, 0, null);
		x = x + currentImage.getWidth();
	    }
	}
	return resultImage;
    }
    
    public static BufferedImage getWhiteImage(int width, int height) throws IOException {
	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	Graphics2D graphics = img.createGraphics();
	graphics.setBackground(Color.WHITE);
	graphics.clearRect(0, 0, width, height);
	return img;
    }

    public static BufferedImage sharpen(BufferedImage image) {
	Kernel kernel = new Kernel(3, 3, new float[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 });
	BufferedImageOp op = new ConvolveOp(kernel);
	return op.filter(image, null);
    }

    public static BufferedImage blur(BufferedImage image) {
	Kernel kernel = new Kernel(3, 3, new float[] { 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f, 1f / 9f });
	BufferedImageOp op = new ConvolveOp(kernel);
	return op.filter(image, null);
    }
}