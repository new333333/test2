package com.sitescape.ef.ssfs.web.crosscontext.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.ef.security.authentication.AuthenticationManager;
import com.sitescape.ef.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.ef.security.authentication.UserDoesNotExistException;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;
import com.sitescape.ef.util.SpringContextUtil;

public class DispatchServer extends GenericServlet {

	private static final Log logger = LogFactory.getLog(DispatchServer.class);

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		Integer operation = (Integer) req.getAttribute(CrossContextConstants.OPERATION);
		
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
				if(e instanceof IOException)
					throw (IOException) e;
				else
					throw new ServletException(e.getMessage());
			}			
		}
		else { 
			// Must be a SSFS request: This operation requires normal context
			// set up. This branch takes care of request-context setup as well
			// as logging of error. 
			
			String zoneName = (String) req.getAttribute(CrossContextConstants.ZONE_NAME);
			String userName = (String) req.getAttribute(CrossContextConstants.USER_NAME);
			
			// Setup request context.
			RequestContextUtil.setThreadContext(zoneName, userName);
			
			try {
				doSsfsRequest(operation, req, res);
			}
			catch(NoAccessException e) {
				// Although NoAccessException class is available to both SSF 
				// and SSFS web apps, they are loaded by two different loaders
				// as opposed to by a shared common loader, resulting in two
				// different class objects (loaded from the same class file) 
				// in the single JVM. This configuration scenario prevents us
				// from passing an object reference (NoAccessException instance
				// in this case) across multiple web app boundaries. Therefore,
				// we return the equivalent information by storing an attribute 
				// entry in the ServletRequest rather than propagating up the
				// exception object.   
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_NO_ACCESS);
				// Once we return from this method, we have no access to our 
				// logger. Therefore this is our last chance to log the error
				// on the SSF side. 
				logger.warn(e);
				// Again, do not store the exception object as a cause into
				// the ServletException for the same reason explained above.
				// Instead, simply get the message text and return it. 
				throw new ServletException(e.getMessage());
			}
			catch(AlreadyExistsException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_ALREADY_EXISTS);				
				logger.warn(e);
				throw new ServletException(e.getMessage());
			}
			catch(NoSuchObjectException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_NO_SUCH_OBJECT);
				logger.warn(e);
				throw new ServletException(e.getMessage());
			}
			catch(IOException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			catch(ServletException e) {
				logger.error(e.getMessage(), e);
				throw e;				
			}
			catch(Exception e) {
				logger.error(e.getMessage(), e);
				throw new ServletException(e.getMessage());
			}
		}

	}

	private void doSsfsRequest(Integer operation, ServletRequest req, ServletResponse res) 
	throws ServletException, IOException, NoAccessException, AlreadyExistsException,
	NoSuchObjectException {
		Map uri = (Map) req.getAttribute(CrossContextConstants.URI);

		SiteScapeFileSystem ssfs = getSiteScapeFileSystem();
		
		if(operation.equals(CrossContextConstants.OPERATION_OBJECT_EXISTS)) {
			boolean result = ssfs.objectExists(uri);
			req.setAttribute(CrossContextConstants.RETURN, Boolean.valueOf(result));
		}
		else if(operation.equals(CrossContextConstants.OPERATION_CREATE_RESOURCE)) {
			ssfs.createResource(uri);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_SET_RESOURCE)) {
			InputStream content = (InputStream) req.getAttribute(CrossContextConstants.INPUT_STREAM);
			ssfs.setResource(uri, content);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_GET_RESOURCE)) {
			InputStream resource = ssfs.getResource(uri);
			req.setAttribute(CrossContextConstants.RETURN, resource);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_GET_RESOURCE_LENGTH)) {
			long length = ssfs.getResourceLength(uri);
			req.setAttribute(CrossContextConstants.RETURN, new Long(length));
		}
		else if(operation.equals(CrossContextConstants.OPERATION_REMOVE_RESOURCE)) {
			ssfs.removeResource(uri);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_GET_LAST_MODIFIED)) {
			Date date = ssfs.getLastModified(uri);
			req.setAttribute(CrossContextConstants.RETURN, date);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_GET_CREATION_DATE)) {
			Date date = ssfs.getCreationDate(uri);
			req.setAttribute(CrossContextConstants.RETURN, date);			
		}
		else if(operation.equals(CrossContextConstants.OPERATION_GET_CHILDREN_NAMES)) {
			String[] names = ssfs.getChildrenNames(uri);
			req.setAttribute(CrossContextConstants.RETURN, names);
		}
		else {
			throw new ServletException("Invalid operation " + operation);
		}		
	}
	
	private AuthenticationManager getAuthenticationManager() {
		return (AuthenticationManager) SpringContextUtil.getBean("authenticationManager");
	}
	
	private SiteScapeFileSystem getSiteScapeFileSystem() {
		return (SiteScapeFileSystem) SpringContextUtil.getBean("ssfs");
	}
}
