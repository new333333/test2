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
import com.sitescape.ef.ssfs.LockException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystemException;
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
			catch(Exception e) {
				// Instead of throwing ServletException to indicate an error, we pass
				// an error status code to the caller by storing it in the servlet
				// request object. This error handling mechanism may feel less intuitive 
				// than using an exception. However, throwing a ServletException causes
				// servlet container to dump the error in a log file along with its
				// full stack dump (this depends on the container in use) even before
				// the control actually returns to the caller. This is because, for
				// cross-context dispatch operation, servlet container is engaged in the
				// middle. To avoid creating this huge log entry, we will pass error
				// status using error code (like in C) across cross-context boundary,
				// rather than utilizing more typical exception mechanism. 
				
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_AUTHENTICATION_FAILURE);	
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				logger.warn(e);
				return;
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
				// By the same reason described above, we pass error information
				// by returning error code and message in the request object
				// rather than throwing ServletException. This is to avoid 
				// unwanted additional entries and stack dump in the log file.
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
			}
			catch(AlreadyExistsException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_ALREADY_EXISTS);				
				logger.warn(e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
			}
			catch(NoSuchObjectException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_NO_SUCH_OBJECT);
				// Unfortunately, the way WCK handles "add" request for a new resource
				// isn't ideal: It first invokes setResource to see if it fails or not.
				// And if it fails, then it invokes createResource. For that reason,
				// there is no good way to distinguish this use case from real 
				// failure situation, and consequently we end up logging the error
				// no matter what the context is. This can alert our users fasely. 
				// To alleviate this undesirable situation, we downgrade the logging
				// level from warning to debug. Of course, this is not a perfect
				// solution, because it adds the potential risk of not logging a
				// true error (nothing's perfect).
				logger.debug(e); // debug level message
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
			}
			catch(LockException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_LOCK);
				logger.warn(e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
			}
			catch(IOException e) {
				logger.error(e.getMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
			}
			catch(ServletException e) {
				logger.error(e.getMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;			
			}
			catch(SiteScapeFileSystemException e) {
				logger.error(e.getMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
			}
			catch(Exception e) {
				logger.error(e.getMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getMessage());
				return;
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
		else if(operation.equals(CrossContextConstants.OPERATION_GET_PROPERTIES)) {
			Map properties = ssfs.getProperties(uri);
			req.setAttribute(CrossContextConstants.RETURN, properties);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_CREATE_SET_RESOURCE)) {
			InputStream content = (InputStream) req.getAttribute(CrossContextConstants.INPUT_STREAM);
			ssfs.createAndSetResource(uri, content);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_LOCK_RESOURCE)) {
			String lockId = (String) req.getAttribute(CrossContextConstants.LOCK_PROPERTIES_ID);
			String lockSubject = (String) req.getAttribute(CrossContextConstants.LOCK_PROPERTIES_SUBJECT);
			Date lockExpirationDate = (Date) req.getAttribute(CrossContextConstants.LOCK_PROPERTIES_EXPIRATION_DATE);
			ssfs.lockResource(uri, lockId, lockSubject, lockExpirationDate);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_UNLOCK_RESOURCE)) {
			String lockId = (String) req.getAttribute(CrossContextConstants.LOCK_PROPERTIES_ID);
			ssfs.unlockResource(uri, lockId);
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
