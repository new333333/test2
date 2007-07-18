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

import javax.portlet.RenderResponse;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.portletadapter.taglib.PortletURLTag;
import com.sitescape.team.web.WebKeys;

/**
 * 
 * Escapes JavaScript characters from given string value and outputs to page.
 * 
 */
public class EscapeJavaScript extends TagSupport {

	protected static final Log logger = LogFactory.getLog(EscapeJavaScript.class);

	private String value;

	public int doStartTag() throws JspTagException {
		try {
			if (value != null)
				pageContext.getOut().print(
						StringEscapeUtils.escapeJavaScript(value));
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			this.value = null;
		}

		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

	public void setValue(String value) {
		this.value = value;
	}

}