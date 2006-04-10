package com.sitescape.ef.ssfs.wck;

import javax.servlet.ServletException;

import org.apache.slide.simple.authentication.SessionAuthenticationManager;

import com.sitescape.ef.ssfs.web.crosscontext.DispatchClient;
import com.sitescape.ef.web.crosscontext.ssfs.CrossContextConstants;
import com.sitescape.ef.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.ef.web.util.NullServletResponse;

public class AuthenticationManager implements SessionAuthenticationManager {

	public Object getAuthenticationSession(String user, String password) throws Exception {
		// Split user name into two parts - zonename:username
		String[] credential = user.split(Constants.USERNAME_DELIM);
		if(credential.length != 2)
			throw new IllegalArgumentException("Enter user name in the format <zonename>;<username>");
		
		String zoneName = credential[0].trim();
		String userName = credential[1].trim();
		
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Constants.CONTEXT_PATH);
		req.setAttribute(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		req.setAttribute(CrossContextConstants.ZONE_NAME, zoneName);
		req.setAttribute(CrossContextConstants.USER_NAME, userName);
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
		
		return user;
	}

	public Object getAuthenticationSession(String user) throws Exception {
		return user;
	}

	public void closeAuthenticationSession(Object session) throws Exception {
	}

}
