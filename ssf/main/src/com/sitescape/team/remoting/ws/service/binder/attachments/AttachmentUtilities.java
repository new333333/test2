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
package com.sitescape.team.remoting.ws.service.binder.attachments;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.attachments.AttachmentPart;

import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.util.attachments.AttachmentsHelper;
import com.sitescape.team.remoting.ws.util.attachments.AxisMultipartFile;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

public class AttachmentUtilities {

	BaseService owningService;
	
	public AttachmentUtilities(BaseService owningService)
	{
		this.owningService = owningService;
	}
	
	protected BinderModule getBinderModule() { return owningService.getBinderModule(); }
	protected IcalModule getIcalModule() { return owningService.getIcalModule(); }
	
	public void uploadBinderFile(long binderId, String fileUploadDataItemName, String fileName) {
					
		fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
		File originalFile = new File(fileName);
		fileName = StringCheckUtil.check(originalFile.getName());

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
		AxisMultipartFile mf = new AxisMultipartFile(fileName, dh, null, null);
		
		// Create a map of file item names to items 
		Map fileItems = new HashMap();
		fileItems.put(fileUploadDataItemName, mf);
		
		try {
			// Finally invoke the business method. 
			getBinderModule().modifyBinder(new Long(binderId),  
				new EmptyInputData(), fileItems, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames) {

		// Get all the attachments
		AttachmentPart[] attachments;
		try {
			attachments = AttachmentsHelper.getMessageAttachments();
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
				name = fileNames[i];
			} else {
				name = "attachment" + (i+1);
			}
			AxisMultipartFile mf = new AxisMultipartFile(name, dh);
			
			fileItems.put(fileUploadDataItemName + (i+1), mf);
			i = i+1;
		}
		
		return fileItems;
	}
}
