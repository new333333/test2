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
package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionModule.DefinitionOperation;
import com.sitescape.team.module.profile.ProfileModule.ProfileOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReleaseInfo;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.util.PortletPreferencesUtil;
import com.sitescape.util.Validator;


public class ViewController extends  SAbstractController {
	protected Log logger = LogFactory.getLog(getClass());

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
 		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
		//force reload so we can setup js correctly
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
			prefs.store();
		}
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
		model.put("releaseInfo", ReleaseInfo.getReleaseInfo());
		//Put in the product name
		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
 		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			//Signal that this is the initialization step
			model.put(WebKeys.PORTLET_INITIALIZATION, "1");
			
			PortletURL url;
			url = response.createActionURL();
			model.put(WebKeys.PORTLET_INITIALIZATION_URL, url);
			return new ModelAndView("administration/view", model);

		}
		
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) model.put(WebKeys.IS_SITE_ADMIN, true);
		
		PortletURL url;
		//Build the tree
		int nextId = 0;
		User user = RequestContextHolder.getRequestContext().getUser();
		Workspace top = getWorkspaceModule().getTopWorkspace();

		Document adminTree = DocumentHelper.createDocument();
		Element rootElement = adminTree.addElement("root");
		rootElement.addAttribute("title", NLT.get("administration.title"));
		rootElement.addAttribute("image", "admin_tools");
		rootElement.addAttribute("displayOnly", "true");
		rootElement.addAttribute("id", String.valueOf(nextId++));

		Map elements = new TreeMap();
		Map designers = new TreeMap();
		//Definition builders
		Element designerElement;
		Element element;
		designerElement = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
		designerElement.addAttribute("title", NLT.get("administration.definition_builder_designers"));
		designerElement.addAttribute("image", "bullet");
		designerElement.addAttribute("displayOnly", "true");
		designerElement.addAttribute("id", String.valueOf(nextId++));
		//Definition builder - Entry form designer
		if (getDefinitionModule().testAccess(Definition.FOLDER_ENTRY, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_entry_form_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_ENTRY));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
				
		//Definition builder - Folder view designer
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_folder_view_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
				
		//Definition builder - Workflow designer
		if (getDefinitionModule().testAccess(Definition.WORKFLOW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_workflow_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKFLOW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
		
		//Definition builder - Profile listing designer
		if (getDefinitionModule().testAccess(Definition.PROFILE_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_profile_listing_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.PROFILE_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
		
		//Definition builder - Profile designer
		if (getDefinitionModule().testAccess(Definition.PROFILE_ENTRY_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_profile_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.PROFILE_ENTRY_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
		
		//Definition builder - Workspace designer
		if (getDefinitionModule().testAccess(Definition.WORKSPACE_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_workspace_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKSPACE_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
		
		//Definition builder - User workspace designer
		if (getDefinitionModule().testAccess(Definition.USER_WORKSPACE_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_user_workspace_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.USER_WORKSPACE_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			designers.put(element.attributeValue("title"), element);
		}
		if (!designers.isEmpty()) {
			elements.put(designerElement.attributeValue("title"), designerElement);
			for (Iterator iter=designers.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry me = (Map.Entry)iter.next();
				designerElement.add((Element)me.getValue());
			}
			
		}

		//Ldap configuration
		if (getLdapModule().testAccess("getLdapConfig")) {
			if (getLdapModule().getLdapConfig() != null) {
				element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.configure_ldap"));
				element.addAttribute("image", "bullet");
				element.addAttribute("id", String.valueOf(nextId++));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LDAP_CONFIGURE);
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode(PortletMode.VIEW);
				element.addAttribute("url", url.toString());
				elements.put(element.attributeValue("title"), element);
			}
		}
		
		//Roles configuration
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			element = DocumentHelper.createElement("child");
			element.addAttribute("title", NLT.get("administration.configure_roles"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_ROLES);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Posting schedule
		if (getAdminModule().testAccess(AdminOperation.managePosting)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_posting_job"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_POSTINGJOB_CONFIGURE);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Search index
		if (getBinderModule().testAccess(top, BinderOperation.indexBinder)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_search_index"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}

		//Manage groups
		if (getProfileModule().testAccess((ProfileBinder)user.getParentBinder(), ProfileOperation.addEntry)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.groups"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_GROUPS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
	
		//Import profiles
		if (getProfileModule().testAccess((ProfileBinder)user.getParentBinder(), ProfileOperation.addEntry)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.import.profiles"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_PROFILES_IMPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
	
		//Definition import
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.import.definitions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_IMPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}

		//Definition export
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.export.definitions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_EXPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//templates
		if (getAdminModule().testAccess(AdminOperation.manageTemplate)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_configurations"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}

		//Report
		if (getAdminModule().testAccess(AdminOperation.report)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.login"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOGIN_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);

			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.license"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LICENSE_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Credits
		element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.credits"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_CREDITS);
		url.setWindowState(WindowState.MAXIMIZED);
		url.setPortletMode(PortletMode.VIEW);
		element.addAttribute("url", url.toString());
		elements.put(element.attributeValue("title"), element);
		
		//For debug - keep off everyones menus
		if (logger.isDebugEnabled() && getAdminModule().testAccess(AdminOperation.managePosting)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.view_change_log"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		for (Iterator iter=elements.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry)iter.next();
			rootElement.add((Element)me.getValue());
		}
		model.put(WebKeys.ADMIN_TREE, adminTree);
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
		return new ModelAndView("administration/view", model);
	}
}
