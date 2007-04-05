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
package com.sitescape.team.samples.remoting.client.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

import com.sitescape.util.PasswordEncryptor;

/**
 * This class implements standard <code>CallbackHandler</code> interface
 * to allow security system (WS client-side runtime in this case) to interact 
 * with the application to retrieve specific authentication data. 
 * The class is implemented using Apache WSS4J library.
 * 
 * This implementation passes Aspen-encrypted password to WS-Security framework.
 * 
 * @author jong
 *
 */
public class PWCallbackDigest implements CallbackHandler {

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		//System.out.println("*** Client-side PWCallbackDigest is called");

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                String id = pc.getIdentifer();
                //System.out.println("Identifier [" + id + "]");
                if ("liferay.com.1".equals(id)) {
                	String clearPassword = "test";
                	// Set the password to a digested value of "test". 
                	// This digest has nothing to do with the internal digest performed 
                	// later by WS-Security framework. With Aspen, all passwords are 
                	// stored in encrypted form in the database using secure one-way
                	// hash function, which can not be decrypted back into the original
                	// password in plain text. Consequently, WS client must provide a 
                	// password in exactly the same encrypted form, which can be 
                	// accomplished through the use of the available password encryption 
                	// class. For non-Java based WS clients, the exact same encryption 
                	// steps will need to be translated and applied. 
                	String encryptedPassword = PasswordEncryptor.encrypt("test");
                	//System.out.println("Client: Aspen-encrypted password is [" + encryptedPassword + "]");
                	pc.setPassword(encryptedPassword);
                }
                else {
                	// Leave the password as blank
                	//System.out.println("Password is not set");
                }
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}

}
