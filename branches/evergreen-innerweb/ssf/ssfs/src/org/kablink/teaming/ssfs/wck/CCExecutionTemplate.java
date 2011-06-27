/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.ssfs.wck;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.ssfs.AlreadyExistsException;
import org.kablink.teaming.ssfs.CrossContextConstants;
import org.kablink.teaming.ssfs.LockException;
import org.kablink.teaming.ssfs.NoAccessException;
import org.kablink.teaming.ssfs.NoSuchObjectException;
import org.kablink.teaming.ssfs.TypeMismatchException;
import org.kablink.teaming.ssfs.web.crosscontext.DispatchClient;
import org.kablink.teaming.web.util.AttributesAndParamsOnlyServletRequest;
import org.kablink.teaming.web.util.NullServletResponse;


public class CCExecutionTemplate {

	public static Object execute(String serverName, String userName, Map uri, 
			Integer operationName, CCClientCallback action) 
	throws AlreadyExistsException, CCClientException, NoAccessException, 
	NoSuchObjectException, LockException, TypeMismatchException {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());

		if(serverName != null)
			req.setAttribute(CrossContextConstants.SERVER_NAME, serverName);
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
	
	public static Object execute(String serverName, String userName, Map sourceUri,
			Map targetUri, Integer operationName, CCClientCallback action) 
	throws AlreadyExistsException, CCClientException, NoAccessException, 
	NoSuchObjectException, LockException, TypeMismatchException {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Util.getSsfContextPath());

		if(serverName != null)
			req.setAttribute(CrossContextConstants.SERVER_NAME, serverName);
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
			else if(statusCode.equals(CrossContextConstants.WARNING_GENERAL))
				throw new CCClientException(message, true);			
			// Unrecognized status code - simply return normally
		}
		// No error code - normal completion 
	}
}
