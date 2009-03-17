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
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.DigestDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.bind.RequestUtils;


public class DigestBasedHardAuthenticationFilter implements Filter {

	protected final Log logger = LogFactory.getLog(getClass());

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		//String zoneName = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "zn");
		String zoneName = WebHelper.getZoneNameByVirtualHost(request);
		Long userId = RequestUtils.getRequiredLongParameter((HttpServletRequest) request, "ui");
		String binderId = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "bi"); 		
		String privateDigest = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "pd"); 
		
		try {
			User user = AuthenticationManagerUtil.authenticate(zoneName, userId, binderId, privateDigest, LoginInfo.AUTHENTICATOR_ICAL);

			RequestContextUtil.setThreadContext(user);
			
			chain.doFilter(request, response); // Proceed
			
			RequestContextHolder.clear();
		}
		catch(UserDoesNotExistException e) {
			logger.warn(e);
			throw new ServletException(e);
		}
		catch(DigestDoesNotMatchException e) {
			logger.warn(e);
			throw new ServletException(e);
		}
	}

	public void destroy() {
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
