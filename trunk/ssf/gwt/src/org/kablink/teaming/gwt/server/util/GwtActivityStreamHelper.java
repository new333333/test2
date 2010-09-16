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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

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
	 * Adds the required comments to an activity stream entry object.
	 */
	@SuppressWarnings("unchecked")
	public static void addASEComments(HttpServletRequest request, AllModulesInjected bs, ActivityStreamEntry ase, int comments, Map<String, String> avatarCache, Map em) {
		// Does the activity stream entry refer to a folder entry?
		String feId = ase.getEntryId();
		if (!(MiscUtil.hasString(feId))) {
			// No!  Then we can't return it's comments.
			return;
		}
	}
	
	/*
	 * Returns an activity stream entry based on the entry map from a
	 * search.
	 */
	@SuppressWarnings("unchecked")
	public static ActivityStreamEntry buildASEFromEM(HttpServletRequest request, AllModulesInjected bs, ActivityStreamParams asp, Map<String, String> avatarCache, Map em, boolean baseEntry) {
		// Create the activity stream entry to return.
		ActivityStreamEntry reply = new ActivityStreamEntry();

		// First, initialize the author information.
		String authorId = getSFromEM(em, Constants.MODIFICATIONID_FIELD);
		reply.setAuthorId(authorId);
		String authorName = getSFromEM(em, Constants.MODIFICATION_TITLE_FIELD);
		String authorLogin = getSFromEM(em, Constants.MODIFICATION_NAME_FIELD);
		if (MiscUtil.hasString(authorLogin)) {
			authorName += (" (" + authorLogin + ")");
		}
		reply.setAuthorName(authorName);
		String authorAvatarUrl = "";
		if (MiscUtil.hasString(authorId)) {
			Long authorWsId;
			try                  {authorWsId = bs.getProfileModule().getEntry(Long.parseLong(authorId)).getWorkspaceId();}
			catch (Exception ex) {authorWsId = null;                                                                     }
			if (null != authorWsId) {
				reply.setAuthorWorkspaceId(String.valueOf(authorWsId));
			}
			
			// Do we have the author's avatar cached?
			String urlFromCache = avatarCache.get(authorId);
			if (null != urlFromCache) {
				// Yes!  Use that rather than trying to re-read it.
				authorAvatarUrl = urlFromCache;
			}
			
			// No, we don't have the author's avatar cached!  Do we
			// have their workspace ID?
			else if (null != authorWsId) {
				// Yes!  Can we access any avatars for the author?
				String paUrl = null;
				ProfileAttribute pa = GwtProfileHelper.getProfileAvatars(request, bs, authorWsId);
				List<ProfileAttributeListElement> paValue = ((List<ProfileAttributeListElement>) pa.getValue());
				if((null != paValue) && (!(paValue.isEmpty()))) {
					// Yes!  We'll use the first one as the URL.
					ProfileAttributeListElement paValueItem = paValue.get(0);
					paUrl = paValueItem.getValue().toString();
				}
				
				// Store something as the author's URL so that we don't
				// try to look it up again.
				authorAvatarUrl = ((null == paUrl) ? "" : paUrl);
				avatarCache.put(authorId, authorAvatarUrl);
			}
		}
		reply.setAuthorAvatarUrl(authorAvatarUrl);

		// Then, the entry information.
		reply.setEntryDescription(     getSFromEM(em, Constants.DESC_FIELD              ));	
		reply.setEntryDocNum(          getSFromEM(em, Constants.DOCNUMBER_FIELD         ));
		reply.setEntryId(              getSFromEM(em, Constants.DOCID_FIELD             ));
		reply.setEntryModificationDate(getSFromEM(em, Constants.MODIFICATION_DATE_FIELD ));		
		reply.setEntryTitle(           getSFromEM(em, Constants.TITLE_FIELD             ));
		reply.setEntryTopEntryId(      getSFromEM(em, Constants.ENTRY_TOP_ENTRY_ID_FIELD));
		reply.setEntryType(            getSFromEM(em, Constants.ENTRY_TYPE_FIELD        ));

		// And finally, the parent binder information.  Does the entry
		// map contain the ID of the binder that contains the entry?
		String binderId = getSFromEM(em, Constants.BINDER_ID_FIELD);
		reply.setParentBinderId(binderId);
		if (MiscUtil.hasString(binderId)) {
			Binder binder;
			try                  {binder = bs.getBinderModule().getBinder(Long.valueOf(binderId));}
			catch (Exception ex) {binder = null;}
			if (null != binder) {
				reply.setParentBinderHover(binder.getPathName());
				reply.setParentBinderName( binder.getTitle()   );
			}
		}

		// Are we working on a base activity stream entry for an
		// activity stream data object? 
		if (baseEntry) {
			// Yes!  Do we need to include comments in this activity
			// stream entry?
			int comments = asp.getActiveComments();
			if (0 < comments) {
				// Yes!  Add them.
				addASEComments(request, bs, reply, comments, avatarCache, em);
			}
		}

		// If we get here, reply refers to the activity stream entry
		// for the entry map.  Return it.
		return reply;
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
		// Create an activity stream data object to return.
		ActivityStreamData reply = new ActivityStreamData();
		
		// If we weren't given a PagingData...
		boolean newData = (null == pd);
		if (newData) {
			// ...create one...
			pd = reply.getPagingData();
			pd.initializePaging(asp);
		}
		
		else {
			// ...otherwise, put the one we were given into effect.
			reply.setPagingData(pd);
		}
		
		// Finally, read the requested activity stream data.
		populateASD(
			reply,
			request,
			bs,
			asp,
			newData,
			asi);		
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

	/*
	 * Returns a string for a value out of the entry map from a search
	 * results.
	 */
	@SuppressWarnings("unchecked")
	private static String getSFromEM(Map entryMap, String key) {
		// Do we have entry data for this key?
		String reply = "";
		Object emData = entryMap.get(key);
		if (null != emData) {
			// Yes!  Is it a string?
			if (emData instanceof String) {
				// Yes!  Return it directly.
				reply = ((String) emData);
			}
			
			// No, it isn't a string!  Is it a date?
			else if (emData instanceof Date) {
				// Yes!  Format it for the current user's locale and
				// return that.
				User user = GwtServerHelper.getCurrentUser();
				DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, user.getLocale());
				reply = df.format(((Date) emData));
			}
			
			else {
				// No, it isn't a date either!  Let the object convert
				// itself to a string and return that.
				reply = emData.toString();
			}
		}
		
		// If we get here, reply refers to an empty string or the
		// appropriate string value for the key from the entry map.
		// Return it.
		return reply;
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
		ProfileModule pm = bs.getProfileModule();
		User user;
		
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
				// Can we access the next one's User?
				id = followedPersonId;
				user = GwtServerHelper.getUserSafely(pm, id);
				if (null != user) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIds[idIndex++] = id;
					asTIChild = buildASTI(bs, id, user.getWSTitle(), null, ActivityStream.FOLLOWED_PERSON);					
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
	
	/*
	 * Reads the activity stream data based on an activity stream
	 * information object and the current paging data.
	 */
	@SuppressWarnings("unchecked")
	private static void populateASD(ActivityStreamData asd, HttpServletRequest request, AllModulesInjected bs, ActivityStreamParams asp, boolean newData, ActivityStreamInfo asi) {		
		// Setup some int's for the controlling the search.
		PagingData pd				= asd.getPagingData();
		int        entriesPerPage	= pd.getEntriesPerPage();
		int        pageIndex		= pd.getPageIndex();
		int        pageStart		= (pageIndex * entriesPerPage);

		// Initialize lists for the tracked places and people.
		List<String> trackedPlacesAL = new ArrayList<String>();
		List<String> trackedPeopleAL = new ArrayList<String>();

		// What type of activity stream are we reading the data for?
		String[] trackedPlaces = asi.getBinderIds();
		switch (asi.getActivityStream()) {
		case FOLLOWED_PEOPLE:
		case FOLLOWED_PERSON:
			// Followed people/person:
			// 1. There are no tracked places; and
			// 2. The tracked people are the owner IDs of the places.
			sAToL(trackedPlaces, trackedPeopleAL);
			trackedPlaces = new String[0];
			
			break;
			
		case CURRENT_BINDER:
		case FOLLOWED_PLACES:
		case FOLLOWED_PLACE:
		case MY_FAVORITES:
		case MY_FAVORITE:
		case MY_TEAMS:
		case MY_TEAM:
			// A place of some sort:
			// 1. The tracked places is used unchanged; and
			// 2. There are no tracked people.
			break;

		default:
		case SITE_WIDE:
			// The entire site:
			// 1. The tracked places is the ID of the top workspace; and
			// 2. There are no tracked people.
			trackedPlaces = new String[]{String.valueOf(bs.getWorkspaceModule().getTopWorkspace().getId().longValue())};
			break;
		}

		// Store the tracked places into the ArrayList for them.
		sAToL(trackedPlaces, trackedPlacesAL);

		// Perform the search and extract the results.
		Criteria searchCriteria = SearchUtils.entriesForTrackedPlacesAndPeople(bs, trackedPlacesAL, trackedPeopleAL);
		Map      searchResults  = bs.getBinderModule().executeSearchQuery(searchCriteria, pageStart, entriesPerPage);
		List     searchEntries  = ((List)    searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
		int      totalRecords   = ((Integer) searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();

		// Update the paging data in the activity stream data.
    	pd.setTotalRecords(totalRecords);
    	asd.setPagingData(pd);

    	// Are there any entries in the search results?
		Map<String, Binder> whatsNewPlaces = (newData ? new HashMap<String, Binder>() : null);
    	if ((null != searchEntries) && (!(searchEntries.isEmpty()))) {
    		// Yes!  Get the list to hold the activity stream
    		// entries and scan the search results. 
    		Map<String, String> avatarCache = new HashMap<String, String>();
        	List<ActivityStreamEntry> entries = asd.getEntries();
	    	for (Iterator it = searchEntries.iterator(); it.hasNext(); ) {
	    		// Construct and add an activity stream entry for the
	    		// next entry map from the search results. 
	    		ActivityStreamEntry entry = buildASEFromEM(
	    			request,
	    			bs,
	    			asp,
	    			avatarCache,
	    			((Map) it.next()),
	    			true);	// true -> This is a base activity stream entry for an activity stream data.
    			entries.add(entry);

    			// Are we constructing new data for which we need to
    			// cache the binders?
    			if (newData) {
	    			// Yes!  Do we have an ID for the binder that
    				// contains this entry?
					String parentBinderId = entry.getParentBinderId();
					if (MiscUtil.hasString(parentBinderId)) {
						// Yes!  Are we already tracking it as a what's new place?
						if (!(whatsNewPlaces.containsKey(parentBinderId))) {
							try {
								// No!  Track it now.
								Binder parentBinder = bs.getBinderModule().getBinder(new Long(parentBinderId));
								whatsNewPlaces.put(parentBinderId, parentBinder);
							}
							catch(Exception e) {/* Ignore.*/}
						}
					}
    			}
	    	}
    	}

    	// Are we constructing new data for which we need to cache the
    	// binders?
    	if (newData) {
    		// Yes!  What to do with:  whatsNewPlaces
//!			...this needs to be implemented...
    	}
	}
	
	/*
	 * Stores the strings from a String[] into a List<String>.
	 */
	private static void sAToL(String[] sA, List<String> sL) {
		int c = ((null == sA) ? 0 : sA.length);
		for (int i = 0; i < c; i += 1) {
			sL.add(sA[i]);
		}
	}
}
