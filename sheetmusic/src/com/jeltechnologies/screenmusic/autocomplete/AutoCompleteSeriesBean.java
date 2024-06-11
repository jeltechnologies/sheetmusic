package com.jeltechnologies.screenmusic.autocomplete;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;

public class AutoCompleteSeriesBean extends AutoCompleteBean {
    private static final long serialVersionUID = -4385756661793309479L;
    
    public AutoCompleteSeriesBean(User user, ScreenMusicContext context) {
	super(user);
	add(new Library(user, context).getSeries());
    }
}
