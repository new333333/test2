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

import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;

import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamingMenuItem;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.workspacetree.TreeInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author jwootton
 */
public interface GwtRpcServiceAsync
{
	// Do a search given the criteria found in the GwtSearchCriteria object.
	public void executeSearch( GwtSearchCriteria searchCriteria, AsyncCallback<GwtSearchResults> callback );
	
	// Return a GwtBrandingData object for the given binder.
	public void getBinderBrandingData( String binderId, AsyncCallback<GwtBrandingData> callback );
	
	// Return a GwtBrandingData object for the corporate branding.
	public void getCorporateBrandingData( AsyncCallback<GwtBrandingData> callback );
	
	// Return the "document base url" that is used in tinyMCE configuration
	public void getDocumentBaseUrl( String binderId, AsyncCallback<String> callback );
	
	// Return an Entry object for the given entry id.
	public void getEntry( String zoneUUID, String entryId, AsyncCallback<GwtFolderEntry> callback );
	
	// Return a list of the names of the files that are attachments of the given binder.
	public void getFileAttachments( String binderId, AsyncCallback<ArrayList<String>> callback );

	// Return a Folder object for the given folder id.
	public void getFolder( String zoneUUID, String folderId, AsyncCallback<GwtFolder> callback );
	
	// Returns various binder URLs.
	public void getBinderPermalink( String binderId, AsyncCallback<String> callback );
	public void getModifyBinderUrl( String binderId, AsyncCallback<String> callback );
	
    public void getTutorialPanelState( AsyncCallback<String> callback );
	public void getExtensionInfo( AsyncCallback<ExtensionInfoClient[]> callback );
	public void removeExtension(String id, AsyncCallback<ExtensionInfoClient[]> callback);
	public void getExtensionFiles(String id, String zoneName, AsyncCallback<ExtensionFiles> callback);
	
	// Returns a permalink to the currently logged in user's workspace.
	public void getUserWorkspacePermalink(AsyncCallback<String> callback);
	
	// The following are used in the implementation of the various
	// forms of the WorkspaceTreeControl.
	public void getHorizontalTree(   String binderId, AsyncCallback<List<TreeInfo>> callback );
	public void getHorizontalNode(   String binderId, AsyncCallback<TreeInfo>       callback );
	public void getRootWorkspaceId(  String binderId, AsyncCallback<String>         callback );
	public void getVerticalTree(     String binderId, AsyncCallback<TreeInfo>       callback );
	public void getVerticalNode(     String binderId, AsyncCallback<TreeInfo>       callback );
	public void persistNodeCollapse( String binderId, AsyncCallback<Boolean>        callback );
	public void persistNodeExpand(   String binderId, AsyncCallback<Boolean>        callback );

	// The following are used in the implementation of the
	// MainMenuControl.
	public void addFavorite(           String             binderId,      AsyncCallback<Boolean>               callback );
	public void removeFavorite(        String             favoriteId,    AsyncCallback<Boolean>               callback );
	public void updateFavorites(       List<FavoriteInfo> favoritesList, AsyncCallback<Boolean>               callback );
	public void getFavorites(                                            AsyncCallback<List<FavoriteInfo>>    callback );
	public void getMyTeams(                                              AsyncCallback<List<TeamInfo>>        callback );
	public void getMenuItems(          String             binderId,      AsyncCallback<List<TeamingMenuItem>> callback );
	public void getTeamManagementInfo( String             binderId,      AsyncCallback<TeamManagementInfo>    callback );
	
	// Save the branding data to the given binder.
	public void saveBrandingData( String binderId, GwtBrandingData brandingData, AsyncCallback<Boolean> callback );

	// Return information about the User Profile
	public void getProfileInfo(String binderId, AsyncCallback<ProfileInfo> callback);
	
	// Save the User Status
	public void saveUserStatus(String status, AsyncCallback<Boolean> callback);
	// Get the User Status
	public void getUserStatus(String binderId,	AsyncCallback<UserStatus> callback); 
	
}// end GwtRpcServiceAsync
