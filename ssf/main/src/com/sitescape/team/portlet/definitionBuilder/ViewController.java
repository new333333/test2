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
package com.sitescape.team.portlet.definitionBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidException;
import com.sitescape.team.domain.DefinitionInvalidOperation;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;

/**
 * @author hurley
 *
 */
public class ViewController extends SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());

		Map formData = request.getParameterMap();
		String selectedItem = PortletRequestUtils.getStringParameter(request,"sourceDefinitionId", "");
		String operation = PortletRequestUtils.getStringParameter(request,"operation", "");
		Long binderId=null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		} catch (Exception ex) {};
		//See if there is an operation to perform
		if (formData.containsKey("cancelBtn")) {
			//The operation was canceled; refresh view
			if (Validator.isNull(selectedItem)) {
				//return to manage
				response.setRenderParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
				response.setRenderParameter(WebKeys.URL_OPERATION, "");
				return;
			}
		} else if (Validator.isNotNull(operation)) {
				
			try {
				if (operation.equals("addDefinition")) {
					//Add a new definition type
					String name = PortletRequestUtils.getStringParameter(request,"propertyId_name", "");
					String caption = PortletRequestUtils.getStringParameter(request, "propertyId_caption", "");
					String operationItem = PortletRequestUtils.getStringParameter(request, "operationItem", "");
					Integer type = PortletRequestUtils.getIntParameter(request, "definitionType_"+operationItem);
					if (Validator.isNotNull(name) && type != null) {
						if (binderId == null) {
							Definition def = getDefinitionModule().addDefinition(null, name, caption, type, new MapInputData(formData));			
							selectedItem = def.getId();
						} else {
							Definition def = getDefinitionModule().addDefinition(getBinderModule().getBinder(binderId), name, caption, type, new MapInputData(formData));			
							selectedItem = def.getId();
							
						}
					}
					
				} else if (operation.equals("modifyDefinition")) {
					//Modify the name of the selected item
					getDefinitionModule().modifyDefinitionProperties(selectedItem,  new MapInputData(formData));
					
				} else if (operation.equals("setVisibility")) {
					//Modify the name of the selected item
					Long targetBinderId = PortletRequestUtils.getLongParameter(request, "targetId");
					Integer visibility = PortletRequestUtils.getIntParameter(request, "visibility");
					getDefinitionModule().modifyVisibility(selectedItem, visibility, targetBinderId);

				} else if (operation.equals("copyDefinition")) {
					//Add a new definition type
					String name = PortletRequestUtils.getStringParameter(request,"propertyId_name", "");
					String caption = PortletRequestUtils.getStringParameter(request, "propertyId_caption", "");
					if (Validator.isNull(caption)) caption = name;
					Definition def = getDefinitionModule().getDefinition(selectedItem);
					Document doc = (Document)def.getDefinition().clone();
					doc.getRootElement().addAttribute("internalId", "");
					doc.getRootElement().addAttribute("databaseId", "");
					if (Validator.isNotNull(name)) {
						if (binderId == null) {
							def = getDefinitionModule().addDefinition(doc, null, name, caption, false);			
							selectedItem = def.getId();
						} else {
							def = getDefinitionModule().addDefinition(doc, getBinderModule().getBinder(binderId), name, caption, false);			
							selectedItem = def.getId();
								
						}
					}
				} else if (operation.equals("deleteDefinition")) {
					try {
						getDefinitionModule().deleteDefinition(selectedItem);
					} catch(NoDefinitionByTheIdException e) {
							//If the id is already deleted, ignore the error
					}
					//return to manage
					response.setRenderParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
					response.setRenderParameter(WebKeys.URL_OPERATION, "");
					return;
					
				} else if (operation.equals("addItem")) {
					//Add the new item
					String itemId = PortletRequestUtils.getStringParameter(request,"selectedId", "");
					String itemToAdd = PortletRequestUtils.getStringParameter(request, "operationItem", "");
					getDefinitionModule().addItem(selectedItem, itemId, itemToAdd,  new MapInputData(formData));
						
				} else if (operation.equals("modifyItem")) {
					//Modify the item
					String itemId = PortletRequestUtils.getStringParameter(request,"selectedId", "");
					getDefinitionModule().modifyItem(selectedItem, itemId, new MapInputData(formData));
					
				} else if (operation.equals("deleteItem")) {
					//Delete the item
					String itemId = PortletRequestUtils.getStringParameter(request,"selectedId", "");
					getDefinitionModule().deleteItem(selectedItem, itemId);
					
				} else if (operation.equals("moveItem")) {
					//Delete the item
					String itemId = PortletRequestUtils.getStringParameter(request, "operationItem", "");
					String targetItemId = PortletRequestUtils.getStringParameter(request, "selectedId", "");
					String location = PortletRequestUtils.getStringParameter(request, "moveTo", "");
					getDefinitionModule().modifyItemLocation(selectedItem, itemId, targetItemId, location);
					
				}
			} catch (DefinitionInvalidOperation e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				response.setRenderParameter("ss_configErrorMessage", e.getLocalizedMessage());
			} catch (DefinitionInvalidException e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				response.setRenderParameter("ss_configErrorMessage", e.getLocalizedMessage());
			} catch(NoDefinitionByTheIdException e) {
				//The selected id must be non-existant. Give an error message
				response.setRenderParameter("ss_configErrorMessage", e.getLocalizedMessage());
				selectedItem = "";
			}
		}
		
		//Pass the selection id to be shown on to the rendering phase
		response.setRenderParameter("selectedItem", selectedItem);
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		Long binderId=null;
		try {
			binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			model.put(WebKeys.BINDER_ID, binderId);
		} catch (Exception ex) {};
		String selectedItem = PortletRequestUtils.getRequiredStringParameter(request, "selectedItem");
		Definition def =  getDefinitionModule().getDefinition(selectedItem);

		Document definitionConfig = getDefinitionModule().getDefinitionConfig();
		model.put(WebKeys.CONFIG_DEFINITION, definitionConfig);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
		model.put(WebKeys.DEFINITION, def);
		String option = PortletRequestUtils.getStringParameter(request, "option", "");		
		Map data = new HashMap();
		model.put("data", data);
		data.put("option", option);
		data.put("selectedItem", selectedItem);
		Document sourceDefinition = def.getDefinition();
		data.put("sourceDefinition", sourceDefinition);
		String title = NLT.getDef(def.getTitle()) + "  (" + def.getName() + ")";
		data.put("selectedItemTitle", title);
			
		if (Validator.isNull(option)) {
						
			//Open the item that was selected
			String nodeOpen = PortletRequestUtils.getStringParameter(request, "selectedId", "");
			data.put("nodeOpen", nodeOpen);
			
			Map idData = new HashMap();
			Map idDataNames = new HashMap();
			Map idDataCaptions = new HashMap();
			data.put("idData", idData);
			idData.put("names",idDataNames);
			idData.put("captions",idDataCaptions);

			Document definitionTree;
			idDataNames.put(def.getId(), def.getName());
			Element sourceRoot = null;
			if (sourceDefinition != null) {
				sourceRoot = sourceDefinition.getRootElement();
			}

			//Build the definition tree
			definitionTree = DocumentHelper.createDocument();
			Element dtRoot = definitionTree.addElement("root");
			dtRoot.addAttribute("title", title);
			dtRoot.addAttribute("id", def.getId());
			
			if (sourceRoot != null) {
				buildDefinitionTree(sourceRoot, dtRoot, idDataNames, idDataCaptions);
			}
	        //There is a forum specified, so get the forum object
			model.put("definitionTree", definitionTree);
			if (formData.containsKey("ss_configErrorMessage")) {
				model.put("ss_configErrorMessage", ((String[]) formData.get("ss_configErrorMessage"))[0]);
			}
			return new ModelAndView(WebKeys.VIEW_DEFINITION, model);

			
		} else {
			response.setContentType("text/xml");			
			Map statusMap = new HashMap();
			model.put(WebKeys.AJAX_STATUS, statusMap);
			if(!WebHelper.isUserLoggedIn(request)) {
				//Signal that the user is not logged in. 
				//  The code on the calling page will output the proper translated message.
				statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			} else {
				//Set up the other data items		
				String itemId = PortletRequestUtils.getStringParameter(request, "itemId", "");		
				data.put("itemId", itemId);
			
				String itemName = PortletRequestUtils.getStringParameter(request, "itemName", "");		
				data.put("itemName", itemName);
			
				String refItemId = PortletRequestUtils.getStringParameter(request, "refItemId", "");		
				data.put("refItemId", refItemId);
				if ("view_definition_options".equals(option)) {
					model.put("ssIsAdmin", Boolean.valueOf(getDefinitionModule().testAccess(null, def.getType(), DefinitionModule.DefinitionOperation.manageDefinition)));
				}
			}
			return new ModelAndView("definition_builder/view_definition_builder_option", model);
		}			
		
	}

    private void buildDefinitionTree(Element sourceElement, Element targetElement, Map idDataNames, Map idDataCaptions) {
		Iterator items = sourceElement.elementIterator("item");
		while (items.hasNext()) {
			Element sourceEle = (Element) items.next();
			Element properties = sourceEle.element("properties");
			String name = NLT.getDef(sourceEle.attributeValue("name"));
			String caption = NLT.getDef(sourceEle.attributeValue("caption"));
			if (properties != null) {
				Element captionProp = (Element) properties.selectSingleNode("property[@name='caption']");
				if (captionProp != null) {
					if (!captionProp.attributeValue("value", "").equals("")) {
						caption += " - " + NLT.getDef(captionProp.attributeValue("value", ""));
					} else {
						Element nameProp = (Element) properties.selectSingleNode("property[@name='name']");
						if (nameProp != null) {
							if (!nameProp.attributeValue("value", "").equals("")) {
								caption += " - " + nameProp.attributeValue("value", "");
							}
						}
					}
				}
			}
			
			caption = caption.replaceAll("&", "&amp;");
			Element targetEle = targetElement.addElement("child");
			targetEle.addAttribute("type", "item");
			targetEle.addAttribute("title", caption);
			targetEle.addAttribute("id", sourceEle.attributeValue("id"));
			idDataNames.put(sourceEle.attributeValue("id"), NLT.getDef(name));
			idDataCaptions.put(sourceEle.attributeValue("id"), caption.replaceAll("'", "\'"));
			
			//See if this element has children to add
			buildDefinitionTree(sourceEle, targetEle, idDataNames, idDataCaptions);
		}
    }

}
