/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
 * This class is used to hold the Desktop Application Configuration
 * data.
 * 
 * @author drfoster@novell.com
 */
public class GwtFileSyncAppConfiguration implements IsSerializable, VibeRpcResponseData {
	private boolean						m_isFileSyncAppEnabled;		//
	private boolean						m_deploymentEnabled;		//
	private boolean						m_allowCachePwd;			//
	private boolean						m_useLocalApps;				//
	private boolean						m_useRemoteApps = true;		//
	private boolean						m_localAppsExist;			//
	private boolean						m_isCachedFilesEnabled = true;
	private boolean						m_allowCacheLifetimeChange = true;	// allow desktop user to override settings
	private GwtDesktopApplicationsLists	m_desktopApplicationsLists;	//
	private int							m_maxFileSize;				//
	private int							m_syncInterval = 15;		//
	private String						m_autoUpdateUrl;			//
	private int							m_cachedFilesLifetime = 30;	// in days
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public GwtFileSyncAppConfiguration() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                     getIsFileSyncAppEnabled()        {return m_isFileSyncAppEnabled;    }
	public boolean                     getIsDeploymentEnabled()         {return m_deploymentEnabled;       }
	public boolean                     getAllowCachePwd()               {return m_allowCachePwd;           }
	public boolean                     getUseLocalApps()                {return m_useLocalApps;            }
	public boolean                     getUseRemoteApps()               {return m_useRemoteApps;           }
	public boolean                     getLocalAppsExist()              {return m_localAppsExist;          }
	public boolean					   getIsCachedFilesEnabled()		{return m_isCachedFilesEnabled;	   }
	public boolean                     getAllowCacheLifetimeChange()    {return m_allowCacheLifetimeChange;}
	public GwtDesktopApplicationsLists getGwtDesktopApplicationsLists() {return m_desktopApplicationsLists;}
	public int                         getMaxFileSize()                 {return m_maxFileSize;             }
	public int                         getSyncInterval()                {return m_syncInterval;            }
	public int						   getCachedFilesLifetime()			{return m_cachedFilesLifetime;	   }
	public String                      getAutoUpdateUrl()               {return m_autoUpdateUrl;           }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setIsFileSyncAppEnabled(       boolean                     enabled)                  {m_isFileSyncAppEnabled     = enabled;                 }
	public void setIsDeploymentEnabled(        boolean                     enabled)                  {m_deploymentEnabled        = enabled;                 }
	public void setAllowCachePwd(              boolean                     allow)                    {m_allowCachePwd            = allow;                   }
	public void setUseLocalApps(               boolean                     useLocalApps)             {m_useLocalApps             = useLocalApps;            }
	public void setUseRemoteApps(              boolean                     useRemoteApps)            {m_useRemoteApps            = useRemoteApps;           }
	public void setLocalAppsExist(             boolean                     localAppsExist)           {m_localAppsExist           = localAppsExist;          }
	public void setIsCachedFilesEnabled(	   boolean 					   enabled)					 {m_isCachedFilesEnabled 	 = enabled;					}
	public void setAllowCacheLifetimeChange(   boolean                     override)                 {m_allowCacheLifetimeChange = override;                }
	public void setGwtDesktopApplicationsLists(GwtDesktopApplicationsLists desktopApplicationsLists) {m_desktopApplicationsLists = desktopApplicationsLists;}
	public void setMaxFileSize(                int                         size)                     {m_maxFileSize              = size;                    }
	public void setSyncInterval(               int                         intervalInMinutes)        {m_syncInterval             = intervalInMinutes;       }
	public void setCachedFilesLifetime(		   int						   lifeTimeDays)			 {m_cachedFilesLifetime		 = lifeTimeDays;			}
	public void setAutoUpdateUrl(              String                      url)                      {m_autoUpdateUrl            = url;                     }
}
