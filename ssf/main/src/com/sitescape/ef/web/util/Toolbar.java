package com.sitescape.ef.web.util;

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
 *   onClick = text     Add an onClick phrase
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
	private Map getCategory(String name, String category) {
		if (this.toolbar.containsKey(name)) {
			Map toolbarData = (Map) this.toolbar.get(name);
			Map toolbarCategories = null;
			if (toolbarData.containsKey("categories")) {
				toolbarCategories = (Map) toolbarData.get("categories");
			} else {
				toolbarCategories = new HashMap();
				toolbarData.put("categories", toolbarCategories);
			}
			Map toolbarCategory = null;
			if (toolbarCategories.containsKey(category)) {
				toolbarCategory = (Map) toolbarCategories.get(category);
			} else {
				toolbarCategory = new HashMap();
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
		Map toolbarCategory = getCategory(name, category);
		if (toolbarCategory == null) return;
		Map toolbarCategoryMap;
		if (toolbarCategory.containsKey(title)) {
			toolbarCategoryMap = (Map) toolbarCategory.get(title);
		} else {
			toolbarCategoryMap = new HashMap();
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
		Map toolbarCategory = getCategory(name, category);
		if (toolbarCategory == null) return;
		Map toolbarCategoryMap;
		if (toolbarCategory.containsKey(title)) {
			toolbarCategoryMap = (Map) toolbarCategory.get(title);
		} else {
			toolbarCategoryMap = new HashMap();
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
		Map toolbarCategory = getCategory(name, category);
		if (toolbarCategory == null) return;
		Map toolbarCategoryMap;
		if (toolbarCategory.containsKey(title)) {
			toolbarCategoryMap = (Map) toolbarCategory.get(title);
		} else {
			toolbarCategoryMap = new HashMap();
		}
		toolbarCategoryMap.put("url", url);
		toolbarCategoryMap.put("qualifiers", qualifiers);
		toolbarCategory.put(title, toolbarCategoryMap);
	}
	public boolean checkToolbarMenuItem(String name, String category, String title) {
		Map toolbarCategory = getCategory(name, category);
		if (toolbarCategory != null && toolbarCategory.containsKey(title)) {
			return true;
		}
		return false;
	}
	public SortedMap getToolbar() {
		return this.toolbar;
	}
}
