package com.sitescape.ef.ssfs.wck;

import javax.servlet.ServletException;

import org.apache.slide.simple.authentication.SessionAuthenticationManager;

import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.web.crosscontext.DispatchClient;
import com.sitescape.ef.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.ef.web.util.NullServletResponse;

public class AuthenticationManager implements SessionAuthenticationManager {

	public Object getAuthenticationSession(String userInput, String password) throws Exception {
		// Parse the user-specified input string into zone and user names.
		String[] credential = Util.parseExtendedUserNameInput(userInput);
		
		if(credential[1] == null)
			throw new IllegalArgumentException("Enter user name"); // user name unspecified
		
		if(credential[0] == null) { // zone name unspecified
			// See if we can use default zone name. 
			// TODO to be changed
			credential[0] = "liferay.com"; // for now...
			
			// if no default zone name, we should throw. 
			// throw new IllegalArgumentException("Enter user name in the format <zonename>;<username>");
		}
		
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.CONTEXT_PATH);
		req.setAttribute(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		req.setAttribute(CrossContextConstants.ZONE_NAME, credential[0]);
		req.setAttribute(CrossContextConstants.USER_NAME, credential[1]);
		req.setAttribute(CrossContextConstants.PASSWORD, password);
		NullServletResponse res = new NullServletResponse();
		
		try {
			DispatchClient.doDispatch(req, res);
		} 
		catch (ServletException e) {
			Throwable cause = e.getCause();
			if((cause != null) && (cause instanceof Exception))
				throw (Exception) cause;
			else
				throw e;
		} 
		
		return Util.makeExtendedUserName(credential[0], credential[1]); // canonical representation
	}

	public Object getAuthenticationSession(String user) throws Exception {
		return user;
	}

	public void closeAuthenticationSession(Object session) throws Exception {
	}

}
