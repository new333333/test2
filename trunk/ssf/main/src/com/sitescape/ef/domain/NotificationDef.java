package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.util.StringUtil;

/**
 * @author Jong Kim
 */
public class NotificationDef  {
   
    protected String emailAddress;//initialized by hibernate access=field
    protected boolean teamOn=false;
    protected String from,subject;
    public NotificationDef() {
    }
 

    /**
     * @hibernate.property column="teamOn"
     * @return
     */
    public boolean isTeamOn() {
        return this.teamOn;
    }
    public void setTeamOn(boolean teamOn) {
        this.teamOn  = teamOn;
    }

    /**
     * Callers deal with emailAddress as a comma separated list
     * @return
     */
    public String[] getEmailAddress() {
    	if (emailAddress == null) return new String[0];
    	return StringUtil.split(emailAddress);
    }
    public void setEmailAddress(String []address) {
    	if ((address == null) || (address.length == 0)) {
    		emailAddress = null;
    	} else {
    		emailAddress = StringUtil.merge(address);
    	}
    }
    /**
     * @hibernate.property length="128" column="fromAddress"
     * @return
     */
    public String getFromAddress() {
    	return from;
    }
    public void setFromAddress(String from) {
    	this.from = from;
    }
    /**
     * @hibernate.property length="128" column="subject"
     * @return
     */
    public String getSubject() {
    	return subject;
    }
    public void setSubject(String subject) {
    	this.subject = subject;
    }    
}
