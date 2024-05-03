package com.jeltechnologies.sheetmusic.autocomplete;

import com.jeltechnologies.sheetmusic.User;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;

public class AutoCompleteSearchBean extends AutoCompleteBean {
    private static final long serialVersionUID = 8279990868343207436L;

    public AutoCompleteSearchBean(User user, SheetMusicContext context) {
	super(user);
	add(new Library(user, context).getAllSearchableText());
    }

}
