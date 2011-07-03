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
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.profile.ProfileRequestInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;


public class ProfileTeamsPanel extends ProfileSectionPanel  {

	private final String IDBASE = "myTeams_";
	protected List<TeamInfo> teamList;

	public ProfileTeamsPanel(ProfileRequestInfo profileRequestInfo, String title) {
		
		super(profileRequestInfo, title);
		
		setStyleName("tracking-subhead");
	
		getTeams();
	}

	
	
	private void getTeams() {
		
		rpcService.getTeams( HttpRequestInfo.createHttpRequestInfo(), profileRequestInfo.getBinderId(), new AsyncCallback<List<TeamInfo>>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTeams(),
					profileRequestInfo.getBinderId());
			}
			
			public void onSuccess(List<TeamInfo> tList)  {
				
				teamList = tList;
				
				// Scan the teams...
				int teamCount = 0;
				SideBarAnchor sbA;
				for (Iterator<TeamInfo> tIT = tList.iterator(); tIT.hasNext(); ) {
					
					// ...creating an item structure for each.
					TeamInfo team = tIT.next();
					String teamId = (IDBASE + team.getBinderId());
					
					sbA = new SideBarAnchor(teamId, team.getTitle(), team.getEntityPath(), new TeamClickHandler(team));
					boolean visible = true;
					if(teamCount > 3){
						visible = false;
						showExpandButton();
					}
					
					addContentWidget(sbA, visible);
					teamCount += 1;
				}
				
				// If there weren't any teams...
				if (0 == teamCount) {
					// ...put something in the menu that tells the user
					// ...that.
					Label content = new Label(messages.mainMenuMyTeamsNoTeams());
					//content.addStyle():
					addContentWidget(content, true);
				}
			}
			
		});
		
	}
	
	
	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class TeamClickHandler implements ClickHandler {
		private TeamInfo team;	// The team clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		TeamClickHandler(TeamInfo t) {
			// Simply store the parameter.
			team = t;
		}

		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {

			if(team.getPermalinkUrl() != "") {
				gotoUrl(team.getPermalinkUrl(), true);
			} else {
				//is this more than lets expand the 
			   
			}
		}
	}

	protected void expand2() {
		
		Window.alert("Expand button pressed");
		
//		// Scan the teams...
//		int teamCount = 0;
//		SideBarAnchor sbA;
//		for (Iterator<TeamInfo> tIT = teamList.iterator(); tIT.hasNext(); ) {
//			
//			//if 
//			if(teamCount == 1) {
//				String teamId = (IDBASE + "More");
//				sbA = new SideBarAnchor(teamId, "More", "", new TeamClickHandler(new TeamInfo()));
//				return;
//			}
//			
//			// ...creating an item structure for each.
//			TeamInfo team = tIT.next();
//			String teamId = (IDBASE + team.getBinderId());
//			
//			sbA = new SideBarAnchor(teamId, team.getTitle(), team.getEntityPath(), new TeamClickHandler(team));
//			addContentWidget(sbA);
//			teamCount += 1;
//		}
	}
	
	/*
	 * This method will be called to goto a permalink URL received as a
	 * parameter.
	 * 
	 */
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
