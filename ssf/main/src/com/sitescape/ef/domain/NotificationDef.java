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
    public static final int CONTEXT_LEVEL_SEND_TITLES_ONLY = 1; // default
    public static final int CONTEXT_LEVEL_DISABLE_EMAIL_NOTIFICATION = 2;
    public static final int CONTEXT_LEVEL_SEND_TITLES_AND_SUMMARIES = 3;
    
    private int contextLevel = CONTEXT_LEVEL_SEND_TITLES_ONLY;
    private String schedule;
    private List distribution;
    private SSClobString email;
    private int summaryLines=5;
    private boolean teamOn=false;
    private Date lastNotification;
    public NotificationDef() {
    }
 
    public boolean isEnabled() {
    	return contextLevel!=CONTEXT_LEVEL_DISABLE_EMAIL_NOTIFICATION;
    	
    }
    public void setEnabled(boolean enabled) {
    	if (enabled == true) 
    		contextLevel=CONTEXT_LEVEL_SEND_TITLES_ONLY;
    	else 
            contextLevel=CONTEXT_LEVEL_DISABLE_EMAIL_NOTIFICATION;
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
     * @hibernate.property length="256" column="schedule"
     * This string represents the quartz schedule.
     * @return
     */
    private String getHSchedule() {
        return schedule;
    }
    private void setHSchedule(String schedule) {
        this.schedule = schedule;
    }
	public Schedule getSchedule() {
		return new Schedule(schedule);
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule.getQuartzSchedule();
	}
    /**
     * @hibernate.property type="com.sitescape.ef.dao.util.SSClobStringType" column="email"
     * @return
     */
    private SSClobString getHEmailAddress() {
        return email;
    }
    private void setHEmailAddress(SSClobString email) {
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
