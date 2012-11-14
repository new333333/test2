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
package org.kablink.teaming.spring.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;

/**
 * @author jong
 *
 */
public class SavedRequestAwareAuthenticationSuccessHandler extends org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler {
	
	public static final String FILR_REDIRECT_AFTER_SUCCESSFUL_LOGIN = "filr-redirect-after-successful-login";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
    	if(session != null) {
    		String redirectUrl = (String) session.getAttribute(FILR_REDIRECT_AFTER_SUCCESSFUL_LOGIN);
    		if(redirectUrl != null) {
    			// This block of code exists to handle the situation where the system needs to redirect
    			// the client to the original resource it attempted to access after successful
    			// authentication via an OpenID provider. Unlike with form-based login, the referer
    			// header information is lost during the OpenID handshake process involving the
    			// browser and the OpenID provider and is not addressed by Spring security. As such,
    			// we need to take care of that situation ourselves by saving the redirect url info
    			// in the guest session object immediately before embarking on OpenID handshake. 
    			// After successful authentication with OpenID provider, Spring security will move
    			// that attribute from the guest session to the newly created session (this is possible
    			// because of the custom configuration we have on the "sessionAuthenticationStrategy"
    			// bean). And then finally here in this block of code, we're retrieving the saved info
    			// and redirect the client to that original url.
    		    // Again, with regular form-based login, this custom code won't get executed and
    			// instead the regular code defined in the super class will get executed.
    			if(logger.isDebugEnabled())
    				logger.debug("Redirecting to '" + redirectUrl + "' based on the session attribute '" + FILR_REDIRECT_AFTER_SUCCESSFUL_LOGIN + "'");
    			session.removeAttribute(FILR_REDIRECT_AFTER_SUCCESSFUL_LOGIN);
    			getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    			return;
    		}
    	}
    	super.onAuthenticationSuccess(request, response, authentication);
    }
}
