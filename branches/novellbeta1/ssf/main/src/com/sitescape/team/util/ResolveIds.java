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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.CustomAttribute;

public class ResolveIds {
	public static List getPrincipals(CustomAttribute attribute) {
		if ((attribute != null) && (attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING)) {
			Set<String> strIds = attribute.getValueSet();
			Set ids = new HashSet();
			for (String id: strIds) {
				try {
					ids.add(Long.valueOf(id));
				} catch (NumberFormatException ne) {};
			}
			
			ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
			return profileDao.loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), true);

		}
		return null;
	}
	public static List getPrincipals(Collection ids) {
		ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
		return profileDao.loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), true);
	}
	//This is used after a search to map the binder id to a title
	public static Map getBinderTitlesAndIcons(Collection ids) {
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
}
