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
package org.kablink.teaming.remoting.ws.model;

public class Duration {
	  private int seconds;
	  private int minutes;
	  private int hours;
	  private int days;
	  private int weeks;
	  
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
	   * Returns the Domain model equivalent of a WS model Duration.
	   * 
	   * @param dur
	   * 
	   * @return
	   */
	  public static org.kablink.util.cal.Duration toDomainModel(Duration dur) {
		  return
		  	new org.kablink.util.cal.Duration(
		  		dur.weeks,
		  		dur.days,
		  		dur.hours,
		  		dur.minutes,
		  		dur.seconds);
	  }
	  
	  /**
	   * Returns the WS model equivalent of a Domain model Duration.
	   * 
	   * @param dur
	   * 
	   * @return
	   */
	  public static Duration toRemoteModel(org.kablink.util.cal.Duration dur) {
		  return
		  	new Duration(
		  		dur.getWeeks(),
		  		dur.getDays(),
		  		dur.getHours(),
		  		dur.getMinutes(),
		  		dur.getSeconds());
	  }

	  /**
	   * Get'er methods.
	   * 
	   * @return
	   */
	  public int getSeconds() {return seconds;}
	  public int getMinutes() {return minutes;}
	  public int getHours()   {return hours;  }
	  public int getDays()    {return days;   }
	  public int getWeeks()   {return weeks;  }

	  /**
	   * Set'er methods.
	   * 
	   * @param
	   */
	  public void setSeconds(int seconds) {this.seconds = seconds;}
	  public void setMinutes(int minutes) {this.minutes = minutes;}
	  public void setHours(  int hours)   {this.hours   = hours;  }
	  public void setDays(   int days)    {this.days    = days;   }
	  public void setWeeks(  int weeks)   {this.weeks   = weeks;  }
	  
	  /**
	   * Get the interval represented by this <code>Duration</code>, in
	   * milliseconds.
	   *
	   * @return
	   */
	  public long getInterval() {
		  return
		  	((seconds * MILLIS_PER_SECOND) +
		  	 (minutes * MILLIS_PER_MINUTE) +
		  	 (hours   * MILLIS_PER_HOUR)   +
		  	 (days    * MILLIS_PER_DAY)    +
		  	 (weeks   * MILLIS_PER_WEEK));
	  }
}
