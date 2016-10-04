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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Cookies;

/**
 * Utility methods for managing cookies for the desktop application
 * download control.
 * 
 * @author drfoster@novell.com
 */
public class DesktopAppDownloadControlCookies {
	/**
	 * Enumeration used to manage the cookies stored for the desktop
	 * application download control.
	 */
	public enum Cookie {
		HINT_VISIBLE,
		
		UNDEFINED;
		
		/*
		 * Converts the ordinal value of a Cookie to its enumeration
		 * equivalent.
		 */
		@SuppressWarnings("unused")
		private static Cookie getEnum(int cookieOrdinal) {
			Cookie cmd;
			try {
				cmd = Cookie.values()[cookieOrdinal];
			}
			catch (ArrayIndexOutOfBoundsException e) {
				cmd = Cookie.UNDEFINED;
			}
			return cmd;
		}
		
		/*
		 * Returns the name to use for the cookie for the given entity.
		 */
		private String getCookieName() {
			return ("desktopApp:" + name());
		}
	}
	
	/*
	 * Constructor method. 
	 */
	private DesktopAppDownloadControlCookies() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Returns the boolean value for a cookie.
	 * 
	 * @param cookie
	 * @param defaultValue
	 * 
	 * @return
	 */
	public static boolean getBooleanCookieValue(Cookie cookie, boolean defaultValue) {
		if (!(isCookiesEnabled())) {
			return defaultValue;
		}
		
		String cookieName  = cookie.getCookieName();
		String cookieValue = Cookies.getCookie(cookieName);
		if (!(GwtClientHelper.hasString(cookieValue))) {
			return defaultValue;
		}
		
		return Boolean.valueOf(cookieValue);
	}

	/**
	 * Returns true if cookies are currently enabled and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isCookiesEnabled() {
		return Cookies.isCookieEnabled();
	}

	/**
	 * Stores a boolean value for a cookie.
	 * 
	 * @param cookie
	 * @param cookieValue
	 */
	public static void setBooleanCookieValue(Cookie cookie, boolean cookieValue) {
		// If cookies are enabled...
		if (isCookiesEnabled()) {
			// ...store the value for this one.
			Cookies.setCookie(cookie.getCookieName(), String.valueOf(cookieValue));
		}
	}
	
	/**
	 * Removes a cookie value for a cookie.
	 * 
	 * @param cookie
	 */
	public static void removeCookieValue(Cookie cookie) {
		// If cookies are enabled...
		if (isCookiesEnabled()) {
			// ...remove the value for this one.
			Cookies.removeCookie(cookie.getCookieName());
		}
	}
}
