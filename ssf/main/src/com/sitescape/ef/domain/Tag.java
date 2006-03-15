package com.sitescape.ef.domain;


/**
 * 
 * @hibernate.class table="SS_Tags" dynamic-update="true" dynamic-insert="true" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Tag {
	protected String id;
	protected String name="";
	protected Long itemId;
	protected Long ownerId;
	protected String ownerType;
	protected boolean isPublic=true;
/*	protected ItemType itemType=null;
	public enum ItemType {
		none (0),
		folder (1), 
		workspace (2), 
		user (3), 
		group (4), 
		folderEntry (5);
		int dbValue;
		ItemType(int dbValue) {
			this.dbValue = dbValue;
		}
		public int getValue() {return dbValue;}
	};
*/
	protected int itemType;
	public static int NONE=0;
	public static int FOLDER=1;
	public static int WORKSPACE=2;
	public static int USER=3;
	public static int GROUP=4;
	public static int FOLDERENTRY=5;

	
	public Tag() {
	}
	/**
	 * Artificial database primary key
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
	/**
	 * The id of the item being tagged
	 * @hibernate.property
	 * @return
	 */
	public Long getItemId() {
	 	return itemId;
	}
	protected void setItemId(Long itemId) {
	 	this.itemId = itemId;
	}
	/**
	 * Internal hiberante accessor
	 * @hibernate.property
	 * @return
	 */
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
/*	protected int getDbType() {
		return itemType.getValue();
	}
	protected void setDbType(int dbType) {
		for (ItemType type : ItemType.values()) {
			if (dbType == type.getValue()) {
				itemType=type;
				break;
			}
		}
	}
	public ItemType getItemType() {
		return itemType;
	}
*/
	/**
	 * The type of entity that applied this tag.  Use AnyOwner types
	 * @hibernate.property length="16"
	 */
	public String getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	/**
	 * The id of the entity that applied this tag
	 * @hibernate.property 
	 */
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
	  	this.ownerId = ownerId;
	}	 		
	/**
	 * @hibernate.property length="64"
	 * @return
	 */
	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}	
	/**
	 * @hibernate.property
	 * @hibernate.column name="isPublic"
	 */
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
