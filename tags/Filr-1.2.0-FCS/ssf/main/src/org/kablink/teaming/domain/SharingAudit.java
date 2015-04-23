/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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

import org.kablink.teaming.util.SPropsUtil;

/**
 * @author Jong
 *
 */
public class SharingAudit extends ZonedObject {

	private static final int ENTRY_TITLE_MAX_SIZE_DEFAULT = 128;

	/*
	 * Type of action
	 */
	public enum ActionType {
		add((short)1),
		modify((short)2),
		delete((short)3);
		short value;
		ActionType(short value) {
	    	this.value = value;
	    }
	    public short getValue() {
	    	return value;
	    }
	    public static ActionType valueOf(short value) {
	    	switch(value) {
	    	case 1: return ActionType.add;
	    	case 2: return ActionType.modify;
	    	case 3: return ActionType.delete;
	    	default: throw new IllegalArgumentException("Invalid db value " + value + " for enum SharingAudit.ActionType");
	    	}
	    }
	}
	
	/*
	 * This enum type defines the string names and their respective database values of the roles
	 * that were in effect at the time of sharing activity such as add, modify, and delete.
	 * 
	 * NOTE: This enum is NOT tied to the other enum type called {@link Role}. While {@link Role}
	 * provides the definition of the roles (in terms of the rights that constitute each) in the
	 * currently executing version of the code, it doesn't guarantee that the software was using
	 * the same definitions or even same role names in the previous versions of the product.
	 * In other word, there's no guarantee that what was previously stored in the database carry
	 * the same meaning or can be interpreted by the current code. For that reason, we store
	 * string name of the role which was meaningful at the time of action. And when we present
	 * this value in the reports later on, we do NOT attempt to interpret the meaning of the value.
	 * Instead, we simply present the string value for display purpose only, and it is up to the
	 * human user to make any sense out of that value.
	 * 
	 * NOTE: This enum type maintains the mapping between the name of the role and its database
	 * value. As such, it is crucial that all historical mappings must be maintained, and no 
	 * mapping element should ever be removed from this list. It is OK if a definition of a role
	 * changes in a version of the software. For example, say, viewer in Filr 2.0 no longer carries
	 * the same meaning as the viewer in Filr 1.1 and in fact it means something very different. 
	 * Then, the developer extending this code for Filr 2.0 will have to make educated decision
	 * as to whether it is best to map the 2.0 definition of the viewer role to a new role name
	 * such as "viewer2.0" or just map it to the same old role name "viewer". It will depend 
	 * primarily on whether or not the reports are required to be able to distinguish between the 
	 * old viewer and new viewer definitions. The bottom line is, unlike the {@link Role} enum
	 * type which represents only the current state of the definition, this enum type must be
	 * accumulative in the sense that the enum elements should represent ALL names of the roles
	 * that have ever come to play in the history of the system regardless of their semantics.
	 */
	public enum RoleName {
		VIEWER((short)1),
		EDITOR((short)2),
		CONTRIBUTOR((short)3);
		short value;
		RoleName(short value) {
	    	this.value = value;
	    }
	    public short getValue() {
	    	return value;
	    }
	    public static RoleName valueOf(short value) {
	    	switch(value) {
	    	case 1: return RoleName.VIEWER;
	    	case 2: return RoleName.EDITOR;
	    	case 3: return RoleName.CONTRIBUTOR;
	    	default: throw new IllegalArgumentException("Invalid db value " + value + " for enum SharingAudit.RoleName");
	    	}
	    }
	    public static RoleName valueOf(ShareItem.Role role) {
			if(ShareItem.Role.VIEWER == role)
				return RoleName.VIEWER;
			else if(ShareItem.Role.EDITOR == role)
				return RoleName.EDITOR;
			else if(ShareItem.Role.CONTRIBUTOR == role)
				return RoleName.CONTRIBUTOR;
			else
				return null;
	    }
	}

	private Long id;
	
	// Required fields
	private Long sharerId; // User ID of the sharer
	private short entityType; // Type of the entity being shared
	private Long entityId; // ID of the entity being shared
	// ID of the owning binder - If entity type is entry, this is the ID of the
	// parent binder. If entity type is binder, this is equal to the entity ID.
	private Long owningBinderId;
	private short recipientType; // Type of the recipient
	private Long recipientId; // ID of the recipient
	private short actionType; // Action type - add, modify, delete
	private Date actionDate; // Time of action
	private short roleNameValue; // Numeric value corresponding to the name of the role in effect at the time of the action

	// Optional fields
	private String entryTitle; // Title of the entity being shared only if the entity is an entry (not a binder)

	// For Hibernate
	protected SharingAudit() {
	}
	
	// For application
	public SharingAudit(Long sharerId, EntityIdentifier.EntityType entityType, Long entityId, Long owningBinderId, ShareItem.RecipientType recipientType, Long recipientId,
			ActionType actionType, Date actionDate, ShareItem.Role role) {
		if(sharerId == null) throw new IllegalArgumentException("Sharer ID must be specifed");
		if(entityType == null) throw new IllegalArgumentException("Entity type must be specifed");
		if(entityId == null) throw new IllegalArgumentException("Entity ID must be specifed");
		if(owningBinderId == null) throw new IllegalArgumentException("Owning binder ID must be specifed");
		if(recipientType == null) throw new IllegalArgumentException("Recipient type must be specifed");
		if(recipientId == null) throw new IllegalArgumentException("Recipient ID must be specifed");
		if(actionType == null) throw new IllegalArgumentException("Action type must be specifed");
		if(actionDate == null) throw new IllegalArgumentException("Action date must be specifed");
		if(role == null) throw new IllegalArgumentException("Role must be specifed");

		this.sharerId = sharerId;
		this.entityType = (short) entityType.getValue();
		this.entityId = entityId;
		this.owningBinderId = owningBinderId;
		this.recipientType = recipientType.getValue();
		this.recipientId = recipientId;
		this.actionType = actionType.getValue();
		this.actionDate = actionDate;
		RoleName roleName = RoleName.valueOf(role);
		if(roleName != null)
			this.roleNameValue = roleName.getValue();
	}
	
	public SharingAudit(ShareItem shareItem, Long owningBinderId, ActionType actionType) {
		this(shareItem.getSharerId(),
				shareItem.getSharedEntityIdentifier().getEntityType(),
				shareItem.getSharedEntityIdentifier().getEntityId(),
				owningBinderId,
				shareItem.getRecipientType(),
				((shareItem.getRecipientId() != null)? shareItem.getRecipientId() : 0), // Use value of zero as an indication that the original value is null 
				actionType,
				computeActionDate(shareItem, actionType),
				shareItem.getRole());
	}
	
	private static Date computeActionDate(ShareItem shareItem, ActionType actionType) {
		Date actionDate = null;
		if(actionType == ActionType.add) {
			// For now, make action date the same as the sharing start date to make comparison/match easier.
			// If we ever support a use case where sharing start can come into effect at a future time
			// (i.e., delayed sharing), then this code must be changed to use the current time as action time.
			actionDate = shareItem.getStartDate();
		}
		else if(actionType == ActionType.delete) {
			// Make action date the same as the deleted date to make comparison/match easier.
			actionDate = shareItem.getDeletedDate();
		}
		if(actionDate == null)
			actionDate = new Date(); // For modification, current time is the action time.
		return actionDate;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getSharerId() {
		return sharerId;
	}
	
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.valueOf(entityType);
	}
	
	public Long getEntityId() {
		return entityId;
	}
	
	public Long getOwningBinderId() {
		return owningBinderId;
	}

	public ShareItem.RecipientType getRecipientType() {
		return ShareItem.RecipientType.valueOf(recipientType);
	}
	
	public Long getRecipientId() {
		return recipientId;
	}
	
	public ActionType getActionType() {
		return ActionType.valueOf(actionType);
	}
	
	public Date getActionDate() {
		return actionDate;
	}
	
	public RoleName getRoleName() {
		return RoleName.valueOf(roleNameValue);
	}
	
	public String getEntryTitle() {
		return entryTitle;
	}
	
	public void setEntryTitle(String entryTitle) {
		int max = SPropsUtil.getInt("sharing.audit.entry.title.max.size", ENTRY_TITLE_MAX_SIZE_DEFAULT);
		if(entryTitle != null && entryTitle.length() > max) {
			entryTitle = "..." + entryTitle.substring(entryTitle.length()-max+3);
		}		
		this.entryTitle = entryTitle;
	}
}
