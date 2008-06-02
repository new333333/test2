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
package com.sitescape.team.security.accesstoken.impl;

import com.sitescape.team.security.accesstoken.AccessToken;

public class TokenInfoRequest extends TokenInfo {
	
	private static final long serialVersionUID = 1L;
	
	private Long applicationId; 
	private Long userId; 
	private Long binderId; // may be null
	private int binderAccessConstraints; // meaningful only when binderId is specified
	
	public TokenInfoRequest(Long applicationId, Long userId, Long binderId, 
			AccessToken.BinderAccessConstraints binderAccessConstraints, String seed) {
		this(applicationId, userId);
		this.binderId = binderId;
		this.binderAccessConstraints = binderAccessConstraints.getNumber();
		this.seed = seed;
	}
	
	public TokenInfoRequest(Long applicationId, Long userId) {
		this.applicationId = applicationId;
		this.userId = userId;
		this.binderAccessConstraints = AccessToken.BinderAccessConstraints.NONE.getNumber();
	}
	
	public TokenInfoRequest() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AccessToken.BinderAccessConstraints getBinderAccessConstraints() {
		return AccessToken.BinderAccessConstraints.valueOf(binderAccessConstraints);
	}

	public void setBinderAccessConstraints(AccessToken.BinderAccessConstraints binderAccessConstraints) {
		this.binderAccessConstraints = binderAccessConstraints.getNumber();
	}

}
