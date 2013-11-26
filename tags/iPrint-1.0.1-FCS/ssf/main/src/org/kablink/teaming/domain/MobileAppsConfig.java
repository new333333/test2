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
package org.kablink.teaming.domain;

import org.kablink.teaming.util.SPropsUtil;

/**
 * Component of zoneConfig that holds the settings for mobile apps
 * @author jwootton
 *
 */
public class MobileAppsConfig
{
	private Boolean mobileAppsEnabled;
	private Boolean mobileAppsAllowCachePwd;
	private Boolean mobileAppsAllowCacheContent;
	private Boolean mobileAppsAllowPlayWithOtherApps;
	private Integer mobileAppsSyncInterval;
	
	/**
	 * 
	 */
	public MobileAppsConfig()
	{
	}

	/**
	 * 
	 */
	public boolean getMobileAppsAllowCacheContent()
	{
		if ( mobileAppsAllowCacheContent == null )
			return SPropsUtil.getBoolean( "mobile.apps.allow.cache.content.default", true );

		return mobileAppsAllowCacheContent.booleanValue();
	}
	
	/**
	 * 
	 */
	public boolean getMobileAppsAllowCachePwd()
	{
		if ( mobileAppsAllowCachePwd == null )
			return SPropsUtil.getBoolean( "mobile.apps.allow.cache.pwd.default", true );
		
		return mobileAppsAllowCachePwd.booleanValue();
	}

	/**
	 * 
	 */
	public boolean getMobileAppsAllowPlayWithOtherApps()
	{
		if ( mobileAppsAllowPlayWithOtherApps == null )
			return SPropsUtil.getBoolean( "mobile.apps.allow.play.with.other.apps.default", true );
		
		return mobileAppsAllowPlayWithOtherApps.booleanValue();
	}
	
	/**
	 * 
	 */
	public boolean getMobileAppsEnabled()
	{
		if( mobileAppsEnabled == null )
			return SPropsUtil.getBoolean( "mobile.apps.enabled.default", true );
		
		return mobileAppsEnabled.booleanValue();
	}

	/**
	 * The sync interval is in minutes.
	 */
	public int getMobileAppsSyncInterval()
	{
		if ( mobileAppsSyncInterval == null )
			return SPropsUtil.getInt( "mobile.apps.sync.interval.default", 15 );
		
		return mobileAppsSyncInterval.intValue();
	}

	/**
	 * 
	 */
	public void setMobileAppsAllowCacheContent( boolean allow )
	{
		mobileAppsAllowCacheContent = Boolean.valueOf( allow );
	}
	
	/**
	 * 
	 */
	public void setMobileAppsAllowCachePwd( boolean allow )
	{
		mobileAppsAllowCachePwd = Boolean.valueOf( allow );
	}

	/**
	 * 
	 */
	public void setMobileAppsAllowPlayWithOtherApps( boolean allow )
	{
		mobileAppsAllowPlayWithOtherApps = Boolean.valueOf( allow );
	}

	/**
	 * 
	 */
	public void setMobileAppsEnabled( boolean enabled )
	{
		mobileAppsEnabled = Boolean.valueOf( enabled );
	}
	
	/**
	 * 
	 */
	public void setMobileAppsSyncInterval( int intervalInMinutes )
	{
		mobileAppsSyncInterval = Integer.valueOf( intervalInMinutes );
	}
}
