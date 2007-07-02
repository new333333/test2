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

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import javax.portlet.PortletURL;


/**
 * @author Peter Hurley
 *
 */
public class ToolbarTag extends BodyTagSupport {
	private String _bodyContent;
	private SortedMap toolbar = null;
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
				if (user.getDisplayStyle() != null && user.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
					isAccessible = Boolean.TRUE;
				}
			}
			
			//Output the start of the area
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/toolbar/top.jsp");

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute(WebKeys.TOOLBAR, this.toolbar);
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
			this.style = "";
			this.item = false;
			this.showHelpButton = false;
			this.skipSeparator = false;
		}
	}

	public void setToolbar(SortedMap toolbar) {
	    this.toolbar = toolbar;
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


