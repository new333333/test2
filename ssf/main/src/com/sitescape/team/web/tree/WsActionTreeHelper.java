package com.sitescape.team.web.tree;

import org.dom4j.Element;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.web.WebKeys;

public class WsActionTreeHelper implements DomTreeHelper {
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
		if (type == DomTreeBuilder.TYPE_SKIPLIST) {return true;}
		return false;
	}
	public boolean hasChildren(AllModulesInjected bs, Object source, int type) {
		return ((Binder)source).getBinderCount() > 0;
	}
	
	public String getAction(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_FOLDER) return WebKeys.ACTION_VIEW_FOLDER_LISTING;
		else if (source instanceof ProfileBinder) return WebKeys.ACTION_VIEW_PROFILE_LISTING;
		return WebKeys.ACTION_VIEW_WS_LISTING;
	}
	public String getURL(int type, Object source){return "";}
	public String getDisplayOnly(int type, Object source) {return "false";}
	public String getTreeNameKey() {return null;};
	public String getPage() {return "";}
	public void customize(AllModulesInjected bs, Object source, int type, Element element) {};
	
}
