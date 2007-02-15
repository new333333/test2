package com.sitescape.team.web.tree;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.web.WebKeys;

public class MailTreeHelper implements DomTreeHelper {
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
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
}

