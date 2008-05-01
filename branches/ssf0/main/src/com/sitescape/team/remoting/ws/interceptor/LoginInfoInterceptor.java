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
package com.sitescape.team.remoting.ws.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.module.report.ReportModule;

public class LoginInfoInterceptor implements MethodInterceptor {
	
	private ReportModule reportModule;

	protected ReportModule getReportModule() {
		return reportModule;
	}

	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		RequestContext rc = RequestContextHolder.getRequestContext();
		String authenticator = rc.getAuthenticator();
		if(LoginInfo.AUTHENTICATOR_WS.equals(authenticator) ||
				LoginInfo.AUTHENTICATOR_REMOTING_T.equals(authenticator)) {	
			// Message-level authentication is in use, for example, WS with WS-Security
			// authentication or any remoting with token-based authentication. 
			// These protocols require authentication to take place for every message
			// invocation. The fact that you're here means that the authentication has 
			// already happended and it was successful. So we should report the fact.
			getReportModule().addLoginInfo(new LoginInfo(authenticator,
					RequestContextHolder.getRequestContext().getUserId()));		
		}
		return invocation.proceed();
	}

}



