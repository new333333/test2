package com.sitescape.ef.context.request;

import com.sitescape.ef.domain.User;

/**
 * @author Jong Kim
 *
 */
public class RequestContext {
    private User user;
    private String zoneId;

    public RequestContext(User user) {
        if(user == null)
            throw new IllegalArgumentException("User must be specified");
        this.user = user;
        if (user.isDefaultIdentity()) {
        	String name = user.getName();
        	// TODO The name of default account must be configurable (by
        	// externalizing its mapping from whatever name the portal uses)
        	// For example, 'default' -> 'anonymous'
        	this.zoneId = name.substring(0, name.indexOf("default") - 1);
        } else {
        	this.zoneId = user.getZoneId();
        }
    }
    public RequestContext(String zoneId) {
        if (zoneId == null)
            throw new IllegalArgumentException("Zone ID must be specified");
        this.user = null;
        this.zoneId = zoneId;
    }
    public String getUserName() {
    	if (user == null) return null;
    	return user.getName();
    }
    public Long getUserId() {
    	if (user == null) return null;
        return user.getId();
    }
    public User getUser() {
        return user;
    }
    public String getZoneId() {
    	return zoneId;
    }

}
