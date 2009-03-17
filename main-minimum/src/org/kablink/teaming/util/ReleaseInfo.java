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
package org.kablink.teaming.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReleaseInfo {

	private static final String KABLINK_TEAMING = "Kablink Teaming";
	
	static String name;
	static String version;
	static String buildNumber,buildDate;
	static String startTime;
	static boolean licenseRequiredEdition = true;
	
	static {
		name = SPropsUtil.getString("product.name", "");
		if(name.equalsIgnoreCase(KABLINK_TEAMING))
			licenseRequiredEdition = false;
			
		version = SPropsUtil.getString("release.version", "0");
		
		buildNumber = SPropsUtil.getString("release.build.number", "0");
		buildDate = SPropsUtil.getString("release.build.date", "");
	   	Date now = new Date();
		startTime = String.valueOf(now.getTime());
	}
	
	static final String releaseInfo = 
		name + " " + version + " (Build " + buildNumber + " / " + buildDate + ")";
	
	/**
	 * Returns version number if official release or <code>0.0.0</code> if unofficial.
	 * @return
	 */
	public static final String getVersion() {
		return version;
	}
	
	public static final String getName() {
		return name;
	}
	/**
	 * Returns build number if official release or <code>0</code> if unofficial. 
	 * @return
	 */
	public static final int getBuildNumber() {
		return Integer.parseInt(buildNumber);
	}
	
	/**
	 * Returns the start time of this server. 
	 * @return
	 */
	public static final String getStartTime() {
		return startTime;
	}
	
	/**
	 * Returns build date if official release or<code>null</code> if unofficial. 
	 * @return
	 */
	public static final Date getBuildDate() {
		if(buildDate.length() == 0)
			return null;
		
		SimpleDateFormat formatter =  new SimpleDateFormat("MMMM dd, yyyy");
		
		try {
			return formatter.parse(buildDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static final String getReleaseInfo() {
		return releaseInfo;
	}
	
	public static final boolean isLicenseRequiredEdition() {
		return licenseRequiredEdition;
	}
	
	/*
	public static void main(String[] args) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        
        String input = "2007-02-26 11:36:20";
        
        Date date = formatter.parse(input);
        
        String output = formatter.format(date);

        if(input.equals(output))
        	System.out.println("Match 1");
        else
        	System.out.println("Does not match 1");
        
        
        formatter = new SimpleDateFormat("MMMM dd, yyyy");
        input = "January 7, 2007";
        
        date = formatter.parse(input);
        
        output = formatter.format(date);

        if(input.equals(output))
        	System.out.println("Match 2");
        else {
        	System.out.println("Does not match 2");
        	System.out.println("Output: " + output);
        }
	}*/
}
