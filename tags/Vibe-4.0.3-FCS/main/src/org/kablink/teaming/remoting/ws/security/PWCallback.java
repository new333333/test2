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
package org.kablink.teaming.remoting.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.authentication.util.AuthenticationAdapter;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.encrypt.EncryptUtil;
import org.springframework.security.core.AuthenticationException;


/**
 * This class implements standard <code>CallbackHandler</code> interface
 * to allow security system (WS server-side runtime in this case) to interact 
 * with the application to retrieve specific authentication data. 
 * The class is implemented using Apache WSS4J library.
 * 
 * @author jong
 *
 */
public class PWCallback implements CallbackHandler {

	private static final Log logger = LogFactory.getLog(PWCallback.class);
	
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                
                // set the password given a username
                String userName = pc.getIdentifer();
                Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
                
        		try {
        			// Clear request context for the thread just in case.
        			RequestContextHolder.clear();

        			User user = getProfileDao().findUserByName(userName, zoneId);
    				// If we're still here, the user exists.

        			String pwType = WSConstants.PASSWORD_TEXT; // This is our default.
        			String pType = pc.getPasswordType();
        			if(pType != null && !pType.equals(""))
        				pwType = pType;
        			
        			if(pwType.equals(WSConstants.PASSWORD_TEXT)) { // wsse:PasswordText
        				String clearPassword = pc.getPassword();
        				
            			AuthenticationContextHolder.setAuthenticationContext(LoginAudit.AUTHENTICATOR_WS, null);
            			
        				try {
	        				AuthenticationAdapter.authenticate(userName, clearPassword);
        				}
        				catch(AuthenticationException e) {
        					// Authentication failed.
        					logger.warn(e.toString());
         					throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        				}
            			
        				// If still here, the authentication was successful.
        				
            			// This is our only chance to get at that piece of info (ie, user identity) 
            			// when invoked by WS-Security runtime (as opposed to web framework, etc.).
        				// So we should set up our request context here.
            			RequestContext rc = RequestContextUtil.setThreadContext(user);
        			}
        			else { // Most likely wsse:PasswordDigest
        				// As of Teaming 2.1, only wsse:PasswordText is supported.
        				throw new WSSecurityException(WSSecurityException.UNSUPPORTED_ALGORITHM);
        			}
        		}
            	catch(NoUserByTheNameException e) {
            		// With wsse:PasswordText, not setting the password on pc is not enough
            		// for the framework to abort the authentication. Instead, we need to
            		// throw an exception explicitly from here. 
             		throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
            	}
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
}
