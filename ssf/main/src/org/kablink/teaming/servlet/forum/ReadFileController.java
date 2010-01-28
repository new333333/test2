/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.FileHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.FileUtil;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.servlet.ModelAndView;

public class ReadFileController extends AbstractReadFileController {
	
	private FileTypeMap mimeTypes = new ConfigurableMimeFileTypeMap();
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		// Assuming that the full request URL was http://localhost:8080/ssf/s/readFile/entityType/entryId/fileTime/fileVersion/filename.ext
		// the following call returns "/readFile/entityType/entryId/fileId/fileTime/fileVersion/filename.ext" portion of the URL.
		String pathInfo = request.getPathInfo();
		
		String[] args = pathInfo.split(Constants.SLASH);
		//We expect the url to be formatted as /readFile/entityType/entryId/fileTime/fileVersion/filename.ext
		//  or /readFile/entityType/entryId/zip
		//To support sitescape forum, where folder structures were allowed on an entry, the url may contain more pathinfo.
		//fileVersion=last, read latest
		//fileTime is present for browser caching
		//filename is present for browser handling of relative files
		if (args.length == WebUrlUtil.FILE_URL_ZIP_ARG_LENGTH && 
				String.valueOf(args[WebUrlUtil.FILE_URL_FILE_ID]).equals("zip")) {
			//The user wants a zip file of all attachments
			try {
				Boolean singleByte = SPropsUtil.getBoolean("export.filename.8bitsinglebyte.only", true);
				DefinableEntity entity = getEntity(args[WebUrlUtil.FILE_URL_ENTITY_TYPE], Long.valueOf(args[WebUrlUtil.FILE_URL_ENTITY_ID]));
				Set<Attachment> attachments = entity.getAttachments();
				String fileName = getBinderModule().filename8BitSingleByteOnly(entity.getTitle() + ".zip", "files.zip", singleByte);
				response.setContentType(mimeTypes.getContentType(fileName));
				response.setHeader("Cache-Control", "private");
				response.setHeader(
							"Content-Disposition",
							"attachment; filename=\"" + fileName + "\"");
				
				InputStream fileStream = null;
				ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());		
			
				//Standard zip encoding is cp437. (needed when chars are outside the ASCII range)
				zipOut.setEncoding("cp437");
				for (Attachment attachment : attachments) {
					if (attachment instanceof FileAttachment) {
						String attName = getBinderModule().filename8BitSingleByteOnly((FileAttachment)attachment, singleByte);
	
						try {
							if (entity.getEntityType().equals(EntityType.folderEntry)) {
								fileStream = getFileModule().readFile(entity.getParentBinder(), entity, (FileAttachment)attachment);
							} else if (entity.getEntityType().equals(EntityType.folder) || 
									entity.getEntityType().equals(EntityType.workspace)) {
								fileStream = getFileModule().readFile((Binder)entity, entity, (FileAttachment)attachment);
							} else {
								zipOut.finish();
								return null;
							}
	
							zipOut.putNextEntry(new ZipEntry(attName));
							FileUtil.copy(fileStream, zipOut);
							zipOut.closeEntry();
	
							fileStream.close();
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
				zipOut.finish();
			
				return null;
			} catch(Exception e) {
				//Bad format of url; just return null
				response.getOutputStream().print(NLT.get("file.error.unknownFile"));
			}
			return null;
		
		} else if (args.length < WebUrlUtil.FILE_URL_ARG_LENGTH) {
			return null;
		
		} else {
			try {
				DefinableEntity entity = getEntity(args[WebUrlUtil.FILE_URL_ENTITY_TYPE], Long.valueOf(args[WebUrlUtil.FILE_URL_ENTITY_ID]));
				//Set up the beans needed by the jsps
				FileAttachment fa = null;
				if (args.length > WebUrlUtil.FILE_URL_ARG_LENGTH && entity instanceof FolderEntry) {
					fa = getAttachment((FolderEntry)entity, Arrays.asList(args).subList(WebUrlUtil.FILE_URL_NAME, args.length).toArray());
					//entity may have changed
					if (fa != null) {
						entity = fa.getOwner().getEntity();
					}
				} else {
					fa = getAttachment(entity, args[WebUrlUtil.FILE_URL_NAME], args[WebUrlUtil.FILE_URL_VERSION], args[WebUrlUtil.FILE_URL_FILE_ID]);
				}
	
				if (fa != null) {
					String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
					String contentType = getFileTypeMap().getContentType(shortFileName);
					response.setContentType(contentType);
					boolean isHttps = request.getScheme().equalsIgnoreCase("https");
					String cacheControl = "private";
					if (isHttps) {
						response.setHeader("Pragma", "public");
						cacheControl += ", proxy-revalidate, s-maxage=0";
					}
					response.setHeader("Cache-Control", cacheControl);
					String attachment = "";
					if (FileHelper.checkIfAttachment(contentType)) attachment = "attachment; ";
					response.setHeader("Content-Disposition",
							attachment + "filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\"");
					response.setHeader("Last-Modified", formatDate(fa.getModification().getDate()));	
					try {
						Binder parent = getBinder(entity);
						response.setHeader("Content-Length", 
								String.valueOf(FileHelper.getLength(parent, entity, fa)));
						getFileModule().readFile(parent, entity, fa, response.getOutputStream());
						getReportModule().addFileInfo(AuditType.download, fa);
					}
					catch(Exception e) {
						response.getOutputStream().print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
					}
				} else {
					response.getOutputStream().print(NLT.get("file.error.unknownFile"));
				}
				try {
					response.getOutputStream().flush();
				}
				catch(Exception ignore) {}
	
			} catch(Exception e) {
				//Bad format of url; just return null
				response.getOutputStream().print(NLT.get("file.error.unknownFile"));
			}
		}
		
		return null;
	}
}
