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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.EventWrapper;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TaskListItemHelper;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class that implements the Composite that contains the task folder
 * listing table.  
 * 
 * @author drfoster@novell.com
 */
public class TaskTable extends Composite implements ActionHandler {
	private boolean					m_sortAscending;			//
	private Column					m_sortColumn;				//
	private EventHandler			m_assigneeMouseOutEvent;	//
	private EventHandler			m_assigneeMouseOverEvent;	//
	private EventHandler			m_cbClickHandler;			//
	private EventHandler			m_expanderClickHandler;		//
	private EventHandler			m_taskOptionClickHandler;	//
	private EventHandler			m_taskSeenClickHandler;		//
	private EventHandler			m_taskViewClickHandler;		//
	private FlexCellFormatter		m_flexTableCF;				//
	private FlexTable				m_flexTable;				//
	private Image 					m_dueDateBusy;				//
	private long					m_renderTime;				//
	private ProcessActiveWidgets	m_processActiveWidgets;		//
	private RowFormatter			m_flexTableRF;				//
	private TaskBundle				m_taskBundle;				//
	private TaskListing				m_taskListing;				//
	private TaskPopupMenu			m_percentDoneMenu;			//
	private TaskPopupMenu			m_priorityMenu;				//
	private TaskPopupMenu			m_statusMenu;				//
	
	private       boolean							m_newTaskTable = true;										//
	private final GwtMainPage						m_gwtMainPage  = GwtTeaming.getMainPage();					// 
	private final GwtRpcServiceAsync				m_rpcService   = GwtTeaming.getRpcService();				// 
	private final GwtTeamingMessages				m_messages     = GwtTeaming.getMessages();					//
	private final GwtTeamingTaskListingImageBundle	m_images       = GwtTeaming.getTaskListingImageBundle();	//
	
	// The following defines attributes added to various task table
	// widgets to enable backtracking to their related task, ...
	private final static String ATTR_ENTRY_ID		= "n_entryId";
	private final static String ATTR_OPTION_MENU	= "n_optionMenu";

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
	
	/*
	 * Enumeration used to represent the type of an AssignmentInfo.
	 */
	private enum AssigneeType {
		INDIVIDUAL,
		GROUP,
		TEAM,
	}
	
	/*
	 * Enumeration used to represent the order of the columns in the
	 * TaskTable.
	 */
	private enum Column {
		SELECTOR(           "*Unsortable*"),
		ORDER(              "order"),
		TASK_NAME(          "_sortTitle"),
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
			DOM.setStyleAttribute(fp.getElement(), "left", Integer.toString(left) + "px");
			
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
			Image busyImg = new Image(m_images.busyAnimation());
			busyImg.getElement().setAttribute("align", "absmiddle");
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
		private Image		m_taskPercentDoneImage;		//
		private Image		m_taskPriorityImage;		//
		private Image		m_taskStatusImage;			//
		private InlineLabel m_taskCompletedLabel;		//
		private InlineLabel	m_taskLabel;				//
		private int			m_taskDepth;				//
		private int 		m_taskOrder = (-1);			//
		private int 		m_taskRow;					//
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
		public Image       getTaskPercentDoneImage()  {return m_taskPercentDoneImage; }
		public Image       getTaskPriorityImage()     {return m_taskPriorityImage;    }
		public Image       getTaskStatusImage()       {return m_taskStatusImage;      }
		public InlineLabel getTaskCompletedLabel()    {return m_taskCompletedLabel;   }
		public InlineLabel getTaskLabel()             {return m_taskLabel;            }
		public int         getTaskDepth()             {return m_taskDepth;            }
		public int         getTaskOrder()             {return m_taskOrder;            }
		public int         getTaskRow()               {return m_taskRow;              }
		public Widget      getTaskPercentDoneWidget() {return m_taskPercentDoneWidget;}

		public void setTaskSelected(         boolean     taskSelected)          {m_taskSelected          = taskSelected;         }
		public void setTaskUnseenAnchor(     Anchor      taskUnseenAnchor)      {m_taskUnseenAnchor      = taskUnseenAnchor;     }
		public void setTaskSelectorCB(       CheckBox    taskSelectorCB)        {m_taskSelectorCB        = taskSelectorCB;       } 
		public void setTaskPercentDoneImage( Image       taskPercentDoneImage)  {m_taskPercentDoneImage  = taskPercentDoneImage; }
		public void setTaskPriorityImage(    Image       taskPriorityImage)     {m_taskPriorityImage     = taskPriorityImage;    }
		public void setTaskStatusImage(      Image       taskStatusImage)       {m_taskStatusImage       = taskStatusImage;      }
		public void setTaskCompletedLabel(   InlineLabel taskCompletedLabel)    {m_taskCompletedLabel    = taskCompletedLabel;   }
		public void setTaskLabel(            InlineLabel taskLabel)             {m_taskLabel             = taskLabel;            }
		public void setTaskDepth(            int         taskDepth)             {m_taskDepth             = taskDepth;            }
		public void setTaskOrder(            int         taskOrder)             {m_taskOrder             = taskOrder;            }
		public void setTaskRow(              int         taskRow)               {m_taskRow               = taskRow;              }
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
		
		// ...create the popup menus we'll need for the TaskTable.
		List<TaskMenuOption> pOpts = new ArrayList<TaskMenuOption>();
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_CRITICAL, m_images.p1(), m_messages.taskPriority_p1()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_HIGH,     m_images.p2(), m_messages.taskPriority_p2()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_MEDIUM,   m_images.p3(), m_messages.taskPriority_p3()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_LOW,      m_images.p4(), m_messages.taskPriority_p4()));
		pOpts.add(new TaskMenuOption(TaskInfo.PRIORITY_LEAST,    m_images.p5(), m_messages.taskPriority_p5()));
		m_priorityMenu = new TaskPopupMenu(this, TeamingAction.TASK_SET_PRIORITY, pOpts);

		List<TaskMenuOption> sOpts = new ArrayList<TaskMenuOption>();
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_COMPLETED,    m_images.completed(),   m_messages.taskStatus_completed()));
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_IN_PROCESS,   m_images.inProcess(),   m_messages.taskStatus_inProcess()));
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_NEEDS_ACTION, m_images.needsAction(), m_messages.taskStatus_needsAction()));
		sOpts.add(new TaskMenuOption(TaskInfo.STATUS_CANCELED,     m_images.cancelled(),   m_messages.taskStatus_cancelled()));
		m_statusMenu = new TaskPopupMenu(this, TeamingAction.TASK_SET_STATUS, sOpts);

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
		m_percentDoneMenu = new TaskPopupMenu(this, TeamingAction.TASK_SET_PERCENT_DONE, pdOpts);

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
			PresenceControl pc = new PresenceControl(String.valueOf(ai.getPresenceUserWSId()), false, false, false, presence);
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
			Image assigneeImg = new Image();
			assigneeImg.setUrl(m_taskListing.getRequestInfo().getImagesPath() + ai.getPresenceDude());
			assigneeImg.getElement().setAttribute("align", "absmiddle");
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
		Image img = buildImage(selectedOption.getMenuImageRes(), selectedOption.getMenuAlt());
		final Element imgElement = img.getElement();
		if      (taskMenu == m_priorityMenu)    uid.setTaskPriorityImage(   img);
		else if (taskMenu == m_statusMenu)      uid.setTaskStatusImage(     img);
		else if (taskMenu == m_percentDoneMenu) uid.setTaskPercentDoneImage(img);

		// Does the user have rights to modify this task?
		Widget reply;
		if (task.getTask().getCanModify()) {	
			// Yes!  Generate the Anchor for this option.
			Anchor  a  = buildAnchor(anchorStyle);
			Element aE = a.getElement();
			aE.appendChild(imgElement);
			aE.appendChild(buildImage(m_images.menu()).getElement());
			aE.setAttribute(ATTR_ENTRY_ID, String.valueOf(task.getTask().getTaskId().getEntryId()));
			aE.setAttribute(ATTR_OPTION_MENU, taskMenu.getTaskAction().toString());
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
		Image reply = new Image(m_images.spacer());
		reply.setHeight(String.valueOf(height) + "px");
		reply.setWidth( String.valueOf(width ) + "px");
		return reply;
	}
	
	private Image buildSpacer(int width) {
		return buildSpacer(16, width);
	}

	private Image buildSpacer() {
		return buildSpacer(16, 16);
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

		// Event handler used when the user clicks on a task's
		// expand / collapse widget.
		m_expanderClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handleTaskExpander(getTaskFromEventWidget((Widget) event.getSource()));
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
				String optionAction = w.getElement().getAttribute(ATTR_OPTION_MENU);
				if      (optionAction.equals(TeamingAction.TASK_SET_PRIORITY.toString()))     {taskMenu = m_priorityMenu;    taskMenuImg = uid.getTaskPriorityImage();   }
				else if (optionAction.equals(TeamingAction.TASK_SET_PERCENT_DONE.toString())) {taskMenu = m_percentDoneMenu; taskMenuImg = uid.getTaskPercentDoneImage();}
				else if (optionAction.equals(TeamingAction.TASK_SET_STATUS.toString()))       {taskMenu = m_statusMenu;      taskMenuImg = uid.getTaskStatusImage();     }
				else                                                                          {return;}
				
				// ...and run the menu.
				taskMenu.showTaskPopupMenu(task, taskMenuImg.getElement());
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
	 * Given a Widget from an event, returns it's corresponding task.
	 */
	private TaskListItem getTaskFromEventWidget(Widget w) {
		String entryId = w.getElement().getAttribute(ATTR_ENTRY_ID);
		return TaskListItemHelper.findTask(m_taskBundle, Long.parseLong(entryId));
	}
	
	/*
	 * Returns a List<Long> of the IDs of the tasks in the TaskTable
	 * that are currently checked.
	 */
	private List<Long> getTaskIdsChecked() {
		List<Long> reply = new ArrayList<Long>();;
		getTaskIdsCheckedImpl(m_taskBundle.getTasks(), reply);
		return reply;
	}
	
	private void getTaskIdsCheckedImpl(List<TaskListItem> tasks, List<Long> checkedTaskIds) {
		for (TaskListItem task:  tasks) {
			if (getUIData(task).isTaskCBChecked()) {
				checkedTaskIds.add(task.getTask().getTaskId().getEntryId());
			}
			getTaskIdsCheckedImpl(task.getSubtasks(), checkedTaskIds);
		}
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
			if (getUIData(task).isTaskCBChecked()) {
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
		initializeUIDataImpl(m_taskBundle.getTasks(), 0, updateOrder);
	}
	
	private void initializeUIData() {
		// Always use the initial form of the method.
		initializeUIData(true);
	}
	
	private void initializeUIDataImpl(List<TaskListItem> tasks, int taskDepth, boolean updateOrder) {
		int     taskOrder    = 1;
		boolean baseTask     = (0 == taskDepth);
		int     subtaskDepth = (taskDepth + 1);
		for (TaskListItem task:  tasks) {
			// Build a UIData object for this TaskListItem...
			UIData newUID = new UIData(((UIData) task.getUIData()));
			task.setUIData(newUID);
			newUID.setTaskDepth(taskDepth);
			if (updateOrder) {
				if (baseTask) {
					newUID.setTaskOrder(taskOrder);
					taskOrder += 1;
				}
				else {
					newUID.setTaskOrder(-1);
				}
			}

			// ...and build the UIData's for any subtasks.
			initializeUIDataImpl(task.getSubtasks(), subtaskDepth, updateOrder);
		}
	}
	
	/**
	 * Called by TaskPopupMenu when a selection has been made in one of
	 * the task's option menus.
	 * 
	 * @param task
	 * @param action
	 * @param optionValue
	 */
	public void setTaskOption(TaskListItem task, TeamingAction action, String optionValue) {
		switch (action) {
		case TASK_SET_PERCENT_DONE:  handleTaskSetPercentDone(task, optionValue); break;
		case TASK_SET_PRIORITY:      handleTaskSetPriority(   task, optionValue); break;
		case TASK_SET_STATUS:        handleTaskSetStatus(     task, optionValue); break;
			
		default:
			Window.alert(m_messages.taskInternalError_UnexpectedAction(action.toString()));
			break;
		}
	}

	/**
	 * Called handle one of the task TeamingActions.
	 * 
	 * Implements the ActionHandler.handleAction() method.
	 * 
	 * @param action
	 * @param obj
	 */
	@Override
	public void handleAction(TeamingAction action, Object obj) {
		switch (action) {
		case TASK_DELETE:      handleTaskDelete();    break;
		case TASK_MOVE_DOWN:   handleTaskMoveDown();  break;
		case TASK_MOVE_LEFT:   handleTaskMoveLeft();  break;
		case TASK_MOVE_RIGHT:  handleTaskMoveRight(); break;
		case TASK_MOVE_UP:     handleTaskMoveUp();    break;
		case TASK_PURGE:       handleTaskPurge();     break;

		default:
			Window.alert(m_messages.taskInternalError_UnexpectedAction(action.toString()));
			break;
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
			m_rpcService.getGroupMembership(HttpRequestInfo.createHttpRequestInfo(), assigneeId, new AsyncCallback<List<AssignmentInfo>>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetGroupMembership(),
						String.valueOf(assigneeId));
				}
				
				@Override
				public void onSuccess(List<AssignmentInfo> groupMembers) {
					// Store the group membership (so we don't re-read
					// it if it gets displayed again) and display it.
					ai.setMembership(           groupMembers                           );
					handleMembershipDisplay(ai, groupMembers, expansionFP, showDisabled);
				}
			});
			
			break;
			
		case TEAM:
			m_rpcService.getTeamMembership(HttpRequestInfo.createHttpRequestInfo(), assigneeId, new AsyncCallback<List<AssignmentInfo>>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTeamMembership(),
						String.valueOf(assigneeId));
				}
				
				@Override
				public void onSuccess(List<AssignmentInfo> teamMembers) {
					// Store the team membership (so we don't re-read
					// it if it gets displayed again) and display it.
					ai.setMembership(           teamMembers                           );
					handleMembershipDisplay(ai, teamMembers, expansionFP, showDisabled);
				}
			});
			
			break;
		}
	}
	
	/*
	 * This method gets invoked when the user clicks on an individual
	 * assignment's presence dude.
	 */
	private void handlePresenceSelect(AssignmentInfo ai, Element element) {
		SimpleProfileParams params;
		
		// Invoke the Simple Profile dialog.
		Long wsId = ai.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		params = new SimpleProfileParams(element, wsIdS, ai.getTitle());
		m_gwtMainPage.handleAction(TeamingAction.INVOKE_SIMPLE_PROFILE, params);
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
	 * Called when the user presses the delete button on the task tool
	 * bar.
	 */
	private void handleTaskDelete() {
		// If nothing is checked...
		List<TaskListItem> tasksChecked = getTasksChecked();
		if ((null == tasksChecked) || tasksChecked.isEmpty()) {
			// ...there's nothing to delete.
			return;
		}

		// Is the user sure they want to perform the delete?
		if (!(Window.confirm(m_messages.taskConfirmDelete()))) {
			// No!  Bail.
			return;
		}

		// Delete the selected tasks.
		final List<TaskId> taskIds = TaskListItemHelper.getTaskIdsFromList(tasksChecked, false);
		m_rpcService.deleteTasks(HttpRequestInfo.createHttpRequestInfo(), taskIds, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_DeleteTasks());
			}
			
			@Override
			public void onSuccess(Boolean success) {
				handleTaskPostRemoveAsync(taskIds);
			}
		});
	}
	
	/*
	 * Called when the user clicks the expand/collapse on a task.
	 */
	private void handleTaskExpander(final TaskListItem task) {
		// Extract the IDs we need to perform the expand/collapse.
		TaskId taskId   = task.getTask().getTaskId();
		Long   binderId = taskId.getBinderId();
		Long   entryId  = taskId.getEntryId();

		// Are we collapsing the subtasks?
		if (task.getExpandSubtasks()) {
			// Yes!  Collapse them...
			m_rpcService.collapseSubtasks(HttpRequestInfo.createHttpRequestInfo(), binderId, entryId, new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_CollapseSubtasks());
				}
				
				@Override
				public void onSuccess(Boolean success) {
					// ...and mark the task as having its subtasks
					// ...collapsed and redisplay the TaskTable. 
					task.setExpandedSubtasks(false);
					initializeUIData();	// Forces the referenced widgets to be reset.
					renderTaskBundle(m_taskBundle);
				}
			});
		}
		
		else {
			// No, we must be expanding the subtasks!  Expand them...
			m_rpcService.expandSubtasks(HttpRequestInfo.createHttpRequestInfo(), binderId, entryId, new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_ExpandSubtasks());
				}
				
				@Override
				public void onSuccess(Boolean success) {
					// ...and mark the task as having its subtasks
					// ...expanded and redisplay the TaskTable. 
					task.setExpandedSubtasks(true);
					initializeUIData();	// Forces the referenced widgets to be reset.
					renderTaskBundle(m_taskBundle);
				}
			});
		}
	}
	
	/*
	 * Called when the user presses the move down button on the task
	 * tool bar.
	 */
	private void handleTaskMoveDown() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (1 == tasksCheckedCount) {
			TaskListItem task = tasksChecked.get(0);
			TaskListItemHelper.moveTaskDown(m_taskBundle, task);
			handleTaskPostMove(buildProcessActive(m_messages.taskProcess_move()), task);
		}
	}
	
	/*
	 * Called when the user presses the move left button on the task
	 * tool bar.
	 */
	private void handleTaskMoveLeft() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (1 == tasksCheckedCount) {
			TaskListItem task = tasksChecked.get(0);
			TaskListItemHelper.moveTaskLeft(m_taskBundle, task);
			handleTaskPostMove(buildProcessActive(m_messages.taskProcess_move()), task);
		}
	}
	
	/*
	 * Called when the user presses the move right button on the task
	 * tool bar.
	 */
	private void handleTaskMoveRight() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (1 == tasksCheckedCount) {
			TaskListItem task = tasksChecked.get(0);
			TaskListItemHelper.moveTaskRight(m_taskBundle, task);
			handleTaskPostMove(buildProcessActive(m_messages.taskProcess_move()), task);
		}
	}
	
	/*
	 * Called when the user presses the move up button on the task tool
	 * bar.
	 */
	private void handleTaskMoveUp() {
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		if (1 == tasksCheckedCount) {
			TaskListItem task = tasksChecked.get(0);
			TaskListItemHelper.moveTaskUp(m_taskBundle, task);
			handleTaskPostMove(buildProcessActive(m_messages.taskProcess_move()), task);
		}
	}

	/*
	 * Does what's necessary after a task is moved to put the change
	 * into affect.
	 */
	private void handleTaskPostMove(final ProcessActive pa, TaskListItem task) {	
		initializeUIData();	// Forces the order and depths to be reset.
		renderTaskBundle(m_taskBundle);		

		// Persist whatever changed in the linkage information.
		final TaskId taskId = task.getTask().getTaskId();
		persistLinkageChangeAsync(
			task,
			new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					updateCalculatedDatesNow(
						pa,
						taskId.getBinderId(),
						taskId.getEntryId());
				}
		});
	}

	/*
	 * Does what's necessary after a task is deleted or purged to put
	 * the change into affect.
	 */
	private void handleTaskPostRemoveAsync(final List<TaskId> taskIds) {
		Scheduler.ScheduledCommand postRemover;
		postRemover = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				handleTaskPostRemoveNow(taskIds);
			}
		};
		Scheduler.get().scheduleDeferred(postRemover);
	}
	
	private void handleTaskPostRemoveNow(List<TaskId> taskIds) {
		// Scan the tasks that were removed...
		for (TaskId taskId:  taskIds) {
			// ...scan the task's subtasks...
			List<TaskListItem> taskList = TaskListItemHelper.findTaskList(m_taskBundle, taskId.getEntryId());
			TaskListItem       task     = TaskListItemHelper.findTask(    taskList,     taskId.getEntryId());
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
			null,	// null  -> No task.  We don't need one when not updating the calculated dates.
			new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					// Refreshing the TaskTable will reread the tasks
					// and display them in the appropriate hierarchy. 
					refreshTaskTableAsync(
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
		// If nothing is checked...
		List<TaskListItem> tasksChecked = getTasksChecked();
		if ((null == tasksChecked) || tasksChecked.isEmpty()) {
			// ...there's nothing to purge.
			return;
		}

		// Is the user sure they want to perform the purge?
		if (!(Window.confirm(m_messages.taskConfirmPurge()))) {
			// No!  Bail.
			return;
		}

		// Purge the selected tasks.
		final List<TaskId> taskIds = TaskListItemHelper.getTaskIdsFromList(tasksChecked, false);
		m_rpcService.purgeTasks(HttpRequestInfo.createHttpRequestInfo(), taskIds, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_PurgeTasks());
			}
			
			@Override
			public void onSuccess(Boolean success) {
				handleTaskPostRemoveAsync(taskIds);
			}
		});
	}
	
	/*
	 * Called when the user clicks the seen sun burst on a task.
	 */
	private void handleTaskSeen(final TaskListItem task) {
		final Long entryId = task.getTask().getTaskId().getEntryId();
		m_rpcService.setSeen(HttpRequestInfo.createHttpRequestInfo(), entryId, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SetSeen(),
					String.valueOf(entryId));
			}
			
			@Override
			public void onSuccess(Boolean success) {
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
			// ...subtasks, ...
			handleTaskSetStatus(task, TaskInfo.STATUS_COMPLETED);
			return;
		}
		
		// Save the new task percent done value.
		final Long entryId = task.getTask().getTaskId().getEntryId();
		m_rpcService.saveTaskCompleted(HttpRequestInfo.createHttpRequestInfo(), task.getTask().getTaskId().getBinderId(), entryId, percentDone, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveTaskCompleted(),
					String.valueOf(entryId));
			}
			
			@Override
			public void onSuccess(String completedDate) {
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
				Scheduler.ScheduledCommand statusUpdater;
				final String finalNewStatus = newStatus;
				statusUpdater = new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						handleTaskSetStatus(task, finalNewStatus);
					}
				};
				Scheduler.get().scheduleDeferred(statusUpdater);
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
		final Long entryId = task.getTask().getTaskId().getEntryId();
		m_rpcService.saveTaskPriority(HttpRequestInfo.createHttpRequestInfo(), task.getTask().getTaskId().getBinderId(), entryId, priority, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveTaskPriority(),
					String.valueOf(entryId));
			}
			
			@Override
			public void onSuccess(Boolean success) {
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
		
		// Collect the tasks this is going to affect.  If we're marking
		// a task complete or cancelled, it implies marking its entire
		// subtask hierarchy the same way too.
		TaskInfo     ti     = task.getTask();
		final TaskId taskId = ti.getTaskId();
		final List<TaskListItem> affectedTasks;
		final List<TaskId>       affectedTaskIds;
		boolean applyToSubtasks = false; //! ((TaskInfo.STATUS_COMPLETED.equals(status) || TaskInfo.STATUS_CANCELED.equals(status)) && (!(task.getSubtasks().isEmpty())));
		if (applyToSubtasks) {
			affectedTasks   = TaskListItemHelper.getTaskHierarchy(  task                );
			affectedTaskIds = TaskListItemHelper.getTaskIdsFromList(affectedTasks, false);
		}
		else {
			affectedTasks = new ArrayList<TaskListItem>();
			affectedTasks.add(task);
			
			affectedTaskIds = new ArrayList<TaskId>();
			affectedTaskIds.add(taskId);
		}

		// Save the new status on the affected tasks.
		m_rpcService.saveTaskStatus(HttpRequestInfo.createHttpRequestInfo(), affectedTaskIds, status, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveTaskStatus(),
					String.valueOf(taskId.getEntryId()));
			}
			
			@Override
			public void onSuccess(String completedDate) {
				// Find the selected status option so that we can
				// update the display.
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
		m_rpcService.getViewFolderEntryUrl(HttpRequestInfo.createHttpRequestInfo(), ti.getTaskId().getBinderId(), ti.getTaskId().getEntryId(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
					String.valueOf(ti.getTaskId().getEntryId()));
			}
			
			@Override
			public void onSuccess(String viewFolderEntryUrl) {
				m_gwtMainPage.handleAction(
					TeamingAction.SHOW_FORUM_ENTRY,
					viewFolderEntryUrl);
			}
		});
	}

	/*
	 * If the TaskTable is sorted by the specified column, add the
	 * appropriate 'sorted by' indicator. 
	 */
	private void markAsSortKey(Anchor a, Column col) {
		// Is this the column we're sorted on?
		if (m_sortColumn == col) {
			// Yes!  Add the appropriate directional arrow
			// (i.e., ^/v)...
			Image i = buildImage(m_sortAscending ? m_images.sortAZ() : m_images.sortZA());
			i.addStyleName("gwtTaskList_sortImage");
			a.getElement().appendChild(i.getElement());
			
			// ...and style to the <TD>.
			m_flexTableCF.addStyleName(0, getColumnIndex(col), "sortedcol");
		}
	}

	/*
	 * Called to write the change in linkage to the folder preferences.
	 */
	private void persistLinkageChangeAsync(final TaskListItem task, final Scheduler.ScheduledCommand postChangeCommand) {
		Scheduler.ScheduledCommand persistor;
		persistor = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				persistLinkageChangeNow(task, postChangeCommand);
			}
		};
		Scheduler.get().scheduleDeferred(persistor);
	}
	
	private void persistLinkageChangeNow(final TaskListItem task, final Scheduler.ScheduledCommand postChangeCommand) {
		// If we're not in a state were link changes can be saved...
		if (!(canPersistLinkage())) {
			// ...bail.
			return;
		}

		// Update the TaskLinkage in the TaskBundle...
		TaskLinkage newLinkage = TaskListItemHelper.buildLinkage(m_taskBundle);
		m_taskBundle.setTaskLinkage(newLinkage);
		
		// ...and write it to the current user's folder preferences.
		final Long binderId = m_taskBundle.getBinderId();
		m_rpcService.saveTaskLinkage(HttpRequestInfo.createHttpRequestInfo(), binderId, m_taskBundle.getTaskLinkage(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveTaskLinkage(),
					String.valueOf(binderId));
			}
			
			@Override
			public void onSuccess(Boolean result) {
				// If we have a post change command...
				if (null != postChangeCommand) {
					// ...schedule it.
					Scheduler.get().scheduleDeferred(postChangeCommand);
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
		m_rpcService.saveTaskSort(HttpRequestInfo.createHttpRequestInfo(), m_taskBundle.getBinderId(), m_sortColumn.getSortKey(), m_sortAscending, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveTaskSort());
			}
			
			@Override
			public void onSuccess(Boolean result) {
				// Nothing to do.
			}
		});
	}

	/*
	 * Called to completely refresh the contents of the TaskTable.
	 */
	private void refreshTaskTableAsync(final boolean preserveChecks, final boolean persistLinkage) {
		Scheduler.ScheduledCommand refresher;
		refresher = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				refreshTaskTableNow(preserveChecks, persistLinkage);
			}
		};
		Scheduler.get().scheduleDeferred(refresher);
	}
	
	private void refreshTaskTableNow(final boolean preserveChecks, final boolean persistLinkage) {
		m_rpcService.getTaskBundle(HttpRequestInfo.createHttpRequestInfo(), m_taskListing.getBinderId(), m_taskListing.getFilterType(), m_taskListing.getMode(), new AsyncCallback<TaskBundle>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetTaskList());
			}

			@Override
			public void onSuccess(TaskBundle result) {
				// Preserve the tasks that are currently checked...
				List<Long> checkedTaskIds;
				if (preserveChecks)
				     checkedTaskIds = getTaskIdsChecked();
				else checkedTaskIds = null;
				
				// ...store the new TaskBundle in the TaskListing...
				m_taskListing.setTaskBundle(result);
				
				// ...force the TaskTable to redisplay...
				m_newTaskTable = true;
				renderTaskBundle(result, checkedTaskIds);
				
				// ...and if we were requested to do so...
				if (persistLinkage) {
					// ...persist the current state of things as the
					// ...new task linkage.
					persistLinkageChangeAsync(
						null,	// null  -> No task.  We don't need one when not updating the calculated dates.
						null);	// null  -> No post change commands.
				}
			}			
		});		
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
		boolean isCancelled = ti.getStatus().equals(TaskInfo.STATUS_CANCELED);;
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
		String dueDate = task.getTask().getEvent().getLogicalEnd().getDateDisplay();
		InlineLabel il = new InlineLabel();
		il.getElement().setInnerHTML(GwtClientHelper.hasString(dueDate) ? dueDate : "&nbsp;");
		il.setWordWrap(false);
		if (task.getTask().isTaskOverdue()) {
			il.addStyleName("gwtTaskList_task-overdue-color");
		}
		m_flexTable.setWidget(row, getColumnIndex(Column.DUE_DATE), il);
	}
	
	/*
	 * Renders the 'Location' column.
	 */
	private void renderColumnLocation(final TaskListItem task, int row) {
		// Are we displaying tasks assigned to the current user?
		if (!(m_taskBundle.getIsFromFolder())) {
			// Yes!  Render the column.
			String location = task.getTask().getLocation();
			if (null == location) {
				return;
			}
			m_flexTable.setHTML(row, getColumnIndex(Column.LOCATION), location);
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

			int order = uid.getTaskOrder();
			String orderHTML;
			if ((0 == uid.getTaskDepth()) && ((-1) != order))
			     orderHTML = String.valueOf(order);
			else orderHTML = "&nbsp;";
			int colIndex = getColumnIndex(Column.ORDER);
			m_flexTable.setHTML(                 row, colIndex, orderHTML);
			m_flexTableCF.setHorizontalAlignment(row, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
			m_flexTableCF.setWidth(              row, colIndex, "16px");
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

		String entryId = String.valueOf(task.getTask().getTaskId().getEntryId());
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
			Anchor a = buildAnchor();
			Image  i = buildImage(task.getExpandSubtasks() ? m_images.task_closer() : m_images.task_opener());
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
		m_flexTable.setWidget(
			row,
			getColumnIndex(Column.STATUS),
			buildOptionColumn(
				task,
				m_statusMenu,
				status,
				"status-icon"));
	}
	
	/*
	 * Renders the 'Task Name' column.
	 */
	private void renderColumnTaskName(final TaskListItem task, int row) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		// Define a panel to contain the task name widgets.
		FlowPanel fp = new FlowPanel();

		// If this is a subtask...
		int taskDepth = uid.getTaskDepth();
		if (0 < taskDepth) {
			// ...add a spacer to indent it proportional to its depth.
			Image spacer = buildSpacer(16 * taskDepth);
			spacer.addStyleName("icon-spacer");
			fp.add(spacer);
		}

		// Add the closed/unseen marker Widget to the panel.
		Widget marker;
		TaskInfo ti = task.getTask();
		String entryId = String.valueOf(ti.getTaskId().getEntryId());
		if (ti.isTaskClosed()) {
			fp.addStyleName("gwtTaskList_task-strike");
			Image i = buildImage(m_images.completed(), m_messages.taskAltTaskClosed());
			marker = i;
		}
		else if (ti.isTaskUnseen()) {
			final Anchor a = buildAnchor();
			uid.setTaskUnseenAnchor(a);
			Image i = buildImage(m_images.unread(), m_messages.taskAltTaskUnread());
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
			fp.addStyleName("gwtTaskList_task-overdue");
		}
		marker.addStyleName("gwtTaskList_task-icon");
		fp.add(marker);
		
		// Add the appropriately styled task name Anchor to the panel.
		Anchor ta = buildAnchor();
		ta.getElement().setAttribute(ATTR_ENTRY_ID, entryId);
		EventWrapper.addHandler(ta, m_taskViewClickHandler);
		InlineLabel taskLabel = new InlineLabel(task.getTask().getTitle());
		uid.setTaskLabel(taskLabel);
		if (ti.isTaskUnseen())    taskLabel.addStyleName(             "bold"   );	// Unseen:     Bold.
		if (ti.isTaskCancelled()) m_flexTableRF.addStyleName(   row, "disabled");	// Cancelled:  Gray.
		else                      m_flexTableRF.removeStyleName(row, "disabled");
		ta.getElement().appendChild(taskLabel.getElement());
		fp.add(ta);
		m_flexTable.setWidget(row, getColumnIndex(Column.TASK_NAME), fp);
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
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_assignedTo());
		markAsSortKey(a, Column.ASSIGNED_TO);
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.ASSIGNED_TO);}			
		});
		m_flexTable.setWidget(0, getColumnIndex(Column.ASSIGNED_TO), a);
	}
	
	/*
	 * Renders the 'Closed - % Done' column header.
	 */
	private void renderHeaderClosedPercentDone() {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_closedPercentDone());
		markAsSortKey(a, Column.CLOSED_PERCENT_DONE);
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.CLOSED_PERCENT_DONE);}			
		});
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
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_dueDate());
		markAsSortKey(a, Column.DUE_DATE);
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.DUE_DATE);}			
		});
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
			Anchor a = buildAnchor("sort-column");
			a.getElement().setInnerHTML(m_messages.taskColumn_location());
			markAsSortKey(a, Column.LOCATION);
			EventWrapper.addHandler(a, new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {handleTableResort(Column.LOCATION);}			
			});
			int colIndex = getColumnIndex(Column.LOCATION);
			m_flexTable.setWidget( 0, colIndex, a);
			m_flexTableCF.setWidth(0, colIndex, "100%");
		}
	}
	
	/*
	 * Renders the 'Order' column header.
	 */
	private void renderHeaderOrder() {
		// Are we supposed to show the 'Order' column?
		if (showOrderColumn()) {
			// Yes!  Render the column header.
			Anchor a = buildAnchor("sort-column");
			a.getElement().setInnerHTML(m_messages.taskColumn_order());
			markAsSortKey(a, Column.ORDER);
			EventWrapper.addHandler(a, new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {handleTableResort(Column.ORDER);}			
			});
			int colIndex = getColumnIndex(Column.ORDER);
			m_flexTableCF.setHorizontalAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
			m_flexTable.setWidget(               0, colIndex, a);
		}
	}
	
	/*
	 * Renders the 'Priority' column header.
	 */
	private void renderHeaderPriority() {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_priority());
		markAsSortKey(a, Column.PRIORITY);
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.PRIORITY);}			
		});
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
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_status());
		markAsSortKey(a, Column.STATUS);
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.STATUS);}			
		});
		m_flexTable.setWidget(0, getColumnIndex(Column.STATUS), a);
	}
	
	/*
	 * Renders the 'Task Name' column header.
	 */
	private void renderHeaderTaskName() {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_name());
		markAsSortKey(a, Column.TASK_NAME);
		EventWrapper.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.TASK_NAME);}			
		});
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
			initializeUIData();
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
	private void renderTaskList(List<TaskListItem> tasks) {
		// Scan the tasks in the list...
		for (TaskListItem task:  tasks) {
			// ..rendering each one...
			renderTaskItem(task);
			
			// ...and their subtasks.
			if (task.getExpandSubtasks()) {
				renderTaskList(task.getSubtasks());
			}
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
		
		Scheduler.ScheduledCommand selector;
		selector = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				selectAllTasksNow(pa, select);
			}
		};
		Scheduler.get().scheduleDeferred(selector);
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
	 * @param updateCalculatedDates
	 * 
	 * @return	The time, in milliseconds, it took to show the tasks.
	 */
	public long showTasks(TaskBundle tb, boolean updateCalculatedDates) {
		// Render the tasks from the bundle, returning the time it
		// took to render them.
		m_renderTime = renderTaskBundle(tb, updateCalculatedDates);
		return m_renderTime;
	}
	
	/*
	 * Sorts the List<TaskListItem> by column in the specified order.
	 */
	private void sortByColumn(Column previousSortColumn) {
		// Are we currently in a mode that respects the task linkage?
		// (We don't if the list is filtered or showing 'Assigned To'
		// items.)
		if (m_taskBundle.respectLinkage()) {
			// Yes!  Are we sorting on other than the order column when
			// we were previously sorted on the order column?
			if ((Column.ORDER != m_sortColumn) && (Column.ORDER == previousSortColumn)) {
				// Yes!  Then we have to flatten the task list before
				// sorting.  Thanks Tracy !!!  :-)
				TaskListItemHelper.flattenTaskList(m_taskBundle);
				initializeUIData(false);	// Forces the depths, ... to be reset.
			}
			
			// No, we didn't have to flatten the list!  Are we sorting
			// on the order column when we were previously sorted on
			// other than the order column?
			else if ((Column.ORDER == m_sortColumn) && (Column.ORDER != previousSortColumn)) {
				// Yes!  Then we have to restructure the list before
				// sorting.  Thanks Tracy !!!  :-)
				TaskListItemHelper.applyTaskLinkage(m_taskBundle);
				initializeUIData();	// Forces the depths, ... to be reset.
			}
		}

		// Apply the sort base on the selected column.
		Comparator<TaskListItem> comparator;
		switch(m_sortColumn) {
		default:
		case ORDER:                comparator = new TaskSorter.OrderComparator(            m_sortAscending); break;
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
	 * Makes a GWT RPC call to the server to update the calculated
	 * dates for the binder and/or task, and any related subtasks.  If
	 * the RPC call succeeds any modified end dates will be reflected
	 * in the task table.
	 */
	private void updateCalculatedDatesNow(final ProcessActive pa, final Long binderId, final Long entryId) {
		if (null != pa) {
			pa.setMessage(m_messages.taskProcess_updatingDates());
		}
		
		m_dueDateBusy.setResource(m_images.busyAnimation());
		m_rpcService.updateCalculatedDates(HttpRequestInfo.createHttpRequestInfo(), binderId, entryId, new AsyncCallback<Map<Long, TaskDate>>() {
			@Override
			public void onFailure(Throwable t) {
				m_dueDateBusy.setResource(m_images.spacer());
				if (null == entryId)
				     GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming.getMessages().rpcFailure_UpdateCalculatedDatesBinder(), String.valueOf(binderId));
				else GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming.getMessages().rpcFailure_UpdateCalculatedDatesTask(),   String.valueOf(entryId ));

				// If we have a ProcessActive message going...
				if (null != pa) {
					// ...kill it.
					pa.killIt();
				}
			}
			
			@Override
			public void onSuccess(Map<Long, TaskDate> updatedTaskInfo) {
				// Did any tasks have the logical end dates changed?
				m_dueDateBusy.setResource(m_images.spacer());
				if ((null != updatedTaskInfo) && (!(updatedTaskInfo.isEmpty()))) {
					// Yes!  Scan them...
					Date now = new Date();
					long nowMS = now.getTime();
					for (Long entryId:  updatedTaskInfo.keySet()) {
						// ...storing their new logical end dates...
						TaskDate     dueDate = updatedTaskInfo.get(entryId);
						TaskListItem task = TaskListItemHelper.findTask(m_taskBundle, entryId);
						TaskInfo     ti = task.getTask();
						ti.getEvent().setLogicalEnd(dueDate);
						
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
		Scheduler.ScheduledCommand updater;
		updater = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				updateCalculatedDatesNow(pa, binderId, entryId);
			}
		};
		Scheduler.get().scheduleDeferred(updater);
	}
	
	/*
	 * Based on what's selected in the task list, validates the tools
	 * in the TaskListing.
	 */
	private void validateTaskToolsAsync() {
		Scheduler.ScheduledCommand validator;
		validator = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				validateTaskToolsNow();
			}
		};
		Scheduler.get().scheduleDeferred(validator);
	}
	
	private void validateTaskToolsNow() {
		boolean enableMoveDown  = false;
		boolean enableMoveLeft  = false;
		boolean enableMoveRight = false;
		boolean enableMoveUp    = false;
		String  toolHint        = null;

		// Get the checked tasks and count.
		List<TaskListItem> tasksChecked = getTasksChecked();
		int tasksCheckedCount = tasksChecked.size();
		
		// Can the user perform a delete or purge?
		boolean enableDelete = (m_taskBundle.getCanTrashEntry() && (0 < tasksCheckedCount) && (!(m_taskBundle.getBinderIsMirrored())));
		boolean enablePurge  = (m_taskBundle.getCanPurgeEntry() && (0 < tasksCheckedCount));
		
		// Does the user have the rights to manage linkage?
		boolean allowMovement = m_taskBundle.getCanModifyTaskLinkage();
		if (allowMovement) {				
			// Yes!  Validate the the base criteria about whether the
			// movement buttons are enabled.  Is one and only one task
			// selected?
			allowMovement = (1 == tasksCheckedCount);
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
							toolHint = m_messages.taskCantMove_Order();
						}
					}
					else {
						// Disallowed:  Virtual.
						toolHint = m_messages.taskCantMove_Virtual();
					}
				}
				else {
					// Disallowed:  Filters.
					toolHint = m_messages.taskCantMove_Filter();
				}
			}			
			else {
				// Disallowed:  Other than 1 item checked.
				if (0 == tasksCheckedCount)
				     toolHint = m_messages.taskCantMove_Zero();
				else toolHint = m_messages.taskCantMove_NotOne(String.valueOf(tasksCheckedCount));
			}
	
			// Is the base criteria for movement satisfied?
			if (allowMovement) {
				// Yes!  Furthermore, the allowed movement is based on the
				// selected task's current position in the linkage.
				TaskListItem task = tasksChecked.get(0);
				enableMoveDown    = TaskListItemHelper.canMoveTaskDown( m_taskBundle, task);
				enableMoveLeft    = TaskListItemHelper.canMoveTaskLeft( m_taskBundle, task);
				enableMoveRight   = TaskListItemHelper.canMoveTaskRight(m_taskBundle, task);
				enableMoveUp      = TaskListItemHelper.canMoveTaskUp(   m_taskBundle, task);
			}
		}
		else {
			// Disallowed:  Insufficient rights.
			toolHint = m_messages.taskCantMove_Rights();
		}
		
		// Hide/show the task tools hint.
		m_taskListing.showHint(toolHint);

		// Enabled/disable the buttons as calculated.
		m_taskListing.getDeleteButton().setEnabled(   enableDelete   );
		m_taskListing.getMoveDownButton().setEnabled( enableMoveDown );
		m_taskListing.getMoveLeftButton().setEnabled( enableMoveLeft );
		m_taskListing.getMoveRightButton().setEnabled(enableMoveRight);
		m_taskListing.getMoveUpButton().setEnabled(   enableMoveUp   );
		m_taskListing.getPurgeButton().setEnabled(    enablePurge    );
	}
}
