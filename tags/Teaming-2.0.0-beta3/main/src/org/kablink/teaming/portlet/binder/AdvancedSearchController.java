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
package org.kablink.teaming.portlet.binder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.regex.Matcher;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Clipboard;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.search.Constants;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Renata Nowicka
 *
 */

public class AdvancedSearchController extends AbstractBinderController {
	
	public static final String NEW_TAB_VALUE = "1";
		
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		
		// set form data for the render method
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		//ajax requests
		if (op.equals(WebKeys.OPERATION_FIND_ENTRY_ATTRIBUTES_WIDGET)) {
			// TODO: move to TypeToFind...
			return ajaxGetEntryAttributes(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_ATTRIBUTES_VALUE_WIDGET)) {
			// TODO: move to TypeToFind...
			return ajaxGetEntryAttributeValue(request, response);
		}
		Map<String,Object> model = new HashMap();
		//Set up the standard beans
		BinderHelper.setupStandardBeans(this, request, response, model);

		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
		String strBinderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		model.put(WebKeys.BINDER_ID, strBinderId);
		Long binderId = null;
		if (!strBinderId.equals("")) {
			binderId = Long.valueOf(strBinderId);
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}
        
        // this is necessary for the breadcrumbs and places choose
        Workspace top = getWorkspaceModule().getTopWorkspace();
		BinderHelper.buildNavigationLinkBeans(this, top, model);
		model.put(WebKeys.DEFINITION_ENTRY, top);

		model.put(WebKeys.SEARCH_TOP_FOLDER_ID, Collections.singletonList(top.getId().toString()));
        Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);
		
		/** Vertical mode has been removed
		if (ObjectKeys.USER_DISPLAY_STYLE_VERTICAL.equals(RequestContextHolder.getRequestContext().getUser().getDisplayStyle())) {
			model.put(WebKeys.FOLDER_ACTION_VERTICAL_OVERRIDE, "yes");
		}
		*/

       if (op.equals(WebKeys.SEARCH_RESULTS)) {
        	BinderHelper.prepareSearchResultData(this, request, tabs, model);
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);

    		//Build a reload url
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

    		return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else if (op.equals(WebKeys.SEARCH_VIEW_PAGE)) {
        	model.putAll(BinderHelper.prepareSearchResultPage(this, request, tabs));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);
        	
    		//Build a reload url
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else if (op.equals(WebKeys.SEARCH_SAVED_QUERY)) {
        	model.putAll(BinderHelper.prepareSavedQueryResultData(this, request, tabs));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);
        	
    		//Build a reload url
     		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else {
        	model.putAll(BinderHelper.prepareSearchFormData(this, request));
        	if (binderId != null) {
        		Map options = new HashMap();
        		options.put("search_subfolders", Boolean.TRUE);
        		try {
        			options.put("searchFolders", Collections.singletonList(getWorkspaceModule().getTopWorkspace().getId()));
            		options.put("search_currentAndSubfolders", Boolean.FALSE);
        		} catch(AccessControlException e) {
        			options.put("searchFolders", Collections.singletonList(binderId));
            		options.put("search_currentAndSubfolders", Boolean.TRUE);
        		}
        		model.put(WebKeys.SEARCH_FILTER_MAP, options);
        	}
        	
    		//Build a reload url
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView("search/search_form", model);
        }
	}
	
	public void addPropertiesForFolderView(Map model) {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		if (!model.containsKey(WebKeys.SEEN_MAP)) 
			model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(user.getId()));
    	
		Definition def = getDefinitionModule().addDefaultDefinition(Definition.FOLDER_VIEW);
		DefinitionHelper.getDefinition(def, model, "//item[@name='forumView']");
    	model.put(WebKeys.SHOW_SEARCH_RESULTS, true);
    	
		Workspace ws = getWorkspaceModule().getTopWorkspace();
		Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
	}	

	
	protected void buildToolbars(Map model, RenderRequest request) {
		model.put(WebKeys.FOOTER_TOOLBAR,  buildFooterToolbar(model, request).getToolbar());
	}
	
	private Toolbar buildFooterToolbar(Map model, RenderRequest request) {
		Toolbar footerToolbar = new Toolbar();
		String[] contributorIds = collectContributorIds((List)model.get(WebKeys.FOLDER_ENTRYPEOPLE + "_all"));

		addClipboardOption(footerToolbar, contributorIds, model);
		addStartMeetingOption(footerToolbar, request, contributorIds);
		return footerToolbar;
	}

	private void addStartMeetingOption(Toolbar footerToolbar, RenderRequest request, String[] contributorIds) {
		if (getIcBrokerModule().isEnabled()) {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			Map qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);		
			qualifiers.put("post", Boolean.TRUE);
			qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));
		
			footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
		}
	}
	
	private void addClipboardOption(Toolbar toolbar, String[] contributorIds, Map model) {
		// clipboard
		Map qualifiers = new HashMap();
		String contributorIdsAsJSString = "";
		for (int i = 0; i < contributorIds.length; i++) {
			contributorIdsAsJSString += contributorIds[i];
			if (i < (contributorIds.length -1)) {
				contributorIdsAsJSString += ", ";	
			}
		}
		qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "]);return false;");
		//toolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "#", qualifiers);
		model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS_AS_JS_STRING, contributorIdsAsJSString);
		model.put(WebKeys.TOOLBAR_CLIPBOARD_SHOW, Boolean.TRUE);
	}
	
	private String[] collectContributorIds(List entries) {
		Set principals = new HashSet();
		
		if (entries != null) {
			Iterator entriesIt = entries.iterator();
			while (entriesIt.hasNext()) {
				Map entry = (Map)entriesIt.next();
				Principal principal = (Principal)entry.get(WebKeys.USER_PRINCIPAL);
				principals.add(principal.getId().toString());
			}	
		}
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}
	
	private ModelAndView ajaxGetEntryAttributes(RenderRequest request, RenderResponse response) {
		String entryTypeId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_ENTRY_DEF_ID, "");
		String entryField = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField, "");
		String strBinderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		
		Map model = new HashMap();
		response.setContentType("text/json-comment-filtered");		
		Long binderId = null;
		Binder binder = null;
		if (!strBinderId.equals("")) {
			binderId = Long.valueOf(strBinderId);
			binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}
		//See if this request is for the list of attribute sets or the values within a set
		if (binder == null || entryField.indexOf(",") == -1) {
			String sourceFieldName = "";
			Definition def = getDefinitionModule().getDefinition(entryTypeId);
			Document defDoc = def.getDefinition();
			Element root = (Element)defDoc.getRootElement();
			Element entryAttributesEle = (Element)root.selectSingleNode("//item[@name='entryAttributes']/properties/property[@name='name' and @value='"+entryField+"']");
			if (entryAttributesEle != null) {
				Element sourceEle = (Element) entryAttributesEle.selectSingleNode("../property[@name='source']");
				if (sourceEle != null) sourceFieldName = sourceEle.attributeValue("value", "");
			}
			List<Long> binderIds = getDefinitionModule().getBindersUsingEntryDef(entryTypeId, sourceFieldName);
			SortedSet<Binder> binders = getBinderModule().getBinders(binderIds);
			model.put(WebKeys.BINDERS, binders);
			
			//This feature has been disabled until it is made to work
			model.put(WebKeys.BINDERS, new ArrayList());
			
			//Return the list of attribute sets (or an empty set if the binder is not specified)
			model.put(SearchFilterKeys.FilterElementNameField, sourceFieldName);
			return new ModelAndView("forum/json/find_entry_attributes_widget", model);
		}
		//The field name is "elementName , index of set"
		String elementName = entryField.substring(0, entryField.indexOf(","));
		model.put(SearchFilterKeys.FilterElementNameField, elementName);
		String elementValue = entryField.substring(entryField.indexOf(",")+1);
		model.put(SearchFilterKeys.FilterElementValueField, elementValue);
		//Get the list of attributes from the selected attribute set
		CustomAttribute attr = binder.getCustomAttribute(elementName);
		if (attr != null) {
			CustomAttribute attrValues = 
				binder.getCustomAttribute(elementName+DefinitionModule.ENTRY_ATTRIBUTES_SET+elementValue);
			model.put(SearchFilterKeys.FilterElementValueSet, attrValues.getValueSet());
		}
		return new ModelAndView("forum/json/find_entry_attributes_value_widget", model);
 	}
	private ModelAndView ajaxGetEntryAttributeValue(RenderRequest request, RenderResponse response) {
		String entryTypeId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_ENTRY_DEF_ID, "");
		String entryField = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField, "");
		String strBinderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		
		Map model = new HashMap();
		response.setContentType("text/json-comment-filtered");		
		Long binderId = null;
		Binder binder = null;
		if (!strBinderId.equals("")) {
			binderId = Long.valueOf(strBinderId);
			binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}
		//See if this request is for the list of attribute sets or the values within a set
		if (binder == null || entryField.indexOf(",") == -1) {
			String sourceFieldName = "";
			Definition def = getDefinitionModule().getDefinition(entryTypeId);
			Document defDoc = def.getDefinition();
			Element root = (Element)defDoc.getRootElement();
			Element entryAttributesEle = (Element)root.selectSingleNode("//item[@name='entryAttributes']/properties/property[@name='name' and @value='"+entryField+"']");
			if (entryAttributesEle != null) {
				Element sourceEle = (Element) entryAttributesEle.selectSingleNode("../property[@name='source']");
				if (sourceEle != null) sourceFieldName = sourceEle.attributeValue("value", "");
			}
			List<Long> binderIds = getDefinitionModule().getBindersUsingEntryDef(entryTypeId, sourceFieldName);
			SortedSet<Binder> binders = getBinderModule().getBinders(binderIds);
			model.put(WebKeys.BINDERS, binders);
			//Return the list of attribute sets (or an empty set if the binder is not specified)
			model.put(SearchFilterKeys.FilterElementNameField, sourceFieldName);
			return new ModelAndView("forum/json/find_entry_attributes_widget", model);
		}
		//The field name is "elementName , index of set"
		String elementName = entryField.substring(0, entryField.indexOf(","));
		model.put(SearchFilterKeys.FilterElementNameField, elementName);
		String elementValue = entryField.substring(entryField.indexOf(",")+1);
		model.put(SearchFilterKeys.FilterElementValueField, elementValue);
		//Get the list of attributes from the selected attribute set
		CustomAttribute attr = binder.getCustomAttribute(elementName);
		if (attr != null) {
			CustomAttribute attrValues = 
				binder.getCustomAttribute(elementName+DefinitionModule.ENTRY_ATTRIBUTES_SET+elementValue);
			model.put(SearchFilterKeys.FilterElementValueSet, attrValues.getValueSet());
		}
		return new ModelAndView("forum/json/find_entry_attributes_value_widget", model);
 	}
	
}
