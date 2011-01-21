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

import java.util.Comparator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.PassThroughEventsPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class that implements the Composite that contains the task folder
 * listing table.  
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class TaskTable extends Composite {
	private FlexTable	m_flexTable;	//
	private int			m_taskCount;	//
	private TaskListing	m_taskListing;	//
	
	private final GwtRpcServiceAsync				m_rpcService = GwtTeaming.getRpcService();				// 
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//

	/*
	 * Inner class to used to track information that's attached to a
	 * TaskListItem for managing the user interface. 
	 */
	private static class UIData {
		public CheckBox	m_cb;				//
		public int		m_taskDepth;		//
		public int 		m_taskOrder = (-1);	//
		
		/**
		 * Class constructor.
		 */
		UIData() {
			// Nothing to do.
		}
	}
	
	/**
	 * Class constructor.
	 */
	public TaskTable(TaskListing taskListing) {
		// Initialize the super class..
		super();
		
		// ...store the parameters...
		m_taskListing = taskListing;

		// ...create the FlexTable that's to hold everything...
		m_flexTable = new FlexTable();
		m_flexTable.addStyleName("gwtTaskList_objlist2");
		m_flexTable.setCellPadding(0);
		m_flexTable.setCellSpacing(0);

		// ...and use it to initialize the TaskTable Composite.
		super.initWidget(m_flexTable);
	}

	/*
	 * Defines the header row in the TaskTable.
	 */
	private void addHeaderRow() {
		// Add the style to the header row.
		m_flexTable.getRowFormatter().addStyleName(0, "columnhead");

		// Extract the table's CellFormatter.  We'll need it repeatedly
		// while generating the task.
		CellFormatter cf = m_flexTable.getCellFormatter();
		
		// Column 0:  Select all checkbox.
		final CheckBox cb = new CheckBox();
		cb.addStyleName("gwtTaskList_ckbox");
		PassThroughEventsPanel.addHandler(cb, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleSelectAll(cb.getValue());}			
		});
		cf.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(0, 0, cb);

		// Column 1:  Order.
		Anchor a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML("#");
		markAsSortKey(a, 1);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(1);}			
		});
		cf.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		m_flexTable.setWidget(0, 1, a);
		
		// Column 2:  Name.
		a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML(m_messages.taskColumn_name());
		markAsSortKey(a, 2);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(2);}			
		});
		m_flexTable.setWidget(0, 2, a);
		
		// Column 3:  Priority.
		a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML(m_messages.taskColumn_priority());
		markAsSortKey(a, 3);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(3);}			
		});
		m_flexTable.setWidget(0, 3, a);
		
		// Column 4:  Due Date.
		a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML(m_messages.taskColumn_dueDate());
		markAsSortKey(a, 4);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(4);}			
		});
		m_flexTable.setWidget(0, 4, a);
		
		// Column 5:  Status.
		a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML(m_messages.taskColumn_status());
		markAsSortKey(a, 5);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(5);}			
		});
		m_flexTable.setWidget(0, 5, a);
		
		// Column 6:  Assigned To.
		a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML(m_messages.taskColumn_assignedTo());
		markAsSortKey(a, 6);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(6);}			
		});
		m_flexTable.setWidget(0, 6, a);
		
		// Column 7:  Completed - % Done.
		a = new Anchor();
		a.addStyleName("sort-column");
		a.addStyleName("cursorPointer");
		a.getElement().setInnerHTML(m_messages.taskColumn_closedPercentDone());
		markAsSortKey(a, 7);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(7);}			
		});
		m_flexTable.setWidget(0, 7, a);		
	}
	
	/*
	 * Called to clear the contents of the TaskTable.
	 */
	public void clearTaskTable() {
		m_flexTable.removeAllRows();
		addHeaderRow();
	}

	/*
	 * Returns a string that represents the HTML of a Spacer image.
	 */
	private Image getSpacer() {
		Image reply = new Image(m_images.spacer());
		reply.setHeight("16px");
		reply.setWidth( "16px");
		return reply;
	}
	
	/**
	 * Returns the order number from the given TaskListItem.
	 * 
	 * @param tli
	 * 
	 * @return
	 */
	public static int getTaskOrder(TaskListItem tli) {
		return getUIData(tli).m_taskOrder;
	}

	/*
	 * Returns the UIData from the TaskListItem.
	 */
	private static UIData getUIData(TaskListItem tli) {
		UIData reply = ((UIData) tli.getUIData());
		if (null == reply) {
			reply = new UIData();
			tli.setUIData(reply);
		}
		return reply;
	}

	/*
	 * Called when the user clicks the select all checkbox.
	 */
	private void handleSelectAll(boolean checked) {
//!		...this needs to be implemented...
		Window.alert("handleSelectAll( " + checked + " ):  ...this needs to be implemented...");
	}

	/*
	 * Called to resort the TaskTable by the specified column.
	 */
	private static void handleTableResort(int column) {
//!		...this needs to be implemented...
		Window.alert("handleTableResort( " + column + " ):  ...this needs to be implemented...");
	}
	
	/*
	 * Called when the user clicks the expand/collapse on a task.
	 */
	private void handleTaskExpander(TaskListItem task) {
//!		... this needs to be implemented...
		Window.alert("handleTaskExpander( " + task.getTask().getTitle() + " ):  ...this needs to be implemented...");
	}
	
	/*
	 * Called when the user clicks the checkbox on a task.
	 */
	private void handleTaskSelect(TaskListItem task) {
//!		... this needs to be implemented...
		Window.alert("handleTaskSelect( " + task.getTask().getTitle() + " ):  ...this needs to be implemented...");
	}
	
	/*
	 * Called when the user clicks the seen sun burst on a task.
	 */
	private void handleTaskSeen(TaskListItem task) {
//!		... this needs to be implemented...
		Window.alert("handleTaskSeen( " + task.getTask().getTitle() + " ):  ...this needs to be implemented...");
	}
	
	/*
	 * Called to run the entry viewer on the given task.
	 */
	private void handleTaskView(final TaskInfo task) {
		m_rpcService.getViewFolderEntryUrl(HttpRequestInfo.createHttpRequestInfo(), task.getBinderId(), task.getTaskId(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(task.getTaskId()));
			}
			
			@Override
			public void onSuccess(String viewFolderEntryUrl) {
				m_taskListing.handleAction(
					TeamingAction.SHOW_FORUM_ENTRY,
					viewFolderEntryUrl);
			}
		});
	}
	
	/*
	 * If the TaskTable is sorted by the specified column, add the
	 * appropriate 'sorted by' indicator. 
	 */
	private void markAsSortKey(Anchor a, int col) {
//!		...this needs to be implemented...
		if (1 != col) {
			return;
		}
		
		Image i = new Image(m_taskListing.getSortDescend() ? m_images.sortZA() : m_images.sortAZ());
		Element ie = i.getElement();
		ie.setAttribute("align", "absmiddle");
		a.getElement().appendChild(ie);
	}
	
	/*
	 * Renders a TaskListItem into the TaskTable.
	 */
	private void renderTaskItem(final TaskListItem task) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		// Extract the table's CellFormatter.  We'll need it repeatedly
		// while generating the task.
		CellFormatter cf = m_flexTable.getCellFormatter();
		
		// Add the style to the header row.
		int row = m_flexTable.getRowCount();
		m_flexTable.getRowFormatter().addStyleName(0, "regrow");
				
		// Column 0:  Select checkbox.
		FlowPanel fp = new FlowPanel();
		final CheckBox cb = new CheckBox();
		uid.m_cb = cb;
		cb.addStyleName("gwtTaskList_ckbox");
		PassThroughEventsPanel.addHandler(cb, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTaskSelect(task);}			
		});
		fp.add(cb);
		if (0 < task.getSubtasks().size()) {
			Anchor a = new Anchor();
			a.addStyleName("cursorPointer");
			Image i = new Image(m_images.task_closer());
			a.getElement().appendChild(i.getElement());
			PassThroughEventsPanel.addHandler(a, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {handleTaskExpander(task);}				
			});
			fp.add(a);
		}
		else {
			fp.add(getSpacer());
		}
		cf.setWordWrap(row, 0, false);
		cf.setAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(row, 0, fp);
		
		// Column 1:  Order.
		String orderHTML = ((0 == uid.m_taskDepth) ? String.valueOf(uid.m_taskOrder) : "");
		m_flexTable.setHTML(row, 1, orderHTML);
		cf.setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		cf.setWidth(row, 1, "16px");

//!		...this needs to be implemented...
		
		// Column 2:  Task Name.
		fp = new FlowPanel();
		TaskInfo ti = task.getTask();
		boolean isSeen      = ti.getSeen();
		boolean isCompleted = (ti.getCompleted().equals("c100") || ti.getStatus().equals("s4"));
		Widget marker;
		if (isCompleted) {
			fp.addStyleName("gwtTaskList_task-strike");
			Image i = new Image(m_images.completed());
			i.setTitle(m_messages.taskAltTaskClosed());
			marker = i;
		}
		else if (isSeen) {
			final Anchor a = new Anchor();
			a.addStyleName("cursorPointer");
			Image i = new Image(m_images.unread());
			i.setTitle(m_messages.taskAltTaskUnread());
			a.getElement().appendChild(i.getElement());
			PassThroughEventsPanel.addHandler(a, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {handleTaskSeen(task);}
			});
			marker = a;
		}
		else {
			marker = getSpacer();
		}
		marker.addStyleName("gwtTaskList_task-icon");
		fp.add(marker);
		Anchor ta = new Anchor();
		ta.addStyleName("reg-entry1-a");
		ta.addStyleName("cursorPointer");
		PassThroughEventsPanel eventsPanel = new PassThroughEventsPanel(ta.getElement());
		eventsPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {handleTaskView(task.getTask());}});
		ta.getElement().setInnerHTML(task.getTask().getTitle());
		fp.add(ta);
		m_flexTable.setWidget(row, 2, fp);
		
		// Column 3:  Priority.
		m_flexTable.getFlexCellFormatter().setColSpan(row, 3, 5);
		m_flexTable.setWidget(row, 3, new InlineLabel("...this needs to be implemented..."));
	}

	/**
	 * Shows the tasks in the List<TaskListItem>.
	 * 
	 * Returns the time, in milliseconds, that it takes to show the tasks.
	 * 
	 * @param tasks
	 * 
	 * @return
	 */
	public long showTasks(List<TaskListItem> tasks) {
		// Save when we start.
		long start = System.currentTimeMillis();

		clearTaskTable();
		showTasksImpl(tasks, 0);		

		// Finally, return how long we took to show the tasks.
		long end = System.currentTimeMillis();
		return (end - start);
	}

	/*
	 * Shows the tasks in the List<TaskListItem> as being at a specific
	 * depth in the listing.
	 */
	private void showTasksImpl(List<TaskListItem> tasks, int taskDepth) {
		int taskOrder = 1;
		boolean baseTask = (0 == taskDepth);
		int subtaskDepth = (taskDepth + 1);
		for (TaskListItem task:  tasks) {
			// Construct and add a UIData object to the TaskListItem...
			UIData uid = new UIData();
			uid.m_taskDepth = taskDepth;
			if (baseTask) {
				uid.m_taskOrder = taskOrder;
				taskOrder += 1;
			}
			task.setUIData(uid);

			// ...render the task...
			renderTaskItem(task);

			// ...and render any subtasks.
			showTasksImpl(task.getSubtasks(), subtaskDepth);
		}
	}

	/*
	 * Sorts the List<TaskListItem> by column in the specified order.
	 */
	private void sortByColumn(List<TaskListItem> tasks, int col, boolean ascending) {
		Comparator<TaskListItem> comparator;
		switch(col) {
		default:
		case 1:  comparator = new TaskSorter.OrderComparator(            ascending); break;
		case 2:  comparator = new TaskSorter.NameComparator(             ascending); break;
		case 3:  comparator = new TaskSorter.PriorityComparator(         ascending); break;
		case 4:  comparator = new TaskSorter.DueDateComparator(          ascending); break;
		case 5:  comparator = new TaskSorter.StatusComparator(           ascending); break;
		case 6:  comparator = new TaskSorter.AssignedToComparator(       ascending); break;
		case 7:  comparator = new TaskSorter.ClosedPercentDoneComparator(ascending); break;		
		}
		TaskSorter.sort(tasks, comparator);
	}	
}
