package com.sitescape.ef.jobs;

import org.quartz.JobDataMap;
import java.util.Map;
import java.util.HashMap;
import com.sitescape.util.Validator;
public class ScheduleInfo {
	private Schedule schedule;
	protected Map details;
	private boolean enabled;
	private String zoneName;
	
	public ScheduleInfo(String zoneName) {
		this.zoneName = zoneName;
	}
	public ScheduleInfo(Map details) {
		this.details = details;
	}
	public Schedule getSchedule() {
		if (schedule == null) { 
			getDetails();
			try {
				String q = (String)details.get("schedule");
				if (!Validator.isNull(q))
					schedule = new Schedule((String)details.get("schedule"));
				else schedule = new Schedule();
			} catch (Exception ex) {
				schedule = new Schedule();
			}
		}
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public void setZone(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getZoneName() {
		if (zoneName == null) {
			getDetails();
			zoneName = (String)details.get("zoneName");
		}
		return zoneName;
	}
	public Map getDetails() {
		if (details == null) details = new JobDataMap();
		if (schedule != null) {
			details.put("schedule", schedule.getQuartzSchedule());
		}
		if (zoneName != null) {
			details.put("zoneName", zoneName);
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
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled.booleanValue();
	}
}
