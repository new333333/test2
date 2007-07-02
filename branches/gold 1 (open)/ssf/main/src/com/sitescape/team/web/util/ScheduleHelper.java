/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.util;

import com.sitescape.team.jobs.Schedule;

import javax.portlet.PortletRequest;

public class ScheduleHelper {
	public static Schedule getSchedule(PortletRequest request) {
		String val;
		Schedule schedule = new Schedule();
		val = PortletRequestUtils.getStringParameter(request, "minuteType", "");
		if (val.equals("repeat")) {
			int iVal = PortletRequestUtils.getIntParameter(request, "minutesRepeat", -1);
			if (iVal != -1)
				schedule.setMinutes("0/" + iVal);
			else 
				schedule.setMinutes("0/1");				
		} else {
			int minutes = PortletRequestUtils.getIntParameter(request, "schedMinutes", -1);
			if (minutes != -1) schedule.setMinutes(Integer.toString(minutes));
		}		
		
		val = PortletRequestUtils.getStringParameter(request, "hourType", "");
		if (val.equals("repeat")) {
			int iVal = PortletRequestUtils.getIntParameter(request, "hoursRepeat", -1);
			if (iVal != -1)
				schedule.setHours("0/" + iVal);
			else
				schedule.setHours("0/1");	
		} else {
			int hours = PortletRequestUtils.getIntParameter(request, "schedHours", -1);
			if (hours != -1) schedule.setHours(Integer.toString(hours));
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
