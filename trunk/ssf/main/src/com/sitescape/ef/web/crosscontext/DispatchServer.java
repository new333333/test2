package com.sitescape.ef.web.crosscontext;

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

import com.sitescape.ef.web.WebKeys;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static Map sessionMap = Collections.synchronizedMap(new HashMap());
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = req.getParameter(CrossContextConstants.OPERATION);
		
		if(operation.equals("createSession")) {
			HttpServletRequest request = (HttpServletRequest) req;
			
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			String portalSessionId = req.getParameter(CrossContextConstants.PORTAL_SESSION_ID);
			
			// Because this servlet is executed only through cross-context dispatch
			// from portal, "request.getSession(boolean)" will return a session 
			// regardless of the parameter value passed into it. In other words,
			// even when we pass 'false' to getSession and it is the first time
			// invoking it, it will still create a SSF session with the same id
			// as the id of the corresponding portal session and returns it
			// (rather than returning null). 
			
			HttpSession ses = request.getSession(false);
			
			if(ses != null) {
				logger.debug("A SSF session created with id = [" + ses.getId() + "]");
			}
			else {
				// This means that the corresponding portal does not have
				// a session created for the user, yet our integration code
				// attempts to create a SSF session - This should never occur. 
				logger.error("Error in session management");
				throw new ServletException("Error in session management");
			}
			
			ses.setAttribute(WebKeys.ZONE_NAME, zoneName);
			ses.setAttribute(WebKeys.USER_NAME, userName);
		
			sessionMap.put(portalSessionId, ses);	
			logger.debug("The session is associated with portal key = [" + portalSessionId + "]");
		}
		else if(operation.equals("destroySession")) {
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
	}
}
