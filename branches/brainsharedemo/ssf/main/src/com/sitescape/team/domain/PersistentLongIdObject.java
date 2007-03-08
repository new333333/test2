/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.sitescape.team.util.Constants;

/**
 * Base class of objects with a Long id and hibernate version column
 */
public class PersistentLongIdObject implements PersistentLongId {
    private Long id;
    long lockVersion;
 
	/**
	 * @hibernate.id generator-class="native" type="long"  unsaved-value="null" 	 */    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
 
    /**
     * @hibernate.version type="long" column="lockVersion"
     */
    public long getLockVersion() {
        return this.lockVersion;
    }
    public void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    }
    public int hashCode() {
    	return id.hashCode();
    }
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        //objects can be proxied so don't compare classes.
        if (obj == null)
            return false;
      
        if (!(obj instanceof PersistentLongIdObject)) return false;
        PersistentLongIdObject o = (PersistentLongIdObject) obj;
        //assume object not persisted yet
        if (o.getId() == null) return false;
        if (getId() == null) return false;
        if (this.id.equals(o.getId()))
            return true;
                
        return false;
    }    
    public String toString() {
        return new ToStringBuilder(this)
            .append(Constants.ID, getId())
            .toString();
    }
 
}