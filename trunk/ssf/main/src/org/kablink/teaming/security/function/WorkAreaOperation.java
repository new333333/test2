/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;

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
@SuppressWarnings("unchecked")
public class WorkAreaOperation {

    // It is critically important to have this map instantiated here
    // BEFORE pre-defined WorkAreOperation instances are created. 
	private static final Map Instances = new HashMap();
    
    // Workarea operations/rights
    public final static WorkAreaOperation CREATE_ENTRIES = new WorkAreaOperation("createEntries");
    public final static WorkAreaOperation MODIFY_ENTRIES = new WorkAreaOperation("modifyEntries");    
    public final static WorkAreaOperation MODIFY_ENTRY_FIELDS = new WorkAreaOperation("modifyEntryFields");    
    public final static WorkAreaOperation RENAME_ENTRIES = new WorkAreaOperation("renameEntries");    
    public final static WorkAreaOperation DELETE_ENTRIES = new WorkAreaOperation("deleteEntries");    
    public final static WorkAreaOperation READ_ENTRIES = new WorkAreaOperation("readEntries");
    public final static WorkAreaOperation ADD_REPLIES = new WorkAreaOperation("addReplies");
    public final static WorkAreaOperation GENERATE_REPORTS = new WorkAreaOperation("generateReports");
    public final static WorkAreaOperation DOWNLOAD_FOLDER_AS_CSV = new WorkAreaOperation("downloadFolderAsCsv");
    public final static WorkAreaOperation BINDER_ADMINISTRATION = new WorkAreaOperation("binderAdministration");
    public final static WorkAreaOperation CREATE_ENTRY_ACLS = new WorkAreaOperation("createEntryAcls");
    public final static WorkAreaOperation CHANGE_ACCESS_CONTROL = new WorkAreaOperation("changeAccessControl");
    public final static WorkAreaOperation CREATE_WORKSPACES = new WorkAreaOperation("createWorkspaces");
    public final static WorkAreaOperation CREATE_FOLDERS = new WorkAreaOperation("createFolders");
    public final static WorkAreaOperation MANAGE_ENTRY_DEFINITIONS = new WorkAreaOperation("manageEntryDefinitions");
    public final static WorkAreaOperation MANAGE_WORKFLOW_DEFINITIONS = new WorkAreaOperation("manageWorkflowDefinitions");
    public final static WorkAreaOperation CREATOR_READ = new WorkAreaOperation("creatorReadEntries");
    public final static WorkAreaOperation CREATOR_MODIFY = new WorkAreaOperation("creatorModifyEntries");
    public final static WorkAreaOperation CREATOR_RENAME = new WorkAreaOperation("creatorRenameEntries");
    public final static WorkAreaOperation CREATOR_DELETE = new WorkAreaOperation("creatorDeleteEntries");
    public final static WorkAreaOperation CREATOR_CREATE_ENTRY_ACLS = new WorkAreaOperation("ownerCreateEntryAcls");
    public final static WorkAreaOperation ADD_COMMUNITY_TAGS = new WorkAreaOperation("addTags");
    public final static WorkAreaOperation VIEW_BINDER_TITLE = new WorkAreaOperation("viewBinderTitle");
    public final static WorkAreaOperation ALLOW_SHARING_INTERNAL = new WorkAreaOperation("allowSharing");
    public final static WorkAreaOperation ALLOW_SHARING_EXTERNAL = new WorkAreaOperation("allowSharingExternal");
    public final static WorkAreaOperation ALLOW_SHARING_PUBLIC = new WorkAreaOperation("allowSharingPublic");
    public final static WorkAreaOperation ALLOW_SHARING_FORWARD = new WorkAreaOperation("allowSharingForward");
    public final static WorkAreaOperation ALLOW_ACCESS_NET_FOLDER = new WorkAreaOperation("allowAccessNetFolder");
    public final static WorkAreaOperation ALLOW_SHARING_PUBLIC_LINKS = new WorkAreaOperation("allowSharingPublicLinks");
    // The following set of three rights are used exclusively for sharing folders in net folders.
    // The old set - ALLOW_SHARING_INTERNAL, ALLOW_SHARING_EXTERNAL, and ALLOW_SHARING_PUBLIC - are used to
    // control sharing of files and folders in home folders and personal storage as well as files in net folders.
    public final static WorkAreaOperation ALLOW_FOLDER_SHARING_INTERNAL = new WorkAreaOperation("allowFolderSharingInternal");
    public final static WorkAreaOperation ALLOW_FOLDER_SHARING_EXTERNAL = new WorkAreaOperation("allowFolderSharingExternal");
    public final static WorkAreaOperation ALLOW_FOLDER_SHARING_PUBLIC = new WorkAreaOperation("allowFolderSharingPublic");
    public final static WorkAreaOperation ALLOW_FOLDER_SHARING_FORWARD = new WorkAreaOperation("allowFolderSharingForward");

    // The following rights should not be used in access management of workareas.
    // Used to give access to zone-wide functions to a group of users
    public final static WorkAreaOperation ZONE_ADMINISTRATION = new WorkAreaOperation("zoneAdministration", true);
    public final static WorkAreaOperation ADD_GUEST_ACCESS = new WorkAreaOperation("addGuestAccess", true);
    public final static WorkAreaOperation TOKEN_REQUEST = new WorkAreaOperation("tokenRequest", true);
    public final static WorkAreaOperation ONLY_SEE_GROUP_MEMBERS = new WorkAreaOperation("onlySeeGroupMembers", true);
    public final static WorkAreaOperation OVERRIDE_ONLY_SEE_GROUP_MEMBERS = new WorkAreaOperation("overrideOnlySeeGroupMembers", true);
    public final static WorkAreaOperation MANAGE_RESOURCE_DRIVERS = new WorkAreaOperation("manageResourceDrivers", true);
    public final static WorkAreaOperation CREATE_FILESPACE = new WorkAreaOperation("createFilespace", true);
    public final static WorkAreaOperation ENABLE_SHARING_INTERNAL = new WorkAreaOperation("enableSharing", true);
    public final static WorkAreaOperation ENABLE_SHARING_EXTERNAL = new WorkAreaOperation("enableSharingExternal", true);    
    public final static WorkAreaOperation ENABLE_SHARING_PUBLIC = new WorkAreaOperation("enableSharingPublic", true);        
    public final static WorkAreaOperation ENABLE_SHARING_FORWARD = new WorkAreaOperation("enableSharingForward", true);
    public final static WorkAreaOperation ENABLE_SHARING_ALL_INTERNAL = new WorkAreaOperation("enableSharingAllInternal", true);
    public final static WorkAreaOperation ENABLE_SHARING_ALL_EXTERNAL = new WorkAreaOperation("enableSharingAllExternal", true);
    public final static WorkAreaOperation ENABLE_LINK_SHARING = new WorkAreaOperation("enableLinkSharing", true);

    // Default set of rights controlled by external ACLs.
	public static final WorkAreaOperation[] EXTERNALLY_CONTROLLED_RIGHTS_DEFAULT = new WorkAreaOperation[] {
		WorkAreaOperation.VIEW_BINDER_TITLE, 
		WorkAreaOperation.READ_ENTRIES, 
		WorkAreaOperation.CREATE_ENTRIES,
		WorkAreaOperation.MODIFY_ENTRIES,
		WorkAreaOperation.MODIFY_ENTRY_FIELDS,
		WorkAreaOperation.RENAME_ENTRIES,
		WorkAreaOperation.DELETE_ENTRIES,
		WorkAreaOperation.ADD_REPLIES,
		WorkAreaOperation.CREATE_ENTRY_ACLS,
		WorkAreaOperation.CHANGE_ACCESS_CONTROL,
		WorkAreaOperation.CREATE_WORKSPACES,
		WorkAreaOperation.CREATE_FOLDERS, 
		WorkAreaOperation.CREATOR_READ,
		WorkAreaOperation.CREATOR_MODIFY, 
		WorkAreaOperation.CREATOR_RENAME, 
		WorkAreaOperation.CREATOR_DELETE,
		WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS,
		WorkAreaOperation.BINDER_ADMINISTRATION,
		WorkAreaOperation.ADD_COMMUNITY_TAGS,
		WorkAreaOperation.GENERATE_REPORTS};
	
	// This list is immutable
	public static List<WorkAreaOperation> EXTERNALLY_CONTROLLED_RIGHTS_DEFAULT_AS_LIST = Collections.unmodifiableList(Arrays.asList(EXTERNALLY_CONTROLLED_RIGHTS_DEFAULT));
	
	public static List<WorkAreaOperation> getDefaultExternallyControlledRights() {
		return Arrays.asList(EXTERNALLY_CONTROLLED_RIGHTS_DEFAULT);
	}

    // Set of rights that allow granting of shares.
	public static final WorkAreaOperation[] ALLOW_SHARING_RIGHTS = new WorkAreaOperation[] {
	    WorkAreaOperation.ALLOW_SHARING_INTERNAL,
	    WorkAreaOperation.ALLOW_SHARING_EXTERNAL,
	    WorkAreaOperation.ALLOW_SHARING_PUBLIC,
	    WorkAreaOperation.ALLOW_SHARING_FORWARD,
	    WorkAreaOperation.ALLOW_SHARING_PUBLIC_LINKS,
	    WorkAreaOperation.ALLOW_FOLDER_SHARING_INTERNAL,
	    WorkAreaOperation.ALLOW_FOLDER_SHARING_EXTERNAL,
	    WorkAreaOperation.ALLOW_FOLDER_SHARING_PUBLIC,
	    WorkAreaOperation.ALLOW_FOLDER_SHARING_FORWARD };

	/**
	 * Returns a List<WorkAreaOperation> containing the various 'Allow'
	 * sharing rights.
	 * 
	 * @return
	 */
	public static List<WorkAreaOperation> getAllowSharingRights() {
		return Arrays.asList(ALLOW_SHARING_RIGHTS);
	}

	/**
	 * Returns true if the given WorkAreaOperation is an 'Allow'
	 * sharing right and false otherwise.
	 * 
	 * @param waOp
	 * 
	 * @return
	 */
	public static boolean isAllowSharingRight(WorkAreaOperation waOp) {
		return (getAllowSharingRights().contains(waOp));
	}

    private String name;
    private boolean zoneWide=false;
    private WorkAreaOperation(String name) {
	    this.name = name;
    	if (checkIfOperationValid(name)) {
		    Instances.put(name, this);
    	}
    }
    
    private WorkAreaOperation(String name, boolean zoneWide) {
        this.name = name;
        this.zoneWide = zoneWide;
        if (checkIfOperationValid(name)) {
        	Instances.put(name, this);
        }
    }
    
    private boolean checkIfOperationValid(String name) {
    	if ("viewBinderTitle".equals(name) && 
    			!SPropsUtil.getBoolean("accessControl.viewBinderTitle.enabled", false)) {
    		return false;
    	}
    	if (!Utils.checkIfFilr() && !SPropsUtil.getBoolean("keepFilrRolesAndRightsInVibe", false)) {
    		//Remove the Filr specific rights if not Filr (unless a developer wants them preserved)
        	if ("allowAccessNetFolder".equals(name)) {
        		return false;
        	}
    	}
    	return true;
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
    
    @Override
	public boolean equals(Object obj) {
        if(obj instanceof WorkAreaOperation)
            return this.name.equals(((WorkAreaOperation) obj).name) && (this.zoneWide == ((WorkAreaOperation)obj).zoneWide);
        else 
            return false;
    }
    
    @Override
	public int hashCode() {
        return 31*Boolean.valueOf(zoneWide).hashCode() + this.name.hashCode();
    }
    
    @Override
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
		protected Boolean renameEntries = Boolean.FALSE;
		protected Boolean deleteEntries = Boolean.FALSE;
		protected Boolean readEntries = Boolean.FALSE;
		protected Boolean addReplies = Boolean.FALSE;
		protected Boolean generateReports = Boolean.FALSE;
		protected Boolean downloadFolderAsCsv = Boolean.FALSE;
		protected Boolean binderAdministration = Boolean.FALSE;
		protected Boolean createEntryAcls = Boolean.FALSE;
		protected Boolean changeAccessControl = Boolean.FALSE;
		protected Boolean createWorkspaces = Boolean.FALSE;
		protected Boolean createFolders = Boolean.FALSE;
		protected Boolean manageEntryDefinitions = Boolean.FALSE;
		protected Boolean manageWorkflowDefinitions = Boolean.FALSE;
		protected Boolean creatorReadEntries = Boolean.FALSE;
		protected Boolean creatorModifyEntries = Boolean.FALSE;
		protected Boolean creatorRenameEntries = Boolean.FALSE;
		protected Boolean creatorDeleteEntries = Boolean.FALSE;
		protected Boolean ownerCreateEntryAcls = Boolean.FALSE;
		protected Boolean addTags = Boolean.FALSE;
		protected Boolean viewBinderTitle = Boolean.FALSE;
		protected Boolean allowSharing = Boolean.FALSE;
		protected Boolean allowSharingExternal = Boolean.FALSE;
		protected Boolean allowSharingPublic = Boolean.FALSE;
		protected Boolean allowSharingForward = Boolean.FALSE;
		protected Boolean allowAccessNetFolder = Boolean.FALSE;
		protected Boolean allowSharingPublicLinks = Boolean.FALSE;
		protected Boolean allowFolderSharingInternal = Boolean.FALSE;
		protected Boolean allowFolderSharingExternal = Boolean.FALSE;
		protected Boolean allowFolderSharingPublic = Boolean.FALSE;
		protected Boolean allowFolderSharingForward = Boolean.FALSE;

        public static RightSet and(RightSet set1, RightSet set2) {
            RightSet andSet = new RightSet();
            andSet.createEntries = andRights(set1.createEntries, set2.createEntries);
            andSet.modifyEntries = andRights(set1.modifyEntries, set2.modifyEntries);
            andSet.modifyEntryFields = andRights(set1.modifyEntryFields, set2.modifyEntryFields);
            andSet.deleteEntries = andRights(set1.deleteEntries, set2.deleteEntries);
            andSet.readEntries = andRights(set1.readEntries, set2.readEntries);
            andSet.addReplies = andRights(set1.addReplies, set2.addReplies);
            andSet.generateReports = andRights(set1.generateReports, set2.generateReports);
            andSet.downloadFolderAsCsv = andRights(set1.downloadFolderAsCsv, set2.downloadFolderAsCsv);
            andSet.binderAdministration = andRights(set1.binderAdministration, set2.binderAdministration);
            andSet.createEntryAcls = andRights(set1.createEntryAcls, set2.createEntryAcls);
            andSet.changeAccessControl = andRights(set1.changeAccessControl, set2.changeAccessControl);
            andSet.createWorkspaces = andRights(set1.createWorkspaces, set2.createWorkspaces);
            andSet.createFolders = andRights(set1.createFolders, set2.createFolders);
            andSet.manageEntryDefinitions = andRights(set1.manageEntryDefinitions, set2.manageEntryDefinitions);
            andSet.manageWorkflowDefinitions = andRights(set1.manageWorkflowDefinitions, set2.manageWorkflowDefinitions);
            andSet.creatorReadEntries = andRights(set1.creatorReadEntries, set2.creatorReadEntries);
            andSet.creatorModifyEntries = andRights(set1.creatorModifyEntries, set2.creatorModifyEntries);
            andSet.creatorDeleteEntries = andRights(set1.creatorDeleteEntries, set2.creatorDeleteEntries);
            andSet.ownerCreateEntryAcls = andRights(set1.ownerCreateEntryAcls, set2.ownerCreateEntryAcls);
            andSet.addTags = andRights(set1.addTags, set2.addTags);
            andSet.viewBinderTitle = andRights(set1.viewBinderTitle, set2.viewBinderTitle);
            andSet.allowSharing = andRights(set1.allowSharing, set2.allowSharing);
            andSet.allowSharingExternal = andRights(set1.allowSharingExternal, set2.allowSharingExternal);
            andSet.allowSharingPublic = andRights(set1.allowSharingPublic, set2.allowSharingPublic);
            andSet.allowSharingForward = andRights(set1.allowSharingForward, set2.allowSharingForward);
            andSet.allowAccessNetFolder = andRights(set1.allowAccessNetFolder, set2.allowAccessNetFolder);
            andSet.allowSharingPublicLinks = andRights(set1.allowSharingPublicLinks, set2.allowSharingPublicLinks);
            andSet.allowFolderSharingInternal = andRights(set1.allowFolderSharingInternal, set2.allowFolderSharingInternal);
            andSet.allowFolderSharingExternal = andRights(set1.allowFolderSharingExternal, set2.allowFolderSharingExternal);
            andSet.allowFolderSharingPublic = andRights(set1.allowFolderSharingPublic, set2.allowFolderSharingPublic);
            andSet.allowFolderSharingForward = andRights(set1.allowFolderSharingForward, set2.allowFolderSharingForward);
            return andSet;
        }
		
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
			result = 29 * result + (Boolean.TRUE.equals(downloadFolderAsCsv) ? 1231 : 1237);
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
			result = 29 * result + (Boolean.TRUE.equals(allowSharingExternal) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowSharingPublic) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowSharingForward) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowAccessNetFolder) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowSharingPublicLinks) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowFolderSharingInternal) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowFolderSharingExternal) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowFolderSharingPublic) ? 1231 : 1237);
			result = 29 * result + (Boolean.TRUE.equals(allowFolderSharingForward) ? 1231 : 1237);
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
			if(!equalRights(this.downloadFolderAsCsv, that.downloadFolderAsCsv)) return false;
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
			if(!equalRights(this.allowSharingExternal, that.allowSharingExternal)) return false;
			if(!equalRights(this.allowSharingPublic, that.allowSharingPublic)) return false;
			if(!equalRights(this.allowSharingForward, that.allowSharingForward)) return false;
			if(!equalRights(this.allowAccessNetFolder, that.allowAccessNetFolder)) return false;
			if(!equalRights(this.allowSharingPublicLinks, that.allowSharingPublicLinks)) return false;
			if(!equalRights(this.allowFolderSharingInternal, that.allowFolderSharingInternal)) return false;
			if(!equalRights(this.allowFolderSharingExternal, that.allowFolderSharingExternal)) return false;
			if(!equalRights(this.allowFolderSharingPublic, that.allowFolderSharingPublic)) return false;
			if(!equalRights(this.allowFolderSharingForward, that.allowFolderSharingForward)) return false;
			return true;
		}

	    public boolean greaterOrEqual(Object obj) {
			if(this==obj) return true;
			if(obj == null) return false;
			if(!(obj instanceof RightSet)) return false;
			RightSet that = (RightSet) obj;
			if(!greaterOrEqualRights(this.createEntries, that.createEntries)) return false;
			if(!greaterOrEqualRights(this.modifyEntries, that.modifyEntries)) return false;
			if(!greaterOrEqualRights(this.modifyEntryFields, that.modifyEntryFields)) return false;
			if(!greaterOrEqualRights(this.deleteEntries, that.deleteEntries)) return false;
			if(!greaterOrEqualRights(this.readEntries, that.readEntries)) return false;
			if(!greaterOrEqualRights(this.addReplies, that.addReplies)) return false;
			if(!greaterOrEqualRights(this.generateReports, that.generateReports)) return false;
			if(!greaterOrEqualRights(this.downloadFolderAsCsv, that.downloadFolderAsCsv)) return false;
			if(!greaterOrEqualRights(this.binderAdministration, that.binderAdministration)) return false;
			if(!greaterOrEqualRights(this.createEntryAcls, that.createEntryAcls)) return false;
			if(!greaterOrEqualRights(this.changeAccessControl, that.changeAccessControl)) return false;
			if(!greaterOrEqualRights(this.createWorkspaces, that.createWorkspaces)) return false;
			if(!greaterOrEqualRights(this.createFolders, that.createFolders)) return false;
			if(!greaterOrEqualRights(this.manageEntryDefinitions, that.manageEntryDefinitions)) return false;
			if(!greaterOrEqualRights(this.manageWorkflowDefinitions, that.manageWorkflowDefinitions)) return false;
			if(!greaterOrEqualRights(this.creatorReadEntries, that.creatorReadEntries)) return false;
			if(!greaterOrEqualRights(this.creatorModifyEntries, that.creatorModifyEntries)) return false;
			if(!greaterOrEqualRights(this.creatorDeleteEntries, that.creatorDeleteEntries)) return false;
			if(!greaterOrEqualRights(this.ownerCreateEntryAcls, that.ownerCreateEntryAcls)) return false;
			if(!greaterOrEqualRights(this.addTags, that.addTags)) return false;
			if(!greaterOrEqualRights(this.viewBinderTitle, that.viewBinderTitle)) return false;
			if(!greaterOrEqualRights(this.allowSharing, that.allowSharing)) return false;
			if(!greaterOrEqualRights(this.allowSharingExternal, that.allowSharingExternal)) return false;
			if(!greaterOrEqualRights(this.allowSharingPublic, that.allowSharingPublic)) return false;
			if(!greaterOrEqualRights(this.allowSharingForward, that.allowSharingForward)) return false;
			if(!greaterOrEqualRights(this.allowAccessNetFolder, that.allowAccessNetFolder)) return false;
			if(!greaterOrEqualRights(this.allowSharingPublicLinks, that.allowSharingPublicLinks)) return false;
			if(!greaterOrEqualRights(this.allowFolderSharingInternal, that.allowFolderSharingInternal)) return false;
			if(!greaterOrEqualRights(this.allowFolderSharingExternal, that.allowFolderSharingExternal)) return false;
			if(!greaterOrEqualRights(this.allowFolderSharingPublic, that.allowFolderSharingPublic)) return false;
			if(!greaterOrEqualRights(this.allowFolderSharingForward, that.allowFolderSharingForward)) return false;
			return true;
		}

	    public List<WorkAreaOperation> getRights() {
	    	List<WorkAreaOperation> rights = new ArrayList<WorkAreaOperation>();
			if(this.createEntries) rights.add(WorkAreaOperation.CREATE_ENTRIES);
			if(this.modifyEntries) rights.add(WorkAreaOperation.MODIFY_ENTRIES);
			if(this.modifyEntryFields) rights.add(WorkAreaOperation.MODIFY_ENTRY_FIELDS);
			if(this.renameEntries) rights.add(WorkAreaOperation.RENAME_ENTRIES);
			if(this.deleteEntries) rights.add(WorkAreaOperation.DELETE_ENTRIES);
			if(this.readEntries) rights.add(WorkAreaOperation.READ_ENTRIES);
			if(this.addReplies) rights.add(WorkAreaOperation.ADD_REPLIES);
			if(this.generateReports) rights.add(WorkAreaOperation.GENERATE_REPORTS);
			if(this.downloadFolderAsCsv) rights.add(WorkAreaOperation.DOWNLOAD_FOLDER_AS_CSV);
			if(this.binderAdministration) rights.add(WorkAreaOperation.BINDER_ADMINISTRATION);
			if(this.createEntryAcls) rights.add(WorkAreaOperation.CREATE_ENTRY_ACLS);
			if(this.changeAccessControl) rights.add(WorkAreaOperation.CHANGE_ACCESS_CONTROL);
			if(this.createWorkspaces) rights.add(WorkAreaOperation.CREATE_WORKSPACES);
			if(this.createFolders) rights.add(WorkAreaOperation.CREATE_FOLDERS);
			if(this.manageEntryDefinitions) rights.add(WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);
			if(this.manageWorkflowDefinitions) rights.add(WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);
			if(this.creatorReadEntries) rights.add(WorkAreaOperation.CREATOR_READ);
			if(this.creatorModifyEntries) rights.add(WorkAreaOperation.CREATOR_MODIFY);
			if(this.creatorRenameEntries) rights.add(WorkAreaOperation.CREATOR_RENAME);
			if(this.creatorDeleteEntries) rights.add(WorkAreaOperation.CREATOR_DELETE);
			if(this.ownerCreateEntryAcls) rights.add(WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS);
			if(this.addTags) rights.add(WorkAreaOperation.ADD_COMMUNITY_TAGS);
			if(this.viewBinderTitle) rights.add(WorkAreaOperation.VIEW_BINDER_TITLE);
			if(this.allowSharing) rights.add(WorkAreaOperation.ALLOW_SHARING_INTERNAL);
			if(this.allowSharingExternal) rights.add(WorkAreaOperation.ALLOW_SHARING_EXTERNAL);
			if(this.allowSharingPublic) rights.add(WorkAreaOperation.ALLOW_SHARING_PUBLIC);
			if(this.allowSharingForward) rights.add(WorkAreaOperation.ALLOW_SHARING_FORWARD);
			if(this.allowAccessNetFolder) rights.add(WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);
			if(this.allowSharingPublicLinks) rights.add(WorkAreaOperation.ALLOW_SHARING_PUBLIC_LINKS);
			if(this.allowFolderSharingInternal) rights.add(WorkAreaOperation.ALLOW_FOLDER_SHARING_INTERNAL);
			if(this.allowFolderSharingExternal) rights.add(WorkAreaOperation.ALLOW_FOLDER_SHARING_EXTERNAL);
			if(this.allowFolderSharingPublic) rights.add(WorkAreaOperation.ALLOW_FOLDER_SHARING_PUBLIC);
			if(this.allowFolderSharingForward) rights.add(WorkAreaOperation.ALLOW_FOLDER_SHARING_FORWARD);
			return rights;
		}

	    //Routine to translate from WorkAreaOperations to the equivalent FolderOperation
	    public List<FolderOperation> getFolderEntryRights() {
	    	List<FolderOperation> rights = new ArrayList<FolderOperation>();
			if(this.createEntries) rights.add(FolderOperation.addEntry);
			if(this.modifyEntries) rights.add(FolderOperation.modifyEntry);
			if(this.modifyEntryFields) rights.add(FolderOperation.modifyEntryFields);
			if(this.renameEntries) rights.add(FolderOperation.renameEntry);
			if(this.deleteEntries) rights.add(FolderOperation.deleteEntry);
			if(this.readEntries) rights.add(FolderOperation.readEntry);
			if(this.addReplies) rights.add(FolderOperation.addReply);
			if(this.generateReports) rights.add(FolderOperation.report);
			if(this.downloadFolderAsCsv) rights.add(FolderOperation.downloadFolderAsCsv);
			if(this.binderAdministration) rights.add(FolderOperation.changeACL);
			if(this.createEntryAcls) rights.add(FolderOperation.changeACL);
			if(this.changeAccessControl) rights.add(FolderOperation.changeACL);
			if(this.createWorkspaces) rights.add(null);
			if(this.createFolders) rights.add(null);
			if(this.manageEntryDefinitions) rights.add(null);
			if(this.manageWorkflowDefinitions) rights.add(null);
			if(this.creatorReadEntries) rights.add(FolderOperation.readEntry);
			if(this.creatorModifyEntries) rights.add(FolderOperation.modifyEntry);
			if(this.creatorRenameEntries) rights.add(FolderOperation.renameEntry);
			if(this.creatorDeleteEntries) rights.add(FolderOperation.deleteEntry);
			if(this.ownerCreateEntryAcls) rights.add(FolderOperation.setEntryAcl);
			if(this.addTags) rights.add(FolderOperation.manageTag);
			if(this.viewBinderTitle) rights.add(FolderOperation.readEntry);
			if(this.allowSharing) rights.add(FolderOperation.allowSharing);
			if(this.allowSharingExternal) rights.add(FolderOperation.allowSharingExternal);
			if(this.allowSharingPublic) rights.add(FolderOperation.allowSharingPublic);
			if(this.allowSharingForward) rights.add(FolderOperation.allowSharingForward);
			if(this.allowAccessNetFolder) rights.add(FolderOperation.readEntry);
			if(this.allowSharingPublicLinks) rights.add(FolderOperation.allowSharingPublicLinks);
			return rights;
		}

	    //Routine to translate from WorkAreaOperations to the equivalent BinderOperation
	    public List getFolderRights() {
	    	List rights = new ArrayList();
			if(this.createEntries) rights.add(WorkAreaOperation.CREATE_ENTRIES);
			if(this.modifyEntries) rights.add(WorkAreaOperation.MODIFY_ENTRIES);
			if(this.modifyEntryFields) rights.add(WorkAreaOperation.MODIFY_ENTRY_FIELDS);
			if(this.renameEntries) rights.add(WorkAreaOperation.RENAME_ENTRIES);
			if(this.deleteEntries) rights.add(WorkAreaOperation.DELETE_ENTRIES);
			if(this.readEntries) rights.add(BinderOperation.readEntries);
			if(this.addReplies) rights.add(WorkAreaOperation.ADD_REPLIES);
			if(this.generateReports) rights.add(BinderOperation.report);
			if(this.downloadFolderAsCsv) rights.add(BinderOperation.downloadFolderAsCsv);
			if(this.binderAdministration) rights.add(WorkAreaOperation.BINDER_ADMINISTRATION);
			if(this.createEntryAcls) rights.add(BinderOperation.changeACL);
			if(this.changeAccessControl) rights.add(BinderOperation.changeACL);
			if(this.createWorkspaces) rights.add(BinderOperation.addWorkspace);
			if(this.createFolders) rights.add(BinderOperation.addFolder);
			if(this.manageEntryDefinitions) rights.add(WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);
			if(this.manageWorkflowDefinitions) rights.add(WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);
			if(this.creatorReadEntries) rights.add(WorkAreaOperation.CREATOR_READ);
			if(this.creatorModifyEntries) rights.add(WorkAreaOperation.CREATOR_MODIFY);
			if(this.creatorRenameEntries) rights.add(WorkAreaOperation.CREATOR_RENAME);
			if(this.creatorDeleteEntries) rights.add(WorkAreaOperation.CREATOR_DELETE);
			if(this.ownerCreateEntryAcls) rights.add(WorkAreaOperation.CREATE_ENTRY_ACLS);
			if(this.addTags) rights.add(BinderOperation.manageTag);
			if(this.viewBinderTitle) rights.add(BinderOperation.viewBinderTitle);
			if(this.allowSharing) rights.add(BinderOperation.allowSharing);
			if(this.allowSharingExternal) rights.add(BinderOperation.allowSharingExternal);
			if(this.allowSharingPublic) rights.add(BinderOperation.allowSharingPublic);
			if(this.allowSharingForward) rights.add(BinderOperation.allowSharingForward);
			if(this.allowAccessNetFolder) rights.add(BinderOperation.allowAccessNetFolder);
			if(this.allowSharingPublicLinks) rights.add(BinderOperation.allowSharingPublicLinks);
			if(this.allowFolderSharingInternal) rights.add(BinderOperation.allowSharing);
			if(this.allowFolderSharingExternal) rights.add(BinderOperation.allowSharingExternal);
			if(this.allowFolderSharingPublic) rights.add(BinderOperation.allowSharingPublic);
			if(this.allowFolderSharingForward) rights.add(BinderOperation.allowSharingForward);
			return rights;
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
				return ((Boolean) f.get(this)).booleanValue();
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

		public boolean isDownloadFolderAsCsv() {
			if(downloadFolderAsCsv == null) return false;
			return downloadFolderAsCsv;
		}

		public void setDownloadFolderAsCsv(boolean downloadFolderAsCsv) {
			this.downloadFolderAsCsv = downloadFolderAsCsv;
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
		
		public boolean isAllowSharingExternal() {
			if(allowSharingExternal == null) return false;
			return allowSharingExternal;
		}

		public void setAllowSharingExternal(boolean allowSharingExternal) {
			this.allowSharingExternal = allowSharingExternal;
		}
		
		public boolean isAllowSharingPublic() {
			if(allowSharingPublic == null) return false;
			return allowSharingPublic;
		}

		public void setAllowSharingPublic(boolean allowSharingPublic) {
			this.allowSharingPublic = allowSharingPublic;
		}
		
		public boolean isAllowSharingForward() {
			if(allowSharingForward == null) return false;
			return allowSharingForward;
		}

		public void setAllowSharingForward(boolean allowSharingForward) {
			this.allowSharingForward = allowSharingForward;
		}
		
		public boolean isAllowAccessNetFolder() {
			if(allowAccessNetFolder == null) return false;
			return allowAccessNetFolder;
		}

		public void setAllowAccessNetFolder(boolean allowAccessNetFolder) {
			this.allowAccessNetFolder = allowAccessNetFolder;
		}
		
		public boolean isAllowSharingPublicLinks() {
			if(allowSharingPublicLinks == null) return false;
			return allowSharingPublicLinks;
		}

		public void setAllowSharingPublicLinks(boolean allowSharingPublicLinks) {
			this.allowSharingPublicLinks = allowSharingPublicLinks;
		}
		
		public boolean isAllowFolderSharingInternal() {
			if(allowFolderSharingInternal == null) return false;
			return allowFolderSharingInternal;
		}

		public void setAllowFolderSharingInternal(boolean allowFolderSharingInternal) {
			this.allowFolderSharingInternal = allowFolderSharingInternal;
		}
		
		public boolean isAllowFolderSharingExternal() {
			if(allowFolderSharingExternal == null) return false;
			return allowFolderSharingExternal;
		}

		public void setAllowFolderSharingExternal(boolean allowFolderSharingExternal) {
			this.allowFolderSharingExternal = allowFolderSharingExternal;
		}
		
		public boolean isAllowFolderSharingPublic() {
			if(allowFolderSharingPublic == null) return false;
			return allowFolderSharingPublic;
		}

		public void setAllowFolderSharingPublic(boolean allowFolderSharingPublic) {
			this.allowFolderSharingPublic = allowFolderSharingPublic;
		}

		public boolean isAllowFolderSharingForward() {
			if(allowFolderSharingForward == null) return false;
			return allowFolderSharingForward;
		}

		public void setAllowFolderSharingForward(boolean allowFolderSharingForward) {
			this.allowFolderSharingForward = allowFolderSharingForward;
		}
		
		private boolean equalRights(Boolean right1, Boolean right2) {
			if(right1 == null)
				right1 = Boolean.FALSE;
			if(right2 == null)
				right2 = Boolean.FALSE;
			return right1.equals(right2);
		}
		
		private boolean greaterOrEqualRights(Boolean right1, Boolean right2) {
			if(right1 == null)
				right1 = Boolean.FALSE;
			if(right2 == null)
				right2 = Boolean.FALSE;
			return (right1.equals(right2) || right1);
		}
		
		private static boolean andRights(Boolean right1, Boolean right2) {
			return right1!=null && right1 && right1.equals(right2);
		}
	}
}
