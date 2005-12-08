package com.sitescape.ef.portletadapter.taglib;

import com.sitescape.ef.portletadapter.support.KeyNames;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class ParamAncestorTagImpl
	extends BodyTagSupport implements ParamAncestorTag {

	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new LinkedHashMap();
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

	public void clearParams() {
		if (_params != null) {
			_params.clear();
		}
	}

	public Map getParams() {
		return _params;
	}

	public ServletContext getServletContext() {
		ServletRequest req = pageContext.getRequest();
		
		return (ServletContext)req.getAttribute(KeyNames.CTX);
	}

	public ServletRequest getServletRequest() {
		ServletRequest req = pageContext.getRequest();

		if (_params != null) {
			req = new DynamicServletRequest((HttpServletRequest)req, _params);
		}

		return req;
	}

	public StringServletResponse getServletResponse() {
		return new StringServletResponse(
			(HttpServletResponse)pageContext.getResponse());
	}

	private Map _params;

}