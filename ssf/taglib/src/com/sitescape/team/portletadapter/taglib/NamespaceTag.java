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
package com.sitescape.team.portletadapter.taglib;

import javax.portlet.RenderResponse;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.web.WebKeys;

public class NamespaceTag extends TagSupport {

	public int doStartTag() throws JspTagException {
		try {
			ServletRequest req = pageContext.getRequest();

			RenderResponse renderResponse = (RenderResponse)req.getAttribute(
				WebKeys.JAVAX_PORTLET_RESPONSE);

			pageContext.getOut().print(renderResponse.getNamespace());
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}

		return SKIP_BODY;
	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

}