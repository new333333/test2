/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

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
	public EntityDashboard(EntityDashboard dashboard) {
		super(dashboard);
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

}
