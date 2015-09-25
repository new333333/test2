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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LimitUserVisibilityInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetLimitedUserVisibilityRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.util.PrincipalType;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for limited user visibility.
 *
 * @author drfoster@novell.com
 */
public class GwtUserVisibilityHelper {
	protected static Log m_logger = LogFactory.getLog(GwtUserVisibilityHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtUserVisibilityHelper() {
		// Nothing to do.
	}
	
	/*
	 * Applies a quick filter to a List<FolderRow> of 'Limited User
	 * Visibility' rows.  A List<FolderRow> of the the FolderRow's from
	 * the input list that matches the filter is returned.
	 */
	public static List<FolderRow> filterLimitedUserVisibilityRows(List<FolderColumn> folderColumns, List<FolderRow> luvRows, String quickFilter) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserVisibilityHelper.filterLimitedUserVisibilityRows()");
		try {
			// Do we have a string to filter with and some FolderRow's
			// to be filtered?
			if (null != quickFilter) {
				quickFilter = quickFilter.trim().toLowerCase();
			}
			if (MiscUtil.hasString(quickFilter) && MiscUtil.hasItems(luvRows)) {
				// Yes!  Yes!  Scan the rows.
				List<FolderRow> reply = new ArrayList<FolderRow>();
				for (FolderRow fr:  luvRows) {
					// Scan the columns.
					for (FolderColumn fc:  folderColumns) {
						// What column is this?
						String cName = fc.getColumnName();
						if (FolderColumn.isColumnLimitedVisibilityUser(cName)) {
							// A limited user visibility column!  If
							// this value contains the quick filter...
							List<AssignmentInfo> aiList = fr.getColumnValueAsAssignmentInfos(fc);
							AssignmentInfo ai =  aiList.get(0);	// Will only ever be one.
							if (GwtViewHelper.valueContainsQuickFilter(ai.getTitle(), quickFilter)) {
								// ...add the row to the reply list.
								reply.add(fr);
								break;
							}
						}
						
						else if (FolderColumn.isColumnPrincipalType(cName)) {
							// A PrincipalType column!  We don't do
							// filtering on that.
						}
						
						else {
							// The remaining columns all contain simple
							// strings!  If the filter is in the
							// string...
							String sv = fr.getColumnValueAsString(fc);
							if (GwtViewHelper.valueContainsQuickFilter(sv, quickFilter)) {
								// ...add the row to the reply list.
								reply.add(fr);
								break;
							}
						}
					}
				}
				luvRows = reply;
			}
			
			// If we get here, filterRows refers to the filtered list
			// of rows.  Return it. 
			return luvRows;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a LimitUserVisibilityInfoRpcResponseData object
	 * containing the information for limiting user visibility.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static LimitUserVisibilityInfoRpcResponseData getLimitUserVisibilityInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the LimitUserVisibilityInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo    bi = GwtServerHelper.getBinderInfo(bs, request, bs.getWorkspaceModule().getTopWorkspaceId());
			WorkspaceType wt = bi.getWorkspaceType();
			if (!(wt.isTopWS() || wt.isLandingPage())) {
				GwtLogHelper.error(m_logger, "GwtServerHelper.getLimitUserVisibilityInformation():  The workspace type of the root binder was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.TOP.name());
			}
			bi.setWorkspaceType(WorkspaceType.LIMIT_USER_VISIBILITY);
			LimitUserVisibilityInfoRpcResponseData reply =
				new LimitUserVisibilityInfoRpcResponseData(
					bi,
					NLT.get("administration.manage.limitUserVisibility"));

			// If we get here, reply refers to the
			// LimitUserVisibilityInfoRpcResponseData object
			// containing the information about limiting user
			// visibility.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/**
	 * Returns the rows for the limit user visibility view.
	 * 
	 * @param bs
	 * @param request
	 * @param binder
	 * @param quickFilter
	 * @param options
	 * @param folderColumns
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderRowsRpcResponseData getLimitUserVisibilityRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserVisibilityHelper.getLimitUserVisibilityRows()");
		try {
			// Scan the user limited user visibility list.
			List<FolderRow> luvRows     = new ArrayList<FolderRow>();
			List<Principal> luvList     = new ArrayList<Principal>();
			List<Long>      luvLimited  = new ArrayList<Long>();
			List<Long>      luvOverride = new ArrayList<Long>();
			getLimitedVisibilityUsers(bs, luvList, luvLimited, luvOverride);
			FolderColumn limitedUserVisibilityColumn = null;
			for (Principal luvPrincipal:  luvList) {
				boolean luvIsGroup =                   (luvPrincipal instanceof GroupPrincipal);
				boolean luvIsUser  = ((!luvIsGroup) && (luvPrincipal instanceof UserPrincipal));
				if ((!luvIsGroup) && (!luvIsUser)) {
					continue;
				}
				
				// Create the FolderRow for this limited user
				// visibility setting and add it to the list.
				Long      luvId = luvPrincipal.getId();
				EntityId  eid   = new EntityId(binder.getId(), luvId, (luvIsGroup ? EntityId.GROUP : EntityId.USER));
				FolderRow fr    = new FolderRow(eid, folderColumns);
				luvRows.add(fr);

				// Scan the columns.
				for (FolderColumn fc:  folderColumns) {
					// What column is this?
					String cName = fc.getColumnName();
					if (FolderColumn.isColumnLimitedVisibilityUser(cName)) {
						// The Limited Visibility User column!
						// Generate an AssignmentInfo for it.
						limitedUserVisibilityColumn = fc;
						List<AssignmentInfo> aiList = new ArrayList<AssignmentInfo>();
						aiList.add(AssignmentInfo.construct(luvId, (luvIsUser ? AssigneeType.INDIVIDUAL : AssigneeType.GROUP)));
						fr.setColumnValue_AssignmentInfos(fc, aiList);
					}
					
					else if (FolderColumn.isColumnPrincipalType(cName)) {
						// The principal type column!  Generate value
						// for it.
						PrincipalType pt = GwtViewHelper.getPrincipalType(luvPrincipal);
						fr.setColumnValue(fc, new PrincipalAdminType(pt, false));	// false -> Don't show these as having admin rights.
					}
					
					else if (FolderColumn.isColumnCanOnlySeeMembers(cName)) {
						// The 'Can Only Seem Members of Group I'm In'
						// column!  Generate a value for it.
						boolean setLimited  = luvLimited.contains( luvId);
						boolean setOverride = luvOverride.contains(luvId);
						fr.setColumnValue(
							fc,
							new LimitedUserVisibilityInfo(
								setLimited,
								setOverride,
								luvId,
								getLimitedUserVisibilityDisplay(bs, request, setLimited, setOverride).getStringValue()));
					}
				}
			}

			// Did we find any limited user visibility items?
			if (!(luvRows.isEmpty())) {
				// Yes!  If we have an limited user visibility
				// column...
				if (null != limitedUserVisibilityColumn) {
					// ...complete the AssignmentInfo's for them.
					GwtAdministratorsHelper.completeAssignmentAIs(bs, request, luvList, luvRows, limitedUserVisibilityColumn);
				}
				
				// If we have a quick filter...
				if (MiscUtil.hasString(quickFilter)) {
					// ...apply it to the List<FolderRow>.
					luvRows = filterLimitedUserVisibilityRows(
						folderColumns,
						luvRows,
						quickFilter);
				}
				
				// Finally, sort the rows based on the user's criteria.
				Comparator<FolderRow> comparator =
					new FolderRowComparator(
						GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      FolderColumn.COLUMN_LIMITED_VISIBILITY_USER),
						GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false                                      ),
						folderColumns);
				
				Collections.sort(luvRows, comparator);
			}
				
			// Return a FolderRowsRpcResponseData containing the row
			// data.
			FolderRowsRpcResponseData reply =
				new FolderRowsRpcResponseData(
					luvRows,				// FolderRows.
					0,						// Start index.
					luvRows.size(),			// Total count.
					TotalCountType.EXACT,	// How the total count should be interpreted.
					new ArrayList<Long>());	// Contributor IDs.
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// requested binder.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				GwtViewHelper.dumpFolderRowsRpcResponseData(m_logger, binder, reply);
			}
			
			return reply;
			
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Fills in various lists of the Principal's that have limited user
	 * visibility rights set.
	 */
	@SuppressWarnings("unchecked")
	private static void getLimitedVisibilityUsers(AllModulesInjected bs, List<Principal> luvList, List<Long> luvLimited, List<Long> luvOverride) {
    	ZoneConfig zoneConfig = MiscUtil.getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
    	AdminModule am = bs.getAdminModule();
		List<WorkAreaFunctionMembership> wafmList = am.getWorkAreaFunctionMemberships(zoneConfig);
		List<Long> memberIds = new ArrayList<Long>();
		if (MiscUtil.hasItems(wafmList)) {
			// Yes!  Scan them.
			for (WorkAreaFunctionMembership wafm:  wafmList) {
				// Is this one of the limited user visibility roles?
				String fiId = am.getFunction(wafm.getFunctionId()).getInternalId();
				if (MiscUtil.hasString(fiId)) {
					boolean isLimited  = fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ONLY_SEE_GROUP_MEMBERS_INTERNALID         );
					boolean isOverride = fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_OVERRIDE_ONLY_SEE_GROUP_MEMBERS_INTERNALID);
					if (isLimited || isOverride) {
						// Yes!  Track the members.
						Collection<Long> wafmMemberIds = wafm.getMemberIds();
						                ListUtil.addCollectionLongToListLongIfUnique(memberIds,   wafmMemberIds);
						if (isLimited)  ListUtil.addCollectionLongToListLongIfUnique(luvLimited,  wafmMemberIds);
						if (isOverride) ListUtil.addCollectionLongToListLongIfUnique(luvOverride, wafmMemberIds);
					}
				}
			}
		}
		
		if (!(memberIds.isEmpty())) {
			List<Principal> pList = ResolveIds.getPrincipals(memberIds);
			if (null != pList) {
				luvList.addAll(pList);
			}
		}
	}

	/**
	 * Given the limited and override values, returns an appropriate
	 * localized display string.
	 * 
	 * @param bs
	 * @param request
	 * @param limited
	 * @param override
	 * 
	 * @return
	 */
	public static StringRpcResponseData getLimitedUserVisibilityDisplay(AllModulesInjected bs, HttpServletRequest request, boolean limited, boolean override) {
		String reply;
		if      (override) reply = NLT.get("administration.manage.limitUserVisibility_Override");
		else if (limited)  reply = NLT.get("administration.manage.limitUserVisibility_Limited" );
		else               reply = NLT.get("administration.manage.limitUserVisibility_None"    );
		return new StringRpcResponseData(reply);
	}
	
	/**
	 * Sets the 'Can Only See Members of Group I'm In' and
	 * corresponding override flags on the given principals.
	 * 
	 * @param bs
	 * @param request
	 * @param principalIds
	 * @param limited
	 * @param override
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static SetLimitedUserVisibilityRpcResponseData setUserVisibility(AllModulesInjected bs, HttpServletRequest request, List<Long> principalIds, Boolean limited, Boolean override) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtUserVisibilityHelper.setUserVisibility()");
		try {
			// If no flags need to be set...
			SetLimitedUserVisibilityRpcResponseData reply = new SetLimitedUserVisibilityRpcResponseData();
			Map<Long, LimitedUserVisibilityInfo> luvChangeMap = new HashMap<Long, LimitedUserVisibilityInfo>();
			reply.setLimitedUserVisibilityChangeMap(luvChangeMap);
			if ((null == limited) && (null == override)) {
				// ...bail.
				return reply;
			}

			// What are we setting and/or clearing?
			boolean    clearLimited  = ((null != limited)  && (!limited) );
			boolean    clearOverride = ((null != override) && (!override));
			boolean    setLimited    = ((null != limited)  &&   limited  );
			boolean    setOverride   = ((null != override) &&   override );
			boolean    setRights     = (setLimited || setOverride);
			
			// We're we given any Principal IDs to set or clear the
			// rights from?
			if (MiscUtil.hasItems(principalIds)) {
				// Yes!  What's the current user's ID?
				Long currentUserId = GwtServerHelper.getCurrentUserId();
				
				// Can we resolved the Principal IDs?
				List<Principal> pList = ResolveIds.getPrincipals(principalIds, false);	// false -> Don't check for active users.
				if (MiscUtil.hasItems(pList)) {
					// Yes!  Scan them.
					AdminModule am = bs.getAdminModule();
					List<Long> validPIDs = new ArrayList<Long>();
					for (Principal p:  pList) {
						// If this Principal has been deleted...
						if (p.isDeleted()) {
							// ...skip it.
							continue;
						}
						
						// Is this Principal a Group?
						String  errKey = null;
						String  pTitle = null;
						if (p instanceof GroupPrincipal) {
							// Yes!  Can we set its limited user
							// visibility rights?
							Group g = ((Group) p);
							pTitle = g.getTitle();
							if (g.isDisabled() && setRights) {
								// You can clear rights from a disabled
								// group, but not set them.
								errKey = "setLimitUserVisibilityRightsError.GroupDisabled";
							}
							else if (g.isLdapContainer() && setRights) {
								// You can't set limited user
								// visibility rights on an LDAP
								// container group.
								errKey = "setLimitUserVisibilityRightsError.GroupLdapContainer";
							}
						}
						
						// No, this Principal is not a Group!  Is it a
						// User?
						else if (p instanceof UserPrincipal) {
							// Yes!  Can we set its limited user
							// visibility rights?
							User u = ((User) p);
							pTitle = Utils.getUserTitle(u);
							if (u.isDisabled() && setRights) {
								// You can clear rights from a disabled
								// user, but not set them.
								errKey = "setLimitUserVisibilityRightsError.UserDisabled";
							}
							else if ((!(u.isPerson())) && setRights) {
								// You can't set limited user
								// visibility rights on built-in system
								// users.
								errKey = "setLimitUserVisibilityRightsError.NotAPerson";
							}
							else if (u.isAdmin()) {
								// The built-in admin account can't
								// have its limited user visibility
								// rights changed.
								if (setRights)
								     errKey = "setLimitUserVisibilityRightsError.UserAdmin.set";
								else errKey = "setLimitUserVisibilityRightsError.UserAdmin.clear";
							}
							else if (currentUserId.equals(u.getId())) {
								// A user cannot change their own
								// limited user visibility rights.
								errKey = "setLimitUserVisibilityRightsError.UserSelf";
							}
						}
						
						else {
							// No, it wasn't a Group either!  What ever
							// it was, we can't handle it.
							pTitle  = p.getTitle();
							errKey = "setLimitUserVisibilityRightsError.UnknownPrincipal";
						}
						
						// Is there a problem with setting limited user
						// visibility rights on this Principal?
						if (null != errKey) {
							// Yes!  Add the error to the reply and
							// skip it.
							reply.addError(NLT.get(errKey, new String[]{pTitle}));
							continue;
						}
						
						// If we get here, this Principal can have its
						// limited user visibility rights set!  Track
						// its ID.
						validPIDs.add(p.getId());
					}

					// Do we have any Principal IDs that we can set the
					// limited user visibility rights on?
					if (!(validPIDs.isEmpty())) {
						// Yes!  Can we find the limit user visibility
						// roles so we can grant or remove them?
						Long limitedRole  = MiscUtil.getCanOnlySeeMembersOfGroupsIAmInRoleId();
						Long overrideRole = MiscUtil.getOverrideCanOnlySeeMembersOfGroupsIAmInRoleId();
						if ((null == limitedRole) || (null == overrideRole)) {
							// No!  Tell the user about the problem.
							reply.addError(NLT.get("setLimitUserVisibilityRightsError.UnknownLimitUserVisibilityRole"));
							validPIDs.clear();
						}
						
						else {
							// Yes, we have the limited user visibility
							// roles so we can grant or remove them!
							// Set/clear it from the valid Principal
							// IDs...
					    	ZoneConfig zoneConfig = MiscUtil.getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
					    	if (setLimited  || clearLimited)  am.updateWorkAreaFunctionMemberships(zoneConfig, limitedRole,  setLimited, validPIDs );
					    	if (setOverride || clearOverride) am.updateWorkAreaFunctionMemberships(zoneConfig, overrideRole, setOverride, validPIDs);

					    	// ...and setup the luvChangeMap with the
					    	// ...new limited user visibility rights
					    	// ...for each item.
					    	for (Long id:  validPIDs) {
					    		for (Principal p:  pList) {
					    			if (id.equals(p.getId())) {
					    				luvChangeMap.put(
					    					id,
					    					new LimitedUserVisibilityInfo(
					    						setLimited,
					    						setOverride,
					    						id,
					    						getLimitedUserVisibilityDisplay(bs, request, setLimited, setOverride).getStringValue()));
					    				break;
					    			}
					    		}
					    	}
						}
					}
				}
			}
			
			
			// If we get here, reply contains a
			// SetLimitedUserVisibilityRpcResponseData containing any
			// error generated or what changed.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
}
