
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 * Implement the hooks for the any key.  For each any type, there needs to be a field that
 * can server as a foreign key for association mapping.
 */
public class AnyOwner {
    protected Entry entry;
    protected String ownerType;
    protected Long ownerId;
	protected FolderEntry folderEntry;
	protected Principal principal;
   //keep as reference for user queries only 
    protected String owningFolderSortKey;
    public final static String PRINCIPAL="principal";
    public final static String FOLDERENTRY="doc";
    

    public AnyOwner() {		
	}
	public AnyOwner(Entry entry) {
		setEntry(entry);
	}
	public AnyOwner(Entry entry, boolean setForeignKey) {
		if (setForeignKey)
			setEntry(entry);
		else {
			setHEntry(entry);
			if (entry instanceof FolderEntry) {
				FolderEntry fEntry = (FolderEntry)entry;
				Folder f = fEntry.getParentFolder();
				if (f != null) {
					//This value is used to help narrow the results of sql reporting queries
					//You can use this to search a folder of sub-folder heirarchy
					owningFolderSortKey = f.getFolderHKey().getSortKey();
				}
			}
  		}
	}
	public static String getType(Entry entry) {
		if (entry instanceof FolderEntry) {
			return FOLDERENTRY;
		} else if (entry instanceof Principal) {
			return PRINCIPAL;
		}
		return null;
		
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
    * @hibernate.any meta-type="string" id-type="java.lang.Long"
    * @hibernate.any-column name="ownerType" length="16"
    * @hibernate.any-column name="ownerId"
    * @hibernate.meta-value value="doc" class="com.sitescape.ef.domain.FolderEntry"		
	* @hibernate.meta-value value="principal" class="com.sitescape.ef.domain.Principal"
	* @hibernate.meta-value value="principal" class="com.sitescape.ef.domain.Group"
	* @hibernate.meta-value value="principal" class="com.sitescape.ef.domain.User"
	*/ 
   protected Entry getHEntry() {
       return entry;
   }
   protected void setHEntry(Entry entry) {
       this.entry = entry;
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
   public Entry getEntry() {
   		return getHEntry();
   }
   public void setEntry(Entry entry) {
   		setHEntry(entry);
   		if (entry == null) {
   			folderEntry = null;
   			principal = null;
   			owningFolderSortKey = null;
   		} else {
   			if (entry instanceof Principal) {
   				principal=(Principal)entry;
   				folderEntry=null;
   			} else {
   				principal=null;
   				folderEntry=(FolderEntry)entry;    	
   			}
   			if (entry instanceof FolderEntry) {
   				FolderEntry fEntry = (FolderEntry)entry;
   				Folder f = fEntry.getParentFolder();
   				//This value is used to help narrow the results of sql reporting queries
   				//You can use this to search a folder of sub-folder heirarchy
   				owningFolderSortKey = f.getFolderHKey().getSortKey();
   			}
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
   		if (entry.equals(o.getEntry()))
   			return true;
            
   		return false;
   }
   public int hashCode() {
   		return entry.hashCode();
   }   
}
