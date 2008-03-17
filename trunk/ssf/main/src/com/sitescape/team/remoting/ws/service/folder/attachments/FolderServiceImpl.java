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
package com.sitescape.team.remoting.ws.service.folder.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.util.FileUtil;

public class FolderServiceImpl extends com.sitescape.team.remoting.ws.service.folder.FolderServiceImpl {

	AttachmentUtilities attachmentUtilities = new AttachmentUtilities(this);
	
	public void uploadFolderFile(String accessToken, long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		attachmentUtilities.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	private static class CalendarDataSource implements DataSource
	{
		String data = "";
		
		public CalendarDataSource(Calendar cal)
		{
			StringWriter writer = new StringWriter();
			CalendarOutputter out = new CalendarOutputter();
			try {
				out.output(cal, writer);
				data = writer.toString();
			} catch(IOException e) {
			} catch(ValidationException e) {
			}
		}
		
		public String getName() { return "com.sitescape.team.CalendarDataSource"; }
		public String getContentType() { return MailModule.CONTENT_TYPE_CALENDAR; }
		
		public InputStream getInputStream() throws IOException
		{
			return new StringBufferInputStream(data);
		}
		
		public OutputStream getOutputStream() throws IOException
		{
			throw new IOException("Output not supported to this DataSource");
		}
	}

	public String getFolderEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		if(includeAttachments) {
			attachmentHandler = new AttachmentHandler() {
				public void handleAttachment(FileAttachment att, String webUrl)
				{
					FolderEntry entry = (FolderEntry)att.getOwner().getEntity();
					String shortFileName = FileUtil.getShortFileName(att.getFileItem().getName());	
					DataSource ds = RepositoryUtil.getDataSourceVersioned(att.getRepositoryName(),
							entry.getParentFolder(), 
							entry, att.getFileItem().getName(), att.getHighestVersion().getVersionName(),
							FileTypeMap.getDefaultFileTypeMap());
					DataHandler dh = new DataHandler(ds);
					MessageContext messageContext = MessageContext.getCurrentContext();
					Message responseMessage = messageContext.getResponseMessage();
					AttachmentPart part = new AttachmentPart(dh);
					part.setContentLocation(webUrl);
					part.setMimeHeader("Content-Disposition",
							"attachment;filename=\"" + shortFileName + "\"");
					responseMessage.addAttachmentPart(part);
				}
			};
		} else {
			attachmentHandler = new AttachmentHandler();
		}

		String xml = super.getFolderEntryAsXML(accessToken, binderId, entryId, includeAttachments);

		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		if(!entry.getEvents().isEmpty()) {
			Calendar eventCalendar = getIcalModule().generate(entry, entry.getEvents(), null);
			DataHandler dh = new DataHandler(new CalendarDataSource(eventCalendar));
			MessageContext messageContext = MessageContext.getCurrentContext();
			Message responseMessage = messageContext.getResponseMessage();
			responseMessage.addAttachmentPart(new AttachmentPart(dh));
		}

		return xml;
	}

}
