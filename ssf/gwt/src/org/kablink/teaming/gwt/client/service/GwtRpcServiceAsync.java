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
package org.kablink.teaming.gwt.client.service;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
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
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
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
	
	// Return a list of the names of the files that are attachments of the given binder.
	public void getFileAttachments( String binderId, AsyncCallback<ArrayList<String>> callback );

	// Return a Folder object for the given folder id.
	public void getFolder( HttpRequestInfo ri, String zoneUUID, String folderId, AsyncCallback<GwtFolder> callback );
	
	// Returns various binder URLs.
	public void getBinderPermalink( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );
	public void getModifyBinderUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );
	
    public void getTutorialPanelState( AsyncCallback<String> callback );

	// The following deal with personal preferences.
	public void getPersonalPreferences( AsyncCallback<GwtPersonalPreferences> callback );
	public void savePersonalPreferences( GwtPersonalPreferences personalPrefs, AsyncCallback<Boolean> callback );
	
    // Return a GwtBrandingData object for the global workspace.
	public void getSiteBrandingData( HttpRequestInfo ri, AsyncCallback<GwtBrandingData> callback );
	
	public void getExtensionInfo( AsyncCallback<ExtensionInfoClient[]> callback );
	public void removeExtension(String id, AsyncCallback<ExtensionInfoClient[]> callback);
	public void getExtensionFiles(String id, String zoneName, AsyncCallback<ExtensionFiles> callback);
	
	// Returns a permalink for the given userId
	public void getUserPermalink(HttpRequestInfo ri, String userId, AsyncCallback<String> callback);

	// Returns a permalink to the currently logged in user's workspace.
	public void getUserWorkspacePermalink( HttpRequestInfo ri, AsyncCallback<String> callback );
	
	// The following are used to interact with the GWT UI defaults.
	public void getGwtUIDefault(   AsyncCallback<Boolean> callback );
	public void getGwtUIEnabled(   AsyncCallback<Boolean> callback );
	public void getGwtUIExclusive( AsyncCallback<Boolean> callback );
	
	// The following are used in the implementation of the various
	// forms of the WorkspaceTreeControl.
	public void getHorizontalTree(   HttpRequestInfo ri, String binderId, AsyncCallback<List<TreeInfo>> callback );
	public void getHorizontalNode(   HttpRequestInfo ri, String binderId, AsyncCallback<TreeInfo>       callback );
	public void getRootWorkspaceId(                      String binderId, AsyncCallback<String>         callback );
	public void getVerticalTree(     HttpRequestInfo ri, String binderId, AsyncCallback<TreeInfo>       callback );
	public void getVerticalNode(     HttpRequestInfo ri, String binderId, AsyncCallback<TreeInfo>       callback );
	public void persistNodeCollapse(                     String binderId, AsyncCallback<Boolean>        callback );
	public void persistNodeExpand(                       String binderId, AsyncCallback<Boolean>        callback );

	// The following are used in the implementation of the
	// MainMenuControl.
	public void addFavorite(                  String             binderId,                                   AsyncCallback<Boolean>               callback );
	public void removeFavorite(               String             favoriteId,                                 AsyncCallback<Boolean>               callback );
	public void updateFavorites(                                           List<FavoriteInfo> favoritesList, AsyncCallback<Boolean>               callback );
	public void getBinderTags(                String             binderId,                                   AsyncCallback<List<TagInfo>>         callback );
	public void canManagePublicBinderTags(    String             binderId,                                   AsyncCallback<Boolean>               callback );
	public void addBinderTag(                 String             binderId, TagInfo            binderTag,     AsyncCallback<TagInfo>               callback );
	public void removeBinderTag(              String             binderId, TagInfo            binderTag,     AsyncCallback<Boolean>               callback );
	public void updateBinderTags(             String             binderId, List<TagInfo>      binderTags,    AsyncCallback<Boolean>               callback );
	public void getBinderInfo(                String             binderId,                                   AsyncCallback<BinderInfo>            callback );
	public void getDefaultFolderDefinitionId( String             binderId,                                   AsyncCallback<String>                callback );
	public void getFavorites(                                                                                AsyncCallback<List<FavoriteInfo>>    callback );
	public void getMyTeams(                   HttpRequestInfo    ri,                                         AsyncCallback<List<TeamInfo>>        callback );
	public void getMyGroups(                  HttpRequestInfo    ri,                                         AsyncCallback<List<GroupInfo>>        callback );
	public void getRecentPlaces(              HttpRequestInfo    ri,                                         AsyncCallback<List<RecentPlaceInfo>> callback );
	public void getSavedSearches(                                                                            AsyncCallback<List<SavedSearchInfo>> callback );
	public void getTeamManagementInfo(        HttpRequestInfo    ri,          String          binderId,      AsyncCallback<TeamManagementInfo>    callback );
	public void getToolbarItems(              String             binderId,                                   AsyncCallback<List<ToolbarItem>>     callback );
	public void getTopRanked(                 HttpRequestInfo    ri,                                         AsyncCallback<List<TopRankedInfo>>   callback );
	public void removeSavedSearch(                                            SavedSearchInfo ssi,           AsyncCallback<Boolean>               callback );
	public void saveSearch(                   String             searchTabId, SavedSearchInfo ssi,           AsyncCallback<SavedSearchInfo>       callback );

	// The following are used to manage the tracking of binders.
	public void trackBinder(   String binderId, AsyncCallback<Boolean> callback );
	public void untrackBinder( String binderId, AsyncCallback<Boolean> callback );
	public void untrackPerson( String binderId, AsyncCallback<Boolean> callback );
	public void isPersonTracked( String binderId, AsyncCallback<Boolean> callback );
	
	// Save the branding data to the given binder.
	public void saveBrandingData( String binderId, GwtBrandingData brandingData, AsyncCallback<Boolean> callback );

	// Return information about the User Profile
	public void getProfileInfo(		HttpRequestInfo ri, String binderId, AsyncCallback<ProfileInfo> 	callback);
	public void getProfileStats(	HttpRequestInfo ri, String binderId, AsyncCallback<ProfileStats> 	callback);
	public void getProfileAvatars(	HttpRequestInfo ri, String binderId, AsyncCallback<ProfileAttribute>callback);
	public void getQuickViewInfo( 	HttpRequestInfo ri, String binderId, AsyncCallback<ProfileInfo> 	callback);
	public void getTeams(			HttpRequestInfo ri, String binderId, AsyncCallback<List<TeamInfo>>  callback );
	public void getGroups(			HttpRequestInfo ri, String binderId, AsyncCallback<List<GroupInfo>> callback );
	public void getMicrBlogUrl( 	HttpRequestInfo ri, String binderId, AsyncCallback<String> 			callback);
	public void isPresenceEnabled(                   AsyncCallback<Boolean>			callback);
	public void getImUrl(			String binderId, AsyncCallback<String> 			callback);
	public void getPresenceInfo(    String binderId, AsyncCallback<GwtPresenceInfo> callback);

	// The following are used for the UserStatus control
	public void saveUserStatus(String status, 	AsyncCallback<Boolean> 		callback);
	public void getUserStatus( String binderId,	AsyncCallback<UserStatus> 	callback); 
	
	// Return information about self registration.
	public void getSelfRegistrationInfo( HttpRequestInfo ri, AsyncCallback<GwtSelfRegistrationInfo> callback );
	
	// Return the url needed to invoke the "site administration" page.
	public void getSiteAdministrationUrl( HttpRequestInfo ri, String binderId, AsyncCallback<String> callback );

	// Return upgrade information.
	public void getUpgradeInfo( AsyncCallback<GwtUpgradeInfo> callback );

	
}// end GwtRpcServiceAsync
