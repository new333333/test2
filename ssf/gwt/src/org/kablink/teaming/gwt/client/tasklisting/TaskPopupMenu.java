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

package org.kablink.teaming.gwt.client.tasklisting;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This popup menu is used to display menus of for the various task
 * options a user can select from when interacting with the TaskTable.
 * 
 * @author drfoster@novell.com
 */
public class TaskPopupMenu extends PopupMenu implements ActionHandler {
	private Element					m_menuPartner;	//
	private List<PopupMenuItem>		m_menuItems;	//
	private List<TaskMenuOption>	m_menuOptions;	//
	private TaskListItem			m_task;			//
	private TaskTable				m_taskTable;	//
	private TeamingAction			m_taskAction;	//
	
	/**
	 * Constructor method.
	 * 
	 * @param taskTable
	 */
	public TaskPopupMenu(TaskTable taskTable, TeamingAction taskAction, List<TaskMenuOption> menuOptions) {
		// Initialize the super class...
		super(true, true);
		
		// ...store the parameters...
		m_taskTable   = taskTable;
		m_taskAction  = taskAction;
		m_menuOptions = menuOptions;
		
		// ...finish initializing the popup...
		setGlassEnabled(true);
		setGlassStyleName("gwtTaskList_popupGlass");

		// Add a close handler to the popup so that it can restore the
		// controlling Widget's styles when the popup menu is closed.
		addCloseHandler(new CloseHandler<PopupPanel>(){
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				removeAutoHidePartner(m_menuPartner);
			}});
		
		// ...and add the menu items.
		m_menuItems = new ArrayList<PopupMenuItem>();
		for (TaskMenuOption po:  m_menuOptions) {
			m_menuItems.add(
				addMenuItem(
					this,
					m_taskAction,
					po.getMenu(),
					po.buildImage(),
					po.getMenuAlt()));
		}
	}

	/**
	 * Get'er method.
	 * 
	 * @return
	 */
	public 	List<TaskMenuOption> getMenuOptions() {
		return m_menuOptions;
	}

	/**
	 * Called by the PopupMenu when one of the menu items is selected.
	 * 
	 * Implements the ActionHandler.handleAction() method.
	 * 
	 * @param action
	 * @param obj
	 */
	@Override
	public void handleAction(TeamingAction action, Object obj) {
		// The only action this will ever receive is the option given
		// during the TaskPopupMenu's creation.  Simply tell the
		// TaskTable to handle it.
		m_taskTable.setTaskOption(m_task, m_taskAction, ((String) obj)); 
	}

	/**
	 * Called to show the TaskPopupMenu.
	 */
	public void showTaskPopupMenu(TaskListItem task, Element menuPartner) {
		// Remember the task and menu partner that we are dealing
		// with...
		m_task        = task;
		m_menuPartner = menuPartner;
		
		// ...make sure all the menu items are visible...
		for (PopupMenuItem mi:  m_menuItems) {
			setMenuItemVisibility(mi, true);
		}

		// ...and allow the popup to be closed if the parter item is
		// ...clicked on again...
		addAutoHidePartner(m_menuPartner);
		
		// ...and position and show the popup.
		int popupLeft = (m_menuPartner.getAbsoluteLeft()   + GwtClientHelper.jsGetContentIFrameLeft());
		int popupTop  = (m_menuPartner.getAbsoluteBottom() + GwtClientHelper.jsGetContentIFrameTop());
		setPopupPosition(popupLeft, popupTop);
		show();
	}
}
