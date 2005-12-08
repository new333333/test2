package com.sitescape.ef.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.sitescape.ef.util.Constants;
/**
 * @author Jong Kim
 *
 */
public class PersistentObject implements PersistentStringId {
    private String id;
    long lockVersion;

	/**
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null" node="@id"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    public void setId(String id) {
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
    public String toString() {
        return new ToStringBuilder(this)
            .append(Constants.ID, getId())
            .toString();
    }
    public int hashCode() {
    	return id.hashCode();
    }
    /**
     * Compares objects using the database Id.  This implies objects must be
     * persisted prior to making this call.
     */
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        //objects can be proxied so don't compare classes.  UUIDS are unique 
        if (obj == null) 
            return false;
        
        PersistentObject o = (PersistentObject) obj;
        //assume not persisted yet
        if (o.getId() == null) return false;
        if (getId() == null) return false;
        if (this.id.equals(o.getId()))
            return true;
                
        return false;
    }
}
