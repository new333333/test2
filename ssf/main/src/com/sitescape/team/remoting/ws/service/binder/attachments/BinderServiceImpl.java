package com.sitescape.team.remoting.ws.service.binder.attachments;

import java.io.File;
import java.util.Map;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.util.attachments.AttachmentsHelper;
import com.sitescape.team.remoting.ws.util.attachments.CalendarHelper;
import com.sitescape.util.Validator;
public class BinderServiceImpl extends com.sitescape.team.remoting.ws.service.binder.BinderServiceImpl {
	
	public void binder_uploadFile(String accessToken, long binderId, 
			String fileUploadDataItemName, String fileName) {
		if (Validator.isNull(fileUploadDataItemName)) fileUploadDataItemName="ss_attachFile1";
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
