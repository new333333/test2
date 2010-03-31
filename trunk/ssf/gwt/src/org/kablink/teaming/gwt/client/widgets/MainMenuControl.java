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

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.ActionHandler;
import org.kablink.teaming.gwt.client.ActionRequestor;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.TeamingAction;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.MenuItemButton;
import org.kablink.teaming.gwt.client.util.MenuItemToggle;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.TeamingMenuItem;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * This widget will display Teaming's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite implements ActionRequestor, ActionTrigger {
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	
	/**
	 * Constructor method.
	 */
	public MainMenuControl() {
		GwtTeamingMainMenuImageBundle images = GwtTeaming.getMainMenuImageBundle();
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		
		// Create the menu's main FlowPanel...
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("mainMenuControl");
		
		// ...add the slide-left/right toggle...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_LeftRight subhead-control-bg1 roundcornerSM");
		MenuItemToggle wsTreeSlider = new MenuItemToggle(this, panel, images.slideLeft(), messages.mainMenuAltLeftNavHideShow(), TeamingAction.HIDE_LEFT_NAVIGATION, images.slideRight(), messages.mainMenuAltLeftNavHideShow(), TeamingAction.SHOW_LEFT_NAVIGATION);
		panel.add(wsTreeSlider);
		mainPanel.add(panel);

		// ...add the slide-up/down toggle...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_UpDown subhead-control-bg1 roundcornerSM");
		MenuItemToggle mastHeadSlider = new MenuItemToggle(this, panel, images.slideUp(), messages.mainMenuAltMastHeadHideShow(), TeamingAction.HIDE_MASTHEAD, images.slideDown(), messages.mainMenuAltMastHeadHideShow(), TeamingAction.SHOW_MASTHEAD);
		panel.add(mastHeadSlider);
		mainPanel.add(panel);

		// ...add the browse hierarchy button...
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_BrowseHierarchy subhead-control-bg1 roundcornerSM");
		MenuItemButton bhButton = new MenuItemButton(this, panel, images.browseHierarchy(), messages.mainMenuAltBrowseHierarchy(), TeamingAction.BROWSE_HIERARCHY, new OnBrowseHierarchyInfo(panel));
		panel.add(bhButton);
		mainPanel.add(panel);
		
		// ...and add the GWT UI button.
		panel = new FlowPanel();
		panel.addStyleName("mainMenuButton mainMenuButton_GwtUI subhead-control-bg1 roundcornerSM");
		MenuItemButton gwtUIButton = new MenuItemButton(this, panel, images.gwtUI(), messages.mainMenuAltGwtUI(), TeamingAction.TOGGLE_GWT_UI);
		panel.add(gwtUIButton);
		mainPanel.add(panel);
		
		// Finally, all composites must call initWidget() in their
		// constructors.
		initWidget(mainPanel);
	}

	/**
	 * Called to add an ActionHandler to this MainMenuControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add( actionHandler );
	}
	

	/**
	 * Called when a new context has been loaded into the content panel
	 * to refresh the menu contents.
	 * 
	 * @param binderId
	 */
	public void contextLoaded(String binderId) {
		GwtTeaming.getRpcService().getMenuItems(binderId, new AsyncCallback<List<TeamingMenuItem>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(List<TeamingMenuItem> miList)  {
//!				...this needs to be implemented...
			}
		});
	}
	
	/*
	 * Fires a TeamingAction at the registered ActionHandler's.
	 */
	public void triggerAction(TeamingAction action) {
		// Always use the final form of the method.
		triggerAction(action, null);
	}
	
	public void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
}
