/*
 * Created on July 12, 2005
 *
 * Keep track of entries viewed per user
 */
package com.sitescape.ef.domain;

import com.sitescape.ef.domain.UserEntityPK;

/**
 * @hibernate.class table="SS_Ratings"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Visits {
    private UserEntityPK id;
    private Long reads = Long.valueOf(0);
    protected Visits() {
    	//keep protected so only called by hibernate 
    }
    public Visits(Long userId, EntityIdentifier entityId) {
    	setId(new UserEntityPK(userId, entityId));
    }
    public Visits(UserEntityPK key) {
       	setId(key);
     }

    /**
	* @hibernate.composite-id
	**/
	public UserEntityPK getId() {
		return id;
	}
	public void setId(UserEntityPK id) {
		this.id = id;
	}    
	/**
	 * @hibernate.property 
	 * @return
     */
    public Long getReads() {
    	return reads;
    } 
    public void setReads(Long reads) {
    	this.reads = reads;
    }
    public void setReads(long reads) {
    	this.reads = Long.valueOf(reads);
    }
    public void incrReads() {
    	if (reads == null) reads = Long.valueOf(1);
    	else reads = Long.valueOf(reads.longValue() + 1);
    }
  
}
