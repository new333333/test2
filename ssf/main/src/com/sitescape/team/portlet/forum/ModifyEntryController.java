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
package com.sitescape.team.portlet.forum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.FolderConfigHelper;
import com.sitescape.team.web.tree.TreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class ModifyEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
	
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_DELETE)) {
			FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
			if (!entry.isTop()) entry = entry.getTopEntry();
			else entry = null;
			getFolderModule().deleteEntry(folderId, entryId);
			if (entry != null) 	setupViewEntry(response, folderId, entry.getId());
			else setupViewFolder(response, folderId);		
		
		} else if (op.equals(WebKeys.OPERATION_LOCK)) {
			getFolderModule().reserveEntry(folderId, entryId);
			setupViewEntry(response, folderId, entryId);
		
		} else if (op.equals(WebKeys.OPERATION_UNLOCK)) {
			getFolderModule().unreserveEntry(folderId, entryId);
			setupViewEntry(response, folderId, entryId);
			
		} else if (op.equals(WebKeys.OPERATION_START_WORKFLOW)) {
			String workflowType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKFLOW_TYPE, "");
			FolderEntry fEntry = getFolderModule().getEntry(folderId, entryId);
    		getFolderModule().addEntryWorkflow(folderId, entryId, workflowType, null);
    		setupViewEntry(response, folderId, entryId);
			
		} else if (op.equals(WebKeys.OPERATION_STOP_WORKFLOW)) {
			String workflowType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKFLOW_TYPE, "");
    		getFolderModule().deleteEntryWorkflow(folderId, entryId, workflowType);
			setupViewEntry(response, folderId, entryId);
			
		} else if (formData.containsKey("okBtn") || formData.containsKey("applyBtn")) {
			if (Validator.isNull(op)) {

				//See if the add entry form was submitted
				//The form was submitted. Go process it
				Map fileMap=null;
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
			
				getFolderModule().modifyEntry(folderId, entryId, 
						new MapInputData(formData), fileMap, deleteAtts, null, null);
								
				//See if the user wants to send mail
				BinderHelper.sendMailOnEntryCreate(this, request, folderId, entryId);

				//See if the user wants to subscribe to this entry
				BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
				
				if (formData.containsKey("okBtn")) {
					setupReloadOpener(response, folderId, entryId);
				} else {
					response.setRenderParameters(formData);
				}
				
			} else if (op.equals(WebKeys.OPERATION_MOVE)) {
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) {
					PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
					portletSession.setAttribute(ObjectKeys.SESSION_SAVE_LOCATION_ID, destinationId);
					getFolderModule().moveEntry(folderId, entryId, destinationId, null);
					setupViewFolder(response, folderId);		
				} else {
					setupViewEntry(response, folderId, entryId);
				}
			} else if (op.equals(WebKeys.OPERATION_COPY)) {
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) {
					PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
					portletSession.setAttribute(ObjectKeys.SESSION_SAVE_LOCATION_ID, destinationId);
					getFolderModule().copyEntry(folderId, entryId, destinationId, null);
				} 
				setupViewEntry(response, folderId, entryId);
				
			}
			
		} else if (formData.containsKey("editElementBtn")) {
			Map inputData = new HashMap(formData);
			//Before modifying the entry, check if the user is only modifying one section
			if (inputData.containsKey(WebKeys.URL_ELEMENT_TO_EDIT) && 
					inputData.containsKey(WebKeys.URL_SECTION_TO_EDIT)) {
				String elementToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT_TO_EDIT, "");
				String sectionToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SECTION_TO_EDIT, "");
				if (!sectionToEdit.equals("")) {
					String newSectionText = PortletRequestUtils.getStringParameter(request, elementToEdit, "");
					//This is a request to edit just one section of the text; get the section to be edited
					FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
					//Start by getting the original full text of the element in the entry
					String elementText = "";
					if (elementToEdit.equals("description")) {
						elementText = entry.getDescription().getText();
					} else {
						CustomAttribute ca = entry.getCustomAttribute(elementToEdit);
						elementText = ca.getValue().toString();
					}
					//Next, split that text into its sections
					List<Map> bodyParts = WebHelper.markupSplitBySection(elementText);
					String newElementText = "";
					//Now, find the section being edited and replace the original section text with the new text
					for (Map part : bodyParts) {
						String sectionNumber = (String) part.get("sectionNumber");
						if (sectionNumber == null) sectionNumber = "";
						String prefix = (String) part.get("prefix");
						if (prefix == null) prefix = "";
						String sectionText = (String) part.get("sectionText");
						if (sectionText == null) sectionText = "";
						
						newElementText = newElementText +  prefix;
						if (sectionToEdit.equals(sectionNumber)) {
							newElementText = newElementText +  newSectionText;
						} else {
							newElementText = newElementText + sectionText;
						}
					}
					//Finally, replace the section text with the new full element text in the input data
					inputData.put(elementToEdit, newElementText);
				}
			}
			getFolderModule().modifyEntry(folderId, entryId, 
					new MapInputData(inputData), null, null, null, null);
			setupReloadOpener(response, folderId, entryId);

		} else if (formData.containsKey("cancelBtn")) {
			//	The user clicked the cancel button
			if (Validator.isNull(op)) {
				setupReloadOpener(response, folderId, entryId);
			} else {
				setupViewEntry(response, folderId, entryId);
			}
		
		} else {
			response.setRenderParameters(formData);		
		}
	}

	private void setupReloadOpener(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}

	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		response.setRenderParameter(WebKeys.IS_REFRESH, "1");
	}
	private void setupViewFolder(ActionResponse response, Long folderId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		
		String elementToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT_TO_EDIT, "");
		String path;
		FolderEntry entry = null;
		if (op.equals(WebKeys.OPERATION_MOVE) || op.equals(WebKeys.OPERATION_COPY)) {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, entry.getParentFolder());
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			Document wsTree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.OPERATION, op);
			PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
			Long id = (Long)portletSession.getAttribute(ObjectKeys.SESSION_SAVE_LOCATION_ID);
			
			if(id != null) {
				try {
					Binder binder = getBinderModule().getBinder(id);
					model.put(WebKeys.DEFAULT_SAVE_LOCATION, binder);
					model.put(WebKeys.DEFAULT_SAVE_LOCATION_ID, id);
				} catch (Exception ex) {
					portletSession.removeAttribute(ObjectKeys.SESSION_SAVE_LOCATION_ID);
				}
			}			
			path = WebKeys.VIEW_MOVE_ENTRY;
		} else {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			model.put(WebKeys.DOM_TREE, getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1));

			model.put(WebKeys.ENTRY, entry);
			Subscription sub = getFolderModule().getSubscription(entry);
			model.put(WebKeys.SUBSCRIPTION, sub);
			
			model.put(WebKeys.FOLDER, entry.getParentFolder());
			model.put(WebKeys.BINDER, entry.getParentFolder());
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
			DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@type='form']");

			//Build the mashup beans
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			DefinitionHelper.buildMashupBeans(this, entry.getParentBinder(), configDocument, model);

			if (elementToEdit.equals("")) {
				path = WebKeys.VIEW_MODIFY_ENTRY;
			} else {
				if (DefinitionHelper.getDefinitionElement(entry.getEntryDef(), model, elementToEdit)) {
					String sectionToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SECTION_TO_EDIT, "");
					model.put(WebKeys.SECTION_NUMBER, sectionToEdit);
					if (!sectionToEdit.equals("")) {
						//This is a request to edit just one section of the text; get the section to be edited
						String elementText = "";
						if (elementToEdit.equals("description")) {
							elementText = entry.getDescription().getText();
						} else {
							CustomAttribute ca = entry.getCustomAttribute(elementToEdit);
							elementText = ca.getValue().toString();
						}
						List<Map> bodyParts = WebHelper.markupSplitBySection(elementText);
						String sectionText = "";
						for (Map part : bodyParts) {
							if (sectionToEdit.equals(part.get("sectionNumber"))) {
								sectionText = (String) part.get("sectionText");
								break;
							}
						}
						model.put(WebKeys.SECTION_TEXT, sectionText);
					}
					path = WebKeys.VIEW_MODIFY_ENTRY_ELEMENT;
				} else {
					path = WebKeys.VIEW_MODIFY_ENTRY;
				}
			}
		} 
			
		return new ModelAndView(path, model);
	}
}

