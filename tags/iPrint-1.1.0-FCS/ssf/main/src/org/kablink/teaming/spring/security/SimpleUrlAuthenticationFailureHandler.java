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

package org.kablink.teaming.spring.security;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.util.Validator;
import org.springframework.security.core.AuthenticationException;

/**
 * The purpose of this class is to change the behavior of <code>SimpleUrlAuthenticationFailureHandler</code>
 * class so that the determination of failure URL takes a dynamically-specified override value stored in the 
 * request object into consideration. If such override value exists, it takes precedence over the
 * statically-configured defaultFailureUrl value.
 * 
 * @author jong
 *
 */
public class SimpleUrlAuthenticationFailureHandler extends org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler {

	// Unfortunately, the defaultFailureUrl variable in the super class is private. So we need to make a copy of it here.
	private String defFailureUrl;
	
	@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

		// Instead of using the statically-configured value of defaultFailureUrl variable,  
		// check to see if there's an override value specified in the request object.
		String failureUrl = decideFailureUrl(request);
		
        if (failureUrl == null) {
            logger.debug("No failure URL set, sending 401 Unauthorized error");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        } else {
            saveException(request, exception);

            if (isUseForward()) {
                logger.debug("Forwarding to " + failureUrl);

                request.getRequestDispatcher(failureUrl).forward(request, response);
            } else {
                logger.debug("Redirecting to " + failureUrl);
                String redirectUrl = request.getParameter("spring-security-redirect");
                if(Validator.isNotNull(redirectUrl)) {
                    if(failureUrl.contains("?"))
                    	failureUrl += "&refererUrl=" + URLEncoder.encode(redirectUrl, "UTF-8");
                    else
                    	failureUrl +="?refererUrl=" + URLEncoder.encode(redirectUrl, "UTF-8");
                }
                getRedirectStrategy().sendRedirect(request, response, failureUrl);
            }
        }
    }

    @Override
    public void setDefaultFailureUrl(String defaultFailureUrl) {
    	super.setDefaultFailureUrl(defaultFailureUrl);
    	// Just copy the value.
    	this.defFailureUrl = defaultFailureUrl;
    }
    
    private String decideFailureUrl(HttpServletRequest request) {
    	String value = request.getParameter("authenticationFailureUrl");
    	if(Validator.isNull(value))
    		return this.defFailureUrl;
    	else
    		return value;
    }
}
