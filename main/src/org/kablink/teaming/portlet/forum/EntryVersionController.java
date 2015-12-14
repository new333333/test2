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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.DataQuotaException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;




public class EntryVersionController extends  SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String op1 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION2, "");
		if (op.equals("revert") && op1.equals("okBtn") && WebHelper.isMethodPost(request)) {
			Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);
			DefinableEntity entity = null;
			String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
			if (entityType.equals(EntityType.folderEntry.name())) {
				entity = getFolderModule().getEntry(null, entityId);
			}
			if (entity != null) {
				try {
					getFolderModule().modifyEntry(entity.getParentBinder().getId(), entity.getId(), 
						new MapInputData(formData), null, null, null, null);
				} catch(WriteEntryDataException e) {
		    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
		    		return;
				}
				//The entry data is updated, now check for files to be reverted
				if (!entity.getParentBinder().isMirrored()) {		// Never try to revert a mirrored file
					Set<Attachment> attachments = entity.getAttachments();
					Iterator itKeys = formData.keySet().iterator();
					while (itKeys.hasNext()) {
						String key = (String) itKeys.next();
						FileAttachment fileAtt = null;
						VersionAttachment fileVer = null;
						if (key.startsWith("file_revert_")) {
							String fileId = key.substring(12);
							String[] values = (String[]) formData.get(key);
							if (values[0].equals(("true"))) {
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
								if (fileVer != null) {
									try {
										getFileModule().revertFileVersion(entity, fileVer);
									} catch(DataQuotaException e) {
							    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
							    		response.setRenderParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
							    		return;
									}
								}
							}
						}
					}
				}
				BinderHelper.indexEntity(entity);

				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
				response.setRenderParameter(WebKeys.URL_BINDER_ID, entity.getParentBinder().getId().toString());
				response.setRenderParameter(WebKeys.URL_ENTRY_ID, entity.getId().toString());
			}
		}
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, null);
		String viewPath = "forum/view_description_history";
		Map model = new HashMap();
		
		Map formData = request.getParameterMap();
				
		List changeList = new ArrayList();
		List changes = null;
		String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
		EntityIdentifier entityIdentifier = new EntityIdentifier(entityId, EntityIdentifier.EntityType.valueOf(entityType));

		if (entityId != null) {
			DefinableEntity entity = null;
			if (entityType.equals(EntityType.folderEntry.name())) {
				entity = getFolderModule().getEntry(null, entityId);
			}
			Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
			if (entity != null && entity instanceof FolderEntry) {
				accessControlMap.put("modifyEntry", 
						getFolderModule().testAccess((FolderEntry)entity, FolderOperation.modifyEntry));
				BinderHelper.setupStandardBeans(this, request, response, model, ((FolderEntry)entity).getParentBinder().getId());
			}
			Map<Long,FolderEntry> folderEntries = new HashMap<Long,FolderEntry>();
			if (entity != null && operation.equals(WebKeys.OPERATION_VIEW_EDIT_HISTORY)) {
				//Start the list with the "addEntry" entry
				changes = getAdminModule().getEntryHistoryChanges(entityIdentifier);
				changeList.addAll(BinderHelper.BuildChangeLogBeans(this, entity, changes, folderEntries));
			} else if (entity != null && operation.equals("revert")) {
				Long versionId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_VERSION_ID);
				//Start the list with the "addEntry" entry
				changes = getAdminModule().getEntryHistoryChanges(entityIdentifier);
				changeList.addAll(BinderHelper.BuildChangeLogBeans(this, entity, changes, folderEntries, versionId));
				model.put(WebKeys.VERSION_ID, versionId);
				viewPath = "forum/view_description_history_revert";
			}
		}
		
		model.put(WebKeys.CHANGE_LOG_LIST, changeList);
		model.put(WebKeys.ENTITY_ID, entityId);

		return new ModelAndView(viewPath, model);
	} 

}