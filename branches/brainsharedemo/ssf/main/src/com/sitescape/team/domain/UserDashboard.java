package com.sitescape.team.domain;


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
		super();
		this.ownerId = ownerId;
		this.binderId = binderId;
	}
	public UserDashboard(UserDashboard dashboard) {
		super(dashboard);
		setBinderId(dashboard.getBinderId());
		setOwnerIdentifier(dashboard.getOwnerIdentifier());
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
}
