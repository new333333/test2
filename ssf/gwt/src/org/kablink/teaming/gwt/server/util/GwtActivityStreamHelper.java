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

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MarkupUtil;
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
		m_activityStreamParams.setActivityStreamsOnLogin(GwtUIHelper.isActivityStreamOnLogin());
		m_activityStreamParams.setActiveComments(        SPropsUtil.getInt("activity.stream.active.comments",         2));
		m_activityStreamParams.setLookback(              SPropsUtil.getInt("activity.stream.interval.lookback",      11));
		m_activityStreamParams.setClientRefresh(         SPropsUtil.getInt("activity.stream.interval.refresh.client", 5));
		m_activityStreamParams.setCacheRefresh(          SPropsUtil.getInt("activity.stream.interval.refresh.cache",  1));
		m_activityStreamParams.setEntriesPerPage(        SPropsUtil.getInt("folder.records.listed",                  25));
		m_activityStreamParams.setMaxHits(               SPropsUtil.getInt("activity.stream.maxhits",              1000));
	}

	/*
	 * Inner class used to track author information in a Map used to
	 * cache it while reading activity stream data.
	 * 
	 * The intent is to save information about an author so that we
	 * don't have to repeatedly read the same information.
	 */
	private static class AuthorInfo {
		private String m_authorAvatarUrl;	// The author's avatar URL.
		private String m_authorId;			// The author's ID.
		private String m_authorWsId;		// The author's workspace ID.

		/*
		 * Class constructor.
		 */
		private AuthorInfo() {
			m_authorAvatarUrl =
			m_authorId        =
			m_authorWsId      = "";
		}

		/*
		 * Returns an author information object based on the author's
		 * ID.  If an author information for this ID is in the author
		 * cache, this is returned.  Otherwise, a new object is
		 * constructed (using database reads, ...), added to the cache
		 * and that is returned.
		 */
		@SuppressWarnings("unchecked")
		private static AuthorInfo getAuthorInfo(HttpServletRequest request, AllModulesInjected bs, Map<String, AuthorInfo> authorCache, String authorId) {
			// If we caching an author information object for this
			// ID...
			AuthorInfo reply = authorCache.get(authorId);
			if (null != reply) {
				// ...simply return it.
				return reply;
			}
			
			// Otherwise, construct a new author information object
			// for it.
			reply = new AuthorInfo();
			reply.m_authorId        = authorId;
			reply.m_authorAvatarUrl = "";			
			if (MiscUtil.hasString(authorId)) {
				Long authorWsId;
				try                  {authorWsId = bs.getProfileModule().getEntry(Long.parseLong(authorId)).getWorkspaceId();}
				catch (Exception ex) {authorWsId = null;                                                                     }
				if (null != authorWsId) {
					reply.m_authorWsId = String.valueOf(authorWsId);
				}
				
				// Do we have their workspace ID?
				if (null != authorWsId) {
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
					reply.m_authorAvatarUrl = ((null == paUrl) ? "" : paUrl);
				}
			}

			// If we get here, reply refers to the AuthorInfo object
			// constructed for the ID received.  Add it to the cache
			// and return it.
			authorCache.put(authorId, reply);
			return reply;
		}
	}
	
	/*
	 * Inner class used to track binder information in a Map used to
	 * cache it while reading activity stream data.
	 * 
	 * The intent is to save information about a binder so that we
	 * don't have to repeatedly read the same information.
	 */
	private static class BinderInfo {
		private Binder m_binder;		// The binder.
		private String m_binderHover;	// The binder's hover text.
		private String m_binderId;		// The binder's ID.
		private String m_binderName;	// The binder's name.

		/*
		 * Class constructor.
		 */
		private BinderInfo() {
			m_binderHover =
			m_binderId    =
			m_binderName  = "";
		}
		
		/*
		 * Returns a binder information object based on the binder's
		 * ID.  If a binder information for this ID is in the binder
		 * cache, this is returned.  Otherwise, a new object is
		 * constructed (using database reads, ...), added to the cache
		 * and that is returned.
		 */
		private static BinderInfo getBinderInfo(AllModulesInjected bs, Map<String, BinderInfo> binderCache, String binderId) {
			// If we caching a binder information object for this ID...
			BinderInfo reply = binderCache.get(binderId);
			if (null != reply) {
				// ...simply return it.
				return reply;
			}
			
			// Otherwise, construct a new binder information object for
			// it.
			reply = new BinderInfo();
			reply.m_binderId = binderId;			
			if (MiscUtil.hasString(binderId)) {
				reply.m_binder = GwtServerHelper.getBinderSafely(bs.getBinderModule(), binderId);
				if (null != reply.m_binder) {
					reply.m_binderHover = reply.m_binder.getPathName();
					reply.m_binderName  = reply.m_binder.getTitle();
				}
			}

			// If we get here, reply refers to the BinderInfo object
			// constructed for the ID received.  Add it to the cache
			// and return it.
			binderCache.put(binderId, reply);
			return reply;
		}
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
	private static void addASEComments(HttpServletRequest request, AllModulesInjected bs, ActivityStreamEntry ase, int comments, Map<String, AuthorInfo> authorCache, Map<String, BinderInfo> binderCache, Map em) {
		// If we weren't asked for any comments...
		if (0 >= comments) {
			// ...bail.
			return;
		}
		
		// If the activity stream entry doesn't refer to a folder
		// entry...
		String feId = ase.getEntryId();
		if (!(MiscUtil.hasString(feId))) {
			// ...bail.
			return;
		}

		// If we can't find any replies for this entry...
		Criteria searchCriteria = SearchUtils.entryReplies(feId);
		Map      searchResults  = bs.getBinderModule().executeSearchQuery(searchCriteria, 0, comments);
		List     searchEntries  = ((List)    searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
		int      totalRecords   = ((Integer) searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		if ((0 >= totalRecords) || (null == searchEntries) || searchEntries.isEmpty()) {
			// ...bail.
			return;
		}
		ase.setEntryComments(totalRecords);

		// Scan the replies...
		List<ActivityStreamEntry> commentList = ase.getComments();
    	for (Iterator it = searchEntries.iterator(); it.hasNext(); ) {
    		// ...and add an activity stream entry for the next entry
    		// ...map from the search results. 
    		ActivityStreamEntry comment = buildASEFromEM(
    			request,
    			bs,
    			null,	// null -> An activity stream parameter object is not required for comments.
    			authorCache,
    			binderCache,
    			((Map) it.next()),
    			false);	// false -> This is a comment activity stream entry for an activity stream data.
    		comment.setEntryComments(getCommentCount(bs, comment.getEntryId()));
    		commentList.add(comment);
    	}
	}
	
	/*
	 * Returns an activity stream entry based on the entry map from a
	 * search.
	 */
	@SuppressWarnings("unchecked")
	private static ActivityStreamEntry buildASEFromEM(HttpServletRequest request, AllModulesInjected bs, ActivityStreamParams asp, Map<String, AuthorInfo> authorCache, Map<String, BinderInfo> binderCache, Map em, boolean baseEntry) {
		// Create the activity stream entry to return.
		ActivityStreamEntry reply = new ActivityStreamEntry();

		// First, initialize the author information.		
		AuthorInfo authorInfo = AuthorInfo.getAuthorInfo(
			request,
			bs,
			authorCache,
			getSFromEM(
				em,
				Constants.MODIFICATIONID_FIELD));
		reply.setAuthorId(         authorInfo.m_authorId       );
		reply.setAuthorWorkspaceId(authorInfo.m_authorWsId     );
		reply.setAuthorAvatarUrl(  authorInfo.m_authorAvatarUrl);
		reply.setAuthorLogin(getSFromEM(em, Constants.MODIFICATION_NAME_FIELD ));
		reply.setAuthorName( getSFromEM(em, Constants.MODIFICATION_TITLE_FIELD));

		// Then the parent binder information.  Does the entry map
		// contain the ID of the binder that contains the entry?
		BinderInfo binderInfo = BinderInfo.getBinderInfo(
			bs,
			binderCache,
			getSFromEM(
				em,
				Constants.BINDER_ID_FIELD));
		reply.setParentBinderId(   binderInfo.m_binderId   );
		reply.setParentBinderHover(binderInfo.m_binderHover);
		reply.setParentBinderName( binderInfo.m_binderName );

		// And finally, the entry information.
		String entryId =               getSFromEM(        em, Constants.DOCID_FIELD              );
		reply.setEntryId(              entryId                                                   );
		reply.setEntryDescription(     getEntryDescFromEM(em, request, bs, entryId              ));	
		reply.setEntryDocNum(          getSFromEM(        em, Constants.DOCNUMBER_FIELD         ));
		reply.setEntryModificationDate(getSFromEM(        em, Constants.MODIFICATION_DATE_FIELD ));		
		reply.setEntryTitle(           getSFromEM(        em, Constants.TITLE_FIELD             ));
		reply.setEntryTopEntryId(      getSFromEM(        em, Constants.ENTRY_TOP_ENTRY_ID_FIELD));
		reply.setEntryType(            getSFromEM(        em, Constants.ENTRY_TYPE_FIELD        ));

		// Are we working on a base activity stream entry for an
		// activity stream data object? 
		if (baseEntry) {
			// Yes!  Do we need to include comments in this activity
			// stream entry?
			int comments = asp.getActiveComments();
			if (0 < comments) {
				// Yes!  Add them.
				addASEComments(
					request,
					bs,
					reply,
					comments,
					authorCache,
					binderCache,
					em);
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
		reply.setDateTime(getDateTimeString(new Date()));
		
		// If we weren't given a PagingData...
		if (null == pd) {
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
	 * Returns a count of the comments on an entry.
	 */
	@SuppressWarnings("unchecked")
	private static int getCommentCount(AllModulesInjected bs, String feId) {
		Criteria searchCriteria = SearchUtils.entryReplies(feId);
		Map      searchResults  = bs.getBinderModule().executeSearchQuery(searchCriteria, 0, 1);
		int      totalRecords   = ((Integer) searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		return totalRecords;
	}

	/**
	 * Returns a formatted date string for the current user's locale
	 * and time zone.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static String getDateTimeString(Date date) {
		User user = GwtServerHelper.getCurrentUser();
		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, user.getLocale());
		df.setTimeZone(user.getTimeZone());
		
		return df.format(date);
	}
	
	/*
	 * Extracts a description from an entry map and replaces any mark
	 * up with the appropriate URL.  For example, replaces:
	 *    {{atachmentUrl: somename.png}}
	 * with a URL that looks like:
	 *    http://somehost/ssf/s/readFile/.../somename.png
	 */
	@SuppressWarnings("unchecked")
	private static String getEntryDescFromEM(Map em, HttpServletRequest request, AllModulesInjected bs, String entryId) {
		// Do we have a base description and an entry ID?
		String reply = getSFromEM(em, Constants.DESC_FIELD);
		if (MiscUtil.hasString(reply) && MiscUtil.hasString(entryId)) {
			// Yes!  Can we access the entry?
			FolderEntry fe = GwtServerHelper.getEntrySafely(bs.getFolderModule(), entryId);
			if (null != fe) {
				// Yes!  Fix the mark up.
				reply = MarkupUtil.markupStringReplacement(
					null,
					null,
					request,
					null,
					fe,
					reply,
					"view");
			}
		}
		
		// If we get here, reply refers to the description string.
		// Return it.
		return reply;
	}
	
	/*
	 * Returns a string for a value out of the entry map from a search
	 * results.
	 */
	@SuppressWarnings("unchecked")
	private static String getSFromEM(Map em, String key) {
		// Do we have entry data for this key?
		String reply = "";
		Object emData = em.get(key);
		if (null != emData) {
			// Yes!  Is it a string?
			if (emData instanceof String) {
				// Yes!  Return it directly.
				reply = ((String) emData);
			}
			
			// No, it isn't a string!  Is it a date?
			else if (emData instanceof Date) {
				// Yes!  Format it for the current user and return
				// that.
				reply = getDateTimeString((Date) emData);
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
		
		// Is the user following any users?
		asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeFollowedPeople"));
		List<String> followedUsersList = GwtServerHelper.getTrackedPeople(bs);
		idCount = ((null == followedUsersList) ? 0 : followedUsersList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			asTIChildren = asTI.getChildBindersList();
			asIds = new String[idCount];
			idIndex = 0;
			for (String followedUserId: followedUsersList) {
				// Can we access the next one's User?
				id = followedUserId;
				user = GwtServerHelper.getUserSafely(pm, id);
				if (null != user) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIds[idIndex++] = id;
					asTIChild = buildASTI(bs, id, user.getTitle(), null, ActivityStream.FOLLOWED_PERSON);					
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
		
		// Add the followed users TreeInfo to the root TreeInfo.
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
		if (isDebugLoggingEnabled()) {
			writeDebugLog("GwtActivityStreamHelper.hasActivityStreamChanged( 'Checking activity stream for changes' ):  " + asi.getStringValue());
		}

		// Update the data that's cached about what's recently changed.
		ActivityStreamCache.updateMaps(bs);

		// When was the last time we updated the data for this activity
		// stream?
		Date updateDate = ActivityStreamCache.getUpdateDate(request);
		
		// Are we watching any binders for changes?
		List<Long> trackedIds = ActivityStreamCache.getTrackedBinderIds(request);
		boolean activityStreamChanged = ((null != trackedIds) && (!(trackedIds.isEmpty())));
		if (activityStreamChanged) {
			// Yes!  Has anything changed in them?
			activityStreamChanged = ActivityStreamCache.checkBindersForNewEntries(
				bs,
				trackedIds,
				updateDate);
		}
		
		// Did we detect changes in the binders?
		if (!activityStreamChanged) {
			// No!  Are we tracking any users for having changed
			// anything?
			trackedIds = ActivityStreamCache.getTrackedUserIds(request);
			activityStreamChanged = ((null != trackedIds) && (!(trackedIds.isEmpty())));
			if (activityStreamChanged) {
				// Yes!  Have these users changed anything?
				activityStreamChanged = ActivityStreamCache.checkUsersForNewEntries(
					bs,
					trackedIds,
					updateDate);
			}
		}

		// If we get here, activityStreamChanged is true if something
		// we care about has changed and false otherwise.  Return it.
		return new Boolean(activityStreamChanged);
	}

	/**
	 * Returns true if we're debug logging is enabled and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isDebugLoggingEnabled() {
		return m_logger.isDebugEnabled();
	}
	
	/*
	 * Reads the activity stream data based on an activity stream
	 * information object and the current paging data.
	 */
	@SuppressWarnings("unchecked")
	private static void populateASD(ActivityStreamData asd, HttpServletRequest request, AllModulesInjected bs, ActivityStreamParams asp, ActivityStreamInfo asi) {		
		// Setup some int's for the controlling the search.
		PagingData pd				= asd.getPagingData();
		int        entriesPerPage	= pd.getEntriesPerPage();
		int        pageIndex		= pd.getPageIndex();
		int        pageStart		= (pageIndex * entriesPerPage);

		// Initialize lists for the tracked places and users.
		List<String> trackedPlacesAL = new ArrayList<String>();
		List<String> trackedUsersAL  = new ArrayList<String>();

		// What type of activity stream are we reading the data for?
		String[] trackedPlaces = asi.getBinderIds();
		switch (asi.getActivityStream()) {
		case FOLLOWED_PEOPLE:
		case FOLLOWED_PERSON:
			// Followed people:
			// 1. There are no tracked places; and
			// 2. The tracked users are the owner IDs of the places.
			sATosL(trackedPlaces, trackedUsersAL);
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
			// 2. There are no tracked users.
			break;

		default:
		case SITE_WIDE:
			// The entire site:
			// 1. The tracked places is the ID of the top workspace; and
			// 2. There are no tracked users.
			trackedPlaces = new String[]{String.valueOf(bs.getWorkspaceModule().getTopWorkspace().getId().longValue())};
			break;
		}

		// Store the tracked places into the ArrayList for them.
		sATosL(trackedPlaces, trackedPlacesAL);

		// Perform the search and extract the results.
		Criteria searchCriteria = SearchUtils.entriesForTrackedPlacesAndPeople(bs, trackedPlacesAL, trackedUsersAL);
		Map      searchResults  = bs.getBinderModule().executeSearchQuery(searchCriteria, pageStart, entriesPerPage);
		List     searchEntries  = ((List)    searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
		int      totalRecords   = ((Integer) searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();

		// Update the paging data in the activity stream data.
    	pd.setTotalRecords(totalRecords);
    	asd.setPagingData(pd);

    	// Are there any entries in the search results?
		Map<String, Binder> whatsNewPlaces = null; //! (newData ? new HashMap<String, Binder>() : null);
    	if ((null != searchEntries) && (!(searchEntries.isEmpty()))) {
    		// Yes!  Get the list to hold the activity stream
    		// entries and scan the search results. 
    		Map<String, AuthorInfo> authorCache = new HashMap<String, AuthorInfo>();
    		Map<String, BinderInfo> binderCache = new HashMap<String, BinderInfo>();
        	List<ActivityStreamEntry> entries = asd.getEntries();
	    	for (Iterator it = searchEntries.iterator(); it.hasNext(); ) {
	    		// Construct and add an activity stream entry for the
	    		// next entry map from the search results. 
	    		ActivityStreamEntry entry = buildASEFromEM(
	    			request,
	    			bs,
	    			asp,
	    			authorCache,
	    			binderCache,
	    			((Map) it.next()),
	    			true);	// true -> This is a base activity stream entry for an activity stream data.
    			entries.add(entry);

    			// Are we constructing new data for which we need to
    			// cache the binders?
    			if (null != whatsNewPlaces) {
	    			// Yes!  Do we have an ID for the binder that
    				// contains this entry that we have yet to track?
					String parentBinderId = entry.getParentBinderId();
					if (MiscUtil.hasString(parentBinderId) && (!(whatsNewPlaces.containsKey(parentBinderId)))) {
						// Yes!  Track it now.
						BinderInfo binderInfo = BinderInfo.getBinderInfo(bs, binderCache, parentBinderId);
						whatsNewPlaces.put(parentBinderId, binderInfo.m_binder);
					}
    			}
	    	}
    	}

    	// Store the places and users we're tracking in the session
    	// cache....
    	ActivityStreamCache.setTrackedBinderIds(request, sLTolL(trackedPlacesAL));
    	ActivityStreamCache.setTrackedUserIds(  request, sLTolL(trackedUsersAL ));
    	
    	// ...and store the date we saved the tracking data.
    	ActivityStreamCache.setUpdateDate(request);
	}
	
	/*
	 * Stores the strings from a String[] into a List<String>.
	 */
	private static void sATosL(String[] sA, List<String> sL) {
		int c = ((null == sA) ? 0 : sA.length);
		for (int i = 0; i < c; i += 1) {
			sL.add(sA[i]);
		}
	}

	/*
	 * Converts the strings from a List<String> into a List<Long>.
	 */
	private static List<Long> sLTolL(List<String> sL) {
		List<Long> reply = new ArrayList<Long>();
		int c = ((null == sL) ? 0 : sL.size());
		if (0 < c) {
			for (String s: sL) {
				reply.add(Long.parseLong(s));
			}
		}
		return reply;
	}

	/**
	 * Writes a string to the debug log as the current user.
	 * 
	 * @param s
	 */
	public static void writeDebugLog(String s) {
		// If debug logging is enabled and we have a string to write...
		if (isDebugLoggingEnabled() && MiscUtil.hasString(s)) {
			// ...generate information about the current user...
			User asUser = GwtServerHelper.getCurrentUser();
			String userId = ((null == asUser) ? "" : ("U:" + asUser.getName() + ":"));
			
			// ...and write it to the log.
			m_logger.debug(userId + s);
		}
	}
}
