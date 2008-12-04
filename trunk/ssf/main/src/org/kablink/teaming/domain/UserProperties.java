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

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import org.dom4j.Document;
import org.kablink.teaming.domain.UserPropertiesPK;


/**
 * @hibernate.class table="SS_UserProperties"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 * Properties are stored on a per/user per/folder basis.
 * A null folder is used to indicate general properties
 *
 */
public class UserProperties extends ZonedObject {
    private Map userProperties;
    private UserPropertiesPK id;

    protected UserProperties() {
    	//keep protected so only called by hibernate and map not null
    }
    public UserProperties(Long userId, Long folderId) {
    	setId(new UserPropertiesPK(userId, folderId));
    	setProperties(new HashMap());
    }
    public UserProperties(Long userId) {
       	setId(new UserPropertiesPK(userId));
    	setProperties(new HashMap());
    }
    public UserProperties(UserPropertiesPK key) {
       	setId(key);
    	setProperties(new HashMap());
    }
    /**
	* @hibernate.composite-id
	**/
	public UserPropertiesPK getId() {
		return id;
	}
	public void setId(UserPropertiesPK id) {
		this.id = id;
	}    
	/**
	 * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType" not-null="true"
     * @return
     */
    public Map getProperties() {
    	return userProperties;
    } 
    public void setProperties(Map userProperties) {
    	this.userProperties = userProperties;
    }
    
    public Object getProperty(String key) {
    	return userProperties.get(key);
    }    
    
    public void setProperty(String key, Object value) {
 	   if (value instanceof Object[]) throw new IllegalArgumentException("Arrays not supported");
 	   if (value instanceof Document) throw new IllegalArgumentException("XML docs not supported");
 	   if (value instanceof java.util.Collection) {
 		   //check the members
 		   for (Object obj:(Collection)value) {
 			   if (obj instanceof Object[]) throw new IllegalArgumentException("Arrays not supported");
 		 	   if (obj instanceof Document) throw new IllegalArgumentException("XML docs not supported");			   
 		   }
 	   }
 	   //only update if it changes; to reduce writes
    	if (userProperties.containsKey(key)) {
    		if (value == null) userProperties.remove(key);
    		else {
    			Object currentVal = userProperties.get(key);
    			if (!value.equals(currentVal)) userProperties.put(key, value);
    		}	
    	} else {
    		if (value != null) userProperties.put(key, value);
    	}
    }    
}
