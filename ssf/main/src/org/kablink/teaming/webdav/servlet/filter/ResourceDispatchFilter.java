/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.webdav.servlet.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jong
 *
 */
public class ResourceDispatchFilter implements Filter {
	
	private static final int BUFFER_SIZE = 4096;

	private ServletContext context;
	
	private String mainUiPath;
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		
		String resource = req.getPathInfo();
		
		// Check if the client is requesting one of the few static resources
		// available only at the system root.
		if(req.getMethod().equalsIgnoreCase("GET")) {
			if(knownStaticResource(resource)) {
				getKnownStaticResource(resource, (HttpServletResponse)response);
				return; // This request fulfilled.
			}
		}

		if(resource.equals("/") && req.getMethod().equalsIgnoreCase("GET")) {
			// Re-direct the client to the conventional webapp entry point.
			((HttpServletResponse)response).sendRedirect(mainUiPath);
		}
		else {
			// Proceed to the WebDAV service.
			chain.doFilter(request, response);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig fc) throws ServletException {
        this.context = fc.getServletContext();
		mainUiPath = fc.getInitParameter("mainUiPath");
		if(mainUiPath == null)
			throw new ServletException("mainUiPath param is missing");
	}

    private boolean knownStaticResource(String resourcePath) {
		return (resourcePath.equals("/favicon.ico") || resourcePath.equals("/robots.txt"));
	}
    
    private void getKnownStaticResource(String resourcePath, HttpServletResponse resp) throws ServletException, IOException {
 		InputStream in = context.getResourceAsStream(resourcePath);
		if(in != null) {
    		OutputStream out = resp.getOutputStream();
    		try {
    			copyStream(in, out);
    		}
    		finally {
    			try {
    				in.close();
    			}
    			catch(IOException ignore) {}
    		}
		}
		else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, resourcePath);
		}
    }

    private void copyStream(InputStream in, OutputStream out)
			throws IOException {
		// Copy the input stream to the output stream
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
    }

}
