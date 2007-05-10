/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.tree;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.util.AllBusinessServicesInjected;
//dom tree helper that only issues callbacks for folders
//used for selecting folders only
//The tree tag will call _showId routine implemented in jsp
public class FolderConfigHelper implements DomTreeHelper {
	
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
		if (type == DomTreeBuilder.TYPE_SKIPLIST) {return true;}
		if (type == DomTreeBuilder.TYPE_TEMPLATE) {return true;}
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
		if ((type == DomTreeBuilder.TYPE_TEMPLATE) && ((TemplateBinder)source).getEntityType().equals(EntityType.folder)) return "false";
		return "true";
	}
	//each name must be unqiue
	public String getTreeNameKey() {return "editForum";}
	public String getPage() {return "";}
			
}
