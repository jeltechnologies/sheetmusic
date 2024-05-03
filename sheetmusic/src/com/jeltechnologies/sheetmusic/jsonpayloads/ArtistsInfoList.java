package com.jeltechnologies.sheetmusic.jsonpayloads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArtistsInfoList {
    private List<ArtistBooksAndSongs> artists = new ArrayList<ArtistBooksAndSongs>();

    public List<ArtistBooksAndSongs> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistBooksAndSongs> artists) {
        this.artists = artists;
    }
    
    public void sortOnName() {
	Collections.sort(artists, new Comparator<ArtistBooksAndSongs>() {
	    @Override
	    public int compare(ArtistBooksAndSongs o1, ArtistBooksAndSongs o2) {
		return o1.getArtistSortField().compareTo(o2.getArtistSortField());
	    }
	});
    }
    
    public void sortOnBooksA_Z() {
	Collections.sort(artists, new Comparator<ArtistBooksAndSongs>() {
	    @Override
	    public int compare(ArtistBooksAndSongs o1, ArtistBooksAndSongs o2) {
		return o2.getBooks().size() - o1.getBooks().size();
	    }
	});
    }
    
    public void sortOnBooksZ_A() {
	Collections.sort(artists, new Comparator<ArtistBooksAndSongs>() {
	    @Override
	    public int compare(ArtistBooksAndSongs o1, ArtistBooksAndSongs o2) {
		return o1.getBooks().size() - o2.getBooks().size();
	    }
	});
    }

    public void sortOnSongs() {
	Collections.sort(artists, new Comparator<ArtistBooksAndSongs>() {
	    @Override
	    public int compare(ArtistBooksAndSongs o1, ArtistBooksAndSongs o2) {
		int diff = o2.getSongs().size() - o1.getSongs().size();
		return diff;
	    }
	});
    }

}
