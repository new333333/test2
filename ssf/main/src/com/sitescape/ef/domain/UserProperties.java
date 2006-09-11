/*
 * Created on July 12, 2005
 *
 * Keep track of entries viewed per user
 */
package com.sitescape.ef.domain;

import java.util.Map;
import java.util.HashMap;
import com.sitescape.ef.domain.UserPropertiesPK;

/**
 * @hibernate.class table="SS_UserProperties"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 * Properties are stored on a per/user per/folder basis.
 * A null folder is used to indicate general properties
 *
 */
public class UserProperties {
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
    	//only update if it changes to reduce writes
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
