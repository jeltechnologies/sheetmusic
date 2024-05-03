package com.jeltechnologies.sheetmusic.autocomplete;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;

public class AutoCompleteSeriesBean extends AutoCompleteBean {
    private static final long serialVersionUID = -4385756661793309479L;
    
    public AutoCompleteSeriesBean(User user, SheetMusicContext context) {
	super(user);
	add(new Library(user, context).getSeries());
    }
}
