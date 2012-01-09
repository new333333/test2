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

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TaskNewTaskEvent;
import org.kablink.teaming.gwt.client.event.TaskSetPercentDoneEvent;
import org.kablink.teaming.gwt.client.event.TaskSetPriorityEvent;
import org.kablink.teaming.gwt.client.event.TaskSetStatusEvent;
import org.kablink.teaming.gwt.client.event.TaskViewEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This popup menu is used to display menus of for the various task
 * options a user can select from when interacting with the TaskTable.
 * 
 * @author drfoster@novell.com
 */
public class TaskPopupMenu extends PopupMenu
	implements
	// Event handlers implemented by this class.
		TaskNewTaskEvent.Handler,
		TaskSetPercentDoneEvent.Handler,
		TaskSetPriorityEvent.Handler,
		TaskSetStatusEvent.Handler,
		TaskViewEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	//
	private List<VibeMenuItem>			m_menuItems;				//
	private List<TaskMenuOption>		m_menuOptions;				//
	private TaskListItem				m_task;						//
	private TaskListing					m_taskListing;				//
	private TaskTable					m_taskTable;				//
	private TeamingEvents				m_taskEventEnum;			//
	private Widget						m_menuPartner;				//
	
	private static TaskPopupMenu		m_closedPopupMenu;			// Most recently closed popup menu.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.TASK_NEW_TASK,
		TeamingEvents.TASK_SET_PERCENT_DONE,
		TeamingEvents.TASK_SET_PRIORITY,
		TeamingEvents.TASK_SET_STATUS,
		TeamingEvents.TASK_VIEW,
	};
	
	/*
	 * Constructor method.
	 */
	private TaskPopupMenu(TaskTable taskTable, TaskListing taskListing, TeamingEvents taskEventEnum, List<TaskMenuOption> menuOptions) {
		// Initialize the super class...
		super(true, true, true);
		
		// ...store the parameters...
		m_taskTable     = taskTable;
		m_taskListing   = taskListing;
		m_taskEventEnum = taskEventEnum;
		m_menuOptions   = menuOptions;
		
		// ...finish initializing the popup...
		setGlassEnabled(true);
		setGlassStyleName("gwtTaskList_popupGlass");

		// Add a close handler to the popup so that it can restore the
		// controlling Widget's styles when the popup menu is closed.
		addCloseHandler(new CloseHandler<PopupPanel>(){
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				removeAutoHidePartner(m_menuPartner.getElement());
			}});
		
		// ...and add the menu items.
		m_menuItems = new ArrayList<VibeMenuItem>();
		for (TaskMenuOption po:  m_menuOptions) {
			if (po.isSeparator()) {
				addSeparator();
			}
			else {
				String eventOption = po.getMenu();
				VibeEventBase<?> taskEvent = EventHelper.createSimpleEvent(m_taskEventEnum);
				switch (m_taskEventEnum) {
				case TASK_NEW_TASK:          ((TaskNewTaskEvent)        taskEvent).setEventOption(eventOption); break;
				case TASK_SET_PERCENT_DONE:  ((TaskSetPercentDoneEvent) taskEvent).setEventOption(eventOption); break;
				case TASK_SET_PRIORITY:      ((TaskSetPriorityEvent)    taskEvent).setEventOption(eventOption); break; 
				case TASK_SET_STATUS:        ((TaskSetStatusEvent)      taskEvent).setEventOption(eventOption); break;
				case TASK_VIEW:              ((TaskViewEvent)           taskEvent).setEventOption(eventOption); break;
				default:
					Window.alert(GwtTeaming.getMessages().taskInternalError_UnexpectedEvent(m_taskEventEnum.toString()));
					continue;
				}
				VibeMenuItem pmi =
					addMenuItem(
						taskEvent,
						po.buildImage(),
						po.getMenuAlt());
				pmi.setCheckedState(po.isMenuChecked());
				m_menuItems.add(pmi);
			}
		}
		
		final TaskPopupMenu thisPopupMenu = this;
		addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				m_closedPopupMenu = thisPopupMenu;
			}			
		});
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param taskListing
	 * @param taskEventEnum
	 * @param menuOptions
	 */
	public TaskPopupMenu(TaskListing taskListing, TeamingEvents taskEventEnum, List<TaskMenuOption> menuOptions) {
		this(null, taskListing, taskEventEnum, menuOptions);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param taskTable
	 * @param taskEventEnum
	 * @param menuOptions
	 */
	public TaskPopupMenu(TaskTable taskTable, TeamingEvents taskEventEnum, List<TaskMenuOption> menuOptions) {
		this(taskTable, null, taskEventEnum, menuOptions);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<TaskMenuOption> getMenuOptions()   {return m_menuOptions;  }
	public TeamingEvents        getTaskEventEnum() {return m_taskEventEnum;}

	/**
	 * Handles TaskNewTaskEvent's received by this class.
	 * 
	 * Implements the TaskNewTaskEvent.Handler.onTaskNewTask() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskNewTask(TaskNewTaskEvent event) {
		if (null != m_task) {
			m_taskTable.setTaskOption(m_task, event.getEventEnum(), event.getEventOption());
		}
	}

	/**
	 * Handles TaskSetPercentDoneEvent's received by this class.
	 * 
	 * Implements the TaskSetPercentDoneEvent.Handler.onTaskSetPercentDone() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskSetPercentDone(TaskSetPercentDoneEvent event) {
		if (null != m_task) {
			m_taskTable.setTaskOption(m_task, event.getEventEnum(), event.getEventOption());
		}
	}

	/**
	 * Handles TaskSetPriorityEvent's received by this class.
	 * 
	 * Implements the TaskSetPriorityEvent.Handler.onTaskSetPriority() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskSetPriority(TaskSetPriorityEvent event) {
		if (null != m_task) {
			m_taskTable.setTaskOption(m_task, event.getEventEnum(), event.getEventOption());
		}
	}

	/**
	 * Handles TaskSetStatusEvent's received by this class.
	 * 
	 * Implements the TaskSetStatusEvent.Handler.onTaskSetStatus() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskSetStatus(TaskSetStatusEvent event) {
		if (null != m_task) {
			m_taskTable.setTaskOption(m_task, event.getEventEnum(), event.getEventOption());
		}
	}

	/**
	 * Handles TaskViewEvent's received by this class.
	 * 
	 * Implements the TaskViewEvent.Handler.onTaskView() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskView(TaskViewEvent event) {
		m_taskListing.setViewOption(event.getEventEnum(), event.getEventOption());
	}

	/**
	 * Called to show the TaskPopupMenu.
	 */
	public void showTaskPopupMenu(TaskListItem task, Widget menuPartner) {
		// Are we tracking a popup that's been closed?
		if (null != m_closedPopupMenu) {
			// Yes!  Unregister it's registered event handlers.
			EventHelper.unregisterEventHandlers(m_closedPopupMenu.m_registeredEventHandlers);
			m_closedPopupMenu.m_registeredEventHandlers = null;
			m_closedPopupMenu = null;
		}
		
		// Register the events to be handled by this class...
		m_registeredEventHandlers = new ArrayList<HandlerRegistration>(); 
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this,
			m_registeredEventHandlers);
		
		// Remember the task and menu partner that we are dealing
		// with...
		m_task        = task;
		m_menuPartner = menuPartner;
		
		// ...make sure all the menu items are visible...
		for (VibeMenuItem mi:  m_menuItems) {
			mi.setVisible( true );
		}

		// ...and allow the popup to be closed if the parter item is
		// ...clicked on again...
		addAutoHidePartner(m_menuPartner.getElement());
		
		// ...and position and show the popup.
		showRelativeToTarget(menuPartner);
	}
	
	public void showTaskPopupMenu(Widget menuPartner) {
		// Always use the initial form of the method.
		showTaskPopupMenu(null, menuPartner);
	}
}
