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
package com.sitescape.team.taglib;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;
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
	}
	
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();
			String webUrl = null;
			if (attachment != null) webUrl = WebUrlUtil.getFileUrl(req, webPath, attachment);
			else if (searchResult != null) webUrl = WebUrlUtil.getFileUrl(req, webPath, searchResult);
			else if (fileId != null) {
				attachment = (FileAttachment)entity.getAttachment(fileId); 
				if (attachment != null) webUrl = WebUrlUtil.getFileUrl(req, webPath, attachment);
			} else {
				//try the first entity
				Set<FileAttachment> atts = entity.getFileAttachments();
				if (!atts.isEmpty())
					webUrl = WebUrlUtil.getFileUrl(req, webPath, atts.iterator().next());
				
			}
			if (Validator.isNotNull(webUrl)) pageContext.getOut().print(webUrl);
			return SKIP_BODY;
		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
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
}


