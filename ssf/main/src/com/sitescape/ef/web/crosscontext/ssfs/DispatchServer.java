package com.sitescape.ef.web.crosscontext.ssfs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.ssfs.SsfsFacade;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.web.crosscontext.ssfs.CrossContextConstants;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = (String) req.getAttribute(CrossContextConstants.OPERATION);
		
		if(operation.equals(CrossContextConstants.OPERATION_AUTHENTICATE)) { 
			// Authentication request: This is treated as a special case.
			String zoneName = (String) req.getAttribute(CrossContextConstants.ZONE_NAME);
			String userName = (String) req.getAttribute(CrossContextConstants.USER_NAME);
			String password = (String) req.getAttribute(CrossContextConstants.PASSWORD);

			// Authenticate the user against SSF user database.
			try {
				getAuthenticationManager().authenticate(zoneName, userName, password, false);
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
				throw new ServletException(e.getMessage());
			}			
		}
		else { 
			// Must be a SSFS request: This operation requires normal context
			// set up. 
			String zoneName = (String) req.getAttribute(CrossContextConstants.ZONE_NAME);
			String userName = (String) req.getAttribute(CrossContextConstants.USER_NAME);
			Map uri = (Map) req.getAttribute(CrossContextConstants.URI);
			
			
		}
		
		

		/*
		else {
			logger.error("Unrecognized operation [" + operation + "]");
			throw new ServletException("");
		}*/
	}

	private AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}
	
	private SsfsFacade getSsfsFacade() {
		return (SsfsFacade) SpringContextUtil.getBean("ssfsFacade");
	}
}
