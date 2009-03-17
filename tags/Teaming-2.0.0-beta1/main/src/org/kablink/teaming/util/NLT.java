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
package org.kablink.teaming.util;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.NoSuchMessageException;

import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;

/**
 * @author hurley
 *
 */
public class NLT implements ApplicationContextAware {
	
	protected static Log logger = LogFactory.getLog(NLT.class);
	
	private static NLT nlt; // Singleton instance

	private ApplicationContext ac;
	
	public NLT() {
		if(nlt == null)
			nlt = this;
		else
			throw new SingletonViolationException(NLT.class);
	}
	
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	protected ApplicationContext getApplicationContext() {
		return ac;
	}
	
	protected static NLT getInstance() {
		return nlt;
	}
	
	private Locale getLocale() {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if(rc != null) {
			User user = rc.getUser();
			if(user != null)
				return user.getLocale();
			else
				return Locale.getDefault();			
		}
		else {
			return Locale.getDefault();
		}
	}
	
	private String getMessageWithTagAsDefault(String tag) {
		return getMessageWithTagAsDefault(tag, null, getLocale());
	}
	
	private String getMessageWithTagAsDefault(String tag, Object[] args) {
		return getMessageWithTagAsDefault(tag, args, getLocale());		
	}
	
	private String getMessageWithTagAsDefault(String tag, Locale locale) {
 		return getMessageWithTagAsDefault(tag, null, locale);
	}
	
	private String getMessageWithTagAsDefault(String tag, Object[] args, Locale locale) {
		return getMessageWithDefault(tag, args, tag, locale);
	}
	
	private String getMessageWithTextAsDefault(String tag, String text) {
		return getMessageWithTextAsDefault(tag, null, text, getLocale());
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text) {
		return getMessageWithTextAsDefault(tag, args, text, getLocale());
	}
	
	private String getMessageWithTextAsDefault(String tag, String text, Locale locale) {
		return getMessageWithTextAsDefault(tag, null, text, locale);
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text, Locale locale) {
		return getMessageWithDefault(tag, args, text, locale);
	}
	
	private String getMessageWithDefault(String tag, Object[] args, String defaultMessage, Locale locale) {
    	String translation = "";
    	if (tag == null || tag.equals("")) return defaultMessage;
    	try {
    		translation = getApplicationContext().getMessage(tag, args, locale);
    	} catch (NoSuchMessageException e) {
    	    logger.warn(e);
    		translation = defaultMessage;
    	}
    	return translation;				
	}
	
	public static String getDef(String tag) {
		if (tag != null && tag.startsWith("__")) {
			//If the tag starts with "__" it is a tag to be translated
			return get(tag);
		}
		else {
			//Otherwise, this is just a string, so return it unchanged.
			return tag;
		}
	}

	public static String getDef(String tag, Locale locale) {
		if (tag != null && tag.startsWith("__")) {
			//If the tag starts with "__" it is a tag to be translated
			return get(tag, locale);
		}
		else {
			//Otherwise, this is just a string, so return it unchanged.
			return tag;
		}
	}
	public static String get(String tag) {
		return getInstance().getMessageWithTagAsDefault(tag);
	}
	
	public static String get(String tag, Object[] args) {
		return getInstance().getMessageWithTagAsDefault(tag, args);
	}

	public static String get(String tag, Locale locale) {
		return getInstance().getMessageWithTagAsDefault(tag, locale);
	}

	public static String get(String tag, Object[] args, Locale locale) {
		return getInstance().getMessageWithTagAsDefault(tag, args, locale);
	}

	public static String get(String tag, String text) {
		return getInstance().getMessageWithTextAsDefault(tag, text);
	}
	
	public static String get(String tag, Object[] args,  String text) {
		return getInstance().getMessageWithTextAsDefault(tag, args, text);
	}
	
	public static String get(String tag, String text, Locale locale) {
		return getInstance().getMessageWithTextAsDefault(tag, text, locale);
	}
	
	public static String get(String tag, Object[] args, String text, Locale locale) {
		return getInstance().getMessageWithTextAsDefault(tag, args, text, locale);
	}
	public static Set<Locale> getLocales() {
		java.util.Locale[] ids = java.util.Locale.getAvailableLocales();
		String[] strs = SPropsUtil.getStringArray("i18n.locale.support", Constants.COMMA);
		SortedSet supported = new TreeSet();
		for (int i=0; i<strs.length; ++i) supported.add(strs[i]);
		//now see which ones are supported
		Set<Locale> results = new java.util.HashSet();
		for (int i=0; i<ids.length; ++i) {
			if (supported.contains(ids[i].getLanguage())) {
				//this will skip the 'general' locales and only show those with countries
				if ( Validator.isNotNull(ids[i].getCountry())) {
					results.add(ids[i]);
				}
			} else if (supported.contains(ids[i].toString())) {
				results.add(ids[i]);
			}
		}
		return results;

	}
}
