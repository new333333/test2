package com.sitescape.ef.samples.remoting.client.ws.security;

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
 * @author jong
 *
 */
public class PWCallback implements CallbackHandler {

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		System.out.println("*** Client-side PWCallback is called");

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];

                // Set the password given a username - Because we need to pass 
                // both zone name and user name as a single user identity, we
                // use our own convention where WS's user identity is made up 
                // of zone name, ":", and user name concatenated in that order.
                if ("liferay.com:liferay.com.1".equals(pc.getIdentifer())) {
                	pc.setPassword(("test"));
                }
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}

}
