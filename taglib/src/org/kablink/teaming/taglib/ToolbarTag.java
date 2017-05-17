/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.taglib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


import javax.portlet.PortletURL;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class ToolbarTag extends BodyTagSupport {
	private String _bodyContent;
	private SortedMap toolbar = null;
	private String format = "";
	private String style = "";
	private boolean item = false;
	private boolean showHelpButton = false;
	private boolean skipSeparator = false;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			RequestContext rc = RequestContextHolder.getRequestContext();
			User user = null;
			Boolean isAccessible = Boolean.FALSE;
			if (rc != null) user = rc.getUser();
			if (user != null) {
				if (user.getDisplayStyle() != null && 
						user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					isAccessible = Boolean.TRUE;
				}
			}
			
			//Output the start of the area
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/toolbar/top.jsp");

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute(WebKeys.TOOLBAR, this.toolbar);
			req.setAttribute(WebKeys.TOOLBAR_FORMAT, this.format);
			req.setAttribute(WebKeys.TOOLBAR_STYLE, this.style);
			req.setAttribute(WebKeys.TOOLBAR_ITEM, this.item);
			req.setAttribute(WebKeys.TOOLBAR_SHOW_HELP_BUTTON, this.showHelpButton);
			req.setAttribute(WebKeys.TOOLBAR_SKIP_SEPARATOR, this.skipSeparator);
			req.setAttribute(WebKeys.TOOLBAR_IS_ACCESSIBLE, isAccessible);
			
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			if (_bodyContent != null) pageContext.getOut().print(_bodyContent);

			//Output the end of the area
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/toolbar/bottom.jsp");
			req = new DynamicServletRequest(httpReq);
			req.setAttribute(WebKeys.TOOLBAR, this.toolbar);
			req.setAttribute(WebKeys.TOOLBAR_FORMAT, this.format);
			req.setAttribute(WebKeys.TOOLBAR_STYLE, this.style);
			req.setAttribute(WebKeys.TOOLBAR_ITEM, this.item);
			req.setAttribute(WebKeys.TOOLBAR_SHOW_HELP_BUTTON, this.showHelpButton);
			req.setAttribute(WebKeys.TOOLBAR_SKIP_SEPARATOR, this.skipSeparator);
			req.setAttribute(WebKeys.TOOLBAR_IS_ACCESSIBLE, isAccessible);
			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			this.toolbar = null;
			this.format = "";
			this.style = "";
			this.item = false;
			this.showHelpButton = false;
			this.skipSeparator = false;
		}
	}

	public void setToolbar(SortedMap toolbar) {
	    this.toolbar = toolbar;
	}

	public void setFormat(String format) {
	    this.format = format;
	}

	public void setStyle(String style) {
	    this.style = style;
	}

	public void setItem(boolean item) {
	    this.item = item;
	}

	public void setShowHelpButton(boolean showHelpButton) {
	    this.showHelpButton = showHelpButton;
	}

	public void setSkipSeparator(boolean skipSeparator) {
		this.skipSeparator = skipSeparator;
	}

}


