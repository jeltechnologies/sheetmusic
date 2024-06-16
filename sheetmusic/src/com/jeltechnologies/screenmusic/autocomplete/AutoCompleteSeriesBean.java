package com.jeltechnologies.screenmusic.autocomplete;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.User;
import com.jeltechnologies.screenmusic.library.Library;
import com.jeltechnologies.screenmusic.servlet.ScreenMusicContext;

public class AutoCompleteSeriesBean extends AutoCompleteBean {
    private static final long serialVersionUID = -4385756661793309479L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCompleteSeriesBean.class);
    
    public AutoCompleteSeriesBean(User user, ScreenMusicContext context) {
	super(user);
	List<String> series = new Library(user, context).getSeries();
	LOGGER.debug("Series: " + series);
	add(series);
    }
}
