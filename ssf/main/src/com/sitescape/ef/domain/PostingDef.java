package com.sitescape.ef.domain;


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
    private String emailAddress="";
    private Definition definition;
    private Long zoneId;
 
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
