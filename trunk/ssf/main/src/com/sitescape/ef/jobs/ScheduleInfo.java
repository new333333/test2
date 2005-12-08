package com.sitescape.ef.jobs;

import org.quartz.JobDataMap;

public class ScheduleInfo {
	private Schedule schedule;
	private JobDataMap details;
	private boolean enabled;
	
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public JobDataMap getDetails() {
		if (details == null) details = new JobDataMap();
		if (schedule != null) {
			details.put("schedule", schedule.getQuartzSchedule());
		}
		return details;
	}
	public void setDetails(JobDataMap details) {
		this.details = details;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
