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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    //Workarea operations
    //  Important: do not create operations that control the viewing of an entry of folder.
    //  Such "view" operations should be controlled by the ACL. (not implemented)
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
//    public final static WorkAreaOperation USER_SEE_COMMUNITY = new WorkAreaOperation("userSeeCommunity");
//    public final static WorkAreaOperation USER_SEE_ALL = new WorkAreaOperation("userSeeAll");

    //The following rights should not be used in access management of workareas.  Used to give access to zone-wide functions to a
    //group of users
    public final static WorkAreaOperation ZONE_ADMINISTRATION = new WorkAreaOperation("zoneAdministration", true);
    public final static WorkAreaOperation ADD_GUEST_ACCESS = new WorkAreaOperation("addGuestAccess", true);
    public final static WorkAreaOperation TOKEN_REQUEST = new WorkAreaOperation("tokenRequest", true);
    public final static WorkAreaOperation ONLY_SEE_GROUP_MEMBERS = new WorkAreaOperation("onlySeeGroupMembers", true);
    public final static WorkAreaOperation OVERRIDE_ONLY_SEE_GROUP_MEMBERS = new WorkAreaOperation("overrideOnlySeeGroupMembers", true);

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

}
