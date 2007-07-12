/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.remoting.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.xml.soap.SOAPException;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.impl.AbstractFacade;
import com.sitescape.team.remoting.impl.RemotingException;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.stringcheck.StringCheckUtil;
import com.sitescape.util.FileUtil;

/**
 * This class extends protocol-neutral <code>AbstractFacade</code> class with
 * SOAP specific features (specifically file upload capability using SOAP
 * attachment). The additional feature is implemented using Axis library.
 * @author jong
 *
 */
public class FacadeImpl extends AbstractFacade {

	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
		fileName = StringCheckUtil.check(fileName);

		// Get all the attachments
		AttachmentPart[] attachments;
		try {
			attachments = getMessageAttachments();
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
		AxisMultipartFile mf = new AxisMultipartFile(fileName, dh);
		
		// Create a map of file item names to items 
		Map fileItems = new HashMap();
		fileItems.put(fileUploadDataItemName, mf);
		
		try {
			// Finally invoke the business method. 
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new EmptyInputData(), fileItems, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public Map getFileAttachments(String fileUploadDataItemName, String[] fileNames) {

		// Get all the attachments
		AttachmentPart[] attachments;
		try {
			attachments = getMessageAttachments();
		} catch (AxisFault e) {
			throw new RemotingException(e);
		}

		// Create a map of file item names to items 
		Map fileItems = new HashMap();
		int i = 1;
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
				name = "attachment" + i;
			}
			AxisMultipartFile mf = new AxisMultipartFile(name, dh);
			
			fileItems.put(fileUploadDataItemName + i, mf);
		}
		
		return fileItems;
	}

	/**
	 * Extend basic support in AbstractFacade to include importing calendar entries
	 *  from attachments.
	 */
	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		iCalDataAsXML = StringCheckUtil.check(iCalDataAsXML);
		
		super.uploadCalendarEntries(folderId, iCalDataAsXML);
		try {
			for(AttachmentPart part : getMessageAttachments()) {
				getIcalModule().parseToEntries(folderId, part.getDataHandler().getInputStream());
			}
		} catch (Exception e) {
			throw new RemotingException(e);
		}
	}

	/**
	* Extract attachments from the current request
	* @return a list of attachmentparts or an empty array for no attachments 
	* support in this axis buid/runtime
	*/
	private AttachmentPart[] getMessageAttachments() throws AxisFault {
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
		public String getContentType() { return "text/calendar"; }
		
		public InputStream getInputStream() throws IOException
		{
			return new StringBufferInputStream(data);
		}
		
		public OutputStream getOutputStream() throws IOException
		{
			throw new IOException("Output not supported to this DataSource");
		}
	}

	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
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

		String xml = super.getFolderEntryAsXML(binderId, entryId, includeAttachments);

		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		if(!entry.getEvents().isEmpty()) {
			Calendar eventCalendar = getIcalModule().generate(entry, entry.getEvents(), MailModule.DEFAULT_TIMEZONE);
			DataHandler dh = new DataHandler(new CalendarDataSource(eventCalendar));
			MessageContext messageContext = MessageContext.getCurrentContext();
			Message responseMessage = messageContext.getResponseMessage();
			responseMessage.addAttachmentPart(new AttachmentPart(dh));
		}

		return xml;
	}
}
