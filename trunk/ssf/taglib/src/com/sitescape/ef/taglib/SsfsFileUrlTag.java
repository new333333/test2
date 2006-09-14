package com.sitescape.ef.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.ssfs.util.SsfsUtil;

public class SsfsFileUrlTag extends TagSupport {
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
		
		String url = SsfsUtil.getInternalFileUrl(binder, entity, elemName, fa);
		
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


