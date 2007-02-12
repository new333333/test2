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
 * @author Peter Hurley
 *
 */
public class MarkupTag extends BodyTagSupport {
	private String _bodyContent;
	private DefinableEntity entity = null;
	private String type = WebKeys.MARKUP_VIEW;
    
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
			
			// Transform the body
			String translatedString = _bodyContent;
			if (type.equals(WebKeys.MARKUP_VIEW)) { 
				//This page is being viewed. Transform markup into view html
				translatedString = WebHelper.markupReplaceForView(httpReq, entity, _bodyContent);
			}
			pageContext.getOut().print(translatedString);

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e); 
	    }
		finally {
			this.type = WebKeys.MARKUP_VIEW;
			this.entity = null;
		}
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setEntity(DefinableEntity entity) {
	    this.entity = entity;
	}

}


