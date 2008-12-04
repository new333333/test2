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
package org.kablink.teaming.remoting.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.EncryptUtil;
import org.kablink.teaming.util.SpringContextUtil;


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

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                
                // set the password given a username
                String userName = pc.getIdentifer();
                Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
                
        		try {
        			User user = getProfileDao().findUserByName(userName, zoneId);
    				// If we're still here, the user exists.
    				String userEncryptedPassword = user.getPassword();

        			String pwType = pc.getPasswordType();
        			if(pwType != null && pwType.equals(WSConstants.PASSWORD_TEXT)) { // wsse:PasswordText
        				String clearPassword = pc.getPassword();
        				String encryptedPasword = EncryptUtil.encryptPassword(clearPassword);
        				if(encryptedPasword.equals(userEncryptedPassword)) {
        					// Encrypted passwords (digest values) match.
        					// Pass the clear text password (passed in from the client), rather 
        					// than the encrypted one, back to the WS-Security framework. 
        					// This hack works around the problem of not having cleartext
        					// password stored in our database.
        					pc.setPassword(clearPassword);
        				}
        				else {
        					// Invalid password - Encrypted passwords do not match.
        					// For some reason, if we return normally from this method,
        					// the wss4j seems to proceed as if the password verification
        					// was successful, regardless of the password value we set on 
        					// pc. I've found that the library behaves properly when we
        					// throw WSSecurityException instead. Couldn't find relevant
        					// information in the documentation. However, looking at their
        					// source code, throwing this exception seems like a right thing
        					// to do.
         					throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        				}
        			}
        			else { // Assume wsse:PasswordDigest
    					// In this case, we assume that the client has passed in an
    					// Aspen-encrypted password.
        				pc.setPassword(userEncryptedPassword);
        			}
        			
        			// While we are here, let's set up our thread context to the
        			// "potentially" matching user object (because authentication is 
        			// still in progress we may not yet know for sure if the client-supplied
        			// user credential is actually valid). If authentication ultimately
        			// fails, this request thread will return without executing further,
        			// meaning the false user object will not be utilized errorneously.
        			// This is our only chance to get at that piece of info (ie, user 
        			// identify) when invoked by WS runtime (as opposed to web framework).
        			RequestContext rc = RequestContextUtil.setThreadContext(user);
        			// Give it a little more information about how this request came in.
        			rc.setAuthenticator(LoginInfo.AUTHENTICATOR_WS);
        		}
            	catch(NoUserByTheNameException e) {
            		// With wsse:PasswordDigest, all we need to do here is not to
            		// set the password on pc. It does the right thing. 
            		// However, with wsse:PasswordText, the framework doesn't behave
            		// the same. To account for both cases we will simply throw this
            		// exception for now. 
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
