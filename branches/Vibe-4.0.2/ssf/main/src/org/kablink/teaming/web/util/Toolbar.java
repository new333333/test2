/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.web.util;

import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashMap;
import javax.portlet.PortletURL;
/**
 * @author hurley
 *
 * A Toolbar object contains a sorted list of toolbar menus to be shown in a bar that 
 *   extends across the display page. Each menu can be either a dropdown menu
 *   or a direct link.
 * 
 * Each Toolbar dropdown menu can have multiple categories to be shown on the menu.
 *   Categories let the toolbar designer congregate the menu links into sections within 
 *   the menu.
 * 
 * If the Toolbar menu is to be a direct link (i.e., this toolbar item has only one 
 *   operation to do), then specify the url itself when adding the toolbar menu. 
 *   The url can be specified in two ways: as a fully specified url, or as a map 
 *   of key/value parameters. These parameters are used to build a url from within the jsp.
 * 
 * A third option when building the toolbar is a set of qualifiers:
 *   popup = true		pop up the url into a new window
 *   
 *   onClick = text     Add an onClick phrase (use: ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK)
 *                      (popup commands are not processed if an "onClick" phrase is specified)
 */
public class Toolbar {
	private SortedMap toolbar = new TreeMap();
	
	public void addToolbarMenu(String name, String title, PortletURL url) {
		Map qualifiers = new HashMap();
		addToolbarMenu(name, title, url, qualifiers);
	}
	public void addToolbarMenu(String name, String title, PortletURL url, Map qualifiers) {
		addToolbarMenu(name, title);
		Map toolbarData = (Map) this.toolbar.get(name);
		toolbarData.put("url", url);
		toolbarData.put("qualifiers", qualifiers);
	}
	public void addToolbarMenu(String name, String title, Map params) {
		Map qualifiers = new HashMap();
		addToolbarMenu(name, title, params, qualifiers);
	}
	public void addToolbarMenu(String name, String title, Map params, Map qualifiers) {
		addToolbarMenu(name, title);
		Map toolbarData = (Map) this.toolbar.get(name);
		toolbarData.put("urlParams", params);
		toolbarData.put("qualifiers", qualifiers);
	}
	public void addToolbarMenu(String name, String title) {
		Map toolbarData = null;
		if (this.toolbar.containsKey(name)) {
			toolbarData = (Map) this.toolbar.get(name);
		} else {
			toolbarData = new HashMap();
			toolbar.put(name, toolbarData);
		}
		toolbarData.put("title", title);
	}
	public void addToolbarMenu(String name, String title, String url) {
		Map qualifiers = new HashMap();
		addToolbarMenu(name, title, url, qualifiers);
	}
	public void addToolbarMenu(String name, String title, String url, Map qualifiers) {
		addToolbarMenu(name, title);
		Map toolbarData = (Map) this.toolbar.get(name);
		toolbarData.put("url", url);
		toolbarData.put("qualifiers", qualifiers);
	}
	public void deleteToolbarMenu(String name) {
		toolbar.remove(name);
	}
	private Map getCategory(String name, String category, Map qualifiers) {
		if (this.toolbar.containsKey(name)) {
			Map toolbarData = (Map) this.toolbar.get(name);
			Map toolbarCategories = null;
			if (toolbarData.containsKey("categories")) {
				toolbarCategories = (Map) toolbarData.get("categories");
			} else {
				toolbarCategories = new TreeMap();
				toolbarData.put("categories", toolbarCategories);
			}
			Map toolbarCategory = null;
			if (toolbarCategories.containsKey(category)) {
				toolbarCategory = (Map) toolbarCategories.get(category);
			} else {
				toolbarCategory = new LinkedHashMap();
				toolbarCategories.put(category, toolbarCategory);
			}
			return toolbarCategory;
		}
		return null;
	}
		
	public void addToolbarMenuItem(String name, String category, String title, Map urlParams) {
		Map qualifiers = new HashMap();
		addToolbarMenuItem(name, category, title, urlParams, qualifiers);
	}
	public void addToolbarMenuItem(String name, String category, String title, Map urlParams, Map qualifiers) {
		Map toolbarCategory = getCategory(name, category, qualifiers);
		if (toolbarCategory == null) return;
		Map toolbarCategoryMap;
		if (toolbarCategory.containsKey(title)) {
			toolbarCategoryMap = (Map) toolbarCategory.get(title);
		} else {
			toolbarCategoryMap = new TreeMap();
		}
		toolbarCategoryMap.put("urlParams", urlParams);
		toolbarCategoryMap.put("qualifiers", qualifiers);
		toolbarCategory.put(title, toolbarCategoryMap);
	}
	
	public void addToolbarMenuItem(String name, String category, String title, PortletURL url) {
		Map qualifiers = new HashMap();
		addToolbarMenuItem(name, category, title, url, qualifiers);
	}
	public void addToolbarMenuItem(String name, String category, String title, PortletURL url, Map qualifiers) {
		Map toolbarCategory = getCategory(name, category, qualifiers);
		if (toolbarCategory == null) return;
		Map toolbarCategoryMap;
		if (toolbarCategory.containsKey(title)) {
			toolbarCategoryMap = (Map) toolbarCategory.get(title);
		} else {
			toolbarCategoryMap = new TreeMap();
		}
		toolbarCategoryMap.put("url", url);
		toolbarCategoryMap.put("qualifiers", qualifiers);
		toolbarCategory.put(title, toolbarCategoryMap);
	}
	public void addToolbarMenuItem(String name, String category, String title, String url) {
		Map qualifiers = new HashMap();
		addToolbarMenuItem(name, category, title, url, qualifiers);
	}
	public void addToolbarMenuItem(String name, String category, String title, String url, Map qualifiers) {
		Map toolbarCategory = getCategory(name, category, qualifiers);
		if (toolbarCategory == null) return;
		Map toolbarCategoryMap;
		if (toolbarCategory.containsKey(title)) {
			toolbarCategoryMap = (Map) toolbarCategory.get(title);
		} else {
			toolbarCategoryMap = new TreeMap();
		}
		toolbarCategoryMap.put("url", url);
		toolbarCategoryMap.put("qualifiers", qualifiers);
		toolbarCategory.put(title, toolbarCategoryMap);
	}
	public boolean checkToolbarMenuItem(String name, String category, String title) {
		Map toolbarCategory = getCategory(name, category, new HashMap());
		if (toolbarCategory != null && toolbarCategory.containsKey(title)) {
			return true;
		}
		return false;
	}
	public Map getToolbar() {
		return this.toolbar;
	}
}
