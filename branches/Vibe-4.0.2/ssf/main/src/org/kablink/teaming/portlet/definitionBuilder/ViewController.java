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
package org.kablink.teaming.portlet.definitionBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidException;
import org.kablink.teaming.domain.DefinitionInvalidOperation;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author hurley
 */
@SuppressWarnings("unchecked")
public class ViewController extends SAbstractController {
	@Override
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
		} else if (Validator.isNotNull(operation) && WebHelper.isMethodPost(request)) {
				
			try {
				if (operation.equals("addDefinition")) {
					//Add a new definition type
					String name = PortletRequestUtils.getStringParameter(request,"propertyId_name", "");
					if (name.equals("")) {
						response.setRenderParameter("ss_configErrorMessage", NLT.get("definition.error.nullname"));
					} else {
						String chars = "!@#\\$&%^*()+=?~[]{}<>";
						String badChars = "";
						for (int i = 0; i < chars.length(); i++) {
							if (name.contains(String.valueOf(chars.charAt(i)))) badChars += chars.charAt(i);
						}
						if (!badChars.equals("")) {
							response.setRenderParameter("ss_configErrorMessage", 
									NLT.get("definition.error.invalidCharacter", new String[]{badChars}));
						} else {
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
						}
					}
					
				} else if (operation.equals("modifyDefinition")) {
					//Modify the name of the selected item
					String name = PortletRequestUtils.getStringParameter(request,"propertyId_name", "");
					if (name.equals("")) {
						response.setRenderParameter("ss_configErrorMessage", NLT.get("definition.error.nullname"));
					} else {
						Definition def = DefinitionHelper.getDefinition(selectedItem);
						String chars = "!@#\\$&%^*()+=?~[]{}<>";
						String badChars = "";
						for (int i = 0; i < chars.length(); i++) {
							if (name.contains(String.valueOf(chars.charAt(i)))) badChars += chars.charAt(i);
						}
						if (!badChars.equals("") && !name.equals(def.getName())) {
							response.setRenderParameter("ss_configErrorMessage", 
									NLT.get("definition.error.invalidCharacterInName", new String[]{badChars}));
						} else {
							getDefinitionModule().modifyDefinitionProperties(selectedItem,  new MapInputData(formData));
						}
					}
					
				} else if (operation.equals("setVisibility")) {
					//Modify the name of the selected item
					Long targetBinderId = PortletRequestUtils.getLongParameter(request, "targetId");
					Integer visibility = PortletRequestUtils.getIntParameter(request, "visibility");
					getDefinitionModule().modifyVisibility(selectedItem, visibility, targetBinderId);

				} else if (operation.equals("moveDefinition")) {
					//Modify the name of the selected item
					Long targetBinderId = PortletRequestUtils.getLongParameter(request, "targetId");
					if (targetBinderId != null) {
						Definition def = getDefinitionModule().getDefinition(selectedItem);
						getDefinitionModule().modifyVisibility(selectedItem, def.getVisibility(), targetBinderId);
						response.setRenderParameter("binderId", targetBinderId.toString());
					} else {
						response.setRenderParameter("ss_configErrorMessage", NLT.get("definition.error.unknowTarget"));
					}
				} else if (operation.equals("copyDefinition")) {
					//Add a new definition type
					String name = PortletRequestUtils.getStringParameter(request,"propertyId_name", "");
					String caption = PortletRequestUtils.getStringParameter(request, "propertyId_caption", "", false);
					if (Validator.isNull(caption)) caption = name;
					Definition def;
					if (Validator.isNotNull(name)) {
						if (binderId == null) {
							def = getDefinitionModule().copyDefinition(selectedItem, null, name, caption);			
							selectedItem = def.getId();
						} else {
							def = getDefinitionModule().copyDefinition(selectedItem, getBinderModule().getBinder(binderId), name, caption);			
							selectedItem = def.getId();
								
						}
					}
				} else if (operation.equals("deleteDefinition")) {
					try {
						getDefinitionModule().deleteDefinition(selectedItem);
					} catch(NoDefinitionByTheIdException e) {
							//If the id is already deleted, ignore the error
					}
					selectedItem=null;
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
					//Move the item
					String itemId = PortletRequestUtils.getStringParameter(request, "operationItem", "");
					String targetItemId = PortletRequestUtils.getStringParameter(request, "selectedId", "");
					String location = PortletRequestUtils.getStringParameter(request, "moveTo", "");
					getDefinitionModule().moveItem(selectedItem, itemId, targetItemId, location);
					
				} else if (operation.equals("copyItem")) {
					//Copy the item
					String itemId = PortletRequestUtils.getStringParameter(request, "operationItem", "");
					String targetItemId = PortletRequestUtils.getStringParameter(request, "selectedId", "");
					getDefinitionModule().copyItem(selectedItem, itemId, targetItemId);
					
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
			} catch (DataIntegrityViolationException de) {
				logger.error(NLT.get("definition.error.dataIntegrityIssue"), de);
				response.setRenderParameter("ss_configErrorMessage", NLT.get("definition.error.dataIntegrityIssue"));
			}
		}
		
		if (Validator.isNull(selectedItem)) {
			//return to manage
			response.setRenderParameter(WebKeys.URL_ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
			response.setRenderParameter(WebKeys.URL_OPERATION, "");

		} else {
			//Pass the selection id to be shown on to the rendering phase
			response.setRenderParameter("selectedItem", selectedItem);
		}
	}
		
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		// Put out the ID of the top Vibe workspace.  Required for the
		// LandingPageEditor when rendered in the zone level form and
		// view designer.
		Map model = new HashMap();
		String topWSId = GwtUIHelper.getTopWSIdSafely(this);
		model.put("topWSId", topWSId);
		
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
		try {
			ProfileBinder binder = getProfileModule().getProfileBinder();
			if (getProfileModule().testAccess(binder, ProfileOperation.manageEntries)) {
				model.put(WebKeys.IS_BINDER_ADMIN, true);
			}
		} catch(AccessControlException ex) {}
		String option = PortletRequestUtils.getStringParameter(request, "option", "");		
		Map data = new HashMap();
		model.put("data", data);
		data.put("option", option);
		data.put("selectedItem", selectedItem);
		Document sourceDefinition = def.getDefinition();
		data.put("sourceDefinition", sourceDefinition);
		String title = NLT.getDef(def.getTitle()) + "  (" + def.getName() + ")";
		title = title.replaceAll("&", "&amp;");
		data.put("selectedItemTitle", title);
			
		if (Validator.isNull(option)) {
			if (binderId != null) model.put(WebKeys.BINDER, getBinderModule().getBinder(binderId));

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

			
		} else if ("moveDefinition".equals(option) && binderId != null) {
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			Document wsTree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.BINDER, getBinderModule().getBinder(binderId));
			return new ModelAndView(WebKeys.VIEW_ADMIN_MOVE_DEFINITION, model);
			
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
				Element itemIdCaptionEle = null;
				if (!itemId.equals("")) 
					itemIdCaptionEle = (Element) def.getDefinition().getRootElement().selectSingleNode("//item[@id='"+itemId+"']");
				String itemIdCaption = "";
				if (itemIdCaptionEle != null) itemIdCaption = itemIdCaptionEle.attributeValue("caption", "");
				itemIdCaption = NLT.getDef(itemIdCaption);
				data.put("itemIdCaption", itemIdCaption);
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
				Element nameProp = (Element) properties.selectSingleNode("property[@name='name']");
				Element captionProp = (Element) properties.selectSingleNode("property[@name='caption']");
				if (captionProp != null) {
					if (!captionProp.attributeValue("value", "").equals("")) {
						if (name.equals("entryDataItem") && nameProp != null) {
							caption += " [" +  nameProp.attributeValue("value", "") + "]";
						}
						caption += " - " + NLT.getDef(captionProp.attributeValue("value", ""));
					} else {
						if (nameProp != null) {
							if (!nameProp.attributeValue("value", "").equals("")) {
								caption += " - " + nameProp.attributeValue("value", "");
							}
						}
					}
				}
			}
			
			caption = caption.replaceAll("&", "&amp;");
			caption = caption.replaceAll("<", "&lt;");
			caption = caption.replaceAll(">", "&gt;");
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
