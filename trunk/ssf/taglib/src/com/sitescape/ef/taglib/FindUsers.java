package com.sitescape.ef.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class FindUsers extends TagSupport {
    private Set userList;
    private String formName;
    private String formElement;
    private String width = "30";
    private String type;
    private Boolean singleUser;
    
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this.userList == null) this.userList = new HashSet();
			if (this.type == null) this.type = WebKeys.USER_SEARCH_USER_GROUP_TYPE_USER;
			if (singleUser == null) singleUser = false;
			
			//Output the start of the area
			RequestDispatcher rd;
			if (singleUser) {
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/find_users/single_user.jsp");
			} else {
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/find_users/user_list.jsp");
			}

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("user_list", this.userList);
			req.setAttribute("form_name", this.formName);
			req.setAttribute("form_element", this.formElement);
			req.setAttribute("element_width", this.width);
			req.setAttribute("list_type", this.type);
			req.setAttribute("singleUser", this.singleUser);
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			this.userList = null;
			this.singleUser = false;
			this.width = "30";
		}
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setUserList(Set userList) {
	    this.userList = userList;
	}

	public void setFormName(String formName) {
	    this.formName = formName;
	}

	public void setFormElement(String formElement) {
	    this.formElement = formElement;
	}

	public void setWidth(String width) {
	    this.width = width;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setSingleUser(Boolean singleUser) {
	    this.singleUser = singleUser;
	}

}


