package com.sitescape.ef.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.ssfs.util.SsfsUtil;

public class SsfsLibraryBinderUrlTag extends TagSupport {
	private Binder binder;
    
	public int doStartTag() throws JspException {
		if(binder == null)
			throw new JspException("Binder must be specified");
		
		String url = SsfsUtil.getLibraryBinderUrl(binder);
		
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
}
