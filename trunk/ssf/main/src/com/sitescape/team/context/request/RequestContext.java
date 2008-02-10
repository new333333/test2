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

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.SpringContextUtil;
/**
 * @author Jong Kim
 *
 */
public class RequestContext {
    /*
     * Zone ID.
      */
    private Long zoneId; 
    /*
     * Zone name.
     */
    private String zoneName; 
    /*
     * User ID.
     */
    private Long userId;
    /*
     * User name.
     */
    private String userName;
    /*
     * Session Context
     */
    private SessionContext sessionCtx; 
    /*
     * (Optional) Authenticator name.
     */
    private String authenticator;
    
    private boolean resolved = false;
    
    // IMPORTANT: This object is designed to contain only those properties that
    //            are needed to fetch corresponding user or zone object. 
    //            Do NOT cache user or zone object directly in this class.
    
    /**
     * Create fully resolved request context.
     */
    public RequestContext(String zoneName, Long zoneId, String userName, Long userId, SessionContext sessionCtx) {
    	this.zoneName = zoneName;
    	this.zoneId = zoneId;
    	this.userName = userName;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    	this.resolved = true;
    }
    
    /**
     * Create request context with partial data.
     * 
     * @param zoneName
     * @param userName
     * @param sessionCtx
     */
    public RequestContext(String zoneName, String userName, SessionContext sessionCtx) {
    	this.zoneName = zoneName;
    	this.userName = userName;
    	this.sessionCtx = sessionCtx;
    }
    
    /**
     * Create request context with partial data.
     * 
     * @param zoneId
     * @param userId
     * @param sessionCtx
     */
    public RequestContext(Long zoneId, Long userId, SessionContext sessionCtx) {
    	this.zoneId = zoneId;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    }
    
    /**
     * Create request context with partial data.
     * 
     * @param zoneName
     * @param userId
     * @param sessionCtx
     */
    public RequestContext(String zoneName, Long userId, SessionContext sessionCtx) {
    	this.zoneName = zoneName;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    }
    
    /**
     * Create request context with partial data.
     * 
     * @param zoneId
     * @param userName
     * @param sessionCtx
     */
    public RequestContext(Long zoneId, String userName, SessionContext sessionCtx) {
    	this.zoneId = zoneId;
    	this.userName = userName;
    	this.sessionCtx = sessionCtx;
    }
    
    /**
     * Create fully resolved request context.
     * 
     * @param user
     * @param sessionCtx
     */
    public RequestContext(User user, SessionContext sessionCtx) {
    	setFromUser(user);
    	this.sessionCtx = sessionCtx;
    }

    public String getZoneName() {
    	return zoneName;
    }
    public void setZoneName(String zoneName) {
    	this.zoneName = zoneName;
    	checkResolved(); // recheck
    }
    
    public String getUserName() {
    	return userName;
    }
    public void setUserName(String userName) {
    	this.userName = userName;
    	checkResolved(); // recheck
    }
   
    public Long getZoneId() {
    	return zoneId;
    }

    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    	checkResolved(); // recheck
    }
    
    public Long getUserId() {
    	return userId;
    }
    public void setUserId(Long userId) {
    	this.userId = userId;
    	checkResolved(); // recheck
    }
    
    /**
     * Returns user object corresponding to the request context only if it is fully resolved.
     * Returns <code>null</cde> otherwise.
     * 
     * @return
     */
    public User getUser() {
    	if(resolved)
    		return fetchUser();
    	else
    		return null;
    }
    
    public void setUser(User user) {
    	setFromUser(user);
    }
    
    /**
     * Returns zone object corresponding to the request context only if it is fully resolved.
     * Returns <code>null</cde> otherwise.
     * 
     * @return
     */
    public Workspace getZone() {
    	if(resolved)
    		return (Workspace) fetchUser().getParentBinder().getRoot();
    	else
    		return null;
    }
    
    public SessionContext getSessionContext() {
    	return sessionCtx;
    }
    
    public void setAuthenticator(String authenticator) {
    	this.authenticator = authenticator;
    }
    
    public String getAuthenticator() {
    	return authenticator;
    }
    
    /**
     * Resolve the request context to full information.
     * If the request object is already resolved, this does nothing.
     * 
     * @return
     */
    public RequestContext resolve() {
    	if(!resolved) {
    		User u = fetchUser();
    		setFromUser(u);	
    	}
    	return this;
    }
    
    private void checkResolved() {
    	if(zoneName != null && zoneId != null && userName != null && userId != null)
    		resolved = true;
    	else
    		resolved = false;
    }
    
    private void setFromUser(User user) {
    	// Do NOT cache the user object itself.
		this.userId = user.getId();
		this.userName = user.getName();
		this.zoneId = user.getZoneId();
		this.zoneName = user.getParentBinder().getRoot().getName(); // there might be more efficient way of getting the same info than doing this...
    	this.resolved = true;
    }
    
	private User fetchUser() {
		User u;
		if(userId != null) {
			if(zoneId != null) {
				u = getProfileDao().loadUser(userId, zoneId);
			}
			else if(zoneName != null) {
				u = getProfileDao().loadUser(userId, zoneName);					
			}
			else {
				throw new IllegalStateException("Either zone id or zone name must be specified first");
			}
		}
		else if(userName != null) {
			if(zoneId != null) {
				u = getProfileDao().findUserByName(userName, zoneId);
			}
			else if(zoneName != null) {
				u = getProfileDao().findUserByName(userName, zoneName);					
			}
			else {
				throw new IllegalStateException("Either zone id or zone name must be specified first");
			}				
		}
		else {
			throw new IllegalStateException("Either user id or user name must be specified first");				
		}
		return u;
	}
	
	private static ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

}
