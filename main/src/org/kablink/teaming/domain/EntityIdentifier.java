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
package org.kablink.teaming.domain;

/**
 * An entity is uniquely identified by its id and a type.
 *
 */
public class EntityIdentifier implements Comparable<EntityIdentifier>, Cloneable {
	protected EntityType entityType=EntityType.none;
	protected Long entityId;
	
	
	public EntityIdentifier () {}
	/**
	 * The database identifiers of entity are not unique alone.
	 * An additional type is needed.  The string values of the type
	 * are used by {@org.kablink.teaming.domain.AnyOwner AnyOwner}
	 *
	 */
	public enum EntityType {
		// WARNING: Do NOT reuse or fix the numeric holes that exist in the sequence.
		none (0),
		profiles (1),
		folder (2), 
		workspace (3), 
		user (4), 
		group (5), 
		folderEntry (6),
		application (8),
		applicationGroup (9),
		shareWith (12);
		int dbValue;
		EntityType(int dbValue) {
			this.dbValue = dbValue;
		}
		public int getValue() {return dbValue;}
		public boolean isBinder() {
			if ((dbValue == 1) || (dbValue == 2) || (dbValue == 3))
				return true;
			return false;
		}
		public boolean isPrincipal() {
			if ((dbValue == 4) || (dbValue == 5) || (dbValue == 8) || (dbValue == 9))
				return true;
			return false;
		}

		public static EntityType valueOf(int type) {
			switch (type) {
			case 0: return EntityType.none;
			case 1: return EntityType.profiles;
			case 2: return EntityType.folder;
			case 3: return EntityType.workspace;
			case 4: return EntityType.user;
			case 5: return EntityType.group;
			case 6: return EntityType.folderEntry;
			case 7: throw new IllegalArgumentException("Illegal type value " + type + " for EntityType"); // return EntityType.dashboard;
			case 8: return EntityType.application;
			case 9: return EntityType.applicationGroup;
			case 10: throw new IllegalArgumentException("Illegal type value " + type + " for EntityType"); // return EntityType.zone;
			case 11: throw new IllegalArgumentException("Illegal type value " + type + " for EntityType"); // return EntityType.resourceDriver;
			case 12: return EntityType.shareWith;
			default: return EntityType.none;
			}
		}
	};

	public EntityIdentifier(Long entityId, EntityType entityType) {
		this.entityId = entityId;
		this.entityType = entityType;
	}
	/**
	 * The id of the item being tagged.
	 * @hibernate.property
	 * @return
	 */
	public Long getEntityId() {
	 	return entityId;
	}
	protected void setEntityId(Long entityId) {
	 	this.entityId = entityId;
	}
	/**
	 * Internal hibernate accessor
	 * @hibernate.property
	 * @return
	 */
	protected int getType() {
		return entityType.getValue();
	}
	protected void setType(int type) {
		for (EntityType eT : EntityType.values()) {
			if (type == eT.getValue()) {
				entityType=eT;
				break;
			}
		}
	}
	/**
	 * Return the type of entity
	 * @return
	 */
	public EntityType getEntityType() {
		return entityType;
	}
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof EntityIdentifier)) return false;
		EntityIdentifier eId = (EntityIdentifier)obj;
		if (entityId.equals(eId.getEntityId()) && 
				entityType.getValue() == eId.getEntityType().getValue())
			return true;
		return false;
		
	}
	public int hashCode() {
		return 31*entityType.getValue() + entityId.hashCode();
	}
	public String toString() {
		return entityType.toString() + ":" + entityId.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(EntityIdentifier anotherEI) {
		if(entityType.getValue() < anotherEI.entityType.getValue()) {
			return -1;
		}
		else if(entityType.getValue() > anotherEI.entityType.getValue()) {
			return 1;
		}
		else {
			return entityId.compareTo(anotherEI.entityId);
		}
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone not supported: " + e.getMessage());
		}
	}

}
