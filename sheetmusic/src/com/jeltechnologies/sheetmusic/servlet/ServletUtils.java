package com.jeltechnologies.sheetmusic.servlet;

import java.security.Principal;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {
    public static String getUserName(ServletRequest request) {
	String name;
	Principal principal = ((HttpServletRequest) request).getUserPrincipal();
	if (principal == null) {
	    name = "Anonymous";
	} else {
	    name = principal.getName();
	}
	return name;
    }
}
