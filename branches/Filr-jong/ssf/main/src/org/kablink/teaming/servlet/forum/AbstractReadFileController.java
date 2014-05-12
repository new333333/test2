/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.servlet.forum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.kablink.util.Validator;

import org.springframework.web.servlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
public abstract class AbstractReadFileController extends SAbstractController {
	
	private FileTypeMap mimeTypes;

	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	protected DefinableEntity getEntity(String strEntityType, Long entityId) {
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.none;
		DefinableEntity entity = null;
		try {
			entityType = EntityIdentifier.EntityType.valueOf(strEntityType);
		} catch(Exception ignore) {}
		if (entityType.isBinder()) {
			//the entry is the binder, the entryId should be used
			entity = getBinderModule().getBinder(entityId);
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) {
			entity = getFolderModule().getEntry(null, entityId);
		} else if (entityType.isPrincipal()) {
			entity = getProfileModule().getEntry(entityId);
		} else {
			//	Try to figure out what type of entity this is
			try {
				entity = getFolderModule().getEntry(null, entityId);
			} catch (Exception e) {}
			if (entity == null) {
				try {
					entity = getProfileModule().getEntry(entityId);
				} catch (Exception e) {};
			}
			if (entity == null) {
				entity = getBinderModule().getBinder(entityId);
			}
		}
		return entity;
	}
	protected Binder getBinder(DefinableEntity entity) {
		if (entity instanceof Binder) return (Binder)entity;
		return entity.getParentBinder();
	}
	protected FileAttachment getAttachment(DefinableEntity entity, String fileName, String fileVersion, String fileAttachmentId) {
		FileAttachment fa = null;
		if (Validator.isNotEmptyString(fileName)) {
			fa = (FileAttachment)entity.getFileAttachment(fileName);
			if (fa == null && fileAttachmentId != null) {
				fa = (FileAttachment)entity.getAttachment(fileAttachmentId);
			}
			if (fa != null && !fileVersion.equals(WebKeys.READ_FILE_LAST) && 
					!fileVersion.equals(WebKeys.READ_FILE_LAST_VIEW)) {
				//request for a specific version
				fa = fa.findFileVersionByNumber(Integer.valueOf(fileVersion));
			}
		}
		return fa;
	}
	protected FileAttachment getAttachment(FolderEntry entity, Object[] args) {
		//this is an old forum folder structure
		StringBuffer path = new StringBuffer(entity.getParentBinder().getPathName());
		for (int i=0; i<args.length-1; ++i) {
			path.append("/" + args[i]);
		}
		String fileName = args[args.length-1].toString(); 
		Folder parent = (Folder)getBinderModule().getBinderByPathName(path.toString());
		entity = getFolderModule().getLibraryFolderEntryByFileName(parent, fileName);
		FileAttachment fa = (FileAttachment)entity.getFileAttachment(fileName);
		return fa;
	}
	protected String formatDate(Date date) {
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
		df.applyPattern("EEE, dd MMM yyyy kk:mm:ss zzz");
		return df.format(date);
	}
	
	@Override
	protected abstract ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception;
}
