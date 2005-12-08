package com.sitescape.ef.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.util.WebUrlUtil;

public class ServletRootUrlTag extends TagSupport {

	private static Log logger = LogFactory.getLog(ServletRootUrlTag.class);
	
	private Boolean secure;
	
	public int doStartTag() throws JspException {
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		
		String url = null;
		
		if(secure == null)
			WebUrlUtil.getServletRootURL(req);
		else
			WebUrlUtil.getServletRootURL(req, secure.booleanValue());
		
		try {
			pageContext.getOut().print(url);
		} 
		catch (IOException e) {
			logger.error(e);
			throw new JspTagException(e.getMessage());
		}

		return SKIP_BODY;
	}
	
	public void setSecure(boolean secure) {
		this.secure = Boolean.valueOf(secure);
	}
}
