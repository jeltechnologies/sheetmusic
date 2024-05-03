package com.jeltechnologies.sheetmusic.autocomplete;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeltechnologies.sheetmusic.jsonpayloads.AutoCompleteResponse;
import com.jeltechnologies.sheetmusic.servlet.BaseServlet;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/autocomplete-search")
public class AutoCompleteSearchServlet extends BaseServlet {
    private static final long serialVersionUID = -5198486290304998499L;

    public static final String CACHE_BEAN = "autocomplete-cache-search";

    private static final String SESSION_BEAN = AutoCompleteSearchBean.class.getName();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String query = request.getParameter("query");
	respondJson(response, getAutoCompleteSuggestions(request, query));
    }

    private AutoCompleteResponse getAutoCompleteSuggestions(HttpServletRequest request, String query) throws JsonProcessingException {
	HttpSession session = request.getSession();
	AutoCompleteBean bean = (AutoCompleteBean) session.getAttribute(SESSION_BEAN);
	if (bean == null) {
	    bean = new AutoCompleteSearchBean(getUser(request), new SheetMusicContext(request));
	    session.setAttribute(SESSION_BEAN, bean);
	}
	return bean.getAutoCompleteSuggestions(query);
    }
}
