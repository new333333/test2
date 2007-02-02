package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sitescape.team.util.CollectionUtil;
import com.sitescape.util.StringUtil;

/**
 * @author Jong Kim
 */
public class NotificationDef  {
   
	protected List distribution;//initialized by hibernate access=field
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
     * 
     * @return
     */
    public List getDistribution() {
    	if (distribution == null) distribution = new ArrayList();
    	return distribution;
    }	
    public void setDistribution(Collection newDistribution) {
    	distribution = CollectionUtil.mergeAsSet(getDistribution(), newDistribution);
     }


    /**
     * @hibernate.property
     * @return
     */
    public String getEmailAddress() {
    	return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
    	this.emailAddress = emailAddress;
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
