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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;

/**
 * @author hurley
 *
 * A Tabbar object contains a list of tabs to be shown at the top of the page. 
 * 
 * Each Tabbar item is a map of data to be used to draw the tab.
 * All public methods must be synchronzied to control access from mutiple browser windows.  Since we
 * create the pop-ups in the first place, we need to control them.
 *
 */
public class Tabs {
   	//Tab map keys
   	
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
    	public final static String SEARCH = "search";
   	
   	private List<TabEntry> tabList = new ArrayList();
   	private int currentTabId=0;
   	private int nextTabId=0;
	public static Tabs getTabs(PortletRequest request) {
		PortletSession ps = null;
		Tabs tabs = null;
		if (request != null) {
			ps = WebHelper.getRequiredPortletSession(request);
			tabs = (Tabs) ps.getAttribute(ObjectKeys.SESSION_TABS, PortletSession.APPLICATION_SCOPE);			
		} 
		if (tabs == null) {
			tabs = new Tabs();
			if (ps != null) ps.setAttribute(ObjectKeys.SESSION_TABS, tabs, PortletSession.APPLICATION_SCOPE);
		}
		return tabs;
	}
	private Tabs() {
		
	}
	public synchronized TabEntry getTab(Long binderId) {
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
	public synchronized List getTabList() {
		return new ArrayList(tabList);
	}
	public synchronized int getCurrentTabId() {
		return currentTabId;
	}
	//Binder (folder or workspace) tab, 1 per binder
	public synchronized TabEntry findTab(Binder binder, boolean clearData) {
		if (binder == null) return new TabEntry(this); //return dummy
		TabEntry binderTab=null;
		//Look for this tab
		for (int i=0; i<tabList.size(); ++i) {
			binderTab = tabList.get(i);
			String type = binderTab.getType();
			if ((BINDER.equals(type) || WORKSPACE.equals(type) || PROFILES.equals(type)) && 
					binder.getId().equals(binderTab.getBinderId()))  {
				if (clearData) {
					Map tabData = binderTab.getData();
					tabData.clear();
					tabData.put(ICON, binder.getIconName());
					tabData.put(TITLE, binder.getTitle());
					tabData.put(PAGE, Integer.valueOf(0));
					binderTab.setData(tabData);
				}
				tabList.remove(i);
				tabList.add(0, binderTab);
				return binderTab;
			}
		}
		binderTab = new TabEntry(this);
	   	//okay to set tab values, not visible yet
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
	
	public synchronized TabEntry findTab(String type, Integer tabId) {
		if (tabId == null) return null;
		TabEntry tab = null;
		//Look for this tab
		for (int i=0; i<tabList.size(); ++i) {
			tab=tabList.get(i);
			if (type.equals(tab.type) && tabId == tab.tabId) {
				tabList.remove(i);
				tabList.add(0, tab);
				return tab;
			}
		}
		return null;
	}
	public synchronized TabEntry addTab(Document query, Map options) {
	   	//okay to set tab values, not visible yet
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
	//The only field in a tabEntry that can change after the entry is created 
	//is tabData, so acess to it must by synchronzied.
	public class TabEntry {
		private Long binderId,entryId;
		private Map tabData = new HashMap();
		private Integer tabId=-1;
		private String type="";
		private String query=null;
		private Tabs tabs;
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
			tabData.clear();
			tabData.putAll(data);
		}
		public synchronized Map getData() {
			return new HashMap(tabData);
		}

	}
}
