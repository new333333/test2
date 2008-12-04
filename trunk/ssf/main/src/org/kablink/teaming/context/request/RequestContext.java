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
package org.kablink.teaming.context.request;

import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.security.accesstoken.AccessToken.BinderAccessConstraints;
import org.kablink.teaming.util.SpringContextUtil;

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
     * (Optional) Application ID.
     */
    private Long applicationId;
    /*
     * (Optional) Session Context
     */
    private SessionContext sessionCtx; 
    /*
     * (Optional) Authenticator name.
     */
    private String authenticator;
    /*
     * (Optional) binder ID
     */
    private Long binderId;
    /*
     * (Optional) a flag indicating the level of access constraints around the specified binder.
     * This value is meaningful if and only if binderId field is non-null.
     */
    private BinderAccessConstraints binderAccessConstraints;
    
    private boolean resolved = false;
    
    // IMPORTANT: This object is designed to contain only those properties that
    //            are needed to fetch corresponding user, application, or zone object. 
    //            Do NOT cache user, application or zone object directly in this class.
    
    public RequestContext(String zoneName, Long zoneId, String userName, Long userId, SessionContext sessionCtx) {
    	this.zoneName = zoneName;
    	this.zoneId = zoneId;
    	this.userName = userName;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    }
    
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
    	setFromUser(user);
    	this.sessionCtx = sessionCtx;
    }

    public String getZoneName() {
    	return zoneName;
    }
    public RequestContext setZoneName(String zoneName) {
    	this.zoneName = zoneName;
    	return this;
    }
    
    public String getUserName() {
    	return userName;
    }
    public RequestContext setUserName(String userName) {
    	this.userName = userName;
    	return this;
    }
   
    public Long getZoneId() {
    	return zoneId;
    }

    public RequestContext setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    	return this;
    }
    
    public Long getUserId() {
    	return userId;
    }
    public RequestContext setUserId(Long userId) {
    	this.userId = userId;
    	return this;
    }
    
    public Long getApplicationId() {
    	return applicationId;
    }
    
    public RequestContext setApplicationId(Long applicationId) {
    	this.applicationId = applicationId;
    	return this;
    }
    
    /**
     * Returns user object corresponding to the request context only if it is fully resolved.
     * Returns <code>null</code> otherwise.
     * 
     * @return
     */
    public User getUser() {
    	if(resolved)
    		return fetchUser();
    	else
    		return null;
    }
    
    public RequestContext setUser(User user) {
    	setFromUser(user);
    	return this;
    }
    
    /**
     * Returns zone object corresponding to the request context only if it is fully resolved.
     * Returns <code>null</code> otherwise.
     * 
     * @return
     */
    public Workspace getZone() {
    	if(resolved)
    		return (Workspace) fetchUser().getParentBinder().getRoot();
    	else
    		return null;
    }
    
    /**
     * Returns application object corresponding to the request context only if 
     * application id is specified in the request context and it is fully resolved.
     * Returns <code>null</code> otherwise.
     * 
     * @return
     */
    public Application getApplication() {
    	if(resolved) {
    		if(applicationId != null)
    			return getProfileDao().loadApplication(applicationId, zoneId);
    		else
    			return null;
    	}
    	else {
    		return null;
    	}
    }
    
    public SessionContext getSessionContext() {
    	return sessionCtx;
    }
    
    public RequestContext setAuthenticator(String authenticator) {
    	this.authenticator = authenticator;
    	return this;
    }
    
    public String getAuthenticator() {
    	return authenticator;
    }
    
    public RequestContext setBinderId(Long binderId) {
    	this.binderId = binderId;
    	return this;
    }
    
    public Long getBinderId() {
    	return binderId;
    }
    
    public BinderAccessConstraints getBinderAccessConstraints() {
		return binderAccessConstraints;
	}

	public void setBinderAccessConstraints(BinderAccessConstraints binderAccessConstraints) {
		this.binderAccessConstraints = binderAccessConstraints;
	}

	/**
     * Resolve the request context to full information.
     * If the request object is already resolved, this does nothing.
     * 
     * @return
     */
    public RequestContext resolve() {
    	if(!resolved) {
    		if(zoneId == null || zoneName == null || userId == null || userName == null) {
    			User u = fetchUser();
    			setFromUser(u);
    		}
    		resolved = true;
    	}
    	return this;
    }
    
    private void setFromUser(User user) {
    	// Do NOT cache the user object itself.
		this.userId = user.getId();
		this.userName = user.getName();
		this.zoneId = user.getZoneId();
		this.zoneName = user.getParentBinder().getRoot().getName(); // there might be more efficient way of getting the same info than doing this...
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
