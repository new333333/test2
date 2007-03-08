package com.sitescape.team.context.request;

import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
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
    		zoneName = user.getParentBinder().getParentBinder().getName();
    		userName = user.getName();
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
