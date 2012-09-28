/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.util.StringUtil;

/**
 * Helper methods used to track icons for files.
 * 
 * @author drfoster@novell.com
 */
public class FileIconsHelper {
	private static Map<String, FileIcons>	m_iconMap;	// Map of extensions to FileIcons initialize at first use.
	
	// The following define the extensions for which we have icons
	// defined.
	private static String[] SUPPORTED_EXTENSIONS = new String[] {
		"avi",
		"doc",
		"flv",
		"gif",
		"html",
		"jpg",
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

	/*
	 * Initializes the Map of extensions to FileIcons if it hasn't been
	 * initialized yet.
	 */
	private synchronized static void initializeIconMap() {
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
			if (ext.charAt(0) == '.') {
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

		// Certain extensions need to be massaged before we can use
		// them for lookup.
		if      (ext.equals("htm"))  ext = "html";
		else if (ext.equals("docx")) ext = "doc";
		else if (ext.equals("jpe"))  ext = "jpg";
		else if (ext.equals("jpeg")) ext = "jpg";
		else if (ext.equals("pptx")) ext = "ppt";
		else if (ext.equals("sxc"))  ext = "ods";
		else if (ext.equals("sxi"))  ext = "odp";
		else if (ext.equals("sxw"))  ext = "odt";
		else if (ext.equals("tar"))  ext = "zip";
		else if (ext.equals("xlsx")) ext = "xls";

		// Make sure the Map of extensions to FileIcons has been
		// initialized.
		initializeIconMap();

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
}
