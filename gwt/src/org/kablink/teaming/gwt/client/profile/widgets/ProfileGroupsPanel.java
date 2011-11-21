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

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;


public class ProfileGroupsPanel extends ProfileSectionPanel  {

	private final String IDBASE = "myGroups_";
	protected List<GroupInfo> groupList;

	public ProfileGroupsPanel(ProfileRequestInfo profileRequestInfo, String title) {
		
		super(profileRequestInfo, title);
		
		setStyleName("tracking-subhead");
	
		getGroups();
	}

	
	
	private void getGroups() {
		
		GetGroupsCmd cmd;
		
		cmd = new GetGroupsCmd( profileRequestInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GwtGroups(),
					profileRequestInfo.getBinderId());
			}
			public void onSuccess(VibeRpcResponse response)  {
				
				List<GroupInfo> gList;
				GetGroupsRpcResponseData responseData;
				
				responseData = (GetGroupsRpcResponseData) response.getResponseData();
				gList = responseData.getGroups();
				groupList = gList;
				
				// Scan the groups...
				int groupCount = 0;
				SideBarAnchor sbA;
				for (Iterator<GroupInfo> gIT = gList.iterator(); gIT.hasNext(); ) {
					
					// ...creating an item structure for each.
					GroupInfo group = gIT.next();
					String groupId = (IDBASE + group.getId().toString());
					
					sbA = new SideBarAnchor(groupId, group.getTitle(), group.getTitle(), new GroupClickHandler(group));
					boolean visible = true;
					if(groupCount > 3){
						visible = false;
						showExpandButton();
					}
					
					addContentWidget(sbA, visible);
					groupCount += 1;
				}
				
				// If there weren't any groups...
				if (0 == groupCount) {
					// ...put something in the menu that tells the user
					// ...that.
					Label content = new Label(messages.mainMenuMyGroupsNoGroups());
					//content.addStyle():
					addContentWidget(content, true);
				}
			}
			
		});
		
	}
	
	
	/*
	 * Inner class that handles clicks on individual groups.
	 */
	private class GroupClickHandler implements ClickHandler {
		@SuppressWarnings("unused")
		private GroupInfo group;	// The group clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param myGroup
		 */
		GroupClickHandler(GroupInfo g) {
			// Simply store the parameter.
			group = g;
		}

		/**
		 * Called when the user clicks on a group.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {

		}
	}

	protected void expand2() {
		
		Window.alert("Expand button pressed");
		
//		// Scan the groups...
//		int groupCount = 0;
//		SideBarAnchor sbA;
//		for (Iterator<GroupInfo> gIT = groupList.iterator(); gIT.hasNext(); ) {
//			
//			//if 
//			if(groupCount == 1) {
//				String groupId = (IDBASE + "More");
//				sbA = new SideBarAnchor(groupId, "More", "", new GroupClickHandler(new GroupInfo()));
//				return;
//			}
//			
//			// ...creating an item structure for each.
//			GroupInfo group = gIT.next();
//			String groupId = (IDBASE + group.getBinderId());
//			
//			sbA = new SideBarAnchor(groupId, group.getTitle(), group.getEntityPath(), new GroupClickHandler(group));
//			addContentWidget(sbA);
//			groupCount += 1;
//		}
	}
	
	/*
	 * This method will be called to goto a permalink URL received as a
	 * parameter.
	 * 
	 */
	@SuppressWarnings("unused")
	private void gotoUrl( Object obj, boolean isPermalink )
	{
		if ( obj instanceof String )
		{
			GwtClientHelper.jsLoadUrlInTopWindow( (String) obj );
			
//			if (isPermalink)
//			     Window.Location.replace( (String) obj );
//			else GwtClientHelper.jsLoadUrlInTopWindow( (String) obj );
		}
		else
			Window.alert( "in gotoUrl() and obj is not a String object" );
	}//end gotoUrl()
}
