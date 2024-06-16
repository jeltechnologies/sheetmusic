package com.jeltechnologies.screenmusic.autocomplete;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeltechnologies.screenmusic.jsonpayloads.AutoCompleteResponse;
import com.jeltechnologies.screenmusic.servlet.BaseServlet;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/autocomplete-series")
public class AutoCompleteSeriesServlet extends BaseServlet {
    private static final long serialVersionUID = -6423746607299814923L;
    
    public static final String SESSION_BEAN = "autocomplete-cache-series";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String query = request.getParameter("query");
	AutoCompleteResponse autoCompleteResponse = getAutoCompleteSuggestions(request, query);
	respondJson(response, autoCompleteResponse);
    }
    
    private AutoCompleteResponse getAutoCompleteSuggestions(HttpServletRequest request, String query) throws JsonProcessingException {
	HttpSession session = request.getSession();
	AutoCompleteBean bean = (AutoCompleteBean) session.getAttribute(AutoCompleteSeriesBean.class.getName());
	if (bean == null) {
	    bean = new AutoCompleteSeriesBean(getUser(request), new ScreenMusicContext(request));
	    session.setAttribute(AutoCompleteSeriesBean.class.getName(), bean);
	}
	return bean.getAutoCompleteSuggestions(query);
    }
}
