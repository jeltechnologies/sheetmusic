package com.jeltechnologies.screenmusic.favorites.pages;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.favorites.Favorite;

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

@Path("/favorites/page")
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
    public Response getFavoritePages() {
	LOGGER.trace("getFavoritePages");
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    List<FavoritePage> favorites = db.getAll(getUserName());
	    Response response = Response.ok(favorites, MediaType.APPLICATION_JSON).build();
	    return response;
	} catch (SQLException e) {
	    LOGGER.error("Cannot get list of favorite books", e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
    }

    @GET
    @Path("{id}/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavoritePage(@PathParam("id") String bookId, @PathParam("page") int page) {
	LOGGER.trace("getFavoritePage " + bookId + ", page: " + page);
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    Favorite fav = db.getFavorite(getUserName(), bookId, page);
	    if (fav == null) {
		fav = new Favorite();
		fav.setFavorite(false);
	    }
	    LOGGER.trace(fav.toString());
	    Response response = Response.ok(fav, MediaType.APPLICATION_JSON).build();
	    return response;
	} catch (SQLException e) {
	    LOGGER.error("Cannot get list of favorite books", e);
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
    @Path("{id}/{page}")
    public Response post(@PathParam("id") String bookId, @PathParam("page") int page, FavoritePage favorite) {
	LOGGER.trace("post " + favorite);
	Response response;
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    db.postFavorite(getUserName(), bookId, page, favorite);
	    response = Response.ok().build();
	    LOGGER.trace(response.toString());
	} catch (SQLException e) {
	    LOGGER.error("Cannot get list of favorite books", e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return response;
    }

    @DELETE
    @Path("{id}/{page}")
    public Response delete(@PathParam("id") String id, @PathParam("page") int page) {
	LOGGER.trace("delete " + id + ", page: " + page);
	Response response;
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    db.deleteFavorite(getUserName(), id, page);
	    response = Response.ok().build();
	} catch (SQLException e) {
	    LOGGER.error("Cannot delete favorite for book id " + id, e);
	    throw new WebApplicationException("Error interaction with database");
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	return response;
    }

}
