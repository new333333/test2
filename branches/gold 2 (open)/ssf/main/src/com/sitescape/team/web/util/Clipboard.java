/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import com.sitescape.team.web.WebKeys;
/**
 * @author hurley
 *
 * A Clipboard object contains a list of maps of user ids, folder ids or entry ids. 
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
		clipboard = (Map) ps.getAttribute(WebKeys.CLIPBOARD, PortletSession.APPLICATION_SCOPE);
		if (clipboard == null) {
			clipboard = new HashMap(3);
			clipboard.put(BINDERS, new HashSet());
			clipboard.put(ENTRIES, new HashSet());
			clipboard.put(USERS, new HashSet());
			ps.setAttribute(WebKeys.CLIPBOARD, clipboard, PortletSession.APPLICATION_SCOPE);
		}
	}
	
	public void add(String musterClass, List ids) {
		if (clipboard.containsKey(musterClass)) {
			Set idList = (Set) clipboard.get(musterClass);
			Iterator it = ids.iterator();
			while (it.hasNext()) {
				Long id = (Long)it.next();
				if (!idList.contains(id)) idList.add(id);
			}
		}
	}
	
	public Map getClipboard() {
		return clipboard;
	}
	
}
