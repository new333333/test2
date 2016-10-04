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
package org.kablink.teaming.context.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.function.WorkAreaOperation;
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
     * (Optional) Session Context
     */
    private SessionContext sessionCtx; 
    /*
     * (Optional) Access Token - This value exists if and only if the access is being made 
     * in the context of a remote application.
     */
    private AccessToken accessToken;
    
    private boolean resolved = false;
    
    /*
     * Place for caching request-scope data. No need for synchronization since
     * a request object is accessed from a single thread. 
     */
    private Map<String,Object> requestCache;
    
    private List<WorkAreaOperation> increaseByRights;
    private List<WorkAreaOperation> decreaseByRights;
    
    private String lastSearchNodeName;
    
    // The name of the thread that created and owned this request context object
    private String owningThreadName; 
    
    // (Optional) parent user object. Only used in conjunction with RunasTemplate class.
    private User parentUser;
    
    // IMPORTANT: This object is designed to contain only those properties that
    //            are needed to fetch corresponding user, application, or zone object. 
    //            Do NOT cache user, application or zone object directly in this class.
    
    public RequestContext(String zoneName, Long zoneId, String userName, Long userId, SessionContext sessionCtx) {
    	setOwningThreadName();
    	this.zoneName = zoneName;
    	this.zoneId = zoneId;
    	this.userName = userName;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    	this.requestCache = new HashMap<String,Object>();
    }
    
    public RequestContext(String zoneName, String userName, SessionContext sessionCtx) {
    	setOwningThreadName();
    	this.zoneName = zoneName;
    	this.userName = userName;
    	this.sessionCtx = sessionCtx;
    	this.requestCache = new HashMap<String,Object>();
    }
    
    public RequestContext(Long zoneId, Long userId, SessionContext sessionCtx) {
    	setOwningThreadName();
    	this.zoneId = zoneId;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    	this.requestCache = new HashMap<String,Object>();
    }
    
    public RequestContext(String zoneName, Long userId, SessionContext sessionCtx) {
    	setOwningThreadName();
    	this.zoneName = zoneName;
    	this.userId = userId;
    	this.sessionCtx = sessionCtx;
    	this.requestCache = new HashMap<String,Object>();
    }
    
    public RequestContext(Long zoneId, String userName, SessionContext sessionCtx) {
    	setOwningThreadName();
    	this.zoneId = zoneId;
    	this.userName = userName;
    	this.sessionCtx = sessionCtx;
    	this.requestCache = new HashMap<String,Object>();
    }
    
    public RequestContext(User user, SessionContext sessionCtx) {
    	setOwningThreadName();
    	setFromUser(user);
    	this.sessionCtx = sessionCtx;
    	this.requestCache = new HashMap<String,Object>();
    }

    private void setOwningThreadName() {
    	this.owningThreadName = Thread.currentThread().getName();
    }

    public String getOwningThreadName() {
    	return owningThreadName;
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
    
    public AccessToken getAccessToken() {
    	return accessToken;
    }
    public RequestContext setAccessToken(AccessToken accessToken) {
    	this.accessToken = accessToken;
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
    		if(accessToken != null && accessToken.getApplicationId() != null)
    			return getProfileDao().loadApplication(accessToken.getApplicationId(), zoneId);
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
    
	private User fetchUser() throws NoContextUserException {
		try {
            if (zoneId==null && zoneName!=null) {
                zoneId = ZoneUtil.getZoneIdByZoneName(zoneName);
            }
			User u;
			if(userId != null) {
				if(zoneId != null) {
					u = getProfileDao().loadUserDeadOrAlive(userId, zoneId);
				}
				else {
					throw new IllegalStateException("Either zone id or zone name must be specified first");
				}
			}
			else if(userName != null) {
				if(zoneId != null) {
					u = getProfileDao().findUserByName(userName, zoneId);
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
		catch(NoUserByTheIdException e) {
			throw new NoContextUserException(e);
		}
		catch(NoUserByTheNameException e) {
			throw new NoContextUserException(e);			
		}
	}

	private static ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

	private static ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

	public String toString() {
		if(accessToken != null)
			return "[" + zoneName + "," + userName + "," + accessToken.getApplicationId() + "]";
		else 
			return "[" + zoneName + "," + userName + "]";
	}
	
	public Object getCacheEntry(String key) {
		return requestCache.get(key);
	}
	public void setCacheEntry(String key, Object value) {
		requestCache.put(key, value);
	}

	public List<WorkAreaOperation> getIncreaseByRights() {
		return increaseByRights;
	}

	public void setIncreaseByRights(List<WorkAreaOperation> increaseByRights) {
		this.increaseByRights = increaseByRights;
	}

	public List<WorkAreaOperation> getDecreaseByRights() {
		return decreaseByRights;
	}

	public void setDecreaseByRights(List<WorkAreaOperation> decreaseByRights) {
		this.decreaseByRights = decreaseByRights;
	}

	public String getLastSearchNodeName() {
		return lastSearchNodeName;
	}

	public void setLastSearchNodeName(String lastSearchNodeName) {
		this.lastSearchNodeName = lastSearchNodeName;
	}

	public String getClientIdentity() {
		return getUserName() + "@" + ZoneContextHolder.getClientAddr();
	}
	
	public String getUserPrintString() {
		return "'" + userName + "' (id=" + userId + ")";
	}

	public User getParentUser() {
		return parentUser;
	}

	public void setParentUser(User parentUser) {
		this.parentUser = parentUser;
	}
}
