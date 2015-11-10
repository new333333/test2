/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.TimeZone;

import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.NoZoneByTheIdException;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * ?
 * 
 * @author jong
 */
public class LocaleUtils {
	public static String getLocaleLanguage() {
		ZoneConfig zoneConfig = getZoneConfig();
		if(zoneConfig != null)		
			return zoneConfig.getLocaleLanguage();
		else
			return SPropsUtil.getString("i18n.default.locale.language", "");
	}
	
	public static String getLocaleCountry() {
		ZoneConfig zoneConfig = getZoneConfig();
		if(zoneConfig != null)		
			return zoneConfig.getLocaleCountry();
		else
			return SPropsUtil.getString("i18n.default.locale.country", "");
	}
	
	private static ZoneConfig getZoneConfig() {
		Long zoneId = null;
		
		if(RequestContextHolder.getRequestContext() != null)
			zoneId = RequestContextHolder.getRequestContext().getZoneId();
		
		// This method may be called during system startup, in which case there is no specific
		// user context set up, hence no specific zone either. 
		if(zoneId == null)
			return null;
		
		try {
			return getCoreDao().loadZoneConfig(zoneId);
		}
		catch(NoZoneByTheIdException e) {
			// This exception can be raised when this method is called during a new zone creation which is normal.
			return null;
		}
	}
	
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}

	/**
	 * Returns the ID of the default timezone to use for new users.
	 * 
	 * @return
	 */
	public static String getDefaultTimeZoneId() {
		return getTimeZoneIdImpl(getZoneConfig().getTimeZone());
	}
	
	/**
	 * Returns the default timezone to use for new users.
	 * 
	 * @return
	 */
	public static TimeZone getDefaultTimeZone() {
		return getTimeZoneImpl(getZoneConfig().getTimeZone());
	}
	
	/**
	 * Returns the ID of the default timezone to use for new external
	 * users.
	 * 
	 * @return
	 */
	public static String getDefaultTimeZoneIdExt() {
		return getTimeZoneIdImpl(getZoneConfig().getTimeZoneExt());
	}
	
	/**
	 * Returns the default timezone to use for new external users.
	 * 
	 * @return
	 */
	public static TimeZone getDefaultTimeZoneExt() {
		return getTimeZoneImpl(getZoneConfig().getTimeZoneExt());
	}

	/*
	 * Returns the normalized timezone ID for the given timezone ID.
	 */
	private static String getTimeZoneIdImpl(String tz) {
		String reply;
		if (MiscUtil.hasString(tz))
		     reply = TimeZoneHelper.fixTimeZoneId(tz);
		else reply = TimeZoneHelper.getDefault().getID();
		return reply;
	}
	
	/*
	 * Returns the TimeZone for the given timezone ID.
	 */
	private static TimeZone getTimeZoneImpl(String tzId) {
		TimeZone reply;
		if (MiscUtil.hasString(tzId))
		     reply = TimeZoneHelper.getTimeZone(tzId);
		else reply = TimeZoneHelper.getDefault();
		return reply;
	}
	
	/**
	 * Returns the language of the default locale to use for new users.
	 * 
	 * @return
	 */
	public static String getDefaultLocaleLanguage() {
		return getLocaleLanguage();
	}
	
	/**
	 * Returns the country of the default locale to use for new users.
	 * 
	 * @return
	 */
	public static String getDefaultLocaleCountry() {
		return getLocaleCountry();
	}
	
	/**
	 * Returns the default locale to use for new users.
	 * 
	 * @param defaultLocaleId
	 * 
	 * @return
	 */
	public static Locale getDefaultLocale(String defaultLocaleId) {
		return
			getLocaleImpl(
				getDefaultLocaleLanguage(),
				getDefaultLocaleCountry(),
				defaultLocaleId);
	}
	
	public static Locale getDefaultLocale() {
		// Always use the initial form of the method.
		return getDefaultLocale(null);
	}
	
	/**
	 * Returns the language of the default locale to use for new
	 * external users.
	 * 
	 * @return
	 */
	public static String getDefaultLocaleLanguageExt() {
		return getZoneConfig().getLocaleLanguageExt();
	}
	
	/**
	 * Returns the country of the default locale to use for new
	 * external users.
	 * 
	 * @return
	 */
	public static String getDefaultLocaleCountryExt() {
		return getZoneConfig().getLocaleCountryExt();
	}
	
	/**
	 * Returns the default locale to use for new external users.
	 * 
	 * @param defaultLocaleId
	 * 
	 * @return
	 */
	public static Locale getDefaultLocaleExt(String defaultLocaleId) {
		return
			getLocaleImpl(
				getDefaultLocaleLanguageExt(),
				getDefaultLocaleCountryExt(),
				defaultLocaleId);
	}
	
	public static Locale getDefaultLocaleExt() {
		// Always use the initial form of the method.
		return getDefaultLocaleExt(null);
	}

	/*
	 * Constructs a Locale from a language and country.
	 */
	private static Locale getLocaleImpl(String language, String country, String defaultLocaleId) {
		Locale reply = null;
		if (MiscUtil.hasString(language)) {
			if (MiscUtil.hasString(country))
			     reply = new Locale(language, country);
			else reply = new Locale(language         );
		}
		if (null == reply) {
			if (MiscUtil.hasString(defaultLocaleId))
			     reply = new Locale(defaultLocaleId);
			else reply =  NLT.getTeamingLocale();
		}
		return reply;
	}
}
