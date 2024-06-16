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

@WebServlet("/autocomplete-search")
public class AutoCompleteSearchServlet extends BaseServlet {
    private static final long serialVersionUID = -5198486290304998499L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String query = request.getParameter("query");
	respondJson(response, getAutoCompleteSuggestions(request, query));
    }

    private AutoCompleteResponse getAutoCompleteSuggestions(HttpServletRequest request, String query) throws JsonProcessingException {
	HttpSession session = request.getSession();
	AutoCompleteBean bean = (AutoCompleteBean) session.getAttribute(AutoCompleteSearchBean.class.getName());
	if (bean == null) {
	    bean = new AutoCompleteSearchBean(getUser(request), new ScreenMusicContext(request));
	    session.setAttribute(AutoCompleteSearchBean.class.getName(), bean);
	}
	return bean.getAutoCompleteSuggestions(query);
    }
}
