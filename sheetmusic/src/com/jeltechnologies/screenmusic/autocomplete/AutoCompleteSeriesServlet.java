package com.jeltechnologies.screenmusic.autocomplete;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeltechnologies.screenmusic.jsonpayloads.AutoCompleteResponse;
import com.jeltechnologies.screenmusic.servlet.BaseServlet;
import com.jeltechnologies.screenmusic.servlet.CachedBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/autocomplete-series")
public class AutoCompleteSeriesServlet extends BaseServlet {
    private static final long serialVersionUID = -6423746607299814923L;
    
    public static final String CACHE_BEAN = "autocomplete-cache-series";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String query = request.getParameter("query");
	respondJson(response, getAutoCompleteSuggestions(request.getSession(), query), false);
    }
    
    private AutoCompleteResponse getAutoCompleteSuggestions(HttpSession session, String query) throws JsonProcessingException {
	AutoCompleteSeriesBean bean = (AutoCompleteSeriesBean) CachedBean.get(session, CACHE_BEAN, AutoCompleteSeriesBean.class);
	return bean.getAutoCompleteSuggestions(query);
    }
}
