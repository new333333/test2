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
package org.kablink.teaming.portlet.forum;

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

import org.kablink.teaming.DataQuotaException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.antivirus.VirusDetectedException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.FolderConfigHelper;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings("unchecked")
public class ModifyEntryController extends SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		if (folderId == null) folderId = entry.getParentFolder().getId();
		if (op.equals(WebKeys.OPERATION_DELETE) && WebHelper.isMethodPost(request)) {
			Binder parentBinder = entry.getParentBinder();
			if (!entry.isTop()) entry = entry.getTopEntry();
			else entry = null;
			String  purgeImmediately   = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PURGE_IMMEDIATELY, "false");
			boolean doPurgeImmediately = parentBinder.isMirrored() || Boolean.parseBoolean(purgeImmediately);
			if (doPurgeImmediately)
			     getFolderModule().deleteEntry(   folderId, entryId);
			else TrashHelper.preDeleteEntry(this, folderId, entryId);
			setupViewFolder(response, folderId);
			
			// If we just deleted or purged an entry from a task
			// folder...
			Folder folder = ((Folder) getBinderModule().getBinder(folderId));
			if (TaskHelper.isTaskFolderType(folder)) {
				// ...we need to update the task folder's linkage to
				// ...reflect the change.
				TaskHelper.removeTaskFromLinkage(this, folder, entryId);
			}
			
			//Force the user's status to be updated.
			BinderHelper.updateUserStatus(folderId, user);
		
		} else if (op.equals(WebKeys.OPERATION_LOCK) && WebHelper.isMethodPost(request)) {
			getFolderModule().reserveEntry(folderId, entryId);
			setupViewEntry(response, folderId, entryId);
		
		} else if (op.equals(WebKeys.OPERATION_UNLOCK) && WebHelper.isMethodPost(request)) {
			getFolderModule().unreserveEntry(folderId, entryId);
			setupViewEntry(response, folderId, entryId);
			
		} else if (op.equals(WebKeys.OPERATION_START_WORKFLOW) && WebHelper.isMethodPost(request)) {
			String workflowType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKFLOW_TYPE, "");
			getFolderModule().getEntry(folderId, entryId);
    		getFolderModule().addEntryWorkflow(folderId, entryId, workflowType, null);
    		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
    		url.setParameter(WebKeys.URL_BINDER_ID, folderId.toString());
    		url.setParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
    		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
    		response.sendRedirect(url.toString());
			
		} else if (op.equals(WebKeys.OPERATION_STOP_WORKFLOW) && WebHelper.isMethodPost(request)) {
			String workflowType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_WORKFLOW_TYPE, "");
    		getFolderModule().deleteEntryWorkflow(folderId, entryId, workflowType);
			setupViewEntry(response, folderId, entryId);
			
		} else if ((formData.containsKey("okBtn") || formData.containsKey("applyBtn")) && WebHelper.isMethodPost(request)) {
			if (Validator.isNull(op)) {

				//See if the add entry form was submitted
				//The form was submitted. Go process it
				Map fileMap=null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
					formData = MiscUtil.defaultTitleToFilename(fileMap, formData);
				} else {
					fileMap = new HashMap();
				}
				
				//Look for requests to create files
				BinderHelper.processCreateFileRequests(this, request, fileMap);
				
				Set deleteAtts = new HashSet();
				for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
					Map.Entry e = (Map.Entry)iter.next();
					String key = (String)e.getKey();
					if (key.startsWith("_delete_")) {
						deleteAtts.add(key.substring(8));
					}
				}
			
				try {
					getFolderModule().modifyEntry(folderId, entryId, 
						new MapInputData(formData), fileMap, deleteAtts, null, null);
				} catch(WriteEntryDataException e) {
		    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		    		return;
				} catch(WriteFilesException e) {
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getMessage());
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		    		return;
				} catch (VirusDetectedException e) {
		    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getLocalizedMessage("<br/>"));
		    		return;
				}
				
				try {
					// If we just modified an entry in a task folder...
					Folder folder = ((Folder) getBinderModule().getBinder(folderId));
					if (TaskHelper.isTaskFolderType(folder)) {
						// ...mark it so that task management knows
						// ...what changed.
						getProfileModule().setUserProperty(user.getId(), folderId, ObjectKeys.BINDER_PROPERTY_TASK_CHANGE, ObjectKeys.BINDER_PROPERTY_TASK_MODIFIED);
						getProfileModule().setUserProperty(user.getId(), folderId, ObjectKeys.BINDER_PROPERTY_TASK_ID,     String.valueOf(entryId));
					}
				}
				catch (Exception ex) {}
				
				//Mark this entry as having been seen by the current user
				getProfileModule().setSeen(null, entry);
				
				//Force the user's status to be updated.
				BinderHelper.updateUserStatus(folderId, entryId, user);

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
					try {
						getFolderModule().moveEntry(folderId, entryId, destinationId, null, null);
					} catch(DataQuotaException e) {
			    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
			    		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			    		return;
					}
					setupViewFolder(response, folderId);

					//Force the user's status to be updated.  Note that
					//we update it from both the source folder's
					//perspective (in case it was the user's MiniBlog
					//folder) and the destination folder's perspective
					//(in case it was the user's MiniBlog folder.)
					BinderHelper.updateUserStatus(folderId, user);
					BinderHelper.updateUserStatus(destinationId, entryId, user);
					
				} else {
					setupViewEntry(response, folderId, entryId);
				}
			} else if (op.equals(WebKeys.OPERATION_COPY)) {
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) {
					PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
					portletSession.setAttribute(ObjectKeys.SESSION_SAVE_LOCATION_ID, destinationId);
					try {
						//Bug: 859044 (pmh) - when copying, start the workflow at the same state
						Map options = new HashMap();
						options.put(ObjectKeys.WORKFLOW_START_WORKFLOW, ObjectKeys.WORKFLOW_START_WORKFLOW_COPY);
						getFolderModule().copyEntry(folderId, entryId, destinationId, null, options);
					} catch(Exception e) {
			    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
			    		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
			    		return;
					}
				} 
				setupViewFolder(response, folderId);
				
			}
			
		} else if (formData.containsKey("editElementBtn") && WebHelper.isMethodPost(request)) {
			Map inputData = new HashMap(formData);
			//Before modifying the entry, check if the user is only modifying one section
			if (inputData.containsKey(WebKeys.URL_ELEMENT_TO_EDIT) && 
					inputData.containsKey(WebKeys.URL_SECTION_TO_EDIT)) {
				String elementToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT_TO_EDIT, "");
				String sectionToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SECTION_TO_EDIT, "");
				if (!sectionToEdit.equals("")) {
					String newSectionText = PortletRequestUtils.getStringParameter(request, elementToEdit, "", false);	// Bugzilla 936898 (DRF:20150620):  false -> Don't XSS check this.  That screws up the quoting of the HTML embedded by tinyMCE.
					//This is a request to edit just one section of the text; get the section to be edited
					//Start by getting the original full text of the element in the entry
					String elementText = "";
					if (elementToEdit.equals("description")) {
						elementText = entry.getDescription().getText();
					} else {
						CustomAttribute ca = entry.getCustomAttribute(elementToEdit);
						elementText = ca.getValue().toString();
					}
					//Next, split that text into its sections
					List<Map> bodyParts = MarkupUtil.markupSplitBySection(elementText);
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

					//Force the user's status to be updated.
					BinderHelper.updateUserStatus(folderId, entryId, user);
				}
			}
			try {
				getFolderModule().modifyEntry(folderId, entryId, 
						new MapInputData(inputData), null, null, null, null);
				setupReloadOpener(response, folderId, entryId);
			} catch(WriteEntryDataException e) {
	    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
	    		return;
			} catch(WriteFilesException e) {
	    		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, e.getMessage());
	    		return;
			}
			//Mark this entry as having been seen by the current user
			getProfileModule().setSeen(null, entry);

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
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, "");
	}
	
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long folderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);				

		Map model = new HashMap();	
		BinderHelper.setupStandardBeans(this, request, response, model, folderId);
		BinderHelper.setupBinderQuotaBeans(this, request, response, model, folderId);
		
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		String errorMsg = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_DATA_PROCESSING_ERRORS, "");
		String errorMsg2 = PortletRequestUtils.getStringParameter(request, WebKeys.FILE_PROCESSING_ERRORS, "");
		if (errorMsg.equals("")) {
			errorMsg = errorMsg2;
		} else if (!errorMsg.equals("") && !errorMsg2.equals("")) {
			errorMsg += "<br/>" + errorMsg2;
		}
		model.put(WebKeys.FILE_PROCESSING_ERRORS, errorMsg);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		
		if (!errorMsg.equals("")) {
			model.put(WebKeys.ERROR_MESSAGE, errorMsg);
			String path = "forum/reload_previous_page";
			return new ModelAndView(path, model);
		}
		String elementToEdit = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ELEMENT_TO_EDIT, "");
		String path;
		FolderEntry entry = null;
		if (op.equals(WebKeys.OPERATION_MOVE) || op.equals(WebKeys.OPERATION_COPY)) {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.BINDER, entry.getParentFolder());
			try {
				Workspace ws = getWorkspaceModule().getTopWorkspace();
				model.put(WebKeys.WORKSPACE_DOM_TREE, getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1));
			} catch(AccessControlException e) {}
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
			
			try {
				Workspace ws = getWorkspaceModule().getTopWorkspace();
				model.put(WebKeys.DOM_TREE, getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this, new FolderConfigHelper()),1));
			} catch(AccessControlException e) {}

			model.put(WebKeys.ENTRY, entry);
			Subscription sub = getFolderModule().getSubscription(entry);
			model.put(WebKeys.SUBSCRIPTION, sub);
			
			model.put(WebKeys.FOLDER, entry.getParentFolder());
			model.put(WebKeys.BINDER, entry.getParentFolder());
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
			DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@type='form']");
			
			Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
			accessControlMap.put(WebKeys.MODIFY_ENTRY_RIGHTS_SET, true);
			if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) 
				accessControlMap.put(WebKeys.MODIFY_ENTRY_ALLOWED, true);
			if (getFolderModule().testAccess(entry, FolderOperation.modifyEntryFields)) 
				accessControlMap.put(WebKeys.MODIFY_ENTRY_FIELDS_ALLOWED, true);

			//Build the mashup beans
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			DefinitionHelper.buildMashupBeans(this, entry.getParentBinder(), configDocument, model, request );

			if (elementToEdit.equals("")) {
				path = WebKeys.VIEW_MODIFY_ENTRY;
			} else {
				if (DefinitionHelper.getDefinitionElement(entry.getEntryDefDoc(), model, elementToEdit)) {
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
						List<Map> bodyParts = MarkupUtil.markupSplitBySection(elementText);
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
