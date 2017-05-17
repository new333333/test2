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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

/**
 * Provide auditting of significant events in the system.
 * @hibernate.class table="SS_AuditTrail"
 * @hibernate.discriminator type="char" discriminator-value="A" column="type"
 *
 * @deprecated As of Filr 1.1.1 and Vibe Hudson - Use {@link BasicAudit} and {@link LoginAudit} instead.
 */
public class AuditTrail extends ZonedObject {
	public enum AuditType {
		unknown,
		view, // viewed a data item
		add, // added a data item
		modify, // modified a data item
		delete, // deleted a data item
		preDelete, // pre-deleted a data item
		restore, // restored a data item
		workflow, // workflow change
		login, // user, client, or user agent login
		download, // user, client, or user agent download
		userStatus, // ?
		token, // application-scoped token generation
		acl // ACL change on a data item
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
    protected Long applicationId;
    // This field is applicable only if transactionType='delete' and entityType='folderEntry'.
    protected String deletedFolderEntryFamily;
    
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
	public AuditTrail(AuditType what, User user, DefinableEntity entity) {
		this(what, user, entity, new Date());
	}
	/**
	 * 
	 * @param what
	 * @param user
	 * @param entity
	 */
	public AuditTrail(AuditType what, User user, DefinableEntity entity, Date startDate) {
		setAuditType(what);
		setStartBy(user.getId());
		setStartDate(startDate);
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
		if((what.equals(AuditType.delete) || what.equals(AuditType.preDelete) || what.equals(AuditType.restore)) 
				&& (entity instanceof FolderEntry) && entity.getCreatedWithDefinitionId() != null) {
        	org.dom4j.Document def = entity.getCreatedWithDefinitionDoc();
        	String family = DefinitionUtils.getFamily(def);
        	if (Validator.isNotNull(family)) {
        		setDeletedFolderEntryFamily(family);
                if (family.equals(Constants.FAMILY_FIELD_FILE)) {
                    setFileId(entity.getPrimaryFileAttachmentId());
                }
        	}
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
    public EntityType getEntityTypeEnum() {
    	if(entityType != null)
    		return EntityType.valueOf(entityType);
    	else
    		return null;
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
	
	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	public String getDeletedFolderEntryFamily() {
		return deletedFolderEntryFamily;
	}
	public void setDeletedFolderEntryFamily(String deletedFolderEntryFamily) {
		this.deletedFolderEntryFamily = deletedFolderEntryFamily;
	}

    public EntityIdentifier toEntityIdentifier() {
        if (entityType!=null && entityId!=null) {
            try {
                EntityIdentifier.EntityType type = EntityIdentifier.EntityType.valueOf(entityType);
                return new EntityIdentifier(entityId, type);
            } catch (IllegalArgumentException e) {
            }
        }
        return null;
    }

    public List<Object> toNewAuditObjects() {
    	List<Object> result = new ArrayList<Object>();
    	BasicAudit basicAudit;
    	DeletedBinder deletedBinder;
    	LoginAudit loginAudit;
    	
    	// NOTE: Those records that are not migrated to the new tables (if any) will remain in the old table.
    	//       Only those records that are successfully migrated will be deleted from the old table.
    	
    	switch(getAuditType()) {
    	case unknown: // We don't expect to see this. If we see it, throw that away.
    		break;
    	case view:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.view);
    		result.add(basicAudit);
    		break;
    	case add:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.add);
    		result.add(basicAudit);
    		break;
    	case modify:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.modify);
    		result.add(basicAudit);
    		break;
    	case delete:
    		EntityIdentifier.EntityType eType = getEntityTypeEnum();
    		if(EntityType.folder == eType ||
    				EntityType.workspace == eType ||
    				EntityType.profiles == eType) { // Binder entity
    			// Do not move the description value from the old record to the new basic audit record. The description is a full path
    			// for deleted binder, which will be moved to a separate table.
        		basicAudit = new BasicAudit(getZoneId(), org.kablink.teaming.domain.AuditType.delete, getStartDate(), getStartBy(), 
        				EntityType.valueOf(getEntityType()), getEntityId(), 
        				getOwningBinderKey(), getOwningBinderId(), null, getFileId(), getDeletedFolderEntryFamily());
        		result.add(basicAudit);
        		// Create a new deleted binder record from the value stored in the description column of the old record. This represents
        		// path information about the deleted binder which can be pulled from and used by multiple types of reports. 
        		String binderPath = getDescription();
        		if(binderPath != null && !binderPath.equals("")) {
        			// It turns out we didn't start storing binder path information in the description column for deleted
        			// binders until after Vibe 3.4 release. So, while those pieces of data are available when upgrading
        			// from any version of Filr, they are NOT present when upgrading from Vibe 3.3 or 3.4. Therefore,
        			// we need to check the presence of the data, and if it isn't present, then do NOT create a deleted
        			// biner record in the new table. Otherwise, it will encounter a constraint violation error due to
        			// missing value in the non-null column.
	    			deletedBinder = new DeletedBinder(EntityType.valueOf(getEntityType()), getEntityId(), getStartDate(), binderPath, getZoneId());
	    			result.add(deletedBinder);
        		}
    		}
    		else { // Non-binder (entry) entity such as folder entry and user/group.
        		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.delete);
        		result.add(basicAudit);
    		}
    		break;
    	case preDelete:    		
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.preDelete);
    		result.add(basicAudit);
    		break;
    	case restore:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.restore);
    		result.add(basicAudit);
    		break;
    	case workflow: // Deprecated as of ancient ICEcore 1.1. Workflow history is stored in a separate table - We don't expect to see this. If we see it, throw that away.
    		break;
    	case login:
    		// Use empty string instead of null to indicate missing value for client IP address.
    		// NOTE: This will cause empty string value to be stored for this column with MySQL and MSSQL databases.
    		//       For Oracle which treats empty string as null, null value will be stored in the column. 
    		//       For that reason, only the Oracle database defines this column as 'nullable' while other databases define it as "not nullable".
    		loginAudit = new LoginAudit(getZoneId(), getDescription(), "", getStartBy(), getStartDate());
    		result.add(loginAudit);
    		break;
    	case download:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.download);
    		result.add(basicAudit);
    		break;
    	case userStatus:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.userStatus);
    		result.add(basicAudit);
    		break;
    	case token: // As of Filr 1.2 and Vibe Hudson, the support for this audit type is dropped. The record in the old table is NOT migrated to new table. Just throw that away.
    		break;
    	case acl:
    		basicAudit = toBasicAudit(org.kablink.teaming.domain.AuditType.acl);
    		result.add(basicAudit);
    		break;
    	}
    	
    	return result;
    }
    
    private BasicAudit toBasicAudit(org.kablink.teaming.domain.AuditType auditType) {
		return new BasicAudit(getZoneId(), auditType, getStartDate(), getStartBy(), 
				EntityType.valueOf(getEntityType()), getEntityId(), 
				getOwningBinderKey(), getOwningBinderId(), getDescription(), getFileId(), getDeletedFolderEntryFamily());
    }
    
}
