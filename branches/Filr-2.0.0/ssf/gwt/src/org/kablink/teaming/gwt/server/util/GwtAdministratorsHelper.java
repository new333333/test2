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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.util.PrincipalType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code in dealing with
 * administrators.
 *
 * @author drfoster@novell.com
 */
public class GwtAdministratorsHelper {
	protected static Log m_logger = LogFactory.getLog(GwtAdministratorsHelper.class);
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtAdministratorsHelper() {
		// Nothing to do.
	}
	
	/**
	 * When initially built, the AssignmentInfo's in the
	 * List<FolderRow>'s only contain the assignee ID and type.  We
	 * need to complete them with each assignee's title, ...
	 * 
	 * @param bs
	 * @param request
	 * @param assignmentList
	 * @param assignmentRows
	 * @param assignmentColumn
	 */
	public static void completeAssignmentAIs(AllModulesInjected bs, HttpServletRequest request, List<Principal> assignmentList, List<FolderRow> assignmentRows, FolderColumn assignmentColumn) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtAdministratorsHelper.completeAssignmentAIs()");
		try {
			// Allocate a List<Long> and track the administrators that
			// need to be completed.
			List<Long> principalIds = new ArrayList<Long>();
			for (Principal admin: assignmentList) {
				ListUtil.addLongToListLongIfUnique(principalIds, admin.getId());
			}
	
			// Construct Maps, mapping the IDs to their titles,
			// membership counts, ...
			Map<Long, String>			avatarUrls        = new HashMap<Long, String>();
			Map<Long, String>			principalEMAs     = new HashMap<Long, String>();
			Map<Long, String>			principalTitles   = new HashMap<Long, String>();
			Map<Long, Integer>			groupCounts       = new HashMap<Long, Integer>();
			Map<Long, GwtPresenceInfo>	userPresence      = new HashMap<Long, GwtPresenceInfo>();
			Map<Long, Boolean>			userExternal      = new HashMap<Long, Boolean>();
			Map<Long, Long>				presenceUserWSIds = new HashMap<Long, Long>();
			Map<Long, String>			teamTitles        = new HashMap<Long, String>();
			Map<Long, Integer>			teamCounts        = new HashMap<Long, Integer>();
			GwtEventHelper.readEventStuffFromDB(
				// Uses these...
				bs,
				request,
				principalIds,
				new ArrayList<Long>(),	// teamIds -> Unused.
	
				// ...to complete these.
				principalEMAs,
				principalTitles,
				groupCounts,
				userPresence,
				userExternal,
				presenceUserWSIds,
				
				teamTitles,
				teamCounts,
				
				avatarUrls);
	
			// Scan the List<FolderRow>'s...
			for (FolderRow adminRow:  assignmentRows) {
				// ...completing the information in each one's
				// ...AssignmentInfo.
				List<AssignmentInfo> aiList = adminRow.getColumnValueAsAssignmentInfos(assignmentColumn);
				AssignmentInfo ai = aiList.get(0);	// Will only ever be one.
				switch (ai.getAssigneeType()) {
				case INDIVIDUAL:
					if (GwtEventHelper.setAssignmentInfoTitle(           ai, principalTitles )) {
						GwtEventHelper.setAssignmentInfoPresence(        ai, userPresence     );
						GwtEventHelper.setAssignmentInfoExternal(        ai, userExternal     );
						GwtEventHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
						GwtEventHelper.setAssignmentInfoAvatarUrl(       ai, avatarUrls       );
						GwtEventHelper.setAssignmentInfoHover(           ai, principalTitles  );
					}
					break;
					
				case GROUP:
					if (GwtEventHelper.setAssignmentInfoTitle(  ai, principalTitles)) {
						GwtEventHelper.setAssignmentInfoMembers(ai, groupCounts     );
						GwtEventHelper.setAssignmentInfoHover(  ai, principalTitles );
						ai.setPresenceDude("pics/group_20.png");
					}
					break;
				}
				
			}
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Applies a quick filter to a List<FolderRow> of 'Administrators'
	 * rows.  A List<FolderRow> of the the FolderRow's from the input
	 * list that matches the filter is returned.
	 */
	public static List<FolderRow> filterAdminRows(List<FolderColumn> folderColumns, List<FolderRow> adminRows, String quickFilter) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtAdministratorsHelper.filterAdminRows()");
		try {
			// Do we have a string to filter with and some FolderRow's
			// to be filtered?
			if (null != quickFilter) {
				quickFilter = quickFilter.trim().toLowerCase();
			}
			if (MiscUtil.hasString(quickFilter) && MiscUtil.hasItems(adminRows)) {
				// Yes!  Yes!  Scan the rows.
				List<FolderRow> reply = new ArrayList<FolderRow>();
				for (FolderRow fr:  adminRows) {
					// Scan the columns.
					for (FolderColumn fc:  folderColumns) {
						// What column is this?
						String cName = fc.getColumnName();
						if (FolderColumn.isColumnAdministrator(cName)) {
							// An administrator column!  If this value
							// contains the quick filter...
							List<AssignmentInfo> aiList = fr.getColumnValueAsAssignmentInfos(fc);
							AssignmentInfo ai =  aiList.get(0);	// Will only ever be one.
							if (GwtViewHelper.valueContainsQuickFilter(ai.getTitle(), quickFilter)) {
								// ...add the row to the reply list.
								reply.add(fr);
								break;
							}
						}
						
						else if (FolderColumn.isColumnEmailAddress(cName)) {
							// An email address column!  If the filter
							// is in the email address...
							EmailAddressInfo eai = fr.getColumnValueAsEmailAddress(fc);
							if ((null != eai) && GwtViewHelper.valueContainsQuickFilter(eai.getEmailAddress(), quickFilter)) {
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
				adminRows = reply;
			}
			
			// If we get here, filterRows refers to the filtered list
			// of rows.  Return it. 
			return adminRows;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns the rows for the given mobile device set.
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
	public static FolderRowsRpcResponseData getAdministratorsRows(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, BinderInfo bi, List<FolderColumn> folderColumns) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtAdministratorsHelper.getAdministratorsRows()");
		try {
			// Scan the user administrators list.
			List<FolderRow> adminRows = new ArrayList<FolderRow>();
			List<Principal> adminList = getSiteAdminMembers(bs);
			FolderColumn administratorColumn = null;
			for (Principal adminPrincipal:  adminList) {
				boolean adminIsGroup =                     (adminPrincipal instanceof GroupPrincipal);
				boolean adminIsUser  = ((!adminIsGroup) && (adminPrincipal instanceof UserPrincipal));
				if ((!adminIsGroup) && (!adminIsUser)) {
					continue;
				}
				User adminUser = (adminIsUser ? ((User) adminPrincipal) : null);
				
				// Create the FolderRow for this administrator and add
				// it to the list.
				Long      adminId = adminPrincipal.getId();
				EntityId  eid     = new EntityId(binder.getId(), adminId, (adminIsGroup ? EntityId.GROUP : EntityId.USER));
				FolderRow fr      = new FolderRow(eid, folderColumns);
				adminRows.add(fr);

				// Scan the columns.
				for (FolderColumn fc:  folderColumns) {
					// What column is this?
					String cName = fc.getColumnName();
					if (FolderColumn.isColumnAdministrator(cName)) {
						// The Administrator column!  Generate an
						// AssignmentInfo for it.
						administratorColumn = fc;
						List<AssignmentInfo> aiList = new ArrayList<AssignmentInfo>();
						aiList.add(AssignmentInfo.construct(adminId, (adminIsUser ? AssigneeType.INDIVIDUAL : AssigneeType.GROUP)));
						fr.setColumnValue_AssignmentInfos(fc, aiList);
					}
					
					else if (FolderColumn.isColumnPrincipalType(cName)) {
						// The principal type column!  Generate value
						// for it.
						PrincipalType pt = GwtViewHelper.getPrincipalType(adminPrincipal);
						fr.setColumnValue(fc, new PrincipalAdminType(pt, true));	// true -> Show these as having admin rights.
					}
					
					else if (FolderColumn.isColumnAdminRights(cName)) {
						// The Admin rights column!  Generate a value
						// for it.
						fr.setColumnValue(fc, GwtViewHelper.getPrincipalAdminRightsString(bs, adminPrincipal));
					}
					
					else if (FolderColumn.isColumnEmailAddress(cName)) {
						// The Email address Column!  (Which only
						// applies to users.)
						if (adminIsUser) {
							EmailAddressInfo emai = GwtServerHelper.getEmailAddressInfoFromUser(adminUser);
							fr.setColumnValue(fc, emai);
						}
					}
					
					else if (FolderColumn.isColumnLoginId(cName)) {
						// The Login ID column!  (Which only applies to
						// users.)
						String loginId = (adminIsUser ? adminUser.getName() : null);
						if (null == loginId) {
							loginId = " ";
						}
						fr.setColumnValue(fc, loginId);
					}
				}
			}

			// Did we find any administrators?
			if (!(adminRows.isEmpty())) {
				// Yes!  If we have an administrator column...
				if (null != administratorColumn) {
					// ...complete the AssignmentInfo's for them.
					completeAssignmentAIs(bs, request, adminList, adminRows, administratorColumn);
				}
				
				// If we have a quick filter...
				if (MiscUtil.hasString(quickFilter)) {
					// ...apply it to the List<FolderRow>.
					adminRows = filterAdminRows(
						folderColumns,
						adminRows,
						quickFilter);
				}
				
				// Finally, sort the rows based on the user's criteria.
				Comparator<FolderRow> comparator =
					new FolderRowComparator(
						GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      FolderColumn.COLUMN_ADMIN_RIGHTS),
						GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false                           ),
						folderColumns);
				
				Collections.sort(adminRows, comparator);
			}
				
			// Return a FolderRowsRpcResponseData containing the row
			// data.
			FolderRowsRpcResponseData reply =
				new FolderRowsRpcResponseData(
					adminRows,				// FolderRows.
					0,						// Start index.
					adminRows.size(),		// Total count.
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
	 * Returns the ID's of the Principal's that have been granted site
	 * administrator rights.
	 */
	@SuppressWarnings("unchecked")
	private static List<Principal> getSiteAdminMembers(AllModulesInjected bs) {
    	ZoneConfig zoneConfig = MiscUtil.getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
    	AdminModule am = bs.getAdminModule();
		List<WorkAreaFunctionMembership> wafmList = am.getWorkAreaFunctionMemberships(zoneConfig);
		Set<Long> memberIds = null;
		if (MiscUtil.hasItems(wafmList)) {
			// Yes!  Scan them.
			for (WorkAreaFunctionMembership wafm:  wafmList) {
				// Is this the site admin role?
				String fiId = am.getFunction(wafm.getFunctionId()).getInternalId();
				if (MiscUtil.hasString(fiId) && fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_SITE_ADMIN_INTERNALID)) {
					// Yes!  Is the given member a member of it?
					memberIds = wafm.getMemberIds();
					break;
				}
			}
		}
		
		List<Principal> pList = ((null == memberIds) ? new ArrayList<Principal>() : ResolveIds.getPrincipals(memberIds));
		User builtInAdmin = BuiltInUsersHelper.getZoneSuperUser();
		if ((null != builtInAdmin) && ((null == memberIds) || (!(memberIds.contains(builtInAdmin.getId()))))) {
			pList.add(0, builtInAdmin);
		}
		
		return pList;
	}
}
