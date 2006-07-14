/*
 * Created on Apr 7, 2005
 *
 *	Stuff for the HTML editor tag
 * 
 */
package com.sitescape.ef.taglib;
import java.util.HashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author billmers
 *;
 */

// This is a stub so far; it is just a 
// gutted version of datepicker.

public class Htmleditor extends TagSupport {
	private String id;
	private String formName;
	private String initText;
	private String height = null;
	private String color = "ButtonFace";
	private String contextPath;
        
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
	
		    if (id == null) {
		        throw new JspException("You must provide an element name"); 
		    }
		    if (height == null) {
		    	height = "250";
		    }
	
		    if (initText == null) {
		    	initText = "";
		    }
	
			//Output the html editor
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/htmlarea/htmlarea.jsp");
	
			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("element_id", this.id);
			req.setAttribute("form_name", this.formName);
			req.setAttribute("init_text", this.initText);
			req.setAttribute("element_height", this.height);
			req.setAttribute("element_color", this.color);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());
	
			return SKIP_BODY;

		}
	    catch (Exception e) {
	    	throw new JspException(e);
	    }
		finally {
		}
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setInitText(String initText) {
		this.initText = initText;
    }

	public void setHeight(String height) {
		this.height = height;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}
}
