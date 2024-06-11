package com.jeltechnologies.screenmusic.servlet;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.jsonpayloads.ArtistBooksAndSongs;
import com.jeltechnologies.screenmusic.jsonpayloads.ArtistsInfoList;
import com.jeltechnologies.screenmusic.library.Library;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/library/artists/*")
public class ArtistsServlet extends BaseServlet {
    private static final long serialVersionUID = -5896315096256450873L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistsServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	Object responseJSON;
	try {
	    String artist = request.getParameter("artist");
	    if (artist != null) {
		responseJSON = getArtist(request, artist);
	    } else {
		responseJSON = getArtistHits(request);
	    } 
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("responsObject:  " + responseJSON);
	    }
	    respondJson(response, responseJSON);

	} catch (Exception e) {
	    throw new ServletException("Cannot get artists", e);
	}
    }

    private ArtistBooksAndSongs getArtist(HttpServletRequest request, String artist) throws Exception {
	ArtistBooksAndSongs artistBooksAndSongs = new Library(getUser(request), new ScreenMusicContext(request)).getArtistBooksAndSongs(artist);
	Collections.sort(artistBooksAndSongs.getSongs());
	return artistBooksAndSongs;
    }

    private ArtistsInfoList getArtistHits(HttpServletRequest request) throws Exception {
	ArtistsInfoList artists = new Library(getUser(request), new ScreenMusicContext(request)).getArtistsBooksAndSongs();
	String sort = request.getParameter("sort");

	if (sort == null || sort.equalsIgnoreCase("artist")) {
	    artists.sortOnName();
	} else {
	    if (sort.equalsIgnoreCase("books")) {
		artists.sortOnBooksA_Z();
	    } else {
		if (sort.equalsIgnoreCase("songs")) {
		    artists.sortOnSongs();
		} else {
		    LOGGER.info("Unknown sort option: " + sort);
		}
	    }
	}
	return artists;
    }

}
