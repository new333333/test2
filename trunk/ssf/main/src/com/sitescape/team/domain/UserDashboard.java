package com.sitescape.team.domain;

import java.util.HashMap;

/**
 * This object represents a dashboard configured for a user. We want it treated different
 * then EntityDashboard during queries so don't inherit.
 *
 * @hibernate.subclass discriminator-value="U" dynamic-update="true"
 * 
 */
public class UserDashboard extends Dashboard {

	protected Long binderId;
	protected EntityIdentifier ownerId;

	public UserDashboard() {
		super();
	}
	public UserDashboard(EntityIdentifier ownerId, Long binderId) {
		this.ownerId = ownerId;
		this.binderId = binderId;
	}
	   /**
     * The Entity that owns the tag
     * @hibernate.componenent
     * @return
     */
    public EntityIdentifier getOwnerIdentifier() {
    	return ownerId;
    }
    public void setOwnerIdentifier(EntityIdentifier ownerId) {
    	this.ownerId = ownerId;
    }

    /**
     * @hibernate.property
     * Id of binder this dashboard resides on
     * May be null
     */
    public Long getBinderId() {
    	return binderId;
    }
    public void setBinderId(Long binderId) {
    	this.binderId = binderId;
    }
    public UserDashboard clone() {
    	try {
    		UserDashboard other = (UserDashboard)super.clone();
    		other.setProperties(new HashMap(getProperties()));
 		   	return other;
 	   	}  catch (CloneNotSupportedException e) {
 	        // 	This shouldn't happen, since we are Cloneable
 	   		throw new InternalError("Clone error: " + e.getMessage());
 	   	}    	
    }
}
