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
package com.sitescape.team.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.ssfs.util.SsfsUtil;

public class SsfsInternalAttachmentUrlTag extends TagSupport {
	private Binder binder;
	private DefinableEntity entity;
	private FileAttachment fa;
	private String escapeSingleQuote = "no";
    
	public int doStartTag() throws JspException {
		if(binder == null)
			throw new JspException("Binder must be specified");
		
		if(entity == null)
			throw new JspException("Entity must be specified");
		
		if(fa == null)
			throw new JspException("File attachment must be specified");
		
		String url = SsfsUtil.getInternalAttachmentUrl((HttpServletRequest) pageContext.getRequest(), binder, entity, fa);
		
		if ("yes".equalsIgnoreCase(escapeSingleQuote)) {
			url = url.replaceAll("'", "\\\\'");
		}
		
		try {
			pageContext.getOut().print(url);
		} catch (IOException e) {
			throw new JspException(e);
		} finally {
			escapeSingleQuote = "no";
		}
	    
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setBinder(Binder binder) {
		this.binder = binder;
	}
	
	public void setEntity(DefinableEntity entity) {
		this.entity = entity;
	}
	
	public void setFileAttachment(FileAttachment fa) {
		this.fa = fa;
	}
	
	public void setEscapeSingleQuote(String escapeSingleQuote) {
		this.escapeSingleQuote = escapeSingleQuote;
	}
}