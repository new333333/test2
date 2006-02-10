package com.sitescape.ef.servlet.forum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import org.springframework.web.bind.RequestUtils;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Long entryId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
		String downloadFile = RequestUtils.getStringParameter(request, WebKeys.URL_DOWNLOAD_FILE, "");
		Binder binder = getBinderModule().getBinder(binderId);
		Entry entry=null;
		if (binder instanceof Folder) {
			entry = getFolderModule().getEntry(binderId, entryId);
		} else {
			entry = getProfileModule().getEntry(binderId, entryId);
			
		}
		//Set up the beans needed by the jsps
		String fileId = RequestUtils.getRequiredStringParameter(request, WebKeys.URL_FILE_ID); 
		String viewType = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_VIEW_TYPE, ""); 
		
		FileAttachment fa = (FileAttachment)entry.getAttachment(fileId);
		
		if (fa != null) {
			
			String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
			FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
			response.setContentType(mimeTypes.getContentType(shortFileName));
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			String attachment = "";
			if (!downloadFile.equals("")) attachment = "attachment; ";
			response.setHeader(
						"Content-Disposition",
						attachment + "filename=\"" + shortFileName + "\"");
			
			if (viewType.equals(WebKeys.FILE_VIEW_TYPE_SCALED) && 
					getFileModule().scaledFileExists(entry.getParentBinder(), entry, fa)) {
				getFileModule().readScaledFile(entry.getParentBinder(), entry, fa, response.getOutputStream());
			} else {
				getFileModule().readFile(entry.getParentBinder(), entry, fa, response.getOutputStream());				
			}

			response.getOutputStream().flush();
		}
		return null;
	}
}
