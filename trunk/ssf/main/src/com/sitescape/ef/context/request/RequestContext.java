package com.sitescape.ef.context.request;

import com.sitescape.ef.domain.User;

/**
 * @author Jong Kim
 *
 */
public class RequestContext {
    private String zoneName; // Always non-null
    private String userName; // Always non-null
    private Long userId; // Non-null if user is set. Otherwise may be null
    private User user;	 // May be null
    

    public RequestContext(String zoneName, String userName) {
    	this.zoneName = zoneName;
    	this.userName = userName;
    }
    
    public String getZoneName() {
    	return zoneName;
    }
    
    public String getUserName() {
    	return userName;
    }

    public void setUserId(Long userId) {
    	this.userId = userId;
    }
    
    public Long getUserId() {
    	return userId;
    }
    
    public void setUser(User user) {
    	this.user = user;
    	this.userId = user.getId(); // In case this wasn't already set.
    }
    
    public User getUser() {
    	return user;
    }
}
