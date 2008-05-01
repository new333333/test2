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
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilterKeys;

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
		List result = new ArrayList();
		
		List filteredIds = new ArrayList();
		Iterator it = ids.iterator();
		while (it.hasNext()) {
			Object firstId = it.next();
			if (firstId.equals(SearchFilterKeys.CurrentUserId)) {
				Map currentUser = new HashMap();
				currentUser.put("id", firstId);
				currentUser.put("title", NLT.get("searchForm.currentUserTitle"));
				result.add(currentUser);
			} else {
				if (firstId != null && firstId.getClass().equals(Long.class)) {
					filteredIds.add(firstId);
				} else {
					try {
						filteredIds.add(Long.parseLong((String)firstId));
					} catch (NumberFormatException e) {
						// ignore id
					}
				}
			}
		}
		result.addAll(profileDao.loadPrincipals(filteredIds, RequestContextHolder.getRequestContext().getZoneId(), true));
		return result;
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
			data.put("id", objs[0].toString());
			results.put(objs[0].toString(), data);
		}
		return results;
	}
	
	public static Set getBinders(Object teamIds) {
		if (teamIds == null) {
			return Collections.EMPTY_SET;
		}
		if (teamIds.getClass().isAssignableFrom(String.class)) {
			return getBinders((String)teamIds);
		} else if (teamIds.getClass().isAssignableFrom(SearchFieldResult.class)) {
			return getBinders((SearchFieldResult)teamIds);
		}
		logger.warn("getBinders called with wrong parameter class [" + teamIds.getClass() + "]");
		return Collections.EMPTY_SET;
	}
	
	public static Set getBinders(SearchFieldResult teamIds) {
		if (teamIds == null) {
			return Collections.EMPTY_SET;
		}		
		Set<String> strIds = teamIds.getValueSet();
		List ids = stringsToLongs(strIds);
		return getBinders(ids);
	}	
	
	public static Set getBinders(String teamIds) {
		if (teamIds == null) {
			return Collections.EMPTY_SET;
		}		
		Set<Long> ids = new HashSet();
		try {
			ids.add(Long.valueOf(teamIds));
		} catch (NumberFormatException ne) {};
		return getBinders(ids);
	}
	
	public static Set getBinders(CustomAttribute attribute) {
		if ((attribute == null) || (attribute.getValueType() != CustomAttribute.COMMASEPARATEDSTRING)) {
			return null;
		}
		
		Set<String> strIds = attribute.getValueSet();
		List ids = stringsToLongs(strIds);
		return getBinders(ids);
	}
	
	public static Set getBinders(Collection ids) {
		if (ids == null) {
			return Collections.EMPTY_SET;
		}
		BinderModule binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
		return binderModule.getBinders(ids);
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
				if (SearchFilterKeys.CurrentUserId.equals(id)) {
					// leave pseudo id on the list
					result.add(id);
				} else {
					result.add(Long.parseLong(id));
				}
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
