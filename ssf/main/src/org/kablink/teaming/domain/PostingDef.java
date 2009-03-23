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
package org.kablink.teaming.domain;

import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;

/**
 * @hibernate.class table="SS_Postings" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * @author Jong Kim
 *
 */
public class PostingDef extends PersistentObject {

    public static final Integer REPLY_RETURN_TO_SENDER = 3; 
    public static final Integer REPLY_POST_AS_A_NEW_TOPIC = 2;
    public static final Integer REPLY_POST_AS_A_REPLY = 1;
    private Integer replyPostingOption = REPLY_POST_AS_A_REPLY;
    private boolean enabled=true;
    private String password=null;//old unencrypted password, null out on upgrade; access=field
    private String credentials=null; //access = field
    private Binder binder;
    private String emailAddress="";
    private Definition definition;
 
    /**
     * @hibernate.property 
     * @return
     */
    public boolean isEnabled() {
    	return enabled;
    	
    }
    public void setEnabled(boolean enabled) {
    	this.enabled = enabled;
    }
    /**
     * @hibernate.property not-null="true"
     */
    public Long getZoneId() {
    	return zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
    /**
     * @hibernate.many-to-one
     */
    public Binder getBinder() {
    	return binder;
    }
    public void setBinder(Binder binder) {
    	this.binder = binder;
    }
    /**
     * The definition to use to create entries
     * @hibernate.many-to-one class="org.kablink.teaming.domain.Definition"
     * hibernate.column name="definition" sql-type="char(32)"
    */
    public Definition getDefinition() {
    	return definition;
    }
    public void setDefinition(Definition definition) {
    	this.definition = definition;
    }   
    /**
     * @hibernate.property
     */
    public String getEmailAddress() {
    	return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
    	this.emailAddress = emailAddress;
    }
    /**
     * @hibernate.property
     */
    public String getCredentials() {
    	return credentials;
    }
    public void setCredentials(String credentials) {
    	//Only store the password if it isn't the dummy password
    	if (credentials == null || !credentials.equals(WebKeys.MAIL_POSTING_DUMMY_PASSWORD)) 
    		this.credentials = credentials;
    }
    //this is used to move the old unencrypted password to the encrypted field 
    public void updateCredentials() {
    	if (Validator.isNull(this.credentials)) {
    		this.credentials = this.password;
    	}
    	this.password = null;
    }
     /**
     * @hibernate.property
     * @return
     */
    public Integer getReplyPostingOption() {
        return replyPostingOption;
    }
    public void setReplyPostingOption(Integer replyPostingOption) {
        this.replyPostingOption = replyPostingOption;
    }
}
