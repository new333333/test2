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
 * Created on Dec 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
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
    public static final Integer DIGEST_STYLE_EMAIL_NOTIFICATION = 1;
    public static final Integer MESSAGE_STYLE_EMAIL_NOTIFICATION = 2;
    public static final Integer MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION = 3;
    public static final Integer MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION = 5;
    public static final Integer DISABLE_ALL_NOTIFICATIONS=4;
    
    protected int style=0;//obsolete; set by hibernate field access until we remove it
    private UserEntityPK id;
    private Map<Integer, String[]> styles;
    private String encodedStyles;
    private static String EMAIL_DELIMITER=",";
    private static String STYLE_DELIMITER=":";
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
     * @hibernate.property length="256"
     * @return
     */
    private String getEncodedStyles() {
    	return encodedStyles;
    }
    private void setEncodedStyles(String encodedStyles) {
    	this.encodedStyles = encodedStyles;
    }
    private void decode() {
    	styles.clear();
    	if (Validator.isNotNull(encodedStyles)) {
			String [] pairs = StringUtil.split(encodedStyles, STYLE_DELIMITER);
			for (int i=0; i < pairs.length; i+=2) {
				try {
					styles.put(Integer.valueOf(pairs[i]), StringUtil.split(pairs[i+1], EMAIL_DELIMITER));
				} catch (Exception ex) {};
			}   			
		}   	
    }
    private void encode() {
    	StringBuffer st = new StringBuffer();
    	for (Map.Entry<Integer, String[]> me: styles.entrySet()) {
    		st.append(me.getKey().toString() + STYLE_DELIMITER);
    		String vals = StringUtil.merge(me.getValue(), EMAIL_DELIMITER);
    		if (Validator.isNull(vals)) st.append(STYLE_DELIMITER);
    		else st.append( vals + STYLE_DELIMITER);
    	}
    	encodedStyles = st.toString();
    }
    //styles are encoded as style0,emailType0,style1,emailType1...
    public Map<Integer, String[]> getStyles() {
    	if (styles == null) {
    		styles = new HashMap();
    		decode();
    	}
        return styles;
    }
    public void setStyles(Map<Integer, String[]> styles) {
    	if (this.styles == null) {
    		this.styles = new HashMap<Integer, String[]>();
    	} else this.styles.clear();
       this.styles.putAll(styles);
       encode();
    }
    public void addStyle(Integer style, String[] emailType) {
        getStyles().put(style, emailType);
        encode();
    }
    public void removeStyle(Integer style) {
    	getStyles().remove(style);
    	encode();
    }
    public boolean hasStyle(Integer style) {
    	return getStyles().containsKey(style);
    }
    public String[] getEmailTypes(Integer style) {
    	String [] result = getStyles().get(style);
    	if (result != null) return result;
    	return new String[0];
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