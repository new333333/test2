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
package org.kablink.teaming.asmodule.spring.security.preauth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.ui.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.Assert;

public class CookiePreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {
	private String principalRequestHeader = "SM_USER"; 
	private String credentialsRequestHeader;

	/**
	 * Read and returns the header named by <tt>principalRequestHeader</tt> from the request.
	 * 
	 * @throws PreAuthenticatedCredentialsNotFoundException if the header is missing 
	 */
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String principal = request.getHeader(principalRequestHeader);
		
		if (principal == null) {
			throw new PreAuthenticatedCredentialsNotFoundException(principalRequestHeader 
					+ " header not found in request.");
		}

		return principal;
	}	
	
	/**
	 * Credentials aren't usually applicable, but if a <tt>credentialsRequestHeader</tt> is set, this
	 * will be read and used as the credentials value. Otherwise a dummy value will be used. 
	 */
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		if (credentialsRequestHeader != null) {
			String credentials = request.getHeader(credentialsRequestHeader);
			
			return credentials;
		}

		return "N/A";
	}
	
	public void setPrincipalRequestHeader(String principalRequestHeader) {
		Assert.hasText(principalRequestHeader, "principalRequestHeader must not be empty or null");
		this.principalRequestHeader = principalRequestHeader;
	}

	public void setCredentialsRequestHeader(String credentialsRequestHeader) {
		Assert.hasText(credentialsRequestHeader, "credentialsRequestHeader must not be empty or null");		
		this.credentialsRequestHeader = credentialsRequestHeader;
	}

	public int getOrder() {
		return FilterChainOrder.PRE_AUTH_FILTER;
	}
}

