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

import org.dom4j.Element;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.AllModulesInjected;

public class WorkspaceAddWorkspaceHelper implements DomTreeHelper {
	public boolean supportsType(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
		if (type == DomTreeBuilder.TYPE_SKIPLIST) {return true;}
		return false;
	}
	public boolean hasChildren(AllModulesInjected bs, Object source, int type) {
		return ((Binder)source).getBinderCount() > 0;
	}
	

	public String getAction(int type, Object source) {
		return null;
	}
	public String getURL(int type, Object source) {return null;}
	public String getDisplayOnly(int type, Object source) {
		if (type == DomTreeBuilder.TYPE_WORKSPACE) return "false";
		return "true";
	}
	//each name must be unqiue
	public String getTreeNameKey() {return "addWs";}
	public String getPage() {return "";}
	public void customize(AllModulesInjected bs, Object source, int type, Element element) {
		if ((type == DomTreeBuilder.TYPE_WORKSPACE)) {
			Workspace ws = (Workspace) source;
			if (!bs.getWorkspaceModule().testAccess(ws, WorkspaceModule.WorkspaceOperation.addWorkspace)) {
				element.addAttribute("titleClass", "ss_light");
				element.addAttribute("titleHighlightedClass", "ss_light");
			}
		}
	}

}
