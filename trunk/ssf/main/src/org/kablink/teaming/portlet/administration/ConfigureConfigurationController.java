/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.util.ZipEntryStream;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.TemplateCopyHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class ConfigureConfigurationController extends  SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION);
		if ((formData.containsKey("okBtn") || formData.containsKey("applyBtn")) && WebHelper.isMethodPost(request)) {
			Long parentBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_PARENT_ID);
			Long entrySourceBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_SOURCE_BINDER_ID);	
			Long originalEntrySourceBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ORIGINAL_ENTRY_SOURCE_BINDER_ID);	
			Boolean clearEntrySourceBinderId = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_CLEAR_ENTRY_SOURCE_BINDER_ID, false);
			Binder parentBinder = null;
			if (parentBinderId != null) {
				//Make sure there is access to the parent binder
				parentBinder = (Binder)getBinderModule().getBinder(parentBinderId);
				response.setRenderParameter(WebKeys.URL_BINDER_PARENT_ID, parentBinderId.toString());
			}
			if (WebKeys.OPERATION_ADD.equals(operation)) {
				//adding a new template
				Integer type = PortletRequestUtils.getIntParameter(request, "definitionType");
				if (type == null)  return;
				if (type == -1) {
					Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
					if (binderId == null) {
						String[] errors = new String[1];
						errors[0] = NLT.get("error.noBinderSelected");
						response.setRenderParameter(WebKeys.ERROR_LIST, errors);
					} else {
						//Check if this is a legal binder to copy
						String[] errors = new String[1];
						Binder binder = (Binder)getBinderModule().getBinder(binderId);
						if (!getTemplateModule().checkIfBinderValidForTemplate(binder, errors)) {
							//This binder cannot be turned into a tempalte
							response.setRenderParameter(WebKeys.ERROR_LIST, errors);
						} else {
							TemplateBinder template = getTemplateModule().addTemplateFromBinder(parentBinder, binderId);
							if (template == null) {
								errors[0] = NLT.get("error.binderCannotBeUsedAsTemplate");
								response.setRenderParameter(WebKeys.ERROR_LIST, errors);
							}
							Long configId = template.getId();
							response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
						}
					}
				} else {
					Map updates = new HashMap();
					String sVal = PortletRequestUtils.getStringParameter(request, "title", null, false);
					updates.put(ObjectKeys.FIELD_TEMPLATE_TITLE, sVal);
					sVal = PortletRequestUtils.getStringParameter(request, "description", null, false);
					updates.put(ObjectKeys.FIELD_TEMPLATE_DESCRIPTION, new Description(Html.stripHtml(sVal)));
					sVal = PortletRequestUtils.getStringParameter(request, "templateName", null);
					if (sVal != null) updates.put(ObjectKeys.FIELD_BINDER_NAME, sVal);
					updates.put(ObjectKeys.FIELD_TEMPLATE_ENTRY_SOURCE_BINDER_ID, entrySourceBinderId);
					Long configId = getTemplateModule().addTemplate(parentBinder, type, updates).getId();
					TemplateBinder config = getTemplateModule().getTemplate(configId);
					if (config == null) {
						String[] errors = new String[1];
						errors[0] = NLT.get("error.template.notAllowedToUse");
						response.setRenderParameter(WebKeys.ERROR_LIST, errors);
					} else {
						//	redirect to modify binder
						response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getId().toString());
						response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
					}
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
										loadTemplates(parentBinder, entry.getName(), new ZipEntryStream(zipIn), replace, errors);
										zipIn.closeEntry();
									}
								} else {
									loadTemplates(parentBinder, myFile.getOriginalFilename(), myFile.getInputStream(), replace, errors);
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
				getTemplateModule().updateDefaultTemplates(RequestContextHolder.getRequestContext().getZoneId(), true);
	    		getProfileModule().setUserProperty(user.getId(), ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES, "true");
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
	   			if (formData.containsKey("applyBtn")) response.setRenderParameters(formData);
			} else if (WebKeys.OPERATION_MODIFY_TEMPLATE.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//Get the function id from the form
				Map updates = new HashMap();
				String sVal = PortletRequestUtils.getStringParameter(request, "title", null, false);
				updates.put(ObjectKeys.FIELD_TEMPLATE_TITLE, sVal);
				sVal = PortletRequestUtils.getStringParameter(request, "description", null, false);
				updates.put(ObjectKeys.FIELD_TEMPLATE_DESCRIPTION, new Description(Html.stripHtml(sVal)));
				sVal = PortletRequestUtils.getStringParameter(request, "templateName", null);
				if (sVal != null) updates.put(ObjectKeys.FIELD_BINDER_NAME, Html.stripHtml(sVal)); //should only be present for root templates
				if (!clearEntrySourceBinderId && entrySourceBinderId == null) {
					//Keep the current source folder
					entrySourceBinderId = originalEntrySourceBinderId;
				}
				updates.put(ObjectKeys.FIELD_TEMPLATE_ENTRY_SOURCE_BINDER_ID, entrySourceBinderId);

				getTemplateModule().modifyTemplate(configId, updates);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			} else if (WebKeys.OPERATION_ADD_FOLDER.equals(operation) ||
					WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
				//Get the function id from the form
				Long srcConfigId = PortletRequestUtils.getRequiredLongParameter(request, "binderConfigId");
				Long newId = getTemplateModule().addTemplate(configId, srcConfigId).getId();
				response.setRenderParameter(WebKeys.URL_BINDER_ID, newId.toString());
				
			} else if (WebKeys.OPERATION_SAVE_FOLDER_COLUMNS.equals(operation)) {
				Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				BinderHelper.saveFolderColumnSettings(this, request, response, configId);					
				response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				response.setRenderParameter(WebKeys.URL_OPERATION, "");
			} else {
				//probably something we don't support in templates
				Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				if (configId != null) response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				response.setRenderParameter(WebKeys.URL_OPERATION, "");
			}
		//process cancels first
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			if (!WebKeys.OPERATION_ADD.equals(operation)) { //on add - binderId may be 
				Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
				if (configId != null) {
					response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
				}
				Long parentBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_PARENT_ID);
				if (parentBinderId != null) {
					//Make sure there is access to the parent binder
					response.setRenderParameter(WebKeys.URL_BINDER_PARENT_ID, parentBinderId.toString());
				}
			}
		} else if (WebKeys.OPERATION_DELETE.equals(operation) && WebHelper.isMethodPost(request)) {
			//Get the function id from the form
			Long configId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
			TemplateBinder config = getTemplateModule().getTemplate(configId);
			if (config == null) {
				String[] errors = new String[1];
				errors[0] = NLT.get("error.template.notAllowedToUse");
				response.setRenderParameter(WebKeys.ERROR_LIST, errors);
			} else {
				if (!config.isRoot())
					response.setRenderParameter(WebKeys.URL_BINDER_ID, config.getParentBinder().getId().toString());
				response.setRenderParameter(WebKeys.URL_OPERATION, "");
				getBinderModule().deleteBinder(configId);
				//an add without an okBtn - check for reload
			}
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
		
		} else if (WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO.equals(operation)) {
			//userproperty => doesn't make sense on templates
			Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, configId.toString());
			response.setRenderParameter(WebKeys.URL_OPERATION, "");
			
		} else response.setRenderParameters(formData);
	}
	protected Long loadTemplates(Binder parentBinder, String fileName, InputStream fIn, boolean replace, List errors)
	{
		try {
			SAXReader xIn = XmlUtil.getSAXReader();
			Document doc = xIn.read(fIn);  
			return getTemplateModule().addTemplate(parentBinder, doc, replace).getId();
		} catch (Exception fe) {
			errors.add((fileName==null ? "" : fileName) + " : " + (fe.getLocalizedMessage()==null ? fe.getMessage() : fe.getLocalizedMessage()));
		}
		return null;
	}
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		model.put(WebKeys.ERROR_LIST,  request.getParameterValues(WebKeys.ERROR_LIST));
		Long configId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Long parentBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_PARENT_ID);
		Long entrySourceBinderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_SOURCE_BINDER_ID);	
		Binder parentBinder = null;
		if (parentBinderId != null) {
			//Make sure there is access to the parent binder
			parentBinder = (Binder)getBinderModule().getBinder(parentBinderId);
		}
		model.put(WebKeys.CONFIGURE_CONFIGURATION_LOCAL, false);
		if (parentBinderId != null) {
			model.put(WebKeys.CONFIGURE_CONFIGURATION_LOCAL, true);
			model.put(WebKeys.URL_BINDER_PARENT_ID, String.valueOf(parentBinderId));
		}
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		String path = WebKeys.VIEW_TEMPLATE;
		if (configId != null) {
			TemplateBinder config = getTemplateModule().getTemplate(configId);
			if (config == null) {
				throw new Exception(NLT.get("error.template.notAllowedToUse"));
			}
			//See if this is a local template binder.
			parentBinderId = config.getTemplateOwningBinderId();
			model.put(WebKeys.CONFIGURE_CONFIGURATION_LOCAL, false);
			model.put(WebKeys.URL_BINDER_PARENT_ID, "");
			if (parentBinderId != null) {
				parentBinder = (Binder)getBinderModule().getBinder(parentBinderId);
				model.put(WebKeys.CONFIGURE_CONFIGURATION_LOCAL, true);
				model.put(WebKeys.URL_BINDER_PARENT_ID, String.valueOf(parentBinderId));
			}
			model.put(WebKeys.BINDER_CONFIG, config);
			model.put(WebKeys.BINDER, config);
			model.put(WebKeys.FOLDER, config); //some jsps still look for folder
			Long templateEntrySourceBinderId = config.getTemplateEntrySourceBinderId();
			if (templateEntrySourceBinderId != null) {
				model.put(WebKeys.BINDER_CONFIG_ENTRY_SOURCE_BINDER_ID, templateEntrySourceBinderId);
				try {
					Binder esb = getBinderModule().getBinder(templateEntrySourceBinderId);
					model.put(WebKeys.BINDER_CONFIG_ENTRY_SOURCE_BINDER, esb);
				} catch(Exception e) {}
			}
			if (WebKeys.OPERATION_ADD_FOLDER.equals(operation)) {
				List<TemplateBinder> configs = getTemplateModule().getTemplates(Definition.FOLDER_VIEW);
				if (parentBinder != null) {
					configs.addAll(getTemplateModule().getTemplates(Definition.FOLDER_VIEW, parentBinder, true, true));
				}
				model.put(WebKeys.BINDER_CONFIGS, configs);
				model.put(WebKeys.OPERATION, operation);								
			} else  if (WebKeys.OPERATION_ADD_WORKSPACE.equals(operation)) {
				List<TemplateBinder> configs = getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
				if (parentBinder != null) {
					configs.addAll(getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW, parentBinder, true, true));
				}
				model.put(WebKeys.OPERATION, operation);				
				model.put(WebKeys.BINDER_CONFIGS, configs);
			} else if (WebKeys.OPERATION_ADD.equals(operation)) {
				//since we have a config, we now want to configure the target.
				//turn this into a modify operation
				PortletURL reloadUrl = response.createActionURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, configId.toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
				if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
					reloadUrl.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
				}
				request.setAttribute("ssReloadUrl", reloadUrl.toString());			
				return new ModelAndView("administration/reload_current");
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
				//Build the mashup beans
				Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
				DefinitionHelper.buildMashupBeans(this, binder, configDocument, model, request );

			} else if (WebKeys.OPERATION_MODIFY_TEMPLATE.equals(operation)) {
				model.put(WebKeys.OPERATION, operation);
				path = WebKeys.VIEW_MODIFY_TEMPLATE;
			} else {
				//Build a reload url
				PortletURL reloadUrl = response.createRenderURL();
				reloadUrl.setParameter(WebKeys.URL_BINDER_ID, configId.toString());
				reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
				if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
					reloadUrl.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
				}
				model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
				model.put(WebKeys.DEFINITION_ENTRY, config);
				BinderHelper.setupStandardBeans(this, request, response, model, configId);
				User user = RequestContextHolder.getRequestContext().getUser();
				Map userProperties = (Map)model.get(WebKeys.USER_PROPERTIES);
				UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
				//See if the user has selected a specific view to use
				String userDefaultDef = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
				DefinitionHelper.getDefinitions(config, model, userDefaultDef);
				DashboardHelper.getDashboardMap(config, userProperties, model);
				buildToolbar(request, response, config, model);
				if (!config.isRoot() || !config.getBinders().isEmpty()) 
					BinderHelper.buildNavigationLinkBeans(this, config, model, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION));
				if (config.getEntityType().equals(EntityType.workspace)) {
					model.put(WebKeys.WORKSPACE_DOM_TREE, BinderHelper.buildTemplateTreeRoot(this, config, new BinderHelper.ConfigHelper(WebKeys.ACTION_CONFIGURATION)));

					if (config.getDefinitionType() != null && 
							(config.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) ||
							 config.getDefinitionType() == Definition.EXTERNAL_USER_WORKSPACE_VIEW) {
						//use current user as prototype
						Document profileDef = user.getEntryDefDoc();
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
				Tabs tabs = Tabs.getTabs(request);
				tabs.findTab(config, true);
				model.put(WebKeys.TABS, tabs);
				model.put(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
				model.put(WebKeys.OPERATION, ""); //make sure not set to anyting we don't support

				Binder binder = getBinderModule().getBinder(configId);				
				Definition binderDef = binder.getEntryDef();
				if (binderDef != null) {
					Document configDocument = binderDef.getDefinition();
					String viewType = DefinitionUtils.getViewType(configDocument);
					if (viewType == null) viewType = "";
					model.put(WebKeys.VIEW_TYPE, viewType);
				}
				//Build the mashup beans (if any)
				Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
				DefinitionHelper.buildMashupBeans(this, config, configDocument, model, request );
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
			List<TemplateBinder> configs = new ArrayList<TemplateBinder>();
			if (parentBinderId == null) {
				//Get all of the global templates
				configs = getTemplateModule().getTemplates(Boolean.TRUE);
			} else {
				//Get just the local templates
				configs = getTemplateModule().getTemplates(parentBinder);
			}
			
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
			qualifiers.put("onClick", "return true;");

			toolbar.addToolbarMenu("1_add", NLT.get("administration.toolbar.add"), "");
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
			url.setParameter("definitionType", String.valueOf(Definition.FOLDER_VIEW));
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenuItem("1_add", "", NLT.get("general.type.folder"), url, qualifiers);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
			url.setParameter("definitionType", String.valueOf(Definition.WORKSPACE_VIEW));
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenuItem("1_add", "", NLT.get("general.type.workspace"), url, qualifiers);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD);
			url.setParameter("definitionType", "-1");
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenuItem("1_add", "", NLT.get("administration.configure_cfg.clone"), url, qualifiers);
			
			if (parentBinderId == null) {
				//Don't offer this option if doing local templates
				url = response.createRenderURL();
				url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RESET);
				if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
					url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
				}
				toolbar.addToolbarMenu("3_reload", NLT.get("administration.toolbar.reset"), url);
			}
			
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_IMPORT);
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenu("4_import", NLT.get("administration.toolbar.import"), url);
			url = response.createRenderURL();
			url.setParameter(WebKeys.URL_ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_EXPORT);
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenu("5_export", NLT.get("administration.toolbar.export"), url);
			
			model.put(WebKeys.TOOLBAR, toolbar.getToolbar());

			List<TemplateBinder> configs = new ArrayList<TemplateBinder>();
			if (parentBinderId == null) {
				configs = getTemplateModule().getTemplates(Boolean.TRUE);
			} else {
				configs = getTemplateModule().getTemplates(parentBinder);
			}
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
		if (manager) {
			//Add Folder
			if (config.getEntityType().equals(EntityType.folder)) {
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
				if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
					url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
				}
				toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url, qualifiersBlock);
			} else {
				//	must be workspace
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
				if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
					url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
				}
				toolbar.addToolbarMenuItem("1_administration", "workspace", NLT.get("toolbar.menu.addWorkspaceTemplate"), url, qualifiersBlock);
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
				url.setParameter(WebKeys.URL_BINDER_ID, configId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FOLDER);
				if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
					url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
				}
				toolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolderTemplate"), url, qualifiersBlock);			
			}
			//Delete config
			qualifiers.put("onClick", "return ss_confirmDeleteConfig(this);");
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.delete_template"), url, qualifiers);		

			//Modify config
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY_TEMPLATE);
			url.setParameter(WebKeys.URL_BINDER_ID, configId);
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.modify_template"), url, qualifiersBlock);		
			//export config
			String webUrl  = WebUrlUtil.getServletRootURL(request) + "templateDownload?id_" + configId.toString() + "=on";
			toolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.export_template"), webUrl, qualifiersBlock);		
			
		}
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.manageThisTarget"));

		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_WORKAREA_ID, config.getWorkAreaId().toString());
		url.setParameter(WebKeys.URL_WORKAREA_TYPE, config.getWorkAreaType());
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
			if ((Boolean) model.get(WebKeys.CONFIGURE_CONFIGURATION_LOCAL)) {
				url.setParameter(WebKeys.URL_BINDER_PARENT_ID, (String) model.get(WebKeys.URL_BINDER_PARENT_ID));
			}
			toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.modify_target"), url, qualifiersBlock);

		}
		
		//	The "Manage dashboard" menu
		BinderHelper.buildDashboardToolbar(request, response, this, config, dashboardToolbar, model);
		
		model.put(WebKeys.FORUM_TOOLBAR,  toolbar.getToolbar());
		model.put(WebKeys.DASHBOARD_TOOLBAR,  dashboardToolbar.getToolbar());
	}
}
