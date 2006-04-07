package com.sitescape.ef.web.crosscontext.portal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.web.WebKeys;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static Map sessionMap = Collections.synchronizedMap(new HashMap());
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = req.getParameter(CrossContextConstants.OPERATION);
		
		if(operation.equals(CrossContextConstants.OPERATION_AUTHENTICATE)) {
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			String password = req.getParameter(CrossContextConstants.PASSWORD);

			// Authenticate the user against SSF user database.
			try {
				boolean passwordAutoSynch = 
					SPropsUtil.getBoolean("portal.password.auto.synchronize", false);
				
				getAuthenticationManager().authenticate(zoneName, userName, password, passwordAutoSynch);
			}
			catch(UserDoesNotExistException e) {
				logger.warn(e);
				// Throw ServletException with cause's error message rather
				// then the cause itself. This is because the class loader
			    // of the calling app does not have access to the class of 
				// the cause exception. 
				throw new ServletException(e.getMessage());
			}
			catch(PasswordDoesNotMatchException e) {
				logger.warn(e);
				throw new ServletException(e.getMessage());
			}	
			catch(Exception e) {
				logger.warn(e);
				throw new ServletException(e.getMessage());
			}
		}
		else if(operation.equals(CrossContextConstants.OPERATION_CREATE_SESSION)) {
			HttpServletRequest request = (HttpServletRequest) req;
			
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			String portalSessionId = req.getParameter(CrossContextConstants.PORTAL_SESSION_ID);
			
			HttpSession ses = request.getSession();
			
			ses.setAttribute(WebKeys.ZONE_NAME, zoneName);
			ses.setAttribute(WebKeys.USER_NAME, userName);
		
			sessionMap.put(portalSessionId, ses);	
			logger.debug("The session with id = [" + ses.getId() + "] is associated with portal key = [" + portalSessionId + "]");
		}
		else if(operation.equals(CrossContextConstants.OPERATION_DESTROY_SESSION)) {
			HttpServletRequest request = (HttpServletRequest) req;
			
			String portalSessionId = req.getParameter(CrossContextConstants.PORTAL_SESSION_ID);
			
			HttpSession ses = (HttpSession) sessionMap.remove(portalSessionId);
			
			if(ses != null) {
				ses.invalidate();
				logger.debug("The session with id = [" + ses.getId() + "] and portal key = [" + portalSessionId + "] is invalidated.");
			}
			else {
				String errorMessage = "Cannot find the session to destroy with portal key = [" + portalSessionId + "]";
				// Log the error message here. Once the control returns to the other
				// side of the context (i.e., back to the portal), then we don't
				// have access to our own log facility. 
				logger.error(errorMessage);
				throw new ServletException(errorMessage);
			}
		}
		else {
			logger.warn("Unrecognized operation [" + operation + "]");
		}
	}
	
	private AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}
}
