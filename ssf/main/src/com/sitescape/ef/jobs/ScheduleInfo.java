package com.sitescape.ef.jobs;

import org.quartz.JobDataMap;
import java.util.Map;
import java.util.HashMap;
public class ScheduleInfo {
	private Schedule schedule;
	private Map details;
	private boolean enabled;
	
	public ScheduleInfo() {
		
	}
	public ScheduleInfo(Map details) {
		this.details = details;
	}
	public Schedule getSchedule() {
		if (schedule == null) 
			if (details != null) schedule = new Schedule((String)details.get("schedule"));
			else schedule = new Schedule();   
		
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public Map getDetails() {
		if (details == null) details = new JobDataMap();
		if (schedule != null) {
			details.put("schedule", schedule.getQuartzSchedule());
		}
		return details;
	}
	public void setDetails(Map details) {
		if (details instanceof JobDataMap)
			this.details = details;
		else
			this.details = new JobDataMap(details);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
