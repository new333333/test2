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
package org.kablink.teaming.web.util;
import java.util.Random;

import javax.portlet.PortletRequest;

import org.kablink.teaming.jobs.Schedule;

public class ScheduleHelper {
	//randomize the starting minutes, so not all schedules kick off the same time
	protected static Random randomMinutes = new Random(); 
	public static Schedule getSchedule(PortletRequest request, String prefix) {
		String val;
		Schedule schedule = new Schedule();
		if (prefix == null) prefix = "";
		val = PortletRequestUtils.getStringParameter(request, prefix + "hourType", "");
		if (val.equals("repeat")) {
			String sVal = PortletRequestUtils.getStringParameter(request, prefix + "hoursRepeat", "");
			if (sVal.contains(".")) {
				schedule.setHours("*");
				if (sVal.equals("0.25")) {
					schedule.setMinutes(randomMinutes.nextInt(15) + "/15");
				} else if (sVal.equals("0.5")) {
					schedule.setMinutes(randomMinutes.nextInt(30) + "/30");
				} else if (sVal.equals("0.75")) {
					schedule.setMinutes("0/45");
				}
			} else {
				schedule.setMinutes(Integer.toString(randomMinutes.nextInt(60)));
				int iVal = PortletRequestUtils.getIntParameter(request, prefix + "hoursRepeat", -1);
				if (iVal != -1)
					schedule.setHours("0/" + iVal);
				else
					schedule.setHours("0/1");
			}
		} else {
			int hours = PortletRequestUtils.getIntParameter(request, prefix + "schedHours", -1);
			if (hours != -1) schedule.setHours(Integer.toString(hours));
			int mins = PortletRequestUtils.getIntParameter(request, prefix + "schedMinutes", -1);
			if (mins != -1) schedule.setMinutes(Integer.toString(mins));
			
		}		
		
		
		val = PortletRequestUtils.getStringParameter(request, prefix + "schedType", "");
		if (val.equals("daily")) {
			schedule.setDaily(true);
		} else {
			schedule.setDaily(false);
			schedule.setOnSunday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_sun", false));
			schedule.setOnMonday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_mon", false));
			schedule.setOnTuesday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_tue", false));
			schedule.setOnWednesday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_wed", false));
			schedule.setOnThursday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_thu", false));
			schedule.setOnFriday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_fri", false));
			schedule.setOnSaturday(PortletRequestUtils.getBooleanParameter(request, prefix + "onday_sat", false));
			
		}
		return schedule;
	}
}
