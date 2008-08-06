package com.sitescape.team.remoting.ws.util.attachments;

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

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.util.FileUtil;

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
		if (entity instanceof com.sitescape.team.domain.Binder) {
			ds = RepositoryUtil.getDataSourceVersioned(att.getRepositoryName(),
				(com.sitescape.team.domain.Binder)entity, 
				entity, att.getFileItem().getName(), att.getHighestVersion().getVersionName(),
				FileTypeMap.getDefaultFileTypeMap());
		} else {
			ds = RepositoryUtil.getDataSourceVersioned(att.getRepositoryName(),
					entity.getParentBinder(), 
					entity, att.getFileItem().getName(), att.getHighestVersion().getVersionName(),
					FileTypeMap.getDefaultFileTypeMap());
			
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
	public static Map getFileAttachments(String fileUploadDataItemName, String[] fileNames) {

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
