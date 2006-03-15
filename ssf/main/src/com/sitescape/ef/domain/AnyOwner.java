
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 * Implement the hooks for the any key.  For each any type, there needs to be a field that
 * can server as a foreign key for association mapping.
 */
public class AnyOwner {
    protected DefinableEntity entity;
    protected String ownerType;
    protected Long ownerId;
	protected FolderEntry folderEntry;
	protected Principal principal;
	protected Binder binder;
   //keep as reference for user queries only 
    protected String owningFolderSortKey;
    public final static String PRINCIPAL="principal";
    public final static String FOLDERENTRY="doc";
    public final static String BINDER="binder";
    

    public AnyOwner() {		
	}
	public AnyOwner(DefinableEntity entity) {
		setEntity(entity);
	}
	public AnyOwner(DefinableEntity entity, boolean setForeignKey) {
		if (setForeignKey)
			setEntity(entity);
		else {
			setHEntry(entity);
			if (entity instanceof FolderEntry) {
				FolderEntry fEntry = (FolderEntry)entity;
				Folder f = fEntry.getParentFolder();
				if (f != null) {
					//This value is used to help narrow the results of sql reporting queries
					//You can use this to search a folder of sub-folder heirarchy
					owningFolderSortKey = f.getFolderHKey().getSortKey();
				}
			}
  		}
	}

   /**
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
	protected void setFolderEntry(FolderEntry folderEntry) {
		this.folderEntry = folderEntry;
	}
	/**
	 * @hibernate.many-to-one
	 */
	protected Principal getPrincipal() {
		return principal;
	}
	protected void setPrincipal(Principal principal) {
		this.principal = principal;
	}
	/**
	 * @hibernate.many-to-one
	 */
	protected Binder getBinder() {
		return binder;
	}
	protected void setBinder(Binder binder) {
		this.binder = binder;
	}
   /**
    * @hibernate.any meta-type="string" id-type="java.lang.Long"
    * @hibernate.any-column name="ownerType" length="16"
    * @hibernate.any-column name="ownerId"
    * @hibernate.meta-value value="doc" class="com.sitescape.ef.domain.FolderEntry"		
	* @hibernate.meta-value value="principal" class="com.sitescape.ef.domain.Principal"
	* @hibernate.meta-value value="binder" class="com.sitescape.ef.domain.Binder"
	*/ 
   protected DefinableEntity getHEntry() {
       return entity;
   }
   protected void setHEntry(DefinableEntity entity) {
       this.entity = entity;
   }
   /**
    * @hibernate.property insert="false" update="false"
    * Used in queries
    */
   protected String getOwnerType() {
   	return ownerType;
   }
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
   protected void setOwnerId(Long ownerId) {
   	this.ownerId = ownerId;
   }
   public DefinableEntity getEntity() {
   		return getHEntry();
   }
   public void setEntity(DefinableEntity entity) {
   		setHEntry(entity);
		folderEntry = null;
		principal = null;
		owningFolderSortKey = null;
		binder = null;

		String entryT = entity.getAnyOwnerType();
		if (FOLDERENTRY.equals(entryT)) {
   			folderEntry = (FolderEntry)entity;
   			//This value is used to help narrow the results of sql reporting queries
   			//You can use this to search a folder of sub-folder heirarchy
   			owningFolderSortKey = folderEntry.getParentFolder().getFolderHKey().getSortKey();  	
   		} else if (PRINCIPAL.equals(entryT)) {
   			principal=(Principal)entity;
   		} else if (BINDER.equals(entryT)) {
   			binder = (Binder)entity;
  		}
   }

   /**
    * @hibernate.property length="512" 
    * @return
    */
   private String getOwningFolderSortKey() {
       return owningFolderSortKey;
   }
   private void setOwningFolderSortKey(String owningFolderSortKey) {
       this.owningFolderSortKey = owningFolderSortKey;
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
