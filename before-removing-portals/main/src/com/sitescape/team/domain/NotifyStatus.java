package com.sitescape.team.domain;
import java.util.Date;
/**
 * @hibernate.class table="SS_NotifyStatus" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
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
	
}
