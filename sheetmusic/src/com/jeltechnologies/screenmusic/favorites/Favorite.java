package com.jeltechnologies.screenmusic.favorites;

import java.io.Serializable;

public class Favorite implements Serializable {
    private static final long serialVersionUID = 9045434772140347897L;
    private boolean favorite;
    private int position;

    public int getPosition() {
	return position;
    }
    
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
	return "FavoriteBook [favorite=" + favorite + ", position=" + position + "]";
    }

}
