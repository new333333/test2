/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.asmodule.servlet.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter {
	
	FilterConfig fc;

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		// Apply the headers
		if(res instanceof HttpServletResponse) {
			HttpServletResponse response = (HttpServletResponse) res;
			
			if ( req instanceof HttpServletRequest )
			{
				String requestURI;
				HttpServletRequest httpRequest;
				
				// Are we dealing with a file that has ".nocache." in its name?
				// A GWT constructed file will have ".nocache." in its file name.  We never
				// want the browser to cache such a file.
				httpRequest = (HttpServletRequest) req;
				requestURI = httpRequest.getRequestURI();
				if ( requestURI.contains( ".nocache." ) )
				{
					Date now;
					
					// Yes
					now = new Date();
					
					response.setDateHeader( "Date", now.getTime() );

					// Set the expiration date to yesterday.
					response.setDateHeader( "Expires", now.getTime() - 86400000L );
					
					// Tell the browser to never cache this file.
					response.setHeader( "Pragma", "no-cache" );
					response.setHeader( "Cache-control", "no-cache, no-store, must-revalidate" );
				}
				// Are we dealing with a file that has ".cache." in its name?
				else if ( requestURI.contains( ".cache." ) )
				{
					// Yes
					// GWT constructs files that have ".cache." in it name.  This
					// tells us the browser can cache this file.
					response.setHeader( "Cache-control", "public,max-age=31536000,no-check" );
					response.setHeader( "Expires", "Wed, 01 Jan 2020 00:00:00 GMT" );
				}
			}
			
			// Set the provided HTTP response parameters
			for (Enumeration e = fc.getInitParameterNames(); e.hasMoreElements();) {
				// Break the param name into header name and scheme name.
				String paramName = (String) e.nextElement();
				String headerName = null;
				String scheme = null;
				int index = paramName.indexOf(":");
				if(index < 0) {
					headerName = paramName;
				}
				else {
					headerName = paramName.substring(0, index);
					scheme = paramName.substring(index+1);
				}
				
				// Set the header only if it isn't already set.
				if(!response.containsHeader(headerName)) {
					if(scheme != null) {
						// applies only if scheme matches
						if(scheme.equalsIgnoreCase(req.getScheme()))
							response.setHeader(headerName, fc.getInitParameter(paramName));
					}
					else {
						// applies regardless of scheme
						response.setHeader(headerName, fc.getInitParameter(paramName));
					}
				}
			}
		}
		// Pass the request/response on. This allows application to selectively overwrite 
		// the previous header values set above. That is, application has the final word
		// over this general settings.
		chain.doFilter(req, res);		
	}

	public void init(FilterConfig fc) throws ServletException {
		this.fc = fc;
	}

	public void destroy() {
	}
}
