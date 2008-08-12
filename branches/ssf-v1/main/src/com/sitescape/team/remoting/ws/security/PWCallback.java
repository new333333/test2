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
package com.sitescape.team.remoting.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;

import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
import com.sitescape.team.security.authentication.AuthenticationException;
import com.sitescape.team.util.EncryptUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;

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
                String zoneName = SZoneConfig.getDefaultZoneName();
                
        		try {
        			User user = getProfileDao().findUserByName(userName, zoneName);
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
        					// Encrypted passwords do not match. 
        					// For some reason, if we don't throw an exception, the wss4j 
        					// seems to proceed as if the password verification succeeded
        					// regardless of the password value we set on pc. 
        					// So for now we throw an exception to prevent the framework
        					// from proceeding normally.
        					// TODO requires further investigation.
        					throw new AuthenticationException("Invalid password");
        				}
        			}
        			else { // Assume wsse:PasswordDigest
    					// In this case, we assume that the client has passed in an
    					// Aspen-encrypted password.
        				pc.setPassword(userEncryptedPassword);
        			}
        			
        			// While we are here, let's set up our thread context using
        			// the user information, since this is our only change to 
        			// get at that piece of info when invoked by WS runtime
        			// (as opposed to web framework).
        			RequestContextUtil.setThreadContext(user);
        		}
            	catch(NoUserByTheNameException e) {
            		// With wsse:PasswordDigest, all we need to do here is not to
            		// set the password on pc. It does the right thing. 
            		// However, with wsse:PasswordText, the framework doesn't behave
            		// the same. To account for both cases we will simply throw
            		// an exception for now. 
            		// TODO requires further investigation.
            		throw new AuthenticationException("Invalid username");
            	}
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

}
