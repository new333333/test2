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
package com.sitescape.team.domain;
import java.io.Serializable;



/**
 * @author Janet McCann
 *
 */
public class UserEntityPK implements Serializable {
	private final static long serialVersionUID=1;
	private EntityIdentifier entityIdentifier;
	private Long entityId,principalId;
	private int entityType;
	
	//only used by hibernate
	protected UserEntityPK() {
	}

	public UserEntityPK(Long principalId, EntityIdentifier entityIdentifier) {
		this.principalId = principalId;
		this.entityIdentifier = entityIdentifier;
		this.entityId = entityIdentifier.getEntityId();
		this.entityType = entityIdentifier.getEntityType().getValue();
	}
	/**
 	 * @hibernate.key-property position="1"
 	 */
	public Long getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}
	/**
 	 * @hibernate.key-property position="2"
 	 */
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	/**
 	 * @hibernate.key-property position="3"
 	 */
	public int getEntityType() {
		return entityType;
	}
	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}

	public EntityIdentifier getEntityIdentifier() {
		return entityIdentifier;
	}
	public void setEntityIdentifier(EntityIdentifier entityIdentifier) {
		this.entityIdentifier = entityIdentifier;
	}
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof UserEntityPK) {
			UserEntityPK pk = (UserEntityPK) obj;
			if (pk.getPrincipalId().equals(principalId) && 
					pk.getEntityId().equals(entityId) &&
					(pk.getEntityType() == entityType)) return true;
		}
		return false;
	}
	public int hashCode() {
		return 31*entityId.hashCode() + principalId.hashCode() + entityType;
	}
}
