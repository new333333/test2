package com.sitescape.ef.domain;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.sitescape.util.GetterUtil;
import com.sitescape.util.StringUtil;
import java.util.Collection;

import com.sitescape.ef.jobs.Schedule;
import com.sitescape.ef.util.CollectionUtil;

/**
 * @author Jong Kim
 */
public class NotificationDef  {
   
    private List distribution;
    private String email;
    private boolean teamOn=false;
     private String from,subject;
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
     * @hibernate.bag  lazy="true" cascade="all,delete-orphan" inverse="true"  optimistic-lock="false" node="."
     * @hibernate.key column="binder"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.Notification" embed-xml="false" node="distribution/@id"
     * 
     * This represents the entire set of entries including managed lists and individual user requests.
     * @return
     */
    private List getHDistribution() {return distribution;}	
    private void setHDistribution(List distribution) {this.distribution = distribution;}
    
    public List getDistribution() {
    	if (distribution == null) distribution = new ArrayList();
    	return distribution;
    }	
    public void setDistribution(Collection newDistribution) {
    	distribution = CollectionUtil.mergeAsSet(getDistribution(), newDistribution);
     }
    public List getDefaultDistribution() {
    	List dList = new ArrayList();
    	List cList = getDistribution();
    	for (int i=0; i<cList.size(); ++i) {
    		Notification n = (Notification)cList.get(i);
    		if (n.getType().equals("N")) dList.add(n);
    	}
    	return dList;
    }

    /**
     * @hibernate.property type="org.springframework.orm.hibernate3.support.ClobStringType" column="email"
     * @return
     */
    private String getHEmailAddress() {
        return email;
    }
    private void setHEmailAddress(String email) {
        this.email = email;
    }
    /**
     * Callers deal with emailAddress as a comma separated list
     * @return
     */
    public String[] getEmailAddress() {
    	if (email == null) return new String[0];
    	return StringUtil.split(email);
    }
    public void setEmailAddress(String []address) {
    	if ((address == null) || (address.length == 0)) {
    		email = null;
    	} else {
    		email = StringUtil.merge(address);
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
