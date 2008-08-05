package com.sitescape.team.remoting.ws.service.binder.attachments;

import java.util.Map;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.remoting.ws.service.binder.attachments.AttachmentUtilities;
import com.sitescape.team.remoting.ws.util.attachments.CalendarHelper;

public class BinderServiceImpl extends com.sitescape.team.remoting.ws.service.binder.BinderServiceImpl {
	AttachmentUtilities attachmentUtilities = new AttachmentUtilities(this);
	
	public void binder_uploadFile(String accessToken, long binderId, 
			String fileUploadDataItemName, String fileName) {
		attachmentUtilities.uploadBinderFile(binderId, fileUploadDataItemName, fileName);
	}
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return attachmentUtilities.getFileAttachments(fileUploadDataItemName, fileNames);
	}

	public com.sitescape.team.remoting.ws.model.Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		com.sitescape.team.remoting.ws.model.Binder binderModel = super.binder_getBinder(accessToken, binderId, includeAttachments);
		CalendarHelper.handleEvents(this, getBinderModule().getBinder(binderId));
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
