package com.sitescape.team.remoting.ws.service.profile.attachments;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.util.attachments.AttachmentsHelper;
import com.sitescape.team.remoting.ws.util.attachments.CalendarHelper;
import com.sitescape.team.util.stringcheck.StringCheckUtil;
import com.sitescape.util.Validator;

public class ProfileServiceImpl extends com.sitescape.team.remoting.ws.service.profile.ProfileServiceImpl {
	public void profile_uploadFile(String accessToken, long principalId, String fileUploadDataItemName, String fileName) {
		if (Validator.isNull(fileUploadDataItemName)) fileUploadDataItemName="ss_attachFile1";
		fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
		File originalFile = new File(fileName);
		fileName = StringCheckUtil.check(originalFile.getName());

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

	public com.sitescape.team.remoting.ws.model.Group profile_getGroup(String accessToken, long groupId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		com.sitescape.team.remoting.ws.model.Group groupModel = super.profile_getGroup(accessToken, groupId, includeAttachments);
		CalendarHelper.handleEvents(this, getProfileModule().getEntry(groupId));
		return groupModel;
	}
	public com.sitescape.team.remoting.ws.model.User profile_getUser(String accessToken, long userId, boolean includeAttachments) {
		handleAttachments(includeAttachments);

		com.sitescape.team.remoting.ws.model.User userModel = super.profile_getUser(accessToken, userId, includeAttachments);
		CalendarHelper.handleEvents(this, getProfileModule().getEntry(userId));
		return userModel;
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
