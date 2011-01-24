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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Implements a GWT based task folder list user interface.
 * 
 * @author drfoster@novell.com
 */
public class TaskListing implements ActionHandler, ActionTrigger {
	private ActionHandler	m_actionHandler;	// Call to handle the TeamingActions generated by this TaskListing.
	private boolean			m_sortDescend;		// true -> Sort is descending.  false -> Sort is ascending. 
	private Element			m_taskListingDIV;	// The <DIV> in the content pane that's to contain the task listing.
	private Element			m_taskToolsDIV;		// The <DIV> in the content pane that's to contain the task tool bar.
	private Long			m_binderId;			// The ID of the binder containing the tasks to be listed.
	private String			m_filterType;		// The current filtering in affect, if any.
	private String			m_mode;				// The current mode being displayed (PHYSICAL vs. VITRUAL.)
	private String			m_sortBy;			// The column the tasks are currently sorted by.
	private TaskBundle		m_taskBundle;		// The TaskLinkage and List<TaskListItem> that we're listing.
	private TaskButton		m_deleteButton;		//
	private TaskButton		m_moveDownButton;	//
	private TaskButton		m_moveUpButton;		//
	private TaskButton		m_moveLeftButton;	//
	private TaskButton		m_moveRightButton;	//
	private TaskTable		m_taskTable;		//
	
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtRpcServiceAsync				m_rpcService = GwtTeaming.getRpcService();				// 
	private final GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//
	
	/**
	 * Class constructor.
	 * 
	 * @param taskToolsDIV
	 * @param taskListingDIV
	 * @param binderId
	 * @param filterType
	 * @param mode
	 * @param sortBy
	 * @param sortDescend
	 */
	public TaskListing(Element taskToolsDIV, Element taskListingDIV, ActionHandler actionHandler, Long binderId, String filterType, String mode, String sortBy, boolean sortDescend) {
		// Store the parameters...
		m_taskToolsDIV   = taskToolsDIV;
		m_taskListingDIV = taskListingDIV;
		m_actionHandler  = actionHandler;
		m_binderId       = binderId;
		m_filterType     = filterType;
		m_mode           = mode;
		m_sortBy         = sortBy;
		m_sortDescend    = sortDescend;
		
		// ...and construct the task toolbar.
		addTaskTools();
	}

	/*
	 * Adds the task tools (move up/down, delete, ...) to the task
	 * tools DIV.
	 * 
	 * Note that all the widgets are initially created as disabled.
	 * They'll be enabled, as appropriate, when the task listing is
	 * populated.
	 */
	private void addTaskTools() {
		// Remove anything currently in the task tools DIV...
		GwtClientHelper.removeAllChildren(m_taskToolsDIV);

		// ...create the order span...
		m_moveDownButton = new TaskButton(this, m_images.arrowDown(), m_images.arrowDownDisabled(), m_images.arrowDownMouseOver(), false, m_messages.taskAltMoveDown(), TeamingAction.TASK_MOVE_DOWN);
		m_moveUpButton   = new TaskButton(this, m_images.arrowUp(),   m_images.arrowUpDisabled(),   m_images.arrowUpMouseOver(),   false, m_messages.taskAltMoveUp(),   TeamingAction.TASK_MOVE_UP);
		InlineLabel il   = new InlineLabel(m_messages.taskLabelOrder());
		il.addStyleName("mediumtext");
		il.addStyleName("gwtTaskTools_Span");
		il.getElement().appendChild(m_moveUpButton.getElement());
		il.getElement().appendChild(m_moveDownButton.getElement());		
		m_taskToolsDIV.appendChild(il.getElement());

		// ...create the subtask span...
		m_moveLeftButton  = new TaskButton(this, m_images.arrowLeft(),  m_images.arrowLeftDisabled(),  m_images.arrowLeftMouseOver(),  false, m_messages.taskAltMoveLeft(),  TeamingAction.TASK_MOVE_LEFT);
		m_moveRightButton = new TaskButton(this, m_images.arrowRight(), m_images.arrowRightDisabled(), m_images.arrowRightMouseOver(), false, m_messages.taskAltMoveRight(), TeamingAction.TASK_MOVE_RIGHT);
		il = new InlineLabel(m_messages.taskLabelSubtask());
		il.addStyleName("mediumtext");
		il.addStyleName("gwtTaskTools_Span");
		il.getElement().appendChild(m_moveLeftButton.getElement());
		il.getElement().appendChild(m_moveRightButton.getElement());
		m_taskToolsDIV.appendChild(il.getElement());

		// ...and create the delete button.
		m_deleteButton = new TaskButton(
			this,
			m_messages.taskLabelDelete(),
			m_messages.taskAltDelete(),
			false,
			TeamingAction.TASK_DELETE);
		m_taskToolsDIV.appendChild(m_deleteButton.getElement());
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ActionHandler getActoinHandler()   {return m_actionHandler;  }
	public boolean       getSortDescend()     {return m_sortDescend;    }
	public Long          getBinderId()        {return m_binderId;       }
	public String        getFilterType()      {return m_filterType;     }
	public String        getMode()            {return m_mode;           }
	public String        getSortBy()          {return m_sortBy;         }
	public TaskBundle    getTaskBundle()      {return m_taskBundle;     }
	public TaskButton    getDeleteButton()    {return m_deleteButton;   }
	public TaskButton    getMoveDownButton()  {return m_moveDownButton; }
	public TaskButton    getMoveUpButton()    {return m_moveUpButton;   }
	public TaskButton    getMoveLeftButton()  {return m_moveLeftButton; }
	public TaskButton    getMoveRightButton() {return m_moveRightButton;}
	
	/**
	 * Handle actions directed to the task listing.
	 * 
	 * Implements the ActionHandler.handleAction() method.
	 * 
	 * @param action
	 * @param obj
	 */
	@Override
	public void handleAction(TeamingAction action, Object obj) {
		switch (action) {
		case TASK_DELETE:
		case TASK_MOVE_DOWN:
		case TASK_MOVE_LEFT:
		case TASK_MOVE_RIGHT:
		case TASK_MOVE_UP:
//!			...this needs to be implemented...
			Window.alert(action.toString() + ":  ...this needs to be implemented...");
			break;
			
		default:
			GwtTeaming.getMainPage().handleAction(action, obj);
			break;
		}
	}
	
	/*
	 * Renders a List<TaskListItem> into the task listing DIV.
	 */
	private void render() {
		boolean newTaskTable = (null == m_taskTable);
		if (newTaskTable) m_taskTable = new TaskTable(this);
		m_taskTable.showTasks(m_taskBundle);
		if (newTaskTable) m_taskListingDIV.appendChild(m_taskTable.getElement());
	}

	/**
	 * Show's the task listing based on the parameters passed into the
	 * constructor.
	 */
	public void show() {
		m_rpcService.getTaskBundle(HttpRequestInfo.createHttpRequestInfo(), m_binderId, m_filterType, m_mode, new AsyncCallback<TaskBundle>() {
			@Override
			public void onFailure(Throwable t) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskList();
				GwtClientHelper.handleGwtRPCFailure(t, error);
				
				// ...and display the error as the task listing.
				GwtClientHelper.removeAllChildren(m_taskListingDIV);
				m_taskListingDIV.appendChild(new InlineLabel(error).getElement());
			}

			@Override
			public void onSuccess(TaskBundle result) {
				// Clear the task listing DIV's contents and render the
				// task list.
				GwtClientHelper.removeAllChildren(m_taskListingDIV);
				m_taskBundle = result;
				render();
			}			
		});		
	}

	/**
	 * Fires a TeamingAction.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 *
	 * @param action
	 * @param obj
	 */
	@Override
	public void triggerAction(TeamingAction action) {
		handleAction(action, null);
	}
	
	public void triggerAction(TeamingAction action, Object obj) {
		handleAction(action, obj);
	}	
}
