/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portletadapter.portlet;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.portletadapter.support.PortletInfo;
import com.sitescape.team.web.WebKeys;

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
