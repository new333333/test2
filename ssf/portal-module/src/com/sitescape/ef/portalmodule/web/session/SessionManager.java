package com.sitescape.ef.portalmodule.web.session;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.ascore.cc.SiteScapeCCUtil;
import com.sitescape.ef.portalmodule.CrossContextConstants;
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
	
	public static void createSession(HttpServletRequest request, 
			String portalSessionId, String zoneName, String userName) 
		throws ServletException, IOException {
		//System.out.println("### SessionManager [createSession]: ");
		//System.out.println("\tportal session id: " + portalSessionId);
		//System.out.println("\tzone name: " + zoneName);
		//System.out.println("\tuser name: " + userName);
		
		RequestDispatcher rd = SiteScapeCCUtil.getCCDispatcher();

		DynamicServletRequest req = new DynamicServletRequest(request);

		req.setParameter(CrossContextConstants.OPERATION, CrossContextConstants.OPERATION_CREATE_SESSION);
		req.setParameter(CrossContextConstants.ZONE_NAME, zoneName);
		req.setParameter(CrossContextConstants.USER_NAME, userName);
		
		NullServletResponse res = new NullServletResponse();
		
		rd.include(req, res);
	}
}
