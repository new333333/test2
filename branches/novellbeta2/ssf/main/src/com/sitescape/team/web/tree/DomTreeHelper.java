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
import com.sitescape.team.util.AllModulesInjected;

public interface DomTreeHelper {
	public boolean supportsType(int type, Object source);
	
	public String getAction(int type, Object source);
	public String getURL(int type, Object source);
	public String getDisplayOnly(int type, Object source);
	public String getTreeNameKey();
	public String getPage();
	public boolean hasChildren(AllModulesInjected bs, Object source, int type);

}
