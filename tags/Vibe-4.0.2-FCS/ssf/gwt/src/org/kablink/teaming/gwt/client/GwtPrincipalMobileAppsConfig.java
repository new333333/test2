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

import java.util.List;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtMobileOpenInSetting;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to hold the Mobile Applications Configuration
 * data stored in a user's or group's UserProperties.
 * 
 * @author drfoster@novell.com
 */
public class GwtPrincipalMobileAppsConfig implements IsSerializable, VibeRpcResponseData {
	private boolean	m_allowCachePwd;			//
	private boolean	m_allowCacheContent;		//
	private boolean	m_allowPlayWithOtherApps;	//
	private boolean m_forcePinCode;				//
	private boolean	m_mobileAppsEnabled;		//
	private boolean	m_useGlobalSettings;		//
	
	// The following are the data members for the Mobile Application
	// Management (MAM) settings.
    private boolean					m_mobileCutCopyEnabled;						//
    private boolean					m_mobileAndroidScreenCaptureEnabled;		//
    private boolean					m_mobileDisableOnRootedOrJailBrokenDevices;	//
    private GwtMobileOpenInSetting	m_mobileOpenIn;								//
    private List<String>			m_androidApplications;						//
    private List<String>			m_iosApplications;							//
    
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public GwtPrincipalMobileAppsConfig() {
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean getAllowCacheContent()      {return m_allowCacheContent;     }
	public boolean getAllowCachePwd()          {return m_allowCachePwd;         }
	public boolean getAllowPlayWithOtherApps() {return m_allowPlayWithOtherApps;}
	public boolean getForcePinCode()           {return m_forcePinCode;          }
	public boolean getMobileAppsEnabled()      {return m_mobileAppsEnabled;     }
	public boolean getUseGlobalSettings()      {return m_useGlobalSettings;     }
	
    public boolean                getMobileCutCopyEnabled()                     {return m_mobileCutCopyEnabled;                    }
    public boolean                getMobileAndroidScreenCaptureEnabled()        {return m_mobileAndroidScreenCaptureEnabled;       }
    public boolean                getMobileDisableOnRootedOrJailBrokenDevices() {return m_mobileDisableOnRootedOrJailBrokenDevices;}
    public GwtMobileOpenInSetting getMobileOpenIn()                             {return m_mobileOpenIn;                            }
    public List<String>           getAndroidApplications()                      {return m_androidApplications;                     }
    public List<String>           getIosApplications()                          {return m_iosApplications;                         }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAllowCacheContent(     boolean allow)     {m_allowCacheContent      = allow;    }
	public void setAllowCachePwd(         boolean allow)     {m_allowCachePwd          = allow;    }
	public void setAllowPlayWithOtherApps(boolean allow)     {m_allowPlayWithOtherApps = allow;    }
	public void setForcePinCode(          boolean force)     {m_forcePinCode           = force;    }
	public void setMobileAppsEnabled(     boolean enabled)   {m_mobileAppsEnabled      = enabled;  }
	public void setUseGlobalSettings(     boolean useGlobal) {m_useGlobalSettings      = useGlobal;}
	
    public void setMobileCutCopyEnabled(                    boolean                mobileCutCopyEnabled)                     {m_mobileCutCopyEnabled                     = mobileCutCopyEnabled;                    }
    public void setMobileAndroidScreenCaptureEnabled(       boolean                mobileAndroidScreenCaptureEnabled)        {m_mobileAndroidScreenCaptureEnabled        = mobileAndroidScreenCaptureEnabled;       }
    public void setMobileDisableOnRootedOrJailBrokenDevices(boolean                mobileDisableOnRootedOrJailBrokenDevices) {m_mobileDisableOnRootedOrJailBrokenDevices = mobileDisableOnRootedOrJailBrokenDevices;}
    public void setMobileOpenIn(                            GwtMobileOpenInSetting mobileOpenIn)                             {m_mobileOpenIn                             = mobileOpenIn;                            }
    public void setAndroidApplications(                     List<String>           androidApplications)                      {m_androidApplications                      = androidApplications;                     }
    public void setIosApplications(                         List<String>           iosApplications)                          {m_iosApplications                          = iosApplications;                         }
}
