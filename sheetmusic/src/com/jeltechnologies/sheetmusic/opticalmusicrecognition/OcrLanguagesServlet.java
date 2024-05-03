package com.jeltechnologies.sheetmusic.opticalmusicrecognition;

import java.io.IOException;

import com.jeltechnologies.sheetmusic.config.AudiverisConfiguration;
import com.jeltechnologies.sheetmusic.config.Configuration;
import com.jeltechnologies.sheetmusic.servlet.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ocr-languages")
public class OcrLanguagesServlet extends BaseServlet {
    private static final long serialVersionUID = 2953803664477046255L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	AudiverisConfiguration config = Configuration.getInstance().opticalmusicrecognition().audiveris();
	respondJson(response, config.languages());
    }
}
