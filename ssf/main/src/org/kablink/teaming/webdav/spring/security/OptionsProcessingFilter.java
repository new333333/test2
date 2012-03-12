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
package org.kablink.teaming.webdav.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.providers.anonymous.AnonymousProcessingFilter;
import org.springframework.security.ui.FilterChainOrder;

/**
 * @author jong
 *
 */
public class OptionsProcessingFilter extends AnonymousProcessingFilter {

	private static final String OPTIONS_METHOD = "OPTIONS";
	
	/* (non-Javadoc)
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	@Override
	public int getOrder() {
		// This filter must be placed not only before regular processing filters such as
		// Basic auth and form-based auth filters but also BEFORE any pre-authentication
		// filter so that the authorization decision about OPTIONS method can be made
		// before ANY type of user identity information is taken into consideration.
		// The anonymous filter this implementation is based on comes way later in the
		// filter pipeline (even after form-based and Basic authentications), so it's
		// crucial to override this method.
		return FilterChainOrder.PRE_AUTH_FILTER-1;
	}

    protected boolean applyAnonymousForThisRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(OPTIONS_METHOD);
    }

}
