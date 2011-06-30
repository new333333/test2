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
package org.kablink.teaming.gwt.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.ShowSetting;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagSortOrder;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.whatsnew.ActionValidation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * This interface defines the methods that can be called when we want to make a remote
 * procedure call.
 * 
 * @author jwootton
 */
@RemoteServiceRelativePath("gwtTeaming.rpc")
public interface GwtRpcService extends RemoteService
{
	// Execute the given command.
	public VibeRpcResponse executeCommand( HttpRequestInfo ri, VibeRpcCmd cmd ) throws GwtTeamingException;
	
	
	
	// Return the "document base url" that is used in tinyMCE configuration
	public String getDocumentBaseUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;
	
	// Return an Entry object for the given entry id.
	public GwtFolderEntry getEntry( HttpRequestInfo ri, String zoneUUID, String entryId ) throws GwtTeamingException;
	
	// Return a permalink that can be used to view the given entry.
	public String getEntryPermalink( HttpRequestInfo ri, String entryId, String zoneUUID );
	
	// Return a base view folder entry URL that can be used directly in
	// the content panel.
	public String getViewFolderEntryUrl( HttpRequestInfo ri, Long binderId, Long entryId ) throws GwtTeamingException;
	
	// Return a list of the names of the files that are attachments of the given binder.
	public ArrayList<String> getFileAttachments( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;
	
	// Return a Folder object for the given folder id.
	public GwtFolder getFolder( HttpRequestInfo ri, String zoneUUID, String folderId ) throws GwtTeamingException;
	
	// Returns various binder URLs.
	public String getBinderPermalink( HttpRequestInfo ri, String binderId );
	public String getModifyBinderUrl( HttpRequestInfo ri, String binderId );
	
	// The following deal with personal preferences.
	public GwtPersonalPreferences getPersonalPreferences( HttpRequestInfo ri );
	public Boolean savePersonalPreferences( HttpRequestInfo ri, GwtPersonalPreferences personalPrefs ) throws GwtTeamingException;
	
	// Return a GwtBrandingData object for the global workspace.
	public GwtBrandingData getSiteBrandingData( HttpRequestInfo ri ) throws GwtTeamingException;
	
	public String getTutorialPanelState( HttpRequestInfo ri );
	public ExtensionInfoClient[] getExtensionInfo( HttpRequestInfo ri );
	public ExtensionInfoClient[] removeExtension( HttpRequestInfo ri, String id ) throws ExtensionDefinitionInUseException;
	public ExtensionFiles getExtensionFiles( HttpRequestInfo ri, String id, String zoneName );
	
	// Returns a permalink for the given userId
	public String getUserPermalink( HttpRequestInfo ri, String userId );
	// Returns a permalink to the currently logged in user's workspace.
	public String getUserWorkspacePermalink( HttpRequestInfo ri );
	
	// The following are used in the implementation of the various
	// forms of the WorkspaceTreeControl.
	public TreeInfo       expandHorizontalBucket(         HttpRequestInfo ri, BucketInfo bucketInfo );
	public TreeInfo       expandVerticalBucket(           HttpRequestInfo ri, BucketInfo bucketInfo );
	public List<TreeInfo> getHorizontalTree(              HttpRequestInfo ri, String     binderId   );
	public TreeInfo       getHorizontalNode(              HttpRequestInfo ri, String     binderId   );
	public String         getRootWorkspaceId(             HttpRequestInfo ri, String     binderId   );
	public TreeInfo       getVerticalActivityStreamsTree( HttpRequestInfo ri, String     binderId   );
	public TreeInfo       getVerticalTree(                HttpRequestInfo ri, String     binderId   );
	public TreeInfo       getVerticalNode(                HttpRequestInfo ri, String     binderId   );
	public Boolean        persistNodeCollapse(            HttpRequestInfo ri, String     binderId   );
	public Boolean        persistNodeExpand(              HttpRequestInfo ri, String     binderId   );
	
	// The following are used in the implementation of the
	// MainMenuControl.
	public Boolean               addFavorite(                  HttpRequestInfo ri, String binderId                                   ) throws GwtTeamingException;
	public Boolean               removeFavorite(               HttpRequestInfo ri, String favoriteId                                 );
	public Boolean               updateFavorites(              HttpRequestInfo ri,                  List<FavoriteInfo> favoritesList );
	public BinderInfo            getBinderInfo(                HttpRequestInfo ri, String binderId                                   );
	public String                getDefaultFolderDefinitionId( HttpRequestInfo ri, String binderId                                   );
	public List<FavoriteInfo>    getFavorites(                 HttpRequestInfo ri                                                    );
	public List<TeamInfo>        getMyTeams(                   HttpRequestInfo ri                                                    );
	public List<GroupInfo>       getMyGroups(                  HttpRequestInfo ri                                                    );
	public List<RecentPlaceInfo> getRecentPlaces(              HttpRequestInfo ri                                                    );
	public List<SavedSearchInfo> getSavedSearches(             HttpRequestInfo ri                                                    );
	public TeamManagementInfo    getTeamManagementInfo(        HttpRequestInfo ri, String binderId                                   );
	public List<ToolbarItem>     getToolbarItems(              HttpRequestInfo ri, String binderId                                   );
	public List<TopRankedInfo>   getTopRanked(                 HttpRequestInfo ri                                                    );
	public Boolean               removeSavedSearch(            HttpRequestInfo ri,                     SavedSearchInfo ssi           );
	public SavedSearchInfo       saveSearch(                   HttpRequestInfo ri, String searchTabId, SavedSearchInfo ssi           );

	// The following methods are used to manage tags on a binder/entry.
	public Boolean canManagePublicBinderTags( HttpRequestInfo ri, String binderId );
	public Boolean canManagePublicEntryTags( HttpRequestInfo ri, String entryId );
	public ArrayList<TagInfo> getEntryTags( HttpRequestInfo ri, String entryId );
	public ArrayList<TagInfo> getBinderTags( HttpRequestInfo ri, String binderId );
	public ArrayList<Boolean> getTagRightsForBinder( HttpRequestInfo ri, String binderId );
	public ArrayList<Boolean> getTagRightsForEntry( HttpRequestInfo ri, String entryId );
	public TagSortOrder getTagSortOrder( HttpRequestInfo ri );
	public Boolean saveTagSortOrder( HttpRequestInfo ri, TagSortOrder sortOrder );
	public Boolean updateEntryTags( HttpRequestInfo ri, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded );
	public Boolean updateBinderTags( HttpRequestInfo ri, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded );

	// The following are used to manage the tracking of information.
	public List<String> getTrackedPeople( HttpRequestInfo ri                  );
	public List<String> getTrackedPlaces( HttpRequestInfo ri                  );
	public Boolean      isPersonTracked(  HttpRequestInfo ri, String binderId );
	public Boolean      trackBinder(      HttpRequestInfo ri, String binderId );
	public Boolean      untrackBinder(    HttpRequestInfo ri, String binderId );
	public Boolean      untrackPerson(    HttpRequestInfo ri, String binderId );
	
	// Save the branding data to the given binder.
	public Boolean saveBrandingData( HttpRequestInfo ri, String binderId, GwtBrandingData brandingData ) throws GwtTeamingException;
	
	// The following are used in the implementation of the
	// User Profiles
	public ProfileInfo 		getProfileInfo(    HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public ProfileStats     getProfileStats(   HttpRequestInfo ri, String binderId, String userId) throws GwtTeamingException;
	public ProfileAttribute getProfileAvatars( HttpRequestInfo ri, String binderId );
	public ProfileInfo 		getQuickViewInfo(  HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public List<TeamInfo> 	getTeams(          HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public List<GroupInfo> 	getGroups(         HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public String 			getMicrBlogUrl(    HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public Boolean 			isPresenceEnabled( HttpRequestInfo ri);
	public String 			getImUrl(          HttpRequestInfo ri, String binderId )               throws GwtTeamingException;
	public GwtPresenceInfo  getPresenceInfo(   HttpRequestInfo ri, String binderId )               throws GwtTeamingException;

	// Return the URL for the start/schedule meeting page
	public String getAddMeetingUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;

	// The following are used in the implementation of the
	// UserStatusControl.
	public Boolean saveUserStatus(HttpRequestInfo ri, String status) throws GwtTeamingException;
	public UserStatus getUserStatus(HttpRequestInfo ri, String binderId) throws GwtTeamingException; 
	
	// Return login information such as self registration and auto complete.
	public GwtLoginInfo getLoginInfo( HttpRequestInfo ri ) throws GwtTeamingException;
	
	// Return the url needed to invoke the "site administration" page.
	public String getSiteAdministrationUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;
	
	// Get DiskUsageInfo.
	public  DiskUsageInfo getDiskUsageInfo( HttpRequestInfo ri, String binderId ) throws GwtTeamingException;

	// Activity Stream servicing APIs.
	public ActivityStreamData   getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd                              );
	public ActivityStreamData   getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd, ActivityStreamDataType asdt );
	public ActivityStreamData   getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi                                             );
	public ActivityStreamData   getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi,                ActivityStreamDataType asdt );
	public ActivityStreamParams getActivityStreamParams(        HttpRequestInfo ri                                                                                               );
	public ActivityStreamInfo   getDefaultActivityStream(       HttpRequestInfo ri, String currentBinderId                                                                       );
	public Boolean              hasActivityStreamChanged(       HttpRequestInfo ri,                           ActivityStreamInfo asi                                             );
	public Boolean              persistActivityStreamSelection( HttpRequestInfo ri, ActivityStreamInfo asi                                                                       );
	public Boolean saveWhatsNewShowSetting( HttpRequestInfo ri, ShowSetting showSetting );

	// Validate the given TeamingActions for the given entry id.
	public ArrayList<ActionValidation> validateEntryActions( HttpRequestInfo ri, ArrayList<ActionValidation> actionValidations, String entryId );

	// Get subscription information for the given entry id.
	public SubscriptionData getSubscriptionData( HttpRequestInfo ri, String entryId );
	
	// Save the subscription information for the given entry id.
	public Boolean saveSubscriptionData( HttpRequestInfo ri, String entryId, SubscriptionData subscriptionData ) throws GwtTeamingException;

	// Task servicing APIs.
	public Boolean              collapseSubtasks(      HttpRequestInfo ri, Long binderId, Long         entryId                           ) throws GwtTeamingException;
	public Boolean              deleteTask(            HttpRequestInfo ri, Long binderId, Long         entryId                           ) throws GwtTeamingException;
	public Boolean              deleteTasks(           HttpRequestInfo ri,                List<TaskId> taskIds                           ) throws GwtTeamingException;
	public Boolean              expandSubtasks(        HttpRequestInfo ri, Long binderId, Long         entryId                           ) throws GwtTeamingException;
	public List<AssignmentInfo> getGroupMembership(    HttpRequestInfo ri,                Long         groupId                           ) throws GwtTeamingException;
	public List<TaskListItem>   getTaskList(           HttpRequestInfo ri, Long binderId, String       filterType, String modeType       ) throws GwtTeamingException;
	public TaskBundle           getTaskBundle(         HttpRequestInfo ri, Long binderId, String       filterType, String modeType       ) throws GwtTeamingException;
	public TaskLinkage          getTaskLinkage(        HttpRequestInfo ri, Long binderId                                                 ) throws GwtTeamingException;
	public List<AssignmentInfo> getTeamMembership(     HttpRequestInfo ri, Long binderId                                                 ) throws GwtTeamingException;
	public Boolean              purgeTask(             HttpRequestInfo ri, Long binderId, Long         entryId                           ) throws GwtTeamingException;
	public Boolean              purgeTasks(            HttpRequestInfo ri,                List<TaskId> taskIds                           ) throws GwtTeamingException;
	public Boolean              removeTaskLinkage(     HttpRequestInfo ri, Long binderId                                                 ) throws GwtTeamingException;
	public String               saveTaskCompleted(     HttpRequestInfo ri, Long binderId, Long         entryId,    String  completed     ) throws GwtTeamingException;
	public String               saveTaskCompleted(     HttpRequestInfo ri,                List<TaskId> taskIds,    String  completed     ) throws GwtTeamingException;
	public Boolean              saveTaskLinkage(       HttpRequestInfo ri, Long binderId, TaskLinkage  taskLinkage                       ) throws GwtTeamingException;
	public Boolean              saveTaskPriority(      HttpRequestInfo ri, Long binderId, Long         entryId,    String  priority      ) throws GwtTeamingException;
	public Boolean              saveTaskSort(          HttpRequestInfo ri, Long binderId, String       sortKey,    boolean sortAscending ) throws GwtTeamingException;
	public String               saveTaskStatus(        HttpRequestInfo ri, Long binderId, Long         entryId,    String  status        ) throws GwtTeamingException;
	public String               saveTaskStatus(        HttpRequestInfo ri,                List<TaskId> taskIds,    String  status        ) throws GwtTeamingException;
	public Map<Long, TaskDate>  updateCalculatedDates( HttpRequestInfo ri, Long binderId, Long         entryId                           ) throws GwtTeamingException;
	
	// SeenMap servicing APIs.
	public Boolean isSeen(    HttpRequestInfo ri, Long       entryId  ) throws GwtTeamingException;
	public Boolean setSeen(   HttpRequestInfo ri, Long       entryId  ) throws GwtTeamingException;
	public Boolean setSeen(   HttpRequestInfo ri, List<Long> entryIds ) throws GwtTeamingException;
	public Boolean setUnseen( HttpRequestInfo ri, Long       entryId  ) throws GwtTeamingException;
	public Boolean setUnseen( HttpRequestInfo ri, List<Long> entryIds ) throws GwtTeamingException;
	
	// Add a reply to the given entry
	public ActivityStreamEntry replyToEntry( HttpRequestInfo ri, String entryId, String title, String desc ) throws GwtTeamingException;
	
	// Send an email notification to the given recipients for the given entry.
	public GwtShareEntryResults shareEntry( HttpRequestInfo ri, String entryId, String comment, ArrayList<String> principalIds, ArrayList<String> teamIds ) throws GwtTeamingException;
	
	// Get the membership of the given group.
	public ArrayList<GwtTeamingItem> getGroupMembership( HttpRequestInfo ri, String groupId ) throws GwtTeamingException;
	
	// See if this group is the "all users" group
	public Boolean isAllUsersGroup( HttpRequestInfo ri, String groupId ) throws GwtTeamingException;

	// Take the given html and replace any Vibe markup with the appropriate html.
	// For example, replace {{attachmentUrl: someimagename}} with the url to the image.
	public String markupStringReplacement( HttpRequestInfo ri, String binderId, String html, String type ) throws GwtTeamingException;
	
}// end GwtRpcService
