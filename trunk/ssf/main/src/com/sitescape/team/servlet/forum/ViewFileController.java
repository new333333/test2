/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.servlet.forum;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.FileUtil;
import com.sitescape.util.Validator;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		String viewType = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_VIEW_TYPE, ""); 
		String fileId = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, ""); 
		String fileTitle = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_TITLE, ""); 
		String fileTime = RequestUtils.getStringParameter(request, WebKeys.URL_FILE_TIME, ""); 
		if (viewType.equals(WebKeys.FILE_VIEW_TYPE_ZIPPED)) {
			streamZipFile(request, response, fileId, fileTitle);
		}
		else
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
			String strEntryId = RequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			Long entryId = null;
			if (!strEntryId.equals("")) entryId = Long.valueOf(strEntryId);
			String downloadFile = RequestUtils.getStringParameter(request, WebKeys.URL_DOWNLOAD_FILE, "");
			DefinableEntity entity=null;
			Binder parent;
			String strEntityType = RequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.none.toString());
			EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.valueOf(strEntityType);
			if (entityType.equals(EntityIdentifier.EntityType.folder) || entityType.equals(EntityIdentifier.EntityType.workspace) ||
					entityType.equals(EntityIdentifier.EntityType.profiles)) {
				Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				entity = getBinderModule().getBinder(binderId);
				parent = (Binder) entity;
			} else if (entryId != null) {
				Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) {
					entity = getFolderModule().getEntry(binderId, entryId);
				} else if (entityType.equals(EntityIdentifier.EntityType.none)) {
					//Try to figure out what type of entity this is
					try {
						entity = getFolderModule().getEntry(binderId, entryId);
					} catch (Exception e) {}
					if (entity == null) {
						try {
							entity = getProfileModule().getEntry(binderId, entryId);
						} catch (Exception e) {}
					}
					
				} else {
					entity = getProfileModule().getEntry(binderId, entryId);
				}
				parent = entity.getParentBinder();
			} else {
				Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				parent = getBinderModule().getBinder(binderId);
				entity = parent;
			}
			//Set up the beans needed by the jsps
			FileAttachment fa = null;
			FileAttachment topAtt = null;
			if (fileId.equals("")) {
				if (!fileTitle.equals("")) {
					fa = entity.getFileAttachment(fileTitle);
					if (fa != null) fileId = fa.getId();
				} else {
					//This must be a request for the title file; go set that up.
					CustomAttribute ca = entity.getCustomAttribute("_fileEntryTitle");
					if (ca == null) ca = entity.getCustomAttribute("title");
					if (ca != null && ca.getValue() instanceof FileAttachment) fileId = ((FileAttachment)ca.getValue()).getId();
				}
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
					String url = WebUrlUtil.getServletRootURL(request, false);
					url += "errorHandler";
					
					String output = "<html><head><script language='javascript'>function submitForm(){ document.errorform.submit(); }</script></head><body onload='javascript:submitForm()'><form name='errorform' action='" + url + "'><input type='hidden' name='ssf-error' value='" + e.getLocalizedMessage() + "'></input></form></body></html>";
					
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
					response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
				}
				
				response.getOutputStream().flush();
			}
			else
			if (fa != null) {
				String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
				FileTypeMap mimeTypes = (FileTypeMap)SpringContextUtil.getBean("mimeTypes");
				response.setContentType(mimeTypes.getContentType(shortFileName));
				if (fileTime.equals("") || 
						!fileTime.equals(String.valueOf(fa.getModification().getDate().getTime()))) {
					response.setHeader("Cache-Control", "private");
					response.setHeader("Pragma", "no-cache");
				}
				String attachment = "";
				if (!downloadFile.equals("")) attachment = "attachment; ";
				response.setHeader(
							"Content-Disposition",
							attachment + "filename=\"" + shortFileName + "\"");
				
				SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
				Date d = fa.getModification().getDate();
				df.applyPattern("EEE, dd MMM yyyy kk:mm:ss zzz");
				response.setHeader(
						"Last-Modified", df.format(d));
				if (viewType.equals(WebKeys.FILE_VIEW_TYPE_SCALED)) {
					try {
						response.setContentType("image/jpeg");
						getFileModule().readScaledFile(parent, entity, fa, response.getOutputStream());
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				} else if (viewType.equals("thumbnail")) {
					try {
						response.setContentType("image/jpeg");
						getFileModule().readThumbnailFile(parent, entity, fa, response.getOutputStream());
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				} else {
					try {
						getFileModule().readFile(parent, entity, fa, response.getOutputStream());				
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				}
	
				response.getOutputStream().flush();
			}
		}
		return null;
	}
	
	private void streamZipFile(HttpServletRequest request,
            				   HttpServletResponse response,
            				   String fileId, String fileTitle)
		throws Exception
	{
		int n;
		byte[] buf = new byte[1024];
		
		try {
			ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
			Document listOfFiles = XmlFileUtil.readStream(TempFileUtil.openTempFile(fileId));

			String filename = listOfFiles.getRootElement().attributeValue("filename");
			response.setContentType("application/zip");
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			if(filename != null && filename != "") {
				String attachment = "attachment; ";
				response.setHeader(
						"Content-Disposition",
						attachment + "filename=\"" + filename + "\"");
			}
			for(Object o : listOfFiles.selectNodes("//file")) {
				Element e = (Element) o;
				String path = e.attributeValue("path");
				zipOut.putNextEntry(new ZipEntry(path));
				FileInputStream in = new FileInputStream(path);
				while((n = in.read(buf, 0, buf.length)) > 0) {
					zipOut.write(buf, 0, n);
				}
				in.close();
			}
			zipOut.finish();
		}
		catch(Exception e) {
			response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
		}

		response.getOutputStream().flush();
	}
	
	public static Document createFileListingForZipDownload(String zipFileName)
	{
		Document listOfFiles = DocumentHelper.createDocument();
		Element listOfFilesRoot = listOfFiles.addElement("root");
		if(zipFileName != null) {
			listOfFilesRoot.addAttribute("filename", zipFileName);
		}
		return listOfFiles;
	}
	
	public static void addFileToList(Document listOfFiles, String path)
	{
		listOfFiles.getRootElement().addElement("file").addAttribute("path", path);
	}
}
