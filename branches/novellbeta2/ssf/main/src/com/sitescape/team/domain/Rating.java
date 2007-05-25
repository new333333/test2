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
public class Rating {
    private UserEntityPK id;
    private Long rating = Long.valueOf(0);
    
    protected Rating() {
    	//keep protected so only called by hibernate 
    }
    public Rating(Long userId, EntityIdentifier entityId) {
    	setId(new UserEntityPK(userId, entityId));
    }
    public Rating(UserEntityPK key) {
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
    public Long getRating() {
    	return rating;
    } 
    public void setRating(Long rating) {
    	this.rating = rating;
    }
    public void setRating(long rating) {
    	this.rating = Long.valueOf(rating);
    }
  
}
