package com.sitescape.team.remoting.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.User;
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
        			
        			// If you're still here, the user exists. Return the password.
        			pc.setPassword(user.getPassword());
        			
        			// While we are here, let's set up our thread context using
        			// the user information, since this is our only change to 
        			// get at that piece of info when invoked by WS runtime
        			// (as opposed to web framework).
        			RequestContextUtil.setThreadContext(user);
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
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

}
