/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.web.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.kablink.teaming.ObjectKeys;
/**
 * @author hurley
 *
 * A Clipboard object contains a list of maps of user ids, folder ids or entry ids. 
 * Since this is shared in the session, and multiple brower instances share a session, need
 * to synchronize access
 * 
 */
public class Clipboard {
	private Map clipboard = null;
   	
	//Type keys
   	public final static String BINDERS = "ss_muster_binders";
   	public final static String ENTRIES = "ss_muster_entries";
   	public final static String USERS = "ss_muster_users";
	
	public Clipboard(PortletRequest request) {
		PortletSession ps = WebHelper.getRequiredPortletSession(request);
		clipboard = (Map) ps.getAttribute(ObjectKeys.SESSION_CLIPBOARD, PortletSession.APPLICATION_SCOPE);
		if (clipboard == null) {
			clipboard = new HashMap(3);
			clipboard.put(BINDERS, new HashSet());
			clipboard.put(ENTRIES, new HashSet());
			clipboard.put(USERS, new HashSet());
			ps.setAttribute(ObjectKeys.SESSION_CLIPBOARD, clipboard, PortletSession.APPLICATION_SCOPE);
		}
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
