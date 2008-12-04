/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.ssfs.servlet;

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
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.LoginInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.ssfs.AlreadyExistsException;
import org.kablink.teaming.ssfs.CrossContextConstants;
import org.kablink.teaming.ssfs.LockException;
import org.kablink.teaming.ssfs.NoAccessException;
import org.kablink.teaming.ssfs.NoSuchObjectException;
import org.kablink.teaming.ssfs.TypeMismatchException;
import org.kablink.teaming.ssfs.server.SiteScapeFileSystem;
import org.kablink.teaming.ssfs.server.SiteScapeFileSystemException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;


public class DispatchServer extends GenericServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(DispatchServer.class);

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		Integer operation = (Integer) req.getAttribute(CrossContextConstants.OPERATION);
		
		if(operation.equals(CrossContextConstants.OPERATION_AUTHENTICATE)) { 
			// Authentication request: This is treated differently than regular SSFS requests.
			String serverName = (String) req.getAttribute(CrossContextConstants.SERVER_NAME);
			String userName = (String) req.getAttribute(CrossContextConstants.USER_NAME);
			String password = (String) req.getAttribute(CrossContextConstants.PASSWORD);
			Boolean ignorePassword = (Boolean) req.getAttribute(CrossContextConstants.IGNORE_PASSWORD);
			if(ignorePassword == null) {
				ignorePassword = SPropsUtil.getBoolean("ssfs.ignore.password.enabled", false);
			}

			String zoneName = getZoneModule().getZoneNameByVirtualHost(serverName);
			
			// Authenticate the user against SSF user database.
			try {
				User user = AuthenticationManagerUtil.authenticate
					(zoneName, userName, password, false, ignorePassword, LoginInfo.AUTHENTICATOR_WEBDAV);
				req.setAttribute(CrossContextConstants.USER_ID, user.getId());
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
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				logger.warn(e);
				return;
			}		
		}
		else { 
			// Must be a SSFS request: This operation requires normal context
			// set up. This branch takes care of request-context setup as well
			// as logging of error. 
			
			String serverName = (String) req.getAttribute(CrossContextConstants.SERVER_NAME);
			String userName = (String) req.getAttribute(CrossContextConstants.USER_NAME);

			// Retrieve zone info corresponding to the specified server name.
			Long zoneId = getZoneModule().getZoneIdByVirtualHost(serverName);
			
			// Setup request context. Do not resolve it yet.
			RequestContextUtil.setThreadContext(zoneId, userName);
			
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
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			catch(AlreadyExistsException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_ALREADY_EXISTS);				
				logger.warn(e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
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
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			catch(LockException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_LOCK);
				logger.warn(e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			catch(TypeMismatchException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_TYPE_MISMATCH);				
				logger.warn(e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			catch(IOException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_GENERAL);				
				logger.error(e.getLocalizedMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			catch(ServletException e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_GENERAL);				
				logger.error(e.getLocalizedMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;			
			}
			catch(SiteScapeFileSystemException e) {
				if(e.isWarning()) {
					req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.WARNING_GENERAL);		
					logger.warn(e);
				}
				else {
					req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_GENERAL);		
					logger.error(e.getLocalizedMessage(), e);
				}
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			catch(Exception e) {
				req.setAttribute(CrossContextConstants.ERROR, CrossContextConstants.ERROR_GENERAL);				
				logger.error(e.getLocalizedMessage(), e);
				req.setAttribute(CrossContextConstants.ERROR_MESSAGE, e.getLocalizedMessage());
				return;
			}
			finally {
				RequestContextHolder.clear();
			}
		}

	}

	private void doSsfsRequest(Integer operation, ServletRequest req, ServletResponse res) 
	throws ServletException, IOException, NoAccessException, AlreadyExistsException,
	NoSuchObjectException, LockException, TypeMismatchException {
		Map uri = (Map) req.getAttribute(CrossContextConstants.URI);
		Map sourceUri = (Map) req.getAttribute(CrossContextConstants.SOURCE_URI);
		Map targetUri = (Map) req.getAttribute(CrossContextConstants.TARGET_URI);

		SiteScapeFileSystem ssfs = getSiteScapeFileSystem();
		
		if(operation.equals(CrossContextConstants.OPERATION_CREATE_RESOURCE)) {
			ssfs.createResource(uri);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_CREATE_FOLDER)) {
			ssfs.createDirectory(uri);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_SET_RESOURCE)) {
			InputStream content = (InputStream) req.getAttribute(CrossContextConstants.INPUT_STREAM);
			ssfs.setResource(uri, content);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_GET_RESOURCE)) {
			InputStream resource = ssfs.getResource(uri);
			req.setAttribute(CrossContextConstants.RETURN, resource);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_REMOVE_OBJECT)) {
			ssfs.removeObject(uri);
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
			String lockOwnerInfo = (String) req.getAttribute(CrossContextConstants.LOCK_PROPERTIES_OWNER_INFO);
			ssfs.lockResource(uri, lockId, lockSubject, lockExpirationDate, lockOwnerInfo);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_UNLOCK_RESOURCE)) {
			String lockId = (String) req.getAttribute(CrossContextConstants.LOCK_PROPERTIES_ID);
			ssfs.unlockResource(uri, lockId);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_COPY_OBJECT)) {
			boolean overwrite = ((Boolean) req.getAttribute(CrossContextConstants.OVERWRITE)).booleanValue();
			boolean recursive = ((Boolean) req.getAttribute(CrossContextConstants.RECURSIVE)).booleanValue();
			ssfs.copyObject(sourceUri, targetUri, overwrite, recursive);
		}
		else if(operation.equals(CrossContextConstants.OPERATION_MOVE_OBJECT)) {
			boolean overwrite = ((Boolean) req.getAttribute(CrossContextConstants.OVERWRITE)).booleanValue();
			ssfs.moveObject(sourceUri, targetUri, overwrite);
		}
		else {
			throw new ServletException("Invalid operation " + operation);
		}		
	}
	
	private SiteScapeFileSystem getSiteScapeFileSystem() {
		return (SiteScapeFileSystem) SpringContextUtil.getBean("ssfs");
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
}
