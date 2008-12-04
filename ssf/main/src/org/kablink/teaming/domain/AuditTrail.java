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

import java.util.Date;

/**
 * Provide auditting of significant events in the system.
 * @hibernate.class table="SS_AuditTrail"
 * @hibernate.discriminator type="char" discriminator-value="A" column="type"
 *
 */
public class AuditTrail extends ZonedObject {
	public enum AuditType {
		unknown,
		view,
		add, 
		modify, 
		delete,
		workflow,
		login,
		download,
		userStatus
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
    protected String fileId;
    
    protected AuditTrail() {
		
	}
    /**
     * Add the owner information, start and end date of the event
     * @param owner
     * @param start
     * @param end
     */
	public AuditTrail(AnyOwner owner, HistoryStamp start, HistoryStamp end) {
    	setEntityId(owner.getOwnerId());
    	setEntityType(owner.getOwnerType());
		setOwningBinderId(owner.getOwningBinderId());
		setOwningBinderKey(owner.getOwningBinderKey());
		setStart(start);
		setEnd(end);
	}
	/**
	 * 
	 * @param what
	 * @param user
	 * @param entity
	 */
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
	 * Audit database id. Automatically generated
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null" node="@id"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    protected void setId(String id) {
        this.id = id;
    }
    /**
     * Return the id of the entity
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
     * The entity type.  {@link org.kablink.teaming.domain.EntityIdentifier.EntityType EntityType}
     * @hibernate.property length="16"
     * @return
     */
    public String getEntityType() {
    	return entityType;
    }
    protected void setEntityType(String entityType) {
    	this.entityType = entityType;
    }
    /**
     * Return the id of the binder owning the entity.
     * If the entity is a binder, the id is the binder's id.
     * @hibernate.property 
     * @return
     */
    public Long getOwningBinderId() {
    	return this.owningBinderId;
    }
    protected void setOwningBinderId(Long owningBinderId) {
    	this.owningBinderId = owningBinderId;
    }
    /**
     * Return the sort key for the owning binder.
     * If the entity is a binder, the key is the binder's sort key.
     * @hibernate.property length="255" 
     * @return
     */
    public String getOwningBinderKey() {
        return owningBinderKey;
    }
    protected void setOwningBinderKey(String owningBinderKey) {
        this.owningBinderKey = owningBinderKey;
    } 
    
    /**
     * Return a description of the event.  
     * May be <code>null</code>
     * @hibernate.property length="512"
     * @return
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * Optional. Set the description.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
	/**
	 * Start time of event
     * @hibernate.property
     */
    public Date getStartDate() {
        return this.startDate;
    }
    protected void setStartDate(Date start) {
        this.startDate = start;
    }

	/**
	 * Return id of <code>Principal</code> that started the audit
     * @hibernate.property
     */
    public Long getStartBy() {
        return this.startBy;
    }
    protected void setStartBy(Long startBy) {
        this.startBy = startBy;
    }
    protected void setStart(HistoryStamp start) {
    	this.startDate = start.getDate();
    	this.startBy = start.getPrincipal().getId();
    }
 	/**
 	 * Return the end date of the event.
     * May be <code>null</code>
     * @hibernate.property
     */
    public Date getEndDate() {
        return this.endDate;
    }
    protected void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
 	/**
 	 * Return the id of the <code>Principal</code> that ended the audit.
     * May be <code>null</code>
     * @hibernate.property
     */
    public Long getEndBy() {
        return this.endBy;
    }
    protected void setEndBy(Long endBy) {
        this.endBy = endBy;
    }
    protected void setEnd(HistoryStamp end) {
    	this.endDate = end.getDate();
    	this.endBy = end.getPrincipal().getId();
    }
    /**
     * Return the type of event being auditted.
     * @return
     */
    public AuditType getAuditType() {
        return auditType;
    }
    protected void setAuditType(AuditType auditType) {
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
	/**
	 * Return the file attachment id.
	 * Used with <code>AutitType.download</code>.
	 * @hibernate.property
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
}
