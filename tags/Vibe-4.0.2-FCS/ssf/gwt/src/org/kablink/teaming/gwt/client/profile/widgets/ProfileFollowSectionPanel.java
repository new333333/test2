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
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;


public class ProfileFollowSectionPanel extends ProfileSectionPanel {

	private List<GwtUser> trackedUsers;
	private String IDBASE = "TrackedId";

	public ProfileFollowSectionPanel(ProfileRequestInfo profileRequestInfo, String title) {
		
		super(profileRequestInfo, title);
		
		setStyleName("tracking-subhead");
	}
	
	public void addtrackedPersons(ProfileStats stats) {

		trackedUsers = stats.getTrackedUsers();
		buildTrackedLinks();
	}
	
	private void buildTrackedLinks(){
		
		if(selectedMore) {
			clearWidgets();
		}
		
		SideBarAnchor sbA;

		int count = 0;
		// ...scan the saved searches...
		for (GwtUser trackedUser: trackedUsers ) {

			// ...creating an item structure for each.
			String id = (IDBASE  + trackedUser.getUserId());
			sbA = new SideBarAnchor(id, trackedUser.getTitle(), trackedUser.getTitle(), new TrackedPersonClickHandler(trackedUser));
			
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
			Label content = new Label(messages.profileNotFollowing());
			//content.addStyle():
			addContentWidget(content, true);
			
			return;
		}
	}
	
	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class TrackedPersonClickHandler implements ClickHandler {
		private GwtUser trackedUser;	// The team clicked on.
	
		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TrackedPersonClickHandler(GwtUser t) {
			// Simply store the parameter.
			trackedUser = t;
		}
	
		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			String url = trackedUser.getViewWorkspaceUrl();
			if(GwtClientHelper.hasString(url)) {
				url = GwtClientHelper.appendUrlParam( url, "operation", "showProfile" );
				GwtClientHelper.jsLoadUrlInCurrentWindow(url);
			}
			else
			{
				// Tell the user they don't have the rights to see this person's profile
				Window.alert( GwtTeaming.getMessages().profileInsufficientViewProfileRights() );
			}

//			if(GwtClientHelper.hasString(trackedUser.getUserId())) {
//				final String userId = trackedUser.getUserId();
//				GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
//			
//				rpcService.getUserPermalink( userId, new AsyncCallback<String>() {
//					public void onFailure( Throwable t ) {
//						Window.alert( t.toString() );
//					}//end onFailure()
//					
//					public void onSuccess( String url ) {
//						OnSelectBinderInfo osbInfo;
//						url = GwtClientHelper.appendUrlParam( url, "operation", "showProfile" );
//						osbInfo = new OnSelectBinderInfo( trackedUser.getWorkspaceId(), url, false, Instigator.OTHER );
//						GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
//					}// end onSuccess()
//				});// end AsyncCallback()
//			} 
		}
	}
	
}
