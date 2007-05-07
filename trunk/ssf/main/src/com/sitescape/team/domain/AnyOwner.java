/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package com.sitescape.team.domain;


/**
 * @author Janet McCann
 * Implement the hooks for the any key.  For each any type, there needs to be a field that
 * can serve as a foreign key for association mapping.
 * 
 * This class is closely related to EntityIdentifier.  New types
 * must be accounted for here and in any hibernate mapping files that user this.
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
    public AnyOwner() {		
	}
	public AnyOwner(DefinableEntity entity) {
		setBinderKey(entity);
		setup(entity);
 	}
	/**
	 * Setup entity
	 * @param entity
	 * @param setForeignKey
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
			setOwningBinderId(((Binder)binder).getId());
			setOwningBinderKey(((Binder)binder).getBinderKey().getSortKey());
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
    * @hibernate.meta-value value="folderEntry" class="com.sitescape.team.domain.FolderEntry"		
	* @hibernate.meta-value value="user" class="com.sitescape.team.domain.User"
	* @hibernate.meta-value value="group" class="com.sitescape.team.domain.Group"
	* @hibernate.meta-value value="profileBinder" class="com.sitescape.team.domain.ProfileBinder"
	* @hibernate.meta-value value="workspace" class="com.sitescape.team.domain.ProfileBinder"
	* @hibernate.meta-value value="folder" class="com.sitescape.team.domain.Folder"
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
   public String getOwningBinderKey() {
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
