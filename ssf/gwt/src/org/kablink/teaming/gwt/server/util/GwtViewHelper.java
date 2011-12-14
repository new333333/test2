/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import java.util.SortedSet;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.BinderDescriptionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.ViewType;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.search.Constants;


/**
 * Helper methods for the GWT binder views.
 *
 * @author drfoster@novell.com
 */
public class GwtViewHelper {
	protected static Log m_logger = LogFactory.getLog(GwtViewHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtViewHelper() {
		// Nothing to do.
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
				MiscUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			
			// Scan this FolderRow's group assignees tracking each
			// unique ID.
			for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME)) {
				MiscUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
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
			try {principals = ResolveIds.getPrincipals(principalIds);}
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
			fixupAIs(     getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        principalTitles, userPresence, presenceUserWSIds);
			fixupAIGroups(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME),      principalTitles, groupCounts                    );
			fixupAIGroups(getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME), principalTitles, groupCounts                    );
			fixupAITeams( getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME),       teamTitles,      teamCounts                     );
			fixupAITeams( getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME),  teamTitles,      teamCounts                     );
		}		

		// Finally, one last scan through the List<FolderRow>'s...
		Comparator<AssignmentInfo> comparator = new GwtServerHelper.AssignmentInfoComparator(true);
		for (FolderRow fr:  folderRows) {
			// ...this time, to sort the assignee lists.
			Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             comparator);
			Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        comparator);
			Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME),      comparator);
			Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME), comparator);
			Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME),       comparator);
			Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME),  comparator);
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
			
		case ADVANCED_SEARCH:
		case OTHER:
			break;
			
		default:
			m_logger.debug("......dumpViewInfo( Not Handled ):  This ViewType is not implemented by the dumper.");
			break;
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
		
		// Scan this AssignmentInfo's group assignees again...
		for (AssignmentInfo ai:  aiGroupsList) {
			// ...setting each one's title and membership count.
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
		
		// Scan this AssignmentInfo's team assignees again...
		for (AssignmentInfo ai:  aiTeamsList) {
			// ...setting each one's title and membership count.
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
			// ...setting each one's title.
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
	 *    by folder_view_common2.jsp.
	 */
	private static void fixupFCs(List<FolderColumn> fcList) {
		for (FolderColumn fc:  fcList) {
			String colName = fc.getColumnName();
			if      (colName.equals("author"))   {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);              fc.setColumnSortKey(Constants.CREATOR_TITLE_FIELD); }
			else if (colName.equals("comments")) {fc.setColumnSearchKey(Constants.TOTALREPLYCOUNT_FIELD);                                                            }
			else if (colName.equals("date"))     {fc.setColumnSearchKey(Constants.LASTACTIVITY_FIELD);                                                               }
			else if (colName.equals("download")) {fc.setColumnSearchKey(Constants.FILENAME_FIELD);                                                                   }
			else if (colName.equals("html"))     {fc.setColumnSearchKey(Constants.FILE_ID_FIELD);                                                                    }
			else if (colName.equals("location")) {fc.setColumnSearchKey(Constants.PRE_DELETED_FIELD);                                                                }
			else if (colName.equals("number"))   {fc.setColumnSearchKey(Constants.DOCNUMBER_FIELD);              fc.setColumnSortKey(Constants.SORTNUMBER_FIELD);    }
			else if (colName.equals("rating"))   {fc.setColumnSearchKey(Constants.RATING_FIELD);                                                                     }
			else if (colName.equals("size"))     {fc.setColumnSearchKey(Constants.FILE_SIZE_FIELD);                                                                  }
			else if (colName.equals("state"))    {fc.setColumnSearchKey(Constants.WORKFLOW_STATE_CAPTION_FIELD); fc.setColumnSortKey(Constants.WORKFLOW_STATE_FIELD);}
			else if (colName.equals("title"))    {fc.setColumnSearchKey(Constants.TITLE_FIELD);                  fc.setColumnSortKey(Constants.SORT_TITLE_FIELD);    }
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
						defId      = temp[0];
						eleType    = temp[1];
						eleName    = temp[2];
						//Since the caption may have commas in it, we need to get everything past the third comma
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
					fc.setColumnEleName(colName);	//If not a custom attribute, the element name is the colName
				}
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
			entryIds.add(fr.getEntryId());
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
			// Access the description information from the binder...
			Binder	    binder        = bs.getBinderModule().getBinder(binderId);
			Description binderDesc    = binder.getDescription();
			int         binderDescFmt = binderDesc.getFormat();

			// ...access the user's expansion state for the
			// ...description...
			StringRpcResponseData regionStateRpcData = getBinderRegionState(bs, request, binderId, "descriptionRegion");
			String regionState = regionStateRpcData.getStringValue();
			boolean expanded = (MiscUtil.hasString(regionState) ? regionState.equals("expanded") : true);
			
			// ...and use the information to construct and return a
			// ...BinderDescriptionRpcResponseData object with the
			// ...binder's description.
			return
				new BinderDescriptionRpcResponseData(
					binderDesc.getText(),
					(Description.FORMAT_HTML == binderDescFmt),
					expanded);
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
					filterMap.put(ksIT.next(), "global");
				}
			}
			
			// Does the user have any personal filters defined?
			UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			searchFilters = ((Map) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS));
			if ((null != searchFilters) && (!(searchFilters.isEmpty()))) {
				// Yes!  Add them to the sort map.
				Set<String> keySet = searchFilters.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					filterMap.put(ksIT.next(), "personal");
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
					String key = ksIT.next();
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,            viewAction               );
					url.setParameter(WebKeys.URL_BINDER_ID,     binder.getId().toString());
					url.setParameter(WebKeys.URL_OPERATION,     WebKeys.URL_SELECT_FILTER);
					url.setParameter(WebKeys.URL_OPERATION2,    filterMap.get(key)       );
					url.setParameter(WebKeys.URL_SELECT_FILTER, key                      );
					ffList.add(new BinderFilter(key, url.toString()));
				}
			}
			
			// Use the data we obtained to create a
			// BinderFiltersRpcResponseData.
			BinderFiltersRpcResponseData reply = new BinderFiltersRpcResponseData(ffList);
			
			// Store the current filter, if any, that the user
			// currently has selected on this binder.
			String currentFilter = ((String) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER));
			reply.setCurrentFilter((null == currentFilter) ? "" : currentFilter);

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
	 * Reads the current user's columns for a folder and returns them
	 * as a FolderColumnsRpcResponseData.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by folder_view_common2.jsp.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param folderType
	 * 
	 * @return
	 */
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, Long folderId, FolderType folderType) throws GwtTeamingException {
		Boolean includeConfigurationInfo = Boolean.FALSE;
		return getFolderColumns(bs, request, folderId, folderType, includeConfigurationInfo);
	}
	
	@SuppressWarnings("unchecked")
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, 
			Long folderId, FolderType folderType, Boolean includeConfigurationInfo) throws GwtTeamingException {
		try {
			Folder			folder               = ((Folder) bs.getBinderModule().getBinder(folderId));
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			
			Map    columnNames;
			Map    columnTitles      = null;
			String columnOrderString = null;
			List columnsAll = new ArrayList();

			// Are we showing the trash on this folder?
			if (FolderType.TRASH == folderType) {
				// Yes!  The columns in a trash view are not
				// configurable.  Use the default trash columns.
				columnNames = getColumnsLHMFromAS(TrashHelper.trashColumns);
			}
			
			else {
				// No, we aren't showing the trash on this folder!  Are
				// there user defined columns on this folder?
				columnNames = ((Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS));
				if (null == columnNames) {
					// No!  Are there defaults stored on the binder?
					columnNames = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS));
					if (null == columnNames) {
						// No!  Use the default as setup in
						// folder_column_defaults.jsp.
						String[] defaultCols;
						if (FolderType.FILE == folderType)
						     defaultCols = new String[]{"title", "comments", "size", "download", "html", "state", "author", "date"};
						else defaultCols = new String[]{"number", "title", "comments", "state", "author", "date", "rating"};
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
							("folder.column." + colName),	// Key to find the resource.
							colName,						// Default if not defined.
							true);							// true -> Silent.  Don't generate an error if undefined.
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
			fixupFCs(fcList);
			

			if (includeConfigurationInfo) {
				//Build a list of all possible columns
				Map<String,Definition> entryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
				for (Definition def :  entryDefs.values()) {
					@SuppressWarnings("unused")
					org.dom4j.Document defDoc = def.getDefinition();
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
										("folder.column." + colName),	// Key to find the resource.
										colTitleDefault,				// Default if not defined.
										true);							// true -> Silent.  Don't generate an error if undefined.
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
	 * @param folderId
	 * 
	 * @return
	 */
	public static FolderDisplayDataRpcResponseData getFolderDisplayData(AllModulesInjected bs, HttpServletRequest request, Long folderId) throws GwtTeamingException {
		try {
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
				sortBy      = Constants.SORTNUMBER_FIELD;
				sortDescend = true;
			}

			// How many entries per page should the folder display?
			int pageSize;
			try                  {pageSize = Integer.parseInt(MiscUtil.entriesPerPage(userProperties));}
			catch (Exception ex) {pageSize = 25;                                                       }
			
			// Finally, use the data we obtained to create a
			// FolderDisplayDataRpcResponseData and return that. 
			return new FolderDisplayDataRpcResponseData(sortBy, sortDescend, pageSize);
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
	 * @param folderId
	 * @param folderType
	 * @param folderColumns
	 * @param start
	 * @param length
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderRowsRpcResponseData getFolderRows(AllModulesInjected bs, HttpServletRequest request, Long folderId, FolderType folderType, List<FolderColumn> folderColumns, int start, int length) throws GwtTeamingException {
		try {
			Folder			folder               = ((Folder) bs.getBinderModule().getBinder(folderId));
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			SeenMap			seenMap              = bs.getProfileModule().getUserSeenMap(null);
			
			// Setup the current search filter the user has selected
			// on the folder.
			Map options = getFolderSearchFilter(bs, folder, userFolderProperties, null);
			options.put(ObjectKeys.SEARCH_OFFSET,   start );
			options.put(ObjectKeys.SEARCH_MAX_HITS, length);

			// Factor in the user's sorting selection.
			FolderDisplayDataRpcResponseData fdd = getFolderDisplayData(bs, request, folderId);
			options.put(ObjectKeys.SEARCH_SORT_BY,      fdd.getFolderSortBy()     );
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, fdd.getFolderSortDescend());

			// Read the entries based on the search.
			Map searchResults;
			if (FolderType.TRASH == folderType)
			     searchResults = TrashHelper.getTrashEntries(bs, new HashMap<String,Object>(), folder, options);
			else searchResults = bs.getFolderModule().getEntries(folderId, options);
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
			int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();

			// Is this the first page of a discussion folder?
			List<Long> pinnedEntryIds = new ArrayList<Long>();
			if ((0 == start) && (FolderType.DISCUSSION == folderType)) {
				// Yes!  Are there any entries pinned in the folder?
				List<Map>  pinnedEntrySearchMaps = getPinnedEntries(bs, folder, userFolderProperties, pinnedEntryIds);
				if (!(pinnedEntrySearchMaps.isEmpty())) {
					// Yes!  Add them to the beginning of the search
					// entries.
					searchEntries.addAll(0, pinnedEntrySearchMaps);
				}
			}

			// Scan the entries we read.
			boolean addedAssignments = false;
			List<FolderRow> folderRows     = new ArrayList<FolderRow>();
			List<Long>      contributorIds = new ArrayList<Long>();
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
				
				// Creating a FolderRow for each entry.
				FolderRow fr = new FolderRow(entryId, folderColumns);
				if (pinnedEntryIds.contains(entryId)) {
					fr.setPinned(true);
				}
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
						String csk     = fc.getColumnSearchKey();
						Object emValue = GwtServerHelper.getValueFromEntryMap(entryMap, csk);
						PrincipalInfo pi;
						if (emValue instanceof Principal)
						     pi = getPIFromPId(((Principal) emValue).getId());
						else pi = null;
						if (null == pi) {
							// No!  Does the column contain assignment
							// information?
							if (isAssignmentColumnSearchKey(csk)) {
								// Yes!  Process it for a
								// List<AssignmentInfo>'s.
								addedAssignments = true;
								fr.setColumnValue(fc, GwtServerHelper.getAssignmentInfoListFromEntryMap(entryMap, csk));
							}
							else {
								// No, the column doesn't contain
								// assignment information either!
								// Extract its String value.
								String value = GwtServerHelper.getStringFromEntryMapValue(
									emValue,
									DateFormat.SHORT,
									DateFormat.SHORT);
								
								// Are we working on a title field?
								if (csk.equals(Constants.TITLE_FIELD)) {
									// Yes!  Construct an
									// EntryTitleInfo for it.
									EntryTitleInfo eti = new EntryTitleInfo();
									eti.setSeen(seenMap.checkIfSeen(entryMap));
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
								
								else {
									// No, we aren't working on a file
									// ID field either!  Are we working
									// on a file size field?
									if (csk.equals(Constants.FILE_SIZE_FIELD)) {
										// Yes!  Trim any leading 0's
										// from the value.
										value = trimLeadingZeros(value);
										if (MiscUtil.hasString(value)) {
											value += "KB";
										}
									}
									
									// Use what ever String value we
									// arrived at.
									fr.setColumnValue(fc, (null == (value) ? "" : value));
								}
							}
						}
						
						else {
							// Yes, we got a PrincipalInfo!  Use that.
							fr.setColumnValue(fc, pi);
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
	private static PrincipalInfo getPIFromPId(Long pId) {
		// Can we resolve the ID to an actual Principal object?
		PrincipalInfo reply = null;
		List<Long> principalIds = new ArrayList<Long>();
		principalIds.add(pId);
		List principals = null;
		try {principals = ResolveIds.getPrincipals(principalIds);}
		catch (Exception ex) {/* Ignored. */}
		if ((null != principals) && (!(principals.isEmpty()))) {
			for (Object o:  principals) {
				// Yes!  Is it a User?
				Principal p = ((Principal) o);
				boolean isUser = (p instanceof UserPrincipal);
				if (isUser) {
					// Yes!  Construct the rest of the PrincipalInfo
					// required.
					pId = p.getId();
					reply = PrincipalInfo.construct(pId);
					reply.setTitle(p.getTitle());
					User user = ((User) p);
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

	/*
	 * Returns a map containing the search filter to use to read the
	 * rows from a folder.
	 */
	@SuppressWarnings("unchecked")
	private static Map getFolderSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, String searchTitle) {
		Map result = new HashMap();
		result.put(ObjectKeys.SEARCH_SEARCH_FILTER, BinderHelper.getSearchFilter(bs, binder, userFolderProperties));
		if (MiscUtil.hasString(searchTitle)) {
			result.put(ObjectKeys.SEARCH_TITLE, searchTitle);
		}
		return result;
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
			List<Document> pinnedFolderEntriesList = new ArrayList<Document>();
			SortedSet<FolderEntry> pinnedFolderEntriesSet = fm.getEntries(peSet);
			for (FolderEntry entry:  pinnedFolderEntriesSet) {
				// Is this entry still viable in this folder?
				if (!(entry.isPreDeleted()) && entry.getParentBinder().equals(folder)) {
					// Yes!  Track its ID...
					pinnedEntryIds.add(entry.getId());

					// ...and indexDoc.
					Document indexDoc = fm.buildIndexDocumentFromEntry(entry.getParentBinder(), entry, null);
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
				if (!(initVIFromUser(bs, GwtServerHelper.getUserFromId(bs, entryId), vi))) {
					m_logger.debug("GwtViewHelper.getViewInfo():  2:Could not determine a view.");
					return null;
				}
			}
			else if (entityType.equals("folder") || entityType.equals("workspace") || entityType.equals("profiles")) {
				// A folder, workspace or the profiles binder!  Setup
				// a binder view based on the binder ID.
				if (!(initVIFromBinderId(bs, nvMap, WebKeys.URL_BINDER_ID, vi, true))) {
					m_logger.debug("GwtViewHelper.getViewInfo():  3:Could not determine a view.");
					return null;
				}
			}
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_WS_LISTING) || action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING)) {
			// A view workspace or folder listing!  Setup a binder view
			// based on the binder ID.
			if (!(initVIFromBinderId(bs, nvMap, WebKeys.URL_BINDER_ID, vi, true))) {
				m_logger.debug("GwtViewHelper.getViewInfo():  4:Could not determine a view.");
				return null;
			}

			// Is this a view folder listing?
			Long binderId = vi.getBinderInfo().getBinderIdAsLong();
			if (action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING)) {
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
			}
			
			// Does it contain a filter selection?
			String op = getQueryParameterString(nvMap, WebKeys.URL_OPERATION);
			if (MiscUtil.hasString(op) && op.equals(WebKeys.URL_SELECT_FILTER)) {
				// Yes!  Apply the selection.
				String filterName = getQueryParameterString(nvMap, WebKeys.URL_SELECT_FILTER);
				ProfileModule pm = bs.getProfileModule();
				Long userId = GwtServerHelper.getCurrentUser().getId();
				bs.getProfileModule().setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, filterName);
				String op2 = getQueryParameterString(nvMap, WebKeys.URL_OPERATION2);
				if (MiscUtil.hasString(op2) && op2.equals("global"))
				     pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE, ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL  );
				else pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE, ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL);
			}
		}

		else if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
			// An add folder entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_FOLDER_ENTRY);
		}
		
		else if (action.equals(WebKeys.ACTION_ADVANCED_SEARCH)) {
			// An advanced search!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADVANCED_SEARCH);
		}
		
		else if (action.equals(WebKeys.ACTION_BUILD_FILTER)) {
			// A build filter!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.BUILD_FILTER);
		}
		
		// If we get here reply refers to the BinderInfo requested or
		// is null.  Return it.
		if (m_logger.isDebugEnabled()) {
			dumpViewInfo(vi);
		}
		return vi;
	}

	/*
	 * Initializes a ViewInfo based on a binder ID.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	private static boolean initVIFromBinderId(AllModulesInjected bs, Map<String, String> nvMap, String binderIdName, ViewInfo vi, boolean checkForTrash) {
		// Initialize as a binder based on the user's workspace.
		Long binderId = getQueryParameterLong(nvMap, binderIdName);
		BinderInfo bi = GwtServerHelper.getBinderInfo(bs, String.valueOf(binderId));
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
	private static boolean initVIFromUser(AllModulesInjected bs, User user, ViewInfo vi) {
		// Were we given a User object to initialize from?
		if (null == user) {
			// No!  Bail.
			return false;
		}

		// Initialize as a binder based on the user's workspace.
		BinderInfo bi = GwtServerHelper.getBinderInfo(bs, String.valueOf(user.getWorkspaceId()));
		if (null == bi) {
			return false;
		}
		vi.setViewType(ViewType.BINDER);
		vi.setBinderInfo(bi);
		
		return true;
	}

	/*
	 * Returns true if a column search key corresponds to an
	 * 'assignment' attribute or false otherwise.
	 */
	private static boolean isAssignmentColumnSearchKey(String csk) {
		boolean reply = false;
		if (MiscUtil.hasString(csk)) {
			reply =
				(csk.equals(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME)             ||
				 csk.equals(TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME)      ||
				 csk.equals(TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME)       ||
				 csk.equals(EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)        ||
				 csk.equals(EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME) ||
				 csk.equals(EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME));
		}
		return reply;
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

	/*
	 * Generates a value for a custom column in a row.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by folder_view_common2.jsp.
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

	/*
	 * Strips the leading 0's off a String value and returns it.
	 */
	private static String trimLeadingZeros(String value) {
		if (null != value) {
	        while (value.startsWith("0")) {
	        	value = value.substring(1, value.length());
	        }
	        if (value.equals("")) {
	        	value = "0";
	        }
		}
		return value;
	}
}
