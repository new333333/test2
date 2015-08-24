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
package org.kablink.teaming.gwt.client.rpc.shared;

import org.kablink.teaming.gwt.client.util.BinderInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get main page info' GWT
 * RPC command.
 * 
 * @author drfoster@novell.com
 */
public class MainPageInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean		m_browserSupportsNPAPI;		//
	private boolean		m_desktopAppEnabled;		//
	private boolean		m_showDesktopAppDownloader;	//
	private boolean		m_useHomeForMyFiles;		// true -> As user's Home folder serves as their My Files repository.  false -> It doesn't.
	private boolean		m_firstLogin;				// true if it is the user's first login
	private boolean		m_isDefaultZone;			// true if we're running in the default zone.
	private boolean		m_isSuperUser;				// true if we are dealing with the super user (admin)
	private boolean		m_isTelemetryTier2Set;		// true if the built-in admin has set a telemetry tier 2 setting on the default zone.
	private BinderInfo	m_binderInfo;				//
	private String		m_userAvatarUrl;			//

	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public MainPageInfoRpcResponseData() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 *
	 * @param binderInfo
	 * @param userAvatarUrl
	 * @param browserSupportsNPAPI
	 * @param desktopAppEnabled
	 * @param showDesktopAppDownloader
	 * @param useHomeAsMyFiles
	 * @param firstLogin
	 * @param superUser
	 * @param defaultZone
	 * @param telemetryTier2Set
	 */
	public MainPageInfoRpcResponseData(BinderInfo binderInfo, String userAvatarUrl, boolean browserSupportsNPAPI, boolean desktopAppEnabled, boolean showDesktopAppDownloader, boolean useHomeAsMyFiles, boolean firstLogin, boolean superUser, boolean defaultZone, boolean telemetryTier2Set) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setBinderInfo(              binderInfo              );
		setUserAvatarUrl(           userAvatarUrl           );
		setBrowserSupportsNPAPI(    browserSupportsNPAPI    );
		setDesktopAppEnabled(       desktopAppEnabled       );
		setShowDesktopAppDownloader(showDesktopAppDownloader);
		setUseHomeAsMyFiles(        useHomeAsMyFiles        );
		setIsFirstLogin(            firstLogin              );
		setIsSuperUser(             superUser               );
		setIsDefaultZone(           defaultZone             );
		setIsTelemetryTier2Set(     telemetryTier2Set       );
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean    browserSupportsNPAPI()       {return m_browserSupportsNPAPI;    }
	public boolean    isDefaultZone()              {return m_isDefaultZone;           }
	public boolean    isDesktopAppEnabled()        {return m_desktopAppEnabled;       }
	public boolean    isFirstLogin()               {return m_firstLogin;              }
	public boolean    isShowDesktopAppDownloader() {return m_showDesktopAppDownloader;}
	public boolean    isSuperUser()                {return m_isSuperUser;             }
	public boolean    isTelemetryTier2Set()        {return m_isTelemetryTier2Set;     }
	public boolean    isUseHomeAsMyFiles()         {return m_useHomeForMyFiles;       }
	public BinderInfo getBinderInfo()              {return m_binderInfo;              }
	public String     getUserAvatarUrl()           {return m_userAvatarUrl;           }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBrowserSupportsNPAPI(    boolean    browserSupportsNPAPI)     {m_browserSupportsNPAPI     = browserSupportsNPAPI;    }
	public void setDesktopAppEnabled(       boolean    desktopAppEnabled)        {m_desktopAppEnabled        = desktopAppEnabled;       }
	public void setIsDefaultZone(           boolean    isDefaultZone)            {m_isDefaultZone            = isDefaultZone;           }
	public void setIsFirstLogin(            boolean    firstLogin)               {m_firstLogin               = firstLogin;              }
	public void setIsSuperUser(             boolean    superUser)                {m_isSuperUser              = superUser;               }
	public void setIsTelemetryTier2Set(     boolean    isTelemetryTier2Set)      {m_isTelemetryTier2Set      = isTelemetryTier2Set;     }
	public void setShowDesktopAppDownloader(boolean    showDesktopAppDownloader) {m_showDesktopAppDownloader = showDesktopAppDownloader;}
	public void setUseHomeAsMyFiles(        boolean    useHomeAsMyFiles)         {m_useHomeForMyFiles        = useHomeAsMyFiles;        }
	public void setBinderInfo(              BinderInfo binderInfo)               {m_binderInfo               = binderInfo;              }
	public void setUserAvatarUrl(           String     userAvatarUrl)            {m_userAvatarUrl            = userAvatarUrl;           }
}
