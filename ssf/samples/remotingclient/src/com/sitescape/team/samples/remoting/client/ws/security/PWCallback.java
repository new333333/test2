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
 * @author jong
 *
 */
public class PWCallback implements CallbackHandler {

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

		System.out.println("*** Client-side PWCallback is called");

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                // Set the password given a username.
                String id = pc.getIdentifer();
                System.out.println("Identifier [" + id + "]");
                if ("liferay.com.1".equals(id)) {
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
                	System.out.println("Setting password to [test]");
                	String encryptedPassword = PasswordEncryptor.encrypt("test");
                	pc.setPassword(encryptedPassword);
                }
                else {
                	System.out.println("Password is not set");
                }
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}

}
