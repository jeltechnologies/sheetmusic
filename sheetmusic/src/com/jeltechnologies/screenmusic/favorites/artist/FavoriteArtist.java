package com.jeltechnologies.screenmusic.favorites.artist;

import java.util.List;

import com.jeltechnologies.screenmusic.favorites.Favorite;

public class FavoriteArtist extends Favorite {
    private static final long serialVersionUID = 4147352886135982931L;
    
    private String artist;
    
    private List<String> bookIds;

    public List<String> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<String> bookIds) {
        this.bookIds = bookIds;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
	return "FavoriteArtist [artist=" + artist + "]";
    }
}
