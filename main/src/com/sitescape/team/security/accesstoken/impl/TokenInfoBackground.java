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

import java.io.Serializable;

import com.sitescape.team.domain.ZonedObject;

public class TokenInfoBackground extends ZonedObject implements TokenInfo, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long applicationId; 
	private Long userId; 
	private Long binderId; // 0 value in DB represents null in memory
	private String seed;
	
	public TokenInfoBackground(Long applicationId, Long userId, Long binderId, String seed) {
		this(applicationId, userId, binderId);
		this.seed = seed;
	}
	
	public TokenInfoBackground(Long applicationId, Long userId, Long binderId) {
		this.applicationId = applicationId;
		this.userId = userId;
		setBinderId(binderId);
	}
	
	public TokenInfoBackground() {
	}
	
	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	public Long getBinderId() {
		if(Long.valueOf(0L).equals(binderId))
			return null;
		else
			return binderId;
	}
	public void setBinderId(Long binderId) {
		if(binderId != null)
			this.binderId = binderId;
		else
			this.binderId = Long.valueOf(0L);
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || !(obj instanceof TokenInfoBackground))
            return false;
            
        TokenInfoBackground info = (TokenInfoBackground) obj;
        if(applicationId.equals(info.getApplicationId()) &&
        		userId.equals(info.getUserId()) &&
        		binderId.equals(info.getBinderId()))
        	return true;
        else
        	return false;
    }
    public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + applicationId.hashCode();
    	hash = 31*hash + userId.hashCode();
    	hash = 31*hash + binderId.hashCode();
    	return hash;
    }

}
