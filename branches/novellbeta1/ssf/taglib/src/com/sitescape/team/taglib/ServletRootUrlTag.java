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
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.web.util.WebUrlUtil;

public class ServletRootUrlTag extends TagSupport {

	private static Log logger = LogFactory.getLog(ServletRootUrlTag.class);
	
	private Boolean secure;
	
	public int doStartTag() throws JspException {
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		
		String url = null;
		
		if(secure == null)
			url = WebUrlUtil.getServletRootURL(req);
		else
			url = WebUrlUtil.getServletRootURL(req, secure.booleanValue());
		
		try {
			pageContext.getOut().print(url);
		} 
		catch (IOException e) {
			logger.error(e);
			throw new JspTagException(e.getLocalizedMessage());
		}

		return SKIP_BODY;
	}
	
	public void setSecure(boolean secure) {
		this.secure = Boolean.valueOf(secure);
	}
}
