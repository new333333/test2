package com.sitescape.team.portlet.administration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.util.Validator;

public class ConfigureConfigurationController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
		if (WebKeys.OPERATION_ADD.equals(operation)) {
			//adding top level config
			int type = PortletRequestUtils.getIntParameter(request, "cfgType");
			Map updates = new HashMap();
			String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
			updates.put("templateTitle", sVal);
			sVal = PortletRequestUtils.getStringParameter(request, "description", null);
			updates.put("templateDescription", new Description(sVal));
			sVal = PortletRequestUtils.getStringParameter(request, "iconName", null);
			updates.put("iconName", sVal);
			Long configId = getAdminModule().addTemplate(type, updates);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
		} else if (WebKeys.OPERATION_MODIFY.equals(operation)) {
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			if (formData.containsKey("okBtn")) {
				//Get the function id from the form
				Map updates = new HashMap();
				Boolean val = PortletRequestUtils.getBooleanParameter(request, "library", false);
				updates.put("library", val);
				val = PortletRequestUtils.getBooleanParameter(request, "uniqueTitles", false);
				updates.put("uniqueTitles", val);
				String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
				updates.put("title", sVal);
				sVal = PortletRequestUtils.getStringParameter(request, "description", null);
				updates.put("description", new Description(sVal));
			
				getAdminModule().modifyTemplate(configId, updates);
				response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			} 
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
		} else if (WebKeys.OPERATION_MODIFY_TEMPLATE.equals(operation)) {
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			if (formData.containsKey("okBtn")) {
				//Get the function id from the form
				Map updates = new HashMap();
				String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
				updates.put("templateTitle", sVal);
				sVal = PortletRequestUtils.getStringParameter(request, "description", null);
				updates.put("templateDescription", new Description(sVal));
				sVal = PortletRequestUtils.getStringParameter(request, "iconName", null);
				updates.put("iconName", sVal);
			
				getAdminModule().modifyTemplate(configId, updates);
				response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY_TEMPLATE);
			} 
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
		} else if (WebKeys.OPERATION_DELETE.equals(operation)) {
			//Get the function id from the form
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			TemplateBinder config = getAdminModule().getTemplate(configId);
			if (!config.isRoot())
				response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getParentBinder().getId().toString());
				
			getAdminModule().deleteTemplate(configId);
		} else if (WebKeys.OPERATION_ADD_FOLDER.equals(operation) ||
				WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			if (formData.containsKey("okBtn")) {
				//Get the function id from the form
				Long srcConfigId = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
				Long newId = getAdminModule().addTemplate(configId, srcConfigId);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, newId.toString());
			} else 	response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (configId != null) {
			TemplateBinder config = getAdminModule().getTemplate(configId);
		
			model.put(WebKeys.BINDER_CONFIG, config);
			model.put(WebKeys.BINDER, config);
			if (WebKeys.OPERATION_RELOAD_LISTING.equals(operation)) {
				//An action is asking us to build the url
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, configId.toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				request.setAttribute("ssReloadUrl", reloadUrl.toString());			
				return new ModelAndView("administration/reload_opener");
			} else if (Validator.isNull(operation)) {
				model.put(WebKeys.DEFINITION_ENTRY, config);
				DefinitionHelper.getDefinitions(config, model, "");
				User user = RequestContextHolder.getRequestContext().getUser();
				Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
				model.put(WebKeys.USER_PROPERTIES, userProperties);
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), config.getId());
				model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
				DashboardHelper.getDashboardMap(config, userProperties, model);

				buildToolbar(request, response, config, model);
				if (!config.isRoot() || !config.getBinders().isEmpty()) 
					BinderHelper.buildNavigationLinkBeans(this, config, model, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION));
			} else  if (WebKeys.OPERATION_ADD_FOLDER.equals(operation)) {
				List<TemplateBinder> configs = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
				model.put(WebKeys.BINDER_CONFIGS, configs);
				model.put(WebKeys.OPERATION, operation);				
				
			} else  if (WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				List<TemplateBinder> configs = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
				configs.addAll(getAdminModule().getTemplates(Definition.WORKSPACE_VIEW));
				model.put(WebKeys.OPERATION, operation);				
				model.put(WebKeys.BINDER_CONFIGS, configs);
			} else
				model.put(WebKeys.OPERATION, operation);
				if (operation.equals(WebKeys.OPERATION_MODIFY_TEMPLATE)) {
					return new ModelAndView(WebKeys.VIEW_MODIFY_TEMPLATE, model);
			}
			
		} else if (WebKeys.OPERATION_ADD.equals(operation)) {
				model.put(WebKeys.OPERATION, operation);				
				model.put("cfgType", PortletRequestUtils.getStringParameter(request, "cfgType", String.valueOf(Definition.FOLDER_VIEW)));
				return new ModelAndView(WebKeys.VIEW_MODIFY_TEMPLATE, model);
		} else {
			List<TemplateBinder> configs = getAdminModule().getTemplates();
			model.put(WebKeys.BINDER_CONFIGS, configs);

		}
		return new ModelAndView(WebKeys.VIEW_TEMPLATE, model);
		
	}
	protected void buildToolbar(RenderRequest request, 
			RenderResponse response, TemplateBinder config, Map model) {
		//Build the toolbar arrays
		Toolbar toolbar = new Toolbar();
		//	The "Add" menu
		PortletURL url;
		//The "Administration" menu
		toolbar.addToolbarMenu("1_administration", NLT.get("toolbar.manageThisTemplate"));
		String configId = config.getId().toString();
		//Add Folder
		if (config.getEntityType().equals(EntityType.folder)) {
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url);
		} else {
			//must be workspace
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
			toolbar.addToolbarMenuItem("1_administration", "workspace", NLT.get("toolbar.menu.addWorkspaceTemplate"), url);
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
			toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url);			
		}
		//Delete config
		Map qualifiers = new HashMap();
		qualifiers.put("onClick", "return ss_confirmDeleteConfig();");
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.delete_template"), url, qualifiers);		

		//Modify config
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY_TEMPLATE);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.modify_template"), url);		
			

		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.manageThisTarget"));

		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, config.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.accessControl"), url);
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, config.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);

		//Modify target
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, config.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.modify_target"), url);		

		
		//	The "Manage dashboard" menu
		//See if the dashboard is being shown in the definition
		if (DefinitionHelper.checkIfBinderShowingDashboard(config)) {
			boolean dashboardContentExists = false;
			Map ssDashboard = (Map)model.get(WebKeys.DASHBOARD);
			if (ssDashboard != null && ssDashboard.containsKey(WebKeys.DASHBOARD_COMPONENTS_LIST)) {
				Map dashboard = (Map)ssDashboard.get("dashboard");
				if (dashboard != null) {
					dashboardContentExists = DashboardHelper.checkIfContentExists(dashboard);
				}
			}
			
			//This folder is showing the dashboard
			toolbar.addToolbarMenu("3_manageDashboard", NLT.get("toolbar.manageDashboard"));
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
			toolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);

			if (dashboardContentExists) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				toolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);
	
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DASHBOARD_TITLE);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter("_scope", "local");
				toolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.setTitle"), url);
	
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter("_scope", "global");
				toolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.global"), url);
	
				//Check the access rights of the user
				try {
					getBinderModule().checkAccess(config, "setProperty");
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, configId);
					url.setParameter("_scope", "binder");
					toolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.binder"), url);
				} catch(AccessControlException e) {};
	
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
						response.getNamespace() + "_dashboardComponentCanvas', 'binderId="+
						configId+"');return false;");
				if (DashboardHelper.checkIfShowingAllComponents(config)) {
					toolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					toolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
			}
		}
		
		model.put(WebKeys.FORUM_TOOLBAR,  toolbar.getToolbar());
	}
	

}
