/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

/*
 * Copyright (c) 2000, 2001, 2002, 2004 Columbia University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution. 
 *
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.kablink.teaming.domain;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Transp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Element;

import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.ical.impl.IcalModuleImpl;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.util.NLT;
import org.kablink.util.cal.CalendarUtil;
import org.kablink.util.cal.DayAndPosition;
import org.kablink.util.cal.Duration;

/**
 * <code>Recurrence</code> represents a recurring interval of time. It
 * corresponds to a CPL time-switch or an iCalendar RRULE.<p>
 * 
 * A recurring interval of time is represented by a start time, a duration
 * (equivalently, an ending time), a frequency, an interval, a termination
 * ("until") date or recurrence count, and a set of by* rules. For more
 * information on this, see below, and the relevant specifications.<p>
 * 
 * Change history:<br>
 * <dl>
 * <dt><b>1.0</b></dt><dd>Initial version</dd>
 * <dt><b>1.1</b></dt><dd>Added sub-day by* and intervals, count, and
 * parsers</dd>
 * </dl>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc2445.txt">RFC 2445 (iCalendar)</a>
 * @see <a href="http://www.rfc-editor.org/rfc/rfc3880.txt">RFC 3880 (CPL)</a>
 * @version 1.1
 * @author Jonathan Lennox
 *
 * @hibernate.class table="SS_Events" dynamic-update="true" lazy="false"
 * @hibernate.mapping auto-import="false" 
 * need auto-import = false so names don't collide with JBPM.
 * 
 * This is based on the Recurrence class from edu.columbia.cpl.Recurrence
 * Changed to reduce cloning and persist with hibernate
 */
@SuppressWarnings("deprecation")
public class Event extends PersistentTimestampObject implements Cloneable, UpdateAttributeSupport {
	protected static Log logger = LogFactory.getLog(Event.class);

	public enum FreeBusyType {
		free,
		busy,
		tentative, 
		outOfOffice
	};
	
	// Recurrence types

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

	/**
	 * Internal start variable.
	 * @see #getStart
	 * @see #setStart
	 */
	protected Calendar dtStart;
	protected Calendar dtCalcStart;

	/**
	 * Internal end variable.
	 * @see #getEnd
	 * @see #setEnd
	 */
	protected Calendar dtEnd;
	protected Calendar dtCalcEnd;

	/**
	 * Internal duration variable.
	 * @see #getDuration
	 * @see #setDuration
	 */
	protected Duration duration;

	/**
	 * Internal frequency variable.
	 * @see #getFrequency
	 * @see #setFrequency
	 */
	protected int frequency;

	/**
	 * Internal interval variable.
	 * @see #getInterval
	 * @see #setInterval
	 */
	protected int interval;

	/**
	 * Internal until variable.
	 * @see #getUntil
	 * @see #setUntil
	 */
	protected Calendar until;

	/**
	 * Internal count variable.
	 * @see #getCount
	 * @see #setCount
	 */
	protected int count;

	/* The BY* parameters can each take a list of values. */

	/**
	 * Internal bySecond variable.
	 * @see #getBySecond
	 * @see #setBySecond
	 */
	protected int[] bySecond;

	/**
	 * Internal byMinute variable.
	 * @see #getByMinute
	 * @see #setByMinute
	 */
	protected int[] byMinute;

	/**
	 * Internal byHour variable.
	 * @see #getByHour
	 * @see #setByHour
	 */
	protected int[] byHour;

	/**
	 * Internal byDay variable.
	 * @see #getByDay
	 * @see #setByDay
	 */
	protected DayAndPosition[] byDay;

	/**
	 * Internal byMonthDay variable.
	 * @see #getByMonthDay
	 * @see #setByMonthDay
	 */
	protected int[] byMonthDay;

	/**
	 * Internal byYearDay variable.
	 * @see #getByYearDay
	 * @see #setByYearDay
	 */
	protected int[] byYearDay;

	/**
	 * Internal byWeekNo variable.
	 * @see #getByWeekNo
	 * @see #setByWeekNo
	 */
	protected int[] byWeekNo;

	/**
	 * Internal byMonth variable.
	 * @see #getByMonth
	 * @see #setByMonth
	 */
	protected int[] byMonth;

	protected AnyOwner owner;

	protected boolean timeZoneSensitive = false;

	protected String name;
	
	// used only as flag: 
	// null - all day(s) event
	// not null - NOT all day(s) event
	protected TimeZone timeZone;
	
	protected String uid;
	
	protected FreeBusyType freeBusy;  //access=field set by hibernate, so we can deal with nulls
	
	/* Constructors */

	/**
	 * Allocate a new Recurrence, with no recurrence frequency, starting 
	 * at time = 0, duration 0.
	 */
	public Event() {
		this(null, null, new Duration(), NO_RECURRENCE);
	}

	/**
	 * Allocate a new Recurrence, with no recurrence frequency.
	 * 
	 * @param start The start time, as a broken-down time. (Only the calendar
	 *              fields are used.  Use {@link #setWeekStart} rather
	 *              than {@link Calendar#setFirstDayOfWeek} to set the
	 *              first day of the week.)
	 *              
	 * @param dur  The duration.
	 * 
	 * @see #setWeekStart
	 */
	public Event(Calendar start, Duration dur) {
		this(start, null, dur, NO_RECURRENCE);
	}
	
	public Event(Calendar start, Calendar end) {
		this(start, end, buildDuration(start, end), NO_RECURRENCE);
	}

	public Event(Duration dur, int freq) {
		this(null, null, dur, freq);
	}
	
	public Event(Calendar start, Duration dur, int freq) {
		this(start, buildEnd(start, dur), dur, freq);
	}
	
	/*
	 * Constructs a Duration object from a start and end Calendar
	 * object.
	 */
	private static Duration buildDuration(Calendar start, Calendar end) {
		Duration dur = new Duration();
		setDurationInterval(dur, start, end);
		return dur;
	}
	
	private static void setDurationInterval(Duration dur, Calendar start, Calendar end) {
		long interval = (end.getTime().getTime() - start.getTime().getTime());
		dur.setInterval(interval);
	}
	
	private static Calendar buildEnd(Calendar start, Duration dur) {
		Calendar end = ((Calendar) start.clone());
	    end.setTime(new Date(start.getTime().getTime() + dur.getInterval()));
		return end;
	}
	
	/**
	 * Allocate a new Recurrence, with the specified frequency.
	 * 
	 * @param start The start time, as a broken-down time.  (Only the calendar
	 *              fields are used.  Use {@link #setWeekStart} rather
	 *              than {@link Calendar#setFirstDayOfWeek} to set the
	 *              first day of the week.)
	 * @param dur   The duration.
	 * @param freq  The recurrence frequency (one of
	 *        {@link Recurrence#DAILY}, {@link Recurrence#WEEKLY},
	 *        {@link Recurrence#MONTHLY}, {@link Recurrence#YEARLY},
	 *        or {@link Recurrence#NO_RECURRENCE}).
	 *         
	 * @see #setWeekStart
	 */
	public Event(Calendar start, Calendar end, Duration dur, int freq) {
		duration = ((Duration) dur.clone());
		frequency = freq;
		interval = 1;
		
		if (start != null) setDtStart(start);
		if (end   != null) setDtEnd(end);
				
		// Everything else gets initialized to 0 or null, which is what we want.
	}
	
	public Event(
			Calendar			dtStart,
			Calendar			dtEnd,
			Duration			duration,
			Integer				frequency,
			Integer				interval,
			Calendar			until,
			Integer				count,
			Integer				weekStart,
			Boolean				timeZoneSensitive,
			TimeZone			timeZone,
			String				uid,
			FreeBusyType		freeBusy,
			int[]				bySecond,
			int[]				byMinute,
			int[]				byHour,
			DayAndPosition[]	byDay,
			int[]				byMonthDay,
			int[]				byYearDay,
			int[]				byWeekNo,
			int[]				byMonth) {
		this(
			dtStart,
			null,	// null -> No dtCalcStart.
			dtEnd,
			null,	// null -> No dtCalcEnd.
			duration,
			frequency,
			interval,
			until,
			count,
			weekStart,
			timeZoneSensitive,
			timeZone,
			uid,
			freeBusy,
			bySecond,
			byMinute,
			byHour,
			byDay,
			byMonthDay,
			byYearDay,
			byWeekNo,
			byMonth);
	}
	
	public Event(
			Calendar			dtStart,
			Calendar			dtCalcStart,
			Calendar			dtEnd,
			Calendar			dtCalcEnd,
			Duration			duration,
			Integer				frequency,
			Integer				interval,
			Calendar			until,
			Integer				count,
			Integer				weekStart,
			Boolean				timeZoneSensitive,
			TimeZone			timeZone,
			String				uid,
			FreeBusyType		freeBusy,
			int[]				bySecond,
			int[]				byMinute,
			int[]				byHour,
			DayAndPosition[]	byDay,
			int[]				byMonthDay,
			int[]				byYearDay,
			int[]				byWeekNo,
			int[]				byMonth) {
		// It is important to call this base constructor, since it sets up some default.
		this(); 
		
		if (dtStart           != null) setDtStart(dtStart);
		if (dtEnd             != null) setDtEnd(dtEnd);
		if (duration          != null) setDuration(duration);
		if (frequency         != null) setFrequency(frequency.intValue());
		if (interval          != null) setInterval(interval.intValue());
		if (until             != null) setUntil(until);
		if (count             != null) setCount(count.intValue());
		if (weekStart         != null) setWeekStart(weekStart.intValue());
		// Due to some stupidity in earlier implementation and resulting backward compatibility issue, this needs to be negated.
		if (timeZoneSensitive != null) setTimeZoneSensitive(timeZoneSensitive.booleanValue()); 
		if (timeZone          != null) setTimeZone(timeZone);
		if (uid               != null) setUid(uid);
		if (freeBusy          != null) setFreeBusy(freeBusy);
		if (bySecond          != null) setBySecond(bySecond);
		if (byMinute          != null) setByMinute(byMinute);
		if (byHour            != null) setByHour(byHour);
		if (byDay             != null) setByDay(byDay);
		if (byMonthDay        != null) setByMonthDay(byMonthDay);
		if (byYearDay         != null) setByYearDay(byYearDay);
		if (byWeekNo          != null) setByWeekNo(byWeekNo);
		if (byMonth           != null) setByMonth(byMonth);
	}
	
	/**
	 * @hibernate.component class="org.kablink.teaming.domain.AnyOwner"
	 * @return
	 */
	public AnyOwner getOwner() {
		return owner;
	}

	public void setOwner(AnyOwner owner) {
		this.owner = owner;
	}

	public void setOwner(DefinableEntity entity) {
		owner = new AnyOwner(entity);
	}

	/**
  	 * @hibernate.property length="64"
	 * Used to tie event to command fields
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get a string representation of the start time of the recurrence.
	 * @return A string representing the start time.
	 */
	public String getDtStartString() {
		return generateDateTimeString(getDtStart());
	}

	public String getDtCalcStartString() {
		return generateDateTimeString(getDtCalcStart());
	}
	
	public String getLogicalStartString() {
		Calendar start = getLogicalStart();
		String reply = ((null == start) ? "" : generateDateTimeString(start));
		return reply;
	}

	/**
	 * Internal routines for hibernate.  Since all features of calendar are
	 * not saved in the SQL column, comparisons fail on dirty check.  So,
	 * use internal setup to persist only fields that can be compared.
	 * @hibernate.property
	 * @hibernate.column name="dtStart" not-null="true"
	 */
	protected Date getHDtStart() {
		return ((null == dtStart) ? null : dtStart.getTime());
	}

	protected Date getHDtCalcStart() {
		return ((null == dtCalcStart) ? null : dtCalcStart.getTime());
	}

	protected void setHDtStart(Date start) {
		if (null != start) {
			Calendar newD = new GregorianCalendar();
			newD.setTime(start);
			setDtStart(newD);
		}
	}

	protected void setHDtCalcStart(Date start) {
		if (null != start) {
			Calendar newD = new GregorianCalendar();
			newD.setTime(start);
			setDtCalcStart(newD);
		}
	}

	/**
	 * Get the start time of the recurrence.
	 * @return The start time.
	 */
	public Calendar getDtStart() {
		return dtStart;
	}

	public Calendar getDtCalcStart() {
		return dtCalcStart;
	}
	
	public Calendar getLogicalStart() {
		Calendar start = getDtStart();
		if (null == start) {
			start = getDtCalcStart();
		}
		return start;
	}

	/**
	 * Set the start time of the recurrence.
	 * @param start The start time, as a broken-down time.  (Only the calendar
	 *              fields are used.  Use {@link #setWeekStart} rather
	 *              than {@link Calendar#setFirstDayOfWeek} to set the
	 *              first day of the week.)
	 * @see #setWeekStart
	 */
	public void setDtStart(Calendar start) {
		if (null == start) {
			dtStart = null;
		}
		else {
			int oldStart = ((null == dtStart) ? Calendar.MONDAY : dtStart.getFirstDayOfWeek());
			dtStart = start;
			setCalendarWeekStart(dtStart, oldStart);
		}
	}
	
	public void setDtCalcStart(Calendar calcStart) {
		if (null == calcStart) {
			dtCalcStart = null;
		}
		else {
			int oldCalcStart = ((null == dtCalcStart) ? Calendar.MONDAY : dtCalcStart.getFirstDayOfWeek());
			dtCalcStart = calcStart;
			setCalendarWeekStart(dtCalcStart, oldCalcStart);
		}
	}

	/**
	 * Set the start time of the recurrence.
	 * @param start The start time, as a string.
	 * @throws IllegalArgumentException If the given string does not describe
	 *                          a valid Date-Time.
	 */
	public void setDtStart(String start) {
		setDtStart(parseDateTime(start));
	}

	public void setDtCalcStart(String calcStart) {
		setDtCalcStart(parseDateTime(calcStart));
	}

	/**
	 * Get the duration of the recurrence.
	 * @return The duration.
	 */
	public Duration getDuration() {
		return duration;
	}
	
	/**
	 * Has event any duration?
	 */
	public boolean hasDuration() {
		return ((null != duration) && (0 != duration.getInterval()));
	}

	/**
	 * Set the duration of the recurrence.
	 * 
	 * @param d The duration.
	 */
	public void setDuration(Duration d) {
		duration = d;
	}

	/**
	 * Set the duration of the recurrence.
	 * 
	 * @param str A string representing the duration.
	 * 
	 * @throws IllegalArgumentException If the given string does not describe
	 *                          a valid Duration.
	 */
	public void setDuration(String str) {
		Duration d = new Duration(str);
		setDuration(d);
	}

	/**
	 * Internal routines for hibernate.  Since all features of calendar are
	 * not saved in the SQL column, comparisons fail on dirty check.  So,
	 * use internal setup to persist only fields that can be compared.
	 * 
	 * @hibernate.property
	 * @hibernate.column name="dtEnd"
	 */
	protected Date getHDtEnd() {
		Calendar end = getDtEnd();
		return ((null == end) ? null : end.getTime());
	}

	protected Date getHDtCalcEnd() {
		Calendar calcEnd = getDtCalcEnd();
		return ((null == calcEnd) ? null : calcEnd.getTime());
	}

	protected void setHDtEnd(Date end) {
		if (null != end) {
			Calendar newDtEnd = new GregorianCalendar();
			newDtEnd.setTime(end);
			setDtEnd(newDtEnd);
		}
		else {
			dtEnd = null;
		}
	}
	
	protected void setHDtCalcEnd(Date calcEnd) {
		if (null != calcEnd) {
			Calendar newDtCalcEnd = new GregorianCalendar();
			newDtCalcEnd.setTime(calcEnd);
			setDtCalcEnd(newDtCalcEnd);
		}
		else {
			dtCalcEnd = null;
		}
	}

	/**
	 * Get the end time of the recurrence.
	 * The broken-down time of the returned dtEnd will be correct, though
	 * its time in milliseconds may not be.
	 * 
	 * @return The end time.
	 */
	public Calendar getDtEnd() {
		return dtEnd;
	}
	
	public Calendar getDtCalcEnd() {
		return dtCalcEnd;
	}
	
	public Calendar getLogicalEnd() {
		Calendar end = getDtEnd();
		if (null == end) {
			end = getDtCalcEnd();
		}
		return end;
	}

	/**
	 * Get a string representing the end time of the recurrence.
	 * @return A string representation of the end time.
	 */
	public String getDtEndString() {
		return generateDateTimeString(getDtEnd());
	}

	public String getDtCalcEndString() {
		return generateDateTimeString(getDtCalcEnd());
	}
	
	public String getLogicalEndString() {
		Calendar end = getLogicalEnd();
		String reply = ((null == end) ? "" : generateDateTimeString(end));
		return reply;
	}

	/**
	 * Set the end time of the recurrence.
	 * @param end The end time.
	 */
	public void setDtEnd(Calendar end) {
		dtEnd = end;
		if ((null != dtStart) && (null != dtEnd)) {
			setDurationInterval(duration, dtStart, dtEnd);
		}
	}

	public void setDtCalcEnd(Calendar end) {
		dtCalcEnd = end;
	}

	/**
	 * Set the end time of the recurrence.
	 * @param end The end time, as a string.
	 * @throws IllegalArgumentException If the given string does not describe
	 *                          a valid Date-Time.
	 */
	public void setDtEnd(String end) {
		setDtEnd(parseDateTime(end));
	}

	public void setDtCalcEnd(String end) {
		setDtCalcEnd(parseDateTime(end));
	}

	/**
	 * @hibernate.property
	 * Get the frequency of the recurrence.
	 * @return The recurrence frequency (one of
	 *         {@link Recurrence#SECONDLY}, {@link Recurrence#MINUTELY},
	 *         {@link Recurrence#HOURLY},
	 *         {@link Recurrence#DAILY}, {@link Recurrence#WEEKLY},
	 *         {@link Recurrence#MONTHLY}, {@link Recurrence#YEARLY},
	 *         or {@link Recurrence#NO_RECURRENCE}).
	*/
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Get the frequency of the recurrence, as a string.
	 * @return A string representing the recurrence frequency (one of
	 *         "SECONDLY", "MINUTELY", "HOURLY", "DAILY", "WEEKLY", "MONTHLY",
	 *         "YEARLY", or <code>null</code> representing no recurrence).
	 */
	public String getFrequencyString() {
		switch (frequency) {
		case SECONDLY:
			return "SECONDLY";
		case MINUTELY:
			return "MINUTELY";
		case HOURLY:
			return "HOURLY";
		case DAILY:
			return "DAILY";
		case WEEKLY:
			return "WEEKLY";
		case MONTHLY:
			return "MONTHLY";
		case YEARLY:
			return "YEARLY";
		case NO_RECURRENCE:
			return null;
		}
    throw new
      IllegalStateException("Internal Error: " +
                            "Invalid frequency in getFrequencyString");
	}

	/**
	 * Set the frequency of the recurrence.
	 * @param freq The recurrence frequency (one of
	 *         {@link Recurrence#SECONDLY}, {@link Recurrence#MINUTELY},
	 *         {@link Recurrence#HOURLY},
	 *            {@link Recurrence#DAILY}, {@link Recurrence#WEEKLY},
	 *         {@link Recurrence#MONTHLY}, {@link Recurrence#YEARLY},
	 *         or {@link Recurrence#NO_RECURRENCE}).
	 * @throws IllegalArgumentException if the frequency isn't one of
	 *         the above values.
	 */
	public void setFrequency(int freq) {
    if (frequency != SECONDLY && frequency != MINUTELY &&
        frequency != HOURLY && frequency != DAILY && frequency != WEEKLY &&
        frequency != MONTHLY && frequency != YEARLY &&
        frequency != NO_RECURRENCE) {
			throw new IllegalArgumentException("Invalid frequency");
		}
		frequency = freq;
	}

	/**
	 * Set the frequency of the recurrence.
	 * @param freq A string representing the recurrence frequency (one of
	 *            "SECONDLY", "MINUTELY", "HOURLY", "DAILY", "WEEKLY", "MONTHLY",
	 *            "YEARLY", or "" or <code>null</code> representing no
	 *            recurrence).
	 * @throws IllegalArgumentException if the frequency isn't one of
	 *         the above values.
	 */
	public void setFrequency(String freq) {
		if (freq == null || freq.length() == 0) {
			frequency = NO_RECURRENCE;
	    }      
	    else if (freq.compareToIgnoreCase("SECONDLY") == 0) {
				frequency = SECONDLY;
	    }
	    else if (freq.compareToIgnoreCase("MINUTELY") == 0) {
				frequency = MINUTELY;
	    }
	    else if (freq.compareToIgnoreCase("HOURLY") == 0) {
				frequency = HOURLY;
	    }
	    else if (freq.compareToIgnoreCase("DAILY") == 0) {
				frequency = DAILY;
	    }
	    else if (freq.compareToIgnoreCase("WEEKLY") == 0) {
				frequency = WEEKLY;
	    }
	    else if (freq.compareToIgnoreCase("MONTHLY") == 0) {
				frequency = MONTHLY;
	    }
	    else if (freq.compareToIgnoreCase("YEARLY") == 0) {
			frequency = YEARLY;
		}
	    else {
	      throw new IllegalArgumentException("Invalid frequency \"" + freq + "\"");
		}
	}

	/**
	 * @hibernate.property
	 * @hibernate.column name="repeatInterval"
	 * Get the interval of the recurrence.
	 * @return The recurrence interval.
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * Get the interval of the recurrence, as a string.
	 * @return A string representing the recurrence interval, or
	 *         <code>null</code> if no interval is defined.
	 */
	public String getIntervalString() {
		if (interval <= 1) {
			return null;
		}

		return String.valueOf(getInterval());
	}

	/**
	 * Set the interval of the recurrence.
	 * @param intr The recurrence interval.
	 */
	public void setInterval(int intr) {
		interval = intr;
	}

	/**
	 * Set the interval of the recurrence, from a string.
	 * 
	 * @param intr The recurrence interval, as a string, or <code>null</code>.
	 * @throws IllegalArgumentException if the argument isn't a valid interval.
	 */
  public void setInterval(String intr)
  {
		if (intr == null || intr.length() == 0) {
			interval = 1;
			return;
		}

		int i;

		try {
			i = Integer.parseInt(intr);
		}
    catch (NumberFormatException e) {
      throw new
        IllegalArgumentException("Bad interval string \"" + intr + "\"");
    }

		if (i < 1) {
			throw new IllegalArgumentException("Bad interval value " + intr);
		}

		interval = i;
	}

	/**
	 * Internal routines for hibernate.  Since all features of calendar are
	 * not saved in the SQL column, comparisons fail on dirty check.  So,
	 * use internal setup to persist only fields that can be compared.
	 * @hibernate.property
	 * @hibernate.column name="until"
	 */
	protected Date getHUntil() {
  	if (getCount() == -1) return getUntil().getTime();
		return null;
	}

	protected void setHUntil(Date until) {
  	if (until == null) this.until = null;
		else {
			this.until = new GregorianCalendar();
			this.until.setTime(until);
		}
	}

	/**
	 * Get the upper bound of the recurrence.
	 * If <code>count</code> has been set, this computes
	 * <code>until</code> from <code>count</code>.
	 * @return The upper bound. <code>null</code> if unbounded.
	 * @see #computeUntilFromCount()
	 */
	public Calendar getUntil() {
		computeUntilFromCount(false);
		return until;
		// return (until != null ? (Calendar)until.clone() : null);
	}

	public Calendar getUntilWithMaxLoopsIfNeeded() {
		computeUntilFromCount(true);
		return until;
		// return (until != null ? (Calendar)until.clone() : null);
	}

	
	/**
	 * Get a string representing the upper bound of the recurrence, or the empty
	 * string if unbounded.
	 * If <code>count</code> has been set, this computes <code>until</code> from
	 * <code>count</code>.
	 * @return A string representation of the upper bound.
	 * <code>null</code> if unbounded.
	 */
	public String getUntilString() {
		computeUntilFromCount(false);

		if (until == null) {
			return null;
		}

		return generateDateTimeString(until);
	}

	/**
	 * Set the upper bound of the recurrence.
	 * @param u The upper bound, or <code>null</code> if unbounded.
	 * @throws IllegalArgumentException if non-null and <code>count</code>
	 *                                  is set.
	 */
	public void setUntil(Calendar u) {
		if (u == null) {
			until = null;
			return;
		}

		if (count != 0) {
      throw new IllegalArgumentException("Cannot set both count and until");
		}

		until = u;
	}

	/**
	 * Set the upper bound of a recurrence, based on a string.
	 * @param u A string representation of the upper bound, or "" or
	 *            <code>null</code> if unbounded.
	 * @throws IllegalArgumentException if non-""/non-<code>null</code> and
	 *                          <code>count</code> is set.
	 * @throws IllegalArgumentException If the given string does not describe
	 *                          a valid Date-Time.
	 */
	public void setUntil(String u) {
		if (u == null || u.length() == 0) {
			setUntil((Calendar) null);
    }
    else {
			setUntil(parseDateTime(u));
		}
	}

	/**
	 * Need internal routine for hibernate. Don't want to make decisions based
	 * on properties that are not loaded yet as is done in getCount
	 * @hibernate.property
	 * @hibernate.column name="repeatCount"
	 */
	@SuppressWarnings("unused")
	private int getHCount() {
		return count;
	}

	@SuppressWarnings("unused")
	private void setHCount(int count) {
		this.count = count;
	}

	/**
	 * Get the repeat count of the recurrence.
	 * @return The repeat count.  0 if it is unset; -1 if it is
	 *         unknown (because 'until' is set).
	 */
	public int getCount() {
		if (count == 0 && until != null) {
			return -1;
		}
		return count;
	}

	/**
	 * Get the repeat count of the recurrence, as a string.
	 * @return A string representation of the repeat count. <code>null</code>
	 *         if it is unset or unknown (because 'until' is set).
	 */
	public String getCountString() {
		if (count == 0) {
			return null;
		}

		return String.valueOf(getCount());
	}

	/**
	 * Set the repeat count of the recurrence.
	 * @param c The repeat count, or <code>0</code> if unbounded.
	 * @throws IllegalArgumentException If a non-computed <code>until</code>
	 *         has been set.
	 * @see #computeUntilFromCount()
	 */
	public void setCount(int c) {
		if (c != 0 && count == 0 && until != null) {
      throw new IllegalArgumentException("Cannot set both count and until");
		}
		if (c != 0 || count != 0) {
			until = null;
		}
		count = c;
	}

	/*
	 * Set the repeat count of the recurrence.
	 * @param c The repeat count, or <code>0</code> if unbounded.
	 * @throws IllegalArgumentException If a non-computed <code>until</code>
	 *         has been set.
	 * @throws IllegalArgumentException if the argument isn't a valid interval.
	 */
  public void setCount(String c)
  {
		if (c == null || c.length() == 0) {
			setCount(0);
			return;
		}

		int cnt;

		try {
			cnt = Integer.parseInt(c);
		}
    catch (NumberFormatException e) {
      throw new
        IllegalArgumentException("Bad count string \"" + c + "\"");
    }

		if (cnt < 1) {
			throw new IllegalArgumentException("Bad count value " + c);
		}

		setCount(cnt);
	}

	/**
	 * @hibernate.property
	 * Get the first day of the week for this recurrence.
	 * 
	 * @return The day of the week (one of {@link Calendar#SUNDAY},
	 *         {@link Calendar#MONDAY}, {@link Calendar#TUESDAY},
	 *         {@link Calendar#WEDNESDAY}, {@link Calendar#THURSDAY},
	 *         {@link Calendar#FRIDAY}, or {@link Calendar#SATURDAY}).
	 */
	public int getWeekStart() {
		int weekStart;
		weekStart = ((null == dtStart) ? Calendar.MONDAY : dtStart.getFirstDayOfWeek());
		return weekStart;
	}

	/**
	 * Get the first day of the week for this recurrence, as a string.
	 * 
	 * @return The first day of the week (one of "SU", "MO", "TU", "WE",
	 *         "TH", "FR", "SA").
	 */
	public String getWeekStartString() {
		return DayAndPosition.generateDayOfWeek(getWeekStart());
	}

	/**
	 * Set the first day of the week for this recurrence.
	 * 
	 * @param weekstart The day of the week (one of {@link Calendar#SUNDAY},
	 *            {@link Calendar#MONDAY}, {@link Calendar#TUESDAY},
	 *            {@link Calendar#WEDNESDAY}, {@link Calendar#THURSDAY},
	 *            {@link Calendar#FRIDAY}, or {@link Calendar#SATURDAY}).
	 */
	public void setWeekStart(int weekstart) {
		if (null != dtStart) {
			dtStart.setFirstDayOfWeek(weekstart);
		}
	}

	/**
	 * Set the first day of the week for this recurrence.
	 * 
	 * @param weekstart The day of the week, as a string (one of "SU", "MO", "TU"
	 *            "WE", "TH", "FR", "SA").
	 * @throws IllegalArgumentException If the string is not a valid weekday.
	 */
	public void setWeekStart(String weekstart) {
		if (weekstart == null || weekstart.length() == 0)
			return;

		int ws = DayAndPosition.parseDayOfWeek(weekstart);

		if (ws == DayAndPosition.NO_WEEKDAY) {
      throw new
        IllegalArgumentException("Bad week start \"" + weekstart + "\"");
		}

		setWeekStart(ws);
	}

	/**
	 * Get the array of seconds-of-the-minute constraints for this recurrence.
	 * 
	 * @return The array of seconds of the minute.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public int[] getBySecond() {
    if (bySecond == null) { return null; }

		int[] b = new int[bySecond.length];
		System.arraycopy(bySecond, 0, b, 0, bySecond.length);

		return b;
	}

	/**
	 * Get a string representing the seconds-of-the-minute constraints for
	 * this recurrence.
	 * 
	 * @return The list of seconds of the minute, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getBySecondString() {
		return generateIntSetString(getBySecond());
	}

	/**
	 * Set the array of seconds-of-the-minute constraints for this recurrence.
	 * 
	 * @param b The array of seconds of the minute.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setBySecond(int[] b) {
    if (b == null) { bySecond = null; return; }

		bySecond = new int[b.length];
		System.arraycopy(b, 0, bySecond, 0, b.length);
	}

	/**
	 * Set the list of seconds-of-the-minute constraints for this recurrence.
	 * 
	 * @param b The seconds-of-the-minute constraints, as a comma-separated
	 *          string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a
	 *         second is outside the range 0 - 60.
	 */
	public void setBySecond(String b) {
		setBySecond(parseIntSet(b, "second", 0, 60, false));
	}

	/**
	 * Get the array of minutes-of-the-hour constraints for this recurrence.
	 * 
	 * @return The array of minutes of the hour.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public int[] getByMinute() {
    if (byMinute == null) { return null; }

		int[] b = new int[byMinute.length];
		System.arraycopy(byMinute, 0, b, 0, byMinute.length);

		return b;
	}

	/**
	 * Get a string representing the minutes-of-the-hour constraints for
	 * this recurrence.
	 * 
	 * @return The list of minutes of the hour, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getByMinuteString() {
		return generateIntSetString(getByMinute());
	}

	/**
	 * Set the array of minutes-of-the-hour constraints for this recurrence.
	 * 
	 * @param b The array of minutes of the hour.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setByMinute(int[] b) {
    if (b == null) { byMinute = null; return; }

		byMinute = new int[b.length];
		System.arraycopy(b, 0, byMinute, 0, b.length);
	}

	/**
	 * Set the list of minutes-of-the-hour constraints for this recurrence.
	 * 
	 * @param b The minutes-of-the-hour constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a
	 *         minute is outside the range 0 - 59.
	 */
	public void setByMinute(String b) {
		setByMinute(parseIntSet(b, "minute", 0, 59, false));
	}

	/**
	 * Get the array of hours-of-the-day constraints for this recurrence.
	 * 
	 * @return The array of hours of the day.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public int[] getByHour() {
    if (byHour == null) { return null; }

		int[] b = new int[byHour.length];
		System.arraycopy(byHour, 0, b, 0, byHour.length);

		return b;
	}

	/**
	 * Get a string representing the hours-of-the-day constraints for
	 * this recurrence.
	 * 
	 * @return The list of hours of the day, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getByHourString() {
		return generateIntSetString(getByHour());
	}

	/**
	 * Set the array of hours-of-the-day constraints for this recurrence.
	 * 
	 * @param b The array of hours of the day.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setByHour(int[] b) {
    if (b == null) { byHour = null; return; }

		byHour = new int[b.length];
		System.arraycopy(b, 0, byHour, 0, b.length);
	}

	/**
	 * Set the list of hours-of-the-day constraints for this recurrence.
	 * 
	 * @param b The hours-of-the-day constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if an hour
	 *         is outside the range 0 - 23.
	 */
	public void setByHour(String b) {
		setByHour(parseIntSet(b, "hour", 0, 23, false));
	}

	/**
	 * Get the array of day-of-the-week and position constraints for this
	 * recurrence.
	 * 
	 * @return The array of days of the week and positions.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public DayAndPosition[] getByDay() {
    if (byDay == null) return null;

		try {
			return (DayAndPosition[]) arrayclone(byDay);
    }
    catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}

	/**
	 * Get a string representing the day-of-the-week and position constraints for
	 * this recurrence.
	 * 
	 * @return The list of day-of-the-week and position constraints,
	 *         comma-separated.  <code>null</code> if this recurrence has
	 *         no such constraints.
	 */
	public String getByDayString() {
		return generateDaySetString(getByDay());
	}

	/**
	 * Set the array of day-of-the-week and position constraints for this
	 * recurrence.
	 * 
	 * @param b The array of days of the week and positions.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setByDay(DayAndPosition[] b) {
    if (b == null) { byDay = null; return; }

		try {
			byDay = (DayAndPosition[]) arrayclone(b);
    }
    catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e.getLocalizedMessage());
		}
	}

	/**
	 * Utility function to clone an array of objects. Used by {@link getByDay},
	 * {@link setByDay}, and {@link clone}.
	 * @param src the source array
	 * @return a copy of the array, with every object cloned.
	 * @throws CloneNotSupportedException if the array is not an array of
	 *         primitive types, and any object in the array does not
	 *         support the <code>Cloneable</code> interface.
	 * @throws IllegalArgumentException if the passed argument is not an array.
	 */
	@SuppressWarnings("unchecked")
	private Object arrayclone(Object src) throws CloneNotSupportedException {
    if (src == null) return null;

		if (!src.getClass().isArray()) {
			throw new IllegalArgumentException("src argument is not an array");
		}

		Object dest = Array.newInstance(src.getClass().getComponentType(),
				Array.getLength(src));

		if (src.getClass().getComponentType().isPrimitive()) {
			System.arraycopy(src, 0, dest, 0, Array.getLength(src));
		} else {
			Class[] nilParams = new Class[0];
			Object[] nilArgs = new Object[0];

			Object[] aSrc = (Object[]) src;
			Object[] aDest = (Object[]) dest;

			for (int i = 0; i < aSrc.length; i++) {
				try {
					/*
					 * Try to invoke the Object's clone method, even if this
					 * wouldn't normally be allowed.
					 */
					Method clone = aSrc[i].getClass().getDeclaredMethod(
							"clone", nilParams);
					aDest[i] = clone.invoke(aSrc[i], nilArgs);
				} catch (Exception e) {
					throw new CloneNotSupportedException(e.getLocalizedMessage());
				}
			}
		}
		return dest;
	}

	/**
	 * Set the list of day-of-the-week and position constraints for this
	 * recurrence.
	 * 
	 * @param b The hours-of-the-day constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a
	 *         day or position is outside its valid range.
	 */
	public void setByDay(String b) {
		setByDay(parseDaySet(b));
	}

	/**
	 * Get the array of day-of-the-month constraints for this recurrence.
	 * 
	 * @return The array of days of the month.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public int[] getByMonthDay() {
		if (byMonthDay == null) {
			return null;
		}

		int[] b = new int[byMonthDay.length];
		System.arraycopy(byMonthDay, 0, b, 0, byMonthDay.length);

		return b;
	}

	/**
	 * Get a string representing the day-of-the-month constraints for
	 * this recurrence.
	 * 
	 * @return The list of days of the month, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getByMonthDayString() {
		return generateIntSetString(getByMonthDay());
	}

	/**
	 * Set the array of day-of-the-month constraints for this recurrence.
	 * 
	 * @param b The array of days of the month.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setByMonthDay(int[] b) {
		if (b == null) {
			byMonthDay = null;
			return;
		}

		byMonthDay = new int[b.length];
		System.arraycopy(b, 0, byMonthDay, 0, b.length);
	}

	/**
	 * Set the list of day-of-the-month constraints for this recurrence.
	 * 
	 * @param b The day-of-the-month constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a day
	 *         is outside the range 1 - 31 or -31 to -1.
	 */
	public void setByMonthDay(String b) {
		setByMonthDay(parseIntSet(b, "monthday", 1, 31, true));
	}

	/**
	 * Get the array of day-of-the-year constraints for this recurrence.
	 * 
	 * @return The array of days of the year.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public int[] getByYearDay() {
		if (byYearDay == null) {
			return null;
		}

		int[] b = new int[byYearDay.length];
		System.arraycopy(byYearDay, 0, b, 0, byYearDay.length);

		return b;
	}

	/**
	 * Get a string representing the day-of-the-year constraints for
	 * this recurrence.
	 * 
	 * @return The list of days of the year, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getByYearDayString() {
		return generateIntSetString(getByYearDay());
	}

	/**
	 * Set the array of day-of-the-year constraints for this recurrence.
	 * 
	 * @param b The array of days of the year.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setByYearDay(int[] b) {
		if (b == null) {
			byYearDay = null;
			return;
		}

		byYearDay = new int[b.length];
		System.arraycopy(b, 0, byYearDay, 0, b.length);
	}

	/**
	 * Set the list of day-of-the-year constraints for this recurrence.
	 * 
	 * @param b The day-of-the-year constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a day
	 *         is outside the range 1 - 366 or -366 to -1.
	 */
	public void setByYearDay(String b) {
		setByYearDay(parseIntSet(b, "yearday", 1, 366, true));
	}

	/**
	 * Get the array of week-number constraints for this recurrence.
	 * 
	 * @return The array of week numbers.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public int[] getByWeekNo() {
		if (byWeekNo == null) {
			return null;
		}

		int[] b = new int[byWeekNo.length];
		System.arraycopy(byWeekNo, 0, b, 0, byWeekNo.length);

		return b;
	}

	/**
	 * Get a string representing the week-number constraints for
	 * this recurrence.
	 * 
	 * @return The list of week numbers, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getByWeekNoString() {
		return generateIntSetString(getByWeekNo());
	}

	/**
	 * Set the array of week-number constraints for this recurrence.
	 * 
	 * @param b The array of week numbers.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public void setByWeekNo(int[] b) {
		if (b == null) {
			byWeekNo = null;
			return;
		}

		byWeekNo = new int[b.length];
		System.arraycopy(b, 0, byWeekNo, 0, b.length);
	}

	/**
	 * Set the list of week-number constraints for this recurrence.
	 * 
	 * @param b The week-number constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a
	 *         week number is outside the range 1 - 53 or -53 to -1.
	 */
	public void setByWeekNo(String b) {
		setByWeekNo(parseIntSet(b, "weekno", 1, 53, true));
	}

	/**
	 * Get the array of month constraints for this recurrence. Months are
	 * specified using the constants from {@link java.util.Calendar}
	 * ({@link Calendar#JANUARY}, etc.).
	 * 
	 * @return The array of months.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @see java.util.Calendar#JANUARY
	 * @see java.util.Calendar#FEBRUARY
	 * @see java.util.Calendar#MARCH
	 * @see java.util.Calendar#APRIL
	 * @see java.util.Calendar#MAY
	 * @see java.util.Calendar#JUNE
	 * @see java.util.Calendar#JULY
	 * @see java.util.Calendar#AUGUST
	 * @see java.util.Calendar#SEPTEMBER
	 * @see java.util.Calendar#OCTOBER
	 * @see java.util.Calendar#NOVEMBER
	 * @see java.util.Calendar#DECEMBER
	 */
	public int[] getByMonth() {
		if (byMonth == null) {
			return null;
		}

		int[] b = new int[byMonth.length];
		System.arraycopy(byMonth, 0, b, 0, byMonth.length);

		return b;
	}

	/**
	 * Get a string representing the month constraints for this recurrence.
	 * Months are counted as January = 1.
	 * 
	 * @return The list of months, comma-separated.
	 *         <code>null</code> if this recurrence has no such constraints.
	 */
	public String getByMonthString() {
		int[] b = getByMonth();
		if (b == null) {
			return ("");
		}

		for (int i = 0; i < b.length; i++) {
			b[i] += 1 - Calendar.JANUARY;
		}
		return generateIntSetString(b);
	}

	/**
	 * Set the array of month constraints for this recurrence.  Months should
	 * be specified using the constants from {@link java.util.Calendar}
	 * ({@link Calendar#JANUARY}, etc.).
	 * 
	 * @param b The array of months.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @see java.util.Calendar#JANUARY
	 * @see java.util.Calendar#FEBRUARY
	 * @see java.util.Calendar#MARCH
	 * @see java.util.Calendar#APRIL
	 * @see java.util.Calendar#MAY
	 * @see java.util.Calendar#JUNE
	 * @see java.util.Calendar#JULY
	 * @see java.util.Calendar#AUGUST
	 * @see java.util.Calendar#SEPTEMBER
	 * @see java.util.Calendar#OCTOBER
	 * @see java.util.Calendar#NOVEMBER
	 * @see java.util.Calendar#DECEMBER
	 */
	public void setByMonth(int[] b) {
		if (b == null) {
			byMonth = null;
			return;
		}

		byMonth = new int[b.length];
		System.arraycopy(b, 0, byMonth, 0, b.length);
	}

	/**
	 * Set the list of month constraints for this recurrence. Months are counted
	 * as January = 1.
	 * 
	 * @param b The month constraints, as a comma-separated string.
	 *         <code>null</code> if this recurrence has no such constraints.
	 * @throws IllegalArgumentException If the string is malformed, or if a month
	 *         is outside the range 1 - 12.
	 */
	public void setByMonth(String b) {
		int[] bm = parseIntSet(b, "month", 1, 12, false);

		if (bm != null) {
			for (int i = 0; i < bm.length; i++) {
				bm[i] += Calendar.JANUARY - 1;
			}
		}
		setByMonth(bm);
	}

	/**
	 * Test if the specified time falls within a repetition of this
	 * recurrence.  See
	 * <a href="../../../algorithm.txt"><tt>algorithm.txt</tt></a> for details
	 * of how this works. 
	 * 
	 * @param current The "current" time, i.e. the time which is being
	 *                considered.
	 * @return Whether it falls within a recurrence.
	 */
	public boolean isInRecurrence(Calendar current) {
		return isInRecurrence(current, false);
	}

	public TimeZone getTimeZone() {
		return TimeZoneHelper.fixTimeZone(timeZone);
	}
	
	public boolean isAllDayEvent() {
		return timeZone == null;
	}
	
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = TimeZoneHelper.fixTimeZone(timeZone);
	}
	
	/**
	 * Debugging interface to {@link #isInRecurrence(java.util.Calendar)}.
	 * 
	 * @param current The "current" time, i.e. the time which is being
	 *                considered.
	 * @param debug   Whether to print debugging information.
	 * @return Whether it falls within a recurrence.
	 * @see #isInRecurrence(java.util.Calendar)
	 */
	public boolean isInRecurrence(Calendar current, boolean debug) {
		Calendar myCurrent = (Calendar) current.clone();

		setCalendarWeekStart(myCurrent, dtStart.getFirstDayOfWeek());

		if (myCurrent.getTime().getTime() < dtStart.getTime().getTime()) {
			// The current time is earlier than the start time.
			if (debug) {
				System.err.println("current < start");
			}
			return false;
		}

		if (myCurrent.getTime().getTime() < dtStart.getTime().getTime()
				+ duration.getInterval()) {
			// We are within "duration" of dtStart.
			if (debug) {
				System.err.println("within duration of start");
			}
			return true;
		}

		/* If we have 'count' and not 'until', compute 'until' now. */
		computeUntilFromCount(false);

		Calendar candidate = internalGetCandidateStartTime(myCurrent, false,
				debug);
		/* Check whether internalGetCandidateStartTime could find a candidate */
		if (candidate == null) {
			if (debug) {
				System.err.println("No candidate start times");
			}
			return false;
		}
		if (candidate.getTime().getTime() > myCurrent.getTime().getTime()) {
			throw new IllegalStateException(
					"Internal error: candidate > current");
		}

		/* Loop over ranges for the duration. */
		while (candidate.getTime().getTime() + duration.getInterval() > myCurrent
				.getTime().getTime()) {
			if (candidateIsInRecurrence(candidate, debug)) {
				return true;
			}

			long oldCandidateTime = candidate.getTime().getTime();

			/* Roll back to one second previous, and try again. */
			candidate.add(Calendar.SECOND, -1);

			/* Make sure we haven't rolled back to before dtStart. */
			if (candidate.getTime().getTime() < dtStart.getTime().getTime()) {
				if (debug) {
					System.err.println("No candidates after dtStart");
				}
				return false;
			}

			candidate = internalGetCandidateStartTime(candidate, false, debug);

			/*
			 * Check whether internalGetCandidateStartTime could find a
			 * candidate
			 */
			if (candidate == null) {
				if (debug) {
					System.err.println("No candidate start times");
				}
				return false;
			}
			if (candidate.getTime().getTime() >= oldCandidateTime) {
				throw new IllegalStateException(
						"Internal error: candidate >= old candidate");
			}
		}

		if (debug) {
			System.err.println("No matching candidates");
		}
		return false;
	}

	/**
	 * Check whether a particular candidate start time falls within a
	 * repetition of the recurrence.  Support function for
	 * {@link #isInRecurrence}.
	 * @param candidate The candidate start time to be checked.
	 * @param debug     Whether to print debugging information.
	 * @return whether it falls within a recurrence
	 */
	protected boolean candidateIsInRecurrence(Calendar candidate, boolean debug) {
		if (until != null
				&& candidate.getTime().getTime() > until.getTime().getTime()) {
			// After "until"
			if (debug) {
				System.err.println("after until");
			}
			return false;
		}

		if (getRecurrenceCount(candidate) % interval != 0) {
			// Not a repetition of the interval
			if (debug) {
				System.err.println("not an interval rep");
			}
			return false;
		}

		if (!matchesBySecond(candidate) || !matchesByMinute(candidate)
				|| !matchesByHour(candidate) || !matchesByDay(candidate)
				|| !matchesByMonthDay(candidate)
				|| !matchesByYearDay(candidate) || !matchesByWeekNo(candidate)
				|| !matchesByMonth(candidate)) {
			// Doesn't match a by* rule
			if (debug) {
				System.err.println("doesn't match a by*");
			}
			return false;
		}
		if (debug) {
			System.err.println("All checks succeeded");
		}
		return true;
	}

	/**
	 * Get the minimum interval for this recurrence, based on 
	 * the by* parameters and the frequency.
	 * 
	 * @return One of {@link #SECONDLY}, {@link #MINUTELY}, {@link #HOURLY},
	 *         {@link #DAILY}, {@link #WEEKLY}, {@link #MONTHLY},
	 *         {@link #YEARLY}, or {@link #NO_RECURRENCE} (infinite).
	 */
	protected int getMinimumInterval() {
		if (frequency == SECONDLY || (bySecond != null && bySecond.length > 0)) {
			return SECONDLY;
		}
		if (frequency == MINUTELY || (byMinute != null && byMinute.length > 0)) {
			return MINUTELY;
		}
		if (frequency == HOURLY || (byHour != null && byHour.length > 0)) {
			return HOURLY;
		}
		if (frequency == DAILY || (byDay != null && byDay.length >0) || (byMonthDay != null && byMonthDay.length >0)
				|| (byYearDay != null && byYearDay.length > 0)) {
			return DAILY;
		} else if (frequency == WEEKLY || (byWeekNo != null && byWeekNo.length > 0)) {
			return WEEKLY;
		} else if (frequency == MONTHLY || (byMonth != null && byMonth.length > 0)) {
			return MONTHLY;
		} else if (frequency == YEARLY) {
			return YEARLY;
		} else if (frequency == NO_RECURRENCE) {
			return NO_RECURRENCE;
		} else {
			// Shouldn't happen
			throw new IllegalStateException(
					"Internal error: Unknown frequency value");
		}
	}

	/**
	 * Get the candidate start time for this recurrence, for a given date.
	 * The candidate start time is the previous/next repetition of the minimum
	 * interval, counting from dtStart.  See
	 * <a href="../../../algorithm.txt"><tt>algorithm.txt</tt></a> for more
	 * details.
	 * 
	 * @param current    The "current" time, i.e. the time which is being
	 *                   considered.
	 * @param is_forward Whether to find the next (true) or previous (false)
	 *                   candidate start time.
	 * @param debug      Whether to print debugging information.
	 * 
	 * @return The candidate start time, or null if there is none.
	 */
	public Calendar getCandidateStartTime(Calendar current, boolean is_forward,
			boolean debug) {
		Calendar myCurrent = (Calendar) current.clone();
		setCalendarWeekStart(myCurrent, dtStart.getFirstDayOfWeek());

		computeUntilFromCount(false);

		return internalGetCandidateStartTime(current, is_forward, debug);
	}

	/**
	 * Internal version of getCandidateStartTime. Doesn't check various
	 * parameters.
	 * 
	 * @param current    The "current" time, i.e. the time which is being
	 *                   considered.
	 * @param is_forward Whether to find the next (true) or previous (false)
	 *                   candidate start time.
	 * @param debug      Whether to print debugging information.
	 * 
	 * @return The candidate start time, or null if there is none.
	 * 
	 * @see #getCandidateStartTime(Calendar, boolean, boolean)
	 */
	protected Calendar internalGetCandidateStartTime(Calendar current,
			boolean is_forward, boolean debug) {
		// XXX Testing
		// Force current to be recalculated.
		// current.clear(Calendar::WEEK_OF_YEAR);
		
		if (dtStart.getTime().getTime() > current.getTime().getTime()) {
			if (debug) {
				System.err.println("Current time before DtStart");
			}
			if (is_forward) {
				return (Calendar) dtStart.clone();
			} else {
				return null;
			}
		}
		/*
		 * If 'until' has not yet been computed, this next statement is a no-op.
		 * That's OK.
		 */
		if (is_forward && until != null
				&& until.getTime().getTime() < current.getTime().getTime()) {
			if (debug) {
				System.err.println("Current time after Until");
			}
			return null;
		}

		if (frequency == NO_RECURRENCE) {
			if (is_forward) {
				return null;
			} else {
				return (Calendar) dtStart.clone();
			}
		}
		int minInterval = getMinimumInterval();

		
		Calendar candidate = (Calendar) current.clone();
		if (minInterval == SECONDLY) {
			return candidate;
		}
		adjust_constant_length_field(Calendar.SECOND, dtStart, candidate,
				is_forward);
		if (minInterval == MINUTELY) {
			return candidate;
		}
		adjust_constant_length_field(Calendar.MINUTE, dtStart, candidate,
				is_forward);
		if (minInterval == HOURLY) {
			return candidate;
		}
		adjust_constant_length_field(Calendar.HOUR_OF_DAY, dtStart, candidate,
				is_forward);
		switch (minInterval) {
		case DAILY:
			/* No more adjustments needed */
			break;
		case WEEKLY:
			adjust_constant_length_field(Calendar.DAY_OF_WEEK, dtStart,
					candidate, is_forward);
			break;
		case MONTHLY:
			adjust_day_of_month(dtStart, candidate, is_forward);
			break;
		case YEARLY:
			adjust_day_of_year(dtStart, candidate, is_forward);
			break;
		default:
			// Shouldn't get here
			throw new IllegalStateException("Internal error: "
					+ "unknown frequency value, shouldn't get here");

		}

		return candidate;
	}

	/**
	 * Adjust the time specified by <code>candidate</code> so that the field
	 * <code>field</code> equals that of <code>start</code>. If
	 * <code>is_forward</code>, increase the time, otherwise reduce it.
	 * Adjust other fields appropriately. Only works for constant-length fields
	 * (e.g., {@link Calendar#SECOND}, {@link Calendar#MINUTE},
	 * {@link Calendar#HOUR_OF_DAY}, or {@link Calendar#DAY_OF_WEEK}).
	 * 
	 * @param field The calendar field to reduce.
	 * @param start The calendar to get the field from
	 * @param candidate The calendar to adjust
	 * @param is_forward Whether to adjust the time forward.
	 * @throws IllegalArgumentException <code>field</code> is not a
	 *                                  constant-length field
	 */
	protected static void adjust_constant_length_field(int field,
			Calendar start, Calendar candidate, boolean is_forward) {

		if (start.getMaximum(field) != start.getLeastMaximum(field)
				|| start.getMinimum(field) != start.getGreatestMinimum(field)) {
			throw new IllegalArgumentException("Not a constant length field");
		}

		int fieldLength = (start.getMaximum(field) - start.getMinimum(field) + 1);

		int delta = start.get(field) - candidate.get(field);

		if (is_forward && delta < 0) {
			delta += fieldLength;
		} else if (!is_forward && delta > 0) {
			delta -= fieldLength;
		}
		
		int candidateTimeZoneOffset = candidate.getTimeZone().getOffset(candidate.getTime().getTime());
		
		candidate.add(field, delta);
		

		int candidateTimeZoneOffsetChange = candidate.getTimeZone().getOffset(candidate.getTime().getTime()) - candidateTimeZoneOffset;
		
		// adjust day light savings change
		if (candidateTimeZoneOffsetChange != 0) {
			candidate.setTimeInMillis(candidate.getTimeInMillis() - candidateTimeZoneOffsetChange);
		}

	}

	/**
	 * Adjust the time specified by <code>candidate</code> until its day of
	 * the month equals that of <code>start</code>.  If <code>is_forward</code>,
	 * increase the time, otherwise reduce it.  Adjust other fields
	 * appropriately.
	 * 
	 * <p>Months can vary in length.  The iCal spec says:
	 * "If BYxxx rule part values are found which are beyond the available
	 * scope (ie, BYMONTHDAY=30 in February), they are simply ignored."
	 * Assume this applies to times derived from DTStart as well,
	 * and roll back/forward through
	 * previous months until we find a match.
	 * 
	 * <p>Example of how this works: consider the current date October 3, and
	 * the start date January 31.  Assume !is_forward.  start - candidate
	 * yields 28, which is more than 0, so we subtract the number of days in
	 * the previous month (September), which is 30.  The resulting value, 1,
	 * of the month day, is not equal to the start month day (31), so we
	 * subtract the number of days in the second-previous month (31).  This
	 * gives us August 31; this agrees, so we stop.
	 * 
	 * <p>When we roll forward, by contrast, we adjust by the number of days
	 * in the <i>current</i> month, not the next month.  (What matters is the
	 * number of days in the month whose end is being crossed.)
	 * 
	 * <p>Because there are never two consecutive months with fewer than 31
	 * days, this algorithm will try at most two months (in the Gregorian
	 * calendar).
	 * 
	 * @param start The calendar with the target day of the month
	 * @param candidate The calendar to adjust
	 * @param is_forward Whether to adjust the time forward.
	 */
	protected static void adjust_day_of_month(Calendar start,
			Calendar candidate, boolean is_forward) {
		// previouse implementation doesn't work correctly, try this one
		int delta = start.get(Calendar.DATE) - candidate.get(Calendar.DATE);
		int month = candidate.get(Calendar.MONTH);
		
		int startMonthOffset = 0;
		if (!is_forward && delta > 0) {
			startMonthOffset = -1;
		} else if (is_forward && delta < 0) {
			startMonthOffset = 1;
		}
		
		while (start.get(Calendar.DATE) != candidate.get(Calendar.DATE)) {
						
			candidate.set(Calendar.MONTH, month + startMonthOffset);
			candidate.set(Calendar.DATE, start.get(Calendar.DATE));
			
			if (is_forward) {
				startMonthOffset++;
			} else {
				startMonthOffset--;
			}
			
		}
	}

	/**
	 * Adjust the time specified by <code>candidate</code> until its day of
	 * the year equals that of <code>start</code>. If <code>is_forward</code>,
	 * increase the time, otherwise reduce it. Adjust other fields
	 * appropriately.
	 * 
	 * <p>The day of year is calculated based on (month, day) pairs, not
	 * the numeric day of the year (which is different after February
	 * in leap years).
	 * 
	 * <p>For start dates of February 29, this algorithm will loop through at
	 * most seven years before it finds a matching year (in the Gregorian
	 * calendar).  For candidates between 1901 and 2099, it will loop through
	 * at most three years.  Start dates which do not fall on a leap day will
	 * not loop at all.
	 * 
	 * @param start The calendar with the target day of the month
	 * @param candidate The calendar to adjust
	 */
	protected static void adjust_day_of_year(Calendar start,
			Calendar candidate, boolean is_forward) {
		if (!is_forward
				&& (start.get(Calendar.MONTH) > candidate.get(Calendar.MONTH) || (start
						.get(Calendar.MONTH) == candidate.get(Calendar.MONTH) && start
						.get(Calendar.DATE) > candidate.get(Calendar.DATE)))) {
			/**
			 * Start date is later in the year than candidate date. Roll the
			 * candidate back to the previous year.
			 */
			candidate.add(Calendar.YEAR, -1);
		}

		if (is_forward
				&& (start.get(Calendar.MONTH) < candidate.get(Calendar.MONTH) || (start
						.get(Calendar.MONTH) == candidate.get(Calendar.MONTH) && start
						.get(Calendar.DATE) < candidate.get(Calendar.DATE)))) {
			/**
			 * Start date is earlier in the year than candidate date. Roll the
			 * candidate forward to the next year.
			 */
			candidate.add(Calendar.YEAR, 1);
		}

		/* Set the candidate date to the start date. */
		candidate.set(Calendar.MONTH, start.get(Calendar.MONTH));
		candidate.set(Calendar.DATE, start.get(Calendar.DATE));

		int increment = is_forward ? 1 : -1;

		/**
		 * If the candidate date doesn't equal the start date (which can happen
		 * if the start date doesn't exist in this year, i.e. Feb 29) try the
		 * next/previous year until we get a match.
		 */
		while (start.get(Calendar.MONTH) != candidate.get(Calendar.MONTH)
				|| start.get(Calendar.DATE) != candidate.get(Calendar.DATE)) {
			candidate.add(Calendar.YEAR, increment);

			candidate.set(Calendar.MONTH, start.get(Calendar.MONTH));
			candidate.set(Calendar.DATE, start.get(Calendar.DATE));
		}
	}

	/**
	 * The last time we're willing to search for <code>count</code> to resolve
	 * it to <code>until</code>. If a recurrence exceeds this instant before
	 * it's filled its counts, consider it infinite, or throw an exception,
	 * depending on the value of strict_count_bounds.
	 * 
	 * <p>The default value of this parameter is 2000000000000L, or
	 * Wednesday, May 18, 2033 03:33:20.000 UTC.  This is comfortably less
	 * than time_t (2^31 - 1), so we don't have to worry about rounding on
	 * systems which implement java.util.Calendar's support functions on a 
	 * 32-bit time_t.
	 * 
	 * @see #computeUntilFromCount()
	 * @see #strict_count_bounds
	 */
	public static long max_count_time = 2000000000000L;

	/**
	 * The maximum number of candidate start times we're willing to consider
	 * for <code>count</code> to resolve it to <code>until</code>.  If a
	 * recurrence exceeds this instant before it's filled its counts, consider
	 * it infinite, or throw an exception, depending on the value of
	 * strict_count_bounds.
	 * 
	 * <p>The default value of this parameter is 1000.
	 * 
	 * @see #computeUntilFromCount()
	 * @see #strict_count_bounds
	 */
	public static int max_count_loops = 1000;

	/**
	 * Whether to throw an exception if a conversion from <code>count</code>
	 * to <code>until</code> exceeds its defined boundaries, or simply to
	 * assume the count is effectively infinite.
	 * 
	 * <p>The default value of this parameter is false.
	 * 
	 * @see #computeUntilFromCount()
	 * @see #max_count_time
	 * @see #max_count_loops
	 */
	public static boolean strict_count_bounds = false;

	/**
	 * If <code>count</code> is set and <code>until</code> is not, calculate
	 * <code>until</code> from <code>count</code>.  <code>until</code> will be
	 * set to one second after the <code>count</code>'th start time of the
	 * interval.
	 * 
	 * If we do not reach <code>count</code> recurrences before we reach
	 * <code>max_count_time</code>, or before we have tried
	 * <code>max_count_loops</code> candidate start times, set
	 * <code>until</code> to <code>max_count_time</code>, or throw an
	 * exception, depending on the state of <code>strict_count_bounds</code>.
	 * 
	 * @throws IllegalArgumentException If we run off the end, and
	 *         <code>strict_count_bounds</code> is in effect.
	 * @see #max_count_time
	 * @see #max_count_loops
	 * @see #strict_count_bounds
	 */
	public void computeUntilFromCount(boolean useMaxCountLoops) {
		computeUntilFromCount(false, useMaxCountLoops);
	}

	/**
	 * Debugging interface to {@link #computeUntilFromCount()}.
	 * 
	 * @throws IllegalArgumentException If we run off the end, and
	 *         <code>strict_count_bounds</code> is in effect.
	 * @see #computeUntilFromCount()
	 */
	public void computeUntilFromCount(boolean debug, boolean useMaxCountLoops) {
		if (until != null) {
			return;
		}
		if (!useMaxCountLoops && count == 0) {
			until = null;
			return;
		}
		
		int intCount = count != 0 ? count : max_count_loops;
		
		int starts_found = 0;
		int loops;
		
		Calendar candidate;

		for (loops = 0, candidate = (Calendar) dtStart.clone(); loops < 100000
				&& candidate.getTime().getTime() < max_count_time; loops++, candidate
				.add(Calendar.SECOND, 1)) {
			long oldCandidateTime = candidate.getTime().getTime();

			candidate = internalGetCandidateStartTime(candidate, true, false);

			if (candidate == null) {
				if (debug) {
					System.err.println("No next candidate start");
				}
				break;
			}

			if (candidate.getTime().getTime() < oldCandidateTime) {
				throw new IllegalStateException(
						"Internal error: candidate < old candidate");
			}

			if (candidateIsInRecurrence(candidate, debug)) {
				starts_found++;
				if (starts_found == intCount) {
					candidate.add(Calendar.SECOND, 1);
					until = candidate;
					return;
				}
			}
		}

		/* If we get here, we've fallen or broken out of the loop */
		if (debug) {
			System.err.println("Could not resolve count to until");
		}
		if (strict_count_bounds) {
			throw new IllegalArgumentException(
					"Could not resolve count to until");
		} else {
			until = (Calendar) dtStart.clone();
			until.setTime(new Date(max_count_time));
			return;
		}
	}

	/**
	 * Get the number of instances of the current recurrence frequency
	 * that have occurred between this recurrence's dtStart, and the
	 * time specified by <code>candidate</code>.
	 * 
	 * @param candidate The end time of the period
	 * @return The number of recurrences
	 */
	protected long getRecurrenceCount(Calendar candidate) {
		switch (frequency) {
		case NO_RECURRENCE:
			return 0;

		case SECONDLY:
		case MINUTELY:
		case HOURLY:
		case DAILY:
			return (long) (getFieldNumber(candidate, frequency) - getFieldNumber(
					dtStart, frequency));

		case WEEKLY:
			Calendar tempCand = (Calendar) candidate.clone();
			tempCand.setFirstDayOfWeek(dtStart.getFirstDayOfWeek());
			return (long) (getWeekNumber(tempCand) - getWeekNumber(dtStart));

		case MONTHLY:
			return (long) (getMonthNumber(candidate) - getMonthNumber(dtStart));

		case YEARLY:
			return (long) (candidate.get(Calendar.YEAR) - dtStart
					.get(Calendar.YEAR));

		default:
			throw new IllegalStateException("Internal error: bad frequency");
		}
	}

	/**
	 * Get the field number corresponding to the date of this calendar
	 * (field intervals &mdash; seconds, minutes, hours, or days &mdash;
	 * since the Epoch).
	 * 
	 * @param cal The calendar to calculate.
	 * @param field The field to calculate - one of {@link #SECONDLY},
	 *            {@link #MINUTELY}, {@link #HOURLY}, or {@link #DAILY}.
	 * 
	 * @return The field number.
	 */
	protected static long getFieldNumber(Calendar cal, int field) {
		Calendar tempCal = (Calendar) cal.clone();
		long multiple = 1000; /* ticks per second */

		// Zero out subfields, so we have an integer.
		tempCal.set(Calendar.MILLISECOND, 0);

		if (field > SECONDLY) {
			tempCal.set(Calendar.SECOND, 0);
			multiple *= 60; /* seconds per minute */
		}
		if (field > MINUTELY) {
			tempCal.set(Calendar.MINUTE, 0);
			multiple *= 60; /* minutes per hour */
		}
		if (field > HOURLY) {
			tempCal.set(Calendar.HOUR_OF_DAY, 0);
			multiple *= 24; /* hours per day */
		}

		return tempCal.getTime().getTime() / multiple;
	}

	/**
	 * Get the week number corresponding to the date of this calendar
	 * (weeks since the Epoch).  This depends on the calendar's
	 * current {@link #getWeekStart} setting.
	 * 
	 * @param cal The calendar to calculate.
	 * @return The week number.
	 * @see #setWeekStart
	 * @see Calendar#setFirstDayOfWeek
	 */
	protected static long getWeekNumber(Calendar cal) {
		Calendar tempCal = (Calendar) cal.clone();

		// Set to midnight
		tempCal.set(Calendar.MILLISECOND, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.HOUR_OF_DAY, 0);

		// Roll back to the first day of the week
		int delta = tempCal.getFirstDayOfWeek()
				- tempCal.get(Calendar.DAY_OF_WEEK);

		if (delta > 0) {
			delta -= 7;
		}

		// tempCal now points to the first instant of this week.

		// Calculate the "week epoch" -- the week start day closest to January 1,
		// 1970 (which was a Thursday)

		long weekEpoch = (tempCal.getFirstDayOfWeek() - Calendar.THURSDAY) * 24
				* 60 * 60 * 1000L;

		return (tempCal.getTime().getTime() - weekEpoch)
				/ (7 * 24 * 60 * 60 * 1000);
	}

	/**
	 * Get the month number corresponding to the date of this calendar
	 * (months since the Epoch).
	 * 
	 * @param cal The calendar to calculate.
	 * @return The month number.
	 */
	protected static long getMonthNumber(Calendar cal) {
		return (cal.get(Calendar.YEAR) - 1970) * 12
				+ (cal.get(Calendar.MONTH) - Calendar.JANUARY);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byDay} rules.
	 * @param candidate The candidate date to check.
	 * @return Whether it matches.
	 */
	protected boolean matchesByDay(Calendar candidate) {
		if (byDay == null || byDay.length == 0) {
			/* No byDay rules, so it matches trivially */
			return true;
		}

		int i;

		for (i = 0; i < byDay.length; i++) {
			if (matchesIndividualByDay(candidate, byDay[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if <code>candidate</code> matches one individual
	 * {@link DayAndPosition}.
	 * @param candidate the candidate date to check
	 * @param pos the candidate position
	 * @return whether it matches
	 */
	protected boolean matchesIndividualByDay(Calendar candidate,
			DayAndPosition pos) {
		if (pos.getDayOfWeek() != candidate.get(Calendar.DAY_OF_WEEK)) {
			return false;
		}
		int position = pos.getDayPosition();

		if (position == 0) {
			return true;
		}

		int field;

		/**
		 * java.util.Calendar has a DAY_OF_WEEK_IN_MONTH field which holds the
		 * "positive" value of this function within the week. It hasn't got a
		 * negative version of it, though, or a DAY_OF_WEEK_IN_YEAR field, so
		 * calculate these manually.
		 */

		switch (frequency) {
		case MONTHLY:
			field = Calendar.DAY_OF_MONTH;
			break;
		case YEARLY:
			field = Calendar.DAY_OF_YEAR;
			break;
		default:
			throw new IllegalStateException("byday has a day position "
					+ "in non-MONTHLY or YEARLY recurrence");
		}

		if (position > 0) {
			int day_of_week_in_field = ((candidate.get(field) - 1) / 7) + 1;
			return (position == day_of_week_in_field);
		} else {
			/* position < 0 */
			int negative_day_of_week_in_field = ((candidate
					.getActualMaximum(field) - candidate.get(field)) / 7) + 1;
			return (-position == negative_day_of_week_in_field);
		}
	}

	/**
	 * Determine if field <code>field</code> of <code>candidate</code> matches
	 * an entry in <code>array</code>
	 * 
	 * @param array The by* array to check
	 * @param field The Calendar field to check
	 * @param candidate The Candidate date to check
	 * @param allowNegative Whether negative numbers should count as
	 *                      N-from-the-end
	 * @return whether it matches.
	 */
	protected static boolean matchesByField(int[] array, int field,
			Calendar candidate, boolean allowNegative) {
		if (array == null || array.length == 0) {
			/* No rules, so it matches trivially */
			return true;
		}

		int i;

		for (i = 0; i < array.length; i++) {
			int val;
			if (allowNegative && array[i] < 0) {
				// byMonthDay = -1, in a 31-day month, means 31
				int max = candidate.getActualMaximum(field);
				val = (max + 1) + array[i];
			} else {
				val = array[i];
			}
			if (val == candidate.get(field)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #bySecond} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesBySecond(Calendar candidate) {
		return matchesByField(bySecond, Calendar.SECOND, candidate, false);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byMinute} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesByMinute(Calendar candidate) {
		return matchesByField(byMinute, Calendar.MINUTE, candidate, false);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byHour} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesByHour(Calendar candidate) {
		return matchesByField(byHour, Calendar.HOUR_OF_DAY, candidate, false);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byMonthDay} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesByMonthDay(Calendar candidate) {
		return matchesByField(byMonthDay, Calendar.DATE, candidate, true);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byYearDay} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesByYearDay(Calendar candidate) {
		return matchesByField(byYearDay, Calendar.DAY_OF_YEAR, candidate, true);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byWeekNo} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesByWeekNo(Calendar candidate) {
		return matchesByField(byWeekNo, Calendar.WEEK_OF_YEAR, candidate, true);
	}

	/**
	 * Determine if <code>candidate</code> matches the recurrence's
	 * {@link #byMonth} rules.
	 * @param candidate the Candidate date to check
	 * @return whether it matches.
	 */
	protected boolean matchesByMonth(Calendar candidate) {
		return matchesByField(byMonth, Calendar.MONTH, candidate, false);
	}

	/**
	 * Set the given calendar's week start value to <code>start</code>, and
	 * set its minimal days in first week to 4.
	 * Clear its <code>WEEK_OF_MONTH</code> and <code>WEEK_OF_YEAR</code>
	 * fields &mdash; setting the values doesn't do this, so old values can
	 * persist.
	 * Note: this works for calendars set by the usual iCalendar/CPL syntax,
	 * e.g. those created by {@link #parseDateTime}, 
	 * but can fail if someone actually specified the current time with 
	 * <code>WEEK_OF_MONTH</code> or <code>WEEK_OF_YEAR</code>.
	 * 
	 * @param cal The calendar to reset
	 * @param start The start day of the wee
	 * @see #parseDateTime
	 */
	protected static void setCalendarWeekStart(Calendar cal, int start) {
		cal.setMinimalDaysInFirstWeek(4);
		cal.setFirstDayOfWeek(start);
		cal.clear(Calendar.WEEK_OF_MONTH);
		cal.clear(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * Parse the given Date-Time string into a Calendar object. Internal
	 * function used as a support function for set*(String). <p>
	 * 
	 * The syntax of a Date-Time is given in RFC 2445 as follows:
	 * <pre>
	 *   date-time  = date "T" time ;As specified in the date and time
	 *                               ;value definitions
	 * 
	 *    date               = date-value
	 * 
	 *    date-value         = date-fullyear date-month date-mday
	 *    date-fullyear      = 4DIGIT
	 * 
	 *    date-month         = 2DIGIT        ;01-12
	 *    date-mday          = 2DIGIT        ;01-28, 01-29, 01-30, 01-31
	 *                                       ;based on month/year
	 * 
	 *    time               = time-hour time-minute time-second [time-utc]
	 * 
	 *    time-hour          = 2DIGIT        ;00-23
	 *    time-minute        = 2DIGIT        ;00-59
	 *    time-second        = 2DIGIT        ;00-60
	 *   ;The "60" value is used to account for "leap" seconds.
	 * 
	 *   time-utc   = "Z"
	 * </pre>
	 * 
	 * 
	 * @param  str  The string representation of the date-time.
	 * @return The date-time
	 * @throws IllegalArgumentException If the given string does not describe a
	 *         valid Date-Time
	 */
	public static Calendar parseDateTime(String str) {
		int y, mo, d, h, m, sec;
		boolean zulu = false;

		/*
		 * The grammar of date-time defines it to be either 15 (local) or 16
		 * (zulu) characters.
		 */
		if (str.length() < 15 || str.length() > 16
				|| (str.length() == 16 && str.charAt(15) != 'Z')) {
			throw new IllegalArgumentException(
					"Bad date-time string: incorrect length");
		}
		if (str.charAt(8) != 'T') {
			throw new IllegalArgumentException(
					"Bad date-time string: missing \'T\'");
		}
		if (str.length() == 16 && str.charAt(15) == 'Z') {
			zulu = true;
		}

		y = getFixedSizeInt(str, 0, 4, "year");
		mo = getFixedSizeInt(str, 4, 2, "month");
		d = getFixedSizeInt(str, 6, 2, "day of month");

		h = getFixedSizeInt(str, 9, 2, "hour");
		m = getFixedSizeInt(str, 11, 2, "minute");
		sec = getFixedSizeInt(str, 13, 2, "second");

		/* Make mo Calendar.JANUARY-based rather than 1-based */
		mo = mo + Calendar.JANUARY - 1;

		Calendar ret = new GregorianCalendar(y, mo, d, h, m, sec);

		if (zulu) {
			ret.setTimeZone(TimeZoneHelper.getTimeZone("GMT"));
		}

		/*
		 * Validate fields by normalizing and making sure they've stayed the
		 * same.
		 */
		ret.getTime(); // Force normalization

		if (ret.get(Calendar.SECOND) != sec) {
			throw new IllegalArgumentException(
					"Bad date-time string: second out of range");
		}
		if (ret.get(Calendar.MINUTE) != m) {
			throw new IllegalArgumentException(
					"Bad date-time string: minute out of range");
		}
		if (ret.get(Calendar.HOUR_OF_DAY) != h) {
			throw new IllegalArgumentException(
					"Bad date-time string: hour out of range");
		}
		if (ret.get(Calendar.DAY_OF_MONTH) != d) {
			throw new IllegalArgumentException("Bad date-time string: "
					+ "day of month out of range");
		}
		if (ret.get(Calendar.MONTH) != mo) {
			throw new IllegalArgumentException(
					"Bad date-time string: month out of range");
		}
		if (ret.get(Calendar.YEAR) != y) {
			throw new IllegalArgumentException(
					"Bad date-time string: year out of range");
		}

		return ret;
	}

	/**
	 * Support function for parseDateTime.  Get a fixed-size integer from
	 * the given string.  Throw an exception if the string isn't entirely
	 * numeric.
	 * 
	 * @param str The string from which to extract the integer
	 * @param pos The position of the integer
	 * @param ext The extent of the integer
	 */
	protected static int getFixedSizeInt(String str, int pos, int ext,
			String fieldname) {
		try {
			return Integer.parseInt(str.substring(pos, pos + ext));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Bad date-time string: non-numeric " + fieldname);
		}
	}

	/**
	 * Parse the given comma-separated list of integers into a int[].
	 * used as a support function for <code>setByXXX(String)</code>, other
	 * than {@link #setByDay(String)}.<p>
	 * 
	 * @param  str  The string representation of the integer list.
	 * @param  name The name of the integer field (for exceptions)
	 * @param  min  The minimum integer value allowed
	 * @param  max  The maximum integer value allowed
	 * @param  neg_ok Whether negative values are allowed
	 * @return The list of integers
	 * @throws IllegalArgumentException If the given string does not describe
	 *         a valid list of integers complying with the above requirements
	 */
	@SuppressWarnings("unchecked")
	protected static int[] parseIntSet(String str, String name, int min,
			int max, boolean neg_ok) {
		if (str == null) {
			return null;
		}

		Vector ret = new Vector();

		int s = 0, end = str.length();
		int value = 0;
		boolean negative_value = false;

		while (s != end) {
			if (!Character.isDigit(str.charAt(s)) && str.charAt(s) != '+'
					&& str.charAt(s) != '-') {
				throw new IllegalArgumentException("Bad by" + name
						+ " list: unexpected character \'" + str.charAt(s)
						+ "\'");
			}

			negative_value = false;
			if (str.charAt(s) == '-') {
				negative_value = true;
			}

			if (str.charAt(s) == '-' || str.charAt(s) == '+') {
				s++;
				if (s == end || !Character.isDigit(str.charAt(s))) {
					throw new IllegalArgumentException("Bad by" + name
							+ " list: \'+\' or \'-\' without number");
				}
			}

			for (value = 0; s != end && Character.isDigit(str.charAt(s)); s++) {
				value = value * 10 + (str.charAt(s) - '0');
			}

			if (value < min || value > max) {
				throw new IllegalArgumentException("Out of range " + name + " "
						+ (negative_value ? "-" : "") + value + " in by" + name
						+ " list");
			}

			if (negative_value) {
				value = -value;
			}
			ret.add(new Integer(value));

			if (s != end) {
				if (str.charAt(s) != ',') {
					throw new IllegalArgumentException("Bad by" + name
							+ " list: unexpected character \'" + str.charAt(s)
							+ "\'");
				}
				s++;
			}
		}

		int[] real_ret = new int[ret.size()];

		for (int i = 0; i < ret.size(); i++) {
			real_ret[i] = ((Integer) ret.elementAt(i)).intValue();
		}

		return real_ret;
	}

	/**
	 * Parse the given comma-separated list of integers into a
	 * DayAndPosition[].  Used as a support function for
	 * {@link #setByDay(String)}.
	 * 
	 * @param  str  The string representation of the day list.
	 * @return The list of day-and-positions
	 * @throws IllegalArgumentException If the given string does not describe
	 *         a valid list of day-and-positions
	 */
	@SuppressWarnings("unchecked")
	protected DayAndPosition[] parseDaySet(String str) {
		if (str == null) {
			return null;
		}

		int i;
		Vector ret = new Vector();

		for (i = 0; i < str.length(); i++) {
			int comma = str.indexOf(',', i);
			String thisdaypos;

			if (comma == -1) {
				thisdaypos = str.substring(i);
			} else {
				thisdaypos = str.substring(i, comma);
			}

			ret.add(new DayAndPosition(thisdaypos));
			i = (comma == -1 ? str.length() - 1 : comma);
		}

		DayAndPosition[] real_ret = new DayAndPosition[ret.size()];

		real_ret = (DayAndPosition[]) ret.toArray(real_ret);

		return real_ret;
	}

	/**
	 * Generate a standard Date-Time string for the given calendar.
	 * 
	 * @param cal The calendar to generate.
	 * @return A string representation of the calendar.
	 */
	public String generateDateTimeString(Calendar cal) {
		if (null == cal) {
			return "";
		}
		
		DecimalFormat twoDigit = new DecimalFormat("00");
		DecimalFormat fourDigit = new DecimalFormat("0000");

		StringBuffer obuf = new StringBuffer();

		obuf.append(fourDigit.format(cal.get(Calendar.YEAR)));

		// Count from 1, not from Calendar.JANUARY
		obuf.append(twoDigit.format(cal.get(Calendar.MONTH) - Calendar.JANUARY
				+ 1));

		obuf.append(twoDigit.format(cal.get(Calendar.DAY_OF_MONTH)));

		obuf.append("T");

		obuf.append(twoDigit.format(cal.get(Calendar.HOUR_OF_DAY)));
		obuf.append(twoDigit.format(cal.get(Calendar.MINUTE)));
		obuf.append(twoDigit.format(cal.get(Calendar.SECOND)));

		return obuf.toString();
	}

	/**
	 * Generate a comma-separated int-set string (as used in BY* arguments).
	 * 
	 * @param set A vector of ints
	 * @return A comma-separated list of ints
	 */
	protected String generateIntSetString(int[] set) {
		if (set == null) {
			return ("");
		}
		int i;

		StringBuffer obuf = new StringBuffer();

		for (i = 0; i < set.length; i++) {
			if (i != 0) {
				obuf.append(",");
			}
			obuf.append(set[i]);
		}

		return obuf.toString();
	}

	/**
	 * Generate a comma-separated DayAndPosition set string (as used in
	 * BYDAY arguments).
	 * 
	 * @param set A vector of DayAndPositions
	 * @return A comma-separated list of DayAndPositions
	 */
	public String generateDaySetString(DayAndPosition[] set) {
		if (set == null) {
			return "";
		}
		int i;
		StringBuffer obuf = new StringBuffer();

		for (i = 0; i < set.length; i++) {
			if (i != 0) {
				obuf.append(",");
			}
			obuf.append(set[i].getString());
		}

		return obuf.toString();
	}

	/**
	 * Overrides Cloneable
	 * 
	 * @return A clone of this object.
	 */
	@Override
	public Object clone() {
		try {
			Event other = (Event) super.clone();
			other.name = name;
			other.dtStart      = (dtStart      != null ? (Calendar) dtStart.clone()      : null);
			other.dtCalcStart  = (dtCalcStart  != null ? (Calendar) dtCalcStart.clone()  : null);
			other.dtEnd        = (dtEnd        != null ? (Calendar) dtEnd.clone()        : null);
			other.dtCalcEnd    = (dtCalcEnd    != null ? (Calendar) dtCalcEnd.clone()    : null);
			other.duration     = (duration     != null ? (Duration) duration.clone()     : null);
			other.frequency = frequency;
			other.interval = interval;
			other.until = (until != null ? (Calendar) until.clone() : null);
			other.count = count;
			if (timeZone != null) {
				other.timeZone = (TimeZone)timeZone.clone();
			}

			other.bySecond = (int[]) arrayclone(bySecond);
			other.byMinute = (int[]) arrayclone(byMinute);
			other.byHour = (int[]) arrayclone(byHour);
			other.byDay = (DayAndPosition[]) arrayclone(byDay);
			other.byMonthDay = (int[]) arrayclone(byMonthDay);
			other.byYearDay = (int[]) arrayclone(byYearDay);
			other.byWeekNo = (int[]) arrayclone(byWeekNo);
			other.byMonth = (int[]) arrayclone(byMonth);

			return other;
		} catch (CloneNotSupportedException e) {
			// This shouldn't happen, since we are Cloneable
			throw new InternalError("Clone error: " + e.getLocalizedMessage());
		}
	}

	/**
	 * Return a string representation of this recurrence. This method 
	 * is intended to be used only for debugging purposes.
	 * The returned string may be empty but may not be <code>null</code>.
	 * 
	 * @return A string representation of this recurrence.
	 */
	@Override
	public String toString() {
		int i;

		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName());
		buffer.append("[dtStart=");
		buffer.append(dtStart != null ? dtStart.toString() : "null");
		buffer.append(",dtCalcStart=");
		buffer.append(dtCalcStart != null ? dtCalcStart.toString() : "null");
		buffer.append(",dtEnd=");
		buffer.append(dtEnd != null ? dtEnd.toString() : "null");
		buffer.append(",dtCalcEnd=");
		buffer.append(dtCalcEnd != null ? dtCalcEnd.toString() : "null");
		buffer.append(",duration=");
		buffer.append(duration != null ? duration.toString() : "null");
		buffer.append(",frequency=");
		buffer.append(frequency);
		buffer.append(",interval=");
		buffer.append(interval);
		buffer.append(",until=");
		buffer.append(until != null ? until.toString() : "null");
		buffer.append(",count=");
		buffer.append(count);
		buffer.append(",bySecond=");
		buffer.append(stringizeIntArray(bySecond));
		buffer.append(",byMinute=");
		buffer.append(stringizeIntArray(byMinute));
		buffer.append(",byHour=");
		buffer.append(stringizeIntArray(byHour));
		buffer.append(",byDay=");
		if (byDay == null) {
			buffer.append("null");
		} else {
			buffer.append("[");
			for (i = 0; i < byDay.length; i++) {
				if (i != 0)
					buffer.append(",");
				buffer.append(byDay[i].toString());
			}
			buffer.append("]");
		}
		buffer.append(",byMonthDay=");
		buffer.append(stringizeIntArray(byMonthDay));
		buffer.append(",byYearDay=");
		buffer.append(stringizeIntArray(byYearDay));
		buffer.append(",byWeekNo=");
		buffer.append(stringizeIntArray(byWeekNo));
		buffer.append(",byMonth=");
		buffer.append(stringizeIntArray(byMonth));
		buffer.append(']');

		return buffer.toString();
	}

	public String toCsvString() {
		User user = RequestContextHolder.getRequestContext().getUser();
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		StringBuffer buffer = new StringBuffer();

		buffer.append(NLT.get("event.start") + ": ");
		buffer.append(dtStart != null ? dateFormatter.format(dtStart.getTime()) : "");
		buffer.append("; ");
		buffer.append(NLT.get("event.end") + ": ");
		buffer.append(dtEnd != null ? dateFormatter.format(dtEnd.getTime()) : "");
		buffer.append("; ");
		buffer.append(NLT.get("event.duration_days") + ": ");
		buffer.append(duration != null ? durationToCsvString(duration) : "");
		buffer.append("; ");
		buffer.append(NLT.get("event.frequency") + ": ");
		String freqStr = EventsViewHelper.eventToRepeatHumanReadableString(this, user.getLocale());
		if (freqStr != null) {
			buffer.append(freqStr);
		}

		return buffer.toString();
	}
	
	  public String durationToCsvString(Duration duration)
	  {
	    StringBuffer buf = new StringBuffer();
	  
	     if (duration.getWeeks() != 0) {
	      buf.append(duration.getWeeks());
	      buf.append(" " + NLT.get("smallWords.weeks") + " ");
	    }
	    if (duration.getDays() != 0) {
	      buf.append(duration.getDays());
	      buf.append(" " + NLT.get("smallWords.days") + " ");
	    }
	    if (duration.getHours() != 0) {
	      buf.append(duration.getHours());
	      buf.append(" " + NLT.get("smallWords.hours") + " ");
	    }
	    if (duration.getMinutes() != 0) {
	      buf.append(duration.getMinutes());
	      buf.append(" " + NLT.get("smallWords.minutes") + " ");
	    }
	    if (duration.getSeconds() != 0) {
	      buf.append(duration.getSeconds());
	      buf.append(" " + NLT.get("smallWords.seconds") + " ");
	    }
	    return buf.toString();
	  }


	public String stringizeIntArray(int[] a) {
		if (a == null) {
			return "null";
		}

		int i;

		StringBuffer buffer = new StringBuffer();

		buffer.append("[");
		for (i = 0; i < a.length; i++) {
			if (i != 0)
				buffer.append(",");
			buffer.append(a[i]);
		}
		buffer.append("]");

		return buffer.toString();
	}

	public boolean isRepeating() {
		return frequency == NO_RECURRENCE ? false : true;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean isTimeZoneSensitive() {
		// v1.1 compatibility - default value was false (should be true) 
		return !timeZoneSensitive;
	}

	public void setTimeZoneSensitive(boolean timeZoneSensitive) {
		// Bugzilla 553319:
		//    Changing the time zone sensitive flag is ignored.  All
		//    events are time zone sensitive by default.
	}
	
	/**
	 * @hibernate.property length="255"
	 * @return
	 */
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	/**
	 * @hibernate.property length="32"
	 * @return
	 */
	public FreeBusyType getFreeBusy() {
		// ver. 1.x compatibility, there was no freeBusy info
		if (freeBusy == null) {
			if (this.isAllDayEvent()) {
				return FreeBusyType.free;
			} else {
				return FreeBusyType.busy;
			}
		}
		
		return freeBusy;
	}

	public void setFreeBusy(FreeBusyType freeBusy) {
		this.freeBusy = freeBusy;
	}
	
	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getDays() {
		return getByDayString();
	}

	@SuppressWarnings("unused")
	private void setDays(String byDay) {
		setByDay(byDay);
	}

	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getMonthDay() {
		return getByMonthDayString();
	}

	@SuppressWarnings("unused")
	private void setMonthDay(String byMonthDay) {
		setByMonthDay(byMonthDay);
	}

	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getYearDay() {
		return getByYearDayString();
	}

	@SuppressWarnings("unused")
	private void setYearDay(String byYearDay) {
		setByYearDay(byYearDay);
	}

	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getWeekNo() {
		return getByWeekNoString();
	}

	@SuppressWarnings("unused")
	private void setWeekNo(String byWeekNo) {
		setByWeekNo(byWeekNo);
	}

	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getMonths() {
		return getByMonthString();
	}

	@SuppressWarnings("unused")
	private void setMonths(String byMonth) {
		setByMonth(byMonth);
	}

	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getMinutes() {
		return getByMinuteString();
	}

	@SuppressWarnings("unused")
	private void setMinutes(String byMinute) {
		setByMinute(byMinute);
	}

	/**
	 * @hibernate.property length="100"
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getHours() {
		return getByHourString();
	}

	@SuppressWarnings("unused")
	private void setHours(String byHour) {
		setByHour(byHour);
	}

	/**
	 * @hibernate.property
	 * @hibernate.column name="duration" length="32" not-null="true"
	 * Get the duration of the recurrence, represented as a string.
	 * @return A string representing the duration.
	 */
	@SuppressWarnings("unused")
	private String getHDuration() {
		return getDuration().getString();
	}

	@SuppressWarnings("unused")
	private void setHDuration(String durationString) {
		setDuration(new Duration(durationString));
	}

	/**
	 * Implements UpdateAttributeSupport.update() interface.
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean update(Object obj) {
		Event newEvent = ((Event) obj);
		boolean changed = false;
		
		if (!areDtCalendarsEqual(getDtStart(), newEvent.getDtStart())) {
			changed = true;
			setDtStart(newEvent.getDtStart());
		}
		if (!areDtCalendarsEqual(getDtCalcStart(), newEvent.getDtCalcStart())) {
			changed = true;
			setDtCalcStart(newEvent.getDtCalcStart());
		}
		
		if (!areDtCalendarsEqual(getDtEnd(), newEvent.getDtEnd())) {
			changed = true;
			setDtEnd(newEvent.getDtEnd());
		}
		if (!areDtCalendarsEqual(getDtCalcEnd(), newEvent.getDtCalcEnd())) {
			changed = true;
			setDtCalcEnd(newEvent.getDtCalcEnd());
		}
		if (!getDuration().equals(newEvent.getDuration())) {
			changed = true;
			setDuration(newEvent.getDuration());
		}
		
		if (newEvent.getCount() == -1) {
			// -1 implies have until's.  See if they match.
			if (getCount() == -1) {
				if (!getUntil().equals(newEvent.getUntil())) {
					// getCount() == -1 means count == -1 or
					// count == 0, reset it.
					setCount(0);
					setUntil(newEvent.getUntil());
					changed = true;
				}
			} else {
				setCount(0);
				setUntil(newEvent.getUntil());
				changed = true;
			}
		} else {
			setUntil((Calendar) null);
			if (getCount() != newEvent.getCount())
				setCount(newEvent.getCount());
			changed = true;
		}

		if (getFrequency() != newEvent.getFrequency()) {
			setFrequency(newEvent.getFrequency());
			changed = true;
		}

		if (getInterval() != newEvent.getInterval()) {
			setInterval(newEvent.getInterval());
			changed = true;
		}
		
		if (isTimeZoneSensitive() != newEvent.isTimeZoneSensitive()) {
			setTimeZoneSensitive(newEvent.isTimeZoneSensitive());
			changed = true;
		}
		
		if ((getUid() == null && newEvent.getUid() != null) || 
				(getUid() != null && newEvent.getUid() == null) ||
				(getUid() != null && newEvent.getUid() != null && !getUid().equals(newEvent.getUid()))) {
			setUid(newEvent.getUid());
			changed = true;
		}
		
		if ((getFreeBusy() == null && newEvent.getFreeBusy() != null) || 
				(getFreeBusy() != null && newEvent.getFreeBusy() == null) ||
				(getFreeBusy() != null && newEvent.getFreeBusy() != null && !getFreeBusy().equals(newEvent.getFreeBusy()))) {
			setFreeBusy(newEvent.getFreeBusy());
			changed = true;
		}
		
		if ((getTimeZone() == null && newEvent.getTimeZone() != null) || 
				(getTimeZone() != null && newEvent.getTimeZone() == null) ||
				(getTimeZone() != null && newEvent.getTimeZone() != null && !getTimeZone().equals(newEvent.getTimeZone()))) {
			setTimeZone(newEvent.getTimeZone());
			changed = true;
		}
		
		int[] val = newEvent.getBySecond();
		if ((bySecond != val) && hasDiffs(bySecond, val)) {
			setBySecond(val);
			changed = true;
		}

		val = newEvent.getByMinute();
		if ((byMinute != val) && hasDiffs(byMinute, val)) {
			setByMinute(val);
			changed = true;
		}

		val = newEvent.getByHour();
		if ((byHour != val) && hasDiffs(byHour, val)) {
			setByHour(val);
			changed = true;
		}

		if (!getByDayString().equals(newEvent.getByDayString())) {
			setByDay(newEvent.getByDay());
			changed = true;
		}

		val = newEvent.getByMonthDay();
		if ((byMonthDay != val) && hasDiffs(byMonthDay, val)) {
			setByMonthDay(val);
			changed = true;
		}

		val = newEvent.getByYearDay();
		if ((byYearDay != val) && hasDiffs(byYearDay, val)) {
			setByYearDay(val);
			changed = true;
		}
		val = newEvent.getByWeekNo();
		if ((byWeekNo != val) && hasDiffs(byWeekNo, val)) {
			setByWeekNo(val);
			changed = true;
		}
		val = newEvent.getByMonth();
		if ((byMonth != val) && hasDiffs(byMonth, val)) {
			setByMonth(val);
			changed = true;
		}
		
		return changed;
	}

	/*
	 * Returns true if to Calendar values are equal and false
	 * otherwise.
	 */
	private static boolean areDtCalendarsEqual(Calendar dt1, Calendar dt2) {
		// If both are null...
		if (dt1 == dt2) {
			// ...they're equal.
			return true;
		}

		// If one or the other is null, but not both...
		if ((null == dt1) || (null == dt2)) {
			// ...they're not equal.
			return false;
		}

		// Otherwise, just compare them.
		return dt1.equals(dt2);
	}

	private boolean hasDiffs(int[] curVal, int[] newVal) {
		if (curVal == newVal)
			return false;
		if ((curVal == null) && (newVal != null))
			return true;
		if ((newVal == null) && (curVal != null))
			return true;
		if (curVal.length != newVal.length)
			return true;
		return !curVal.equals(newVal);
	}

	public Element addChangeLog(Element parent) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_EVENT);
		element.addAttribute("id", getId());
		element.addAttribute("name", getName());

		XmlUtils.addProperty(element, "start", getDtStartString());
		XmlUtils.addProperty(element, "calcStart", getDtCalcStartString());
		XmlUtils.addProperty(element, "duration", getDuration().getString());

		if (getCount() > 0) {
			XmlUtils.addProperty(element, "count", getCountString());
		} else if (getCount() == -1) {
			XmlUtils.addProperty(element, "until", getUntilString());
		}

		XmlUtils.addProperty(element, "frequency",
				getFrequencyString());

		XmlUtils.addProperty(element, "interval", getIntervalString());

		XmlUtils.addProperty(element, "timeZoneSensitive", Boolean
				.toString(isTimeZoneSensitive()));
		
		XmlUtils.addProperty(element, "uid", getUid());		
		
		XmlUtils.addProperty(element, "freeBusy", getFreeBusy());	
		
		XmlUtils.addProperty(element, "bySecond", getBySecondString());

		XmlUtils.addProperty(element, "byMinute", getByMinuteString());

		XmlUtils.addProperty(element, "byHour", getByHourString());

		XmlUtils.addProperty(element, "byDay", getByDayString());

		XmlUtils.addProperty(element, "byMonthDay",
				getByMonthDayString());

		XmlUtils.addProperty(element, "byYearDay",
				getByYearDayString());

		XmlUtils.addProperty(element, "byWeekNo", getByWeekNoString());

		XmlUtils.addProperty(element, "byMonth", getByMonthString());
		
		if (getTimeZone() != null) {
			XmlUtils.addProperty(element, "timeZone", String.valueOf(getTimeZone().getRawOffset()));
		}

		if (creation != null)
			creation.addChangeLog(element, ObjectKeys.XTAG_ENTITY_CREATION);
		if (modification != null)
			modification.addChangeLog(element,
					ObjectKeys.XTAG_ENTITY_MODIFICATION);
		return element;

	}
	
	/**
	 * Gets a list of all recurrences until <code>until</code> date (or <code>count</code>).
	 * 
	 * If we do not reach <code>count</code> recurrences before we reach
	 * <code>max_count_time</code>, or before we have tried
	 * <code>max_count_loops</code> candidate start times, set
	 * <code>until</code> to <code>max_count_time</code>.
	 *
	 * @return "all" recurrences list. each list element is a 2-element long table, first table element is start date, 
	 * 				second one is end date
	 */
	@SuppressWarnings("unchecked")
	public List getAllRecurrenceDatesForIndexing(int maxDays) {
		List result = new ArrayList();
		if (null == getDtStart()) {
			return result;
		}

		// Is this an all day event?
		VEvent vEvent = null;
		if (!isAllDayEvent()) {
			// No!
			net.fortuna.ical4j.model.DateTime start = new net.fortuna.ical4j.model.DateTime(getDtStart().getTime());
			if (timeZone != null) {
				// it's NOT enough to set always GMT - recurrences depends on given time zone
				// f.e. recurrences on week days
				start.setTimeZone(IcalModuleImpl.getTimeZone(getTimeZone(), "GMT"));
			}

			Dur duration = getDurFromEvent();
			vEvent = new VEvent(start, duration, "");
			vEvent.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE_TIME);
		} else {
			// Yes, this is an all day event!  Construct the start date for it.
			net.fortuna.ical4j.model.Date start = new net.fortuna.ical4j.model.Date(getDtStart().getTime());
			
			// Should we use a Dur to create the VEvent?
			if (useDurForAllDayVEvent()) {
				// Yes!  Construct the VEvent using a Dur.
				vEvent = new VEvent(start, getDurFromEvent(), "");
				vEvent.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE);
			}
			else {
				// No, we shouldn't use a Dur to create the VEvent!
				// Construct the VEvent using start/end dates. 
				net.fortuna.ical4j.model.Date end = (net.fortuna.ical4j.model.Date)start.clone();
				if (getDtEnd() != null) {
					end = new net.fortuna.ical4j.model.Date(getDtEnd().getTime());
				}
				end = new net.fortuna.ical4j.model.Date(new org.joda.time.DateTime(end).plusDays(1).toDate());
				vEvent = new VEvent(start, end, "");
				vEvent.getProperties().getProperty(Property.DTSTART)
						.getParameters().add(Value.DATE);
				vEvent.getProperties().getProperty(Property.DTEND)
						.getParameters().add(Value.DATE);
			}
		}
		
		vEvent.getProperties().add(Transp.OPAQUE); // to be sure getConsumedTime works correctly
		
		IcalModuleImpl.addRecurrences(vEvent, this);

		PeriodList periods;
		try {
			periods = vEvent.getConsumedTime(new net.fortuna.ical4j.model.Date(getDtStart().getTime().getTime()-1), new net.fortuna.ical4j.model.Date(max_count_time), false);
		}
		catch (Exception ex) {
			logger.error(ex);
			periods = new PeriodList();
		}
		
		Iterator it = periods.iterator();
		while (it.hasNext()) {
			Period period = (Period)it.next();
			
			Calendar start = new GregorianCalendar();
			start.setTime(period.getStart());
			
			Calendar end = new GregorianCalendar();
			end.setTime(period.getEnd());
			
			// Is this an all day event that we didn't use a Dur
			// to construct the VEvent with?
			if (isAllDayEvent() && (!(useDurForAllDayVEvent()))) {
				// Yes!  Update the start/end times accordingly.
				start.setTime(new org.joda.time.DateTime(period.getStart()).plusMinutes(12*60 - 1).toDate());
				end.setTime(new org.joda.time.DateTime(period.getStart()).plusMinutes(12*60 - 1).toDate());
			}
			
			result.add(new Calendar[] { start, end });
			if (result.size() > maxDays) {
				break;
			}
		}
		
		return result;
	}
	
	/*
	 * Constructs a Dur object based on an Event's Duration.
	 */
	private Dur getDurFromEvent() {
		Dur duration;
		if (getDuration().getWeeks() > 0) {
			duration = new Dur(getDuration().getWeeks());
		} else {
			duration = new Dur(getDuration().getDays(), getDuration().getHours(), getDuration()
					.getMinutes(), getDuration().getSeconds());
		}
		return duration;
	}
	
	/*
	 * Returns true if we should construct VEvents using a Dur object
	 * or false if we should construct them using start/end dates.
	 * 
	 * For all day events that span multiple days, we use the Dur
	 * method.  For all day events that are only for a single day,
	 * we use the start/end dates method.
	 */
	private boolean useDurForAllDayVEvent() {
		// Do we have a duration?
		boolean reply = false;
		Duration duration = getDuration();
		if (null != duration) {
			// Yes!  If it's >= 1 day, we use Dur's.  Otherwise, we use
			// start/end dates.
			reply = ((0 < duration.getWeeks()) || (0 < duration.getDays()));
		}
		
		// If we get here, reply is true if we should use Dur's and
		// false if we should use start/end dates.  Return it.
		return reply;
	}

	@SuppressWarnings("unchecked")
	private static List getAllDatesBetweenForIndexing(Calendar start, Calendar end, int maxDays) {
		List result = new ArrayList();
		
		Calendar date = (Calendar)start.clone();
		result.add(date.clone());
		
		date.add(Calendar.DAY_OF_MONTH, 1);
		CalendarUtil.toGTTime(date);

		while (date.getTimeInMillis() <= end.getTimeInMillis()) {
			result.add(date.clone());
			if (result.size() > maxDays) {
				break;
			}
			date.add(Calendar.DAY_OF_MONTH, 1);
		};
		
		result.add(end.clone());
		return result;
	}

	public boolean isOneDayEvent() {
		if (getDtStart() == null || getDtEnd() == null) {
			return false;
		}
		YearMonthDay start = new DateTime(getDtStart()).toYearMonthDay();
		YearMonthDay end = new DateTime(getDtEnd()).toYearMonthDay();
		return (start).equals(end);
	}

	@SuppressWarnings("unchecked")
	public static List getEventDaysFromRecurrencesDatesForIndexing(List recurencesDates, int maxDays) {
		List result = new ArrayList();
		
		if (recurencesDates == null) {
			return result;
		}
		
		Iterator it = recurencesDates.iterator();
		while (it.hasNext()) {
			Calendar[] eventDates = (Calendar[]) it.next();
			int maxDaysRemaining = (maxDays - result.size());
			if (0 >= maxDaysRemaining) {
				break;
			}
			result.addAll(getAllDatesBetweenForIndexing(eventDates[0], eventDates[1], maxDaysRemaining));
		}
		
		return result;
	}
	
	public void allDaysEvent() {
		setTimeZone(null);
	}
	
	/**
	 * Uid format: [binderId]-[entryId]-[eventId]
	 * 
	 * @author nowicki
	 */
	public class UidBuilder {
		
		Long binderId;
		
		Long entryId;
		
		String eventId;
		
		public UidBuilder(DefinableEntity entry) {
			if (entry != null && entry.getParentBinder() != null && entry.getParentBinder().getId() != null) {
				this.binderId = entry.getParentBinder().getId();
			}
			if (entry != null && entry.getId() != null) {
				this.entryId = entry.getId();
			}
			if (getId() != null) {
				this.eventId = getId();
			}
		}

		/**
		 * Parse uid.
		 * 
		 * @param uid
		 */
		public UidBuilder(String uid) {
			if (uid == null) {
				return;
			}
			try {
				String tUid = uid.replaceAll("[^-]", "");
				if (tUid.length() == 2) {
					this.binderId = Long.parseLong(uid.substring(0, uid.indexOf("-")));
					this.entryId = Long.parseLong(uid.substring(uid.indexOf("-") + 1, uid.lastIndexOf("-")));
					this.eventId = uid.substring(uid.lastIndexOf("-") + 1);
				}
			} catch (NumberFormatException e) {
				// it's not intern generated uid, skip parsing
				this.binderId = null;
				this.entryId = null;
				this.eventId = null;
			}
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (binderId != null) {
				sb.append(binderId.toString());
				sb.append("-");
			}
			if (entryId != null) {
				sb.append(entryId.toString());
				sb.append("-");
			}
			if (eventId != null) {
				sb.append(eventId);
			}
			return sb.toString();
		}

		public Long getBinderId() {
			return binderId;
		}

		public Long getEntryId() {
			return entryId;
		}

		public String getEventId() {
			return eventId;
		}
		
	}

	/**
	 * Call it first when event id is set (event id is the part of uid).
	 */
	public String buildUid(DefinableEntity entry) {
		UidBuilder uidBuilder = new UidBuilder(entry);
		return uidBuilder.toString();
	}

	/**
	 * Call it first when event id is set (event id is the part of uid). Does nothing if uid is already set. 
	 */
	public String generateUid(DefinableEntity entry) {
		if (this.uid == null) {
			this.uid = buildUid(entry);
		}
		return this.uid;
	}	
	
	public UidBuilder parseUid() {
		return new UidBuilder(this.uid);
	}
}
