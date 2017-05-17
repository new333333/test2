/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.XmlUtil;

/**
 * A Tab bar object contains a list of tabs to be shown at the top of the page. 
 * 
 * Each Tab bar item is a map of data to be used to draw the tab.
 * All public methods must be synchronized to control access from multiple browser windows.  Since we
 * create the pop-ups in the first place, we need to control them.
 * 
 * @author hurley
 */
@SuppressWarnings("unchecked")
public class Tabs {
   	//Tab map keys
   	
	public static final String END_DATE="endDate";
	public static final String YEAR_MONTH="yearMonth";
	public static final String TAG_COMMUNITY = "cTag";
	public static final String TAG_PERSONAL = "pTag";
   	
   	public final static String TITLE = "title";
   	public final static String PATH = "path";
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

   	// Keys used to store and retrieve data from the Map used to
   	// serialize Tabs for persistence in the user's properties.
	private final static String SERIALIZATION_KEY_BINDER_ID			= "binderId";
	private final static String SERIALIZATION_KEY_CURRENT_TAB_ID	= "currentTabId";
	private final static String SERIALIZATION_KEY_ENTRY_ID			= "entryId";
	private final static String SERIALIZATION_KEY_NEXT_TAB_ID		= "nextTabId";
	private final static String SERIALIZATION_KEY_QUERY				= "query";
	private final static String SERIALIZATION_KEY_TAB_BASE			= "tab.";
	private final static String SERIALIZATION_KEY_TAB_COUNT			= "tabCount";
	private final static String SERIALIZATION_KEY_TAB_DATA			= "tabData";
	private final static String SERIALIZATION_KEY_TAB_ID			= "tabId";
	private final static String SERIALIZATION_KEY_TYPE				= "type";

	
   	/**
   	 * Returns the Tabs object from the request or session.  Note that
   	 * if there is yet to be one stored in the session, the user's
   	 * properties are queried to see if one has been persisted there.
   	 * 
   	 * @param request | session
   	 * 
   	 * @return
   	 */
	public static Tabs getTabs(PortletRequest request) {
		return getTabs(WebHelper.getRequiredPortletSession(request));
	}
	
	public static Tabs getTabs(PortletSession ps) {
		return getTabsImpl(ps, null);
	}
	
	public static Tabs getTabs(HttpServletRequest request) {
		return getTabs(WebHelper.getRequiredSession(request));
	}
	
	public static Tabs getTabs(HttpSession hs) {
		return getTabsImpl(null, hs);
	}
	
	private static Tabs getTabsImpl(PortletSession ps, HttpSession hs) {
		Tabs reply = null;
		
		if      (null != ps) reply = ((Tabs) ps.getAttribute(ObjectKeys.SESSION_TABS, PortletSession.APPLICATION_SCOPE));			
		else if (null != hs) reply = ((Tabs) hs.getAttribute(ObjectKeys.SESSION_TABS));
		
		if (null == reply) {
			User user = RequestContextHolder.getRequestContext().getUser();
			reply = loadUserTabsImpl(ps, hs, user.getId());
		}
		
		return reply;
	}
	
	/*
	 */
	private Tabs() {	
	}

	/**
	 * Returns the tab for a specific binder.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
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
	
	/**
	 * Returns a List of the currently defined TabEntry's.
	 * 
	 * @return
	 */
	public synchronized List getTabList() {
		return new ArrayList(tabList);
	}
	
	/**
	 * Returns the current tab ID that we're working with.
	 * 
	 * @return
	 */
	public synchronized int getCurrentTabId() {
		return currentTabId;
	}

	/**
	 * Binder (folder or workspace) tab, 1 per binder.
	 * 
	 * @param binder
	 * @boolean clearData
	 * 
	 * @return
	 */
	public synchronized TabEntry findTab(Binder binder, boolean clearData) {
		if (null == binder) {
			return new TabEntry(this); // Return dummy.
		}
		
		// Look for this tab.
		TabEntry binderTab = null;
		for (int i = 0; i < tabList.size(); i += 1) {
			binderTab = tabList.get(i);
			String type = binderTab.getType();
			if ((BINDER.equals(type) || WORKSPACE.equals(type) || PROFILES.equals(type)) && 
					binder.getId().equals(binderTab.getBinderId()))  {
				if (clearData) {
					Map tabData = binderTab.getData();
					tabData.clear();
					tabData.put(ICON,  binder.getIconName());
					tabData.put(TITLE, binder.getTitle());
					tabData.put(PATH,  binder.getPathName());
					tabData.put(PAGE,  Integer.valueOf(0));
					binderTab.setData(tabData);
				}
				tabList.remove(i);
				tabList.add(0, binderTab);
				return binderTab;
			}
		}
		
	   	// Okay to set tab values, not visible yet.
		binderTab = new TabEntry(this);
	   	if (binder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
	   		binderTab.type = WORKSPACE;
    	}
	   	else if (binder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
    		binderTab.type = BINDER;
    	}
	   	else if (binder.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
    		binderTab.type = PROFILES;
    	}
 	   	binderTab.binderId = binder.getId();
	   	binderTab.tabData.put(ICON,  binder.getIconName());
	   	binderTab.tabData.put(TITLE, binder.getTitle());
	   	binderTab.tabData.put(PATH,  binder.getPathName());
	   	binderTab.tabData.put(PAGE,  Integer.valueOf(0));
	   	addTab(binderTab);
		return binderTab;		
	}	
	
	/**
	 * ?
	 * 
	 * @param type
	 * @param tabId
	 * 
	 * @return
	 */
	public synchronized TabEntry findTab(String type, Integer tabId) {
		if (tabId == null) {
			return null;
		}
		
		//Look for this tab
		TabEntry tab = null;
		for (int i = 0; i < tabList.size(); i += 1) {
			tab = tabList.get(i);
			if (type.equals(tab.type) && tabId.equals(tab.tabId)) {
				tabList.remove(i);
				tabList.add(0, tab);
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * ?
	 * 
	 * @param query
	 * @param options
	 * 
	 * @return
	 */
	public synchronized TabEntry addTab(Document query, Map options) {
	   	// Okay to set tab values, not visible yet.
		TabEntry tab = new TabEntry(this);
		tab.type = SEARCH;
		tab.query= query.asXML();
		tab.tabData.put(PAGE,  Integer.valueOf(1));
		tab.tabData.put(TITLE, "");
		tab.tabData.put(PATH,  "");
		tab.tabData.put(ICON,  "pics/sym_s_search.gif");
		tab.tabData.putAll(options);
		addTab(tab);
		return tab;
	   	
	}
	
	/* 
	 */
	protected void addTab(TabEntry tab) {
		// History list now puts new items at the top of the
		// list and prunes off old stuff.
		tab.tabId = ++nextTabId;
		tabList.add(0, tab);
		int maxItems = SPropsUtil.getInt("recent-places-depth", 10);
		if (tabList.size() > maxItems) {
			tabList.remove(maxItems);
		}
	}
	
	/*
	 * Returns a Map that that represents the serialization of this
	 * Tabs object.
	 */
	private Map<String, Object> getSerializationMap() {
		Map<String, Object> reply = new HashMap<String, Object>();
		
		reply.put(SERIALIZATION_KEY_CURRENT_TAB_ID, new Integer(currentTabId));
		reply.put(SERIALIZATION_KEY_NEXT_TAB_ID,    new Integer(nextTabId   ));
		
		int tabIndex = 0;
		for (TabEntry tab:  tabList) {
			String tabBase = (SERIALIZATION_KEY_TAB_BASE + String.valueOf(tabIndex++) + ".");
			
			reply.put(tabBase + SERIALIZATION_KEY_BINDER_ID, tab.binderId);
			reply.put(tabBase + SERIALIZATION_KEY_ENTRY_ID,  tab.entryId );
			reply.put(tabBase + SERIALIZATION_KEY_TAB_ID,    tab.tabId   );
			reply.put(tabBase + SERIALIZATION_KEY_TYPE,      tab.type    );
			reply.put(tabBase + SERIALIZATION_KEY_QUERY,     tab.query   );
			reply.put(tabBase + SERIALIZATION_KEY_TAB_DATA,  tab.tabData );
		}
		reply.put(SERIALIZATION_KEY_TAB_COUNT, new Integer(tabIndex));
		
		return reply;
	}
	
	/*
	 * Constructs and returns a Tabs object from a serialization Map.
	 */
	private static Tabs loadSerializationMap(Map<String, Object> serializationMap) {
		Tabs reply = new Tabs();
		if ((null != serializationMap) && (!(serializationMap.isEmpty()))) {
			reply.currentTabId = ((Integer) serializationMap.get(SERIALIZATION_KEY_CURRENT_TAB_ID)).intValue();
			reply.nextTabId    = ((Integer) serializationMap.get(SERIALIZATION_KEY_NEXT_TAB_ID)).intValue();
			
			int tabCount = ((Integer) serializationMap.get(SERIALIZATION_KEY_TAB_COUNT)).intValue();
			for (int i = 0; i < tabCount; i += 1) {
				String tabBase = (SERIALIZATION_KEY_TAB_BASE + String.valueOf(i) + ".");
				
				TabEntry te = new TabEntry(reply);
				te.binderId = ((Long)    serializationMap.get(tabBase + SERIALIZATION_KEY_BINDER_ID));
				te.entryId  = ((Long)    serializationMap.get(tabBase + SERIALIZATION_KEY_ENTRY_ID));
				te.tabId    = ((Integer) serializationMap.get(tabBase + SERIALIZATION_KEY_TAB_ID));
				te.type     = ((String)  serializationMap.get(tabBase + SERIALIZATION_KEY_TYPE));
				te.query    = ((String)  serializationMap.get(tabBase + SERIALIZATION_KEY_QUERY));
				te.tabData  = ((Map)     serializationMap.get(tabBase + SERIALIZATION_KEY_TAB_DATA));
				reply.tabList.add(te);
			}
		}
		return reply;
	}

	/**
	 * Loads the Tabs from the user's properties and stores them in the
	 * session cache.
	 * 
	 * This should be called for a user immediately after they login.
	 * 
	 * @param request | session
	 * @param userId
	 * 
	 * @return
	 */
	public static Tabs loadUserTabs(PortletRequest request, Long userId) {		
		return loadUserTabs(WebHelper.getRequiredPortletSession(request), userId);
	}
	
	public static Tabs loadUserTabs(PortletSession ps, Long userId) {		
		return loadUserTabsImpl(ps, null, userId);
	}
	
	public static Tabs loadUserTabs(HttpServletRequest request, Long userId) {		
		return loadUserTabs(WebHelper.getRequiredSession(request), userId);
	}	
	
	public static Tabs loadUserTabs(HttpSession hs, Long userId) {		
		return loadUserTabsImpl(null, hs, userId);
	}	
	
	private static Tabs loadUserTabsImpl(PortletSession ps, HttpSession hs, Long userId) {
		Tabs reply = null;
		if ((null != ps) || (null != hs)) {
			ProfileModule pm = ((ProfileModule) SpringContextUtil.getBean("profileModule"));
			Map<String, Object> tabsMap;
			try {
				UserProperties up = pm.getUserProperties(userId);
				tabsMap = ((Map<String, Object>) up.getProperty(ObjectKeys.USER_PROPERTY_TABS));
			}
			catch (Exception ex) {
				tabsMap = null;
			}
			reply = loadSerializationMap(tabsMap);
			if      (null != ps) ps.setAttribute(ObjectKeys.SESSION_TABS, reply, PortletSession.APPLICATION_SCOPE);
			else if (null != hs) hs.setAttribute(ObjectKeys.SESSION_TABS, reply);
		}
		return reply;
	}

	/**
	 * Stores the user's Tabs from the session cache to their properties.
	 *
	 * This should be called for a user as their session terminates.
	 * 
	 * @param request | session
	 * @param userId
	 */
	public static void saveUserTabs(PortletRequest request, Long userId) {
		saveUserTabs(WebHelper.getRequiredPortletSession(request), userId);
	}
	
	public static void saveUserTabs(PortletSession ps, Long userId) {
		saveUserTabsImpl(ps, null, userId);
	}
	
	public static void saveUserTabs(HttpServletRequest request, Long userId) {
		saveUserTabs(WebHelper.getRequiredSession(request), userId);
	}
	
	public static void saveUserTabs(HttpSession hs, Long userId) {
		saveUserTabsImpl(null, hs, userId);
	}
	
	private static void saveUserTabsImpl(PortletSession ps, HttpSession hs, Long userId) {
		ProfileModule pm = ((ProfileModule) SpringContextUtil.getBean("profileModule"));
		try {
			User user = RequestContextHolder.getRequestContext().getUser();
			if (!user.isShared()) {
				//Only save the tabs to the disk if not the guest account
				pm.setUserProperty(userId, ObjectKeys.USER_PROPERTY_TABS, getTabsImpl(ps, hs).getSerializationMap());
			}
		} catch(Exception e) {}
	}
	
	/**
	 * The only field in a tabEntry that can change after the entry is
	 * created is tabData, so access to it must by synchronized.
	 * 
	 * @author hurley
	 */
	public static class TabEntry {
		private Long binderId;
		private Long entryId;
		private Map tabData = new HashMap();
		private Integer tabId=-1;
		private String type="";
		private String query=null;
		private Tabs tabs;
		
		/**
		 * Class constructor.
		 * 
		 * @param tabs
		 */
		public TabEntry(Tabs tabs) {
			this.tabs = tabs;			
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Tabs    getTabs()     {return tabs;    }
		public Long    getEntryId()  {return entryId; }
		public Long    getBinderId() {return binderId;}
		public Integer getTabId()    {return tabId;   }
		public String  getType()     {return type;    }
		public String  getQuery()    {return query;   }
		
		/**
		 * ?
		 * 
		 * @return
		 */
		public Document getQueryDoc() {
			try {
				if (query != null) return XmlUtil.parseText(query);
			}
			catch (Exception de) {}
			return DocumentHelper.createDocument();
		}
		
		/**
		 * ?
		 * 
		 * @param data
		 */
		public synchronized void setData(Map data) {
			tabData.clear();
			tabData.putAll(data);
		}
		
		/**
		 * ?
		 * 
		 * @return
		 */
		public synchronized Map getData() {
			return new HashMap(tabData);
		}
	}
}
