package com.jeltechnologies.screenmusic.jsonpayloads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jeltechnologies.screenmusic.library.Book;
import com.jeltechnologies.screenmusic.library.Category;

public class CategoriesAndBooks {
    private List<CategoryAndBooks> categories = new ArrayList<CategoryAndBooks>();
    
    public void add(String categoryName, Book book) {
	CategoryAndBooks cb = getCategoriesAndBooks(categoryName);
	if (cb == null) {
	    cb = new CategoryAndBooks();
	    Category category = new Category();
	    category.setName(categoryName);
	    cb.setCategory(category);
	    categories.add(cb);
	}
	cb.add(book);
    }
    
    private CategoryAndBooks getCategoriesAndBooks(String categoryName) {
	CategoryAndBooks found = null;
	Iterator<CategoryAndBooks> iterator = categories.iterator();
	while (found == null && iterator.hasNext()) {
	    CategoryAndBooks current = iterator.next();
	    if (categoryName.equals(current.getCategory().getName())) {
		found = current;
	    }
	}
	return found;
    }

    public List<CategoryAndBooks> getCategories() {
        return categories;
    }
    
    public void sort() {
	for (CategoryAndBooks cb : categories) {
	    cb.sort();
	}
    }
}
