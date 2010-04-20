/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.web.util;

import org.kablink.teaming.util.SPropsUtil;

/**
 * Class that manages the defaults for access to the GWT UI.
 * 
 * @author drfoster@novell.com
 */
public class GwtUIDefaults {
	public static String GWT_UI_ENABLED_FLAG	= "use-durango-ui";
	public static String GWT_UI_DEFAULT_FLAG	= (GWT_UI_ENABLED_FLAG + "-default");
	public static String GWT_UI_EXCLUSIVE_FLAG	= (GWT_UI_ENABLED_FLAG + "-exclusive");
	
	private boolean m_default;		// true -> The GWT UI is the default UI at login.             false -> otherwise.
	private boolean m_enabled;		// true -> The GWT UI is the enabled.                         false -> otherwise.
	private boolean m_exclusive;	// true -> There should be no UI exposed to exit the GWT UI.  false -> otherwise.

	/**
	 * Constructor method.
	 */
	public GwtUIDefaults() {
		m_enabled = isSettingOn(GWT_UI_ENABLED_FLAG, "true");
		if (m_enabled) {
			m_default   = isSettingOn(GWT_UI_DEFAULT_FLAG,   "false");
			m_exclusive = isSettingOn(GWT_UI_EXCLUSIVE_FLAG, "false");
		}
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isGwtUIDefault()   {return m_default;}
	public boolean isGwtUIEnabled()   {return m_enabled;}
	public boolean isGwtUIExclusive() {return m_exclusive;}
	
	/*
	 * Returns true of a setting is turned in in the ssf*.properties
	 * files and false otherwise.
	 */
	private boolean isSettingOn(String setting, String defValue) {
		String value = SPropsUtil.getString(setting, defValue);
		return ((null != value) && ("1".equals(value) || "true".equals(value.toLowerCase())));
	}
}
