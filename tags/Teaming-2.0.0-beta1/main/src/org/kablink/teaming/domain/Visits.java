/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
/*
 * Created on July 12, 2005
 *
 * Keep track of entries viewed per user
 */
package org.kablink.teaming.domain;

import org.kablink.teaming.domain.UserEntityPK;

/**
 * @hibernate.class table="SS_Ratings"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Visits extends ZonedObject {
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
