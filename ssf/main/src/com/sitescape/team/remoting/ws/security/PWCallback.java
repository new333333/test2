/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.util.PasswordEncryptor;

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
        				String encryptedPasword = PasswordEncryptor.encrypt(clearPassword);
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
