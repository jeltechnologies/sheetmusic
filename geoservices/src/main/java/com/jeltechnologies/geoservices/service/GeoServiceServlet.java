package com.jeltechnologies.geoservices.service;

import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.geoservices.datamodel.AddressRequest;
import com.jeltechnologies.geoservices.datamodel.Coordinates;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/address")
public class GeoServiceServlet extends BaseServlet {
    private static final long serialVersionUID = -8314074265849869252L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoServiceServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String latString = null;
	String lonString = null;
	String latlonString = request.getParameter("latlon");
	if (latlonString != null && !latlonString.isBlank()) {
	    String[] parts = latlonString.split(",");
	    if (parts.length == 2) {
		latString = parts[0].trim();
		lonString = parts[1].trim();
	    }
	}
	if (latString == null) {
	    latString = request.getParameter("latitude");
	}
	if (lonString == null) {
	    lonString = request.getParameter("longitude");
	}
	BigDecimal latitude = getBigDecimal(latString);
	if (latitude == null) {
	    response.sendError(400, "Missing or wrongly formated latitude");
	} else {
	    BigDecimal longitude = getBigDecimal(lonString);
	    if (longitude == null) {
		response.sendError(400, "Missing or wrongly formated attribute longitude");
	    } else {
		Coordinates coordinate = new Coordinates(latitude.floatValue(), longitude.floatValue());
		respondLocation(request, response, coordinate);
	    }
	}
    }

    private BigDecimal getBigDecimal(String s) {
	BigDecimal result = null;
	if (s != null && !s.isBlank()) {
	    try {
		result = new BigDecimal(s);
	    } catch (Exception e) {
		LOGGER.warn("Cannot covert " + s + " because " + e.getMessage());
	    }
	}
	return result;
    }

    private void respondLocation(HttpServletRequest request, HttpServletResponse response, Coordinates coordinates) throws IOException {
	DataSourceEngine engine = DataSourceEngine.getDataSourceEngine(request.getServletContext());
	if (!engine.isReadyForService()) {
	    response.sendError(503, "Loading from database. Please try again in a few minutes.");
	} else {
	    AddressRequest address = engine.getAddress(coordinates);
	    if (address == null) {
		response.sendError(404, "No address for these coordinates");
	    } else {
		respondJson(response, address);
	    }
	}

    }

}
