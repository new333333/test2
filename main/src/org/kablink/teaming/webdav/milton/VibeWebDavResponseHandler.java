/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.webdav.milton;

import org.kablink.teaming.util.SPropsUtil;

import com.bradmcevoy.http.AuthenticationService;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.http.ResponseStatus;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;

/**
 * @author jong
 *
 */
public class VibeWebDavResponseHandler extends DefaultWebDavResponseHandler {
	
    public VibeWebDavResponseHandler( AuthenticationService authenticationService ) {
    	super(authenticationService);
    }
    
	@Override
    public void respondUnauthorised( Resource resource, Response response, Request request ) {
		if(org.kablink.teaming.context.request.RequestContextHolder.getRequestContext() != null) {
			// A successfully logged in user has been denied access to a particular resource
			// or an operation on that resource. In this case, there's no point in asking the
			// user to authenticate again. As a matter of fact, when/if this happens, it 
			// make Windows Explorer truly mad, and it will go wild to the extent that the
			// user has no choice but disconnect the drive and map another one. Yuk...
			int statusCode = SPropsUtil.getInt("wd.respond.unauthorised.status.code", ResponseStatus.SC_FORBIDDEN);
			// Bug in the library - The fromCode method should have been a static method...
			// I'm working around this bug by calling it on a random instance.
			response.setStatus(Response.Status.SC_UNAUTHORIZED.fromCode(statusCode));
		}
		else {
			 wrapped.respondUnauthorised( resource, response, request );
		}
	}
}
