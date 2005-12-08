package com.sitescape.ef.portletadapter.portlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

public class PortletConfigImpl implements PortletConfig {

	private String portletName;
	private PortletContext portletCtx;
	private Map params;
	private String resourceBundle;
	
	public PortletConfigImpl(String portletName, PortletContext portletCtx,
							 Map params, String resourceBundle) {

		this.portletName = portletName;
		this.portletCtx = portletCtx;
		this.params = params;
		this.resourceBundle = resourceBundle;
	}

	public String getPortletName() {
		return portletName;
	}

	public PortletContext getPortletContext() {
		return portletCtx;
	}

	public ResourceBundle getResourceBundle(Locale locale) {
		
		// TODO This implementation is rather  inefficient as is the case with 
		// typical usage of ResourceBundle...
				
		return ResourceBundle.getBundle(resourceBundle, locale, 
				Thread.currentThread().getContextClassLoader());
	}

	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return (String)params.get(name);
	}

	public Enumeration getInitParameterNames() {
		return Collections.enumeration(params.keySet());
	}
}
