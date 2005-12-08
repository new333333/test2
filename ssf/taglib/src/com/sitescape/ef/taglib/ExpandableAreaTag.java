package com.sitescape.ef.taglib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.sitescape.ef.web.util.WebUrlUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import javax.portlet.PortletURL;


/**
 * @author Peter Hurley
 *
 */
public class ExpandableAreaTag extends BodyTagSupport {
	private String _bodyContent;
	private String title = "";
    
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
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tags/expandable_area/top.jsp");

			Map _params = new HashMap();
			_params.put("title", new String[] {this.title});

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			pageContext.getOut().print(_bodyContent);

			//Output the end of the area
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tags/expandable_area/bottom.jsp");
			req = new DynamicServletRequest(httpReq, _params);
			res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
		}
	}

	public void setTitle(String title) {
	    this.title = title;
	}

}


