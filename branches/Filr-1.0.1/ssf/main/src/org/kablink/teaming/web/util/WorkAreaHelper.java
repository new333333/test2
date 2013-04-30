/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.PrincipalComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class WorkAreaHelper {
	public static void buildAccessControlTableBeans(AllModulesInjected bs, RenderRequest request, RenderResponse response, 
			WorkArea wArea, List functions, List membership, Map model, boolean ignoreFormData) {
		buildAccessControlTableBeansImpl(bs, request.getParameterMap(), wArea, functions, membership, model, ignoreFormData);
	}
	
	public static void buildAccessControlTableBeans(AllModulesInjected bs,
			WorkArea wArea, List functions, List membership, Map model) {
		buildAccessControlTableBeansImpl(bs, new HashMap(), wArea, functions, membership, model, true);
	}
	
	private static void buildAccessControlTableBeansImpl(AllModulesInjected bs, Map formData, 
			WorkArea wArea, List functions, List membership, Map model, boolean ignoreFormData) {
		User user = RequestContextHolder.getRequestContext().getUser();
		boolean roleTypeEntry = false;
		if (wArea instanceof Entry) roleTypeEntry = true;
		boolean roleTypeZone = false;
		if (wArea instanceof ZoneConfig) roleTypeZone = true;
		
		Principal binderOwner = wArea.getOwner();
        Comparator c = new PrincipalComparator(user.getLocale());
		Set teamMembers = new TreeSet(c);
		//bs.getProfileModule().getProfileBinder(); //Check access to user list
		if (wArea instanceof Binder) teamMembers = bs.getBinderModule().getTeamMembers((Binder)wArea, false);

		Set newRoleIds = new HashSet();
		String[] roleIds = new String[0];
		String[] principalIds = new String[0];
		String[] principalId = new String[0];
		
		Map functionMap = new HashMap();
		Map operationMap = new HashMap();
		Map allowedFunctions = new HashMap();
		Map sortedGroupsMap = new TreeMap();
		Map sortedGroupsMapAll = new TreeMap();
		Map sortedUsersMap = new TreeMap();
		Map sortedUsersMapAll = new TreeMap();
		Map sortedApplicationsMap = new TreeMap();
		Map sortedApplicationGroupsMap = new TreeMap();
		boolean zoneWide = wArea.getWorkAreaType().equals(ZoneConfig.WORKAREA_TYPE);

		String[] btnClicked = new String[] {""};
 		if (formData.containsKey("btnClicked")) btnClicked = (String[])formData.get("btnClicked");
		if (!ignoreFormData && (formData.containsKey("addRoleBtn") || 
				btnClicked[0].equals("addPrincipal") || btnClicked[0].equals("addRole"))) {
			if (formData.containsKey("roleIds")) {
				roleIds = (String[]) formData.get("roleIds");
				for (int i = 0; i < roleIds.length; i++) {
					if (!roleIds[i].equals("")) newRoleIds.add(Long.valueOf(roleIds[i]));
				}
			}
			if (formData.containsKey("roleIdToAdd")) {
				roleIds = (String[]) formData.get("roleIdToAdd");
				for (int i = 0; i < roleIds.length; i++) {
					if (!roleIds[i].equals("") && !newRoleIds.contains(Long.valueOf(roleIds[i]))) 
						newRoleIds.add(Long.valueOf(roleIds[i]));
				}
			}
			if (formData.containsKey("principalId")) {
				principalId = (String[]) formData.get("principalId");
			}

			if (formData.containsKey("principalIds")) {
				principalIds = (String[]) formData.get("principalIds");
			}

			//Get the role and user data from the form
			Map roleMembers = new HashMap();
			membership = new ArrayList();
						
			for (int i = 0; i < principalId.length; i++) {
				if (!principalId[i].equals("")) {
					Long id = Long.valueOf(principalId[i]);
					if (!membership.contains(id)) membership.add(id);
				}
			}			
						
			for (int i = 0; i < principalIds.length; i++) {
				if (!principalIds[i].equals("")) {
					Long id = Long.valueOf(principalIds[i]);
					if (!membership.contains(id)) membership.add(id);
				}
			}
			Iterator itFormData = formData.entrySet().iterator();
			while (itFormData.hasNext()) {
				Map.Entry me = (Map.Entry)itFormData.next();
				String key = (String)me.getKey();
				if (key.length() >= 8 && key.substring(0,7).equals("role_id")) {
					String[] s_roleId = key.substring(7).split("_");
					if (s_roleId.length == 2) {
						Long roleId = Long.valueOf(s_roleId[0]);
						Long memberId;
						if (s_roleId[1].equals("owner")) {
							memberId = ObjectKeys.OWNER_USER_ID;
						} else if (s_roleId[1].equals("teamMember")) {
							memberId = ObjectKeys.TEAM_MEMBER_ID;
						} else {
							memberId = Long.valueOf(s_roleId[1]);
						}
						if (!roleMembers.containsKey(roleId)) roleMembers.put(roleId, new ArrayList());
						List members = (List)roleMembers.get(roleId);
						if (!members.contains(memberId)) members.add(memberId);
						if (!membership.contains(memberId)) membership.add(memberId);
					}
				}
			}
			Collection ids = ResolveIds.getPrincipals(membership);
			Map principalMap = new HashMap();
    		for (Iterator iter=ids.iterator();iter.hasNext();) {
	    		Principal p = (Principal)iter.next();
				principalMap.put(p.getId(), p);
			}

			//Build the basic map structure
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				if (!zoneWide && f.isZoneWide()) continue;
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map groups = new HashMap();
				Map users = new HashMap();
				Map applicationGroups = new HashMap();
				Map applications = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				pMap.put(WebKeys.APPLICATIONS, applications);
				pMap.put(WebKeys.APPLICATION_GROUPS, applicationGroups);
				
				//Populate the map with data from the form instead of getting it from the database
				List members = (List)roleMembers.get(f.getId());
				if (members != null) {
					for (Iterator iter = members.iterator();iter.hasNext();) {
						Long pId = (Long)iter.next();
						if (pId.equals(ObjectKeys.OWNER_USER_ID)) {
							//The owner has this right
							pMap.put(WebKeys.OWNER, pId);
						} else if (pId.equals(ObjectKeys.TEAM_MEMBER_ID)) {
							//The team members have this right
							pMap.put(WebKeys.TEAM_MEMBER, pId);
						} else {
							Principal p = (Principal)principalMap.get(pId);
							if (p instanceof Group) {
								groups.put(p.getId(), p);
							} else if (p instanceof User) {
								users.put(p.getId(), p);
							} else if (p instanceof Application) {
								applications.put(p.getId(), p);
							} else if (p instanceof ApplicationGroup) {
								applicationGroups.put(p.getId(), p);
							}
						}
					}
				}
			}
			//Populate the sorted users and groups maps 
			for (Iterator iter = membership.iterator();iter.hasNext();) {
				Long pId = (Long)iter.next();
				Principal p = (Principal)principalMap.get(pId);
				if(p == null) continue;
				if (p instanceof Group) {
					sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
					sortedGroupsMapAll.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p instanceof User) {
					sortedUsersMap.put(Utils.getUserTitle(p).toLowerCase() + p.getName().toString(), p);
					sortedUsersMapAll.put(Utils.getUserTitle(p).toLowerCase() + p.getName().toString(), p);
				} else if (p instanceof Application) {
					sortedApplicationsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				} else if (p instanceof ApplicationGroup) {
					sortedApplicationGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
				}
			}

		} else {
			for (int i=0; i<functions.size(); ++i) {
				Function f = (Function)functions.get(i);
				if (!zoneWide && f.isZoneWide()) continue;
				Map pMap = new HashMap();
				functionMap.put(f, pMap);
				Map<Long,Principal> groups = new HashMap();
				Map<Long,Principal> users = new HashMap();
				Map applicationGroups = new HashMap();
				Map applications = new HashMap();
				pMap.put(WebKeys.USERS, users);
				pMap.put(WebKeys.GROUPS, groups);
				pMap.put(WebKeys.APPLICATIONS, applications);
				pMap.put(WebKeys.APPLICATION_GROUPS, applicationGroups);
				for (int j=0; j<membership.size(); ++j) {
					WorkAreaFunctionMembership m = (WorkAreaFunctionMembership)membership.get(j);
					if (f.getId().equals(m.getFunctionId())) {
						if (m.getMemberIds().contains(ObjectKeys.OWNER_USER_ID)) {
							pMap.put(WebKeys.OWNER, ObjectKeys.OWNER_USER_ID);
						};
						if (m.getMemberIds().contains(ObjectKeys.TEAM_MEMBER_ID)) {
							pMap.put(WebKeys.TEAM_MEMBER, ObjectKeys.TEAM_MEMBER_ID);
						}
						Collection ids = ResolveIds.getPrincipals(m.getMemberIds());
						for (Iterator iter=ids.iterator(); iter.hasNext();) {
							Principal p = (Principal)iter.next();
							if(p == null) continue;
							if (p instanceof Group) {
								groups.put(p.getId(), p);
								sortedGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
								sortedGroupsMapAll.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p instanceof User) {
								users.put(p.getId(), p);
								sortedUsersMap.put(Utils.getUserTitle(p).toLowerCase() + p.getName().toString(), p);
								sortedUsersMapAll.put(Utils.getUserTitle(p).toLowerCase() + p.getName().toString(), p);
							} else if (p instanceof Application) {
								applications.put(p.getId(), p);
								sortedApplicationsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							} else if (p instanceof ApplicationGroup) {
								applicationGroups.put(p.getId(), p);
								sortedApplicationGroupsMap.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
							}
						}
					}
				}
				//Build a map of users and groups per operation (e.g., operationMap.operation.ss_Users.userId)
				for (Object wo : f.getOperations()) {
					if (!operationMap.containsKey(((WorkAreaOperation)wo).getName())) 
						operationMap.put(((WorkAreaOperation)wo).getName(), new HashMap());
					Map operationMemberships = (Map)operationMap.get(((WorkAreaOperation)wo).getName());
					
					if (!operationMemberships.containsKey(WebKeys.USERS)) operationMemberships.put(WebKeys.USERS, new HashMap());
					Map operationUsers = (Map)operationMemberships.get(WebKeys.USERS);
					for (Map.Entry me : users.entrySet()) operationUsers.put(me.getKey(), me.getValue());
					if (pMap.containsKey(WebKeys.OWNER)) {
						operationUsers.put(binderOwner.getId(), binderOwner);
						sortedUsersMapAll.put(Utils.getUserTitle(binderOwner).toLowerCase() + binderOwner.getName().toString(), binderOwner);
					}
					
					if (!operationMemberships.containsKey(WebKeys.GROUPS)) operationMemberships.put(WebKeys.GROUPS, new HashMap());
					Map operationGroups = (Map)operationMemberships.get(WebKeys.GROUPS);
					for (Map.Entry me : groups.entrySet()) operationGroups.put(me.getKey(), me.getValue());
					
					if (pMap.containsKey(WebKeys.TEAM_MEMBER)) {
						Iterator itTeamMembers = teamMembers.iterator();
						while (itTeamMembers.hasNext()) {
							Principal p = (Principal)itTeamMembers.next();
							if(p == null) continue;
							if (p instanceof Group) {
								sortedGroupsMapAll.put(p.getTitle().toLowerCase() + p.getName().toString(), p);
								operationGroups.put(p.getId(), p);
							} else if (p instanceof User) {
								sortedUsersMapAll.put(Utils.getUserTitle(p).toLowerCase() + p.getName().toString(), p);
								operationUsers.put(p.getId(), p);
							}
						}
					}
				}
			}
		}
		
		//Build a sorted list of functions
		Map sortedFunctionsMap = new TreeMap();
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			Map pMap = (Map)functionMap.get(f);
			if (pMap == null) continue;
			Map users = (Map)pMap.get(WebKeys.USERS);
			Map groups = (Map)pMap.get(WebKeys.GROUPS);
			Map applications = (Map)pMap.get(WebKeys.APPLICATIONS);
			Map applicationGroups = (Map)pMap.get(WebKeys.APPLICATION_GROUPS);
			Integer operationCount = f.getOperations().size() + 1000;
			String sortKey = String.valueOf(operationCount);
			if (roleTypeEntry || roleTypeZone ||
					users.size() > 0 || 
					groups.size() > 0 || 
					applications.size() > 0 || 
					applicationGroups.size() > 0 || 
					pMap.containsKey(WebKeys.OWNER) || 
					pMap.containsKey(WebKeys.TEAM_MEMBER) ||
					newRoleIds.contains(f.getId())) {
				//This function has some membership (or it is an entry ACL); add it to the sorted list
				sortedFunctionsMap.put(sortKey + f.getName(), f);
			}
		}
		//list of sorted functions
		List sortedFunctions = new ArrayList(sortedFunctionsMap.values());

		//Build the sorted lists of users and groups
		List sortedGroups = new ArrayList(sortedGroupsMap.values());
		List sortedGroupsAll = new ArrayList(sortedGroupsMapAll.values());

		List sortedUsers = new ArrayList(sortedUsersMap.values());
		List sortedUsersAll = new ArrayList(sortedUsersMapAll.values());
		
		List sortedApplicationGroups = new ArrayList(sortedApplicationGroupsMap.values());

		List sortedApplications = new ArrayList(sortedApplicationsMap.values());
		
		//Build list of allowed roles
		for (int i=0; i<functions.size(); ++i) {
			Function f = (Function)functions.get(i);
			allowedFunctions.put(f.getId(), f);
		}
		
		model.put(WebKeys.WORKAREA, wArea);
		model.put(WebKeys.FUNCTION_MAP, functionMap);
		model.put(WebKeys.OPERATION_MAP, operationMap);
		model.put(WebKeys.FUNCTIONS_ALLOWED, allowedFunctions);
		model.put(WebKeys.ACCESS_SORTED_FUNCTIONS, sortedFunctions);
		model.put(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP, sortedFunctionsMap);
		model.put(WebKeys.ACCESS_FUNCTIONS_COUNT, Integer.valueOf(functionMap.size()));
		model.put(WebKeys.ACCESS_SORTED_USERS_MAP, sortedUsersMap);
		model.put(WebKeys.ACCESS_SORTED_USERS_MAP_ALL, sortedUsersMapAll);
		model.put(WebKeys.ACCESS_SORTED_USERS, sortedUsers);
		model.put(WebKeys.ACCESS_SORTED_USERS_ALL, sortedUsersAll);
		model.put(WebKeys.ACCESS_USERS_COUNT, Integer.valueOf(sortedUsers.size()));
		model.put(WebKeys.ACCESS_SORTED_GROUPS_MAP, sortedGroupsMap);
		model.put(WebKeys.ACCESS_SORTED_GROUPS_MAP_ALL, sortedGroupsMapAll);
		model.put(WebKeys.ACCESS_SORTED_GROUPS, sortedGroups);
		model.put(WebKeys.ACCESS_SORTED_GROUPS_ALL, sortedGroupsAll);
		model.put(WebKeys.ACCESS_GROUPS_COUNT, Integer.valueOf(sortedGroups.size()));

		model.put(WebKeys.ACCESS_SORTED_APPLICATIONS_MAP, sortedApplicationsMap);
		model.put(WebKeys.ACCESS_SORTED_APPLICATIONS, sortedApplications);
		model.put(WebKeys.ACCESS_APPLICATIONS_COUNT, Integer.valueOf(sortedApplications.size()));
		model.put(WebKeys.ACCESS_SORTED_APPLICATION_GROUPS_MAP, sortedApplicationGroupsMap);
		model.put(WebKeys.ACCESS_SORTED_APPLICATION_GROUPS, sortedApplicationGroups);
		model.put(WebKeys.ACCESS_APPLICATION_GROUPS_COUNT, Integer.valueOf(sortedApplicationGroups.size()));
	}
	
	public static void mergeAccessControlTableBeans(Map model) {
		
		List sortedFunctions = (List)model.get(WebKeys.ACCESS_SORTED_FUNCTIONS);
		Map sortedFunctionsMap = (Map)model.get(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP);
		List sortedGroups = (List)model.get(WebKeys.ACCESS_SORTED_GROUPS);
		List sortedUsers = (List)model.get(WebKeys.ACCESS_SORTED_USERS);
		Map sortedGroupsMap = (Map)model.get(WebKeys.ACCESS_SORTED_GROUPS_MAP);
		Map sortedUsersMap = (Map)model.get(WebKeys.ACCESS_SORTED_USERS_MAP);
		
		Map parentModel = (Map)model.get(WebKeys.ACCESS_PARENT);
		Map parentSortedFunctionsMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_FUNCTIONS_MAP);
		Map parentSortedGroupsMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_GROUPS_MAP);
		Map parentSortedUsersMap = (Map)parentModel.get(WebKeys.ACCESS_SORTED_USERS_MAP);
		
		for (Iterator i = parentSortedFunctionsMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedFunctionsMap.put(me.getKey(), me.getValue());
		}
		Iterator itFunctions = sortedFunctionsMap.keySet().iterator();
		while (itFunctions.hasNext()) {
			Function f = (Function)sortedFunctionsMap.get((String) itFunctions.next());
			if (!sortedFunctions.contains(f)) sortedFunctions.add(f);
		}

		for (Iterator i = parentSortedGroupsMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedGroupsMap.put(me.getKey(), me.getValue());
		}

		for (Iterator i = parentSortedUsersMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
			sortedUsersMap.put(me.getKey(), me.getValue());
		}

		//Merge the sorted lists of users and groups
		Iterator itGroups = sortedGroupsMap.keySet().iterator();
		while (itGroups.hasNext()) {
			Principal p = (Principal)sortedGroupsMap.get((String) itGroups.next());
			if (!sortedGroups.contains(p)) sortedGroups.add(p);
		}
		Iterator itUsers = sortedUsersMap.keySet().iterator();
		while (itUsers.hasNext()) {
			Principal p = (Principal)sortedUsersMap.get((String) itUsers.next());
			if (!sortedUsers.contains(p)) sortedUsers.add(p);
		}

		model.put(WebKeys.ACCESS_USERS_COUNT, Integer.valueOf(sortedUsers.size()));
		model.put(WebKeys.ACCESS_GROUPS_COUNT, Integer.valueOf(sortedGroups.size()));
	}
	public static void buildAccessControlRoleBeans(AllModulesInjected bs, Map model, boolean zoneWide) {
		//Add the list of existing functions for this zone
		//since names are translatable, build our own function map
		Map functions = new TreeMap();
		List<Function> fs = bs.getAdminModule().getFunctions();
		for (Function f:fs) {
			if (!zoneWide && f.isZoneWide()) continue;
			functions.put(NLT.getDef(f.getName()).toLowerCase() + f.getId(), f);
		}
		model.put(WebKeys.FUNCTIONS, functions.values());
		
		//Add the list of workAreaOperations that can be added to each function
		//title must be map key to keep alphabetical
		Map operations = new TreeMap();
		Iterator itWorkAreaOperations = WorkAreaOperation.getWorkAreaOperations();
		while (itWorkAreaOperations.hasNext()) {
			WorkAreaOperation wa = (WorkAreaOperation) itWorkAreaOperations.next();
			if (!zoneWide && wa.isZoneWide()) continue;
			operations.put(NLT.get("workarea_operation." + wa.getName()),wa.getName());
		}
		model.put(WebKeys.WORKAREA_OPERATIONS, operations);
		
		List<Condition> conditions = bs.getAdminModule().getFunctionConditions();
		model.put(WebKeys.FUNCTION_CONDITIONS, conditions);
	}
	
	public static void buildRoleConditionBeans(AllModulesInjected bs, Map model) {
		List<Condition> conditions = bs.getAdminModule().getFunctionConditions();
		model.put(WebKeys.FUNCTION_CONDITIONS, conditions);
		@SuppressWarnings("unused")
		Binder topBinder = bs.getWorkspaceModule().getTopWorkspace();
	}
		
}
