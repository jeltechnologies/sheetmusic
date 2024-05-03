package com.jeltechnologies.sheetmusic.tags;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.sheetmusic.db.DBUsers;
import com.jeltechnologies.sheetmusic.jsonpayloads.UserPreferences;
import com.jeltechnologies.sheetmusic.library.Category;
import com.jeltechnologies.sheetmusic.library.Library;
import com.jeltechnologies.sheetmusic.servlet.SheetMusicContext;

public class CategoriesSelectTag extends AbstractSelectTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesSelectTag.class);

    @Override
    protected List<String> getOptions() {
	List<Category> categories = new Library(getUser(), new SheetMusicContext(getRequest())).getAllCategories();
	List<String> options = new ArrayList<String>(categories.size());
	for (Category cat : categories) {
	    options.add(cat.getName());
	}
	return options;
    }

    @Override
    protected String getSelected() {
	String user = getUser().name();
	String selected = null;
	DBUsers db = null;
	try {
	    db = new DBUsers();
	    UserPreferences preferences = db.getPreferences(user);
	    if (preferences != null) {
		selected = preferences.getCategorySelected();
	    }

	} catch (Exception e) {
	    LOGGER.warn("Cannot get user preferences for " + user, e);
	} finally {
	    if (db != null) {
		db.close();
	    }
	}
	if (selected == null) {
	    List<String> options = getOptions();
	    if (!options.isEmpty()) {
		selected = options.get(0);
	    }
	}
	return selected;
    }
}