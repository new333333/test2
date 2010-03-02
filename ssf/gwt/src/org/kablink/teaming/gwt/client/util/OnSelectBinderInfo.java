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
package org.kablink.teaming.gwt.client.util;


/**
 * Class used to communicate about the selected binder in calls between
 * the WorkspaceTreeControl on its OnSelectHandler's.
 * 
 * @author drfoster@novell.com
 */
public class OnSelectBinderInfo {
	private Long m_binderId;
	private String m_binderUrl = "";

	/**
	 * Constructor method.  (1 of 3)
	 * 
	 * @param ti
	 */
	public OnSelectBinderInfo(TreeInfo ti) {
		this(ti.getBinderId(), ti.getBinderPermalink());
	}

	/**
	 * Constructor method.  (2 of 3)
	 * 
	 * @param binderId
	 * @param binderUrl
	 */
	public OnSelectBinderInfo(String binderId, String binderUrl) {
		this(Long.parseLong(binderId), binderUrl);
	}
	
	/**
	 * Constructor method.  (3 of 3)
	 * 
	 * @param binderId
	 * @param binderUrl
	 */
	public OnSelectBinderInfo(Long binderId, String binderUrl) {
		m_binderId = binderId;
		m_binderUrl = binderUrl;
	}
	
	/**
	 * Returns the Binder ID from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public Long getBinderId() {
		return m_binderId;
	}
	
	/**
	 * Returns the Binder URL from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public String getBinderUrl() {
		return m_binderUrl;
	}
}
