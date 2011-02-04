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
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskLinkageHelper;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.widgets.PassThroughEventsPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
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
	private boolean				m_sortAscending;	//
	private Column				m_sortColumn;		//
	private FlexCellFormatter	m_flexTableCF;		//
	private FlexTable			m_flexTable;		//
	private int					m_taskCount;		//
	private RowFormatter		m_flexTableRF;		//
	private TaskBundle			m_taskBundle;		//
	private TaskListing			m_taskListing;		//
	private TaskPopupMenu		m_percentDoneMenu;	//
	private TaskPopupMenu		m_priorityMenu;		//
	private TaskPopupMenu		m_statusMenu;		//
	
	private       boolean							m_newTaskTable = true;										//
	private final GwtMainPage						m_gwtMainPage  = GwtTeaming.getMainPage();					// 
	private final GwtRpcServiceAsync				m_rpcService   = GwtTeaming.getRpcService();				// 
	private final GwtTeamingMessages				m_messages     = GwtTeaming.getMessages();					//
	private final GwtTeamingTaskListingImageBundle	m_images       = GwtTeaming.getTaskListingImageBundle();	//
	
	// The following defines the number of entries we show before
	// adding an ellipse to show more.
	private final int MEMBERSHIP_ELLIPSE_COUNT	= 10;

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
		NAME(               "_sortTitle"),
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
			Column reply = Column.ORDER;
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
			boolean reply = ((null != cb) && jsIsCBChecked(cb.getElement().getId()));
			return reply;
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
			PassThroughEventsPanel.addHandler(pc, new ClickHandler() {
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
			eventHandlers.add(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					assignee.addStyleName("gwtTaskList_assigneeHover");
				}
			});
			eventHandlers.add(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					assignee.removeStyleName("gwtTaskList_assigneeHover");
				}
			});
			eventHandlers.add(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					assignee.removeStyleName("gwtTaskList_assigneeHover");
					handlePresenceSelect(ai, assignee.getElement());
				}
			});
			PassThroughEventsPanel.addHandlers(assignee, eventHandlers);
			
			break;
		
		case GROUP:
		case TEAM:
			// Group or team assignee!
			Image assigneeImg = new Image();
			assigneeImg.setUrl(m_gwtMainPage.getRequestInfo().getImagesPath() + ai.getPresenceDude());
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
				eventHandlers.add(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						assignee.addStyleName("gwtTaskList_assigneeHover");
					}
				});
				eventHandlers.add(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						assignee.removeStyleName("gwtTaskList_assigneeHover");
					}
				});
				eventHandlers.add(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						assignee.removeStyleName("gwtTaskList_assigneeHover");
						handleMembershipSelect(assigneeType, ai, expansionFP, showDisabled);
					}
				});
				PassThroughEventsPanel.addHandlers(assignee, eventHandlers);
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
		PassThroughEventsPanel.addHandler(a, new ClickHandler() {
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
			PassThroughEventsPanel.addHandler(a, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					taskMenu.showTaskPopupMenu(task, imgElement);
				}
			});
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
	 * Returns a spacer Image.
	 */
	private Image buildSpacer(int width) {
		Image reply = new Image(m_images.spacer());
		reply.setHeight(                     "16px");
		reply.setWidth(String.valueOf(width) + "px");
		return reply;
	}
	
	private Image buildSpacer() {
		return buildSpacer(16);
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

	/**
	 * Returns in the index to display a column at.
	 * 
	 * @return
	 */
	private int getColumnIndex(Column col) {
		int reply = col.ordinal();
		if ((Column.ORDER.ordinal() < col.ordinal()) && (!(showOrderColumn()))) {
			reply -= 1;
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
		return getUIData(task).m_taskOrder;
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
		if ((Column.LOCATION == m_sortColumn) && m_taskBundle.getIsFromFolder()) {
			m_sortColumn = Column.ORDER;
		}

		// ...accounting for cases where we don't show the order
		// ...column...
		if ((!(showOrderColumn())) && (Column.ORDER == m_sortColumn)) {
			m_sortColumn = Column.NAME;
		}

		// ...and apply it, as necessary.
		if ((Column.ORDER != m_sortColumn) || (!(m_sortAscending))) {
			sortByColumn(m_taskBundle.getTasks(), m_sortColumn, m_sortAscending);
		}
	}
	
	/*
	 * (Re)initializes the UIData objects on the tasks.
	 */
	private void initializeUIData() {
		initializeUIDataImpl(m_taskBundle.getTasks(), 0);
	}
	
	private void initializeUIDataImpl(List<TaskListItem> tasks, int taskDepth) {
		int     taskOrder    = 1;
		boolean baseTask     = (0 == taskDepth);
		int     subtaskDepth = (taskDepth + 1);
		for (TaskListItem task:  tasks) {
			// Build a UIData object for this TaskListItem...
			UIData newUID = new UIData(((UIData) task.getUIData()));
			task.setUIData(newUID);
			newUID.setTaskDepth(taskDepth);
			if (baseTask) {
				newUID.setTaskOrder(taskOrder);
				taskOrder += 1;
			}
			else {
				newUID.setTaskOrder(-1);
			}

			// ...and build the UIData's for any subtasks.
			initializeUIDataImpl(task.getSubtasks(), subtaskDepth);
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
		// Perform the selection and validate the TaskListing tools.
		selectAllTasks(checked);
		validateTaskTools();
	}

	/*
	 * Called to resort the TaskTable by the specified column.
	 */
	private void handleTableResort(Column col) {
		// Apply the resort...
		if (col == m_sortColumn)
			 m_sortAscending = (!m_sortAscending);
		else m_sortColumn    = col;		
		sortByColumn(m_taskBundle.getTasks(), m_sortColumn, m_sortAscending);
		
		// ...and redisplay the tasks.
		showTasks(m_taskBundle);
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
		List<TaskId> taskIds = TaskLinkageHelper.getTaskIdsFromList(tasksChecked, false);
		m_rpcService.deleteTasks(HttpRequestInfo.createHttpRequestInfo(), taskIds, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_DeleteTasks());
			}
			
			@Override
			public void onSuccess(Boolean success) {
				// Simply refresh the TaskTable and we'll reread the
				// tasks and display them in the appropriate hierarchy. 
				refreshTaskTable(false, true);
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
					showTasks(m_taskBundle);
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
					showTasks(m_taskBundle);
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
			TaskLinkageHelper.moveTaskDown(m_taskBundle, task);
			handleTaskPostMove(task);
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
			TaskLinkageHelper.moveTaskLeft(m_taskBundle, task);
			handleTaskPostMove(task);
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
			TaskLinkageHelper.moveTaskRight(m_taskBundle, task);
			handleTaskPostMove(task);
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
			TaskLinkageHelper.moveTaskUp(m_taskBundle, task);
			handleTaskPostMove(task);
		}
	}

	/*
	 * Does what's necessary after a task is moved to put the change
	 * into affect.
	 */
	private void handleTaskPostMove(TaskListItem task) {	
		initializeUIData();	// Forces the order and depths to be reset.
		showTasks(m_taskBundle);
		persistLinkageChange(task, true);
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
		List<TaskId> taskIds = TaskLinkageHelper.getTaskIdsFromList(tasksChecked, false);
		m_rpcService.purgeTasks(HttpRequestInfo.createHttpRequestInfo(), taskIds, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_PurgeTasks());
			}
			
			@Override
			public void onSuccess(Boolean success) {
				// Simply refresh the TaskTable and we'll reread the
				// tasks and display them in the appropriate hierarchy. 
				refreshTaskTable(false, true);
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
	private void handleTaskSelect(TaskListItem task) {
		// Track the state of the task's checkbox...
		UIData uid = getUIData(task);
		boolean checked = uid.isTaskCBChecked();
		uid.setTaskSelected(checked);

		// ...and/remove the selected style from the affected rows...
		int row = uid.getTaskRow();
		if (checked)
		     m_flexTableRF.addStyleName(   row, "selected");
		else m_flexTableRF.removeStyleName(row, "selected");
		
		// ...and validate the TaskListing tools.
		validateTaskTools();
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
		if (percentDone.equals("c100")) {
			// ...simply change its status to complete.  That change
			// ...will take care of any mucking that has to occur with
			// ...subtasks, ...
			handleTaskSetStatus(task, "s3");
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
				handleTaskSetPercentDoneImpl(task, percentDone);
			}
		});
	}
	
	private void handleTaskSetPercentDoneImpl(TaskListItem task, String percentDone) {
		// Store the new percent done value in the task.
		TaskInfo ti = task.getTask();
		ti.setCompleted(percentDone);
		if (!("c100".equals(percentDone))) {
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
				// Update the Image and text displayed on the priority.
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
		if (("s3".equals(status)) || ("s4".equals(status))) {
			affectedTasks   = TaskLinkageHelper.getTaskHierarchy(  task                );
			affectedTaskIds = TaskLinkageHelper.getTaskIdsFromList(affectedTasks, false);
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
			ti.setCompleted(    "c100"     );
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
				completedLabel.setVisible(   false       );
				percentDoneWidget.setVisible(true        );
				handleTaskSetPercentDoneImpl(task, "c090");
			}
		}

		// Finally, make sure the row is showing with the correct
		// styles, ...  We can do that by simply re-rendering the
		// 'Task Name', 'Due Date' and 'Assigned To' columns.
		int row = uid.getTaskRow();
		renderColumnTaskName(  task, row, getColumnIndex(Column.NAME       ));
		renderColumnDueDate(   task, row, getColumnIndex(Column.DUE_DATE   ));
		renderColumnAssignedTo(task, row, getColumnIndex(Column.ASSIGNED_TO));
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
		// Is this the column we're sorted on?
		if (m_sortColumn == col) {
			// Yes!  Add the appropriate directional arrow
			// (i.e., ^/v)...
			Image i = buildImage(m_sortAscending ? m_images.sortAZ() : m_images.sortZA());
			a.getElement().appendChild(i.getElement());
			
			// ...and style to the <TD>.
			m_flexTableCF.addStyleName(0, getColumnIndex(col), "sortedcol");
		}
	}

	/*
	 * Called to write the change in linkage to the folder preferences.
	 */
	private void persistLinkageChange(final TaskListItem task, final boolean updateCalculatedDates) {
		// If we're not in a state were link changes can be saved...
		if (!(canPersistLinkage())) {
			// ...bail.
			return;
		}

		// Update the TaskLinkage in the TaskBundle...
		TaskLinkage newLinkage = TaskLinkageHelper.buildLinkage(m_taskBundle);
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
				// If we were requested to do so, update the calculated
				// dates, based on the given task having changed.
				if (updateCalculatedDates) {
					Scheduler.ScheduledCommand updater;
					updater = new Scheduler.ScheduledCommand() {
						@Override
						public void execute() {
							// Note that we run this delayed so that we
							// don't invoke one RPC method while
							// processing the results of another.
							updateCalculatedDates(task);
						}
					};
					Scheduler.get().scheduleDeferred(updater);
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
	private void refreshTaskTable(final boolean preserveChecks, final boolean persistLinkage) {
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
				showTasks(result, checkedTaskIds);
				
				// ...and if we were requested to do so...
				if (persistLinkage) {
					// ...persist the current state of things as the
					// ...new task linkage.
					persistLinkageChange(
						null,	// null  -> No task.  We don't need one when not updating the calculated dates.
						false);	// false -> Don't update calculated dates. 
				}
			}			
		});		
	}
	
	/*
	 * Removes all the child Node's from a Widget.
	 */
	private void removeAllChildren(Widget w) {
		Element wE = w.getElement();
		Node child = wE.getFirstChild();
		while (null != child) {
			Node nextChild = child.getNextSibling();
			wE.removeChild(child);
			child = nextChild;
		}
	}
	
	/*
	 * Renders a column of a row based on a task into the TaskTable.
	 */
	private void renderColumn(TaskListItem task, int row, Column col) {
		int colIndex = getColumnIndex(col);
		switch(col) {
		case CLOSED_PERCENT_DONE:  renderColumnClosedPercentDone(task, row, colIndex); break;
		case ASSIGNED_TO:          renderColumnAssignedTo(       task, row, colIndex); break;		
		case DUE_DATE:             renderColumnDueDate(          task, row, colIndex); break;		
		case NAME:                 renderColumnTaskName(         task, row, colIndex); break;
		case ORDER:                renderColumnOrder(            task, row, colIndex); break;
		case PRIORITY:             renderColumnPriority(         task, row, colIndex); break;		
		case SELECTOR:             renderColumnSelectCB(         task, row, colIndex); break;
		case STATUS:               renderColumnStatus(           task, row, colIndex); break;
		case LOCATION:             renderColumnLocation(         task, row, colIndex); break;
		}
	}
	
	/*
	 * Renders the 'Assigned To' column.
	 */
	private void renderColumnAssignedTo(final TaskListItem task, int row, int colIndex) {
		VerticalPanel vp = new VerticalPanel();
		vp.addStyleName("gwtTaskList_assigneesList");

		// Scan the individual assignees...
		TaskInfo ti = task.getTask();
		boolean isCancelled = ti.getStatus().equals("s4");;
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
			m_flexTable.setWidget(row, colIndex, vp);
		}
	}

	/*
	 * Renders the 'Closed - % Done' column.
	 */
	private void renderColumnClosedPercentDone(final TaskListItem task, int row, int colIndex) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		InlineLabel completedLabel;
		Widget percentDoneWidget;
		
		// What's the current priority of this task?
		TaskInfo ti = task.getTask();
		String percentDone = ti.getCompleted();
		if (!(GwtClientHelper.hasString(percentDone))) {
			percentDone = "c000";
		}
		boolean complete = ("c100".equals(percentDone));

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
		if (complete)
		     percentDoneWidget.setVisible(false);
		else completedLabel.setVisible(     false);

		// Finally, construct a FlowPanel containing both and add that
		// to the TaskTable.
		FlowPanel fp = new FlowPanel();
		fp.add(completedLabel);
		fp.add(percentDoneWidget);
		m_flexTable.setWidget(row, colIndex, fp);
		
	}
	
	/*
	 * Renders the 'Due Date' column.
	 */
	private void renderColumnDueDate(final TaskListItem task, int row, int colIndex) {
		InlineLabel il = new InlineLabel(task.getTask().getEvent().getLogicalEnd().getDateDisplay());
		il.setWordWrap(false);
		if (task.getTask().isTaskOverdue()) {
			il.addStyleName("gwtTaskList_task-overdue-color");
		}
		m_flexTable.setWidget(row, colIndex, il);
	}
	
	/*
	 * Renders the 'Location' column.
	 */
	private void renderColumnLocation(final TaskListItem task, int row, int colIndex) {
		// Are we displaying tasks assigned to the current user?
		if (!(m_taskBundle.getIsFromFolder())) {
			// Yes!  Render the column.
			String location = task.getTask().getLocation();
			if (null == location) {
				return;
			}
			m_flexTable.setHTML(row, colIndex, location);
		}
	}

	/*
	 * Renders the 'Order' column.
	 */
	private void renderColumnOrder(final TaskListItem task, int row, int colIndex) {
		// Are we supposed to show the 'Order' column?
		if (showOrderColumn()) {
			// Yes!  Render the column.  Extract the UIData from this
			// task.
			UIData uid = getUIData(task);
			
			String orderHTML = ((0 == uid.getTaskDepth()) ? String.valueOf(uid.getTaskOrder()) : "");
			m_flexTable.setHTML(row, colIndex, orderHTML);
			m_flexTableCF.setHorizontalAlignment(row, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
			m_flexTableCF.setWidth(row, colIndex, "16px");
		}
	}

	/*
	 * Renders the 'Priority' column.
	 */
	private void renderColumnPriority(final TaskListItem task, int row, int colIndex) {
		// What's the current priority of this task?
		String priority = task.getTask().getPriority();
		if (!(GwtClientHelper.hasString(priority))) {
			priority = "p1";
		}
		
		// Add an Anchor for it to the TaskTable.
		m_flexTable.setWidget(
			row,
			colIndex,
			buildOptionColumn(
				task,
				m_priorityMenu,
				priority,
				"priority-icon"));
	}
	
	/*
	 * Renders the 'Select CheckBox' column.
	 */
	private void renderColumnSelectCB(final TaskListItem task, int row, int colIndex) {
		// Extract the UIData from this task.
		UIData uid = getUIData(task);
		
		CheckBox cb = new CheckBox();
		uid.setTaskSelectorCB(cb);
		cb.getElement().setId("gwtTaskList_taskSelect_" + task.getTask().getTaskId().getEntryId());
		cb.addStyleName("gwtTaskList_ckbox");
		PassThroughEventsPanel.addHandler(cb, new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {handleTaskSelect(task);}			
		});
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
		m_flexTableCF.setWordWrap( row, colIndex, false);
		m_flexTableCF.setAlignment(row, colIndex, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(     row, colIndex, fp);
	}

	/*
	 * Renders the 'Status' column.
	 */
	private void renderColumnStatus(final TaskListItem task, int row, int colIndex) {
		// What's the current priority of this task?
		String status = task.getTask().getStatus();
		if (!(GwtClientHelper.hasString(status))) {
			status = "s1";
		}
		
		// Add an Anchor for it to the TaskTable.
		m_flexTable.setWidget(
			row,
			colIndex,
			buildOptionColumn(
				task,
				m_statusMenu,
				status,
				"status-icon"));
	}
	
	/*
	 * Renders the 'Task Name' column.
	 */
	private void renderColumnTaskName(final TaskListItem task, int row, int colIndex) {
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
		if (ti.isTaskClosed()) {
			fp.addStyleName("gwtTaskList_task-strike");
			Image i = buildImage(m_images.completed(), m_messages.taskAltTaskClosed());
			marker = i;
		}
		else if (ti.isTaskUnseen()) {
			final Anchor a = buildAnchor();
			uid.setTaskUnseenAnchor(a);
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
		if (ti.isTaskOverdue()) {
			fp.addStyleName("gwtTaskList_task-overdue");
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
		InlineLabel taskLabel = new InlineLabel(task.getTask().getTitle());
		uid.setTaskLabel(taskLabel);
		if (ti.isTaskUnseen())    taskLabel.addStyleName(             "bold"   );	// Unseen:     Bold.
		if (ti.isTaskCancelled()) m_flexTableRF.addStyleName(   row, "disabled");	// Cancelled:  Gray.
		else                      m_flexTableRF.removeStyleName(row, "disabled");
		ta.getElement().appendChild(taskLabel.getElement());
		fp.add(ta);
		m_flexTable.setWidget(row, colIndex, fp);
	}

	/*
	 * Renders a header column in the TaskTable.
	 */
	private void renderHeader(Column col) {
		int colIndex = getColumnIndex(col);
		switch(col) {
		case CLOSED_PERCENT_DONE:  renderHeaderClosedPercentDone(colIndex); break;
		case ASSIGNED_TO:          renderHeaderAssignedTo(       colIndex); break;		
		case DUE_DATE:             renderHeaderDueDate(          colIndex); break;		
		case NAME:                 renderHeaderTaskName(         colIndex); break;
		case ORDER:                renderHeaderOrder(            colIndex); break;
		case PRIORITY:             renderHeaderPriority(         colIndex); break;		
		case SELECTOR:             renderHeaderSelectCB(         colIndex); break;
		case STATUS:               renderHeaderStatus(           colIndex); break;		
		case LOCATION:             renderHeaderLocation(         colIndex); break;		
		}
	}
	
	/*
	 * Renders the 'Assigned To' column header.
	 */
	private void renderHeaderAssignedTo(int colIndex) {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_assignedTo());
		markAsSortKey(a, Column.ASSIGNED_TO);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.ASSIGNED_TO);}			
		});
		m_flexTable.setWidget(0, colIndex, a);
	}
	
	/*
	 * Renders the 'Closed - % Done' column header.
	 */
	private void renderHeaderClosedPercentDone(int colIndex) {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_closedPercentDone());
		markAsSortKey(a, Column.CLOSED_PERCENT_DONE);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.CLOSED_PERCENT_DONE);}			
		});
		m_flexTable.setWidget(0, colIndex, a);
	}
	
	/*
	 * Renders the 'Due Date' column header.
	 */
	private void renderHeaderDueDate(int colIndex) {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_dueDate());
		markAsSortKey(a, Column.DUE_DATE);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.DUE_DATE);}			
		});
		m_flexTable.setWidget(0, colIndex, a);
	}

	/*
	 * Renders the 'Location' column header.
	 */
	private void renderHeaderLocation(int colIndex) {
		// Are we displaying tasks assigned to the current user?
		if (!(m_taskBundle.getIsFromFolder())) {
			// Yes!  Render the column header.
			Anchor a = buildAnchor("sort-column");
			a.getElement().setInnerHTML(m_messages.taskColumn_location());
			markAsSortKey(a, Column.LOCATION);
			PassThroughEventsPanel.addHandler(a, new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {handleTableResort(Column.LOCATION);}			
			});
			m_flexTable.setWidget(0, colIndex, a);
		}
	}
	
	/*
	 * Renders the 'Order' column header.
	 */
	private void renderHeaderOrder(int colIndex) {
		// Are we supposed to show the 'Order' column?
		if (showOrderColumn()) {
			// Yes!  Render the column header.
			Anchor a = buildAnchor("sort-column");
			a.getElement().setInnerHTML("#");
			markAsSortKey(a, Column.ORDER);
			PassThroughEventsPanel.addHandler(a, new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {handleTableResort(Column.ORDER);}			
			});
			m_flexTableCF.setHorizontalAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_CENTER);
			m_flexTable.setWidget(0, colIndex, a);
		}
	}
	
	/*
	 * Renders the 'Priority' column header.
	 */
	private void renderHeaderPriority(int colIndex) {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_priority());
		markAsSortKey(a, Column.PRIORITY);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.PRIORITY);}			
		});
		m_flexTable.setWidget(0, colIndex, a);
	}
	
	/*
	 * Renders the 'Selector' column header.
	 */
	private void renderHeaderSelectCB(int colIndex) {
		final CheckBox cb = new CheckBox();
		cb.addStyleName("gwtTaskList_ckbox");
		cb.getElement().setId("gwtTaskList_taskSelect_All");
		PassThroughEventsPanel.addHandler(cb, new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {handleSelectAll(jsIsCBChecked("gwtTaskList_taskSelect_All"));}			
		});
		m_flexTableCF.setAlignment(0, colIndex, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_flexTable.setWidget(0, colIndex, cb);
	}
	
	/*
	 * Renders the 'Status' column header.
	 */
	private void renderHeaderStatus(int colIndex) {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_status());
		markAsSortKey(a, Column.STATUS);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.STATUS);}			
		});
		m_flexTable.setWidget(0, colIndex, a);
	}
	
	/*
	 * Renders the 'Task Name' column header.
	 */
	private void renderHeaderTaskName(int colIndex) {
		Anchor a = buildAnchor("sort-column");
		a.getElement().setInnerHTML(m_messages.taskColumn_name());
		markAsSortKey(a, Column.NAME);
		PassThroughEventsPanel.addHandler(a, new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {handleTableResort(Column.NAME);}			
		});
		m_flexTable.setWidget(0, colIndex, a);
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
	 * Checks or removes the check from all the tasks in the TaskTable.
	 */
	private void selectAllTasks(boolean select) {
		selectAllTasksImpl(m_taskBundle.getTasks(), select);
	}
	
	private void selectAllTasksImpl(List<TaskListItem> tasks, boolean selected) {
		for (TaskListItem task:  tasks) {
			UIData uid = getUIData(task);
			uid.getTaskSelectorCB().setValue(selected);
			uid.setTaskSelected(selected);
			
			int row = uid.getTaskRow();
			if (selected)
		         m_flexTableRF.addStyleName(   row, "selected");
		    else m_flexTableRF.removeStyleName(row, "selected");

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
	 * Shows the tasks in the List<TaskListItem>.
	 * 
	 * Returns the time, in milliseconds, that it took to show them.
	 * 
	 * @param taskBundle
	 * @param checkedTaskIds
	 * 
	 * @return
	 */
	public long showTasks(TaskBundle taskBundle, List<Long> checkedTaskIds) {
		// Save when we start...
		long start = System.currentTimeMillis();

		// ...decide how the table should be sorted...
		m_taskBundle = taskBundle;
		if (m_newTaskTable) {
			m_newTaskTable = false;
			initializeUIData();
			initializeSorting();
		}

		// ...apply any tasks checks that are being preserved...
		if (null != checkedTaskIds) {
			for (Long entryId:  checkedTaskIds) {
				TaskListItem task = TaskLinkageHelper.findTask(m_taskBundle, entryId);
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
			il.addStyleName("wiki-noentries-panel");
			m_flexTable.setWidget(row, 0, il);
		}
		else {
			// Yes, there any tasks to show!
			showTasksImpl(tasks);
		}

		// Validate the task tools for what we've got displayed.
		Scheduler.ScheduledCommand validator;
		validator = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				validateTaskTools();
			}
		};
		Scheduler.get().scheduleDeferred(validator);
		
		// Finally, return how long we took to show the tasks.
		long end = System.currentTimeMillis();
		return (end - start);
	}
	
	public void showTasks(TaskBundle tb) {
		// Always use the initial form of the method.
		showTasks(tb, null);	// null -> No checked tasks to preserve.
	}

	/*
	 * Shows the tasks in the List<TaskListItem> as being at a specific
	 * depth in the listing.
	 */
	private void showTasksImpl(List<TaskListItem> tasks) {
		// Scan the tasks in the list...
		for (TaskListItem task:  tasks) {
			// ..rendering each one...
			renderTaskItem(task);
			
			// ...and their subtasks.
			if (task.getExpandSubtasks()) {
				showTasksImpl(task.getSubtasks());
			}
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
		case LOCATION:             comparator = new TaskSorter.LocationComparator(         sortAscending); break;		
		}
		TaskSorter.sort(tasks, comparator);
	}

	/*
	 * Makes a GWT RPC call to the server to update the calculated
	 * dates for the task, and any related tasks.  If the RPC call
	 * succeeds and returns true, the TaskTable will be completely
	 * refreshed.
	 */
	private void updateCalculatedDates(TaskListItem task) {
		final TaskId taskId  = task.getTask().getTaskId();
		final Long   entryId = taskId.getEntryId();
		m_rpcService.updateCalculatedDates(HttpRequestInfo.createHttpRequestInfo(), taskId.getBinderId(), entryId, new AsyncCallback<Map<Long, TaskDate>>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_UpdateCalculatedDates(),
					String.valueOf(entryId));
			}
			
			@Override
			public void onSuccess(Map<Long, TaskDate> updatedTaskInfo) {
				// Did any tasks have the logical end dates changed?
				if ((null != updatedTaskInfo) && (!(updatedTaskInfo.isEmpty()))) {
					// Yes!  Scan them...
					for (Long entryId:  updatedTaskInfo.keySet()) {
						// ...storing their new logical end dates...
						TaskListItem task = TaskLinkageHelper.findTask(m_taskBundle, entryId);
						task.getTask().getEvent().setLogicalEnd(updatedTaskInfo.get(entryId));
					}
					
					// ...and redisplay the tasks in the TaskTable,
					showTasks(m_taskBundle);
				}
			}
		});
	}
	
	/*
	 * Based on what's selected in the task list, validates the tools
	 * in the TaskListing.
	 */
	private void validateTaskTools() {
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
				enableMoveDown    = TaskLinkageHelper.canMoveTaskDown( m_taskBundle, task);
				enableMoveLeft    = TaskLinkageHelper.canMoveTaskLeft( m_taskBundle, task);
				enableMoveRight   = TaskLinkageHelper.canMoveTaskRight(m_taskBundle, task);
				enableMoveUp      = TaskLinkageHelper.canMoveTaskUp(   m_taskBundle, task);
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
