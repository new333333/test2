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
package org.kablink.teaming.remoting.rest.jersey.filter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SessionUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.WindowsUtil;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class ContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {

	// It is ok to use instance-level logger, since this class is instantiated only once (i.e., singleton).
	private Log logger = LogFactory.getLog(getClass());
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		// Check authentication
		checkAuthentication(request);
		
		// Trace request
		traceRequest(request);
		
		// Set up request context
		setupRequestContext(request);
		
		// Resolve request context
		resolveRequestContext(request);
		
		return request;
	}

	@Override
	public ContainerResponse filter(ContainerRequest request,
			ContainerResponse response) {
		// Clear request context
		clearRequestContext(request);
		
		// Trace response
		traceResponse(response);
		
		return response;
	}

	private void checkAuthentication(ContainerRequest request) {
		if(request.getUserPrincipal() == null)
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
	}
	
	private void setupRequestContext(ContainerRequest request) {
		RequestContextHolder.clear();
		
        Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());

        String userName = request.getUserPrincipal().getName();
		
		RequestContextUtil.setThreadContext(zoneId, WindowsUtil.getSamaccountname(userName));
	}

	private void resolveRequestContext(ContainerRequest request) {
		RequestContextHolder.getRequestContext().resolve();
	}
	
	private void clearRequestContext(ContainerRequest request) {
		RequestContextHolder.clear();
	}
	
	private void traceRequest(ContainerRequest request) {
		if(logger.isDebugEnabled())
			logger.debug(request.toString());
	}
	
	private void traceResponse(ContainerResponse response) {
		if(logger.isDebugEnabled())
			logger.debug(response.toString());
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
}
