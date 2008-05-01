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
import com.sitescape.team.module.license.LicenseChecker;
import com.sitescape.team.module.license.LicenseModule.LicenseOperation;
import com.sitescape.team.module.ldap.LdapModule.LdapOperation;
import com.sitescape.team.module.profile.ProfileModule.ProfileOperation;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
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
		model.put(WebKeys.PRODUCT_TITLE, SPropsUtil.getString("product.title", ObjectKeys.PRODUCT_TITLE_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_NAME, SPropsUtil.getString("product.conferencing.name", ObjectKeys.PRODUCT_CONFERENCING_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_TITLE, SPropsUtil.getString("product.conferencing.title", ObjectKeys.PRODUCT_CONFERENCING_TITLE_DEFAULT));
 		model.put(WebKeys.PORTLET_TYPE, WebKeys.PORTLET_TYPE_ADMIN);
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
		Map reports = new TreeMap();
		AdaptedPortletURL adapterUrl;
		
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
		if (getLdapModule().testAccess(LdapOperation.manageLdap)) {
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
		
		//email schedule
		if (getAdminModule().testAccess(AdminOperation.manageMail)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_mail"));
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
			element.addAttribute("target", "_blank");
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_PROFILES_IMPORT);
			element.addAttribute("url", adapterUrl.toString());
			elements.put(element.attributeValue("title"), element);
		}
	
		//Definition import
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.import.definitions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			element.addAttribute("target", "_blank");
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_IMPORT);
			element.addAttribute("url", adapterUrl.toString());
			elements.put(element.attributeValue("title"), element);

			//Reset all definitions
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.reload.definitions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_IMPORT);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_CONFIRM);
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

		//Manage license
		if(ReleaseInfo.isEnterpriseEdition()) {
			if (getLicenseModule().testAccess(LicenseOperation.manageLicense)) {
				element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.manage.license"));
				element.addAttribute("image", "bullet");
				element.addAttribute("id", String.valueOf(nextId++));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_LICENSE);
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode(PortletMode.VIEW);
				element.addAttribute("url", url.toString());
				elements.put(element.attributeValue("title"), element);
			}
		}

		//Reports
		Element reportElement;
		reportElement = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
		reportElement.addAttribute("title", NLT.get("toolbar.reports"));
		reportElement.addAttribute("image", "bullet");
		reportElement.addAttribute("displayOnly", "true");
		reportElement.addAttribute("id", String.valueOf(nextId++));

		//Login report
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
			reports.put(element.attributeValue("title"), element);

			//License report
			if(ReleaseInfo.isEnterpriseEdition()) {
				element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.report.title.license"));
				element.addAttribute("image", "bullet");
				element.addAttribute("id", String.valueOf(nextId++));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LICENSE_REPORT);
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode(PortletMode.VIEW);
				element.addAttribute("url", url.toString());
				reports.put(element.attributeValue("title"), element);
			}
			
			// Disk usage report
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.quota"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_QUOTA_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);
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
		reports.put(element.attributeValue("title"), element);
		
		//Change logs
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.view_change_log"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);
		}
		
		//System error logs
		if (getAdminModule().testAccess(AdminOperation.manageErrorLogs)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.system_error_logs"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_ACTION_GET_LOG_FILES);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);
		}
		
		//Forum restore
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			element = DocumentHelper.createElement("child");
			element.addAttribute("title", NLT.get("administration.forum_restore"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_FORUM_RESTORE);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}

		if (!reports.isEmpty()) {
			elements.put(reportElement.attributeValue("title"), reportElement);
			for (Iterator iter=reports.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry me = (Map.Entry)iter.next();
				reportElement.add((Element)me.getValue());
			}
		}

		//Manage zones
		
		if (LicenseChecker.isAuthorizedByLicense("com.sitescape.team.module.zone.MultiZone") &&
				getZoneModule().testAccess()) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.zones"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_ZONES);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Manage applications
		if (getProfileModule().testAccess((ProfileBinder)user.getParentBinder(), ProfileOperation.addEntry)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.applications"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_APPLICATIONS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
	
		//Manage application groups
		if (getProfileModule().testAccess((ProfileBinder)user.getParentBinder(), ProfileOperation.addEntry)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.application.groups"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_APPLICATION_GROUPS);
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
