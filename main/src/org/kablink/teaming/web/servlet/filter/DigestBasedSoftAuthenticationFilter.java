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
package org.kablink.teaming.web.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.DigestDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserAccountNotActiveException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.bind.ServletRequestUtils;


public class DigestBasedSoftAuthenticationFilter implements Filter {

	protected final Log logger = LogFactory.getLog(getClass());

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		//String zoneName = ServletRequestUtils.getRequiredStringParameter((HttpServletRequest) request, "zn");
		String zoneName = WebHelper.getZoneNameByVirtualHost(request);
		Long userId = null;
		String binderId = "";
		String privateDigest = "";
		
		RequestContextHolder.clear();

		try {
			userId = ServletRequestUtils.getRequiredLongParameter((HttpServletRequest) request, "ui");
			binderId = ServletRequestUtils.getRequiredStringParameter((HttpServletRequest) request, "bi"); 		
			privateDigest = ServletRequestUtils.getRequiredStringParameter((HttpServletRequest) request, "pd"); 
		} catch(Exception e) {
			logger.warn("RSS: "+e.getLocalizedMessage());
		}
		
		if (userId != null && !binderId.equals("") && !privateDigest.equals("")) {
			try {
				User user = AuthenticationManagerUtil.authenticate(zoneName, userId, binderId, privateDigest, LoginAudit.AUTHENTICATOR_RSS);
	
				RequestContextUtil.setThreadContext(user);
			}
			catch(UserDoesNotExistException e) {
				logger.warn("RSS: "+e.getLocalizedMessage());
				request.setAttribute(WebKeys.UNAUTHENTICATED_REQUEST, Boolean.TRUE);
			}
			catch(UserAccountNotActiveException e) {
				logger.warn("RSS: "+e.getLocalizedMessage());
				request.setAttribute(WebKeys.UNAUTHENTICATED_REQUEST, Boolean.TRUE);
			}
			catch(DigestDoesNotMatchException e) {
				logger.warn("RSS: "+e.getLocalizedMessage());
				request.setAttribute(WebKeys.UNAUTHENTICATED_REQUEST, Boolean.TRUE);
			}
		} else {
			request.setAttribute(WebKeys.UNAUTHENTICATED_REQUEST, Boolean.TRUE);
		}
		
		chain.doFilter(request, response); // Proceed
		
		RequestContextHolder.clear();
	}

	public void destroy() {
	}

	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
