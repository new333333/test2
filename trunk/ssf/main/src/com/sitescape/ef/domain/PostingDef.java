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
    private boolean enabled=false;
    private String subject;
    private Binder binder;
    private Long emailId;
 
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
     * @hibernate.many-to-one
     */
    public Binder getBinder() {
    	return binder;
    }
    public void setBinder(Binder binder) {
    	this.binder = binder;
    }
    /**
     * The mapping from id to address is kept in the scheduler.
     * @hibernate.property
     * @return
     */
    public Long getEmailId() {
    	return emailId;
    }
    public void setEmailId(Long emailId) {
    	this.emailId = emailId;
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
    public SearchTerm getSearchTerm(String emailAddress) {
    	if (emailId == null) return null;
    	
    	if (Validator.isNull(emailAddress)) return null;
    	if (Validator.isNull(subject)) {
    		return new RecipientStringTerm(Message.RecipientType.TO,emailAddress);
    	} else {
    		return new AndTerm(new RecipientStringTerm(Message.RecipientType.TO,emailAddress), new SubjectTerm(subject));
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
