package com.jeltechnologies.sheetmusic.statistics;

import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;
import com.jeltechnologies.sheetmusic.tags.BaseTag;
import com.jeltechnologies.utils.StringUtils;

public class LibraryTag extends BaseTag {

    @Override
    public void processTag() throws Exception {
	SheetMusicContext context = new SheetMusicContext(getRequest());
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
