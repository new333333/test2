package com.sitescape.ef.servlet.forum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.util.Validator;
import org.springframework.web.bind.RequestUtils;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Long entryId = RequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		String downloadFile = RequestUtils.getStringParameter(request, WebKeys.URL_DOWNLOAD_FILE, "");
		Binder binder = getBinderModule().getBinder(binderId);
		DefinableEntity entity=null;
		Binder parent;
		if (entryId != null) {
			if (binder instanceof Folder) {
				entity = getFolderModule().getEntry(binderId, entryId);
			} else {
				entity = getProfileModule().getEntry(binderId, entryId);
				
			}
			parent = ((Entry)entity).getParentBinder();
		} else {
			entity = binder;
			parent = binder;
		}
		//Set up the beans needed by the jsps
		String fileId = RequestUtils.getRequiredStringParameter(request, WebKeys.URL_FILE_ID); 
		String viewType = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_VIEW_TYPE, ""); 
		FileAttachment fa=null;
		FileAttachment topAtt = (FileAttachment)entity.getAttachment(fileId);
		if (topAtt != null) {
			//see if we want a version
			String versionId = RequestUtils.getStringParameter(request, WebKeys.URL_VERSION_ID, ""); 
			if (Validator.isNull(versionId)) {
				fa = topAtt;
			} else {
				fa = topAtt.findFileVersionById(versionId);
			}
		}
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
			
			if (viewType.equals(WebKeys.FILE_VIEW_TYPE_SCALED)) {
				boolean scaledFileExists = false;
				try {
					if (getFileModule().scaledFileExists(parent, entity, fa)) {
						scaledFileExists = true;
					}
				}
				catch(Exception e1) {}
				if (scaledFileExists) {
					getFileModule().readScaledFile(parent, entity, fa, response.getOutputStream());
				} else {
					try {
						getFileModule().readFile(parent, entity, fa, response.getOutputStream());				
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getMessage());
					}
				}
			} else {
				try {
					getFileModule().readFile(parent, entity, fa, response.getOutputStream());				
				}
				catch(Exception e) {
					response.getOutputStream().print(NLT.get("file.error") + ": " + e.getMessage());
				}
			}

			response.getOutputStream().flush();
		}
		return null;
	}
}
