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

package org.kablink.teaming.gwt.client.profile.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetSavedSearchesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSavedSearchesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class ProfileSearchesSectionPanel extends ProfileSectionPanel {

	private final String IDBASE = "mySearches_";
	private List<SavedSearchInfo> ssList;
	
	/**
	 * Create the saved searches
	 * 
	 * @param profileRequestInfo
	 * @param title
	 * @param trigger
	 */
	public ProfileSearchesSectionPanel(ProfileRequestInfo profileRequestInfo, String title) {
		super(profileRequestInfo, title);
		setStyleName("tracking-subhead");
		//populate the saved searches list
		populateSavedSearchList();
	}
	
	/*
	 * Called to use GWT RPC to populate the saved searches list box.
	 */
	private void populateSavedSearchList() {
		GetSavedSearchesCmd cmd;
		
		//ssList.addItem(GwtTeaming.getMessages().mainMenuSearchOptionsNoSavedSearches(),"noSavedSearches");
		//ssList.setEnabled(false);
		
		// Does the user have any saved searches defined?
		cmd = new GetSavedSearchesCmd();
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetSavedSearches());
			}
			
			public void onSuccess(VibeRpcResponse response)  {
				List<SavedSearchInfo> ssiList;
				GetSavedSearchesRpcResponseData responseData;
				
				responseData = (GetSavedSearchesRpcResponseData) response.getResponseData();
				ssiList = responseData.getSavedSearches();
				
				ssList = ssiList;
				buildSavedSearchLinks();
			}
		});
	}

	
	private void buildSavedSearchLinks(){
		
		if(selectedMore) {
			clearWidgets();
		}
		
		SideBarAnchor sbA;

		int count = 0;
		// ...scan the saved searches...
		for (SavedSearchInfo savedSearch: ssList ) {

			// ...creating an item structure for each.
			String id = (IDBASE + savedSearch.getName());
			sbA = new SideBarAnchor(id, savedSearch.getName(), savedSearch.getName(), new SavedSearchesClickHandler(savedSearch));
			
			boolean visible = true;
			if(count > 3){
				visible = false;
				showExpandButton();
			}
			
			addContentWidget(sbA, visible);
			count++;
		}
		
		// If there weren't any teams...
		if (0 == count) {
			// ...put something in the menu that tells the user
			// ...that.
			Label content = new Label(messages.profileNoSavedSearches());
			//content.addStyle():
			addContentWidget(content, true);
			
			return;
		}
	}

	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class SavedSearchesClickHandler implements ClickHandler {
		private SavedSearchInfo savedSearch;	// The team clicked on.
	
		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		SavedSearchesClickHandler(SavedSearchInfo s) {
			// Simply store the parameter.
			savedSearch = s;
		}
	
		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {	
			String searchFor = savedSearch.getName();
			if(GwtClientHelper.hasString(searchFor)) {
				searchFor = GwtClientHelper.jsEncodeURIComponent(searchFor);
				String searchUrl = (profileRequestInfo.getSavedSearchUrl() + "&ss_queryName=" + searchFor);
				GwtClientHelper.jsLoadUrlInCurrentWindow(searchUrl);
			} 
		}
	}
}