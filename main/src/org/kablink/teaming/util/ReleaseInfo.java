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
package org.kablink.teaming.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReleaseInfo {

	private static final String KABLINK_TEAMING = "Kablink Vibe";
	
	protected static Log m_logger = LogFactory.getLog( ReleaseInfo.class );
	
	static String name;
	static String version;
	static String buildNumber,buildDate;
	static Date serverStartTime;
	static String startTime;
	static boolean licenseRequiredEdition = true;
	static String contentVersion;
	
	static {
		name = SPropsUtil.getString("product.name", "");
		if(name.equalsIgnoreCase(KABLINK_TEAMING))
			licenseRequiredEdition = false;
			
		version = SPropsUtil.getString("release.version", "0");

		// Are we running Filr
		if ( Utils.checkIfFilr() )
		{
			// Yes, get the Filr release version
			version = SPropsUtil.getString( "filr.release.version", "0" );
		}
		
		buildNumber = SPropsUtil.getString("release.build.number", "0");
		buildDate = SPropsUtil.getString("release.build.date", "");
	   	serverStartTime = new Date();
		startTime = String.valueOf(serverStartTime.getTime());
		contentVersion = SPropsUtil.getString("ssf.content.version", "v3");
	}
	
	static final String releaseInfo = buildReleaseInfoString(buildDate);

	// Dump out the release information.  This must be done after the call to buildReleaseInfoString();
	static
	{
		if ( m_logger.isInfoEnabled() )
			m_logger.info( ReleaseInfo.getReleaseInfo() );
		else
			System.out.println( ReleaseInfo.getReleaseInfo() );
	}
	
	private static final String buildReleaseInfoString(String buildDateStr) {
		return name + " " + version + " (" + NLT.get( "releaseInfo.build" ) + buildNumber  + " / " + buildDateStr + ")";
	}
	
	/**
	 * 
	 */
	public static final String getBuildAsStr( Locale locale )
	{
		return buildNumber + " / " + getBuildDate( locale );
	}
	
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
	 * 
	 */
	public static final String getNameAndVersion()
	{
		return name + " " + version;
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
	 * Same as <code>getStartTime</code> except that this returns <code>Date</code> value.
	 * @return
	 */
	public static final Date getServerStartTime() {
		return serverStartTime;
	}
	
	/**
	 * 
	 */
	public static String getBuildDate( Locale locale )
	{
		Date date;
		DateFormat df;

		date = getBuildDate();
		if ( date == null )
		{
			//If there isn't a build date, set it to 1/1/70 so it doesn't throw an error
			date = new Date();
			date.setTime(0);
		}
		
		df = DateFormat.getDateInstance( DateFormat.LONG, locale );
		return df.format( date );
	
	}
	
	/**
	 * Returns build date if official release or<code>null</code> if unofficial. 
	 * @return
	 */
	public static final Date getBuildDate() {
		if(buildDate.length() == 0)
			return null;
		
		// The build date is stored in the properties file as a string in one particular format,
		// which may or may not be understood by the default locale of the OS on which this server
		// is executing (e.g. Japanese/Chinese Windows Server). To avoid parse failure, we specify 
		// US locale explicitly to instruct the system how the string should be parsed.
		SimpleDateFormat formatter =  new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
		
		try {
			return formatter.parse(buildDate);
		} catch (ParseException e) {
			return null; // This shouldn't happen
		}
	}
	
	public static final String getReleaseInfo() {
		return releaseInfo;
	}
	
	public static final String getLocalizedReleaseInfo(Locale locale) {
		return buildReleaseInfoString( getBuildDate( locale ) );
	}

    public static String getFilrApplianceReleaseInfo()
    {
        String version = getApplianceVersionString();
        if ( version != null )
        {
            return NLT.get( "filr.appliance" ) + " " + version;
        }
        return null;
    }

    public static String getApplianceVersion()
    {
        String versionStr = getApplianceVersionString();
        if ( versionStr != null )
        {
        	int len;
        	int separator;
        	
        	len = versionStr.length();
        	separator = '.';
        	
        	// Find the 3rd '.', everything before the 3rd '.' is the appliance version
            int index = versionStr.indexOf( separator );
            if ( index >= 0 && index < len )
            {
            	++index;
            	index = versionStr.indexOf( separator, index );
            	if ( index != -1 && index < len )
            	{
            		++index;
            		index = versionStr.indexOf( separator, index );
            		if ( index != -1 )
            			return versionStr.substring( 0, index );
            	}
            }

            return versionStr;
        }

        return null;
    }

    public static Integer getApplianceBuildNumber()
    {
        String versionStr = getApplianceVersionString();
        if ( versionStr != null )
        {
            int index = versionStr.lastIndexOf('.');
            if (index>=0)
            {
                try {
                    return Integer.parseInt(versionStr.substring(index + 1));
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return null;
    }

	/**
	 * Get the Filr appliance version and build as a string
	 */
	public static String getApplianceVersionString()
	{
		String version = null;
		
		// Are we running Filr?
		if ( Utils.checkIfFilr() )
		{
			String filePath;
			File file;
			
			// Yes
			// Try to open the file, /etc/Novell-VA-release
			filePath = File.separator + "etc" + File.separator + "Novell-VA-release";
			file = new File( filePath );
			if ( file.exists() )
			{
				Properties prop;

				prop = new Properties();
				try
				{
					prop.load( new FileInputStream( filePath ) );
					version = prop.getProperty( "version" );
				}
				catch ( Exception ex )
				{
					m_logger.info( "In ReleaseInfo.getFilrApplianceReleaseInfo(), exception reading version from /etc/Novell-VA-release" );
					ex.printStackTrace();
				}
			}
			else
			{
				m_logger.info( "In ReleaseInfo.getFilrApplianceReleaseInfo(), can't find /etc/Novell-VA-release" );
			}
		}
		
		return version;
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

	/**
	 * Returns the content version string to be used for Teaming.
	 * 
	 * @return
	 */
	public static String getContentVersion() {
		return contentVersion;
	}
}
