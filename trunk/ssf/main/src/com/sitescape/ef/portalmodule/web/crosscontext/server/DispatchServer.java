package com.sitescape.ef.portalmodule.web.crosscontext.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.kernel.util.SiteScapeClassLoaderUtil;
import com.sitescape.ef.portalmodule.CrossContextConstants;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SZoneConfig;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.web.WebKeys;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);
	
	private static final String PORTAL_CC_DISPATCHER = "portalCCDispatcher";
	
	public void init(ServletConfig config) throws ServletException {
		RequestDispatcher rd = config.getServletContext().getNamedDispatcher(PORTAL_CC_DISPATCHER);
		SiteScapeClassLoaderUtil.setCCDispatcher(rd);
	}
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = req.getParameter(CrossContextConstants.OPERATION);
		
		if(operation.equals(CrossContextConstants.OPERATION_AUTHENTICATE)) {
			String zoneName = req.getParameter(CrossContextConstants.ZONE_NAME);
			if(zoneName == null)
				zoneName = SZoneConfig.getDefaultZoneName();
			String userName = req.getParameter(CrossContextConstants.USER_NAME);
			String password = req.getParameter(CrossContextConstants.PASSWORD);
			Map updates = (Map)req.getAttribute(CrossContextConstants.USER_INFO);

			// Authenticate the user against SSF user database.
			try {
				boolean passwordAutoSynch = 
					SPropsUtil.getBoolean("portal.password.auto.synchronize", false);
				
				getAuthenticationManager().authenticate(zoneName, userName, password, passwordAutoSynch, updates);
			}
			catch(UserDoesNotExistException e) {
				logger.warn(e.getMessage(), e);
				// Throw ServletException with cause's error message rather
				// then the cause itself. This is because the class loader
			    // of the calling app does not have access to the class of 
				// the cause exception. 
				throw new ServletException(e.getMessage());
			}
			catch(PasswordDoesNotMatchException e) {
				logger.warn(e.getMessage(), e);
				throw new ServletException(e.getMessage());
			}	
			catch(Exception e) {
				logger.warn(e.getMessage(), e);
				if(e instanceof IOException)
					throw (IOException) e;
				else
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
		
			logger.debug("The session with id = [" + ses.getId() + "] is associated with portal key = [" + portalSessionId + "]");
		}
		else {
			logger.error("Unrecognized operation [" + operation + "]");
		}
	}
	
	private AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}
}
