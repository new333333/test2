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
package org.kablink.teaming.taglib;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class FileUrlTag extends BodyTagSupport {
    private String webPath = WebKeys.ACTION_READ_FILE;
    private Map searchResult=null;
    private FileAttachment attachment;
	private DefinableEntity entity=null;
	private String fileId=null;
	private boolean baseUrl = false;
	private boolean zipUrl = false;
	private boolean useVersionNumber = false;
	public FileUrlTag() {
		setup();
	}
	/** 
	 * Initalize params at end of call and creation
	 * 
	 *
	 */
	protected void setup() {
		//need to reinitialize - class must be cached
		searchResult=null;
		attachment = null;
		fileId = null;
		entity = null;
		webPath = WebKeys.ACTION_READ_FILE;
		baseUrl = false;
		zipUrl = false;
		useVersionNumber = false;
	}
	
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();
			String webUrl = null;
			boolean download = false;
			if (webPath.equals(WebKeys.ACTION_READ_FILE) && !useVersionNumber) {
				//When viewing files, audit that it was explicitly viewed
				download = true;
			}
			if (attachment != null) webUrl = WebUrlUtil.getFileUrl(req, webPath, attachment, useVersionNumber, download);
			else if (searchResult != null) webUrl = WebUrlUtil.getFileUrl(req, webPath, searchResult);
			else if (fileId != null && !this.zipUrl) {
				attachment = (FileAttachment)entity.getAttachment(fileId); 
				if (attachment != null) webUrl = WebUrlUtil.getFileUrl(req, webPath, attachment, useVersionNumber, download);
			} else {
				if (this.baseUrl) {
					//We are building a base url that has no file name
					webUrl = WebUrlUtil.getFileUrl(req, WebKeys.ACTION_READ_FILE, entity, "");
				} else if (this.zipUrl && fileId == null) {
					//We are building a zip url that has no file name
					webUrl = WebUrlUtil.getFileZipUrl(req, WebKeys.ACTION_READ_FILE, entity);
				} else if (this.zipUrl && fileId != null) {
					//We are building a zip url that has no file name
					webUrl = WebUrlUtil.getFileZipUrl(req, WebKeys.ACTION_READ_FILE, entity, fileId);
				} else {
					//try the first entity
					Set<FileAttachment> atts = entity.getFileAttachments(); 
					if (!atts.isEmpty())
						webUrl = WebUrlUtil.getFileUrl(req, webPath, atts.iterator().next(), useVersionNumber, download);
				}
				
			}
			if (Validator.isNotNull(webUrl)) pageContext.getOut().print(webUrl);
			return SKIP_BODY;
		} catch (Exception e) {
			return SKIP_BODY;
		} finally {
			setup();
		}
	}

	public void setSearch(Map searchResult) {
	    this.searchResult = searchResult;
	}
	public void setFile(FileAttachment attachment) {
	    this.attachment = attachment;
	}
	public void setEntity(DefinableEntity entity) {
		this.entity = entity;
	}
	public void setWebPath(String webPath) {
	    this.webPath = webPath;
	}
	public void setFileId(String fileId) {
	    this.fileId = fileId;
	}
	public void setBaseUrl(boolean baseUrl) {
	    this.baseUrl = baseUrl;
	}
	public void setZipUrl(boolean zipUrl) {
	    this.zipUrl = zipUrl;
	}
	public void setUseVersionNumber(boolean useVersionNumber) {
	    this.useVersionNumber = useVersionNumber;
	}
}


