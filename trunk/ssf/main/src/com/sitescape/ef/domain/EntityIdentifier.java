package com.sitescape.ef.domain;


public class EntityIdentifier {
	protected EntityType entityType=EntityType.none;
	protected Long entityId;
	public enum EntityType {
		none (0),
		folder (1), 
		workspace (2), 
		user (3), 
		group (4), 
		folderEntry (5);
		int dbValue;
		EntityType(int dbValue) {
			this.dbValue = dbValue;
		}
		public int getValue() {return dbValue;}
		public static EntityType valueOf(int type) {
			switch (type) {
			case 0: return EntityType.none;
			case 1: return EntityType.folder;
			case 2: return EntityType.workspace;
			case 3: return EntityType.user;
			case 4: return EntityType.group;
			case 5: return EntityType.folderEntry;
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
	protected int getDbType() {
		return entityType.getValue();
	}
	protected void setDbType(int dbType) {
		for (EntityType type : EntityType.values()) {
			if (dbType == type.getValue()) {
				entityType=type;
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
		

}
