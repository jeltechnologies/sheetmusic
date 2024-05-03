package com.jeltechnologies.sheetmusic.favorites.artist;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.favorites.Favorite;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/favorites/artist")
public class Resource {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    @Context
    private SecurityContext sc;

    public Resource(@Context SecurityContext sc) {
	this.sc = sc;
	LOGGER.trace("Instantiated");
    }

    private String getUserName() {
	return sc.getUserPrincipal().getName();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavorites() {
	LOGGER.trace("getFavorites");
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    List<FavoriteArtist> favorites = db.getAll(getUserName());
	    Response response = Response.ok(favorites, MediaType.APPLICATION_JSON).build();
	    return response;
	} catch (SQLException e) {
	    LOGGER.error("Cannot get list of favorites", e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    @GET
    @Path("{artist}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavorite(@PathParam("artist") String artist) {
	LOGGER.trace("getFavorite " + artist);
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    Favorite fav = db.getFavorite(getUserName(), artist);
	    if (fav == null) {
		fav = new Favorite();
		fav.setFavorite(false);
	    } else {
		fav.setFavorite(true);
	    }
	    LOGGER.trace(fav.toString());
	    Response response = Response.ok(fav, MediaType.APPLICATION_JSON).build();
	    return response;
	} catch (SQLException e) {
	    LOGGER.error("Cannot get favorite", e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{artist}")
    public Response post(FavoriteArtist favorite) {
	LOGGER.trace("post " + favorite);
	Response response;
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    db.postFavorite(getUserName(), favorite);
	    response = Response.ok().build();
	    LOGGER.trace(response.toString());
	} catch (SQLException e) {
	    LOGGER.error("Cannot post", e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return response;
    }

    @DELETE
    @Path("{artist}")
    public Response delete(@PathParam("artist") String artist) {
	LOGGER.trace("delete " + artist);
	Response response;
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    db.deleteFavorite(getUserName(), artist);
	    response = Response.ok().build();
	} catch (SQLException e) {
	    LOGGER.error("Cannot delete" + artist, e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return response;
    }

}
