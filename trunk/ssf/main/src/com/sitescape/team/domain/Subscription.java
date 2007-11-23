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
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;


/**
 * @author Janet McCann
 * @hibernate.class table="SS_Subscriptions" dynamic-update="true" lazy="false" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * A subscription is similiar to a notification, except it is managed by a user.  
 * An individual chooses to be notified.  A notification is managed by the folder 
 * administrator.
 */
public class Subscription extends ZonedObject {
    public static final int DIGEST_STYLE_EMAIL_NOTIFICATION = 1;
    public static final int MESSAGE_STYLE_EMAIL_NOTIFICATION = 2;
    public static final int MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION = 3;
    public static final int DISABLE_ALL_NOTIFICATIONS=4;
    
    private int style=DIGEST_STYLE_EMAIL_NOTIFICATION;
    private UserEntityPK id;
	public Subscription() {
    	
    }
    public Subscription(Long userId, EntityIdentifier entityId) {
    	setId(new UserEntityPK(userId, entityId));
    }
    public Subscription(UserEntityPK key) {
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
    public int getStyle() {
        return style;
    }
    public void setStyle(int style) {
        this.style = style;
    }
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || (!(obj instanceof Subscription)))
            return false;
        
        Subscription o = (Subscription) obj;
        if (getId().equals(o.getId())) return true;
                
        return false;
    }
    public int hashCode() {
    	return getId().hashCode();
    }
  
}