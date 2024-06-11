package com.jeltechnologies.screenmusic.statistics;

import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;
import com.jeltechnologies.screenmusic.tags.BaseTag;
import com.jeltechnologies.utils.StringUtils;

public class LibraryTag extends BaseTag {

    @Override
    public void processTag() throws Exception {
	ScreenMusicContext context = new ScreenMusicContext(getRequest());
	Library library = new Library(getUser(), context);
	LibraryStatistics statistics = library.getLibraryStatistics();
	addLine("<table class=\"library-statistics\">");
	addStat("Books:", statistics.books());
	addStat("Pages:", statistics.pages());
	addLine("</table>");
    }
    
    private void addStat(String label, int number) throws Exception {
	String formattedNumber = StringUtils.formatNumber(number);
	add("<tr>");
	add("<td class=\"library-statistics-name\">" + label + "</td>");
	add("<td class=\"library-statistics-value\">" + formattedNumber + "</td>");
	add("</tr>");
    }

}
