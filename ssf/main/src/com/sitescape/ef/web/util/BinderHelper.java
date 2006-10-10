package com.sitescape.ef.web.util;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.portlet.forum.ListFolderController.TreeBuilder;
import com.sitescape.ef.portlet.workspaceTree.WorkspaceTreeController.WsTreeBuilder;
import com.sitescape.ef.util.AllBusinessServicesInjected;
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
		User user = RequestContextHolder.getRequestContext().getUser();
		PortletURL url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.URL_ACTION_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTITY_TYPE, WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_BINDER_ID, WebKeys.URL_BINDER_ID_PLACE_HOLDER);
		url.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
		if (!url.toString().equals(getBinderPermaLink(bs)))
			bs.getProfileModule().setUserProperty(user.getId(), 
					ObjectKeys.USER_PROPERTY_PERMALINK_URL, url.toString());
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
		while (parentBinder != null) {
	    	Document tree = null;
	    	Map navigationLinkMap = new HashMap();
	    	if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
	    		navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
	    	if (parentBinder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
				tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
						new WsTreeBuilder((Workspace)parentBinder, true, bs.getBinderModule()),1);
			} else if (parentBinder.getEntityIdentifier().getEntityType().equals(EntityIdentifier.EntityType.folder)) {
				tree = bs.getFolderModule().getDomFolderTree(parentBinder.getId(), new TreeBuilder());
			}
			navigationLinkMap.put(parentBinder.getId(), tree);
			model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
			parentBinder = ((Binder)parentBinder).getParentBinder();
		}
	}
}
