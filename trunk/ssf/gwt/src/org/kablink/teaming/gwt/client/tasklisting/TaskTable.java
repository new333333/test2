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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TaskDeleteEvent;
import org.kablink.teaming.gwt.client.event.TaskHierarchyDisabledEvent;
import org.kablink.teaming.gwt.client.event.TaskMoveDownEvent;
import org.kablink.teaming.gwt.client.event.TaskMoveLeftEvent;
import org.kablink.teaming.gwt.client.event.TaskMoveRightEvent;
import org.kablink.teaming.gwt.client.event.TaskMoveUpEvent;
import org.kablink.teaming.gwt.client.event.TaskPurgeEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.event.ViewWhoHasAccessEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.rpc.shared.AssignmentInfoListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CollapseSubtasksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandSubtasksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupAssigneeMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskBundleCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTeamAssigneeMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskCompletedCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskDueDateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskLinkageCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskPriorityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskSortCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TaskBundleRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TaskEventRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateCalculatedDatesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateCalculatedDatesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.tasklisting.TaskDispositionDlg.TaskDisposition;
import org.kablink.teaming.gwt.client.tasklisting.TaskDueDateDlg;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EventWrapper;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TaskListItemHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class that implements the Composite that contains the task folder
 * listing table.  
 * 
 * @author drfoster@novell.com
 */
public class TaskTable extends Composite
	implements
		// Event handlers implemented by this class.
		ChangeEntryTypeSelectedEntitiesEvent.Handler,
		CopySelectedEntitiesEvent.Handler,
		DeleteSelectedEntitiesEvent.Handler,
		LockSelectedEntitiesEvent.Handler,
		MarkReadSelectedEntitiesEvent.Handler,
		MoveSelectedEntitiesEvent.Handler,
		QuickFilterEvent.Handler,
		ShareSelectedEntitiesEvent.Handler,
		SubscribeSelectedEntitiesEvent.Handler,
		TaskDeleteEvent.Handler,
		TaskHierarchyDisabledEvent.Handler,
		TaskMoveDownEvent.Handler,
		TaskMoveLeftEvent.Handler,
		TaskMoveRightEvent.Handler,
		TaskMoveUpEvent.Handler,
		TaskPurgeEvent.Handler,
		UnlockSelectedEntitiesEvent.Handler,
		ViewSelectedEntryEvent.Handler,
		ViewWhoHasAccessEvent.Handler
{
	private boolean						m_sortAscending;			//
	private Column						m_sortColumn;				//
	private EventHandler				m_assigneeMouseOutEvent;	//
	private EventHandler				m_assigneeMouseOverEvent;	//
	private EventHandler				m_cbClickHandler;			//
	private EventHandler				m_columnClickHandler;		//
	private EventHandler				m_dueDateClickHandler;		//
	private EventHandler				m_expanderClickHandler;		//
	private EventHandler				m_newTaskClickHandler;		//
	private EventHandler				m_taskLocationClickHandler;	//
	private EventHandler				m_taskOptionClickHandler;	//
	private EventHandler				m_taskOrderBlurHandler;		//
	private EventHandler				m_taskOrderClickHandler;	//
	private EventHandler				m_taskOrderKeyPressHandler;	//
	private EventHandler				m_taskSeenClickHandler;		//
	private EventHandler				m_taskViewClickHandler;		//
	private FlexCellFormatter			m_flexTableCF;				//
	private FlexTable					m_flexTable;				//
	private Image 						m_dueDateBusy;				//
	private int							m_markerSize;				// Calculated size of a marker (e.g., unseen bubble, completed checkmark, ...)
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private long						m_renderTime;				//
	private ProcessActiveWidgets		m_processActiveWidgets;		//
	private RowFormatter				m_flexTableRF;				//
	private String						m_quickFilter;				//
	private TaskBundle					m_taskBundle;				//
	private TaskDueDateDlg				m_dueDateDlg;				//
	private TaskListing					m_taskListing;				//
	private TaskPopupMenu				m_newTaskMenu;				//
	private TaskPopupMenu				m_percentDoneMenu;			//
	private TaskPopupMenu				m_priorityMenu;				//
	private TaskPopupMenu				m_statusMenu;				//
	
	private       boolean							m_newTaskTable = true;										//
	private final GwtTeamingMessages				m_messages     = GwtTeaming.getMessages();					//
	private final GwtTeamingTaskListingImageBundle	m_images       = GwtTeaming.getTaskListingImageBundle();	//

	// Pixel size of spacers used throughout the task table.
	private final static int SPACER_SIZE = 16;
	
	// The following defines attributes added to various task table
	// widgets to enable backtracking to their related object, ...
	private final static String ATTR_COLUMN_SORT_KEY	= "n_columnSortKey";
	private final static String ATTR_ENTRY_ID			= "n_entryId";
	private final static String ATTR_OPTION_MENU		= "n_optionMenu";

	// The following defines the number of entries we show before
	// adding an ellipse to show more.
	private final static int MEMBERSHIP_ELLIPSE_COUNT	= 10;
	
	// The following defines the number of milliseconds before a
	// process active message will be delayed for those operations that
	// display one.
	private final static int PROCESS_ACTIVE_DELAY = 250;	// This is the same value used by the activity streams.
	
	// The following defines the minimum time it must take for the
	// initial rendering of the tasks in the task table before we'll
	// display process active messages.
	private final static long PROCESS_ACTIVE_RENDER_TIME = 500l; 
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTITIES,
		TeamingEvents.COPY_SELECTED_ENTITIES,
		TeamingEvents.DELETE_SELECTED_ENTITIES,
		TeamingEvents.LOCK_SELECTED_ENTITIES,
		TeamingEvents.MARK_READ_SELECTED_ENTITIES,
		TeamingEvents.MOVE_SELECTED_ENTITIES,
		TeamingEvents.QUICK_FILTER,
		TeamingEvents.SHARE_SELECTED_ENTITIES,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTITIES,
		TeamingEvents.TASK_DELETE,
		TeamingEvents.TASK_HIERARCHY_DISABLED,
		TeamingEvents.TASK_MOVE_DOWN,
		TeamingEvents.TASK_MOVE_LEFT,
		TeamingEvents.TASK_MOVE_RIGHT,
		TeamingEvents.TASK_MOVE_UP,
		TeamingEvents.TASK_PURGE,
		TeamingEvents.UNLOCK_SELECTED_ENTITIES,
		TeamingEvents.VIEW_SELECTED_ENTRY,
		TeamingEvents.VIEW_WHO_HAS_ACCESS,
	};
	
	/*
	 * Enumeration used to represent the order of the columns in the
	 * TaskTable.
	 */
	private enum Column {
		SELECTOR(           "*Unsortable*"),
		ORDER(              "order"),
		TASK_NAME(          "_sortTitle"),
		NEW_TASK_MENU(      "*never sorted*"),
		PRIORITY(           "priority"),
		DUE_DATE(           "start_end#LogicalEndDate"),
		STATUS(             "status"),
		ASSIGNED_TO(        "assignment"),
		CLOSED_PERCENT_DONE("completed"),
		LOCATION(           "location");
		
		private String m_sortKey;	// The sort key sorted on the server for this Column.

		/**
		 * Class constructor.
		 * 
		 * @param sortKey
		 */
		private Column(String sortKey) {
			// Simply store the parameter.
			m_sortKey = sortKey;
		}

		/**
		 * Returns the sort key for this Column.
		 * 
		 * @return
		 */
		String getSortKey() {
			return m_sortKey;
		}
		
		/**
		 * Maps a sort key from the server to a Column.
		 * 
		 * @param sortKey
		 * 
		 * @return
		 */
		static Column mapSortKeyToColumn(String sortKey) {
			Column reply = null;
			for (Column column:  Column.values()) {
				String columnKey = column.getSortKey();
				if (sortKey.equals(columnKey)) {
					reply = column;
					break;
				}
			}
			return reply;
		}
	}

	/*
	 * Inner class used to track the state of the movement buttons in
	 * the task listing toolbar.
	 */
	private class MoveStates {
		private boolean m_canMoveDown;	//
		private boolean m_canMoveLeft;	//
		private boolean m_canMoveRight;	//
		private boolean m_canMoveUp;	//

		/**
		 * Constructor method.
		 */
		public MoveStates() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean canMoveAll()   {return (m_canMoveDown && m_canMoveLeft && m_canMoveRight && m_canMoveUp);}
		public boolean canMoveAny()   {return (m_canMoveDown || m_canMoveLeft || m_canMoveRight || m_canMoveUp);}
		public boolean canMoveDown()  {return m_canMoveDown; }
		public boolean canMoveLeft()  {return m_canMoveLeft; }
		public boolean canMoveRight() {return m_canMoveRight;}
		public boolean canMoveUp()    {return m_canMoveUp;   }
		
		/**
		 * Set'er methods.
		 * 
		 * @param canMoveDown
		 */
		public void setCanMoveDown( boolean canMoveDown)  {m_canMoveDown  = canMoveDown; }
		public void setCanMoveLeft( boolean canMoveLeft)  {m_canMoveLeft  = canMoveLeft; }
		public void setCanMoveRight(boolean canMoveRight) {m_canMoveRight = canMoveRight;}
		public void setCanMoveUp(   boolean canMoveUp)    {m_canMoveUp    = canMoveUp;   }
		
		/**
		 * Clears all the movement flags in the object.
		 */
		public void reset() {
			m_canMoveDown  =
			m_canMoveLeft  =
			m_canMoveRight =
			m_canMoveUp    = false;
		}
	}
	
	/*
	 * Inner class used to show a popup message while a potentially
	 * long running process is in progress.
	 */
	private class ProcessActive {
		private boolean					m_processActive;		// true -> The process is still active.  false -> It's no longer active.
		private ProcessActiveWidgets	m_processActiveWidgets;	// The widgets used to hold the process active message.
		private Timer   				m_processActiveTimer;	// Timer used to delay showing the process active message.

		/**
		 * Class constructor.
		 * 
		 * @param processActiveMessage
		 */
		ProcessActive(ProcessActiveWidgets widgets, String processActiveMessage, int delay) {
			m_processActive        = true;
			m_processActiveWidgets = widgets;
			setMessage(processActiveMessage);
			runIt(delay);
		}
		
		@SuppressWarnings("unused")
		ProcessActive(ProcessActiveWidgets widgets, String processActiveMessage) {
			// Always use the initial form of the constructor.
			this(widgets, processActiveMessage, PROCESS_ACTIVE_DELAY);
		}
		
		/**
		 * Kills the process active message.
		 */
		public void killIt() {
			if (null != m_processActiveTimer) {
				m_processActiveTimer.cancel();
				m_processActiveTimer = null;
			}
			m_processActive = false;
			m_processActiveWidgets.getProcessActiveDIV().setVisible(false);
		}

		/**
		 * Runs the process active message.
		 */
		public void runIt(int delay) {
			// If we don't have a delay...
			if (0 == delay) {
				// ...simply show it.
				showIt();
			}
			else {
				// ...otherwise, delay showing it for the give number
				// ...of milliseconds.
				if (null == m_processActiveTimer) {
					m_processActiveTimer = new Timer() {
						@Override
						public void run() {
							// If the process is still active...
							if (m_processActive) {
								// ...show the message.
								showIt();
							}
						}
					};
				}			
				m_processActiveTimer.schedule(delay);
			}
		}
		
		@SuppressWarnings("unused")
		public void runIt() {
			runIt(PROCESS_ACTIVE_DELAY);
		}

		/**
		 * Called to change the text being displayed in the process
		 * active message.
		 * 
		 * @param text
		 */
		public void setMessage(String processActiveMessage) {
			// Simply store the message in the process active label.
			m_processActiveWidgets.getProcessActiveLabel().setText(processActiveMessage);
		}
		
		/*
		 * Shows the process active message.
		 */
		private void showIt()
		{
			// Center the message...
			FlowPanel fp = m_processActiveWidgets.getProcessActiveDIV();
			int width = getWidget().getOffsetWidth();
			int left  = (((width - fp.getOffsetWidth()) / 2) - 40);
			fp.getElement().getStyle().setProperty("left", (Integer.toString(left) + "px"));
			
			// ...and show it.
			fp.setVisible(true);
		}
	}

	/*
	 * Inner class used to encapsulate the widgets for the
	 * ProcessActive object. 
	 */
	private class ProcessActiveWidgets {
		private FlowPanel	m_processActiveDIV;		// The <DIV>  containing the process active message.
		private InlineLabel m_processActiveLabel;	// The <SPAN> containing the actual text.
		
		/**
		 * Class constructor.
		 */
		ProcessActiveWidgets() {
			m_processActiveDIV   = new FlowPanel();
			m_processActiveLabel = new InlineLabel();
			m_processActiveDIV.add(m_processActiveLabel);
			m_processActiveDIV.addStyleName("gwtTaskList_processActive");
			Image busyImg = GwtClientHelper.buildImage(m_images.busyAnimation_small());
			m_processActiveDIV.add(busyImg);
			m_processActiveDIV.setVisible(false);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public FlowPanel   getProcessActiveDIV()   {return m_processActiveDIV;  }
		public InlineLabel getProcessActiveLabel() {return m_processActiveLabel;}
	}
	
	/*
	 * Inner class to used to track information attached to a
	 * TaskListItem for managing the user interface. 
	 */
	private static class UIData {
		private boolean		m_taskSelected;				//
		private Anchor		m_taskUnseenAnchor;			//
		private CheckBox	m_taskSelectorCB;			//
		private FlowPanel	m_taskOrderPanel;			//
		private Image		m_taskNewTaskMenuImage;		//
		private Image		m_taskPercentDoneImage;		//
		private Image		m_taskPriorityImage;		//
		private Image		m_taskStatusImage;			//
		private InlineLabel m_taskCompletedLabel;		//
		private InlineLabel	m_taskLabel;				//
		private int			m_taskDepth;				//
		private int 		m_taskOrder    = (-1);		//
		private int 		m_taskTopOrder = (-1);		//
		private int 		m_taskRow;					//
		private Label		m_taskOrderAnchor;			//
		private TextBox		m_taskOrderTextBox;			//
		private Widget      m_taskPercentDoneWidget;	//
		
		/**
		 * Class constructor.
		 */
		UIData(UIData baseUID) {
			// If we have a base UIData that we're constructing this
			// from...
			if (null != baseUID) {
				// ...copy the fields that we maintain between them.
				m_taskSelected = baseUID.m_taskSelected;
				m_taskOrder    = baseUID.m_taskOrder;
			}
		}
		
		UIData() {
			this(null);
		}

		/**
		 * Get'er / Set'er methods.
		 * 
		 * @param
		 * 
		 * @return
		 */
		public boolean     getTaskSelected()          {return m_taskSelected;         }
		public Anchor      getTaskUnseenAnchor()      {return m_taskUnseenAnchor;     }
		public CheckBox    getTaskSelectorCB()        {return m_taskSelectorCB;       }
		public FlowPanel   getTaskOrderPanel()        {return m_taskOrderPanel;       }
		public Image       getTaskNewTaskMenuImage()  {return m_taskNewTaskMenuImage; }
		public Image       getTaskPercentDoneImage()  {return m_taskPercentDoneImage; }
		public Image       getTaskPriorityImage()     {return m_taskPriorityImage;    }
		public Image       getTaskStatusImage()       {return m_taskStatusImage;      }
		public InlineLabel getTaskCompletedLabel()    {return m_taskCompletedLabel;   }
		public InlineLabel getTaskLabel()             {return m_taskLabel;            }
		public int         getTaskDepth()             {return m_taskDepth;            }
		public int         getTaskOrder()             {return m_taskOrder;            }
		public int         getTaskTopOrder()          {return m_taskTopOrder;         }
		public int         getTaskRow()               {return m_taskRow;              }
		public Label       getTaskOrderAnchor()       {return m_taskOrderAnchor;      }
		public TextBox     getTaskOrderTextBox()      {return m_taskOrderTextBox;     }
		public Widget      getTaskPercentDoneWidget() {return m_taskPercentDoneWidget;}

		public void setTaskSelected(         boolean     taskSelected)          {m_taskSelected          = taskSelected;         }
		public void setTaskUnseenAnchor(     Anchor      taskUnseenAnchor)      {m_taskUnseenAnchor      = taskUnseenAnchor;     }
		public void setTaskSelectorCB(       CheckBox    taskSelectorCB)        {m_taskSelectorCB        = taskSelectorCB;       }
		public void setTaskOrderPanel(       FlowPanel   taskOrderPanel)        {m_taskOrderPanel        = taskOrderPanel;       }
		public void setTaskNewTaskMenuImage( Image       taskNewTaskMenuImage)  {m_taskNewTaskMenuImage  = taskNewTaskMenuImage; }
		public void setTaskPercentDoneImage( Image       taskPercentDoneImage)  {m_taskPercentDoneImage  = taskPercentDoneImage; }
		public void setTaskPriorityImage(    Image       taskPriorityImage)     {m_taskPriorityImage     = taskPriorityImage;    }
		public void setTaskStatusImage(      Image       taskStatusImage)       {m_taskStatusImage       = taskStatusImage;      }
		public void setTaskCompletedLabel(   InlineLabel taskCompletedLabel)    {m_taskCompletedLabel    = taskCompletedLabel;   }
		public void setTaskLabel(            InlineLabel taskLabel)             {m_taskLabel             = taskLabel;            }
		public void setTaskDepth(            int         taskDepth)             {m_taskDepth             = taskDepth;            }
		public void setTaskOrder(            int         taskOrder)             {m_taskOrder             = taskOrder;            }
		public void setTaskTopOrder(         int         taskTopOrder)          {m_taskTopOrder          = taskTopOrder;         }
		public void setTaskRow(              int         taskRow)               {m_taskRow               = taskRow;              }
		public void setTaskOrderAnchor(      Label       taskOrderAnchor)       {m_taskOrderAnchor       = taskOrderAnchor;      }
		public void setTaskOrderTextBox(     TextBox     taskOrderTextBox)      {m_taskOrderTextBox      = taskOrderTextBox;     }
		public void setTaskPercentDoneWidget(Widget      taskPercentDoneWidget) {m_taskPercentDoneWidget = taskPercentDoneWidget;}
		
		/**
		 * Returns true if the task corresponding to this UIData is
		 * selected (i.e., its checkbox is checked) and false
		 * otherwise.
		 * 
		 * @return
		 */
		public boolean isTaskCBChecked() {
			CheckBox cb = getTaskSelectorCB();
			boolean reply = ((null != cb) && cb.getValue());
			return reply;
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
		
		// ...initialize the JSNI mouse event handlers...
		jsInitTaskMouseEventHandlers(this);
		
		// ...create the popup menus we'll need for the TaskTable.
		List<TaskMenuOption> pOpts = new ArrayList<TaskMenuOption>();
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_CRITICAL, m_images.p1(), m_messages.taskPriority_p1()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_HIGH,     m_images.p2(), m_messages.taskPriority_p2()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_MEDIUM,   m_images.p3(), m_messages.taskPriority_p3()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_LOW,      m_images.p4(), m_messages.taskPriority_p4()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_LEAST,    m_images.p5(), m_messages.taskPriority_p5()));
		m_priorityMenu = new TaskPopupMenu(this, TeamingEvents.TASK_SET_PRIORITY, pOpts);

		List<TaskMenuOption> sOpts = new ArrayList<TaskMenuOption>();
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_COMPLETED,    m_images.completed(),   m_messages.taskStatus_completed()));
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_IN_PROCESS,   m_images.inProcess(),   m_messages.taskStatus_inProcess()));
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_NEEDS_ACTION, m_images.needsAction(), m_messages.taskStatus_needsAction()));
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_CANCELED,     m_images.cancelled(),   m_messages.taskStatus_cancelled()));
		m_statusMenu = new TaskPopupMenu(this, TeamingEvents.TASK_SET_STATUS, sOpts);

		List<TaskMenuOption> pdOpts = new ArrayList<TaskMenuOption>();
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_0,   m_images.c0(),   m_messages.taskCompleted_c0()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_10,  m_images.c10(),  m_messages.taskCompleted_c10()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_20,  m_images.c20(),  m_messages.taskCompleted_c20()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_30,  m_images.c30(),  m_messages.taskCompleted_c30()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_40,  m_images.c40(),  m_messages.taskCompleted_c40()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_50,  m_images.c50(),  m_messages.taskCompleted_c50()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_60,  m_images.c60(),  m_messages.taskCompleted_c60()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_70,  m_images.c70(),  m_messages.taskCompleted_c70()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_80,  m_images.c80(),  m_messages.taskCompleted_c80()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_90,  m_images.c90(),  m_messages.taskCompleted_c90()));
		pdOpts.add(new TaskMenuOption(TaskInfo.COMPLETED_100, m_images.c100(), m_messages.taskCompleted_c100()));
		m_percentDoneMenu = new TaskPopupMenu(this, TeamingEvents.TASK_SET_PERCENT_DONE, pdOpts);
		
		List<TaskMenuOption> ntOpts = new ArrayList<TaskMenuOption>();
		ntOpts.add(new TaskMenuOption(TaskDisposition.BEFORE.toString(),  m_messages.taskNewAbove()));
		ntOpts.add(new TaskMenuOption(TaskDisposition.AFTER.toString(),   m_messages.taskNewBelow()));
		ntOpts.add(new TaskMenuOption(TaskDisposition.SUBTASK.toString(), m_messages.taskNewSubtask()));
		m_newTaskMenu = new TaskPopupMenu(this, TeamingEvents.TASK_NEW_TASK, ntOpts);

		// ...calculate the size we'll use for marker's throughout the
		// ...task table...
		m_markerSize = Math.max(
			m_images.completed().getWidth(),
			Math.max(
				m_images.unread().getWidth(),
				SPACER_SIZE));

		// ...create the FlexTable that's to hold everything...
		m_flexTable   = new FlexTable();
		m_flexTableCF = m_flexTable.getFlexCellFormatter();
		m_flexTableRF = m_flexTable.getRowFormatter();
		m_flexTable.addStyleName("gwtTaskList_objlist2");
		m_flexTable.setCellPadding(0);
		m_flexTable.setCellSpacing(0);

		// ...create the widgets to hold a process active message...
		m_processActiveWidgets = new ProcessActiveWidgets();
		m_taskListing.getTaskListingDIV().add(m_processActiveWidgets.getProcessActiveDIV());
		
		// ...create the event handlers that we'll need repeatedly in
		// ...the rows rendered in the table.
		createEventHandlers();
		
		// ...and use it to initialize the TaskTable Composite.
		super.initWidget(m_flexTable);
	}

	/*
	 * Defines the header row in the TaskTable.
	 */
	private void addHeaderRow() {
		// Add the header style to the row and render the column
		// headers.
		m_flexTableRF.addStyleName(0, "columnhead");		
		for (Column col:  Column.values()) {
			renderHeader(col);
		}
	}

	/**
	 * Called from the task disposition dialog to apply the selection.
	 * 
	 * @param disposition
	 * @param newTaskId
	 * @param selectedTaskId
	 * @param updateAllDates
	 */
	public void applyTaskDisposition(TaskDisposition disposition, Long newTaskId, Long selectedTaskId, boolean updateAllDates) {		
		switch (disposition) {
		default:
		case APPEND:
			// Nothing to do.  Tasks are appended by default.
			return;
			
		case BEFORE:			
		case AFTER:
		case SUBTASK:
			// Find the affected tasks...
			TaskListItem newTask      = TaskListItemHelper.findTask(m_taskBundle, newTaskId     );
			TaskListItem selectedTask = TaskListItemHelper.findTask(m_taskBundle, selectedTaskId);
			
			// ...perform the move...
			switch (disposition) {			
			case BEFORE:
				TaskListItemHelper.moveTaskAbove(m_taskBundle, newTask, selectedTask); 
				break;
				
			case SUBTASK:
			case AFTER:
				TaskListItemHelper.moveTaskBelow(m_taskBundle, newTask, selectedTask);
				if (TaskDisposition.SUBTASK == disposition) {
					TaskListItemHelper.moveTaskRight(m_taskBundle, newTask);
				}
				break;
			}
			
			// ...and refresh the list.
			List<TaskListItem> newTaskList;
			if (updateAllDates) {
				newTaskList = null;
			}
			else {
				newTaskList = new ArrayList<TaskListItem>();
				newTaskList.add(newTask);
			}
			handleTaskPostMove(buildProcessActive(m_messages.taskProcess_move()), newTaskList);
		}		
	}
	
	public void applyTaskDisposition(TaskDisposition disposition, Long newTaskId, Long selectedTaskId) {
		// Always use the initial form of the method.
		applyTaskDisposition(disposition, newTaskId, selectedTaskId, false);	// false -> Don't update all dates, just specific ones.
	}
	
	/**
	 * Called from the task disposition dialog to apply the selection.
	 * 
	 * @param newDueDate
	 * @param selectedTaskId
	 */
	public void applyTaskDueDate(TaskEvent newDueDate, Long selectedTaskId) {
		final TaskListItem selectedTask = TaskListItemHelper.findTask(m_taskBundle, selectedTaskId);
		final TaskInfo ti = selectedTask.getTask();
		final Long entryId = ti.getTaskId().getEntityId();
		SaveTaskDueDateCmd cmd = new SaveTaskDueDateCmd(ti.getTaskId(), newDueDate);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveTaskDueDate(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Store the new event, as modified by the RPC call, in
				// the task...
				TaskEventRpcResponseData responseData = ((TaskEventRpcResponseData) result.getResponseData());
				ti.setEvent(  responseData.getTaskEvent());
				ti.setOverdue(responseData.isOverdue()   );
				
				// ...force the due date column to redraw...
				UIData uid = getUIData(selectedTask);
				renderColumnDueDate(selectedTask, uid.getTaskRow());

				// ...and update any calculated dates that may be
				// ...affected.
				updateCalculatedDatesAsync(null, ti.getTaskId().getBinderId(), entryId);
			}
		});
	}
	
	/*
	 * Returns a base Label that can be used as an anchor like widget.
	 */
	private Label buildAnchorLabel(List<String> styles) {
		Label reply = new Label();
		for (String style:  styles) {
			reply.addStyleName(style);
		}
		return reply;
	}
	
	private Label buildAnchorLabel(String style) {
		List<String> styles = new ArrayList<String>();
		styles.add(style);
		if (!(style.equals("cursorPointer"))) {
			styles.add("cursorPointer");
		}
		return buildAnchorLabel(styles);
	}
	
	private Label buildAnchorLabel() {
		return buildAnchorLabel("cursorPointer");
	}

	/*
	 * Builds and adds Widgets for an AssignmentInfo to a
	 * VerticalPanel.
	 */
	private void buildAssigneeWidgets(VerticalPanel vp, final AssigneeType assigneeType, final AssignmentInfo ai, final boolean showDisabled) {
		FlowPanel fp = new FlowPanel();
		final Label assignee;
		List<EventHandler> eventHandlers;

		// What type of assignee is this?
		switch (assigneeType) {
		case INDIVIDUAL:
			// Individual assignee!  Generate a PresenceControl...
			GwtPresenceInfo presence = ai.getPresence();
			PresenceControl pc = new PresenceControl(String.valueOf(ai.getId()), String.valueOf(ai.getPresenceUserWSId()), false, false, false, presence);
			pc.setImageAlignment("top");
			pc.addStyleName("displayInline");
			pc.addStyleName("verticalAlignTop");
			pc.setAnchorStyleName("cursorPointer");
			fp.add(pc);			
			EventWrapper.addHandler(pc, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Widget w;					
					w = ((Widget) event.getSource());
					handlePresenceSelect(ai, w.getElement());
				}				
			});

			// ...and a name link for it...
			assignee = new Label(ai.getTitle());
			String addedStyle = (showDisabled ? "gwtTaskList_assigneeDisabled" : "gwtTaskList_assigneeEnabled");
			assignee.addStyleName("gwtTaskList_assignee " + addedStyle);
			fp.add(assignee);
			vp.add(fp);
			
			// ...and add the event handlers for it.
			eventHandlers = new ArrayList<EventHandler>();
			eventHandlers.add(m_assigneeMouseOverEvent);
			eventHandlers.add(m_assigneeMouseOutEvent );
			eventHandlers.add(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					assignee.removeStyleName("gwtTaskList_assigneeHover");
					handlePresenceSelect(ai, assignee.getElement());
				}
			});
			EventWrapper.addHandlers(assignee, eventHandlers);
			
			break;
		
		case GROUP:
		case TEAM:
			// Group or team assignee!
			Image assigneeImg = GwtClientHelper.buildImage(m_taskListing.getRequestInfo().getImagesPath() + ai.getPresenceDude());
			fp.add(assigneeImg);

			int    members       = ai.getMembers();
			String membersString = m_messages.taskMemberCount(String.valueOf(members));
			String assigneeLabel = (ai.getTitle() + " " + membersString);
			assignee = new Label(assigneeLabel);
			assignee.addStyleName("gwtTaskList_assignee");
			fp.add(assignee);
			vp.add(fp);
			
			// Does the group/team have any members?
			if (0 < members) {
				// Yes!
				final FlowPanel expansionFP = new FlowPanel();
				expansionFP.addStyleName("gwtTaskList_showGroupListHidden marginleft2");
				vp.add(expansionFP);
				
				// Add event handlers so the user can see them.
				eventHandlers = new ArrayList<EventHandler>();
				eventHandlers.add(m_assigneeMouseOverEvent);
				eventHandlers.add(m_assigneeMouseOutEvent );
				eventHandlers.add(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						assignee.removeStyleName("gwtTaskList_assigneeHover");
						handleMembershipSelect(assigneeType, ai, expansionFP, showDisabled);
					}
				});
				EventWrapper.addHandlers(assignee, eventHandlers);
			}
			
			break;
		}
	}

	/*
	 * Builds and adds Widgets for a 'More...' link (when there are
	 * more than the number of AssignmentInfo's we display at a time)
	 * to a VerticalPanel.
	 */
	private void buildMoreEllipseWidgets(final VerticalPanel vp, final AssigneeType assigneeType, final AssignmentInfo assignee, final boolean showDisabled) {
		// Create an <A> to hold a 'More...' link for the user to
		// request that addition members be shown.
		final Anchor a = new Anchor();
		String addedStyle = (showDisabled ? "gwtTaskList_assigneeDisabled" : "gwtTaskList_assigneeEnabled");
		a.addStyleName("gwtTaskList_showMoreMembersAnchor " + addedStyle);
		InlineLabel li = new InlineLabel(m_messages.taskShowMore());
		a.getElement().appendChild(li.getElement());
		vp.add(a);

		// Add a ClickHandler to the <A>.
		EventWrapper.addHandler(a, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Remove the previous 'More...' link.
				vp.remove(a);

				// Scan the membership from the point where we left
				// off.
				List<AssignmentInfo> membership   = assignee.getMembership();
				int                  members      = membership.size();
				int                  membersShown = assignee.getMembersShown();
				int                  assignments;
				for (assignments = membersShown; assignments < members; assignments += 1) {
					// Have we displayed enough members yet?
					if (assignments == (membersShown + MEMBERSHIP_ELLIPSE_COUNT)) {
						// Yes!  Add a 'More...' link for the user to
						// request more. 
						buildMoreEllipseWidgets(
							vp,
							assigneeType,
							assignee,
							showDisabled);
						
						break;
					}
					
					else {
						// No, we haven't displayed enough members yet!
						// Display another one.
						buildAssigneeWidgets(
							vp,
							assigneeType,
							membership.get(assignments),
							showDisabled);
					}
				}
				
				// Update the number of members that we've got
				// displayed.
				assignee.setMembersShown(assignments);
			}
		});
	}
	
	/*
	 * Returns a MoveStates object based on the state of the tasks in a
	 * task list.
	 */
	public MoveStates buildMoveStates(List<TaskListItem> taskList) {
		// If there's nothing in the list...
		MoveStates reply = new MoveStates();
		int taskCount = ((null == taskList) ? 0 : taskList.size());
		if (0 == taskCount) {
			// ...nothing can be moved.  Return the base object.
			return reply;
		}

		// Scan the items in the list...
		for (TaskListItem task:  taskList) {
			// ...setting the move states as appropriate.
			if (TaskListItemHelper.canMoveTaskDown( m_taskBundle, task)) reply.setCanMoveDown( true);
			if (TaskListItemHelper.canMoveTaskLeft( m_taskBundle, task)) reply.setCanMoveLeft( true);
			if (TaskListItemHelper.canMoveTaskRight(m_taskBundle, task)) reply.setCanMoveRight(true);
			if (TaskListItemHelper.canMoveTaskUp(   m_taskBundle, task)) reply.setCanMoveUp(   true);

			// Once all the move states are set...
			if (reply.canMoveAll()) {
				// ...we don't need to look at any more.
				break;
			}
		}

		// If we get here, reply removes the the MoveStates object
		// based on the task list.  Return it.
		return reply;
	}
	/*
	 * Build a column that contains a task option Widget. 
	 */
	private Widget buildOptionColumn(final TaskListItem task, final TaskPopupMenu taskMenu, String optionValue, String anchorStyle) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
				
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
		Image img = GwtClientHelper.buildImage(selectedOption.getMenuImageRes(), selectedOption.getMenuAlt());
		final Element imgElement = img.getElement();
		if      (taskMenu == m_priorityMenu)    uid.setTaskPriorityImage(   img);
		else if (taskMenu == m_statusMenu)      uid.setTaskStatusImage(     img);
		else if (taskMenu == m_percentDoneMenu) uid.setTaskPercentDoneImage(img);

		// Does the user have rights to modify this task?
		Widget reply;
		if (task.getTask().getCanModify()) {	
			// Yes!  Generate the Anchor for this option.
			Anchor  a  = GwtClientHelper.buildAnchor(anchorStyle);
			Element aE = a.getElement();
			aE.appendChild(imgElement);
			aE.appendChild(GwtClientHelper.buildImage(m_images.menu()).getElement());
			aE.setAttribute(ATTR_ENTRY_ID, String.valueOf(task.getTask().getTaskId().getEntityId()));
			aE.setAttribute(ATTR_OPTION_MENU, taskMenu.getTaskEventEnum().toString());
			EventWrapper.addHandler(a, m_taskOptionClickHandler);
			reply = a;
		}
		
		else {
			// No, the user doesn't have rights to modify this task!
			// Show the image statically.
			InlineLabel il  = new InlineLabel();
			Element     ilE = il.getElement();
			ilE.appendChild(imgElement);
			ilE.appendChild(buildSpacer().getElement());
			il.addStyleName(anchorStyle);
			reply = il;
		}

		// If we get here, reply refers to the Widget for this option
		// column.  Return it.
		return reply;
	}

	/*
	 * Constructs and returns a ProcessActive object containing the
	 * given message.  The message will be delayed being show for the
	 * given number of milliseconds.
	 */
	private ProcessActive buildProcessActive(String processActiveMessage, int delay) {
		ProcessActive reply;
		if (PROCESS_ACTIVE_RENDER_TIME <= m_renderTime) {
			reply = new ProcessActive(
				m_processActiveWidgets,
				processActiveMessage,
				delay);
		}
		else {
			reply = null;
		}		
		return reply;
	}
	
	private ProcessActive buildProcessActive(String processActiveMessage) {
		// Always use the initial form of the method.
		return buildProcessActive(processActiveMessage, PROCESS_ACTIVE_DELAY);
	}
	
	/*
	 * Returns a spacer Image.
	 */
	private Image buildSpacer(int height, int width) {
		Image reply = GwtClientHelper.buildImage(m_images.spacer());
		reply.setHeight(String.valueOf(height) + "px");
		reply.setWidth( String.valueOf(width ) + "px");
		return reply;
	}
	
	private Image buildSpacer(int width) {
		return buildSpacer(SPACER_SIZE, width);
	}

	private Image buildSpacer() {
		return buildSpacer(SPACER_SIZE, SPACER_SIZE);
	}

	/*
	 * Returns true if based on the current environment, linkage
	 * changes can be persisted and false otherwise.
	 */
	private boolean canPersistLinkage() {
		// Did the TaskBundle tells us to respect the task linkage
		// information?
		boolean reply = m_taskBundle.respectLinkage();
		if (reply) {
			// Yes!  Does the user have rights to save linkage on this
			// binder?
			reply = m_taskBundle.getCanModifyTaskLinkage();
		}
		
		// If we get here, reply is true if linkage can be saved and
		// false otherwise.  Return it.
		return reply;
	}
	
	/*
	 * Called to clear the contents of the TaskTable.
	 */
	private void clearTaskTable() {
		m_flexTable.removeAllRows();
		addHeaderRow();
	}

	/*
	 * Creates the event handlers that we'll need repeatedly as we
	 * render rows in the table.
	 */
	private void createEventHandlers() {
		// Even handler used on mouse outs of an assignee.
		m_assigneeMouseOutEvent = new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				((Widget) event.getSource()).removeStyleName("gwtTaskList_assigneeHover");
			}
		};
		
		// Even handler used on mouse overs of an assignee.
		m_assigneeMouseOverEvent = new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				((Widget) event.getSource()).addStyleName("gwtTaskList_assigneeHover");
			}
		};
		
		// Event handler used when the user clicks on a task's
		// selection check box.
		m_cbClickHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				CheckBox cb;					
				cb = ((CheckBox) event.getSource());
				handleTaskSelect(getTaskFromEventWidget(cb), cb.getValue());
			}			
		};

		// Event handler used when the user clicks on a column header.
		m_columnClickHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				Widget w = ((Widget) event.getSource());
				String colSortKey = w.getElement().getAttribute(ATTR_COLUMN_SORT_KEY);
				Column col = Column.mapSortKeyToColumn(colSortKey);
				handleTableResort(col);
			}			
		};

		// Event handler used when the user clicks on a task's due
		// date.
		m_dueDateClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Anchor ddA = ((Anchor) event.getSource());
				handleTaskChangeDueDateAsync(ddA, getTaskFromEventWidget(ddA));
			}				
		};
		
		// Event handler used when the user clicks on a task's
		// expand / collapse widget.
		m_expanderClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleTaskExpander(getTaskFromEventWidget((Widget) event.getSource()));
			}				
		};
		
		// Event handler used when the user clicks on the new task
		// link to create a new task.
		m_newTaskClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleNewTaskMenu(getTaskFromEventWidget((Widget) event.getSource()));
			}
		};
		
		
		// Event handler used when the user clicks on a task's location
		// link to go to that folder.
		m_taskLocationClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleTaskLocation(getTaskFromEventWidget((Widget) event.getSource()));
			}
		};
		
		// Event handler used when the user clicks on one of a task's
		// option menus.
		m_taskOptionClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Find the task from the event...
				Widget w = ((Widget) event.getSource());
				TaskListItem task = getTaskFromEventWidget(w);
				UIData uid = getUIData(task);
				
				// ...decide on the appropriate menu for the event...
				TaskPopupMenu taskMenu;
				Image taskMenuImg;
				String optionEvent = w.getElement().getAttribute(ATTR_OPTION_MENU);
				if      (optionEvent.equals(TeamingEvents.TASK_SET_PRIORITY.toString()))     {taskMenu = m_priorityMenu;    taskMenuImg = uid.getTaskPriorityImage();   }
				else if (optionEvent.equals(TeamingEvents.TASK_SET_PERCENT_DONE.toString())) {taskMenu = m_percentDoneMenu; taskMenuImg = uid.getTaskPercentDoneImage();}
				else if (optionEvent.equals(TeamingEvents.TASK_SET_STATUS.toString()))       {taskMenu = m_statusMenu;      taskMenuImg = uid.getTaskStatusImage();     }
				else                                                                         {return;}
				
				// ...and run the menu.
				taskMenu.showTaskPopupMenu(task, taskMenuImg);
			}
		};
		
		// Event handler used when the focus leave a task's order
		// number edit widget.
		m_taskOrderBlurHandler = new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				handleTaskOrderBlur(getTaskFromEventWidget((Widget) event.getSource()));
			}
		};
		
		// Event handler used when the user clicks on a task's order
		// number link edit the tasks order.
		m_taskOrderClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleTaskOrderEdit(getTaskFromEventWidget((Widget) event.getSource()));
			}
		};
		
		// Event handler used when the user types into a task's order
		// number edit widget.
		m_taskOrderKeyPressHandler = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				handleTaskOrderKeyPress(getTaskFromEventWidget((Widget) event.getSource()), event);
			}
		};
		
		// Event handler used when the user clicks on a task's unseen
		// star burst.
		m_taskSeenClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleTaskSeen(getTaskFromEventWidget((Widget) event.getSource()));
			}
		};
		
		// Event handler used when the user clicks on a task's name
		// link to run the entry viewer.
		m_taskViewClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleTaskView(getTaskFromEventWidget((Widget) event.getSource()));
			}
		};
	}

	/*
	 * Returns in the index to display a column at.
	 */
	private int getColumnIndex(Column col) {
		int reply = col.ordinal();
		if ((Column.ORDER.ordinal() < col.ordinal()) && (!(showOrderColumn()))) {
			reply -= 1;
		}
		return reply;
	}
	
	/*
	 * Given an Element from an event, returns it's corresponding task.
	 */
	private TaskListItem getTaskFromEventElement(Element e) {
		String entryId = e.getAttribute(ATTR_ENTRY_ID);
		return TaskListItemHelper.findTask(m_taskBundle, Long.parseLong(entryId));
	}
	
	/*
	 * Given a Widget from an event, returns it's corresponding task.
	 */
	private TaskListItem getTaskFromEventWidget(Widget w) {
		return getTaskFromEventElement(w.getElement());
	}
	
	/*
	 * Returns a List<EntityId> of the IDs of the tasks in the TaskTable
	 * that are currently checked.
	 */
	private List<EntityId> getTaskIdsChecked() {
		List<EntityId> reply = new ArrayList<EntityId>();
		getTaskIdsCheckedImpl(m_taskBundle.getTasks(), reply);
		return reply;
	}
	
	private void getTaskIdsCheckedImpl(List<TaskListItem> tasks, List<EntityId> checkedTaskIds) {
		for (TaskListItem task:  tasks) {
			if (getUIData(task).isTaskCBChecked()) {
				checkedTaskIds.add(task.getTask().getTaskId());
			}
			getTaskIdsCheckedImpl(task.getSubtasks(), checkedTaskIds);
		}
	}

	/*
	 * Returns a List<Long> of just the entry IDs from a List<EntityId>.
	 */
	private List<Long> getTaskIdsCheckedAsListLong() {
		List<EntityId> entryIds = getTaskIdsChecked();
		List<Long> reply = new ArrayList<Long>();
		for (EntityId entryId:  entryIds) {
			reply.add(entryId.getEntityId());
		}
		return reply;
	}
	
	/**
	 * Returns the order number from the given TaskListItem.
	 *
	 * @param task
	 * 
	 * @return
	 */
	public static int getTaskOrder(TaskListItem task) {
		return getUIData(task).getTaskOrder();
	}

	/**
	 * Returns the order number from the top most task containing the
	 * given TaskListItem.
	 *
	 * @param task
	 * 
	 * @return
	 */
	public static int getTaskTopOrder(TaskListItem task) {
		return getUIData(task).getTaskTopOrder();
	}

	/*
	 * Returns a List<TaskListItem> of the tasks in the TaskTable that
	 * are currently checked.
	 */
	private List<TaskListItem> getTasksChecked() {
		List<TaskListItem> reply = new ArrayList<TaskListItem>();
		getTasksCheckedImpl(m_taskBundle.getTasks(), reply);
		return reply;
	}
	
	private void getTasksCheckedImpl(List<TaskListItem> tasks, List<TaskListItem> checkedTasks) {
		for (TaskListItem task:  tasks) {
			if (getUIData(task).isTaskCBChecked()) {
				checkedTasks.add(task);
			}
			getTasksCheckedImpl(task.getSubtasks(), checkedTasks);
		}
	}

	/*
	 * Returns a List<String> of the reasons why task hierarchy
	 * manipulation is disabled.
	 */
	private List<String> getToolWarnings() {
		List<String> reasons = new ArrayList<String>();
		
		if (m_taskBundle.getIsFiltered())                         reasons.add(m_messages.taskHierarchyDisabled_Filter());
		if (!(m_taskBundle.getCanModifyTaskLinkage()))            reasons.add(m_messages.taskHierarchyDisabled_Rights());
		if ((Column.ORDER != m_sortColumn) || (!m_sortAscending)) reasons.add(m_messages.taskHierarchyDisabled_Sort());
		if (!(m_taskBundle.getIsFromFolder()))                    reasons.add(m_messages.taskHierarchyDisabled_Virtual());
		
		return reasons;
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

	/*
	 * Processes the information from the server and sets up the
	 * initial sorting.
	 */
	private void initializeSorting() {
		// Process the sort criteria from the TaskList...
		m_sortAscending = (!(m_taskListing.getSortDescend()));
		m_sortColumn    = Column.mapSortKeyToColumn(m_taskListing.getSortBy());
		if (null == m_sortColumn) {
			m_sortColumn    = Column.ORDER;
			m_sortAscending = true;
		}
		else if ((Column.LOCATION == m_sortColumn) && m_taskBundle.getIsFromFolder()) {
			m_sortColumn = Column.ORDER;
		}

		// ...accounting for cases where we don't show the order
		// ...column...
		if ((!(showOrderColumn())) && (Column.ORDER == m_sortColumn)) {
			m_sortColumn = Column.TASK_NAME;
		}

		// ...and apply it, as necessary.
		if ((Column.ORDER != m_sortColumn) || (!(m_sortAscending))) {
			sortByColumn(Column.ORDER);
		}
	}
	
	/*
	 * (Re)initializes the UIData objects on the tasks.
	 */
	private void initializeUIData(boolean updateOrder) {
		initializeUIDataImpl(
			m_taskBundle.getTasks(),
			1,	// Order starts at 1...
			0,	// ...at depth 0.
			updateOrder);
	}
	
	private void initializeUIDataImpl(List<TaskListItem> tasks, int taskOrder, int taskDepth, boolean updateOrder) {
		boolean topTask = (0 == taskDepth);
		for (TaskListItem task:  tasks) {
			// Build a UIData object for this TaskListItem...
			UIData newUID = new UIData(((UIData) task.getUIData()));
			task.setUIData(newUID);
			newUID.setTaskDepth(taskDepth);
			if (updateOrder) {
				if (topTask) {
					newUID.setTaskTopOrder(taskOrder);
					newUID.setTaskOrder(   taskOrder);
					taskOrder += 1;
				}
				else {
					newUID.setTaskTopOrder(taskOrder - 1);	// Net effect is to store the order from top most task containing this subtask.
					newUID.setTaskOrder(             - 1);	// A subtask's order is always -1.
				}
			}

			// ...and build the UIData's for any subtasks.
			initializeUIDataImpl(
				task.getSubtasks(),
				taskOrder,
				(taskDepth + 1),
				updateOrder);
		}
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by the task
	 * listing.
	 * 
	 * @param event
	 */
	public void handleContributorIdsRequest(ContributorIdsRequestEvent event) {
		// If we're embedded in JSP...
		if (m_taskListing.isEmbeddedInJSP()) {
			// ...contributor IDs will have been handled by the
			// ...controller.  Simply bail.
			return;
		}
		
		// Is the event targeted to this folder?
		final Long eventBinderId = event.getBinderId();
		if (eventBinderId.equals(m_taskListing.getBinderId())) {
			// Yes!  Asynchronously fire the corresponding reply event
			// with the contributor IDs.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(
						new ContributorIdsReplyEvent(
							eventBinderId,
							TaskListItemHelper.findContributorIds(
								m_taskBundle.getTasks())));
				}
			});
		}
	}
	
	/*
	 * Called when the membership of a group or team has been
	 * determined to display the members.
	 */
	private void handleMembershipDisplay(AssignmentInfo assignee, List<AssignmentInfo> assignees, FlowPanel expansionFP, boolean showDisabled) {
		// Create a VerticalPanel to display the membership in.
		VerticalPanel vp = new VerticalPanel();
		vp.addStyleName("gwtTaskList_assigneesList");
		
		// Scan the AssignmentInfo's.
		int assignments = 0;
		for (AssignmentInfo ai:  assignees) {
			// What type of assignee is this?
			AssigneeType assigneeType =
				((null == ai.getPresence()) ?
					AssigneeType.GROUP      :
					AssigneeType.INDIVIDUAL);

			// Have we displayed enough members yet?
			if (MEMBERSHIP_ELLIPSE_COUNT == assignments) {
				// Yes!  Add a 'More...' link for the user to request
				// more.
				buildMoreEllipseWidgets(
					vp,
					assigneeType,
					assignee,
					showDisabled);
				
				break;
			}
			else {
				// No, we haven't displayed enough members yet!
				// Display another one.
				assignments += 1;
				buildAssigneeWidgets(
					vp,
					assigneeType,
					ai,
					showDisabled);
			}
		}
		
		// Store the number of members that we've got displayed...
		assignee.setMembersShown(assignments);
		
		// ...and display the <DIV> containing them.
		expansionFP.clear();
		expansionFP.add(vp);		
		expansionFP.removeStyleName("gwtTaskList_showGroupListHidden");
		expansionFP.addStyleName(   "gwtTaskList_showGroupList"      );
	}
	
	/*
	 * Called when the user clicks on the link to show a group or
	 * team's membership.
	 */
	private void handleMembershipSelect(AssigneeType assigneeType, final AssignmentInfo ai, final FlowPanel expansionFP, final boolean showDisabled) {
		// Is the <DIV> that contains the membership currently visible?
		String s = expansionFP.getStyleName();
		boolean visible = (0 > s.indexOf("gwtTaskList_showGroupListHidden"));
		if (visible) {
			// Yes!  Then we simply need to hide and empty it.
			expansionFP.removeStyleName("gwtTaskList_showGroupList"      );
			expansionFP.addStyleName(   "gwtTaskList_showGroupListHidden");
			expansionFP.clear();
			
			return;
		}

		// Have we already queried the membership for this
		// AssignmentInfo?
		List<AssignmentInfo> members = ai.getMembership();
		if (null != members) {
			// Yes!  Just re-display it.  No need to read it again.
			handleMembershipDisplay(ai, members, expansionFP, showDisabled);
			return;
		}
		
		final Long assigneeId = ai.getId();
		switch (assigneeType) {
		case GROUP:
			GetGroupAssigneeMembershipCmd groupCmd = new GetGroupAssigneeMembershipCmd(assigneeId);
			GwtClientHelper.executeCommand(groupCmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetGroupMembership(),
						String.valueOf(assigneeId));
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// Store the group membership (so we don't re-read
					// it if it gets displayed again) and display it.
					AssignmentInfoListRpcResponseData responseData = ((AssignmentInfoListRpcResponseData) result.getResponseData());
					List<AssignmentInfo> groupMembers = responseData.getAssignmentInfoList();
					ai.setMembership(           groupMembers                           );
					handleMembershipDisplay(ai, groupMembers, expansionFP, showDisabled);
				}				
			});
			
			break;
			
		case TEAM:
			GetTeamAssigneeMembershipCmd teamCmd = new GetTeamAssigneeMembershipCmd(assigneeId);
			GwtClientHelper.executeCommand(teamCmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetTeamMembership(),
						String.valueOf(assigneeId));
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// Store the team membership (so we don't re-read
					// it if it gets displayed again) and display it.
					AssignmentInfoListRpcResponseData responseData = ((AssignmentInfoListRpcResponseData) result.getResponseData());
					List<AssignmentInfo> teamMembers = responseData.getAssignmentInfoList();
					ai.setMembership(           teamMembers                           );
					handleMembershipDisplay(ai, teamMembers, expansionFP, showDisabled);
				}
			});
			
			break;
		}
	}

	/*
	 * Handles the user clicking the new task menu on a task.
	 */
	private void handleNewTaskMenu(TaskListItem task) {
		// Simply run the new task menu for this task.
		m_newTaskMenu.showTaskPopupMenu(
			task,
			getUIData(task).getTaskNewTaskMenuImage());
	}
	
	/*
	 * This method gets invoked when the user clicks on an individual
	 * assignment's presence dude.
	 */
	private void handlePresenceSelect(AssignmentInfo ai, Element element) {
		// Invoke the Simple Profile dialog.
		Long wsId = ai.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtClientHelper.invokeSimpleProfile(element, String.valueOf(ai.getId()), wsIdS, ai.getTitle());
	}

	/*
	 * Called when the user clicks the select all checkbox.
	 */
	private void handleSelectAll(Boolean checked) {
		// Perform the selection.
		selectAllTasksAsync(checked);
	}

	/*
	 * Called to resort the TaskTable by the specified column.
	 */
	private void handleTableResort(Column col) {
		// Apply the resort...
		Column previousSortColumn = m_sortColumn;
		if (col == m_sortColumn)
			 m_sortAscending = (!m_sortAscending);
		else m_sortColumn    = col;		
		sortByColumn(previousSortColumn);
		
		// ...and redisplay the tasks.
		renderTaskBundle(m_taskBundle);
		persistSortChange();
	}

	/*
	 * Called when the user clicks the due date on a task to
	 * asynchronously run the due date editor dialog.
	 */
	private void handleTaskChangeDueDateAsync(final Anchor dueDateAnchor, final TaskListItem task) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				handleTaskChangeDueDateNow(dueDateAnchor, task);
			}
		});
	}
	
	/*
	 * Called when the user clicks the due date on a task to
	 * synchronously run the due date editor dialog.
	 */
	private void handleTaskChangeDueDateNow(Anchor dueDateAnchor, TaskListItem task) {
		// Run the task due date editing dialog.
		TaskInfo ti = task.getTask();
		if (null == m_dueDateDlg) {
			m_dueDateDlg = new TaskDueDateDlg(this, ti);
			m_dueDateDlg.addStyleName("taskDueDateDlg");
		}
		else {
			m_dueDateDlg.resetDueDateTask(ti);
		}
		m_dueDateDlg.showRelativeTo(dueDateAnchor);
	}
	
	/*
	 * Called when the user presses the delete button on the task tool
	 * bar.
	 */
	private void handleTaskDelete() {
		// If nothing the user can delete is checked...
		final List<TaskListItem> tasksChecked = getTasksChecked();
		int c = ((null == tasksChecked) ? 0 : tasksChecked.size());
		for (int i = (c - 1); i >= 0; i -= 1) {
			TaskListItem task = tasksChecked.get(i);
			if (!(task.getTask().getCanTrash())) {
				tasksChecked.remove(i);
			}
		}
		if (!(GwtClientHelper.hasItems(tasksChecked))) {
			// ...there's nothing to delete.
			return;
		}
		
		// Is the user sure they want to perform the delete?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes!  Delete the selected tasks.
							final List<EntityId> taskIds = TaskListItemHelper.getTaskIdsFromList(tasksChecked, false);
							DeleteEntitiesHelper.deleteSelectedTasksAsync(taskIds, new DeleteEntitiesCallback() {
								@Override
								public void operationCanceled() {
									handleTaskPostRemoveAsync(taskIds);
								}

								@Override
								public void operationComplete() {
									handleTaskPostRemoveAsync(taskIds);
								}
								
								@Override
								public void operationFailed() {
									// Nothing to do.  The delete call
									// will have told the user about
									// the failure.
								}
							});
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.taskConfirmDelete());
			}
		});
	}
	
	/*
	 * Called when the user clicks the expand/collapse on a task.
	 */
	private void handleTaskExpander(final TaskListItem task) {
		// Extract the IDs we need to perform the expand/collapse.
		EntityId taskId   = task.getTask().getTaskId();
		Long    binderId = taskId.getBinderId();
		Long    entryId  = taskId.getEntityId();

		// Are we collapsing the subtasks?
		if (task.getExpandSubtasks()) {
			// Yes!  Collapse them...
			CollapseSubtasksCmd cmd = new CollapseSubtasksCmd(binderId, entryId);
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_CollapseSubtasks());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// ...and mark the task as having its subtasks
					// ...collapsed and redisplay the TaskTable. 
					task.setExpandedSubtasks(false);
					initializeUIData(true);	// true -> Forces the order and depths to be reset.
					renderTaskBundle(m_taskBundle);
				}
			});
		}
		
		else {
			// No, we must be expanding the subtasks!  Expand them...
			ExpandSubtasksCmd cmd = new ExpandSubtasksCmd(binderId, entryId);
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_ExpandSubtasks());
				}

				@Override
				public void onSuccess(VibeRpcResponse result) {
					// ...and mark the task as having its subtasks
					// ...expanded and redisplay the TaskTable. 
					task.setExpandedSubtasks(true);
					initializeUIData(true);	// true -> Forces the order and depths to be reset.
					renderTaskBundle(m_taskBundle);
				}
			});
		}
	}
	
	/*
	 * Called when the user wants to know why task hierarchy
	 * manipulation has been disabled.
	 * 
	 * Shows the dialog asynchronously.
	 */
	private void handleTaskHierarchyDisabledAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				handleTaskHierarchyDisabledNow();
			}
		});
	}
	
	/*
	 * Called when the user wants to know why task hierarchy
	 * manipulation has been disabled.
	 * 
	 * Shows the dialog synchronously.
	 */
	private void handleTaskHierarchyDisabledNow() {
		TaskHierarchyDisabledDlg dlg = new TaskHierarchyDisabledDlg(getToolWarnings());
		dlg.showRelativeTo(m_taskListing.getTaskToolsWarningDIV());
	}
	
	/*
	 * Called to switch to the folder location of a task.
	 */
	private void handleTaskLocation(TaskListItem task) {
		final String folderId = String.valueOf(task.getTask().getTaskId().getBinderId());
		GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd(folderId);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
				String binderPermalink = responseData.getStringValue();
				EventHelper.fireChangeContextEventAsync(folderId, binderPermalink, Instigator.GOTO_CONTENT_URL);
			}
		});
	}

	/*
	 * Called from a JSNI method when the mouse leaves a task.
	 */
	private void handleTaskMouseOut(String taskRowS) {
		int taskRow = Integer.parseInt(taskRowS);
		Element trElement = m_flexTableRF.getElement(taskRow);
		TaskListItem task = getTaskFromEventElement(trElement);
		Image img = getUIData(task).getTaskNewTaskMenuImage();
		img.setUrl(m_images.newTaskButton1().getSafeUri().asString());
	}
	
	/*
	 * Called from a JSNI method when the mouse enters a task.
	 */
	private void handleTaskMouseOver(String taskRowS) {
		int taskRow = Integer.parseInt(taskRowS);
		Element trElement = m_flexTableRF.getElement(taskRow);
		TaskListItem task = getTaskFromEventElement(trElement);
		Image img = getUIData(task).getTaskNewTaskMenuImage();
		img.setUrl(m_images.newTaskButton2().getSafeUri().asString());
	}
	
	/*
	 * Called when the user presses the move down button on the task
	 * tool bar.
	 */
	private void handleTaskMoveDown() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (0 < tasksCheckedCount) {
			// Note:  Unlike the other movers, we must iterate through
			// these from bottom to top.  Otherwise, moving two items
			// that are peers to each other would have no net affect.
			for (int i = (tasksCheckedCount - 1); i >= 0; i -= 1) {
				TaskListItem task = tasksChecked.get(i);
				TaskListItemHelper.moveTaskDown(m_taskBundle, task);
			}
			
			handleTaskPostMove(
				buildProcessActive(m_messages.taskProcess_move()),
				tasksChecked);
		}
	}
	
	/*
	 * Called when the user presses the move left button on the task
	 * tool bar.
	 */
	private void handleTaskMoveLeft() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (0 < tasksCheckedCount) {
			for (TaskListItem task:  tasksChecked) {
				TaskListItemHelper.moveTaskLeft(m_taskBundle, task);
			}
			
			handleTaskPostMove(
				buildProcessActive(m_messages.taskProcess_move()),
				tasksChecked);
		}
	}
	
	/*
	 * Called when the user presses the move right button on the task
	 * tool bar.
	 */
	private void handleTaskMoveRight() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (0 <  tasksCheckedCount) {
			for (TaskListItem task:  tasksChecked) {
				TaskListItemHelper.moveTaskRight(m_taskBundle, task);
			}
			
			handleTaskPostMove(
				buildProcessActive(m_messages.taskProcess_move()),
				tasksChecked);
		}
	}
	
	/*
	 * Called when the user presses the move up button on the task tool
	 * bar.
	 */
	private void handleTaskMoveUp() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (0 < tasksCheckedCount) {
			for (TaskListItem task:   tasksChecked) {
				TaskListItemHelper.moveTaskUp(m_taskBundle, task);
			}
			
			handleTaskPostMove(
				buildProcessActive(m_messages.taskProcess_move()),
				tasksChecked);
		}
	}

	/*
	 * Called when the selects something from the new task menu.
	 */
	private void handleTaskNewTask(TaskListItem task, String newTaskDisposition) {
		jsSetNewTaskDisposition(newTaskDisposition);
		jsSetSelectedTaskId(String.valueOf(task.getTask().getTaskId().getEntityId()));
		String newTaskUrl = m_taskBundle.getNewTaskUrl();
		if (m_taskListing.isEmbeddedInJSP()) {
			GwtClientHelper.jsLaunchToolbarPopupUrl(newTaskUrl);
		}
		else {
			OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
				newTaskUrl,
				Instigator.GOTO_CONTENT_URL);
			
			if (GwtClientHelper.validateOSBI(osbInfo)) {
				GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
			}
		}
	}
	
	/*
	 * Called to order number edit widget looses the focus.
	 */
	private void handleTaskOrderBlur(TaskListItem task) {
		// Hide the task order edit widget...
		UIData uid = getUIData(task);
		TextBox tb = uid.getTaskOrderTextBox();		
		tb.setVisible(false);

		// ...and show the task order anchor.
		uid.getTaskOrderAnchor().setVisible(true);		
	}
	
	/*
	 * Called to edit the order number on the given task.
	 */
	private void handleTaskOrderEdit(TaskListItem task) {
		// Hide the task order anchor.
		UIData uid = getUIData(task);
		Label ta = uid.getTaskOrderAnchor();
		ta.setVisible(false);

		// Have we create the TextBox to edit this task's order
		// number yet? 
		TextBox tb = uid.getTaskOrderTextBox();
		if (null == tb) {
			// No!  Create it now.
			int tasks = m_taskBundle.getTasks().size();
			int vl = 2;
			while (true ){
				tasks = (tasks / 10);
				if (0 == tasks) {
					break;
				}
				vl += 1;
			}
			tb = new TextBox();
			tb.setVisibleLength(vl);
			tb.setMaxLength(    vl);
			tb.setText(String.valueOf(uid.getTaskOrder()));
			tb.getElement().setAttribute(ATTR_ENTRY_ID, ta.getElement().getAttribute(ATTR_ENTRY_ID));
			EventWrapper.addHandler(tb, m_taskOrderKeyPressHandler);
			EventWrapper.addHandler(tb, m_taskOrderBlurHandler    );
			uid.getTaskOrderPanel().add(tb);				
			uid.setTaskOrderTextBox(tb);
		}
		
		// Finally, show the edit widget.
		tb.setVisible(true);
		tb.setSelectionRange(0, tb.getValue().length());
		tb.setFocus(true);
	}

	/*
	 * Called for each key press event in a  task order edit widget.
	 */
	private void handleTaskOrderKeyPress(TaskListItem task, KeyPressEvent event) {
        // Get the key the user pressed.
		UIData uid = getUIData(task);
		TextBox tb = uid.getTaskOrderTextBox();		
        int keyCode = event.getNativeEvent().getKeyCode();

        // Can we do something with this key?
        if ((!(Character.isDigit(event.getCharCode()))) &&
        		(KeyCodes.KEY_TAB       != keyCode)     &&
        		(KeyCodes.KEY_BACKSPACE != keyCode)     &&
        		(KeyCodes.KEY_DELETE    != keyCode)     &&
        		(KeyCodes.KEY_ENTER     != keyCode)     &&
        		(KeyCodes.KEY_HOME      != keyCode)     &&
        		(KeyCodes.KEY_END       != keyCode)     &&
        		(KeyCodes.KEY_LEFT      != keyCode)     &&
        		(KeyCodes.KEY_RIGHT     != keyCode)) {
       		// No!  Suppress it.
       		tb.cancelKey();
        }

        // Did the user press the enter key?
        if (KeyCodes.KEY_ENTER == keyCode) {
        	// Yes!  Kill the focus to hide/show the order widgets as
        	// appropriate...
        	tb.setFocus(false);
        	
    		// ...and put the new value into affect.  Do we have a new,
        	// ...valid order number for this task?
    		int    origOrder = uid.getTaskOrder();
    		int    newOrder  = 0;
    		String newOrderS = tb.getValue();
    		if (null == newOrderS) {
    			newOrderS = "";
    		}
    		if (0 < newOrderS.length()) {
    			try                  {newOrder  = Integer.parseInt(newOrderS);}
    			catch (Exception ex) {newOrderS = "";                      }
    		}
    		if ((0 == newOrderS.length() || (0 > newOrder))) {
    			// No!  Set the original order number back into the edit
    			// widget and bail.
    			tb.setValue(String.valueOf(origOrder));
    			return;
    		}
    		
    		// If the user didn't change the task's order number...
    		if (0 == newOrder) newOrder = 1;
    		if (newOrder == origOrder) {
    			// ...we don't have anything to do.  Bail.
    			return;
    		}

    		// Are we moving a task down in the list?
    		if (newOrder > origOrder) {
    			// Yes!  As per a comment from Lynn on the Firestone
    			// task to edit ordering, we want the task to have
    			// the order number given.  This adjustment takes care
    			// of that in the event the task is being moved down.
    			// Note that no adjustment is necessary if we're moving
    			// the task up.
    			newOrder += 1;
    		}
    		
    		// Put the new order number into effect.  Is the new order
    		// beyond the end of the task list?
    		List<TaskListItem> tasks = m_taskBundle.getTasks();
    		int count = tasks.size();
    		if (newOrder > count) {
    			// Yes!  Remove it from its current position and append it
    			// to the end of the list.
    			tasks.remove(task);
    			tasks.add(   task);
    		}
    		
    		else {
    			// No, the new order number is not beyond the end of the
    			// task list!  Find the task to it before...
    			TaskListItem insertBefore = tasks.get(newOrder - 1);
    			
    			// ...and move it there.
    			tasks.remove(task);
    			tasks.add(tasks.indexOf(insertBefore), task);
    		}

    		// Finally, after processing the move, we need to persist the
    		// linkage change and force the task list to refresh.
    		final ProcessActive pa = buildProcessActive(m_messages.taskProcess_move());
    		selectAllTasksNow(null, false);
    		uid.setTaskSelected(             true);
    		uid.getTaskSelectorCB().setValue(true);
    		persistLinkageChangeNow(
    			new ScheduledCommand() {
    				@Override
    				public void execute() {
    					// Refreshing the TaskTable will reread the tasks
    					// and display them in the appropriate hierarchy.
    					refreshTaskTableAsync(
    						pa,
    						true,	// true  -> Preserve checks.
    						false);	// false -> Don't persist the task linkage again AFTER rereading the tasks.
    				}
    		});
        }
	}
	
	/*
	 * Does what's necessary after a task is moved to put the change
	 * into affect.
	 */
	private void handleTaskPostMove(final ProcessActive pa, List<TaskListItem> taskList) {	
		initializeUIData(true);	// true -> Forces the order and depths to be reset.
		renderTaskBundle(m_taskBundle);		

		// Persist whatever changed in the linkage information.
		final Long binderId;
		final Long entryId;
		if ((null != taskList) && (1 == taskList.size())) {
			EntityId taskId = taskList.get(0).getTask().getTaskId();
		    binderId       = taskId.getBinderId();
		    entryId        = taskId.getEntityId();
		}
		else {
			binderId = m_taskBundle.getBinderId();
			entryId  = null;
		}
		persistLinkageChangeAsync(
			new ScheduledCommand() {
				@Override
				public void execute() {
					updateCalculatedDatesNow(pa, binderId, entryId);
				}
		});
	}

	/*
	 * Does what's necessary after a task is deleted or purged to put
	 * the change into affect.
	 */
	private void handleTaskPostRemoveAsync(final List<EntityId> taskIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				handleTaskPostRemoveNow(taskIds);
			}
		});
	}
	
	private void handleTaskPostRemoveNow(List<EntityId> taskIds) {
		// Scan the tasks that were removed...
		for (EntityId taskId:  taskIds) {
			// ...scan the task's subtasks...
			List<TaskListItem> taskList = TaskListItemHelper.findTaskList(m_taskBundle, taskId.getEntityId());
			TaskListItem       task     = TaskListItemHelper.findTask(    taskList,     taskId.getEntityId());
			int taskIndex = taskList.indexOf(task);
			List<TaskListItem> subtaskList = task.getSubtasks();
			int tasks = subtaskList.size();
			for (int i = tasks - 1; i >= 0; i -= 1) {
				// ...moving each subtask into the hierarchy where it's parent task was. 
				TaskListItem subtask = subtaskList.get(i);
				subtaskList.remove(i);
				taskList.add(taskIndex, subtask);
			}
		}

		// After we're done mucking with the task linkage information,
		// we need to persist it and then use it to refresh the
		// TaskTable.
		persistLinkageChangeNow(
			new ScheduledCommand() {
				@Override
				public void execute() {
					// Refreshing the TaskTable will reread the tasks
					// and display them in the appropriate hierarchy. 
					refreshTaskTableAsync(
						null,	// null  -> No ProcessActive.
						false,	// false -> Don't preserve checks.
						true);	// true  -> Persist the task linkage again AFTER rereading the tasks.
				}
		});
	}
	
	/*
	 * Called when the user presses the purge button on the task tool
	 * bar.
	 */
	private void handleTaskPurge() {
		// If nothing the user can purge is checked...
		final List<TaskListItem> tasksChecked = getTasksChecked();
		int c = ((null == tasksChecked) ? 0 : tasksChecked.size());
		for (int i = (c - 1); i >= 0; i -= 1) {
			TaskListItem task = tasksChecked.get(i);
			if (!(task.getTask().getCanPurge())) {
				tasksChecked.remove(i);
			}
		}
		if (!(GwtClientHelper.hasItems(tasksChecked))) {
			// ...there's nothing to purge.
			return;
		}

		// Is the user sure they want to perform the purge?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes!  Purge the selected tasks.
							final List<EntityId> taskIds = TaskListItemHelper.getTaskIdsFromList(tasksChecked, false);
							DeleteEntitiesHelper.purgeSelectedTasksAsync(taskIds, new DeleteEntitiesCallback() {
								@Override
								public void operationCanceled() {
									handleTaskPostRemoveAsync(taskIds);
								}

								@Override
								public void operationComplete() {
									handleTaskPostRemoveAsync(taskIds);
								}
								
								@Override
								public void operationFailed() {
									// Nothing to do.  The purge call
									// will have told the user about
									// the failure.
								}
							});
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.taskConfirmPurge());
			}
		});
	}
	
	/*
	 * Sets/clears a quick filter.
	 */
	private void handleTaskQuickFilter(String quickFilter) {
		// Are we setting a quick filter?
		if (null != quickFilter) {
			// Yes!  Are we really?
			quickFilter = quickFilter.trim();
			if (0 == quickFilter.length()) {
				// No!  Clear it instead.
				quickFilter = null;
			}
			else {
				// Yes, we really are!  Track the filter string
				// in lower case so what we don't have to mess
				// with case sensitivity later.
				quickFilter = quickFilter.toLowerCase();
			}
		}

		// If the quick filter is not changing, simply bail.
		boolean hadQuickFilter = (null != m_quickFilter);
		boolean hasQuickFilter = (null != quickFilter);
		if ((!hadQuickFilter) && (!hasQuickFilter))                                     return;
		if (  hadQuickFilter  &&   hasQuickFilter && quickFilter.equals(m_quickFilter)) return;
		
		// Do we need to flatten or restructure the list?
		m_quickFilter = quickFilter;
		if (shouldShowStructure() && (hasQuickFilter != hadQuickFilter)) {
			// Yes!  Flatten or restructure it and then resort it to
			// ensure either change ends up sorted correctly.
			if (hasQuickFilter)
			     TaskListItemHelper.flattenTaskList(m_taskBundle);
			else TaskListItemHelper.applyTaskLinkage(m_taskBundle);
			initializeUIData(false);	// false -> Forces the depths, but not the order to be reset.
			sortByColumnImpl();
		}
		
		// Simply redisplay the tasks to put the filter into affect.
		renderTaskBundle(m_taskBundle);
	}
	
	/*
	 * Called when the user clicks the seen sun burst on a task.
	 */
	private void handleTaskSeen(final TaskListItem task) {
		final Long entryId = task.getTask().getTaskId().getEntityId();
		SetSeenCmd cmd = new SetSeenCmd(entryId);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SetSeen(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Get and remove the Anchor from the task's UIData...
				UIData uid = getUIData(task);
				Anchor a = uid.getTaskUnseenAnchor();
				uid.setTaskUnseenAnchor(null);

				// ...replace the Anchor with a simple spacer image...
				a.getParent().getElement().replaceChild(buildSpacer().getElement(), a.getElement());
				
				// ...and mark the task as having been seen.
				uid.getTaskLabel().removeStyleName("bold");
				task.getTask().setSeen(true);
			}
		});
	}
	
	/*
	 * Called when the user clicks the checkbox on a task.
	 */
	private void handleTaskSelect(TaskListItem task, boolean checked) {
		// Track the state of the task's checkbox...
		UIData uid = getUIData(task);
		uid.setTaskSelected(checked);

		// ...and/remove the selected style from the affected rows...
		int row = uid.getTaskRow();
		if (checked)
		     m_flexTableRF.addStyleName(   row, "selected");
		else m_flexTableRF.removeStyleName(row, "selected");
		
		// ...and validate the TaskListing tools.
		validateTaskToolsAsync();
	}
	
	/*
	 * Called when the user changes the percent done value on the task.
	 */
	private void handleTaskSetPercentDone(final TaskListItem task, final String percentDone) {
		// If the selected percent done isn't changing...
		if (percentDone.equals(task.getTask().getCompleted())) {
			// ...bail.
			return;
		}

		// If we're marking the task 100% complete...
		if (percentDone.equals(TaskInfo.COMPLETED_100)) {
			// ...simply change its status to complete.  That change
			// ...will take care of any mucking that has to occur with
			// ...parent tasks, subtasks, ...
			handleTaskSetStatus(task, TaskInfo.STATUS_COMPLETED);
			return;
		}
		
		// Save the new task percent done value.
		final Long entryId = task.getTask().getTaskId().getEntityId();
		SaveTaskCompletedCmd cmd = new SaveTaskCompletedCmd(task.getTask().getTaskId().getBinderId(), entryId, percentDone);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveTaskCompleted(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				handleTaskSetPercentDoneImpl(task, percentDone, true);
			}
		});
	}
	
	private void handleTaskSetPercentDoneImpl(final TaskListItem task, String percentDone, boolean reflectInStatus) {
		// Store the new percent done value in the task.
		String newStatus = null;
		TaskInfo ti = task.getTask();
		ti.setCompleted(percentDone);
		boolean c100 = TaskInfo.COMPLETED_100.equals(percentDone);
		if (!c100) {
			ti.setCompletedDate(new TaskDate());
		}
		
		// Update the Image and text displayed on the percentDone.
		Image img = getUIData(task).getTaskPercentDoneImage();
		List<TaskMenuOption> pOpts = m_percentDoneMenu.getMenuOptions();
		for (TaskMenuOption tmo:  pOpts) {
			if (tmo.getMenu().equals(percentDone)) {
				img.setTitle(   tmo.getMenuAlt());
				img.setResource(tmo.getMenuImageRes());
			}
		}

		// Do we need to reflect the change in the status field?
		if (reflectInStatus) {
			// Maybe!  If we're changing the '% Done' to >0% and
			// <100%...
			boolean c000 = TaskInfo.COMPLETED_0.equals(percentDone);
			if ((!c000) && (!c100)) {
				// ...and the task's status is other than 'In Progress'...
				if (!(TaskInfo.STATUS_IN_PROCESS.equals(ti.getStatus()))) {
					// ...set it to 'In Progress'.
					newStatus = TaskInfo.STATUS_IN_PROCESS;
				}
			}
	
			// If we're changing the '% Done' to 0%...
			else if (c000) {
				// ...and the task's status is other than 'Needs Action'...
				if (!(TaskInfo.STATUS_NEEDS_ACTION.equals(ti.getStatus()))) {
					// ...set it to 'Needs Action'.
					newStatus = TaskInfo.STATUS_NEEDS_ACTION;
				}
			}
	
			// Do we have a new status value to put into affect?
			if (null != newStatus) {
				// Yes!  Apply it.
				final String finalNewStatus = newStatus;
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						handleTaskSetStatus(task, finalNewStatus);
					}
				});
			}
		}
	}
	
	/*
	 * Called when the user changes the priority value on the task.
	 */
	private void handleTaskSetPriority(final TaskListItem task, final String priority) {
		// If the selected priority isn't changing...
		if (priority.equals(task.getTask().getPriority())) {
			// ...bail.
			return;
		}

		// Save the new task priority.
		final Long entryId = task.getTask().getTaskId().getEntityId();
		SaveTaskPriorityCmd cmd = new SaveTaskPriorityCmd(task.getTask().getTaskId().getBinderId(), entryId, priority);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveTaskPriority(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Store the new priority in the task...
				task.getTask().setPriority(priority);
				
				// ...and update the Image and text displayed on the
				// ...priority.
				Image img = getUIData(task).getTaskPriorityImage();
				List<TaskMenuOption> pOpts = m_priorityMenu.getMenuOptions();
				for (TaskMenuOption tmo:  pOpts) {
					if (tmo.getMenu().equals(priority)) {
						img.setTitle(   tmo.getMenuAlt());
						img.setResource(tmo.getMenuImageRes());
					}
				}
			}
		});
	}
	
	/*
	 * Called when the user changes the status value on the task.
	 */
	private void handleTaskSetStatus(final TaskListItem task, final String status) {
		// If the selected status isn't changing...
		if (status.equals(task.getTask().getStatus())) {
			// ...bail.
			return;
		}
		
		// Collect the tasks this is going to affect.
		final List<TaskListItem> affectedTasks = new ArrayList<TaskListItem>();
		affectedTasks.add(task);
		if (TaskInfo.STATUS_COMPLETED.equalsIgnoreCase(status)) {
			TaskListItemHelper.findAffectedParentTasks_Completed(m_taskBundle, task, affectedTasks);
		}		
		else if (TaskInfo.STATUS_IN_PROCESS.equalsIgnoreCase(status) || TaskInfo.STATUS_NEEDS_ACTION.equalsIgnoreCase(status)) {
			TaskListItemHelper.findAffectedParentTasks_Active(m_taskBundle, task, affectedTasks);
		}

		// Collect the TaskId's of the affected tasks.
		TaskInfo             ti              = task.getTask();
		final EntityId       taskId          = ti.getTaskId();
		final List<EntityId> affectedTaskIds = new ArrayList<EntityId>();
		for (TaskListItem affectedTask:  affectedTasks) {
			affectedTaskIds.add(affectedTask.getTask().getTaskId());
		}

		// Save the new status on the affected tasks.
		SaveTaskStatusCmd cmd = new SaveTaskStatusCmd(affectedTaskIds, status);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveTaskStatus(),
					String.valueOf(taskId.getEntityId()));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Find the selected status option so that we can
				// update the display.
				StringRpcResponseData responseData = ((StringRpcResponseData) result.getResponseData());
				String completedDate = responseData.getStringValue();
				List<TaskMenuOption> sOpts = m_statusMenu.getMenuOptions();
				for (TaskMenuOption tmo:  sOpts) {
					if (tmo.getMenu().equals(status)) {
						// Scan the affected tasks...
						for (TaskListItem affectedTask:  affectedTasks) {
							// ...and update each one.
							handleTaskSetStatusImpl(
								affectedTask,
								status,
								tmo,
								completedDate);
						}
						break;
					}
				}
			}
		});
	}
	
	/*
	 * Called to update a task as per a new status menu selection.
	 */
	private void handleTaskSetStatusImpl(TaskListItem task, String status, TaskMenuOption tmo, String completedDateDisplay) {
		// Store the new status value in the task.
		TaskInfo ti = task.getTask();
		ti.setStatus(status);
		
		// Extract the UIData from this task.
		UIData uid = getUIData(task);

		// Update the status value.
		Image img = uid.getTaskStatusImage();
		img.setTitle(   tmo.getMenuAlt());
		img.setResource(tmo.getMenuImageRes());			

		// If we were given a completed date string, that means that
		// we're now marking this task completed.  Is that the case?
		InlineLabel completedLabel    = uid.getTaskCompletedLabel();
		Widget      percentDoneWidget = uid.getTaskPercentDoneWidget();
		if (GwtClientHelper.hasString(completedDateDisplay)) {
			// Yes!  Hide the percent done widget...  
			percentDoneWidget.setVisible(false);
			
			// ...update/show the completed widget...
			completedLabel.setText(   completedDateDisplay);
			completedLabel.setVisible(true                );
			
			// ...and update the task.
			TaskDate completedDate = new TaskDate();
			completedDate.setDateDisplay(completedDateDisplay);
			ti.setCompleted(TaskInfo.COMPLETED_100);
			ti.setCompletedDate(completedDate);
		}
		
		else {
			// No, we don't have a completed date string!  Do we need
			// to change what we're displaying in the 'Closed - % Done'
			// column?
			boolean completedWasVisible   = completedLabel.isVisible();
			boolean percentDoneWasVisible = percentDoneWidget.isVisible();
			if (completedWasVisible || (!percentDoneWasVisible)) {
				// Yes!  Hide the completed date widget and show the
				// percent done widget, now at 90%.
				completedLabel.setVisible(false);
				percentDoneWidget.setVisible(true);
				handleTaskSetPercentDoneImpl(task, TaskInfo.COMPLETED_90, false);
			}
		}

		// Finally, make sure the row is showing with the correct
		// styles, ...  We can do that by simply re-rendering the
		// 'Task Name', 'Due Date' and 'Assigned To' columns.
		int row = uid.getTaskRow();
		renderColumnTaskName(  task, row);
		renderColumnDueDate(   task, row);
		renderColumnAssignedTo(task, row);
	}
	
	/*
	 * Called to run the entry viewer on the given task.
	 */
	private void handleTaskView(TaskListItem task) {
		final TaskInfo ti = task.getTask();
		GetViewFolderEntryUrlCmd cmd;
		
		cmd = new GetViewFolderEntryUrlCmd( ti.getTaskId().getBinderId(), ti.getTaskId().getEntityId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(ti.getTaskId().getEntityId()));
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				String viewFolderEntryUrl;
				
				viewFolderEntryUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
				GwtClientHelper.jsShowForumEntry(viewFolderEntryUrl);
			}
		});
	}

	/*
	 * Returns true if a task should be displayed back on the current
	 * quick filter and false otherwise.
	 */
	private boolean isTaskInQuickFilter(TaskListItem task) {
		// If there's no quick filter defined...
		if (null == m_quickFilter) {
			// ...all tasks are displayed.
			return true;
		}

		// If a task's title contains the quick filter...
		String title = task.getTask().getTitle();
		if (title.trim().toLowerCase().contains(m_quickFilter)) {
			// ...it's displayed.
			return true;
		}

		// Otherwise, the task is not displayed.
		return false;
	}
	
	/*
	 * Uses JSNI to return the disposition of newly created task.
	 */
	private native String jsGetNewTaskDisposition() /*-{
		return $wnd.top.ss_newTaskDisposition;
	}-*/;
	
	/*
	 * Uses JSNI to return the ID of the most recently selected task.
	 */
	private native String jsGetSelectedTaskId() /*-{
		return $wnd.top.ss_selectedTaskId;
	}-*/;
	
	/*
	 * Called to create JavaScript methods that will be invoked from
	 * the the task table to handle hover events over a task.
	 */
	private native void jsInitTaskMouseEventHandlers(TaskTable taskTable) /*-{
		$wnd.ss_taskMouseOut = function(row) {
			taskTable.@org.kablink.teaming.gwt.client.tasklisting.TaskTable::handleTaskMouseOut(Ljava/lang/String;)(row);
		}
		
		$wnd.ss_taskMouseOver = function(row) {
			taskTable.@org.kablink.teaming.gwt.client.tasklisting.TaskTable::handleTaskMouseOver(Ljava/lang/String;)(row);
		}
	}-*/;

	/*
	 * Uses JSNI to store the ID of the most recently selected task.
	 */
	private native void jsSetNewTaskDisposition(String newTaskDisposition) /*-{
		$wnd.top.ss_newTaskDisposition = String(newTaskDisposition);
	}-*/;
	
	/*
	 * Uses JSNI to store the ID of the most recently selected task.
	 */
	private native void jsSetSelectedTaskId(String taskId) /*-{
		$wnd.top.ss_selectedTaskId = String(taskId);
	}-*/;
	
	/*
	 * If the TaskTable is sorted by the specified column, add the
	 * appropriate 'sorted by' indicator. 
	 */
	private void markAsSortKey(Anchor a, Column col) {
		// Is this the column we're sorted on?
		if (m_sortColumn == col) {
			// Yes!  Add the appropriate directional arrow
			// (i.e., ^/v)...
			ImageResource	ir = (m_sortAscending ? m_images.sortAZ() : m_images.sortZA());
			Image			i  = GwtClientHelper.buildImage(ir.getSafeUri());
			i.addStyleName("gwtTaskList_sortImage");
			a.getElement().insertFirst(i.getElement());
			
			// ...and style to the <TD>.
			m_flexTableCF.addStyleName(0, getColumnIndex(col), "sortedcol");
		}
	}

	/**
	 * Called when the task table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Handles ChangeEntryTypeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ChangeEntryTypeSelectedEntitiesEvent.Handler.onChangeEntryTypeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeEntryTypeSelectedEntities(ChangeEntryTypeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the change.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getTaskIdsChecked();
			}
			BinderViewsHelper.changeEntryTypes(selectedEntityIds);
		}
	}
	
	/**
	 * Handles CopySelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntitiesEvent.Handler.onCopySelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntities(CopySelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Are there any entities in the event?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// No!  Invoke the copy on those selected in the view.
				selectedEntityIds = getTaskIdsChecked();
				BinderViewsHelper.copyEntries(selectedEntityIds);
			}
		}
	}
	
	/**
	 * Handles DeleteSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntitiesEvent.Handler.onDeleteSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntities(DeleteSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Are there any entities in the event?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// No!  Delete the entities selected in the view.
				List<TaskListItem> tasksChecked = getTasksChecked();
				int c = ((null == tasksChecked) ? 0 : tasksChecked.size());
				for (int i = (c - 1); i >= 0; i -= 1) {
					TaskListItem task = tasksChecked.get(i);
					if (!(task.getTask().getCanTrash())) {
						tasksChecked.remove(i);
					}
				}
				if (!(GwtClientHelper.hasItems(tasksChecked))) {
					// ...there's nothing to delete.
					return;
				}
				final List<EntityId> taskIds = TaskListItemHelper.getTaskIdsFromList(tasksChecked, false);
				BinderViewsHelper.deleteSelections(taskIds, new DeleteEntitiesCallback() {
					@Override
					public void operationCanceled() {
						handleTaskPostRemoveAsync(taskIds);
					}

					@Override
					public void operationComplete() {
						handleTaskPostRemoveAsync(taskIds);
					}

					@Override
					public void operationFailed() {
						// Nothing to do.  The delete call will have
						// told the user about the failure.
					}
				});
			}
		}
	}
	
	/**
	 * Called when the task table is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles LockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the LockSelectedEntitiesEvent.Handler.onLockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLockSelectedEntities(LockSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the lock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getTaskIdsChecked();
			}
			BinderViewsHelper.lockEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MarkReadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkReadSelectedEntitiesEvent.Handler.onMarkReadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkReadSelectedEntities(MarkReadSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getTaskIdsChecked();
			}
			BinderViewsHelper.markEntriesRead(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MoveSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntitiesEvent.Handler.onMoveSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntities(MoveSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Are there any entities in the event?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// No!  Invoke the move on those selected in the view.
				selectedEntityIds = getTaskIdsChecked();
				BinderViewsHelper.moveEntries(selectedEntityIds);
			}
		}
	}
	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter(QuickFilterEvent event) {
		// If the event is targeted to the task folder we're viewing...
		if (event.getFolderId().equals(m_taskBundle.getBinderId())) {
			// ...handle it.
			handleTaskQuickFilter(event.getQuickFilter());
		}
	}

	/**
	 * Handles ShareSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntitiesEvent.Handler.onShareSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntities(ShareSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the share.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getTaskIdsChecked();
			}
			BinderViewsHelper.shareEntities(selectedEntityIds);
		}
	}
	
	/**
	 * Handles SubscribeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the SubscribeSelectedEntitiesEvent.Handler.onSubscribeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSubscribeSelectedEntities(SubscribeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the subscribe to.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getTaskIdsChecked();
			}
			BinderViewsHelper.subscribeToEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles TaskDeleteEvent's received by this class.
	 * 
	 * Implements the TaskDeleteEvent.Handler.onTaskDelete() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskDelete(TaskDeleteEvent event) {
		handleTaskDelete();
	}

	/**
	 * Handles TaskHierarchyDisabledEvent's received by this class.
	 * 
	 * Implements the TaskHierarchyDisabledEvent.Handler.onTaskHierarchyDisabled() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskHierarchyDisabled(TaskHierarchyDisabledEvent event) {
		handleTaskHierarchyDisabledAsync();
	}

	/**
	 * Handles TaskMoveDownEvent's received by this class.
	 * 
	 * Implements the TaskMoveDownEvent.Handler.onTaskMoveDown() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskMoveDown(TaskMoveDownEvent event) {
		handleTaskMoveDown();
	}

	/**
	 * Handles TaskMoveLeftEvent's received by this class.
	 * 
	 * Implements the TaskMoveLeftEvent.Handler.onTaskMoveLeft() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskMoveLeft(TaskMoveLeftEvent event) {
		handleTaskMoveLeft();
	}

	/**
	 * Handles TaskMoveRightEvent's received by this class.
	 * 
	 * Implements the TaskMoveRightEvent.Handler.onTaskMoveRight() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskMoveRight(TaskMoveRightEvent event) {
		handleTaskMoveRight();
	}

	/**
	 * Handles TaskMoveUpEvent's received by this class.
	 * 
	 * Implements the TaskMoveUpEvent.Handler.onTaskMoveUp() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskMoveUp(TaskMoveUpEvent event) {
		handleTaskMoveUp();
	}

	/**
	 * Handles TaskPurgeEvent's received by this class.
	 * 
	 * Implements the TaskPurgeEvent.Handler.onTaskPurge() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskPurge(TaskPurgeEvent event) {
		handleTaskPurge();
	}

	/**
	 * Handles UnlockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the UnlockSelectedEntitiesEvent.Handler.onUnlockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUnlockSelectedEntities(UnlockSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the unlock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getTaskIdsChecked();
			}
			BinderViewsHelper.unlockEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles ViewSelectedEntryEvent's received by this class.
	 * 
	 * Implements the ViewSelectedEntryEvent.Handler.onViewSelectedEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewSelectedEntry(ViewSelectedEntryEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getTaskIdsChecked();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					if (eid.isEntry()) {
						BinderViewsHelper.viewEntry(eid);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Handles ViewWhoHasAccessEvent's received by this class.
	 * 
	 * Implements the ViewWhoHasAccessEvent.Handler.onViewWhoHasAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewWhoHasAccess(ViewWhoHasAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_taskBundle.getBinderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getTaskIdsChecked();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					BinderViewsHelper.viewWhoHasAccess(eid);
					return;
				}
			}
		}
	}
	
	/*
	 * Called to write the change in linkage to the folder preferences.
	 */
	private void persistLinkageChangeAsync(final ScheduledCommand postChangeCommand) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				persistLinkageChangeNow(postChangeCommand);
			}
		});
	}
	
	private void persistLinkageChangeNow(final ScheduledCommand postChangeCommand) {
		// If we're not in a state were link changes can be saved...
		if (!(canPersistLinkage())) {
			// ...bail.
			return;
		}

		// Update the TaskLinkage in the TaskBundle...
		TaskLinkage newLinkage = TaskListItemHelper.buildLinkage(m_taskBundle);
		m_taskBundle.setTaskLinkage(newLinkage);
		
		// ...and write it to the binder's properties.
		final Long binderId = m_taskBundle.getBinderId();
		SaveTaskLinkageCmd cmd = new SaveTaskLinkageCmd(binderId, m_taskBundle.getTaskLinkage());
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveTaskLinkage(),
					String.valueOf(binderId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// If we have a post change command...
				if (null != postChangeCommand) {
					// ...schedule it.
					GwtClientHelper.deferCommand(postChangeCommand);
				}
			}
		});
	}
	
	/*
	 * Called to write a change in the sort criteria to the user's
	 * preferences.
	 */
	private void persistSortChange() {
		// Simply tell the server to persist the new sort setting on
		// the binder.
		BinderInfo bi = new BinderInfo();
		bi.setBinderType(BinderType.FOLDER         );
		bi.setFolderType(FolderType.TASK           );
		bi.setBinderId(  m_taskBundle.getBinderId());
		bi.setEntityType("folder"                  );
		SaveTaskSortCmd cmd = new SaveTaskSortCmd(bi, m_sortColumn.getSortKey(), m_sortAscending);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveTaskSort());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Nothing to do.
			}
		});
	}

	/*
	 * Asynchronously prompts the user for where they want to place a
	 * new task.
	 */
	private void promptForDispositionAsync(final Long newTaskId, final Long selectedTaskId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				promptForDispositionNow(newTaskId, selectedTaskId);
			}
		});
	}
	
	/*
	 * Synchronously prompts the user for where they want to place a
	 * new task.
	 */
	private void promptForDispositionNow(Long newTaskId, Long selectedTaskId) {
		TaskListItem task = TaskListItemHelper.findTask(m_taskBundle, selectedTaskId);
		TaskDispositionDlg tdd = new TaskDispositionDlg(
			false,	// false -> Don't auto hide.
			true,	// true  -> Modal.
			0,		// Left.
			0,		// Top.
			this,
			newTaskId,
			task);
		tdd.addStyleName("taskDispositionDlg");
		tdd.show(true);
	}
	
	/*
	 * Called to completely refresh the contents of the TaskTable.
	 */
	private void refreshTaskTableAsync(final ProcessActive pa, final boolean preserveChecks, final boolean persistLinkage) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				refreshTaskTableNow(pa, preserveChecks, persistLinkage);
			}
		});
	}
	
	private void refreshTaskTableNow(final ProcessActive pa, final boolean preserveChecks, final boolean persistLinkage) {
		GetTaskBundleCmd cmd = new GetTaskBundleCmd(m_taskListing.isEmbeddedInJSP(), m_taskListing.getBinderId(), m_taskListing.getFilterType(), m_taskListing.getMode());
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// If we were given a ProcessActive...
				if (null != pa) {
					// ...kill it.
					pa.killIt();
				}

				// Tell the user about the failure.
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetTaskList());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// If we were given a ProcessActive...
				if (null != pa) {
					// ...Kill it.
					pa.killIt();
				}

				// Extract the TaskBundle from the response data...
				TaskBundleRpcResponseData responseData = ((TaskBundleRpcResponseData) result.getResponseData());
				TaskBundle taskBundle = responseData.getTaskBundle();
				
				// ...preserve the tasks that are currently checked...
				List<Long> checkedTaskIds;
				if (preserveChecks)
				     checkedTaskIds = getTaskIdsCheckedAsListLong();
				else checkedTaskIds = null;
				
				// ...store the new TaskBundle in the TaskListing...
				m_taskListing.setTaskBundle(taskBundle);
				
				// ...force the TaskTable to redisplay...
				m_newTaskTable = true;
				renderTaskBundle(taskBundle, checkedTaskIds);
				
				// ...and if we were requested to do so...
				if (persistLinkage) {
					// ...persist the current state of things as the
					// ...new task linkage.
					persistLinkageChangeAsync(null);	// null  -> No post change commands.
				}
			}			
		});
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Renders a column of a row based on a task into the TaskTable.
	 */
	private void renderColumn(TaskListItem task, int row, Column col) {
		switch(col) {
		case CLOSED_PERCENT_DONE:  renderColumnClosedPercentDone(task, row); break;
		case ASSIGNED_TO:          renderColumnAssignedTo(       task, row); break;		
		case DUE_DATE:             renderColumnDueDate(          task, row); break;		
		case TASK_NAME:            renderColumnTaskName(         task, row); break;
		case NEW_TASK_MENU:        renderColumnNewTaskMenuLink(  task, row); break;
		case ORDER:                renderColumnOrder(            task, row); break;
		case PRIORITY:             renderColumnPriority(         task, row); break;		
		case SELECTOR:             renderColumnSelectCB(         task, row); break;
		case STATUS:               renderColumnStatus(           task, row); break;
		case LOCATION:             renderColumnLocation(         task, row); break;
		}
	}
	
	/*
	 * Renders the 'Assigned To' column.
	 */
	private void renderColumnAssignedTo(final TaskListItem task, int row) {
		VerticalPanel vp = new VerticalPanel();
		vp.addStyleName("gwtTaskList_assigneesList");

		// Scan the individual assignees...
		TaskInfo ti = task.getTask();
		boolean isCancelled = ti.getStatus().equals(TaskInfo.STATUS_CANCELED);
		int assignments = 0;
		for (final AssignmentInfo ai:  ti.getAssignments()) {
			// ...adding a PresenceControl for each.
			assignments += 1;
			buildAssigneeWidgets(vp, AssigneeType.INDIVIDUAL, ai, isCancelled);
		}

		// Scan the group assignees...
		for (AssignmentInfo ai:  ti.getAssignmentGroups()) {
			// ...adding a FlowPanel display for each.
			assignments += 1;
			buildAssigneeWidgets(vp, AssigneeType.GROUP, ai, isCancelled);
		}
		
		// Scan the team assignees...
		for (AssignmentInfo ai:  ti.getAssignmentTeams()) {			
			// ...adding a FlowPanel display for each.
			assignments += 1;
			buildAssigneeWidgets(vp, AssigneeType.TEAM, ai, isCancelled);
		}

		// If there were any assignees...
		if (0 < assignments) {
			// ...add the VerticalPanel to the TaskTable.
			m_flexTable.setWidget(row, getColumnIndex(Column.ASSIGNED_TO), vp);
		}
	}

	/*
	 * Renders the 'Closed - % Done' column.
	 */
	private void renderColumnClosedPercentDone(final TaskListItem task, int row) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		InlineLabel completedLabel;
		Widget percentDoneWidget;
		
		// What's the current priority of this task?
		TaskInfo ti = task.getTask();
		String percentDone = ti.getCompleted();
		if (!(GwtClientHelper.hasString(percentDone))) {
			percentDone = TaskInfo.COMPLETED_0;
		}
		boolean c100 = TaskInfo.COMPLETED_100.equals(percentDone);

		// Generate a completed date label...
		String completedDateDisplay = ti.getCompletedDate().getDateDisplay();
		if (null == completedDateDisplay) completedDateDisplay = "";
		completedLabel = new InlineLabel(completedDateDisplay);
		completedLabel.setWordWrap(false);
		uid.setTaskCompletedLabel(completedLabel);
		
		// ...generate an Anchor percent done selector...
		percentDoneWidget = buildOptionColumn(
			task,
			m_percentDoneMenu,
			percentDone,
			"percent-done");
		uid.setTaskPercentDoneWidget(percentDoneWidget);

		// ...and hide whichever one we don't need.
		if (c100)
		     percentDoneWidget.setVisible(false);
		else completedLabel.setVisible(   false);

		// Finally, construct a FlowPanel containing both and add that
		// to the TaskTable.
		FlowPanel fp = new FlowPanel();
		fp.add(completedLabel);
		fp.add(percentDoneWidget);
		m_flexTable.setWidget(row, getColumnIndex(Column.CLOSED_PERCENT_DONE), fp);
		
	}
	
	/*
	 * Renders the 'Due Date' column.
	 */
	private void renderColumnDueDate(final TaskListItem task, int row) {
		TaskInfo  ti  = task.getTask();
		TaskEvent tie = ti.getEvent();
		String dueDate = tie.getLogicalEnd().getDateDisplay();
		boolean hasDueDate = GwtClientHelper.hasString(dueDate);
		InlineLabel il = new InlineLabel();
		il.getElement().setInnerHTML(hasDueDate ? dueDate : m_messages.taskNoDueDate());
		il.setWordWrap(false);
		if (ti.isTaskOverdue()) {
			il.addStyleName("gwtTaskList_task-overdue-color");
		}
		
		boolean parentWithDurationError = TaskListItemHelper.isParentWithDurationError(task);
		if (tie.getEndIsCalculated() && hasDueDate) {
			il.addStyleName("gwtTaskList_calculatedDate");
			if (!parentWithDurationError) {
				il.setTitle(m_messages.taskAltDateCalculated());
			}
		}
		
		Widget dueDateWidget;
		if (ti.getCanModify() && ti.isTaskActive()) {
			Anchor a = GwtClientHelper.buildAnchor();
			Element aE = a.getElement();
			aE.appendChild(il.getElement());
			aE.appendChild(GwtClientHelper.buildImage(m_images.menu()).getElement());
			aE.setAttribute(ATTR_ENTRY_ID, String.valueOf(ti.getTaskId().getEntityId()));
			EventWrapper.addHandler(a, m_dueDateClickHandler);
			dueDateWidget = a;
		}
		else {
			dueDateWidget = il;
		}
		dueDateWidget.addStyleName("marginleft5px");
		int col = getColumnIndex(Column.DUE_DATE);
		m_flexTable.setWidget(row, col, dueDateWidget);
		
		// Is this a parent task with a calculated due date?
		if (parentWithDurationError) {
			// Yes!  Style it accordingly.
			m_flexTableCF.addStyleName(row, col, "gwtTaskList_parentDurationError");
			m_flexTableCF.getElement(row, col).setTitle(m_messages.taskAltParentWithDurationError());
		}
		else {
			m_flexTableCF.removeStyleName(row, col, "gwtTaskList_parentDurationError");
			m_flexTableCF.getElement(row, col).setTitle("");
		}
	}
	
	/*
	 * Renders the 'Location' column.
	 */
	private void renderColumnLocation(final TaskListItem task, int row) {
		// Are we displaying tasks assigned to the current user?
		if (!(m_taskBundle.getIsFromFolder())) {
			// Yes!  Do we have a location string for this task?
			TaskInfo ti = task.getTask();
			String location = ti.getLocation();
			if (!(GwtClientHelper.hasString(location))) {
				// No!  Then we don't render anything.
				return;
			}
			
			// Yes, we have a location string for this task!  If the
			// task is from the folder that we're displaying...
			EntityId tid = ti.getTaskId();
			InlineLabel locationLabel = new InlineLabel(location);
			Widget locationWidget;
			if (tid.getBinderId().equals(m_taskBundle.getBinderId())) {
				// ...simply render the location as text.
				locationLabel.setTitle(m_messages.taskAltLocationIsThisFolder());
				locationWidget = locationLabel;
			}
			
			else {
				// ...otherwise, render a link for it.
				Anchor locationAnchor = GwtClientHelper.buildAnchor();
				locationAnchor.addStyleName("gwtTaskList_task-locationAnchor");
				locationAnchor.getElement().setAttribute(ATTR_ENTRY_ID, String.valueOf(tid.getEntityId()));
				EventWrapper.addHandler(locationAnchor, m_taskLocationClickHandler);
				locationLabel.setTitle(m_messages.taskAltLocationGotoThisFolder());
				Element taElement = locationAnchor.getElement();
				taElement.appendChild(locationLabel.getElement());
				locationWidget = locationAnchor;
			}
			m_flexTable.setWidget(row, getColumnIndex(Column.LOCATION), locationWidget);
		}
	}

	/*
	 * Renders the 'New Task Menu' link.
	 */
	private void renderColumnNewTaskMenuLink(final TaskListItem task, int row) {
		// Do we need to include a new task menu link for this task?
		boolean includeNewTaskMenu =
			(m_taskBundle.respectLinkage()          &&
			 m_taskBundle.getCanModifyTaskLinkage() &&
			 m_taskBundle.getCanAddEntry()          &&
			 m_taskBundle.getCanContainTasks()      &&
			 (Column.ORDER == m_sortColumn) && m_sortAscending && (null == m_quickFilter));

		int newTaskMenuIndex = getColumnIndex(Column.NEW_TASK_MENU);
		if (includeNewTaskMenu) {
			// Yes!  Add an Anchor for it...
			Anchor menuAnchor = GwtClientHelper.buildAnchor("gwtTaskList_newTaskMenu_Link");
			Element menuElement = menuAnchor.getElement();
			String entryId = String.valueOf(task.getTask().getTaskId().getEntityId());
			menuElement.setAttribute(ATTR_ENTRY_ID, entryId);
			EventWrapper.addHandler(menuAnchor, m_newTaskClickHandler);
			Image newTaskMenuImg = GwtClientHelper.buildImage(m_images.newTaskButton1().getSafeUri().asString());
			newTaskMenuImg.addStyleName("gwtTaskList_newTaskMenu_LinkImg");
			newTaskMenuImg.setTitle(m_messages.taskAltTaskActions());
			getUIData(task).setTaskNewTaskMenuImage(newTaskMenuImg);
			menuElement.appendChild(newTaskMenuImg.getElement());
			m_flexTable.setWidget(row, newTaskMenuIndex, menuAnchor);
			
			// ...and add hover event handlers to the task.
			m_flexTableRF.getElement(row).setAttribute(ATTR_ENTRY_ID, entryId);
			Element tdElement = m_flexTableCF.getElement(row, newTaskMenuIndex);
			tdElement.setAttribute("onMouseOut",  "ss_taskMouseOut('"  + row + "');");
			tdElement.setAttribute("onMouseOver", "ss_taskMouseOver('" + row + "');");
		}
		
		else {
			// No, we don't need to include a new task menu link for
			// this task!  Add a non-breaking space so that IE displays
			// the cell correctly.
			m_flexTable.setHTML(row, newTaskMenuIndex, "&nbsp;");
		}
	}
		
	/*
	 * Renders the 'Order' column.
	 */
	private void renderColumnOrder(final TaskListItem task, int row) {
		// Are we supposed to show the 'Order' column?
		if (showOrderColumn()) {
			// Yes!  Render the column.  Extract the UIData from this
			// task.
			UIData uid = getUIData(task);

			// What should we display in the column?
			int order = uid.getTaskOrder();
			String orderHTML;
			boolean hasOrderNumber = ((0 == uid.getTaskDepth()) && ((-1) != order));
			if (hasOrderNumber)
			     orderHTML = String.valueOf(order);
			else orderHTML = "&nbsp;";
			int colIndex = getColumnIndex(Column.ORDER);

			// Do we have an order number that the user can edit?
			if (hasOrderNumber && m_taskBundle.getCanModifyTaskLinkage()) {
				// Yes!  Create a panel that can hold the text and an
				// edit widgets...
				FlowPanel fp = new FlowPanel();
				uid.setTaskOrderPanel(fp);
				
				// ...create an anchor the user can click to edit the
				// ...the order number...
				Label ta = buildAnchorLabel();
				ta.setText(orderHTML);
				ta.addStyleName("gwtTaskList_task-orderAnchor");
				Element taE = ta.getElement();
				String entryId = String.valueOf(task.getTask().getTaskId().getEntityId());
				taE.setAttribute(ATTR_ENTRY_ID, entryId);
				EventWrapper.addHandler(ta, m_taskOrderClickHandler);
				fp.add(ta);
				uid.setTaskOrderAnchor(ta);
				
				// ...note that the TextBox to edit the order is
				// ...created the first time the user selects the task
				// ...order number anchor...

				// ...and add the panel to the table.
				m_flexTable.setWidget(row, colIndex, fp);
			}
			
			else {
				// No, we don't have an order number or the user can't
				// edit it!  Add the order to the table as a simple
				// chunk of text.
				m_flexTable.setHTML(row, colIndex, orderHTML);
			}			
			
			m_flexTableCF.setHorizontalAlignment(row, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
			m_flexTableCF.setWidth(              row, colIndex, (SPACER_SIZE + "px"));
		}
	}

	/*
	 * Renders the 'Priority' column.
	 */
	private void renderColumnPriority(final TaskListItem task, int row) {
		// What's the current priority of this task?
		String priority = task.getTask().getPriority();
		if (!(GwtClientHelper.hasString(priority))) {
			priority = TaskInfo.PRIORITY_CRITICAL;
		}
		
		// Add an Anchor for it to the TaskTable.
		m_flexTable.setWidget(
			row,
			getColumnIndex(Column.PRIORITY),
			buildOptionColumn(
				task,
				m_priorityMenu,
				priority,
				"priority-icon"));
	}
	
	/*
	 * Renders the 'Select CheckBox' column.
	 */
	private void renderColumnSelectCB(final TaskListItem task, int row) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);

		String entryId = String.valueOf(task.getTask().getTaskId().getEntityId());
		CheckBox cb = new CheckBox();
		uid.setTaskSelectorCB(cb);
		cb.getElement().setId("gwtTaskList_taskSelect_" + entryId);
		cb.addStyleName("gwtTaskList_ckbox");
		cb.getElement().setAttribute(ATTR_ENTRY_ID, entryId);
		EventWrapper.addHandler(cb, m_cbClickHandler);
		if (uid.getTaskSelected()) {
			cb.setValue(true);
			uid.setTaskSelected(true);
			m_flexTableRF.addStyleName(row, "selected");
		}
		FlowPanel fp = new FlowPanel();
		fp.add(cb);
		if (0 < task.getSubtasks().size()) {
			Anchor a = GwtClientHelper.buildAnchor();
			Image  i = GwtClientHelper.buildImage(task.getExpandSubtasks() ? m_images.task_closer() : m_images.task_opener());
			Element aE = a.getElement();
			aE.appendChild(i.getElement());
			aE.setAttribute(ATTR_ENTRY_ID, entryId);
			EventWrapper.addHandler(a, m_expanderClickHandler);
			fp.add(a);
		}
		else {
			fp.add(buildSpacer());
		}
		int colIndex = getColumnIndex(Column.SELECTOR);
		m_flexTableCF.setWordWrap( row, colIndex, false);
		m_flexTableCF.setAlignment(row, colIndex, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(     row, colIndex, fp);
	}

	/*
	 * Renders the 'Status' column.
	 */
	private void renderColumnStatus(final TaskListItem task, int row) {
		// What's the current priority of this task?
		String status = task.getTask().getStatus();
		if (!(GwtClientHelper.hasString(status))) {
			status = TaskInfo.STATUS_NEEDS_ACTION;
		}
		
		// Add an Anchor for it to the TaskTable.
		Widget statusWidget = buildOptionColumn(task, m_statusMenu, status, "status-icon");
		statusWidget.addStyleName("marginleft5px");
		m_flexTable.setWidget(row, getColumnIndex(Column.STATUS), statusWidget);
	}
	
	/*
	 * Renders the 'Task Name' column.
	 */
	private void renderColumnTaskName(final TaskListItem task, int row) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		// If we're running in IE, there's some style hacks we need
		// to put in place.
		boolean isIE = GwtClientHelper.jsIsIE();
		String namePanelStyles  =         "gwtTaskList_task-namePanel ";
		namePanelStyles        += (isIE ? "gwtTaskList_task-namePanel_IE" : "gwtTaskList_task-namePanel_NonIE");
		String nameMarkerStyles =         "gwtTaskList_task-nameMarker ";
		nameMarkerStyles       += (isIE ? "gwtTaskList_task-nameMarker_IE" : "gwtTaskList_task-nameMarker_NonIE");
		
		// Define a panel that contains the task name widgets.
		int col = getColumnIndex(Column.TASK_NAME);
		FlowPanel fp = new FlowPanel();
		m_flexTable.setWidget(row, col, fp);
		String taStyles = "";	// The styles for the panel are built up as we determine what it's going to display.

		// Account for any subtask indentation.
		int taskDepth    = uid.getTaskDepth();
		int taLeftOffset = ((0 < taskDepth) ? (SPACER_SIZE * taskDepth) : 0);

		// Add the closed/unseen marker Widget to the panel.
		FlowPanel markerPanel = new FlowPanel();
		markerPanel.addStyleName(nameMarkerStyles);
		Widget marker;
		TaskInfo ti = task.getTask();
		String entryId = String.valueOf(ti.getTaskId().getEntityId());
		if (ti.isTaskClosed()) {
			taStyles        += " gwtTaskList_task-strike_Inner";
			namePanelStyles += " gwtTaskList_task-strike_Outer";
			Image i          = GwtClientHelper.buildImage(m_images.completed(), m_messages.taskAltTaskClosed());
			marker           = i;
		}
		else if (ti.isTaskUnseen()) {
			Anchor a = GwtClientHelper.buildAnchor();
			uid.setTaskUnseenAnchor(a);
			Image i = GwtClientHelper.buildImage(m_images.unread(), m_messages.taskAltTaskUnread());
			Element aE = a.getElement();
			aE.appendChild(i.getElement());
			aE.setAttribute(ATTR_ENTRY_ID, entryId);
			EventWrapper.addHandler(a, m_taskSeenClickHandler);
			marker = a;
		}
		else {
			marker = buildSpacer();
		}
		if (ti.isTaskOverdue()) {
			namePanelStyles += " gwtTaskList_task-overdue";
		}
		marker.addStyleName("gwtTaskList_task-icon");
		markerPanel.add(marker);
		markerPanel.getElement().getStyle().setLeft(taLeftOffset, Unit.PX);
		fp.add(markerPanel);
		
		// Add the calculated marker size to the left offset.
		taLeftOffset += m_markerSize;
		
		// Add the appropriately styled task name Anchor to the panel.
		Anchor ta = GwtClientHelper.buildAnchor();
		ta.addStyleName(taStyles + " gwtTaskList_task-nameAnchor");
		ta.getElement().setAttribute(ATTR_ENTRY_ID, entryId);
		EventWrapper.addHandler(ta, m_taskViewClickHandler);
		InlineLabel taskLabel = new InlineLabel(task.getTask().getTitle());
		uid.setTaskLabel(taskLabel);
		if (ti.isTaskUnseen())    taskLabel.addStyleName(             "bold"   );	// Unseen:     Bold.
		if (ti.isTaskCancelled()) m_flexTableRF.addStyleName(   row, "disabled");	// Cancelled:  Gray.
		else                      m_flexTableRF.removeStyleName(row, "disabled");
		Element taElement = ta.getElement();
		taElement.appendChild(taskLabel.getElement());
		fp.addStyleName(namePanelStyles);		
		Style fpStyle = fp.getElement().getStyle();
		fpStyle.setPaddingLeft(taLeftOffset, Unit.PX);
		fp.add(ta);

		m_flexTableCF.addStyleName(row, col, "gwtTaskList_task-nameCell");
		m_flexTableCF.setWidth(    row, col, "100%");
	}

	/*
	 * Renders a header column in the TaskTable.
	 */
	private void renderHeader(Column col) {
		switch(col) {
		case CLOSED_PERCENT_DONE:  renderHeaderClosedPercentDone(); break;
		case ASSIGNED_TO:          renderHeaderAssignedTo();        break;		
		case DUE_DATE:             renderHeaderDueDate();           break;		
		case TASK_NAME:            renderHeaderTaskName();          break;
		case NEW_TASK_MENU:        renderHeaderNewTaskMenuLink();   break;
		case ORDER:                renderHeaderOrder();             break;
		case PRIORITY:             renderHeaderPriority();          break;		
		case SELECTOR:             renderHeaderSelectCB();          break;
		case STATUS:               renderHeaderStatus();            break;		
		case LOCATION:             renderHeaderLocation();          break;		
		}
	}
	
	/*
	 * Renders the 'Assigned To' column header.
	 */
	private void renderHeaderAssignedTo() {
		Anchor a = GwtClientHelper.buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_assignedTo());
		markAsSortKey(a, Column.ASSIGNED_TO);
		a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.ASSIGNED_TO.m_sortKey);
		EventWrapper.addHandler(a, m_columnClickHandler);
		m_flexTable.setWidget(0, getColumnIndex(Column.ASSIGNED_TO), a);
	}
	
	/*
	 * Renders the 'Closed - % Done' column header.
	 */
	private void renderHeaderClosedPercentDone() {
		Anchor a = GwtClientHelper.buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_closedPercentDone());
		markAsSortKey(a, Column.CLOSED_PERCENT_DONE);
		a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.CLOSED_PERCENT_DONE.m_sortKey);
		EventWrapper.addHandler(a, m_columnClickHandler);
		int colIndex = getColumnIndex(Column.CLOSED_PERCENT_DONE);
		m_flexTable.setWidget(0, colIndex, a);
		if (m_taskBundle.getIsFromFolder()) {
			m_flexTableCF.setWidth(0, colIndex, "100%");
		}
	}
	
	/*
	 * Renders the 'Due Date' column header.
	 */
	private void renderHeaderDueDate() {
		FlowPanel fp = new FlowPanel();
		Anchor a = GwtClientHelper.buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_dueDate());
		markAsSortKey(a, Column.DUE_DATE);
		a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.DUE_DATE.m_sortKey);
		EventWrapper.addHandler(a, m_columnClickHandler);
		fp.add(a);

		// Add a 'Due Date' busy Image that we'll use when calculating
		// dates for a task.  Depending on that task relationships
		// involved, that could be a time consuming operation.
		m_dueDateBusy = buildSpacer(14, 14);
		m_dueDateBusy.addStyleName("marginleft2px");
		m_dueDateBusy.getElement().setAttribute("align", "absmiddle");
		fp.add(m_dueDateBusy);
		
		m_flexTable.setWidget(0, getColumnIndex(Column.DUE_DATE), fp);
	}

	/*
	 * Renders the 'Location' column header.
	 */
	private void renderHeaderLocation() {
		// Are we displaying tasks assigned to the current user?
		if (!(m_taskBundle.getIsFromFolder())) {
			// Yes!  Render the column header.
			Anchor a = GwtClientHelper.buildAnchor("sort-column");
			a.getElement().setInnerHTML(m_messages.taskColumn_location());
			markAsSortKey(a, Column.LOCATION);
			a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.LOCATION.m_sortKey);
			EventWrapper.addHandler(a, m_columnClickHandler);
			int colIndex = getColumnIndex(Column.LOCATION);
			m_flexTable.setWidget( 0, colIndex, a);
			m_flexTableCF.setWidth(0, colIndex, "100%");
		}
	}

	/*
	 * Renders the 'New Task Menu' link column header.
	 */
	private void renderHeaderNewTaskMenuLink() {
		// Add a non-breaking space so that IE displays the cell
		// correctly.
		m_flexTable.setHTML(0, getColumnIndex(Column.NEW_TASK_MENU), "&nbsp;");
	}
	
	/*
	 * Renders the 'Order' column header.
	 */
	private void renderHeaderOrder() {
		// Are we supposed to show the 'Order' column?
		if (showOrderColumn()) {
			// Yes!  Render the column header.
			Anchor a = GwtClientHelper.buildAnchor("sort-column");
			a.getElement().setInnerHTML(m_messages.taskColumn_order());
			markAsSortKey(a, Column.ORDER);
			a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.ORDER.m_sortKey);
			EventWrapper.addHandler(a, m_columnClickHandler);
			int colIndex = getColumnIndex(Column.ORDER);
			m_flexTableCF.setHorizontalAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
			m_flexTable.setWidget(               0, colIndex, a);
		}
	}
	
	/*
	 * Renders the 'Priority' column header.
	 */
	private void renderHeaderPriority() {
		Anchor a = GwtClientHelper.buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_priority());
		markAsSortKey(a, Column.PRIORITY);
		a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.PRIORITY.m_sortKey);
		EventWrapper.addHandler(a, m_columnClickHandler);
		m_flexTable.setWidget(0, getColumnIndex(Column.PRIORITY), a);
	}
	
	/*
	 * Renders the 'Selector' column header.
	 */
	private void renderHeaderSelectCB() {
		CheckBox cb = new CheckBox();
		cb.addStyleName("gwtTaskList_ckbox");
		cb.getElement().setId("gwtTaskList_taskSelect_All");
		EventWrapper.addHandler(cb, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				handleSelectAll(((CheckBox) event.getSource()).getValue());
			}			
		});
		int colIndex = getColumnIndex(Column.SELECTOR);
		m_flexTableCF.setAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(     0, colIndex, cb);
	}
	
	/*
	 * Renders the 'Status' column header.
	 */
	private void renderHeaderStatus() {
		Anchor a = GwtClientHelper.buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_status());
		markAsSortKey(a, Column.STATUS);
		a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.STATUS.m_sortKey);
		EventWrapper.addHandler(a, m_columnClickHandler);
		m_flexTable.setWidget(0, getColumnIndex(Column.STATUS), a);
	}
	
	/*
	 * Renders the 'Task Name' column header.
	 */
	private void renderHeaderTaskName() {
		Anchor a = GwtClientHelper.buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_name());
		markAsSortKey(a, Column.TASK_NAME);
		a.getElement().setAttribute(ATTR_COLUMN_SORT_KEY, Column.TASK_NAME.m_sortKey);
		EventWrapper.addHandler(a, m_columnClickHandler);
		m_flexTable.setWidget(0, getColumnIndex(Column.TASK_NAME), a);
	}
	
	/*
	 * Renders the tasks in the TaskBundle.
	 */
	private long renderTaskBundle(TaskBundle taskBundle, List<Long> checkedTaskIds) {
		// Save the time when we start.
		long start = System.currentTimeMillis();

		// Decide how the table should be sorted...
		m_taskBundle = taskBundle;
		if (m_newTaskTable) {
			m_newTaskTable = false;
			initializeUIData(true);	// true -> Forces the order and depths to be reset.
			initializeSorting();
		}

		// ...apply any task checks that are being preserved...
		if (null != checkedTaskIds) {
			for (Long entryId:  checkedTaskIds) {
				TaskListItem task = TaskListItemHelper.findTask(m_taskBundle, entryId);
				if (null != task) {
					getUIData(task).setTaskSelected(true);
				}
			}
		}
				
		// ...and render the TaskTable.
		clearTaskTable();
		List<TaskListItem> tasks = taskBundle.getTasks();
		
		// Are there any tasks to show?
		if (tasks.isEmpty()) {
			// No!  Add a message saying there are no tasks.
			int row = m_flexTable.getRowCount();
			m_flexTableCF.setColSpan(row, 0, 8);
			m_flexTableCF.addStyleName(row, 0, "paddingTop10px");
			InlineLabel il = new InlineLabel(m_messages.taskNoTasks());
			il.addStyleName("wiki-noentries-panel marginleft5px");
			m_flexTable.setWidget(row, 0, il);
		}
		else {
			// Yes, there any tasks to show!
			renderTaskList(tasks);
		}

		// Validate the task tools for what we've got displayed.
		validateTaskToolsAsync();
		
		// Finally, return how long we took to render the task bundle.
		long end = System.currentTimeMillis();
		return (end - start);
	}
	
	private long renderTaskBundle(TaskBundle tb, boolean updateCalculatedDates) {
		// Always use the initial form of the method.
		long reply = renderTaskBundle(tb, null);	// null -> No checked tasks to preserve.

		// After displaying the table, are we supposed to update the
		// calculated dates?
		if (updateCalculatedDates) {
			// Yes!  Perform the update.  Note that we run this
			// asynchronously so that the user is able to view and
			// interact with the task table while we're updating.
			updateCalculatedDatesAsync(null, tb.getBinderId(), null);
		}
		
		return reply;
	}
	
	private long renderTaskBundle(TaskBundle tb) {
		// Always use the initial form of the method.
		return renderTaskBundle(tb, null);	// null -> No checked tasks to preserve.
	}

	/*
	 * Renders a TaskListItem into the TaskTable.
	 */
	private void renderTaskItem(final TaskListItem task) {		
		// Add the style to the row...
		int row = m_flexTable.getRowCount();
		m_flexTableRF.addStyleName(row, "regrow");
		
		// ...store the row index in the table that this row is
		// ...being rendered at... 
		getUIData(task).setTaskRow(row);
		
		// ...and render the columns.
		for (Column col:  Column.values()) {
			renderColumn(task, row, col);
		}		
	}

	/*
	 * Renders the tasks in the List<TaskListItem>.
	 */
	private void renderTaskListImpl(List<TaskListItem> tasks, boolean topLevelTasks) {
		// Scan the tasks in the list...
		boolean hasQuickFilter = (null != m_quickFilter);
		for (TaskListItem task:  tasks) {
			if (hasQuickFilter) {
				if (isTaskInQuickFilter(task)) {
					renderTaskItem(task);
				}
			}
			else {
				// ..rendering each one...
				renderTaskItem(task);
			
				// ...and their subtasks.
				if (task.getExpandSubtasks()) {
					renderTaskListImpl(task.getSubtasks(), false);
				}
			}
		}
	}
	
	private void renderTaskList(List<TaskListItem> tasks) {
		// Always use the implementation form of the method.
		renderTaskListImpl(tasks, true);
	}

	/**
	 * Called by TaskPopupMenu when a selection has been made in one of
	 * the task's option menus.
	 * 
	 * @param task
	 * @param event
	 * @param optionValue
	 */
	public void setTaskOption(TaskListItem task, TeamingEvents event, String optionValue) {
		switch (event) {
		case TASK_NEW_TASK:          handleTaskNewTask(       task, optionValue); break;
		case TASK_SET_PERCENT_DONE:  handleTaskSetPercentDone(task, optionValue); break;
		case TASK_SET_PRIORITY:      handleTaskSetPriority(   task, optionValue); break;
		case TASK_SET_STATUS:        handleTaskSetStatus(     task, optionValue); break;
			
		default:
			GwtClientHelper.deferredAlert(m_messages.taskInternalError_UnexpectedEvent(event.toString()));
			break;
		}
	}

	/*
	 * Checks or removes the check from all the tasks in the TaskTable.
	 */
	private void selectAllTasksAsync(final boolean select) {
		final ProcessActive pa = buildProcessActive(
			(select                                ?
				m_messages.taskProcess_selectAll() :
				m_messages.taskProcess_unSelectAll()),
			0);
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				selectAllTasksNow(pa, select);
			}
		});
	}
	
	private void selectAllTasksNow(ProcessActive pa, boolean select) {
		selectAllTasksImpl(m_taskBundle.getTasks(), select);
		if (null != pa) {
			pa.killIt();
		}
		validateTaskToolsAsync();
	}
	
	private void selectAllTasksImpl(List<TaskListItem> tasks, boolean selected) {
		// Scan the tasks.
		for (TaskListItem task:  tasks) {
			// Mark this task's selection state.
			UIData uid = getUIData(task);
			uid.setTaskSelected(selected);
			
			// Is this task visible?  (If a task's subtasks are
			// collapsed, the subtasks aren't visible.)
			CheckBox selectorCB = uid.getTaskSelectorCB();
			if (null != selectorCB) {
				// Yes!  Set its checkbox state...
				selectorCB.setValue(selected);
				
				// ...and make the appropriate style change to the row.
				int row = uid.getTaskRow();
				if (selected)
			         m_flexTableRF.addStyleName(   row, "selected");
			    else m_flexTableRF.removeStyleName(row, "selected");
			}
			
			// Select this task's subtasks.
			selectAllTasksImpl(task.getSubtasks(), selected);
		}
	}

	/*
	 * Returns true if we're in a mode that should show structure and
	 * false otherwise.
	 */
	private boolean shouldShowStructure() {
		return (m_taskBundle.respectLinkage() && (Column.ORDER == m_sortColumn));
	}

	/*
	 * Returns true if the hierarchy manipulation warning should be
	 * shown on the tool bar and false otherwise.
	 */
	private boolean shouldShowToolWarning() {
		boolean byFilter  = m_taskBundle.getIsFiltered();
		boolean byRights  = (!(m_taskBundle.getCanModifyTaskLinkage()));
		boolean bySort    = ((Column.ORDER != m_sortColumn) || (!m_sortAscending));
		boolean byVirtual = (!(m_taskBundle.getIsFromFolder()));
		// GwtClientHelper.deferredAlert("f:" + byFilter + ", r:" + byRights + ", s:" + bySort + ", v:" + byVirtual);
		return (bySort || byVirtual || byFilter || byRights);
	}
	
	/*
	 * Returns true if we should show the order column and false
	 * otherwise.
	 */
	private boolean showOrderColumn() {
		// We hide the order column if the TaskBundle tells us to not
		// respect the task linkage information.
		return m_taskBundle.respectLinkage();
	}
	
	/**
	 * Shows the tasks in the TaskBundle.
	 * 
	 * @param taskBundle
	 * 
	 * @return	The time, in milliseconds, it took to show the tasks.
	 */
	public long showTasks(TaskBundle tb) {
		// Are we currently in a mode that respects the task linkage
		// and can the user modify the task linkage?
		Long            newTaskId       = null;
		Long            selectedTaskId  = null;
		TaskDisposition taskDisposition = null;
		if (tb.respectLinkage() && tb.getCanModifyTaskLinkage()) {
			// Yes!  Were we call because the user added a new task?
			String taskChangeReason = m_taskListing.getTaskChangeReason();
			if ((null != taskChangeReason) && taskChangeReason.equals("taskAdded")) {
				// Yes!  What's the ID of the new task?
				newTaskId = m_taskListing.getTaskChangeId();
				
				// When they added the task, was there one and only one
				// task selected?
				String selectedTaskIdS = jsGetSelectedTaskId();
				if (GwtClientHelper.hasString(selectedTaskIdS)) {
					// Yes!  What's ID of the task that was selected?
					selectedTaskId = Long.parseLong(selectedTaskIdS);
					
					// Do we already know how the user wants to dispose
					// of this task?
					String newTaskDisposition = jsGetNewTaskDisposition();
					if (GwtClientHelper.hasString(newTaskDisposition)) {
						// Yes!  Track how they want it disposed of.
						taskDisposition = TaskDisposition.valueOf(newTaskDisposition);
						TaskListItem newTask = TaskListItemHelper.findTask(tb, newTaskId);
						getUIData(newTask).setTaskSelected(true);
					}
					
					else {
						// No, we don't know how the user wants to
						// dispose of this task!  We'll need to ask
						// them where they want to put it.  Mark the
						// selected task so they have a reference.
						TaskListItem selectedTask = TaskListItemHelper.findTask(tb, selectedTaskId);
						getUIData(selectedTask).setTaskSelected(true);
					}
				}
				
				else {
					// No, there was other that one task selected!
					// Simply append and select the new task.
					taskDisposition = TaskDisposition.APPEND;
					TaskListItem newTask = TaskListItemHelper.findTask(tb, newTaskId);
					getUIData(newTask).setTaskSelected(true);
				}
			}
		}

		// Forget about any new task stuff we may have stored for use
		// above.
		jsSetNewTaskDisposition("");
		jsSetSelectedTaskId(    "");
		
		// Render the tasks from the bundle.
		boolean disposeWillUpdate = ((null != newTaskId) && (null != taskDisposition) && (!(TaskDisposition.APPEND.equals(taskDisposition))));
		m_renderTime = renderTaskBundle(tb, ((!disposeWillUpdate) && m_taskListing.getUpdateCalculatedDates()));

		// Did we just add a new task?
		if (null != newTaskId) {
			// Yes!  Do we know where the new task should be placed?
			if (null != taskDisposition) {
				// Yes!  Just put it there.
				applyTaskDisposition(
					taskDisposition,
					newTaskId,
					selectedTaskId,
					m_taskListing.getUpdateCalculatedDates());		
			}

			// No, we don't know where the new task should be placed!
			// Should we prompt the user for where?
			else if ((Column.ORDER == m_sortColumn) && m_sortAscending && (null != selectedTaskId)) {
				// Yes!  Do it.
				promptForDispositionAsync(newTaskId, selectedTaskId);
			}
		}
		
		// ...finally, return the time it took to render the tasks.
		return m_renderTime;
	}
	
	/*
	 * Sorts the List<TaskListItem> by column in the specified order.
	 */
	private void sortByColumn(Column previousSortColumn) {
		// Are we currently in a mode that respects the task linkage?
		// (We don't if the list is filtered or showing 'Assigned To'
		// items.)
		if (m_taskBundle.respectLinkage() && (null == m_quickFilter)) {
			// Yes!  Are we sorting on other than the order column when
			// we were previously sorted on the order column?
			if ((Column.ORDER != m_sortColumn) && (Column.ORDER == previousSortColumn)) {
				// Yes!  Then we have to flatten the task list before
				// sorting.  Thanks Tracy !!!  :-)
				TaskListItemHelper.flattenTaskList(m_taskBundle);
				initializeUIData(false);	// false -> Forces the depths, but not the order to be reset.
			}
			
			// No, we didn't have to flatten the list!  Are we sorting
			// on the order column when we were previously sorted on
			// other than the order column?
			else if ((Column.ORDER == m_sortColumn) && (Column.ORDER != previousSortColumn)) {
				// Yes!  Then we have to restructure the list before
				// sorting.  Thanks Tracy !!!  :-)
				TaskListItemHelper.applyTaskLinkage(m_taskBundle);
				initializeUIData(true);		// true -> Forces the order and depths to be reset.
			}
		}

		// Apply the sort base on the selected column.
		sortByColumnImpl();
	}
	
	/*
	 * Apply the sort base on the selected column.
	 */
	private void sortByColumnImpl() {
		Comparator<TaskListItem> comparator;
		switch(m_sortColumn) {
		default:
		case ORDER:                comparator = new TaskSorter.OrderComparator(            m_sortAscending); break;
		case NEW_TASK_MENU:        m_sortColumn = Column.TASK_NAME;
		case TASK_NAME:            comparator = new TaskSorter.NameComparator(             m_sortAscending); break;
		case PRIORITY:             comparator = new TaskSorter.PriorityComparator(         m_sortAscending); break;
		case DUE_DATE:             comparator = new TaskSorter.DueDateComparator(          m_sortAscending); break;
		case STATUS:               comparator = new TaskSorter.StatusComparator(           m_sortAscending); break;
		case ASSIGNED_TO:          comparator = new TaskSorter.AssignedToComparator(       m_sortAscending); break;
		case CLOSED_PERCENT_DONE:  comparator = new TaskSorter.ClosedPercentDoneComparator(m_sortAscending); break;		
		case LOCATION:             comparator = new TaskSorter.LocationComparator(         m_sortAscending); break;		
		}
		TaskSorter.sort(m_taskBundle.getTasks(), comparator);
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	/*
	 * Makes a GWT RPC call to the server to update the calculated
	 * dates for the binder and/or task, and any related subtasks.  If
	 * the RPC call succeeds any modified end dates will be reflected
	 * in the task table.
	 */
	private void updateCalculatedDatesNow(final ProcessActive pa, final Long binderId, final Long entryId) {
		if (null != pa) {
			pa.setMessage(m_messages.taskProcess_updatingDates());
		}
		
		m_dueDateBusy.setResource(m_images.busyAnimation_small());
		UpdateCalculatedDatesCmd cmd = new UpdateCalculatedDatesCmd(binderId, entryId);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				m_dueDateBusy.setResource(m_images.spacer());
				if (null == entryId)
				     GwtClientHelper.handleGwtRPCFailure(caught, GwtTeaming.getMessages().rpcFailure_UpdateCalculatedDatesBinder(), String.valueOf(binderId));
				else GwtClientHelper.handleGwtRPCFailure(caught, GwtTeaming.getMessages().rpcFailure_UpdateCalculatedDatesTask(),   String.valueOf(entryId ));

				// If we have a ProcessActive message going...
				if (null != pa) {
					// ...kill it.
					pa.killIt();
				}
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				UpdateCalculatedDatesRpcResponseData responseData = ((UpdateCalculatedDatesRpcResponseData) result.getResponseData());
				Map<Long, TaskDate> updatedTaskInfo = responseData.getUpdateCalculatedDatesResults();
				
				// Did any tasks have the logical end dates changed?
				m_dueDateBusy.setResource(m_images.spacer());
				if (GwtClientHelper.hasItems(updatedTaskInfo)) {
					// Yes!  Scan them...
					Date now = new Date();
					long nowMS = now.getTime();
					for (Long entryId:  updatedTaskInfo.keySet()) {
						// ...storing their new logical end dates...
						TaskDate     dueDate = updatedTaskInfo.get(entryId);
						TaskListItem task = TaskListItemHelper.findTask(m_taskBundle, entryId);
						TaskInfo     ti = task.getTask();
						TaskEvent    tie  = ti.getEvent();
						tie.setLogicalEnd(dueDate);
						tie.setEndIsCalculated(true);
						
						// ...if the task's overdue state changed... 
						long dueMS = (((null == dueDate) || (!(GwtClientHelper.hasString(dueDate.getDateDisplay())))) ? Long.MAX_VALUE : dueDate.getDate().getTime()); 
						boolean overdue = (nowMS > dueMS);
						int row = getUIData(task).getTaskRow();
						if (overdue != ti.getOverdue()) {
							// ...track that fact and redisplay the
							// ...task's name...
							ti.setOverdue(overdue);
							renderColumnTaskName(task, row);
						}
						
						// ...and redisplay the task's due date.
						renderColumnDueDate(task, row);
					}
				}
				
				// If we have a ProcessActive message going...
				if (null != pa) {
					// ...kill it.
					pa.killIt();
				}
			}
		});
	}
	
	private void updateCalculatedDatesAsync(final ProcessActive pa, final Long binderId, final Long entryId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				updateCalculatedDatesNow(pa, binderId, entryId);
			}
		});
	}
	
	/*
	 * Based on what's selected in the task list, validates the tools
	 * in the TaskListing.
	 */
	private void validateTaskToolsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				validateTaskToolsNow();
			}
		});
	}
	
	private void validateTaskToolsNow() {
		String  arrowHint = null;

		// Get the checked tasks and count.
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		String selectedTaskId;
		if (1 == tasksCheckedCount)
		     selectedTaskId = String.valueOf(tasksChecked.get(0).getTask().getTaskId().getEntityId());
		else selectedTaskId = "";
		jsSetSelectedTaskId(selectedTaskId);
		
		// Can the user perform a purge or delete?
		boolean enablePurge = (m_taskBundle.getCanPurgeEntry() && (0 < tasksCheckedCount) && TaskListItemHelper.canPurgeTask(tasksChecked));
		boolean enableTrash = (m_taskBundle.getCanTrashEntry() && (0 < tasksCheckedCount) && TaskListItemHelper.canTrashTask(tasksChecked) && (!(m_taskBundle.getBinderIsMirrored())));
		
		// Does the user have the rights to manage linkage?
		MoveStates moveStates;
		boolean allowMovement = m_taskBundle.getCanModifyTaskLinkage();
		if (allowMovement) {				
			// Yes!  Validate the the base criteria about whether the
			// movement buttons are enabled.  Are there any movable
			// tasks selected?
			moveStates = buildMoveStates(tasksChecked);
			allowMovement = moveStates.canMoveAny();
			if (allowMovement) {
				// Yes!  Is this list a non-filtered list?
				allowMovement = (!(m_taskBundle.getIsFiltered()));
				if (allowMovement) {
					// Yes!  Is it a list of tasks from a folder (vs.
					// those assigned to the user)?
					allowMovement = m_taskBundle.getIsFromFolder();
					if (allowMovement) {
						// Yes!  Are we sorted on the order column?
						allowMovement = ((Column.ORDER == m_sortColumn) && m_sortAscending);
						if (!allowMovement) {
							// Disallowed:  Not order/ascending.
							arrowHint = m_messages.taskCantMove_Order();
						}
					}
					else {
						// Disallowed:  Virtual.
						arrowHint = m_messages.taskCantMove_Virtual();
					}
				}
				else {
					// Disallowed:  Filters.
					arrowHint = m_messages.taskCantMove_Filter();
				}
			}			
			else {
				// Disallowed:  No movable tasks selected.
				if (0 == tasksCheckedCount)
				     arrowHint = m_messages.taskCantMove_Zero();
				else arrowHint = m_messages.taskCantMove_NoMoveableTasksSelected();
			}
	
			// Is the base criteria for movement satisfied?
			if (!allowMovement) {
				// No!  Then use a cleared MoveStates object.
				moveStates.reset();
			}
		}
		else {
			// Disallowed:  Insufficient rights.
			moveStates = new MoveStates();
			arrowHint = m_messages.taskCantMove_Rights();
		}

		if (shouldShowToolWarning())
		     m_taskListing.showTaskToolsWarning();
		else m_taskListing.showTaskToolsLinkage();
		
		// Enabled/disable the buttons as calculated.
		m_taskListing.getMoveDownButton().setEnabled( moveStates.canMoveDown(),  arrowHint);
		m_taskListing.getMoveLeftButton().setEnabled( moveStates.canMoveLeft(),  arrowHint);
		m_taskListing.getMoveRightButton().setEnabled(moveStates.canMoveRight(), arrowHint);
		m_taskListing.getMoveUpButton().setEnabled(   moveStates.canMoveUp(),    arrowHint);
		m_taskListing.setEntriesSelected((enableTrash || enablePurge), (1 == tasksCheckedCount));
	}
}
