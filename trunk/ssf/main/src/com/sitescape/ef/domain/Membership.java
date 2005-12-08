
package com.sitescape.ef.domain;
import java.io.Serializable;

/**
 * This is a persistent class that is defined externally.
 * 
 * This is implemented as a class so we can do acl checking and group membership 
 * checking without loading in
 * the user and group records. The same table is referenced from
 * Principal.java and Group.java but in those cases the ids are mapped to real objects
 */
public class Membership implements Serializable {
    Long user_id;
    Long group_id;
    public Membership(){
    }
    public Membership(Long group, Long user) {
    	group_id = group;
		user_id = user;
    }
    /**
     * @hibernate.property
     * @hibernate.column name="user"
     * @return
     */
    public Long getUserId() {
        return this.user_id;
    }
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }
    /**
     * @hibernate.property
     * @hibernate.column name="group"
     * @return
     */
    public Long getGroupId() {
        return this.group_id;
    }
    public void setGroupId(Long group_id) {
        this.group_id = group_id;
    }    

    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || (obj.getClass() != getClass()))
            return false;
            
        Membership mem = (Membership) obj;
        if (!(user_id.equals(mem.getUserId()))) return false;
        if (!(group_id.equals(mem.getGroupId()))) return false;
        return true;
    }
    public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + user_id.hashCode();
    	hash = 31*hash + group_id.hashCode();
    	return hash;
    }
}
