package com.sitescape.ef.web.util;
import java.util.Map;

import com.sitescape.ef.jobs.Schedule;
import com.sitescape.ef.portlet.forum.ActionUtil;
public class ScheduleHelper {
	public static Schedule getSchedule(Map formData) {
		String val;
		Schedule schedule = new Schedule();
		val = ActionUtil.getStringValue(formData, "minuteType");
		if (val.equals("repeat")) {
			schedule.setMinutes("0/" + ActionUtil.getStringValue(formData, "minutesRepeat"));			
		} else {
			schedule.setMinutes(ActionUtil.getStringValue(formData, "schedMinutes"));			
		}		
		
		val = ActionUtil.getStringValue(formData, "hourType");
		if (val.equals("repeat")) {
			schedule.setHours("0/" + ActionUtil.getStringValue(formData, "hoursRepeat"));			
		} else {
			schedule.setHours(ActionUtil.getStringValue(formData, "schedHours"));			
		}		
		
		
		val = ActionUtil.getStringValue(formData, "schedType");
		if (val.equals("daily")) {
			schedule.setDaily(true);
		} else {
			schedule.setDaily(false);
			if (formData.containsKey("onday_sun")) schedule.setOnSunday(true);
			if (formData.containsKey("onday_mon")) schedule.setOnMonday(true);
			if (formData.containsKey("onday_tue")) schedule.setOnTuesday(true);
			if (formData.containsKey("onday_wed")) schedule.setOnWednesday(true);
			if (formData.containsKey("onday_thu")) schedule.setOnThursday(true);
			if (formData.containsKey("onday_fri")) schedule.setOnFriday(true);
			if (formData.containsKey("onday_sat")) schedule.setOnSaturday(true);
			
		}
		return schedule;
	}
}
