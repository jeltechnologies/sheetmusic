package com.jeltechnologies.sheetmusic.jsonpayloads;

import java.io.Serializable;

public class UserPreferences implements Serializable {
    private static final long serialVersionUID = -3507805079993555398L;
    private int slidesPerView = 2;
    private String categorySelected = "";
    private String seriesSelected = "";
    private SortType songBooksSorting = null;
    private int slideTransitionSpeed = 250; // 	Duration of transition between slides (in ms) https://swiperjs.com/swiper-api#param-speed
    private final MusicXMLPreferences ocr;
    
    public UserPreferences() {
	ocr = new MusicXMLPreferences();
    }

    public int getSlideTransitionSpeed() {
        return slideTransitionSpeed;
    }

    public void setSlideTransitionSpeed(int slideTransitionSpeed) {
        this.slideTransitionSpeed = slideTransitionSpeed;
    }

    public String getSeriesSelected() {
	return seriesSelected;
    }

    public void setSeriesSelected(String seriesSelected) {
	this.seriesSelected = seriesSelected;
    }

    public int getSlidesPerView() {
	return slidesPerView;
    }

    public void setSlidesPerView(int slidesPerView) {
	this.slidesPerView = slidesPerView;
    }

    public String getCategorySelected() {
	return categorySelected;
    }

    public void setCategorySelected(String selectedCategory) {
	this.categorySelected = selectedCategory;
    }

    public SortType getSongBooksSorting() {
	return songBooksSorting;
    }
    
    public void setSongBooksSorting(SortType sorting) {
	songBooksSorting = sorting;
    }

    public MusicXMLPreferences getOcr() {
        return ocr;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("UserPreferences [slidesPerView=");
	builder.append(slidesPerView);
	builder.append(", categorySelected=");
	builder.append(categorySelected);
	builder.append(", seriesSelected=");
	builder.append(seriesSelected);
	builder.append(", artistSorting=");
	builder.append(songBooksSorting);
	builder.append("]");
	return builder.toString();
    }
}
