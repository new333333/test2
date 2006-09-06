package com.sitescape.ef.taglib;

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

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebUrlUtil;
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
			
			//Output the start of the area
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/toolbar/top.jsp");

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute(WebKeys.TOOLBAR, this.toolbar);
			req.setAttribute(WebKeys.TOOLBAR_STYLE, this.style);
			req.setAttribute(WebKeys.TOOLBAR_ITEM, this.item);
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

}


