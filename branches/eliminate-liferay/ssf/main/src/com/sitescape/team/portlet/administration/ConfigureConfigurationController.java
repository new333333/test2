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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portlet.administration.ImportDefinitionController.ZipStreamWrapper;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.TemplateCopyHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.ListFolderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;

public class ConfigureConfigurationController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
		if (formData.containsKey("okBtn")) {
			if (WebKeys.OPERATION_ADD.equals(operation)) {
				//adding top level config
				Integer type = PortletRequestUtils.getIntParameter(request, "definitionType");
				if (type == null)  return;
				if (type == -1) {
					Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
					Long configId = getTemplateModule().addTemplateFromBinder(binderId);
					response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				} else {
					Map updates = new HashMap();
					String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
					updates.put(ObjectKeys.FIELD_TEMPLATE_TITLE, sVal);
					sVal = PortletRequestUtils.getStringParameter(request, "description", null);
					updates.put(ObjectKeys.FIELD_TEMPLATE_DESCRIPTION, new Description(sVal));
					sVal = PortletRequestUtils.getStringParameter(request, "templateName", null);
					if (sVal != null) updates.put(ObjectKeys.FIELD_BINDER_NAME, sVal);
					Long configId = getTemplateModule().addTemplate(type, updates);
					TemplateBinder config = getTemplateModule().getTemplate(configId);
					//	redirect to modify binder
					response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getId().toString());
					response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
				}
			} else if (WebKeys.OPERATION_IMPORT.equals(operation)) {
				if (request instanceof MultipartFileSupport) {
					int i=0;
					Map fileMap = ((MultipartFileSupport) request).getFileMap();
					if (fileMap != null) {
						List errors = new ArrayList();
						while (++i>0) {
							MultipartFile myFile=null;
							try {
								myFile = (MultipartFile)fileMap.get("template" + i);
								if (myFile == null) break;
								if (Validator.isNull(myFile.getOriginalFilename())) continue; //not filled in
								Boolean replace = PortletRequestUtils.getBooleanParameter(request,"template" + i + "ck", false);
								if(myFile.getOriginalFilename().toLowerCase().endsWith(".zip")) {
									ZipInputStream zipIn = new ZipInputStream(myFile.getInputStream());
									ZipEntry entry = null;
									while((entry = zipIn.getNextEntry()) != null) {
										loadTemplates(entry.getName(), new ZipStreamWrapper(zipIn), replace, errors);
										zipIn.closeEntry();
									}
								} else {
									loadTemplates(myFile.getOriginalFilename(), myFile.getInputStream(), replace, errors);
								}
								myFile.getInputStream().close();
							} catch (Exception fe) {
								errors.add((myFile==null ? "" : myFile.getOriginalFilename()) + " : " + (fe.getLocalizedMessage()==null ? fe.getMessage() : fe.getLocalizedMessage()));
							}
						}
						if (!errors.isEmpty()) response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
					}
					
				}
				response.setRenderParameter(WebKeys.URL_OPERATION,  WebKeys.OPERATION_IMPORT);
			} else if (WebKeys.OPERATION_RESET.equals(operation)) {
				getTemplateModule().updateDefaultTemplates(RequestContextHolder.getRequestContext().getZoneId());
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
				getBinderModule().modifyBinder(configId, new MapInputData(formData), fileMap, deleteAtts, null);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			} else if (WebKeys.OPERATION_MODIFY_TEMPLATE.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//Get the function id from the form
				Map updates = new HashMap();
				String sVal = PortletRequestUtils.getStringParameter(request, "title", null);
				updates.put(ObjectKeys.FIELD_TEMPLATE_TITLE, sVal);
				sVal = PortletRequestUtils.getStringParameter(request, "description", null);
				updates.put(ObjectKeys.FIELD_TEMPLATE_DESCRIPTION, new Description(sVal));
				sVal = PortletRequestUtils.getStringParameter(request, "templateName", null);
				if (sVal != null) updates.put(ObjectKeys.FIELD_BINDER_NAME, sVal); //should only be present for root templates
			
				getTemplateModule().modifyTemplate(configId, updates);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			} else if (WebKeys.OPERATION_ADD_FOLDER.equals(operation) ||
					WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//Get the function id from the form
				Long srcConfigId = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
				Long newId = getTemplateModule().addTemplate(configId, srcConfigId);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, newId.toString());
				
			} else if (WebKeys.OPERATION_SAVE_FOLDER_COLUMNS.equals(operation)) {
				Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				Map columns = new LinkedHashMap();
				String[] columnNames = new String[] {"number", "title", "comments", "size", "download", "html", "state", "author", "date"};
				for (int i = 0; i < columnNames.length; i++) {
					columns.put(columnNames[i], PortletRequestUtils.getStringParameter(request, columnNames[i], ""));
				}
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (me.getKey().toString().startsWith("customCol_", 0)) {
						String colName = me.getKey().toString().substring(10, me.getKey().toString().length());
						columns.put(colName, "on");
					}
				}
				getBinderModule().setProperty(configId, ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS, columns);
					
				//Reset the column positions to the default
				getProfileModule().setUserProperty(null, configId, WebKeys.FOLDER_COLUMN_POSITIONS, "");
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				response.setRenderParameter(WebKeys.URL_OPERATION, "");
			} 
		//process cancels first
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (!WebKeys.OPERATION_ADD.equals(operation)) { //on add - binderId may be 
				Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				if (configId != null) {
					response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				}
			}
		} else if (WebKeys.OPERATION_DELETE.equals(operation)) {
			//Get the function id from the form
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			TemplateBinder config = getTemplateModule().getTemplate(configId);
			if (!config.isRoot())
				response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getParentBinder().getId().toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
			getBinderModule().deleteBinder(configId);
			//an add without an okBtn - check for reload
		} else if (WebKeys.OPERATION_SET_DISPLAY_DEFINITION.equals(operation)) {
			Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			getProfileModule().setUserProperty(RequestContextHolder.getRequestContext().getUserId(), configId, 
					ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
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
	protected Long loadTemplates(String fileName, InputStream fIn, boolean replace, List errors)
	{
		try {
			SAXReader xIn = new SAXReader();
			Document doc = xIn.read(fIn);  
			return getTemplateModule().addTemplate(doc, replace);
		} catch (Exception fe) {
			errors.add((fileName==null ? "" : fileName) + " : " + (fe.getLocalizedMessage()==null ? fe.getMessage() : fe.getLocalizedMessage()));
		}
		return null;
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		model.put(WebKeys.ERROR_LIST,  request.getParameterValues(WebKeys.ERROR_LIST));
		Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		String path = WebKeys.VIEW_TEMPLATE;
		if (configId != null) {
			TemplateBinder config = getTemplateModule().getTemplate(configId);
		
			model.put(WebKeys.BINDER_CONFIG, config);
			model.put(WebKeys.BINDER, config);
			if (Validator.isNull(operation)) {
				//Build a reload url
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, configId.toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
				model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
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
						model.put(WebKeys.PROFILE_CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
						model.put(WebKeys.PROFILE_CONFIG_ENTRY, user);
					} 
				} else {
					ListFolderHelper.getShowTemplate(this, request, response, config, model);
				}
				model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_TEMPLATE);
				Tabs tabs = Tabs.getTabs(null);
				tabs.findTab(config, true);
				model.put(WebKeys.TABS, tabs);
					
			} else  if (WebKeys.OPERATION_ADD_FOLDER.equals(operation)) {
				List<TemplateBinder> configs = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
				model.put(WebKeys.BINDER_CONFIGS, configs);
				model.put(WebKeys.OPERATION, operation);				
				
			} else  if (WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				List<TemplateBinder> configs = getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
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
				model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
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
				String definitionType = PortletRequestUtils.getStringParameter(request, "definitionType", String.valueOf(Definition.FOLDER_VIEW));
				if (definitionType.equals("-1")) {
					Document wsTree = getBinderModule().getDomBinderTree(RequestContextHolder.getRequestContext().getZone().getId(), new WsDomTreeBuilder(null, true, this, new TemplateCopyHelper()), 1);									
					model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
				}
				model.put("definitionType", definitionType);
				path = WebKeys.VIEW_MODIFY_TEMPLATE;
		} else if (WebKeys.OPERATION_IMPORT.equals(operation)) {
			model.put(WebKeys.OPERATION, operation);
			path = WebKeys.VIEW_IMPORT_TEMPLATE;
		} else if (WebKeys.OPERATION_RESET.equals(operation)) {
			model.put(WebKeys.OPERATION, operation);
			path = WebKeys.VIEW_IMPORT_TEMPLATE;
		} else if (WebKeys.OPERATION_EXPORT.equals(operation)) {
			List<TemplateBinder> configs = getTemplateModule().getTemplates();

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
			PortletURL url;

			Toolbar toolbar = new Toolbar();
			Map qualifiers = new HashMap();
			qualifiers.put("onClick", "{return true}");

			toolbar.addToolbarMenu("1_add", NLT.get("administration.toolbar.add"), "");
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
			url.setParameter("definitionType", String.valueOf(Definition.FOLDER_VIEW));
			toolbar.addToolbarMenuItem("1_add", "", NLT.get("general.type.folder"), url, qualifiers);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
			url.setParameter("definitionType", String.valueOf(Definition.WORKSPACE_VIEW));
			toolbar.addToolbarMenuItem("1_add", "", NLT.get("general.type.workspace"), url, qualifiers);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
			url.setParameter("definitionType", "-1");
			toolbar.addToolbarMenuItem("1_add", "", NLT.get("administration.configure_cfg.clone"), url, qualifiers);
			
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RESET);
			toolbar.addToolbarMenu("3_reload", NLT.get("administration.toolbar.reset"), url);
			
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_IMPORT);
			toolbar.addToolbarMenu("4_import", NLT.get("administration.toolbar.import"), url);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_EXPORT);
			toolbar.addToolbarMenu("5_export", NLT.get("administration.toolbar.export"), url);
			
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());

			List<TemplateBinder> configs = getTemplateModule().getTemplates();
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
		boolean manager = getBinderModule().testAccess(config, BinderOperation.setProperty);
		Map qualifiers = new HashMap();
		Map qualifiersBlock = new HashMap();
		qualifiersBlock.put("onClick", "{return true}");
		if (manager) {
			//Add Folder
			if (config.getEntityType().equals(EntityType.folder)) {
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
				toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url, qualifiersBlock);
			} else {
				//	must be workspace
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
				toolbar.addToolbarMenuItem("1_administration", "workspace", NLT.get("toolbar.menu.addWorkspaceTemplate"), url, qualifiersBlock);
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
				toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url, qualifiersBlock);			
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
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.modify_template"), url, qualifiersBlock);		
			
		}
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.manageThisTarget"));

		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, config.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.accessControl"), url, qualifiersBlock);
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
		url.setParameter(WebKeys.URL_BINDER_ID, configId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, config.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url, qualifiersBlock);

		if (manager) {
			//Modify target
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.modify_target"), url, qualifiersBlock);

		}
		
		//	The "Manage dashboard" menu
		BinderHelper.buildDashboardToolbar(request, response, this, config, dashboardToolbar, model);
		
		model.put(WebKeys.FORUM_TOOLBAR,  toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR,  dashboardToolbar.getToolbar());
	}
	

}
