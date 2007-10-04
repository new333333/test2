/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.calendar;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Fixes wrong time zone definitions: converts all deprecated 3-characters time
 * zone IDs to Olson database IDs. Java above Java SE v1.3.x to Java SE 1.5.0_13
 * has wrong time zones definitions for "EST", "HST" and "MST". We convert it to
 * Olson database IDs too.
 * 
 */
public class TimeZoneHelper {

	private static Map<String, String> cZoneIdConversion = new HashMap();
	static {
		cZoneIdConversion.put("MIT", "Pacific/Apia");
		cZoneIdConversion.put("HST", "Pacific/Honolulu");
		cZoneIdConversion.put("AST", "America/Anchorage");
		cZoneIdConversion.put("PST", "America/Los_Angeles");
		cZoneIdConversion.put("MST", "America/Denver");
		cZoneIdConversion.put("PNT", "America/Phoenix");
		cZoneIdConversion.put("CST", "America/Chicago");
		cZoneIdConversion.put("EST", "America/New_York");
		cZoneIdConversion.put("IET", "America/Indianapolis");
		cZoneIdConversion.put("PRT", "America/Puerto_Rico");
		cZoneIdConversion.put("CNT", "America/St_Johns");
		cZoneIdConversion.put("AGT", "America/Buenos_Aires");
		cZoneIdConversion.put("BET", "America/Sao_Paulo");
		cZoneIdConversion.put("WET", "Europe/London");
		cZoneIdConversion.put("ECT", "Europe/Paris");
		cZoneIdConversion.put("ART", "Africa/Cairo");
		cZoneIdConversion.put("CAT", "Africa/Harare");
		cZoneIdConversion.put("EET", "Europe/Bucharest");
		cZoneIdConversion.put("EAT", "Africa/Addis_Ababa");
		cZoneIdConversion.put("MET", "Asia/Tehran");
		cZoneIdConversion.put("NET", "Asia/Yerevan");
		cZoneIdConversion.put("PLT", "Asia/Karachi");
		cZoneIdConversion.put("IST", "Asia/Calcutta");
		cZoneIdConversion.put("BST", "Asia/Dhaka");
		cZoneIdConversion.put("VST", "Asia/Saigon");
		cZoneIdConversion.put("CTT", "Asia/Shanghai");
		cZoneIdConversion.put("JST", "Asia/Tokyo");
		cZoneIdConversion.put("ACT", "Australia/Darwin");
		cZoneIdConversion.put("AET", "Australia/Sydney");
		cZoneIdConversion.put("SST", "Pacific/Guadalcanal");
		cZoneIdConversion.put("NST", "Pacific/Auckland");
	}

	private static TimeZone defaultTimeZone;
	static {
		defaultTimeZone = TimeZone.getTimeZone(getConvertedId(TimeZone
				.getDefault().getID()));
	}

	public static TimeZone getTimeZone(String ID) {
		return TimeZone.getTimeZone(getConvertedId(ID));
	}
	
	public static TimeZone fixTimeZone(TimeZone timeZone) {
		if (timeZone == null) {
			return null;
		}
		return TimeZone.getTimeZone(getConvertedId(timeZone.getID()));
	}

	public static TimeZone getDefault() {
		return defaultTimeZone;
	}

	private static String getConvertedId(String id) {
		if (!cZoneIdConversion.containsKey(id)) {
			return id;
		}
		return (String) cZoneIdConversion.get(id);
	}

}
