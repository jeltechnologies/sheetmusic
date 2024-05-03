package com.jeltechnologies.sheetmusic.servlet.jstree;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import com.jeltechnologies.sheetmusic.User;

/**
 * See https://github.com/vakata/jstree/wiki
 */
public class JSTreeFactory {
    
    private final User user;
    
    public JSTreeFactory(User user) {
	this.user = user;
    }
    
    public JSTreeData getFolders(File selectedFolder) {
	JSTreeData root = new JSTreeData();
	File folder = user.sheetMusicFolder().toFile();
	walkFolder(root, folder, selectedFolder);
	return root;
    }
    
    private void walkFolder(JSTreeData data, File folder, File selectedFolder) {
	String id = user.getRelativeFileName(folder);
	if (!id.startsWith("/")) {
	    id = "/" + id;
	}
	String name;
	if (id.equals("/")) {
	    name = id;
	} else {
	    name = folder.getName();
	}
	data.setText(name);
	data.setId(id);
	JSTreeDataState state = new JSTreeDataState();
	state.setOpened(false);
	if (selectedFolder.equals(folder)) {
	    state.setSelected(true);
	}
	data.setState(state);
	File[] childFolders = folder.listFiles(new FileFilter() {
	    @Override
	    public boolean accept(File pathname) {
		return pathname.isDirectory();
	    }
	});
	for (File childFolder : childFolders) {
	    JSTreeData childData = new JSTreeData();
	    if (data.getChildren() == null) {
		data.setChildren(new ArrayList<JSTreeData>());
	    }
	    data.getChildren().add(childData);
	    walkFolder(childData, childFolder, selectedFolder);
	}
    }

}
