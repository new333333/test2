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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class ResolveIds {
	private static Log logger = LogFactory.getLog(ResolveIds.class);
	
	public static List getPrincipals(Object principalIds) {
		return getPrincipals(principalIds, true);
	}
	public static List getPrincipals(Object principalIds, boolean checkActive) {
		if (principalIds == null) {
			return Collections.EMPTY_LIST;
		}
		if (principalIds.getClass().isAssignableFrom(String.class)) {
			return getPrincipals((String)principalIds, checkActive);
		} else if (principalIds.getClass().isAssignableFrom(SearchFieldResult.class)) {
			return getPrincipals((SearchFieldResult)principalIds, checkActive);
		}
		logger.warn("getPrincipals called with wrong parameter class [" + principalIds.getClass() + "]");
		return Collections.EMPTY_LIST;
	}
	
	public static List getPrincipals(String principalId) {
		return getPrincipals(principalId, true);
	}
	public static List getPrincipals(String principalId, boolean checkActive) {
		if (principalId == null) {
			return Collections.EMPTY_LIST;
		}		
		Set<Long> ids = new HashSet();
		try {
			ids.add(Long.valueOf(principalId));
		} catch (NumberFormatException ne) {};
		return getPrincipals(ids, checkActive);
	}
	
	public static List getPrincipals(SearchFieldResult principalIds) {
		return getPrincipals(principalIds, true);
	}
	public static List getPrincipals(SearchFieldResult principalIds, boolean checkActive) {
		if (principalIds == null) {
			return Collections.EMPTY_LIST;
		}		
		Set<String> strIds = principalIds.getValueSet();
		List ids = stringsToLongs(strIds);
		return getPrincipals(ids, checkActive);
	}
	
	public static List getPrincipals(CustomAttribute attribute) {
		return getPrincipals(attribute, true);
	}
	public static List getPrincipals(CustomAttribute attribute, boolean checkActive) {
		if ((attribute == null) || (attribute.getValueType() != CustomAttribute.COMMASEPARATEDSTRING)) {
			return null;
		}
		
		Set<String> strIds = attribute.getValueSet();
		List ids = stringsToLongs(strIds);
		return getPrincipals(ids, checkActive);
	}
	

	public static List getPrincipals(Collection ids) {
		return getPrincipals(ids, true);
	}
	public static List getPrincipals(Collection ids, boolean checkActive) {
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
		result.addAll(profileDao.loadPrincipals(filteredIds, RequestContextHolder.getRequestContext().getZoneId(), checkActive));
		return profileDao.filterInaccessiblePrincipals(result);
	}
	
	public static Set<Long> getPrincipalNamesAsLongIdSet(String []sNames, boolean checkActive) {
		Set<Long> memberIds = new HashSet<Long>();
		List<Principal> members = getPrincipalsByName(LongIdUtil.getNamesAsStringSet(sNames), checkActive);
		for (Principal p : members) memberIds.add(p.getId());
		return memberIds;		
	}

	public static List<Principal> getPrincipalsByName(Collection names, boolean checkActive) {
		if (names == null) {
			return Collections.EMPTY_LIST;
		}

		ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
		List<Principal> result = new ArrayList();
		
		List filteredIds = new ArrayList();
		Iterator it = names.iterator();
		while (it.hasNext()) {
			try {
				Principal p = profileDao.findPrincipalByName((String)it.next(), RequestContextHolder.getRequestContext().getZoneId());
				if (checkActive) {
					if (p.isActive()) result.add(p);
				} else {
					result.add(p);
				}
			} catch(NoPrincipalByTheNameException e) {}
		}
		return profileDao.filterInaccessiblePrincipals(result);
	}
	
	public static List<Principal> findPrincipalByEmailAdr(Collection names, boolean checkActive) {
		if (names == null) {
			return Collections.EMPTY_LIST;
		}

		ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
		List<Principal> result = new ArrayList();
		
		List filteredIds = new ArrayList();
		Iterator it = names.iterator();
		while (it.hasNext()) {
			try {
				List<Principal> pList = profileDao.loadPrincipalByEmail((String)it.next(), null, RequestContextHolder.getRequestContext().getZoneId());
				if (pList != null && pList.size() > 0) {
					Principal p = pList.get(0);
					if (checkActive) {
						if (p.isActive()) result.add(p);
					} else {
						result.add(p);
					}
				}
			} catch(NoPrincipalByTheNameException e) {}
		}
		return profileDao.filterInaccessiblePrincipals(result);
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
		ids = LongIdUtil.getIdsAsLongSet(ids); // ids is collection of Longs or Strings
		Map results = new HashMap();
		Map data = new HashMap();
		Map icons = new HashMap();
		if ((ids == null) || ids.isEmpty()) return data;
		CoreDao coreDao = (CoreDao)SpringContextUtil.getBean("coreDao");
		List<Binder> result;
		try {
			result = coreDao.loadObjects(ids, Class.forName("org.kablink.teaming.domain.Binder"), RequestContextHolder.getRequestContext().getZoneId());
			for (Binder binder: result) {
				data = new HashMap();
				data.put("id", binder.getId().toString());
				data.put("title", binder.getTitle());
				data.put("iconName", binder.getIconName());
				data.put("deleted", binder.isDeleted());
				if (binder.getDefinitionType() == null) {
					int defType;
					if      (binder instanceof Workspace) defType = Definition.WORKSPACE_VIEW;
					else if (binder instanceof Folder)    defType = Definition.FOLDER_VIEW;
					else                                  defType = (-1);
					if ((-1) != defType) {
						data.put("definitionType", defType);
					}
				}
				else {
					data.put("definitionType", binder.getDefinitionType().toString());
				}
				data.put("pathName", binder.getPathName());
				data.put("parentTitle", binder.getParentBinder().getTitle());
				results.put(binder.getId().toString(), data);
			}
		} catch (Exception e) {
			/* return an empty results map. */
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
		ids = LongIdUtil.getIdsAsLongSet(ids); // ids is collection of Longs or Strings
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

	/**
	 * Resolves, if possible, a user ID to a User object.
	 * 
	 * @param userId
	 * @param checkActive
	 * 
	 * @return
	 */
	public static User getResolvedUser(Long userId, boolean checkActive) {
		User user = null;
		String userIdS = String.valueOf(userId);
		List<String> userIdList = new ArrayList<String>();
		userIdList.add(userIdS);
		List resolvedList = getPrincipals(userIdList, checkActive);
		if (MiscUtil.hasItems(resolvedList)) {
			Object o = resolvedList.get(0);
			if (o instanceof User) {
				user = ((User) o);
			}
		}
		return user;
	}
}
