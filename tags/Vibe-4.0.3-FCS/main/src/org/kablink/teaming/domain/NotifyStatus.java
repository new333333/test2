/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.domain;

import java.util.Date;

import org.apache.commons.logging.Log;

/**
 * ?
 * 
 * @hibernate.class table="SS_NotifyStatus" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * @author Janet McCann
 */
public class NotifyStatus extends ZonedObject {
	protected Date lastModified;
	protected Date lastDigestSent;
	protected Date lastFullSent;
	protected Long ownerId;
	protected Long owningBinderId;
	protected String ownerType;
	protected String owningBinderKey;
	public NotifyStatus() {
	}
	public NotifyStatus(Binder binder, DefinableEntity entity) {
		setOwnerId(entity.getEntityIdentifier().getEntityId());
		setOwnerType(entity.getEntityIdentifier().getEntityType().name());
		setOwningBinderId(binder.getId());
		setOwningBinderKey(binder.getBinderKey().getSortKey());
		setLastModified(entity.getModification().getDate());
		setLastDigestSent(new Date(getLastModified().getTime()-1000));
		setLastFullSent(getLastDigestSent());
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	/**
	 * This field servers as a key for sorting.
	 * @hibernate.property
	 */
	public Long getOwningBinderId() {
		return owningBinderId;
	}
	/**
	 * Hibernate accessor
	 */
	public void setOwningBinderId(Long owningBinderId) {
		this.owningBinderId = owningBinderId;
	}
	/**
	 * @hibernate.property length="16"
	 * Here for future use.
	 */
	protected String getOwnerType() {
		return ownerType;
	}	
	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	/**
	 * @hibernate.property length="255" 
	 * @return
	 */
	public String getOwningBinderKey() {
		return owningBinderKey;
	}	
	public void setOwningBinderKey(String owningBinderKey) {
		this.owningBinderKey = owningBinderKey;
	}   

	/**
	 * @hibernate.property
	 * @return
	 */
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Date getLastDigestSent() {
		return lastDigestSent;
	}
	public void setLastDigestSent(Date lastDigestSent) {
		this.lastDigestSent = lastDigestSent;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Date getLastFullSent() {
		return lastFullSent;
	}
	public void setLastFullSent(Date lastFullSent) {
		this.lastFullSent = lastFullSent;
	}

	public void traceStatus(Log logger) {
        logger.debug("...NotifyStatus:");
        logger.debug("......lastModified: "    + getLastModified());
        logger.debug("......lastDigestSent: "  + getLastDigestSent());
        logger.debug("......lastFullSent: "    + getLastFullSent());
        logger.debug("......ownerId: "         + getOwnerId());
        logger.debug("......owningBinderId: "  + getOwningBinderId());
        logger.debug("......ownerType: "       + getOwnerType());
        logger.debug("......owningBinderKey: " + getOwningBinderKey());
	}
}
