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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class is used to hold the Desktop Applications
 * Whitelist/Blacklist configuration data.
 * 
 * @author drfoster@novell.com
 */
public class GwtDesktopApplicationsLists implements IsSerializable, VibeRpcResponseData {
	private GwtAppListMode							m_mode;					// The mode of                   the desktop application list.
	private Map<GwtAppPlatform, List<GwtAppInfo>>	m_blackApplicationsMap;	// The blacklist applications in the desktop application list.
	private Map<GwtAppPlatform, List<GwtAppInfo>>	m_whiteApplicationsMap;	// The whitelist applications in the desktop application list.
	
	/**
	 * Inner class used to track application information. 
	 */
	public static class GwtAppInfo implements IsSerializable {
		private String	m_description;	//
		private String	m_processName;	//
		
		/*
		 * Constructor method.
		 * 
		 * Zero parameter constructor required for GWT serialization.
		 */
		private GwtAppInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param description
		 * @param processName
		 */
		public GwtAppInfo(String description, String processName) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setDescription(description);
			setProcessName(processName);
		}
	
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getDescription() {return m_description;}
		public String getProcessName() {return m_processName;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param description
		 */
		public void setDescription(String description) {m_description = description;}
		public void setProcessName(String processName) {m_processName = processName;}
	}
	
	/**
	 * Enumeration value that represents the mode of the desktop
	 * application list.
	 */
	public enum GwtAppListMode implements IsSerializable {
		BLACKLIST,
		DISABLED,
		WHITELIST,
		BOTH;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isBlacklist() {return BLACKLIST.equals(this);}
		public boolean isBoth()      {return BOTH.equals(     this);}
		public boolean isDisabled()  {return DISABLED.equals( this);}
		public boolean isWhitelist() {return WHITELIST.equals(this);}
		
		/**
		 * Given a mode's name, returns the mode.
		 * 
		 * @param modeName
		 * 
		 * @return
		 */
		public static GwtAppListMode getMode(String modeName) {
			GwtAppListMode reply;
			try {
				reply = GwtAppListMode.valueOf(modeName);
			}
			catch (Exception ex) {
				reply = GwtAppListMode.DISABLED;
			}
			return reply;
		}
	}
	
	/**
	 * Enumeration value that represents the platform of the desktop
	 * application.
	 */
	public enum GwtAppPlatform implements IsSerializable {
		MAC,
		WINDOWS;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isMac()     {return MAC.equals(    this);}
		public boolean isWindows() {return WINDOWS.equals(this);}
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public GwtDesktopApplicationsLists() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires
		// ...initialization.
		setAppListMode(GwtAppListMode.DISABLED);

		m_blackApplicationsMap = new HashMap<GwtAppPlatform, List<GwtAppInfo>>();
		for (GwtAppPlatform platform:  GwtAppPlatform.values()) {
			m_blackApplicationsMap.put(platform, new ArrayList<GwtAppInfo>());
		}
		
		m_whiteApplicationsMap = new HashMap<GwtAppPlatform, List<GwtAppInfo>>();
		for (GwtAppPlatform platform:  GwtAppPlatform.values()) {
			m_whiteApplicationsMap.put(platform, new ArrayList<GwtAppInfo>());
		}
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean          isBlacklist()                  {return m_mode.isBlacklist();                        }
	public boolean          isBoth()                       {return m_mode.isBoth();                             }
	public boolean          isDisabled()                   {return m_mode.isDisabled();                         }
	public boolean          isWhitelist()                  {return m_mode.isWhitelist();                        }
	public GwtAppListMode   getAppListMode()               {return m_mode;                                      }
	public List<GwtAppInfo> getMacBlacklist()              {return getBlacklist(        GwtAppPlatform.MAC);    }
	public List<GwtAppInfo> getMacDisabledBlacklist()      {return getDisabledBlacklist(GwtAppPlatform.MAC);    }
	public List<GwtAppInfo> getMacDisabledWhitelist()      {return getDisabledWhitelist(GwtAppPlatform.MAC);    }
	public List<GwtAppInfo> getMacWhitelist()              {return getWhitelist(        GwtAppPlatform.MAC);    }
	public List<GwtAppInfo> getWindowsBlacklist()          {return getBlacklist(        GwtAppPlatform.WINDOWS);}
	public List<GwtAppInfo> getWindowsDisabledBlacklist()  {return getDisabledBlacklist(GwtAppPlatform.WINDOWS);}
	public List<GwtAppInfo> getWindowsDisabledWhitelist()  {return getDisabledWhitelist(GwtAppPlatform.WINDOWS);}
	public List<GwtAppInfo> getWindowsWhitelist()          {return getWhitelist(        GwtAppPlatform.WINDOWS);}

	/**
	 * Get'er helper methods
	 * 
	 * @return
	 */
	public List<GwtAppInfo> getBlacklist(        GwtAppPlatform platform) {return ((isBlacklist() || isBoth()) ? getBlackApplications(platform) : new ArrayList<GwtAppInfo>());}
	public List<GwtAppInfo> getDisabledBlacklist(GwtAppPlatform platform) {return ( isDisabled()               ? getBlackApplications(platform) : new ArrayList<GwtAppInfo>());}
	public List<GwtAppInfo> getDisabledWhitelist(GwtAppPlatform platform) {return ( isDisabled()               ? getWhiteApplications(platform) : new ArrayList<GwtAppInfo>());}
	public List<GwtAppInfo> getWhitelist(        GwtAppPlatform platform) {return ((isWhitelist() || isBoth()) ? getWhiteApplications(platform) : new ArrayList<GwtAppInfo>());}
	
	public List<GwtAppInfo> getBlackApplications(GwtAppPlatform platform) {return m_blackApplicationsMap.get(platform);                                          }
	public List<GwtAppInfo> getWhiteApplications(GwtAppPlatform platform) {return m_whiteApplicationsMap.get(platform);                                          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAppListMode(GwtAppListMode mode) {m_mode = mode;}

	/*
	 * Adds an application to a platform's desktop application list.
	 */
	private void addApplicationImpl(List<GwtAppInfo> appList, GwtAppInfo appInfo) {
		// Scan the applications for this platform.
		for (GwtAppInfo app:  appList) {
			// Is the application to be added for the same process?
			if (app.getProcessName().equalsIgnoreCase(appInfo.getProcessName())) {
				// Yes!  Skip it.  We don't want duplicates in the
				// list.
				return;
			}
		}
		
		// This application is unique.  Add it to the list.
		appList.add(appInfo);
	}
	
	private void addApplicationImpl(List<GwtAppInfo> appList, String description, String processName) {
		// If we don't have a process name...
		if (null == processName) {
			// ...bail.
			return;
		}
		processName = processName.trim();
		if (0 == processName.length()) {
			// ...bail.
			return;
		}

		// Always use the initial form of the method.
		addApplicationImpl(
			appList,
			new GwtAppInfo(
				((null == description) ?
					""                 :
					description.trim()),
				processName));
	}

	/**
	 * Adds an application to a platform's desktop application
	 * blacklist.
	 * 
	 * @param platform
	 * @param appInfo
	 */
	public void addBlackApplication(GwtAppPlatform platform, GwtAppInfo appInfo) {
		addApplicationImpl(m_blackApplicationsMap.get(platform), appInfo);
	}
	
	public void addBlackApplication(GwtAppPlatform platform, String description, String processName) {
		addApplicationImpl(m_blackApplicationsMap.get(platform), description, processName);
	}

	/**
	 * Adds an application to a platform's desktop application
	 * whitelist.
	 * 
	 * @param platform
	 * @param appInfo
	 */
	public void addWhiteApplication(GwtAppPlatform platform, GwtAppInfo appInfo) {
		addApplicationImpl(m_whiteApplicationsMap.get(platform), appInfo);
	}
	
	public void addWhiteApplication(GwtAppPlatform platform, String description, String processName) {
		addApplicationImpl(m_whiteApplicationsMap.get(platform), description, processName);
	}

	/**
	 * Adds an application to a platform's desktop application list.
	 * 
	 * @param description
	 * @param platformName
	 */
	public void addMacBlackApplication(    String description, String platformName) {addBlackApplication(GwtAppPlatform.MAC,     description, platformName);}
	public void addMacWhiteApplication(    String description, String platformName) {addWhiteApplication(GwtAppPlatform.MAC,     description, platformName);}
	public void addWindowsBlackApplication(String description, String platformName) {addBlackApplication(GwtAppPlatform.WINDOWS, description, platformName);}
	public void addWindowsWhiteApplication(String description, String platformName) {addWhiteApplication(GwtAppPlatform.WINDOWS, description, platformName);}
}
