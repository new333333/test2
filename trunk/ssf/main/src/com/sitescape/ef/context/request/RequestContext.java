package com.sitescape.ef.context.request;

import com.sitescape.ef.domain.User;

/**
 * @author Jong Kim
 *
 */
public class RequestContext {
    private User user;
    private String zoneName;
    private String userName;

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
    
    public void setUser(User user) {
    	this.user = user;
    }
    
    public User getUser() {
    	return user;
    }
}
