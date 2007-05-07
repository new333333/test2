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

import java.util.Date;

import com.sitescape.team.domain.EntityIdentifier.EntityType;

/**
 * @hibernate.class table="SS_AuditTrail"
 * @hibernate.discriminator type="char" discriminator-value="A" column="type"
 *
 */
public class AuditTrail  {
	public enum AuditType {
		unknown,
		view,
		add, 
		modify, 
		delete,
		workflow,
		login
	};

	
    protected String id;
	protected Date startDate,endDate;
    protected Long startBy,endBy;
	protected Long entityId;
	protected String entityType;
	protected Long owningBinderId;
	protected String owningBinderKey;  //used for queries
    protected String description; // any additional description
    protected String transactionType; // type of transaction/operation
    protected AuditType auditType=AuditType.unknown;
    
    public AuditTrail() {
		
	}
	public AuditTrail(AnyOwner owner, HistoryStamp start, HistoryStamp end) {
    	setEntityId(owner.getOwnerId());
    	setEntityType(owner.getOwnerType());
		setOwningBinderId(owner.getOwningBinderId());
		setOwningBinderKey(owner.getOwningBinderKey());
		setStart(start);
		setEnd(end);
	}
	public AuditTrail(AuditType what, User user, DefinableEntity entity) {
		setAuditType(what);
		setStartBy(user.getId());
		setStartDate(new Date());
		setEntityId(entity.getEntityIdentifier().getEntityId());
		setEntityType(entity.getEntityType().name());
		if (entity instanceof Binder) {
			setOwningBinderId(entity.getId());
			setOwningBinderKey(((Binder)entity).getBinderKey().getSortKey());
		} else {
			Binder b = entity.getParentBinder();
			setOwningBinderId(b.getId());
			setOwningBinderKey(b.getBinderKey().getSortKey());
		}
		
		
	}
	/**
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null" node="@id"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Long getEntityId() {
    	return entityId;
    }
    public void setEntityId(Long entityId) {
    	this.entityId = entityId;
    }
    /**
     * @hibernate.property length="16"
     * @return
     */
    public String getEntityType() {
    	return entityType;
    }
    public void setEntityType(String entityType) {
    	this.entityType = entityType;
    }
    /**
     * Binder owning the entity - may be null
     * @hibernate.property 
     */
    public Long getOwningBinderId() {
    	return this.owningBinderId;
    }
    public void setOwningBinderId(Long owningBinderId) {
    	this.owningBinderId = owningBinderId;
    }
    /**
     * query key for owningbinder - may be null
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
     * - may be null
     * @hibernate.property length="512"
     * @return
     */
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
	/**
     * @hibernate.property
     */
    public Date getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Date start) {
        this.startDate = start;
    }

	/**
     * @hibernate.property
     */
    public Long getStartBy() {
        return this.startBy;
    }
    public void setStartBy(Long startBy) {
        this.startBy = startBy;
    }
    public void setStart(HistoryStamp start) {
    	this.startDate = start.getDate();
    	this.startBy = start.getPrincipal().getId();
    }
 	/**
     * @hibernate.property
     * - may be null
     */
    public Date getEndDate() {
        return this.endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
 	/**
     * @hibernate.property
     * - may be null
     */
    public Long getEndBy() {
        return this.endBy;
    }
    public void setEndBy(Long endBy) {
        this.endBy = endBy;
    }
    public void setEnd(HistoryStamp end) {
    	this.endDate = end.getDate();
    	this.endBy = end.getPrincipal().getId();
    }
   
    public AuditType getAuditType() {
        return auditType;
    }
    public void setAuditType(AuditType auditType) {
        this.auditType = auditType;
    }
    /**
	 * Internal hibernate accessor
     * @hibernate.property length="16"
     * @return
     */
    protected String getTransactionType() {
    	if (auditType == null) return AuditType.unknown.name();
        return auditType.name();
    }
    protected void setTransactionType(String transactionType) {
		for (AuditType eT : AuditType.values()) {
			if (transactionType.equals(eT.name())) {
				auditType=eT;
				break;
			}
		}
   }
}
