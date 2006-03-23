package com.sitescape.ef.domain;


/**
 * 
 * @hibernate.class table="SS_Tags" dynamic-update="true" dynamic-insert="true" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Tag  {
	protected String id;
	protected String name="";
	protected EntityIdentifier ownerId;
	protected EntityIdentifier entityId;
	protected boolean isPublic=true;
	
	public Tag() {
	}
	
	public Tag(EntityIdentifier entityId) {
		this.entityId = entityId;
		
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
     * The entity the tag is marking
     * @hibernate.componenent
     * @return
     */
    public EntityIdentifier getEntityIdentifier() {
    	return entityId;
    }
    public void setEntityIdentifier(EntityIdentifier entityId) {
    	this.entityId = entityId;
    }

    /**
     * The Entity that owns the tag
     * @hibernate.componenent
     * @return
     */
    public EntityIdentifier getOwnerIdentifier() {
    	return ownerId;
    }
    public void setOwnerIdentifier(EntityIdentifier ownerId) {
    	this.ownerId = ownerId;
    }
 	public boolean isOwner(DefinableEntity entity) {
 		if (entity == null) return false;
 		if (entity.getEntityIdentifier().equals(entityId)) return true;
 		return false;
 	}
 	public boolean isOwner(EntityIdentifier entityId) {
 		if (entityId == null) return false;
 		if (this.entityId.equals(entityId)) return true;
 		return false;
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
