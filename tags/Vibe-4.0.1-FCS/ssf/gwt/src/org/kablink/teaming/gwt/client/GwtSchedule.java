/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;



import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to represent the Schedule class found in Vibe in a way that the
 * schedule information can be passed in rpc requests/responses
 * 
 * @author jwootton@novell.com
 */
public class GwtSchedule 
	implements IsSerializable, VibeRpcResponseData
{
	private boolean m_enabled;
	private DayFrequency m_dayFrequency;
	private TimeFrequency m_timeFrequency;
	private boolean m_onMonday;
	private boolean m_onTuesday;
	private boolean m_onWednesday;
	private boolean m_onThursday;
	private boolean m_onFriday;
	private boolean m_onSaturday;
	private boolean m_onSunday;
	private int m_atHours;
	private int m_atMinutes;
	private int m_repeatEveryValue;	// This value could be in minutes or hours depending on the value of m_timeFrequency
	
	
	/**
	 * The different schedule frequencies
	 */
	public enum DayFrequency implements IsSerializable
	{
		EVERY_DAY,
		ON_SELECTED_DAYS,
		UNKNOWN
	}
	
	/**
	 * The possible times
	 */
	public enum TimeFrequency implements IsSerializable
	{
		AT_SPECIFIC_TIME,
		REPEAT_EVERY_HOUR,
		REPEAT_EVERY_MINUTE,
		UNKNOWN
	}
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtSchedule()
	{
		// Nothing to do.
		m_dayFrequency = DayFrequency.EVERY_DAY;
	}	

	/**
	 * 
	 */
	public int getAtHours()
	{
		return m_atHours;
	}
	
	/**
	 * 
	 */
	public String getAtHoursAsString()
	{
		return String.valueOf( m_atHours );
	}
	
	/**
	 * 
	 */
	public int getAtMinutes()
	{
		return m_atMinutes;
	}
	
	/**
	 * 
	 */
	public String getAtMinutesAsString()
	{
		if ( m_atMinutes == 0 )
			return "00";
		
		if ( m_atMinutes == 5 )
			return "05";
		
		return String.valueOf( m_atMinutes );
	}
	
	/**
	 * 
	 */
	public DayFrequency getDayFrequency()
	{
		return m_dayFrequency;
	}
	
	/**
	 * 
	 */
	public boolean getEnabled()
	{
		return m_enabled;
	}
	
	/**
	 * 
	 */
	public boolean getOnMonday()
	{
		return m_onMonday;
	}
	
	/**
	 * 
	 */
	public boolean getOnTuesdy()
	{
		return m_onTuesday;
	}
	
	/**
	 * 
	 */
	public boolean getOnWednesday()
	{
		return m_onWednesday;
	}
	
	/**
	 * 
	 */
	public boolean getOnThursday()
	{
		return m_onThursday;
	}
	
	/**
	 * 
	 */
	public boolean getOnFriday()
	{
		return m_onFriday;
	}
	
	/**
	 * 
	 */
	public boolean getOnSaturday()
	{
		return m_onSaturday;
	}
	
	/**
	 * 
	 */
	public boolean getOnSunday()
	{
		return m_onSunday;
	}

	/**
	 * 
	 */
	public int getRepeatEveryValue()
	{
		return m_repeatEveryValue;
	}
	
	/**
	 * 
	 */
	public TimeFrequency getTimeFrequency()
	{
		return m_timeFrequency;
	}

	/**
	 * 
	 */
	public void setAtHours( int hours )
	{
		m_atHours = hours;
	}
	
	/**
	 * 
	 */
	public void setAtMinutes( int minutes )
	{
		m_atMinutes = minutes;
	}
	
	/**
	 * 
	 */
	public void setDayFrequency( DayFrequency frequency )
	{
		m_dayFrequency = frequency;
	}
	
	/**
	 * 
	 */
	public void setEnabled( boolean enabled )
	{
		m_enabled = enabled;
	}

	/**
	 * 
	 */
	public void setOnMonday( boolean set )
	{
		m_onMonday = set;
	}

	/**
	 * 
	 */
	public void setOnTuesday( boolean set )
	{
		m_onTuesday = set;
	}

	/**
	 * 
	 */
	public void setOnWednesday( boolean set )
	{
		m_onWednesday = set;
	}

	/**
	 * 
	 */
	public void setOnThursday( boolean set )
	{
		m_onThursday = set;
	}

	/**
	 * 
	 */
	public void setOnFriday( boolean set )
	{
		m_onFriday = set;
	}

	/**
	 * 
	 */
	public void setOnSaturday( boolean set )
	{
		m_onSaturday = set;
	}

	/**
	 * 
	 */
	public void setOnSunday( boolean set )
	{
		m_onSunday = set;
	}

	/**
	 * 
	 */
	public void setRepeatEveryValue( int value )
	{
		m_repeatEveryValue = value;
	}
	
	/**
	 * 
	 */
	public void setTimeFrequency( TimeFrequency frequency )
	{
		m_timeFrequency = frequency;
	}
}
