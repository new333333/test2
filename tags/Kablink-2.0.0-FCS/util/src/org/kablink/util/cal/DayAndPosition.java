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
/*
 * Copyright (c) 2000, 2001, 2002 Columbia University.  All rights reserved.
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

// DayAndPosition -- represent a day-of-the-week and its position within
// a larger unit (month or year)

package org.kablink.util.cal;

import java.util.Calendar;


/**
 * <code>DayAndPosition</code> is a utility class for representing byday
 * parameters in iCalendar recurrences and CPL time-switches.<p>
 *
 * It simply holds two pieces of information: a day of the week, and a day
 * position. <p>
 * 
 * The day of the week is represented by the day-of-week parameters of the
 * class {@link java.util.Calendar}: one of {@link Calendar#SUNDAY}, {@link
 * Calendar#MONDAY}, {@link Calendar#TUESDAY}, {@link Calendar#WEDNESDAY},
 * {@link Calendar#THURSDAY}, {@link Calendar#FRIDAY}, or {@link
 * Calendar#SATURDAY}.  It may also have the value
 * {@link #NO_WEEKDAY} if it has not been initialized.<p>
 *
 * The day position is an integer with a value between -53 and 53,
 * representing the <em>n</em>th occurence of the day in the enclosing
 * period (a month or a year).  Positive values count from the beginning of
 * the period; negative values count from the end.  A zero value means that
 * no position is implied, and that every day of the week with the given day
 * value is intended.<p>
 *
 * If the position is intended to represent a position within a month, only
 * the positions -5 to 5 are meaningful.  However,
 * <code>DayAndPosition</code> has no knowledge of what period its position
 * represents.<p>
 *
 * Change history:<br>
 * <dl>
 * <dt><b>1.0</b></dt><dd>Initial version</dd>
 * </dl>
 * @see Recurrence
 * @see Recurrence#setByDay
 * @see java.util.Calendar
 * @version 1.0
 * @author Jonathan Lennox */
public class DayAndPosition implements Cloneable {
  private int day;
  private int position;

  /**
   * The value of <code>dayOfWeek</code> if it has not been initalized.
   * Not equal to any of the day-of-week values from the class
   * {@link java.util.Calendar}, which run from 1 (Sunday) to 7
   * (Saturday).
   * @see #setDayOfWeek
   * @see java.util.Calendar#SUNDAY
   * @see java.util.Calendar#MONDAY
   * @see java.util.Calendar#TUESDAY
   * @see java.util.Calendar#WEDNESDAY
   * @see java.util.Calendar#THURSDAY
   * @see java.util.Calendar#FRIDAY
   * @see java.util.Calendar#SATURDAY 
   */
  public final static int NO_WEEKDAY = 0;


  /**
   * Construct a DayAndPosition with no weekday and zero recurrence.
   */
  public DayAndPosition() {
    day = NO_WEEKDAY;
    position = 0;
  }


  /**
   * Construct a DayAndPosition with the given weekday and recurrence.
   *
   * @param d The day
   * @param p The period
   * @throws java.lang.IllegalArgumentException  If either argument is out
   *         of range.
   */
  public DayAndPosition(int d, int p) {
    if (!isValidDayOfWeek(d))
      throw new IllegalArgumentException("Invalid day of week");
    if (!isValidDayPosition(p))
      throw new IllegalArgumentException("Invalid day position");
    day = d;
    position = p;
  }


  /**
   * Construct a DayAndPosition from the given string
   * 
   * @param s String
   * @throws java.lang.IllegalArgumentException If the given string does
   *         not describe a valid DayAndPosition
   */
  public DayAndPosition(String s)
  {
    setString(s);
  }


  /**
   * Get the standardized string representation of the DayAndPosition
   *
   * @return The string representation, of the form [[-]num]DY
   */
  public String getString()
  {
    StringBuffer buf = new StringBuffer();

    if (position != 0) {
      buf.append(position);
    }

    buf.append(generateDayOfWeek(day));

    return buf.toString();
  }


  /**
   * Set the DayAndPosition to the given string
   *
   * @param s String
   * @throws java.lang.IllegalArgumentException If the given string does
   *         not describe a valid DayAndPosition
   */
  public void setString(String s)
  {
    int p = 0;
    int d;
    int it = 0;
    int end = s.length();

    if (it == end) {
      throw new
        IllegalArgumentException("Bad day-and-position string \"" + s +
                                 "\": no day-of-week");
    }

    boolean negative = false;
    if (s.charAt(it) == '-') {
      negative = true;
      it++;
      if (it == end || !Character.isDigit(s.charAt(it))) {
        throw new IllegalArgumentException("Bad day-and-position string \"" +
                                           s + "\": minus sign but no digits");
      }
    }

    for(; it != end && Character.isDigit(s.charAt(it)); it++) {
      p = p * 10 + (s.charAt(it) - '0');
    }
    if (negative) {
      p = -p;
    }

    if (it == end) {
      throw new IllegalArgumentException("Bad day-and-position string \"" +
                                         s + "\": no day-of-week");
    }

    String daystr = s.substring(it);

    d = parseDayOfWeek(daystr);

    if (d == NO_WEEKDAY) {
      throw new IllegalArgumentException("Bad day-and-position string \"" +
                                         s + "\": invalid day-of-week");
    }

    position = p;
    day = d;

    return;
  } 


  /**
   * Get the day of the week stored in this object.  The value will be
   * a day-of-week constant from {@link java.util.Calendar} or
   * {@link #NO_WEEKDAY}.
   *
   * @return The day of the week, or {@link #NO_WEEKDAY}.
   * @see java.util.Calendar#SUNDAY
   * @see java.util.Calendar#MONDAY
   * @see java.util.Calendar#TUESDAY
   * @see java.util.Calendar#WEDNESDAY
   * @see java.util.Calendar#THURSDAY
   * @see java.util.Calendar#FRIDAY
   * @see java.util.Calendar#SATURDAY 
   * @see DayAndPosition#NO_WEEKDAY
   */
  public int getDayOfWeek() {
    return day;
  }


  /**
   * Set the day of the week stored in this object.  The value must be
   * a day-of-week constant from {@link java.util.Calendar} or
   * {@link #NO_WEEKDAY}.
   *
   * @param d The day of the week, or {@link #NO_WEEKDAY}.
   * @see java.util.Calendar#SUNDAY
   * @see java.util.Calendar#MONDAY
   * @see java.util.Calendar#TUESDAY
   * @see java.util.Calendar#WEDNESDAY
   * @see java.util.Calendar#THURSDAY
   * @see java.util.Calendar#FRIDAY
   * @see java.util.Calendar#SATURDAY 
   * @see DayAndPosition#NO_WEEKDAY
   * @throws java.lang.IllegalArgumentException  If an invalid weekday is
   *         given.
   */
  public void setDayOfWeek(int d) {
    if (!isValidDayOfWeek(d))
      throw new IllegalArgumentException("Invalid day of week");
    day = d;
  }


  /**
   * Get the day position stored in this object.  The value is an integer
   * between -53 and 53, representing the <em>n</em>th occurence of
   * a day within a larger period (a month or year).  A value of 0 means
   * every occurence of the day.<p>
   *
   * Thus, if <code>dayOfWeek</code> were {@link Calendar#THURSDAY}, a value
   * of 1 would mean means the first Thursday, 2 would mean the seocnd
   * Thursday, -1 would mean the last Thursday, and so forth, while 0 would
   * mean every Thursday.
   *
   * @return The day position.
   */
  public int getDayPosition() {
    return position;
  }

  /**
   * Get the day of the week of this entry as a string
   * Note: this cannot take an argument because it is 
   * to be used by JSTL
   */

  public String getDayOfWeekString() {
	  switch (day) {
	  case Calendar.SUNDAY:
		  return "Sunday";
	  case Calendar.MONDAY:
		  return "Monday";
	  case Calendar.TUESDAY:
		  return "Tuesday";
	  case Calendar.WEDNESDAY:
		  return "Wednesday";
	  case Calendar.THURSDAY:
		  return "Thursday";
	  case Calendar.FRIDAY:
		  return "Friday";
	  case Calendar.SATURDAY:
		  return "Saturday";
	  }
	  return null;
  }
  
  /**
   * Set the day position stored in this object.  The value must be an
   * integer between -53 and 53, representing the <em>n</em>th occurence of
   * a day within a larger period (a month or year).  A value of 0 means
   * every occurence of the day.
   *
   * @param p The day position.
   * @throws java.lang.IllegalArgumentException  If an invalid position is
   *         given.
   */
  public void setDayPosition(int p) {
    if (!isValidDayPosition(p))
      throw new IllegalArgumentException("Invalid day position");
    position = p;
  }


  /**
   * Compare this day-and-position to the specified object.
   * The result is <code>true</code> if and only if the argument is
   * not <code>null</code> and is a <code>DayAndPosition</code> object that
   * represents the same day and position as this object.
   *
   * @param obj the object to compare with.
   * @return <code>true</code> if the objects are the same;
   *         <code>false</code> otherwise.
   */
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (this == obj) return true;
    if (!(obj instanceof DayAndPosition)) return false;

    DayAndPosition that = (DayAndPosition) obj;

    return getDayOfWeek() == that.getDayOfWeek() && 
      getDayPosition() == that.getDayPosition();
  }


  /**
   * Test if an integer is a valid day of the week.
   * The result is <code>true</code> if and only if the argument
   * is a valid weekday constant from {@link java.util.Calendar}, or
   * {@link #NO_WEEKDAY}.
   *
   * @param d The value to be tested.
   * @return  Whether the value is a valid weekday.
   * @see java.util.Calendar#SUNDAY
   * @see java.util.Calendar#MONDAY
   * @see java.util.Calendar#TUESDAY
   * @see java.util.Calendar#WEDNESDAY
   * @see java.util.Calendar#THURSDAY
   * @see java.util.Calendar#FRIDAY
   * @see java.util.Calendar#SATURDAY 
   * @see DayAndPosition#NO_WEEKDAY
   */
  public static boolean isValidDayOfWeek(int d) {
    switch (d) {
    case NO_WEEKDAY:
    case Calendar.SUNDAY:
    case Calendar.MONDAY:
    case Calendar.TUESDAY:
    case Calendar.WEDNESDAY:
    case Calendar.THURSDAY:
    case Calendar.FRIDAY:
    case Calendar.SATURDAY:
      return true;
    default:
      return false;
    }
  }


  /**
   * Test if an integer is a valid day position.
   * The result is <code>true</code> if and only if the argument
   * is in the range -53 to 53.
   *
   * @param p The value to be tested.
   * @return  Whether the value is a valid day position.
   */
  public static boolean isValidDayPosition(int p) {
    return (p >= -53 && p <= 53);
  }


  /**
   * Parse a two-char RFC 2445 day-of-week string.
   *
   * @param daystr The string to parse.
   * @return An appropriate weekday constant from Calendar, or
   *         NO_WEEKDAY if the value does not correspond to any
   *         weekday. 
   */
  public static int parseDayOfWeek(String daystr)
  {
    if (daystr.length() != 2) {
      return NO_WEEKDAY;
    }

    /* XXX: worth changing to a map? */
    if (daystr.compareToIgnoreCase("SU") == 0) {
      return Calendar.SUNDAY;
    }
    else if (daystr.compareToIgnoreCase("MO") == 0) {
      return Calendar.MONDAY;
    }
    else if (daystr.compareToIgnoreCase("TU") == 0) {
      return Calendar.TUESDAY;
    }
    else if (daystr.compareToIgnoreCase("WE") == 0) {
      return Calendar.WEDNESDAY;
    }
    else if (daystr.compareToIgnoreCase("TH") == 0) {
      return Calendar.THURSDAY;
    }
    else if (daystr.compareToIgnoreCase("FR") == 0) {
      return Calendar.FRIDAY;
    }
    else if (daystr.compareToIgnoreCase("SA") == 0) {
      return Calendar.SATURDAY;
    }
    else {
      return NO_WEEKDAY;
    }
  }

  /**
   * Return a string corresponding to the given day-of-the-week.
   * @param day  The day of the week
   * @return A two-letter string, as in RFC 2445.
   */
  public static String generateDayOfWeek(int day)
  {
    switch (day) {
    case Calendar.SUNDAY:
      return "SU";

    case Calendar.MONDAY:
      return "MO";

    case Calendar.TUESDAY:
      return "TU";

    case Calendar.WEDNESDAY:
      return "WE";

    case Calendar.THURSDAY:
      return "TH";

    case Calendar.FRIDAY:
      return "FR";

    case Calendar.SATURDAY:
      return "SA";

    default:
      throw new
        IllegalStateException("Internal error: Invalid day-of-week in " +
                              "DayAndPosition.generateDayOfWeek()");
    }
  }


  /**
   * Overrides Cloneable
   * @return A clone of this object.
   */
  public Object clone() {
    try {
      DayAndPosition other = (DayAndPosition) super.clone();
      
      other.day = day;
      other.position = position;
      
      return other;
    }
    catch (CloneNotSupportedException e) {
      // This shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }


  /**
   * Return a string representation of this day-and-position. This method 
   * is intended to be used only for debugging purposes.
   * The returned string may be empty but may not be <code>null</code>.
   * 
   * @return  A string representation of this day-and-position.
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append(getClass().getName());

    buffer.append("[day=");
    buffer.append(day);
    buffer.append(",position=");
    buffer.append(position);
    buffer.append("]");

    return buffer.toString();
  }
}
