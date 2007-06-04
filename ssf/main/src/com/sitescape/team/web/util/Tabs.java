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
package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.dom4j.Document;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.util.NLT;
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
	private Map tabs = null;
   	public final static String TABLIST = "tablist";
   	public final static String CURRENT_TAB = "current_tab";
   	public final static String NEXT_TAB_ID = "next_tab_id";
   	public final static String NEXT_REF = "next_ref";
   	
   	//Tab map keys
   	public final static String TYPE = "type";
   	public final static String TAB_ID = "tabId";
   	public final static String LAST_REF = "lastRef";
   	public final static String BINDER_ID = "binderId";
   	public final static String ENTRY_ID = "entryId";
   	public final static String QUERY_DOC = "query_doc";
   	public final static String AND_QUERY_DOC = "andQuery_doc";
   	
	public static final String END_DATE="endDate";
	public static final String YEAR_MONTH="yearMonth";
	public static final String TAG_COMMUNITY = "cTag";
	public static final String TAG_PERSONAL = "pTag";
   	
   	public final static String TITLE = "title";
   	public final static String ICON = "icon";
   	public final static String PAGE = "page";
   	public final static String SORTBY = "sortBy";
   	public final static String SORTDESCEND = "sortDescend";
   	public final static String RECORDS_IN_PAGE = "recordsInPage";
   	public final static String TAB_SEARCH_TEXT = "tabSearchText";
   	public final static String TAB_COMMUNITY_TAG_SEARCH_TEXT = "tabCommunityTagSearchText";
   	public final static String TAB_PERSONAL_TAG_SEARCH_TEXT = "tabPersonalTagSearchText";
   	
   	//Type keys
   	public final static String WORKSPACE = "workspace";
   	public final static String BINDER = "binder";
   	public final static String PROFILES = "profiles";
   	public final static String ENTRY = "entry";
   	public final static String USER = "user";
   	public final static String QUERY = "query";
   	public final static String SEARCH = "search";
	
	public Tabs(PortletRequest request) {
		PortletSession ps = null;
		if (request != null) {
			ps = WebHelper.getRequiredPortletSession(request);
			tabs = (Map) ps.getAttribute(WebKeys.TABS, PortletSession.APPLICATION_SCOPE);
		} 
		if (tabs == null) {
			tabs = new HashMap();
			tabs.put(TABLIST, new ArrayList());
			tabs.put(CURRENT_TAB, new Integer(0));
			tabs.put(NEXT_TAB_ID, new Integer(0));
			tabs.put(NEXT_REF, new Integer(0));
			if (ps != null) ps.setAttribute(WebKeys.TABS, tabs, PortletSession.APPLICATION_SCOPE);
		}
	}
	
	//Binder (folder or workspace) tab
	public int addTab(Binder binder) {
		Map options = new HashMap();
		return addTab(binder, options);
	}
	public int addTab(Binder binder, Map options) {
		int tabId = addTab();
		return setTab(tabId, binder, options);
	}
	
	public int findTab(Binder binder) {
		Map options = new HashMap();
		return findTab(binder, options);
	}
	public int findTab(Binder binder, boolean blnClearTab) {
		Map options = new HashMap();
		return findTab(binder, options, blnClearTab, -1);
	}
	public int findTab(Binder binder, Map options) {
		boolean blnClearTab = false;
		return findTab(binder, options, blnClearTab, -1);
	}
	
	public int findTab(Binder binder, Map options, boolean blnClearTab, int defaultTabId) {
		List tabList = (List) tabs.get(TABLIST);
		int tabId = -1;
		//Look for this tab
		for (int i = 0; i < tabList.size(); i++) {
			Map tab = (Map)tabList.get(i);
			if ((tab.get(TYPE)!=null && (tab.get(TYPE).equals(BINDER) || 
					tab.get(TYPE).equals(WORKSPACE) || 
					tab.get(TYPE).equals(PROFILES)) && 
					tab.containsKey(BINDER_ID) && 
					((Long)tab.get(BINDER_ID)).equals(binder.getId()))) {
				tabId = ((Integer)tab.get(TAB_ID)).intValue();
				break;
			}
		}
		if (tabId == -1 && defaultTabId == -1) {
			tabId = addTab();
		} else if (tabId == -1 && defaultTabId >= 0) {
			tabId = defaultTabId;
		}
		if (blnClearTab == true) clearTabInfo(tabId);
		return setTab(tabId, binder, options);
	}	
	
	//Entry tab
	public int addTab(Entry entry) {
		Map options = new HashMap();
		return addTab(entry, options);
	}
	public int addTab(Entry entry, Map options) {
		int tabId = addTab();
		return setTab(tabId, entry, options);
	}
	
	public int findTab(Entry entry) {
		Map options = new HashMap();
		return findTab(entry, options, -1);
	}
	
	public int findTab(Entry entry, boolean blnClearTab) {
		Map options = new HashMap();
		return findTab(entry, options, blnClearTab, -1);
	}

	public int findTab(Entry entry, Map options, int defaultTabId) {
		boolean blnClearTab = false;
		return findTab(entry, options, blnClearTab, defaultTabId);
	}
	
	public int findTab(Entry entry, Map options, boolean blnClearTab, int defaultTabId) {
		List tabList = (List) tabs.get(TABLIST);
		int tabId = -1;
		//Look for this tab
		for (int i = 0; i < tabList.size(); i++) {
			Map tab = (Map)tabList.get(i);
			if ((tab.get(TYPE).equals(ENTRY) || 
					tab.get(TYPE).equals(USER)) && 
					tab.containsKey(ENTRY_ID) && 
					((Long)tab.get(ENTRY_ID)).equals(entry.getId())) {
				tabId = ((Integer)tab.get(TAB_ID)).intValue();
				break;
			}
		}
		if (tabId == -1 && defaultTabId == -1) {
			tabId = addTab();
		} else if (tabId == -1 && defaultTabId >= 0) {
			tabId = defaultTabId;
		}
		if (blnClearTab == true) clearTabInfo(tabId);
		return setTab(tabId, entry, options);
	}
	
	//Search tab
	public int addTab(Document query) {
		Map options = new HashMap();
		return addTab(query, options);
	}
	public int addTab(Document query, Map options) {
		int tabId = addTab();
		return setTab(tabId, query, options);
	}
	public int findTab(Document query) {
		Map options = new HashMap();
		return findTab(query, options);
	}
	public int findTab(Document query, Map options) {
		boolean blnClearTab = false;
		return findTab(query, options, blnClearTab, -1);
	}
	public int findTab(Document query, Map options, boolean blnClearTab, int defaultTabId) {
		List tabList = (List) tabs.get(TABLIST);
		int tabId = -1;
		//Look for this tab
		for (int i = 0; i < tabList.size(); i++) {
			Map tab = (Map)tabList.get(i);
			if (tab != null && tab.get(TYPE)!=null && ((query != null && tab.get(TYPE).equals(QUERY) && tab.containsKey(QUERY_DOC) && 
					((Document)tab.get(QUERY_DOC)).asXML().equals(query.asXML()))) || (tab.get(TYPE).equals(SEARCH) && defaultTabId != -1)) {
				tabId = ((Integer)tab.get(TAB_ID)).intValue();
				break;
			}
		}
		if (tabId == -1 && defaultTabId == -1) {
			tabId = addTab();
		} else if (tabId == -1 && defaultTabId >= 0) {
			tabId = defaultTabId;
		}
		if (blnClearTab == true) clearTabInfo(tabId);
		return setTab(tabId, query, options);
	}
	
	public void clearCurrentTabInfo() {
		clearTabInfo(getCurrentTab());
	}
	
	public void clearTabInfo(int tabId)	{
		List tabList = getTabList();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber > -1) {
			Map tab = (Map) tabList.get(tabNumber);
			tab = new HashMap();
			tab.put(TAB_ID, new Integer(tabId));
			tabList.set(tabNumber, tab);
		}
	}
	
	public int setTab(int tabId, Binder binder) {
		return setTab(tabId, binder, new HashMap());
	}
	public int setTab(Binder binder) {
		return setTab(getCurrentTab(), binder, new HashMap());
	}
	public int setTab(Binder binder, boolean blnClearTab) {
		return setTab(getCurrentTab(), binder, new HashMap(), blnClearTab);
	}
	public int setTab(Binder binder, Map options) {
		return setTab(getCurrentTab(), binder, options);
	}
	
	public int setTab(int tabId, Binder binder, Map options) {
		boolean blnClearTab = false;
		return setTab(tabId, binder, options, blnClearTab);
	}
	
	public int setTab(int tabId, Binder binder, Map options, boolean blnClearTab) {
		if (blnClearTab) clearTabInfo(tabId);
		List tabList = getTabList();
		if (checkTabId(tabId) != tabId) tabId = addTab();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0) tabNumber = findTabNumber(addTab());
		Map tab = (Map) tabList.get(tabNumber);
		Integer page = (Integer) tab.get(PAGE);
		if (page == null) page = new Integer(0);
		if (options.containsKey(PAGE)) page = (Integer) options.get(PAGE);
		String sortBy = (String) tab.get(Tabs.SORTBY);
		if (options.containsKey(Tabs.SORTBY)) sortBy = (String) options.get(Tabs.SORTBY);
		if (sortBy != null) tab.put(Tabs.SORTBY, sortBy);
		String tabSearchText = (String) tab.get(Tabs.TAB_SEARCH_TEXT);
		if (options.containsKey(Tabs.TAB_SEARCH_TEXT)) tabSearchText = (String) options.get(Tabs.TAB_SEARCH_TEXT);
		if (tabSearchText != null) tab.put(Tabs.TAB_SEARCH_TEXT, tabSearchText);
		String tabTagSearchText = (String) tab.get(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT);
		if (options.containsKey(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT)) tabTagSearchText = (String) options.get(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT);
		if (tabTagSearchText != null) tab.put(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT, tabTagSearchText);
		String tabPTagSearchText = (String) tab.get(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT);
		if (options.containsKey(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT)) tabPTagSearchText = (String) options.get(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT);
		if (tabPTagSearchText != null) tab.put(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT, tabPTagSearchText);
		String sortDescend = (String) tab.get(Tabs.SORTDESCEND);
		if (options.containsKey(Tabs.SORTDESCEND)) sortDescend = (String) options.get(Tabs.SORTDESCEND);
		if (sortDescend != null) tab.put(Tabs.SORTDESCEND, sortDescend);
    	if (binder.getEntityType().
    			equals(EntityIdentifier.EntityType.workspace)) {
    		tab.put(TYPE, WORKSPACE);
    	} else if (binder.getEntityType().
    			equals(EntityIdentifier.EntityType.folder)) {
    		tab.put(TYPE, BINDER);
    	} else if (binder.getEntityType().
    			equals(EntityIdentifier.EntityType.profiles)) {
    		tab.put(TYPE, PROFILES);
    	}
    	
    	String strEndDate = (String) tab.get(END_DATE);
    	if (options.containsKey(END_DATE)) strEndDate = (String) options.get(END_DATE);
    	
    	String strYearMonth = (String) tab.get(YEAR_MONTH);
    	if (options.containsKey(YEAR_MONTH)) strYearMonth = (String) options.get(YEAR_MONTH);

    	String strTagCommunity = (String) tab.get(TAG_COMMUNITY);
    	if (options.containsKey(TAG_COMMUNITY)) strTagCommunity = (String) options.get(TAG_COMMUNITY);

    	String strTagPersonal = (String) tab.get(TAG_PERSONAL);
    	if (options.containsKey(TAG_PERSONAL)) strTagPersonal = (String) options.get(TAG_PERSONAL);
    	
		tab.put(BINDER_ID, binder.getId());
		tab.put(PAGE, page);
		
		if (options.containsKey(TITLE)) {
			tab.put(TITLE, new String((String) options.get(TITLE)));
		} else {
			tab.put(TITLE, binder.getTitle());
		}
		
		tab.put(ICON, binder.getIconName());
    	tab.put(END_DATE, strEndDate);
    	tab.put(YEAR_MONTH, strYearMonth);		
    	tab.put(TAG_COMMUNITY, strTagCommunity);		
    	tab.put(TAG_PERSONAL, strTagPersonal);		
		tab.remove(ENTRY_ID);
		tab.remove(QUERY_DOC);
		tab.remove(AND_QUERY_DOC);
		
		return ((Integer)tab.get(TAB_ID)).intValue();
	}
	
	public int setTab(int tabId, Entry entry) {
		return setTab(tabId, entry, new HashMap());
	}
	public int setTab(Entry entry) {
		return setTab(getCurrentTab(), entry, new HashMap());
	}
	public int setTab(Entry entry, boolean blnClearTab) {
		return setTab(getCurrentTab(), entry, new HashMap(), blnClearTab);
	}
	public int setTab(Entry entry, Map options) {
		return setTab(getCurrentTab(), entry, options);
	}
	
	public int setTab(int tabId, Entry entry, Map options) {
		boolean blnClearTab = false;
		return setTab(tabId, entry, options, blnClearTab);
	}
	
	public int setTab(int tabId, Entry entry, Map options, boolean blnClearTab) {
		if (blnClearTab) clearTabInfo(tabId);
		List tabList = getTabList();
		if (checkTabId(tabId) != tabId) tabId = addTab();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0) tabNumber = findTabNumber(addTab());
		Map tab = (Map) tabList.get(tabNumber);
    	if (entry.getEntityType().
    			equals(EntityIdentifier.EntityType.folderEntry)) {
    		tab.put(TYPE, ENTRY);
    	} else if (entry.getEntityType().
    			equals(EntityIdentifier.EntityType.user)) {
    		tab.put(TYPE, USER);
    	}
		//tab.put(TYPE, ENTRY);
		tab.put(BINDER_ID, entry.getParentBinder().getId());
		tab.put(ENTRY_ID, entry.getId());
		tab.put(TITLE, entry.getTitle());
		tab.put(ICON, entry.getIconName());
		tab.remove(PAGE);
		tab.remove(QUERY_DOC);
		tab.remove(AND_QUERY_DOC);
		
		return ((Integer)tab.get(TAB_ID)).intValue();
	}
	
	public int setTab(int tabId, Document query) {
		return setTab(tabId, query, new HashMap());
	}
	public int setTab(Document query) {
		return setTab(getCurrentTab(), query, new HashMap());
	}
	public int setTab(Document query, Map options) {
		return setTab(getCurrentTab(), query, options);
	}
	public int setTab(Document query, Map options, boolean blnClearTab) {
		return setTab(getCurrentTab(), query, options, blnClearTab);
	}
	public int setTab(int tabId, Document query, Map options) {
		boolean blnClearTab = false;
		return setTab(tabId, query, options, blnClearTab);
	}
	public int setTab(int tabId, Document query, Map options, boolean blnClearTab) {
		if (blnClearTab) clearTabInfo(tabId);
		List tabList = getTabList();
		if (checkTabId(tabId) != tabId) tabId = addTab();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0) tabNumber = findTabNumber(addTab());
		Map tab = (Map) tabList.get(tabNumber);
		Integer page = (Integer) tab.get(PAGE);
		if (page == null) page = new Integer(0);
		if (options.containsKey(PAGE)) page = (Integer) options.get(PAGE);
		String sortBy = (String) tab.get(Tabs.SORTBY);
		if (options.containsKey(Tabs.SORTBY)) sortBy = (String) options.get(Tabs.SORTBY);
		if (sortBy != null) tab.put(Tabs.SORTBY, sortBy);
		String tabSearchText = (String) tab.get(Tabs.TAB_SEARCH_TEXT);
		if (options.containsKey(Tabs.TAB_SEARCH_TEXT)) tabSearchText = (String) options.get(Tabs.TAB_SEARCH_TEXT);
		if (tabSearchText != null) tab.put(Tabs.TAB_SEARCH_TEXT, tabSearchText);
		String tabTagSearchText = (String) tab.get(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT);
		if (options.containsKey(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT)) tabTagSearchText = (String) options.get(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT);
		if (tabTagSearchText != null) tab.put(Tabs.TAB_COMMUNITY_TAG_SEARCH_TEXT, tabTagSearchText);
		String tabPTagSearchText = (String) tab.get(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT);
		if (options.containsKey(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT)) tabPTagSearchText = (String) options.get(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT);
		if (tabPTagSearchText != null) tab.put(Tabs.TAB_PERSONAL_TAG_SEARCH_TEXT, tabPTagSearchText);
		String sortDescend = (String) tab.get(Tabs.SORTDESCEND);
		if (options.containsKey(Tabs.SORTDESCEND)) sortDescend = (String) options.get(Tabs.SORTDESCEND);
		if (sortDescend != null) tab.put(Tabs.SORTDESCEND, sortDescend);
		if (options.containsKey(TYPE)) tab.put(TYPE, options.get(TYPE));
		else tab.put(TYPE, QUERY);
		tab.put(QUERY_DOC, query);
		if (options.containsKey(AND_QUERY_DOC)) {
			Document andQuery = (Document) options.get(AND_QUERY_DOC);
			tab.put(AND_QUERY_DOC, andQuery);
		} else {
			tab.put(AND_QUERY_DOC, null);
		}
		tab.put(PAGE, page);
		if (options.containsKey(TITLE)) {
			tab.put(TITLE, new String((String) options.get(TITLE)));
		} else {
			tab.put(TITLE, "");
		}
		
    	String strEndDate = (String) tab.get(END_DATE);
    	if (options.containsKey(END_DATE)) strEndDate = (String) options.get(END_DATE);
    	
    	String strYearMonth = (String) tab.get(YEAR_MONTH);
    	if (options.containsKey(YEAR_MONTH)) strYearMonth = (String) options.get(YEAR_MONTH);

    	String strTagCommunity = (String) tab.get(TAG_COMMUNITY);
    	if (options.containsKey(TAG_COMMUNITY)) strTagCommunity = (String) options.get(TAG_COMMUNITY);

    	String strTagPersonal = (String) tab.get(TAG_PERSONAL);
    	if (options.containsKey(TAG_PERSONAL)) strTagPersonal = (String) options.get(TAG_PERSONAL);
		
    	tab.put(END_DATE, strEndDate);
    	tab.put(YEAR_MONTH, strYearMonth);		
    	tab.put(TAG_COMMUNITY, strTagCommunity);		
    	tab.put(TAG_PERSONAL, strTagPersonal);		
		tab.put(ICON, "pics/sym_s_search.gif");
		tab.remove(BINDER_ID);
		tab.remove(ENTRY_ID);

		if (options.containsKey(ObjectKeys.SEARCH_USER_MAX_HITS)) tab.put(ObjectKeys.SEARCH_USER_MAX_HITS, options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		if (options.containsKey(ObjectKeys.SEARCH_OFFSET)) tab.put(ObjectKeys.SEARCH_OFFSET, options.get(ObjectKeys.SEARCH_OFFSET));
		if (options.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) tab.put(ObjectKeys.SEARCH_USER_OFFSET, options.get(ObjectKeys.SEARCH_USER_OFFSET));
		if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS)) tab.put(ObjectKeys.SEARCH_MAX_HITS, options.get(ObjectKeys.SEARCH_MAX_HITS));
		if (options.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) tab.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));
		if (options.containsKey(WebKeys.SEARCH_FORM_QUICKSEARCH)) tab.put(WebKeys.SEARCH_FORM_QUICKSEARCH, options.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		if (options.containsKey(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE)) tab.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, options.get(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE));
		
		return ((Integer)tab.get(TAB_ID)).intValue();
	}
	
	public Map getTab(int tabId) {
		List tabList = getTabList();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0 || tabNumber >= tabList.size()) return null;
		Map tab = (Map) tabList.get(tabNumber);
		return tab;
	}
	
	public void deleteTab(int tabId) {
		List tabList = getTabList();
		int currentTab = getCurrentTab();
		if (tabId != checkTabId(tabId)) return;
		int tabNum = findTabNumber(tabId);
		if (tabNum < 0 || tabNum >= tabList.size()) return;
		//Remove the tab
		tabList.remove(tabNum);
		//Make sure the current tab is adjusted to reflect the deletion
		if (currentTab == tabId) setCurrentTab(findLastRef());
	}
	
	public Map getTabs() {
		return this.tabs;
	}
	public List getTabList() {
		List tabList = (List) tabs.get(TABLIST);
		return tabList;
	}
	protected int addTab() {
		List tabList = (List) tabs.get(TABLIST);
		Integer nextTabId = (Integer) tabs.get(NEXT_TAB_ID);
		tabs.put(NEXT_TAB_ID, new Integer(nextTabId.intValue() + 1));
		Map tab = new HashMap();
		tab.put(TAB_ID, nextTabId);
		tabList.add(tab);
		return nextTabId.intValue();
	}
	public int getCurrentTab() {
		Integer currentTab = (Integer) tabs.get(CURRENT_TAB);
		if (currentTab == null) currentTab = new Integer(0);
		return checkTabId(currentTab.intValue());
	}
	public int setCurrentTab(int tabId) {
		int newTabId = checkTabId(tabId);
		tabs.put(CURRENT_TAB, newTabId);
		Integer nextRef = (Integer) tabs.get(NEXT_REF);
		tabs.put(NEXT_REF, new Integer(nextRef.intValue() + 1));
		Map tab = getTab(newTabId);
		tab.put(LAST_REF, new Integer(nextRef));
		return newTabId;
	}
	public String getTabType(int tabId) {
		Map tab = getTab(tabId);
		if (tab == null) return "";
		String tabType = (String) tab.get(TYPE);
		if (tabType == null) return "";
		return tabType;
	}
	
	protected int findTabNumber(int tabId) {
		tabId = checkTabId(tabId);
		List tabList = (List) tabs.get(TABLIST);
		if (tabList.size() == 0) return -1;
		int tabNumber = 0;
		if (tabList.size() > 0) {
			Map tab = (Map) tabList.get(0);
			//Assume the first tab if the id is not found
			if (tab.containsKey(TAB_ID)) tabNumber = ((Integer)tab.get(TAB_ID)).intValue();
		}
		for (int i = 0; i < tabList.size(); i++) {
			Map tab = (Map)tabList.get(i);
			if (tab.containsKey(TAB_ID) && 
					((Integer)tab.get(TAB_ID)).intValue() == tabId) {
				tabNumber = i;
				break;
			}
		}
		return tabNumber;
	}
	protected int checkTabId(int tabId) {
		List tabList = (List) tabs.get(TABLIST);
		int newTabId = findLastRef();
		if (tabList.size() > 0) {
			Map tab = (Map) tabList.get(0);
			//Assume the first tab if the id is not found
			if (tab.containsKey(TAB_ID)) newTabId = ((Integer)tab.get(TAB_ID)).intValue();
		}
		for (int i = 0; i < tabList.size(); i++) {
			Map tab = (Map)tabList.get(i);
			if (tab.containsKey(TAB_ID) && 
					((Integer)tab.get(TAB_ID)).intValue() == tabId) {
				newTabId = tabId;
				break;
			}
		}
		return newTabId;
	}
	protected int findLastRef() {
		List tabList = (List) tabs.get(TABLIST);
		int tabId = 0;
		int lastRef = -1;
		for (int i = 0; i < tabList.size(); i++) {
			Map tab = (Map)tabList.get(i);
			if (tab.containsKey(TAB_ID) && tab.containsKey(LAST_REF) && 
					((Integer)tab.get(LAST_REF)).intValue() > lastRef) {
				lastRef = ((Integer)tab.get(LAST_REF)).intValue();
				tabId = ((Integer)tab.get(TAB_ID)).intValue();
			}
		}
		return tabId;
	}
}
