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
package com.sitescape.team.portletadapter.support;

import java.util.Vector;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import com.sitescape.team.util.ReflectHelper;

public class PortletInfo {
	private String name;
	private String className;
	private Portlet portlet;
	private PortletConfig portletConfig;
	private Vector mimeTypes;

	public PortletInfo(String name, String className, PortletConfig portletConfig, Vector mimeTypes) 
		throws InstantiationException, IllegalAccessException, ClassNotFoundException, PortletException {
		this.name = name;
		this.className = className;
		this.portlet = (Portlet) ReflectHelper.classForName(className).newInstance();
		this.portlet.init(portletConfig);
		this.portletConfig = portletConfig;
		this.mimeTypes = mimeTypes;
	}

	public String getClassName() {
		return className;
	}

	public String getName() {
		return name;
	}

	public Portlet getPortlet() {
		return portlet;
	}
	
	public PortletConfig getPortletConfig() {
		return portletConfig;
	}
	
	public Vector getMimeTypes() {
		return mimeTypes;
	}
}
