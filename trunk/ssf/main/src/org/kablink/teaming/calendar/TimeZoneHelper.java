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
package org.kablink.teaming.calendar;

import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;

/**
 * Fixes wrong time zone definitions: converts all deprecated 3-characters time
 * zone IDs to Olson database IDs. Java above Java SE v1.3.x to Java SE 1.5.0_13
 * has wrong time zones definitions for "EST", "HST" and "MST". We convert it to
 * Olson database IDs too.
 * 
 */
public class TimeZoneHelper {

	private static Map<String, String> cZoneIdConversion = new TreeMap();
	static {
		cZoneIdConversion.put("GMT", "GMT"); // leave it as it is (joda makes Etc/GMT)
		cZoneIdConversion.put("ACDT", "Australia/Adelaide");
		cZoneIdConversion.put("CSuT", "Australia/Adelaide");
		cZoneIdConversion.put("ACST", "Australia/Darwin");
		cZoneIdConversion.put("CAST", "Australia/Darwin");
		cZoneIdConversion.put("ADT", "America/Halifax");
		cZoneIdConversion.put("AEDT", "Australia/Sydney");
		cZoneIdConversion.put("ESuT", "Australia/Sydney");
		cZoneIdConversion.put("AEST", "Australia/Sydney");
		cZoneIdConversion.put("AES", "Australia/Sydney");
		cZoneIdConversion.put("EAST", "Australia/Sydney");
		cZoneIdConversion.put("AFT", "Asia/Tehran");
		cZoneIdConversion.put("AKDT", "America/Juneau");
		cZoneIdConversion.put("AKST", "America/Anchorage");
		cZoneIdConversion.put("AWDT", "Australia/Perth");
		cZoneIdConversion.put("AWST", "Australia/Perth");
		cZoneIdConversion.put("CDT", "America/Chicago");
		cZoneIdConversion.put("CEDT", "Europe/Paris");
		cZoneIdConversion.put("CEST", "Europe/Paris");
		cZoneIdConversion.put("CET", "Europe/Paris");
		cZoneIdConversion.put("CXT", "Indian/Christmas");
		cZoneIdConversion.put("EDT", "America/New_York");
		cZoneIdConversion.put("EEDT", "Europe/Athens");
		cZoneIdConversion.put("EEST", "Europe/Athens");
		cZoneIdConversion.put("HAA", "America/Halifax");
		cZoneIdConversion.put("HAC", "America/Chicago");
		cZoneIdConversion.put("HADT", "America/Adak");
		cZoneIdConversion.put("HAE", "America/New_York");
		cZoneIdConversion.put("HAP", "America/Vancouver");
		cZoneIdConversion.put("HAR", "America/Denver");
		cZoneIdConversion.put("HAST", "America/Adak");
		cZoneIdConversion.put("HAT", "America/St_Johns");
		cZoneIdConversion.put("HAY", "America/Anchorage");
		cZoneIdConversion.put("HNA", "America/Anchorage");
		cZoneIdConversion.put("HNC", "America/Chicago");
		cZoneIdConversion.put("HNE", "America/New_York");
		cZoneIdConversion.put("HNP", "America/Los_Angeles");
		cZoneIdConversion.put("HNR", "America/Denver");
		cZoneIdConversion.put("HNT", "Pacific/Auckland");
		cZoneIdConversion.put("HNY", "America/Anchorage");
		cZoneIdConversion.put("MDT", "America/Denver");
		cZoneIdConversion.put("MESZ", "Europe/Paris");
		cZoneIdConversion.put("MEZ", "Europe/Paris");
		cZoneIdConversion.put("MSD", "Europe/Moscow");
		cZoneIdConversion.put("MSK", "Europe/Moscow");
		cZoneIdConversion.put("NDT", "America/St_Johns");
		cZoneIdConversion.put("NFT", "Pacific/Norfolk");
		cZoneIdConversion.put("PDT", "America/Vancouver");
		cZoneIdConversion.put("WDT", "Australia/Perth");
		cZoneIdConversion.put("WEDT", "Europe/Lisbon");
		cZoneIdConversion.put("WEST", "Europe/Lisbon");
		cZoneIdConversion.put("WST", "Australia/Perth");
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
		cZoneIdConversion.put("KST", "Asia/Seoul");
		cZoneIdConversion.put("SBT", "Pacific/Guadalcanal");
	}

	private static TimeZone defaultTimeZone;
	static {
		defaultTimeZone = fixTimeZone(TimeZone.getDefault());
	}

	public static TimeZone getDefault() {
		return defaultTimeZone;
	}
	
	public static TimeZone getTimeZone(String ID) {
		return fixTimeZone(TimeZone.getTimeZone(fixTimeZoneId(ID)));
	}

	public static TimeZone fixTimeZone(TimeZone timeZone) {
		if (timeZone == null) {
			return null;
		}
		if (timeZone.getID().equals("GMT")) {
			return timeZone;
		}
				
		String fixedId = fixTimeZoneId(timeZone.getID());
		try {
			DateTimeZone dateTimeZone = DateTimeZone.forID(fixedId);
			if (dateTimeZone != null) {
				return dateTimeZone.toTimeZone();
			}
		} catch (IllegalArgumentException e) {
			// The datetime zone id is not recognised
		}
		return timeZone;
	}	

	private static String fixTimeZoneId(String id) {
		if (id == null) {
			return null;
		}
		
		if (cZoneIdConversion.containsKey(id)) {
			return (String) cZoneIdConversion.get(id);
		}
		
		try {
			DateTimeZone dateTimeZone = DateTimeZone.forID(id);
			if (dateTimeZone != null) {
				return dateTimeZone.toTimeZone().getID();
			}
		} catch (IllegalArgumentException e) {
			// The datetime zone id is not recognised
		}
		
		return id;
	}
	public static Set<String> getTimeZoneIds() {
		String[] ids = java.util.TimeZone.getAvailableIDs();
		//prune the list
		Set<String> results = new java.util.HashSet();
		for (int i=0; i<ids.length; ++i) {
			String id = fixTimeZoneId(ids[i]);
			if (id == null) continue;
			if (id.startsWith("Etc/") || id.startsWith("SystemV")) continue;
			if (id.length() == 3 && !id.equals("GMT")) continue; //UTC is only one left
			results.add(id);
		}
		return results;

	}
}
