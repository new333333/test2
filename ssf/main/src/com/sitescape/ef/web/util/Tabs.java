package com.sitescape.ef.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
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
   	
   	//Tab map keys
   	public final static String TYPE = "type";
   	public final static String TAB_ID = "tabId";
   	public final static String BINDER_ID = "binderId";
   	public final static String ENTRY_ID = "entryId";
   	public final static String QUERY_DOC = "query_doc";
   	public final static String TITLE = "title";
   	public final static String ICON = "icon";
   	public final static String PAGE = "page";
   	
   	//Type keys
   	public final static String WORKSPACE = "workspace";
   	public final static String BINDER = "binder";
   	public final static String PROFILES = "profiles";
   	public final static String ENTRY = "entry";
   	public final static String USER = "user";
   	public final static String QUERY = "query";
	
	public Tabs(PortletRequest request) {
		PortletSession ps = WebHelper.getRequiredPortletSession(request);
		tabs = (Map) ps.getAttribute(WebKeys.TABS, PortletSession.APPLICATION_SCOPE);
		if (tabs == null) {
			tabs = new HashMap();
			tabs.put(TABLIST, new ArrayList());
			tabs.put(CURRENT_TAB, new Integer(0));
			tabs.put(NEXT_TAB_ID, new Integer(0));
			ps.setAttribute(WebKeys.TABS, tabs, PortletSession.APPLICATION_SCOPE);
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
	
	//Entry tab
	public int addTab(Entry entry) {
		Map options = new HashMap();
		return addTab(entry, options);
	}
	public int addTab(Entry entry, Map options) {
		int tabId = addTab();
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
	
	public int setTab(int tabId, Binder binder) {
		return setTab(tabId, binder, new HashMap());
	}
	public int setTab(Binder binder) {
		return setTab(getCurrentTab(), binder, new HashMap());
	}
	public int setTab(Binder binder, Map options) {
		return setTab(getCurrentTab(), binder, options);
	}
	public int setTab(int tabId, Binder binder, Map options) {
		List tabList = getTabList();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0) tabNumber = findTabNumber(addTab());
		Map tab = (Map) tabList.get(tabNumber);
		Integer page = (Integer) tab.get(PAGE);
		if (page == null) page = new Integer(0);
		if (options.containsKey(PAGE)) page = (Integer) options.get(PAGE);
    	if (binder.getEntityIdentifier().getEntityType().
    			equals(EntityIdentifier.EntityType.workspace)) {
    		tab.put(TYPE, WORKSPACE);
    	} else if (binder.getEntityIdentifier().getEntityType().
    			equals(EntityIdentifier.EntityType.folder)) {
    		tab.put(TYPE, BINDER);
    	} else if (binder.getEntityIdentifier().getEntityType().
    			equals(EntityIdentifier.EntityType.profiles)) {
    		tab.put(TYPE, PROFILES);
    	}
		tab.put(BINDER_ID, binder.getId());
		tab.put(PAGE, page);
		tab.put(TITLE, binder.getTitle());
		tab.put(ICON, binder.getIconName());
		tab.remove(ENTRY_ID);
		tab.remove(QUERY_DOC);

		return ((Integer)tab.get(TAB_ID)).intValue();
	}
	
	public int setTab(int tabId, Entry entry) {
		return setTab(tabId, entry, new HashMap());
	}
	public int setTab(Entry entry) {
		return setTab(getCurrentTab(), entry, new HashMap());
	}
	public int setTab(Entry entry, Map options) {
		return setTab(getCurrentTab(), entry, options);
	}
	public int setTab(int tabId, Entry entry, Map options) {
		List tabList = getTabList();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0) tabNumber = findTabNumber(addTab());
		Map tab = (Map) tabList.get(tabNumber);
    	if (entry.getEntityIdentifier().getEntityType().
    			equals(EntityIdentifier.EntityType.folderEntry)) {
    		tab.put(TYPE, ENTRY);
    	} else if (entry.getEntityIdentifier().getEntityType().
    			equals(EntityIdentifier.EntityType.user)) {
    		tab.put(TYPE, USER);
    	}
		tab.put(TYPE, ENTRY);
		tab.put(BINDER_ID, entry.getParentBinder().getId());
		tab.put(ENTRY_ID, entry.getId());
		tab.put(TITLE, entry.getTitle());
		tab.put(ICON, entry.getIconName());
		tab.remove(PAGE);
		tab.remove(QUERY_DOC);
		
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
	public int setTab(int tabId, Document query, Map options) {
		List tabList = getTabList();
		int tabNumber = findTabNumber(tabId);
		if (tabNumber < 0) tabNumber = findTabNumber(addTab());
		Map tab = (Map) tabList.get(tabNumber);
		Integer page = (Integer) tab.get(PAGE);
		if (page == null) page = new Integer(0);
		if (options.containsKey(PAGE)) page = (Integer) options.get(PAGE);
		tab.put(TYPE, QUERY);
		tab.put(QUERY_DOC, query);
		tab.put(PAGE, page);
		if (options.containsKey(TITLE)) {
			tab.put(TITLE, new String((String) options.get(TITLE)));
		} else {
			tab.put(TITLE, NLT.get("tabs.search"));
		}
		tab.put(ICON, "pics/sym_s_search.gif");
		tab.remove(BINDER_ID);
		tab.remove(ENTRY_ID);

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
		int tabNum = findTabNumber(tabId);
		if (tabNum < 0 || tabNum >= tabList.size()) return;
		//Make sure the current tab is adjusted to reflect the deletion
		int currentTab = getCurrentTab();
		if (currentTab >= tabNum) setCurrentTab(--currentTab);
		//Remove the tab
		tabList.remove(tabNum);
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
		int newTabId = 0;
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
}
