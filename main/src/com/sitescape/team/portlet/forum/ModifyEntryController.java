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
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.tree.FolderConfigHelper;

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
    		getFolderModule().addEntryWorkflow(folderId, entryId, workflowType);
    		setupViewEntry(response, folderId, entryId);
			
		} else if (op.equals(WebKeys.OPERATION_STOP_WORKFLOW)) {
			String workflowType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKFLOW_TYPE, "");
    		getFolderModule().deleteEntryWorkflow(folderId, entryId, workflowType);
			setupViewEntry(response, folderId, entryId);
			
		} else if (formData.containsKey("okBtn")) {
			if (op.equals("")) {

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
						new MapInputData(formData), fileMap, deleteAtts, null);
								
				//See if the user wants to send mail
				BinderHelper.sendMailOnEntryCreate(this, request, folderId, entryId);

				//See if the user wants to subscribe to this entry
				BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
				
				setupReloadOpener(response, folderId, entryId);
				
				//See what type of view the folder is using
				Binder binder = null;
				try {
					binder = getBinderModule().getBinder(folderId);
				} catch(NoBinderByTheIdException e) {}
				if (binder != null) {
			        User user = RequestContextHolder.getRequestContext().getUser();
			        UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), folderId);
					Map model = new HashMap();
					DefinitionHelper.getDefinitions(binder, model, (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
					String viewType = "";
					Element configElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
					if (configElement != null) {
						viewType = DefinitionUtils.getPropertyValue(configElement, "type");
						if (viewType == null) viewType = "";
					}
					if (viewType.equals(Definition.VIEW_STYLE_CALENDAR)) {
						//In calendar view, we want to refresh the folder, too
//						setupReloadOpenerParent(response, folderId, entryId);
					}
				}
			} else if (op.equals(WebKeys.OPERATION_MOVE)) {
				//must be move entry
				long[] destinationIds = PortletRequestUtils.getLongParameters(request, "destination");
				Long destinationId = null;
				if (destinationIds != null && destinationIds.length > 0) destinationId = destinationIds[destinationIds.length - 1];
				if (destinationId != null) {
					PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
					portletSession.setAttribute(WebKeys.DEFAULT_SAVE_LOCATION_ID, destinationId);
					getFolderModule().moveEntry(folderId, entryId, new Long(destinationId));
					setupViewFolder(response, folderId);		
				} else {
					setupViewEntry(response, folderId, entryId);
				}
			}
			
		} else if (formData.containsKey("editElementBtn")) {
			getFolderModule().modifyEntry(folderId, entryId, 
					new MapInputData(formData), new HashMap(), null, null);
			setupReloadOpener(response, folderId, entryId);

		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupReloadOpener(response, folderId, entryId);
		
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
	private void setupReloadOpenerParent(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER_PARENT);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
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
		String elementToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT_TO_EDIT, "");
		String path;
		FolderEntry entry = null;
		if (op.equals(WebKeys.OPERATION_MOVE)) {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, entry.getParentFolder());
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			
			PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
			Long id = (Long)portletSession.getAttribute(WebKeys.DEFAULT_SAVE_LOCATION_ID);
			
			if(id != null)
			{
				model.put(WebKeys.DEFAULT_SAVE_LOCATION_ID, id);
				Binder binder = getBinderModule().getBinder(id);
				model.put(WebKeys.DEFAULT_SAVE_LOCATION, binder);
			}
			
			path = WebKeys.VIEW_MOVE_ENTRY;
		} else {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			
			Workspace ws = getWorkspaceModule().getWorkspace();
			model.put(WebKeys.DOM_TREE, getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1));

			model.put(WebKeys.ENTRY, entry);
			Subscription sub = getFolderModule().getSubscription(entry);
			model.put(WebKeys.SUBSCRIPTION, sub);
			
			model.put(WebKeys.FOLDER, entry.getParentFolder());
			model.put(WebKeys.CONFIG_JSP_STYLE, "form");
			DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@type='form']");
			if (elementToEdit.equals("")) {
				path = WebKeys.VIEW_MODIFY_ENTRY;
			} else {
				DefinitionHelper.getDefinitionElement(entry.getEntryDef(), model, elementToEdit);
				path = WebKeys.VIEW_MODIFY_ENTRY_ELEMENT;
			}
		} 
			
		return new ModelAndView(path, model);
	}
}

