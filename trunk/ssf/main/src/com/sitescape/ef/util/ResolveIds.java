package com.sitescape.ef.util;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.CustomAttribute;

public class ResolveIds {
	public static Collection getPrincipals(CustomAttribute attribute) {
		if ((attribute != null) && (attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING)) {
			Set<String> strIds = attribute.getValueSet();
			Set ids = new HashSet();
			for (String id: strIds) {
				try {
					ids.add(Long.valueOf(id));
				} catch (NumberFormatException ne) {};
			}
			
			ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
			return profileDao.loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());

		}
		return null;
	}
	public static Collection getPrincipals(Collection ids) {
		ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
		return profileDao.loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
	}
	//This is used after a search to map the binder id to a title
	public static Map getBinderTitles(Collection ids) {
		HashMap titles = new HashMap();
		if ((ids == null) || ids.isEmpty()) return titles;
		CoreDao coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
		String query = new String("select x.id,x.title from x in class com.sitescape.ef.domain.Binder where x.id in (:idList)");
		titles.put("idList", ids);
		List<Object[]> result = coreDao.loadObjects(query, titles);
		titles.clear();
		for (Object[] objs: result) {
			titles.put(objs[0].toString(), objs[1]);
		}
		return titles;
		
		
	}
}
