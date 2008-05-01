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
package com.sitescape.team.domain;

import com.sitescape.team.web.WebKeys;


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
    private String password=null;
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
     * @hibernate.many-to-one class="com.sitescape.team.domain.Definition"
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
    public String getPassword() {
    	return password;
    }
    public void setPassword(String password) {
    	//Only store the password if it isn't the dummy password
    	if (password == null || !password.equals(WebKeys.MAIL_POSTING_DUMMY_PASSWORD)) 
    		this.password = password;
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
