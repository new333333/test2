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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.EventWrapper;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Implements a GWT based task folder list user interface.
 * 
 * @author drfoster@novell.com
 */
public class TaskListing extends Composite implements ActionTrigger {
	public static RequestInfo m_requestInfo;			//
	
	private boolean			m_updateCalculatedDates;	// true -> Tell the task table to update the calculated dates upon loading.
	private boolean			m_showModeSelect;			// true -> Show the 'All Entries vs. From Folder' options.  false -> Don't.
	private boolean			m_sortDescend;				// true -> Sort is descending.  false -> Sort is ascending. 
	private FlowPanel		m_taskListingDIV;			// The <DIV> in the content pane that's to contain the task listing.
	private FlowPanel		m_taskRootDIV;				// The <DIV> in the content pane that's to contain the task tool bar.
	private FlowPanel		m_taskToolsDIV;				// The <DIV> in the content pane that's to contain the task tool bar.
	private InlineLabel		m_hintSpan;					// The <SPAN> containing the reason why the movement buttons are disabled.
	private InlineLabel		m_pleaseWaitLabel;			//
	private Long			m_binderId;					// The ID of the binder containing the tasks to be listed.
	private String			m_filterType;				// The current filtering in affect, if any.
	private String			m_mode;						// The current mode being displayed (PHYSICAL vs. VITRUAL.)
	private String			m_sortBy;					// The column the tasks are currently sorted by.
	private TaskBundle		m_taskBundle;				// The TaskLinkage and List<TaskListItem> that we're listing.
	private TaskButton		m_deleteButton;				//
	private TaskButton		m_moveDownButton;			//
	private TaskButton		m_moveUpButton;				//
	private TaskButton		m_moveLeftButton;			//
	private TaskButton		m_moveRightButton;			//
	private TaskButton		m_purgeButton;				//
	private TaskPopupMenu	m_viewMenu;					//
	private TaskTable		m_taskTable;				//
	
	private final GwtRpcServiceAsync				m_rpcService = GwtTeaming.getRpcService();				// 
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//
	
	private final int FOOTER_FUDGE         = 45;	// Space allowed for the task folder's footer.
	private final int TASK_LISTING_MINIMUM = 50;	// Minimum size for the task listing <DIV>.  Below this, and it reverts to '100%'.
	
	/**
	 * Class constructor.
	 */
	public TaskListing() {
		super();
		
		// Initialize the JSNI components...
		jsInitResizeHandler(this);		
		m_requestInfo = jsGetRequestInfo();
		
		// ...extract the parameters we need to render the tasks...
		m_binderId              = Long.parseLong(m_requestInfo.getBinderId());
		m_filterType            =                 jsGetElementValue("ssCurrentTaskFilterType");
		m_mode                  =                 jsGetElementValue("ssCurrentFolderModeType");
		m_sortBy                =                 jsGetElementValue("ssFolderSortBy"         );
		m_sortDescend           = Boolean.valueOf(jsGetElementValue("ssFolderSortDescend"   ));
		m_updateCalculatedDates = Boolean.valueOf(jsGetElementValue("updateCalculatedDates" ));
		
		String showMode = jsGetElementValue("ssShowFolderModeSelect");
		m_showModeSelect = (GwtClientHelper.hasString(showMode) && Boolean.valueOf(showMode));

		// ...create the panels that are to contain the task tools and
		// ...listing...
		m_taskRootDIV  = new FlowPanel();		
		m_taskToolsDIV = new FlowPanel();
		m_taskToolsDIV.addStyleName("gwtTaskTools");
		m_taskToolsDIV.getElement().setId("ss_gwtTaskToolsDIV");
		m_taskRootDIV.add(m_taskToolsDIV);		
		m_taskListingDIV = new FlowPanel();
		m_taskListingDIV.addStyleName("gwtTaskListing");
		m_taskListingDIV.getElement().setId("ss_gwtTaskListingDIV");
		FlowPanel pleaseWaitPanel = new FlowPanel();
		pleaseWaitPanel.addStyleName("wiki-noentries-panel gwtTaskList_loading");
		m_pleaseWaitLabel = new InlineLabel(m_messages.taskPleaseWait_Loading());
		pleaseWaitPanel.add(m_pleaseWaitLabel);
		Image busyImg = new Image(m_images.busyAnimation());
		busyImg.getElement().setAttribute("align", "absmiddle");
		pleaseWaitPanel.add(busyImg);
		m_taskListingDIV.add(pleaseWaitPanel);
		m_taskRootDIV.add(m_taskListingDIV);
		
		// ...populate the task panels...
		Scheduler.ScheduledCommand populateCommand;
		populateCommand = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				populateTaskDIVs();
			}
		};
		Scheduler.get().scheduleDeferred(populateCommand);

		// ...and tell the Composite that we're good to go.
		initWidget(m_taskRootDIV);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean     getSortDescend()     {return m_sortDescend;    }
	public FlowPanel   getTaskListingDIV()  {return m_taskListingDIV; }
	public FlowPanel   getTaskRootDIV()     {return m_taskRootDIV;    }
	public FlowPanel   getTaskToolsDIV()    {return m_taskToolsDIV;   }
	public InlineLabel getHintSpan()        {return m_hintSpan;       }
	public Long        getBinderId()        {return m_binderId;       }
	public RequestInfo getRequestInfo()     {return m_requestInfo;    }
	public String      getFilterType()      {return m_filterType;     }
	public String      getMode()            {return m_mode;           }
	public String      getSortBy()          {return m_sortBy;         }
	public TaskBundle  getTaskBundle()      {return m_taskBundle;     }
	public TaskButton  getDeleteButton()    {return m_deleteButton;   }
	public TaskButton  getMoveDownButton()  {return m_moveDownButton; }
	public TaskButton  getMoveUpButton()    {return m_moveUpButton;   }
	public TaskButton  getMoveLeftButton()  {return m_moveLeftButton; }
	public TaskButton  getMoveRightButton() {return m_moveRightButton;}
	public TaskButton  getPurgeButton()     {return m_purgeButton;    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setTaskBundle(TaskBundle taskBundle) {m_taskBundle = taskBundle;}

	/*
	 * Called when the user changes the selection in the view menu.
	 */
	private void handleViewOption(String viewOption) {
		// If the menu is checked...
		TaskMenuOption tmo = TaskMenuOption.getTMOFromList(viewOption, m_viewMenu.getMenuOptions());
		if (null == tmo) {
			Window.alert(m_messages.taskInternalError_UnexpectedViewOption(viewOption));
			return;
		}
		if (tmo.isMenuChecked()) {
			// ...the selected view option is already active so we
			// ...really don't have to do anything.
			return;
		}

		// What operand to we use to change the view option?
		String operand;
		if (viewOption.equals(    "ALL"   ) || 
			    viewOption.equals("CLOSED") ||
			    viewOption.equals("DAY"   ) ||
			    viewOption.equals("WEEK"  ) ||
			    viewOption.equals("MONTH" ) ||
			    viewOption.equals("ACTIVE")) {
			operand = "ssTaskFilterType";
		}
		
		else if (viewOption.equals("VIRTUAL" ) ||
				viewOption.equals( "PHYSICAL")) {
			operand = "ssFolderModeType";
		}
		
		else {
			Window.alert(m_messages.taskInternalError_UnexpectedViewOption(viewOption));
			return;
		}
		
		// Put the view option into affect.
		String url = m_requestInfo.getAdaptedUrl();
		url = GwtClientHelper.replace(url, "xxx_operand_xxx", operand);
		url = GwtClientHelper.replace(url, "xxx_option_xxx",  viewOption);
		GwtClientHelper.jsLoadUrlInCurrentWindow(url);
	}
	
	/**
	 * Hides the hint <SPAN>, if visible.
	 */
	public void hideHint() {
		// If the hint <SPAN> is visible...
		if (m_hintSpan.isVisible()) {
			// ...empty and hide it.
			m_hintSpan.setText("");
			m_hintSpan.addStyleName("gwtTaskTools_HintSpan_Hidden");
		}
	}
	
	/*
	 * Uses JSNI to grab the JavaScript object that holds the
	 * information about the request dealing with.
	 */
	private native RequestInfo jsGetRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.m_requestInfo;
	}-*/;

	/*
	 * Uses JSNI to return the value of a document element.
	 */
	private native String jsGetElementValue(String eId) /*-{
		return $doc.getElementById(eId).value;
	}-*/;
	
	/*
	 * Called to create a JavaScript method that will be invoked when
	 * the content area resizes.
	 */
	private native void jsInitResizeHandler(TaskListing taskListing) /*-{
		$wnd.ss_resizeTasks = function() {
			taskListing.@org.kablink.teaming.gwt.client.tasklisting.TaskListing::resize()();
		}
	}-*/;

	/*
	 * Populates the <DIV>'s for the task tools and listing.
	 */
	private void populateTaskDIVs() {
		populateTaskToolsDIV();
		populateTaskListingDIV();
	}

	/*
	 * Adds the task listing widgets to the task listing DIV.
	 */
	private void populateTaskListingDIV() {
		// Resize the TaskListing <DIV> to the content panel...
		resizeNow();
		
		// ...and display the tasks in it.
		final long start = System.currentTimeMillis();
		m_rpcService.getTaskBundle(HttpRequestInfo.createHttpRequestInfo(), m_binderId, m_filterType, m_mode, new AsyncCallback<TaskBundle>() {
			@Override
			public void onFailure(Throwable t) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskList();
				GwtClientHelper.handleGwtRPCFailure(t, error);
				
				// ...and display the error as the task listing.
				m_taskListingDIV.clear();
				m_taskListingDIV.add(new InlineLabel(error));
			}

			@Override
			public void onSuccess(TaskBundle result) {
				// Clear the task listing DIV's contents and render the
				// task list.
				long end = System.currentTimeMillis();
				m_pleaseWaitLabel.setText(m_messages.taskPleaseWait_Rendering());
				m_taskBundle = result;
				showTaskBundle(end - start);
			}			
		});		
	}
	
	/*
	 * Adds the task tools (move up/down, delete, purge, ...) to the
	 * task tools DIV.
	 * 
	 * Note that all the widgets are initially created as disabled.
	 * They'll be enabled, as appropriate, when the task listing is
	 * populated.
	 */
	private void populateTaskToolsDIV() {
		// Remove anything currently in the task tools DIV...
		m_taskToolsDIV.clear();

		// ...create the order span...
		FlowPanel fp = new FlowPanel();
		m_moveUpButton   = new TaskButton(this, m_images.arrowUp(),   m_images.arrowUpDisabled(),   m_images.arrowUpMouseOver(),   false, m_messages.taskAltMoveUp(),   TeamingAction.TASK_MOVE_UP);
		m_moveDownButton = new TaskButton(this, m_images.arrowDown(), m_images.arrowDownDisabled(), m_images.arrowDownMouseOver(), false, m_messages.taskAltMoveDown(), TeamingAction.TASK_MOVE_DOWN);
		m_moveDownButton.addStyleName("gwtTaskTools_Span");
		InlineLabel il   = new InlineLabel(m_messages.taskLabelOrder());
		il.addStyleName("mediumtext");
		fp.add(il);
		fp.add(m_moveUpButton);
		fp.add(m_moveDownButton);

		// ...create the subtask span...
		m_moveLeftButton  = new TaskButton(this, m_images.arrowLeft(),  m_images.arrowLeftDisabled(),  m_images.arrowLeftMouseOver(),  false, m_messages.taskAltMoveLeft(),  TeamingAction.TASK_MOVE_LEFT);
		m_moveRightButton = new TaskButton(this, m_images.arrowRight(), m_images.arrowRightDisabled(), m_images.arrowRightMouseOver(), false, m_messages.taskAltMoveRight(), TeamingAction.TASK_MOVE_RIGHT);
		m_moveRightButton.addStyleName("gwtTaskTools_Span");
		il = new InlineLabel(m_messages.taskLabelSubtask());
		il.addStyleName("mediumtext");
		fp.add(il);
		fp.add(m_moveLeftButton);
		fp.add(m_moveRightButton);

		// ...create the delete button...
		m_deleteButton = new TaskButton(
			this,
			m_messages.taskLabelDelete(),
			m_messages.taskAltDelete(),
			false,
			TeamingAction.TASK_DELETE);
		m_deleteButton.addStyleName("marginright2px");
		fp.add(m_deleteButton);

		// ...create the purge button...
		m_purgeButton = new TaskButton(
			this,
			m_messages.taskLabelPurge(),
			m_messages.taskAltPurge(),
			false,
			TeamingAction.TASK_PURGE);
		fp.add(m_purgeButton);

		// ...create a span to hold any hints the TaskTable may
		// ...want to display...
		m_hintSpan = new InlineLabel();
		m_hintSpan.addStyleName("gwtTaskTools_HintSpan gwtTaskTools_HintSpan_Hidden");
		fp.add(m_hintSpan);
		m_taskToolsDIV.add(fp);

		// ...create a popup menu for the view options...
		List<TaskMenuOption> vOpts = new ArrayList<TaskMenuOption>();
		vOpts.add(new TaskMenuOption("ALL",          m_messages.taskViewAllEntries(),    m_filterType.equals("ALL"     )));
		vOpts.add(new TaskMenuOption("CLOSED",       m_messages.taskViewCompleted(),     m_filterType.equals("CLOSED"  )));
		vOpts.add(new TaskMenuOption("DAY",          m_messages.taskViewToday(),         m_filterType.equals("DAY"     )));
		vOpts.add(new TaskMenuOption("WEEK",         m_messages.taskViewWeek(),          m_filterType.equals("WEEK"    )));
		vOpts.add(new TaskMenuOption("MONTH",        m_messages.taskViewMonth(),         m_filterType.equals("MONTH"   )));
		vOpts.add(new TaskMenuOption("ACTIVE",       m_messages.taskViewAllActive(),     m_filterType.equals("ACTIVE"  )));
		if (m_showModeSelect) {
			vOpts.add(new TaskMenuOption());
			vOpts.add(new TaskMenuOption("VIRTUAL",  m_messages.taskViewAssignedTasks(), m_mode.equals(      "VIRTUAL" )));
			vOpts.add(new TaskMenuOption("PHYSICAL", m_messages.taskViewFromFolder(),    m_mode.equals(      "PHYSICAL")));
		}
		m_viewMenu = new TaskPopupMenu(this, TeamingAction.TASK_VIEW, vOpts);

		// ...generate the string to display on the menu...
		StringBuffer menuBuf = new StringBuffer(m_messages.taskView());
		menuBuf.append("  ");
		menuBuf.append(TaskMenuOption.getTMOFromList(m_filterType, vOpts).getMenuAlt());
		if (m_showModeSelect) {
			menuBuf.append(" | ");
			menuBuf.append(TaskMenuOption.getTMOFromList(m_mode,   vOpts).getMenuAlt());
		}
		
		// ...and create the <A> that activates the menu.
		Anchor a = new Anchor();
		a.setWordWrap(false);
		a.addStyleName("gwtTaskTools_ViewMenuAnchor");
		il = new InlineLabel(menuBuf.toString());
		il.setWordWrap(false);
		il.addStyleName("gwtTaskTools_ViewMenuSpan");
		final Element aE = a.getElement();
		aE.appendChild(il.getElement());
		Image img = new Image(m_images.menu());
		img.getElement().setAttribute("align", "absmiddle");
		aE.appendChild(img.getElement());
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				m_viewMenu.showTaskPopupMenu(aE);
			}			
		});
		fp = new FlowPanel();
		fp.addStyleName("gwtTaskTools_ViewMenuDIV");
		fp.add(a);
		m_taskToolsDIV.add(fp);
	}

	/**
	 * Called when the TaskListing <DIV> needs to resize itself based
	 * on the current size of the content frame.
	 */
	public void resize() {
		Scheduler.ScheduledCommand resizeCommand;
		resizeCommand = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				resizeNow();
			}
		};
		Scheduler.get().scheduleDeferred(resizeCommand);
	}
	
	public void resize(boolean immediate) {
		if (immediate) resizeNow();
		else           resize();
	}
	
	private void resizeNow() {
		// How tall is the content frame?
		Document contentDoc = Document.get();
		int contentHeight = contentDoc.getClientHeight();
		
		// Where'e the top of the task listing <DIV> relative to that?
		int absContentTop = contentDoc.getBody().getAbsoluteTop();
		int absListingTop = m_taskListingDIV.getElement().getAbsoluteTop();
		int relListingtop = (absListingTop - absContentTop);

		// Calculate the height and put it into effect.
		int tasksHeight = (contentHeight - (relListingtop + FOOTER_FUDGE));
		Unit tasksUnits;
		if (TASK_LISTING_MINIMUM > tasksHeight) {
			// If we calculated smaller than the minimum, we revert to
			// 100%.
			tasksHeight = 100;
			tasksUnits  = Unit.PCT;
		}
		else {
			// If we calculated more than the minimum, use that value
			// as pixels.
			tasksUnits = Unit.PX;
		}		
		m_taskListingDIV.getElement().getStyle().setHeight(tasksHeight, tasksUnits);
	}

	/**
	 * Called by TaskPopupMenu when a selection has been made in the
	 * task listing's view menu.
	 * 
	 * @param viewAction
	 * @param viewOption
	 */
	public void setViewOption(TeamingAction viewAction, String viewOption) {
		switch (viewAction) {
		case TASK_VIEW:  handleViewOption(viewOption); break;
			
		default:
			Window.alert(m_messages.taskInternalError_UnexpectedAction(viewAction.toString()));
			break;
		}
	}

	/**
	 * Displays a string in the hint <SPAN>.
	 * 
	 * @param hint
	 */
	public void showHint(String hint) {
		// If we weren't given a hint to display...
		if (!(GwtClientHelper.hasString(hint))) {
			// ...hide the hint <SPAN>...
			hideHint();
		}
		else {
			// ...otherwise, display it.
			m_hintSpan.setText(hint);
			m_hintSpan.removeStyleName("gwtTaskTools_HintSpan_Hidden");
		}
	}
	
	/*
	 * Shows the TaskBundle into the task listing DIV.
	 */
	private void showTaskBundle(final long readTime) {
		Scheduler.ScheduledCommand showCommand;
		showCommand = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				showTaskBundleNow(readTime);
			}
		};
		Scheduler.get().scheduleDeferred(showCommand);
	}
	
	private void showTaskBundleNow(final long readTime) {
		m_taskListingDIV.clear();
		boolean newTaskTable = (null == m_taskTable);
		if (newTaskTable) m_taskTable = new TaskTable(this);
		final long showTime = m_taskTable.showTasks(m_taskBundle, m_updateCalculatedDates);
		if (newTaskTable) m_taskListingDIV.add(m_taskTable);
		if (m_taskBundle.getIsDebug()) {
			Scheduler.ScheduledCommand showTimeCommand;
			showTimeCommand = new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					Window.alert(m_messages.taskDebug_times(
						String.valueOf(m_taskBundle.getTotalTasks()),
						String.valueOf(readTime),
						String.valueOf(showTime),
						String.valueOf(readTime + showTime)));
				}
			};
			Scheduler.get().scheduleDeferred(showTimeCommand);
		}
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
		m_taskTable.handleAction(action, null);
	}
	
	public void triggerAction(TeamingAction action, Object obj) {
		m_taskTable.handleAction(action, obj);
	}	
}
