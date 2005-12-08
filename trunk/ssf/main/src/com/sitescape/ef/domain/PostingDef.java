package com.sitescape.ef.domain;

import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SubjectTerm;
import javax.mail.search.AndTerm;
import javax.mail.Message;
import javax.mail.search.SearchTerm;

import com.sitescape.util.Validator;


/**
 * @hibernate.class table="SS_Postings" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * @author Jong Kim
 *
 */
public class PostingDef extends PersistentObject {
    public static final int RETURN_TO_SENDER = 1; // default
    public static final int POST_AS_A_NEW_TOPIC = 2;
    
    private int replyPostingOption = RETURN_TO_SENDER;
    private boolean enabled=true;
    private String subject;
    private Binder binder;
    private EmailAlias emailAlias;
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
     * @hibernate.many-to-one
     */
    public Definition getDefinition() {
    	return definition;
    }
    public void setDefinition(Definition definition) {
    	this.definition = definition;
    }   
    /**
     * @hibernate.many-to-one
     * @return
     */
    public EmailAlias getEmailAlias() {
    	return emailAlias;
    }
    public void setEmailAlias(EmailAlias emailAlias) {
    	this.emailAlias = emailAlias;
    }
    /**
     * @hibernate.property length="256" 
     * @return
     */
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public SearchTerm getSearchTerm() {
    	if (emailAlias == null) return null;
    	
    	if (Validator.isNull(emailAlias.getAliasName())) return null;
    	if (Validator.isNull(subject)) {
    		return new RecipientStringTerm(Message.RecipientType.TO,emailAlias.getAliasName());
    	} else {
    		return new AndTerm(new RecipientStringTerm(Message.RecipientType.TO,emailAlias.getAliasName()), new SubjectTerm(subject));
    	}
    }
    /**
     * @hibernate.property
     * @return
     */
    public int getReplyPostingOption() {
        return replyPostingOption;
    }
    public void setReplyPostingOption(int replyPostingOption) {
    	if ((replyPostingOption != RETURN_TO_SENDER) &&
    		(replyPostingOption != POST_AS_A_NEW_TOPIC)) throw new IllegalArgumentException("replyPostingOption");
        this.replyPostingOption = replyPostingOption;
    }
}
