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
package org.kablink.teaming.remoting.ws.model;

import java.util.Calendar;
import java.util.TimeZone;

public class Event {

	/// free/busy types
	
	public static final String FREEBUSYTYPE_FREE 		= "free";
	public static final String FREEBUSYTYPE_BUSY 		= "busy";
	public static final String FREEBUSYTYPE_TENTATIVE	= "tentative";
	public static final String FREEBUSYTYPE_OUTOFOFFICE = "outOfOffice";
	
	/// Recurrence types

	/**
	 * The value of frequency indicating a secondly recurrence.
	 * @see #setFrequency
	 */
	public final static int SECONDLY = 0;

	/**
	 * The value of frequency indicating a minutely recurrence.
	 * @see #setFrequency
	 */
	public final static int MINUTELY = 1;

	/**
	 * The value of frequency indicating an hourly recurrence.
	 * @see #setFrequency
	 */
	public final static int HOURLY = 2;

	/**
	 * The value of frequency indicating a daily recurrence.
	 * @see #setFrequency
	 */
	public final static int DAILY = 3;

	/**
	 * The value of frequency indicating a weekly recurrence.
	 * @see #setFrequency
	 */
	public final static int WEEKLY = 4;

	/**
	 * The value of frequency indicating a monthly recurrence.
	 * @see #setFrequency
	 */
	public final static int MONTHLY = 5;

	/**
	 * The value of frequency indicating a yearly recurrence.
	 * @see #setFrequency
	 */
	public final static int YEARLY = 6;

	/**
	 * The value of frequency indicating no recurrence.
	 * @see #setFrequency
	 */
	public final static int NO_RECURRENCE = 7;

	/// Properties

	private Calendar dtStart;

	private Duration duration;

	private Integer frequency;

	private Integer interval;

	private Calendar until;

	private Integer count;

	private Integer weekStart;
	
	private Boolean timeZoneSensitive;
	
	// used only as flag: 
	// null - all day(s) event
	// not null - NOT all day(s) event ( equivalent to java.util.TimeZone.getID() )
	private String timeZone;
	
	private String uid;
	
	private String freeBusy;
	
	private int[] bySecond;

	private int[] byMinute;

	private int[] byHour;

	private DayAndPosition[] byDay;

	private int[] byMonthDay;

	private int[] byYearDay;

	private int[] byWeekNo;

	private int[] byMonth;

	public static org.kablink.teaming.domain.Event toDomainModel(Event event) {
		
		org.kablink.teaming.domain.Event.FreeBusyType freeBusy = null;
		if(event.freeBusy != null) {
			try {
				freeBusy = org.kablink.teaming.domain.Event.FreeBusyType.valueOf(event.freeBusy);
			} catch (Exception e) {}				
		}
		
		return new org.kablink.teaming.domain.Event(
				event.dtStart,
				Duration.toDomainModel(event.duration),
				event.frequency,
				event.interval,
				event.until,
				event.count,
				event.weekStart,
				event.timeZoneSensitive,
				((event.timeZone != null)? TimeZone.getTimeZone(event.timeZone) : null),
				event.uid,
				freeBusy,
				event.bySecond,
				event.byMinute,
				event.byHour,
				DayAndPosition.toDomainModel(event.byDay),
				event.byMonthDay,
				event.byYearDay,
				event.byWeekNo,
				event.byMonth);
	}
	
	public Event() {
	}
		
	public Calendar getDtStart() {
		return dtStart;
	}

	public void setDtStart(Calendar dtStart) {
		this.dtStart = dtStart;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Calendar getUntil() {
		return until;
	}

	public void setUntil(Calendar until) {
		this.until = until;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getWeekStart() {
		return weekStart;
	}

	public void setWeekStart(Integer weekStart) {
		this.weekStart = weekStart;
	}

	public int[] getBySecond() {
		return bySecond;
	}

	public void setBySecond(int[] bySecond) {
		this.bySecond = bySecond;
	}

	public int[] getByMinute() {
		return byMinute;
	}

	public void setByMinute(int[] byMinute) {
		this.byMinute = byMinute;
	}

	public int[] getByHour() {
		return byHour;
	}

	public void setByHour(int[] byHour) {
		this.byHour = byHour;
	}

	public DayAndPosition[] getByDay() {
		return byDay;
	}

	public void setByDay(DayAndPosition[] byDay) {
		this.byDay = byDay;
	}

	public int[] getByMonthDay() {
		return byMonthDay;
	}

	public void setByMonthDay(int[] byMonthDay) {
		this.byMonthDay = byMonthDay;
	}

	public int[] getByYearDay() {
		return byYearDay;
	}

	public void setByYearDay(int[] byYearDay) {
		this.byYearDay = byYearDay;
	}

	public int[] getByWeekNo() {
		return byWeekNo;
	}

	public void setByWeekNo(int[] byWeekNo) {
		this.byWeekNo = byWeekNo;
	}

	public int[] getByMonth() {
		return byMonth;
	}

	public void setByMonth(int[] byMonth) {
		this.byMonth = byMonth;
	}

	public Boolean isTimeZoneSensitive() {
		return timeZoneSensitive;
	}

	public void setTimeZoneSensitive(Boolean timeZoneSensitive) {
		this.timeZoneSensitive = timeZoneSensitive;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFreeBusy() {
		return freeBusy;
	}

	public void setFreeBusy(String freeBusy) {
		this.freeBusy = freeBusy;
	}

	public static class Duration {
		  private int weeks;
		  private int days;
		  private int hours;
		  private int minutes;
		  private int seconds;
		  
		public static org.kablink.util.cal.Duration toDomainModel(Duration duration) {
			org.kablink.util.cal.Duration result = null;
			if(duration != null) {
				result = new org.kablink.util.cal.Duration(duration.weeks, duration.days, duration.hours, duration.minutes, duration.seconds);
			}
			return result;
		}

		public static Duration toRemoteModel(org.kablink.util.cal.Duration duration) {
			if(duration == null)
				return null;
			else 
				return new Duration(duration);
		}
		
		public Duration() {
		}
		
		private Duration(org.kablink.util.cal.Duration duration) {
			weeks = duration.getWeeks();
			days = duration.getDays();
			hours = duration.getHours();
			minutes = duration.getMinutes();
			seconds = duration.getSeconds();
		}
		
		public int getWeeks() {
			return weeks;
		}
		public void setWeeks(int weeks) {
			this.weeks = weeks;
		}
		public int getDays() {
			return days;
		}
		public void setDays(int days) {
			this.days = days;
		}
		public int getHours() {
			return hours;
		}
		public void setHours(int hours) {
			this.hours = hours;
		}
		public int getMinutes() {
			return minutes;
		}
		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}
		public int getSeconds() {
			return seconds;
		}
		public void setSeconds(int seconds) {
			this.seconds = seconds;
		}

	}
	
	public static class DayAndPosition {
		  private int day;
		  private int position;
		  
		public static DayAndPosition[] toRemoteModel(org.kablink.util.cal.DayAndPosition[] daps) {
			if(daps == null)
				return null;
			DayAndPosition[] result = new DayAndPosition[daps.length];
			for(int i = 0; i < daps.length; i++)
				result[i] = new DayAndPosition(daps[i]);
			return result;
		}
		
		public static org.kablink.util.cal.DayAndPosition[] toDomainModel(DayAndPosition[] daps) {
			if(daps == null)
				return null;
			org.kablink.util.cal.DayAndPosition[] result = new org.kablink.util.cal.DayAndPosition[daps.length];
			for(int i = 0; i < daps.length; i++)
				result[i] = new org.kablink.util.cal.DayAndPosition(daps[i].day, daps[i].position);
			return result;
		}
		
		public DayAndPosition() {	
		}
		
		private DayAndPosition(org.kablink.util.cal.DayAndPosition dap) {
			day = dap.getDayOfWeek();
			position = dap.getDayPosition();
		}
		
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}

	}
}
