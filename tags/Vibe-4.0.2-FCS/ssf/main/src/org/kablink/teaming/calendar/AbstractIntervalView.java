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
package org.kablink.teaming.calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.DateTools;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kablink.util.cal.CalendarUtil;


public abstract class AbstractIntervalView {
	
	public static class VisibleIntervalFormattedDates {
		
		public String startDate;
		
		public String endDate;
		
		public VisibleIntervalFormattedDates(String startDate, String endDate) {
			super();
			this.startDate = startDate;
			this.endDate = endDate;
		}
	}
	
	protected Interval interval;
	
	protected Interval visibleInterval;
	
	public VisibleIntervalFormattedDates getVisibleIntervalInTZ() {
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmm").withChronology(visibleInterval.getChronology());
		String endDate = dtf.print(visibleInterval.getEnd());
		String startDate = dtf.print(visibleInterval.getStart());
		return new VisibleIntervalFormattedDates(startDate, endDate);
	}
	
	public VisibleIntervalFormattedDates getVisibleIntervalRaw() {		
		return new VisibleIntervalFormattedDates (
				DateTools.dateToString(visibleInterval.getStart().toDate(), DateTools.Resolution.MINUTE), 
				DateTools.dateToString(visibleInterval.getEnd().toDate(), DateTools.Resolution.MINUTE));
	}
	
	public boolean dateInView(Date dateToTest) {
		if (dateToTest == null) {
			return false;
		}
		return visibleInterval.contains(dateToTest.getTime());
	}

	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return true if at least a part of event overlaps visible view.
	 */
	public boolean intervalInViewInTZ(long startTimeTZ, long endTimeTZ) {
		// The +1/-1 adjustments being made are to get around what I
		// consider to be a bug in AbstractInterval.overlaps() where it
		// doesn't properly consider the times to overlap if the
		// start/end times are equal.  DRF:  20090408
		if (startTimeTZ == this.visibleInterval.getEndMillis()) {
			startTimeTZ -= 1l;
		}
		
		if (endTimeTZ == this.visibleInterval.getStartMillis()) {
			endTimeTZ += 1l;
		}

		Interval otherInterval = new Interval(startTimeTZ, endTimeTZ, this.visibleInterval.getChronology());
		return this.visibleInterval.overlaps(otherInterval);
	}
	
	public boolean intervalInViewRaw(long startTime, long endTime) {
		return this.visibleInterval.overlaps(new Interval(startTime, endTime));
	}
	
	public Map getCurrentDateMonthInfo() {
		Map result = new HashMap();

		result.put("year", interval.getStart().getYear());
		result.put("month", interval.getStart().getMonthOfYear() - 1);
		result.put("beginView", visibleInterval.getStart().toDate());
		result.put("endView", visibleInterval.getEnd().minusDays(1).toDate());
		
		int daysInMonthView = CalendarUtil.fullDaysBetween(
				visibleInterval.getEnd().toDate(), visibleInterval.getStart().toDate());
		// there is a bug on some systems, I can't test it so small workaround
		if (daysInMonthView == 27 || daysInMonthView == 29) {
			daysInMonthView = 28; // == 4 weeks * 7
		}
		if (daysInMonthView == 34 || daysInMonthView == 36) {
			daysInMonthView = 35; // == 5 weeks * 7
		}	
		if (daysInMonthView == 41 || daysInMonthView == 43) {
			daysInMonthView = 42; // == 6 weeks * 7
		}
		result.put("numberOfDaysInView", daysInMonthView);

		return result;
	}
	
	public Date getStart() {
		return interval.getStart().toDate();
	}
	
	public Date getEnd() {
		return interval.getEnd().toDate();
	}	
	
	public Date getVisibleStart() {
		return this.visibleInterval.getStart().toDate();
	}
	
	public Date getVisibleEnd() {
		return this.visibleInterval.getEnd().toDate();
	}

}
