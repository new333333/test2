package com.sitescape.ef.domain;

/**
 * This object represents a dashboard configured for a user
 *
 * @hibernate.subclass discriminator-value="U" dynamic-update="true"
 * 
 */
public class UserDashboard extends EntityDashboard {

	protected Long binderId;
	
	public UserDashboard() {
		super();
	}
	public UserDashboard(EntityIdentifier ownerId, Long binderId) {
		super(ownerId);
		this.binderId = binderId;
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

}
