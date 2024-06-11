package com.jeltechnologies.screenmusic.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.db.DBUsers;
import com.jeltechnologies.utils.JsonUtils;
import com.jeltechnologies.utils.datatypes.NamedValueCollection;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = -6161294504027306128L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServlet.class);

    protected void respondJson(HttpServletResponse response, Object info) throws IOException {
	respondJson(response, info, false);
    }
    
    public static User getUser(ServletRequest request) {
	DBUsers db = null;
	String name = ServletUtils.getUserName(request);
	User user;
	try {
	    db = new DBUsers();
	    user= db.getUser(name);
	}
	catch (SQLException e) {
	    user = null;
	    LOGGER.warn("Cannot find user with name " + name);
	}
	finally {
	    if (db != null) {
		db.close();
	    }
	}
	return user;
    }
    
    protected void respondJson(HttpServletResponse response, Object info, boolean log) throws IOException {
	try {
	    String json = new JsonUtils().toJSON(info);
	    if (log) {
		LOGGER.info(json);
	    }
	    if (json != null) {
		response.setHeader("Content-Type", "application/json");
		respondText(response, json);
	    } else {
		throw new IOException("Cannot convert to JSON: " + info);
	    }
	} catch (JsonProcessingException e) {
	    throw new IOException("Cannot create JSON from command", e);
	}
    }

    @SuppressWarnings("unchecked")
    protected Object getJsonFromBody(HttpServletRequest request, @SuppressWarnings("rawtypes") Class clazz) throws Exception {
	Object result;
	ObjectMapper mapper = new ObjectMapper();
	mapper.setSerializationInclusion(Include.NON_NULL);
	String body = getBody(request);
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("JSON body: " + body);
	}
	result = mapper.readValue(body, clazz);
	if (LOGGER.isTraceEnabled()) {
	    LOGGER.trace("JSON object: " + result);
	}

	return result;
    }

    protected NamedValueCollection getRequestParameters(HttpServletRequest request) {
	NamedValueCollection requestParams = new NamedValueCollection(false);
	Enumeration<String> paramEnum = request.getParameterNames();
	while (paramEnum.hasMoreElements()) {
	    String name = paramEnum.nextElement();
	    String[] values = request.getParameterValues(name);
	    for (String value : values) {
		requestParams.add(name, value);
	    }
	}
	return requestParams;
    }
    
    protected static int respondBinaryFile(HttpServletResponse response, ServletContext context, File file, boolean attachment) throws IOException {
	int responseCode;
	if (file.exists() && file.isFile()) {
	    response.setHeader("Content-Source", context.getMimeType(file.getCanonicalPath()));
	    response.setHeader("Content-Length", String.valueOf(file.length()));
	    if (attachment) {
		response.setHeader("Content-disposition", "attachment; filename=\"" + file.getName() + "\"");
	    } else {
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
	    }
	    Files.copy(file.toPath(), response.getOutputStream());
	    responseCode = HttpServletResponse.SC_OK;
	    response.setStatus(responseCode);
	} else {
	    responseCode = HttpServletResponse.SC_NOT_FOUND;
	    response.sendError(responseCode);
	}
	return responseCode;
    }

    protected void respondBinary(HttpServletResponse response, ServletContext context, byte[] imagePayload) throws IOException {
	if (imagePayload != null && imagePayload.length > 0) {
	    ServletOutputStream out = null;
	    try {
		out = response.getOutputStream();
		out.write(imagePayload, 0, imagePayload.length);
		response.setStatus(HttpServletResponse.SC_OK);
	    } finally {
		if (out != null) {
		    out.close();
		}
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    }
	} else {
	    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
    }
    
    protected void respondText(HttpServletResponse httpServletResponse, String responseBody) throws IOException {
	if (responseBody == null) {
	    httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
	} else {
	    httpServletResponse.setCharacterEncoding("UTF-8");
	    httpServletResponse.getWriter().append(responseBody);
	    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
	}
    }

    protected String getBody(HttpServletRequest request) throws IOException {
	StringBuilder builder = new StringBuilder();
	String line = null;
	BufferedReader reader = null;
	try {
	    reader = request.getReader();
	    while ((line = reader.readLine()) != null) {
		builder.append(line);
	    }
	} finally {
	    if (reader != null) {
		reader.close();
	    }
	}
	return builder.toString();
    }


}
