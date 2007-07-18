/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.ssfs.wck;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.ssfs.AlreadyExistsException;
import com.sitescape.team.ssfs.CrossContextConstants;
import com.sitescape.team.ssfs.LockException;
import com.sitescape.team.ssfs.NoAccessException;
import com.sitescape.team.ssfs.NoSuchObjectException;
import com.sitescape.team.ssfs.TypeMismatchException;
import com.sitescape.team.ssfs.web.crosscontext.DispatchClient;
import com.sitescape.team.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.team.web.util.NullServletResponse;

public class CCExecutionTemplate {

	public static Object execute(String zoneName, String userName, Map uri, 
			Integer operationName, CCClientCallback action) 
	throws AlreadyExistsException, CCClientException, NoAccessException, 
	NoSuchObjectException, LockException, TypeMismatchException {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());

		req.setAttribute(CrossContextConstants.ZONE_NAME, zoneName);
		req.setAttribute(CrossContextConstants.USER_NAME, userName);
		req.setAttribute(CrossContextConstants.URI, uri);
		req.setAttribute(CrossContextConstants.OPERATION, operationName);

		action.additionalInput(req);
		
		NullServletResponse res = new NullServletResponse();
		
		try {
			DispatchClient.doDispatch(req, res);
			
			checkError(req);
			
			// If still here, there was no error. 		
			Object returnObj = req.getAttribute(CrossContextConstants.RETURN);
			
			return returnObj; // This may be null since not all operations return something.
		}
		catch(ServletException e) {
			throw new CCClientException(e.getLocalizedMessage());
		} 
		catch (IOException e) {
			throw new CCClientException(e.getLocalizedMessage());
		}		
	}
	
	public static Object execute(String zoneName, String userName, Map sourceUri,
			Map targetUri, Integer operationName, CCClientCallback action) 
	throws AlreadyExistsException, CCClientException, NoAccessException, 
	NoSuchObjectException, LockException, TypeMismatchException {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());

		req.setAttribute(CrossContextConstants.ZONE_NAME, zoneName);
		req.setAttribute(CrossContextConstants.USER_NAME, userName);
		req.setAttribute(CrossContextConstants.SOURCE_URI, sourceUri);
		req.setAttribute(CrossContextConstants.TARGET_URI, targetUri);
		req.setAttribute(CrossContextConstants.OPERATION, operationName);

		action.additionalInput(req);
		
		NullServletResponse res = new NullServletResponse();
		
		try {
			DispatchClient.doDispatch(req, res);
			
			checkError(req);
			
			// If still here, there was no error. 		
			Object returnObj = req.getAttribute(CrossContextConstants.RETURN);
			
			return returnObj; // This may be null since not all operations return something.
		}
		catch(ServletException e) {
			throw new CCClientException(e.getLocalizedMessage());
		} 
		catch (IOException e) {
			throw new CCClientException(e.getLocalizedMessage());
		}		
	}
	
	private static void checkError(HttpServletRequest req)
		throws NoAccessException, NoSuchObjectException, 
		AlreadyExistsException, LockException, CCClientException,
		TypeMismatchException {
		String statusCode = (String) req.getAttribute(CrossContextConstants.ERROR);
		String message = (String) req.getAttribute(CrossContextConstants.ERROR_MESSAGE);
		if(statusCode != null) {
			if(statusCode.equals(CrossContextConstants.ERROR_NO_SUCH_OBJECT))
				throw new NoSuchObjectException(message);
			else if(statusCode.equals(CrossContextConstants.ERROR_NO_ACCESS))
				throw new NoAccessException(message);
			else if(statusCode.equals(CrossContextConstants.ERROR_ALREADY_EXISTS))
				throw new AlreadyExistsException(message);
			else if(statusCode.equals(CrossContextConstants.ERROR_LOCK))
				throw new LockException(message);
			else if(statusCode.equals(CrossContextConstants.ERROR_TYPE_MISMATCH))
				throw new TypeMismatchException(message);
			else if(statusCode.equals(CrossContextConstants.ERROR_GENERAL))
				throw new CCClientException(message);			
			// Unrecognized status code - simply return normally
		}
		// No error code - normal completion 
	}
}
