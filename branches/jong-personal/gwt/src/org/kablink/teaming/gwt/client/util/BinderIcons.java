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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to track multiple icons for a binder.
 * 
 * @author drfoster@novell.com
 */
public class BinderIcons implements IsSerializable {
	private String[]	m_binderIcons = new String[BinderIconSize.UNDEFINED.ordinal()];
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	public BinderIcons() {
		// Initialize the suer class.
		super();
	}
	
	/**
	 * Clears the binder icons being tracked in this BinderIcons.
	 */
	public void clearBinderIcons() {
		for (int i = 0; i < m_binderIcons.length; i += 1) {
			m_binderIcons[i] = null;
		}
	}
	
	/**
	 * Returns the name of the icons for the Binder corresponding to
	 * this TreeInfo.
	 *
	 * @param iconSize
	 * 
	 * @return
	 */
	public String getBinderIcon(BinderIconSize iconSize) {
		String reply = m_binderIcons[iconSize.ordinal()];
		if ((null == reply) && (BinderIconSize.SMALL != iconSize)) {
			if (BinderIconSize.LARGE == iconSize) {
				reply = m_binderIcons[BinderIconSize.MEDIUM.ordinal()];
			}
			if (null == reply) {
				reply = m_binderIcons[BinderIconSize.SMALL.ordinal()];
			}
		}
		return reply; 
	}
	
	/**
	 * Stores the names of the icons for the Binder.
	 * 
	 * @param binderIcon
	 */
	public void setBinderIcon(String binderIcon, BinderIconSize size) {
		m_binderIcons[size.ordinal()] = binderIcon;
	}
}
