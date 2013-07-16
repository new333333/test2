/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
 * This class is used to hold the File Sync App Configuration data.
 * 
 * @author jwootton@novell.com
 */
public class GwtFileSyncAppConfiguration
	implements IsSerializable, VibeRpcResponseData
{
	private boolean m_isFileSyncAppEnabled = false;
	private boolean m_deploymentEnabled = false;
	private boolean m_allowCachePwd = false;
	private boolean m_useLocalApps = false;
	private boolean m_useRemoteApps = true;
	private boolean m_localAppsExist = false;
	private int m_syncInterval = 15;
	private int m_maxFileSize = 0;
	private String m_autoUpdateUrl = null;
	
	/**
	 * 
	 */
	public GwtFileSyncAppConfiguration()
	{
	}
	
	/**
	 * 
	 */
	public String getAutoUpdateUrl()
	{
		return m_autoUpdateUrl;
	}
	
	/**
	 * 
	 */
	public boolean getAllowCachePwd()
	{
		return m_allowCachePwd;
	}
	
	/**
	 * 
	 */
	public boolean getIsDeploymentEnabled()
	{
		return m_deploymentEnabled;
	}
	
	/**
	 * 
	 */
	public boolean getIsFileSyncAppEnabled()
	{
		return m_isFileSyncAppEnabled;
	}

	/**
	 * 
	 */
	public int getMaxFileSize()
	{
		return m_maxFileSize;
	}
	
	/**
	 * The sync interval is in minutes.
	 */
	public int getSyncInterval()
	{
		return m_syncInterval;
	}

	/**
	 * Whether to use desktop applications local to the system.
	 */
	public boolean getUseLocalApps()
	{
		return m_useLocalApps;
	}
	
	/**
	 * Whether to use desktop applications from a remote location.
	 */
	public boolean getUseRemoteApps()
	{
		return m_useRemoteApps;
	}
	
	/**
	 * Whether desktop applications exist on the server.
	 */
	public boolean getLocalAppsExist()
	{
		return m_localAppsExist;
	}
	
	/**
	 * 
	 */
	public void setAllowCachePwd( boolean allow )
	{
		m_allowCachePwd = allow;
	}
	
	/**
	 * 
	 */
	public void setAutoUpdateUrl( String url )
	{
		m_autoUpdateUrl = url;
	}

	/**
	 * 
	 */
	public void setIsDeploymentEnabled( boolean enabled )
	{
		m_deploymentEnabled = enabled;
	}
	
	/**
	 * 
	 */
	public void setIsFileSyncAppEnabled( boolean enabled )
	{
		m_isFileSyncAppEnabled = enabled;
	}
	
	/**
	 * 
	 */
	public void setMaxFileSize( int size )
	{
		m_maxFileSize = size;
	}
	
	/**
	 * 
	 */
	public void setSyncInterval( int intervalInMinutes )
	{
		m_syncInterval = intervalInMinutes;
	}
	
	/**
	 *
	 */
	public void setUseLocalApps( boolean useLocalApps )
	{
		m_useLocalApps = useLocalApps;
	}
	
	/**
	 *
	 */
	public void setUseRemoteApps( boolean useRemoteApps)
	{
		m_useRemoteApps = useRemoteApps;
	}
	
	/**
	 *
	 */
	public void setLocalAppsExist( boolean localAppsExist )
	{
		m_localAppsExist = localAppsExist;
	}
}
