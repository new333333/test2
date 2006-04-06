package com.sitescape.ef.remoting.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.util.SpringContextUtil;

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

	private static final String DELIM = ";";
	
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];
                
                // set the password given a username
                String zoneName = parseZoneName(pc.getIdentifer());
                String userName = parseUsername(pc.getIdentifer());
                
        		try {
        			User user = getProfileDao().findUserByName(userName, zoneName);
        			
        			// If you're still here, the user exists. Return the password.
        			pc.setPassword(user.getPassword());
        			
        			// While we are here, let's set up our thread context using
        			// the user information, since this is our only change to 
        			// get at that piece of info when invoked by WS runtime
        			// (as opposed to web framework).
        			RequestContextUtil.setThreadContext(zoneName, userName);
        		}
            	catch(NoUserByTheNameException e) {
            		// Do not throw an exception. Just not setting the password
            		// on pc is enough an indication to the framework. 
            	}
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
	}

	private String parseZoneName(String wssId) {
		int index = wssId.indexOf(DELIM);
		return wssId.substring(0, index);
	}
	
	private String parseUsername(String wssId) {
		int index = wssId.indexOf(DELIM);
		return wssId.substring(index+2);
	}
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

}
