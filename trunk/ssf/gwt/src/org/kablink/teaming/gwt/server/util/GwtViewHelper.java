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

package org.kablink.teaming.gwt.server.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.portlet.PortletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.rpc.shared.AvatarInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BinderDescriptionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ColumnWidthsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData.EntryType;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.CalendarDayView;
import org.kablink.teaming.gwt.client.util.CalendarHours;
import org.kablink.teaming.gwt.client.util.CalendarShow;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntryId;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.ViewType;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.module.folder.FilesLockedByOtherUsersException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.RenderRequestImpl;
import org.kablink.teaming.portletadapter.portlet.RenderResponseImpl;
import org.kablink.teaming.portletadapter.support.AdaptedPortlets;
import org.kablink.teaming.portletadapter.support.KeyNames;
import org.kablink.teaming.portletadapter.support.PortletInfo;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.task.TaskHelper.FilterType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DateComparer;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.teaming.web.util.TrashHelper.TrashEntry;
import org.kablink.teaming.web.util.TrashHelper.TrashResponse;
import org.kablink.util.search.Constants;

/**
 * Helper methods for the GWT binder views.
 *
 * @author drfoster@novell.com
 */
public class GwtViewHelper {
	protected static Log m_logger = LogFactory.getLog(GwtViewHelper.class);

	// Attribute names used for items related to milestones.
	public static final String RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME	= "responsible_groups";
	public static final String RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME			= "responsible";
	public static final String RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME		= "responsible_teams";
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtViewHelper() {
		// Nothing to do.
	}

	/*
	 * If there's an attribute value, constructs a ProfileAttribute for
	 * it and adds it to a ProfileEntryInfoRpcResponseData object.
	 */
	private static void addProfileAttribute(ProfileEntryInfoRpcResponseData paData, String attributeName, String attributeValue) {
		if (MiscUtil.hasString(attributeValue)) {
			paData.addProfileAttribute(attributeName, new ProfileEntryInfoRpcResponseData.ProfileAttribute(NLT.get("__" + attributeName), attributeValue));
		}
	}

	/*
	 * Extracts the ID's of the entry contributors and adds them to the
	 * contributor ID's list if they're not already there. 
	 */
	@SuppressWarnings("unchecked")
	private static void collectContributorIds(Map entryMap, List<Long> contributorIds) {
		MiscUtil.addLongToListLongIfUnique(contributorIds, getLongFromMap(entryMap, Constants.CREATORID_FIELD)     );
		MiscUtil.addLongToListLongIfUnique(contributorIds, getLongFromMap(entryMap, Constants.MODIFICATIONID_FIELD));
	}
	
	/*
	 * When initially built, the AssignmentInfo's in the
	 * List<AssignmentInfo>'s only contain the assignee IDs.  We need
	 * to complete them with each assignee's title, ...
	 */
	@SuppressWarnings("unchecked")
	private static void completeAIs(AllModulesInjected bs, List<FolderRow> folderRows) {
		// If we don't have any FolderRows's to complete...
		if ((null == folderRows) || folderRows.isEmpty()) {
			// ..bail.
			return;
		}

		// Allocate List<Long>'s to track the assignees that need to be
		// completed.
		List<Long> principalIds = new ArrayList<Long>();
		List<Long> teamIds      = new ArrayList<Long>();

		// Scan the List<FolderRow>'s.
		for (FolderRow fr:  folderRows) {
			// Scan this FolderRow's individual assignees tracking each
			// unique ID.
			for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(((AssigneeType.TEAM == ai.getAssigneeType()) ? teamIds : principalIds), ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(((AssigneeType.TEAM == ai.getAssigneeType()) ? teamIds : principalIds), ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(((AssigneeType.TEAM == ai.getAssigneeType()) ? teamIds : principalIds), ai.getId());
			}
			
			// Scan this FolderRow's group assignees tracking each
			// unique ID.
			for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			
			// Scan this FolderRow's team assignees tracking each
			// unique ID.
			for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(teamIds, ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(teamIds, ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(teamIds, ai.getId());
			}
		}

		// If we don't have any assignees to complete...
		boolean hasPrincipals = (!(principalIds.isEmpty()));
		boolean hasTeams      = (!(teamIds.isEmpty()));		
		if ((!hasPrincipals) && (!hasTeams)) {
			// ...bail.
			return;
		}

		// Construct Maps, mapping the principal IDs to their titles
		// and membership counts.
		Map<Long, String>          principalTitles   = new HashMap<Long, String>();
		Map<Long, Integer>         groupCounts       = new HashMap<Long, Integer>();
		Map<Long, GwtPresenceInfo> userPresence      = new HashMap<Long, GwtPresenceInfo>();
		Map<Long, Long>            presenceUserWSIds = new HashMap<Long, Long>();
		boolean                    isPresenceEnabled = GwtServerHelper.isPresenceEnabled();
		if (hasPrincipals) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds, false);}
			catch (Exception ex) {/* Ignored. */}
			if ((null != principals) && (!(principals.isEmpty()))) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					Long pId = p.getId();
					boolean isUser = (p instanceof UserPrincipal);
					principalTitles.put(pId, p.getTitle());					
					if (p instanceof GroupPrincipal) {
						groupCounts.put(pId, GwtServerHelper.getGroupCount((GroupPrincipal) p));						
					}
					else if (isUser) {
						User user = ((User) p);
						presenceUserWSIds.put(pId, user.getWorkspaceId());
						if (isPresenceEnabled) {
							userPresence.put(pId, GwtServerHelper.getPresenceInfo(user));
						}
					}
				}
			}
		}
		
		// Construct Maps, mapping the team IDs to their titles and
		// membership counts.
		Map<Long, String>  teamTitles = new HashMap<Long, String>();
		Map<Long, Integer> teamCounts = new HashMap<Long, Integer>();
		if (hasTeams) {
			SortedSet<Binder> binders = null;
			try {binders = bs.getBinderModule().getBinders(teamIds);}
			catch (Exception ex) {/* Ignored. */}
			if ((null != binders) && (!(binders.isEmpty()))) {
				for (Binder b:  binders) {
					Long bId = b.getId();
					teamTitles.put(bId, b.getTitle());
					teamCounts.put(bId, GwtServerHelper.getTeamCount(bs, b));
				}
			}
		}
		
		// Scan the List<FolderRow>'s again...
		for (FolderRow fr:  folderRows) {
			// ...this time, fixing the assignee lists.
			fixupAIs(     getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             principalTitles, userPresence, presenceUserWSIds);
			fixupAIGroups(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             principalTitles, groupCounts                    );
			fixupAITeams( getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             teamTitles,      teamCounts                     );
			fixupAIs(     getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        principalTitles, userPresence, presenceUserWSIds);
			fixupAIGroups(getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        principalTitles, groupCounts                    );
			fixupAITeams( getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        teamTitles,      teamCounts                     );
			fixupAIs(     getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  principalTitles, userPresence, presenceUserWSIds);
			fixupAIGroups(getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  principalTitles, groupCounts                    );
			fixupAITeams( getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  teamTitles,      teamCounts                     );
			
			fixupAIGroups(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME),      principalTitles, groupCounts                    );
			fixupAIGroups(getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME), principalTitles, groupCounts                    );
			fixupAIGroups(getAIListFromFR(fr, RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME),           principalTitles, groupCounts                    );
			
			fixupAITeams( getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME),       teamTitles,      teamCounts                     );
			fixupAITeams( getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME),  teamTitles,      teamCounts                     );
			fixupAITeams( getAIListFromFR(fr, RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME),            teamTitles,      teamCounts                     );
		}		

		// Finally, one last scan through the List<FolderRow>'s...
		Comparator<AssignmentInfo> comparator = new GwtServerHelper.AssignmentInfoComparator(true);
		for (FolderRow fr:  folderRows) {
			// ...this time, to sort the assignee lists.
			Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             comparator);
			Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        comparator);
			Collections.sort(getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  comparator);
			
			Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME),      comparator);
			Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME), comparator);
			Collections.sort(getAIListFromFR(fr, RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME),           comparator);
			
			Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME),       comparator);
			Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME),  comparator);
			Collections.sort(getAIListFromFR(fr, RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME),            comparator);
		}
	}
	
	/**
	 * Change the entry types for a collection of entries.
	 *
	 * @param bs
	 * @param request
	 * @param defId
	 * @param entryIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData changeEntryTypes(AllModulesInjected bs, HttpServletRequest request, String defId, List<EntryId> entryIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());
			
			// Were we given the IDs of any entries to change their
			// entry types and the entry type to change them to?
			if ((null != entryIds) && (!(entryIds.isEmpty())) && MiscUtil.hasString(defId)) {
				// Yes!  Scan them.
				FolderModule fm = bs.getFolderModule();
				for (EntryId entryId:  entryIds) {
					try {
						// Can we change this entry's entry type?
						fm.changeEntryType(entryId.getEntryId(), defId);
					}
					catch (Exception e) {
						// No!  Add an error  to the error list.
						String entryTitle = GwtServerHelper.getEntryTitle(bs, entryId.getBinderId(), entryId.getEntryId());
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "changeEntryTypeError.AccssControlException";
						else                                     messageKey = "changeEntryTypeError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.changeEntryTypes( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Copies the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param targetFolderId
	 * @param entryIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData copyEntries(AllModulesInjected bs, HttpServletRequest request, Long targetFolderId, List<EntryId> entryIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any entries to copy?
			if ((null != entryIds) && (!(entryIds.isEmpty()))) {
				// Yes!  Scan them.
				for (EntryId entryId:  entryIds) {
					try {
						// Can we copy this entry?
						bs.getFolderModule().copyEntry(entryId.getBinderId(), entryId.getEntryId(), targetFolderId, null, null);
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String entryTitle = GwtServerHelper.getEntryTitle(bs, entryId.getBinderId(), entryId.getEntryId());
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "copyEntryError.AccssControlException";
						else                                     messageKey = "copyEntryError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.copyEntries( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Delete user workspaces.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData deleteUserWorkspaces(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any users to delete?
			Long currentUserId = GwtServerHelper.getCurrentUser().getId(); 
			if ((null != userIds) && (!(userIds.isEmpty()))) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being delete?
					User user = getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't delete their own
							// workspace.  Ignore it.
							reply.addError(NLT.get("deleteUserWorkspaceError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
							reply.addError(NLT.get("deleteUserWorkspaceError.reserved", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Does this user have a workspace ID?
						Long wsId = user.getWorkspaceId();
						if (null != wsId) {
							// Yes!  Delete the workspace.
							TrashHelper.preDeleteBinder(bs, wsId);
						}
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "deleteUserWorkspaceError.AccssControlException";
						else                                          messageKey = "deleteUserWorkspaceError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.deleteUserWorkspaces( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Disables the users.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData disableUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any users to disable?
			Long currentUserId = GwtServerHelper.getCurrentUser().getId(); 
			if ((null != userIds) && (!(userIds.isEmpty()))) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being disabled?
					User user = getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't disable themselves.
							// Ignore it.
							reply.addError(NLT.get("disableUserError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
							reply.addError(NLT.get("disableUserError.reserved", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Can we disable this user?
						bs.getProfileModule().disableEntry(userId, true);	// true -> Disable.
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "disableUserError.AccssControlException";
						else                                          messageKey = "disableUserError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.disableUsers( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Dumps the contents of a ViewInfo object.
	 */
	private static void dumpViewInfo(ViewInfo vi) {
		// If debug tracing isn't enabled...
		if (!(m_logger.isDebugEnabled())) {
			// ...bail.
			return;
		}

		// If we weren't given a ViewInfo to dump...
		if (null == vi) {
			// ...trace that fact and bail.
			m_logger.debug("...dumpViewInfo( null ):  No ViewInfo to dump.");
			return;
		}
		
		ViewType vt = vi.getViewType();
		m_logger.debug("...dumpViewInfo( " + vt.name() + " )");
		switch (vt) {
		case BINDER:
			BinderInfo bi = vi.getBinderInfo();
			BinderType bt = bi.getBinderType();
			m_logger.debug(".....dumpViewInfo( BINDER ):  " + bt.name());
			switch (bt) {
			case FOLDER:
				m_logger.debug("........dumpViewInfo( BINDER:FOLDER     ):  " + bi.getFolderType().name());
				break;
				
			case WORKSPACE:
				m_logger.debug("........dumpViewInfo( BINDER:WORKSPACE  ):  " + bi.getWorkspaceType().name());
				break;
			
			case OTHER:
				m_logger.debug("........dumpViewInfo( BINDER:OTHER      )");
				break;
				
			default:
				m_logger.debug(".........dumpViewInfo( BINDER:Not Handled ):  This BinderType is not implemented by the dumper.");
				break;
			}
			
			m_logger.debug("........dumpViewInfo( BINDER:Id         ):  " + bi.getBinderId());
			m_logger.debug("........dumpViewInfo( BINDER:Title      ):  " + bi.getBinderTitle());
			m_logger.debug("........dumpViewInfo( BINDER:EntityType ):  " + bi.getEntityType());
			
			break;
			
		case ADD_BINDER:
		case ADD_FOLDER_ENTRY:
		case ADD_PROFILE_ENTRY:
		case ADVANCED_SEARCH:
		case BUILD_FILTER:
		case VIEW_PROFILE_ENTRY:
		case OTHER:
			break;
			
		default:
			m_logger.debug("......dumpViewInfo( Not Handled ):  This ViewType is not implemented by the dumper.");
			break;
		}
	}

	/**
	 * Enables the users.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData enableUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any users to enable?
			if ((null != userIds) && (!(userIds.isEmpty()))) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					try {
						// Can we enable this user?
						bs.getProfileModule().disableEntry(userId, false);	// false -> Enable.
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						User   user      = getResolvedUser(userId);
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "enableUserError.AccssControlException";
						else                                          messageKey = "enableUserError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.enableUsers( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Checks for assignment column search key in the folder columns.
	 * If there's a column for it, simply returns.  If there's not a
	 * column for it, checks the entry map for assignments and if any
	 * are found, adds them to the assignment list.
	 */
	@SuppressWarnings("unchecked")
	private static void factorInAssignments(Map entryMap, List<FolderColumn> folderColumns, String csk, AssigneeType assigneeType, List<AssignmentInfo> assignmentList) {
		// Scan the columns.
		for (FolderColumn fc:  folderColumns) {
			// Is this column for the given search key?
			if (fc.getColumnSearchKey().equals(csk)) {
				// Yes!  Then we don't handle it's assignments
				// separately.  Bail.
				return;
			}
		}
		
		// Are there any assignments for the given column search key?
		List<AssignmentInfo> addList = GwtServerHelper.getAssignmentInfoListFromEntryMap(entryMap, csk, assigneeType);
		if ((null != addList) && (!(addList.isEmpty()))) {
			// Yes!  Copy them into the assignment list we were given.
			for (AssignmentInfo ai:  addList) {
				assignmentList.add(ai);
			}
		}
	}
	
	/*
	 * Checks for the group assignment corresponding to the individual
	 * column search key in the folder columns.  If there's a column
	 * for it, simply returns.  If there's not a column for it, checks
	 * the entry map for assignments and if any are found, adds them to
	 * the assignment list.
	 */
	@SuppressWarnings("unchecked")
	private static void factorInGroupAssignments(Map entryMap, List<FolderColumn> folderColumns, String csk, List<AssignmentInfo> assignmentList) {
		// Can we determine the group assignment attribute?
		String groupCSK;
		if (csk.equals(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME))           groupCSK = TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) groupCSK = EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME))           groupCSK = RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME;
		else                                                                       groupCSK = null;
		if (null != groupCSK) {
			// Yes!  Factor in any group assignments using it.
			factorInAssignments(entryMap, folderColumns, groupCSK, AssigneeType.GROUP, assignmentList);
		}
	}
	
	/*
	 * Checks for the team assignment corresponding to the individual
	 * column search key in the folder columns.  If there's a column
	 * for it, simply returns.  If there's not a column for it, checks
	 * the entry map for assignments and if any are found, adds them to
	 * the assignment list.
	 */
	@SuppressWarnings("unchecked")
	private static void factorInTeamAssignments(Map entryMap, List<FolderColumn> folderColumns, String csk, List<AssignmentInfo> assignmentList) {
		// Can we determine the team assignment attribute?
		String teamCSK;
		if (csk.equals(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME))           teamCSK = TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) teamCSK = EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME))           teamCSK = RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME;
		else                                                                       teamCSK = null;
		if (null != teamCSK) {
			// Yes!  Factor in any team assignments using it.
			factorInAssignments(entryMap, folderColumns, teamCSK, AssigneeType.TEAM, assignmentList);
		}
	}
	
	/*
	 * Returns the days duration value if only days were found. 
	 * Otherwise returns -1.
	 */
	@SuppressWarnings("unchecked")
	private static int getDurDaysFromEntryMap(String colName, Map entryMap) {
		int days = (-1);
		int seconds = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#S")), (-1));
		int minutes = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#M")),  (-1));
		int hours   = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#H")),  (-1));
		int weeks   = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#W")),  (-1));
		if ((0 >= seconds) && (0 >= minutes) && (0 >= hours) && (0 >= weeks)) {
			days = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#D")),  (-1));
		}		
		return days;
	}
	
	/*
	 * Fixes up the group assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAIGroups(List<AssignmentInfo> aiGroupsList, Map<Long, String> principalTitles, Map<Long, Integer> groupCounts) {
		// If don't have a list to fixup...
		if ((null == aiGroupsList) || aiGroupsList.isEmpty()) {
			// ...bail.
			return;
		}
		
		// The removeList is used to handle cases where an ID could
		// not be resolved (e.g., an 'Assigned To' group has been
		// deleted.)
		List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
		
		// Scan this AssignmentInfo's group assignees...
		for (AssignmentInfo ai:  aiGroupsList) {
			// ...skipping those that aren't really groups...
			if (AssigneeType.GROUP != ai.getAssigneeType()) {
				continue;
			}
			
			// ...and setting each one's title and membership count.
			if (GwtServerHelper.setAssignmentInfoTitle(  ai, principalTitles)) {
				GwtServerHelper.setAssignmentInfoMembers(ai, groupCounts     );
				ai.setPresenceDude("pics/group_icon_small.png");
			}
			else {
				removeList.add(ai);
			}
		}
		GwtServerHelper.removeUnresolvedAssignees(aiGroupsList, removeList);
	}
	
	/*
	 * Fixes up the team assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAITeams(List<AssignmentInfo> aiTeamsList, Map<Long, String> teamTitles, Map<Long, Integer> teamCounts) {
		// If don't have a list to fixup...
		if ((null == aiTeamsList) || aiTeamsList.isEmpty()) {
			// ...bail.
			return;
		}
		
		// The removeList is used to handle cases where an ID could
		// not be resolved (e.g., an 'Assigned To' team has been
		// deleted.)
		List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
		
		// Scan this AssignmentInfo's team assignees...
		for (AssignmentInfo ai:  aiTeamsList) {
			// ...skipping those that aren't really teams...
			if (AssigneeType.TEAM != ai.getAssigneeType()) {
				continue;
			}
			
			// ...and setting each one's title and membership count.
			if (GwtServerHelper.setAssignmentInfoTitle(  ai, teamTitles)) {
				GwtServerHelper.setAssignmentInfoMembers(ai, teamCounts );
				ai.setPresenceDude("pics/team_16.png");
			}
			else {
				removeList.add(ai);
			}
		}
		GwtServerHelper.removeUnresolvedAssignees(aiTeamsList, removeList);
	}
	
	/*
	 * Fixes up the individual assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAIs(List<AssignmentInfo> aiList, Map<Long, String> principalTitles, Map<Long, GwtPresenceInfo> userPresence, Map<Long, Long> presenceUserWSIds) {
		// If don't have a list to fixup...
		if ((null == aiList) || aiList.isEmpty()) {
			// ...bail.
			return;
		}
		
		// The removeList is used to handle cases where an ID could
		// not be resolved (e.g., an 'Assigned To' user has been
		// deleted.)
		List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
		
		// Scan this AssignmentInfo's individual assignees...
		for (AssignmentInfo ai:  aiList) {
			// ...skipping those that aren't really individuals...
			if (AssigneeType.INDIVIDUAL != ai.getAssigneeType()) {
				continue;
			}
			
			// ...and setting each one's title.
			if (GwtServerHelper.setAssignmentInfoTitle(           ai, principalTitles )) {
				GwtServerHelper.setAssignmentInfoPresence(        ai, userPresence     );
				GwtServerHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
			}
			else {
				removeList.add(ai);
			}
		}
		GwtServerHelper.removeUnresolvedAssignees(aiList, removeList);
	}
	
	/*
	 * Walks the List<FolderColumn>'s setting the search and sort keys
	 * appropriately for each.
	 * 
	 * Note:  The algorithm used in this method for columns whose names
	 *    contain multiple parts was reverse engineered from that used
	 *    by folder_view_common2.jsp or view_trash.jsp.
	 */
	private static void fixupFCs(List<FolderColumn> fcList, boolean isTrash) {
		for (FolderColumn fc:  fcList) {
			String colName = fc.getColumnName();
			if      (colName.equals("author"))          {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);              fc.setColumnSortKey(Constants.CREATOR_TITLE_FIELD); }
			else if (colName.equals("comments"))        {fc.setColumnSearchKey(Constants.TOTALREPLYCOUNT_FIELD);                                                            }
			else if (colName.equals("date"))            {fc.setColumnSearchKey(Constants.LASTACTIVITY_FIELD);                                                               }
			else if (colName.equals("description"))     {fc.setColumnSearchKey(Constants.DESC_FIELD);                                                                       }
			else if (colName.equals("descriptionHtml")) {fc.setColumnSearchKey(Constants.DESC_FIELD);                                                                       }
			else if (colName.equals("download"))        {fc.setColumnSearchKey(Constants.FILENAME_FIELD);                                                                   }
			else if (colName.equals("dueDate"))         {fc.setColumnSearchKey(Constants.DUE_DATE_FIELD);                                                                   }
			else if (colName.equals("emailAddress"))    {fc.setColumnSearchKey(Constants.EMAIL_FIELD);                                                                      }
			else if (colName.equals("fullName"))        {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);              fc.setColumnSortKey(Constants.SORT_TITLE_FIELD);    }
			else if (colName.equals("guest"))           {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);              fc.setColumnSortKey(Constants.CREATOR_TITLE_FIELD); }
			else if (colName.equals("html"))            {fc.setColumnSearchKey(Constants.FILE_ID_FIELD);                                                                    }
			else if (colName.equals("location"))        {fc.setColumnSearchKey(Constants.PRE_DELETED_FIELD);                                                                }
			else if (colName.equals("loginId"))         {fc.setColumnSearchKey(Constants.LOGINNAME_FIELD);                                                                  }
			else if (colName.equals("number"))          {fc.setColumnSearchKey(Constants.DOCNUMBER_FIELD);              fc.setColumnSortKey(Constants.SORTNUMBER_FIELD);    }
			else if (colName.equals("rating"))          {fc.setColumnSearchKey(Constants.RATING_FIELD);                                                                     }
			else if (colName.equals("responsible"))     {fc.setColumnSearchKey(Constants.RESPONSIBLE_FIELD);                                                                }
			else if (colName.equals("size"))            {fc.setColumnSearchKey(Constants.FILE_SIZE_FIELD);                                                                  }
			else if (colName.equals("state"))           {fc.setColumnSearchKey(Constants.WORKFLOW_STATE_CAPTION_FIELD); fc.setColumnSortKey(Constants.WORKFLOW_STATE_FIELD);}
			else if (colName.equals("status"))          {fc.setColumnSearchKey(Constants.STATUS_FIELD);                                                                     }
			else if (colName.equals("tasks"))           {fc.setColumnSearchKey(Constants.TASKS_FIELD);                                                                      }
			else if (colName.equals("title"))           {fc.setColumnSearchKey(Constants.TITLE_FIELD);                  fc.setColumnSortKey(Constants.SORT_TITLE_FIELD);    }
			else {
				// Does the column name contain multiple parts wrapped
				// in a single value?
				String defId      = null;
				String eleType    = null;
				String eleName    = null;
				String eleCaption = null;
				if (colName.contains(",")) {
					String[] temp = colName.split(",");
					if (4 <= temp.length) {
						defId   = temp[0];
						eleType = temp[1];
						eleName = temp[2];
						
						// Since the caption may have commas in it, we
						// need to get everything past the third comma.
						eleCaption = colName.substring(colName.indexOf(",")+1);
						eleCaption = eleCaption.substring(eleCaption.indexOf(",")+1);
						eleCaption = eleCaption.substring(eleCaption.indexOf(",")+1);
					}
				}
				if (MiscUtil.hasString(defId)) {
					// Yes!  Update the FolderColumn components based
					// on the information extracted from the field.
					fc.setColumnDefId(defId  );
					fc.setColumnType( eleType);
					fc.setColumnEleName( eleName);
					
					if (!(MiscUtil.hasString(fc.getColumnTitle()))) {
						fc.setColumnTitle(eleCaption);
					}
					
					String eleSortName;
					if      (eleType.equals("selectbox") || eleType.equals("radio"))  eleSortName = ("_caption_" + eleName);
					else if (eleType.equals("text")      || eleType.equals("hidden")) eleSortName = ("_sort_"    + eleName);
					else if (eleType.equals("event"))                                 eleSortName = (eleName + "#LogicalStartDate");
					else                                                              eleSortName = eleName;
					fc.setColumnSortKey(eleSortName);
				} 
				
				else {
					// No, the name doesn't have multiple parts wrapped
					// in a single value!  Just use the name for the
					// search and sort keys.
					fc.setColumnSearchKey(colName);
					fc.setColumnEleName(  colName);	//If not a custom attribute, the element name is the colName
				}
			}

			// If we're dealing with a trash view...
			if (isTrash) {
				// ...some of the fields are managed using a different search key. 
				if      (colName.equals("author"))   {fc.setColumnSearchKey(Constants.PRE_DELETED_BY_ID_FIELD); fc.setColumnSortKey("");}
				else if (colName.equals("date"))     {fc.setColumnSearchKey(Constants.PRE_DELETED_WHEN_FIELD);  fc.setColumnSortKey("");}
				else if (colName.equals("location")) {fc.setColumnSearchKey(Constants.PRE_DELETED_FROM_FIELD);  fc.setColumnSortKey("");}
			}
		}
	}
	
	/*
	 * Scans the List<FolderRow> and sets the access rights for the
	 * current user for each row.
	 */
	private static void fixupFRs(AllModulesInjected bs, List<FolderRow> frList) {
		// If we don't have any FolderRow's to complete...
		if ((null == frList) || frList.isEmpty()) {
			// ..bail.
			return;
		}

		// Collect the entry IDs of the rows from the List<FolderRow>.
		List<Long> entryIds = new ArrayList<Long>();
		for (FolderRow fr:  frList) {
			entryIds.add(fr.getEntryId().getEntryId());
		}
		
		try {
			// Read the FolderEntry's for the rows...
			FolderModule fm = bs.getFolderModule();
			SortedSet<FolderEntry> entries = fm.getEntries(entryIds);
			
			// ...mapping each FolderEntry to its ID.
			Map<Long, FolderEntry> entryMap = new HashMap<Long, FolderEntry>();
			for (FolderEntry entry: entries) {
				entryMap.put(entry.getId(), entry);
			}

			// Scan the List<FolderRow> again.
			for (FolderRow fr:  frList) {
				// Do we have a FolderEntry for this row?
				FolderEntry entry = entryMap.get(fr.getEntryId());
				if (null != entry) {
					// Yes!  Store the user's rights to that
					// FolderEntry.
					fr.setCanModify(fm.testAccess(entry, FolderOperation.modifyEntry   ));
					fr.setCanPurge( fm.testAccess(entry, FolderOperation.deleteEntry   ));
					fr.setCanTrash( fm.testAccess(entry, FolderOperation.preDeleteEntry));
				}
			}
			
		}
		catch (Exception ex) {/* Ignored. */}
	}

	/*
	 * Returns a non-null List<AssignmentInfo> from a folder row for a
	 * given attribute.
	 */
	private static List<AssignmentInfo> getAIListFromFR(FolderRow fr, String attrName) {
		Map<String, List<AssignmentInfo>> aiMap = fr.getRowAssigneeInfoListsMap();
		List<AssignmentInfo> reply = aiMap.get(attrName);
		return ((null == reply) ? new ArrayList<AssignmentInfo>() : reply);
	}
	
	/**
	 * Reads information for rendering a binder's description in a
	 * BinderDescriptionRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	public static BinderDescriptionRpcResponseData getBinderDescription(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			// Access the BinderInfo for the binder's description
			// information...
			BinderInfo binderInfo = GwtServerHelper.getBinderInfo(request, bs, String.valueOf(binderId));

			// ...and use it to construct and return a
			// ...BinderDescriptionRpcResponseData object.
			return
				new BinderDescriptionRpcResponseData(
					binderInfo.getBinderDesc(),
					binderInfo.isBinderDescHTML(),
					binderInfo.isBinderDescExpanded());
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getBinderDescription( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/**
	 * Reads the current user's filters for a binder and returns them
	 * as a BinderFiltersRpcResponseData.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by BinderHelper.getSearchFilter() and
	 * view_forum_user_filters.jsp.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BinderFiltersRpcResponseData getBinderFilters(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			Binder				binder = bs.getBinderModule().getBinder(binderId);
			List<BinderFilter>	ffList = new ArrayList<BinderFilter>();
			User				user   = GwtServerHelper.getCurrentUser();

			// Are there any global filters defined?
			TreeMap<String, String>	filterMap = new TreeMap<String, String>(new StringComparator(user.getLocale()));
			Map searchFilters = ((Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS));
			if ((null != searchFilters) && (!(searchFilters.isEmpty()))) {
				// Yes!  Add them to the sort map.
				Set<String> keySet = searchFilters.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					filterMap.put(ksIT.next(), ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL);
				}
			}
			
			// Does the user have any personal filters defined?
			UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			searchFilters = ((Map) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS));
			if ((null != searchFilters) && (!(searchFilters.isEmpty()))) {
				// Yes!  Add them to the sort map.
				Set<String> keySet = searchFilters.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					filterMap.put(ksIT.next(), ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL);
				}
			}

			// Based on the binder type, define the appropriate action
			// to use to view this type of binder.
			String viewAction;
			String binderType = binder.getEntityType().name();
			if      (binderType.equals(EntityIdentifier.EntityType.folder.name()))    viewAction = WebKeys.ACTION_VIEW_FOLDER_LISTING;
			else if (binderType.equals(EntityIdentifier.EntityType.workspace.name())) viewAction = WebKeys.ACTION_VIEW_WS_LISTING;
			else if (binderType.equals(EntityIdentifier.EntityType.profiles.name()))  viewAction = WebKeys.ACTION_VIEW_PROFILE_LISTING;
			else {
				throw new IllegalArgumentException("Unknown binderType" + binderType);
			}
			
			// Did we find any filters?
			AdaptedPortletURL url;
			if (!(filterMap.isEmpty())) {
				// ...scan the sorted set...
				Set<String> keySet = filterMap.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					// ...and add a BinderFilter for each to the list.
					String filterName  = ksIT.next();
					String filterScope = filterMap.get(filterName);
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,            viewAction               );
					url.setParameter(WebKeys.URL_BINDER_ID,     binder.getId().toString());
					url.setParameter(WebKeys.URL_OPERATION,     WebKeys.URL_SELECT_FILTER);
					url.setParameter(WebKeys.URL_OPERATION2,    filterScope              );
					url.setParameter(WebKeys.URL_SELECT_FILTER, filterName               );
					String filterAddUrl = url.toString();
					
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,           viewAction               );
					url.setParameter(WebKeys.URL_BINDER_ID,    binder.getId().toString());
					url.setParameter(WebKeys.URL_OPERATION,    WebKeys.URL_CLEAR_FILTER );
					url.setParameter(WebKeys.URL_OPERATION2,   filterScope              );
					url.setParameter(WebKeys.URL_CLEAR_FILTER, filterName               );
					String filterClearUrl = url.toString();
					
					ffList.add(
						new BinderFilter(
							filterName,
							filterScope,
							filterAddUrl,
							filterClearUrl));
				}
			}
			
			// Use the data we obtained to create a
			// BinderFiltersRpcResponseData.
			BinderFiltersRpcResponseData reply = new BinderFiltersRpcResponseData(ffList);
			
			// Store the current filters, if any, that the user
			// currently has selected on this binder.
			reply.setCurrentFilters(GwtServerHelper.getCurrentUserFilters(userBinderProperties));

			// Store a URL to turn off filtering on the binder.
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,            viewAction               );
			url.setParameter(WebKeys.URL_BINDER_ID,     binder.getId().toString());
			url.setParameter(WebKeys.URL_OPERATION,     WebKeys.URL_SELECT_FILTER);
			url.setParameter(WebKeys.URL_SELECT_FILTER, ""                       );
			reply.setFiltersOffUrl(url.toString());
			
			// Store a URL to edit the filters on the binder.
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_BUILD_FILTER);
			url.setParameter(WebKeys.URL_BINDER_ID,   binder.getId().toString()  );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType                 );
			reply.setFilterEditUrl(url.toString());
			
			// Finally, return the BinderFiltersRpcResponseData we just
			// constructed.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getBinderFilters( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/**
	 * Returns an AvatarInfoRpcResponseData object containing the
	 * information about a binder owner's avatar.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static AvatarInfoRpcResponseData getBinderOwnerAvatarInfo(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			// Construct a GuestInfo from the binder's owner...
			Binder binder = bs.getBinderModule().getBinder(Long.valueOf(binderId));
			Principal p = binder.getCreation().getPrincipal(); //creator is user
			GuestInfo gi = getGuestInfoFromPrincipal(bs, request, Utils.fixProxy(p));
			
			// ...and use that to construct an AvatarInfoRpcResponseData.
			AvatarInfoRpcResponseData reply = new AvatarInfoRpcResponseData(gi);

			// If we get here, reply refers to the
			// AvatarInfoRpcResponseData for the binder's owner.
			// Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getBinderOwnerAvatarInfo( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Reads the current user's region state for a binder and returns
	 * it as a StringRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param regionId
	 * 
	 * @return
	 */
	public static StringRpcResponseData getBinderRegionState(AllModulesInjected bs, HttpServletRequest request, Long binderId, String regionId) throws GwtTeamingException {
		try {
			// Does the user have this region state defined?
			UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUser().getId(), binderId);
			String regionState = ((String) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_REGION_VIEW + "." + regionId));

			// Use the data we obtained to create a
			// StringRpcResponseData and return it.
			return new StringRpcResponseData(MiscUtil.hasString(regionState) ? regionState : "expanded");
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getBinderRegionState( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/**
	 * Reads the current user's display data for a calendar and returns
	 * it as a CalendarDisplayDataRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static CalendarDisplayDataRpcResponseData getCalendarDisplayData(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		try {
			Long			folderId             = folderInfo.getBinderIdAsLong();
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userProperties       = bs.getProfileModule().getUserProperties(user.getId());
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);

//!			...this needs to be implemented...
			CalendarDayView	dayView     = CalendarDayView.MONTH;
			CalendarHours	hours	    = CalendarHours.WORK_DAY;
			CalendarShow	show        = CalendarShow.PHYSICAL_EVENTS;
			String			displayDate = GwtServerHelper.getDateString(new Date(), DateFormat.SHORT);
			
			// Finally, use the data we obtained to create a
			// CalendarDisplayDataRpcResponseData and return that. 
			return new CalendarDisplayDataRpcResponseData(dayView, hours, show, displayDate);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getCalendarDisplayData( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Returns a LinkedHashMap of the column names from a String[]
	 * of them.
	 */
	@SuppressWarnings("unchecked")
	private static Map getColumnsLHMFromAS(String[] columnNames) {
		Map reply = new LinkedHashMap();
		for (String columnName:  columnNames) {
			reply.put(columnName, columnName);
		}
		return reply;
	}

	/**
	 * Returns the column widths for a user on a folder.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ColumnWidthsRpcResponseData getColumnWidths(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		try {
			// Read the column widths stored in the folder properties...
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderInfo.getBinderIdAsLong());
			String          propKey              = (ObjectKeys.USER_PROPERTY_COLUMN_WIDTHS + (folderInfo.isBinderTrash() ? ".Trash" : ""));
			Map<String, String> columnWidths = ((Map<String, String>) userFolderProperties.getProperty(propKey));
			if ((null != columnWidths) && columnWidths.isEmpty()) {
				columnWidths = null;
			}
			
			// ...and return a ColumnWidthsRpcResponseData containing
			// ...them.
			return new ColumnWidthsRpcResponseData(columnWidths);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getColumnWidths( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Returns the entry description from a search results Map.
	 */
	@SuppressWarnings("unchecked")
	public static String getEntryDescriptionFromMap(HttpServletRequest httpReq, Map entryMap) {
		String reply = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DESC_FIELD);
		if (MiscUtil.hasString(reply)) {
			reply = MarkupUtil.markupStringReplacement(null, null, httpReq, null, entryMap, reply, WebKeys.MARKUP_VIEW, false);
			reply = MarkupUtil.markupSectionsReplacement(reply);
		}
		return reply;
	}

	/**
	 * Returns the collected entry types defined for a collection of binders.
	 *
	 * @param bs
	 * @param request
	 * @param entryId
	 * @param binderIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EntryTypesRpcResponseData getEntryTypes(AllModulesInjected bs, HttpServletRequest request, EntryId entryId, List<Long> binderIds) throws GwtTeamingException {
		try {
			// Allocate an EntryTypesRpcResponseData to track the entry
			// types for the requested binders.
			EntryTypesRpcResponseData reply = new EntryTypesRpcResponseData();
			
			// Scan the binder's whose entry types are being requested.
			for (Long binderId:  binderIds) {
				// Scan this binder's entry definitions...
				SortedMap<String, Definition> binderDefs = DefinitionHelper.getAvailableDefinitions(binderId, Definition.FOLDER_ENTRY);
				for (String defKey:  binderDefs.keySet()) {
					// ...adding an EntryType for each unique one to
					// ...the reply.
					Definition binderDef = binderDefs.get(defKey);
					String defId = binderDef.getId();
					if (!(reply.isEntryTypeInList(defId))) {
						boolean localDef = ((-1) != binderDef.getBinderId());
						EntryType et = new EntryType(defId, defKey, localDef);
						reply.addEntryType(et);
					}
				}
			}

			// Was the entry type of a specific entry requested?
			if (null != entryId) {
				// Yes!  Get its definition ID...
				FolderEntry fe = bs.getFolderModule().getEntry(entryId.getBinderId(), entryId.getEntryId());
				String feDefId = fe.getEntryDefId();
				
				// ...can the definition IDs we found...
				for (EntryType et:  reply.getEntryTypes()) {
					// ...and when one matches...
					if (feDefId.equals(et.getDefId())) {
						// ...use its EntryType for the requested entry.
						reply.setBaseEntryType( et           );
						reply.setBaseEntryTitle(fe.getTitle());
						break;
					}
				}
			}

			// If we get here, reply refers to the
			// EntryTypesRpcResponseData of the entry types for the
			// requested binders.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getEntryTypes( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Reads the current user's columns for a folder and returns them
	 * as a FolderColumnsRpcResponseData.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by folder_view_common2.jsp or view_trash.jsp.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 */
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		return getFolderColumns(bs, request, folderInfo, Boolean.FALSE);
	}
	
	@SuppressWarnings("unchecked")
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, Boolean includeConfigurationInfo) throws GwtTeamingException {
		try {
			Long			folderId             = folderInfo.getBinderIdAsLong();
			Binder			binder               = bs.getBinderModule().getBinder(folderId);
			Folder			folder               = ((binder instanceof Folder) ? ((Folder) binder) : null);
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			
			Map    columnNames;
			Map    columnTitles      = null;
			String columnOrderString = null;
			List columnsAll = new ArrayList();

			// Are we showing the trash on this folder?
			String baseNameKey;
			boolean isTrash = folderInfo.isBinderTrash();
			if (isTrash) {
				// Yes!  The columns in a trash view are not
				// configurable.  Use the default trash columns.
				baseNameKey = "trash.column.";
				columnNames = getColumnsLHMFromAS(TrashHelper.trashColumns);
			}

			// No, we aren't showing the trash on this folder!  Are we
			// looking at the root profiles binder? 
			else if (folderInfo.isBinderProfilesRootWS()) {
				// Yes!
				baseNameKey = "profiles.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"fullName", "emailAddress", "loginId"});
			}
			
			else {
				// No, we aren't showing the root profiles binder
				// either!  If we weren't given a folder...
				if (null == folder) {
					// ...we can't do anything with it.
            		throw
            			new GwtTeamingException(
            				GwtTeamingException.ExceptionType.FOLDER_EXPECTED,
            				"GwtViewHelper.getFolderColumns( *Internal Error* ):  The ID could not be resolved to a folder.");
				}
				
				// Are there user defined columns on this folder?
				FolderType folderType = folderInfo.getFolderType();
				switch (folderType) {
				case GUESTBOOK:  baseNameKey = "guestbook.column."; break;
				case MILESTONE:  baseNameKey = "milestone.";        break;
				case MINIBLOG:   baseNameKey = "miniblog.column.";  break;
				case SURVEY:     baseNameKey = "survey.";           break;
				default:         baseNameKey = "folder.column.";    break;
				}
				columnNames = ((Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS));
				if (null == columnNames) {
					// No!  Are there defaults stored on the binder?
					columnNames = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS));
					if (null == columnNames) {
						// No!  Use the default as setup in
						// folder_column_defaults.jsp.
						String[] defaultCols;
						switch (folderType) {
						case FILE:       defaultCols = new String[]{"title", "comments", "size", "download", "html", "state", "author", "date"}; break;
						case GUESTBOOK:  defaultCols = new String[]{"guest", "title", "date", "descriptionHtml"};                                break;
						case MILESTONE:  defaultCols = new String[]{"title", "responsible", "tasks", "status", "dueDate"};                       break;
						case MINIBLOG:   defaultCols = new String[]{"title", "description"};                                                     break;
						case SURVEY:     defaultCols = new String[]{"title", "author", "dueDate"};                                               break;
						default:         defaultCols = new String[]{"number", "title", "comments", "state", "author", "date", "rating"};         break;
						}
						columnNames = getColumnsLHMFromAS(defaultCols);
					}
					
					else {
						// Yes, there are defaults from the binder!
						// Read and names and sort order from there as
						// well.
						columnTitles      = ((Map)    folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_TITLES    ));
						columnOrderString = ((String) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_SORT_ORDER));
					}
				}
				
				else {
					// Yes, there are user defined columns on the
					// folder!  Read and names and sort order from
					// there as well.
					columnTitles      = ((Map)    userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_TITLES    ));
					columnOrderString = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_SORT_ORDER));
				}
			}

			// If we don't have any column names...
			if (null == columnTitles) {
				// ...just use an empty map.
				columnTitles = new HashMap();
			}
			
			// If we don't have any column sort order...
			if (!(MiscUtil.hasString(columnOrderString))) {
				// ...define one based on the column names.
				Set<String> keySet = columnNames.keySet();
				boolean firstCol = true;
				StringBuffer sb = new StringBuffer("");
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					if (!firstCol) {
						sb.append("|");
					}
					sb.append(ksIT.next());
					firstCol = false;
				}
				columnOrderString = sb.toString();
			}

			// Finally, generate a List<String> from the raw column
			// order string...
			List<String> columnSortOrder = new ArrayList<String>();
			String[] sortOrder = columnOrderString.split("\\|");
			for (String columnName:  sortOrder) {
				if (MiscUtil.hasString(columnName)) {
					columnSortOrder.add(columnName);
				}
			}
			
			// ...and ensure all the columns are accounted for in it.
			Set<String> keySet = columnNames.keySet();
			for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
				String columnName = ksIT.next();
				if (!(columnSortOrder.contains(columnName))) {
					columnSortOrder.add(columnName);
				}
			}

			// If we get here, we've got all the data we need to define
			// the List<FolderColumn> for this folder.  Allocate the
			// list that we can fill from that data.
			List<FolderColumn> fcList = new ArrayList<FolderColumn>();
			List<FolderColumn> fcListAll = new ArrayList<FolderColumn>();
			for (String colName:  columnSortOrder) {
				if (!columnsAll.contains(colName)) {
					FolderColumn fc = new FolderColumn(colName);
					if (colName.contains(",")) {
						String[] cnParts = colName.split(",");
						fc.setColumnDefId(cnParts[0]);
						fc.setColumnType(cnParts[1]);
						fc.setColumnEleName(cnParts[2]);
						String caption = colName.substring(colName.indexOf(",")+1);
						caption = caption.substring(caption.indexOf(",")+1);
						caption = caption.substring(caption.indexOf(",")+1);
						fc.setColumnDefaultTitle(caption);
					}
					// Is this column to be shown?
					String columnValue = ((String) columnNames.get(colName));
					if (!(MiscUtil.hasString(columnValue))) {
						// No!  Skip it.
						fc.setColumnIsShown(Boolean.FALSE);;
					} else {
						fc.setColumnIsShown(Boolean.TRUE);
					}
	
					// Is there a custom title for this column?
					String colTitle = ((String) columnTitles.get(colName));
					String colTitleDefault = "";
					if (!MiscUtil.hasString(fc.getColumnDefId())) {
						colTitleDefault = NLT.get(
							(baseNameKey + colName),	// Key to find the resource.
							colName,					// Default if not defined.
							true);						// true -> Silent.  Don't generate an error if undefined.
					} else {
						colTitleDefault = fc.getColumnDefaultTitle();
					}
					fc.setColumnDefaultTitle(colTitleDefault);
					if (!(MiscUtil.hasString(colTitle))) {
						// There is no custom title,  use the default.
						colTitle = colTitleDefault;
					} else {
						fc.setColumnCustomTitle(colTitle);
					}
					fc.setColumnTitle(colTitle);
	
					// Add a FolderColumn for this to the list we're
					// going to return.
					if (fc.getColumnIsShown()) {
						//This column is being shown
						fcList.add(fc);
					}
					fcListAll.add(fc);
					columnsAll.add(colName);
				}
			}

			// Walk the List<FolderColumn>'s performing fixups on each
			// as necessary.
			fixupFCs(fcList, isTrash);
			

			if (includeConfigurationInfo && (!isTrash)) {
				//Build a list of all possible columns
				Map<String,Definition> entryDefs = DefinitionHelper.getEntryDefsAsMap(((Folder) bs.getBinderModule().getBinder(folderId)));
				for (Definition def :  entryDefs.values()) {
					@SuppressWarnings("unused")
					Document defDoc = def.getDefinition();
					Map<String,Map> elementData = bs.getDefinitionModule().getEntryDefinitionElements(def.getId());
					for (Map.Entry me : elementData.entrySet()) {
						String eleName = (String)me.getKey();
						String type = (String)((Map)me.getValue()).get("type");
						String caption = (String)((Map)me.getValue()).get("caption");
						String colName = def.getId()+","+type+","+eleName+","+caption;
						if (!columnsAll.contains(colName)) {
							if (type.equals("selectbox") ||
									type.equals("selectbox") ||
									type.equals("radio") ||
									type.equals("checkbox") ||
									type.equals("date") ||
									type.equals("date_time") ||
									type.equals("event") ||
									type.equals("text") ||
									type.equals("number") ||
									type.equals("url") ||
									type.equals("hidden") ||
									type.equals("user_list") ||
									type.equals("userListSelectbox")) {
								FolderColumn fc = new FolderColumn(colName);
								fc.setColumnDefId(def.getId());
								fc.setColumnType(type);
								// Is this column to be shown?
								String columnValue = ((String) columnNames.get(colName));
								if (!(MiscUtil.hasString(columnValue))) {
									// No!  
									fc.setColumnIsShown(Boolean.FALSE);
								} else {
									fc.setColumnIsShown(Boolean.TRUE);
								}
			
								// Is there a custom title for this column?
								String colTitleDefault = (String)((Map)me.getValue()).get("caption");
								if (!(MiscUtil.hasString(fc.getColumnDefId()))) {
									colTitleDefault = NLT.get(
										(baseNameKey + colName),	// Key to find the resource.
										colTitleDefault,			// Default if not defined.
										true);						// true -> Silent.  Don't generate an error if undefined.
								}
								fc.setColumnDefaultTitle(colTitleDefault);
								String colTitle = (String) columnTitles.get(colName);
								if (!(MiscUtil.hasString(colTitle))) {
									// There is no custom title,  use the default.
									colTitle = colTitleDefault;
								} else {
									fc.setColumnCustomTitle(colTitle);
								}
								fc.setColumnTitle(colTitle);
			
								// Add a FolderColumn for this to the list of all columns if it isn't already there.
								fcListAll.add(fc);
								columnsAll.add(colName);
							}
						}
					}
				}
			}

			// Finally, use the data we obtained to create a
			// FolderColumnsRpcResponseData and return that. 
			return new FolderColumnsRpcResponseData(fcList, fcListAll);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getFolderColumns( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/**
	 * Reads the current user's display data for a folder and returns
	 * them as a FolderDisplayDataRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 */
	public static FolderDisplayDataRpcResponseData getFolderDisplayData(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		try {
			Long			folderId             = folderInfo.getBinderIdAsLong();
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userProperties       = bs.getProfileModule().getUserProperties(user.getId());
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			
			// How should the folder be sorted?
			String	sortBy = ((String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY));
			boolean sortDescend;
			if (MiscUtil.hasString(sortBy)) {
				String sortDescendS = ((String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_DESCEND));
				sortDescend = (("true").equalsIgnoreCase(sortDescendS));
			}
			else {
				sortDescend = false;
				if (folderInfo.isBinderProfilesRootWS()) {
					sortBy = Constants.SORT_TITLE_FIELD;
				}
				else {
					switch (folderInfo.getFolderType()) {
					case FILE:
					case MILESTONE:
					case MINIBLOG:
					case SURVEY:     sortBy = Constants.SORT_TITLE_FIELD;                     break;
					case TASK:       sortBy = Constants.SORT_ORDER_FIELD;                     break;
					case GUESTBOOK:  sortBy = Constants.CREATOR_TITLE_FIELD;                  break;
					default:         sortBy = Constants.SORTNUMBER_FIELD; sortDescend = true; break;
					}
				}
			}

			// How many entries per page should the folder display?
			int pageSize;
			try                  {pageSize = Integer.parseInt(MiscUtil.entriesPerPage(userProperties));}
			catch (Exception ex) {pageSize = 25;                                                       }
			
			// Has the user defined any column widths on this folder?
			ColumnWidthsRpcResponseData cwData = getColumnWidths(bs, request, folderInfo);
			
			// Finally, use the data we obtained to create a
			// FolderDisplayDataRpcResponseData and return that. 
			return new FolderDisplayDataRpcResponseData(sortBy, sortDescend, pageSize, cwData.getColumnWidths());
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getFolderDisplayData( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Reads the row data from a folder and returns it as a
	 * FolderRowsRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param folderColumns
	 * @param start
	 * @param length
	 * @param quickFilter
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderRowsRpcResponseData getFolderRows(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, List<FolderColumn> folderColumns, int start, int length, String quickFilter) throws GwtTeamingException {
		try {
			// Access the binder/folder.
			Long   folderId = folderInfo.getBinderIdAsLong();
			Binder binder   = bs.getBinderModule().getBinder(folderId);
			Folder folder   = ((binder instanceof Folder)    ? ((Folder)    binder) : null);
			
			// If we're reading from a mirrored file folder...
			if (FolderType.MIRROREDFILE == folderInfo.getFolderType()) {
				// ...whose driver is not configured...
				String rdn = binder.getResourceDriverName();
				if (!(MiscUtil.hasString(rdn))) {
					// ...we don't read anything.
					return
						new FolderRowsRpcResponseData(
							new ArrayList<FolderRow>(),
							start,
							0,
							new ArrayList<Long>());
				}
			}
			
			// What type of folder are we dealing with?
			boolean isDiscussion     = false;
			boolean isFolder         = (null != folder);
			boolean isGuestbook      = false;
			boolean isMilestone      = false;
			boolean isSurvey         = false;
			boolean isProfilesRootWS = folderInfo.isBinderProfilesRootWS();
			boolean isTrash          = folderInfo.isBinderTrash();
			switch (folderInfo.getFolderType()) {
			case DISCUSSION:  isDiscussion = true; break;
			case GUESTBOOK:   isGuestbook  = true; break;
			case MILESTONE:   isMilestone  = true; break;
			case SURVEY:      isSurvey     = true; break;
			}

			// Access any other information we need to read the data.
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			SeenMap			seenMap              = bs.getProfileModule().getUserSeenMap(null);

			// Setup the current search filter the user has selected
			// on the folder.
			Map options;
			if (isFolder)
			     options = getFolderSearchFilter(bs, folder, userFolderProperties, null);
			else options = new HashMap();
			GwtServerHelper.addQuickFilterToSearch(options, quickFilter);
			options.put(ObjectKeys.SEARCH_OFFSET,   start );
			options.put(ObjectKeys.SEARCH_MAX_HITS, length);

			// Factor in the user's sorting selection.
			FolderDisplayDataRpcResponseData fdd = getFolderDisplayData(bs, request, folderInfo);
			options.put(ObjectKeys.SEARCH_SORT_BY,      fdd.getFolderSortBy()     );
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, fdd.getFolderSortDescend());

			// Read the entries based on the search.
			Map searchResults;
			if      (isTrash)          searchResults = TrashHelper.getTrashEntries(bs, binder,   options);
			else if (isProfilesRootWS) searchResults = bs.getProfileModule().getUsers(           options);
			else                       searchResults = bs.getFolderModule().getEntries(folderId, options);
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
			int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();

			// Is this the first page of a discussion folder?
			List<Long> pinnedEntryIds = new ArrayList<Long>();
			if ((0 == start) && isDiscussion) {
				// Yes!  Are there any entries pinned in the folder?
				List<Map>  pinnedEntrySearchMaps = getPinnedEntries(bs, folder, userFolderProperties, pinnedEntryIds);
				if (!(pinnedEntrySearchMaps.isEmpty())) {
					// Yes!  Add them to the beginning of the search
					// entries.
					searchEntries.addAll(0, pinnedEntrySearchMaps);
				}
			}

			// Scan the entries we read.
			boolean         addedAssignments = false;
			List<FolderRow> folderRows       = new ArrayList<FolderRow>();
			List<Long>      contributorIds   = new ArrayList<Long>();
			for (Map entryMap:  searchEntries) {
				// Have we already process this entry's ID?
				String entryIdS  = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DOCID_FIELD);
				Long entryId = Long.parseLong(entryIdS);
				if (isEntryIInList(entryId, folderRows)) {
					// Yes!  Skip it now.  Note that we may have
					// duplicates because of pinning.
					continue;
				}
				
				// Extract the contributors from this entry.
				collectContributorIds(entryMap, contributorIds);
				
				// Create a FolderRow for each entry.
				String entityType       = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ENTITY_FIELD   );
				String locationBinderId = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.BINDER_ID_FIELD);
				if (!(MiscUtil.hasString(locationBinderId))) {
					locationBinderId = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.BINDERS_PARENT_ID_FIELD);
				}
				FolderRow fr = new FolderRow(new EntryId(Long.parseLong(locationBinderId), entryId), entityType, folderColumns);
				if (pinnedEntryIds.contains(entryId)) {
					fr.setPinned(true);
				}
				
				// Scan the columns.
				for (FolderColumn fc:  folderColumns) {
					// Is this a custom column?
					if (fc.isCustomColumn()) {
						// Yes!  Generate a value for it.
						setValueForCustomColumn(bs, entryMap, fr, fc);
					}
					
					else {
						// No, this isn't a custom column!  Can we
						// construct a PrincipalInfo for this column
						// using the value from Map?
						String cn      = fc.getColumnName();
						String csk     = fc.getColumnSearchKey();
						Object emValue = GwtServerHelper.getValueFromEntryMap(entryMap, csk);
						GuestInfo     gi = null;
						PrincipalInfo pi = null;
						if (emValue instanceof Principal) {
							// Yes!  Are we looking at the 'guest'
							// column in a guest book folder?
							Principal p   = ((Principal) emValue);
							if (isGuestbook && cn.equals("guest")) {
								// Yes!  Use the principal to generate
								// a GuestInfo for the column.
								gi = getGuestInfoFromPrincipal(bs, request, p);
								fr.setColumnValue(fc, gi);
							}
							
							else {
								// No, we aren't looking at the 
								// guest' column in a guest book
								// folder!  If we can create a
								// PrincipalInfo for the principal...
								pi = getPIFromPId(bs, request, folderInfo, p.getId());
								if (null != pi) {
									// ...store it directly.
									fr.setColumnValue(fc, pi);
								}
							}
						}
					
						if ((null == pi) && (null == gi)) {
							// No!  Does the column contain assignment
							// information?
							if (AssignmentInfo.isColumnAssigneeInfo(csk)) {
								// Yes!  Read its
								// List<AssignmentInfo>'s.
								AssigneeType ait = AssignmentInfo.getColumnAssigneeType(csk);
								List<AssignmentInfo> assignmentList = GwtServerHelper.getAssignmentInfoListFromEntryMap(entryMap, csk, ait);
								
								// Is this column for an individual
								// assignee?
								if (AssigneeType.INDIVIDUAL == ait) {
									// Yes!  If we don't have columns
									// for group or team assignments,
									// factor those in as well.
									factorInGroupAssignments(entryMap, folderColumns, csk, assignmentList);
									factorInTeamAssignments( entryMap, folderColumns, csk, assignmentList);
								}
								
								// Add the column data to the list.
								addedAssignments = true;
								fr.setColumnValue_AssignmentInfos(fc, assignmentList);
							}
							
							// No, the column doesn't contain
							// assignment information either!  Does
							// it contain a collection of task folders?
							else if (csk.equals("tasks")) {
								// Yes!  Create a List<TaskFolderInfo>
								// from the IDs it contains and set
								// that as the column value.
								List<TaskFolderInfo> taskFolderList = GwtServerHelper.getTaskFolderInfoListFromEntryMap(bs, request, entryMap, csk);
								fr.setColumnValue_TaskFolderInfos(fc, taskFolderList);
							}
							
							// No, the column doesn't contain a
							// collection of task folders either!
							// Does it contain an email address?
							else if (csk.equals("emailAddress")) {
								// Yes!  Construct an EmailAddressInfo
								// from the entry map.
								EmailAddressInfo emai = GwtServerHelper.getEmailAddressInfoFromEntryMap(bs, entryMap);
								fr.setColumnValue(fc, emai);
							}
	
							else {
								// No, the column doesn't contain an
								// email address either!  Extract its
								// String value.
								String value = GwtServerHelper.getStringFromEntryMapValue(
									emValue,
									DateFormat.MEDIUM,
									DateFormat.SHORT);
								
								// Are we working on a title field?
								if (csk.equals(Constants.TITLE_FIELD)) {
									// Yes!  Construct an
									// EntryTitleInfo for it.
									EntryTitleInfo eti = new EntryTitleInfo();
									eti.setSeen(seenMap.checkIfSeen(entryMap));
									eti.setTrash(isTrash);
									eti.setEntityType(entityType);
									eti.setTitle(MiscUtil.hasString(value) ? value : ("--" + NLT.get("entry.noTitle") + "--"));
									eti.setEntryId(entryId);
									eti.setDescription(getEntryDescriptionFromMap(request, entryMap));
									fr.setColumnValue(fc, eti);
								}
								
								// No, we aren't working on a title
								// field!  Are we working on a file ID
								// field?
								else if (csk.equals(Constants.FILE_ID_FIELD)) {
									// Yes!  Do we have a single file
									// ID?
									if ((!(MiscUtil.hasString(value))) || ((-1) != value.indexOf(','))) {
										// No!  Ignore the value.
										value = null;
									}
									
									else {
										// Yes, we have a single file
										// ID!  Do we have a file path
										// that we support viewing of?
										String relativeFilePath = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILENAME_FIELD);
										if ((!(MiscUtil.hasString(relativeFilePath))) || (!(SsfsUtil.supportsViewAsHtml(relativeFilePath)))) {
											// No!  Ignore the value.
											value = null;
										}
									}
									
									// Do we have a file ID to work
									// with?
									if (MiscUtil.hasString(value)) {
										// Yes!  Construct a
										// ViewFileInfo for it.
										ViewFileInfo vfi = new ViewFileInfo();
										vfi.setFileId(     value);
										vfi.setBinderId(   Long.parseLong(GwtServerHelper.getStringFromEntryMap(entryMap, Constants.BINDER_ID_FIELD)));
										vfi.setEntryId(    entryId);
										vfi.setEntityType( GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ENTITY_FIELD));
										vfi.setFileTime(   GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILE_TIME_FIELD));
										vfi.setViewFileUrl(GwtServerHelper.getViewFileUrl(       request,  vfi));
										fr.setColumnValue(fc, vfi);
									}
								}

								// No, we aren't working on a file ID
								// field either!  Are we working on an
								// HTML description field?
								else if (csk.equals(Constants.DESC_FIELD) && cn.equals("descriptionHtml")) {
									// Yes!  Check if the description
									// is in HTML format and store it.
									String descFmt = GwtServerHelper. getStringFromEntryMap(entryMap, Constants.DESC_FORMAT_FIELD);
									boolean isHtml = ((null != descFmt) && "1".equals(descFmt));
									fr.setColumnValue(fc, new DescriptionHtml(value, isHtml));
								}
								
								else {
									// No, we aren't working on an HTML
									// description field either!  Are
									// we working on a field whose
									// value is a Date?
									if (emValue instanceof Date) {
										// Yes!  Is that Date overdue?
										if (DateComparer.isOverdue((Date) emValue)) {
											// Yes!  Mark that column
											// as being an overdue
											// date, and if this a
											// due date...
											if (csk.equals(Constants.DUE_DATE_FIELD)) {
												// ...that's not...
												// ...completed...
												String status = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.STATUS_FIELD);
												boolean completed = (MiscUtil.hasString(status) && status.equals("completed"));
												if (!completed) {
													// ...show it as
													// ...being
													// ...overdue.
													fr.setColumnOverdueDate(fc, Boolean.TRUE);
													if      (isSurvey)    value += (" " + NLT.get("survey.overdue"   ));
													else if (isMilestone) value += (" " + NLT.get("milestone.overdue"));
												}
											}
										}
										fr.setColumnValue(fc, (null == (value) ? "" : value));
									}
									
									// No, we aren't working on a Date
									// field!  Are we working on a file
									// size field?
									else if (csk.equals(Constants.FILE_SIZE_FIELD)) {
										// Yes!  Trim any leading 0's
										// from the value.
										value = trimFileSize(value);
										if (MiscUtil.hasString(value)) {
											value += "KB";
										}
									}

									// No, we aren't working on a file
									// size field!  Are we working on
									// the status field of a milestone?
									else if (csk.equals(Constants.STATUS_FIELD) && isMilestone) {
										// Yes!  Do we have a status
										// value for it?
										if (MiscUtil.hasString(value)) {
											// Yes!  Pull its localized
											// string from the
											// resources.
											value = NLT.get(("__milestone_status_" + value), value);
										}
									}
									
									// Use what ever String value we
									// arrived at.
									fr.setColumnValue(fc, (null == (value) ? "" : value));
								}
							}
						}
					}
				}
				
				// Add the FolderRow we just built to the
				// List<FolderRow> of them.
				folderRows.add(fr);
			}

			// Did we add any rows with assignment information?
			if (addedAssignments) {
				// Yes!  We need to complete the definition of the
				// AssignmentInfo objects.
				//
				// When initially built, the AssignmentInfo's in the
				// List<AssignmentInfo>'s only contain the assignee
				// IDs.  We need to complete them with each assignee's
				// title, ...
				completeAIs(bs, folderRows);
			}
			
			// Walk the List<FolderRow>'s performing any remaining
			// fixups on each as necessary.
			fixupFRs(bs, folderRows);
			
			// Finally, return the List<FolderRow> wrapped in a
			// FolderRowsRpcResponseData.
			return new FolderRowsRpcResponseData(folderRows, start, totalRecords, contributorIds);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getFolderRows( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/*
	 * Constructs and returns a GuestInfo from a Principal.
	 */
	private static GuestInfo getGuestInfoFromPrincipal(AllModulesInjected bs, HttpServletRequest request, Principal p) {
		Long pId = p.getId();
		GuestInfo reply = new GuestInfo(
			pId,
			p.getTitle(),
			PermaLinkUtil.getUserPermalink(request, String.valueOf(pId)));
		reply.setAvatarUrl(getUserAvatarUrl(bs, request, p));
		reply.setEmailAddress(      p.getEmailAddress()      );
		reply.setMobileEmailAddress(p.getMobileEmailAddress());
		reply.setTextEmailAddress(  p.getTxtEmailAddress()   );
		if (p instanceof User) {
			reply.setPhone(((User) p).getPhone());
		}
		return reply;
	}
	
	/*
	 * Extracts a Long value from a entry Map.
	 */
	@SuppressWarnings("unchecked")
	public static Long getLongFromMap(Map entryMap, String key) {
		Object v = entryMap.get(key);
		Long reply;
		if      (v instanceof String) reply = Long.parseLong((String) v);
		else if (v instanceof Long)   reply = ((Long) v);
		else                          reply = -1L;
		return reply;
	}

	/*
	 * Given a Principal's ID read from an entry map, returns an
	 * equivalent PrincipalInfo object.
	 */
	@SuppressWarnings("unchecked")
	private static PrincipalInfo getPIFromPId(AllModulesInjected bs, HttpServletRequest request, BinderInfo fi, Long pId) {
		// Can we resolve the ID to an actual Principal object?
		PrincipalInfo reply = null;
		List<Long> principalIds = new ArrayList<Long>();
		principalIds.add(pId);
		List principals = null;
		try {principals = ResolveIds.getPrincipals(principalIds, false);}
		catch (Exception ex) {/* Ignored. */}
		if ((null != principals) && (!(principals.isEmpty()))) {
			for (Object o:  principals) {
				// Yes!  Is it a User?
				Principal p = ((Principal) o);
				boolean isUser = (p instanceof UserPrincipal);
				if (isUser) {
					// Yes!  Construct the rest of the PrincipalInfo
					// required.
					pId   = p.getId();
					reply = PrincipalInfo.construct(pId);
					reply.setTitle(p.getTitle());
					User      user          = ((User) p);
					Workspace userWS        = GwtServerHelper.getUserWorkspace(user);
					boolean   userHasWS     = (null != userWS);
					boolean   userWSInTrash = (userHasWS && userWS.isPreDeleted());
					reply.setUserDisabled( user.isDisabled());
					reply.setUserHasWS(    userHasWS        );
					reply.setUserWSInTrash(userWSInTrash    );
					reply.setViewProfileEntryUrl(getViewProfileEntryUrl(bs, request, pId));
					reply.setPresenceUserWSId(user.getWorkspaceId());
					
					// Setup an appropriate GwtPresenceInfo for the
					// Vibe environment?
					GwtPresenceInfo presenceInfo;
					if (GwtServerHelper.isPresenceEnabled())
					     presenceInfo = GwtServerHelper.getPresenceInfo(user);
					else presenceInfo = null;
					if (null == presenceInfo) {
						presenceInfo = GwtServerHelper.getPresenceInfoDefault();
					}
					if (null != presenceInfo) {
						reply.setPresence(presenceInfo);
						reply.setPresenceDude(GwtServerHelper.getPresenceDude(presenceInfo));
					}
				}
				
				// There can only ever be one ID.
				break;
			}
		}
		
		// If we get here, reply refers to the PrincipalInfo object
		// for the principal ID we received or is null.  Return it.
		return reply;
	}
	
	/**
	 * Returns a ProfileEntryInfoRpcRequestData containing information
	 * about a user's profile.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ProfileEntryInfoRpcResponseData getProfileEntryInfo(AllModulesInjected bs, HttpServletRequest request, Long userId) throws GwtTeamingException {
		try {
			// Allocate an profile entry info response we can return.
			ProfileEntryInfoRpcResponseData reply = new ProfileEntryInfoRpcResponseData();

			// Can we access the user in question?
			List<String> userIdList = new ArrayList<String>();
			String userIdS = String.valueOf(userId);
			userIdList.add(userIdS);
			List resolvedList = ResolveIds.getPrincipals(userIdList, false);
			if ((null != resolvedList) && (!(resolvedList.isEmpty()))) {
				// Yes!  Extract the profile information we need to
				// display.
				User user = ((User) resolvedList.get(0));
				addProfileAttribute(reply, "title",              user.getTitle());
				addProfileAttribute(reply, "emailAddress",       user.getEmailAddress());
				addProfileAttribute(reply, "mobileEmailAddress", user.getMobileEmailAddress());
				addProfileAttribute(reply, "txtEmailAddress",    user.getTxtEmailAddress());
				addProfileAttribute(reply, "phone",              user.getPhone());
				addProfileAttribute(reply, "timeZone",           user.getTimeZone().getDisplayName());
				addProfileAttribute(reply, "locale",             user.getLocale().getDisplayName());
				
				// Store the URL for the user's avatar, if they have
				// one.
				reply.setAvatarUrl(getUserAvatarUrl(bs, request, user));

				// Does the current user have rights to modify users?
				ProfileModule pm = bs.getProfileModule();
				String profilesWSIdS = String.valueOf(pm.getProfileBinderId());
				if (pm.testAccess(user, ProfileOperation.modifyEntry)) {
					// Yes!  Store the modify URL for this user.
					AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,         WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
					url.setParameter(WebKeys.URL_BINDER_ID,  profilesWSIdS                      );
					url.setParameter(WebKeys.URL_ENTRY_ID,   userIdS                            );
					reply.setModifyUrl(url.toString());
				}
				
				// Does the current user have rights to delete users
				// and is this other than a reserved user?
				if (pm.testAccess(user, ProfileOperation.deleteEntry) && (!(user.isReserved()))) {
					// Yes!  Store the delete URL for this user.
					AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,         WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
					url.setParameter(WebKeys.URL_OPERATION,  WebKeys.OPERATION_DELETE           );
					url.setParameter(WebKeys.URL_BINDER_ID,  profilesWSIdS                      );
					url.setParameter(WebKeys.URL_ENTRY_ID,   userIdS                            );
					reply.setDeleteUrl(url.toString());
				}
			}

			// If we get here, reply refers to an
			// ProfileEntryInfoRpcResponseData containing the user's
			// profile information.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getProfileEntryInfo( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Returns a map containing the search filter to use to read the
	 * rows from a folder.
	 */
	@SuppressWarnings("unchecked")
	private static Map getFolderSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, String searchTitle) {
		Map result = new HashMap();
		GwtServerHelper.addSearchFiltersToOptions(bs, binder, userFolderProperties, true, result);
		if (MiscUtil.hasString(searchTitle)) {
			result.put(ObjectKeys.SEARCH_TITLE, searchTitle);
		}
		return result;
	}

	/**
	 * Gets HTML from the execution of a jsp
	 * 
	 * @param bs
	 * @param request
	 * @param jsp
	 * @param model
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JspHtmlRpcResponseData getJspHtml(AllModulesInjected bs, HttpServletRequest request, 
			HttpServletResponse response, ServletContext servletContext, 
			VibeJspHtmlType jspType, Map<String,Object> model) throws GwtTeamingException {
		String html = "";
		String jspPath = null;
		try {
			switch (jspType) {
				// The following are the supported jsp calls.
			case ACCESSORY_PANEL:
			{
				try {
					RenderRequestImpl renderReq;
					RenderResponseImpl renderRes;
					PortletInfo portletInfo;
					
					//Build request and render objects needed to build the toolbar
					{
						Map<String, Object> params;
	
						String portletName;
						String charEncoding;
	
						portletName = "ss_forum";
						portletInfo = (PortletInfo) AdaptedPortlets.getPortletInfo( portletName );
						
						renderReq = new RenderRequestImpl( request, portletInfo, AdaptedPortlets.getPortletContext() );
						
						params = new HashMap<String, Object>();
						params.put( KeyNames.PORTLET_URL_PORTLET_NAME, new String[] {portletName} );
						renderReq.setRenderParameters( params );
						
						renderRes = new RenderResponseImpl( renderReq, response, portletName );
						charEncoding = SPropsUtil.getString( "web.char.encoding", "UTF-8" );
						renderRes.setContentType( "text/html; charset=" + charEncoding );
						renderReq.defineObjects( portletInfo.getPortletConfig(), renderRes );
						
						renderReq.setAttribute( PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE );
					}

					//Display the whole accessory panel
					User user = RequestContextHolder.getRequestContext().getUser();
					String s_binderId = (String) model.get("binderId");
					Binder binder = bs.getBinderModule().getBinder(Long.valueOf(s_binderId));

					UserProperties userProperties = new UserProperties(user.getId());
					Map userProps = new HashMap();
		    		if (userProperties.getProperties() != null) {
		    			userProps = userProperties.getProperties();
		    		}

		    		if (user != null) {
		    			userProperties = bs.getProfileModule().getUserProperties(user.getId());
		    		}
		    		Map<String,Object> panelModel = new HashMap<String,Object>();
					DashboardHelper.getDashboardMap(binder, userProps, panelModel);
					//Build the "Add Accessory" toolbar
					Toolbar dashboardToolbar = new Toolbar();
					BinderHelper.buildDashboardToolbar(renderReq, renderRes, bs, binder, dashboardToolbar, panelModel);

					//Set up the beans used by the jsp
					panelModel.put(WebKeys.BINDER_ID, binder.getId());
					panelModel.put(WebKeys.BINDER, binder);
					panelModel.put(WebKeys.USER_PROPERTIES, userProps);
					panelModel.put(WebKeys.SNIPPET, true);				//Signal that <html> and <head> should not be output
					panelModel.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
					
					jspPath = "definition_elements/view_dashboard_canvas.jsp";
					html = GwtServerHelper.executeJsp(bs, request, response, servletContext, jspPath, panelModel);
					break;
				} catch(Exception e) {
					if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
					     m_logger.debug("GwtViewHelper.getJspHtml( SOURCE EXCEPTION ):  ", e);
					}
					//Return an error back to the user
					String[] args = new String[1];
					args[0] = e.getMessage();
					html = NLT.get("errorcode.dashboardComponentViewFailure", args);
				}
			}
			
			case ACCESSORY:
			{
				try {
					//Set up bean used by the dashboard component (aka accessory)
					User user = RequestContextHolder.getRequestContext().getUser();
					String s_binderId = (String) model.get("binderId");
					Binder binder = bs.getBinderModule().getBinder(Long.valueOf(s_binderId));
					String componentId = (String) model.get("ssComponentId");
					String scope = componentId.split("_")[0];

					UserProperties userProperties = new UserProperties(user.getId());
					Map userProps = new HashMap();
		    		if (userProperties.getProperties() != null) {
		    			userProps = userProperties.getProperties();
		    		}

		    		if (user != null) {
		    			userProperties = bs.getProfileModule().getUserProperties(user.getId());
		    		}
		    		if (componentId != null && !componentId.equals("")) {
			    		Map<String,Object> componentModel = new HashMap<String,Object>();
						DashboardHelper.getDashboardMap(binder, userProps, componentModel, scope, componentId, false);
						Map<String,Object> componentDashboard = (Map<String,Object>) componentModel.get("ssDashboard");
						componentDashboard.put("ssComponentId", componentId);
						componentModel.put(WebKeys.BINDER_ID, binder.getId());
						componentModel.put(WebKeys.BINDER, binder);
						jspPath = "definition_elements/view_dashboard_component.jsp";
						html = GwtServerHelper.executeJsp(bs, request, response, servletContext, jspPath, componentModel);
		    		}
					break;
				} catch(Exception e) {
					if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
					     m_logger.debug("GwtViewHelper.getJspHtml( SOURCE EXCEPTION ):  ", e);
					}
					//Return an error back to the user
					String[] args = new String[1];
					args[0] = e.getMessage();
					html = NLT.get("errorcode.dashboardComponentViewFailure", args);
				}
			}
			
				default: 
				{
					// Log an error that we encountered an unhandled command.
					m_logger.error("JspHtmlRpcResponseData( Unknown jsp type ):  " + jspType.name());
					break;
				}
			}
			
			return new JspHtmlRpcResponseData(html, model);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.getJspHtml( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/*
	 * Returns a List<Long> of the entry ID's of the entries that are
	 * pinned in the given folder. 
	 */
	@SuppressWarnings("unchecked")
	private static List<Map> getPinnedEntries(AllModulesInjected bs, Folder folder, UserProperties userFolderProperties, List<Long> pinnedEntryIds) {
		// Allocate a List<Map> for the search results for the entries
		// pinned in this folder.
		List<Map> pinnedEntrySearchMaps = new ArrayList<Map>();

		// Are there any pinned entries stored in the user's folder
		// properties on this folder?
		Map properties = userFolderProperties.getProperties();
		String pinnedEntries;
		if ((null != properties) && properties.containsKey(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES))
		     pinnedEntries = ((String) properties.get(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES));
		else pinnedEntries = null;
		if (MiscUtil.hasString(pinnedEntries)) {
			// Yes!  Parse them converting the String ID's to Long's.
			if (pinnedEntries.lastIndexOf(",") == (pinnedEntries.length() - 1)) { 
				pinnedEntries = pinnedEntries.substring(0, (pinnedEntries.length() - 1));
			}
			String[] peArray = pinnedEntries.split(",");
			List<Long> peSet = new ArrayList();
			for (int i = 0; i < peArray.length; i += 1) {
				String pe = peArray[i];
				if (MiscUtil.hasString(pe)) {
					peSet.add(Long.valueOf(pe));
				}
			}

			// Scan the pinned entries.
			FolderModule fm = bs.getFolderModule();
			List<org.apache.lucene.document.Document> pinnedFolderEntriesList = new ArrayList<org.apache.lucene.document.Document>();
			SortedSet<FolderEntry> pinnedFolderEntriesSet = fm.getEntries(peSet);
			for (FolderEntry entry:  pinnedFolderEntriesSet) {
				// Is this entry still viable in this folder?
				if (!(entry.isPreDeleted()) && entry.getParentBinder().equals(folder)) {
					// Yes!  Track its ID...
					pinnedEntryIds.add(entry.getId());

					// ...and indexDoc.
					org.apache.lucene.document.Document indexDoc = fm.buildIndexDocumentFromEntry(entry.getParentBinder(), entry, null);
					pinnedFolderEntriesList.add(indexDoc);
				}
			}

			// Construct search Map's from the indexDoc's for the
			// pinned entries.
			pinnedEntrySearchMaps = SearchUtils.getSearchEntries(pinnedFolderEntriesList);
			bs.getFolderModule().getEntryPrincipals(pinnedEntrySearchMaps);
		}
		
		// If we get here, pinnedEntrySearchMaps refers to a List<Map>
		// search Map's for the pinned entries.  Return it.
		return pinnedEntrySearchMaps;
	}

	/*
	 * Returns a Map<String, String> for the query parameters from a
	 * URL.
	 */
	private static Map<String, String> getQueryParameters(String url) {
		URI uri;
		try {
			// Can we parse the URL?
			uri = new URI(url);
		} catch (URISyntaxException e) {
			// No!  Log the error and bail.
			m_logger.error("GwtViewHelper.getQueryParameters( URL Parsing Exception ):  ", e);
			return null;
		}
		
		// Allocate a Map<String, String> to return the parameters.
		Map<String, String> reply = new HashMap<String, String>();

		// Does the URL contain a query?
		String query = uri.getQuery();
		if (null != query) {
			// Yes!  Split it at the &'s.
			String[] parameters = query.split("&");
			
			// Scan the parameters.
			for (String p:  parameters) {
				// Does this parameter contain an '='?
				int eq = p.indexOf('=');
				if ((-1) != eq) {
					// Yes!  Does it have a name part?
					String name = p.substring(0, eq);
					if (MiscUtil.hasString(name)) {
						// Yes!  Add it to the map.
						reply.put(name.toLowerCase(), p.substring(  eq + 1));
					}
				}
			}
		}
		
		else {
			// No, the URL didn't contain a query!  Does it contain a
			// path?
			String path = uri.getPath();
			if (null != path) {
				// Yes!  Split it at the /'s.
				String[] parameters = path.split("/");
				
				// Scan the parameters by 2's.
				int c = (parameters.length - 1);
				for (int i = 0; i < c; i += 2) {
					// Does this parameter have a name?
					String name = parameters[i];
					if (MiscUtil.hasString(name)) {
						// Yes!  Add it to the map.
						reply.put(name.toLowerCase(), parameters[i + 1]);
					}
				}
			}
		}
		
		// If we get here, reply refers to a Map<String, String> of the
		// parameters from the URL.  Return it.
		return reply;
	}

	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Boolean value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	private static boolean getQueryParameterBoolean(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			boolean reply;
			try                 {reply = Boolean.parseBoolean(v);}
			catch (Exception e) {reply = false;                  }
			return reply;
		}		
		return false;
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Boolean value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	@SuppressWarnings("unused")
	private static int getQueryParameterInt(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			int reply;
			try                 {reply = Integer.parseInt(v);}
			catch (Exception e) {reply = (-1);               }
			return reply;
		}		
		return (-1);
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Long value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	private static Long getQueryParameterLong(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			Long reply;
			try                 {reply = Long.parseLong(v);}
			catch (Exception e) {reply = null;             }
			return reply;
		}		
		return null;
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter.
	 * 
	 * If the value can't be found, null is returned. 
	 */
	private static String getQueryParameterString(Map<String, String> nvMap, String name) {
		String reply;
		if ((null != nvMap) && (!(nvMap.isEmpty())))
		     reply = nvMap.get(name.toLowerCase());
		else reply = null;
		return ((null == reply) ? "" : reply);
	}

	/*
	 * Resolves, if possible, a user ID to a User object.
	 */
	@SuppressWarnings("rawtypes")
	private static User getResolvedUser(Long userId) {
		User user = null;
		String userIdS = String.valueOf(userId);
		List<String> userIdList = new ArrayList<String>();
		userIdList.add(userIdS);
		List resolvedList = ResolveIds.getPrincipals(userIdList, false);
		if ((null != resolvedList) && (!(resolvedList.isEmpty()))) {
			Object o = resolvedList.get(0);
			if (o instanceof User) {
				user = ((User) o);
			}
		}
		return user;
	}
		
	/*
	 * Returns the URL for a user's avatar.
	 */
	@SuppressWarnings("unchecked")
	private static String getUserAvatarUrl(AllModulesInjected bs, HttpServletRequest request, Principal user) {
		// Can we access any avatars for the user?
		String reply = null;
		ProfileAttribute pa;
		try                  {pa = GwtProfileHelper.getProfileAvatars(request, bs, user);}
		catch (Exception ex) {pa = null;                                                 }
		List<ProfileAttributeListElement> paValue = ((null == pa) ? null : ((List<ProfileAttributeListElement>) pa.getValue()));
		if((null != paValue) && (!(paValue.isEmpty()))) {
			// Yes!  We'll use the first one as the URL.  Does it
			// have a URL?
			ProfileAttributeListElement paValueItem = paValue.get(0);
			reply = GwtProfileHelper.fixupAvatarUrl(paValueItem.getValue().toString());
		}
		
		// If we get here, reply refers to the user's avatar URL or is
		// null.  Return it.
		return reply;
	}

	/**
	 * Returns a ViewInfo used to control folder views based on a URL.
	 * 
	 * @param bs
	 * @param request
	 * @param url
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	@SuppressWarnings("unchecked")
	public static ViewInfo getViewInfo(AllModulesInjected bs, HttpServletRequest request, String url) throws GwtTeamingException {
		// Trace the URL we're working with.
		m_logger.debug("GwtViewHelper.getViewInfo():  " + url);

		// Can we parse the URL?
		Map<String, String> nvMap = getQueryParameters(url);
		if ((null == nvMap) || nvMap.isEmpty()) {
			// No!  Then we can't get a BinderInfo.
			m_logger.debug("GwtViewHelper.getViewInfo():  1:Could not determine a view.");
			return null;
		}

		// Construct a ViewInfo we can setup with the information for
		// viewing this URL.
		ViewInfo vi = new ViewInfo();

		// What's URL requesting?
		String action = getQueryParameterString(nvMap, WebKeys.URL_ACTION).toLowerCase();		
		if (action.equals(WebKeys.ACTION_VIEW_PERMALINK)) {
			// A view on a permalink!  What type of entity is being
			// viewed?
			String entityType = getQueryParameterString(nvMap, WebKeys.URL_ENTITY_TYPE).toLowerCase();
			if (entityType.equals("user")) {
				// A user!  Can we access the user?
				Long entryId = getQueryParameterLong(nvMap, WebKeys.URL_ENTRY_ID);			
				if (!(initVIFromUser(request, bs, GwtServerHelper.getUserFromId(bs, entryId), vi))) {
					m_logger.debug("GwtViewHelper.getViewInfo():  2:Could not determine a view.");
					return null;
				}
			}
			else if (entityType.equals("folder") || entityType.equals("workspace") || entityType.equals("profiles")) {
				// A folder, workspace or the profiles binder!  Setup
				// a binder view based on the binder ID.
				if (!(initVIFromBinderId(request, bs, nvMap, WebKeys.URL_BINDER_ID, vi, true))) {
					m_logger.debug("GwtViewHelper.getViewInfo():  3:Could not determine a view.");
					return null;
				}
			}
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_WS_LISTING) || action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING)) {
			// A view workspace or folder listing!  Are we view a
			// folder while changing its view?
			boolean       viewFolderListing = action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING);
			String        op                = getQueryParameterString(nvMap, WebKeys.URL_OPERATION);
			boolean       hasOp             = MiscUtil.hasString(op);
			ProfileModule pm                = bs.getProfileModule();
			Long          userId            = GwtServerHelper.getCurrentUser().getId();
			if (viewFolderListing && hasOp && op.equals(WebKeys.OPERATION_SET_DISPLAY_DEFINITION)) {
				// Yes!  Do we have both the view definition and binder
				// ID's?
				String defId    = getQueryParameterString(nvMap, WebKeys.URL_VALUE    );
				Long   binderId = getQueryParameterLong(  nvMap, WebKeys.URL_BINDER_ID);
				if (MiscUtil.hasString(defId) && (null != binderId)) {
					// Yes!  Put the view into affect.
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, defId);
				}
			}
			
			// Setup a binder view based on the binder ID.
			if (!(initVIFromBinderId(request, bs, nvMap, WebKeys.URL_BINDER_ID, vi, true))) {
				m_logger.debug("GwtViewHelper.getViewInfo():  4:Could not determine a view.");
				return null;
			}

			// Is this a view folder listing?
			Long binderId = vi.getBinderInfo().getBinderIdAsLong();
			if (viewFolderListing) {
				// Yes!  Does it also contain changes to the folder
				// sorting?
				String sortBy       = getQueryParameterString(nvMap, WebKeys.FOLDER_SORT_BY);		
				String sortDescendS = getQueryParameterString(nvMap, WebKeys.FOLDER_SORT_DESCEND);
				if (MiscUtil.hasString(sortBy) && MiscUtil.hasString(sortDescendS)) {
					// Yes!  Apply the sort changes.
					Boolean sortDescend = Boolean.parseBoolean(sortDescendS);
					GwtServerHelper.saveFolderSort(
						bs,
						binderId,
						sortBy,
						(!sortDescend));
				}
				
				// If the request contain changes to the task
				// filtering...
				String taskFilterType = getQueryParameterString(nvMap, WebKeys.TASK_FILTER_TYPE);
				if (MiscUtil.hasString(taskFilterType)) {
					// ...put them into effect.
					TaskHelper.setTaskFilterType(bs, userId, binderId, FilterType.valueOf(taskFilterType));
				}
				String folderModeType = getQueryParameterString(nvMap, WebKeys.FOLDER_MODE_TYPE);
				if (MiscUtil.hasString(folderModeType)) {
					// ...put them into effect.
					ListFolderHelper.setFolderModeType(bs, userId, binderId, ModeType.valueOf(folderModeType));
				}
			}
			
			// Does it contain a filter operation?
			if (hasOp && op.equals(WebKeys.URL_SELECT_FILTER)) {
				// Yes, a filter selection!  Apply the selection.
				String filterName = getQueryParameterString(nvMap, WebKeys.URL_SELECT_FILTER);
				String op2 = getQueryParameterString(nvMap, WebKeys.URL_OPERATION2);
				String filterScope;
				if (MiscUtil.hasString(op2) && op2.equals(ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL))
				     filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL;
				else filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL;
				pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER,       filterName );
				pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE, filterScope);

				UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUser().getId(), binderId);
				List<String> currentFilters = ((List<String>) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTERS));
				if (null == currentFilters) {
					currentFilters = new ArrayList<String>();
				}
				if (MiscUtil.hasString(filterName)) {
					String filterSpec = BinderFilter.buildFilterSpec(filterName, filterScope);
					if (!(currentFilters.contains(filterSpec))) {
						currentFilters.add(filterSpec);
					}
				}
				else {
					currentFilters.clear();
				}
				pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTERS, currentFilters);
			}
			
			if (MiscUtil.hasString(op) && op.equals(WebKeys.URL_CLEAR_FILTER)) {
				// Yes, a filter clear!  Apply the selection.
				String op2 = getQueryParameterString(nvMap, WebKeys.URL_OPERATION2);
				String filterScope;
				if (MiscUtil.hasString(op2) && op2.equals(ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL))
				     filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL;
				else filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL;
				pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER,       ""         );
				pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE, filterScope);

				String filterName = getQueryParameterString(nvMap, WebKeys.URL_CLEAR_FILTER);
				String filterSpec = BinderFilter.buildFilterSpec(filterName, filterScope);
				UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUser().getId(), binderId);
				List<String> currentFilters = ((List<String>) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTERS));
				if (null == currentFilters) {
					currentFilters = new ArrayList<String>();
				}
				currentFilters.remove(filterSpec);
				pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTERS, currentFilters);
			}
		}

		else if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
			// An add folder entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_FOLDER_ENTRY);
		}
		
		else if (action.equals(WebKeys.ACTION_ADD_BINDER)) {
			// An add binder!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_BINDER);
		}
		
		else if (action.equals(WebKeys.ACTION_ADD_PROFILE_ENTRY)) {
			// An add profile entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_PROFILE_ENTRY);
		}
		
		else if (action.equals(WebKeys.ACTION_ADVANCED_SEARCH)) {
			// An advanced search!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADVANCED_SEARCH);
		}
		
		else if (action.equals(WebKeys.ACTION_BUILD_FILTER)) {
			// A build filter!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.BUILD_FILTER);
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_PROFILE_ENTRY)) {
			// A view profile entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.VIEW_PROFILE_ENTRY);
		}
		
		// If we get here reply refers to the BinderInfo requested or
		// is null.  Return it.
		if (m_logger.isDebugEnabled()) {
			dumpViewInfo(vi);
		}
		return vi;
	}

	/*
	 * Returns the URL to use to view a user's profile entry.
	 */
	private static String getViewProfileEntryUrl(AllModulesInjected bs, HttpServletRequest request, Long userId) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.URL_BINDER_ID,        String.valueOf(bs.getProfileModule().getProfileBinder().getId()));
		url.setParameter(WebKeys.URL_ACTION,           WebKeys.ACTION_VIEW_PROFILE_ENTRY                               );
		url.setParameter(WebKeys.URL_ENTRY_VIEW_STYLE, WebKeys.URL_ENTRY_VIEW_STYLE_FULL                               );
		url.setParameter(WebKeys.URL_NEW_TAB,          "1"                                                             );
		url.setParameter(WebKeys.URL_ENTRY_ID,         String.valueOf(userId)                                          );
		return url.toString();
	}

	/*
	 * Initializes a ViewInfo based on a binder ID.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	private static boolean initVIFromBinderId(HttpServletRequest request, AllModulesInjected bs, Map<String, String> nvMap, String binderIdName, ViewInfo vi, boolean checkForTrash) {
		// Initialize as a binder based on the user's workspace.
		Long binderId = getQueryParameterLong(nvMap, binderIdName);
		BinderInfo bi = GwtServerHelper.getBinderInfo(request, bs, String.valueOf(binderId));
		if (null == bi) {
			return false;
		}
		vi.setViewType(ViewType.BINDER);
		vi.setBinderInfo(bi);

		// Do we need to check for a show trash flag?
		if (checkForTrash) {
			// Yes!  Are we showing the trash on a this binder?
			boolean showTrash = getQueryParameterBoolean(nvMap, WebKeys.URL_SHOW_TRASH);
			if (showTrash) {
				// Yes!  Update the folder/workspace type
				// accordingly.
				if      (bi.isBinderFolder())    bi.setFolderType(   FolderType.TRASH   );
				else if (bi.isBinderWorkspace()) bi.setWorkspaceType(WorkspaceType.TRASH);
			}
		}
		return true;
	}
	
	/*
	 * Initializes a ViewInfo based on a User.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	private static boolean initVIFromUser(HttpServletRequest request, AllModulesInjected bs, User user, ViewInfo vi) {
		// Were we given a User object to initialize from?
		if (null == user) {
			// No!  Bail.
			return false;
		}

		// Initialize as a binder based on the user's workspace.
		BinderInfo bi = GwtServerHelper.getBinderInfo(request, bs, String.valueOf(user.getWorkspaceId()));
		if (null == bi) {
			return false;
		}
		vi.setViewType(ViewType.BINDER);
		vi.setBinderInfo(bi);
		
		return true;
	}

	/*
	 * Returns true if a FolderRow with the specified entry ID is in
	 * a List<FolderRow> or false otherwise.
	 */
	private static boolean isEntryIInList(Long entryId, List<FolderRow> folderRows) {
		for (FolderRow fr:  folderRows) {
			if (fr.getEntryId().equals(entryId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Locks the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param entryIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData lockEntries(AllModulesInjected bs, HttpServletRequest request, List<EntryId> entryIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any entries to lock?
			if ((null != entryIds) && (!(entryIds.isEmpty()))) {
				// Yes!  Scan them.
				for (EntryId entryId:  entryIds) {
					try {
						// Can we lock this entry?
						bs.getFolderModule().reserveEntry(entryId.getBinderId(), entryId.getEntryId());
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String entryTitle = GwtServerHelper.getEntryTitle(bs, entryId.getBinderId(), entryId.getEntryId());
						String messageKey;
						if      (e instanceof AccessControlException)           messageKey = "lockEntryError.AccssControlException";
						else if (e instanceof ReservedByAnotherUserException)   messageKey = "lockEntryError.ReservedByAnotherUserException";
						else if (e instanceof FilesLockedByOtherUsersException) messageKey = "lockEntryError.FilesLockedByOtherUsersException";
						else                                                    messageKey = "lockEntryError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.lockEntries( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Moves the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param targetFolderId
	 * @param entryIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData moveEntries(AllModulesInjected bs, HttpServletRequest request, Long targetFolderId, List<EntryId> entryIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any entries to move?
			if ((null != entryIds) && (!(entryIds.isEmpty()))) {
				// Yes!  Scan them.
				for (EntryId entryId:  entryIds) {
					try {
						// Can we move this entry?
						bs.getFolderModule().moveEntry(entryId.getBinderId(), entryId.getEntryId(), targetFolderId, null, null);
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String entryTitle = GwtServerHelper.getEntryTitle(bs, entryId.getBinderId(), entryId.getEntryId());
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "moveEntryError.AccssControlException";
						else                                     messageKey = "moveEntryError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.moveEntries( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Purge users and their workspaces.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * @param purgeMirrored
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ErrorListRpcResponseData purgeUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, boolean purgeMirrored) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any users to purge?
			Long currentUserId = GwtServerHelper.getCurrentUser().getId(); 
			if ((null != userIds) && (!(userIds.isEmpty()))) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being purge?
					User user = getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't purge their own
							// user object or workspace.  Ignore it.
							reply.addError(NLT.get("purgeUserError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
							reply.addError(NLT.get("purgeUserError.reserved", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Does this user have a workspace ID?
						Long wsId = user.getWorkspaceId();
						if (null != wsId) {
							// Yes!  Purge the workspace...
							bs.getBinderModule().deleteBinder(wsId, purgeMirrored, null);
						}
						
						// ...and purge the user.
						Map options = new HashMap();
						options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, false);
						bs.getProfileModule().deleteEntry(userId, options);
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "purgeUserError.AccssControlException";
						else                                          messageKey = "purgeUserError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.purgeUsers( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Purge user workspaces.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * @param purgeMirrored
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData purgeUserWorkspaces(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, boolean purgeMirrored) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any users to purge?
			Long currentUserId = GwtServerHelper.getCurrentUser().getId(); 
			if ((null != userIds) && (!(userIds.isEmpty()))) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being purge?
					User user = getResolvedUser(userId);
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't purge their own
							// workspace.  Ignore it.
							reply.addError(NLT.get("purgeUserWorkspaceError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
							reply.addError(NLT.get("purgeUserWorkspaceError.reserved", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Does this user have a workspace ID?
						Long wsId = user.getWorkspaceId();
						if (null != wsId) {
							// Yes!  Purge the workspace.
							bs.getBinderModule().deleteBinder(wsId, purgeMirrored, null);
						}
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
						String messageKey;
						if      (e instanceof AccessControlException) messageKey = "purgeUserWorkspaceError.AccssControlException";
						else                                          messageKey = "purgeUserWorkspaceError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.purgeUserWorkspaces( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Unlocks the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param entryIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData unlockEntries(AllModulesInjected bs, HttpServletRequest request, List<EntryId> entryIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<String>());

			// Were we given the IDs of any entries to unlock?
			if ((null != entryIds) && (!(entryIds.isEmpty()))) {
				// Yes!  Scan them.
				for (EntryId entryId:  entryIds) {
					try {
						// Can we unlock this entry?
						bs.getFolderModule().unreserveEntry(entryId.getBinderId(), entryId.getEntryId());
					}

					catch (Exception e) {
						// No!  Add an error  to the error list.
						String entryTitle = GwtServerHelper.getEntryTitle(bs, entryId.getBinderId(), entryId.getEntryId());
						String messageKey;
						if      (e instanceof AccessControlException)         messageKey = "unlockEntryError.AccssControlException";
						else if (e instanceof ReservedByAnotherUserException) messageKey = "unlockEntryError.ReservedByAnotherUserException";
						else                                                  messageKey = "unlockEntryError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.unlockEntries( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Stores a region state for the current user on a binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param regionId
	 * @param regionState
	 * 
	 * @return
	 */
	public static BooleanRpcResponseData saveBinderRegionState(AllModulesInjected bs, HttpServletRequest request, Long binderId, String regionId, String regionState) throws GwtTeamingException {
		try {
			// Store the new region state and return true.
			bs.getProfileModule().setUserProperty(GwtServerHelper.getCurrentUser().getId(), binderId, ObjectKeys.USER_PROPERTY_REGION_VIEW + "." + regionId, regionState);
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.saveBinderRegionState( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/**
	 * Stores the column widths for a user on a folder.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param columnWidths
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveColumnWidths(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, Map<String, String> columnWidths) throws GwtTeamingException {
		try {
			// Store the column widths...
			String propKey = ObjectKeys.USER_PROPERTY_COLUMN_WIDTHS;
			if (folderInfo.isBinderTrash()) {
				propKey += ".Trash";
			}
			bs.getProfileModule().setUserProperty(
				GwtServerHelper.getCurrentUser().getId(),
				folderInfo.getBinderIdAsLong(),
				propKey,
				columnWidths);
			
			// ...and return true.
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.saveColumnWidths( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Generates a value for a custom column in a row.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by folder_view_common2.jsp or view_trash.jsp.
	 */
	@SuppressWarnings("unchecked")
	private static void setValueForCustomColumn(AllModulesInjected bs, Map entryMap, FolderRow fr, FolderColumn fc) throws ParseException {
		try {
			// If we don't have a column element name...
			String colEleName = fc.getColumnEleName();
			if (!MiscUtil.hasString(colEleName)) {
				// ...just render it as an empty string.
				fr.setColumnValue(fc, "");
				return;
			}

			// Do we have a value or event for this column?
			Object colValue = entryMap.get(colEleName);
			String colType = fc.getColumnType();
			if (null == colType) colType = "";
			if ((null != colValue) || colType.equals("event")) {
				// Yes!  Handles those that are a simple list of values.
				Definition entryDef = bs.getDefinitionModule().getDefinition(fc.getColumnDefId());
				if (colType.equals("selectbox")           ||
					    colType.equals("radio")           ||
					    colType.equals("checkbox")        ||
					    colType.equals("text")            ||
					    colType.equals("entryAttributes") ||
					    colType.equals("hidden")          ||
					    colType.equals("number")) {
					String strValue = DefinitionHelper.getCaptionsFromValues(entryDef, colEleName, colValue.toString());
					fr.setColumnValue(fc, strValue);
				}

				// Handle those that are a URL.
				else if (colType.equals("url")) {
					// Can we find the definition of the column?
		         	Element colElement = ((Element) DefinitionHelper.findAttribute(colEleName, entryDef.getDefinition()));
		         	if (null == colElement) {
		         		// No!  Just render the column as an empty string.
						fr.setColumnValue(fc, "");
		         	}
		         	
		         	else {
						// Yes, we found the definition of the column!
		         		// Extract what we need to render it.
			         	String linkText = DefinitionHelper.getItemProperty(colElement, "linkText");
			         	String target   = DefinitionHelper.getItemProperty(colElement, "target"  );
			         	
			         	String  strValue   = DefinitionHelper.getCaptionsFromValues(entryDef, colEleName, colValue.toString());
			         	if (!(MiscUtil.hasString(linkText))) linkText = strValue;
			         	if ((null == target) || target.equals("false")) target = "";
			         	if ((null != target) && target.equals("true" )) target = "_blank";
			         	
			         	// Construct an EntryLinkInfo for the data.
			         	EntryLinkInfo linkValue = new EntryLinkInfo(strValue, target, linkText);
						fr.setColumnValue(fc, linkValue);
		         	}
				}

				// Handle date stamps.
				else if (colType.equals("date")) {
					if (null != colValue) {
						String tdStamp = ((String) colValue);
					    String year    = tdStamp.substring(0, 4);
						String month   = tdStamp.substring(4, 6);
						String day     = tdStamp.substring(6, 8);
						String strValue;
						if (8 < tdStamp.length()) {
							String time = tdStamp.substring(8);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd:HHmm");
							Date date = formatter.parse(year + "-" + month + "-" + day + ":" + time);
							strValue = GwtServerHelper.getDateString(date, DateFormat.SHORT);
						}
						else {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							Date date = formatter.parse(year + "-" + month + "-" + day);
							strValue = GwtServerHelper.getDateString(date, DateFormat.SHORT);
						}
						fr.setColumnValue(fc, strValue);
					}
				}

				// Handle time/date stamps.
				else if (colType.equals("date_time")) {
					if (null != colValue) {
						String tdStamp = ((String) colValue);
					    String year    = tdStamp.substring(0, 4);
						String month   = tdStamp.substring(4, 6);
						String day     = tdStamp.substring(6, 8);
						String strValue;
						if (8 < tdStamp.length()) {
							String time = tdStamp.substring(8, 12);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd:HHmm");
							Date date = formatter.parse(year + "-" + month + "-" + day + ":" + time);
							strValue = GwtServerHelper.getDateTimeString(date, DateFormat.SHORT, DateFormat.SHORT);
						}
						else {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							Date date = formatter.parse(year + "-" + month + "-" + day);
							strValue = GwtServerHelper.getDateString(date, DateFormat.SHORT);
						}
						fr.setColumnValue(fc, strValue);
					}
				}

				// Handle events.
				else if (colType.equals("event")) {
					String tzId = ((String) entryMap.get(colEleName + "#TimeZoneID"));
					boolean showTimes = false;
					Date logicalEnd   = ((Date) entryMap.get(colEleName + "#LogicalEndDate"));
					Date logicalStart = ((Date) entryMap.get(colEleName + "#LogicalStartDate"));
					if ((null != logicalStart) && (null != logicalEnd)) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						if (sdf.format(logicalStart).equals(sdf.format(logicalEnd))) {
							// The two dates are the same, so show the
							// time field.
							showTimes = true;
						}
					}
					
					String logicalEndS   = null;
					String logicalEtartS = null;
					boolean allDayEvent  = (null == tzId);
					if (allDayEvent) {
						//All day event.
						if (null != logicalStart) logicalEtartS = GwtServerHelper.getDateString(logicalStart, DateFormat.SHORT);
						if (null != logicalEnd)   logicalEndS   = GwtServerHelper.getDateString(logicalEnd,   DateFormat.SHORT);
					}
					
					else {
						// Regular event.
						if (null != logicalStart) {
							if (showTimes)
							     logicalEtartS = GwtServerHelper.getDateTimeString(logicalStart, DateFormat.SHORT, DateFormat.SHORT);
							else logicalEtartS = GwtServerHelper.getDateString(    logicalStart, DateFormat.SHORT                  );
							
						}
						if (null != logicalEnd) {
							if (showTimes)
							     logicalEndS = GwtServerHelper.getDateTimeString(logicalEnd, DateFormat.SHORT, DateFormat.SHORT);
							else logicalEndS = GwtServerHelper.getDateString(    logicalEnd, DateFormat.SHORT                  );
						}
					}
					
					EntryEventInfo eventValue = new EntryEventInfo(allDayEvent, logicalEtartS, logicalEndS);
					Date actualEnd   = ((Date) entryMap.get(colEleName + "#EndDate"));
					int  durDays     = getDurDaysFromEntryMap(colEleName, entryMap);
					boolean daysOnly = ((null == actualEnd) && (0 < durDays));
					if (daysOnly) {
						// Duration only.
						eventValue.setDurationDaysOnly(true   );
						eventValue.setDurationDays(    durDays);
					}
		         	
					// Construct an EntryEventInfo for the data.
					fr.setColumnValue(fc, eventValue);
				}

				// Handle lists of users.
				else if (colType.equals("user_list") || colType.equals("userListSelectbox")) {
					String       idList     = colValue.toString();
					Set          ids        = LongIdUtil.getIdsAsLongSet(idList, ","  );
					List         principals = ResolveIds.getPrincipals(  ids,    false);
					StringBuffer strValue   = new StringBuffer("");
					boolean      firstP     = true;
					for (Object pO:  principals) {
						Principal p = ((Principal) pO);
						if (!firstP) {
							strValue.append(", ");
						}
						strValue.append(p.getTitle());
						firstP = false;
					}
					fr.setColumnValue(fc, strValue.toString());
				}
			}
		}
		
		catch (Exception ex) {
			// Log the exception...
			m_logger.debug("GwtViewHelper.setValueForCustomColumn( EXCEPTION ):  ", ex);
			m_logger.debug("...Element:  " + fc.getColumnEleName());
			m_logger.debug("...Column:  " + fc.getColumnName());
			m_logger.debug("...Row:  "    + fr.getEntryId());

			// ...and store something for the column to display.
			fr.setColumnValue(
				fc,
				(GwtUIHelper.isVibeUiDebug()         ?
					("Exception:  " + ex.toString()) :
					""));
		}
	}

	/**
	 * Called to purge all the entries in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * @param purgeMirroredSources
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashPurgeAll(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId, boolean purgeMirroredSources) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Purge the entries in the trash...
			TrashEntry[] trashEntries = TrashHelper.getAllTrashEntries(bs, binderId);
			TrashResponse tr = TrashHelper.purgeSelectedEntries(bs, trashEntries, purgeMirroredSources);
			if (tr.isError() || tr.m_rd.hasRenames()) {
				// ...and return any messages we get in response.
				reply.setStringValue(tr.getTrashMessage(bs));
			}

			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.trashPurgeAll( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Called to purge the selected entries in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * @param purgeMirroredSources
	 * @param trashSelectionData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashPurgeSelectedEntries(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId, boolean purgeMirroredSources, List<String> trashSelectionData) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Do we have any selections to purge?
			int count = ((null == trashSelectionData) ? 0 : trashSelectionData.size());
			if (0 < count) {
				// Yes!  Convert them to a TrashEntry[]...
				TrashEntry[] trashEntries = new TrashEntry[count];
				for (int i = 0; i < count; i += 1) {
					trashEntries[i] = new TrashEntry(trashSelectionData.get(i));
				}
				
				// ...purge those...
				TrashResponse tr = TrashHelper.purgeSelectedEntries(bs, trashEntries, purgeMirroredSources);
				if (tr.isError() || tr.m_rd.hasRenames()) {
					// ...and return any messages we get in response.
					reply.setStringValue(tr.getTrashMessage(bs));
				}
			}
			
			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.trashPurgeSelectedEntries( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Called to restore all the entries in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashRestoreAll(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Restore the entries in the trash...
			TrashEntry[] trashEntries = TrashHelper.getAllTrashEntries(bs, binderId);
			TrashResponse tr = TrashHelper.restoreSelectedEntries(bs, trashEntries);
			if (tr.isError() || tr.m_rd.hasRenames()) {
				// ...and return any messages we get in response.
				reply.setStringValue(tr.getTrashMessage(bs));
			}

			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.trashRestoreAll( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Called to restore the selected entries in the trash.
	 * 
	 * @param bs
	 * @param reqeust
	 * @param binderId
	 * @param trashSelectionData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData trashRestoreSelectedEntries(AllModulesInjected bs, HttpServletRequest reqeust, Long binderId, List<String> trashSelectionData) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Do we have any selections to restore?
			int count = ((null == trashSelectionData) ? 0 : trashSelectionData.size());
			if (0 < count) {
				// Yes!  Convert them to a TrashEntry[]...
				TrashEntry[] trashEntries = new TrashEntry[count];
				for (int i = 0; i < count; i += 1) {
					trashEntries[i] = new TrashEntry(trashSelectionData.get(i));
				}
				
				// ...restore those...
				TrashResponse tr = TrashHelper.restoreSelectedEntries(bs, trashEntries);
				if (tr.isError() || tr.m_rd.hasRenames()) {
					// ...and return any messages we get in response.
					reply.setStringValue(tr.getTrashMessage(bs));
				}
			}
			
			// If we get here, reply refers to a StringRpcResponseData
			// containing any errors we encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtViewHelper.trashRestoreSelectedEntries( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/*
	 * Strips the leading 0's and any trying decimal information off a
	 * String value and returns it.
	 */
	private static String trimFileSize(String value) {
		if (null != value) {
	        while (value.startsWith("0")) {
	        	value = value.substring(1, value.length());
	        }
	        value = trimFollowing(value, ".");	// Cleans any decimal...
	        value = trimFollowing(value, ",");	// information.
	        if (value.equals("")) {
	        	value = "0";
	        }
		}
		return value;
	}

	/*
	 * Strips everything in a string followingThis.
	 */
	private static String trimFollowing(String value, String followingThis) {
		if (null != value) {
	        int dPos = value.indexOf(followingThis);
	        while ((-1) != dPos) {
	        	value = value.substring(0, dPos);
		        dPos = value.indexOf(followingThis);
	        }
		}
		return value;
	}
}
