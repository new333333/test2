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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo.BinderType;
import org.kablink.teaming.gwt.client.util.TreeInfo.FolderType;
import org.kablink.teaming.gwt.client.util.TreeInfo.WorkspaceType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;


/**
 * Helper methods for the GWT UI server code.
 *
 * @author drfoster@novell.com
 */
public class GwtServerHelper {
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
		TreeInfo binderTI = findBinderTI(ti, String.valueOf(binder.getId()));
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
	 * Builds a TreeInfo object for a given Binder.
	 *
	 * @param bs
	 * @param binder
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo buildTreeInfoFromBinder(AllModulesInjected bs, Binder binder) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinderImpl(bs, binder, null, false, 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(AllModulesInjected bs, Binder binder, List<Long> expandedBindersList) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinderImpl(bs, binder, expandedBindersList, (null != expandedBindersList), 0);
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
			List<Binder> childBinderList = GwtServerHelper.getVisibleBinderDecendents(bs, binder);
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
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 *
	 * @param bs
	 * @param binderId
	 */
	@SuppressWarnings("unchecked")
	public static void persistNodeCollapse(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		List<Long> usersExpandedBindersList = ((List<Long>) userProperties.getProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST));

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
			userProperties.setProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST, usersExpandedBindersList);
		}
	}

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void persistNodeExpand(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		List<Long> usersExpandedBindersList = ((List<Long>) userProperties.getProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST));

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
			userProperties.setProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST, usersExpandedBindersList);
		}
	}
	
	/**
	 * Returns the User object of the currently logged in user.
	 * @return
	 */
	public static User getCurrentUser() {
		return RequestContextHolder.getRequestContext().getUser();
	}

	/**
	 * Returns the TreeInfo from another TreeInfo that references a
	 * specific Binder ID.
	 */
	public static TreeInfo findBinderTI(TreeInfo ti, String binderId) {
		// If this TreeInfo is for the binder in question...
		if (ti.getBinderId().equals(binderId)) {
			// ...return it.
			return ti;
		}

		// Otherwise, if the TreeInfo has child Binder's...
		List<TreeInfo> childBindersList = ti.getChildBindersList();
		if ((null != childBindersList) && (0 < childBindersList.size())) {
			// ...scan them...
			for (Iterator<TreeInfo> tii = childBindersList.iterator(); tii.hasNext(); ) {
				// ...and if one of them references the Binder ID in
				// ...question...
				TreeInfo childTI = tii.next();
				TreeInfo reply = findBinderTI(childTI, binderId);
				if (null != reply) {
					// ...return it.
					return reply;
				}
			}
		}

		// If we get here, the binder ID was nowhere to be found in the
		// TreeInfo.  Return null.
		return null;
	}

	/*
	 * Takes a Binder permalink and does what's necessary to bring up
	 * the trash on that Binder.
	 */
	private static String getTrashPermalink(String binderPermalink) {
		String reply = binderPermalink;
		String marker;
		boolean useAmpersand = (0 < reply.indexOf("a/do?"));
		if (useAmpersand)
			 marker = ("&" + WebKeys.URL_SHOW_TRASH + "=true");
		else marker = ("/" + WebKeys.URL_SHOW_TRASH + "/true");
		reply += marker;
		return reply;
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
	 * Merges the expanded Binder list from the current User's
	 * preferences into those in expandedBindersList.  If the resultant
	 * lists differ, they are written back to the User's preferences
	 */
	@SuppressWarnings("unchecked")
	private static void mergeBinderExpansions(AllModulesInjected bs, List<Long> expandedBindersList) {
		// Access the current User's expanded Binder's list.
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		List<Long> usersExpandedBindersList = ((List<Long>) userProperties.getProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST));

		// Make sure we have two non-null lists to work with.
		if (null == expandedBindersList)      expandedBindersList      = new ArrayList<Long>();
		if (null == usersExpandedBindersList) usersExpandedBindersList = new ArrayList<Long>();

		// If the lists differ in size, will have write the new list
		// back to the User's properties no matter what.
		boolean updateUsersList = (expandedBindersList.size() != usersExpandedBindersList.size());
		
		// Scan the Binder ID's in the User's expanded Binder's list.
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this Binder ID in the list we were given?
			Long l = lIT.next();
			if (!(isLongInList(l, expandedBindersList))) {
				// No!  Added it to it and force the changes to be
				// written to the User's list.
				expandedBindersList.add(0, l);
				updateUsersList = true;
			}
		}

		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Write it.
			userProperties.setProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST, expandedBindersList);
		}
	}
}
