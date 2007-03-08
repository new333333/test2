package com.sitescape.team.taglib;

import java.io.IOException;

import javax.portlet.RenderResponse;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import com.sitescape.team.web.WebKeys;

/**
 * 
 * Escapes JavaScript characters from given string value and outputs to page.
 * 
 */
public class EscapeJavaScript extends TagSupport {

	private String value;

	public int doStartTag() throws JspTagException {
		try {
			if (value != null)
				pageContext.getOut().print(
						StringEscapeUtils.escapeJavaScript(value));
		} catch (IOException e) {
			e.printStackTrace();
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