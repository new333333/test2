/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.NoContextUserException;
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
 * ?
 * 
 * @author hurley
 */
@SuppressWarnings("unchecked")
public class NLT implements ApplicationContextAware {
	
	protected static Log logger = LogFactory.getLog(NLT.class);
	
	private static Locale teamingLocale = null;
	
	private static NLT nlt; // Singleton instance

	private ApplicationContext ac;
	
	public NLT() {
		if(nlt == null)
			nlt = this;
		else
			throw new SingletonViolationException(NLT.class);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	protected ApplicationContext getApplicationContext() {
		return ac;
	}
	
	protected static NLT getInstance() {
		return nlt;
	}
	
	public static Locale getTeamingLocale() {
		if (null == teamingLocale) {
			String language = LocaleUtils.getLocaleLanguage();
			String country  = LocaleUtils.getLocaleCountry();
			if ((null != language) && (0 < language.length())) {
				if ((null != country) && (0 < country.length())) teamingLocale = new Locale(language, country);
				else                                             teamingLocale = new Locale(language);
			}
			else {
				teamingLocale = Locale.getDefault();
			}
		}
		return teamingLocale;
	}
	
	public static Locale getDefaultLocale() {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if(rc != null) {
			User user = null;
			try {
				user = rc.getParentUser();
				if(user == null)				
					user = rc.getUser();
			}
			catch(NoContextUserException doNotPropogate) {}
			if(user != null)
				return user.getLocale();
			else
				return getTeamingLocale();			
		}
		else {
			return getTeamingLocale();
		}
	}
	
	private String getMessageWithTagAsDefault(String tag) {
		return getMessageWithTagAsDefault(tag, null, getDefaultLocale());
	}
	
	private String getMessageWithTagAsDefault(String tag, Object[] args) {
		return getMessageWithTagAsDefault(tag, args, getDefaultLocale());		
	}
	
	private String getMessageWithTagAsDefault(String tag, Locale locale) {
 		return getMessageWithTagAsDefault(tag, null, locale);
	}
	
	private String getMessageWithTagAsDefault(String tag, Object[] args, Locale locale) {
		return getMessageWithDefault(tag, args, tag, locale, false);
	}
	
	private String getMessageWithTextAsDefault(String tag, String text) {
		return getMessageWithTextAsDefault(tag, null, text, getDefaultLocale());
	}
	
	private String getMessageWithTextAsDefault(String tag, String text, Boolean silent) {
		return getMessageWithTextAsDefault(tag, null, text, getDefaultLocale(), silent);
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text) {
		return getMessageWithTextAsDefault(tag, args, text, getDefaultLocale());
	}
	
	private String getMessageWithTextAsDefault(String tag, String text, Locale locale) {
		return getMessageWithTextAsDefault(tag, null, text, locale);
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text, Locale locale, Boolean silent) {
		return getMessageWithDefault(tag, args, text, locale, silent);
	}
	
	private String getMessageWithTextAsDefault(String tag, Object[] args, String text, Locale locale) {
		return getMessageWithDefault(tag, args, text, locale, false);
	}
	
	private String getMessageWithDefault(String tag, Object[] args, String defaultMessage, Locale locale, Boolean silent) {
    	String translation = "";
    	if (tag == null || tag.equals("")) return defaultMessage;
    	try {
    		translation = getApplicationContext().getMessage(tag, args, locale);
    	} catch (NoSuchMessageException e) {
    	    if (!silent) logger.warn(e);
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
	
	public static String get(String tag, String text, Boolean silent) {
		return getInstance().getMessageWithTextAsDefault(tag, text, silent);
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
	public static TreeMap<String,Locale> getSortedLocaleList(User user) {
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		Set<Locale> ids = getLocales();
		TreeMap<String,Locale> map = new TreeMap(new StringComparator(user.getLocale())); //sort
		for (Locale lc : ids) {
			map.put(lc.getDisplayName(lc) + "   [" + lc.getDisplayName(currentUser.getLocale()) + "]", lc);
		}
		//make sure current users Locale appears
		map.put(user.getLocale().getDisplayName(user.getLocale()) + "   [" + user.getLocale().getDisplayName(currentUser.getLocale()) + "]", user.getLocale());
		
		return map;
	}
}
