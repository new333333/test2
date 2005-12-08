package com.sitescape.ef.servlet.forum;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.repository.RepositoryServiceNames;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import org.springframework.web.bind.RequestUtils;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long forumId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_FORUM_ID));
		Long entryId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_ENTRY_ID));
		FolderEntry entry = getFolderModule().getEntry(forumId, entryId);
		//Set up the beans needed by the jsps
		FileAttachment fa = null;
		String fileId = RequestUtils.getStringParameter(request, WebKeys.FORUM_URL_FILE_ID, "");
		
		if (fileId.equals("")) {
			String name = RequestUtils.getStringParameter(request, WebKeys.FORUM_URL_ATTRIBUTE, ""); 
			if (!name.equals("")) {
				CustomAttribute attr = entry.getCustomAttribute(name);
				if (attr != null)
					fa = (FileAttachment)attr.getValue();
			}
		} else {
			fa = (FileAttachment)entry.getAttachment(fileId);
		}
		if (fa != null) {
			
			String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
			FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
			response.setContentType(mimeTypes.getContentType(shortFileName));
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + shortFileName + "\"");
			RepositoryServiceUtil.read(entry.getParentFolder(), entry, 
					RepositoryServiceNames.FILE_REPOSITORY_SERVICE, fa.getFileItem().getName(), response.getOutputStream()); 

			response.getOutputStream().flush();
		}
		return null;
	}
}
