package com.sitescape.team.remoting.ws.util.attachments;

import java.util.Iterator;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.remoting.ws.BaseService;

public class AttachmentsHelper extends BaseService {

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


}
