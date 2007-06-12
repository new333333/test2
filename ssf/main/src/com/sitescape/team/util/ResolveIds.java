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
package com.sitescape.team.util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.search.SearchFieldResult;

public class ResolveIds {
	
	private static Log logger = LogFactory.getLog(ResolveIds.class);
	
	public static List getPrincipals(Object principalIds) {
		if (principalIds == null) {
			return Collections.EMPTY_LIST;
		}
		if (principalIds.getClass().isAssignableFrom(String.class)) {
			return getPrincipals((String)principalIds);
		} else if (principalIds.getClass().isAssignableFrom(SearchFieldResult.class)) {
			return getPrincipals((SearchFieldResult)principalIds);
		}
		logger.warn("getPrincipals called with wrong parameter class [" + principalIds.getClass() + "]");
		return Collections.EMPTY_LIST;
	}
	
	public static List getPrincipals(String principalId) {
		if (principalId == null) {
			return Collections.EMPTY_LIST;
		}		
		Set<Long> ids = new HashSet();
		try {
			ids.add(Long.valueOf(principalId));
		} catch (NumberFormatException ne) {};
		return getPrincipals(ids);
	}
	
	public static List getPrincipals(SearchFieldResult principalIds) {
		if (principalIds == null) {
			return Collections.EMPTY_LIST;
		}		
		Set<String> strIds = principalIds.getValueSet();
		List ids = stringsToLongs(strIds);
		return getPrincipals(ids);
	}
	
	public static List getPrincipals(CustomAttribute attribute) {
		if ((attribute == null) || (attribute.getValueType() != CustomAttribute.COMMASEPARATEDSTRING)) {
			return null;
		}
		
		Set<String> strIds = attribute.getValueSet();
		List ids = stringsToLongs(strIds);
		return getPrincipals(ids);
	}
	

	public static List getPrincipals(Collection ids) {
		if (ids == null) {
			return Collections.EMPTY_LIST;
		}
		ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
		return profileDao.loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), true);
	}
	
	public static Map getBinderTitlesAndIcons(Object binderIds) {
		if (binderIds == null) {
			return Collections.EMPTY_MAP;
		}
		if (binderIds.getClass().isAssignableFrom(String.class)) {
			return getBinderTitlesAndIcons((String)binderIds);
		} else if (binderIds.getClass().isAssignableFrom(SearchFieldResult.class)) {
			return getBinderTitlesAndIcons((SearchFieldResult)binderIds);
		}
		logger.warn("getBinderTitlesAndIcons called with wrong parameter class [" + binderIds.getClass() + "]");
		return Collections.EMPTY_MAP;
	}
	
	public static Map getBinderTitlesAndIcons(String binderIds) {
		if (binderIds == null) {
			return Collections.EMPTY_MAP;
		}
		Set<Long> ids = new HashSet();
		try {
			ids.add(Long.valueOf(binderIds));
		} catch (NumberFormatException ne) {};
		return getBinderTitlesAndIcons(ids);
	}
	
	public static Map getBinderTitlesAndIcons(SearchFieldResult binderIds) {
		if (binderIds == null) {
			return Collections.EMPTY_MAP;
		}
		Set<String> strIds = binderIds.getValueSet();
		List ids = stringsToLongs(strIds);
		return getBinderTitlesAndIcons(ids);
	}
	
	public static Map getBinderTitlesAndIcons(CustomAttribute attribute) {
		if ((attribute == null) || (attribute.getValueType() != CustomAttribute.COMMASEPARATEDSTRING)) {
			return Collections.EMPTY_MAP;
		}
		
		List ids = stringsToLongs(attribute.getValueSet());
		return getBinderTitlesAndIcons(ids);
	}
	
	//This is used after a search to map the binder id to a title
	public static Map getBinderTitlesAndIcons(Collection ids) {
		if (ids == null) {
			return Collections.EMPTY_MAP;
		}
		Map results = new HashMap();
		Map data = new HashMap();
		Map icons = new HashMap();
		if ((ids == null) || ids.isEmpty()) return data;
		CoreDao coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
		String query = new String("select x.id,x.title,x.iconName from x in class com.sitescape.team.domain.Binder where x.id in (:idList)");
		data.put("idList", ids);
		List<Object[]> result = coreDao.loadObjects(query, data);
		data.clear();
		for (Object[] objs: result) {
			data = new HashMap();
			data.put("title", objs[1]);
			data.put("iconName", objs[2]);
			results.put(objs[0].toString(), data);
		}
		return results;
	}
	
	public static List stringsToLongs(Collection ids) {
		List result = new ArrayList();
		
		if (ids == null) {
			return result;
		}
		
		Iterator it = ids.iterator();
		while (it.hasNext()) {
			String id = (String)it.next();
			try {
				result.add(Long.parseLong(id));
			} catch (NumberFormatException e) {}
		}
		
		return result;
	}
	
	public static List longsToString(Collection ids) {
		List result = new ArrayList();
		
		if (ids == null) {
			return result;
		}
		
		Iterator it = ids.iterator();
		while (it.hasNext()) {
			Long id = (Long)it.next();
			result.add(id.toString());
		}
		
		return result;
	}

}
