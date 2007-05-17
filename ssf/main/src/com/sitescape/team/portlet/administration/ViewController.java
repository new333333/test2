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

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReleaseInfo;
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
		
		if (getAdminModule().testAccess("addFunction")) model.put(WebKeys.IS_SITE_ADMIN, true);
		
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

		
		//Definition builders
		Element designerElement;
		Element element;
		designerElement = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
		designerElement.addAttribute("title", NLT.get("administration.definition_builder_designers"));
		designerElement.addAttribute("image", "bullet");
		designerElement.addAttribute("displayOnly", "true");
		designerElement.addAttribute("id", String.valueOf(nextId++));
		boolean hasDefAccess=false;
		//Definition builder - Entry form designer
		if (getDefinitionModule().testAccess(Definition.FOLDER_ENTRY, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_entry_form_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_ENTRY));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
				
		//Definition builder - Folder view designer
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_folder_view_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
				
		//Definition builder - Workflow designer
		if (getDefinitionModule().testAccess(Definition.WORKFLOW, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_workflow_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKFLOW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
		
		//Definition builder - Profile listing designer
		if (getDefinitionModule().testAccess(Definition.PROFILE_VIEW, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_profile_listing_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.PROFILE_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
		
		//Definition builder - Profile designer
		if (getDefinitionModule().testAccess(Definition.PROFILE_ENTRY_VIEW, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_profile_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.PROFILE_ENTRY_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
		
		//Definition builder - Workspace designer
		if (getDefinitionModule().testAccess(Definition.WORKSPACE_VIEW, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_workspace_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKSPACE_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
		
		//Definition builder - User workspace designer
		if (getDefinitionModule().testAccess(Definition.USER_WORKSPACE_VIEW, "addDefinition")) {
			element = designerElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.definition_builder_user_workspace_designer"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
			url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.USER_WORKSPACE_VIEW));
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
			hasDefAccess=true;
		}
		if (!hasDefAccess) rootElement.remove(designerElement);

		//Ldap configuration
		if (getLdapModule().testAccess("getLdapConfig")) {
			if (getLdapModule().getLdapConfig() != null) {
				element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
				element.addAttribute("title", NLT.get("administration.configure_ldap"));
				element.addAttribute("image", "bullet");
				element.addAttribute("id", String.valueOf(nextId++));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LDAP_CONFIGURE);
				url.setWindowState(WindowState.MAXIMIZED);
				url.setPortletMode(PortletMode.VIEW);
				element.addAttribute("url", url.toString());
			}
		}
		
		//Roles configuration
		if (getAdminModule().testAccess("addFunction")) {
			element = rootElement.addElement("child");
			element.addAttribute("title", NLT.get("administration.configure_roles"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_ROLES);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
		
		//Posting schedule
		if (getAdminModule().testAccess("setPostingSchedule")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_posting_job"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_POSTINGJOB_CONFIGURE);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
		
		//Search index
		if (getBinderModule().testAccess(top, "indexBinder")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_search_index"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}

		//Manage groups
		if (getProfileModule().testAccess((ProfileBinder)user.getParentBinder(), "addEntries")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.manage.groups"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_GROUPS);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
	
		//Import profiles
		if (getProfileModule().testAccess((ProfileBinder)user.getParentBinder(), "addEntries")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.import.profiles"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_PROFILES_IMPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
	
		//Definition import
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, "addDefinition")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.import.definitions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_IMPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}

		//Definition export
		if (getDefinitionModule().testAccess(Definition.FOLDER_VIEW, "addDefinition")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.export.definitions"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_EXPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
		
		//templates
		if (getAdminModule().testAccess("addTemplate")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.configure_configurations"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}

		//Report
		if (getReportModule().testAccess("report")) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.report"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_LOGIN_REPORT);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
		
		//For debug - keep off everyones menus
		if (getAdminModule().testAccess("addPosting") &&
				logger.isDebugEnabled()) {
			element = rootElement.addElement(DomTreeBuilder.NODE_CHILD);
			element.addAttribute("title", NLT.get("administration.view_change_log"));
			element.addAttribute("image", "bullet");
			element.addAttribute("id", String.valueOf(nextId++));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG);
			url.setWindowState(WindowState.MAXIMIZED);
			url.setPortletMode(PortletMode.VIEW);
			element.addAttribute("url", url.toString());
		}
		
		model.put(WebKeys.ADMIN_TREE, adminTree);
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
		return new ModelAndView("administration/view", model);
	}
}
