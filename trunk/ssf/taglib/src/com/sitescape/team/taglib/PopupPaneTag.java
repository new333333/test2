package com.sitescape.team.taglib;

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

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import javax.portlet.PortletURL;


/**
 * @author Dave Griffin
 *
 */
public class PopupPaneTag extends BodyTagSupport {
	private String _bodyContent;
    private String titleTag;
    private String closeScript;
    private String width;
    
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

			httpReq.setAttribute("titleTag", this.titleTag);
			httpReq.setAttribute("closeScript", this.closeScript);
			httpReq.setAttribute("width", this.width);
			
			// Top
			String jsp = "/WEB-INF/jsp/tag_jsps/popup_pane/top.jsp";
			
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			
			ServletRequest req = pageContext.getRequest();

			StringServletResponse res = new StringServletResponse(httpRes);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			// Body

			pageContext.getOut().print(_bodyContent);

			// Bottom

			jsp = "/WEB-INF/jsp/tag_jsps/popup_pane/bottom.jsp";

			rd = httpReq.getRequestDispatcher(jsp);

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
	
	
	public void setTitleTag(String text) {
	    this.titleTag = text;
	}

	public void setCloseScript(String text) {
	    this.closeScript = text;
	}

	public void setWidth(String text) {
	    this.width = text;
	}

}


