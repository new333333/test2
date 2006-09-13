package com.sitescape.ef.domain;


public class EntityIdentifier {
	protected EntityType entityType=EntityType.none;
	protected Long entityId;
	
	
	public EntityIdentifier () {}
	
	public enum EntityType {
		none (0),
		profiles (1),
		folder (2), 
		workspace (3), 
		user (4), 
		group (5), 
		folderEntry (6);
		int dbValue;
		EntityType(int dbValue) {
			this.dbValue = dbValue;
		}
		public int getValue() {return dbValue;}
		public static EntityType valueOf(int type) {
			switch (type) {
			case 0: return EntityType.none;
			case 1: return EntityType.profiles;
			case 2: return EntityType.folder;
			case 3: return EntityType.workspace;
			case 4: return EntityType.user;
			case 5: return EntityType.group;
			case 6: return EntityType.folderEntry;
			default: return EntityType.none;
			}
		}
	};

	public EntityIdentifier(Long entityId, EntityType entityType) {
		this.entityId = entityId;
		this.entityType = entityType;
	}
	/**
	 * The id of the item being tagged
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

}
