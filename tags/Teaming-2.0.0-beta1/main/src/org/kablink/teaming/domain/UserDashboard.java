/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.domain;


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
