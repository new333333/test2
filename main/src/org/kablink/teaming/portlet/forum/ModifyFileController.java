/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
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
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.web.util.BinderHelper;
import org.springframework.web.portlet.ModelAndView;

import org.kablink.teaming.DataQuotaException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.FolderConfigHelper;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class ModifyFileController extends SAbstractController {
	@SuppressWarnings("unchecked")
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);				
		String entityType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ENTITY_TYPE);				
		String fileId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		DefinableEntity entity = null;
		Binder binder = null;
		if (entityType.equals(EntityType.folderEntry.name())) {
			entity = getFolderModule().getEntry(null, entityId);
			binder = entity.getParentBinder();
			setupViewEntry(response, entity.getParentBinder().getId(), entity.getId());
		} else if (entityType.equals(EntityType.folder.name()) || entityType.equals(EntityType.workspace.name())) {
			entity = getBinderModule().getBinder(entityId);
			binder = (Binder)entity;
			setupViewFolder(response, entity.getId());
		}
		if (entity != null) {
			//Get the file version
			Set<Attachment> attachments = entity.getAttachments();
			FileAttachment fileAtt = null;
			VersionAttachment fileVer = null;
			for (Attachment attachment : attachments) {
				if (attachment instanceof FileAttachment) {
					if (attachment.getId().equals(fileId)) {
						fileAtt = (FileAttachment)attachment;
						break;
					}
					fileVer = ((FileAttachment)attachment).findFileVersionById(fileId);
					fileAtt = fileVer;
					if (fileAtt != null) break;
				}
			}

			if (op.equals(WebKeys.OPERATION_DELETE) && WebHelper.isMethodPost(request)) {
				if(fileAtt != null)
					getBinderModule().deleteFileVersion(binder, entity, fileAtt);
			} else if (op.equals(WebKeys.OPERATION_DELETE_MULTIPLE_VERSIONS) && WebHelper.isMethodPost(request)) {
				//Get the file version
				Set<Attachment> attachments2 = entity.getAttachments();
				Set<String> formDataKeySet = formData.keySet();
				for (String key : formDataKeySet) {
					if (key.indexOf("delete_version_") == 0) {
						String fileId2 = key.substring(15);
						FileAttachment fileAtt2 = null;
						VersionAttachment fileVer2 = null;
						for (Attachment attachment : attachments2) {
							if (attachment instanceof FileAttachment) {
								if (attachment.getId().equals(fileId2)) {
									fileAtt2 = (FileAttachment)attachment;
									break;
								}
								fileVer2 = ((FileAttachment)attachment).findFileVersionById(fileId2);
								fileAtt2 = fileVer2;
								if (fileAtt2 != null) break;
							}
						}
						//Delete multiple versions (but not the top version)
						if (fileAtt2 != null && fileAtt2 instanceof VersionAttachment) {
							getFileModule().deleteVersion(binder, entity, (VersionAttachment)fileAtt2);
						}
					}
				}
				BinderHelper.indexEntity(entity);
				
			} else if (op.equals(WebKeys.OPERATION_MODIFY_FILE_DESCRIPTION) && WebHelper.isMethodPost(request)) {
				//The form was submitted. Go process it
				if (fileAtt != null) {
					String text = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DESCRIPTION, "", false);
					getBinderModule().setFileVersionNote(entity, fileAtt, text);
				}
				
			} else if (op.equals(WebKeys.OPERATION_MODIFY_FILE_REVERT) && WebHelper.isMethodPost(request)) {
				//The form was submitted. Go process it
				if (fileVer != null && !entity.getParentBinder().isMirrored()) {
					getBinderModule().promoteFileVersionCurrent(entity, fileVer);
				}
				
			} else if (op.equals(WebKeys.OPERATION_MODIFY_FILE_MAJOR_VERSION) && WebHelper.isMethodPost(request)) {
				//The form was submitted. Go process it
				if (fileAtt != null) {
					getBinderModule().incrementFileMajorVersion(entity, fileAtt);
				}
			}

			if (formData.containsKey("okBtn")) {
				setupReloadOpener(response, entity);
			}
		}
	}

	private void setupReloadOpener(ActionResponse response, DefinableEntity entity) {
		//return to view entity
		if (entity.getEntityType().equals(EntityType.folder) || entity.getEntityType().equals(EntityType.workspace)) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, entity.getId().toString());
		} else {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
			response.setRenderParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, entity.getId().toString());
		}
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
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);				
		String entityType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ENTITY_TYPE);				
		String fileId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, "");				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		DefinableEntity entity = null;
		if (entityType.equals(EntityType.folderEntry.name())) {
			entity = getFolderModule().getEntry(null, entityId);
		} else if (entityType.equals(EntityType.folder.name()) || entityType.equals(EntityType.workspace.name())) {
			entity = getBinderModule().getBinder(entityId);
		}
		Set<Attachment> attachments = entity.getAttachments();
		FileAttachment fileAtt = null;
		for (Attachment attachment : attachments) {
			if (attachment instanceof FileAttachment) {
				if (attachment.getId().equals(fileId)) {
					fileAtt = (FileAttachment)attachment;
					break;
				}
				fileAtt = ((FileAttachment)attachment).findFileVersionById(fileId);
				if (fileAtt != null) break;
			}
		}

		Map model = new HashMap();	
		BinderHelper.setupStandardBeans(this, request, response, model);
		model.put(WebKeys.ENTITY, entity);
		model.put(WebKeys.FILE_ATTACHMENT, fileAtt);
		
		if (op.equals(WebKeys.OPERATION_DELETE)) {
			return new ModelAndView("forum/delete_file_version", model);
		} else if (op.equals(WebKeys.OPERATION_MODIFY_FILE_REVERT)) {
			model.put(WebKeys.FILE_QUOTA_WOULD_BE_EXCEEDED, 
					getFileModule().checkIfQuotaWouldBeExceeded(entity.getParentBinder(), 
							fileAtt.getFileItem().getLength(), fileAtt.getFileItem().getName()));
			return new ModelAndView("forum/revert_file_version", model); 
		} else if (op.equals(WebKeys.OPERATION_MODIFY_FILE_MAJOR_VERSION)) {
			model.put(WebKeys.FILE_QUOTA_WOULD_BE_EXCEEDED, 
					getFileModule().checkIfQuotaWouldBeExceeded(entity.getParentBinder(), 
							fileAtt.getFileItem().getLength(), fileAtt.getFileItem().getName()));
			return new ModelAndView("forum/increment_major_file_version", model); 
		} else {
			return new ModelAndView("forum/modify_file_description", model);
		}
	}
}

