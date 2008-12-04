package org.kablink.teaming.remoting.ws.service.profile.attachments;

import java.io.File;
import java.util.Map;

import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.remoting.RemotingException;
import org.kablink.teaming.remoting.ws.util.attachments.AttachmentsHelper;
import org.kablink.teaming.remoting.ws.util.attachments.CalendarHelper;
import org.kablink.util.Validator;


public class ProfileServiceImpl extends org.kablink.teaming.remoting.ws.service.profile.ProfileServiceImpl {
	public void profile_uploadFile(String accessToken, long principalId, String fileUploadDataItemName, String fileName) {
		if (Validator.isNull(fileUploadDataItemName)) fileUploadDataItemName="ss_attachFile1";
		File originalFile = new File(fileName);
		fileName = originalFile.getName();

		// Wrap it up in a datastructure expected by our app.
		Map fileItems = AttachmentsHelper.getFileAttachment(fileUploadDataItemName, fileName);
		
		try {
			// Finally invoke the business method. 
			getProfileModule().modifyEntry(principalId, new EmptyInputData(), fileItems, null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return AttachmentsHelper.getFileAttachments(fileUploadDataItemName, fileNames);
	}

	public org.kablink.teaming.remoting.ws.model.Group profile_getGroup(String accessToken, long groupId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		org.kablink.teaming.remoting.ws.model.Group groupModel = super.profile_getGroup(accessToken, groupId, includeAttachments);
		CalendarHelper.handleEvents(this, getProfileModule().getEntry(groupId));
		return groupModel;
	}
	public org.kablink.teaming.remoting.ws.model.User profile_getUser(String accessToken, long userId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		org.kablink.teaming.remoting.ws.model.User userModel = super.profile_getUser(accessToken, userId, includeAttachments);
		CalendarHelper.handleEvents(this, getProfileModule().getEntry(userId));
		return userModel;
	}
	protected void handleAttachments(boolean includeAttachments) {
		if(includeAttachments) {
			attachmentHandler = new AttachmentHandler() {
				public void handleAttachment(FileAttachment att, String webUrl)
				{
					org.kablink.teaming.remoting.ws.util.attachments.AttachmentsHelper.handleAttachment(att, webUrl);
				}
			};
		} else {
			attachmentHandler = new AttachmentHandler();
		}	
	}
	
}
