/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.lang.reflect.Field;
import java.util.Date;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.security.function.WorkAreaOperation;

/**
 * @author jong
 *
 */
public class ShareItemMember {

	public enum RecipientType {
		user((short)1),
		group((short)2),
		team((short)11);
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
	    	default: throw new IllegalArgumentException("Invalid db value " + value + " for enum RecipientType");
	    	}
	    }
	}
	
	protected Date endDate;
	protected short recipientType;
	protected Long recipientId;
	protected RightSet rightSet;

	// For use by Hibernate only
	protected ShareItemMember() {
	}
	
	public ShareItemMember(Date endDate, RecipientType recipientType, Long recipientId, RightSet rightSet) {
		if(rightSet == null) throw new IllegalArgumentException("Right set must be specified");
		if(recipientType == null) throw new IllegalArgumentException("Recipient type must be specified");
		if(recipientId == null) throw new IllegalArgumentException("Recipient ID must be specified");

		this.endDate = endDate;
		setRecipientType(recipientType);
		this.recipientId = recipientId;
		this.rightSet = rightSet;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	protected void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getRecipientId() {
		return recipientId;
	}

	protected void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public RecipientType getRecipientType() {
		return RecipientType.valueOf(recipientType);
	}

	protected void setRecipientType(RecipientType recipientType) {
		this.recipientType = recipientType.getValue();
	}

	public RightSet getRightSet() {
		return rightSet;
	}

	protected void setRightSet(RightSet rightSet) {
		this.rightSet = rightSet;
	}
	
	public boolean isExpired() {
		if(endDate == null)
			return false; // never expires
		else
			return endDate.before(new Date());
	}
	
	public static class RightSet {
		protected boolean createEntries = false;
		protected boolean modifyEntries = false;
		protected boolean modifyEntryFields = false;
		protected boolean deleteEntries = false;
		protected boolean readEntries = false;
		protected boolean addReplies = false;
		protected boolean generateReports = false;
		protected boolean binderAdministration = false;
		protected boolean createEntryAcls = false;
		protected boolean changeAccessControl = false;
		protected boolean createWorkspaces = false;
		protected boolean createFolders = false;
		protected boolean manageEntryDefinitions = false;
		protected boolean manageWorkflowDefinitions = false;
		protected boolean creatorReadEntries = false;
		protected boolean creatorModifyEntries = false;
		protected boolean creatorDeleteEntries = false;
		protected boolean ownerCreateEntryAcls = false;
		protected boolean addTags = false;
		protected boolean viewBinderTitle = false;
		
		// The following four methods provide read and update on any right using
		// its right name as argument. Useful as generic getter/setter.
		
		public boolean getRight(WorkAreaOperation operation) {
			return getRight(operation.getName());
		}
		
		public void setRight(WorkAreaOperation operation, boolean rightValue) {
			setRight(operation.getName(), rightValue);
		}
		
		public boolean getRight(String rightName) {
			Class<?> c = this.getClass();
			Field f;
			try {
				f = c.getDeclaredField(rightName);
				return f.getBoolean(this);
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException("Invalid right name '" + rightName + "'", e);
			} catch (IllegalAccessException e) {
				throw new InternalException(e);
			}
		}
		
		public void setRight(String rightName, boolean rightValue) {
			Class<?> c = this.getClass();
			Field f;
			try {
				f = c.getDeclaredField(rightName);
				f.setBoolean(this, Boolean.valueOf(rightValue));
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException("Invalid right name '" + rightName + "'", e);
			} catch (IllegalAccessException e) {
				throw new InternalException(e);
			}
		}

		public boolean isCreateEntries() {
			return createEntries;
		}

		public void setCreateEntries(boolean createEntries) {
			this.createEntries = createEntries;
		}

		public boolean isModifyEntries() {
			return modifyEntries;
		}

		public void setModifyEntries(boolean modifyEntries) {
			this.modifyEntries = modifyEntries;
		}

		public boolean isModifyEntryFields() {
			return modifyEntryFields;
		}

		public void setModifyEntryFields(boolean modifyEntryFields) {
			this.modifyEntryFields = modifyEntryFields;
		}

		public boolean isDeleteEntries() {
			return deleteEntries;
		}

		public void setDeleteEntries(boolean deleteEntries) {
			this.deleteEntries = deleteEntries;
		}

		public boolean isReadEntries() {
			return readEntries;
		}

		public void setReadEntries(boolean readEntries) {
			this.readEntries = readEntries;
		}

		public boolean isAddReplies() {
			return addReplies;
		}

		public void setAddReplies(boolean addReplies) {
			this.addReplies = addReplies;
		}

		public boolean isGenerateReports() {
			return generateReports;
		}

		public void setGenerateReports(boolean generateReports) {
			this.generateReports = generateReports;
		}

		public boolean isBinderAdministration() {
			return binderAdministration;
		}

		public void setBinderAdministration(boolean binderAdministration) {
			this.binderAdministration = binderAdministration;
		}

		public boolean isCreateEntryAcls() {
			return createEntryAcls;
		}

		public void setCreateEntryAcls(boolean createEntryAcls) {
			this.createEntryAcls = createEntryAcls;
		}

		public boolean isChangeAccessControl() {
			return changeAccessControl;
		}

		public void setChangeAccessControl(boolean changeAccessControl) {
			this.changeAccessControl = changeAccessControl;
		}

		public boolean isCreateWorkspaces() {
			return createWorkspaces;
		}

		public void setCreateWorkspaces(boolean createWorkspaces) {
			this.createWorkspaces = createWorkspaces;
		}

		public boolean isCreateFolders() {
			return createFolders;
		}

		public void setCreateFolders(boolean createFolders) {
			this.createFolders = createFolders;
		}

		public boolean isManageEntryDefinitions() {
			return manageEntryDefinitions;
		}

		public void setManageEntryDefinitions(boolean manageEntryDefinitions) {
			this.manageEntryDefinitions = manageEntryDefinitions;
		}

		public boolean isManageWorkflowDefinitions() {
			return manageWorkflowDefinitions;
		}

		public void setManageWorkflowDefinitions(boolean manageWorkflowDefinitions) {
			this.manageWorkflowDefinitions = manageWorkflowDefinitions;
		}

		public boolean isCreatorReadEntries() {
			return creatorReadEntries;
		}

		public void setCreatorReadEntries(boolean creatorReadEntries) {
			this.creatorReadEntries = creatorReadEntries;
		}

		public boolean isCreatorModifyEntries() {
			return creatorModifyEntries;
		}

		public void setCreatorModifyEntries(boolean creatorModifyEntries) {
			this.creatorModifyEntries = creatorModifyEntries;
		}

		public boolean isCreatorDeleteEntries() {
			return creatorDeleteEntries;
		}

		public void setCreatorDeleteEntries(boolean creatorDeleteEntries) {
			this.creatorDeleteEntries = creatorDeleteEntries;
		}

		public boolean isOwnerCreateEntryAcls() {
			return ownerCreateEntryAcls;
		}

		public void setOwnerCreateEntryAcls(boolean ownerCreateEntryAcls) {
			this.ownerCreateEntryAcls = ownerCreateEntryAcls;
		}

		public boolean isAddTags() {
			return addTags;
		}

		public void setAddTags(boolean addTags) {
			this.addTags = addTags;
		}

		public boolean isViewBinderTitle() {
			return viewBinderTitle;
		}

		public void setViewBinderTitle(boolean viewBinderTitle) {
			this.viewBinderTitle = viewBinderTitle;
		}
		
	}
	
	public static void main(String[] args) {
		RightSet rightSet = new RightSet();
		System.out.println("BEFORE: " + rightSet.getRight("generateReports"));
		rightSet.setRight("generateReports", true);
		System.out.println("AFTER: " + rightSet.getRight("generateReports"));
	}

}
