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
                if ("admin".equals(id)) {
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
