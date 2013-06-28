/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.ObjectKeys;

/**
 * A Clipboard object contains a list of maps of user IDs, folder IDs
 * or entry IDs.
 *  
 * Since this is shared in the session, and multiple browser instances
 * share a session, need to synchronize access.
 * 
 * @author phurley@novell.com
 */
@SuppressWarnings("unchecked")
public class Clipboard {
	private Map clipboard = null;
   	
	//Type keys
   	public final static String BINDERS = "ss_muster_binders";
   	public final static String ENTRIES = "ss_muster_entries";
   	public final static String USERS = "ss_muster_users";
	
	private Clipboard(PortletRequest pRequest, HttpServletRequest hRequest) {
		HttpSession hs;
		PortletSession ps;
		if (null != pRequest) {
			hs = null;
			ps = WebHelper.getRequiredPortletSession(pRequest);
			clipboard = ((Map) ps.getAttribute(ObjectKeys.SESSION_CLIPBOARD, PortletSession.APPLICATION_SCOPE));
		}
		else {
			ps = null;
			hs = WebHelper.getRequiredSession(hRequest);
			clipboard = ((Map) hs.getAttribute(ObjectKeys.SESSION_CLIPBOARD));
		}
		
		if (clipboard == null) {
			clipboard = new HashMap(3);
			clipboard.put(BINDERS, new HashSet());
			clipboard.put(ENTRIES, new HashSet());
			clipboard.put(USERS, new HashSet());
			if (null != ps)
				 ps.setAttribute(ObjectKeys.SESSION_CLIPBOARD, clipboard, PortletSession.APPLICATION_SCOPE);
			else hs.setAttribute(ObjectKeys.SESSION_CLIPBOARD, clipboard                                  );
		}
	}
	
	public Clipboard(PortletRequest pRequest) {
		this(pRequest, null);
	}
	
	public Clipboard(HttpServletRequest hRequest) {
		this(null, hRequest);
	}
	
	public synchronized void add(String musterClass, Collection ids) {
		Set idList = (Set) clipboard.get(musterClass);
		if (idList == null) set(musterClass, ids);
		else idList.addAll(ids);
	}
	public synchronized void remove(String musterClass, Collection ids) {
		Set idList = (Set) clipboard.get(musterClass);
		if (idList == null) return;
		else idList.removeAll(ids);
	}
	//return copy
	public synchronized Set get(String musterClass) {
		Set result = (Set)clipboard.get(musterClass);
		if (result != null) return new HashSet(result);
		return new HashSet();
	}
	public synchronized void set(String musterClass, Collection ids) {
		Set result = new HashSet(ids);
		clipboard.put(musterClass, result);
	}
	public synchronized void clear(String musterClass) {
		Set result = (Set)clipboard.get(musterClass);
		result.clear();		
	}
}
