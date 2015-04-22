/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.QuickFilter;
import org.kablink.teaming.gwt.client.binderviews.TaskFolderView;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.TaskHierarchyDisabledEvent;
import org.kablink.teaming.gwt.client.event.TaskListReadyEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskBundleCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskBundleRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TaskDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EventWrapper;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.widgets.EventButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Implements a GWT based task folder list user interface.
 * 
 * @author drfoster@novell.com
 */
public class TaskListing extends Composite implements TaskProvider {
	public static RequestInfo m_requestInfo;			//

	private boolean			m_embeddedInJSP;			// true -> The TaskListing is embedded in a JSP page.  false -> It's embedded in a TaskFolderView.
	private boolean			m_updateCalculatedDates;	// true -> Tell the task table to update the calculated dates upon loading.
	private boolean			m_showModeSelect;			// true -> Show the 'All Entries vs. From Folder' options.  false -> Don't.
	private boolean			m_sortDescend;				// true -> Sort is descending.  false -> Sort is ascending.
	private EventButton		m_deleteButton;				//
	private EventButton		m_moveDownButton;			//
	private EventButton		m_moveUpButton;				//
	private EventButton		m_moveLeftButton;			//
	private EventButton		m_moveRightButton;			//
	private EventButton		m_purgeButton;				//
	private FlowPanel		m_taskListingDIV;			// The <DIV> in the content pane that's to contain the task listing.
	private FlowPanel		m_taskRootDIV;				// The <DIV> in the content pane that's to contain the task tool bar.
	private FlowPanel		m_taskToolsDIV;				// The <DIV> in the content pane that's to contain the task tool bar.
	private FlowPanel		m_taskToolsLinkageDIV;		//
	private FlowPanel		m_taskToolsWarningDIV;		//
	private InlineLabel		m_pleaseWaitLabel;			//
	private Long			m_binderId;					// The ID of the binder containing the tasks to be listed.
	private Long			m_taskChangeId;				// Empty or the ID of an added or modified task.
	private String			m_adaptedUrl;				//
	private String			m_filterType;				// The current filtering in affect, if any.
	private String			m_mode;						// The current mode being displayed (PHYSICAL vs. VITRUAL.)
	private String			m_sortBy;					// The column the tasks are currently sorted by.
	private String			m_taskChangeReason;			// Empty, taskAdded or taskModified, as the case may be.
	private TaskBundle		m_taskBundle;				// The TaskLinkage and List<TaskListItem> that we're listing.
	private TaskFolderView	m_taskFolderView;			//
	private TaskPopupMenu	m_viewMenu;					//
	private TaskTable		m_taskTable;				//
	
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//
	
	private final int FOOTER_FUDGE         = 45;	// Space allowed for the task folder's footer.
	private final int TASK_LISTING_MINIMUM = 50;	// Minimum size for the task listing <DIV>.  Below this, and it reverts to '100%'.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TaskListing(TaskFolderView taskFolderView) {
		// Initialize the super class
		super();

		// Is this task listing embedded in a JSP page?
		m_embeddedInJSP = (null == taskFolderView);
		if (isEmbeddedInJSP()){
			// Yes!  Initialize it based on the values stored in the
			// JSP.
			initFromJSP();
		}
		else {
			// No, it's not embedded in a JSP page!  It must be
			// embedded in a task folder view!  Store the task folder
			// view and initialize based on that.
			m_taskFolderView = taskFolderView;
			initFromTaskFolderView();
		}
		
		// Create the panels that are to contain the task tools and
		// ...listing...
		m_taskRootDIV  = new FlowPanel();		
		m_taskToolsDIV = new FlowPanel();
		m_taskToolsDIV.addStyleName("gwtTaskTools");
		m_taskToolsDIV.getElement().setId("ss_gwtTaskToolsDIV");
		m_taskRootDIV.add(m_taskToolsDIV);		
		m_taskListingDIV = new FlowPanel();
		m_taskListingDIV.addStyleName("gwtTaskListing");
		m_taskListingDIV.addStyleName(GwtClientHelper.jsIsIE() ? "gwtTaskListing_IE" : "gwtTaskListing_NonIE");
		m_taskListingDIV.getElement().setId("ss_gwtTaskListingDIV");
		FlowPanel pleaseWaitPanel = new FlowPanel();
		pleaseWaitPanel.addStyleName("wiki-noentries-panel gwtTaskList_loading");
		m_pleaseWaitLabel = new InlineLabel(m_messages.taskPleaseWait_Loading());
		pleaseWaitPanel.add(m_pleaseWaitLabel);
		Image busyImg = GwtClientHelper.buildImage(m_images.busyAnimation_small());
		busyImg.addStyleName("gwtTaskList_loadingImg");
		pleaseWaitPanel.add(busyImg);
		m_taskListingDIV.add(pleaseWaitPanel);
		m_taskRootDIV.add(m_taskListingDIV);
		
		// ...populate the task panels...
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateTaskDIVs();
			}
		});

		// ...and tell the Composite that we're good to go.
		initWidget(m_taskRootDIV);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean     isEmbeddedInJSP()          {return m_embeddedInJSP;        }
	public boolean     getSortDescend()           {return m_sortDescend;          }
	public boolean     getUpdateCalculatedDates() {return m_updateCalculatedDates;}
	public FlowPanel   getTaskListingDIV()        {return m_taskListingDIV;       }
	public FlowPanel   getTaskRootDIV()           {return m_taskRootDIV;          }
	public FlowPanel   getTaskToolsDIV()          {return m_taskToolsDIV;         }
	public FlowPanel   getTaskToolsLinkageDIV()   {return m_taskToolsLinkageDIV;  }
	public FlowPanel   getTaskToolsWarningDIV()   {return m_taskToolsWarningDIV;  }
	public Long        getBinderId()              {return m_binderId;             }
	public Long        getTaskChangeId()          {return m_taskChangeId;         }
	public RequestInfo getRequestInfo()           {return m_requestInfo;          }
	public String      getFilterType()            {return m_filterType;           }
	public String      getMode()                  {return m_mode;                 }
	public String      getSortBy()                {return m_sortBy;               }
	public String      getTaskChangeReason()      {return m_taskChangeReason;     }
	public TaskBundle  getTaskBundle()            {return m_taskBundle;           }
	public EventButton  getDeleteButton()          {return m_deleteButton;         }
	public EventButton  getMoveDownButton()        {return m_moveDownButton;       }
	public EventButton  getMoveUpButton()          {return m_moveUpButton;         }
	public EventButton  getMoveLeftButton()        {return m_moveLeftButton;       }
	public EventButton  getMoveRightButton()       {return m_moveRightButton;      }
	public EventButton  getPurgeButton()           {return m_purgeButton;          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setTaskBundle(TaskBundle taskBundle) {m_taskBundle = taskBundle;}

	/**
	 * Returns the task list from the TaskListing.
	 * 
	 * Implements the TaskProvider.getTasks() method.
	 * 
	 * @return
	 */
	@Override
	public List<TaskListItem> getTasks() {
		List<TaskListItem> reply;
		if (null == m_taskBundle)
		     reply = null;
		else reply = m_taskBundle.getTasks();
		return reply;
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this task
	 * folder view.
	 * 
	 * @param event
	 */
	public void handleContributorIdsRequest(ContributorIdsRequestEvent event) {
		if (null != m_taskTable) {
			m_taskTable.handleContributorIdsRequest(event);
		}
	}
	
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
		String url = m_adaptedUrl;
		url = GwtClientHelper.replace(url, "xxx_operand_xxx", operand);
		url = GwtClientHelper.replace(url, "xxx_option_xxx",  viewOption);
		if (null == m_taskFolderView)
		     GwtClientHelper.jsLoadUrlInCurrentWindow(    url);
		else GwtTeaming.fireEvent(new GotoContentUrlEvent(url));
	}

	/*
	 * Called to initialize the TaskListing when it's embedded in a JSP
	 * page.
	 */
	private void initFromJSP() {
		// Initialize the JSNI components...
		jsInitResizeHandler(this);		
		m_requestInfo = jsGetRequestInfo();
		m_adaptedUrl  = m_requestInfo.getAdaptedUrl();
		
		// ...and extract the parameters we need to render the tasks.
		m_binderId              = Long.parseLong(m_requestInfo.getBinderId());
		m_filterType            =                 jsGetElementValue("ssCurrentTaskFilterType");
		m_mode                  =                 jsGetElementValue("ssCurrentFolderModeType");
		m_sortBy                =                 jsGetElementValue("ssFolderSortBy"         );
		m_sortDescend           = Boolean.valueOf(jsGetElementValue("ssFolderSortDescend"   ));
		m_updateCalculatedDates = Boolean.valueOf(jsGetElementValue("updateCalculatedDates" ));
		m_taskChangeReason      =                 jsGetElementValue("taskChange"             );
		String tcId = jsGetElementValue("taskId");
		m_taskChangeId          = (GwtClientHelper.hasString(tcId) ? Long.parseLong(tcId) : null);
		
		String showMode = jsGetElementValue("ssShowFolderModeSelect");
		m_showModeSelect = (GwtClientHelper.hasString(showMode) && Boolean.valueOf(showMode));
	}
	
	/*
	 * Called to initialize the TaskListing when it's embedded in a
	 * TaskFolderView.
	 */
	private void initFromTaskFolderView() {
		// Get the task display data from the task view.
		TaskDisplayDataRpcResponseData tdd = m_taskFolderView.getTaskDisplayData();
		
		// Initialize the request info object...
		m_requestInfo = GwtClientHelper.getRequestInfo();
		m_adaptedUrl  = tdd.getAdaptedUrl();
		
		// ...and extract the parameters we need to render the tasks.
		m_binderId              = m_taskFolderView.getFolderId();
		m_filterType            = tdd.getFilterType();
		m_mode                  = tdd.getMode();
		m_sortBy                = m_taskFolderView.getFolderDisplayData().getFolderSortBy();
		m_sortDescend           = m_taskFolderView.getFolderDisplayData().getFolderSortDescend();
		m_taskChangeId          = tdd.getTaskChangeId();
		m_taskChangeReason      = tdd.getTaskChangeReason();
		m_updateCalculatedDates = tdd.getUpdateCalculatedDates();
		m_showModeSelect        = tdd.getShowModeSelect();
	}
	
	/*
	 * Uses JSNI to return the value of a document element.
	 */
	private native String jsGetElementValue(String eId) /*-{
		return $doc.getElementById(eId).value;
	}-*/;
	
	/*
	 * Uses JSNI to grab the JavaScript object that holds the
	 * information about the request dealing with.
	 */
	private native RequestInfo jsGetRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.m_requestInfo;
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
		populateTaskFilterDIV();
		populateTaskToolsDIV();
		populateTaskListingDIV();
	}

	/*
	 * Adds the task filter widgets to the task filter DIV.
	 */
	private void populateTaskFilterDIV() {
		// Is this TaskListing embedded in a JSP page?
		if (isEmbeddedInJSP()) {
			// Yes!  Attach a QuickFilter.
			RootPanel taskFilterRoot = RootPanel.get("gwtTaskFilter");		
			taskFilterRoot.add(new QuickFilter(m_binderId));
		}
	}
	
	/*
	 * Adds the task listing widgets to the task listing DIV.
	 */
	private void populateTaskListingDIV() {
		// Is the task listing embedded in a JSP?
		if (isEmbeddedInJSP()) {
			// Yes!  Resize its <DIV> to the content panel.
			resizeNow();
		}
		
		// Note that when the task listing is embedded in task folder
		// view, the <DIV> gets resized AFTER everything has completed
		// loading.  See TaskFolderView.viewComplete() for where that
		// happens.
		
		// Display the tasks in the task listing.
		final long start = System.currentTimeMillis();
		GetTaskBundleCmd cmd = new GetTaskBundleCmd(isEmbeddedInJSP(), m_binderId, m_filterType, m_mode);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskList();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
				
				// ...and display the error as the task listing.
				m_taskListingDIV.clear();
				m_taskListingDIV.add(new InlineLabel(error));
				
				GwtTeaming.fireEvent(new TaskListReadyEvent(m_binderId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				TaskBundleRpcResponseData responseData = ((TaskBundleRpcResponseData) result.getResponseData());
				TaskBundle taskBundle = responseData.getTaskBundle();
				
				// Clear the task listing DIV's contents and render the
				// task list.
				long end = System.currentTimeMillis();
				m_pleaseWaitLabel.setText(m_messages.taskPleaseWait_Rendering());
				m_taskBundle = taskBundle;
				showTaskBundle(end - start);
				
				GwtTeaming.fireEvent(new TaskListReadyEvent(m_binderId));
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

		// Create a panel to hold everything...
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("gwtTaskTools_LeftDIV");
		
		String displayStyle;
		if (GwtClientHelper.jsIsIE())
		     displayStyle = " displayInline";
		else displayStyle = " displayInlineBlock";
		
		// ...create the linkage panel...
		m_taskToolsLinkageDIV = new FlowPanel();
		m_taskToolsLinkageDIV.addStyleName("gwtTaskTools_LinkageDIV" + displayStyle);
		
		// ...create the order buttons...
		m_moveUpButton   = new EventButton(m_images.arrowUp(),   m_images.arrowUpDisabled(),   m_images.arrowUpMouseOver(),   false, m_messages.taskAltMoveUp(),   TeamingEvents.TASK_MOVE_UP);
		m_moveDownButton = new EventButton(m_images.arrowDown(), m_images.arrowDownDisabled(), m_images.arrowDownMouseOver(), false, m_messages.taskAltMoveDown(), TeamingEvents.TASK_MOVE_DOWN);
		m_moveDownButton.addStyleName("gwtTaskTools_Span");
		InlineLabel il   = new InlineLabel(m_messages.taskLabelOrder());
		il.addStyleName("gwtTaskTools_Order");
		m_taskToolsLinkageDIV.add(il);
		m_taskToolsLinkageDIV.add(m_moveUpButton);
		m_taskToolsLinkageDIV.add(m_moveDownButton);

		// ...create the subtask buttons...
		m_moveLeftButton  = new EventButton(m_images.arrowLeft(),  m_images.arrowLeftDisabled(),  m_images.arrowLeftMouseOver(),  false, m_messages.taskAltMoveLeft(),  TeamingEvents.TASK_MOVE_LEFT);
		m_moveRightButton = new EventButton(m_images.arrowRight(), m_images.arrowRightDisabled(), m_images.arrowRightMouseOver(), false, m_messages.taskAltMoveRight(), TeamingEvents.TASK_MOVE_RIGHT);
		m_moveRightButton.addStyleName("gwtTaskTools_Span");
		il = new InlineLabel(m_messages.taskLabelSubtask());
		il.addStyleName("gwtTaskTools_Order");
		m_taskToolsLinkageDIV.add(il);
		m_taskToolsLinkageDIV.add(m_moveLeftButton);
		m_taskToolsLinkageDIV.add(m_moveRightButton);
		fp.add(m_taskToolsLinkageDIV);

		// ...create the warning panel...
		m_taskToolsWarningDIV = new FlowPanel();
		m_taskToolsWarningDIV.addStyleName("gwtTaskTools_WarningDIV gwtTaskTools_Span" + displayStyle);
		m_taskToolsWarningDIV.setVisible(false);
		Label button = new Label(m_messages.taskHierarchyDisabled());
		button.setTitle(m_messages.taskAltHierarchyDisabled());
		button.addStyleName("cursorPointer");
		button.addStyleName("gwtTaskTools_WarningAnchor" + displayStyle);
		EventWrapper.addHandler(button, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TaskHierarchyDisabledEvent.fireOne();
			}
		});
		m_taskToolsWarningDIV.add(button);
		fp.add(m_taskToolsWarningDIV);

		// ...if the task listing is embedded in a JSP page...
		if (isEmbeddedInJSP()) {
			// ...create the delete and purge button panel...
			FlowPanel buttonDIV = new FlowPanel();
			buttonDIV.addStyleName("gwtTaskTools_ButtonDIV" + displayStyle);
			m_deleteButton = new EventButton(
				m_messages.taskLabelDelete(),
				m_messages.taskAltDelete(),
				false,	// false -> Disabled by default.
				TeamingEvents.TASK_DELETE);
			m_deleteButton.addStyleName("marginright2px");
			buttonDIV.add(m_deleteButton);
			m_purgeButton = new EventButton(
				m_messages.taskLabelPurge(),
				m_messages.taskAltPurge(),
				false,	// false -> Disabled by default.
				TeamingEvents.TASK_PURGE);
			buttonDIV.add(m_purgeButton);
			fp.add(buttonDIV);
			
			// ...in a task folder view, these buttons are incorporated
			// ...in the entry menu panel...
		}
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
		m_viewMenu = new TaskPopupMenu(this, TeamingEvents.TASK_VIEW, vOpts);

		// ...generate the string to display on the menu...
		StringBuffer menuBuf = new StringBuffer(m_messages.taskView());
		menuBuf.append("  ");
		menuBuf.append(TaskMenuOption.getTMOFromList(m_filterType, vOpts).getMenuAlt());
		if (m_showModeSelect) {
			menuBuf.append(" | ");
			menuBuf.append(TaskMenuOption.getTMOFromList(m_mode,   vOpts).getMenuAlt());
		}
		
		// ...and create the <A> that activates the menu.
		final Anchor a = new Anchor();
		a.setWordWrap(false);
		a.addStyleName("gwtTaskTools_ViewMenuAnchor");
		il = new InlineLabel(menuBuf.toString());
		il.setWordWrap(false);
		il.addStyleName("gwtTaskTools_ViewMenuSpan");
		final Element aE = a.getElement();
		aE.appendChild(il.getElement());
		Image img = GwtClientHelper.buildImage(m_images.menu());
		aE.appendChild(img.getElement());
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				m_viewMenu.showTaskPopupMenu(a);
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resizeNow();
			}
		});
	}
	
	public void resize(boolean immediate) {
		if (immediate) resizeNow();
		else           resize();
	}
	
	private void resizeNow() {
		if (isEmbeddedInJSP())
		     resizeNow_JSP();
		else resizeNow_View();
	}
	
	private void resizeNow_JSP() {
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

	private void resizeNow_View() {
		// Get the sizes we need to calculate the height of the <DIV>.
		FooterPanel fp  = m_taskFolderView.getFooterPanel();
		int viewHeight	= m_taskFolderView.getOffsetHeight();				// Height of the view.
		int viewTop		= m_taskFolderView.getAbsoluteTop();				// Absolute top of the view.		
		int tlDivTop	= (m_taskListingDIV.getAbsoluteTop() - viewTop);	// Top of the task listing relative to the top of the view.		
		int fpHeight	= ((null == fp) ? 0 : fp.getOffsetHeight());		// Height of the view's footer panel.
		int totalBelow	= fpHeight;											// Total space on the page below the task listing.

		// What's the optimum height for the task listing so we don't
		// get a vertical scroll bar?
		int tlHeight  = (((viewHeight - tlDivTop) - totalBelow) - m_taskFolderView.getNoVScrollAdjustment());
		int minHeight = m_taskFolderView.getMinimumContentHeight();
		if (minHeight > tlHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			tlHeight = minHeight;
		}
		
		// Set the height of the taskListing.
		m_taskListingDIV.setHeight(tlHeight + "px");
	}

	/**
	 * Enabled/disables the the menu items that require a selection.
	 * 
	 * @param enable
	 */
	public void setEntriesSelected(boolean enable, boolean singleTaskSelected) {
		// If the task listing is embedded in a JSP page...
		if (isEmbeddedInJSP()) {
			// ...enabled/disable the buttons on its tool bar...
			getDeleteButton().setEnabled(enable);
			getPurgeButton().setEnabled( enable);
		}
		
		else {
			// ...otherwise, enable/disable them on the entry menu.
			EntryMenuPanel emp = m_taskFolderView.getEntryMenuPanel();
			if (null != emp) {
				EntryMenuPanel.setEntriesSelected(emp, enable            );
				EntryMenuPanel.setEntrySelected(  emp, singleTaskSelected);
			}
		}
	}

	/**
	 * Called by TaskPopupMenu when a selection has been made in the
	 * task listing's view menu.
	 * 
	 * @param viewEvent
	 * @param viewOption
	 */
	public void setViewOption(TeamingEvents viewEvent, String viewOption) {
		switch (viewEvent) {
		case TASK_VIEW:  handleViewOption(viewOption); break;
			
		default:
			Window.alert(m_messages.taskInternalError_UnexpectedEvent(viewEvent.toString()));
			break;
		}
	}

	/*
	 * Shows the TaskBundle into the task listing DIV.
	 */
	private void showTaskBundle(final long readTime) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showTaskBundleNow(readTime);
			}
		});
	}
	
	private void showTaskBundleNow(final long readTime) {
		m_taskListingDIV.clear();
		boolean newTaskTable = (null == m_taskTable);
		if (newTaskTable) m_taskTable = new TaskTable(this);
		final long showTime = m_taskTable.showTasks(m_taskBundle);
		if (newTaskTable) m_taskListingDIV.add(m_taskTable);
		if (m_taskBundle.getIsDebug() && GwtClientHelper.isDebugUI()) {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					Window.alert(m_messages.taskDebug_times(
						String.valueOf(m_taskBundle.getTotalTasks()),
						String.valueOf(readTime),
						String.valueOf(showTime),
						String.valueOf(readTime + showTime)));
				}
			});
		}
	}

	/**
	 * Hides the warning panel and shows the linkage panel.
	 */
	public void showTaskToolsLinkage() {
		m_taskToolsWarningDIV.setVisible(false);
		m_taskToolsLinkageDIV.setVisible(true );
	}
	
	/**
	 * Hides the linkage panel and shows the warning panel.
	 */
	public void showTaskToolsWarning() {
		m_taskToolsLinkageDIV.setVisible(false);
		m_taskToolsWarningDIV.setVisible(true );
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the task listing and perform some operation on it.            */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the task listing
	 * asynchronously after it loads. 
	 */
	public interface TaskListingClient {
		void onSuccess(TaskListing taskListing);
		void onUnavailable();
	}

	/**
	 * Loads the TaskListing split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param taskListingClient
	 */
	public static void createAsync(final TaskFolderView taskFolderView, final TaskListingClient taskListingClient) {
		GWT.runAsync(TaskListing.class, new RunAsyncCallback() {			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_TaskListing() );
				taskListingClient.onUnavailable();
			}
			
			@Override
			public void onSuccess() {
				TaskListing taskListing = new TaskListing(taskFolderView);
				taskListingClient.onSuccess(taskListing);
			}
		});
	}
}
