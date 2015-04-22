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
package org.kablink.teaming.portlet.administration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.definition.DefinitionModule.DefinitionOperation;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class ManageDefinitionsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
		Long binderId = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		} catch (Exception ex) {};
				//process cancels first
		if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (binderId != null) response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		if (formData.containsKey("ss_configErrorMessage")) {
			model.put("ss_configErrorMessage", ((String[]) formData.get("ss_configErrorMessage"))[0]);
		}

		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if ("getTree".equals(operation)) {
			//ajax request to expand tree
			return getTree(request, response);
		}
		Long binderId = null;
		Binder binder = null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER_ID, binderId);
			model.put(WebKeys.BINDER, binder);
		} catch (Exception ex) {};
		//Set up the standard beans
 		BinderHelper.setupStandardBeans(this, request, response, model);
		String path = WebKeys.VIEW_DEFINITIONS;
		if (Validator.isNull(operation)) {
			
			if (binder != null) {
				model.put(WebKeys.DEFINITION_ENTRY, binder);
				//Build the navigation beans
				BinderHelper.buildNavigationLinkBeans(this, binder, model);
			}
			Document definitionConfig = getDefinitionModule().getDefinitionConfig();
			PortletURL url;
			Toolbar toolbar = new Toolbar();
			toolbar.addToolbarMenu("1_add", NLT.get("administration.toolbar.add"), "");
			//Build the tree
			Map designers = new TreeMap(new StringComparator(RequestContextHolder.getRequestContext().getUser().getLocale()));
			//Definition builders
			Element element;
			Element configNode;
			Document adminTree = DocumentHelper.createDocument();
			Element rootElement = adminTree.addElement("root");
			rootElement.addAttribute("title", NLT.get("administration.definition_builder_designers"));
			rootElement.addAttribute("image", "bullet");
			rootElement.addAttribute("displayOnly", "true");
			rootElement.addAttribute("id", "0");
			Map qualifiers = new HashMap();
			qualifiers.put("onClick", "{return true}");
			//Definition builder - Entry form designer
			List defs = getDefinitionModule().getDefinitions(binderId, Boolean.FALSE);
			//Definition builder - Profile listing designer
			if (binder == null && getDefinitionModule().testAccess(binder, Definition.PROFILE_VIEW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.PROFILE_VIEW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__profile_views", String.valueOf(Definition.PROFILE_VIEW));
					fillChildElements(element, Definition.PROFILE_VIEW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='profileViewDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				url.setParameter("definitionType", String.valueOf(Definition.PROFILE_VIEW));
				if ( binder != null )
					url.setParameter( WebKeys.URL_BINDER_ID, binder.getId().toString() );
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			
			//Definition builder - Profile designer
			if (binder == null && getDefinitionModule().testAccess(binder, Definition.PROFILE_ENTRY_VIEW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.PROFILE_ENTRY_VIEW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__profile_entry_view", String.valueOf(Definition.PROFILE_ENTRY_VIEW));
					fillChildElements(element, Definition.PROFILE_ENTRY_VIEW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				//cannot add new ones
			}
			
			
			//Definition builder - User workspace designer
			if (binder == null && getDefinitionModule().testAccess(binder, Definition.USER_WORKSPACE_VIEW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.USER_WORKSPACE_VIEW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__user_workspace_view", String.valueOf(Definition.USER_WORKSPACE_VIEW));
					fillChildElements(element, Definition.USER_WORKSPACE_VIEW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='userWorkspaceViewDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				url.setParameter("definitionType", String.valueOf(Definition.USER_WORKSPACE_VIEW));
				if ( binder != null )
					url.setParameter( WebKeys.URL_BINDER_ID, binder.getId().toString() );
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			if (binder == null && getDefinitionModule().testAccess(binder, Definition.EXTERNAL_USER_WORKSPACE_VIEW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__external_user_workspace_view", String.valueOf(Definition.EXTERNAL_USER_WORKSPACE_VIEW));
					fillChildElements(element, Definition.EXTERNAL_USER_WORKSPACE_VIEW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='externalUserWorkspaceViewDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				url.setParameter("definitionType", String.valueOf(Definition.EXTERNAL_USER_WORKSPACE_VIEW));
				if ( binder != null )
					url.setParameter( WebKeys.URL_BINDER_ID, binder.getId().toString() );
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			if (getDefinitionModule().testAccess(binder, Definition.FOLDER_ENTRY, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.FOLDER_ENTRY)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__entry_definitions", String.valueOf(Definition.FOLDER_ENTRY));
					fillChildElements(element, Definition.FOLDER_ENTRY, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='entryTypeDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				if (binder != null) url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				url.setParameter("definitionType", String.valueOf(Definition.FOLDER_ENTRY));
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			//Definition builder - Workflow designer
			if (getDefinitionModule().testAccess(binder, Definition.WORKFLOW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.WORKFLOW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__workflow_processes", String.valueOf(Definition.WORKFLOW));
					fillChildElements(element, Definition.WORKFLOW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='workflowProcessDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				if (binder != null) url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				url.setParameter("definitionType", String.valueOf(Definition.WORKFLOW));
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			if (getDefinitionModule().testAccess(binder, Definition.FOLDER_VIEW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.FOLDER_VIEW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__folder_views", String.valueOf(Definition.FOLDER_VIEW));
					fillChildElements(element, Definition.FOLDER_VIEW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='forumViewDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				if (binder != null) url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				url.setParameter("definitionType", String.valueOf(Definition.FOLDER_VIEW));
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			if (((binder == null) || binder instanceof Workspace) && getDefinitionModule().testAccess(binder, Definition.WORKSPACE_VIEW, DefinitionOperation.manageDefinition)) {
				if (hasDefinitionType(defs, Definition.WORKSPACE_VIEW)) {
					element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					fillTypeElement(element, "__workspace_view", String.valueOf(Definition.WORKSPACE_VIEW));
					fillChildElements(element, Definition.WORKSPACE_VIEW, defs);
					designers.put(element.attributeValue("title"), element);
				}
				configNode = (Element)definitionConfig.selectSingleNode("/definitions/definition[@name='workspaceViewDefinition']");
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				if (binder != null) url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				url.setParameter("definitionType", String.valueOf(Definition.WORKSPACE_VIEW));
				toolbar.addToolbarMenuItem("1_add", "", NLT.get(configNode.attributeValue("caption")), url, qualifiers);
			}
			//	add sorted elements
			for (Iterator iter=designers.entrySet().iterator(); iter.hasNext(); ) {
					Map.Entry me = (Map.Entry)iter.next();
					rootElement.add((Element)me.getValue());
			}

			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, "manage_definitions");
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_COPY);
			if (binderId != null) url.setParameter("binderId", binderId.toString());
			toolbar.addToolbarMenu("2_copy", NLT.get("administration.toolbar.copy"), url);
			if (binderId == null) {
				url = response.createRenderURL();
				url.setParameter(WebKeys.URL_ACTION, "import_definition");
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_CONFIRM);
				toolbar.addToolbarMenu("3_reload", NLT.get("administration.toolbar.reset"), url);
			}
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, "import_definition");
			if (binderId != null) url.setParameter("binderId", binderId.toString());			
			toolbar.addToolbarMenu("4_import", NLT.get("administration.toolbar.import"), url);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, "manage_definitions");
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_EXPORT);
			if (binderId != null) url.setParameter("binderId", binderId.toString());			
			toolbar.addToolbarMenu("5_export", NLT.get("administration.toolbar.export"), url);
			
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());
			model.put(WebKeys.ADMIN_TREE, adminTree);
			AdaptedPortletURL adapter = new AdaptedPortletURL(request, "ss_forum", true);
			if (binderId != null) adapter.setParameter("binderId2", binderId.toString());
			adapter.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
			adapter.setParameter(WebKeys.URL_OPERATION, "getTree");
			model.put("callbackUrl", adapter.toString());
			return new ModelAndView(path, model);
		
		} else if (WebKeys.OPERATION_ADD.equals(operation)) {
			Integer definitionType = PortletRequestUtils.getIntParameter(request, "definitionType");
			Document config = getDefinitionModule().getDefinitionConfig();
			if (definitionType == null) {
				definitionType = Definition.FOLDER_ENTRY;
			} 
			Element item = (Element)config.selectSingleNode("/definitions/definition[@definitionType='" + definitionType.toString() + 
					"']/operations/operation[@name='addDefinition']");
			model.put("itemName", item.attributeValue("item"));
			model.put(WebKeys.OPERATION, operation);
			model.put(WebKeys.CONFIG_DEFINITION, config);
			model.put("definitionType", definitionType);
			return new ModelAndView(path, model);
		} else if (WebKeys.OPERATION_EXPORT.equals(operation)) {			
			model.put(WebKeys.DOM_TREE, DefinitionHelper.getDefinitionTree(this, binderId));
	 		return new ModelAndView(WebKeys.VIEW_ADMIN_EXPORT_DEFINITIONS, model);
		} else if (WebKeys.OPERATION_COPY.equals(operation)) {			
			model.put(WebKeys.OPERATION, operation);
			String definitionId = PortletRequestUtils.getStringParameter(request, "sourceDefinitionId", "");
			if (Validator.isNull(definitionId)) {
				model.put(WebKeys.DOM_TREE, DefinitionHelper.getDefinitionTree(this, binderId));
			} else {
				Definition def = getDefinitionModule().getDefinition(definitionId);
				model.put(WebKeys.DEFINITION, def);
			}
				
	 		return new ModelAndView(path, model);
		} 
					
		
		return new ModelAndView(path, model);
		
	}
	protected void fillTypeElement(Element element, String title, String id) {
		element.addAttribute("title", NLT.get(title));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", id);
		element.addAttribute("hasChildren", "true");
		element.addAttribute("displayOnly", "true");

	}
	protected void fillChildElements(Element element, Integer type, Collection<Definition> definitions) {
		//build sorted map of definitions
		if (definitions == null || type == null) return;
		if (definitions.isEmpty()) element.getParent().addAttribute("hasChildren", "false");
		List prunedDefs = new ArrayList();
		for (Definition def:definitions) {
			if (!type.equals(def.getType())) continue;
			prunedDefs.add(def);
		}
		Map<String, Definition> sortedMap = DefinitionHelper.orderDefinitions(prunedDefs, true);
		for (Map.Entry<String,Definition> me: sortedMap.entrySet()) {
			Definition def = me.getValue();
			Element curDefEle = element.addElement("child");
			if (Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) {
				curDefEle.addAttribute("image", "/pics/delete.gif");
			} 
			curDefEle.addAttribute("title", me.getKey());
			curDefEle.addAttribute("id", def.getId());
			curDefEle.addAttribute("url", "");

		}

	}
	protected boolean hasDefinitionType(List<Definition>defs, Integer type) {
		if (defs == null) return true;
		for (Definition def:defs) {
			if (type.equals(def.getType())) return true;
		}
		return false;
	}
	protected ModelAndView getTree(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();
		if (!WebHelper.isUserLoggedIn(request)) {
			Map statusMap = new HashMap();
				
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);
			response.setContentType("text/xml");			
			return new ModelAndView("tag_jsps/tree/get_tree_div", model);
		}
		//AJAX request
		String op2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		String namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
		model.put(WebKeys.NAMESPACE, namespace);
		//in this case binderId is the definitionType
		Integer definitionType = PortletRequestUtils.getIntParameter(request, "binderId");
		Long binderId = PortletRequestUtils.getLongParameter(request, "binderId2");
		List<Definition> definitions = getDefinitionModule().getDefinitions(binderId, Boolean.FALSE, definitionType);
			
		Document adminTree = DocumentHelper.createDocument();
		model.put(WebKeys.WORKSPACE_DOM_TREE, adminTree);
		Element rootElement = adminTree.addElement("root");
		switch (definitionType) {
		case Definition.FOLDER_ENTRY: 
			fillTypeElement(rootElement, "__entry_definitions", String.valueOf(Definition.FOLDER_ENTRY));
			break;
		case Definition.WORKFLOW:
			fillTypeElement(rootElement, "__workflow_processes", String.valueOf(Definition.WORKFLOW));
			break;
		case Definition.FOLDER_VIEW:
			fillTypeElement(rootElement, "__folder_view", String.valueOf(Definition.FOLDER_VIEW));
			break;
		case Definition.WORKSPACE_VIEW:
			fillTypeElement(rootElement, "__workspace_view", String.valueOf(Definition.WORKSPACE_VIEW));
			break;
		case Definition.PROFILE_ENTRY_VIEW:
			fillTypeElement(rootElement, "__profile_entry_view", String.valueOf(Definition.PROFILE_ENTRY_VIEW));
			break;
		case Definition.USER_WORKSPACE_VIEW:
			fillTypeElement(rootElement, "__user_workspace_view", String.valueOf(Definition.USER_WORKSPACE_VIEW));
			break;
		case Definition.EXTERNAL_USER_WORKSPACE_VIEW:
			fillTypeElement(rootElement, "__external_user_workspace_view", String.valueOf(Definition.EXTERNAL_USER_WORKSPACE_VIEW));
			break;
		case Definition.PROFILE_VIEW:
			fillTypeElement(rootElement, "__profile_views", String.valueOf(Definition.PROFILE_VIEW));
			break;		
		}
		fillChildElements(rootElement, definitionType, definitions);
		//now add sorted entries to dom tree
		model.put("ss_tree_binderId", definitionType.toString());
		model.put("ss_tree_id", definitionType.toString());
			
		model.put("ss_tree_treeName", PortletRequestUtils.getStringParameter(request, "treeName", ""));
		model.put("ss_tree_showIdRoutine", PortletRequestUtils.getStringParameter(request, "showIdRoutine", ""));
		model.put("ss_tree_indentKey", PortletRequestUtils.getStringParameter(request, "indentKey", ""));
		model.put("ss_tree_topId", op2);
		model.put("ss_tree_select_type", "0");
			
			
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		User user = RequestContextHolder.getRequestContext().getUser();
		String view = "tag_jsps/tree/get_tree_div";
		if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(user.getDisplayStyle()) && accessible_simple_ui) {
			view = "tag_jsps/tree/get_tree_div_accessible";
		} else {
			response.setContentType("text/xml");
		}
		return new ModelAndView(view, model);
	}
}
