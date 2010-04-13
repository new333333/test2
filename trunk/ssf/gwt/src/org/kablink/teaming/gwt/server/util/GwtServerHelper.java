/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.HttpSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo.FolderType;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo.WorkspaceType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;


/**
 * Helper methods for the GWT UI server code.
 *
 * @author drfoster@novell.com
 */
public class GwtServerHelper {
	// String used to recognized an '&' formatted URL vs. a '/'
	// formatted permalink URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";
	
	/**
	 * Inner class used compare two TeamInfo objects.
	 */
	private static class TeamInfoComparator implements Comparator<TeamInfo> {
		/**
		 * Class constructor.
		 */
		public TeamInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * TeamInfo's.
		 *
		 * Returns:
		 *    -1 if ti1 <  ti2;
		 *     0 if ti1 == ti2; and
		 *     1 if ti1 >  ti2.
		 *     
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		public int compare(TeamInfo ti1, TeamInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getTitle(), ti2.getTitle());
		}
	}	   
	   
	/**
	 * Inner class used compare two TreeInfo objects.
	 */
	private static class TreeInfoComparator implements Comparator<TreeInfo> {
		/**
		 * Class constructor.
		 */
		public TreeInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * TreeInfo's.
		 *
		 * Returns:
		 *    -1 if ti1 <  ti2;
		 *     0 if ti1 == ti2; and
		 *     1 if ti1 >  ti2.
		 *     
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		public int compare(TreeInfo ti1, TreeInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getBinderTitle(), ti2.getBinderTitle());
		}
	}	   
	   
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtServerHelper() {
		// Nothing to do.
	}

	/**
	 * Adds a Trash folder to the TreeInfo.
	 * 
	 * Note:  At the point this gets called, it is assumed that the
	 *        caller has done whatever checks need to be done to
	 *        validate the user's access to the trash on this folder.
	 * 
	 * @param bs
	 * @param ti
	 * @param binder
	 */
	public static void addTrashFolder(AllModulesInjected bs, TreeInfo ti, Binder binder) {
		// Find the TreeInfo in question and copy it so we can make a
		// trash TreeInfo out of it.
		TreeInfo binderTI = TreeInfo.findBinderTI(ti, String.valueOf(binder.getId()));
		TreeInfo trashTI = binderTI.copyBaseTI();
		
		// Change the copy to a trash TreeInfo.
		if (BinderType.FOLDER == trashTI.getBinderType())
			 trashTI.setFolderType(   FolderType.TRASH   );
		else trashTI.setWorkspaceType(WorkspaceType.TRASH);
		trashTI.setBinderExpanded(false);
		trashTI.setBinderIconName(null);
		trashTI.setBinderTitle(NLT.get("profile.abv.element.trash"));
		String trashUrl = getTrashPermalink(trashTI.getBinderPermalink());
		trashTI.setBinderPermalink(     trashUrl);
		trashTI.setBinderTrashPermalink(trashUrl);

		// Finally, add the trash TreeInfo to the base TreeInfo's list
		// of children.
		List<TreeInfo> childBindersList = ti.getChildBindersList();
		if (null == childBindersList) {
			childBindersList = new ArrayList<TreeInfo>();
		}
		childBindersList.add(trashTI);
		ti.setChildBindersList(childBindersList);
	}

	/**
	 * Appends a parameter to to a URL.
	 * 
	 * @param urlString
	 * @param pName
	 * @param pValue
	 * 
	 * @return
	 */
	public static String appendUrlParam(String urlString, String pName, String pValue) {
		String param;
		boolean useAmpersand = (0 < urlString.indexOf(AMPERSAND_FORMAT_MARKER));
		if (useAmpersand)
			 param = ("&" + pName + "=" + pValue);
		else param = ("/" + pName + "/" + pValue);
		if (0 > urlString.indexOf(param)) {
			urlString += param;
		}
		return urlString;
	}
	
	/**
	 * Builds a TreeInfo object for a given Binder.
	 *
	 * @param bs
	 * @param binder
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo buildTreeInfoFromBinder(AllModulesInjected bs, Binder binder, List<Long> expandedBindersList) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinderImpl(bs, binder, expandedBindersList, (null != expandedBindersList), 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(AllModulesInjected bs, Binder binder) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinderImpl(bs, binder, null, false, 0);
	}
	
	/*
	 * Builds a TreeInfo object for a given Binder.
	 */
	private static TreeInfo buildTreeInfoFromBinderImpl(AllModulesInjected bs, Binder binder, List<Long> expandedBindersList, boolean mergeUsersExpansions, int depth) {
		// Construct the base TreeInfo for the Binder.
		TreeInfo reply = new TreeInfo();
		reply.setBinderId(binder.getId());
		reply.setBinderTitle(binder.getTitle());
		reply.setBinderChildren(binder.getBinderCount());
		String binderPermalink = PermaLinkUtil.getPermalink(binder);
		reply.setBinderPermalink(binderPermalink);
		reply.setBinderTrashPermalink(getTrashPermalink(binderPermalink));
		if (binder instanceof Workspace) {
			reply.setBinderType(BinderType.WORKSPACE);
			WorkspaceType wsType = WorkspaceType.OTHER;
			Workspace ws = ((Workspace) binder);
			if (ws.isReserved()) {
				if (ws.getInternalId().equals(ObjectKeys.TOP_WORKSPACE_INTERNALID)) wsType = WorkspaceType.TOP;
				if (ws.getInternalId().equals(ObjectKeys.TEAM_ROOT_INTERNALID))     wsType = WorkspaceType.TEAM_ROOT;
				if (ws.getInternalId().equals(ObjectKeys.GLOBAL_ROOT_INTERNALID))   wsType = WorkspaceType.GLOBAL_ROOT;
				if (ws.getInternalId().equals(ObjectKeys.PROFILE_ROOT_INTERNALID))  wsType = WorkspaceType.PROFILE_ROOT;
			}
			else {
				if      (BinderHelper.isBinderUserWorkspace(binder)) wsType = WorkspaceType.USER;
				else if (BinderHelper.isBinderTeamWorkspace(binder)) wsType = WorkspaceType.TEAM;
			}
			reply.setWorkspaceType(wsType);
		}
		else if (binder instanceof Folder) {
			reply.setBinderType(BinderType.FOLDER);
			reply.setBinderIconName(binder.getIconName());
			
//!			...this needs to be implemented...
		}

		// When requested to do so...
		if (mergeUsersExpansions) {
			// ...merge any User Binder expansions with those we were
			// ...given.
			mergeBinderExpansions(bs, expandedBindersList);
		}

		// Should this Binder should be expanded?
		boolean expandBinder = ((0 == depth) || isLongInList(binder.getId(), expandedBindersList));
		reply.setBinderExpanded(expandBinder);
		if (expandBinder) {
			// Yes!  Scan the Binder's children...
			List<TreeInfo> childTIList = reply.getChildBindersList(); 
			List<Binder> childBinderList = getVisibleBinderDecendents(bs, binder);
			for (Iterator<Binder> bi = childBinderList.iterator(); bi.hasNext(); ) {
				// ...creating a TreeInfo for each...
				Binder subBinder = bi.next();
				TreeInfo subWsTI = buildTreeInfoFromBinderImpl(bs, subBinder, expandedBindersList, false, (depth + 1));
				childTIList.add(subWsTI);
			}
			
			// ...and update the count of Binder children as it may
			// ...have changed based on visibility.
			reply.setBinderChildren(childBinderList.size());
		}

		// If there's more than one child being tracked for this
		// TreeInfo...
		List<TreeInfo> childBindersList = reply.getChildBindersList();
		if (1 < childBindersList.size()) {
			// ...sort them.
			Collections.sort(childBindersList, new TreeInfoComparator());
		}
		
		// If we get here, reply refers to the TreeInfo object for this
		// Binder.  Return it.
		return reply;
	}

	/**
	 * Builds a TreeInfo object for a given Binder, using a List<Long>
	 * of Binder IDs for its children.
	 *
	 * @param bs
	 * @param bindersList
	 * 
	 * @return
	 */
	public static List<TreeInfo> buildTreeInfoFromBinderList(AllModulesInjected bs, List<Long> bindersList) {
		ArrayList<TreeInfo> reply = new ArrayList<TreeInfo>();
		for (Iterator<Long> lIT = bindersList.iterator(); lIT.hasNext(); ) {
			Binder binder = bs.getBinderModule().getBinder(lIT.next());
			reply.add(buildTreeInfoFromBinderImpl(bs, binder, null, false, (-1)));
		}
		return reply;
	}
	
	/**
	 * Returns the User object of the currently logged in user.
	 * 
	 * @return
	 */
	public static User getCurrentUser() {
		return RequestContextHolder.getRequestContext().getUser();
	}

	/**
	 * Returns the current HttpSession, if accessible.
	 * 
	 * @return
	 */
	public static HttpSession getCurrentHttpSession() {
		HttpSession reply = null;
		RequestContext rc = RequestContextHolder.getRequestContext();
		SessionContext sc = rc.getSessionContext();
		if (sc instanceof HttpSessionContext) {
			reply = ((HttpSessionContext) sc).getHttpSession();
		}
		return reply;
	}

	/*
	 * Returns a cloned copy of the expanded Binder's list from the
	 * UserProperties.
	 */
	@SuppressWarnings("unchecked")
	private static List<Long> getExpandedBindersList(AllModulesInjected bs) {
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		List<Long> reply= ((List<Long>) userProperties.getProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST));
		return reply;
	}
	
	/**
	 * Returns information about the current user's favorites.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<FavoriteInfo> getFavorites(AllModulesInjected bs) {
		// Allocate an ArrayList<FavoriteInfo> to hold the favorites.
		ArrayList<FavoriteInfo> reply = new ArrayList<FavoriteInfo>();

		// Read the user's favorites.
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		Object userFavorites = userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites favorites;
		if (userFavorites instanceof Document) {
			favorites = new Favorites((Document)userFavorites);
			bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, favorites.toString());
		} else {		
			favorites = new Favorites((String)userFavorites);
		}
		
		// Scan the user's favorites...
		List favoritesList = favorites.getFavoritesList();
		for (Iterator flIT = favoritesList.iterator(); flIT.hasNext(); ) {
			// ...constructing a FavoriteInfor object for each.
			reply.add(fiFromJSON((JSONObject) flIT.next()));
		}

		// If we get here, reply refers to an ArrayList<FavoriteInfo>
		// of the user's defined favorites.  Return it.
		return reply;
	}

	/*
	 * Constructs a FavoriteInfo object from a JSONObject.
	 */
	private static FavoriteInfo fiFromJSON(JSONObject jso) {
		FavoriteInfo reply = new FavoriteInfo();
		
		reply.setAction(  getSFromJSO(jso, "action"  ));
		reply.setCategory(getSFromJSO(jso, "category"));
		reply.setEletype( getSFromJSO(jso, "eletype" ));
		reply.setHover(   getSFromJSO(jso, "hover"   ));
		reply.setId(      getSFromJSO(jso, "id"      ));
		reply.setName(    getSFromJSO(jso, "name"    ));
		reply.setType(    getSFromJSO(jso, "type"    ));
		reply.setValue(   getSFromJSO(jso, "value"   ));
		
		return reply;
	}
	
	/*
	 * Extracts a non-null string from a JSONObject.
	 */
	private static String getSFromJSO(JSONObject jso, String key) {
		String reply;
		if (jso.has(key)) {
			reply = jso.getString(key);
			if (null == reply) {
				reply = "";
			}
		}
		else {
			reply = "";
		}
		return reply;
	}
	
	/**
	 * Returns information about the teams the current user is a member of.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<TeamInfo> getMyTeams(AllModulesInjected bs) {
		// Allocate an ArrayList<TeamInfo> to hold the teams.
		ArrayList<TeamInfo> reply = new ArrayList<TeamInfo>();
		
		// Scan the teams the current user is a member of...
		User user = getCurrentUser();
		List<Map> myTeams = bs.getBinderModule().getTeamMemberships( user.getId() );
		for (Iterator<Map> myTeamsIT = myTeams.iterator(); myTeamsIT.hasNext(); ) {
			// ...adding a TeamInfo for each to the reply list.
			Map myTeam = myTeamsIT.next();
			TeamInfo ti = new TeamInfo();
			ti.setBinderId(   ((String) myTeam.get(      "_docId"     )));
			ti.setEntityPath( ((String) myTeam.get(      "_entityPath")));
			ti.setPermalink(  PermaLinkUtil.getPermalink( myTeam       ));
			ti.setTitle(      ((String) myTeam.get(      "title"      )));
			reply.add(ti);
		}
		
		// If there's more than one team being returned...
		if (1 < reply.size()) {
			// ...sort them.
			Collections.sort(reply, new TeamInfoComparator());
		}
		
		// If we get here, reply refers to the ArrayList<TeamInfo> of
		// the teams the current user is a member of.  Return it.
		return reply;
	}
	
	/*
	 * Takes a Binder permalink and does what's necessary to bring up
	 * the trash on that Binder.
	 */
	private static String getTrashPermalink(String binderPermalink) {
		return appendUrlParam(binderPermalink, WebKeys.URL_SHOW_TRASH, "true");
	}
	
	/**
	 * Returns a List<Binder> of the child Binder's of binder that are
	 * visible to the user.
	 * 
	 * Note:  For a way to do this using the search index, start with
	 *    TrashHelper.containsVisibleBinders().
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Binder> getVisibleBinderDecendents(AllModulesInjected bs, Binder binder) {
		ArrayList<Binder> reply = new ArrayList<Binder>();
		
		List allSubBinders = binder.getBinders();
		for (Iterator bi = allSubBinders.iterator(); bi.hasNext(); ) {
			Binder subBinder = ((Binder) bi.next());
			if (bs.getBinderModule().testAccess(subBinder, BinderOperation.readEntries)) {
				reply.add(0, subBinder);
			}
		}
		
		return reply;
	}

	/*
	 * Returns true if a Long is in a List<Long> and false otherwise.
	 */
	private static boolean isLongInList(Long l, List<Long> lList) {
		if ((null != l) && (null != lList)) {
			long lv = l.longValue();
			for (Iterator<Long> lIT = lList.iterator(); lIT.hasNext(); ) {
				if (lv == lIT.next().longValue()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Returns true if two List<Long>'s contain different values and
	 * false otherwise.
	 */
	private static boolean longListsDiffer(List<Long> list1, List<Long> list2) {
		// If the lists differ in size...
		int c1 = ((null == list1) ? 0 : list1.size());
		int c2 = ((null == list2) ? 0 : list2.size());
		if (c1 != c2) {
			// ...they're different.
			return true;
		}
		
		// If they're both empty...
		if (0 == c1) {
			// ...they're the same.
			return false;
		}

		// Scan the Long's in the first list.
		for (Iterator<Long> l1IT = list1.iterator(); l1IT.hasNext(); ) {
			long l1 = l1IT.next().longValue();
			boolean found1in2 = false;
			
			// Scan the Long's in the second list.
			for (Iterator<Long> l2IT = list2.iterator(); l2IT.hasNext(); ) {
				// If we find a Long from the first list in the second
				// list...
				long l2 = l2IT.next().longValue();
				found1in2 = (l1 == l2);
				if (found1in2) {
					// ...quit looking.
					break;
				}
			}
			
			// If we failed to find a Long from the first list in the
			// second list...
			if (!found1in2) {
				// ...the lists are different.
				return true;
			}
		}
		
		// If we get here, the lists are the same.  Return
		// false.
		return false;
	}
	
	/*
	 * Merges the expanded Binder list from the current User's
	 * preferences into those in expandedBindersList.  If the resultant
	 * lists differ, they are written back to the User's preferences
	 */
	private static void mergeBinderExpansions(AllModulesInjected bs, List<Long> expandedBindersList) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// Make sure we have two non-null lists to work with.
		if (null == expandedBindersList)      expandedBindersList      = new ArrayList<Long>();
		if (null == usersExpandedBindersList) usersExpandedBindersList = new ArrayList<Long>();

		// Scan the Binder ID's in the User's expanded Binder's list.
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this Binder ID in the list we were given?
			Long l = lIT.next();
			if (!(isLongInList(l, expandedBindersList))) {
				// No!  Added it to it and force the changes to be
				// written to the User's list.
				expandedBindersList.add(0, l);
			}
		}

		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (longListsDiffer(usersExpandedBindersList, expandedBindersList)) {
			// Yes!  Write it.
			setExpandedBindersList(bs, expandedBindersList);
		}
	}
	
	/**
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 *
	 * @param bs
	 * @param binderId
	 */
	public static void persistNodeCollapse(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// If there's no list...
		if (null == usersExpandedBindersList) {
			// ...we don't have to do anything since we only store
			// ...the IDs of expanded binders.  Bail.
			return;
		}

		// Scan the User's expanded Binder's list.
		boolean updateUsersList = false;
		long binderIdVal = binderId.longValue();
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this the Binder in question?
			Long l = lIT.next();
			if (l.longValue() == binderIdVal) {
				// Yes, remove it from the list, force the list to be
				// written back out and quit looking.
				usersExpandedBindersList.remove(l);
				updateUsersList = true;
				break;
			}
		}
		
		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Write it.
			setExpandedBindersList(bs, usersExpandedBindersList);
		}
	}

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param bs
	 * @param binderId
	 */
	public static void persistNodeExpand(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// If there's no list...
		boolean updateUsersList = (null == usersExpandedBindersList);
		if (updateUsersList) {
			// ...we need to create one...
			usersExpandedBindersList = new ArrayList<Long>();
		}
		
		else {
			// ...otherwise, we look for the Binder ID in that list.
			updateUsersList = (!(isLongInList(binderId, usersExpandedBindersList)));
		}
		
		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Add this Binder ID to it and write it.
			usersExpandedBindersList.add(0, binderId);
			setExpandedBindersList(bs, usersExpandedBindersList);
		}
	}
	
	/*
	 * Stores the expanded Binder's List in a UserProperties.
	 */
	private static void setExpandedBindersList(AllModulesInjected bs, List<Long> expandedBindersList) {
		bs.getProfileModule().setUserProperty(
			null,
			ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST,
			((null == expandedBindersList) ?
				new ArrayList<Long>()      :
				expandedBindersList));
	}
}
