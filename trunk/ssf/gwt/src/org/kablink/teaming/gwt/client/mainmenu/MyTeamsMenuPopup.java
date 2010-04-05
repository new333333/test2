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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * Class used for the My Teams menu item popup.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MyTeamsMenuPopup extends MenuBarPopup {
	private final String IDBASE = "myTeams_";
	
	/*
	 * Inner class that handles clicks on individual teams.
	 */
	private class MyTeamClickHandler implements ClickHandler {
		private TeamInfo m_myTeam;	// The team clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param myTeam
		 */
		MyTeamClickHandler(TeamInfo myTeam) {
			// Simply store the parameter.
			m_myTeam = myTeam;
		}

		/**
		 * Called when the user clicks on a team.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Hide the menu...
			hide();
			
			// ...and trigger a selection changed event.
			m_actionTrigger.triggerAction(
				TeamingAction.SELECTION_CHANGED,
				new OnSelectBinderInfo(
					m_myTeam.getBinderId(),
					m_myTeam.getPermalinkUrl(),
					false,
					Instigator.OTHER));
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param left
	 * @param top
	 */
	public MyTeamsMenuPopup(ActionTrigger actionTrigger, int left, int top) {
		// Initialize the super class.
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarMyTeams(), left, top);
	}
	
	/**
	 * Completes construction of the menu and show's it.
	 * 
	 * Overrides MenuBarPopup.show().
	 */
	@Override
	public void show() {
		m_rpcService.getMyTeams(new AsyncCallback<List<TeamInfo>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(List<TeamInfo> mtList)  {
				// Scan the teams...
				int mtCount = 0;
				MenuPopupAnchor mtA;
				for (Iterator<TeamInfo> mtIT = mtList.iterator(); mtIT.hasNext(); ) {
					// ...creating an item structure for each.
					TeamInfo mt = mtIT.next();
					String mtId = (IDBASE + mt.getBinderId());
					
					mtA = new MenuPopupAnchor(mtId, mt.getTitle(), mt.getEntityPath(), new MyTeamClickHandler(mt));
					addContentWidget(mtA);
					mtCount += 1;
				}
				
				// If there weren't any teams...
				if (0 == mtCount) {
					// ...put something in the menu that tells the user
					// ...that.
					MenuPopupLabel content = new MenuPopupLabel(m_messages.mainMenuMyTeamsNoTeams());
					addContentWidget(content);
				}

				// Add a spacer between the teams and team commands.
				FlowPanel spacerPanel = new FlowPanel();
				spacerPanel.addStyleName("mainMenuPopup_ItemSpacer");
				addContentWidget(spacerPanel);
				
				// Add the team command items.
				mtA = new MenuPopupAnchor((IDBASE + "View"), m_messages.mainMenuMyTeamsViewTeam(), null, new ClickHandler() {
					public void onClick(ClickEvent event) {
						hide();
						m_actionTrigger.triggerAction(TeamingAction.VIEW_TEAM_MEMBERS);
					}
				});
				addContentWidget(mtA);
				mtA = new MenuPopupAnchor((IDBASE + "Manage"), m_messages.mainMenuMyTeamsManageTeam(), null, new ClickHandler() {
					public void onClick(ClickEvent event) {
						hide();
						Window.alert("MyTeamsMenuPopup.ManageTeamMembers( ...this needs to be implemented... )");
					}
				});
				addContentWidget(mtA);
				mtA = new MenuPopupAnchor((IDBASE + "Send"), m_messages.mainMenuMyTeamsSendEmailToTeam(), null, new ClickHandler() {
					public void onClick(ClickEvent event) {
						hide();
						Window.alert("MyTeamsMenuPopup.SendTeamEmail( ...this needs to be implemented... )");
					}
				});
				addContentWidget(mtA);
				mtA = new MenuPopupAnchor((IDBASE + "Start"), m_messages.mainMenuMyTeamsStartTeamMeeting(), null, new ClickHandler() {
					public void onClick(ClickEvent event) {
						hide();
						Window.alert("MyTeamsMenuPopup.StartTeamMeeting( ...this needs to be implemented... )");
					}
				});
				addContentWidget(mtA);

				// Finally, show the menu.
				showImpl();
			}
		});
	}

	/*
	 * Simply calls the superclass's show() method.
	 */
	private void showImpl() {
		super.show();
	}
}
