/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.servlet.forum;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.FileTypeMap;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.BrowserSniffer;
import com.sitescape.util.FileUtil;
import com.sitescape.util.Validator;

public class ViewFileController extends SAbstractController {
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
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
				//the entry is the binder
				if (entryId == null) entryId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				entity = getBinderModule().getBinder(entryId);
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
				
				try {
					response.getOutputStream().flush();
				}
				catch(Exception ignore) {}
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
							attachment + "filename=\"" + encodeFileName(request, shortFileName) + "\"");
				
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
						response.setHeader("Content-Length", String.valueOf(getLength(parent, entity, fa)));
						getFileModule().readFile(parent, entity, fa, response.getOutputStream());
						getReportModule().addFileInfo(AuditType.download, fa);
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				}

				try {
					response.getOutputStream().flush();
				}
				catch(Exception ignore) {}
			}
		}
		return null;
	}
	
	private long getLength(Binder binder, DefinableEntity entity, FileAttachment fa) {
		if(ObjectKeys.FI_ADAPTER.equals(fa.getRepositoryName())) {
			return RepositoryUtil.getContentLengthUnversioned(fa.getRepositoryName(), binder, entity, fa.getFileItem().getName());
		}
		else {
			return fa.getFileItem().getLength();
		}
	}
	
	private void streamZipFile(HttpServletRequest request,
            				   HttpServletResponse response,
            				   String fileId, String fileTitle)
		throws Exception
	{
		int n;
		byte[] buf = new byte[1024];
		
		try {
			java.io.InputStream in = TempFileUtil.openTempFile(fileId);

			response.setContentType("application/zip");
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			if (Validator.isNotNull(fileTitle)) {
				String attachment = "attachment; ";
				response.setHeader(
						"Content-Disposition",
						attachment + "filename=\"" + fileTitle + "\"");
			}
			OutputStream out = response.getOutputStream();
			while((n = in.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, n);
			}
			in.close();
			
		} catch(Exception e) {
			response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
		}

		try {
			response.getOutputStream().flush();
		}
		catch(Exception ignore) {}
	}
	
	private String encodeFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
		if(BrowserSniffer.is_ie(request)) {
			String file = URLEncoder.encode(fileName, "UTF8");
			file = StringUtils.replace(file, "+", "%20");
			file = StringUtils.replace(file, "%2B", "+");
			return file;
		}
		else if(BrowserSniffer.is_mozilla(request)) {
			String file = MimeUtility.encodeText(fileName, "UTF8", "Q");
			file = StringUtils.replace(file, "+", "%20");
			file = StringUtils.replace(file, "%2B", "+");
			return file;
		}
		else {
			return fileName;
		}
	}
}
