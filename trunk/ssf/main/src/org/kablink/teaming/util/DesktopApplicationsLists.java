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
package org.kablink.teaming.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.web.util.MiscUtil;

/**
 * Class for manipulating the Desktop Applications Blacklist/Whitelist
 * information stored in a blob in the SS_ZoneConfig table.
 * 
 * The information is stored in the blob as an XML stream in the
 * following format:
 *
 *		<DesktopApplicationsLists version="1 | 2 | ..." mode="...">	<!-- mode is BLACKLIST, DISABLED or WHITELIST. -->
 *			<MAC>
 *				<WHITELIST><App description="..." processName="..." /><App ...more applications here... /></WHITELIST>
 *					-or-
 *				<BLACKLIST><App description="..." processName="..." /><App ...more applications here... /></BLACKLIST>
 *			</MAC>
 *
 *			<WINDOWS>
 *				<WHITELIST><App description="..." processName="..." /><App ...more applications here... /></WHITELIST>
 *					-or-
 *				<BLACKLIST><App description="..." processName="..." /><App ...more applications here... /></BLACKLIST>
 *			</WINDOWS>
 *		</DesktopApplicationsLists>
 * 
 * @author drfoster@novell.com
 */
public class DesktopApplicationsLists {
	private static Log m_logger = LogFactory.getLog(DesktopApplicationsLists.class);
	
	private AppListMode						m_mode;				// The mode of         the desktop application list.
	private Map<AppPlatform, List<AppInfo>>	m_applicationsMap;	// The applications in the desktop application list.

	private final static int	CURRENT_VERSION						= 1;
	private final static String ATTRIBUTE_DESCRIPTION				= "description";
	private final static String	ATTRIBUTE_MODE						= "mode";
	private final static String ATTRIBUTE_PROCESS_NAME				= "processName";
	private final static String ATTRIBUTE_VERSION					= "version";
	private final static String	ELEMENT_APP							= "App";
	private final static String ELEMENT_DESKTOP_APPLICATIONS_LISTS	= "DesktopApplicationsLists";

	/**
	 * Inner class used to track application information. 
	 */
	public static class AppInfo {
		private String	m_description;	//
		private String	m_processName;	//
		
		/**
		 * Constructor method.
		 * 
		 * @param description
		 * @param processName
		 */
		public AppInfo(String description, String processName) {
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
	public enum AppListMode {
		BLACKLIST,
		DISABLED,
		WHITELIST;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isBlacklist() {return BLACKLIST.equals(this);}
		public boolean isDisabled()  {return DISABLED.equals( this);}
		public boolean isWhitelist() {return WHITELIST.equals(this);}
		
		/**
		 * Given a mode's name, returns the mode.
		 * 
		 * @param modeName
		 * 
		 * @return
		 */
		public static AppListMode getMode(String modeName) {
			AppListMode reply;
			try {
				reply = AppListMode.valueOf(modeName);
			}
			catch (Exception ex) {
				m_logger.error("AppListMod.getMode( PARSE ERROR ):  ", ex);
				reply = AppListMode.DISABLED;
			}
			return reply;
		}
	}
	
	/**
	 * Enumeration value that represents the platform of the desktop
	 * application.
	 */
	public enum AppPlatform {
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
	 */
	public DesktopApplicationsLists() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires
		// ...initialization.
		setAppListMode(AppListMode.DISABLED);

		m_applicationsMap = new HashMap<AppPlatform, List<AppInfo>>();
		for (AppPlatform platform:  AppPlatform.values()) {
			m_applicationsMap.put(platform, new ArrayList<AppInfo>());
		}
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean       isBlacklist()         {return m_mode.isBlacklist();             }
	public boolean       isDisabled()          {return m_mode.isDisabled();              }
	public boolean       isWhitelist()         {return m_mode.isWhitelist();             }
	public AppListMode   getAppListMode()      {return m_mode;                           }
	public List<AppInfo> getMacBlacklist()     {return getBlacklist(AppPlatform.MAC);    }
	public List<AppInfo> getMacDisabled()      {return getDisabled( AppPlatform.MAC);    }
	public List<AppInfo> getMacWhitelist()     {return getWhitelist(AppPlatform.MAC);    }
	public List<AppInfo> getWindowsBlacklist() {return getBlacklist(AppPlatform.WINDOWS);}
	public List<AppInfo> getWindowsDisabled()  {return getDisabled( AppPlatform.WINDOWS);}
	public List<AppInfo> getWindowsWhitelist() {return getWhitelist(AppPlatform.WINDOWS);}

	/**
	 * Get'er helper methods (Public.)
	 */
	public List<AppInfo> getBlacklist(AppPlatform platform) {return (isBlacklist() ? getApplications(platform) : new ArrayList<AppInfo>());}
	public List<AppInfo> getDisabled( AppPlatform platform) {return (isDisabled()  ? getApplications(platform) : new ArrayList<AppInfo>());}
	public List<AppInfo> getWhitelist(AppPlatform platform) {return (isWhitelist() ? getApplications(platform) : new ArrayList<AppInfo>());}
	
	/*
	 * Get'er helper methods (Private.)
	 */
	private List<AppInfo> getApplications(AppPlatform platform) {return m_applicationsMap.get(platform);                                       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAppListMode(AppListMode mode) {m_mode = mode;}

	/**
	 * Adds an application to a platform's desktop application list.
	 * 
	 * @param platform
	 * @param appInfo
	 */
	public void addApplication(AppPlatform platform, AppInfo appInfo) {
		// Scan the applications for this platform.
		List<AppInfo> appList = m_applicationsMap.get(platform);
		for (AppInfo app:  appList) {
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
	
	public void addApplication(AppPlatform platform, String description, String processName) {
		// If we don't have a platform or process name...
		if ((null == platform) || (null == processName)) {
			// ...bail.
			return;
		}
		processName = processName.trim();
		if (0 == processName.length()) {
			// ...bail.
			return;
		}

		// Always use the initial form of the method.
		addApplication(
			platform,
			new AppInfo(
				((null == description) ?
					""                 :
					description.trim()),
				processName));
	}

	/**
	 * Adds an application to a platform's desktop application list.
	 * 
	 * @param description
	 * @param platformName
	 */
	public void addMacApplication(    String description, String platformName) {addApplication(AppPlatform.MAC,     description, platformName);}
	public void addWindowsApplication(String description, String platformName) {addApplication(AppPlatform.WINDOWS, description, platformName);}
	
	/**
	 * Returns the String representation of the desktop application
	 * list that can be stored in the fsaApplicationsBlob in a
	 * ZoneConfig object.
	 * 
	 * @param formatXML
	 * 
	 * @return
	 */
	public String getFsaApplicationsBlob(boolean formatXML) {
		// Create the XML document...
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement(ELEMENT_DESKTOP_APPLICATIONS_LISTS);
		
		// ...with appropriate attributes.
		String modeName = m_mode.name();
		rootElement.addAttribute(ATTRIBUTE_MODE,    modeName);
		rootElement.addAttribute(ATTRIBUTE_VERSION, String.valueOf(CURRENT_VERSION));

		// Scan the possible platforms...
		for (AppPlatform platform:  AppPlatform.values()) {
			// ...add a platform element for each...
			String  platformName    = platform.name();
	    	Element platformElement = rootElement.addElement(platformName);

	    	// ...add a mode element to the platform...
	    	Element modeElement = platformElement.addElement(modeName);

	    	// ...scan this platform's applications...
	    	List<AppInfo> platformApps = m_applicationsMap.get(platform);
	    	for (AppInfo platformApp:  platformApps) {
	    		// ...adding an <App> element for each...
		    	Element appElement = modeElement.addElement(ELEMENT_APP);
		    	
		    	// ...with the appropriate attributes.
		    	appElement.addAttribute(ATTRIBUTE_DESCRIPTION,  platformApp.getDescription());
		    	appElement.addAttribute(ATTRIBUTE_PROCESS_NAME, platformApp.getProcessName());
	    	}
		}

		// Finally, return the string representation of the XML.
		String xml;
		if (formatXML)
		     xml = XmlUtil.asPrettyString(doc);
		else xml = doc.asXML();
		return xml;
	}
	
	public String getFsaApplicationsBlob() {
		return getFsaApplicationsBlob(false);	// false -> Don't format the XML.
	}

	/**
	 * Given a fsaApplicationsBlob from a ZoneConfig object, parses it
	 * and returns a DesktopApplicationsLists object.
	 * 
	 * @param fsaApplicationsBlob
	 * 
	 * @return
	 * 
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	public static DesktopApplicationsLists parseFsaApplicationsBlob(String fsaApplicationsBlob) {
		// Create a DesktopApplicationsLists we can fill in and return.
		DesktopApplicationsLists reply = new DesktopApplicationsLists();

		// Do we have a string from the blob?
		if (MiscUtil.hasString(fsaApplicationsBlob)) {
			// Yes!  Parse it as XML.
			Document doc;
			try {
				doc = DocumentHelper.parseText(fsaApplicationsBlob);
			}
			catch (DocumentException ex) {
				m_logger.error("parseFsaApplicationsBlob( XML Parese Error ):  ", ex);
				doc = null;
			}
			
			// Can we find the XML's root element?
			Element rootElement = ((null == doc) ? null : doc.getRootElement());
			if (null != rootElement) {
				// Yes!  Parse its version number.
				String versionNumber = rootElement.attributeValue(ATTRIBUTE_VERSION);
				int version;
				try {
					version = Integer.parseInt(versionNumber);
				}
				catch (NumberFormatException ex) {
					m_logger.error("parseFsaApplicationsBlob( Version Format Exception ):  ", ex);
					version = (-1);
				}
				
				// Process the XML based on its version.
				switch (version) {
				case 1:
					// Get the mode from the root element.
					String modeName = rootElement.attributeValue(ATTRIBUTE_MODE);
					reply.setAppListMode(AppListMode.getMode(modeName));
					
					// Scan the possible platforms.
					for (AppPlatform platform:  AppPlatform.values()) {
						// Can we find the platform element for this
						// platform?
						String platformName = platform.name();
						Element platformElement = ((Element) rootElement.selectSingleNode("./" + platformName));
						if (null != platformElement) {
							// Yes!  Can we find the mode element with
							// the platform?
							Element modeElement = ((Element) platformElement.selectSingleNode("./" + modeName));
							if (null != modeElement) {
								// Yes!  Does it contain any
								// application elements?
								List<Element> appElements = modeElement.selectNodes("./" + ELEMENT_APP);
								if (MiscUtil.hasItems(appElements)) {
									// Yes!  Scan them...
									for (Element appElement:  appElements) {
										// ...adding the application to
										// ...the appropriate
										// ...platform's applications
										// ...list.
										reply.addApplication(
											platform,
											appElement.attributeValue(ATTRIBUTE_DESCRIPTION),
											appElement.attributeValue(ATTRIBUTE_PROCESS_NAME));
									}
								}
							}
						}
					}
					break;
					
				default:
					m_logger.error("parseFsaApplicationsBlob( Unknown XML Version ):  " + version);
					break;
				}
			}
		}

		// If we get here, reply refers to the DesktopApplicationsLists
		// object for the given fsaApplicationsBlob string.  Return it.
		return reply;
	}
}
