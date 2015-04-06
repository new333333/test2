/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
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
import org.kablink.util.StringUtil;


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
    	if (userProperties == null) return null;
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
    	if (userProperties != null) {
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

    public String getStringProperty(String key) {
        Object value = getProperty(key);
        if (value!=null && value instanceof String) {
            return (String) value;
        }
        return null;
    }

    public String [] getStringArrayProperty(String key) {
        Object value = getProperty(key);
        if (value!=null && value instanceof String) {
            return StringUtil.unpack((String) value);
        }
        return null;
    }

    public Integer getIntegerProperty(String key) {
        Object value = getProperty(key);
        if (value!=null && value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        return null;
    }

    public Boolean getBooleanProperty(String key) {
        String value = getStringProperty(key);
        if (value!=null) {
            return Boolean.valueOf(value);
        }
        return null;
    }
}
