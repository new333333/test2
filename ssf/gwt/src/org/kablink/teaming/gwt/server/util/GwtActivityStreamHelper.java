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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.dom4j.Document;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.server.util.GwtServerProfiler;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Html;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction.Conjunction;
import org.kablink.util.search.Junction.Disjunction;
import org.kablink.util.search.Restrictions;

import static org.kablink.util.search.Restrictions.conjunction;
import static org.kablink.util.search.Restrictions.disjunction;
import static org.kablink.util.search.Restrictions.in;

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
		// Calculate various refresh intervals.  (Note:  The
		// proportions used are based off the original Teaming Feed
		// values of:  Client refresh=5 minutes, look back=11 minutes
		// and cache refresh=1 minute.)
		int clientRefresh = SPropsUtil.getInt("activity.stream.interval.refresh.client", 60);
		int cacheLookback = ((int) Math.ceil(((double) clientRefresh) * 2.2));
		int cacheRefresh  = (clientRefresh / 5);
		if (0 != (clientRefresh % 5)) {
			cacheRefresh += 1;
		}

		// Create an activity stream parameter object with the
		// appropriate defaults.
		m_activityStreamParams = new ActivityStreamParams();
		m_activityStreamParams.setShowSetting(           ActivityStreamDataType.ALL                               );
		m_activityStreamParams.setActivityStreamsOnLogin(GwtUIHelper.isActivityStreamOnLogin()                    );
		m_activityStreamParams.setLookback(              cacheLookback                                            );
		m_activityStreamParams.setClientRefresh(         clientRefresh                                            );
		m_activityStreamParams.setCacheRefresh(          cacheRefresh                                             );
		m_activityStreamParams.setActiveComments(        SPropsUtil.getInt("activity.stream.active.comments",   2));
		m_activityStreamParams.setDisplayWords(          SPropsUtil.getInt("activity.stream.display.words",  (-1)));
		m_activityStreamParams.setEntriesPerPage(        SPropsUtil.getInt("folder.records.listed",            25));
		m_activityStreamParams.setMaxHits(               SPropsUtil.getInt("activity.stream.maxhits",        1000));
		m_activityStreamParams.setReadEntryMax(          SPropsUtil.getInt("activity.stream.read.entry.max", 1000));
		m_activityStreamParams.setReadEntryDays(         SPropsUtil.getInt("activity.stream.read.entry.days",  30));
	};
	
	/*
	 * Inner class used to track author information while constructing
	 * an ASEntryData object.
	 * 
	 * The intent is to save information about an author so that we
	 * don't have to repeatedly read the same information.
	 */
	private static class ASAuthorInfo {
		private GwtPresenceInfo m_authorPresence;	// The author's presence information.
		private String          m_authorAvatarUrl;	// The author's avatar URL.
		private String          m_authorId;			// The author's ID.
		private String          m_authorWsId;		// The author's workspace ID.
		private String          m_authorTitle;		// The author's title, given visibility by the current user.

		/*
		 * Constructor method.
		 */
		private ASAuthorInfo() {
			// Initialize the super class...
			super();
			
			// ...and initialize everything else.
			m_authorPresence  = GwtServerHelper.getPresenceInfoDefault();
			m_authorAvatarUrl =
			m_authorId        =
			m_authorWsId      =
			m_authorTitle     = "";
		}

		/*
		 * Constructs an ASAuthorInfo object based on its author ID.
		 */
		@SuppressWarnings("unchecked")
		private static ASAuthorInfo buildAuthorInfo(HttpServletRequest request, AllModulesInjected bs, boolean isPresenceEnabled, boolean isOtherUserAccessRestricted, Long authorId, User authorUser, String authorTitle) {
			// Construct a new ASAuthorInfo object.
			ASAuthorInfo reply = new ASAuthorInfo();
			reply.m_authorId = String.valueOf(authorId);
			reply.m_authorAvatarUrl = "";
			
			// Do we have their workspace ID?
			Long authorWsId = ((null == authorUser) ? null : authorUser.getWorkspaceId());
			if (null != authorWsId) {
				// Yes!  Can we access any avatars for the author?
				reply.m_authorWsId = String.valueOf(authorWsId);
				String paUrl = null;
				ProfileAttribute pa;
				try                  {pa = GwtProfileHelper.getProfileAvatars(request, bs, authorWsId);}
				catch (Exception ex) {pa = null;                                                       }
				List<ProfileAttributeListElement> paValue = ((null == pa) ? null : ((List<ProfileAttributeListElement>) pa.getValue()));
				if((null != paValue) && (!(paValue.isEmpty()))) {
					// Yes!  We'll use the first one as the URL.
					// Does it have a URL?
					ProfileAttributeListElement paValueItem = paValue.get(0);
					paUrl = GwtProfileHelper.fixupAvatarUrl(paValueItem.getValue().toString());
				}
				
				// Store something as the author's URL so that we don't
				// try to look it up again.
				reply.m_authorAvatarUrl = ((null == paUrl) ? "" : paUrl);
			}

			// If the presence service is enabled...
			if (isPresenceEnabled) {
				// ...obtain the author's presence information.
				reply.m_authorPresence = GwtServerHelper.getPresenceInfo(authorUser);
			}

			// Finally, store the title for the author based on the
			// user's access to it.
			reply.m_authorTitle = GwtServerHelper.getUserTitle(
				bs.getProfileModule(),
				isOtherUserAccessRestricted,
				reply.m_authorId,
				authorTitle);
			
			return reply;
		}
	}
		
	/*
	 * Inner class used to track binder information while constructing
	 * an ASEntryData object.
	 * 
	 * The intent is to save information about a binder so that we
	 * don't have to repeatedly read the same information.
	 */
	private static class ASBinderInfo {
		private String m_binderHover;	// The binder's hover text.
		private String m_binderId;		// The binder's ID.
		private String m_binderName;	// The binder's name.

		/*
		 * Constructor method.
		 */
		private ASBinderInfo() {
			// Initialize the super class...
			super();

			// ...and initialize everything else.
			m_binderHover =
			m_binderId    =
			m_binderName  = "";
		}

		/*
		 * Constructs a ASBinderInfo object based on its Binder object.
		 */
		private static ASBinderInfo buildBinderInfo(Long binderId, Binder binder) {
			ASBinderInfo reply = new ASBinderInfo();
			
			reply.m_binderId    = String.valueOf(binderId);
			reply.m_binderHover = ((null == binder) ? "" : binder.getPathName());
			reply.m_binderName  = ((null == binder) ? "" : binder.getTitle());
			
			return reply;
		}
	}

	/*
	 * Inner class used to collect data about what needs to be
	 * displayed in an activity stream listing.
	 * 
	 * Its purpose is to optimize the number of database reads and
	 * search index searches required to construct the
	 * ActivityStreamEntry objects returned for display.
	 */
	@SuppressWarnings("unchecked")
	private static class ASEntryData {
		private ASAuthorInfo      m_authorInfo;				// Information about this entry's author.
		private ASBinderInfo      m_binderInfo;				// Information about the Binder containing this entry.
		private int               m_commentCount;			// Total number of comments (at any level) on this entry.
		private List<ASEntryData> m_commentEntryDataList;	// List<ASEntryData> containing the most recent comments.
		private Long              m_authorId;				// The ID of the entry's author.
		private Long              m_binderId;				// The ID of the Binder containing this entry.
		private Long              m_entryId;				// The ID on the entry itself
		private Long              m_topEntryId;				// The ID of the top entry, if the entry itself is not the top.
		private Map               m_entryMap;				// The entry's search results Map.
		private String            m_authorTitle;			// The title of this entry's author.  (Does NOT account for visibility restrictions.)
		
		/*
		 * Constructor method.
		 */
		private ASEntryData(Map entryMap, Long binderId, Long entryId, Long topEntryId) {
			// Initialize the super class...
			super();
			
			// ...store the parameters...
			m_entryMap   = entryMap;
			m_binderId   = binderId;
			m_entryId    = entryId;
			m_topEntryId = topEntryId;

			// ...and initialize everything else.
			m_commentEntryDataList = new ArrayList<ASEntryData>();			
			m_authorId             = Long.parseLong(GwtServerHelper.getStringFromEntryMap(m_entryMap, Constants.CREATORID_FIELD   ));
			m_authorTitle          =                GwtServerHelper.getStringFromEntryMap(m_entryMap, Constants.CREATOR_TITLE_FIELD);
		}
		
		private ASEntryData(Map entryMap, Long binderId, Long entryId) {
			// Always use the initial form of the constructor.
			this(entryMap, binderId, entryId, null);
		}
		
		/*
		 * Class Get'er/Set'er methods.
		 */
		private boolean           isTopEntry()             {return (null == m_topEntryId);}
		private ASAuthorInfo      getAuthorInfo()          {return m_authorInfo;          }
		private ASBinderInfo      getBinderInfo()          {return m_binderInfo;          }
		private int               getCommentCount()        {return m_commentCount;        }
		private List<ASEntryData> getCommentEntryDataList(){return m_commentEntryDataList;}
		private Long              getAuthorId()            {return m_authorId;            }
		private Long              getBinderId()            {return m_binderId;            }
		private Long              getEntryId()             {return m_entryId;             }
		private Long              getTopEntryId()          {return m_topEntryId;          }
		private Map               getEntryMap()            {return m_entryMap;            }
		private String            getAuthorTitle()         {return m_authorTitle;         }
		
		private void incrCommentCount()                         {m_commentCount += 1;           }
		private void setAuthorInfo(  ASAuthorInfo authorInfo)   {m_authorInfo    = authorInfo;  }
		private void setBinderInfo(  ASBinderInfo binderInfo)   {m_binderInfo    = binderInfo;  }
		private void setCommentCount(int          commentCount) {m_commentCount  = commentCount;}

		/*
		 * Returns a List<ASEntryData> corresponding to what should be
		 * displayed for a given List<Map> of entry search results.
		 */
		private static List<ASEntryData> buildEntryDataList(HttpServletRequest request, AllModulesInjected bs, ActivityStreamDataType asdt, boolean returnComments, boolean forcePlainTextDescriptions, boolean isPresenceEnabled, boolean isOtherUserAccessRestricted, ActivityStreamParams asp, SeenMap sm, List<Map> searchEntries) {
			// If we weren't given any search results...
			List<ASEntryData> reply = new ArrayList<ASEntryData>();			
			if ((null == searchEntries) || searchEntries.isEmpty()) {
				// Return an empty List<ASEntryData>.
				return reply;
			}
			
			// Scan the search results Map's.
			for (Map entryMap:  searchEntries) {
				// Does this map contain both binder and entry IDs?
				String binderId = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.BINDER_ID_FIELD);
				String entryId  = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DOCID_FIELD    );
				if ((!(MiscUtil.hasString(binderId))) || (!(MiscUtil.hasString(entryId)))) {
					// No!  Skip it.
					continue;
				}

				// Does the map contain a top entry ID for this entry?
				String entryTopIdS = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ENTRY_TOP_ENTRY_ID_FIELD);
				Long   entryTopId;
				if (MiscUtil.hasString(entryTopIdS))
				     entryTopId = Long.parseLong(entryTopIdS);
				else entryTopId = null;

				// Add a stubbed out ASEntryData to the
				// List<ASEntryData> that we're going to return.
				ASEntryData ased = new ASEntryData(entryMap, Long.parseLong(binderId), Long.parseLong(entryId), entryTopId);
				if (!returnComments) {
					String totalReplyCount = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.TOTALREPLYCOUNT_FIELD);
					if (MiscUtil.hasString(totalReplyCount)) {
						ased.setCommentCount(Integer.parseInt(totalReplyCount));
					}
				}
				reply.add(ased);
			}

			// Are we tracking any ASEntryData's in the
			// List<ASEntryData> that we're going to return?
			if (!(reply.isEmpty())) {
				// Yes!  Have comments been requested?
				if (returnComments) {
					// Yes!  Build their stubbed out comment
					// List<ASEntryData>'s.
					completeCommentEntryDataLists(bs, asdt, asp, sm, reply);
				}

				// Read the ASEntryData's required User's and
				// Binder's...
				Map<Long, User>   userMap   = readUsers(  bs,          reply);
				Map<Long, Binder> binderMap = readBinders(bs, userMap, reply);
				
				// ...and complete their binder and author data.
				completeBinderData(         bs, binderMap,                                               reply);
				completeAuthorData(request, bs, userMap, isPresenceEnabled, isOtherUserAccessRestricted, reply);
			}

			// If we get here, reply refers to a List<ASEntryData> of
			// the ASEntryData's corresponding to a List<Map> of search
			// results.  Return it.
			return reply;
		}

		/*
		 * Uses a Map<Long, User> to complete the author information in
		 * a List<ASEntryData>.
		 */
		private static void completeAuthorData(HttpServletRequest request, AllModulesInjected bs, Map<Long, User> userMap, boolean isPresenceEnabled, boolean isOtherUserAccessRestricted, List<ASEntryData> entryDataList) {
			// Scan the List<ASEntryData>.
			Map<Long, ASAuthorInfo> authorInfoMap = new HashMap<Long, ASAuthorInfo>();
			for (ASEntryData entryData:  entryDataList) {
				// Are we caching an ASAuthorInfo for this ASEntryData
				// yet?
				Long authorId = entryData.getAuthorId();
				ASAuthorInfo authorInfo = authorInfoMap.get(authorId);
				if (null == authorInfo) {
					// No!  Construct one and cache it now.  
					User authorUser = userMap.get(authorId);
					authorInfo = ASAuthorInfo.buildAuthorInfo(request, bs, isPresenceEnabled, isOtherUserAccessRestricted, authorId, authorUser, entryData.getAuthorTitle());
					authorInfoMap.put(authorId, authorInfo);
				}
				
				// Store the ASAuthorInfo in this ASEntryData.
				entryData.setAuthorInfo(authorInfo);
				
				// Scan the ASEntryData's comment ASEntryData.
				for (ASEntryData commentEntryData:  entryData.getCommentEntryDataList()) {
					// Are we caching an ASAuthorInfo for this comment
					// ASEntryData yet?
					Long commentAuthorId = commentEntryData.getAuthorId();
					ASAuthorInfo commentAuthorInfo = authorInfoMap.get(commentAuthorId);
					if (null == commentAuthorInfo) {
						// No!  Construct one and cache it now.
						User authorUser = userMap.get(commentAuthorId);
						commentAuthorInfo = ASAuthorInfo.buildAuthorInfo(request, bs, isPresenceEnabled, isOtherUserAccessRestricted, commentAuthorId, authorUser, commentEntryData.getAuthorTitle());
						authorInfoMap.put(commentAuthorId, commentAuthorInfo);
					}

					// Store the ASAuthorInfo in this comment
					// ASEntryData.
					commentEntryData.setAuthorInfo(commentAuthorInfo);
				}
			}
		}
		
		/*
		 * Using Map<Long, Binder> to complete the binder information
		 * in a List<ASEntryData>.
		 */
		private static void completeBinderData(AllModulesInjected bs, Map<Long, Binder> binderMap, List<ASEntryData> entryDataList) {
			// Scan the List<ASEntryData>.
			Map<Long, ASBinderInfo> binderInfoMap = new HashMap<Long, ASBinderInfo>();
			for (ASEntryData entryData:  entryDataList) {
				// Yes!  Are we caching an ASBinderInfo for this
				// ASEntryData yet?
				Long binderId = entryData.getBinderId();
				ASBinderInfo binderInfo = binderInfoMap.get(binderId);
				if (null == binderInfo) {
					// No!  Construct one and cache it now.  
					Binder binder = binderMap.get(binderId);
					binderInfo = ASBinderInfo.buildBinderInfo(binderId, binder);
					binderInfoMap.put(binderId, binderInfo);
				}
				
				// Store the ASBinderInfo in this ASEntryData.
				entryData.setBinderInfo(binderInfo);
				
				// Scan the comment ASEntryData on this ASEntryData...
				for (ASEntryData commentEntryData:  entryData.getCommentEntryDataList()) {
					// ...storing the same ASBinderInfo in them as well
					// ...since comments MUST be in the same binder as
					// ...their top level entries.
					commentEntryData.setBinderInfo(binderInfo);
				}				
			}
		}
		
		/*
		 * Uses a single index search to complete the comment
		 * information in a List<ASEntryData>.
		 */
		private static void completeCommentEntryDataLists(AllModulesInjected bs, ActivityStreamDataType asdt, ActivityStreamParams asp, SeenMap sm, List<ASEntryData> entryDataList) {
			// Scan the List<ASEntryData>...
			String[] topEntryIds = new String[entryDataList.size()];
			int i = 0;
			for (ASEntryData entryData:  entryDataList) {
				// ...tracking each ASEntryData's entry ID.
				Long topEntryId = (entryData.isTopEntry() ? entryData.getEntryId() : entryData.getTopEntryId());
				topEntryIds[i++] = String.valueOf(topEntryId);
			}

			// Are there any comments posted to any of these entries?
			Criteria searchCriteria = SearchUtils.entryReplies(topEntryIds, true);	// true -> All replies, at any level.
			Map       searchResults = bs.getBinderModule().executeSearchQuery(searchCriteria, Constants.SEARCH_MODE_NORMAL, 0, (Integer.MAX_VALUE - 1), null);
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
			int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
			if ((0 >= totalRecords) || (null == searchEntries) || searchEntries.isEmpty()) {
				// No!  Then there's no comment data to complete.
				return;
			}

			// Scan the comment entry search results Map's
			boolean isAll = ActivityStreamDataType.ALL.equals(asdt);
			int activeComments = (isAll ? asp.getActiveComments() : Integer.MAX_VALUE);
			for (Map commentEntryMap:  searchEntries) {
				// Can we find the ASEntryData for the top level entry
				// for this comment?
				String entryIdS = GwtServerHelper.getStringFromEntryMap(commentEntryMap, Constants.ENTRY_TOP_ENTRY_ID_FIELD);
				Long entryId = Long.parseLong(entryIdS);
				ASEntryData entryData = ASEntryData.findEntryData(entryDataList, entryId);
				if (null == entryData) {
					entryIdS = GwtServerHelper.getStringFromEntryMap(commentEntryMap, Constants.ENTRY_PARENT_ID_FIELD);
					entryId = Long.parseLong(entryIdS);
					entryData = ASEntryData.findEntryData(entryDataList, entryId);
				}
				if (null == entryData) {
					// No!  Skip it.
					continue;
				}
				
				// Does this comment Map contain both a binder and
				// entry ID? 
				String commentBinderId = GwtServerHelper.getStringFromEntryMap(commentEntryMap, Constants.BINDER_ID_FIELD);
				String commentEntryIdS = GwtServerHelper.getStringFromEntryMap(commentEntryMap, Constants.DOCID_FIELD    );
				if ((!(MiscUtil.hasString(commentBinderId))) || (!(MiscUtil.hasString(commentEntryIdS)))) {
					// No!  Skip it.
					continue;
				}
							
				// Keep track of the number of comments we found for
				// this top level entry.
				entryData.incrCommentCount();
				
				// Have we resolved all the comments we need to display
				// for this top level entry?
				List<ASEntryData> commentEntryDataList = entryData.getCommentEntryDataList();
				
				// We only want the last n comments.  If we have
				// already added n comments to the top level entry,
				// remove the first one.
				if (commentEntryDataList.size() == activeComments) {
					commentEntryDataList.remove(0);
				}

				if (commentEntryDataList.size() < activeComments) {
					// No!  Add a stubbed out ASEntryData to the
					// comment List<ASEntryData> that we're completing.
					if (!isAll) {
						boolean commentSeen = sm.checkIfSeen(commentEntryMap);
						switch (asdt) {
						case READ:    if (!commentSeen) continue; break;
						case UNREAD:  if ( commentSeen) continue; break;
						}
					}
					commentEntryDataList.add(
						new ASEntryData(
							commentEntryMap,
							Long.parseLong(commentBinderId),
							Long.parseLong(commentEntryIdS)));
				}
			}
		}

		/*
		 * Searches a List<ASEntryData> for the ASEntryData with a
		 * given entry ID.
		 */
		private static ASEntryData findEntryData(List<ASEntryData> entryDataList, Long entryId) {
			// Scan the List<ASEntryData>.
			for (ASEntryData entryData:  entryDataList) {
				// Is this the ASEntryData in question?
				if (entryData.getEntryId().equals(entryId)) {
					// Yes!  Return it.
					return entryData;
				}
			}
			
			// If we get here, we couldn't find the ASEntryData for the
			// entry ID in the List<ASEntryData>.  Return null.
			return null;
		}
		
		/*
		 * Uses a single database read to build a Map of the binders,
		 * indexed by their ID, from a Map<Long, User> and
		 * List<ASEntryData>.
		 */
		private static Map<Long, Binder> readBinders(AllModulesInjected bs, Map<Long, User> userMap, List<ASEntryData> entryDataList) {
			// Scan the Map<Long, User> containing the authors.
			List<Long> binderIds = new ArrayList<Long>();
			for (Long authorUserId:  userMap.keySet()) {
				// If we're not already tracking this author's
				// workspace ID as a binder ID, track it now.
				ListUtil.addLongToListLongIfUnique(binderIds, userMap.get(authorUserId).getWorkspaceId());
			}
			
			// Scan the List<ASEntryData>.
			for (ASEntryData entryData:  entryDataList) {
				// If we're not already tracking this ASEntryData's
				// binder ID, track it now.
				ListUtil.addLongToListLongIfUnique(binderIds, entryData.getBinderId());
				
				// Note that we don't have to process the comments on
				// an entry for their binders since comments must be
				// contained in the same binder as the top level entry.
			}
			
			// Read the binders that we're tracking (in a single
			// database read) and scan them...
			Map<Long, Binder> reply = new HashMap<Long, Binder>();
			if (!(binderIds.isEmpty())) {
				SortedSet<Binder> binders;
				try {
					binders = bs.getBinderModule().getBinders(binderIds);
				}
				catch (Exception ex) {
					m_logger.debug("GwtActivityStreamHelper.readBinders( 1:EXCEPTION ):  ", ex);
					binders = new TreeSet<Binder>();
				}
				for (Binder b:  binders) {
					// ...adding each to a Map using their ID as the
					// ...key.
					if ((!(b.isDeleted())) && (!(GwtUIHelper.isBinderPreDeleted(b)))) {
						reply.put(b.getId(), b);
					}
				}
			}
			
			// If we get here, reply refers to the Map<Long, Binder>
			// being requested.  Return it.
			return reply;
		}
		
		/*
		 * Uses a single database read to build a Map of the authors,
		 * indexed by their user IDs, from a List<ASEntryData>.
		 */
		private static Map<Long, User> readUsers(AllModulesInjected bs, List<ASEntryData> entryDataList) {
			// Scan the List<ASEntryData>.
			Map<Long, User> reply = new HashMap<Long, User>();
			List<Long> authorIds = new ArrayList<Long>();
			for (ASEntryData entryData:  entryDataList) {
				// If we're not tracking this ASEntryData's author ID
				// yet, track it now.
				ListUtil.addLongToListLongIfUnique(authorIds, entryData.getAuthorId());
				
				// Scan this ASEntryData's comments List<ASEntryData>.
				for (ASEntryData commentEntryData:  entryData.getCommentEntryDataList()) {
					// If we're not tracking this comment ASEntryData's
					// author ID yet, track it now.
					ListUtil.addLongToListLongIfUnique(authorIds, commentEntryData.getAuthorId());
				}
			}
			
			// Are we tracking any author IDs?
			if (!(authorIds.isEmpty())) {
				// Yes!  Read them (in single database read) and scan
				// them...
				SortedSet<Principal> authorPrincipals;
				try {
					authorPrincipals = bs.getProfileModule().getPrincipals(authorIds, false);
				}
				catch (Exception ex) {
					m_logger.debug("GwtActivityStreamHelper.readUsers( 1:EXCEPTION ):  ", ex);
					authorPrincipals = new TreeSet<Principal>();
				}
				for (Principal p:  authorPrincipals) {
					// ...adding each as a User to a Map using their ID
					// ...as the key.
					if (!(p.isDeleted())) {
						reply.put(p.getId(), ((User) p));
					}
				}
			}

			// If we get here, reply refers to the Map<Long, User>
			// being requested.  Return it.
			return reply;
		}		
	}

	/*
	 * Inner class used to wrap the data returned from an activity
	 * stream data search.
	 */
	@SuppressWarnings("unchecked")
	private static class ASSearchResults {
		private boolean		m_totalApproximate;	//
		private int			m_totalRecords;		//
		private List<Map>	m_searchEntries;	//
		
		/*
		 * Constructor method.
		 */
		private ASSearchResults(List<Map> searchEntries, int totalRecords, boolean totalApproximate) {
			// Initialize the super class...
			super();
			
			// ...and initialize everything else.
			m_searchEntries    = searchEntries;
			m_totalRecords     = totalRecords;
			m_totalApproximate = totalApproximate;
		}

		/*
		 * Get'er methods.
		 */
		private boolean   isTotalApproximate() {return m_totalApproximate;}
		private int       getTotalRecords()    {return m_totalRecords;    }
		private List<Map> getSearchEntries()   {return m_searchEntries;   }
	}
	
	/*
	 * Inner class used to collect data about what needs to be
	 * displayed in an activity stream sidebar.
	 * 
	 * Its purpose is to optimize the number of database reads required
	 * to construct the TreeInfo object returned for display.
	 */
	private static class ASTreeData {
		private List<FavoriteInfo> m_myFavoritesList;				// The current user's favorites.
		private List<String>       m_followedPlacesList;			// The current user's followed places.
		private List<String>       m_followedUsersList;				// The current user's followed users.
		private List<String>       m_collection_MyFilesList;		// The 'My Files'       collection point binder list.
		private List<String>       m_collection_NetFoldersList;		// The 'Net Folders'    collection point binder list.
		private List<String>       m_collection_SharedByMeList;		// The 'Shared by Me'   collection point binder list.
		private List<String>       m_collection_SharedWithMeList;	// The 'Shared with Me' collection point binder list.
		private List<String>       m_collection_SharedPublicList;	// The 'Public'         collection point binder list.
		private List<TeamInfo>     m_myTeamsList;					// The teams the current user is a member of.
		private Long               m_baseBinderId;					// The ID of the base binder this ASTreeData was constructed for.
		private Map<Long, Binder>  m_bindersMap;					// Map of the Binder's referenced by the lists.
		private Map<Long, User>    m_usersMap;						// Map of the User's   referenced by the lists.

		/*
		 * Constructor method.
		 */
		private ASTreeData(Long binderId) {
			// Initialize the super class...
			super();
			
			// ...and store the parameter.
			m_baseBinderId = binderId;
		}

		/*
		 * Get'er/Set'er methods.
		 */
		private Binder             getBinder(Long   binderId)      {return m_bindersMap.get(        binderId );}
		private Binder             getBinder(String binderId)      {return getBinder(Long.parseLong(binderId));}
		private User               getUser(  Long   userId)        {return m_usersMap.get(          userId   );}
		private User               getUser(  String userId)        {return getUser(Long.parseLong(  userId  ));}
		private List<FavoriteInfo> getMyFavoritesList()            {return m_myFavoritesList;                  }
		private List<TeamInfo>     getMyTeamsList()                {return m_myTeamsList;                      }
		private List<String>       getFollowedPlacesList()         {return m_followedPlacesList;               }
		private List<String>       getFollowedUsersList()          {return m_followedUsersList;                }
		private List<String>       getCollectionMyFilesList()      {return m_collection_MyFilesList;           }
		private List<String>       getCollectionNetFoldersList()   {return m_collection_NetFoldersList;        }
		private List<String>       getCollectionSharedByMeList()   {return m_collection_SharedByMeList;        }
		private List<String>       getCollectionSharedWithMeList() {return m_collection_SharedWithMeList;      }
		private List<String>       getCollectionSharedPublicList() {return m_collection_SharedPublicList;      }
		private Long               getBaseBinderId()               {return m_baseBinderId;                     }

		/*
		 * Constructs an ASTreeData object containing the information
		 * required to build the TreeInfo for displaying the activity
		 * streams sidebar tree.
		 */
		private static ASTreeData buildTreeData(HttpServletRequest request, AllModulesInjected bs, Long binderId) {
			// Construct the ASTreeData object that we'll return.
			ASTreeData reply = new ASTreeData(binderId);			
			
			// Read the various lists we need to construct the
			// ASTreeData.
			reply.m_followedPlacesList = GwtServerHelper.getTrackedPlaces(   bs);
			reply.m_followedUsersList  = GwtServerHelper.getTrackedPeople(   bs);
			reply.m_myFavoritesList    = GwtServerHelper.getFavorites(       bs);
			reply.m_myTeamsList        = GwtServerHelper.getMyTeams(request, bs);
			
			reply.m_collection_MyFilesList      = new ArrayList<String>(); fillCollectionLists(bs, CollectionType.MY_FILES,       reply.m_collection_MyFilesList     );
			reply.m_collection_SharedByMeList   = new ArrayList<String>(); fillCollectionLists(bs, CollectionType.SHARED_BY_ME,   reply.m_collection_SharedByMeList  );
			reply.m_collection_SharedWithMeList = new ArrayList<String>(); fillCollectionLists(bs, CollectionType.SHARED_WITH_ME, reply.m_collection_SharedWithMeList);
			reply.m_collection_SharedPublicList = new ArrayList<String>(); fillCollectionLists(bs, CollectionType.SHARED_PUBLIC,  reply.m_collection_SharedPublicList);
			reply.m_collection_NetFoldersList   = new ArrayList<String>(); fillCollectionLists(bs, CollectionType.NET_FOLDERS,    reply.m_collection_NetFoldersList  );
			
			// Read the required User's and Binder's.
			reply.m_usersMap   = readUsers(  bs,           reply);
			reply.m_bindersMap = readBinders(bs, binderId, reply);

			// If we get here, reply refers to an ASTreeData containing
			// the information required to construct a TreeInfo.
			// Return it.
			return reply;
		}
				
		/*
		 * Uses a single database read to build a Map of the binders,
		 * indexed by their ID, from lists of information contained in
		 * this ASTreeData object.
		 */
		private static Map<Long, Binder> readBinders(AllModulesInjected bs, Long binderId, ASTreeData td) {
			// Construct a List<Long> containing the IDs of the binders
			// being referenced.
			List<Long> binderIds = new ArrayList<Long>();
			binderIds.add(binderId);
			for (String       placeId:  td.m_followedPlacesList) ListUtil.addLongToListLongIfUnique(binderIds, placeId);			
			for (FavoriteInfo fi:       td.m_myFavoritesList)    ListUtil.addLongToListLongIfUnique(binderIds, fi.getValue());			
			for (TeamInfo     ti:       td.m_myTeamsList)        ListUtil.addLongToListLongIfUnique(binderIds, ti.getBinderId());
			
			for (String collectionId:  td.m_collection_MyFilesList)      ListUtil.addLongToListLongIfUnique(binderIds, collectionId);			
			for (String collectionId:  td.m_collection_SharedByMeList)   ListUtil.addLongToListLongIfUnique(binderIds, collectionId);			
			for (String collectionId:  td.m_collection_SharedWithMeList) ListUtil.addLongToListLongIfUnique(binderIds, collectionId);			
			for (String collectionId:  td.m_collection_SharedPublicList) ListUtil.addLongToListLongIfUnique(binderIds, collectionId);			
			for (String collectionId:  td.m_collection_NetFoldersList)   ListUtil.addLongToListLongIfUnique(binderIds, collectionId);			
			
			// Read the binders that we're tracking (in a single
			// database read) and scan them...
			Map<Long, Binder> reply = new HashMap<Long, Binder>();
			if (!(binderIds.isEmpty())) {
				SortedSet<Binder> binders;
				try {
					binders = bs.getBinderModule().getBinders(binderIds);
				}
				catch (Exception ex) {
					m_logger.debug("GwtActivityStreamHelper.readBinders( 2:EXCEPTION ):  ", ex);
					binders = new TreeSet<Binder>();
				}
				for (Binder b:  binders) {
					// ...adding each to a Map using their ID as the
					// ...key.
					if ((!(b.isDeleted())) && (!(GwtUIHelper.isBinderPreDeleted(b)))) {
						reply.put(b.getId(), b);
					}
				}
			}
			
			// If we get here, reply refers to the Map<Long, Binder>
			// being requested.  Return it.
			return reply;
		}
		
		private static ASTreeData buildTreeData(HttpServletRequest request, AllModulesInjected bs, String binderId) {
			// Always use the initial form of the method.
			return buildTreeData(request, bs, Long.parseLong(binderId));
		}
		
		/*
		 * Uses a single database read to build a Map of the users,
		 * indexed by their user ID, from lists of information contained in
		 * this ASTreeData object.
		 */
		private static Map<Long, User> readUsers(AllModulesInjected bs, ASTreeData td) {
			// Construct a List<Long> containing the IDs of the users
			// being referenced.
			List<Long> userIds = new ArrayList<Long>();
			for (String userId:  td.m_followedUsersList) {
				ListUtil.addLongToListLongIfUnique(userIds, userId);
			}			
			
			// Read them (in single database read) and scan them...
			Map<Long, User> reply = new HashMap<Long, User>();
			if (!(userIds.isEmpty())) {
				SortedSet<Principal> authorPrincipals;
				try {
					authorPrincipals = bs.getProfileModule().getPrincipals(userIds, false);
				}
				catch (Exception ex) {
					m_logger.debug("GwtActivityStreamHelper.readUsers( 2:EXCEPTION ):  ", ex);
					authorPrincipals = new TreeSet<Principal>();
				}
				for (Principal p:  authorPrincipals) {
					// ...adding each as a User to a Map using their ID
					// ...as the key.
					if (!(p.isDeleted())) {
						reply.put(p.getId(), ((User) p));
					}
				}
			}
			
			// If we get here, reply refers to the Map<Long, User>
			// being requested.  Return it.
			return reply;
		}
	}
	
	/*
	 * Constructor method.
	 * 
	 * Private to inhibits this class from being instantiated. 
	 */
	private GwtActivityStreamHelper() {
		// Nothing to do.
		super();
	}

	/*
	 * Returns true if two activity stream information objects are
	 * compatible (i,e., they refer to the same, valid activity stream
	 * and they refer to compatible binder ID lists, as appropriate)
	 * and returns false otherwise.
	 */
	private static boolean areASIsCompatible(ActivityStreamInfo asi1, ActivityStreamInfo asi2) {		
		// Do the ASI's refer to the same activity list type?
		boolean singleBinder          = false;
		boolean singleBinderMustMatch = false;
		ActivityStream as1 = asi1.getActivityStream();
		boolean reply = (as1 == asi2.getActivityStream());
		if (reply) {
			// Yes!  Extract the binder ID lists and counts from them.
			String[] bl1 = asi1.getBinderIds(); int bc1 = ((null == bl1) ? 0 : bl1.length);
			String[] bl2 = asi2.getBinderIds(); int bc2 = ((null == bl2) ? 0 : bl2.length);
			
			// Can we handle checking their binder ID lists?
			switch (as1) {
			default:
			case UNKNOWN:
				// These can never be valid!
				reply = false;
				break;
				
			case FOLLOWED_PEOPLE:
			case FOLLOWED_PLACES:
			case MY_FAVORITES:
			case MY_FILES:
			case MY_TEAMS:
			case NET_FOLDERS:
			case SHARED_BY_ME:
			case SHARED_WITH_ME:
			case SHARED_PUBLIC:
				// These are valid so long as they both refer to a
				// non-empty binder ID list.
				reply = ((0 < bc1) && (0 < bc2));
				break;
				
			case CURRENT_BINDER:
				// This is valid so long as they both refer to a single
				// binder in their binder ID lists. 
				singleBinder = true;
				break;
				
			case FOLLOWED_PERSON:
			case FOLLOWED_PLACE:
			case MY_FAVORITE:
			case MY_FILE:
			case MY_TEAM:
			case NET_FOLDER:
			case SHARED_BY_ME_FOLDER:
			case SHARED_WITH_ME_FOLDER:
			case SHARED_PUBLIC_FOLDER:
			case SPECIFIC_BINDER:
			case SPECIFIC_FOLDER:
				// These are valid so long as they both refer to the
				// same, single binder in their binder ID list. 
				singleBinder          =
				singleBinderMustMatch = true;
				break;
				
			case SITE_WIDE:
				// This is valid so long as neither refer to any
				// binders in their binder ID lists. 
				reply = ((0 == bc1) && (0 == bc2));
				break;				
			}

			// Must both ASI's refer to a single binder?
			if (singleBinder) {
				// Yes!  Do they?
				reply = ((bc1 == bc2) && (1 == bc1));
				if (reply) {
					// Yes!  Must those single binder's match?
					if (singleBinderMustMatch) {
						// Yes!  Do they?
						reply = bl1[0].equals(bl2[0]);
					}
				}
			}
		}

		// If we get here, reply is true if the ASI's are compatible
		// and false otherwise.  Return it.
		return reply;
	}

	/*
	 * Returns an ActivityStreamEntry object based on an EntryData
	 * object.
	 */
	@SuppressWarnings("unchecked")
	private static ActivityStreamEntry buildASEFromED(HttpServletRequest request, SeenMap sm, ASEntryData entryData, boolean forcePlainTextDescriptions) {
		ActivityStreamEntry reply = new ActivityStreamEntry();

		// First, initialize the author information.
		Map em = entryData.getEntryMap();
		ASAuthorInfo authorInfo = entryData.getAuthorInfo();
		reply.setAuthorPresence(   authorInfo.m_authorPresence );
		reply.setAuthorAvatarUrl(  authorInfo.m_authorAvatarUrl);
		reply.setAuthorId(         authorInfo.m_authorId       );
		reply.setAuthorName(       authorInfo.m_authorTitle    );
		reply.setAuthorWorkspaceId(authorInfo.m_authorWsId     );
		reply.setAuthorLogin(
			GwtServerHelper.getStringFromEntryMap(
				em,
				Constants.CREATOR_NAME_FIELD));

		// Then the parent binder information.
		ASBinderInfo binderInfo = entryData.getBinderInfo();
		reply.setParentBinderId(   binderInfo.m_binderId   );
		reply.setParentBinderHover(binderInfo.m_binderHover);
		reply.setParentBinderName( binderInfo.m_binderName );
		
		// Then the entry information.
		String entryId = String.valueOf(entryData.getEntryId());
		reply.setEntryId(               entryId                                                                      );
		reply.setEntryComments(         entryData.getCommentCount()                                                  );
		reply.setEntryDescription(      getEntryDescFromEM(                   em, request                           ));	
		reply.setEntryDescriptionFormat(getEntryDescFormatFromEM(             em                                    ));	
		reply.setEntryDocNum(           GwtServerHelper.getStringFromEntryMap(em, Constants.DOCNUMBER_FIELD         ));
		reply.setEntryModificationDate( GwtServerHelper.getStringFromEntryMap(em, Constants.MODIFICATION_DATE_FIELD ));		
		reply.setEntryTitle(            GwtServerHelper.getStringFromEntryMap(em, Constants.TITLE_FIELD             ));
		reply.setEntryTopEntryId(       GwtServerHelper.getStringFromEntryMap(em, Constants.ENTRY_TOP_ENTRY_ID_FIELD));
		reply.setEntryType(             GwtServerHelper.getStringFromEntryMap(em, Constants.ENTRY_TYPE_FIELD        ));
		reply.setEntrySeen(             sm.checkIfSeen(                       em                                    ));

		// Is this entry a file?
		if (GwtServerHelper.isFamilyFile(GwtServerHelper.getStringFromEntryMap(em, Constants.FAMILY_FIELD))) {
			// Yes!  Can we find its filename?
			reply.setEntryFile(true);
			String fName = GwtServerHelper.getStringFromEntryMap(em, Constants.FILENAME_FIELD);
			if (MiscUtil.hasString(fName)) {
				// Yes!  Map it to a file icon.
				reply.setEntryFileIcon(
					FileIconsHelper.getFileIconFromFileName(
						fName,
						GwtViewHelper.mapBISToIS(
							BinderIconSize.getActivityStreamIconSize())));
			}
		}

		// Are we supposed to force plain text descriptions?
		if (forcePlainTextDescriptions) {
			// Yes!  Is this entry's description in HTML?
			if (Description.FORMAT_HTML == reply.getEntryDescriptionFormat()) {
				String desc = reply.getEntryDescription();
				if (MiscUtil.hasString(desc)) {
					// Yes!  Convert it to plain text...
					reply.setEntryDescription(Html.stripHtml(desc));
				}
				
				// ...and mark the description format as being plain
				// ...text.
				reply.setEntryDescriptionFormat(Description.FORMAT_NONE);
			}
		}

		// Finally, scan the comment ASEntryData...
		List<ActivityStreamEntry> commentsASEList = reply.getComments();
		for (ASEntryData commentEntryData:  entryData.getCommentEntryDataList()) {
			// ...adding an ActivityStreamEntry for each to the
			// ...ActivityStreamEntry's List<ActivityStreamEntry> of
			// ...comments.
			commentsASEList.add(buildASEFromED(request, sm, commentEntryData, forcePlainTextDescriptions));
		}
		
		// If we get here, reply refers to the ActivityStreamEntry that
		// corresponds to the ASEntryData.  Return it.
		return reply;
	}
	
	/*
	 * Builds an ActivityStreamInfo object based on an ActivityStream
	 * enumeration value and an String[] of Binder IDs.
	 */
	private static ActivityStreamInfo buildASI(ActivityStream as, List<String> asIdsList, String title) {
		ActivityStreamInfo reply = new ActivityStreamInfo();

		reply.setActivityStream(as);
		if ((null != asIdsList) && (!(asIdsList.isEmpty()))) {
			reply.setBinderIds(asIdsList.toArray(new String[0]));
		}
		reply.setTitle(title);
		
		return reply;
	}
	
	private static ActivityStreamInfo buildASI(ActivityStream as, String asId, String title) {
		// Always use the initial form of the method.
		List<String> asIdsList;
		if (MiscUtil.hasString(asId)) {
			asIdsList = new ArrayList<String>();
			asIdsList.add(asId);
		}
		else {
			asIdsList = null;
		}
		return buildASI(as, asIdsList, title);
	}

	/*
	 * Builds an TreeInfo object based on a Binder's ID, title, hover
	 * text and an ActivityStream enumeration value.
	 */
	private static TreeInfo buildASTI(HttpServletRequest request, AllModulesInjected bs, boolean isBinder, String id, String title, String hover, ActivityStream as) {
		TreeInfo reply = new TreeInfo();
		if (isBinder && MiscUtil.hasString(id)) {
			reply.setBinderInfo(GwtServerHelper.getBinderInfo(bs, request, id));
		}
		
		reply.setActivityStream(true);
		reply.setBinderTitle(title);
		reply.setBinderHover(hover);
		reply.setBinderBorderTop(Utils.checkIfFilr() && ActivityStream.SITE_WIDE.equals(as));
		reply.setActivityStreamEvent(
			TeamingEvents.ACTIVITY_STREAM,
			buildASI(
				as,
				id,
				title));
		
		return reply;
	}

	/*
	 * Builds a TreeInfo for a collection point activity stream.
	 */
	private static TreeInfo buildCollectionPointTI(AllModulesInjected bs, HttpServletRequest request, ASTreeData td, CollectionType ct) {
		ActivityStream	ctFolderAS;
		ActivityStream	ctRootAS;
		boolean			ctBorderTop;
		List<String>	ctFoldersList;
		String			ctTitleKey;
		switch (ct) {
		default:
		case MY_FILES:
			ctBorderTop   = true;
			ctTitleKey    = "asTreeMyFiles";
			ctFolderAS    = ActivityStream.MY_FILE;
			ctRootAS      = ActivityStream.MY_FILES;
			ctFoldersList = td.getCollectionMyFilesList();
			break;
			
		case NET_FOLDERS:
			ctBorderTop   = false;
			ctTitleKey    = "asTreeNetFolders";
			ctFolderAS    = ActivityStream.NET_FOLDER;
			ctRootAS      = ActivityStream.NET_FOLDERS;
			ctFoldersList = td.getCollectionNetFoldersList();
			break;
			
		case SHARED_BY_ME:
			ctBorderTop   = false;
			ctTitleKey    = "asTreeSharedByMe";
			ctFolderAS    = ActivityStream.SHARED_BY_ME_FOLDER;
			ctRootAS      = ActivityStream.SHARED_BY_ME;
			ctFoldersList = td.getCollectionSharedByMeList();
			break;
			
		case SHARED_WITH_ME:
			ctBorderTop   = false;
			ctTitleKey    = "asTreeSharedWithMe";
			ctFolderAS    = ActivityStream.SHARED_WITH_ME_FOLDER;
			ctRootAS      = ActivityStream.SHARED_WITH_ME;
			ctFoldersList = td.getCollectionSharedWithMeList();
			break;
			
		case SHARED_PUBLIC:
			ctBorderTop   = false;
			ctTitleKey    = "asTreeSharedPublic";
			ctFolderAS    = ActivityStream.SHARED_PUBLIC_FOLDER;
			ctRootAS      = ActivityStream.SHARED_PUBLIC;
			ctFoldersList = td.getCollectionSharedPublicList();
			break;
		}
		
		TreeInfo asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get(ctTitleKey));
		asTI.setBinderBorderTop(ctBorderTop);
		int idCount = ctFoldersList.size();
		if (0 < idCount) {
			// Yes!  Scan them.
			List<TreeInfo>	asTIChildren = asTI.getChildBindersList();
			List<String>	asIdsList    = new ArrayList<String>();
			for (String ctFolderId: ctFoldersList) {
				// Can we access the next one's Binder?
				String	id     = ctFolderId;
				Binder	binder = td.getBinder(id);
				if (null != binder) {
					// Yes!  If this is other than the user's active My
					// Files Storage folder...
			    	if (!(BinderHelper.isBinderUsersActiveMyFilesStorage(bs, binder))) {
						// ...add an appropriate TreeInfo for it.
						asIdsList.add(id);					
						TreeInfo asTIChild = buildASTI(
							request,
							bs,
							true,
							id,
							binder.getTitle(),
							binder.getPathName(),
							ctFolderAS);					
						asTIChildren.add(asTIChild);
			    	}
				}
			}
			
			// If we processed any entries...
			if (!(asIdsList.isEmpty())) {
				// ...update the parent TreeInfo.
				asTI.updateChildBindersCount();
				asTI.setActivityStreamEvent(
					TeamingEvents.ACTIVITY_STREAM,
					buildASI(
						ctRootAS,
						asIdsList,
						asTI.getBinderTitle()));
			}
		}
		
		// If we didn't add any folders...
		if (TeamingEvents.UNDEFINED == asTI.getActivityStreamEvent()) {
			// ...we need a base event so that we can display no
			// ...selections in the control.
			asTI.setActivityStreamEvent(
				TeamingEvents.ACTIVITY_STREAM,
				buildASI(
					ctRootAS,
					((List<String>) null),
					asTI.getBinderTitle()));
		}

		// Return the TreeInfo just build.
		return asTI;
	}
	
	/*
	 * Builds a TreeInfo for the user's Followed People activity
	 * stream.
	 */
	private static TreeInfo buildFollowedPeopleTI(AllModulesInjected bs, HttpServletRequest request, ASTreeData td, boolean isOtherUserAccessRestricted) {
		TreeInfo asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeFollowedPeople"));
		List<String> followedUsersList = td.getFollowedUsersList();
		int idCount = ((null == followedUsersList) ? 0 : followedUsersList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			List<TreeInfo>	asTIChildren = asTI.getChildBindersList();
			List<String>	asIdsList    = new ArrayList<String>();
			for (String followedUserId: followedUsersList) {
				// Can we access the next one's User?
				String	id   = followedUserId;
				User	user = td.getUser(id);
				if (null != user) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIdsList.add(id);					
					TreeInfo asTIChild = buildASTI(
						request,
						bs,
						false,
						id,
						GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, id, user.getTitle()),
						null,
						ActivityStream.FOLLOWED_PERSON);					
					asTIChildren.add(asTIChild);
				}
			}
			
			// If we processed any entries...
			if (!(asIdsList.isEmpty())) {
				// ...update the parent TreeInfo.
				asTI.updateChildBindersCount();
				asTI.setActivityStreamEvent(
					TeamingEvents.ACTIVITY_STREAM,
					buildASI(
						ActivityStream.FOLLOWED_PEOPLE,
						asIdsList,
						asTI.getBinderTitle()));
			}
		}
		
		// If we didn't add any people...
		if (TeamingEvents.UNDEFINED == asTI.getActivityStreamEvent()) {
			// ...we need a base event so that we can display no
			// ...selections in the control.
			asTI.setActivityStreamEvent(
				TeamingEvents.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.FOLLOWED_PEOPLE,
					((List<String>) null),
					asTI.getBinderTitle()));
		}

		// Return the TreeInfo just build.
		return asTI;
	}
	
	/*
	 * Builds a TreeInfo for the user's Followed Places activity
	 * stream.
	 */
	private static TreeInfo buildFollowedPlacesTI(AllModulesInjected bs, HttpServletRequest request, ASTreeData td) {
		TreeInfo asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeFollowedPlaces"));
		List<String> followedPlacesList = td.getFollowedPlacesList();
		int idCount = ((null == followedPlacesList) ? 0 : followedPlacesList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			List<TreeInfo>	asTIChildren = asTI.getChildBindersList();
			List<String>	asIdsList    = new ArrayList<String>();
			for (String followedPlaceId: followedPlacesList) {
				// Can we access the next one's Binder?
				String	id     = followedPlaceId;
				Binder	binder = td.getBinder(id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIdsList.add(id);					
					TreeInfo asTIChild = buildASTI(
						request,
						bs,
						true,
						id,
						binder.getTitle(),
						binder.getPathName(),
						ActivityStream.FOLLOWED_PLACE);					
					asTIChildren.add(asTIChild);
				}
			}
			
			// If we processed any entries...
			if (!(asIdsList.isEmpty())) {
				// ...update the parent TreeInfo.
				asTI.updateChildBindersCount();
				asTI.setActivityStreamEvent(
					TeamingEvents.ACTIVITY_STREAM,
					buildASI(
						ActivityStream.FOLLOWED_PLACES,
						asIdsList,
						asTI.getBinderTitle()));
			}
		}
		
		// If we didn't add any places...
		if (TeamingEvents.UNDEFINED == asTI.getActivityStreamEvent()) {
			// ...we need a base event so that we can display no
			// ...selections in the control.
			asTI.setActivityStreamEvent(
				TeamingEvents.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.FOLLOWED_PLACES,
					((List<String>) null),
					asTI.getBinderTitle()));
		}

		// Return the TreeInfo just build.
		return asTI;
	}
	
	/*
	 * Builds a TreeInfo for the user's My Favorites activity stream.
	 */
	private static TreeInfo buildMyFavoritesTI(AllModulesInjected bs, HttpServletRequest request, ASTreeData td) {
		// Does the user have any favorites defined?
		TreeInfo asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeMyFavorites"));
		asTI.setBinderBorderTop(true);
		List<FavoriteInfo> myFavoritesList = td.getMyFavoritesList();
		int idCount = ((null == myFavoritesList) ? 0 : myFavoritesList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			List<TreeInfo>	asTIChildren = asTI.getChildBindersList();
			List<String>	asIdsList    = new ArrayList<String>();
			for (FavoriteInfo myFavorite: myFavoritesList) {
				// Can we access the next one's Binder?
				String	id     = myFavorite.getValue();
				Binder	binder = td.getBinder(id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIdsList.add(id);					
					TreeInfo asTIChild = buildASTI(
						request,
						bs,
						true,
						id,
						myFavorite.getName(),
						myFavorite.getHover(),
						ActivityStream.MY_FAVORITE);
					asTIChildren.add(asTIChild);
				}
			}

			// If we processed any entries...
			if (!(asIdsList.isEmpty())) {
				// ...update the parent TreeInfo.
				asTI.updateChildBindersCount();
				asTI.setActivityStreamEvent(
					TeamingEvents.ACTIVITY_STREAM,
					buildASI(
						ActivityStream.MY_FAVORITES,
						asIdsList,
						asTI.getBinderTitle()));
			}
		}

		// If we didn't add any favorites...
		if (TeamingEvents.UNDEFINED == asTI.getActivityStreamEvent()) {
			// ...we need a base event so that we can display no
			// ...selections in the control.
			asTI.setActivityStreamEvent(
				TeamingEvents.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.MY_FAVORITES,
					((List<String>) null),
					asTI.getBinderTitle()));
		}

		// Return the TreeInfo just build.
		return asTI;
	}
	
	/*
	 * Builds a TreeInfo for the user's My Teams activity stream.
	 */
	private static TreeInfo buildMyTeamsTI(AllModulesInjected bs, HttpServletRequest request, ASTreeData td) {
		TreeInfo asTI = new TreeInfo();
		asTI.setActivityStream(true);
		asTI.setBinderTitle(NLT.get("asTreeMyTeams"));
		List<TeamInfo> myTeamsList = td.getMyTeamsList();
		int idCount = ((null == myTeamsList) ? 0 : myTeamsList.size());
		if (0 < idCount) {
			// Yes!  Scan them.
			List<TreeInfo>	asTIChildren = asTI.getChildBindersList();
			List<String>	asIdsList    = new ArrayList<String>();
			for (TeamInfo myTeam: myTeamsList) {
				// Can we access the next one's Binder?
				String	id     = myTeam.getBinderId();
				Binder	binder = td.getBinder(id);
				if (null != binder) {
					// Yes!  Add an appropriate TreeInfo for it.
					asIdsList.add(id);					
					TreeInfo asTIChild = buildASTI(
						request,
						bs,
						true,
						id,
						myTeam.getTitle(),
						binder.getPathName(),
						ActivityStream.MY_TEAM);					
					asTIChildren.add(asTIChild);
				}
			}
			
			// If we processed any entries...
			if (!(asIdsList.isEmpty())) {
				// ...update the parent TreeInfo.
				asTI.updateChildBindersCount();
				asTI.setActivityStreamEvent(
					TeamingEvents.ACTIVITY_STREAM,
					buildASI(
						ActivityStream.MY_TEAMS,
						asIdsList,
						asTI.getBinderTitle()));
			}
		}
		
		// If we didn't add any teams...
		if (TeamingEvents.UNDEFINED == asTI.getActivityStreamEvent()) {
			// ...we need a base event so that we can display no
			// ...selections in the control.
			asTI.setActivityStreamEvent(
				TeamingEvents.ACTIVITY_STREAM,
				buildASI(
					ActivityStream.MY_TEAMS,
					((List<String>) null),
					asTI.getBinderTitle()));
		}
		
		// Return the TreeInfo just build.
		return asTI;
	}
	
	/*
	 * Constructs and returns the base Criteria object for performing
	 * the search for activity stream data.
	 */
	private static Criteria buildSearchCriteria(AllModulesInjected bs, List<String> trackedPlacesAL, List<String> trackedEntriesAL, List<String> trackedUsersAL) {
		// Create the search criteria for the places, entries and
		// people...
		Criteria reply =
			SearchUtils.entriesForTrackedPlacesEntriesAndPeople(
				bs,
				trackedPlacesAL,
				trackedEntriesAL,
				trackedUsersAL,
				true,	// true -> Entries only (no replies.)
				Constants.LASTACTIVITY_FIELD);

		// ...and return it.
		return reply;
	}
	
	private static Criteria buildSearchCriteria(AllModulesInjected bs, Long entryId) {
		Criteria reply = new Criteria();
		reply.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}))
		     .add(Restrictions.in(Constants.DOC_TYPE_FIELD,   new String[] {Constants.DOC_TYPE_ENTRY                              }))
		     .add(Restrictions.in(Constants.DOCID_FIELD,      new String[] {String.valueOf(entryId)                               }));
		return reply;
	}
	
	/*
	 * Constructs and returns the base Criteria object for performing
	 * the search for activity stream data.
	 */
	private static Document buildSearchQuery(AllModulesInjected bs, Long sfId, SpecificFolderData sfData, String lastActivityStart, String lastActivityEnd) {
		Long creationStartTime;
		Long creationEndTime;
		TagInfo globalTagInfo;

		// Create a SearchFilter for the search query.
		SearchFilter sf = new SearchFilter(true);

		// If we to search for a date range bounded set of items...
		if (MiscUtil.hasString(lastActivityStart) && MiscUtil.hasString(lastActivityEnd)) {
			// ...add in the dates.
			sf.addLastActivityDateRange(lastActivityStart, lastActivityEnd);
		}

		// Yes!  Do we need to factor in the user's search filters?
		if (sfData.isApplyFolderFilters()) {
			// Yes!  Do they have any defined on the folder?
			Folder			folder               = bs.getFolderModule().getFolder(sfId);
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), sfId);
			Document searchFilters = BinderHelper.getBinderSearchFilter(bs, folder, userFolderProperties, true);
			if (null != searchFilters) {
				// Yes!  Add them to the search filter.
				sf.appendFilter(searchFilters);
			}
		}
		
		// Do we have a creation start/end time?
		creationStartTime = sfData.getCreationStartTime();
		creationEndTime   = sfData.getCreationEndTime();
		if ((null != creationStartTime) && (null != creationEndTime)) {
			// Yes!  Add it to the filter.
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.forTimeZone(GwtServerHelper.getCurrentUser().getTimeZone()));
			Date date = new Date(creationStartTime.longValue());
			String startDate = fmt.print(new DateTime(date));
			date = new Date(creationEndTime.longValue());
			String endDate = fmt.print(new DateTime(date));
			sf.addCreationDateRange(startDate, endDate);
		}
		
		// Do we have a global tag?
		globalTagInfo = sfData.getGlobalTagInfo();
		if (null != globalTagInfo) {
			// Yes!  Add it to the filter.
			sf.addCommunityTag(globalTagInfo.getTagSearchText());
		}

		// If we get here, sf refers to a SearchFilter object
		// containing the search query for the activity stream data.
		// Extract its XML Document. 
		Document reply = sf.getFilter();
		
		// If we're logging debug messages...
		if (m_logger.isDebugEnabled()) {
			// ...dump the search filter XML.
			m_logger.debug("GwtActivityStreamHelper.buildSearchQuery():  Search query:\n" + GwtServerHelper.getXmlString(reply));
		}

		// Finally, return the search query XML.
		return reply;
	}
	
	private static Document buildSearchQuery(AllModulesInjected bs, Long sfId, SpecificFolderData sfData) {
		// Always use the initial form of the method.
		return buildSearchQuery(bs, sfId, sfData, null, null);	// nulls -> No activity date range.
	}
	
	/*
	 * Returns a List<String> of the IDs of the folders associated
	 * with a collection.
	 */
	@SuppressWarnings("unchecked")
	private static void fillCollectionLists(AllModulesInjected bs, CollectionType collectionType, List<String> folderIds, List<String> entryIds) {
		// If we don't have lists to track the folders or entries...
		boolean	trackFolders = (null != folderIds);
		boolean	trackEntries = (null != entryIds);
		if ((!trackFolders) && (!trackEntries)) {
			// ...bail.
			return;
		}
		
		User	user     = GwtServerHelper.getCurrentUser();
		String	userWSId = String.valueOf(user.getWorkspaceId());
		
		// Based on the installed license, what definition families do
		// we consider as 'file'?
		String[] fileFamilies;
		if (Utils.checkIfFilr())
		     fileFamilies = new String[]{Definition.FAMILY_FILE                         };
		else fileFamilies = new String[]{Definition.FAMILY_FILE, Definition.FAMILY_PHOTO};
		
		switch (collectionType) {
		default:
		case MY_FILES: {
			// Search for file folders within the binder...
			Criteria crit = new Criteria();
			crit.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
			crit.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{userWSId}));
			crit.add(in(Constants.FAMILY_FIELD,            fileFamilies));
			crit.add(in(Constants.IS_LIBRARY_FIELD,        new String[]{Constants.TRUE}));

			// ...that are non-mirrored...
			Disjunction disj = disjunction();
			crit.add(disj);
			Conjunction conj = conjunction();
			conj.add(in(Constants.IS_MIRRORED_FIELD, new String[]{Constants.FALSE}));
			disj.add(conj);

			// ...or configured mirrored File Folders.
    		conj = conjunction();
			conj.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
			conj.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));
			conj.add(in(Constants.IS_HOME_DIR_FIELD,         new String[]{Constants.TRUE}));
			disj.add(conj);
			
			Map searchResults = bs.getBinderModule().executeSearchQuery(
				crit,
				Constants.SEARCH_MODE_NORMAL,
				0,
				ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS,
				null);
			
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			for (Map entryMap:  searchEntries) {
				if (trackFolders) {
					folderIds.add(GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DOCID_FIELD));
				}
			}
			
			break;
		}

		case NET_FOLDERS: {
			// Can we access the ID of the top workspace?
			String	topWSId  = GwtUIHelper.getTopWSIdSafely(bs, false);
			if (!(MiscUtil.hasString(topWSId))) {
				// No!  Then use the ID of the user's workspace.
				topWSId = userWSId;
			}

			// Add the criteria for top level mirrored file folders
			// that have been configured.
			Criteria crit = new Criteria();
			crit.add(in(Constants.DOC_TYPE_FIELD,            new String[]{Constants.DOC_TYPE_BINDER}));
			crit.add(in(Constants.ENTRY_ANCESTRY,            new String[]{topWSId}));
			crit.add(in(Constants.FAMILY_FIELD,              new String[]{Definition.FAMILY_FILE}));
			crit.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
			crit.add(in(Constants.IS_TOP_FOLDER_FIELD,       new String[]{Constants.TRUE}));
			crit.add(in(Constants.IS_HOME_DIR_FIELD,         new String[]{Constants.FALSE}));
    		crit.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));
			
			Map searchResults = bs.getBinderModule().executeSearchQuery(
				crit,
				Constants.SEARCH_MODE_NORMAL,
				0,
				ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS,
				null);
			
			List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			for (Map entryMap:  searchEntries) {
				if (trackFolders) {
					folderIds.add(GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DOCID_FIELD));
				}
			}
			
			break;
		}
		
		case SHARED_BY_ME: {
			// Scan the SharedItem's shared by the current user.
			ShareItemSelectSpec	spec   = new ShareItemSelectSpec();
			Long				userId = GwtServerHelper.getCurrentUserId();
			List<Long>			users  = new ArrayList<Long>();
			users.add(userId);
			spec.setSharerIds(users);
			List<ShareItem> shareItems = bs.getSharingModule().getShareItems(spec);
			for (ShareItem si:  shareItems) {
				// Is this share expired or obsolete?
				if (si.isExpired() || (!(si.isLatest()))) {
					// Yes!  Skip it.
					continue;
				}
				
				// Add the share's entity ID to the appropriate list. 
				EntityIdentifier	siEntityIdentifier = si.getSharedEntityIdentifier();
				EntityType			siEntityType       = siEntityIdentifier.getEntityType();
				String				siEntityId         = String.valueOf(siEntityIdentifier.getEntityId());
				if      (trackFolders && siEntityType.equals(EntityType.folder))      ListUtil.addStringToListStringIfUnique(folderIds, siEntityId);
				else if (trackEntries && siEntityType.equals(EntityType.folderEntry)) ListUtil.addStringToListStringIfUnique(entryIds,  siEntityId);
			}
			
			break;
		}
			
		case SHARED_WITH_ME: {
			// Scan the SharedItem's shared with the current user (or
			// their teams or groups.)
			ShareItemSelectSpec	spec = new ShareItemSelectSpec();
			spec.setRecipientsFromUserMembership(user.getId());
			List<ShareItem> shareItems = bs.getSharingModule().getShareItems(spec);
			for (ShareItem si:  shareItems) {
				// Is this share expired, obsolete or public?
				if (si.isExpired() || (!(si.isLatest()) || si.getIsPartOfPublicShare())) {
					// Yes!  Skip it.
					continue;
				}
				
				// Add the share's entity ID to the appropriate list. 
				EntityIdentifier	siEntityIdentifier = si.getSharedEntityIdentifier();
				EntityType			siEntityType       = siEntityIdentifier.getEntityType();
				String				siEntityId         = String.valueOf(siEntityIdentifier.getEntityId());
				if      (trackFolders && siEntityType.equals(EntityType.folder))      ListUtil.addStringToListStringIfUnique(folderIds, siEntityId);
				else if (trackEntries && siEntityType.equals(EntityType.folderEntry)) ListUtil.addStringToListStringIfUnique(entryIds,  siEntityId);
			}
			
			break;
		}
		
		case SHARED_PUBLIC: {
			// Scan the SharedItem's shared with the current user (or
			// their teams or groups.)
			ShareItemSelectSpec	spec = new ShareItemSelectSpec();
			spec.setRecipientsFromUserMembership(user.getId());
			List<ShareItem> shareItems = bs.getSharingModule().getShareItems(spec);
			for (ShareItem si:  shareItems) {
				// Is this share expired, obsolete or not public?
				if (si.isExpired() || (!(si.isLatest())  || (!(si.getIsPartOfPublicShare())))) {
					// Yes!  Skip it.
					continue;
				}
				
				// Add the share's entity ID to the appropriate list. 
				EntityIdentifier	siEntityIdentifier = si.getSharedEntityIdentifier();
				EntityType			siEntityType       = siEntityIdentifier.getEntityType();
				String				siEntityId         = String.valueOf(siEntityIdentifier.getEntityId());
				if      (trackFolders && siEntityType.equals(EntityType.folder))      ListUtil.addStringToListStringIfUnique(folderIds, siEntityId);
				else if (trackEntries && siEntityType.equals(EntityType.folderEntry)) ListUtil.addStringToListStringIfUnique(entryIds,  siEntityId);
			}
			
			break;
		}
		}
	}
	
	private static void fillCollectionLists(AllModulesInjected bs, CollectionType collectionType, List<String> folderIds) {
		// Always use the initial form of the method.
		fillCollectionLists(bs, collectionType, folderIds, null);
	}
	
	/*
	 * Searches the activity stream information objects in a tree
	 * information object and returns the one that matches the given
	 * activity stream information object.
	 */
	private static ActivityStreamInfo findASIInTIList(ActivityStreamInfo asi, List<TreeInfo> tiList) {
		// Scan the tree information's children.
		ActivityStreamInfo reply = null;
		ActivityStream as = asi.getActivityStream();		
		for (TreeInfo ti:  tiList) {
			// Does this child contain an activity stream event?
			if (TeamingEvents.ACTIVITY_STREAM == ti.getActivityStreamEvent()) {
				// Yes!  Does that event's activity stream match that
				// we were given?
				ActivityStreamInfo tiASI = ti.getActivityStreamInfo();
				if (tiASI.getActivityStream().equals(as)) {
					// Yes!  Are the to ASI's compatible and can the
					// one from the tree information object be selected?
					if (areASIsCompatible(asi, tiASI) && isASISelectable(tiASI)) {
						// Yes!  Then we'll return it.  Stop looking.
						reply = tiASI;
						break;
					}
				}
			}

			// If we get here, the ASI we were given doesn't match the
			// one from this tree information object!  Does it match
			// any of its children?
			reply = findASIInTIList(asi, ti.getChildBindersList());
			if (null != reply) {
				// Yes!  Then we'll return that.  Stop looking.
				break;
			}
		}

		// If we get here, reply is null or refers to the activity
		// stream object from the tree information that matches the one
		// we were given.  Return it.
		return reply;
	}

	/*
	 * Performs whatever fixups are necessary on an entry's description
	 * and returns it.
	 */
	@SuppressWarnings("unchecked")
	private static String fixupDescription(HttpServletRequest request, Map entryMap, String desc) {
		// Were we given a description?
		String reply = desc;
		if (MiscUtil.hasString(desc)) {
			// Yes!  Is there anything left after fixing its markup?
			reply = MarkupUtil.markupStringReplacement(
				null,
				null,
				request,
				null,
				entryMap,
				desc,
				"view");
			
			if (MiscUtil.hasString(reply)) {
				// Yes!  Do we need to further truncate what gets
				// displayed?
				int displayWords = m_activityStreamParams.getDisplayWords();
				if (displayWords != (-1)) {
					// Yes!  Strip it down it its bare necessities.
					reply = MarkupUtil.markupSectionsReplacement(reply);
					if (MiscUtil.hasString(reply)) {
						reply = Html.wordStripHTML(
							reply,
							m_activityStreamParams.getDisplayWords());
					}
				}
			}
		}

		// If we get here, reply refers to the fixed up description.
		// Return it.
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
	 * @param asdt
	 * @param sfData
	 * 
	 * @return
	 */
	public static ActivityStreamData getActivityStreamData(HttpServletRequest request, AllModulesInjected bs, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd, ActivityStreamDataType asdt, SpecificFolderData sfData) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtActivityStreamHelper.getActivityStreamData()");
		try {
			// Create an activity stream data object to return.
			ActivityStreamData reply = new ActivityStreamData();
			reply.setDateTime(GwtServerHelper.getDateTimeString(new Date()));
			
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
			try {
				populateASD(
					request,
					bs,
					bs.getProfileModule().getUserSeenMap(null),
					GwtServerHelper.isPresenceEnabled(),
					Utils.canUserOnlySeeCommonGroupMembers(),
					reply,
					asp,
					asi,
					asdt,
					sfData);		
			}
			catch (Exception e) {
				m_logger.error("GwtActivityStreamHelper.getActivityStreamData( EXCEPTION ):  ", e);
			}
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Create an ActivityStreamEntry from the given FolderEntry object.
	 * 
	 * @param request
	 * @param bs
	 * @param folderEntry
	 */
	public static ActivityStreamEntry getActivityStreamEntry(HttpServletRequest request, AllModulesInjected bs, FolderEntry folderEntry) {
		// Gather the information from the FolderEntry and return an
		// ActivityStreamEntry.
		ActivityStreamEntry asEntry = new ActivityStreamEntry();
		if (null != folderEntry) {
			// Initialize the author information.
			UserPrincipal	authorPrincipal = folderEntry.getCreation().getPrincipal();
			User			author          = ((authorPrincipal instanceof User) ? ((User) authorPrincipal) : GwtServerHelper.getResolvedUser(authorPrincipal.getId()));
			ASAuthorInfo	authorInfo      = ASAuthorInfo.buildAuthorInfo(
				request,
				bs,
				GwtServerHelper.isPresenceEnabled(),
				Utils.canUserOnlySeeCommonGroupMembers(),
				folderEntry.getOwnerId(),
				author,
				author.getTitle());

			asEntry.setAuthorPresence(   authorInfo.m_authorPresence );
			asEntry.setAuthorAvatarUrl(  authorInfo.m_authorAvatarUrl);
			asEntry.setAuthorId(         authorInfo.m_authorId       );
			asEntry.setAuthorName(       authorInfo.m_authorTitle    );
			asEntry.setAuthorWorkspaceId(authorInfo.m_authorWsId     );
			asEntry.setAuthorLogin(      author.getName()            );
			
			// Initialize parent binder information.
			Binder parentBinder = folderEntry.getParentBinder();
			if (null != parentBinder) {
				asEntry.setParentBinderId(String.valueOf(parentBinder.getId())     );
				asEntry.setParentBinderHover(            parentBinder.getPathName());
				asEntry.setParentBinderName(             parentBinder.getTitle()   );
			}
			
			// Initialize the entry information.
			Description	desc    = folderEntry.getDescription();
			String		entryId = String.valueOf(folderEntry.getId());
			asEntry.setEntryId(               entryId                           );
			asEntry.setEntryComments(         0                                 );
			asEntry.setEntryDescription(      desc.getText()                    );	
			asEntry.setEntryDescriptionFormat(desc.getFormat()                  );	
			asEntry.setEntryDocNum(           folderEntry.getDocNumber()        );
			asEntry.setEntryTitle(            folderEntry.getTitle()            );
			asEntry.setEntryType(             folderEntry.getEntityType().name());
			
			// Set the modification date.
			Date	date    = folderEntry.getLastActivity();
			String	dateStr = GwtServerHelper.getDateTimeString(date);
			asEntry.setEntryModificationDate(dateStr);
			
			// Set the top entry.
			FolderEntry topEntry = folderEntry.getParentEntry();
			while (null != topEntry.getParentEntry()) {
				topEntry = topEntry.getParentEntry();
			}
			
			if (null != topEntry) {
				asEntry.setEntryTopEntryId(String.valueOf(topEntry.getId()));
			}

			// Set whether this entry has been seen.
			SeenMap seenMap = bs.getProfileModule().getUserSeenMap(null);
			asEntry.setEntrySeen(seenMap.checkIfSeen(folderEntry));
		}
	
		return asEntry;
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
			// Get the user's properties.
			UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
			
			// Read the user's show setting for the what's new page.
			ActivityStreamDataType showSetting = GwtServerHelper.getWhatsNewShowSetting(userProperties);
			reply.setShowSetting(showSetting);

			// Read the user's entries per page setting and store it in
			// the ActivityStreamParams.
			String eppS = MiscUtil.entriesPerPage(userProperties);
			if (MiscUtil.hasString(eppS)) {
				int epp = Integer.parseInt(eppS);
				reply.setEntriesPerPage(epp);
			}
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
	 * Returns the current user's default activity stream.  If they
	 * don't have one set in their user profile, null is returned.
	 * 
	 * @param request
	 * @param bs
	 * @param currentBinderId
	 * @param overrideAS
	 * @param overrideASId
	 * 
	 * @return
	 */
	public static ActivityStreamInfo getDefaultActivityStream(HttpServletRequest request, AllModulesInjected bs, Long currentBinderId, ActivityStream overrideAS, Long overrideASId) {
		// By default, we'll return null if the user doesn't have an
		// activity stream that can be selected.
		ActivityStreamInfo reply = null;

		String asiProp;
		if (overrideAS.equals(ActivityStream.UNKNOWN)) {
			// Does the user have an activity stream information string stored
			// in their user profile?
			UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
			asiProp = ((String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_DEFAULT_ACTIVITY_STREAM));
		}
		else {
			ActivityStreamInfo overrideASI = new ActivityStreamInfo();
			overrideASI.setActivityStream(overrideAS);
			if (((-1L) == overrideASId) && (!(overrideAS.equals(ActivityStream.SITE_WIDE)))) {
				overrideASId = currentBinderId;
			}
			overrideASI.setBinderId(String.valueOf(overrideASId));
			asiProp = overrideASI.getStringValue();
		}
		if (MiscUtil.hasString(asiProp)) {
			// Yes!  Can we parse it as a valid activity stream?
			reply = ActivityStreamInfo.parse(asiProp);
			if (null != reply) {
				// Yes!  Does it refer to a known activity stream?
				ActivityStream as = reply.getActivityStream();
				if (!(ActivityStream.UNKNOWN.equals(as))) {
					// Yes!  Build a TreeInfo for the current binder.
					// (This will provide everything we need to match
					// what's stored in the user's profile with what's
					// currently available.)  Then search it for the
					// activity stream information object to return.
					TreeInfo ti = getVerticalActivityStreamsTree(request, bs, String.valueOf(currentBinderId));
					reply = findASIInTIList(reply, ti.getChildBindersList());
				}
			}
		}
		
		// If we get here, reply is null or refers to an activity
		// stream information object for the current user's default
		// activity stream.  Return it.
		return reply;
	}
	
	/**
	 * Return a list of comments for the given entry
	 * 
	 * @param bs
	 * @param request
	 * @param entryId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<ActivityStreamEntry> getEntryComments(AllModulesInjected bs, HttpServletRequest request, Long entryId) {
		// Can we find the single entry whose comments were requested?
		ASSearchResults           searchResults = performASSearch_One(bs, entryId);
		List<Map>                 searchEntries = searchResults.getSearchEntries();
		List<ActivityStreamEntry> reply         = null;
    	if ((null != searchEntries) && (1 == searchEntries.size())) {
    		// Yes!  Construct a base ActivityStreamData to build the
    		// rest of its activity stream information with...
			ActivityStreamParams asp = getActivityStreamParams(bs);
			asp.setEntriesPerPage(Integer.MAX_VALUE);
			asp.setActiveComments(Integer.MAX_VALUE);
			
			ActivityStreamData asd = new ActivityStreamData();
			asd.setDateTime(GwtServerHelper.getDateTimeString(new Date()));
			
			PagingData pd = asd.getPagingData();
			pd.initializePaging(asp);
			pd.setTotalRecords(searchResults.getTotalRecords(), searchResults.m_totalApproximate);

			// ...read the current user's seen map...
    		SeenMap sm = bs.getProfileModule().getUserSeenMap(null);
    		
    		// ...and populate the ActivityStreamData from the entry
    		// ...data we got from the search.
 			populateASDFromED(
				request,
				sm,
				asd,
				false,	// false -> Don't force plain text descriptions.
				ASEntryData.buildEntryDataList(
					request,
					bs,
					ActivityStreamDataType.ALL,
					true,	// true  -> Return comments - That's what this is about, right?
					false,	// false -> Don't force plain text descriptions.
					GwtServerHelper.isPresenceEnabled(),
					Utils.canUserOnlySeeCommonGroupMembers(),
					asp,
					sm,
					searchEntries));

 			// Did we resolve things down to the single entry we requested?
 			List<ActivityStreamEntry> entries = asd.getEntries();
 			if ((null != entries) && (1 == entries.size())) {
 				// Yes!  Return its comments.
 	 			reply = entries.get(0).getComments();
 			}
    	}
    	
    	// If we don't have a List<ActivityStreamEntry> of the entry's
    	// comments...
    	if (null == reply) {
    		// ...return an empty list.
    		new ArrayList<ActivityStreamEntry>();
    	}

    	// If we get here, reply refers to a List<ActivityStreamEntry>
    	// of the requested entry's comments.  Return it.
		return reply;
	}
	
	
	/*
	 * Extracts a description from an entry map and replaces any mark
	 * up with the appropriate URL.  For example, replaces:
	 *    {{atachmentUrl: somename.png}}
	 * with a URL that looks like:
	 *    http://somehost/ssf/s/readFile/.../somename.png
	 */
	@SuppressWarnings("unchecked")
	private static String getEntryDescFromEM(Map em, HttpServletRequest request) {
		// Do we have a base description?
		String reply = GwtServerHelper.getStringFromEntryMap(em, Constants.DESC_FIELD);
		if (MiscUtil.hasString(reply)) {
			// Yes!  Fix the mark up.
			reply = fixupDescription(request, em, reply);
		}
		
		// If we get here, reply refers to the description string.
		// Return it.
		return reply;
	}
	
	/*
	 * Extracts a description format from an entry map.  Defaults to
	 * Description.FORMAT_HTML if a value is not found.
	 */
	@SuppressWarnings("unchecked")
	private static int getEntryDescFormatFromEM(Map em) {
		int reply = Description.FORMAT_HTML;
		String descFmt = GwtServerHelper.getStringFromEntryMap(em, Constants.DESC_FORMAT_FIELD);
		if (MiscUtil.hasString(descFmt)) {
			try {
				reply = Integer.parseInt(descFmt);
			}
			catch (Exception ex) {}
		}
		return reply;
	}

	/*
	 * Returns the appropriate sort descending boolean for an activity
	 * stream search.
	 */
	private static boolean getSortDescend(SpecificFolderData sfData) {
		return ((null != sfData) ? sfData.isSortDescending() : true);
	}
	
	/*
	 * Returns the appropriate sort key for an activity stream search.
	 */
	private static String getSortKey(SpecificFolderData sfData) {
		String sortKey = ((null != sfData) ? sfData.getSortKey() : null);
		return (MiscUtil.hasString(sortKey) ? sortKey : Constants.LASTACTIVITY_FIELD);
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
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtActivityStreamHelper.getVerticalActivityStreamsTree()");
		try {
			ASTreeData		td = ASTreeData.buildTreeData(request, bs, binderIdS);
			Binder			binder;
			boolean			isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
			TreeInfo		reply                       = new TreeInfo();
			reply.setActivityStream(true);
			reply.setBinderTitle(NLT.get("asTreeWhatsNew"));
			List<TreeInfo>	rootASList = reply.getChildBindersList();
							
			// Can we access the Binder?
			binder = td.getBinder(td.getBaseBinderId());
			if (null != binder) {
				// Yes!  Build a TreeInfo for it.
				rootASList.add(
					buildASTI(
						request,
						bs,
						true,
						binderIdS,
						binder.getTitle(),
						binder.getPathName(),
						ActivityStream.CURRENT_BINDER));
			}
	
			// Add TreeInfo's for the various collection points.
			User currentUser = GwtServerHelper.getCurrentUser();
			if (!(currentUser.isShared() && Utils.checkIfFilr())) {
				rootASList.add(buildCollectionPointTI(    bs, request, td, CollectionType.MY_FILES)      );
				rootASList.add(buildCollectionPointTI(    bs, request, td, CollectionType.SHARED_WITH_ME));
				rootASList.add(buildCollectionPointTI(    bs, request, td, CollectionType.SHARED_BY_ME)  );
				if (LicenseChecker.showFilrFeatures()) {
					rootASList.add(buildCollectionPointTI(bs, request, td, CollectionType.NET_FOLDERS)   );
				}
			}
			if (AdminHelper.getEffectivePublicCollectionSetting(bs, currentUser)) {
				rootASList.add(buildCollectionPointTI(bs, request, td, CollectionType.SHARED_PUBLIC ));
			}
			
			// Are we in Filr only mode?
			if (!(Utils.checkIfFilr())) {
				// No!  Add a 'My Favorites' TreeInfo to the root
				// TreeInfo.
				TreeInfo asTI = buildMyFavoritesTI(bs, request, td);
				rootASList.add(asTI);

				// Add a 'My Teams' TreeInfo to the root TreeInfo.
				asTI = buildMyTeamsTI(bs, request, td);
				rootASList.add(asTI);

				// Add a 'Followed People' TreeInfo to the root TreeInfo.
				asTI = buildFollowedPeopleTI(bs, request, td, isOtherUserAccessRestricted);
				rootASList.add(asTI);
				
				// Add a 'Followed Places' TreeInfo to the root TreeInfo.
				asTI = buildFollowedPlacesTI(bs, request, td);
				rootASList.add(asTI);
			}
			
			// We always have a Site Wide.
			rootASList.add(
				buildASTI(
					request,
					bs,
					true,
					null,	// null -> No ID.
					NLT.get("asTreeSiteWide"),
					null,	// null -> No hover text.
					ActivityStream.SITE_WIDE));
	
			// Finally, ensure the child binder count in the TreeInfo
			// that we're returning is correct and return it.
			reply.updateChildBindersCount();
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Returns true if the activity stream information has enough
	 * information to be selected and false otherwise.
	 */
	private static boolean isASISelectable(ActivityStreamInfo asi) {
		boolean reply;
		
		// What type of activity stream is this?
		switch (asi.getActivityStream()) {
		default:
		case UNKNOWN:
			// We don't know:  It can't be selected.
			reply = false;
			break;
			
		case SITE_WIDE:
			// Site Wide:  This requires no binders.
			reply = true;
			break;
			
		case CURRENT_BINDER:
		case FOLLOWED_PEOPLE:
		case FOLLOWED_PERSON:
		case FOLLOWED_PLACES:
		case FOLLOWED_PLACE:
		case MY_FAVORITES:
		case MY_FAVORITE:
		case MY_FILES:
		case MY_FILE:
		case MY_TEAMS:
		case MY_TEAM:
		case NET_FOLDERS:
		case NET_FOLDER:
		case SHARED_BY_ME:
		case SHARED_BY_ME_FOLDER:
		case SHARED_WITH_ME:
		case SHARED_WITH_ME_FOLDER:
		case SHARED_PUBLIC:
		case SHARED_PUBLIC_FOLDER:
		case SPECIFIC_BINDER:
		case SPECIFIC_FOLDER:
			// These require 1 or more binders.
			String[] binderIds = asi.getBinderIds();
			int binders = ((null == binderIds) ? 0 : binderIds.length);
			reply = (0 < binders);
			break;
		}
		
		// If we get here, reply is true if we have enough information
		// to select the activity stream and false otherwise.  Return
		// it. 
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
		String asiDump = (isDebugLoggingEnabled() ? asi.getStringValue() : null);
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtActivityStreamHelper.hasActivityStreamChanged( " + asiDump + " )");
		try {
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
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Stores an ActivityStreamIn as the current user's default
	 * activity stream in their user profile.
	 * 
	 * @param ri
	 * @param asi
	 * 
	 * @return
	 */
	public static Boolean persistActivityStreamSelection(HttpServletRequest request, AllModulesInjected bs, ActivityStreamInfo asi) {
		// Store the string representation of the activity stream
		// in the user's properties...
		String asiProp = ((null == asi) ? "" : asi.getStringValue());
		bs.getProfileModule().setUserProperty(
			null,
			ObjectKeys.USER_PROPERTY_DEFAULT_ACTIVITY_STREAM,
			asiProp);

		// ...and return true.
		return Boolean.TRUE;
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
	 * Returns an ASSearchResults object containing the search results
	 * from performing an activity stream search for all entries in the
	 * tracked places and users lists. 
	 */
	@SuppressWarnings("unchecked")
	private static ASSearchResults performASSearch_All(AllModulesInjected bs, List<String> trackedPlacesAL, List<String> trackedEntriesAL, List<String> trackedUsersAL, int pageStart, int entriesPerPage) {
		// Build the base search criteria for the activity stream.
		Criteria searchCriteria = buildSearchCriteria(bs, trackedPlacesAL, trackedEntriesAL, trackedUsersAL);
		
		// Perform the search...
		Map searchResults = bs.getBinderModule().executeSearchQuery(
			searchCriteria,
			Constants.SEARCH_MODE_NORMAL,
			pageStart,
			entriesPerPage,
			null);
		
		// ...and return an appropriate ASSearchResults.
		List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
		int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		return new ASSearchResults(searchEntries, totalRecords, searchResultsTotalApproximate(searchResults));
	}
	
	/*
	 * Returns an ASSearchResults object containing the search results
	 * from performing an activity stream search for all entries in the
	 * tracked places and users lists. 
	 */
	@SuppressWarnings("unchecked")
	private static ASSearchResults performASSearch_All(AllModulesInjected bs, Long sfId, int pageStart, int entriesPerPage, SpecificFolderData sfData) {
		// Create the search options map with the paging setup.
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,   pageStart     );
		options.put(ObjectKeys.SEARCH_MAX_HITS, entriesPerPage);
		
		// Add the search query to the map.
		Document searchQuery = buildSearchQuery(bs, sfId, sfData);
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchQuery);

		// Add the sort options to the map.
		options.put(ObjectKeys.SEARCH_SORT_BY,      getSortKey(    sfData));
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, getSortDescend(sfData));

		// Factor in any quick filter.
		GwtServerHelper.addQuickFilterToSearch(options, sfData.getQuickFilter());
		
		// Perform the search...
		Map searchResults = bs.getFolderModule().getEntries(sfId, options);

		// ...and return an appropriate ASSearchResults.
		List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
		int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		return new ASSearchResults(searchEntries, totalRecords, searchResultsTotalApproximate(searchResults));
	}
	
	/*
	 * Returns an ASSearchResults object containing the search results
	 * from performing an activity stream search for a single entry. 
	 */
	@SuppressWarnings("unchecked")
	private static ASSearchResults performASSearch_One(AllModulesInjected bs, Long entryId) {
		// Perform the search...
		Criteria searchCriteria = buildSearchCriteria(bs, entryId);
		Map searchResults = bs.getBinderModule().executeSearchQuery(
			searchCriteria,
			Constants.SEARCH_MODE_NORMAL,
			0,
			Integer.MAX_VALUE,
			null);

		// ...and return an appropriate ASSearchResults.
		List<Map> searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES    ));
		int       totalRecords  = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		return new ASSearchResults(searchEntries, totalRecords, searchResultsTotalApproximate(searchResults));
	}
	
	/*
	 * Returns an ASSearchResults object containing the search results
	 * from performing an activity stream search for the read or unread
	 * entries in the tracked places and users lists. 
	 */
	@SuppressWarnings("unchecked")
	private static ASSearchResults performASSearch_ReadUnread(AllModulesInjected bs, List<String> trackedPlacesAL, List<String> trackedEntriesAL, List<String> trackedUsersAL, int pageStart, int entriesPerPage, boolean read, ActivityStreamParams asp) {
		// Build the base search criteria for the activity string.
		Criteria searchCriteria = buildSearchCriteria(bs, trackedPlacesAL, trackedEntriesAL, trackedUsersAL);
		
	    // Return up to the maximum number entries that have had
		// activity within last n days.
		Date activityDate = new Date();
		activityDate.setTime(activityDate.getTime() - (((long) asp.getReadEntryDays()) * 24L * 60L * 60L * 1000L));
		String startDate = DateTools.dateToString(activityDate, DateTools.Resolution.SECOND);
		String now       = DateTools.dateToString(new Date(),   DateTools.Resolution.SECOND);
		searchCriteria.add(
			Restrictions.between(
				Constants.LASTACTIVITY_FIELD,
				startDate,
				now));
		
		Map searchResults = bs.getBinderModule().executeSearchQuery(
			searchCriteria,
			Constants.SEARCH_MODE_NORMAL,
			pageStart,
			asp.getReadEntryMax(),
			null);

		// Get the user's seen map...
		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
		
		// ...and scan the entries we read.
		List<Map> targetEntries    = new ArrayList<Map>();
		List<Map> searchEntries    = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
		int       totalRecords     = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		boolean   totalApproximate = searchResultsTotalApproximate(searchResults);
		boolean   readSatisfied = false;
		for (Map searchEntry: searchEntries) {
			// If the user has seen this entry and we're looking for
			// read entries or the user has not seen it and we're
			// looking for unread entries...
			boolean hasSeen = seen.checkIfSeen(searchEntry);
			if (hasSeen == read) {
				// ...and we haven't satisfied the number of records
				// ...requested...
				if (!readSatisfied) {
					// ...keep track of it until we've got all the entries
					// ...we need.
					targetEntries.add(searchEntry);
					readSatisfied = (targetEntries.size() >= (pageStart + entriesPerPage));
				}
			}
			
			else {
				// Otherwise, this is read when were looking for unread
				// or unread while we're looking for read!  In either
				// case, we don't want to include it in the total
				// record count.
				totalRecords    -= 1;
				totalApproximate = true;
			}
		}

		// Ensure that we've only got the entries we need from the
		// list...
		if (targetEntries.size() > pageStart && targetEntries.size() >= pageStart + entriesPerPage) {
			targetEntries = targetEntries.subList(pageStart, pageStart + entriesPerPage);
		}		
		else if (targetEntries.size() > pageStart) {
			targetEntries = targetEntries.subList(pageStart, targetEntries.size());
		}
				
		// ...and return an appropriate ASSearchResults.
		return new ASSearchResults(targetEntries, totalRecords, totalApproximate);
	}
	
	/*
	 * Returns an ASSearchResults object containing the search results
	 * from performing an activity stream search for the read or unread
	 * entries in the tracked places and users lists. 
	 */
	@SuppressWarnings("unchecked")
	private static ASSearchResults performASSearch_ReadUnread(AllModulesInjected bs, Long sfId, int pageStart, int entriesPerPage, boolean read, ActivityStreamParams asp, SpecificFolderData sfData) {
		// Create the search options map with the paging setup.
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,   pageStart            );
		options.put(ObjectKeys.SEARCH_MAX_HITS, asp.getReadEntryMax());
		
	    // Return up to the maximum number entries that have had
		// activity within last n days.
		Date activityDate = new Date();
		activityDate.setTime(activityDate.getTime() - (((long) asp.getReadEntryDays()) * 24L * 60L * 60L * 1000L));
		DateTimeFormatter	fmt       = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.forTimeZone(GwtServerHelper.getCurrentUser().getTimeZone()));
		String				startDate = fmt.print(new DateTime(activityDate));
		String				now       = fmt.print(new DateTime(new Date()  ));

		// Add the search query to the map.
		Document searchQuery = buildSearchQuery(bs, sfId, sfData, startDate, now);
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchQuery);
		
		// Add the sort options to the map.
		options.put(ObjectKeys.SEARCH_SORT_BY,      getSortKey(    sfData));
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, getSortDescend(sfData));
		
		// Perform the search...
		Map searchResults = bs.getFolderModule().getEntries(sfId, options);

		// ...get the user's seen map...
		SeenMap seen = bs.getProfileModule().getUserSeenMap(null);
		
		// ...and scan the entries we read.
		List<Map> targetEntries    = new ArrayList<Map>();
		List<Map> searchEntries    = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
		int       totalRecords     = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		boolean   totalApproximate = searchResultsTotalApproximate(searchResults);
		boolean   readSatisfied    = false;
		for (Map searchEntry: searchEntries) {
			// If the user has seen this entry and we're looking for
			// read entries or the user has not seen it and we're
			// looking for unread entries...
			boolean hasSeen = seen.checkIfSeen(searchEntry);
			if (hasSeen == read) {
				// ...and we haven't satisfied the number of records
				// ...requested...
				if (!readSatisfied) {
					// ...keep track of it until we've got all the entries
					// ...we need.
					targetEntries.add(searchEntry);
					readSatisfied = (targetEntries.size() >= (pageStart + entriesPerPage));
				}
			}
			
			else {
				// Otherwise, this is read when were looking for unread
				// or unread while we're looking for read!  In either
				// case, we don't want to include it in the total
				// record count.
				totalRecords    -= 1;
				totalApproximate = true;
			}
		}

		// Ensure that we've only got the entries we need from the
		// list...
		if (targetEntries.size() > pageStart && targetEntries.size() >= pageStart + entriesPerPage) {
			targetEntries = targetEntries.subList(pageStart, pageStart + entriesPerPage);
		}		
		else if (targetEntries.size() > pageStart) {
			targetEntries = targetEntries.subList(pageStart, targetEntries.size());
		}
				
		// ...and return an appropriate ASSearchResults.
		return new ASSearchResults(targetEntries, totalRecords, totalApproximate);
	}
	
	/*
	 * Reads the activity stream data based on an activity stream
	 * information object and the current paging data.
	 */
	@SuppressWarnings("unchecked")
	private static void populateASD(HttpServletRequest request, AllModulesInjected bs, SeenMap sm, boolean isPresenceEnabled, boolean isOtherUserAccessRestricted, ActivityStreamData asd, ActivityStreamParams asp, ActivityStreamInfo asi, ActivityStreamDataType asdt, SpecificFolderData sfData) {		
		// Setup some int's for the controlling the search.
		PagingData pd				= asd.getPagingData();
		int        entriesPerPage	= pd.getEntriesPerPage();
		int        pageIndex		= pd.getPageIndex();
		int        pageStart		= (pageIndex * entriesPerPage);

		// Initialize lists for the tracked places, entries and users.
		List<String> trackedPlacesAL  = new ArrayList<String>();
		List<String> trackedEntriesAL = new ArrayList<String>();
		List<String> trackedUsersAL   = new ArrayList<String>();

		// Initialize the variables for processing a specific folder.
		boolean	asIsSpecificFolder = false;
		Long	specificFolderId   = null;
		
		// What type of activity stream are we reading the data for?
		String[] trackedPlaces = asi.getBinderIds();
		switch (asi.getActivityStream()) {
		case FOLLOWED_PEOPLE:
		case FOLLOWED_PERSON:
			// Followed people:
			// 1. There are no tracked places; and
			// 2. The tracked users are the owner IDs of the places.
			// 3. There are no tracked entries.
			ListUtil.arrayStringToListString(trackedPlaces, trackedUsersAL);
			trackedPlaces = new String[0];
			
			break;
			
		case SPECIFIC_FOLDER:
			asIsSpecificFolder = true;
			
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * //
			// Fall through and handle with the other 'place' types. //
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * //
			
		case CURRENT_BINDER:
		case FOLLOWED_PLACES:
		case FOLLOWED_PLACE:
		case MY_FAVORITES:
		case MY_FAVORITE:
		case MY_FILE:
		case MY_TEAMS:
		case MY_TEAM:
		case NET_FOLDER:
		case SHARED_BY_ME_FOLDER:
		case SHARED_WITH_ME_FOLDER:
		case SHARED_PUBLIC_FOLDER:
		case SPECIFIC_BINDER:
			// A place of some sort:
			// 1. The tracked places is used unchanged; and
			// 2. There are no tracked users.
			// 3. There are no tracked entries.
			break;

		case MY_FILES:
		case NET_FOLDERS:
		case SHARED_BY_ME:
		case SHARED_WITH_ME:
		case SHARED_PUBLIC:
			// A collection point:
			// 1. The tracked places are the IDs of the folders from
			//    the collection point.
			// 2. There are no tracked users.
			// 3. The tracked entries are the IDs of the entries from
			//    the collection point.
			CollectionType collectionType;
			switch (asi.getActivityStream()) {
			default:
			case MY_FILES:        collectionType = CollectionType.MY_FILES;       break;
			case NET_FOLDERS:     collectionType = CollectionType.NET_FOLDERS;    break;
			case SHARED_BY_ME:    collectionType = CollectionType.SHARED_BY_ME;   break;
			case SHARED_WITH_ME:  collectionType = CollectionType.SHARED_WITH_ME; break;
			case SHARED_PUBLIC:   collectionType = CollectionType.SHARED_PUBLIC;  break;
			}
			fillCollectionLists(bs, collectionType, trackedPlacesAL, trackedEntriesAL);
			break;
			
		default:
		case SITE_WIDE:
			// The entire site:
			// 1. The tracked places is the ID of the top workspace; and
			// 2. There are no tracked users.
			// 3. There are no tracked entries.
			trackedPlaces = new String[]{GwtUIHelper.getTopWSIdSafely(bs)};
			break;
		}

		// Store the tracked places into the ArrayList for them.
		ListUtil.arrayStringToListString(trackedPlaces, trackedPlacesAL);

		// Is this an activity stream request for a specific folder?
		if (asIsSpecificFolder) {
			// Yes!  There shouldn't be any users and a single place,
			// that being the specific folder.  Is that the case?
			int users  = ((null == trackedUsersAL)  ? 0 : trackedUsersAL.size() );
			int places = ((null == trackedPlacesAL) ? 0 : trackedPlacesAL.size());
			if ((0 != users) || (1 != places)) {
				// No!  Log a warning that something is bogus...
				m_logger.warn("GwtActivityStreamDataHelper.populateASD( *Internal Error* ):  SPECIFIC_FOLDER request being converted to SPECIFIC_BINDER.  Users: " + users + " (should be 0), Places: " + places + " (should be 1).");
				
				// ...and treat things as though a specific binder was
				// ...requested.
				asIsSpecificFolder = false;
				asi.setActivityStream(ActivityStream.SPECIFIC_BINDER);
				sfData = null;
			}
			
			else {
				// Yes, there's no users and only a single place!
				// Convert that place ID to a Long...
				specificFolderId = Long.parseLong(trackedPlacesAL.get(0));
				
				// ...and if we weren't give a SpecificFolderData
				// ...object...
				if (null == sfData) {
					// ...create one with the appropriate defaults.
					sfData = new SpecificFolderData();
				}
			}
		}
		
		// Are we processing an activity stream for other than a
		// specific binder and given a SpecificFolderData object?
		if ((!asIsSpecificFolder) && (null != sfData)) {
			// Yes!  Log a warning and forget about the
			// SpecificFolderData.
			m_logger.warn("GwtActivityStreamHelper.populateASD( *Internal Error* ):  A SpecificFolderData was supplied for a non SPECIFIC_FOLDER activity stream request.  The object is being ignored.");
			sfData = null;
		}

		// Perform the search and extract the results.
		ASSearchResults	searchResults;
		boolean			forcePlainTextDescriptions;
		boolean			returnComments;
		
		if (asIsSpecificFolder) {
			forcePlainTextDescriptions = sfData.isForcePlainTextDescriptions();
			returnComments             = sfData.isReturnComments();
			
			switch (asdt) {
			default:
			case ALL:     searchResults = performASSearch_All(       bs, specificFolderId, pageStart, entriesPerPage,             sfData); break;
			case READ:    searchResults = performASSearch_ReadUnread(bs, specificFolderId, pageStart, entriesPerPage, true,  asp, sfData); break;
			case UNREAD:  searchResults = performASSearch_ReadUnread(bs, specificFolderId, pageStart, entriesPerPage, false, asp, sfData); break;
			}
		}
		
		else {
			forcePlainTextDescriptions = false;
			returnComments             = true;

			// Do we have anything to populate the activity stream
			// from?
			int tracks  = ((null == trackedPlacesAL)  ? 0 : trackedPlacesAL.size() );
			    tracks += ((null == trackedEntriesAL) ? 0 : trackedEntriesAL.size());
			    tracks += ((null == trackedUsersAL)   ? 0 : trackedUsersAL.size()  );
			    
			if (0 == tracks) {
				// No!  Generate an empty ASSearchResults.
				searchResults = new ASSearchResults(new ArrayList<Map>(), 0, false);
			}
			
			else {
				// Yes, we have something to populate the activity
				// stream from!  Perform the search.
				switch (asdt) {
				default:
				case ALL:     searchResults = performASSearch_All(       bs, trackedPlacesAL, trackedEntriesAL, trackedUsersAL, pageStart, entriesPerPage            ); break;
				case READ:    searchResults = performASSearch_ReadUnread(bs, trackedPlacesAL, trackedEntriesAL, trackedUsersAL, pageStart, entriesPerPage, true,  asp); break;
				case UNREAD:  searchResults = performASSearch_ReadUnread(bs, trackedPlacesAL, trackedEntriesAL, trackedUsersAL, pageStart, entriesPerPage, false, asp); break;
				}
			}
		}
		
		// Update the paging data in the activity stream data.
		List<Map>	searchEntries = searchResults.getSearchEntries();
		int			totalRecords  = searchResults.getTotalRecords();
    	pd.setTotalRecords(totalRecords, searchResults.isTotalApproximate());
    	asd.setPagingData(pd);

    	// Are there any entries in the search results?
    	if ((null != searchEntries) && (!(searchEntries.isEmpty()))) {
    		// Yes!  Use a List<ASEntryData> built from the entry
    		// search results to populate the ActivityStreamData
    		// object.
			populateASDFromED(
				request,
				sm,
				asd,
				forcePlainTextDescriptions,
				ASEntryData.buildEntryDataList(
					request,
					bs,
					asdt,
					returnComments,
					forcePlainTextDescriptions,
					isPresenceEnabled,
					isOtherUserAccessRestricted,
					asp,
					sm,
					searchEntries));
    	}
    				

    	// Store the places and users we're tracking in the session
    	// cache....
    	ActivityStreamCache.setTrackedBinderIds(request, ListUtil.listStringToListLong(trackedPlacesAL));
    	ActivityStreamCache.setTrackedUserIds(  request, ListUtil.listStringToListLong(trackedUsersAL ));
    	
    	// ...and store the date we saved the tracking data.
    	ActivityStreamCache.setUpdateDate(request);
	}

	/*
	 * Populates an ActivityStreamData object from the information
	 * contained in a List<ASEntryData>.
	 */
	private static void populateASDFromED(HttpServletRequest request, SeenMap sm, ActivityStreamData asd, boolean forcePlainTextDescriptions, List<ASEntryData> entryDataList) {
		// Scan the List<ASEntryData>...
    	List<ActivityStreamEntry> aseList = asd.getEntries();
    	for (ASEntryData entryData:  entryDataList) {
    		// ...and add an ActivtyStreamEntry for each to the
			// ...List<ActivityStreamEntry> in the ActivityStreamData.
   			aseList.add(
   				buildASEFromED(
   					request,
   					sm,
   					entryData,
   					forcePlainTextDescriptions));
    	}
	}

	/*
	 * Returns true if the total count in a search results is an
	 * approximate value and false otherwise.
	 */
	@SuppressWarnings("unchecked")
	private static boolean searchResultsTotalApproximate(Map searchResults) {
		Boolean totalApproximate = ((Boolean) searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL_APPROXIMATE));
		return ((null == totalApproximate) ? false : totalApproximate.booleanValue());
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
