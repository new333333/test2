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
package com.sitescape.team.context.request;

import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
/**
 * @author Jong Kim
 *
 */
public class RequestContext {
	/*
	 * User object. May be null.
	 */
    private User user;
    /*
     * Zone ID. 
     * If user object is null, at least one of zoneId and zoneName must be non-null.
     * If user object is non-null, this is also non-null.
     */
    private Long zoneId; 
    /*
     * Zone name. 
     * If user object is null, at least one of zoneId and zoneName must be non-null.
     * If user object is non-null, this is also non-null.
     */
    private String zoneName; 
    /*
     * User ID. 
     * If user object is null, at least one of userId and userName must be non-null.  
     * If user object is non-null, this is also non-null.
     */
    private Long userId;
    /*
     * User name. 
     * If user object is null, at least one of userId and userName must be non-null.  
     * If user object is non-null, this is also non-null.
     */
    private String userName;
    /*
     * Session Context
     */
    private SessionContext sessionCtx; 
    public RequestContext(String zoneName, String userName, SessionContext sessionCtx) {
    	this.zoneName = zoneName;
    	this.userName = userName;
    	this.sessionCtx = sessionCtx;
    }
    
    public RequestContext(Long zoneId, Long userId, SessionContext sessionCtx) {
    	this.zoneId = zoneId;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    }
    
    public RequestContext(String zoneName, Long userId, SessionContext sessionCtx) {
    	this.zoneName = zoneName;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    }
    
    public RequestContext(Long zoneId, String userName, SessionContext sessionCtx) {
    	this.zoneId = zoneId;
    	this.userName = userName;
    	this.sessionCtx = sessionCtx;
    }
    
    public RequestContext(User user, SessionContext sessionCtx) {
    	this.sessionCtx = sessionCtx;
    	setUser(user);
    }

    public String getZoneName() {
    	return zoneName;
    }
    
    public String getUserName() {
    	return userName;
    }
   
    public Long getUserId() {
    	return userId;
    }
    
    public void setUserId(Long userId) {
    	this.userId = userId;
    }
    
    public Long getZoneId() {
    	return zoneId;
    }
    public void setUser(User user) {
    	this.user = user;
    	if(user != null) {
    		this.userId = user.getId();
    		this.userName = user.getName();
    		this.zoneId = user.getZoneId();
    		this.zoneName = user.getParentBinder().getRoot().getName();
    	}
    }
    
    public User getUser() {
    	return user;
    }
    public Workspace getZone() {
    	if (user == null) return null;
    	return (Workspace)user.getParentBinder().getRoot();
    }
    public SessionContext getSessionContext() {
    	return sessionCtx;
    }

}
