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
package org.kablink.teaming.client.ws;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;
import org.kablink.util.PasswordHashEncryptor;


/**
 * This class implements standard <code>CallbackHandler</code> interface
 * to allow security system (WS client-side runtime in this case) to interact 
 * with the application to retrieve specific authentication data. 
 * The class is implemented using Apache WSS4J library.
 * 
 * This implementation passes Teaming-encrypted password to WS-Security framework.
 * 
 * @author jong
 *
 */
public class PasswordCallbackDigest implements CallbackHandler {

	private String password; // clear text password
	
	public PasswordCallbackDigest(String password) {
		this.password = password;
	}
	
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                String id = pc.getIdentifer(); // this is username, which we don't use here
                if (password != null) {
                	// Encrypt the clear-text password using the supplied class.
                	// This is the same class used by the product to encrypt user password
                	// before storing it in the user database. 
                	// This encryption has nothing to do with the internal digest performed 
                	// later by the WS-Security framework. With Teaming, all passwords are 
                	// stored in encrypted form in the database using secure one-way
                	// hash function, which can not be decrypted back into the original
                	// password in plain text. Consequently, WS client must provide a 
                	// password in exactly the same encrypted form, so that the server
                	// can compare the credential against the user record in the system.
                	// For non-Java based WS client that wishes to use WS-Security 
                	// authentication with digest password, the exact same algorighm in
                	// the supplied class must be translated in the platform's language.
                	// And at runtime the same encryption must be applied to the password
                	// before handing it over to the WS-Security for digest computation.
                	// Note: You need to know which encryption algorithm the Teaming server
                	// uses to encrypt user passwords, and pass that information to the
                	// encrypt method call below.
                	String encryptedPassword = PasswordHashEncryptor.encrypt("MD5", password);
                	//System.out.println(encryptedPassword);
                	pc.setPassword(encryptedPassword);
                }
                else {
                	throw new RuntimeException("Password must be specified");
                }
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}

}
