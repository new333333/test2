package com.sitescape.ef.domain;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.sitescape.util.StringUtil;
import java.util.Collection;
import com.sitescape.ef.util.CollectionUtil;

/**
 * @author Jong Kim
 */
public class NotificationDef  {
    public static final int CONTEXT_LEVEL_SEND_TITLES_ONLY = 1; // default
    public static final int CONTEXT_LEVEL_DISABLE_EMAIL_NOTIFICATION = 2;
    public static final int CONTEXT_LEVEL_SEND_TITLES_AND_SUMMARIES = 3;
    
    private int contextLevel = CONTEXT_LEVEL_SEND_TITLES_ONLY;
    private String schedule;
    private List distribution;
    private SSClobString email;
    private List emailAddresses;
    private int summaryLines=5;
    private boolean teamOn=false;
    private Date lastNotification;
    private boolean disabled=true;
    public NotificationDef() {
    }
    /**
     * @hibernate.property column="disabled"
     * @return
     */
    public boolean isDisabled() {
    	return disabled;
    }
    public void setDisabled(boolean disabled) {
    	this.disabled = disabled;
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
     * @hibernate.property column="lastNotification"
     * @return
     */
    public Date getLastNotification() {
        return this.lastNotification;
    }
    public void setLastNotification(Date lastNotification) {
        this.lastNotification = lastNotification;
    }
    /**
     * @hibernate.property column="summaryLines"
     * @return
     */
    public int getSummaryLines() {
        return this.summaryLines;
    }
    public void setSummaryLines(int lines) {
        this.summaryLines = lines;
    }
    /**
     * @hibernate.property column="contextLevel"
     * @return
     */
    public int getContextLevel() {
        return contextLevel;
    }
    public void setContextLevel(int contextLevel) {
        this.contextLevel = contextLevel;
    }
    /**
     * @hibernate.bag  lazy="true" cascade="all,delete-orphan" inverse="false"  optimistic-lock="false" node="."
     * @hibernate.key column="forum"
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
    /**
     * @hibernate.property length="256" column="schedule"
     * This string represents the quartz schedule.
     * @return
     */
    public String getSchedule() {
        return schedule;
    }
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType" column="email"
     * @return
     */
    private SSClobString getEmail() {
        return email;
    }
    private void setEmail(SSClobString email) {
        this.email = email;
    }
    /**
     * Callers deal with emailAddress as a comma separated list
     * @return
     */
    public String[] getEmailAddress() {
    	if (email == null) return new String[0];
    	return StringUtil.split(email.getText());
    }
    public void setEmailAddress(String []address) {
    	if ((address == null) || (address.length == 0)) {
    		email = null;
    	} else {
    		email = new SSClobString(StringUtil.merge(address));
    	}
    }
    
}
