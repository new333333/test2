/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.liferaylight.portlet;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.portletadapter.support.RequestLocaleResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Jong Kim
 *
 */
public class MessageUtil implements ApplicationContextAware {

	private static MessageUtil messageUtil; // singleton instance
	
	private ApplicationContext ac;
	
	private RequestLocaleResolver localeResolver;

	public MessageUtil() {
		if(messageUtil == null)
			messageUtil = this;
		else
			throw new SingletonViolationException(MessageUtil.class);
	}
	
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	protected ApplicationContext getApplicationContext() {
		return ac;
	}
	
	protected RequestLocaleResolver getLocaleResolver() {
		return localeResolver;
	}

	public void setLocaleResolver(RequestLocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	protected static MessageUtil getInstance() {
		return messageUtil;
	}

	public static String get(HttpServletRequest req, String tag) {
		return getInstance().getMessage(req, tag);
	}
	
	public static String get(PortletRequest req, String tag) {
		return getInstance().getMessage(req, tag);
	}
	
	public String getMessage(HttpServletRequest req, String tag) {
		return getMessage(getLocaleResolver().resolveLocale(req), tag);
	}
	
	public String getMessage(PortletRequest req, String tag) {
		return getMessage(getLocaleResolver().resolveLocale(req), tag);
	}
	
	protected String getMessage(Locale locale, String tag) {
		return getApplicationContext().getMessage(tag, null, locale);
	}
}
