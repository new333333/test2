package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.dom4j.Document;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.util.NLT;
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
   	public final static String BINDERS = "binders";
   	public final static String ENTRIES = "entries";
   	public final static String USERS = "users";
	
	public Clipboard(PortletRequest request) {
		PortletSession ps = WebHelper.getRequiredPortletSession(request);
		clipboard = (Map) ps.getAttribute(WebKeys.CLIPBOARD, PortletSession.APPLICATION_SCOPE);
		if (clipboard == null) {
			clipboard = new HashMap();
			clipboard.put(BINDERS, new ArrayList());
			clipboard.put(ENTRIES, new ArrayList());
			clipboard.put(USERS, new ArrayList());
			ps.setAttribute(WebKeys.CLIPBOARD, clipboard, PortletSession.APPLICATION_SCOPE);
		}
	}
	
	public Map getClipboard() {
		return clipboard;
	}
	
}
