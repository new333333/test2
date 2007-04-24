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
import com.sitescape.team.web.WebKeys;

public class MailTreeHelper implements DomTreeHelper {
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
		if (type == DomTreeBuilder.TYPE_SKIPLIST) {return true;}
		return false;
	}
	public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
		return bs.getBinderModule().hasBinders((Binder)source);
	}

	public String getAction(int type, Object source) {
		return WebKeys.ACTION_CONFIG_EMAIL;
	}
	public String getURL(int type, Object source) {return "";}
	public String getDisplayOnly(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_FOLDER) return "false";
		return "true";
	}
	//each name must be unqiue
	public String getTreeNameKey() {return "email";}
	public String getPage() {return "";}
}

