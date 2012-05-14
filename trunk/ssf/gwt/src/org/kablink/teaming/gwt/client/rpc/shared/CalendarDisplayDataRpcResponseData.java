/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.rpc.shared;

import org.kablink.teaming.gwt.client.util.CalendarDayView;
import org.kablink.teaming.gwt.client.util.CalendarHours;
import org.kablink.teaming.gwt.client.util.CalendarShow;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get calendar display
 * data' RPC command.
 * 
 * @author drfoster@novell.com
 */
public class CalendarDisplayDataRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private CalendarDayView	m_dayView;				// Day view:  1 day, 3 days, 5 days, 1 week, 2 weeks or 1 month.
	private CalendarHours	m_hours;				//
	private CalendarShow	m_show;					//
	private String			m_displayDate;			// The date to display in the navigation bar corresponding to the selected view.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public CalendarDisplayDataRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param dayView
	 * @param hours
	 * @param show
	 * @param displayDate
	 */
	public CalendarDisplayDataRpcResponseData(CalendarDayView dayView, CalendarHours hours, CalendarShow show, String displayDate) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setDayView(    dayView    );
		setHours(      hours      );
		setShow(       show       );
		setDisplayDate(displayDate);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public CalendarDayView getDayView()     {return m_dayView;    }
	public CalendarHours   getHours()       {return m_hours;      }
	public CalendarShow    getShow()        {return m_show;       }
	public String          getDisplayDate() {return m_displayDate;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDayView(    CalendarDayView dayView)     {m_dayView     = dayView;    }
	public void setHours(      CalendarHours   hours)       {m_hours       = hours;      }
	public void setShow(       CalendarShow    show)        {m_show        = show;       }
	public void setDisplayDate(String          displayDate) {m_displayDate = displayDate;}
}
