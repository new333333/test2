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
package org.kablink.teaming.servlet.rss;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;


public class ListController extends SAbstractController {

	private static final int THREESECS = 3000;
	private boolean authErr = false;
	private boolean binderExists = true;

	@Override
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Get the list of RSS items and write it to servlet response as XML.
		Long binderId = new Long(ServletRequestUtils.getRequiredStringParameter(request, "bi"));
		Binder binder = null;
		authErr = false;
		binderExists = true;
		boolean rss = true;
		
		// Test if the user is authenticated or not using the flag stored in
		// the request. Don't ever make this decision based on the existence
		// of request context data, since it may be a stale data from previous
		// request that for some reason was cleared properly. 
		if(!WebHelper.isUnauthenticatedRequest(request)) {
			try {
				binder = getBinderModule().getBinder(binderId);
				boolean skipBinder = (null == binder);
				if (!skipBinder) {
					if (binder instanceof Folder) {
						skipBinder = ((Folder) binder).isPreDeleted();
					}
					else if (binder instanceof Workspace) {
						skipBinder = ((Workspace) binder).isPreDeleted();
					}
				}
				if (skipBinder) {
					binder = null;
					binderExists = false;
				}
			} catch (NoBinderByTheIdException nbe) {
				binderExists = false;
			} catch (OperationAccessControlExceptionNoName oace) {
				authErr = true;
			} catch (AccessControlException ace) {
				authErr = true;
			}
		} else {
			// the authentication key is incorrect, make them wait
			// a bit (to stop immediate retries from hackers), and 
			// and then let them know that the request failed.
			Thread.sleep(THREESECS);
			authErr = true;
			
		}
		if (request.getRequestURL().toString().contains("ssf/atom")) {
			rss = false;
		}
		response.resetBuffer();
		
		if (rss) {
			response.setContentType("application/rss+xml; charset=" + XmlFileUtil.FILE_ENCODING);
		} else {
			response.setContentType("application/atom+xml; charset=" + XmlFileUtil.FILE_ENCODING);
		}
		
		response.setHeader("Cache-Control", "private");
		//use writer to enfoce character set
		if (authErr) {
			response.getWriter().write(getRssModule().AuthError(request, response));
		} else if (!binderExists) {
			response.getWriter().write(getRssModule().BinderExistenceError(request, response));
		} else {
			if (rss) {
				getRssModule().filterRss(request, response, binder); 
			} else {
				getRssModule().filterAtom(request, response, binder);
			}
		}
		response.flushBuffer();
		return null;
	}

}
