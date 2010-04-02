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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used for the My Teams menu item popup.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MyTeamsMenuPopup extends MenuItemPopup {
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
		super(actionTrigger, GwtTeaming.getMessages().mainMenuItemMyTeams(), left, top);
	}
	
	/**
	 * Completes construction of the menu and show's it.
	 * 
	 * Overrides MenuItemPopup.show().
	 */
	@Override
	public void show() {
		GwtTeaming.getRpcService().getMyTeams(new AsyncCallback<List<TeamInfo>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(List<TeamInfo> mtList)  {
				// Scan the teams...
				for (Iterator<TeamInfo> mtIT = mtList.iterator(); mtIT.hasNext(); ) {
					// ...creating an item structure for each.
					TeamInfo mt = mtIT.next();
					String mtId = ("myTeam_" + mt.getBinderId());

					FlowPanel mtItemPanel = new FlowPanel();
					mtItemPanel.addStyleName("mainMenuPopup_ItemPanel");
					FlowPanel mtTitlePanel = new FlowPanel();
					mtTitlePanel.addStyleName("mainMenuPopup_Item");
					Label mtTitle = new Label(mt.getTitle());
					mtTitle.getElement().setId(mtId);
					mtTitle.addStyleName("mainMenuPopup_ItemText");
					mtTitlePanel.add(mtTitle);
					
					Anchor mtA = new Anchor();
					mtA.addStyleName("mainMenuPopup_ItemA");
					mtA.setTitle(mt.getEntityPath());
					mtA.addClickHandler(new MyTeamClickHandler(mt));
					MenuItemIDHover hover = new MenuItemIDHover(mtId, "mainMenuPopup_ItemHover");
					mtA.addMouseOverHandler(hover);
					mtA.addMouseOutHandler( hover);
					
					mtA.getElement().appendChild(mtTitle.getElement());
					mtTitlePanel.add(mtA);
					mtItemPanel.add(mtTitlePanel);
					addContentWidget(mtItemPanel);
				}

				// Add a spacer between the teams and team commands.
				FlowPanel spacerPanel = new FlowPanel();
				spacerPanel.addStyleName("mainMenuPopup_ItemSpacer");
				addContentWidget(spacerPanel);

				// Add the team command items.
//!				...this needs to be implemented...
				Label content = new Label("...this needs to be implemented...");
				content.addStyleName("mainMenuPopup_Item mainMenuPopup_ItemText");
				addContentWidget(content);

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
