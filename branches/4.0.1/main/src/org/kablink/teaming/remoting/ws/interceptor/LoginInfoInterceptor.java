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
package org.kablink.teaming.remoting.ws.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.module.report.ReportModule;


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
		String authenticator = AuthenticationContextHolder.getAuthenticator();
		if(LoginAudit.AUTHENTICATOR_WS.equals(authenticator) ||
				LoginAudit.AUTHENTICATOR_REMOTING_T.equals(authenticator)) {	
			// Message-level authentication is in use, for example, WS with WS-Security
			// authentication or any remoting with token-based authentication. 
			// These protocols require authentication to take place for every message
			// invocation. The fact that you're here means that the authentication has 
			// already happended and it was successful. So we should report the fact.
			
			/* As of Vibe Hudson, we are not going to create audit log for this action.
			 * Since this creates one log per SOAP message, it can literally flood the
			 * log table with large number of (and more importantly with very little
			 * value) identical records consuming disk space and memory.
			 * If customer complains about this (which I doubt), we will revisit then.
			LoginAudit loginInfo = new LoginAudit(authenticator,
					ZoneContextHolder.getClientAddr(),
					RequestContextHolder.getRequestContext().getUserId());
			
			if(rc.getAccessToken() != null)
				loginInfo.setApplicationId(rc.getAccessToken().getApplicationId());
			
			getReportModule().addLoginInfo(loginInfo);		
			*/
		}
		return invocation.proceed();
	}

}



