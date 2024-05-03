package com.jeltechnologies.sheetmusic.favorites.books;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.library.Book;

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

@Path("/favorites/book")
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
    public Response getFavoriteBooks() {
	LOGGER.trace("getFavoriteBooks");
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    List<FavoriteBook> favorites = db.getFavoriteBooks(getUserName());
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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFavoriteBook(@PathParam("id") String id) {
	LOGGER.trace("getFavoriteBooks " + id);
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    FavoriteBook bf = db.getFavoriteBook(getUserName(), id);
	    LOGGER.trace(bf.toString());
	    Response response = Response.ok(bf, MediaType.APPLICATION_JSON).build();
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
    @Path("{id}")
    public Response post(FavoriteBook favoriteBook) {
	LOGGER.trace("post " + favoriteBook);
	Response response;
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    db.postFavorite(getUserName(), favoriteBook);
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
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String id) {
	LOGGER.trace("delete " + id);
	Response response;
	DataAccessObject db = null;
	try {
	    db = new DataAccessObject();
	    FavoriteBook fb = new FavoriteBook();
	    fb.setBook(new Book());
	    db.deleteFavorite(getUserName(), id);
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
