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
package com.sitescape.team.remoting.ws.service.profile;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sitescape.team.util.SpringContextUtil;

public class ProfileServiceEndpoint implements ServiceLifecycle, ProfileService {

	private ProfileService profileService;
	
	protected ProfileService getProfileService() {
		return profileService;
	}
	
	public long addGroup(String accessToken, long binderId, String definitionId, String inputDataAsXML) {
		return getProfileService().addGroup(accessToken, binderId, definitionId, inputDataAsXML);
	}
	
	public long addUser(String accessToken, long binderId, String definitionId, String inputDataAsXML) {
		return getProfileService().addUser(accessToken, binderId, definitionId, inputDataAsXML);
	}
	
	public void addUserToGroup(String accessToken, long userId, String username, long groupId) {
		getProfileService().addUserToGroup(accessToken, userId, username, groupId);
	}
	
	public void deletePrincipal(String accessToken, long binderId, long principalId) {
		getProfileService().deletePrincipal(accessToken, binderId, principalId);
	}
	
	public String getAllPrincipalsAsXML(String accessToken, int firstRecord, int maxRecords) {
		return getProfileService().getAllPrincipalsAsXML(accessToken, firstRecord, maxRecords);
	}
	
	public String getPrincipalAsXML(String accessToken, long binderId, long principalId) {
		return getProfileService().getPrincipalAsXML(accessToken, binderId, principalId);
	}
	
	public void modifyPrincipal(String accessToken, long binderId, long principalId, String inputDataAsXML) {
		getProfileService().modifyPrincipal(accessToken, binderId, principalId, inputDataAsXML);
	}
	
	public long addUserWorkspace(String accessToken, long userId) {
		return getProfileService().addUserWorkspace(accessToken, userId);
	}
	public void init(Object context) throws ServiceException {
		this.profileService = (ProfileService) SpringContextUtil.getBean("profileService");
	}
	
	public void destroy() {
	}

}