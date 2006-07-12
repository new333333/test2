
package com.sitescape.ef.taglib;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DashboardHelper;
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

/**
 *
 * @author  ported by Peter Hurley
 * @version $Revision: 1.0 $
 *
 */
public class SearchFormTag extends BodyTagSupport implements ParamAncestorTag {

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

			String jsp = "/WEB-INF/jsp/tag_jsps/search_form/search_form.jsp";

			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);

			ServletRequest req = null;
			if (_params != null) {
				req = new DynamicServletRequest(httpReq, _params);
			}
			else {
				req = pageContext.getRequest();
			}

			StringServletResponse res = new StringServletResponse(httpRes);

			req.setAttribute(WebKeys.SEARCH_FORM_FORM, this._form);
			req.setAttribute(WebKeys.SEARCH_FORM_ELEMENT, this._element);
			req.setAttribute(WebKeys.SEARCH_FORM_DATA, this._data);

			rd.include(req, res);

			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		finally {
			_form = "";
			_element = "";
		}
	}

	public void setForm(String form) {
		_form = form;
	}

	public void setElement(String element) {
		_element = element;
	}

	public void setData(Map data) {
		_data = data;
	}

	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new HashMap();
		}

		String[] values = (String[])_params.get(name);

		if (values == null) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		_params.put(name, values);
	}

	private String _form;
	private String _element;
	private Map _data;
	private String _bodyContent;
	private Map _params;

}