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
package org.kablink.teaming.gwt.client;


import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to hold the Net Folder settings stored at the zone level.
 * @author jwootton
 *
 */
public class GwtNetFolderGlobalSettings
	implements IsSerializable, VibeRpcResponseData
{
	private boolean m_jitsEnabled = true;
	private long m_maxWaitTime = 15;
	private Boolean m_useDirectoryRights;
	private Integer m_cachedRightsRefreshInterval;
	
	/**
	 * 
	 */
	public GwtNetFolderGlobalSettings()
	{
		m_useDirectoryRights = null;
		m_cachedRightsRefreshInterval = null;
	}

	/**
	 * 
	 */
	public Integer getCachedRightsRefreshInterval()
	{
		return m_cachedRightsRefreshInterval;
	}
	
	/**
	 * 
	 */
	public boolean getJitsEnabled()
	{
		return m_jitsEnabled;
	}
	
	/**
	 * The max wait time in seconds.
	 */
	public long getMaxWaitTime()
	{
		return m_maxWaitTime;
	}

	/**
	 * 
	 */
	public Boolean getUseDirectoryRights()
	{
		return m_useDirectoryRights;
	}
	
	/**
	 * 
	 */
	public void setCachedRightsRefreshInterval( Integer value )
	{
		m_cachedRightsRefreshInterval = value;
	}
	
	/**
	 * 
	 */
	public void setJitsEnabled( boolean enabled )
	{
		m_jitsEnabled = enabled;
	}
	
	/**
	 * 
	 */
	public void setMaxWaitTime( long waitTimeInSeconds )
	{
		m_maxWaitTime = waitTimeInSeconds;
	}

	/**
	 * 
	 */
	public void setUseDirectoryRights( Boolean value )
	{
		m_useDirectoryRights = value;
	}
}
