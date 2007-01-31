package com.sitescape.ef.servlet.forum;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import javax.activation.FileTypeMap;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.SAbstractController;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.FileUtil;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.util.Validator;
import org.springframework.web.bind.RequestUtils;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		String viewType = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_VIEW_TYPE, ""); 
		String fileId = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, ""); 
		if (viewType.equals(WebKeys.FILE_VIEW_TYPE_UPLOAD_FILE)) {
			//This is a request to view a recently uploaded file in the temp area
			String shortFileName = WebHelper.getFileName(fileId);	
			FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
			response.setContentType(mimeTypes.getContentType(shortFileName));
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			String attachment = "attachment; ";
			response.setHeader(
						"Content-Disposition",
						attachment + "filename=\"" + shortFileName + "\"");
			
			try {
				WebHelper.readFileHandleContent(fileId, response.getOutputStream());
			} catch(IOException e) {
				//An error occurred (most likely that the file was released and no longer exists
				//Just output nothing
			}
			response.getOutputStream().flush();
			
		} else {
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
			FileAttachment fa = null;
			FileAttachment topAtt = null;
			if (fileId.equals("")) {
				//This must be a request for the title file; go set that up.
				CustomAttribute ca = entity.getCustomAttribute("_fileEntryTitle");
				if (ca == null) ca = entity.getCustomAttribute("title");
				if (ca != null && ca.getValue() instanceof FileAttachment) fileId = ((FileAttachment)ca.getValue()).getId();
			}
			if (!fileId.equals("")) topAtt = (FileAttachment)entity.getAttachment(fileId);
			if (topAtt != null) {
				//see if we want a version
				String versionId = RequestUtils.getStringParameter(request, WebKeys.URL_VERSION_ID, ""); 
				if (Validator.isNull(versionId)) {
					fa = topAtt;
				} else {
					fa = topAtt.findFileVersionById(versionId);
				}
			}
			
			if (viewType.equals("html"))
			{
				/**
				 * Convert specified file (XLS, PDF, DOC, etc) to HTML format and display to browser. Part of "View as HTML" functionality.
				 */
				try {
					response.setContentType("text/html");
					getFileModule().readCacheHtmlFile(request.getRequestURI(), parent, entity, fa, response.getOutputStream());
					return null;
				}
				catch(Exception e) {
					String url = request.getRequestURL().toString();
					url = url.substring(0, url.lastIndexOf("/")+1) + "errorHandler";
					String output = "<html><head><script language='javascript'>function submitForm(){ document.errorform.submit(); }</script></head><body onload='javascript:submitForm()'><form name='errorform' action='" + url + "'><b>Error Form</b><input type='hidden' name='ssf-error' value='" + e.getMessage() + "'></input></form></body></html>";
					
					response.getOutputStream().print(output);
					response.getOutputStream().flush();
				}
			}
			else
			if (viewType.equals("image")
			|| viewType.equals("url"))
			{
				/**
				 * There is a <IMG> or <A> in an HTML file that points to a file within the SS file repository
				 * We must fetch that file from disk an stream into the browser. The file location could be anywhere
				 * on the server machine. Part of "View as HTML" functionality.
				 */
				try {
					String fileName = RequestUtils.getStringParameter(request, "filename", ""); 
					if (viewType.equals("url"))
					{
						response.setContentType("text/html");
						getFileModule().readCacheUrlReferenceFile(parent, entity, fa, response.getOutputStream(), fileName);
					}
					else
					{
						response.setContentType("image/jpeg");
						getFileModule().readCacheImageReferenceFile(parent, entity, fa, response.getOutputStream(), fileName);
					}
				}
				catch(Exception e) {
					response.getOutputStream().print(NLT.get("file.error") + ": " + e.getMessage());
				}
				
				response.getOutputStream().flush();
			}
			else
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
						// (rsordillo) different file types are possible need to convert extension to 'JPG' to ensure image
						fa.getFileItem().setName(fa.getFileItem().getName() + com.sitescape.ef.docconverter.IImageConverterManager.IMG_EXTENSION);
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
				} else if (viewType.equals("thumbnail")) {
					try {
						getFileModule().readThumbnailFile(parent, entity, fa, response.getOutputStream());
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getMessage());
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
		}
		return null;
	}
}
