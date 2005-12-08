package com.sitescape.ef.web.util;

import com.sitescape.ef.jobs.Schedule;
import com.sitescape.ef.util.PortletRequestUtils;
import javax.portlet.PortletRequest;

public class ScheduleHelper {
	public static Schedule getSchedule(PortletRequest request) {
		String val;
		Schedule schedule = new Schedule();
		val = PortletRequestUtils.getStringParameter(request, "minuteType", "");
		if (val.equals("repeat")) {
			int iVal = PortletRequestUtils.getIntParameter(request, "minutesRepeat", 0);
			if (iVal != 0)
				schedule.setMinutes("0/" + iVal);
			else 
				schedule.setMinutes("0");				
		} else {
			schedule.setMinutes(Integer.toString(PortletRequestUtils.getIntParameter(request, "schedMinutes", 5)));			
		}		
		
		val = PortletRequestUtils.getStringParameter(request, "hourType", "");
		if (val.equals("repeat")) {
			int iVal = PortletRequestUtils.getIntParameter(request, "hoursRepeat", 0);
			if (iVal != 0)
				schedule.setHours("0/" + iVal);
			else
				schedule.setHours("*");	
		} else {
			schedule.setHours(Integer.toString(PortletRequestUtils.getIntParameter(request, "schedHours", 14)));			
		}		
		
		
		val = PortletRequestUtils.getStringParameter(request, "schedType", "");
		if (val.equals("daily")) {
			schedule.setDaily(true);
		} else {
			schedule.setDaily(false);
			schedule.setOnSunday(PortletRequestUtils.getBooleanParameter(request, "onday_sun", false));
			schedule.setOnMonday(PortletRequestUtils.getBooleanParameter(request, "onday_mon", false));
			schedule.setOnTuesday(PortletRequestUtils.getBooleanParameter(request, "onday_tue", false));
			schedule.setOnWednesday(PortletRequestUtils.getBooleanParameter(request, "onday_wed", false));
			schedule.setOnThursday(PortletRequestUtils.getBooleanParameter(request, "onday_thu", false));
			schedule.setOnFriday(PortletRequestUtils.getBooleanParameter(request, "onday_fri", false));
			schedule.setOnSaturday(PortletRequestUtils.getBooleanParameter(request, "onday_sat", false));
			
		}
		return schedule;
	}
}
