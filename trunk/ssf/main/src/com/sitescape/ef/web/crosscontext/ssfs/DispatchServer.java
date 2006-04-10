package com.sitescape.ef.web.crosscontext.ssfs;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.web.crosscontext.ssfs.CrossContextConstants;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		String operation = (String) req.getAttribute(CrossContextConstants.OPERATION);
		
		if(operation.equals(CrossContextConstants.OPERATION_AUTHENTICATE)) {
			String zoneName = (String) req.getAttribute(CrossContextConstants.ARG_ZONE_NAME);
			String userName = (String) req.getAttribute(CrossContextConstants.ARG_USER_NAME);
			String password = (String) req.getAttribute(CrossContextConstants.ARG_PASSWORD);

			// Authenticate the user against SSF user database.
			try {
				getAuthenticationManager().authenticate(zoneName, userName, password, false);
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
		else {
			logger.warn("Unrecognized operation [" + operation + "]");
		}
	}

	private AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}
}
