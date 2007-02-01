package com.sitescape.ef.context.request;

import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
/**
 * @author Jong Kim
 *
 */
public class RequestContext {
    private String zoneName; // Always non-null
    private String userName; // Always non-null
    private Long userId; // Non-null if user is set. Otherwise may be null
    private Long zoneId; //Non-null if user is set.
    private User user;	 // May be null
    

    public RequestContext(String zoneName, String userName) {
    	this.zoneName = zoneName;
    	this.userName = userName;
    }
    
    public RequestContext(Long zoneId, Long userId) {
    	this.zoneId = zoneId;
    	this.userId = userId;
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
    
    public Long getZoneId() {
    	return zoneId;
    }
    public void setUser(User user) {
    	this.user = user;
    	if(user != null) {
    		this.userId = user.getId(); // In case this wasn't already set.
    		this.zoneId = user.getZoneId();
    	}
    }
    
    public User getUser() {
    	return user;
    }
    public Workspace getZone() {
    	if (user == null) return null;
    	return (Workspace)user.getParentBinder().getParentBinder();
    }
}
