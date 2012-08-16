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
package org.kablink.teaming.security.function;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;

/**
 * <code>Operation</code> class defines an operation that can be performed
 * to a <code>WorkArea</code>.
 * <p>
 * Operation definitions are global within a zone, which means that
 * there do not exist separate operation definitions for each work area.
 * <p>
 * <code>Operation</code> is extensible. 
 *
 * @author Jong Kim
 */
public class WorkAreaOperation {

    // It is critically important to have this map instantiated here
    // BEFORE pre-defined WorkAreOperation instances are created. 
    private static final Map Instances = new HashMap();
    
    // Workarea operations/rights
    public final static WorkAreaOperation CREATE_ENTRIES = new WorkAreaOperation("createEntries");
    public final static WorkAreaOperation MODIFY_ENTRIES = new WorkAreaOperation("modifyEntries");    
    public final static WorkAreaOperation MODIFY_ENTRY_FIELDS = new WorkAreaOperation("modifyEntryFields");    
    public final static WorkAreaOperation DELETE_ENTRIES = new WorkAreaOperation("deleteEntries");    
    public final static WorkAreaOperation READ_ENTRIES = new WorkAreaOperation("readEntries");
    public final static WorkAreaOperation ADD_REPLIES = new WorkAreaOperation("addReplies");
    public final static WorkAreaOperation GENERATE_REPORTS = new WorkAreaOperation("generateReports");
    public final static WorkAreaOperation BINDER_ADMINISTRATION = new WorkAreaOperation("binderAdministration");
    public final static WorkAreaOperation CREATE_ENTRY_ACLS = new WorkAreaOperation("createEntryAcls");
    public final static WorkAreaOperation CHANGE_ACCESS_CONTROL = new WorkAreaOperation("changeAccessControl");
    public final static WorkAreaOperation CREATE_WORKSPACES = new WorkAreaOperation("createWorkspaces");
    public final static WorkAreaOperation CREATE_FOLDERS = new WorkAreaOperation("createFolders");
    public final static WorkAreaOperation MANAGE_ENTRY_DEFINITIONS = new WorkAreaOperation("manageEntryDefinitions");
    public final static WorkAreaOperation MANAGE_WORKFLOW_DEFINITIONS = new WorkAreaOperation("manageWorkflowDefinitions");
    public final static WorkAreaOperation CREATOR_READ = new WorkAreaOperation("creatorReadEntries");
    public final static WorkAreaOperation CREATOR_MODIFY = new WorkAreaOperation("creatorModifyEntries");
    public final static WorkAreaOperation CREATOR_DELETE = new WorkAreaOperation("creatorDeleteEntries");
    public final static WorkAreaOperation CREATOR_CREATE_ENTRY_ACLS = new WorkAreaOperation("ownerCreateEntryAcls");
    public final static WorkAreaOperation ADD_COMMUNITY_TAGS = new WorkAreaOperation("addTags");
    public final static WorkAreaOperation VIEW_BINDER_TITLE = new WorkAreaOperation("viewBinderTitle");
    public final static WorkAreaOperation ALLOW_SHARING = new WorkAreaOperation("allowSharing");

    // The following rights should not be used in access management of workareas.
    // Used to give access to zone-wide functions to a group of users
    public final static WorkAreaOperation ZONE_ADMINISTRATION = new WorkAreaOperation("zoneAdministration", true);
    public final static WorkAreaOperation ADD_GUEST_ACCESS = new WorkAreaOperation("addGuestAccess", true);
    public final static WorkAreaOperation TOKEN_REQUEST = new WorkAreaOperation("tokenRequest", true);
    public final static WorkAreaOperation ONLY_SEE_GROUP_MEMBERS = new WorkAreaOperation("onlySeeGroupMembers", true);
    public final static WorkAreaOperation OVERRIDE_ONLY_SEE_GROUP_MEMBERS = new WorkAreaOperation("overrideOnlySeeGroupMembers", true);
    public final static WorkAreaOperation MANAGE_RESOURCE_DRIVERS = new WorkAreaOperation("manageResourceDrivers", true);
    public final static WorkAreaOperation CREATE_FILESPACE = new WorkAreaOperation("createFilespace", true);
    public final static WorkAreaOperation ENABLE_SHARING = new WorkAreaOperation("enableSharing", true);

    // Default set of rights controlled by external ACLs.
	public static final WorkAreaOperation[] EXTERNALLY_CONTROLLED_RIGHTS_DEFAULT = new WorkAreaOperation[] {
		WorkAreaOperation.READ_ENTRIES, 
		WorkAreaOperation.CREATE_ENTRIES,
		WorkAreaOperation.MODIFY_ENTRIES,
		WorkAreaOperation.MODIFY_ENTRY_FIELDS,
		WorkAreaOperation.DELETE_ENTRIES,
		WorkAreaOperation.BINDER_ADMINISTRATION,
		WorkAreaOperation.CREATE_ENTRY_ACLS,
		WorkAreaOperation.CHANGE_ACCESS_CONTROL,
		WorkAreaOperation.CREATE_WORKSPACES,
		WorkAreaOperation.CREATE_FOLDERS, 
		WorkAreaOperation.CREATOR_READ,
		WorkAreaOperation.CREATOR_MODIFY, 
		WorkAreaOperation.CREATOR_DELETE,
		WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS };

    private String name;
    private boolean zoneWide=false;
    private WorkAreaOperation(String name) {
	    this.name = name;
    	if (!"viewBinderTitle".equals(name) || 
    			SPropsUtil.getBoolean("accessControl.viewBinderTitle.enabled", false)) {
		    Instances.put(name, this);
    	}
    }
    
    private WorkAreaOperation(String name, boolean zoneWide) {
        this.name = name;
        this.zoneWide = zoneWide;
    	Instances.put(name, this);
    }
    public String getName() {
        return name;
    }
    public boolean isZoneWide() {
    	return zoneWide;
    }
    public void setZoneWide(boolean zoneWide) {
    	this.zoneWide = zoneWide;
    }
    public static WorkAreaOperation getInstance(String name) {
        WorkAreaOperation op = (WorkAreaOperation) Instances.get(name);
        if(op == null) {
            op = new WorkAreaOperation(name);
        }
        return op;
    }
    //used to removed old operations from system
    public static void deleteInstance(String name) {
    	Instances.remove(name);
    }
    
    public boolean equals(Object obj) {
        if(obj instanceof WorkAreaOperation)
            return this.name.equals(((WorkAreaOperation) obj).name) && (this.zoneWide == ((WorkAreaOperation)obj).zoneWide);
        else 
            return false;
    }
    
    public int hashCode() {
        return 31*Boolean.valueOf(zoneWide).hashCode() + this.name.hashCode();
    }
    
    public String toString() {
        return getName();
    }
    
    /**
     * Returns an iterator over <code>WorkAreaOperation</code>s.
     * The returned datastructure should never be modified by the caller. 
     * 
     * @return
     */
    public static Iterator getWorkAreaOperations() {
    	return Instances.values().iterator();
    }

	public static class RightSet implements Cloneable {
		protected Boolean createEntries = Boolean.FALSE;
		protected Boolean modifyEntries = Boolean.FALSE;
		protected Boolean modifyEntryFields = Boolean.FALSE;
		protected Boolean deleteEntries = Boolean.FALSE;
		protected Boolean readEntries = Boolean.FALSE;
		protected Boolean addReplies = Boolean.FALSE;
		protected Boolean generateReports = Boolean.FALSE;
		protected Boolean binderAdministration = Boolean.FALSE;
		protected Boolean createEntryAcls = Boolean.FALSE;
		protected Boolean changeAccessControl = Boolean.FALSE;
		protected Boolean createWorkspaces = Boolean.FALSE;
		protected Boolean createFolders = Boolean.FALSE;
		protected Boolean manageEntryDefinitions = Boolean.FALSE;
		protected Boolean manageWorkflowDefinitions = Boolean.FALSE;
		protected Boolean creatorReadEntries = Boolean.FALSE;
		protected Boolean creatorModifyEntries = Boolean.FALSE;
		protected Boolean creatorDeleteEntries = Boolean.FALSE;
		protected Boolean ownerCreateEntryAcls = Boolean.FALSE;
		protected Boolean addTags = Boolean.FALSE;
		protected Boolean viewBinderTitle = Boolean.FALSE;
		protected Boolean allowSharing = Boolean.FALSE;
		
		public RightSet() {}
		
		public RightSet(WorkAreaOperation[] workAreaOperations) {
			if(workAreaOperations != null) {
				for(WorkAreaOperation workAreaOperation:workAreaOperations)
					this.setRight(workAreaOperation, true);
			}
		}
		
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException("Clone not supported: " + e.getMessage());
			}
		}

		@Override
	    public int hashCode() {
			int result = 14;
			result = 29 * result + (Boolean.TRUE.equals(createEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(modifyEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(modifyEntryFields) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(deleteEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(readEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(addReplies) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(generateReports) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(binderAdministration) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(createEntryAcls) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(changeAccessControl) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(createWorkspaces) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(createFolders) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(manageEntryDefinitions) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(manageWorkflowDefinitions) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(creatorReadEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(creatorModifyEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(creatorDeleteEntries) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(ownerCreateEntryAcls) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(addTags) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(viewBinderTitle) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowSharing) ? 1231 : 1237);
			return result;
		}

		@Override
	    public boolean equals(Object obj) {
			if(this==obj) return true;
			if(obj == null) return false;
			if(!(obj instanceof RightSet)) return false;
			RightSet that = (RightSet) obj;
			if(!equalRights(this.createEntries, that.createEntries)) return false;
			if(!equalRights(this.modifyEntries, that.modifyEntries)) return false;
			if(!equalRights(this.modifyEntryFields, that.modifyEntryFields)) return false;
			if(!equalRights(this.deleteEntries, that.deleteEntries)) return false;
			if(!equalRights(this.readEntries, that.readEntries)) return false;
			if(!equalRights(this.addReplies, that.addReplies)) return false;
			if(!equalRights(this.generateReports, that.generateReports)) return false;
			if(!equalRights(this.binderAdministration, that.binderAdministration)) return false;
			if(!equalRights(this.createEntryAcls, that.createEntryAcls)) return false;
			if(!equalRights(this.changeAccessControl, that.changeAccessControl)) return false;
			if(!equalRights(this.createWorkspaces, that.createWorkspaces)) return false;
			if(!equalRights(this.createFolders, that.createFolders)) return false;
			if(!equalRights(this.manageEntryDefinitions, that.manageEntryDefinitions)) return false;
			if(!equalRights(this.manageWorkflowDefinitions, that.manageWorkflowDefinitions)) return false;
			if(!equalRights(this.creatorReadEntries, that.creatorReadEntries)) return false;
			if(!equalRights(this.creatorModifyEntries, that.creatorModifyEntries)) return false;
			if(!equalRights(this.creatorDeleteEntries, that.creatorDeleteEntries)) return false;
			if(!equalRights(this.ownerCreateEntryAcls, that.ownerCreateEntryAcls)) return false;
			if(!equalRights(this.addTags, that.addTags)) return false;
			if(!equalRights(this.viewBinderTitle, that.viewBinderTitle)) return false;
			if(!equalRights(this.allowSharing, that.allowSharing)) return false;
			return true;
		}
		
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
				f.set(this, Boolean.valueOf(rightValue));
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException("Invalid right name '" + rightName + "'", e);
			} catch (IllegalAccessException e) {
				throw new InternalException(e);
			}
		}

		public boolean isCreateEntries() {
			if(createEntries == null) return false;
			return createEntries;
		}

		public void setCreateEntries(boolean createEntries) {
			this.createEntries = createEntries;
		}

		public boolean isModifyEntries() {
			if(modifyEntries == null) return false;
			return modifyEntries;
		}

		public void setModifyEntries(boolean modifyEntries) {
			this.modifyEntries = modifyEntries;
		}

		public boolean isModifyEntryFields() {
			if(modifyEntryFields == null) return false;
			return modifyEntryFields;
		}

		public void setModifyEntryFields(boolean modifyEntryFields) {
			this.modifyEntryFields = modifyEntryFields;
		}

		public boolean isDeleteEntries() {
			if(deleteEntries == null) return false;
			return deleteEntries;
		}

		public void setDeleteEntries(boolean deleteEntries) {
			this.deleteEntries = deleteEntries;
		}

		public boolean isReadEntries() {
			if(readEntries == null) return false;
			return readEntries;
		}

		public void setReadEntries(boolean readEntries) {
			this.readEntries = readEntries;
		}

		public boolean isAddReplies() {
			if(addReplies == null) return false;
			return addReplies;
		}

		public void setAddReplies(boolean addReplies) {
			this.addReplies = addReplies;
		}

		public boolean isGenerateReports() {
			if(generateReports == null) return false;
			return generateReports;
		}

		public void setGenerateReports(boolean generateReports) {
			this.generateReports = generateReports;
		}

		public boolean isBinderAdministration() {
			if(binderAdministration == null) return false;
			return binderAdministration;
		}

		public void setBinderAdministration(boolean binderAdministration) {
			this.binderAdministration = binderAdministration;
		}

		public boolean isCreateEntryAcls() {
			if(createEntryAcls == null) return false;
			return createEntryAcls;
		}

		public void setCreateEntryAcls(boolean createEntryAcls) {
			this.createEntryAcls = createEntryAcls;
		}

		public boolean isChangeAccessControl() {
			if(changeAccessControl == null) return false;
			return changeAccessControl;
		}

		public void setChangeAccessControl(boolean changeAccessControl) {
			this.changeAccessControl = changeAccessControl;
		}

		public boolean isCreateWorkspaces() {
			if(createWorkspaces == null) return false;
			return createWorkspaces;
		}

		public void setCreateWorkspaces(boolean createWorkspaces) {
			this.createWorkspaces = createWorkspaces;
		}

		public boolean isCreateFolders() {
			if(createFolders == null) return false;
			return createFolders;
		}

		public void setCreateFolders(boolean createFolders) {
			this.createFolders = createFolders;
		}

		public boolean isManageEntryDefinitions() {
			if(manageEntryDefinitions == null) return false;
			return manageEntryDefinitions;
		}

		public void setManageEntryDefinitions(boolean manageEntryDefinitions) {
			this.manageEntryDefinitions = manageEntryDefinitions;
		}

		public boolean isManageWorkflowDefinitions() {
			if(manageWorkflowDefinitions == null) return false;
			return manageWorkflowDefinitions;
		}

		public void setManageWorkflowDefinitions(boolean manageWorkflowDefinitions) {
			this.manageWorkflowDefinitions = manageWorkflowDefinitions;
		}

		public boolean isCreatorReadEntries() {
			if(creatorReadEntries == null) return false;
			return creatorReadEntries;
		}

		public void setCreatorReadEntries(boolean creatorReadEntries) {
			this.creatorReadEntries = creatorReadEntries;
		}

		public boolean isCreatorModifyEntries() {
			if(creatorModifyEntries == null) return false;
			return creatorModifyEntries;
		}

		public void setCreatorModifyEntries(boolean creatorModifyEntries) {
			this.creatorModifyEntries = creatorModifyEntries;
		}

		public boolean isCreatorDeleteEntries() {
			if(creatorDeleteEntries == null) return false;
			return creatorDeleteEntries;
		}

		public void setCreatorDeleteEntries(boolean creatorDeleteEntries) {
			this.creatorDeleteEntries = creatorDeleteEntries;
		}

		public boolean isOwnerCreateEntryAcls() {
			if(ownerCreateEntryAcls == null) return false;
			return ownerCreateEntryAcls;
		}

		public void setOwnerCreateEntryAcls(boolean ownerCreateEntryAcls) {
			this.ownerCreateEntryAcls = ownerCreateEntryAcls;
		}

		public boolean isAddTags() {
			if(addTags == null) return false;
			return addTags;
		}

		public void setAddTags(boolean addTags) {
			this.addTags = addTags;
		}

		public boolean isViewBinderTitle() {
			if(viewBinderTitle == null) return false;
			return viewBinderTitle;
		}

		public void setViewBinderTitle(boolean viewBinderTitle) {
			this.viewBinderTitle = viewBinderTitle;
		}

		public boolean isAllowSharing() {
			if(allowSharing == null) return false;
			return allowSharing;
		}

		public void setAllowSharing(boolean allowSharing) {
			this.allowSharing = allowSharing;
		}
		
		private boolean equalRights(Boolean right1, Boolean right2) {
			if(right1 == null)
				right1 = Boolean.FALSE;
			if(right2 == null)
				right2 = Boolean.FALSE;
			return right1.equals(right2);
		}
		
	}

}
