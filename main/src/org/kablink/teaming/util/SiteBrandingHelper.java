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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.FileUtil;

/**
 * Class for manipulating the Desktop and Mobile Applications Site
 * Branding files.
 * 
 * @author drfoster@novell.com
 */
public class SiteBrandingHelper {
	private static Log m_logger = LogFactory.getLog(SiteBrandingHelper.class);

	// Strings that supply an application's type.
	private final static String	DESKTOP	= "desktop";
	private final static String	MOBILE	= "mobile";
	
	// Strings that supply an application's platform.
	public final static String ANDROID	= "android";
	public final static String IOS		= "ios";
	public final static String MAC		= "mac";
	public final static String WINDOWS	= "windows";
	
	// String that supplies the base path name for application site
	// branding.
	private final static String SITE_BRANDING_BASE	= "siteBranding";
	
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
		return getApplicationBrandingImpl(getAppNode(ANDROID, MOBILE));
	}

	public static File getAndroidMobileApplicationBrandingFile() {
		return getApplicationBrandingFile(getAppNode(ANDROID, MOBILE));
	}

	/*
	 * Returns the directory File object to use for the appNode. 
	 */
	private static File getAppDir(String appNode, boolean addTrailingSeparator) {
		File appDir = new File(getSiteBrandingPath(appNode, addTrailingSeparator));
		int tries = 0;
		while (appDir.exists() && (!(appDir.isDirectory()))) {
			appDir = new File(getSiteBrandingPath(appNode + "_" + ++tries)); 
		}
		if (!(appDir.exists())) {
			appDir.mkdirs();
		}
		return appDir;
	}
	
	private static File getAppDir(String appNode) {
		// Always use the initial form of the method.
		return getAppDir(appNode, false);	// false -> Don't include a trailing path separator. 
	}
	
	/*
	 * Returns the name of the current Application Site Branding file
	 * from the specified application node.
	 */
	private static String getApplicationBrandingImpl(String appNode) {
		File   appDir = getAppDir(appNode);
		File[] files  = appDir.listFiles();
		String reply = null;
		if ((null != files) && (0 < files.length)) {
			for (File file:  files) {
				if (file.isFile()) {
					reply = file.getName();
					break;
				}
			}
		}
		return reply;
	}

	/*
	 * Returns the name of the current Application Site Branding file
	 * from the specified application node.
	 */
	private static File getApplicationBrandingFile(String appNode) {
		File   appDir = getAppDir(appNode);
		File[] files  = appDir.listFiles();
		File reply = null;
		if ((null != files) && (0 < files.length)) {
			for (File file:  files) {
				if (file.isFile()) {
					reply = file;
					break;
				}
			}
		}
		return reply;
	}

	public static File getMobileApplicationBrandingFile(String platform) {
		return getApplicationBrandingFile(getAppNode(platform, MOBILE));
	}

	public static File getDesktopApplicationBrandingFile(String platform) {
		return getApplicationBrandingFile(getAppNode(platform, DESKTOP));
	}

	/*
	 * Returns the path node that corresponds to a platform
	 * and application type.
	 */
	private static String getAppNode(String platform, String type) {
		return (type + File.separator + platform);
	}
	
	/**
	 * Returns the name of the current IOS Mobile Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getIosMobileApplicationBranding() {
		return getApplicationBrandingImpl(getAppNode(IOS, MOBILE));
	}

	public static File getIosMobileApplicationBrandingFile() {
		return getApplicationBrandingFile(getAppNode(IOS, MOBILE));
	}

	/**
	 * Returns the name of the current Mac Desktop Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getMacDesktopApplicationBranding() {
		return getApplicationBrandingImpl(getAppNode(MAC, DESKTOP));
	}

	public static File getMacMobileApplicationBrandingFile() {
		return getApplicationBrandingFile(getAppNode(MAC, DESKTOP));
	}

	/*
	 * Returns the path to where the desktop and mobile application
	 * site branding files are stored.
	 * 
	 * Example:
	 *    data/siteBranding/kablink/mac/desktop
	 */
	private static String getSiteBrandingPath(String appNode, boolean addTrailingSeparator) {
		String reply = (SPropsUtil.getDirPath("data.root.dir") + SITE_BRANDING_BASE + File.separator + RequestContextHolder.getRequestContext().getZoneName() + File.separator + appNode + File.separator);
		if (addTrailingSeparator) {
			reply += File.separator;
		}
		return reply;
	}
	
	private static String getSiteBrandingPath(String appNode) {
		// Always use the initial form of the method.
		return getSiteBrandingPath(appNode, false);	// false -> Don't include a trailing path separator.
	}

	/**
	 * Returns the name of the current Windows Desktop Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getWindowsDesktopApplicationBranding() {
		return getApplicationBrandingImpl(getAppNode(WINDOWS, DESKTOP));
	}

	public static File getWindowsDesktopApplicationBrandingFile() {
		return getApplicationBrandingFile(getAppNode(WINDOWS, DESKTOP));
	}

	/**
	 * Returns the name of the current Windows Mobile Application Site
	 * Branding file.
	 * 
	 * @return
	 */
	public static String getWindowsMobileApplicationBranding() {
		return getApplicationBrandingImpl(getAppNode(WINDOWS, MOBILE));
	}

	public static File getWindowsMobileApplicationBrandingFile() {
		return getApplicationBrandingFile(getAppNode(WINDOWS, MOBILE));
	}

	/**
	 * Sets the Android Mobile Application Site Branding file.
	 *
	 * @param fileName
	 * @param is
	 */
	public static void setAndroidMobileApplicationBranding(String fileName, InputStream is) {
		setApplicationBrandingImpl(getAppNode(ANDROID, MOBILE), fileName, is);
	}
	
	/*
	 * Sets the Application Site Branding file using the given
	 * InputStream into the specified application node using the given
	 * file name.
	 */
	private static void setApplicationBrandingImpl(String appNode, String fileName, InputStream is) {
		// Do we have the name of the file to set?
		if (MiscUtil.hasString(fileName)) {
			// Yes!  Does the target directory contain any File's?
			File   appDir = getAppDir(appNode);
			File[] files  = appDir.listFiles();
			if ((null != files) && (0 < files.length)) {
				// Yes!  Scan them?
				for (File file:  files) {
					// Is this a file (vs. a directory)?
					if (file.isFile()) {
						// Yes!  Delete it.
						file.delete();
					}
				}
			}
			
			// Create the new file...
			File fo = new File(appDir, fileName);
			FileOutputStream fos = null;
			try {
				// ...and copy its contents from the InputStream.
				fos = new FileOutputStream(fo, false);
				FileUtil.copy(is, fos);
			}
			catch (IOException e) {
				m_logger.error("setApplicationBrandingImpl( EXCEPTION ):  Could not write '" + appNode + "' site branding file '" + fileName + "'", e);
			}
			finally {
				// Ensure the output stream we write the file to has been
				// closed.
				if (null != fos) {
					try                 {fos.close(); }
					catch (Exception e) {/* Ignore. */}
					fos = null;
				}
			}
		}
	}

	/**
	 * Sets the IOS Mobile Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setIosMobileApplicationBranding(String fileName, InputStream is) {
		setApplicationBrandingImpl(getAppNode(IOS, MOBILE), fileName, is);
	}
	
	/**
	 * Sets the Mac Desktop Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setMacDesktopApplicationBranding(String fileName, InputStream is) {
		setApplicationBrandingImpl(getAppNode(MAC, DESKTOP), fileName, is);
	}
	
	/**
	 * Sets the Windows Desktop Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setWindowsDesktopApplicationBranding(String fileName, InputStream is) {
		setApplicationBrandingImpl(getAppNode(WINDOWS, DESKTOP), fileName, is);
	}
	
	/**
	 * Sets the Windows Mobile Application Site Branding file.
	 * 
	 * @param fileName
	 * @param is
	 */
	public static void setWindowsMobileApplicationBranding(String fileName, InputStream is) {
		setApplicationBrandingImpl(getAppNode(WINDOWS, MOBILE), fileName, is);
	}
	
	/**
	 * Removes the Android Mobile Application Site Branding file of the
	 * given name.
	 * 
	 * @return
	 */
	public static void removeAndroidMobileApplicationBranding(String fileName) {
		removeApplicationBrandingImpl(getAppNode(ANDROID, MOBILE), fileName);
	}
	
	/*
	 * Removes the Application Site Branding file from the specified
	 * application node with the given name.
	 */
	private static void removeApplicationBrandingImpl(String appNode, String fileName) {
		if (MiscUtil.hasString(fileName)) {
			File   appDir = getAppDir(appNode);
			File[] files  = appDir.listFiles();
			if ((null != files) && (0 < files.length)) {
				for (File file:  files) {
					if (file.isFile() && file.getName().equals(fileName)) {
						file.delete();
						break;
					}
				}
			}
		}
	}

	/**
	 * Removes the IOS Mobile Application Site Branding file of the
	 * given name.
	 * 
	 * @return
	 */
	public static void removeIosMobileApplicationBranding(String fileName) {
		removeApplicationBrandingImpl(getAppNode(IOS, MOBILE), fileName);
	}

	/**
	 * Removes the Mac Desktop Application Site Branding file of the
	 * given name.
	 * 
	 * @return
	 */
	public static void removeMacDesktopApplicationBranding(String fileName) {
		removeApplicationBrandingImpl(getAppNode(MAC, DESKTOP), fileName);
	}

	/**
	 * Removes the Windows Desktop Application Site Branding file of
	 * the given name.
	 * 
	 * @return
	 */
	public static void removeWindowsDesktopApplicationBranding(String fileName) {
		removeApplicationBrandingImpl(getAppNode(WINDOWS, DESKTOP), fileName);
	}

	/**
	 * Removes the Windows Mobile Application Site Branding file of the
	 * given name.
	 * 
	 * @return
	 */
	public static void removeWindowsMobileApplicationBranding(String fileName) {
		removeApplicationBrandingImpl(getAppNode(WINDOWS, MOBILE), fileName);
	}

	/**
	 * Is mobile branding supported
	 *
	 */
	public static boolean isMobileBrandingSupported() {
		return LicenseChecker.showFilrFeatures() && SPropsUtil.getBoolean("show.filr.mobile.site.branding", false);
	}

	/**
	 * Is desktop branding supported
	 *
	 */
	public static boolean isDesktopBrandingSupported() {
		return LicenseChecker.showFilrFeatures() && SPropsUtil.getBoolean("show.filr.desktop.site.branding", false);
	}
}
