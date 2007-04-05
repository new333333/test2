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
import com.sitescape.team.util.AllBusinessServicesInjected;

public class SearchTreeHelper implements DomTreeHelper {
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
		return false;
	}
	public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
		return bs.getBinderModule().hasBinders((Binder)source);
	}

	public String getAction(int type, Object source) {
		//use action to indicate type of name that is choose.  This allows us to 
		//determine the name of the checkbox.  We use variable names so the
		//profile binder can show up as the parent of the users/groups and 
		//as the parent of user workspaces. 
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return DomTreeBuilder.NODE_TYPE_WORKSPACE;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return DomTreeBuilder.NODE_TYPE_FOLDER;}
		if (type == DomTreeBuilder.TYPE_PEOPLE) {return DomTreeBuilder.NODE_TYPE_PEOPLE;}
		
		return null;
	}
	public String getURL(int type, Object source) {return "";}
	public String getDisplayOnly(int type, Object source) {return "false";}
	//each name must be unqiue
	public String getTreeNameKey() {return "search";}
	
}

