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
import java.util.Comparator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.PassThroughEventsPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class that implements the Composite that contains the task folder
 * listing table.  
 * 
 * @author drfoster@novell.com
 */
public class TaskTable extends Composite {
	private FlexCellFormatter	m_flexTableCF;		//
	private FlexTable			m_flexTable;		//
	private int					m_taskCount;		//
	private RowFormatter		m_flexTableRF;		//
	private TaskBundle			m_taskBundle;		//
	private TaskListing			m_taskListing;		//
	private TaskPopupMenu		m_percentDoneMenu;	//
	private TaskPopupMenu		m_priorityMenu;		//
	private TaskPopupMenu		m_statusMenu;		//
	
	private final GwtRpcServiceAsync				m_rpcService = GwtTeaming.getRpcService();				// 
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//

	/*
	 * Enumeration value used to represent the order of the columns in
	 * the TaskTable.
	 */
	private enum Column {
		SELECTOR,
		ORDER,
		NAME,
		PRIORITY,
		DUE_DATE,
		STATUS,
		ASSIGNED_TO,
		CLOSED_PERCENT_DONE,
	}

	/*
	 * Inner class to used to track information attached to a
	 * TaskListItem for managing the user interface. 
	 */
	private static class UIData {
		private CheckBox	m_taskSelectorCB;	//
		private int			m_taskDepth;		//
		private int 		m_taskOrder = (-1);	//
		
		/**
		 * Class constructor.
		 */
		UIData() {
			// Nothing to do.
		}

		/**
		 * Get'er / Set'er methods.
		 * 
		 * @param
		 * 
		 * @return
		 */
		public CheckBox getTaskSelectorCB() {return m_taskSelectorCB;}
		public int      getTaskDepth()      {return m_taskDepth;     }
		public int      getTaskOrder()      {return m_taskOrder;     }
		
		public void setTaskSelectorCB(CheckBox taskSelectorCB) {m_taskSelectorCB = taskSelectorCB;}
		public void setTaskDepth(     int      taskDepth)      {m_taskDepth      = taskDepth;     }
		public void setTaskOrder(     int      taskOrder)      {m_taskOrder      = taskOrder;     }
		
		/**
		 * Returns true if the task corresponding to this UIData is
		 * selected (i.e., its checkbox is checked) and false
		 * otherwise.
		 * 
		 * @return
		 */
		public boolean isTaskSelected() {
			return jsIsCBChecked(getTaskSelectorCB().getElement().getId());
		}
		
		/**
		 * Selects the task corresponding to this UIData.
		 * 
		 * @param selected
		 */
		public void setTaskSelected(boolean selected) {
			jsSetCBCheck(getTaskSelectorCB().getElement().getId(), selected);
		}
	}
	
	/**
	 * Class constructor.
	 */
	public TaskTable(TaskListing taskListing) {
		// Initialize the super class..
		super();
		
		// ...store the parameter...
		m_taskListing = taskListing;
		
		// ...create the popup menus we'll need for the TaskTable.
		List<TaskMenuOption> pOpts = new ArrayList<TaskMenuOption>();
		pOpts.add(new TaskMenuOption("p1", m_images.p1(), m_messages.taskPriority_p1()));
		pOpts.add(new TaskMenuOption("p2", m_images.p2(), m_messages.taskPriority_p2()));
		pOpts.add(new TaskMenuOption("p3", m_images.p3(), m_messages.taskPriority_p3()));
		pOpts.add(new TaskMenuOption("p4", m_images.p4(), m_messages.taskPriority_p4()));
		pOpts.add(new TaskMenuOption("p5", m_images.p5(), m_messages.taskPriority_p5()));
		m_priorityMenu = new TaskPopupMenu(this, TeamingAction.TASK_SET_PRIORITY, pOpts);

		List<TaskMenuOption> sOpts = new ArrayList<TaskMenuOption>();
		sOpts.add(new TaskMenuOption("s3", m_images.completed(),   m_messages.taskStatus_completed()));
		sOpts.add(new TaskMenuOption("s2", m_images.inProcess(),   m_messages.taskStatus_inProcess()));
		sOpts.add(new TaskMenuOption("s1", m_images.needsAction(), m_messages.taskStatus_needsAction()));
		sOpts.add(new TaskMenuOption("s4", m_images.cancelled(),   m_messages.taskStatus_cancelled()));
		m_statusMenu = new TaskPopupMenu(this, TeamingAction.TASK_SET_STATUS, sOpts);

		List<TaskMenuOption> pdOpts = new ArrayList<TaskMenuOption>();
		pdOpts.add(new TaskMenuOption("c000", m_images.c0(),   m_messages.taskCompleted_c0()));
		pdOpts.add(new TaskMenuOption("c010", m_images.c10(),  m_messages.taskCompleted_c10()));
		pdOpts.add(new TaskMenuOption("c020", m_images.c20(),  m_messages.taskCompleted_c20()));
		pdOpts.add(new TaskMenuOption("c030", m_images.c30(),  m_messages.taskCompleted_c30()));
		pdOpts.add(new TaskMenuOption("c040", m_images.c40(),  m_messages.taskCompleted_c40()));
		pdOpts.add(new TaskMenuOption("c050", m_images.c50(),  m_messages.taskCompleted_c50()));
		pdOpts.add(new TaskMenuOption("c060", m_images.c60(),  m_messages.taskCompleted_c60()));
		pdOpts.add(new TaskMenuOption("c070", m_images.c70(),  m_messages.taskCompleted_c70()));
		pdOpts.add(new TaskMenuOption("c080", m_images.c80(),  m_messages.taskCompleted_c80()));
		pdOpts.add(new TaskMenuOption("c090", m_images.c90(),  m_messages.taskCompleted_c90()));
		pdOpts.add(new TaskMenuOption("c100", m_images.c100(), m_messages.taskCompleted_c100()));
		m_percentDoneMenu = new TaskPopupMenu(this, TeamingAction.TASK_SET_PERCENT_DONE, pdOpts);

		// ...create the FlexTable that's to hold everything...
		m_flexTable   = new FlexTable();
		m_flexTableCF = m_flexTable.getFlexCellFormatter();
		m_flexTableRF = m_flexTable.getRowFormatter();
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
		m_flexTableRF.addStyleName(0, "columnhead");

		// Column 0:  Select all checkbox.
		final CheckBox cb = new CheckBox();
		cb.addStyleName("gwtTaskList_ckbox");
		cb.getElement().setId("gwtTaskList_taskSelect_All");
		PassThroughEventsPanel.addHandler(cb, new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {handleSelectAll(jsIsCBChecked("gwtTaskList_taskSelect_All"));}			
		});
		m_flexTableCF.setAlignment(0, Column.SELECTOR.ordinal(), HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(0, Column.SELECTOR.ordinal(), cb);

		// Column 1:  Order.
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML("#");
		markAsSortKey(a, Column.ORDER);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.ORDER);}			
		});
		m_flexTableCF.setHorizontalAlignment(0, Column.ORDER.ordinal(), HasHorizontalAlignment.ALIGN_CENTER);
		m_flexTable.setWidget(0, Column.ORDER.ordinal(), a);
		
		// Column 2:  Task Name.
		a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_name());
		markAsSortKey(a, Column.NAME);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.NAME);}			
		});
		m_flexTable.setWidget(0, Column.NAME.ordinal(), a);
		
		// Column 3:  Priority.
		a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_priority());
		markAsSortKey(a, Column.PRIORITY);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.PRIORITY);}			
		});
		m_flexTable.setWidget(0, Column.PRIORITY.ordinal(), a);
		
		// Column 4:  Due Date.
		a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_dueDate());
		markAsSortKey(a, Column.DUE_DATE);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.DUE_DATE);}			
		});
		m_flexTable.setWidget(0, Column.DUE_DATE.ordinal(), a);
		
		// Column 5:  Status.
		a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_status());
		markAsSortKey(a, Column.STATUS);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.STATUS);}			
		});
		m_flexTable.setWidget(0, Column.STATUS.ordinal(), a);
		
		// Column 6:  Assigned To.
		a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_assignedTo());
		markAsSortKey(a, Column.ASSIGNED_TO);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.ASSIGNED_TO);}			
		});
		m_flexTable.setWidget(0, Column.ASSIGNED_TO.ordinal(), a);
		
		// Column 7:  Closed - % Done.
		a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_closedPercentDone());
		markAsSortKey(a, Column.CLOSED_PERCENT_DONE);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.CLOSED_PERCENT_DONE);}			
		});
		m_flexTable.setWidget(0, Column.CLOSED_PERCENT_DONE.ordinal(), a);		
	}

	/*
	 * Returns a base Anchor widget.
	 */
	private Anchor buildAnchor(List<String> styles) {
		Anchor reply = new Anchor();
		for (String style:  styles) {
			reply.addStyleName(style);
		}
		return reply;
	}
	
	private Anchor buildAnchor(String style) {
		List<String> styles = new ArrayList<String>();
		styles.add(style);
		if (!(style.equals("cursorPointer"))) {
			styles.add("cursorPointer");
		}
		return buildAnchor(styles);
	}
	
	private Anchor buildAnchor() {
		return buildAnchor("cursorPointer");
	}

	/*
	 * Returns a base Image widget.
	 */
	private Image buildImage(ImageResource res, String title) {
		Image reply = new Image(res);
		reply.getElement().setAttribute("align", "absmiddle");
		if (GwtClientHelper.hasString(title)) {
			reply.setTitle(title);
		}
		return reply;
	}
	
	private Image buildImage(ImageResource res) {
		return buildImage(res, null);
	}
	
	/*
	 * Build a column that contains a TaskPopupMenu <SELECT> widget. 
	 */
	private Anchor buildOptionColumnAnchor(final TaskListItem task, final TaskPopupMenu taskMenu, String optionValue, String anchorStyle) {
		// What image do we display for this task?
		List<TaskMenuOption> taskOptions = taskMenu.getMenuOptions();
		TaskMenuOption selectedOption = null;
		for (TaskMenuOption taskOption:  taskOptions) {
			if (taskOption.getMenu().equals(optionValue)) {
				selectedOption = taskOption;
				break;
			}
		}
		if (null == selectedOption) {
			selectedOption = taskOptions.get(0);
		}
		Image img = buildImage(selectedOption.getMenuImageRes(), selectedOption.getMenuAlt());
		final Element imgElement = img.getElement();

		// Generate the Anchor for this option.
		Anchor reply = buildAnchor(anchorStyle);
		reply.getElement().appendChild(imgElement);
		reply.getElement().appendChild(buildImage(m_images.menu()).getElement());
		PassThroughEventsPanel.addHandler(reply, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				taskMenu.showTaskPopupMenu(task, imgElement);
			}
		});

		// If we get here, reply refers to the Anchor for this option
		// column.  Return it.
		return reply;
	}
	
	/*
	 * Returns a spacer Image.
	 */
	private Image buildSpacer() {
		Image reply = new Image(m_images.spacer());
		reply.setHeight("16px");
		reply.setWidth( "16px");
		return reply;
	}
	
	/*
	 * Called to clear the contents of the TaskTable.
	 */
	public void clearTaskTable() {
		m_flexTable.removeAllRows();
		addHeaderRow();
	}

	/**
	 * Returns the order number from the given TaskListItem.
	 * 
	 * @param task
	 * 
	 * @return
	 */
	public static int getTaskOrder(TaskListItem task) {
		return getUIData(task).m_taskOrder;
	}

	/*
	 * Returns a List<TaskListItem> of the tasks in the TaskTable that
	 * are currently checked.
	 */
	private List<TaskListItem> getTasksChecked() {
		List<TaskListItem> reply = new ArrayList<TaskListItem>();;
		getTasksCheckedImpl(m_taskBundle.getTasks(), reply);
		return reply;
	}
	
	private void getTasksCheckedImpl(List<TaskListItem> tasks, List<TaskListItem> checkedTasks) {
		for (TaskListItem task:  tasks) {
			if (getUIData(task).isTaskSelected()) {
				checkedTasks.add(task);
			}
			getTasksCheckedImpl(task.getSubtasks(), checkedTasks);
		}
	}
	
	/*
	 * Returns the UIData from the TaskListItem.
	 */
	private static UIData getUIData(TaskListItem task) {
		UIData reply = ((UIData) task.getUIData());
		if (null == reply) {
			reply = new UIData();
			task.setUIData(reply);
		}
		return reply;
	}

	/**
	 * Called by TaskPopupMenu when a selection has been made in one of
	 * the task's option menus.
	 * 
	 * @param task
	 * @param taskAction
	 * @param optionValue
	 */
	public void setTaskOption(TaskListItem task, TeamingAction taskAction, String optionValue) {
//!		...this needs to be implemented...
		Window.alert("setTaskOption( " + taskAction + ":" + task.getTask().getTitle() + ":" + optionValue + " )");
	}
	
	/*
	 * Called when the user clicks the select all checkbox.
	 */
	private void handleSelectAll(Boolean checked) {
		// Perform the selection and validate the TaskListing tools.
		selectAllTasks(checked);
		validateTaskTools();
	}

	/*
	 * Called to resort the TaskTable by the specified column.
	 */
	private static void handleTableResort(Column col) {
//!		...this needs to be implemented...
		Window.alert("handleTableResort( " + col.ordinal() + " ):  ...this needs to be implemented...");
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
		// Simply validate the TaskListing tools.
		validateTaskTools();
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
	private void handleTaskView(TaskListItem task) {
		final TaskInfo ti = task.getTask();
		m_rpcService.getViewFolderEntryUrl(HttpRequestInfo.createHttpRequestInfo(), ti.getBinderId(), ti.getTaskId(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(ti.getTaskId()));
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
	 * Returns true if the checkbox Element is checked and false
	 * otherwise.
	 */
	private static native boolean jsIsCBChecked(String cbID) /*-{
		var cbE = $wnd.top.gwtContentIframe.document.getElementById(cbID).firstChild;
		return cbE.checked;
	}-*/;
	
	/*
	 * Checks or removes the check from a checkbox Element.
	 */
	private static native void jsSetCBCheck(String cbID, boolean selected) /*-{
		var cbE = $wnd.top.gwtContentIframe.document.getElementById(cbID).firstChild;
		cbE.checked = selected;
	}-*/;
	
	/*
	 * If the TaskTable is sorted by the specified column, add the
	 * appropriate 'sorted by' indicator. 
	 */
	private void markAsSortKey(Anchor a, Column col) {
//!		...this needs to be implemented...
		if (Column.SELECTOR != col) {
			return;
		}
		
		Image i = buildImage(m_taskListing.getSortDescend() ? m_images.sortZA() : m_images.sortAZ());
		a.getElement().appendChild(i.getElement());
	}
	
	/*
	 * Renders the 'Assigned To' column.
	 */
	private void renderColumnAssignedTo(final TaskListItem task, int row, Column col) {
//!		...this needs to be implemented...
		m_flexTable.setWidget(row, col.ordinal(), new InlineLabel("...to do..."));
	}
	
	/*
	 * Renders the 'Closed - % Done' column.
	 */
	private void renderColumnClosedPercentDone(final TaskListItem task, int row, Column col) {
		// What's the current priority of this task?
		String percentDone = task.getTask().getCompleted();
		if (!(GwtClientHelper.hasString(percentDone))) {
			percentDone = "c000";
		}
		
		else if (percentDone.equals("c100")) {
			String completedDateDisplay = task.getTask().getCompletedDateDisplay();
			if (GwtClientHelper.hasString(completedDateDisplay)) {
				InlineLabel il = new InlineLabel(completedDateDisplay);
				il.setWordWrap(false);
				m_flexTable.setWidget(row, col.ordinal(), il);				
				return;
			}
		}
		
		// Add an Anchor for it to the TaskTable.
		m_flexTable.setWidget(
			row,
			col.ordinal(),
			buildOptionColumnAnchor(
				task,
				m_percentDoneMenu,
				percentDone,
				"percent-done"));
	}
	
	/*
	 * Renders the 'Due Date' column.
	 */
	private void renderColumnDueDate(final TaskListItem task, int row, Column col) {
		InlineLabel il = new InlineLabel(task.getTask().getEvent().getLogicalEndDisplay());
		il.setWordWrap(false);
		m_flexTable.setWidget(row, col.ordinal(), il);
	}
	
	/*
	 * Renders the 'Order'.
	 */
	private void renderColumnOrder(final TaskListItem task, int row, Column col) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		String orderHTML = ((0 == uid.getTaskDepth()) ? String.valueOf(uid.getTaskOrder()) : "");
		m_flexTable.setHTML(row, col.ordinal(), orderHTML);
		m_flexTableCF.setHorizontalAlignment(row, col.ordinal(), HasHorizontalAlignment.ALIGN_CENTER);
		m_flexTableCF.setWidth(row, col.ordinal(), "16px");
	}

	/*
	 * Renders the 'Priority' column.
	 */
	private void renderColumnPriority(final TaskListItem task, int row, Column col) {
		// What's the current priority of this task?
		String priority = task.getTask().getPriority();
		if (!(GwtClientHelper.hasString(priority))) {
			priority = "p1";
		}
		
		// Add an Anchor for it to the TaskTable.
		m_flexTable.setWidget(
			row,
			col.ordinal(),
			buildOptionColumnAnchor(
				task,
				m_priorityMenu,
				priority,
				"priority-icon"));
	}
	
	/*
	 * Renders the 'Select CheckBox' column.
	 */
	private void renderColumnSelectCB(final TaskListItem task, int row, Column col) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		CheckBox cb = new CheckBox();
		uid.setTaskSelectorCB(cb);
		cb.getElement().setId("gwtTaskList_taskSelect_" + task.getTask().getTaskId());
		cb.addStyleName("gwtTaskList_ckbox");
		PassThroughEventsPanel.addHandler(cb, new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {handleTaskSelect(task);}			
		});
		FlowPanel fp = new FlowPanel();
		fp.add(cb);
		if (0 < task.getSubtasks().size()) {
			Anchor a = buildAnchor();
			Image  i = buildImage(m_images.task_closer());
			a.getElement().appendChild(i.getElement());
			PassThroughEventsPanel.addHandler(a, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {handleTaskExpander(task);}				
			});
			fp.add(a);
		}
		else {
			fp.add(buildSpacer());
		}
		m_flexTableCF.setWordWrap( row, col.ordinal(), false);
		m_flexTableCF.setAlignment(row, col.ordinal(), HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(     row, col.ordinal(), fp);
	}

	/*
	 * Renders the 'Status' column.
	 */
	private void renderColumnStatus(final TaskListItem task, int row, Column col) {
		// What's the current priority of this task?
		String status = task.getTask().getStatus();
		if (!(GwtClientHelper.hasString(status))) {
			status = "s1";
		}
		
		// Add an Anchor for it to the TaskTable.
		m_flexTable.setWidget(
			row,
			col.ordinal(),
			buildOptionColumnAnchor(
				task,
				m_statusMenu,
				status,
				"status-icon"));
	}
	
	/*
	 * Renders the 'Task Name' column.
	 */
	private void renderColumnTaskName(final TaskListItem task, int row, Column col) {
		// Is the task unseen, cancelled and/or closed?
		TaskInfo ti = task.getTask();
		boolean isUnseen    = (!(ti.getSeen()));
		boolean isCancelled =    ti.getStatus().equals("s4");
		boolean isClosed    =   (ti.getCompleted().equals("c100") || isCancelled);
		
		// Define a panel to contain the task name widgets.
		FlowPanel fp = new FlowPanel();

		// Add the closed/unseen marker Widget to the panel.
		Widget marker;
		if (isClosed) {
			fp.addStyleName("gwtTaskList_task-strike");
			Image i = buildImage(m_images.completed(), m_messages.taskAltTaskClosed());
			marker = i;
		}
		else if (isUnseen) {
			final Anchor a = buildAnchor();
			Image i = buildImage(m_images.unread(), m_messages.taskAltTaskUnread());
			a.getElement().appendChild(i.getElement());
			PassThroughEventsPanel.addHandler(a, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {handleTaskSeen(task);}
			});
			marker = a;
		}
		else {
			marker = buildSpacer();
		}
		marker.addStyleName("gwtTaskList_task-icon");
		fp.add(marker);
		
		// Add the appropriately styled task name Anchor to the panel.
		Anchor ta = buildAnchor();
		PassThroughEventsPanel eventsPanel = new PassThroughEventsPanel(ta.getElement());
		eventsPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {handleTaskView(task);}
		});
		String html = task.getTask().getTitle();
		if (isUnseen)    html = ("<b>" + html + "</b>");				// Unseen:     Bold.
		if (isCancelled) m_flexTableRF.addStyleName(row, "disabled");	// Cancelled:  Gray.
		ta.getElement().setInnerHTML(html);
		fp.add(ta);
		m_flexTable.setWidget(row, col.ordinal(), fp);
	}

	/*
	 * Renders a TaskListItem into the TaskTable.
	 */
	private void renderTaskItem(final TaskListItem task) {		
		// Add the style to the row...
		int row = m_flexTable.getRowCount();
		m_flexTableRF.addStyleName(row, "regrow");
				
		// ...and render the columns.
		renderColumnSelectCB(         task, row, Column.SELECTOR           );
		renderColumnOrder(            task, row, Column.ORDER              );
		renderColumnTaskName(         task, row, Column.NAME               );
		renderColumnPriority(         task, row, Column.PRIORITY           );		
		renderColumnDueDate(          task, row, Column.DUE_DATE           );		
		renderColumnStatus(           task, row, Column.STATUS             );		
		renderColumnAssignedTo(       task, row, Column.ASSIGNED_TO        );		
		renderColumnClosedPercentDone(task, row, Column.CLOSED_PERCENT_DONE);		
	}

	/*
	 * Checks or removes the check from all the tasks in the TaskTable.
	 */
	private void selectAllTasks(boolean select) {
		selectAllTasksImpl(m_taskBundle.getTasks(), select);
	}
	
	private void selectAllTasksImpl(List<TaskListItem> tasks, boolean selected) {
		for (TaskListItem task:  tasks) {
			getUIData(task).setTaskSelected(selected);
			selectAllTasksImpl(task.getSubtasks(), selected);
		}
	}
	
	/**
	 * Shows the tasks in the List<TaskListItem>.
	 * 
	 * Returns the time, in milliseconds, that it took to show them.
	 * 
	 * @param taskBundle
	 * 
	 * @return
	 */
	public long showTasks(TaskBundle taskBundle) {
		// Save when we start...
		long start = System.currentTimeMillis();

		// ...and render the TaskTable.
		clearTaskTable();
		m_taskBundle = taskBundle;
		List<TaskListItem> tasks = taskBundle.getTasks();
		
		// Are there any tasks to show?
		if (tasks.isEmpty()) {
			// No!  Add a message saying there are no tasks.
			int row = m_flexTable.getRowCount();
			m_flexTableCF.setColSpan(row, 0, 8);
			m_flexTableCF.addStyleName(row, 0, "paddingTop10px");
			InlineLabel il = new InlineLabel(m_messages.taskNoTasks());
			il.addStyleName("wiki-noentries-panel");
			m_flexTable.setWidget(row, 0, il);
		}
		else {
			// Yes, there any tasks to show!
			showTasksImpl(tasks, 0);
		}

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
			UIData uid = getUIData(task);
			uid.setTaskDepth(taskDepth);
			if (baseTask) {
				uid.setTaskOrder(taskOrder);
				taskOrder += 1;
			}
			else {
				uid.setTaskOrder(-1);
			}
			task.setUIData(uid);

			// ...render the task and any subtasks.
			renderTaskItem(task);
			showTasksImpl( task.getSubtasks(), subtaskDepth);
		}
	}

	/*
	 * Sorts the List<TaskListItem> by column in the specified order.
	 */
	private void sortByColumn(List<TaskListItem> tasks, Column col, boolean sortAscending) {
		Comparator<TaskListItem> comparator;
		switch(col) {
		default:
		case ORDER:                comparator = new TaskSorter.OrderComparator(            sortAscending); break;
		case NAME:                 comparator = new TaskSorter.NameComparator(             sortAscending); break;
		case PRIORITY:             comparator = new TaskSorter.PriorityComparator(         sortAscending); break;
		case DUE_DATE:             comparator = new TaskSorter.DueDateComparator(          sortAscending); break;
		case STATUS:               comparator = new TaskSorter.StatusComparator(           sortAscending); break;
		case ASSIGNED_TO:          comparator = new TaskSorter.AssignedToComparator(       sortAscending); break;
		case CLOSED_PERCENT_DONE:  comparator = new TaskSorter.ClosedPercentDoneComparator(sortAscending); break;		
		}
		TaskSorter.sort(tasks, comparator);
	}

	/*
	 * Based on what's selected in the task list, validates the tools
	 * in the TaskListing.
	 */
	private void validateTaskTools() {
		// Get the checked tasks and count.
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();

		boolean enableDelete = (0 < tasksCheckedCount);
		boolean enableMoveDown;
		boolean enableMoveLeft;
		boolean enableMoveRight;
		boolean enableMoveUp;

		// Validate the the base criteria about whether the movement
		// buttons are enabled.  Is one and only one task selected?
		boolean allowMovement = (1 == tasksCheckedCount);
		if (allowMovement) {
			// Yes!  Is this list an non-filtered list of the tasks
			// from the folder?
			allowMovement = ((!(m_taskBundle.getIsFiltered())) && m_taskBundle.getIsFromFolder());
			if (allowMovement) {
				// Yes!  Are we sorted on the order column?
//!				...this needs to be implemented...
			}
		}			

		// Is the base criteria for movement satisfied?
		if (allowMovement) {
			// Yes!  Furthermore, the allowed movement is based on the
			// selected task's current position in the linkage.
			Long        taskId = tasksChecked.get(0).getTask().getTaskId();
			TaskLinkage tl     = m_taskBundle.getTaskLinkage();
			enableMoveDown  = tl.canMoveTaskDown( taskId);
			enableMoveLeft  = tl.canMoveTaskLeft( taskId);
			enableMoveRight = tl.canMoveTaskRight(taskId);
			enableMoveUp    = tl.canMoveTaskUp(   taskId);
		}
		else {
			// No, the base criteria is not satisfied!  All the
			// movement buttons are disabled.
			enableMoveDown  =
			enableMoveLeft  =
			enableMoveRight =
			enableMoveUp    = false;
		}
		
		// Enabled/disable the buttons as calculated.
		m_taskListing.getDeleteButton().setEnabled(   enableDelete   );
		m_taskListing.getMoveDownButton().setEnabled( enableMoveDown );
		m_taskListing.getMoveLeftButton().setEnabled( enableMoveLeft );
		m_taskListing.getMoveRightButton().setEnabled(enableMoveRight);
		m_taskListing.getMoveUpButton().setEnabled(   enableMoveUp   );
	}
}
