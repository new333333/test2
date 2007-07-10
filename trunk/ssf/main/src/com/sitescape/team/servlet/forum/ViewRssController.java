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
package com.sitescape.team.servlet.forum;

import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;

import org.springframework.web.bind.RequestUtils;

public class ViewRssController extends SAbstractController {
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Binder binder = getBinderModule().getBinder(binderId);
		User user = (User)request.getUserPrincipal();
		
		if(user == null) {
			// The request object has no information about authenticated user.
			// Note: It means that this is not a request made by the portal
			// through cross-context dispatch targeted to a SSF portlet. 
			HttpSession ses = request.getSession(false);

			if(ses != null) {
				user = (User) ses.getAttribute(WebKeys.USER_PRINCIPAL);
				
				if (user == null) {
					// No principal object is cached in the session.
					// Note: This occurs when a SSF web component (either a servlet
					// or an adapted portlet) is accessed BEFORE at least one SSF
					// portlet is invoked  by the portal through regular cross-context
					// dispatch. 
					user = RequestContextHolder.getRequestContext().getUser();
				}
			}
			else {
				throw new ServletException("No session in place - Illegal request sequence.");
			}
		}
		

		//response.getWriter(getRssModule().filterRss(request, response, binder,user));
			
		/*response.setContentType(mimeTypes.getContentType(shortFileName));*/
		response.resetBuffer();
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		
		OutputStream out = response.getOutputStream();
		byte[] buffer = getRssModule().filterRss(request, response, binder,user).getBytes();
		out.write(buffer, 0, buffer.length);

		out.flush();

		response.getOutputStream().flush();

		return null;
	}
}
