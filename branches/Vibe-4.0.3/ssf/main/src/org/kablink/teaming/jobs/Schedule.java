/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.jobs;
import java.util.Map;
import java.util.TimeZone;

import org.kablink.teaming.util.NLT;
import org.kablink.util.Validator;

public class Schedule {
	private boolean daily=false,minutesRepeat=false,hoursRepeat=false;
	private String hours="12";
	private String minutes="15";
	private boolean onMonday,onTuesday,onWednesday,onThursday,onFriday,onSaturday,onSunday;
	private Map jobDetails;
	public Schedule() {
	}
	public Schedule(String schedule) {
		if (Validator.isNull(schedule)) return;
		String[] vals = schedule.split(" +");
		String val;
		int pos = 1;
		int len = vals.length;
		if (pos > len) return;
		setMinutes(vals[pos++]);
		if (pos > len) return;
		setHours(vals[pos++]);
		//skip dayOfMonth and months
		pos += 2;
		if (pos > len) return;
		val = vals[pos].trim();
		if (val.equals("*")) {
			setDaily(true);
		} else {
			vals = val.split(",");
			for (int i=0; i<vals.length; ++i) {
				doDaySegment(vals[i].trim());
			}
		}
		
	}
	private void doDaySegment(String daysString) {
		String[] days = daysString.split("-");
		String start = days[0];
		String end;
		if (days.length==1) end=""; else end=days[1];
		if (start.compareToIgnoreCase("sun")==0) {
			onSunday=true;
			if (isLast("sun", end)) return;
			start="mon";
		}
		if (start.compareToIgnoreCase("mon")==0) {
			onMonday=true;
			if (isLast("mon", end)) return;
			start="tue";
		}
		if (start.compareToIgnoreCase("tue")==0) {
			onTuesday=true;
			if (isLast("tue", end)) return;
			start="wed";
		} 
		if (start.compareToIgnoreCase("wed")==0) {
			onWednesday=true;
			if (isLast("wed", end)) return;
			start="thu";
		}
		if (start.compareToIgnoreCase("thu")==0) {
			onThursday=true;
			if (isLast("thu", end)) return;
			start="fri";
		} 
		if (start.compareToIgnoreCase("fri")==0) {
			onFriday=true;
			if (isLast("fri", end)) return;
			start="sat";
		} 
		if (start.compareToIgnoreCase("sat")==0) {
			onSaturday=true;
		}  
	}
	private boolean isLast(String day, String end) {
		if (end.equals("")) return true;
		if (day.compareToIgnoreCase(end)==0) return true;
		return false;
	}
	public boolean isDaily()  {
		return daily;
	}
	public void setDaily(boolean daily) {
		this.daily = daily;
	}
	public boolean isOnMonday() {
		return onMonday;
	}
	public void setOnMonday(boolean onMonday) {
		this.onMonday = onMonday;
	}
	
	public boolean isOnTuesday() {
		return onTuesday;
	}
	public void setOnTuesday(boolean onTuesday) {
		this.onTuesday = onTuesday;
	}
	public boolean isOnWednesday() {
		return onWednesday;
	}
	public void setOnWednesday(boolean onWednesday) {
		this.onWednesday = onWednesday;
	}
	public boolean isOnThursday() {
		return onThursday;
	}
	public void setOnThursday(boolean onThursday) {
		this.onThursday = onThursday;
	}
	public boolean isOnFriday() {
		return onFriday;
	}
	public void setOnFriday(boolean onFriday) {
		this.onFriday = onFriday;
	}
	public boolean isOnSaturday() {
		return onSaturday;
	}
	public void setOnSaturday(boolean onSaturday) {
		this.onSaturday = onSaturday;
	}
	public boolean isOnSunday() {
		return onSunday;
	}
	public void setOnSunday(boolean onSunday) {
		this.onSunday = onSunday;
	}
	public boolean isRepeatHours() {
		return hoursRepeat;
	}
	public String getHoursRepeat() {
		int index = hours.indexOf('/');
		if (index == -1) return "";
		return hours.substring(++index);
	}
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
		if (hours.indexOf('/') != -1) {
			hoursRepeat=true;
		} else if ("*".equals(hours)) {
			hoursRepeat = true;
		} else hoursRepeat=false;
	}
	public boolean isRepeatMinutes() {
		return minutesRepeat;
	}	
	public String getMinutes() {
		return minutes;
	}
	public void setMinutes(String minutes) {
		this.minutes = minutes;
		if (minutes.length() == 1) this.minutes="0" + minutes;
		if (minutes.indexOf('/') != -1) {
			minutesRepeat=true;
		} else if ("*".equals(hours)) {
			minutesRepeat = true;
		} else minutesRepeat = false;
	}
	public String getMinutesRepeat() {
		int index = minutes.indexOf('/');
		if (index == -1) return "";
		return minutes.substring(++index);
	}
	
	/**
	 * Return quartz schedule string.  We don't support seconds, dayOfMonth, months, year
	 * seconds minutes hours dayOfMonth months days year"
	 *
	 * @return
	 */
	public String getQuartzSchedule() {
		StringBuffer schedule = new StringBuffer("0 " + minutes + " " + hours + " ? * ");
		if (isDaily() || areAllDaysDisabled()) {
			schedule.append("* *");
		} else {
			if (isOnSunday()) schedule.append("sun,");
			if (isOnMonday()) schedule.append("mon,");
			if (isOnTuesday()) schedule.append("tue,");
			if (isOnWednesday()) schedule.append("wed,");
			if (isOnThursday()) schedule.append("thu,");
			if (isOnFriday()) schedule.append("fri,");
			if (isOnSaturday()) schedule.append("sat,");
			schedule.replace(schedule.length()-1, schedule.length(), " *");
		}
			
		return schedule.toString();
	}
	public Map getDetails() {
		return jobDetails;
		
	}
	public void setDetails(Map jobDetails) {
		this.jobDetails = jobDetails;
	}

	public boolean areAllDaysDisabled() {
		return !onSunday && !onMonday && !onTuesday && !onWednesday && !onThursday && !onFriday && !onSaturday;
	}
}
