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
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portlet.forum.ListFolderController;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.servlet.forum.ViewFileController;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.TemplateCopyHelper;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;

public class ConfigureConfigurationController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
		if (formData.containsKey("okBtn")) {
			if (WebKeys.OPERATION_ADD.equals(operation)) {
				//adding top level config
				int type = PortletRequestUtils.getIntParameter(request, "cfgType");
				if (type == -1) {
					Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
					Long configId = getAdminModule().addTemplateFromBinder(binderId);
					response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				} else if (type == -2) {
					int i=0;
					Map errorMap = new HashMap();
					while (++i>0) {
						String data;
						try {
							data = PortletRequestUtils.getStringParameter(request, "template" + i);
						} catch (Exception ex) {continue;}
				    	if (data == null) break;
				    	try {
					    	StringReader fIn = new StringReader(data);
				    		SAXReader xIn = new SAXReader();
				    		Document doc = xIn.read(fIn);   
				    		fIn.close();
				    		getAdminModule().addTemplate(doc);
				    	} catch (Exception fe) {
//				    		errorMap.put(entry.getKey(), fe.getLocalizedMessage());	
				    		logger.error(fe.getLocalizedMessage(), fe);
				    	}
					}

				} else {
					Map updates = new HashMap();
					String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
					updates.put("templateTitle", sVal);
					sVal = PortletRequestUtils.getStringParameter(request, "description", null);
					updates.put("templateDescription", new Description(sVal));
					Long configId = getAdminModule().addTemplate(type, updates);
					TemplateBinder config = getAdminModule().getTemplate(configId);
					//	redirect to modify binder
					response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getId().toString());
					response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				}
			} else if (WebKeys.OPERATION_EXPORT.equals(operation)) {
				List errors = new ArrayList();
				HashSet<String> uniqueFilenames = new HashSet<String>();
				String dirPath = SPropsUtil.getDirPath("data.root.dir") + File.separator + "templates" +
					File.separator + RequestContextHolder.getRequestContext().getZoneName();
				FileHelper.mkdirsIfNecessary(dirPath);
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (((String)me.getKey()).startsWith("id_")) {
						String defId = ((String)me.getKey()).substring(3);
						if (Validator.isNotNull(defId)) {
							TemplateBinder binder =null;
							try {
								binder = getAdminModule().getTemplate(Long.valueOf(defId));
								Document doc = getAdminModule().getTemplateAsXml(binder);
								String name = binder.getName();
								if (Validator.isNull(name)) name = binder.getTemplateTitle();
								// explicity set encoding so their is no mistake.
								//cannot guarentee default will be set to UTF-8
								String filePath = dirPath + File.separator +  name + ".xml";
								XmlFileUtil.writeFile(doc, filePath);
								uniqueFilenames.add(filePath);
							} catch (Exception ex) {
								errors.add(ex.getLocalizedMessage()==null ? ex.getMessage() : ex.getLocalizedMessage());
							}
						}
					}
				}
				
				Document listOfFiles = ViewFileController.createFileListingForZipDownload("templates.zip");
				for(String path : uniqueFilenames) {
					ViewFileController.addFileToList(listOfFiles, path);
				}
				File listOfFilesTempFile = TempFileUtil.createTempFile("exportTemplates");
				XmlFileUtil.writeFile(listOfFiles, listOfFilesTempFile.getAbsolutePath());

				response.setRenderParameter(WebKeys.DOWNLOAD_URL, 
						WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?viewType=zipped&fileId=" +
						listOfFilesTempFile.getName());
				response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
				response.setRenderParameter("redirect", "true");
				
						
			} else if (WebKeys.OPERATION_MODIFY.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//	The modify form was submitted. Go process it
				Map fileMap = null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
				} else {
					fileMap = new HashMap();
				}
				Set deleteAtts = new HashSet();
				for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
					Map.Entry e = (Map.Entry)iter.next();
					String key = (String)e.getKey();
					if (key.startsWith("_delete_")) {
						deleteAtts.add(key.substring(8));
					}
					
				}
				getBinderModule().modifyBinder(configId, new MapInputData(formData), fileMap, deleteAtts);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			} else if (WebKeys.OPERATION_MODIFY_TEMPLATE.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//Get the function id from the form
				Map updates = new HashMap();
				String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
				updates.put("templateTitle", sVal);
				sVal = PortletRequestUtils.getStringParameter(request, "description", null);
				updates.put("templateDescription", new Description(sVal));
				sVal = PortletRequestUtils.getStringParameter(request, "iconName", null);
				updates.put("iconName", sVal);
			
				getAdminModule().modifyTemplate(configId, updates);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			} else if (WebKeys.OPERATION_ADD_FOLDER.equals(operation) ||
					WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//Get the function id from the form
				Long srcConfigId = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
				Long newId = getAdminModule().addTemplate(configId, srcConfigId);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, newId.toString());
				
			}
		} else if (WebKeys.OPERATION_DELETE.equals(operation)) {
			//Get the function id from the form
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			TemplateBinder config = getAdminModule().getTemplate(configId);
			if (!config.isRoot())
				response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getParentBinder().getId().toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
			getBinderModule().deleteBinder(configId);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (!WebKeys.OPERATION_ADD.equals(operation)) { //on add - binderId may be 
				Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				if (configId != null) {
					response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
					response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
				}
			}
		} else if (WebKeys.OPERATION_SET_DISPLAY_DEFINITION.equals(operation)) {
			Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			getProfileModule().setUserProperty(RequestContextHolder.getRequestContext().getUserId(), configId, 
					ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
//		} else if (WebKeys.OPERATION_SET_CALENDAR_DISPLAY_MODE.equals(operation)) {
//			Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
//			getProfileModule().setUserProperty(RequestContextHolder.getRequestContext().getUserId(), configId, 
//					ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE, 
//					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
//			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
//			response.setRenderParameter(WebKeys.URL_OPERATION, "");
		} else if (WebKeys.OPERATION_CALENDAR_GOTO_DATE.equals(operation)) {
			Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			Date dt = (new MapInputData(formData)).getDateValue("ss_goto");
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, dt);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
		
		} else
			response.setRenderParameters(formData);
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		if (!Validator.isNull(request.getParameter("redirect"))) {
			String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
			model.put(WebKeys.ERROR_LIST, errors);
			model.put(WebKeys.DOWNLOAD_URL, PortletRequestUtils.getStringParameter(request, WebKeys.DOWNLOAD_URL, ""));
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT, model);
		}
		Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		String path = WebKeys.VIEW_TEMPLATE;
		if (configId != null) {
			TemplateBinder config = getAdminModule().getTemplate(configId);
		
			model.put(WebKeys.BINDER_CONFIG, config);
			model.put(WebKeys.BINDER, config);
			if (WebKeys.OPERATION_RELOAD_LISTING.equals(operation)) {
				//An action is asking us to build the url
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, configId.toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				request.setAttribute(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());			
				return new ModelAndView("administration/reload_opener");
			} else if (Validator.isNull(operation)) {
				model.put(WebKeys.DEFINITION_ENTRY, config);
				User user = RequestContextHolder.getRequestContext().getUser();
				Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
				model.put(WebKeys.USER_PROPERTIES, userProperties);
				UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), config.getId());
				model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
				//See if the user has selected a specific view to use
				String userDefaultDef = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
				DefinitionHelper.getDefinitions(config, model, userDefaultDef);
				DashboardHelper.getDashboardMap(config, userProperties, model);
				buildToolbar(request, response, config, model);
				if (!config.isRoot() || !config.getBinders().isEmpty()) 
					BinderHelper.buildNavigationLinkBeans(this, config, model, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION));
				if (config.getEntityType().equals(EntityType.workspace)) {
					model.put(WebKeys.WORKSPACE_DOM_TREE, BinderHelper.buildTemplateTreeRoot(this, config, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION)));

					if (config.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) {
						//use current user as prototype
						Document profileDef = user.getEntryDef().getDefinition();
						model.put(WebKeys.PROFILE_CONFIG_DEFINITION, profileDef);
						model.put(WebKeys.PROFILE_CONFIG_ELEMENT, 
								profileDef.getRootElement().selectSingleNode("//item[@name='profileEntryBusinessCard']"));
						model.put(WebKeys.PROFILE_CONFIG_JSP_STYLE, "view");
						model.put(WebKeys.PROFILE_CONFIG_ENTRY, user);
					} 
				} else {
					ListFolderController.getShowTemplate(request, response, config, model);
				}
				model.put(WebKeys.CONFIG_JSP_STYLE, "template");
				Tabs tabs = new Tabs(null);
				tabs.addTab(config);
				model.put(WebKeys.TABS, tabs.getTabs());
					
			} else  if (WebKeys.OPERATION_ADD_FOLDER.equals(operation)) {
				List<TemplateBinder> configs = getAdminModule().getTemplates(Definition.FOLDER_VIEW);
				model.put(WebKeys.BINDER_CONFIGS, configs);
				model.put(WebKeys.OPERATION, operation);				
				
			} else  if (WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				List<TemplateBinder> configs = getAdminModule().getTemplates(Definition.WORKSPACE_VIEW);
				model.put(WebKeys.OPERATION, operation);				
				model.put(WebKeys.BINDER_CONFIGS, configs);
			} else if (WebKeys.OPERATION_ADD.equals(operation)) {
				//since we have a config, we now want to configure the target.
				//turn this into a modify operation
				PortletURL reloadUrl = response.createActionURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, configId.toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
				request.setAttribute("ssReloadUrl", reloadUrl.toString());			
				return new ModelAndView("administration/reload_opener");
			} else if (WebKeys.OPERATION_MODIFY.equals(operation)) {
				// just added the template, now configure the target, treat as a modify
				Binder binder = getBinderModule().getBinder(configId);				
				model.put(WebKeys.BINDER, binder);
				model.put(WebKeys.CONFIG_JSP_STYLE, "form");
				Definition binderDef = binder.getEntryDef();
				if (binderDef == null) {
					DefinitionHelper.getDefaultBinderDefinition(binder, model, "//item[@type='form']");
				} else {
					DefinitionHelper.getDefinition(binderDef, model, "//item[@type='form']");
				}
				path = WebKeys.VIEW_MODIFY_TEMPLATE;	
				model.put(WebKeys.OPERATION, operation);
			} else {
				model.put(WebKeys.OPERATION, operation);
				if (operation.equals(WebKeys.OPERATION_MODIFY_TEMPLATE)) {
					path = WebKeys.VIEW_MODIFY_TEMPLATE;
				}
			}
			
		} else if (WebKeys.OPERATION_ADD.equals(operation)) {
				model.put(WebKeys.OPERATION, operation);
				String cfgType = PortletRequestUtils.getStringParameter(request, "cfgType", String.valueOf(Definition.FOLDER_VIEW));
				if (cfgType.equals("-1")) {
					Document wsTree = getWorkspaceModule().getDomWorkspaceTree(null, new WsDomTreeBuilder(null, true, this, new TemplateCopyHelper()), 1);									
					model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
				}
				model.put("cfgType", cfgType);
				path = WebKeys.VIEW_MODIFY_TEMPLATE;
		} else if (WebKeys.OPERATION_EXPORT.equals(operation)) {
			List<TemplateBinder> configs = getAdminModule().getTemplates();

			//Build the definition tree
			Document definitionTree = DocumentHelper.createDocument();
			Element dtRoot = definitionTree.addElement(DomTreeBuilder.NODE_ROOT);
			dtRoot.addAttribute("title", NLT.get("administration.configure_cfg"));
			dtRoot.addAttribute("id", "templates");
			dtRoot.addAttribute("displayOnly", "true");
			dtRoot.addAttribute("url", "");

			for (TemplateBinder tb:configs) {
				Element treeEle = dtRoot.addElement("child");
				treeEle.addAttribute("type", "template");
				treeEle.addAttribute("title", NLT.getDef(tb.getTemplateTitle()));
				treeEle.addAttribute("id", tb.getId().toString());	
				treeEle.addAttribute("displayOnly", "false");
				treeEle.addAttribute("url", "");
			}
			
			model.put(WebKeys.DOM_TREE, definitionTree);
			model.put(WebKeys.OPERATION, operation);				
		} else {
			List<TemplateBinder> configs = getAdminModule().getTemplates();
			model.put(WebKeys.BINDER_CONFIGS, configs);

		}
		return new ModelAndView(path, model);
		
	}
	protected void buildToolbar(RenderRequest request, 
			RenderResponse response, TemplateBinder config, Map model) {
		//Build the toolbar arrays
		Toolbar toolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		//	The "Add" menu
		PortletURL url;
		//The "Administration" menu
		toolbar.addToolbarMenu("1_administration", NLT.get("toolbar.manageThisTemplate"));
		String configId = config.getId().toString();
		//see if have rights to change anything
		boolean manager = getBinderModule().testAccess(config, "setProperty");
		Map qualifiers = new HashMap();
		if (manager) {
			//Add Folder
			if (config.getEntityType().equals(EntityType.folder)) {
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
				toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url);
			} else {
				//	must be workspace
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
			
		}
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

		if (manager) {
			//Modify target
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.modify_target"), url);		
		}
		
		//	The "Manage dashboard" menu
		//See if the dashboard is being shown in the definition
		if (DefinitionHelper.checkIfBinderShowingDashboard(config)) {
			Map ssDashboard = (Map)model.get(WebKeys.DASHBOARD);
			boolean dashboardContentExists = DashboardHelper.checkIfAnyContentExists(ssDashboard);
			
			//This folder is showing the dashboard
			dashboardToolbar.addToolbarMenu("3_manageDashboard", NLT.get("toolbar.manageDashboard"));
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
			dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);

			if (dashboardContentExists) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);
	
	
				//Check the access rights of the user
				if (manager) {
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, configId);
					url.setParameter("_scope", "binder");
					dashboardToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.binder"), url);
				}
	
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
						response.getNamespace() + "_dashboardComponentCanvas', 'binderId="+
						configId+"');return false;");
				
				if (DashboardHelper.checkIfShowingAllComponents(config)) {
					qualifiers.put("icon", "dashboard_hide.gif");
					dashboardToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					qualifiers.put("icon", "dashboard_show.gif");
					dashboardToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
			}
		}
		
		model.put(WebKeys.FORUM_TOOLBAR,  toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR,  dashboardToolbar.getToolbar());
	}
	

}
