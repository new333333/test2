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
import org.kablink.teaming.gwt.client.event.TaskHierarchyDisabledEvent;
import org.kablink.teaming.gwt.client.event.TaskQuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskBundleCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskBundleRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.EventWrapper;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements a GWT based task folder list user interface.
 * 
 * @author drfoster@novell.com
 */
public class TaskListing extends Composite {
	public static RequestInfo m_requestInfo;			//
	
	private boolean			m_updateCalculatedDates;	// true -> Tell the task table to update the calculated dates upon loading.
	private boolean			m_showModeSelect;			// true -> Show the 'All Entries vs. From Folder' options.  false -> Don't.
	private boolean			m_sortDescend;				// true -> Sort is descending.  false -> Sort is ascending.
	private FlowPanel		m_taskListingDIV;			// The <DIV> in the content pane that's to contain the task listing.
	private FlowPanel		m_taskRootDIV;				// The <DIV> in the content pane that's to contain the task tool bar.
	private FlowPanel		m_taskToolsDIV;				// The <DIV> in the content pane that's to contain the task tool bar.
	private FlowPanel		m_taskToolsLinkageDIV;		//
	private FlowPanel		m_taskToolsWarningDIV;		//
	private InlineLabel		m_pleaseWaitLabel;			//
	private Long			m_binderId;					// The ID of the binder containing the tasks to be listed.
	private RootPanel		m_taskFilterRoot;			//
	private String			m_filterType;				// The current filtering in affect, if any.
	private String			m_mode;						// The current mode being displayed (PHYSICAL vs. VITRUAL.)
	private String			m_sortBy;					// The column the tasks are currently sorted by.
	private String			m_taskChangeId;				// Empty or the ID of an added or modified task.
	private String			m_taskChangeReason;			// Empty, taskAdded or taskModified, as the case may be.
	private TaskBundle		m_taskBundle;				// The TaskLinkage and List<TaskListItem> that we're listing.
	private TaskButton		m_deleteButton;				//
	private TaskButton		m_moveDownButton;			//
	private TaskButton		m_moveUpButton;				//
	private TaskButton		m_moveLeftButton;			//
	private TaskButton		m_moveRightButton;			//
	private TaskButton		m_purgeButton;				//
	private TaskFilter		m_taskFilter;				//
	private TaskPopupMenu	m_viewMenu;					//
	private TaskTable		m_taskTable;				//
	
	private final GwtRpcServiceAsync				m_rpcService = GwtTeaming.getRpcService();				// 
	private final GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();				//
	private final GwtTeamingTaskListingImageBundle	m_images     = GwtTeaming.getTaskListingImageBundle();	//
	
	private final int FOOTER_FUDGE         = 45;	// Space allowed for the task folder's footer.
	private final int TASK_LISTING_MINIMUM = 50;	// Minimum size for the task listing <DIV>.  Below this, and it reverts to '100%'.

	/*
	 * Inner class used to encapsulate the task filter widgets.
	 */
	private class TaskFilter extends Composite {
		private boolean		m_taskFilterEmpty = true;	//
		private boolean		m_taskFilterOff   = true;	//
		private FlowPanel	m_taskFilterDIV;			// <DIV> containing the filter widgets.
		private Image		m_taskFilterImage;			// <IMG> for the filter.  Changes based on whether a filter is active or not.
		private TextBox		m_taskFilterInput;			// The <INPUT> for entering a filter.
		
		/**
		 * Class constructor.
		 */
		public TaskFilter() {
			super();
			
			// Create the filter <IMG>...
			m_taskFilterImage = new Image(m_images.filterOff());
			m_taskFilterImage.setTitle(m_messages.taskAltFilterOff());
			m_taskFilterImage.setStyleName("cursorPointer");
			m_taskFilterImage.getElement().setAttribute("align", "absmiddle");
			EventWrapper.addHandler(m_taskFilterImage, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// Simply kill any filter information.
					killFilter();
				}				
			});
			
			// ...create the filter <INPUT>...
			m_taskFilterInput = new TextBox();
			m_taskFilterInput.setValue(m_messages.taskFilter_empty());
			m_taskFilterInput.addStyleName("gwtTaskFilter_input");
			setBlurStyles();
			List<EventHandler> inputHandlers = new ArrayList<EventHandler>();
			inputHandlers.add(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					// Is this the enter key being pressed?
					int key = event.getNativeEvent().getKeyCode();
					if (KeyCodes.KEY_ENTER == key) {
						// Yes!  Is there anything in the filter?
						String filter = getFilterValue();
						if (0 < filter.length()) {
							// Yes!  Put the filter into effect.
							m_taskFilterImage.setResource(m_images.filterOn());
							m_taskFilterImage.setTitle(m_messages.taskAltFilterOn());
							m_taskFilterOff   =
							m_taskFilterEmpty = false;
						}						
						else {
							// No, there's nothing in the filter!  Turn
							// off any filter that's in effect.
							m_taskFilterImage.setResource(m_images.filterOff());
							m_taskFilterImage.setTitle(m_messages.taskAltFilterOff());
							m_taskFilterOff   =
							m_taskFilterEmpty = true;
						}
						filterListAsync(filter);
					}
				}
			});
			inputHandlers.add(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					// Set the appropriate styles on the input...
					setBlurStyles();

					// ...and if the filter input is empty...
					String filter     = getFilterValue();
					m_taskFilterEmpty = (0 == filter.length());
					if (m_taskFilterEmpty) {
						// ...display an empty message in it.
						m_taskFilterInput.setValue(m_messages.taskFilter_empty());
					}
				}
			});
			inputHandlers.add(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					// Set the appropriate styles on the input...
					setFocusStyles();
					
					// ...and if the filter input is empty...
					if (m_taskFilterEmpty) {
						// ...remove any empty message from it.
						m_taskFilterInput.setValue("");
					}
				}
			});
			EventWrapper.addHandlers(m_taskFilterInput, inputHandlers);

			// ...tie it all together...
			m_taskFilterDIV = new FlowPanel();
			m_taskFilterDIV.add(m_taskFilterImage);
			m_taskFilterDIV.add(m_taskFilterInput);
			
			// ...and tell the Composite that we're good to go.
			initWidget(m_taskFilterDIV);
		}

		/*
		 * Asynchronously sets/clears a filter.
		 */
		private void filterListAsync(final String filter) {
			ScheduledCommand doFilter = new ScheduledCommand() {
				@Override
				public void execute() {
					filterListNow(filter);
				}
			};
			Scheduler.get().scheduleDeferred(doFilter);
		}
		
		/*
		 * Synchronously sets/clears a filter.
		 */
		private void filterListNow(String filter) {
			GwtTeaming.fireEvent(new TaskQuickFilterEvent(filter));
		}

		/*
		 * Returns a non-null, non-space padded filter value from the
		 * input widget.
		 */
		private String getFilterValue() {
			String reply = m_taskFilterInput.getValue();
			if (null == reply)           reply = "";
			else if (0 < reply.length()) reply = reply.trim();
			return reply;
		}

		/*
		 * Sets the appropriate styles on the input widget for when
		 * it loses focus.
		 */
		private void setBlurStyles() {
			if (m_taskFilterOff) {
				m_taskFilterInput.removeStyleName("gwtTaskFilter_inputFocus");
				m_taskFilterInput.addStyleName(   "gwtTaskFilter_inputBlur");
			}
			
			else {
				setFocusStyles();
			}
		}
		
		/*
		 * Sets the appropriate styles on the input widget for when
		 * it gets focus.
		 */
		private void setFocusStyles() {
			m_taskFilterInput.removeStyleName("gwtTaskFilter_inputBlur");
			m_taskFilterInput.addStyleName(   "gwtTaskFilter_inputFocus");
		}		
		
		/*
		 * Does what's necessary to turn off a filter.
		 */
		private void killFilter() {
			boolean filterWasOn = (!m_taskFilterOff);

			m_taskFilterInput.setValue(m_messages.taskFilter_empty());
			m_taskFilterImage.setResource(m_images.filterOff());
			m_taskFilterImage.setTitle(m_messages.taskAltFilterOff());
			m_taskFilterOff   =
			m_taskFilterEmpty = true;
			setBlurStyles();
			
			if (filterWasOn) {
				filterListAsync(null);
			}
		}
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TaskListing() {
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
		m_taskChangeId          =                 jsGetElementValue("taskId"                 );
		m_taskChangeReason      =                 jsGetElementValue("taskChange"             );
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
		ScheduledCommand populateCommand = new ScheduledCommand() {
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
	public boolean     getSortDescend()           {return m_sortDescend;          }
	public boolean     getUpdateCalculatedDates() {return m_updateCalculatedDates;}
	public FlowPanel   getTaskListingDIV()        {return m_taskListingDIV;       }
	public FlowPanel   getTaskRootDIV()           {return m_taskRootDIV;          }
	public FlowPanel   getTaskToolsDIV()          {return m_taskToolsDIV;         }
	public FlowPanel   getTaskToolsLinkageDIV()   {return m_taskToolsLinkageDIV;  }
	public FlowPanel   getTaskToolsWarningDIV()   {return m_taskToolsWarningDIV;  }
	public Long        getBinderId()              {return m_binderId;             }
	public RequestInfo getRequestInfo()           {return m_requestInfo;          }
	public String      getFilterType()            {return m_filterType;           }
	public String      getMode()                  {return m_mode;                 }
	public String      getSortBy()                {return m_sortBy;               }
	public String      getTaskChangeReason()      {return m_taskChangeReason;     }
	public TaskBundle  getTaskBundle()            {return m_taskBundle;           }
	public TaskButton  getDeleteButton()          {return m_deleteButton;         }
	public TaskButton  getMoveDownButton()        {return m_moveDownButton;       }
	public TaskButton  getMoveUpButton()          {return m_moveUpButton;         }
	public TaskButton  getMoveLeftButton()        {return m_moveLeftButton;       }
	public TaskButton  getMoveRightButton()       {return m_moveRightButton;      }
	public TaskButton  getPurgeButton()           {return m_purgeButton;          }
	
	public Long getTaskChangeId() {
		Long reply;
		if (GwtClientHelper.hasString(m_taskChangeId))
		     reply = Long.parseLong(m_taskChangeId);
		else reply = null;
		return reply;
	}
	
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
	 * Uses JSNI to grab the document Element that's to contain the
	 * task filter widgets.
	 */
	private native Element jsGetTaskFilterDIV() /*-{
		return $doc.getElementById("gwtTaskFilter");
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
		m_taskFilterRoot = RootPanel.get("gwtTaskFilter");		
		m_taskFilter     = new TaskFilter();
		m_taskFilterRoot.add(m_taskFilter);
	}
	
	/*
	 * Adds the task listing widgets to the task listing DIV.
	 */
	private void populateTaskListingDIV() {
		// Resize the TaskListing <DIV> to the content panel...
		resizeNow();
		
		// ...and display the tasks in it.
		final long start = System.currentTimeMillis();
		GetTaskBundleCmd cmd = new GetTaskBundleCmd(m_binderId, m_filterType, m_mode);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskList();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
				
				// ...and display the error as the task listing.
				m_taskListingDIV.clear();
				m_taskListingDIV.add(new InlineLabel(error));
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
		m_moveUpButton   = new TaskButton(m_images.arrowUp(),   m_images.arrowUpDisabled(),   m_images.arrowUpMouseOver(),   false, m_messages.taskAltMoveUp(),   TeamingEvents.TASK_MOVE_UP);
		m_moveDownButton = new TaskButton(m_images.arrowDown(), m_images.arrowDownDisabled(), m_images.arrowDownMouseOver(), false, m_messages.taskAltMoveDown(), TeamingEvents.TASK_MOVE_DOWN);
		m_moveDownButton.addStyleName("gwtTaskTools_Span");
		InlineLabel il   = new InlineLabel(m_messages.taskLabelOrder());
		il.addStyleName("mediumtext");
		m_taskToolsLinkageDIV.add(il);
		m_taskToolsLinkageDIV.add(m_moveUpButton);
		m_taskToolsLinkageDIV.add(m_moveDownButton);

		// ...create the subtask buttons...
		m_moveLeftButton  = new TaskButton(m_images.arrowLeft(),  m_images.arrowLeftDisabled(),  m_images.arrowLeftMouseOver(),  false, m_messages.taskAltMoveLeft(),  TeamingEvents.TASK_MOVE_LEFT);
		m_moveRightButton = new TaskButton(m_images.arrowRight(), m_images.arrowRightDisabled(), m_images.arrowRightMouseOver(), false, m_messages.taskAltMoveRight(), TeamingEvents.TASK_MOVE_RIGHT);
		m_moveRightButton.addStyleName("gwtTaskTools_Span");
		il = new InlineLabel(m_messages.taskLabelSubtask());
		il.addStyleName("mediumtext");
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
		
		// ...create the delete and purge button panel...
		FlowPanel buttonDIV = new FlowPanel();
		buttonDIV.addStyleName("gwtTaskTools_ButtonDIV" + displayStyle);
		m_deleteButton = new TaskButton(
			m_messages.taskLabelDelete(),
			m_messages.taskAltDelete(),
			false,	// false -> Disabled by default.
			TeamingEvents.TASK_DELETE);
		m_deleteButton.addStyleName("marginright2px");
		buttonDIV.add(m_deleteButton);
		m_purgeButton = new TaskButton(
			m_messages.taskLabelPurge(),
			m_messages.taskAltPurge(),
			false,	// false -> Disabled by default.
			TeamingEvents.TASK_PURGE);
		buttonDIV.add(m_purgeButton);
		fp.add(buttonDIV);
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
		Image img = new Image(m_images.menu());
		img.getElement().setAttribute("align", "absmiddle");
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
		ScheduledCommand resizeCommand = new ScheduledCommand() {
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
		ScheduledCommand showCommand = new ScheduledCommand() {
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
		final long showTime = m_taskTable.showTasks(m_taskBundle);
		if (newTaskTable) m_taskListingDIV.add(m_taskTable);
		if (m_taskBundle.getIsDebug() && GwtClientHelper.isDebugUI()) {
			ScheduledCommand showTimeCommand = new ScheduledCommand() {
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
	public static void createAsync(final TaskListingClient taskListingClient) {
		GWT.runAsync(TaskListing.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				TaskListing taskListing = new TaskListing();
				taskListingClient.onSuccess(taskListing);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_TaskListing() );
				taskListingClient.onUnavailable();
			}
		});
	}
}
