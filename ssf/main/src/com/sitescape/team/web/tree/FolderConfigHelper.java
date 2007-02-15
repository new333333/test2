package com.sitescape.team.web.tree;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.util.AllBusinessServicesInjected;
//dom tree helper that only issues callbacks for folders
//The tree tag will call _showId routine implemented in jsp
public class FolderConfigHelper implements DomTreeHelper {
	
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
		return false;
	}
	public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
		return bs.getBinderModule().hasBinders((Binder)source);
	}
	
	public String getAction(int type, Object source) {
		return null;
	}
	public String getURL(int type, Object source) {return null;}
	public String getDisplayOnly(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_FOLDER) return "false";
		return "true";
	}
	//each name must be unqiue
	public String getTreeNameKey() {return "editForum";}
			
}
