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
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.TagSortOrder;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.ShowSetting;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.whatsnew.ActionValidation;
import org.kablink.teaming.gwt.client.workspacetree.BucketInfo;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author jwootton
 */
public interface GwtRpcServiceAsync
{
	// Do a search given the criteria found in the GwtSearchCriteria object.
	public void executeSearch( HttpRequestInfo ri, GwtSearchCriteria searchCriteria, AsyncCallback<GwtSearchResults> callback );
	
	// Return the administration actions the user has rights to run.
	public void getAdminActions( HttpRequestInfo ri, String binderId, AsyncCallback<ArrayList<GwtAdminCategory>> callback );
	
	// Return a GwtBrandingData object for the given binder.
	public void getBinderBrandingData( HttpRequestInfo ri, String binderId, AsyncCallback<GwtBrandingData> callback );
	
	// Return the "document base url" that is used in tinyMCE configuration
	public void getDocumentBaseUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );
	
	// Return an Entry object for the given entry id.
	public void getEntry( HttpRequestInfo ri, String zoneUUID, String entryId, AsyncCallback<GwtFolderEntry> callback );

	// Return a permalink that can be used to view the given entry.
	public void getEntryPermalink( HttpRequestInfo ri, String entryId, String zoneUUID, AsyncCallback<String> callback );
	
	// Return a base view folder entry URL that can be used directly in
	// the content panel.
	public void getViewFolderEntryUrl( HttpRequestInfo ri, Long binderId, Long entryId, AsyncCallback<String> callback );
	
	// Return a list of the names of the files that are attachments of the given binder.
	public void getFileAttachments( HttpRequestInfo ri, String binderId, AsyncCallback<ArrayList<String>> callback );

	// Return a Folder object for the given folder id.
	public void getFolder( HttpRequestInfo ri, String zoneUUID, String folderId, AsyncCallback<GwtFolder> callback );
	
	// Returns various binder URLs.
	public void getBinderPermalink( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );
	public void getModifyBinderUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );
	
    public void getTutorialPanelState( HttpRequestInfo ri, AsyncCallback<String> callback );

	// The following deal with personal preferences.
	public void getPersonalPreferences( HttpRequestInfo ri, AsyncCallback<GwtPersonalPreferences> callback );
	public void savePersonalPreferences( HttpRequestInfo ri, GwtPersonalPreferences personalPrefs, AsyncCallback<Boolean> callback );
	
    // Return a GwtBrandingData object for the global workspace.
	public void getSiteBrandingData( HttpRequestInfo ri, AsyncCallback<GwtBrandingData> callback );
	
	public void getExtensionInfo( HttpRequestInfo ri, AsyncCallback<ExtensionInfoClient[]> callback );
	public void removeExtension(HttpRequestInfo ri, String id, AsyncCallback<ExtensionInfoClient[]> callback);
	public void getExtensionFiles(HttpRequestInfo ri, String id, String zoneName, AsyncCallback<ExtensionFiles> callback);
	
	// Returns a permalink for the given userId
	public void getUserPermalink(HttpRequestInfo ri, String userId, AsyncCallback<String> callback);

	// Returns a permalink to the currently logged in user's workspace.
	public void getUserWorkspacePermalink( HttpRequestInfo ri, AsyncCallback<String> callback );
	
	// The following are used in the implementation of the various
	// forms of the WorkspaceTreeControl.
	public void expandHorizontalBucket(         HttpRequestInfo ri, BucketInfo bucketInfo, AsyncCallback<TreeInfo>       callback );
	public void expandVerticalBucket(           HttpRequestInfo ri, BucketInfo bucketInfo, AsyncCallback<TreeInfo>       callback );
	public void getHorizontalTree(              HttpRequestInfo ri, String     binderId,   AsyncCallback<List<TreeInfo>> callback );
	public void getHorizontalNode(              HttpRequestInfo ri, String     binderId,   AsyncCallback<TreeInfo>       callback );
	public void getRootWorkspaceId(             HttpRequestInfo ri, String     binderId,   AsyncCallback<String>         callback );
	public void getVerticalActivityStreamsTree( HttpRequestInfo ri, String     binderId,   AsyncCallback<TreeInfo>       callback );
	public void getVerticalTree(                HttpRequestInfo ri, String     binderId,   AsyncCallback<TreeInfo>       callback );
	public void getVerticalNode(                HttpRequestInfo ri, String     binderId,   AsyncCallback<TreeInfo>       callback );
	public void persistNodeCollapse(            HttpRequestInfo ri, String     binderId,   AsyncCallback<Boolean>        callback );
	public void persistNodeExpand(              HttpRequestInfo ri, String     binderId,   AsyncCallback<Boolean>        callback );

	// The following are used in the implementation of the
	// MainMenuControl.
	public void addFavorite(                  HttpRequestInfo ri, String binderId,                                   AsyncCallback<Boolean>               callback );
	public void removeFavorite(               HttpRequestInfo ri, String favoriteId,                                 AsyncCallback<Boolean>               callback );
	public void updateFavorites(              HttpRequestInfo ri,                  List<FavoriteInfo> favoritesList, AsyncCallback<Boolean>               callback );
	public void getBinderInfo(                HttpRequestInfo ri, String binderId,                                   AsyncCallback<BinderInfo>            callback );
	public void getDefaultFolderDefinitionId( HttpRequestInfo ri, String binderId,                                   AsyncCallback<String>                callback );
	public void getFavorites(                 HttpRequestInfo ri,                                                    AsyncCallback<List<FavoriteInfo>>    callback );
	public void getMyTeams(                   HttpRequestInfo ri,                                                    AsyncCallback<List<TeamInfo>>        callback );
	public void getMyGroups(                  HttpRequestInfo ri,                                                    AsyncCallback<List<GroupInfo>>        callback );
	public void getRecentPlaces(              HttpRequestInfo ri,                                                    AsyncCallback<List<RecentPlaceInfo>> callback );
	public void getSavedSearches(             HttpRequestInfo ri,                                                    AsyncCallback<List<SavedSearchInfo>> callback );
	public void getTeamManagementInfo(        HttpRequestInfo ri, String binderId,                                   AsyncCallback<TeamManagementInfo>    callback );
	public void getToolbarItems(              HttpRequestInfo ri, String binderId,                                   AsyncCallback<List<ToolbarItem>>     callback );
	public void getTopRanked(                 HttpRequestInfo ri,                                                    AsyncCallback<List<TopRankedInfo>>   callback );
	public void removeSavedSearch(            HttpRequestInfo ri,                     SavedSearchInfo ssi,           AsyncCallback<Boolean>               callback );
	public void saveSearch(                   HttpRequestInfo ri, String searchTabId, SavedSearchInfo ssi,           AsyncCallback<SavedSearchInfo>       callback );

	// The following methods are used to manage tags on a binder/entry.
	public void canManagePublicBinderTags( HttpRequestInfo ri, String binderId, AsyncCallback<Boolean> callback );
	public void canManagePublicEntryTags( HttpRequestInfo ri, String binderId, AsyncCallback<Boolean> callback );
	public void getEntryTags( HttpRequestInfo ri, String entryId, AsyncCallback<ArrayList<TagInfo>> callback );
	public void getBinderTags( HttpRequestInfo ri, String binderId, AsyncCallback<ArrayList<TagInfo>> callback );
	public void getTagRightsForBinder( HttpRequestInfo ri, String binderId, AsyncCallback<ArrayList<Boolean>> callback );
	public void getTagRightsForEntry( HttpRequestInfo ri, String entryId, AsyncCallback<ArrayList<Boolean>> callback );
	public void getTagSortOrder( HttpRequestInfo ri, AsyncCallback<TagSortOrder> callback );
	public void saveTagSortOrder( HttpRequestInfo ri, TagSortOrder sortOrder, AsyncCallback<Boolean> callback );
	public void updateEntryTags( HttpRequestInfo ri, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded, AsyncCallback<Boolean> callback );
	public void updateBinderTags( HttpRequestInfo ri, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded, AsyncCallback<Boolean> callback );

	// The following are used to manage the tracking of information.
	public void getTrackedPeople( HttpRequestInfo ri,                  AsyncCallback<List<String>> callback );
	public void getTrackedPlaces( HttpRequestInfo ri,                  AsyncCallback<List<String>> callback );
	public void isPersonTracked(  HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	public void trackBinder(      HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	public void untrackBinder(    HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	public void untrackPerson(    HttpRequestInfo ri, String binderId, AsyncCallback<Boolean>      callback );
	
	// Save the branding data to the given binder.
	public void saveBrandingData( HttpRequestInfo ri, String binderId, GwtBrandingData brandingData, AsyncCallback<Boolean> callback );

	// Return information about the User Profile
	public void getProfileInfo(		HttpRequestInfo ri, String binderId,                AsyncCallback<ProfileInfo> 	    callback );
	public void getProfileStats(	HttpRequestInfo ri, String binderId, String userId, AsyncCallback<ProfileStats> 	callback );
	public void getProfileAvatars(	HttpRequestInfo ri, String binderId,                AsyncCallback<ProfileAttribute> callback );
	public void getQuickViewInfo( 	HttpRequestInfo ri, String binderId,                AsyncCallback<ProfileInfo> 	    callback );
	public void getTeams(			HttpRequestInfo ri, String binderId,                AsyncCallback<List<TeamInfo>>   callback );
	public void getGroups(			HttpRequestInfo ri, String binderId,                AsyncCallback<List<GroupInfo>>  callback );
	public void getMicrBlogUrl( 	HttpRequestInfo ri, String binderId,                AsyncCallback<String> 			callback );
	public void isPresenceEnabled(  HttpRequestInfo ri,                                 AsyncCallback<Boolean>			callback );
	public void getImUrl(			HttpRequestInfo ri, String binderId,                AsyncCallback<String> 			callback );
	public void getPresenceInfo(    HttpRequestInfo ri, String binderId,                AsyncCallback<GwtPresenceInfo>  callback );

	// Return the URL for the start/schedule meeting page
	public void getAddMeetingUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback);

	// The following are used for the UserStatus control
	public void saveUserStatus( HttpRequestInfo ri, String status, 	 AsyncCallback<Boolean>    callback );
	public void getUserStatus(  HttpRequestInfo ri, String binderId, AsyncCallback<UserStatus> callback ); 
	
	// Return login information such as self registration and auto complete.
	public void getLoginInfo( HttpRequestInfo ri, AsyncCallback<GwtLoginInfo> callback );
	
	// Return the url needed to invoke the "site administration" page.
	public void getSiteAdministrationUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );

	// Return upgrade information.
	public void getUpgradeInfo( HttpRequestInfo ri, AsyncCallback<GwtUpgradeInfo> callback );
	
	// Get DiskUsage Info.
	public void getDiskUsageInfo( HttpRequestInfo ri, String binderId, AsyncCallback<DiskUsageInfo> callback );

	// Activity Stream servicing APIs.
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd,                              AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pd, ActivityStreamDataType asdt, AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi,                                             AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamData(          HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi,                ActivityStreamDataType asdt, AsyncCallback<ActivityStreamData>   callback );
	public void getActivityStreamParams(        HttpRequestInfo ri,                                                                                               AsyncCallback<ActivityStreamParams> callback );
	public void getDefaultActivityStream(       HttpRequestInfo ri, String currentBinderId,                                                                       AsyncCallback<ActivityStreamInfo>   callback );
	public void hasActivityStreamChanged(       HttpRequestInfo ri,                           ActivityStreamInfo asi,                                             AsyncCallback<Boolean>              callback );	
	public void persistActivityStreamSelection( HttpRequestInfo ri,                           ActivityStreamInfo asi,                                             AsyncCallback<Boolean>              callback );
	public void saveWhatsNewShowSetting( HttpRequestInfo ri, ShowSetting showSetting, AsyncCallback<Boolean> callback );
	
	// Validate the given TeamingActions for the given entry id.
	public void validateEntryActions( HttpRequestInfo ri, ArrayList<ActionValidation> actionValidations, String entryId, AsyncCallback<ArrayList<ActionValidation>> callback );

	// Get subscription information for the given entry id.
	public void getSubscriptionData( HttpRequestInfo ri, String entryId, AsyncCallback<SubscriptionData> callback );
	
	// Save the given subscription data for the given entry id.
	public void saveSubscriptionData( HttpRequestInfo ri, String entryId, SubscriptionData subscriptionData, AsyncCallback<Boolean> callback );

	// Task servicing APIs.
	public void collapseSubtasks(      HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void deleteTask(            HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void deleteTasks(           HttpRequestInfo ri,                List<TaskId> taskIds,                           AsyncCallback<Boolean>              callback );
	public void expandSubtasks(        HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void getGroupMembership(    HttpRequestInfo ri,                Long         groupId,                           AsyncCallback<List<AssignmentInfo>> callback );
	public void getTaskList(           HttpRequestInfo ri, Long binderId, String       filterType, String modeType,       AsyncCallback<List<TaskListItem>>   callback );
	public void getTaskBundle(         HttpRequestInfo ri, Long binderId, String       filterType, String modeType,       AsyncCallback<TaskBundle>           callback );
	public void getTaskLinkage(        HttpRequestInfo ri, Long binderId,                                                 AsyncCallback<TaskLinkage>          callback );
	public void getTeamMembership(     HttpRequestInfo ri, Long binderId,                                                 AsyncCallback<List<AssignmentInfo>> callback );
	public void purgeTask(             HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Boolean>              callback );
	public void purgeTasks(            HttpRequestInfo ri,                List<TaskId> taskIds,                           AsyncCallback<Boolean>              callback );
	public void removeTaskLinkage(     HttpRequestInfo ri, Long binderId,                                                 AsyncCallback<Boolean>              callback );
	public void saveTaskCompleted(     HttpRequestInfo ri, Long binderId, Long         entryId,    String  completed,     AsyncCallback<String>               callback );
	public void saveTaskCompleted(     HttpRequestInfo ri,                List<TaskId> taskIds,    String  completed,     AsyncCallback<String>               callback );
	public void saveTaskLinkage(       HttpRequestInfo ri, Long binderId, TaskLinkage  taskLinkage,                       AsyncCallback<Boolean>              callback );
	public void saveTaskPriority(      HttpRequestInfo ri, Long binderId, Long         entryId,    String  priority,      AsyncCallback<Boolean>              callback );
	public void saveTaskSort(          HttpRequestInfo ri, Long binderId, String       sortKey,    boolean sortAscending, AsyncCallback<Boolean>              callback );
	public void saveTaskStatus(        HttpRequestInfo ri, Long binderId, Long         entryId,    String  status,        AsyncCallback<String>               callback );
	public void saveTaskStatus(        HttpRequestInfo ri,                List<TaskId> taskIds,    String  status,        AsyncCallback<String>               callback );
	public void updateCalculatedDates( HttpRequestInfo ri, Long binderId, Long         entryId,                           AsyncCallback<Map<Long, TaskDate>>  callback );
	
	// SeenMap servicing APIs.
	public void isSeen(    HttpRequestInfo ri, Long       entryId,  AsyncCallback<Boolean> callback );
	public void setSeen(   HttpRequestInfo ri, Long       entryId,  AsyncCallback<Boolean> callback );
	public void setSeen(   HttpRequestInfo ri, List<Long> entryIds, AsyncCallback<Boolean> callback );
	public void setUnseen( HttpRequestInfo ri, Long       entryId,  AsyncCallback<Boolean> callback );
	public void setUnseen( HttpRequestInfo ri, List<Long> entryIds, AsyncCallback<Boolean> callback );
	
	// Add a reply to the given entry
	public void replyToEntry( HttpRequestInfo ri, String entryId, String title, String desc, AsyncCallback<ActivityStreamEntry> callback );

	// Send an email notification to the given recipients for the given entry.
	public void shareEntry( HttpRequestInfo ri, String entryId, String comment, ArrayList<String> principalIds, ArrayList<String> teamIds, AsyncCallback<GwtShareEntryResults> callback );

	// Get the membership of the given group.
	public void getGroupMembership( HttpRequestInfo ri, String groupId, AsyncCallback<ArrayList<GwtTeamingItem>> callback );

	// See if this group is the "all users" group
	public void isAllUsersGroup( HttpRequestInfo ri, String groupId, AsyncCallback<Boolean> callback );

	// Take the given html and replace any Vibe markup with the appropriate html.
	// For example, replace {{attachmentUrl: someimagename}} with the url to the image.
	public void markupStringReplacement( HttpRequestInfo ri, String binderId, String html, String type, AsyncCallback<String> callback );
	
}// end GwtRpcServiceAsync
