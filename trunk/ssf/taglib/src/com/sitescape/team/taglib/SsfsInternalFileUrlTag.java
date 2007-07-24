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

public class SsfsInternalFileUrlTag extends TagSupport {
	private Binder binder;
	private DefinableEntity entity;
	private String elemName;
	private FileAttachment fa;
    
	public int doStartTag() throws JspException {
		if(binder == null)
			throw new JspException("Binder must be specified");
		
		if(entity == null)
			throw new JspException("Entity must be specified");
		
		if(elemName == null)
			throw new JspException("Definition element name must be specified");
		
		if(fa == null)
			throw new JspException("File attachment must be specified");
		
		String url = SsfsUtil.getInternalFileUrl((HttpServletRequest) pageContext.getRequest(), binder, entity, elemName, fa);
		
		try {
			pageContext.getOut().print(url);
		} catch (IOException e) {
			throw new JspException(e);
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
	
	public void setElemName(String elemName) {
		this.elemName = elemName;
	}
	
	public void setFileAttachment(FileAttachment fa) {
		this.fa = fa;
	}
}


