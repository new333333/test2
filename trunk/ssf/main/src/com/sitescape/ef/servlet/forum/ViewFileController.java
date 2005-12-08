package com.sitescape.ef.servlet.forum;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.portlet.PortletKeys;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		Map formData = request.getParameterMap();

		String forumId = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_FORUM_ID);
		String entryId = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_ENTRY_ID);
		if (forumId.equals("") || entryId.equals("")) {
			//There is no forum or entry specified.
		    return null;		
		}
		FolderEntry entry = getFolderModule().getEntry(Long.valueOf(forumId), Long.valueOf(entryId));
		//Set up the beans needed by the jsps
		FileAttachment fa = null;
		String fileId = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_FILE);
		if (fileId.equals("")) {
			String name = ActionUtil.getStringValue(formData, PortletKeys.FORUM_URL_ATTRIBUTE); 
			if (!name.equals("")) {
				CustomAttribute attr = entry.getCustomAttribute(name);
				fa = (FileAttachment)attr.getValue();
			}
		} else {
			fa = (FileAttachment)entry.getAttachment(fileId);
		}
		if (fa != null) {
			FileInputStream fIn = new FileInputStream("c:/home/sitescape/forum/" + 
    				entry.getParentFolder().getStringId() + "/" + 
					entry.getStringId() + "/" + 
					fa.getFileItem().getName());
			try {
				byte[] byteArray = new byte[20000];
				fIn.read(byteArray);
	
				String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());
	
				response.setContentType("application/octet-stream");
				response.setHeader("Cache-Control", "private");
				response.setHeader("Pragma", "no-cache");
				response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + shortFileName + "\"");
				response.getOutputStream().write(byteArray);
				response.getOutputStream().flush();

				OutputStream out = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int count;
				while((count = fIn.read(buffer)) > -1) {
					out.write(buffer, 0, count);
				}
				out.flush();
			}
			finally {
				fIn.close();
			}
		}
		return null;
	}
}
