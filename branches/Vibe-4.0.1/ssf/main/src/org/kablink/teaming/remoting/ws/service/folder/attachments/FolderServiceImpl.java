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
package org.kablink.teaming.remoting.ws.service.folder.attachments;

import java.util.Map;

import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.remoting.ws.util.attachments.CalendarHelper;

public class FolderServiceImpl extends org.kablink.teaming.remoting.ws.service.folder.FolderServiceImpl {

	AttachmentUtilities attachmentUtilities = new AttachmentUtilities(this);
	
	public void folder_uploadFile(String accessToken, long entryId, 
			String fileUploadDataItemName, String fileName) {
		attachmentUtilities.uploadFolderFile(null, entryId, fileUploadDataItemName, fileName);
	}
	public void folder_uploadFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		attachmentUtilities.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return attachmentUtilities.getFileAttachments(fileUploadDataItemName, fileNames);
	}


	public String folder_getEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		handleAttachments(includeAttachments);
		
		String xml = super.folder_getEntryAsXML(accessToken, binderId, entryId, includeAttachments);

		CalendarHelper.handleEvents(this, getFolderModule().getEntry(binderId, entryId));
		return xml;
	}

	public org.kablink.teaming.remoting.ws.model.FolderEntry folder_getEntryByFileName(String accessToken, long binderId, String fileName, boolean includeAttachments, boolean eventAsIcalString) {
		handleAttachments(includeAttachments);

		org.kablink.teaming.remoting.ws.model.FolderEntry entryModel = super.folder_getEntryByFileName(accessToken, binderId, fileName, includeAttachments, eventAsIcalString); 

		if (includeAttachments) CalendarHelper.handleEvents(this, getFolderModule().getEntry(null, entryModel.getId()));
		
		return entryModel;
		
	}
	public org.kablink.teaming.remoting.ws.model.FolderEntry folder_getEntry(String accessToken, long entryId, boolean includeAttachments, boolean eventAsIcalString) {
		handleAttachments(includeAttachments);
		
		org.kablink.teaming.remoting.ws.model.FolderEntry entryModel = super.folder_getEntry(accessToken, entryId, includeAttachments, eventAsIcalString);

		if (includeAttachments) CalendarHelper.handleEvents(this, getFolderModule().getEntry(null, entryId));
		return entryModel;
	}

	protected void handleAttachments(boolean includeAttachments) {
		if(includeAttachments) {
			attachmentHandler = new AttachmentHandler() {
				public void handleAttachment(FileAttachment att, String webUrl)
				{
					org.kablink.teaming.remoting.ws.util.attachments.AttachmentsHelper.handleAttachment(att, webUrl);
				}
			};
		} else {
			attachmentHandler = new AttachmentHandler();
		}	
	}
	

}
