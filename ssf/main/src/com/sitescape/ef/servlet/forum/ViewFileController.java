package com.sitescape.ef.servlet.forum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import org.springframework.web.bind.RequestUtils;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long forumId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_FORUM_ID));
		Long entryId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.FORUM_URL_ENTRY_ID));
		FolderEntry entry = getFolderModule().getEntry(forumId, entryId);
		//Set up the beans needed by the jsps
		String fileId = RequestUtils.getRequiredStringParameter(request, WebKeys.FORUM_URL_FILE_ID); 
		
		FileAttachment fa = (FileAttachment)entry.getAttachment(fileId);
		
		if (fa != null) {
			
			String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
			FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
			response.setContentType(mimeTypes.getContentType(shortFileName));
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			response.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + shortFileName + "\"");
			String repositoryServiceName = fa.getRepositoryServiceName();
			if(repositoryServiceName == null)
				repositoryServiceName = RepositoryServiceUtil.getDefaultRepositoryServiceName();
			
			// Since viewing of file resource does not require any metadata manipulation
			// (at least for now), we can bypass FileManager layer and use repository
			// service directly. We might want to capture some audit information around
			// repository access, in which case we will need to use higher level service
			// - possibly FileManager - rather than low level repository service. 
			// But that's for later.  
			RepositoryServiceUtil.read(repositoryServiceName, entry.getParentFolder(), entry, 
					fa.getFileItem().getName(), response.getOutputStream()); 

			response.getOutputStream().flush();
		}
		return null;
	}
}
