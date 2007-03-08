package com.sitescape.team.remoting.ws;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;

import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.impl.AbstractFacade;
import com.sitescape.team.remoting.impl.RemotingException;

public class FacadeImpl extends AbstractFacade {

	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {

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
