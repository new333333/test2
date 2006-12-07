package com.sitescape.ef.web.util;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.util.AllBusinessServicesInjected;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;

public class BinderHelper {

	static public String getViewListingJsp(AllBusinessServicesInjected bs) {
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("")) {
			displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
		}
		String viewListingJspName;
		if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
			viewListingJspName = WebKeys.VIEW_LISTING_POPUP;
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			viewListingJspName = WebKeys.VIEW_LISTING_ACCESSIBLE;
		} else {
			viewListingJspName = WebKeys.VIEW_LISTING_VERTICAL;
		}
		return viewListingJspName;
	}

	//Routine to save a generic portal url used to build a url to a binder or entry 
	//  This routine is callable only from a portlet controller
	static public void setBinderPermaLink(AllBusinessServicesInjected bs, 
			RenderRequest request, RenderResponse response) {
		if (request.getWindowState().equals(WindowState.MAXIMIZED)) {
			User user = RequestContextHolder.getRequestContext().getUser();
			PortletURL url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
			url.setParameter(WebKeys.URL_NEW_TAB, WebKeys.URL_NEW_TAB_PLACE_HOLDER);
			if (!url.toString().equals(getBinderPermaLink(bs)))
				bs.getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_PERMALINK_URL, url.toString());
		}
	}
	//Routine to get a portal url that points to a binder or entry 
	//  This routine is callable from an adaptor controller
	static public String getBinderPermaLink(AllBusinessServicesInjected bs) {
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProperties = (UserProperties) bs.getProfileModule().getUserProperties(user.getId());
		String url = (String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_PERMALINK_URL);
		if (url == null) url = "";
		return url;
	}

	static public void buildNavigationLinkBeans(AllBusinessServicesInjected bs, Binder binder, Map model) {
		Binder parentBinder = binder;
    	Map navigationLinkMap;
    	if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
    		navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
    	else {
    		navigationLinkMap = new HashMap();
    		model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
    	}
    	while (parentBinder != null) {
	    	Document tree = null;
	    	if (parentBinder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
				tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
						new TreeBuilder(null, true, bs.getBinderModule()),0);
			} else if (parentBinder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.folder)) {
				tree = bs.getFolderModule().getDomFolderTree(parentBinder.getId(), new TreeBuilder(null, true, bs.getBinderModule()), 0);
			} else if (parentBinder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
				tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
						new TreeBuilder(null, true, bs.getBinderModule()),0);
			}
			navigationLinkMap.put(parentBinder.getId(), tree);
			parentBinder = ((Binder)parentBinder).getParentBinder();
		}
	}
	public static class TreeBuilder implements DomTreeBuilder {
		Binder bottom;
		boolean check;
		BinderModule binderModule;
		public TreeBuilder(Binder bottom, boolean checkChildren, BinderModule binderModule) {
			this.bottom = bottom;
			this.check = checkChildren;
			this.binderModule = binderModule;
		}
		public Element setupDomElement(String type, Object source, Element element) {
			Binder binder = (Binder) source;
			element.addAttribute("title", binder.getTitle());
			element.addAttribute("id", binder.getId().toString());

			//only need this information if this is the bottom of the tree
			if (check && (bottom == null ||  bottom.equals(binder.getParentBinder()))) {
				if (binderModule.hasBinders(binder)) {
					element.addAttribute("hasChildren", "true");
				} else {	
					element.addAttribute("hasChildren", "false");
				}
			}
			if (type.equals(DomTreeBuilder.TYPE_WORKSPACE)) {
				Workspace ws = (Workspace)source;
				String icon = ws.getIconName();
				String imageClass = "ss_twIcon";
				if (icon == null || icon.equals("")) {
					icon = "/icons/workspace.gif";
					imageClass = "ss_twImg";
				}
				element.addAttribute("type", "workspace");
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", imageClass);
				element.addAttribute("action", WebKeys.ACTION_VIEW_WS_LISTING);
			} else if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				String icon = f.getIconName();
				if (icon == null || icon.equals("")) icon = "/icons/folder.png";
				element.addAttribute("type", "folder");
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", "ss_twIcon");
				element.addAttribute("action", WebKeys.ACTION_VIEW_FOLDER_LISTING);
			} else return null;
			return element;
		}
	}	

   	public static SortField[] getBinderEntries_getSortFields(Map options) {
   		SortField[] fields = new SortField[1];
   		String sortBy = EntityIndexUtils.MODIFICATION_DATE_FIELD;
    	if (options.containsKey(ObjectKeys.SEARCH_SORT_BY)) 
    		sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
   		
    	boolean descend = true;
    	if (options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) 
    		descend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
    	
    	fields[0] = new SortField(sortBy, descend);
    	return fields;
   	}
    public static Map getCommonEntryElements() {
    	Map entryElements = new HashMap();
    	Map itemData;
    	//Build a map of common elements for use in search filters
    	//  Each map has a "type" and a "caption". Types can be: title, text, user_list, or date.
    	
    	//title
    	itemData = new HashMap();
    	itemData.put("type", "title");
    	itemData.put("caption", NLT.get("filter.title"));
    	entryElements.put("title", itemData);
    	
    	//author
    	itemData = new HashMap();
    	itemData.put("type", "user_list");
    	itemData.put("caption", NLT.get("filter.author"));
    	entryElements.put("owner", itemData);
    	
    	//creation date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.creationDate"));
    	entryElements.put("creation", itemData);
    	
    	//modification date
    	itemData = new HashMap();
    	itemData.put("type", "date");
    	itemData.put("caption", NLT.get("filter.modificationDate"));
    	entryElements.put("modification", itemData);
    	
    	return entryElements;
    }
       	

}
