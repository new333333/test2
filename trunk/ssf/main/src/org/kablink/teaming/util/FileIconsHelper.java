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

import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.StringUtil;

/**
 * Helper methods used to track icons for files.
 * 
 * @author drfoster@novell.com
 */
public class FileIconsHelper {
	private static Map<String, FileIcons>	m_iconMap;		// Map of extensions to FileIcons initialize at first use.
	private static Map<String, String>      m_transformMap;	// Map of extensions to extensions where one extension is transformed to another for purposes of icon generation.
	
	// The following define the extensions for which we have icons
	// defined.
	private static String[] SUPPORTED_EXTENSIONS = new String[] {
		"avi",
		"bmp",
		"doc",
		"flv",
		"gif",
		"html",
		"jpg",
		"mpg",
		"mov",
		"odp",
		"ods",
		"odt",
		"one",
		"pdf",
		"ppt",
		"txt",
		"vsd",
		"wav",
		"wpd",
		"wmv",
		"xls",
		"zip",
	};

	// The following are used to map a supported extension to the
	// appropriate icon name. 
	private static String PARAM           = "XXXXX";
	private static String SMALL_TEMPLATE  = "filr_" + PARAM + "_file.png";
	private static String MEDIUM_TEMPLATE = "filr_" + PARAM + "_file_36.png";
	private static String LARGE_TEMPLATE  = "filr_" + PARAM + "_file_48.png";

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private FileIconsHelper() {
		// Nothing to do.
	}

	/**
	 * Returns the name of the icon for a file based on its extension.
	 * 
	 * @param ext
	 * @param is
	 * 
	 * @return
	 */
	public static String getFileIcon(String ext, IconSize is) {
		// Do we have an extension? 
		int extLength;
		if (null != ext) {
			// Yes!  Trim it, convert it to lower case and strip off
			// any leading '.'.
			ext = ext.trim().toLowerCase();
			if ((0 < ext.length()) && (ext.charAt(0) == '.')) {
				ext = ext.substring(1);
			}
			extLength = ext.length();
		}
		
		else {
			// No, we don't have an extension!
			extLength = 0;
		}
		
		if (0 == extLength) {
			// No, we don't have an extension!  Return null.
			return null;
		}

		// Make sure the Map of extensions to FileIcons has been
		// initialized.
		initializeIconMaps();

		// Certain extensions need to be transformed before we can use
		// them for lookup.  Does this one need to be?
		String xformExt = m_transformMap.get(ext);
		if (MiscUtil.hasString(xformExt)) {
			// Yes!  Use the transformed name.
			ext = xformExt;
		}

		// Do we have a FileIcons object for this extension?
		FileIcons fi = m_iconMap.get(ext);
		if (null == fi) {
			// No!  Return null.
			return null;
		}
		
		// Does that FileIcons have an icon saved in the requested
		// size?
		String fileIcon = fi.getFileIcon(is);
		if (null == fileIcon) {
			// No!  Return null.
			return null;
		}

		// If we get here, fileIcon refers to the icon for the given
		// extension.  Return it, prefaced with the relative path to
		// where the file icons are stored.
		return "icons/files/" + fileIcon;
	}
	
	public static String getFileIcon(String ext) {
		// Always use the initial form of the method.
		return getFileIcon(ext, IconSize.SMALL);
	}
	
	/**
	 * Returns the name of the icon for a file based on its filename.
	 * 
	 * @param fName
	 * @param is
	 * 
	 * @return
	 */
	public static String getFileIconFromFileName(String fName, IconSize is) {
		// Do we have a filename?
		int fNameLength;
		if (null != fName) {
			// Yes!  Trim it and convert it to lower case.
			fName = fName.trim().toLowerCase();
			fNameLength = fName.length();
		}
		
		else {
			fNameLength = 0;
		}
		
		if (0 == fNameLength) {
			// No, we don't have a filename!  Return null.
			return null;
		}

		// Does that filename have an extension?
		int pPos = fName.lastIndexOf('.');
		if (0 >= pPos) {
			// No!  Return null.
			return null;
		}

		// Return the icon based on the file's extension.
		return getFileIcon(fName.substring(pPos + 1), is);
	}
	
	public static String getFileIconFromFileName(String fName) {
		// Always use the initial form of the method.
		return getFileIconFromFileName(fName, IconSize.SMALL);
	}
	
	/*
	 * Initializes the Map's used to map extensions to FileIcons if
	 * they haven't been initialized yet.
	 */
	private synchronized static void initializeIconMaps() {
		// If we've already initialized the map...
		if (null != m_iconMap) {
			// ...bail.
			return;
		}

		// Allocate a new map...
		m_iconMap = new HashMap<String, FileIcons>();
		
		// ...scan the supported extensions..
		for (String extension:  SUPPORTED_EXTENSIONS) {
			// ...storing a FileIcons object for each in the map.
			FileIcons fi = new FileIcons();
			m_iconMap.put(extension, fi);

			// Scan the define icon sizes...
			for (IconSize is:  IconSize.values()) {
				// ...generating the appropriate icon name for each.
				String template;
				switch (is) {
				case SMALL:   template = SMALL_TEMPLATE;  break;
				case MEDIUM:  template = MEDIUM_TEMPLATE; break;
				case LARGE:   template = LARGE_TEMPLATE;  break;
				default:                                  continue;
				}
				String fileIcon = StringUtil.replace(template, PARAM, extension);
				fi.setFileIcon(fileIcon, is);
			}
		}

		// Initialize the Map of extensions that are transformed from
		// one name to another for selecting an icon file.
		m_transformMap = new HashMap<String, String>();
		m_transformMap.put("aif",  "wav" );
		m_transformMap.put("docx", "doc" );
		m_transformMap.put("docm", "doc" );
		m_transformMap.put("htm",  "html");
		m_transformMap.put("jpe",  "jpg" );
		m_transformMap.put("jpeg", "jpg" );
		m_transformMap.put("mid",  "wav" );
		m_transformMap.put("mpa",  "wav" );
		m_transformMap.put("mp3",  "wav" );
		m_transformMap.put("mpeg", "mpg" );
		m_transformMap.put("mp4",  "mpg" );
		m_transformMap.put("m4a",  "wav" );
		m_transformMap.put("png",  "gif" );
		m_transformMap.put("pptx", "ppt" );
		m_transformMap.put("pptm", "ppt" );
		m_transformMap.put("ra",   "wav" );
		m_transformMap.put("sxc",  "ods" );
		m_transformMap.put("sxi",  "odp" );
		m_transformMap.put("sxw",  "odt" );
		m_transformMap.put("tar",  "zip" );
		m_transformMap.put("wma",  "wav" );
		m_transformMap.put("xlsx", "xls" );
		m_transformMap.put("xlsm", "xls" );
	}
}
