/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.asmodule.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.asmodule.bridge.BridgeUtil;

public class SSFClassLoaderServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	// guarded by this
	private HttpServlet servlet;
	
	private ServletConfig config;

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		super.init();
	}

	public void service(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {

		HttpServlet s = getServlet();
		
		ClassLoader contextClassLoader =
			Thread.currentThread().getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(
				BridgeUtil.getClassLoader());

			s.service(req, res);
		}
		finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}

	public void destroy() {
		ClassLoader contextClassLoader =
			Thread.currentThread().getContextClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(
					BridgeUtil.getClassLoader());

			synchronized(this) {
				if(servlet != null) {
					servlet.destroy();
					servlet = null;
				}
			}
		}
		finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
	}

	private synchronized HttpServlet getServlet() throws ServletException {
		if(servlet == null) {
			// Lazy load and initialize
			lazyLoadAndInit();
		}
		return servlet;
	}
	
	private void lazyLoadAndInit() throws ServletException {
		ClassLoader contextClassLoader =
			Thread.currentThread().getContextClassLoader();

		ClassLoader ssfClassLoader = BridgeUtil.getClassLoader();

		try {
			Thread.currentThread().setContextClassLoader(ssfClassLoader);

			String servletClass = config.getInitParameter("servlet-class");

			HttpServlet s = (HttpServlet)ssfClassLoader.loadClass(
				servletClass).newInstance();

			s.init(config);
			
			this.servlet = s;
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
		finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}				
	}
}
