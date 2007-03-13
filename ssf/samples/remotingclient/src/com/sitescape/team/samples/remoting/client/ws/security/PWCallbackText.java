package com.sitescape.team.samples.remoting.client.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

/** 
 * This class implements standard <code>CallbackHandler</code> interface
 * to allow security system (WS client-side runtime in this case) to interact 
 * with the application to retrieve specific authentication data. 
 * The class is implemented using Apache WSS4J library.
 * 
 * This implementation passes cleartext password to WS-Security framework.
 * 
 * @author jong
 *
 */
public class PWCallbackText implements CallbackHandler {

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		//System.out.println("*** Client-side PWCallbackText is called");

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                String id = pc.getIdentifer();
                //System.out.println("Identifier [" + id + "]");
                if ("liferay.com.1".equals(id)) {
                	String clearPassword = "test";
                	//System.out.println("Client: Cleartext password is [" + clearPassword + "]");
        			pc.setPassword(clearPassword);
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
