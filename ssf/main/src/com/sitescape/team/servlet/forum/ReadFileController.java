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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;
import com.sitescape.util.FileUtil;
import com.sitescape.util.Validator;

public class ReadFileController extends SAbstractController {
	
	private FileTypeMap mimeTypes;

	protected FileTypeMap getFileTypeMap() {
		return mimeTypes;
	}
	public void setFileTypeMap(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		// Assuming that the full request URL was http://localhost:8080/ssf/s/readFile/xxx/123/456/789/junk.doc,
		// the following call returns "/readFile/xxx/123/456/789/junk.doc" portion of the URL.
		String pathInfo = request.getPathInfo();
		
		String[] args = pathInfo.split("/");
		//We expect the url to be formatted as /readFile/entityType/binderId/entryId/fileId/fileTime/filename.ext
		//  If there is no entryId (in the case where the file is in the binder itself), the entryId is set to "-"
		if (args.length >= 6) {
			try {
				String strEntityType = args[2];
				Long binderId = Long.valueOf(args[3]);
				Long entryId = null;
				if (!args[4].equals("-")) {
					//There is an entryId specified
					entryId = Long.valueOf(args[4]);
				}
				String fileId = args[5];
				String fileTime = args[6];

				DefinableEntity entity = null;
				Binder parent;
				EntityIdentifier.EntityType entityType = null;
				try {
					entityType = EntityIdentifier.EntityType.valueOf(strEntityType);
				} catch(Exception e) {
					entityType = EntityIdentifier.EntityType.none;
				}
				if (entityType.equals(EntityIdentifier.EntityType.folder) || entityType.equals(EntityIdentifier.EntityType.workspace) ||
						entityType.equals(EntityIdentifier.EntityType.profiles)) {
					//the entry is the binder
					if (entryId == null) entryId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
					entity = getBinderModule().getBinder(entryId);
					parent = (Binder) entity;
				} else if (entryId != null) {
					if (entityType.equals(EntityIdentifier.EntityType.folderEntry)) {
						entity = getFolderModule().getEntry(binderId, entryId);
					} else if (entityType.equals(EntityIdentifier.EntityType.none)) {
						//Try to figure out what type of entity this is
						try {
							entity = getFolderModule().getEntry(binderId, entryId);
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
				} else {
					parent = getBinderModule().getBinder(binderId);
					entity = parent;
				}
				//Set up the beans needed by the jsps
				FileAttachment fa = null;
				if (!fileId.equals("")) fa = (FileAttachment)entity.getAttachment(fileId);

				if (fa != null) {
					String shortFileName = FileUtil.getShortFileName(fa.getFileItem().getName());	
					String contentType = mimeTypes.getContentType(shortFileName);
					response.setContentType(contentType);
					response.setHeader("Cache-Control", "private");
					if (fileTime.equals("")) {
						response.setHeader("Cache-Control", "private");
					}
					String attachment = "";
					if (FileHelper.checkIfAttachment(contentType)) attachment = "attachment; ";
					response.setHeader("Content-Disposition",
							attachment + "filename=\"" + FileHelper.encodeFileName(request, shortFileName) + "\"");
					
					SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
					Date d = fa.getModification().getDate();
					df.applyPattern("EEE, dd MMM yyyy kk:mm:ss zzz");
					response.setHeader("Last-Modified", df.format(d));
					try {
						response.setHeader("Content-Length", 
								String.valueOf(FileHelper.getLength(parent, entity, fa)));
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

			} catch(Exception e) {
				//Bad format of url; just return null
				response.getOutputStream().print(NLT.get("file.error.unknownFile"));
			}
		}
		return null;
	}
}
