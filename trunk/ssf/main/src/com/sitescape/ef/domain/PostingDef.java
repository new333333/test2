package com.sitescape.ef.domain;

import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;


/**
 * @hibernate.class table="SS_Postings" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * @author Jong Kim
 *
 */
public class PostingDef extends PersistentObject {

    public static final Integer RETURN_TO_SENDER = 3; // default
    public static final Integer POST_AS_A_NEW_TOPIC = 2;
    public static final Integer POST_AS_A_REPLY = 1;
    private Integer replyPostingOption = POST_AS_A_REPLY;
    private boolean enabled=true;
    private Binder binder;
    private String emailAddress;
    private Definition definition;
    private String zoneName;
 
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
     * @hibernate.property length="100" not-null="true"
     */
    public String getZoneName() {
    	return zoneName;
    }
    public void setZoneName(String zoneName) {
    	this.zoneName = zoneName;
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
     * @hibernate.many-to-one class="com.sitescape.ef.domain.Definition"
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
     * @return
     */
    public Integer getReplyPostingOption() {
        return replyPostingOption;
    }
    public void setReplyPostingOption(Integer replyPostingOption) {
        this.replyPostingOption = replyPostingOption;
    }
}
