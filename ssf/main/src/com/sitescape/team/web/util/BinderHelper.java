package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.DomTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.domain.Definition;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

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

	//The getViewListingJSP function has been overloaded, to check if the displayDefinition is of type
	//search. For the 'search' display defintion, we should not have the display at bottom (vertical)
	//option. So when a user chooses display at bottom option, we will be showing the user a overlay display
	//Along with 'search', we have added 'blog' and 'guestbook' to above check 
	static public String getViewListingJsp(AllBusinessServicesInjected bs, String displayDefinition) {
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
		} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
			if (displayDefinition != null && (displayDefinition.equalsIgnoreCase(Definition.VIEW_STYLE_SEARCH) 
					|| displayDefinition.equalsIgnoreCase(ObjectKeys.SEARCH_RESULTS_DISPLAY)
					|| displayDefinition.equalsIgnoreCase(Definition.VIEW_STYLE_BLOG)
					|| displayDefinition.equalsIgnoreCase(Definition.VIEW_STYLE_GUESTBOOK))) {
				viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
			} else {
				viewListingJspName = WebKeys.VIEW_LISTING_VERTICAL;
			}
		} else {
			viewListingJspName = WebKeys.VIEW_LISTING_IFRAME;
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
	
	static public void getBinderAccessibleUrl(AllBusinessServicesInjected bs, Long binderId, Long entryId,
			RenderRequest request, RenderResponse response, Map model) {
		Binder binder = bs.getBinderModule().getBinder(binderId);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		String displayStyle = user.getDisplayStyle();
		if (displayStyle == null || displayStyle.equals("")) {
			displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
		}
		model.put(WebKeys.DISPLAY_STYLE, displayStyle);
		
		PortletURL url = response.createActionURL();
		if (binder instanceof Workspace) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		else if (binder instanceof Folder) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		else if (binder instanceof Folder) url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (entryId != null) url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		} else {
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		}
		model.put(WebKeys.ACCESSIBLE_URL, url.toString());
}

	static public Map getAccessControlEntityMapBean(Map model, DefinableEntity entity) {
		//Initialize the acl bean
		if (!model.containsKey(WebKeys.ACCESS_CONTROL_MAP)) 
			model.put(WebKeys.ACCESS_CONTROL_MAP, new HashMap());
		Map accessControlMap = (Map)model.get(WebKeys.ACCESS_CONTROL_MAP);
		if (!accessControlMap.containsKey(entity.getId())) 
			accessControlMap.put(entity.getId(), new HashMap());
		return (Map)accessControlMap.get(entity.getId());
	}
	static public void buildNavigationLinkBeans(AllBusinessServicesInjected bs, Binder binder, Map model) {
		if (binder instanceof TemplateBinder)
			buildNavigationLinkBeans(bs, (TemplateBinder)binder, model, new ConfigHelper(""));
		else
			buildNavigationLinkBeans(bs, binder, model, null);
	}
	static public void buildNavigationLinkBeans(AllBusinessServicesInjected bs, Binder binder, Map model, DomTreeHelper helper) {
		if (binder instanceof TemplateBinder) {
			buildNavigationLinkBeans(bs, (TemplateBinder)binder, model, helper);
		} else {
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
				if (!bs.getBinderModule().testAccess(parentBinder.getId(), 
						WorkAreaOperation.READ_ENTRIES.toString()))
					break;
				if (parentBinder.getEntityType().equals(EntityIdentifier.EntityType.workspace)) {
					tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
						new WsDomTreeBuilder(null, true, bs, helper),0);
				} else if (parentBinder.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
					tree = bs.getFolderModule().getDomFolderTree(parentBinder.getId(), new WsDomTreeBuilder(null, true, bs, helper), 0);
				} else if (parentBinder.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
					tree = bs.getWorkspaceModule().getDomWorkspaceTree(parentBinder.getId(), 
							new WsDomTreeBuilder(null, true, bs, helper),0);
				}
				navigationLinkMap.put(parentBinder.getId(), tree);
				parentBinder = ((Binder)parentBinder).getParentBinder();
			}
		}
	}

	static public void buildNavigationLinkBeans(AllBusinessServicesInjected bs, TemplateBinder config, Map model, DomTreeHelper helper) {
		TemplateBinder parentConfig = config;
		Map navigationLinkMap;
		if (model.containsKey(WebKeys.NAVIGATION_LINK_TREE)) 
			navigationLinkMap = (Map)model.get(WebKeys.NAVIGATION_LINK_TREE);
		else {
			navigationLinkMap = new HashMap();
			model.put(WebKeys.NAVIGATION_LINK_TREE, navigationLinkMap);
		}
    	while (parentConfig != null) {
        	Document tree = buildTemplateTreeRoot(bs, parentConfig, model, helper);
 			navigationLinkMap.put(parentConfig.getId(), tree);
			parentConfig = (TemplateBinder)parentConfig.getParentBinder();
		}
	}
	//trees should not be deep - do entire thing
	static public Document buildTemplateTreeRoot(AllBusinessServicesInjected bs, TemplateBinder config, Map model, DomTreeHelper helper) {
       	Document tree = DocumentHelper.createDocument();
    	Element element = tree.addElement(DomTreeBuilder.NODE_ROOT);
    	//only need this information if this is the bottom of the tree
    	buildTemplateChildren(element, config, helper);
    	return tree;
	}
	//trees should not be deep - do entire thing
	static public Document buildTemplateTreeRoot(AllBusinessServicesInjected bs, List configs, DomTreeHelper helper) {
       	Document tree = DocumentHelper.createDocument();
    	Element element = tree.addElement(DomTreeBuilder.NODE_ROOT);
	   	element.addAttribute("title", NLT.get("administration.configure_cfg"));
    	element.addAttribute("displayOnly", "true");
    	if (!configs.isEmpty()) {
			element.addAttribute("hasChildren", "true");
			for (int i=0; i<configs.size(); ++i) {
				TemplateBinder child = (TemplateBinder)configs.get(i);
    			Element cElement = element.addElement(DomTreeBuilder.NODE_CHILD);
    			buildTemplateChildren(cElement, child, helper);
    		}
    	} else 	element.addAttribute("hasChildren", "false");

    	return tree;
	}
	static void buildTemplateChildren(Element element, TemplateBinder config, DomTreeHelper helper) {
		buildTemplateElement(element, config, helper);
    	List<TemplateBinder> children = config.getBinders();
    	for (TemplateBinder child: children) {
    		Element cElement = element.addElement(DomTreeBuilder.NODE_CHILD);
    		buildTemplateChildren(cElement, child, helper);
    	}
	}
	static void buildTemplateElement(Element element, TemplateBinder config, DomTreeHelper helper) {
	   	element.addAttribute("title", NLT.get(config.getTitle()));
    	element.addAttribute("id", config.getId().toString());
 		
    	if (!config.getBinders().isEmpty()) {
			element.addAttribute("hasChildren", "true");
		} else
			element.addAttribute("hasChildren", "false");
			
		if (config.getEntityType().equals(EntityType.workspace)) {
			String icon = config.getIconName();
			String imageClass = "ss_twIcon";
			if (icon == null || icon.equals("")) {
				icon = "/icons/workspace.gif";
				imageClass = "ss_twImg";
			}
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_WORKSPACE);
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", imageClass);
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_WORKSPACE);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_TEMPLATE, config));
					
		} else {
			String icon = config.getIconName();
			if (icon == null || icon.equals("")) icon = "/icons/folder.png";
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", "ss_twIcon");
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_FOLDER);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_TEMPLATE, config));
		} 
		
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
    	// entryElements.put(EntityIndexUtils.CREATORID_FIELD, itemData);
    	// entryElements.put(EntityIndexUtils.CREATOR_NAME_FIELD, itemData);
    	entryElements.put(EntityIndexUtils.CREATOR_TITLE_FIELD, itemData);
    	
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
       	
	// This method reads thru the results from a search, finds the tags, 
	// and places them into an array in a alphabetic order.
	public static List sortCommunityTags(List entries) {
		return sortCommunityTags(entries, "");
	}
	public static List sortCommunityTags(List entries, String wordRoot) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principals.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("\\s");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j];

		    	if (strTag.equals("")) continue;
		    	
		    	//See if this must match a specific word root
		    	if (!wordRoot.equals("") && !strTag.toLowerCase().startsWith(wordRoot.toLowerCase())) continue;
		    	
		    	Integer tagCount = (Integer) tagMap.get(strTag);
		    	if (tagCount == null) {
		    		tagMap.put(strTag, new Integer(1));
		    	}
		    	else {
		    		int intTagCount = tagCount.intValue();
		    		tagMap.put(strTag, new Integer(intTagCount+1));
		    	}
		    }
		}
		
		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}
	
	//This method rates the community tags
	public static List rateCommunityTags(List entries, int intMaxHits) {
		//Same rating algorithm is used for both community and personal tags
		return rateTags(entries, intMaxHits);
	}
	
	//This method identifies if we need a + or - sign infront of the
	//tags being displayed in the tags tab in the search tab
	public static List determineSignBeforeTag(List entries, String tabTagTitle) {
		ArrayList tagList = new ArrayList();
		for (int i = 0; i < entries.size(); i++) {
			String strTabTitle = tabTagTitle;
			Map tag = (Map) entries.get(i);
			String strTagName = (String) tag.get(WebKeys.TAG_NAME);
			if (strTabTitle != null && !strTabTitle.equals("")) {
				if ( (strTabTitle.indexOf(strTagName+ " ") != -1) || (strTabTitle.indexOf(" " + strTagName) != -1) ) {
					tag.put(WebKeys.TAG_SIGN, "-");
					
					int intFirstIndex = strTabTitle.indexOf(strTagName+ " ");
					int intFirstLength = (strTagName+ " ").length();
					
					if (intFirstIndex != -1) {
						String strFirstPart = "";
						String strLastPart = "";
						
						if (intFirstIndex != 0) {
							strFirstPart = strTabTitle.substring(0, (intFirstIndex));
						}
						if ( strTabTitle.length() !=  (intFirstIndex+1+intFirstLength) ) {
							strLastPart = strTabTitle.substring(intFirstIndex+intFirstLength, strTabTitle.length());
						}
						strTabTitle = strFirstPart + strLastPart;
					}
					
					int intLastIndex = strTabTitle.indexOf(" " + strTagName);
					int intLastLength = (" " + strTagName).length();

					if (intLastIndex != -1) {
						String strFirstPart = "";
						String strLastPart = "";
						
						if (intLastIndex != 0) {
							strFirstPart = strTabTitle.substring(0, (intLastIndex));
						}
						if ( strTabTitle.length() !=  (intLastIndex+intLastLength) ) {
							strLastPart = strTabTitle.substring(intLastIndex+intLastLength, strTabTitle.length());
						}
						strTabTitle = strFirstPart + strLastPart;
					}
					tag.put(WebKeys.TAG_SEARCH_TEXT, strTabTitle);					
				}
				else if (strTabTitle.equals(strTagName)) {
					tag.put(WebKeys.TAG_SIGN, "-");
					tag.put(WebKeys.TAG_SEARCH_TEXT, "");
				}
				else {
					tag.put(WebKeys.TAG_SIGN, "+");
					tag.put(WebKeys.TAG_SEARCH_TEXT, strTabTitle + " " + strTagName);
				}
			}
			else {
				tag.put(WebKeys.TAG_SIGN, "+");
				tag.put(WebKeys.TAG_SEARCH_TEXT, strTagName);
			}
			tagList.add(tag);
		}
		return tagList;
	}

	// This method reads thru the results from a search, finds the personal tags, 
	// and places them into an array in a alphabetic order.
	public static List sortPersonalTags(List entries) {
		HashMap tagMap = new HashMap();
		ArrayList tagList = new ArrayList();
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String strTags = (String)entry.get(WebKeys.SEARCH_ACL_TAG_ID);
			if (strTags == null || "".equals(strTags)) continue;
			
		    String [] strTagArray = strTags.split("ACL");
		    for (int j = 0; j < strTagArray.length; j++) {
		    	String strTag = strTagArray[j].trim();
		    	if (strTag.equals("")) continue;
		    	
		    	String strFirstSixChars = "";
		    	if (strTag.length() >= 6) {
		    		strFirstSixChars = strTag.substring(0, 6);
		    	}
		    	//Ignore these entries as they refer to community entries.
		    	if (strFirstSixChars.equals("allTAG")) continue;

		    	User user = RequestContextHolder.getRequestContext().getUser();
		    	long userId = user.getId();
		    	
		    	String strUserIdTag = userId + "TAG";
		    	String strValueToCompare = strTag.substring(0, strUserIdTag.length());
		    	
		    	//We are going to get only the personal tags relating to the user
		    	if (strValueToCompare.equals(strUserIdTag)) {
		    		String strTagValues = strTag.substring(strUserIdTag.length());
				    String [] strIntTagArray = strTagValues.split("\\s");
				    for (int k = 0; k < strIntTagArray.length; k++) {
				    	String strIntTag = strIntTagArray[k].trim();
				    	if (strIntTag.equals("")) continue;
				    	
				    	Integer tagCount = (Integer) tagMap.get(strIntTag);
				    	if (tagCount == null) {
				    		tagMap.put(strIntTag, new Integer(1));
				    	} else {
				    		int intTagCount = tagCount.intValue();
				    		tagMap.put(strIntTag, new Integer(intTagCount+1));
				    	}
				    }
		    	}
		    	else continue;
		    }
		}

		//sort the tags string
		Collection collection = tagMap.keySet();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap tags = new HashMap();
			String strTag = (String) array[j];
			tags.put(WebKeys.TAG_NAME, strTag);
			tags.put(WebKeys.SEARCH_RESULTS_COUNT, (Integer) tagMap.get(strTag));
			tagList.add(tags);
		}
		return tagList;
	}

	//This method rates the personal tags
	public static List ratePersonalTags(List entries, int intMaxHits) {
		//Same rating algorithm is used for both community and personal tags
		return rateTags(entries, intMaxHits);
	}	

	//This method provides ratings for the tags
	public static List rateTags(List entries, int intMaxHits) {
		ArrayList ratedList = new ArrayList();
		int intMaxHitsPerFolder = intMaxHits;
		/*
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (resultCount.intValue() > intMaxHitsPerFolder) {
				intMaxHitsPerFolder = resultCount.intValue();
			}
		}
		*/
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			int intResultCount = resultCount.intValue();
			Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
			int intRatingForFolder = DblRatingForFolder.intValue();
			tag.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
			if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_largerprint");
			}
			else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_largeprint");
			}
			else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_normalprint");
			}
			else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_smallprint");
			}
			else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
				tag.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_fineprint");
			}
			ratedList.add(tag);
		}
	
		return ratedList;		
	}
	
	public static int getMaxHitsPerTag(List entries) {
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map tag = (Map) entries.get(i);
			Integer resultCount = (Integer) tag.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (resultCount.intValue() > intMaxHitsPerFolder) {
				intMaxHitsPerFolder = resultCount.intValue();
			}
		}
		return intMaxHitsPerFolder;
	}
	
	public static void buildAccessControlTableBeans(RenderRequest request, RenderResponse response, 
			Binder binder, List functions, List membership, Map model, boolean ignoreFormData) {
		Map formData = request.getParameterMap();

		Set newRoleIds = new HashSet();
		String[] roleIds = new String[0];
		String[] principalIds = new String[0];
		String[] principalId = new String[0];
		
		Map functionMap = new HashMap();
		Map allowedFunctions = new HashMap();
		Map sortedGroupsMap = new TreeMap();
		Map sortedUsersMap = new TreeMap();

		String[] btnClicked = new String[] {""};
 		if (formData.containsKey("btnClicked")) btnClicked = (String[])formData.get("btnClicked");
		if (!ignoreFormData && (formData.containsKey("addRoleBtn") || 
				btnClicked[0].equals("addPrincipal") || btnClicked[0].equals("addRole"))) {
			if (formData.containsKey("roleIds")) {
				roleIds = (String[]) formData.get("roleIds");
				for (int i = 0; i < roleIds.length; i++) {
					if (!roleIds[i].equals("")) newRoleIds.add(Long.valueOf(roleIds[i]));
				}
			}
			if (formData.containsKey("roleIdToAdd")) {
				roleIds = (String[]) formData.get("roleIdToAdd");
				for (int i = 0; i < roleIds.length; i++) {
					if (!roleIds[i].equals("") && !newRoleIds.contains(Long.valueOf(roleIds[i]))) 
						newRoleIds.add(Long.valueOf(roleIds[i]));
				}
			}
			if (formData.containsKey("principalId")) {
				principalId = (String[]) formData.get("principalId");
			}

			if (formData.containsKey("principalIds")) {
				principalIds = (String[]) formData.get("principalIds");
			}

			//Get the role and user data from the form
			Map roleMembers = new HashMap();
			membership = new ArrayList();
						
			for (int i = 0; i < principalId.length; i++) {
				if (!principalId[i].equals("")) {
					Long id = Long.valueOf(principalId[i]);
					if (!membership.contains(id)) membership.add(id);
				}
			}			
						
			for (int i = 0; i < principalIds.length; i++) {
				if (!principalIds[i].equals("")) {
					Long id = Long.valueOf(principalIds[i]);
					if (!membership.contains(id)) membership.add(id);
				}
			}
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry)itFormData.next();
				String key = (String)me.getKey();
				if (key.length() >= 8 && key.substring(0,7).equals("role_id")) {
					String[] s_roleId = key.substring(7).split("_");
					if (s_roleId.length == 2) {
						Long roleId = Long.valueOf(s_roleId[0]);
						Long memberId;
						if (s_roleId[1].equals("owner")) {
							memberId = Long.valueOf("-1");
						} else {
							memberId = Long.valueOf(s_roleId[1]);
						}
						if (!roleMembers.containsKey(roleId)) roleMembers.put(roleId, new ArrayList());
						List members = (List)roleMembers.get(roleId);
						if (!members.contains(memberId)) members.add(memberId);
						if (!membership.contains(memberId)) membership.add(memberId);
					}
				}
			}
			Collection ids = ResolveIds.getPrincipals(membership);
			Map principalMap = new HashMap();
    		for (Iterator iter=ids.iterator();iter.hasNext();) {
	    		Principal p = (Principal)iter.next();
				principalMap.put(p.getId(), p);
			}

			//Build the basic map structure
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map groups = new HashMap();
				Map users = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				
				//Populate the map with data from the form instead of getting it from the database
				List members = (List)roleMembers.get(f.getId());
				if (members != null) {
					for (Iterator iter = members.iterator();iter.hasNext();) {
						Long pId = (Long)iter.next();
						if (pId.equals(Long.valueOf("-1"))) {
							//The owner has this right
							pMap.put(WebKeys.OWNER, pId);
						} else {
							Principal p = (Principal)principalMap.get(pId);
							if (p instanceof Group) {
								groups.put(p.getId(), p);
							} else {
								users.put(p.getId(), p);
							}
						}
					}
				}
			}
			//Populate the sorted users and groups maps 
			for (Iterator iter = membership.iterator();iter.hasNext();) {
				Long pId = (Long)iter.next();
				Principal p = (Principal)principalMap.get(pId);
				if (p != null && p instanceof Group) {
					sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p != null && p instanceof User) {
					sortedUsersMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				}
			}

		} else {
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map groups = new HashMap();
				Map users = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				for (int j=0; j<membership.size(); ++j) {
					WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)membership.get(j);
					if (f.getId().equals(m.getFunctionId())) {
						if (m.getMemberIds().contains(Long.valueOf("-1"))) 
							pMap.put(WebKeys.OWNER, Long.valueOf("-1"));
						Collection ids = ResolveIds.getPrincipals(m.getMemberIds());
						for (Iterator iter=ids.iterator(); iter.hasNext();) {
							Principal p = (Principal)iter.next();
							if (p != null && p instanceof Group) {
								groups.put(p.getId(), p);
								sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p != null && p instanceof User) {
								users.put(p.getId(), p);
								sortedUsersMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							}
						}
						break;
					}
				}
			}
		}
		
		//Build a sorted list of functions
		List sortedFunctions = new ArrayList();
		Map sortedFunctionsMap = new TreeMap();
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			Map pMap = (Map)functionMap.get(f);
			Map users = (Map)pMap.get(WebKeys.USERS);
			Map groups = (Map)pMap.get(WebKeys.GROUPS);
			if (users.size() > 0 || groups.size() > 0 || pMap.containsKey(WebKeys.OWNER) || 
					newRoleIds.contains(f.getId())) {
				//This function has some membership; add it to the sorted list
				sortedFunctionsMap.put(f.getName().toLowerCase() + f.getId().toString(), f);
			}
		}
		Iterator itFunctions = sortedFunctionsMap.keySet().iterator();
		while (itFunctions.hasNext()) {
			sortedFunctions.add(sortedFunctionsMap.get((String) itFunctions.next()));
		}
		//Build the sorted lists of users and groups
		List sortedGroups = new ArrayList();
		Iterator itGroups = sortedGroupsMap.keySet().iterator();
		while (itGroups.hasNext()) {
			sortedGroups.add(sortedGroupsMap.get((String) itGroups.next()));
		}

		List sortedUsers = new ArrayList();
		Iterator itUsers = sortedUsersMap.keySet().iterator();
		while (itUsers.hasNext()) {
			sortedUsers.add(sortedUsersMap.get((String) itUsers.next()));
		}
		
		//Build list of allowed roles
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			allowedFunctions.put(f.getId(), f);
		}
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FUNCTION_MAP, functionMap);
		model.put(WebKeys.FUNCTIONS_ALLOWED, allowedFunctions);
		model.put(WebKeys.ACCESS_SORTED_FUNCTIONS, sortedFunctions);
		model.put(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP, sortedFunctionsMap);
		model.put(WebKeys.ACCESS_FUNCTIONS_COUNT, Integer.valueOf(functionMap.size()));
		model.put(WebKeys.ACCESS_SORTED_USERS_MAP, sortedUsersMap);
		model.put(WebKeys.ACCESS_SORTED_USERS, sortedUsers);
		model.put(WebKeys.ACCESS_USERS_COUNT, Integer.valueOf(sortedUsers.size()));
		model.put(WebKeys.ACCESS_SORTED_GROUPS_MAP, sortedGroupsMap);
		model.put(WebKeys.ACCESS_SORTED_GROUPS, sortedGroups);
		model.put(WebKeys.ACCESS_GROUPS_COUNT, Integer.valueOf(sortedGroups.size()));
	}
	
	public static void mergeAccessControlTableBeans(Map model) {
		List sortedFunctions = (List)model.get(WebKeys.ACCESS_SORTED_FUNCTIONS);
		Map sortedFunctionsMap = (Map)model.get(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP);
		List sortedGroups = (List)model.get(WebKeys.ACCESS_SORTED_GROUPS);
		List sortedUsers = (List)model.get(WebKeys.ACCESS_SORTED_USERS);
		Map sortedGroupsMap = (Map)model.get(WebKeys.ACCESS_SORTED_GROUPS_MAP);
		Map sortedUsersMap = (Map)model.get(WebKeys.ACCESS_SORTED_USERS_MAP);
		
		Map parentModel = (Map)model.get(WebKeys.ACCESS_PARENT);
		Map parentSortedFunctionsMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP);
		Map parentSortedGroupsMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_GROUPS_MAP);
		Map parentSortedUsersMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_USERS_MAP);
		
		for (Iterator i = parentSortedFunctionsMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedFunctionsMap.put(me.getKey(), me.getValue());
		}
		Iterator itFunctions = sortedFunctionsMap.keySet().iterator();
		while (itFunctions.hasNext()) {
			Function f = (Function)sortedFunctionsMap.get((String) itFunctions.next());
			if (!sortedFunctions.contains(f)) sortedFunctions.add(f);
		}

		for (Iterator i = parentSortedGroupsMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedGroupsMap.put(me.getKey(), me.getValue());
		}

		for (Iterator i = parentSortedUsersMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedUsersMap.put(me.getKey(), me.getValue());
		}

		//Merge the sorted lists of users and groups
		Iterator itGroups = sortedGroupsMap.keySet().iterator();
		while (itGroups.hasNext()) {
			Principal p = (Principal)sortedGroupsMap.get((String) itGroups.next());
			if (!sortedGroups.contains(p)) sortedGroups.add(p);
		}
		Iterator itUsers = sortedUsersMap.keySet().iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal)sortedUsersMap.get((String) itUsers.next());
			if (!sortedUsers.contains(p)) sortedUsers.add(p);
		}

		model.put(WebKeys.ACCESS_USERS_COUNT, Integer.valueOf(sortedUsers.size()));
		model.put(WebKeys.ACCESS_GROUPS_COUNT, Integer.valueOf(sortedGroups.size()));
	}
	public static class ConfigHelper implements DomTreeHelper {
		String action;
		public ConfigHelper(String action) {
			this.action = action;
		}
		public boolean supportsType(int type, Object source) {
			if (type == DomTreeBuilder.TYPE_TEMPLATE) {return true;}
			return false;
		}
		public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
			TemplateBinder config = (TemplateBinder)source;
			return !config.getBinders().isEmpty();
		}
	
		public String getAction(int type, Object source) {
			return action;
		}
		public String getURL(int type, Object source) {return null;}
		public String getDisplayOnly(int type, Object source) {
			return "false";
		}
		public String getTreeNameKey() {return null;}
		
	}
	public static void buildAccessControlRoleBeans(AllBusinessServicesInjected bs, Map model) {
		//Add the list of existing functions for this zone
		model.put(WebKeys.FUNCTIONS, bs.getAdminModule().getFunctions());
		
		//Add the list of workAreaOperations that can be added to each function
		Map operations = new HashMap();
		Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
		while (itWorkAreaOperations.hasNext()) {
			String operationName = (String) ((WorkAreaOperation) itWorkAreaOperations.next()).toString();
			operations.put(operationName, NLT.get("workarea_operation." + operationName));
		}
		model.put(WebKeys.WORKAREA_OPERATIONS, operations);
	}
	
	// Walk the list of entries returned by the search engine.  If
	// an entry has doctype=attachment then see if it's entry is
	// already in the list, and add this attachment to it's map.
	// if not, see if there is an attachment for the same entry
	// already in the list.  If so, add this as an attachment to that 
	// attachment.
	//
	// Note that if an entry was on this list, then a new key/value pair of
	// (WebKeys.ENTRY_HAS_META_HIT, true) will be added to it's map.
	//
	// if attachments are found for an entry, then they will be taken off
	// the entry list, and added to the list of attachments for that entry
	// in the list. (key value - WebKeys.ENTRY_ATTACHMENTS
	//
	// if attachments are found, but not the entry they're associated
	// with, then leave an attachment on the list, and if there are 
	// mulitple attachments for the same entry, then the attachment
	// will contain a map entry (WebKeys.ENTRY_ATTACHMENTS), which
	// contains all the attachments for that entry.
	public static List filterEntryAttachmentResults(List entries) {

		for (int count = 0; count < entries.size(); count++) {
			Map entry = (Map)entries.get(count);
			String type = (String)entry.get(BasicIndexUtils.DOC_TYPE_FIELD);
			// if it's an entry, see if there's already an attachment in the list for it.
			String docId = (String)entry.get(EntityIndexUtils.DOCID_FIELD);
			String entityType = (String)entry.get(EntityIndexUtils.ENTITY_FIELD);
			if (type.equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ENTRY)) {
				int i = 0;
				for (i=0; i < count; i++) {
					String d = (String)((Map)entries.get(i)).get(EntityIndexUtils.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(EntityIndexUtils.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then it's an attachment, 
						// so insert ourselves in here, add the attachment to 
						// the entry, and delete this attachment from the list.
						Map att = (Map)entries.get(i);
						// see if the attachment has other attachments added to it.
						// if it does, then add those to this entry.
						List attachments = (List)att.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments != null) 
							entry.put(WebKeys.ENTRY_ATTACHMENTS, attachments);
						else
							entry.put(WebKeys.ENTRY_ATTACHMENTS, att);
						entry.put(WebKeys.ENTRY_HAS_META_HIT, true);
						entries.remove(i);
						count--;
					}
				}
				if (i == count || count == 1) {
					entry.put(WebKeys.ENTRY_HAS_META_HIT, true);
				}
			} else if (type.equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ATTACHMENT)) {

				for (int i = 0; i < count; i++) {
					String d = (String) ((Map) entries.get(i)).get(EntityIndexUtils.DOCID_FIELD);
					String e = (String)((Map)entries.get(i)).get(EntityIndexUtils.ENTITY_FIELD);
					if (d.equalsIgnoreCase(docId) && e.equalsIgnoreCase(entityType)) {
						// if it's already in the list, then check if it's an
						// entry. If it is an entry, then add this attachment to
						// the entry, and delete this attachment from the list.
						// if it's an attachment, then if the attachment already
						// has an attachments map, add this to it. Otherwise,
						// create the attachments map, and add the attachment, and this
						// entry to it.
						Map ent = (Map) entries.get(i);
						String typ = (String) ent.get(BasicIndexUtils.DOC_TYPE_FIELD);

						// entry = (Map)entries.get(count);
						// see if this entry already has attachments
						List attachments = (List) ent.get(WebKeys.ENTRY_ATTACHMENTS);
						if (attachments == null) {
							attachments = new ArrayList();
						}
						if (typ.equalsIgnoreCase(BasicIndexUtils.DOC_TYPE_ATTACHMENT)) {
							attachments.add(ent);
						}
						attachments.add(entry);
						ent.put(WebKeys.ENTRY_ATTACHMENTS, attachments);
					
						entries.remove(count);
						count--;
						break;
					}
				}
			}
		}
		return entries;		
	}
	
	public static void filterEntryAttachmentResults(Map results) {
		List entries = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		filterEntryAttachmentResults(entries);
		int newCountOfReturnedEntries = entries.size();
		int newTotalCount = (Integer)results.get(ObjectKeys.SEARCH_COUNT_TOTAL)-((Integer)results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED) - newCountOfReturnedEntries);
		
		results.put(ObjectKeys.SEARCH_COUNT_TOTAL, newTotalCount);
		results.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, newCountOfReturnedEntries);
	}
}
