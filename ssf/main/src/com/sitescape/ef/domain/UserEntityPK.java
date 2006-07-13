package com.sitescape.ef.domain;
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
