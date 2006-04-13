package com.sitescape.ef.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.sitescape.util.StringUtil;
import java.util.Collection;

import com.sitescape.ef.util.CollectionUtil;

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
     * This represents the entire set of entries including managed lists and individual user requests.
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
     * Return list of Notifications excluding UserNotifications
     * 
     */ 
    public List getDefaultDistribution() {
    	List dList = new ArrayList();
    	List cList = getDistribution();
    	for (int i=0; i<cList.size(); ++i) {
    		Notification n = (Notification)cList.get(i);
    		if (! (n instanceof UserNotification)) dList.add(n);
    	}
    	return dList;
    }
    /**
     * Set the Notifications.  Leave UserNotifications as is
     * @param newDefault
     */
    public void setDefaultDistribution(Collection newDefault) {
   		if (newDefault == null) newDefault = new ArrayList();
		List oldDefault = getDefaultDistribution();
		Set newM = CollectionUtil.differences(newDefault, oldDefault);
		Set remM = CollectionUtil.differences(oldDefault, newDefault);
		distribution.addAll(newM);
		distribution.removeAll(remM);
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
