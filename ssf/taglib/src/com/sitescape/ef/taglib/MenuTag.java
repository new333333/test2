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
public class MenuTag extends BodyTagSupport {
	private String _bodyContent;
	private String title = "";
	private String titleId = "";
	private String _class = "";
	private String openStyle = "slide_down";     //slide_down, slide_right, immediate
	private String anchor = "";
	private String offsetTop = "8";
	private String offsetLeft = "4";
    
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
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menu/top.jsp");

			Map _params = new HashMap();
			_params.put("title", new String[] {this.title});
			_params.put("titleId", new String[] {this.titleId});
			_params.put("class", new String[] {this._class});
			_params.put("openStyle", new String[] {this.openStyle});
			_params.put("anchor", new String[] {this.anchor});
			_params.put("offsetTop", new String[] {this.offsetTop});
			_params.put("offsetLeft", new String[] {this.offsetLeft});

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			// Body
			pageContext.getOut().print(_bodyContent);

			//Output the end of the area
			rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menu/bottom.jsp");
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

	public void setTitleId(String titleId) {
	    this.titleId = titleId;
	}

	public void setClass(String divClass) {
	    this._class = divClass;
	}

	public void setOpenStyle(String openStyle) {
	    this.openStyle = openStyle;
	}

	public void setAnchor(String anchor) {
	    this.anchor = anchor;
	}

	public void setOffsetTop(String offsetTop) {
	    this.offsetTop = offsetTop;
	}

	public void setOffsetLeft(String offsetLeft) {
	    this.offsetLeft = offsetLeft;
	}

}


