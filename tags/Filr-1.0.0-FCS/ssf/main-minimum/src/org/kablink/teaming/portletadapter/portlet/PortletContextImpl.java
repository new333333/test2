/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portletadapter.portlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.Constants;
import org.kablink.util.GetterUtil;


public class PortletContextImpl implements PortletContext {

	private ServletContext ctx;
	private String ctxName;
	
	private static final Log log = LogFactory.getLog(PortletContextImpl.class);
	
	public PortletContextImpl(ServletContext ctx) {
		this.ctx = ctx;
		this.ctxName = GetterUtil.getString(ctx.getServletContextName());
	}
	
	public String getServerInfo() {
		throw new UnsupportedOperationException();
	}

	public PortletRequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher rd = null;

		try {
			rd = ctx.getRequestDispatcher(path);
		}
		catch (IllegalArgumentException iae) {
			return null;
		}

		// Workaround for bug in Jetty that returns the default request
		// dispatcher instead of null for an invalid path

		if ((rd != null) &&
			(rd.getClass().getName().equals(
				"org.mortbay.jetty.servlet.Dispatcher"))) {

			// Dispatcher[/,default[org.mortbay.jetty.servlet.Default]]

			String rdToString = rd.toString();

			String rdPath = rdToString.substring(11, rdToString.indexOf(","));

			if (rdPath.equals(Constants.SLASH) &&
				!path.equals(Constants.SLASH)) {

				rd = null;
			}
		}

		if (rd != null) {
			return new PortletRequestDispatcherImpl(rd, this, path);
		}
		else {
			return null;
		}
	}

	public PortletRequestDispatcher getNamedDispatcher(String name) {
		throw new UnsupportedOperationException();
	}

	public InputStream getResourceAsStream(String path) {
		return ctx.getResourceAsStream(path);
	}

	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	public String getMimeType(String file) {
		return ctx.getMimeType(file);
	}

	public String getRealPath(String path) {
		return ctx.getRealPath(path);
	}

	public Set getResourcePaths(String path) {
		return ctx.getResourcePaths(path);
	}

	public URL getResource(String path) throws MalformedURLException {
		if ((path == null) || (!path.startsWith(Constants.SLASH))) {
			throw new MalformedURLException();
		}

		return ctx.getResource(path);
	}

	public Object getAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return ctx.getAttribute(name);

	}

	public Enumeration getAttributeNames() {
		return ctx.getAttributeNames();
	}

	public String getInitParameter(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return ctx.getInitParameter(name);

	}

	public Enumeration getInitParameterNames() {
		return ctx.getInitParameterNames();
	}

	public void log(String msg) {
		log.info(msg);
	}

	public void log(String message, Throwable throwable) {
		log.info(message, throwable);
	}

	public void removeAttribute(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		ctx.removeAttribute(name);

	}

	public void setAttribute(String name, Object obj) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		ctx.setAttribute(name, obj);
	}

	public String getPortletContextName() {
		return ctxName;
	}

	public Enumeration<String> getContainerRuntimeOptions() {
		return new Enumeration<String>() {
		    public boolean hasMoreElements() { return false; }
		    public String nextElement() { return null; }
		};
	}

}
