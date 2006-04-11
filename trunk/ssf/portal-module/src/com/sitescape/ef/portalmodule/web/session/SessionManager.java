package com.sitescape.ef.portalmodule.web.session;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.portalmodule.CrossContextConstants;
import com.sitescape.ef.portalmodule.web.crosscontext.DispatchClient;
import com.sitescape.ef.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.ef.web.util.NullServletResponse;
import com.sitescape.util.servlet.DynamicServletRequest;

/**
 * This class serves as a bridge between the portal and the sitescape
 * portlet application and is deployed directly into the portal rather
 * than packaged with the sitescape app.
 * <p>
 * The primary utility of this class is to provide mechanism for managing
 * SSF sessions for users in such a manner that the lifecycle of SSF
 * sessions become entirely subordinate to that of portal sessions.  
 * (i.e., whan a portal session is created, corresponding SSF session is
 * created; when the portal session times out or gets invalidated, the
 * corresponding SSF session gets invalidated as well, etc.)
 *  
 * @author jong
 *
 */
public class SessionManager {
	
	// Map of portal session id to its context path. 
	private static Map sessionMap = Collections.synchronizedMap(new HashMap());
	
	public static void createSession(HttpServletRequest request, 
			String portalSessionId, String zoneName, String userName) 
		throws ServletException, IOException {
		//System.out.println("### SessionManager [createSession]: ");
		//System.out.println("\tportal session id: " + portalSessionId);
		//System.out.println("\tzone name: " + zoneName);
		//System.out.println("\tuser name: " + userName);
		
		DynamicServletRequest req = new DynamicServletRequest(request);
		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_CREATE_SESSION);
		req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		req.setParameter(CrossContextConstants.USER_NAME, userName);
		req.setParameter(CrossContextConstants.PORTAL_SESSION_ID, portalSessionId);
		NullServletResponse res = new NullServletResponse();
		
		DispatchClient.doDispatch(req, res);
		
		sessionMap.put(portalSessionId, request.getContextPath());
	}
	
	public static void destroySession(String portalSessionId) throws ServletException, IOException {
		//System.out.println("### SessionManager [destroySession]: ");
		//System.out.println("\tportal session id: " + portalSessionId);
		
		if(!sessionMap.containsKey(portalSessionId)) {
			//System.out.println("\tWarning: the session was not even created!");
			return;
		}
		
		AttributesAndParamsOnlyServletRequest req = new AttributesAndParamsOnlyServletRequest((String) sessionMap.get(portalSessionId));
		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_DESTROY_SESSION);
		req.setParameter(CrossContextConstants.PORTAL_SESSION_ID, portalSessionId);
		NullServletResponse res = new NullServletResponse();
		
		DispatchClient.doDispatch(req, res);
		
		sessionMap.remove(portalSessionId);
	}

}
