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
/*
 * Created on July 12, 2005
 *
 * Keep track of entries viewed per user
 */
package com.sitescape.team.domain;

import com.sitescape.team.domain.UserEntityPK;

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
    public Long getReadCount() {
    	return reads;
    } 
    public void setReadCount(Long reads) {
    	this.reads = reads;
    }
    public void setReadCount(long reads) {
    	this.reads = Long.valueOf(reads);
    }
    public void incrReadCount() {
    	if (reads == null) reads = Long.valueOf(1);
    	else reads = Long.valueOf(reads.longValue() + 1);
    }
  
}
