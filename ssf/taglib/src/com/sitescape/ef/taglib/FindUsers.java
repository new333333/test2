package com.sitescape.ef.taglib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.ef.util.NLT;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class FindUsers extends TagSupport {
    private List userList;
    private String formElement;
    
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			//Output the start of the area
			RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/find_users/user_list.jsp");

			Map _params = new HashMap();
			_params.put("user_list", this.userList);
			_params.put("form_element", this.formElement);

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			StringServletResponse res = new StringServletResponse(httpRes);
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

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setUserList(List userList) {
	    this.userList = userList;
	}

	public void setFormElement(String formElement) {
	    this.formElement = formElement;
	}

}


