/**
' * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.security.SecureRandom;
import java.util.Date;

import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.function.WorkAreaOperation.RightSet;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;

/**
 * ?
 * 
 * @author jong
 */
public class ShareItem extends PersistentLongIdObject implements EntityIdentifiable { 

	public enum RecipientType {
		user((short)1),
		group((short)2),
		team((short)11),
		publicLink((short)12);
		short value;
	    RecipientType(short value) {
	    	this.value = value;
	    }
	    public short getValue() {
	    	return value;
	    }
	    public static RecipientType valueOf(short value) {
	    	switch(value) {
	    	case 1: return RecipientType.user;
	    	case 2: return RecipientType.group;
	    	case 11: return RecipientType.team;
	    	case 12: return RecipientType.publicLink;
	    	default: throw new IllegalArgumentException("Invalid db value " + value + " for enum RecipientType");
	    	}
	    }
	    //Routine to get recipient title
		public String getTitle() {
			String tag;
	    	switch(value) {
	    	case 1: tag = "share.recipientType.title.user";
	    	case 2: tag = "share.recipientType.title.group";
	    	case 11: tag = "share.recipientType.title.team";
	    	case 12: tag = "share.recipientType.title.publicLink";
	    	default: tag = "share.recipientType.title.unknown";
	    	}
			return NLT.get(tag);
		}
	    //Routine to get recipient icon
		public String getIcon() {
	    	switch(value) {
	    	case 1: return "User_16.png";
	    	case 2: return "group_16.png";
	    	case 11: return "team_16.png";
	    	case 12: return "publicLink_16.png";
	    	default: return "";
	    	}
		}
	}

	protected boolean latest = true;
	protected Long sharerId;
	protected EntityIdentifier sharedEntityIdentifier;
	protected String comment;
	protected int daysToExpire = -1;
	protected Date startDate;
	protected Date endDate;
	protected short recipientType;
	protected Long recipientId;
	protected RightSet rightSet;
	protected Boolean partOfPublicShare = Boolean.FALSE;
	protected String passKey;
	// This field is meaningful only for expired shares.
	protected Boolean expirationHandled;
    protected Date deletedDate;

	// For use by Hibernate only
	protected ShareItem() {
	}

	// For user by application
	public ShareItem(Long sharerId, 
			EntityIdentifier sharedEntityIdentifier, 
			String comment, 
			Date endDate, 
			RecipientType recipientType, 
			Long recipientId, 
			RightSet rightSet ) {
		if (sharerId == null) throw new IllegalArgumentException("Sharer ID must be specified");
		if (sharedEntityIdentifier == null) throw new IllegalArgumentException("Shared entity identifier must be specified");
		if(recipientType == null) throw new IllegalArgumentException("Recipient type must be specified");
		
		if ( recipientId == null && recipientType != RecipientType.publicLink )
			throw new IllegalArgumentException("Recipient ID must be specified");
		
		if(recipientType != RecipientType.publicLink && rightSet == null) throw new IllegalArgumentException("Right set must be specified");

		this.sharerId = sharerId;
		this.sharedEntityIdentifier = sharedEntityIdentifier;
		int commentMax = SPropsUtil.getInt("shareitem.comment.max.length", 255);
		if(comment != null && comment.length() > commentMax)
			comment = comment.substring(0, 255);
		this.comment = comment;
		this.startDate = new Date();
		this.endDate = endDate;
		setRecipientType(recipientType);
		this.recipientId = recipientId;
		this.rightSet = rightSet;
		if (recipientType == RecipientType.publicLink) {
			SecureRandom random = new SecureRandom();
			String pk = String.valueOf(random.nextLong());
			if (pk.length() > 32) pk = pk.substring(0, 31);
			this.passKey = pk;
		}
	}

	// Copy constructor.
	public ShareItem(ShareItem si) {
		// Don't copy ID and zone ID. Copy just the data.
		this.latest = si.latest;
		this.sharerId = si.sharerId;
		this.sharedEntityIdentifier = (EntityIdentifier) si.sharedEntityIdentifier.clone();
		this.comment = si.comment;
		this.daysToExpire = si.daysToExpire;
		this.startDate = new Date(si.startDate.getTime());
        if (si.endDate!=null) {
		    this.endDate = new Date(si.endDate.getTime());
        }
		this.recipientType = si.recipientType;
		this.recipientId = si.recipientId;
		this.rightSet = (RightSet) si.rightSet.clone();
		this.partOfPublicShare = si.partOfPublicShare;
        this.deletedDate = si.deletedDate;
        this.passKey = si.passKey;
	}
	
	@Override
	public EntityIdentifier getEntityIdentifier() {
		return new EntityIdentifier(getId(), getEntityType());
	}

	@Override
	public String getEntityTypedId() {
	   	return getEntityType().name() + "_" + getEntityIdentifier().getEntityId();
	}

	@Override
	public EntityType getEntityType() {
		return EntityIdentifier.EntityType.shareWith;
	}

	/**
	 * 
	 */
	public boolean getIsPartOfPublicShare()
	{
    	if ( partOfPublicShare == null )
    		return false;

    	return partOfPublicShare.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setIsPartOfPublicShare( boolean partOfPublicShare )
	{
		this.partOfPublicShare = partOfPublicShare;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getSharerId() {
		return sharerId;
	}

	public void setSharerId(Long sharerId) {
		this.sharerId = sharerId;
	}

	public EntityIdentifier getSharedEntityIdentifier() {
		return sharedEntityIdentifier;
	}

	public void setSharedEntityIdentifier(EntityIdentifier sharedEntityIdentifier) {
		this.sharedEntityIdentifier = sharedEntityIdentifier;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPassKey() {
		return passKey;
	}

	public void setPassKey(String passKey) {
		this.passKey = passKey;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getDaysToExpire() {
		return daysToExpire;
	}

	public void setDaysToExpire(int daysToExpire) {
        if (daysToExpire<=0) {
            daysToExpire = -1;
        }
		this.daysToExpire = daysToExpire;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public RecipientType getRecipientType() {
		return RecipientType.valueOf(recipientType);
	}

	public void setRecipientType(RecipientType recipientType) {
		this.recipientType = recipientType.getValue();
	}

	public RightSet getRightSet() {
		return rightSet;
	}

	public void setRightSet(RightSet rightSet) {
		this.rightSet = rightSet;
	}
	
	public boolean isExpired() {
		if(endDate == null)
			return false; // never expires
		else
			return endDate.before(new Date());
	}

    public boolean isDeleted() {
        return deletedDate != null;
    }

    //Routine to get the Role that best matches the RightSet for this ShareItem
	public Role getRole() {
        RightSet testSet = RightSet.and(this.getRightSet(), Role.CONTRIBUTOR.getRightSet());
		if (testSet.equals(Role.VIEWER.getRightSet())) return Role.VIEWER;
		if (testSet.equals(Role.EDITOR.getRightSet())) return Role.EDITOR;
		if (testSet.equals(Role.CONTRIBUTOR.getRightSet())) return Role.CONTRIBUTOR;
		if (testSet.equals(Role.NONE.getRightSet())) return Role.NONE;
		return Role.CUSTOM;
	}
	
	public boolean isLatest() {
		return latest;
	}
	
	public void setLatest(boolean current) {
		this.latest = current;
	}
	
	/*
	 * This method is meaningful only for expired shares.
	 */
	public boolean isExpirationHandled() {
		if(expirationHandled == null)
			return false;
		else
			return expirationHandled.booleanValue();
	}
	
	public void setExpirationHandled(boolean expirationHandled) {
		this.expirationHandled = expirationHandled;
	}

    public boolean createdSince(Date date) {
        return startDate!=null && date.before(startDate);
    }

    public boolean expiredSince(Date date) {
        Date expiredDate = getExpiredDate();
        if (expiredDate!=null && date.before(expiredDate)) {
            return true;
        }
        return false;
    }

	public boolean deletedSince(Date date) {
		return deletedDate!=null && date.before(deletedDate);
	}

	public Date getExpiredDate() {
        if (endDate!=null && endDate.before(new Date())) {
            if (deletedDate==null) {
                return endDate;
            }
            else {
                return endDate.before(deletedDate) ? endDate : deletedDate;
            }
        } else {
            return deletedDate;
        }
    }
	
	//****IMPORTANT****
	//These lists should be kept in sync with the fillFilrRoleViewer, fillFilrRoleEditor, 
	//    and fillFilrRoleContributor routines in AbstractZoneModule
	// The ordinalCode value is used to compare roles.
	// It's important that CONTRIBUTOR.ordinalCode > EDITOR.ordinalCode > VIEWER.ordinalCode > NONE.ordinalCode
	public static enum Role {
		VIEWER(1, "share.role.title.viewer",
				new WorkAreaOperation[] {
				WorkAreaOperation.READ_ENTRIES,
				WorkAreaOperation.ADD_REPLIES
				}),
		EDITOR(2, "share.role.title.editor",
				new WorkAreaOperation[] {
				WorkAreaOperation.READ_ENTRIES, 
				WorkAreaOperation.ADD_REPLIES,
				WorkAreaOperation.MODIFY_ENTRIES 
				}),
		CONTRIBUTOR(3, "share.role.title.contributor",
				new WorkAreaOperation[] {
				WorkAreaOperation.READ_ENTRIES, 
				WorkAreaOperation.ADD_REPLIES, 
				WorkAreaOperation.MODIFY_ENTRIES, 
				WorkAreaOperation.RENAME_ENTRIES, 
				WorkAreaOperation.CREATE_ENTRIES, 
				WorkAreaOperation.CREATE_FOLDERS, 
				WorkAreaOperation.DELETE_ENTRIES,
				WorkAreaOperation.BINDER_ADMINISTRATION, 
				WorkAreaOperation.ADD_COMMUNITY_TAGS, 
				WorkAreaOperation.GENERATE_REPORTS
				}),
		NONE(0, "share.role.title.none",
				new WorkAreaOperation[] {}),
		CUSTOM(-1, "share.role.title.custom",
				new WorkAreaOperation[] {});

		private int ordinalCode;
		private String titleCode;
		private WorkAreaOperation[] workAreaOperations;
		
		private Role(int ordinalCode, String titleCode, WorkAreaOperation[] workAreaOperations) {
			this.ordinalCode = ordinalCode;
			this.titleCode = titleCode;
			this.workAreaOperations = workAreaOperations;
		}

		public int ordinalCode() {
			return ordinalCode;
		}
		
		public String getTitle() {
			return NLT.get(titleCode);
		}
		
		public RightSet getRightSet() {
			RightSet rightSet = new RightSet();
			if(workAreaOperations != null) {
				for(WorkAreaOperation workAreaOperation:workAreaOperations)
					rightSet.setRight(workAreaOperation, true);
			}
			return rightSet;
		}
		
		/**
		 * 
		 */
		public WorkAreaOperation[] getWorkAreaOperations()
		{
			return workAreaOperations;
		}
	}
}
