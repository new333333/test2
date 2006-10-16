package com.sitescape.ef.domain;

/**
 * This object represents a dashboard configured for a binder
 *
 * @hibernate.subclass discriminator-value="E" dynamic-update="true"
 * 
 * 
 */
public class EntityDashboard extends Dashboard {

	protected EntityIdentifier ownerId;
	
	public EntityDashboard() {
		super();
	}
	public EntityDashboard(EntityIdentifier ownerId) {
		super();
		this.ownerId = ownerId;
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


}
