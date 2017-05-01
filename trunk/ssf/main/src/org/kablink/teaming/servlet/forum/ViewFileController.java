/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.servlet.forum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.GangliaMonitoring;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TempFileUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;
import org.kablink.util.Http;
import org.kablink.util.Validator;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
public class ViewFileController extends SAbstractController {
	private FileTypeMap mimeTypes;

	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	@Override
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		FileAttachment fa = null;
		String viewType = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_VIEW_TYPE, ""); 
		String fileId = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_ID, ""); 
		String fileTitle = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_TITLE, ""); 
		if (!fileTitle.equals("")) fileTitle = Http.decodeURL(fileTitle);
		String fileTime = ServletRequestUtils.getStringParameter(request, WebKeys.URL_FILE_TIME, ""); 
		if (viewType.equals(WebKeys.FILE_VIEW_TYPE_ZIPPED)) {
			streamZipFile(request, response, fileId, fileTitle);
		}
		else
		if (viewType.equals(WebKeys.FILE_VIEW_TYPE_UPLOAD_FILE)) {
			//This is a request to view a recently uploaded file in the temp area
			String shortFileName = WebHelper.getFileName(fileId);	
			String contentType = getFileTypeMap().getContentType(shortFileName);
			
			//Protect against XSS attacks if this is an HTML file
			contentType = FileUtils.validateDownloadContentType(contentType);

			if (!(contentType.toLowerCase().contains("charset"))) {
				String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
				if (MiscUtil.hasString(encoding)) {
					contentType += ("; charset=" + encoding);
				}
			}
			response.setContentType(contentType);
			response.setHeader("Cache-Control", "private");
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
			
		}
		else {
			String strBinderId = ServletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
			String strEntryId = ServletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			Long entryId = null;
			if (!strEntryId.equals("")) entryId = Long.valueOf(strEntryId);
			String downloadFile = ServletRequestUtils.getStringParameter(request, WebKeys.URL_DOWNLOAD_FILE, "");
			DefinableEntity entity=null;
			Binder parent;
			String strEntityType = ServletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.none.toString());
			EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.valueOf(strEntityType);
			if (entityType.isBinder()) {
				//the entry is the binder
				if (entryId == null) entryId = new Long(ServletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				entity = getBinderModule().getBinder(entryId);
				parent = (Binder) entity;
			} else if (entryId != null) {
				if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) {
					entity = getFolderModule().getEntry(null, entryId);
				} else if (entityType.equals(EntityIdentifier.EntityType.none)) {
					//Try to figure out what type of entity this is
					try {
						entity = getFolderModule().getEntry(null, entryId);
					} catch (Exception e) {}
					if (entity == null) {
						try {
							entity = getProfileModule().getEntry(entryId);
						} catch (Exception e) {}
					}
					
				} else {
					entity = getProfileModule().getEntry(entryId);
				}
				parent = entity.getParentBinder();
			} else if (strBinderId.equals("")) {
				//There is no binderId or entryId, see if we can get these from the fileId itself
				fa = BinderHelper.getFileAttachmentById(this, fileId);
				parent = null;
				if (fa != null) {
					entity = fa.getOwner().getEntity();
					if (entity instanceof Binder) parent = (Binder)entity;
					if (entity instanceof FolderEntry) parent = ((FolderEntry)entity).getParentBinder();
				}
			} else {
				Long binderId = new Long(ServletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
				parent = getBinderModule().getBinder(binderId);
				entity = parent;
			}
			//Set up the beans needed by the jsps
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
			if (topAtt == null) {
				//See if there is a title included
				if (!fileTitle.equals("")) {
					fa = entity.getFileAttachment(fileTitle);
					if (fa != null) {
						fileId = fa.getId();
						topAtt = (FileAttachment)entity.getAttachment(fileId);
					}
				}
			}
			if (topAtt != null) {
				//see if we want a version
				String versionId = ServletRequestUtils.getStringParameter(request, WebKeys.URL_VERSION_ID, ""); 
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
					response.setHeader("Cache-Control", "private");
					getConvertedFileModule().readCacheHtmlFile(request.getRequestURI(), parent, entity, fa, response.getOutputStream());
					GangliaMonitoring.incrementFilePreviewRequests();
					getReportModule().addFileInfo(AuditType.view, fa);
					return null;
				}
				catch(Exception e) {
					String url = WebUrlUtil.getServletRootURL(request);
					url += "errorHandler";
					String eMsg = e.getLocalizedMessage();
					if (eMsg == null) eMsg = e.toString();
					eMsg = eMsg.replaceAll("\"", "'");
					String output = "<html><head><script language='javascript'>function submitForm(){ document.errorform.submit(); }</script></head><body onload='javascript:submitForm()'><form name='errorform' action='" + url + "'><input type='hidden' name='ssf-error' value=\"" + eMsg + "\"></input></form></body></html>";
					
					response.setContentType("text/html; charset=UTF-8");
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
					String fileName = ServletRequestUtils.getStringParameter(request, "filename", ""); 
					if (viewType.equals("url"))
					{
						response.setContentType("text/html");
						response.setHeader("Cache-Control", "private");
						getConvertedFileModule().readCacheUrlReferenceFile(parent, entity, fa, response.getOutputStream(), fileName);
					}
					else
					{
						response.setContentType("image/jpeg");
						getConvertedFileModule().readCacheImageReferenceFile(parent, entity, fa, response.getOutputStream(), fileName);
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
				String contentType = mimeTypes.getContentType(shortFileName);
				//Protect against XSS attacks if this is an HTML file
				contentType = FileUtils.validateDownloadContentType(contentType);

				if (!(contentType.toLowerCase().contains("charset"))) {
					String encoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
					if (MiscUtil.hasString(encoding)) {
						contentType += ("; charset=" + encoding);
					}
				}
				
				response.setContentType(contentType);
				response.setHeader("Cache-Control", "private");
				if (fileTime.equals("")) {
					response.setHeader("Cache-Control", "private");
				}
				String attachment = "";
				if (!downloadFile.equals("")) attachment = "attachment; ";
				response.setHeader(
							"Content-Disposition",
							attachment + "filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\"");
				
				SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
				Date d = fa.getModification().getDate();
				df.applyPattern("EEE, dd MMM yyyy kk:mm:ss zzz");
				response.setHeader(
						"Last-Modified", df.format(d));
				if (viewType.equals(WebKeys.FILE_VIEW_TYPE_SCALED)) {
					try {
						response.setContentType("image/jpeg");
						getConvertedFileModule().readScaledFile(parent, entity, fa, response.getOutputStream());
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				} else if (viewType.equals("thumbnail")) {
					try {
						response.setContentType("image/jpeg");
						getConvertedFileModule().readThumbnailFile(parent, entity, fa, response.getOutputStream());
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				} else {
					try {
						// Can the user download files?
						boolean canDownload = AdminHelper.getEffectiveDownloadSetting(this, RequestContextHolder.getRequestContext().getUser());
						if (canDownload) {
							// Yes!
							if (!fa.isEncrypted()) {
								//The file length may be wrong if the file is encrypted. Don't give the wrong length, it confuses the browser
								response.setHeader("Content-Length", 
									String.valueOf(FileHelper.getLength(parent, entity, fa)));
							}
							getFileModule().readFile(parent, entity, fa, response.getOutputStream());
							getReportModule().addFileInfo(AuditType.view, fa);
						}
						else {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error.cantDownload"));
						}
					}
					catch(Exception e) {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error") + ": " + e.getLocalizedMessage());
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
	
	private void streamZipFile(HttpServletRequest request,
            				   HttpServletResponse response,
            				   String fileId, String fileTitle)
		throws Exception
	{
		int n;
		byte[] buf = new byte[1024];
		
		java.io.InputStream in = null;
		try {
			in = TempFileUtil.openTempFile(fileId);

			response.setContentType("application/zip");
			response.setHeader("Cache-Control", "private");
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
			in = null;
			
		} catch(Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, NLT.get("file.error") + ": " + e.getLocalizedMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch(Exception e) {}
			}
		}

		try {
			response.getOutputStream().flush();
		}
		catch(Exception ignore) {}
	}
}
