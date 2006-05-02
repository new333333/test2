package com.sitescape.ef.ssfs.wck;

import javax.servlet.ServletException;

import org.apache.slide.simple.authentication.SessionAuthenticationManager;

import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.web.crosscontext.DispatchClient;
import com.sitescape.ef.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.ef.web.util.NullServletResponse;

public class AuthenticationManager implements SessionAuthenticationManager {

	public Object getAuthenticationSession(String user, String password) throws Exception {
		String[] id = Util.parseUserIdInput(user);
		
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());
		req.setAttribute(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_AUTHENTICATE);
		req.setAttribute(CrossContextConstants.ZONE_NAME, id[0]);
		req.setAttribute(CrossContextConstants.USER_NAME, id[1]);
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
		
		return Util.makeExtendedUserName(id[0], id[1]); // canonical representation
	}

	public Object getAuthenticationSession(String user) throws Exception {
		// Instead of maintaining mapping from user id string to session object,
		// we will simply re-compute it here. This is because 1) SSFS does not
		// need separate session/state for each login (i.e. multiple logins
		// from different users or even from the same user), and 2) keeping
		// session map is much more problematic for nothing. 
		
		String[] id = Util.parseUserIdInput(user);
		return Util.makeExtendedUserName(id[0], id[1]);
	}

	public void closeAuthenticationSession(Object session) throws Exception {
	}

	
}
