/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

// Duration -- represent an iCal duration.

package org.kablink.util.cal;


/**
 * <code>Duration</code> represents a duration field in an iCalendar recurrence
 * or a CPL time-switch.<p>
 *
 * It represents a duration of time, expressed as a number of days, hours,
 * minutes, and seconds, or a number of weeks.<p>
 *
 * <b>The iCalendar spec forbids a duration from simultaneously specifying
 * weeks
 * and other units.</b>  The <code>Duration</code> class enforces this
 * restriction.  Trying to specify a week value when there are any non-zero
 * non-week values, or a non-week value when there is a non-zero week value,
 * will throw an <code>InvalidStateException</code> exception.<p>
 *
 * Values may be specified which are larger than permitted for their
 * enclosing fields.  They will be normalized appropriately.<p>
 *
 * Change history:<br>
 * <dl>
 * <dt><b>1.0</b></dt><dd>Initial version</dd>
 * </dl>
 * @see Recurrence
 * @version 1.0
 * @author Jonathan Lennox
 */
public class Duration implements Cloneable {
  private int weeks;
  private int days;
  private int hours;
  private int minutes;
  private int seconds;

  public final static int SECONDS_PER_MINUTE = 60;
  public final static int MINUTES_PER_HOUR   = 60;
  public final static int HOURS_PER_DAY      = 24;
  public final static int DAYS_PER_WEEK      = 7;

  public final static int  MILLIS_PER_SECOND = 1000;
  public final static int  MILLIS_PER_MINUTE = (SECONDS_PER_MINUTE * MILLIS_PER_SECOND);
  public final static long MILLIS_PER_HOUR   = (MINUTES_PER_HOUR   * MILLIS_PER_MINUTE);
  public final static long MILLIS_PER_DAY    = (HOURS_PER_DAY      * MILLIS_PER_HOUR);
  public final static long MILLIS_PER_WEEK   = (DAYS_PER_WEEK      * MILLIS_PER_DAY);

  /**
   * Construct a <code>Duration</code> object and initializes it to
   * a zero-length interval.
   */
  public Duration() {
    /* Zero-initialization of all fields happens by default */
  }


  /**
   * Construct a <code>Duration</code> object with the specified duration.
   *
   * @param  w  The number of weeks.
   * @param  d  The number of days.
   * @param  h  The number of hours.
   * @param  m  The number of minutes.
   * @param  s  The number of seconds.
   */
  public Duration(int w, int d, int h, int m, int s) {
	weeks = w;
    days = d;
    hours = h;
    minutes = m;
    seconds = s;
  }

  /**
   * Construct a <code>Duration</code> object with the specified duration.
   *
   * @param  d  The number of days.
   * @param  h  The number of hours.
   * @param  m  The number of minutes.
   * @param  s  The number of seconds.
   */
  public Duration(int d, int h, int m, int s) {
    days = d;
    hours = h;
    minutes = m;
    seconds = s;
  }


  /**
   * Construct a <code>Duration</code> object with the specified duration.
   *
   * @param  h  The number of hours.
   * @param  m  The number of minutes.
   * @param  s  The number of seconds.
   */
  public Duration(int h, int m, int s) {
    this(0, h, m, s);
  }


  /**
   * Construct a <code>Duration</code> object with the specified duration.
   *
   * @param  w  The number of weeks.
   */
  public Duration(int w) {
    weeks = w;
  }


  /**
   * Construct a <code>Duration</code> object described by the given string.
   * A duration string is defined as follows, according to RFC 2445:
   * <pre>
   *     dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
   *
   *     dur-date   = dur-day [dur-time]
   *     dur-time   = "T" (dur-hour / dur-minute / dur-second)
   *     dur-week   = 1*DIGIT "W"
   *     dur-hour   = 1*DIGIT "H" [dur-minute]
   *     dur-minute = 1*DIGIT "M" [dur-second]
   *     dur-second = 1*DIGIT "S"
   *     dur-day    = 1*DIGIT "D"
   * </pre>
   *
   * @param  s  The string representation of the duration.
   * @throws IllegalArgumentException If the given string does not
   *         describe a valid Duration
   */
  public Duration(String s)
  {
    setString(s);
  }


  /**
   * Reset the duration to a zero-length interval.
   */
  public void clear() {
    weeks = 0;
    days = 0;
    hours = 0;
    minutes = 0;
    seconds = 0;
  }


  /**
   * Set the value of the <code>Duration</code> object to the given string.
   * A duration string is defined as follows, according to RFC 2445:
   * <pre>
   *     dur-value  = (["+"] / "-") "P" (dur-date / dur-time / dur-week)
   *
   *     dur-date   = dur-day [dur-time]
   *     dur-time   = "T" (dur-hour / dur-minute / dur-second)
   *     dur-week   = 1*DIGIT "W"
   *     dur-hour   = 1*DIGIT "H" [dur-minute]
   *     dur-minute = 1*DIGIT "M" [dur-second]
   *     dur-second = 1*DIGIT "S"
   *     dur-day    = 1*DIGIT "D"
   * </pre>
   *
   * @param  str  The string representation of the duration.
   * @throws IllegalArgumentException If the given string does not
   *         describe a valid Duration
   */
  public void setString(String str)
  {
	str = str.trim();
    int s = 0;
    if(str.startsWith("DURATION:")) {
    	s = 9;
    }
    int end = str.length();
  
    int w = 0, d = 0, h = 0, m = 0, sec = 0;
  
    if (s == end) {
      throw new
        IllegalArgumentException("Bad duration string: missing \'P\'");
    }
    if (str.charAt(s) == '-') {
      throw new
        IllegalArgumentException("Bad duration string: negative durations " +
                                 "not supported");
    }
    else if (str.charAt(s) == '+') {
      s++;
    }
  
    if (s == end || str.charAt(s) != 'P') {
      throw new IllegalArgumentException("Bad duration string: missing \'P\'");
    }
    s++;
  
    boolean seen_T = false;
    boolean seen_W = false;
    boolean seen_non_W = false;
    boolean value_has_digits = false;
    int value = 0;
    for (; s != end; s++) {
      if (Character.isDigit(str.charAt(s))) {
        value = value * 10 + (str.charAt(s) - '0');
        value_has_digits = true;
      }
      else {
        switch (str.charAt(s)) {
        case 'T':
          seen_T = true;
          break;
        
        case 'W':
          if (seen_T) {
            throw new
              IllegalArgumentException("Bad duration string: " +
                                       "\'W\' after \'T\'");
          }
          else if (seen_non_W) {
            throw new
              IllegalArgumentException("Bad duration string: " +
                                       "mixed \'W\' and non-\'W\'");
          }
          else if (!value_has_digits) {
            throw new
              IllegalArgumentException("Bad duration string: " +
                                       "\'W\' not after value");
          }
          w = value;
          seen_W = true;
          break;
        
        case 'D':
          if (seen_T) {
            throw new
              IllegalArgumentException("Bad duration string: " +
                                       "\'D\' after \'T\'");
          }
          else if (seen_W) {
            throw new
              IllegalArgumentException("Bad duration string: " +
                                       "mixed \'W\' and non-\'W\'");
          }
          else if (!value_has_digits) {
            throw new
              IllegalArgumentException("Bad duration string: " +
                                       "\'D\' not after value");
          }
          d = value;
          seen_non_W = true;
          break;
        
        case 'H':
        case 'M':
        case 'S':
          if (!seen_T) {
            throw new IllegalArgumentException("Bad duration string: \'" +
                                               str.charAt(s) +
                                               "\' not after \'T\'");
          }
          else if (seen_W) {
            throw new IllegalArgumentException("Bad duration string:" +
                                               "mixed \'W\' and non-\'W\'");
          }
          else if (!value_has_digits) {
            throw new IllegalArgumentException("Bad duration string: \'" +
                                               str.charAt(s) +
                                               "\' not after value");
          }
          if (str.charAt(s) == 'H') {
            h = value;
          }
          else if (str.charAt(s) == 'M') {
            m = value;
          }
          else if (str.charAt(s) == 'S') {
            sec = value;
          }
          seen_non_W = true;
          break;
        
        default:
          throw new
            IllegalArgumentException("Bad duration string: " +
                                     "unknown duration specifier \'" +
                                     str.charAt(s) +
                                     "\'");
        }
        value = 0;
        value_has_digits = false;
      }
    }
  
    weeks = w;
    days = d;
    hours = h;
    minutes = m;
    seconds = sec;
  
    return;
  }


  /**
   * Get the standardized string representation of the Duration.
   *
   * @return The string representation, of the form
   * P(<num>W|[<num>D]T[<num>H][<num>M][<num>S]).  A zero-time duration is
   * represented as PT0S.
   */
  public String getString()
  {
    StringBuffer buf = new StringBuffer();
  
    buf.append("P");
    if (weeks != 0) {
      buf.append(weeks);
      buf.append("W");
    }
    if (days != 0) {
      buf.append(days);
      buf.append("D");
    }
    if (hours != 0 || minutes != 0 || seconds != 0) {
      buf.append("T");
    }
    if (hours != 0) {
      buf.append(hours);
      buf.append("H");
    }
    if (minutes != 0) {
      buf.append(minutes);
      buf.append("M");
    }
    if (seconds != 0) {
      buf.append(seconds);
      buf.append("S");
    }
    if (weeks == 0 && days == 0 && hours == 0 && minutes == 0 &&
        seconds == 0) {
      buf.append("T0S");
    }
    return buf.toString();
  }


  /**
   * Get the weeks field of this duration.
   * @return The number of weeks.
   */
  public int getWeeks() {
    return weeks;
  }


  /**
   * Set the weeks field of this duration.
   * @param w The number of weeks.
   * @throws IllegalArgumentException If <code>w</code> is negative.
   * @throws IllegalStateException    If any non-week field is set.
   */
  public void setWeeks(int w) {
    if (w < 0) throw new IllegalArgumentException("Week value out of range");
    checkWeeksOkay(w);
    weeks = w;
  }


  /**
   * Get the days field of this duration.
   * @return The number of days.
   */
  public int getDays() {
    return days;
  }


  /**
   * Set the days field of this duration.
   * @param d The number of days.
   * @throws IllegalArgumentException If <code>d</code> is negative.
   * @throws IllegalStateException If the week field is set.
   */
  public void setDays(int d) {
    if (d < 0) throw new IllegalArgumentException("Day value out of range");
    checkNonWeeksOkay(d);
    days = d;
    normalize();
  }


  /**
   * Get the hours field of this duration.
   * @return The number of hours.
   */
  public int getHours() {
    return hours;
  }


  /**
   * Set the hours field of this duration.
   * @param h The number of hours.
   * @throws IllegalArgumentException If <code>h</code> is negative.
   * @throws IllegalStateException If the week field is set.
   */
  public void setHours(int h) {
    if (h < 0) throw new IllegalArgumentException("Hour value out of range");
    checkNonWeeksOkay(h);
    hours = h;
    normalize();
  }


  /**
   * Get the minutes field of this duration.
   * @return The number of minutes.
   */
  public int getMinutes() {
    return minutes;
  }


  /**
   * Set the minutes field of this duration.
   * @param m The number of minutes.
   * @throws IllegalArgumentException If <code>m</code> is negative.
   * @throws IllegalStateException If the week field is set.
   */
  public void setMinutes(int m) {
    if (m < 0) throw new IllegalArgumentException("Minute value out of range");
    checkNonWeeksOkay(m);
    minutes = m;
    normalize();
  }


  /**
   * Get the seconds field of this duration.
   * @return The number of seconds.
   */
  public int getSeconds() {
    return seconds;
  }


  /**
   * Set the seconds field of this duration.
   * @param s The number of days.
   * @throws IllegalArgumentException If <code>s</code> is negative.
   * @throws IllegalStateException If the week field is set.
   */
  public void setSeconds(int s) {
    if (s < 0) throw new IllegalArgumentException("Second value out of range");
    checkNonWeeksOkay(s);
    seconds = s;
    normalize();
  }


  /**
   * Get the interval represented by this <code>Duration</code>, in
   * milliseconds.  Pure Gregorian time is used; leap-seconds and
   * daylight-savings time transitions are ignored.
   *
   * @return  the number of milliseconds equivalent to this duration.
   */
  public long getInterval() {
    return
      seconds * MILLIS_PER_SECOND +
      minutes * MILLIS_PER_MINUTE +
      hours   * MILLIS_PER_HOUR +
      days    * MILLIS_PER_DAY +
      weeks   * MILLIS_PER_WEEK;
  }


  /**
   * Set the duration's interval to the specified number of milliseconds.
   * Pure-Gregorian time is used; leap-seconds and daylight-savings time
   * transitions are ignored.  The duration is rounded to a whole number
   * of seconds.  The duration is broken down into the <code>days</code>,
   * <code>hours</code>, <code>minutes</code>, and <code>seconds</code>
   * fields; the <code>weeks</code> field will be zero after this method is
   * called.
   *
   * @param millis  The number of milliseconds for this duration.
   * @throws IllegalArgumentException If <code>millis</code> is negative.
   */
  public void setInterval(long millis) {
    if (millis < 0) {
    	// Bugzilla 505869:  Commented out the following throw.
    	//    When the DTSTART occurs before the DUE, don't fail with
    	//    an exception.  Simply assume a 0 length interval.
    	//
    	// throw new IllegalArgumentException("Negative-length interval");
    	millis = 0;
    }
    clear();

    days = (int)(millis / MILLIS_PER_DAY);
    seconds = (int)((millis % MILLIS_PER_DAY) / MILLIS_PER_SECOND);

    normalize();
  }


  /**
   * Normalize the duration, so that every field is within its legal range.
   */
  protected void normalize() {
    minutes += seconds / SECONDS_PER_MINUTE;
    seconds %= SECONDS_PER_MINUTE;
    
    hours   += minutes / MINUTES_PER_HOUR;
    minutes %= MINUTES_PER_HOUR;
    
    /* N.B. do *not* normalize hours to days.  24 hours is not the same thing
     * as 1 day, if we're near a DST jump. */
  }


  /**
   * Check if it would be okay to set the weeks field of this duration.
   * This is allowed if none of the other fields are set, or if the
   * prospective value for the weeks field is zero.
   * @param f The prospective value for the weeks field
   * @throws IllegalStateException If it wouldn't be okay.
   */
  protected void checkWeeksOkay(int f) {
    if (f != 0 &&
        (days != 0 || hours != 0 || minutes != 0 || seconds != 0)) {
      throw new
        IllegalStateException("Weeks and non-weeks are incompatible");
    }
  }


  /**
   * Check if it would be okay to set a non-weeks field of this duration.
   * This is allowed if the weeks field is not set, or if the
   * prospective value for this non-weeks field is zero.
   * @param f The prospective value for the non-weeks field
   * @throws IllegalStateException If it wouldn't be okay.
   */
  protected void checkNonWeeksOkay(int f) {
    if (f != 0 && weeks != 0) {
      throw new
        IllegalStateException("Weeks and non-weeks are incompatible");
    }
  }


  /**
   * Overrides Cloneable
   * @return A clone of this object.
   */
  public Object clone() {
    try {
      Duration other = (Duration) super.clone();
      
      other.weeks = weeks;
      other.days = days;
      other.hours = hours;
      other.minutes = minutes;
      other.seconds = seconds;
      
      return other;
    }
    catch (CloneNotSupportedException e) {
      // This shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }


  /**
   * Return a string representation of this duration. This method 
   * is intended to be used only for debugging purposes.
   * The returned string may be empty but may not be <code>null</code>.
   * 
   * @return  a string representation of this duration.
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
  
    buffer.append(getClass().getName());

    buffer.append("[weeks=");
    buffer.append(weeks);
    buffer.append(",days=");
    buffer.append(days);
    buffer.append(",hours=");
    buffer.append(hours);
    buffer.append(",minutes=");
    buffer.append(minutes);
    buffer.append(",seconds=");
    buffer.append(seconds);
    buffer.append("]");

    return buffer.toString();
  }
  
	/**
	 * Returns true if the Duration only contains a days value and
	 * false otherwise.
	 *
	 * @return
	 */
	public boolean hasDaysOnly() {
		return
			((0 == seconds) &&
			 (0 == minutes) &&
			 (0 == hours)   &&
			 (0 != days)    &&
			 (0 == weeks));
	}
}

