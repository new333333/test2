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
package org.kablink.teaming.remoting.ws.util.attachments;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.util.FileUtil;


public class AttachmentsHelper  {

	private final static Log logger = LogFactory.getLog(AttachmentsHelper.class);

	/**
	* Extract attachments from the current request
	* @return a list of attachmentparts or an empty array for no attachments 
	* support in this axis buid/runtime
	*/
	public static AttachmentPart[] getMessageAttachments() throws AxisFault {
		MessageContext msgContext = MessageContext.getCurrentContext();
		Message reqMsg = msgContext.getRequestMessage();
		Attachments messageAttachments = reqMsg.getAttachmentsImpl();
		if (null == messageAttachments) {
			logger.warn("No attachment support");
			return new AttachmentPart[0];
		}
		int attachmentCount = messageAttachments.getAttachmentCount();
		AttachmentPart attachments[] = new AttachmentPart[attachmentCount];
		Iterator it = messageAttachments.getAttachments().iterator();
		int count = 0;
		while (it.hasNext()) {
			AttachmentPart part = (AttachmentPart) it.next();
			attachments[count++] = part;
		}
		return attachments;
	}
	public static void handleAttachment(FileAttachment att, String webUrl)
	{
		DefinableEntity entity = att.getOwner().getEntity();
		String shortFileName = FileUtil.getShortFileName(att.getFileItem().getName());	
		DataSource ds;
		if (entity instanceof org.kablink.teaming.domain.Binder) {
			ds = RepositoryUtil.getDataSourceVersioned(att,
				(org.kablink.teaming.domain.Binder)entity, entity, FileTypeMap.getDefaultFileTypeMap());
		} else {
			ds = RepositoryUtil.getDataSourceVersioned(att,
					entity.getParentBinder(), entity, FileTypeMap.getDefaultFileTypeMap());
			
		}
		DataHandler dh = new DataHandler(ds);
		MessageContext messageContext = MessageContext.getCurrentContext();
		Message responseMessage = messageContext.getResponseMessage();
		AttachmentPart part = new AttachmentPart(dh);
		part.setContentLocation(webUrl);
		part.setMimeHeader("Content-Disposition",
				"attachment;filename=\"" + shortFileName + "\"");
		responseMessage.addAttachmentPart(part);
	}
	public static Map<String, AxisMultipartFile> getFileAttachments(String fileUploadDataItemName, String[] fileNames) {
		fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);

		// Get all the attachments
		AttachmentPart[] attachments;
		try {
			attachments = getMessageAttachments();
		} catch (AxisFault e) {
			throw new RemotingException(e);
		}

		// Create a map of file item names to items 
		Map fileItems = new HashMap();
		int i = 0;
		for(AttachmentPart attachment : attachments) {
			DataHandler dh;
			try {
					dh = attachment.getDataHandler();
			} catch (SOAPException e) {
				throw new RemotingException(e);
			}
	
			// Wrap it up in a datastructure expected by our app.
			String name = null;
			if(i < fileNames.length) {
				name = StringCheckUtil.check(fileNames[i]);
			} else {
				name = "attachment" + (i+1);
			}
			AxisMultipartFile mf = new AxisMultipartFile(name, dh);
			
			fileItems.put(fileUploadDataItemName + (i+1), mf);
			i = i+1;
		}
		
		return fileItems;
	}
	public static Map<String, AxisMultipartFile> getFileAttachment(String fileUploadDataItemName, String fileName) {
		fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
		fileName = StringCheckUtil.check(fileName);
		// Get all the attachments
		AttachmentPart[] attachments;
		try {
			attachments = AttachmentsHelper.getMessageAttachments();
		} catch (AxisFault e) {
			throw new RemotingException(e);
		}

		//Extract the first attachment. (Since in this case we have only one attachment sent)
		DataHandler dh;
		try {
			dh = attachments[0].getDataHandler();
		} catch (SOAPException e) {
			throw new RemotingException(e);
		}

		// Wrap it up in a datastructure expected by our app.
		AxisMultipartFile mf = new AxisMultipartFile(fileName, dh, null); 
		// Create a map of file item names to items 
		Map fileItems = new HashMap();
		fileItems.put(fileUploadDataItemName, mf);
		return fileItems;

	}
}
