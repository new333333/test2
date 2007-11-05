/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.web.WebKeys;
/**
 * @author hurley
 *
 * A Tabbar object contains a list of tabs to be shown at the top of the page. 
 * 
 * Each Tabbar item is a map of data to be used to draw the tab.
 *
 */
public class Tabs {
   	//Tab map keys
   	public final static String QUERY_DOC = "query_doc";
   	
	public static final String END_DATE="endDate";
	public static final String YEAR_MONTH="yearMonth";
	public static final String TAG_COMMUNITY = "cTag";
	public static final String TAG_PERSONAL = "pTag";
   	
   	public final static String TITLE = "title";
   	public final static String ICON = "icon";
   	public final static String PAGE = "page";
   	public final static String RECORDS_IN_PAGE = "recordsInPage";
   	
   	//Type keys
   	public final static String WORKSPACE = "workspace";
   	public final static String BINDER = "binder";
   	public final static String PROFILES = "profiles";
   	public final static String ENTRY = "entry";
   	public final static String USER = "user";
   	public final static String SEARCH = "search";
   	
   	private List<TabEntry> tabList = new ArrayList();
   	private int currentTabId=0;
   	private int nextTabId=0;
   	private int nextRefId=0;
	public static Tabs getTabs(PortletRequest request) {
		PortletSession ps = null;
		Tabs tabs = null;
		if (request != null) {
			ps = WebHelper.getRequiredPortletSession(request);
			tabs = (Tabs) ps.getAttribute(WebKeys.TABS, PortletSession.APPLICATION_SCOPE);			
		} 
		if (tabs == null) {
			tabs = new Tabs();
			if (ps != null) ps.setAttribute(WebKeys.TABS, tabs, PortletSession.APPLICATION_SCOPE);
		}
		return tabs;
	}
	private Tabs() {
		
	}
	public TabEntry getTab(Long binderId) {
		//Look for this tab
		for (TabEntry tab:tabList) {
			String type = tab.getType();
			if ((BINDER.equals(type) || WORKSPACE.equals(type) || PROFILES.equals(type)) && 
					binderId.equals(tab.getBinderId()))  {
				return tab;
			}
		}
		return null;		
	}
	public List getTabList() {
		return new ArrayList(tabList);
	}
	public int getCurrentTabId() {
		return currentTabId;
	}
	public int getNextTabId() {
		return nextTabId;
	}
	//Binder (folder or workspace) tab, 1 per binder
	public synchronized TabEntry findTab(Binder binder, boolean clearData) {
		TabEntry binderTab=null;
		//Look for this tab
		for (int i=0; i<tabList.size(); ++i) {
			binderTab = tabList.get(i);
			String type = binderTab.getType();
			if ((BINDER.equals(type) || WORKSPACE.equals(type) || PROFILES.equals(type)) && 
					binder.getId().equals(binderTab.getBinderId()))  {
				if (clearData) {
					binderTab.tabData.clear();
					binderTab.tabData.put(ICON, binder.getIconName());
					binderTab.tabData.put(TITLE, binder.getTitle());
					binderTab.tabData.put(PAGE, Integer.valueOf(0));
				}
				tabList.remove(i);
				tabList.add(0, binderTab);
				return binderTab;
			}
		}
		binderTab = new TabEntry(this);
	   	if (binder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
	   		binderTab.type = WORKSPACE;
    	} else if (binder.getEntityType().
    			equals(EntityIdentifier.EntityType.folder)) {
    		binderTab.type = BINDER;
    	} else if (binder.getEntityType().
    			equals(EntityIdentifier.EntityType.profiles)) {
    		binderTab.type = PROFILES;
    	}
    	
	   	binderTab.binderId = binder.getId();
	   	binderTab.tabData.put(ICON, binder.getIconName());
	   	binderTab.tabData.put(TITLE, binder.getTitle());
	   	binderTab.tabData.put(PAGE, Integer.valueOf(0));
	   	addTab(binderTab);
		return binderTab;		
	}	
	
	//Entry tab - 1 per entry
	public synchronized TabEntry findTab(FolderEntry entry, boolean clearData) {
		TabEntry entryTab = null;
		//Look for this tab
		for (int i=0; i<tabList.size(); ++i) {
			entryTab = tabList.get(i);
			if (ENTRY.equals(entryTab.getType()) && entry.getId().equals(entryTab.getEntryId()))  {
				if (clearData) {
					entryTab.tabData.clear();
					entryTab.tabData.put(TITLE, entry.getTitle());
				}
				tabList.remove(i);
				tabList.add(0, entryTab);
				return entryTab;
			}
		}
		entryTab = new TabEntry(this);
		entryTab.type=ENTRY;
		entryTab.binderId = entry.getParentBinder().getId();
		entryTab.entryId = entry.getId();
		addTab(entryTab);
		return entryTab;
	}
	
	public synchronized TabEntry findTab(Document query, Map options, Integer tabId) {
		//may be reusing a tab
		TabEntry tab = findTab(SEARCH, tabId);
		if (tab != null) {
			tab.tabData.clear();
			tab.tabData.put(PAGE, Integer.valueOf(1));
			tab.query = query.asXML();
			tab.tabData.putAll(options);
		} else {
			tab = addTab(query, options);
		}
		return tab;
	}
	public synchronized TabEntry findTab(String type, Integer tabId) {
		if (tabId == null) return null;
		TabEntry tab = null;
		//Look for this tab
		for (int i=0; i<tabList.size(); ++i) {
			tab=tabList.get(i);
			if (type.equals(tab.getType()) && tabId == tab.getTabId()) {
				tabList.remove(i);
				tabList.add(0, tab);
				return tab;
			}
		}
		return null;
	}
	public synchronized TabEntry addTab(Document query, Map options) {
		TabEntry tab = new TabEntry(this);
		tab.type = SEARCH;
		tab.query= query.asXML();
		tab.tabData.put(PAGE, Integer.valueOf(1));
		tab.tabData.put(TITLE, "");
		tab.tabData.put(ICON, "pics/sym_s_search.gif");
		tab.tabData.putAll(options);
		addTab(tab);
		return tab;
	   	
	}
	
	protected void addTab(TabEntry tab) {
		tab.tabId = ++nextTabId;
		// History list now puts new items at the top of the
		// list and prunes off old stuff.
		tabList.add(0, tab);
		if (tabList.size() > 5) {
			tabList.remove(5);
		}
		
	}

	public class TabEntry {
		protected Long binderId,entryId;
		protected Map tabData = new HashMap();
		protected Integer tabId=-1;
		protected String type="";
		protected String query=null;
		protected Tabs tabs;
		public TabEntry(Tabs tabs) {
			this.tabs = tabs;
			
		}
		public Tabs getTabs() {
			return tabs;
		}
		public Long getEntryId() {
			return entryId;
		}
		public Long getBinderId() {
			return binderId;
		}
		public Integer getTabId() {
			return tabId;
		}
		public String getType() {
			return type;
		}
		public String getQuery() {
			return query;
		}
		public Document getQueryDoc() {
			try {
				if (query != null) return DocumentHelper.parseText(query);
			} catch (Exception de) {
				
			}
			return DocumentHelper.createDocument();
		}
		public synchronized void setData(Map data) {
			tabData.putAll(data);
		}
		public Map getData() {
			return new HashMap(tabData);
		}
		public void clear() {
			binderId = null;
			entryId = null;
			tabData.clear();
			type = "";
			query = null;
		}
	}
}
