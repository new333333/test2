package com.sitescape.team.web.util;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

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
	
	public Map getClipboard() {
		return clipboard;
	}
	
}
