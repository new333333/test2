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
package org.kablink.teaming.remoting.ws.service.binder.attachments;

import java.io.File;
import java.util.Map;

import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.remoting.ws.util.attachments.AttachmentsHelper;
import org.kablink.teaming.remoting.ws.util.attachments.CalendarHelper;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.Validator;

public class BinderServiceImpl extends org.kablink.teaming.remoting.ws.service.binder.BinderServiceImpl {
	
	public void binder_uploadFile(String accessToken, long binderId, 
			String fileUploadDataItemName, String fileName) {
		if (Validator.isNull(fileUploadDataItemName) || "ss_attachFile".equals(fileUploadDataItemName))
			fileUploadDataItemName="ss_attachFile1";
		File originalFile = new File(fileName);
		fileName = originalFile.getName();

		// Wrap it up in a datastructure expected by our app.
		Map fileItems = AttachmentsHelper.getFileAttachment(fileUploadDataItemName, fileName);
		
		try {
			// Finally invoke the business method. 
			getBinderModule().modifyBinder(new Long(binderId),  
				new EmptyInputData(), fileItems, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return AttachmentsHelper.getFileAttachments(fileUploadDataItemName, fileNames);
	}

	public org.kablink.teaming.remoting.ws.model.Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		org.kablink.teaming.remoting.ws.model.Binder binderModel = super.binder_getBinder(accessToken, binderId, includeAttachments);
		if (includeAttachments) CalendarHelper.handleEvents(this, getBinderModule().getBinder(binderId));
		return binderModel;
	}
	public org.kablink.teaming.remoting.ws.model.Binder binder_getBinderByPathName(String accessToken, String pathName, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		org.kablink.teaming.remoting.ws.model.Binder binderModel = super.binder_getBinderByPathName(accessToken, pathName, includeAttachments);
		if (includeAttachments) CalendarHelper.handleEvents(this, getBinderModule().getBinder(binderModel.getId()));
		return binderModel;
		
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
