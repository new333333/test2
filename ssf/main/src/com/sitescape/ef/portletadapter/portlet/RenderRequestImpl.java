package com.sitescape.ef.portletadapter.portlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.portletadapter.support.PortletInfo;
import com.sitescape.ef.web.WebKeys;

public class RenderRequestImpl extends PortletRequestImpl implements RenderRequest {

	private Map params;

	public RenderRequestImpl(HttpServletRequest req, PortletInfo portletInfo, PortletContext portletContext) {
		super(req, portletInfo, portletContext);
	}

	public String getParameter(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		if(params == null)
			return req.getParameter(name);
		else {
			String[] values = (String[]) params.get(name);
			if(values != null)
				return values[0];
			else
				return null;
		}
	}

	public Enumeration getParameterNames() {
		if(params == null) {
			return req.getParameterNames();
		}
		else {
			return new Vector(params.keySet()).elements();
		}
	}

	public String[] getParameterValues(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		if(params == null)
			return req.getParameterValues(name);
		else
			return (String[]) params.get(name);
	}

	public Map getParameterMap() {
		if(params == null)
			return req.getParameterMap();
		else
			return params;
	}

	// Non-standard
	public void setRenderParameters(Map params) {
		this.params = params;
	}
	
	public void defineObjects(PortletConfig portletConfig, RenderResponse res) {
		setAttribute(WebKeys.JAVAX_PORTLET_CONFIG, portletConfig);
		setAttribute(WebKeys.JAVAX_PORTLET_REQUEST, this);
		setAttribute(WebKeys.JAVAX_PORTLET_RESPONSE, res);
	}
}
