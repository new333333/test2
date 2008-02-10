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
package com.sitescape.team.module.binder.remoting.ws;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sitescape.team.util.SpringContextUtil;

public class BinderServiceEndpoint implements ServiceLifecycle, BinderService {

	// Implementation note: NEVER extend org.springframework.remoting.jaxrpc.ServletEndpointSupport
	// to get a handle on the application context from it. Our system design requires it to be
	// possible to invoke this service endpoint across app boundaries, that is, from different 
	// servlet context. Therefore we must obtain the application context through our own
	// SpringContextUtil class. 
	private BinderService binderService;
	
	protected BinderService getBinderService() {
		return binderService;
	}
	
	public String getTeamMembersAsXML(long binderId) {
		return getBinderService().getTeamMembersAsXML(binderId);
	}
	
	public String getTeamsAsXML() {
		return getBinderService().getTeamsAsXML();
	}
	
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		return getBinderService().getWorkspaceTreeAsXML(binderId, levels, page);
	}
	
	public String search(String query, int offset, int maxResults) {
		return getBinderService().search(query, offset, maxResults);
	}

	public void init(Object context) throws ServiceException {
		this.binderService = (BinderService) SpringContextUtil.getBean("binderService");
	}
	
	public void destroy() {
	}

}
