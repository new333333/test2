package com.sitescape.ef.remoting.ws.jaxrpc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.activation.DataHandler;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.springframework.util.FileCopyUtils;

import com.sitescape.ef.remoting.impl.AbstractFacade;

public class FacadeImpl extends AbstractFacade {

	public int uploadFile(long binderId, long entryId, String fileUploadDataItemName, String fileName) {

		
		InputStream is =null;
		FileOutputStream os = null;

		try
		{
			// Get all the attachments
			AttachmentPart[] attachments = getMessageAttachments();

			//Extract the first attachment. (Since in this case we have only one attachment sent)
			DataHandler dh = attachments[0].getDataHandler();

			is = dh.getInputStream();
			File file = new File("C:/junk2", fileName);
			os = new FileOutputStream(file);
			FileCopyUtils.copy(is, os);
			is.close();
			os.close();
		}
		catch(Exception e)
		{
			String status="File Could Not Be Saved: "+e.getMessage();
			System.out.println("In Impl: "+e);
		}

		return 1;
		
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
	
}
