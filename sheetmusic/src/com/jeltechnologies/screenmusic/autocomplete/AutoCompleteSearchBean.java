package com.jeltechnologies.screenmusic.autocomplete;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;

public class AutoCompleteSearchBean extends AutoCompleteBean {
    private static final long serialVersionUID = 8279990868343207436L;

    public AutoCompleteSearchBean(User user, ScreenMusicContext context) {
	super(user);
	add(new Library(user, context).getAllSearchableText());
    }

}
