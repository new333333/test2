package com.sitescape.ef.portletadapter.support;

import java.util.Vector;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import com.sitescape.ef.util.ReflectHelper;

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
