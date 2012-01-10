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


/**
 * 
 * Implement the hooks for the any key.  The any key allows shared tables for common associations between
 * <code>Binders, Principals, and FolderEntry</code>.  For each any type, there needs to be a field that
 * can serve as a foreign key for association mapping.  
 * 
 * This class is closely related to EntityIdentifier.  New types
 * must be accounted for here and in any hibernate mapping files that reference this class.
 */
public class AnyOwner {
    protected DefinableEntity entity;
    protected String ownerType;
    protected Long ownerId;
	protected FolderEntry folderEntry;
	protected Principal principal;
	protected Binder binder;
   //keep as reference for user queries that search sub-trees 
    protected String owningBinderKey;   
    //optimization to delete associations for entries in a binder, but not associations of the binder itself
    protected Long owningBinderId;
    /**
     * This should be used only by hibernate
     *
     */
    protected AnyOwner() {		
	}
    /**
     * Setup entity key
     * @param entity
     */
	public AnyOwner(DefinableEntity entity) {
		setBinderKey(entity);
		setup(entity);
 	}
	/**
	 * Setup entity key.  
	 * @param entity
	 * @param setForeignKey False if this object is not a member of the first level association with entry.  For example
	 * 		VersionAttachments are not, but FileAttachments are.
	 */
	public AnyOwner(DefinableEntity entity, boolean setForeignKey) {
		setBinderKey(entity);
		if (setForeignKey)
			setup(entity);
		else {
			setEntity(entity);
  		}
	}
	/**
	 * Setup foreign key mappings for lookups by respective classes
	 * @param entity
	 */
	private void setup(DefinableEntity entity) {
		setEntity(entity);
		if (entity instanceof FolderEntry) {
   			folderEntry = (FolderEntry)entity;
    		} else if (entity instanceof Principal) {
   			principal=(Principal)entity;
  		} else if (entity instanceof Binder) {
   			binder = (Binder)entity;
  		}
	}
	private void setBinderKey(DefinableEntity entity) {
		if (entity instanceof Binder) {
			setOwningBinderId(((Binder)entity).getId());
			setOwningBinderKey(((Binder)entity).getBinderKey().getSortKey());
		} else { 
			setOwningBinderId(((Entry)entity).getParentBinder().getId());
			setOwningBinderKey(((Entry)entity).getParentBinder().getBinderKey().getSortKey());
		}
	}
	/**
	 * This field servers as a key into the database for quick bulk deletes.
	 * @hibernate.property
	 */
	protected Long getOwningBinderId() {
		return owningBinderId;
	}
	/**
	 * Hibernate accessor
	 */
	protected void setOwningBinderId(Long owningBinderId) {
		this.owningBinderId = owningBinderId;
	}
    /*
    * These fields are for foreign key mapping.  An <any> field cannot be
    * mapped as a foreign key to multiple tables.  Associations from the owner class,
    * attempt to do this.
    */
	/**
	 * @hibernate.many-to-one
	 */
	protected FolderEntry getFolderEntry() {
		return folderEntry;
	}
	/**
	 * Hibernate accessor
	 */
	protected void setFolderEntry(FolderEntry folderEntry) {
		this.folderEntry = folderEntry;
	}
	/**
	 * @hibernate.many-to-one
	 */
	protected Principal getPrincipal() {
		return principal;
	}
	/**
	 * Hibernate accessor
	 */
	protected void setPrincipal(Principal principal) {
		this.principal = principal;
	}
	/**
	 * @hibernate.many-to-one
	 */
	protected Binder getBinder() {
		return binder;
	}
	/**
	 * Hibernate accessor
	 */
	protected void setBinder(Binder binder) {
		this.binder = binder;
	}
   /**
    * @hibernate.any meta-type="string" id-type="java.lang.Long"
    * @hibernate.any-column name="ownerType" length="16"
    * @hibernate.any-column name="ownerId"
    * @hibernate.meta-value value="folderEntry" class="org.kablink.teaming.domain.FolderEntry"		
	* @hibernate.meta-value value="user" class="org.kablink.teaming.domain.User"
	* @hibernate.meta-value value="group" class="org.kablink.teaming.domain.Group"
	* @hibernate.meta-value value="profileBinder" class="org.kablink.teaming.domain.ProfileBinder"
	* @hibernate.meta-value value="workspace" class="org.kablink.teaming.domain.ProfileBinder"
	* @hibernate.meta-value value="folder" class="org.kablink.teaming.domain.Folder"
	*/ 
   public DefinableEntity getEntity() {
       return entity;
   }
   /**
    * Hiberate accessor
    * @param entity
    */
   protected void setEntity(DefinableEntity entity) {
       this.entity = entity;
   }
   /**
    * @hibernate.property insert="false" update="false"
    * Used in queries
    */
   protected String getOwnerType() {
   	return ownerType;
   }
	/**
	 * Hibernate accessor
	 */
   protected void setOwnerType(String ownerType) {
   	this.ownerType = ownerType;
   }
   /**
    * @hibernate.property insert="false" update="false" 
    * Used in queries
    */
   protected Long getOwnerId() {
   	return ownerId;
   }
	/**
	 * Hibernate accessor
	 */
   protected void setOwnerId(Long ownerId) {
   	this.ownerId = ownerId;
   }
   /**
    * @hibernate.property length="255" 
    * @return
    */
   protected String getOwningBinderKey() {
       return owningBinderKey;
   }
   private void setOwningBinderKey(String owningBinderKey) {
       this.owningBinderKey = owningBinderKey;
   }  
   public boolean equals(Object obj) {
   		if(this == obj)
   			return true;

   		if ((obj == null) || (obj.getClass() != getClass()))
   			return false;
    
   		AnyOwner o = (AnyOwner) obj;
   		if (entity.equals(o.getEntity()))
   			return true;
            
   		return false;
   }

   public int hashCode() {
   		return 31*entity.hashCode() + ownerType.hashCode();
   }   
}
