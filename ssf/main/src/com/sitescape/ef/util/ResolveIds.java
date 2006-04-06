package com.sitescape.ef.util;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collection;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.dao.ProfileDao;
public class ResolveIds {
	public static Collection getPrincipals(CustomAttribute attribute) {
		if ((attribute != null) && (attribute.getValueType() == CustomAttribute.COMMASEPARATEDSTRING)) {
			Set strIds = attribute.getValueSet();
			Set ids = new HashSet();
			for (Iterator i=strIds.iterator(); i.hasNext();) {
				try {
					ids.add(Long.valueOf((String)i.next()));
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
}
