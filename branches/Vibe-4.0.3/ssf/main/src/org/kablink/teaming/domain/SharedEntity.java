/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
/*
 * Created on July 12, 2005
 *
 * Keep track of entries viewed per user
 */
package org.kablink.teaming.domain;
import java.util.Date;

/**
 * @hibernate.class table="SS_SharedEntity"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 *
 */
public class SharedEntity extends ZonedObject  {
	public static Long ACCESS_TYPE_PRINCIPAL=3L;
	public static Long ACCESS_TYPE_TEAM=2L;
	protected Long accessId; 
	protected Long accessType;
	protected Date sharedDate;
	protected User referer;
	protected String id;
	protected DefinableEntity entity;
    protected SharedEntity() {
    	//keep protected so only called by hibernate 
    }
    public SharedEntity(User referer, DefinableEntity entity, Long accessId, Long accessType) {
    	this.referer = referer;
    	this.accessId = accessId;
    	this.accessType = accessType;
    	this.entity = entity;
    	this.sharedDate = new Date();
    }
    protected String getId() {
    	return this.id;
    }
    protected void setId(String id) {
    	this.id = id;
    }
	public Date getSharedDate() {
		return this.sharedDate;
	}
	public void setSharedDate(Date sharedDate) {
		this.sharedDate = sharedDate;
	}
	public DefinableEntity getEntity() {
		return entity;
	}
	/**
	 * Hiberate accessor
	 * @param entity
	 */
	protected void setEntity(DefinableEntity entity) {
		this.entity = entity;
	}	
	/**
	 * User who shared the entity
	 * @return
	 */
	public User getReferer() {
		return this.referer;
	}
	protected void setReferer(User referer) {
		this.referer = referer;
	}
	protected Long getAccessId() {
		return this.accessId;
	}
	protected void setAccessId(Long accessId) {
		this.accessId = accessId;
	}
	protected Long getAccessType() {
		return this.accessType;
	}
	protected void setAccessType(Long accessType) {
		this.accessType = accessType;
	}
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SharedEntity)) return false;
		SharedEntity eId = (SharedEntity)obj;
		if (referer.equals(eId.referer) && accessId.equals(eId.accessId) && 
				accessType.equals(eId.accessType) &&
				getEntity().equals(eId.getEntity()))
			return true;
		return false;
		
	}
	public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + referer.hashCode();
    	hash = 31*hash + accessId.hashCode();
    	hash = 31*hash + accessType.hashCode();
    	hash = 31*hash + getEntity().hashCode();
		return hash;
	}

}
