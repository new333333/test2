package com.sitescape.ef.taglib;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class DashboardTag extends BodyTagSupport {

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			User user = RequestContextHolder.getRequestContext().getUser();

			// Get the jsp to run
			String jsp = "";
			if (_type == null || _type.equals("")) _type = "config";
			if (_type.equals("config")) {
				jsp = SPropsUtil.getString("dashboard.configJsp." + this._name, "");
			} else if (_type.equals("view")) {
				jsp = SPropsUtil.getString("dashboard.viewJsp." + this._name, "");
			}
			if (!jsp.equals("")) {
				RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
	
				ServletRequest req = new DynamicServletRequest(
					(HttpServletRequest)pageContext.getRequest());
				
				req.setAttribute(WebKeys.DASHBOARD_ID, this._id);
				
				StringServletResponse res = new StringServletResponse(httpRes);
	
				rd.include(req, res);
	
				pageContext.getOut().print(res.getString());
			}

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		finally {
			_name = "";
			_type = "";
		}
	}

	public void setName(String name) {
		_name = name;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setConfiguration(Map configuration) {
		_configuration = configuration;
	}

	private String _name;
	private String _id;
	private String _type;
	private Map _configuration;
	private String _bodyContent;

}