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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code that services activity
 * stream requests.
 *
 * @author drfoster@novell.com
 */
public class GwtActivityStreamHelper {
	protected static Log m_logger = LogFactory.getLog(GwtActivityStreamHelper.class);

	// Various control values pulled from the ssf*.properties.
	public static ActivityStreamParams m_activityStreamParams;
	static {
		m_activityStreamParams = new ActivityStreamParams();
		m_activityStreamParams.setActiveComments(SPropsUtil.getInt("activity.stream.active.comments",         2));
		m_activityStreamParams.setLookback(      SPropsUtil.getInt("activity.stream.interval.lookback",      11));
		m_activityStreamParams.setClientRefresh( SPropsUtil.getInt("activity.stream.interval.refresh.client", 5));
		m_activityStreamParams.setCacheRefresh(  SPropsUtil.getInt("activity.stream.interval.refresh.cache",  1));
		m_activityStreamParams.setEntriesPerPage(SPropsUtil.getInt("folder.records.listed",                  25));
		m_activityStreamParams.setMaxHits(       SPropsUtil.getInt("activity.stream.maxhits",              1000));
	}

	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtActivityStreamHelper() {
		// Nothing to do.
	}

	/*
	 * Builds an ActivityStreamInfo object based on an ActivityStream
	 * enumeration value and an String[] of Binder IDs.
	 */
	private static ActivityStreamInfo buildASI(ActivityStream as, String[] asIds, String title) {
		ActivityStreamInfo reply = new ActivityStreamInfo();

		reply.setActivityStream(as);
		if ((null != asIds) && ((1 < asIds.length) || (null != asIds[0]))) {
			reply.setBinderIds(asIds);
		}
		reply.setTitle(title);
		
		return reply;
	}
	
	private static ActivityStreamInfo buildASI(ActivityStream as, String asId, String title) {
		// Always use the initial form of the method.
		return buildASI(as, new String[]{asId}, title);
	}

	/*
	 * Builds an TreeInfo object based on a Binder's ID, title, hover
	 * text and an ActivityStream enumeration value.
	 */
	private static TreeInfo buildASTI(AllModulesInjected bs, String id, String title, String hover, ActivityStream as) {
		TreeInfo reply = new TreeInfo();
		if (MiscUtil.hasString(id)) {
			reply.setBinderInfo(GwtServerHelper.getBinderInfo(bs, id));
		}
		
		reply.setActivityStream(true);
		reply.setBinderTitle(title);
		reply.setBinderHover(hover);
		reply.setActivityStreamAction(
			TeamingAction.ACTIVITY_STREAM,
			buildASI(
				as,
				id,
				title));
		
		return reply;
	}
	
	/**
	 * Returns a ActivityStreamData of corresponding to activity stream
	 * parameters, paging data and info provided.
	 * 
	 * @param request
	 * @param bs
	 * @param asp
	 * @param asi
	 * @param pagingData - null -> Start fresh at page 0.
	 * 
	 * @return
	 */
	public static ActivityStreamData getActivityStreamData(HttpServletRequest request, AllModulesInjected bs, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd) {
		// Create an ActivityStreamData to return.
		ActivityStreamData reply = new ActivityStreamData();
		
		// If we weren't given a PagingData...
		boolean newData = (null == pd);
		if (newData) {
			// ...create one...
			pd = reply.getPagingData();
			pd.initializePaging(asp);
		}
		
		else {
			// ...otherwise, put the one we were given into affect.
			reply.setPagingData(pd);
		}
		
//!		...this needs to be implemented...
		
		return reply;
	}
	
	/**
	 * Returns an ActivityStreamParams object containing information
	 * the current activity stream setup.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static ActivityStreamParams getActivityStreamParams(AllModulesInjected bs) {
		// Create a copy of the base ActivityStreamParams object.
		ActivityStreamParams reply = m_activityStreamParams.copyBaseASP();

		try {
			// Read the user's entries per page setting and store it in
			// the ActivityStreamParams.
			String eppS = MiscUtil.entriesPerPage(bs.getProfileModule().getUserProperties(null));
			int epp = Integer.parseInt(eppS);
			reply.setEntriesPerPage(epp);
		}
		catch (Exception ex) {
			// Ignore.  With any error, will just use the default from
			// the base ActivityStreamsParam.
		}
		
		// If we get here, ActivityStreamParams refers to the
		// ActivityStreamParams to return.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if the data for an activity stream has changed (or
	 * has never been cached) and false otherwise.
	 * 
	 * @param request
	 * @param bs
	 * @param asi
	 * 
	 * @return
	 */
	public static Boolean hasActivityStreamChanged(HttpServletRequest request, AllModulesInjected bs, ActivityStreamInfo asi) {
//!		...this needs to be implemented...
		return Boolean.FALSE;
	}	
	/**
	 * Returns a TreeInfo object containing the display information for
	 * and activity streams tree using the current Binder referred to
	 * by binderId from the perspective of the currently logged in
	 * user.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget when in activity streams
	 * mode.
	 * 
	 * @param request
	 * @param bs
	 * @param binderIdS
	 * 
	 * @return
	 */
	public static TreeInfo getVerticalActivityStreamsTree(HttpServletRequest request, AllModulesInjected bs, String binderIdS) {
		Binder binder;
		BinderModule bm = bs.getBinderModule();
		TreeInfo reply = new TreeInfo();
		reply.setActivityStream(true);
		reply.setBinderTitle(NLT.get("asTreeWhatsNew"));
		List<TreeInfo> rootASList = reply.getChildBindersList();
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderSafely(bm, binderIdS);
		if (null != binder) {
			// Yes!  Build a TreeInfo for it.
			rootASList.add(
				buildASTI(
					bs,
					binderIdS,
					binder.getTitle(),
					binder.getPathName(),
					ActivityStream.CURRENT_BINDER));
		}

		// Define the variables required to build the TreeInfo's for
		// the other activity stream's.
		int idCount;
		int idIndex;
		List<TreeInfo> asTIChildren;
		String id;
		String[] asIds;
		TreeInfo asTI;
		TreeInfo asTIChild;

		// Does the user have any favorites defined?
		asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeMyFavorites"));
		List<FavoriteInfo> myFavoritesList = GwtServerHelper.getFavorites(bs);
		idCount = ((null == myFavoritesList) ? 0 : myFavoritesList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			asTIChildren = asTI.getChildBindersList();
			asIds = new String[idCount];
			idIndex = 0;
			for (FavoriteInfo myFavorite: myFavoritesList) {
				// Can we access the next one's Binder?
				id = myFavorite.getValue();
				binder = GwtServerHelper.getBinderSafely(bm, id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIds[idIndex++] = id;					
					asTIChild = buildASTI(bs, id, myFavorite.getName(), myFavorite.getHover(), ActivityStream.MY_FAVORITE);
					asTIChildren.add(asTIChild);
				}
			}
			
			// Update the parent TreeInfo.
			asTI.updateChildBindersCount();
			asTI.setActivityStreamAction(
				TeamingAction.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.MY_FAVORITES,
					asIds,
					asTI.getBinderTitle()));
		}
		
		// Add the favorites TreeInfo to the root TreeInfo.
		rootASList.add(asTI);
		
		// Is the user a member of any teams?
		asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeMyTeams"));
		List<TeamInfo> myTeamsList = GwtServerHelper.getMyTeams(request, bs);
		idCount = ((null == myTeamsList) ? 0 : myTeamsList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			asTIChildren = asTI.getChildBindersList();
			asIds = new String[idCount];
			idIndex = 0;
			for (TeamInfo myTeam: myTeamsList) {
				// Can we access the next one's Binder?
				id = myTeam.getBinderId();
				binder = GwtServerHelper.getBinderSafely(bm, id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIds[idIndex++] = id;					
					asTIChild = buildASTI(bs, id, myTeam.getTitle(), binder.getPathName(), ActivityStream.MY_TEAM);					
					asTIChildren.add(asTIChild);
				}
			}
			
			// Update the parent TreeInfo.
			asTI.updateChildBindersCount();
			asTI.setActivityStreamAction(
				TeamingAction.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.MY_TEAMS,
					asIds,
					asTI.getBinderTitle()));
		}
		
		// Add the teams TreeInfo to the root TreeInfo.
		rootASList.add(asTI);
		
		// Is the user following any people?
		asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeFollowedPeople"));
		List<String> followedPeopleList = GwtServerHelper.getTrackedPeople(bs);
		idCount = ((null == followedPeopleList) ? 0 : followedPeopleList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			asTIChildren = asTI.getChildBindersList();
			asIds = new String[idCount];
			idIndex = 0;
			for (String followedPersonId: followedPeopleList) {
				// Can we access the next one's Binder?
				id = followedPersonId;
				binder = GwtServerHelper.getBinderSafely(bm, id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIds[idIndex++] = id;					
					asTIChild = buildASTI(bs, id, binder.getTitle(), binder.getPathName(), ActivityStream.FOLLOWED_PERSON);					
					asTIChildren.add(asTIChild);
				}
			}
			
			// Update the parent TreeInfo.
			asTI.updateChildBindersCount();
			asTI.setActivityStreamAction(
				TeamingAction.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.FOLLOWED_PEOPLE,
					asIds,
					asTI.getBinderTitle()));
		}
		
		// Add the followed people TreeInfo to the root TreeInfo.
		rootASList.add(asTI);
		
		// Is the user following any places?
		asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeFollowedPlaces"));
		List<String> followedPlacesList = GwtServerHelper.getTrackedPlaces(bs);
		idCount = ((null == followedPlacesList) ? 0 : followedPlacesList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			asTIChildren = asTI.getChildBindersList();
			asIds = new String[idCount];
			idIndex = 0;
			for (String followedPlaceId: followedPlacesList) {
				// Can we access the next one's Binder?
				id = followedPlaceId;
				binder = GwtServerHelper.getBinderSafely(bm, id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIds[idIndex++] = id;
					asTIChild = buildASTI(bs, id, binder.getTitle(), binder.getPathName(), ActivityStream.FOLLOWED_PLACE);					
					asTIChildren.add(asTIChild);
				}
			}
			
			// Update the parent TreeInfo.
			asTI.updateChildBindersCount();
			asTI.setActivityStreamAction(
				TeamingAction.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.FOLLOWED_PLACES,
					asIds,
					asTI.getBinderTitle()));
		}
		
		// Add the followed places TreeInfo to the root TreeInfo.
		rootASList.add(asTI);
		
		// We always have a Site Wide.
		rootASList.add(
			buildASTI(
				bs,
				null,	// null -> No ID.
				NLT.get("asTreeSiteWide"),
				null,	// null -> No hover text.
				ActivityStream.SITE_WIDE));

		// Finally, ensure the child binder count in the TreeInfo that
		// we're returning is correct and return it.
		reply.updateChildBindersCount();
		return reply;
	}	
}
