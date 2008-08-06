package com.sitescape.team.remoting.ws.service.binder.attachments;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.attachments.AttachmentPart;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.util.attachments.AttachmentsHelper;
import com.sitescape.team.remoting.ws.util.attachments.AxisMultipartFile;
import com.sitescape.team.remoting.ws.util.attachments.CalendarHelper;
import com.sitescape.team.util.stringcheck.StringCheckUtil;
import com.sitescape.util.Validator;
public class BinderServiceImpl extends com.sitescape.team.remoting.ws.service.binder.BinderServiceImpl {
	
	public void binder_uploadFile(String accessToken, long binderId, 
			String fileUploadDataItemName, String fileName) {
		if (Validator.isNull(fileUploadDataItemName)) fileUploadDataItemName="ss_attachFile1";
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
	
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return AttachmentsHelper.getFileAttachments(fileUploadDataItemName, fileNames);
	}

	public com.sitescape.team.remoting.ws.model.Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		com.sitescape.team.remoting.ws.model.Binder binderModel = super.binder_getBinder(accessToken, binderId, includeAttachments);
		CalendarHelper.handleEvents(this, getBinderModule().getBinder(binderId));
		return binderModel;
	}
	public com.sitescape.team.remoting.ws.model.Binder binder_getBinderByPathName(String accessToken, String pathName, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		com.sitescape.team.remoting.ws.model.Binder binderModel = super.binder_getBinderByPathName(accessToken, pathName, includeAttachments);
		CalendarHelper.handleEvents(this, getBinderModule().getBinder(binderModel.getId()));
		return binderModel;
		
	}
	protected void handleAttachments(boolean includeAttachments) {
		if(includeAttachments) {
			attachmentHandler = new AttachmentHandler() {
				public void handleAttachment(FileAttachment att, String webUrl)
				{
					com.sitescape.team.remoting.ws.util.attachments.AttachmentsHelper.handleAttachment(att, webUrl);
				}
			};
		} else {
			attachmentHandler = new AttachmentHandler();
		}	
	}
	
}
