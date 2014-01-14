/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;

/**
 * This class is used to marker a class that is not a <code>Binder</code>
 */
@SuppressWarnings("unchecked")
public abstract class Entry extends DefinableEntity implements WorkArea {
 
    protected Boolean hasEntryAcl = Boolean.FALSE; // Used for internally-controlled ACLs only
    protected Boolean checkFolderAcl = Boolean.FALSE; // Used for internally-controlled ACLs only
    protected Boolean hasEntryExternalAcl = Boolean.FALSE; // Used for externally-controlled ACLs only
    protected String entryDefId; //initialized by hiberate access=field

    public Entry() {
    }

    public Entry(Entry entry) {
    	super(entry);
    	entryDefId = entry.entryDefId;
    }

    public String getEntryDefId() {
    	if (entryDefId != null) 
    		return entryDefId;
    	Definition def = getParentBinder().getDefaultEntryDef();
    	if(def != null)
    		return def.getId();
    	else
    		return null;
    }
    
     public void setEntryDefId(String definitionId) {
     	// This association is not maintained/managed by Hibernate but by application. 
     	// Consequently, there is no foreign key constraint defined on the table that
     	// enforces this association at the database level. Instead, it is up to the 
     	// application to maintain the referential integrity properly. A small price 
    	// to pay in exchange for improved efficiency.
     	this.entryDefId = definitionId;
     }
     
     public void setEntryDef(Definition entryDef) {
      	setEntryDefId((entryDef == null)? null : entryDef.getId());
     }
      
     public String getCreatedWithDefinitionId() {
     	// returns the original definition with which this entity was created. 
     	return entryDefId;
     }

    public boolean isTop() {
    	return true;
    }
	public boolean hasEntryAcl() {
		if (this instanceof FolderEntry && !((FolderEntry)this).isTop()) {
			//This is a reply to a folder entry. Check the top entry instead
			return ((FolderEntry)this).getTopEntry().hasEntryAcl();
		}
		if (hasEntryAcl == null) {
			return false;
		} else {
			return hasEntryAcl;
		}
	}
	public void setHasEntryAcl(boolean hasEntryAcl) {
		this.hasEntryAcl = hasEntryAcl;
	}
	public boolean isIncludeFolderAcl() {
		if (this instanceof FolderEntry && !((FolderEntry)this).isTop()) {
			//This is a reply to a folder entry. Check the top entry instead
			return ((FolderEntry)this).getTopEntry().isIncludeFolderAcl();
		}
		if (checkFolderAcl == null) return false;
		else return checkFolderAcl;
	}
	public void setCheckFolderAcl(boolean checkFolderAcl) {
		this.checkFolderAcl = checkFolderAcl;
	}
    
	public boolean hasEntryExternalAcl() {
		if (this instanceof FolderEntry && !((FolderEntry)this).isTop()) {
			//This is a reply to a folder entry. Check the top entry instead
			return ((FolderEntry)this).getTopEntry().hasEntryExternalAcl();
		}
		if (hasEntryExternalAcl == null) {
			return false;
		} else {
			return hasEntryExternalAcl;
		}
	}
	public void setHasEntryExternalAcl(boolean hasEntryExternalAcl) {
		this.hasEntryExternalAcl = hasEntryExternalAcl;
	}

	//*****************WorkArea interface stuff***********/
    public Long getWorkAreaId() {
        return getId();
    }
    public String getWorkAreaType() {
        return getEntityType().name();
    }
    public WorkArea getParentWorkArea() {
        //This is the top entry. There is no inheritance from the parent binder.
        return null;
    }
    public Set getChildWorkAreas() {
    	return new HashSet();
    }
    
    public boolean isAclExternallyControlled() {
    	return this.getParentBinder().isAclExternallyControlled();
    }
    public List<WorkAreaOperation> getExternallyControlledRights() {
    	return this.getParentBinder().getExternallyControlledRights();
    }
    public String getRegisteredRoleType() {
    	if (this.getParentBinder().getResourceDriver() instanceof AclResourceDriver) {
    		return ((AclResourceDriver)this.getParentBinder().getResourceDriver()).getRegisteredRoleTypeName();
    	}
    	return "";
    }
    
	/**
	 * @hibernate.property not-null="true"
	 * @return
	 */
    public boolean isFunctionMembershipInherited() {
    	return false;
    }
    public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
    }
     public boolean isFunctionMembershipInheritanceSupported() {
    	return false;
    }
     public boolean isExtFunctionMembershipInherited() {
     	return false;
     }
     public void setExtFunctionMembershipInherited(boolean extFunctionMembershipInherited) {
     }
     public Long getOwnerId() {
    	Principal owner = getOwner();
    	if (owner == null)	return null;
    	return owner.getId();
    }
     /**
      * Return the owner of the binder.
      * The owner default to the creator.
      * Used in access management.
      * @hibernate.many-to-one
      */
  	public Principal getOwner() {
 	   	HistoryStamp creation = getCreation();
     	if ((creation != null) && creation.getPrincipal() != null) {
     		return creation.getPrincipal();
     	}
     	return null;
 		
 	}
 	public void setOwner(Principal owner) {
 	}
     public boolean isTeamMembershipInherited() {
    	return false;   	
    }
    public void setTeamMembershipInherited(boolean teamMembershipInherited) {
    }
    /**
     * Return the team member ids
     * @return
     */
    public Set<Long> getTeamMemberIds() {
    	return new HashSet<Long>();
    	
    }
     public void setTeamMemberIds(Set<Long> memberIds) {
     }
     /*****************End WorkArea interface stuff***********/
}
