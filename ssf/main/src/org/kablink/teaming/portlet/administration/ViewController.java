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
import java.util.HashMap;
import java.util.List;
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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionModule.DefinitionOperation;
import org.kablink.teaming.module.ldap.LdapModule.LdapOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.license.LicenseModule.LicenseOperation;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.PortletPreferencesUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;



public class ViewController extends  SAbstractController {
	protected Log logger = LogFactory.getLog(getClass());

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
 		try {
 			PortletPreferences prefs = request.getPreferences();
			String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
			//force reload so we can setup js correctly
			if (Validator.isNull(ss_initialized)) {
				prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
				prefs.store();
			}
 		} catch(Exception e) {}
 		
 		if (ObjectKeys.SUPER_USER_INTERNALID.equals(user.getInternalId())) {
 			Binder topWorkspace = getWorkspaceModule().getTopWorkspace();
	 		String upgradeVersion = (String) topWorkspace.getProperty(ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION);
	 		if (upgradeVersion == null || !upgradeVersion.equals(ObjectKeys.PRODUCT_UPGRADE_VERSION)) {
	 			//See if all upgrade tasks have been done
	 			UserProperties userProperties = getProfileModule().getUserProperties(user.getId());
	 			if ("true".equals(userProperties.getProperty(ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX)) &&
	 					"true".equals(userProperties.getProperty(ObjectKeys.USER_PROPERTY_UPGRADE_DEFINITIONS)) &&
	 					"true".equals(userProperties.getProperty(ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES))) {
	 				//All upgrade tasks were done, mark the upgrade complete
	 				getBinderModule().setProperty(topWorkspace.getId(), ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION, ObjectKeys.PRODUCT_UPGRADE_VERSION);
	 			}
	 		}
 		}
		response.setRenderParameters(request.getParameterMap());
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
 		Map<String,Object> model = new HashMap<String,Object>();

 		Workspace top = null;
 		try {
			top = getWorkspaceModule().getTopWorkspace();
 		} catch(Exception e) {}

		model.put("releaseInfo", ReleaseInfo.getReleaseInfo());
		//Put in the product name
		model.put(WebKeys.PRODUCT_NAME, SPropsUtil.getString("product.name", ObjectKeys.PRODUCT_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_TITLE, SPropsUtil.getString("product.title", ObjectKeys.PRODUCT_TITLE_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_NAME, SPropsUtil.getString("product.conferencing.name", ObjectKeys.PRODUCT_CONFERENCING_NAME_DEFAULT));
		model.put(WebKeys.PRODUCT_CONFERENCING_TITLE, SPropsUtil.getString("product.conferencing.title", ObjectKeys.PRODUCT_CONFERENCING_TITLE_DEFAULT));
 		model.put(WebKeys.PORTLET_TYPE, WebKeys.PORTLET_TYPE_ADMIN);
 		if (top != null) {
 			model.put(WebKeys.UPGRADE_VERSION_CURRENT, top.getProperty(ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION));
 		} else {
 			model.put(WebKeys.UPGRADE_VERSION_CURRENT, ObjectKeys.PRODUCT_UPGRADE_VERSION);
 		}
 		model.put(WebKeys.UPGRADE_VERSION, ObjectKeys.PRODUCT_UPGRADE_VERSION);
		try {
 			//If running in a portal, see if we should redraw ourselves just after adding the portlet
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
 		} catch(Exception e) {}
		
		//Set up the standard beans (without a binder)
		BinderHelper.setupStandardBeans(this, request, response, model);

		Long binderId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder=null;
		if (binderId != null) {
			try {
				binder = getBinderModule().getBinder(binderId);
	 		} catch(AccessControlException e) {
				//Access is not allowed
				String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
				model.put(WebKeys.URL, refererUrl);
				if (WebHelper.isUserLoggedIn(request) && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					//Access is not allowed
					return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
				} else {
					//Please log in
					return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
				}
			}
			//Set up the standard beans (with the binder)
			BinderHelper.setupStandardBeans(this, request, response, model, binderId);
			if (binder != null) model.put(WebKeys.ENTITY_TYPE_BEAN, binder.getEntityType().name());
		}
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) model.put(WebKeys.IS_SITE_ADMIN, true);
		
		PortletURL url;
		//Build the tree
		int nextId = 0;

		Document adminTree = DocumentHelper.createDocument();
		Element rootElement = adminTree.addElement("root");
		rootElement.addAttribute("title", NLT.get("administration.title"));
		rootElement.addAttribute("image", "admin_tools");
		rootElement.addAttribute("displayOnly", "true");
		rootElement.addAttribute("id", String.valueOf(nextId++));

		Map elements = new TreeMap();
		Map reports = new TreeMap();
		AdaptedPortletURL adapterUrl;
		
		//Definition builders
		Element element;
		//Definition builder - Entry form designer
		if (getDefinitionModule().testAccess(binder, Definition.FOLDER_ENTRY, DefinitionOperation.manageDefinition) ||
			getDefinitionModule().testAccess(binder, Definition.WORKFLOW, DefinitionOperation.manageDefinition)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_designers"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
				

		//Ldap configuration
		if (getLdapModule().testAccess(LdapOperation.manageLdap)) {
			if (getLdapModule().getLdapSchedule() != null) {
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
		
		//User access configuration
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			if (ReleaseInfo.isLicenseRequiredEdition()) {
				element.addAttribute("title", NLT.get("administration.configure_userAccessOnly", NLT.get("administration.configure_userAccess")));
			} else {
				element.addAttribute("title", NLT.get("administration.configure_userAccess"));
			}
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_USER_ACCESS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Mobile access configuration
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_mobileAccess"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_MOBILE_ACCESS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Default landing page configuration
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_homePage"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_HOME_PAGE);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//User Accounts: Add User Account, Disable/Delete Accounts, Import Profiles
		try {
			ProfileBinder profilesBinder = getProfileModule().getProfileBinder();
			if (getProfileModule().testAccess(profilesBinder, ProfileOperation.manageEntries)) {
				List defaultEntryDefinitions = profilesBinder.getEntryDefinitions();
				if (!defaultEntryDefinitions.isEmpty()) {
					// Only one option
					Definition def = (Definition) defaultEntryDefinitions.get(0);
					adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, profilesBinder.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					adapterUrl.setParameter(WebKeys.URL_CONTEXT, "adminMenu");
					String title = NLT.get("administration.manage.userAccounts");
					element = DocumentHelper.createElement("child");
					element.addAttribute("title", title);
					element.addAttribute("image", "bullet");
					element.addAttribute("id", String.valueOf(nextId++));
					element.addAttribute("url", adapterUrl.toString());
					elements.put(element.attributeValue("title"), element);
				}
			}
		} catch(AccessControlException e) {}
		
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
		if (top != null) {
			if (ObjectKeys.SUPER_USER_INTERNALID.equals(user.getInternalId()) && 
				getBinderModule().testAccess(top, BinderOperation.indexBinder)) {
				element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.configure_search_index"));
				element.addAttribute("image", "bullet");			
				element.addAttribute("id", String.valueOf(nextId++));
				elements.put(element.attributeValue("title"), element);		
				
				if (getAdminModule().retrieveIndexNodesHA() != null) {
					element.addAttribute("displayOnly", "true");
					// index
					Element indexElem = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					indexElem.addAttribute("title", NLT.get("administration.search.title.index"));
					indexElem.addAttribute("image", "bullet");			
					indexElem.addAttribute("id", String.valueOf(nextId++));
					url = response.createRenderURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE);
					url.setWindowState(WindowState.MAXIMIZED);
					url.setPortletMode(PortletMode.VIEW);
					indexElem.addAttribute("url", url.toString());
					element.add(indexElem);
					// index
					Element nodesElem = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
					nodesElem.addAttribute("title", NLT.get("administration.search.title.nodes"));
					nodesElem.addAttribute("image", "bullet");			
					nodesElem.addAttribute("id", String.valueOf(nextId++));
					url = response.createRenderURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_FOLDER_SEARCH_NODES_CONFIGURE);
					url.setWindowState(WindowState.MAXIMIZED);
					url.setPortletMode(PortletMode.VIEW);
					nodesElem.addAttribute("url", url.toString());
					element.add(nodesElem);
				}
				else {
					url = response.createRenderURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE);
					url.setWindowState(WindowState.MAXIMIZED);
					url.setPortletMode(PortletMode.VIEW);
					element.addAttribute("url", url.toString());
				}
			}
		}

		//Manage groups
		try {
			ProfileBinder profiles = getProfileModule().getProfileBinder();
			if (getProfileModule().testAccess(profiles, ProfileOperation.manageEntries)) {
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
		} catch(AccessControlException e) {}
	
		//Manage quotas
		try {
			if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
				element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.manage.quotas"));
				element.addAttribute("image", "bullet");
				element.addAttribute("id", String.valueOf(nextId++));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_QUOTAS);
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode(PortletMode.VIEW);
				element.addAttribute("url", url.toString());
				elements.put(element.attributeValue("title"), element);
			}
		} catch(AccessControlException e) {}
	
		//Manage resource drivers
		try {
			if (getAdminModule().testAccess(AdminOperation.manageFunction) &&
					LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder")) {
				element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.manage.resourceDrivers"));
				element.addAttribute("image", "bullet");
				element.addAttribute("id", String.valueOf(nextId++));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_RESOURCE_DRIVERS);
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode(PortletMode.VIEW);
				element.addAttribute("url", url.toString());
				elements.put(element.attributeValue("title"), element);
			}
		} catch(AccessControlException e) {}
	
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
		if(ReleaseInfo.isLicenseRequiredEdition()) {
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
			//Email report
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.email"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_EMAIL_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);

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
			if(ReleaseInfo.isLicenseRequiredEdition()) {
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
			
			//Activity by user
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.activityByUser"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			element.addAttribute( "action", WebKeys.ACTION_ACTIVITY_REPORT_BY_USER );
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT_BY_USER); 
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);
			
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
			
			// Disk quota exceeded report
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.disk_quota_exceeded"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_QUOTA_EXCEEDED_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);

			// Disk quota highwater exceeded report
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report.title.highwater_exceeded"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_QUOTA_HIGHWATER_EXCEEDED_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);	
			
			// User access report
			element = DocumentHelper.createElement( DomTreeBuilder.NODE_CHILD );
			element.addAttribute( "title", NLT.get( "administration.report.title.user_access" ) );
			element.addAttribute( "image", "bullet" );
			element.addAttribute( "id", String.valueOf( nextId++ ) );
			url = response.createRenderURL();
			url.setParameter( WebKeys.ACTION, WebKeys.ACTION_USER_ACCESS_REPORT );
			url.setWindowState( WindowState.MAXIMIZED );
			url.setPortletMode( PortletMode.VIEW );
			element.addAttribute( "url", url.toString() );
			reports.put( element.attributeValue( "title" ), element );
			
			// XSS report
			element = DocumentHelper.createElement( DomTreeBuilder.NODE_CHILD );
			element.addAttribute( "title", NLT.get( "administration.report.title.xss", "XSS Report" ) );
			element.addAttribute( "image", "bullet" );
			element.addAttribute( "id", String.valueOf( nextId++ ) );
			url = response.createRenderURL();
			url.setParameter( WebKeys.ACTION, WebKeys.ACTION_XSS_REPORT );
			url.setWindowState( WindowState.MAXIMIZED );
			url.setPortletMode( PortletMode.VIEW );
			element.addAttribute( "url", url.toString() );
			reports.put( element.attributeValue( "title" ), element );
		}
		
		//Credits
		element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
		element.addAttribute("title", NLT.get("administration.credits"));
		element.addAttribute("image", "bullet");
		element.addAttribute("id", String.valueOf(nextId++));
		element.addAttribute( "action", WebKeys.ACTION_VIEW_CREDITS );
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
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG);
			element.addAttribute("url", adapterUrl.toString());
			reports.put(element.attributeValue("title"), element);
		}
		
		//System error logs
		if (getAdminModule().testAccess(AdminOperation.manageErrorLogs)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.system_error_logs"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			element.addAttribute("action", WebKeys.ADMIN_ACTION_GET_LOG_FILES);
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_ACTION_GET_LOG_FILES);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			reports.put(element.attributeValue("title"), element);
		}
		
		if (!reports.isEmpty()) {
			elements.put(reportElement.attributeValue("title"), reportElement);
			for (Iterator iter=reports.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry me = (Map.Entry)iter.next();
				reportElement.add((Element)me.getValue());
			}
		}

		//Manage zones
		
		if (LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.zone.MultiZone") &&
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
		//Zone admin
		ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
		if (getAdminModule().testAccess(AdminOperation.manageFunctionMembership)) {
			
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.accessControl"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			url.setParameter(WebKeys.URL_WORKAREA_ID, zoneConfig.getWorkAreaId().toString());
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, zoneConfig.getWorkAreaType());
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		//Manage applications
		try {
			ProfileBinder profiles = getProfileModule().getProfileBinder();
			if (getProfileModule().testAccess(profiles, ProfileOperation.manageEntries)) {
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
		} catch(AccessControlException e) {}
	
		//Manage application groups
		try {
			ProfileBinder profiles = getProfileModule().getProfileBinder();
			if (getProfileModule().testAccess(profiles, ProfileOperation.manageEntries)) {
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
		} catch(AccessControlException e) {}
		
		
		//Manage Extensions
		if (getAdminModule().testAccess(AdminOperation.manageExtensions)) {
			element = DocumentHelper.createElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.extensions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_EXTENSIONS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			elements.put(element.attributeValue("title"), element);
		}
		
		// Site Branding
		if ( top != null && GwtUIHelper.isGwtUIActive( request ) )
		{
			// Does the user have the rights to modify the top binder
			if ( getBinderModule().testAccess( top, BinderOperation.modifyBinder ) )
			{
				// Yes, add a "Site Branding" option.
				element = DocumentHelper.createElement( DomTreeBuilder.NODE_CHILD );
				element.addAttribute( "title", NLT.get( "administration.modifySiteBranding" ) );
				element.addAttribute( "image", "bullet" );
				element.addAttribute( "id", String.valueOf( nextId++ ) );
				url = response.createRenderURL();
				url.setParameter( WebKeys.ACTION, WebKeys.ACTION_LDAP_CONFIGURE );
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode( PortletMode.VIEW );
				element.addAttribute( "url", url.toString() );
				elements.put( element.attributeValue( "title" ), element );
			}
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
