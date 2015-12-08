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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for manipulating the Desktop and Mobile Applications Site
 * Branding files.
 * 
 * @author drfoster@novell.com
 */
public class SiteBrandingHelper {
	@SuppressWarnings("unused")
	private static Log m_logger = LogFactory.getLog(SiteBrandingHelper.class);
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private SiteBrandingHelper() {
		// Nothing to do.
	}

	/**
	 * Returns the name of the current Android Mobile Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getAndroidMobileApplicationBranding() {
//!		...this needs to be implemented...		
		return null;
	}

	/**
	 * Returns the name of the current IOS Mobile Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getIosMobileApplicationBranding() {
//!		...this needs to be implemented...		
		return null;
	}

	/**
	 * Returns the name of the current Mac Desktop Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getMacDesktopApplicationBranding() {
//!		...this needs to be implemented...		
		return null;
	}

	/**
	 * Returns the name of the current Windows Desktop Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getWindowsDesktopApplicationBranding() {
//!		...this needs to be implemented...		
		return null;
	}

	/**
	 * Returns the name of the current Windows Mobile Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getWindowsMobileApplicationBranding() {
//!		...this needs to be implemented...		
		return null;
	}

	/**
	 * Sets the Android Mobile Application Site Branding file.
	 *
	 * @param fileName
	 * @param is
	 */
	public static void setAndroidMobileApplicationBranding(String fileName, InputStream is) {
//!		...this needs to be implemented...		
	}
	
	/**
	 * Sets the IOS Mobile Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setIosMobileApplicationBranding(String fileName, InputStream is) {
//!		...this needs to be implemented...		
	}
	
	/**
	 * Sets the Mac Desktop Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setMacDesktopApplicationBranding(String fileName, InputStream is) {
//!		...this needs to be implemented...		
	}
	
	/**
	 * Sets the Windows Desktop Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setWindowsDesktopApplicationBranding(String fileName, InputStream is) {
//!		...this needs to be implemented...		
	}
	
	/**
	 * Sets the Windows Mobile Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setWindowsMobileApplicationBranding(String fileName, InputStream is) {
//!		...this needs to be implemented...		
	}
	
	/**
	 * Removes the the Android Mobile Application Site Branding file of
	 * the given name.
	 * 
	 * @return
	 */
	public static void removeAndroidMobileApplicationBranding(String fName) {
//!		...this needs to be implemented...		
	}

	/**
	 * Removes the the IOS Mobile Application Site Branding file of
	 * the given name.
	 * 
	 * @return
	 */
	public static void removeIosMobileApplicationBranding(String fName) {
//!		...this needs to be implemented...		
	}

	/**
	 * Removes the the Mac Desktop Application Site Branding file of
	 * the given name.
	 * 
	 * @return
	 */
	public static void removeMacDesktopApplicationBranding(String fName) {
//!		...this needs to be implemented...		
	}

	/**
	 * Removes the the Windows Desktop Application Site Branding file of
	 * the given name.
	 * 
	 * @return
	 */
	public static void removeWindowsDesktopApplicationBranding(String fName) {
//!		...this needs to be implemented...		
	}

	/**
	 * Removes the the Windows Mobile Application Site Branding file of
	 * the given name.
	 * 
	 * @return
	 */
	public static void removeWindowsMobileApplicationBranding(String fName) {
//!		...this needs to be implemented...		
	}
}
