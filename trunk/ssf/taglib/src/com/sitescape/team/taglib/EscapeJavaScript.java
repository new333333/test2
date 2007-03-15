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
			logger.error(e.getMessage(), e);
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