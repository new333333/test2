/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.sitescape.ef.PropertyNames;

/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PersistentLongIdObject implements PersistentLongId {
    private Long id;
    long lockVersion;
    String stringId;

	/**
	 * @hibernate.id generator-class="native" type="long"  unsaved-value="null" node="@id"
	 */    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    /*
     * This was added to support liferays use of strings.
     * We just convert the id
     * @see com.sitescape.ef.domain.PersistentLongId#getStringId()
     */
    public String getStringId() {
    	if (id == null) return "";
    	if (stringId == null) stringId = id.toString();
    	return stringId;
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

        if ((obj == null) || (obj.getClass() != getClass()))
            return false;
      
        PersistentLongIdObject o = (PersistentLongIdObject) obj;
        if (this.id.equals(o.getId()))
            return true;
                
        return false;
    }    
    public String toString() {
        return new ToStringBuilder(this)
            .append(PropertyNames.ID, getId())
            .toString();
    }
 
}