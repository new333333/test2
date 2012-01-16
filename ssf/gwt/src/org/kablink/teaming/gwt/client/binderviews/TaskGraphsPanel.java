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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TaskListReadyEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.SaveTaskGraphStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.tasklisting.TaskProvider;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class used for the content of the task graphs in the task folder
 * view.  
 * 
 * @author drfoster@novell.com
 */
public class TaskGraphsPanel extends ToolPanelBase
	implements
	// Event handlers implemented by this class.
		TaskListReadyEvent.Handler
{
	private boolean						m_notifyOnReady;			// true -> Notify the container when this panel is ready.  false -> Don't.
	private boolean						m_expandGraphs;				// true -> This graphs are initially expanded.  false -> They're initially collapsed.
	private FlexTable					m_graphGrid;				// The FlexTable that holds the graphs.
	private FlexCellFormatter			m_graphCellFormatter;		// A FlexCellFormatter for manipulating cells within the FlexTable.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	@SuppressWarnings("unused")
	private List<TaskListItem>			m_taskList;					// The current task list the graphs are displaying.
	private TaskProvider				m_taskProvider;				// The interface that provides the task list.
	private VibeFlowPanel				m_fp;						// The panel holding the content.
	private VibeFlowPanel				m_priorityPanel;			// The panel holding the priority graph.
	private VibeFlowPanel				m_refreshPanel;				// The panel holding the refresh push button.
	private VibeFlowPanel				m_statusPanel;				// The panel holding the status graph.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.TASK_LIST_READY,
	};
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TaskGraphsPanel(RequiresResize containerResizer, TaskProvider taskProvider, boolean expandGraphs, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_taskProvider = taskProvider;
		m_expandGraphs = expandGraphs;
		
		// ...initialize any other data members...
		m_notifyOnReady = true;
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-taskGraphsPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the TaskGraphsPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param taskProvider
	 * @param expandGraphs
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final TaskProvider taskProvider, final boolean expandGraphs, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(TaskGraphsPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				TaskGraphsPanel bcp = new TaskGraphsPanel(containerResizer, taskProvider, expandGraphs, binderInfo, toolPanelReady);
				tpClient.onSuccess(bcp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_TaskGraphsPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Asynchronously construct's the contents of the task graphs
	 * panel.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the panel.
	 */
	private void loadPart1Now() {
		// Nothing to do.  We can't actually do anything until we have
		// a task list available.
	}

	/**
	 * Called when the task folder view is attached.
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
	 * Called when the task folder view is detached.
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
	 * Handles TaskListReadyEvent's received by this class.
	 * 
	 * Implements the TaskListReadyEvent.Handler.onTaskListReady() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskListReady(TaskListReadyEvent event) {
		// Simply render the task graphs.
		renderTaskGraphsAsync();
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
	 * Renders the cell that contains the expand/collapse widgets.
	 * 
	 * Row:   0
	 * Cell:  0
	 */
	private void renderExpanderCell() {
		// Create a panel to hold the graph expander.
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-taskGraphsExpander");
		m_graphCellFormatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
		m_graphGrid.setWidget(0, 0, fp);

		// Create an Anchor for the expander...
		final Anchor a = new Anchor();
		a.addStyleName("vibe-taskGraphsExpanderA");
		a.setTitle(m_expandGraphs ? m_messages.taskGraphsAltHide() : m_messages.taskGraphsAltShow());
		fp.add(a);

		// ...add a label to it...
		InlineLabel il = new InlineLabel(m_messages.taskGraphs());
		il.addStyleName("vibe-taskGraphsExpanderLabel");
		Element aE = a.getElement();
		aE.appendChild(il.getElement());

		// ...add an expand/collapse image to it...
		final Image img = new Image(m_expandGraphs ? m_images.collapser() : m_images.expander());
		img.addStyleName("vibe-taskGraphsExpanderImg");
		img.getElement().setAttribute("align", "absmiddle");
		aE.appendChild(img.getElement());

		// ...and add a handler for when the user clicks it.
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// If the graphs are visible...
				if (m_expandGraphs) {
					// ...hide them...
					m_priorityPanel.addStyleName("vibe-taskGraphsHidden");
					m_statusPanel.addStyleName(  "vibe-taskGraphsHidden");
					m_refreshPanel.addStyleName( "vibe-taskGraphsHidden");
				}
				else {
					// ...otherwise, show them.
					m_priorityPanel.removeStyleName("vibe-taskGraphsHidden");
					m_statusPanel.removeStyleName(  "vibe-taskGraphsHidden");
					m_refreshPanel.removeStyleName( "vibe-taskGraphsHidden");
				}
				
				// Toggle the state of the expander...
				m_expandGraphs = (!m_expandGraphs);
				img.setResource(m_expandGraphs ? m_images.collapser() : m_images.expander());
				a.setTitle(m_expandGraphs ? m_messages.taskGraphsAltHide() : m_messages.taskGraphsAltShow());
				panelResized();

				// ...and save the users setting for the expansion.
				SaveTaskGraphStateCmd cmd = new SaveTaskGraphStateCmd(m_binderInfo.getBinderIdAsLong(), m_expandGraphs);
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_SaveTaskGraphState());
					}

					@Override
					public void onSuccess(VibeRpcResponse result) {
						// Nothing to do.
					}
				});
			}
		});
		a.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				a.removeStyleName("vibe-taskGraphsExpanderA-hover");
			}
		});
		a.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				a.addStyleName("vibe-taskGraphsExpanderA-hover");
			}
		});
	}
	
	/*
	 * Renders the cell that contains the priority graph.
	 * 
	 * Row:   0
	 * Cell:  1
	 */
	private void renderPriorityCell() {
		m_priorityPanel = new VibeFlowPanel();
		m_priorityPanel.addStyleName("vibe-taskGraphsPriorities");
		if (!m_expandGraphs) {
			m_priorityPanel.addStyleName("vibe-taskGraphsHidden");
		}
		m_graphGrid.setWidget(0, 1, m_priorityPanel);
		
//!		...this needs to be implemented...
		m_priorityPanel.add(new InlineLabel("...priorities graph..."));
	}
	
	/*
	 * Renders the cell that contains the priority graph.
	 * 
	 * Row:   1
	 * Cell:  0, Span:  3
	 */
	private void renderRefreshRow() {
		// Create a panel to hold the refresh row...
		m_refreshPanel = new VibeFlowPanel();
		m_refreshPanel.addStyleName("vibe-taskGraphsRefresh");
		if (!m_expandGraphs) {
			m_refreshPanel.addStyleName("vibe-taskGraphsHidden");
		}
		m_graphCellFormatter.setColSpan(1, 0, 3);
		m_graphCellFormatter.setAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
		m_graphGrid.setWidget(1, 0, m_refreshPanel);

		// ...and add a refresh button to it.
		Button button = new Button(m_messages.taskGraphsRefresh());
		button.addStyleName("vibe-taskGraphsRefreshButton");
		m_refreshPanel.add(button);
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Simply render the task graphs again.  This will pull
				// the current task list and rebuild the graphs from
				// that.
				renderTaskGraphsAsync();
			}
		});
	}
	
	/*
	 * Renders the cell that contains the status graph.
	 * 
	 * Row:   0
	 * Cell:  2
	 */
	private void renderStatusCell() {
		m_statusPanel = new VibeFlowPanel();
		m_statusPanel.addStyleName("vibe-taskGraphsStatus");
		if (!m_expandGraphs) {
			m_statusPanel.addStyleName("vibe-taskGraphsHidden");
		}
		m_graphGrid.setWidget(0, 2, m_statusPanel);
		
//!		...this needs to be implemented...		
		m_statusPanel.add(new InlineLabel("...status graph..."));
	}
	
	/*
	 * Asynchronously renders the tasks from the task list.
	 */
	private void renderTaskGraphsAsync() {
		ScheduledCommand doRender = new ScheduledCommand() {
			@Override
			public void execute() {
				renderTaskGraphsNow();
			}
		};
		Scheduler.get().scheduleDeferred(doRender);
	}
	
	/*
	 * Synchronously renders the tasks from the task list.
	 */
	private void renderTaskGraphsNow() {
		// Pull the current task list from the task provider...
		m_taskList = m_taskProvider.getTasks();

		// ...render the task graphs...
		m_fp.clear();
		m_graphGrid = new FlexTable();
		m_graphCellFormatter = m_graphGrid.getFlexCellFormatter();
		m_graphGrid.addStyleName("vibe-taskGraphsGrid");
		m_fp.add(m_graphGrid);
		renderExpanderCell();
		renderPriorityCell();
		renderStatusCell();
		renderRefreshRow();

		// ...and if we need to...
		if (m_notifyOnReady) {
			// ...tell our container that we're ready.
			toolPanelReady();
			m_notifyOnReady = false;
		}
		
		else {
			panelResized();
		}
	}

	/**
	 * Called from the binder view to allow the panel to do any work
	 * required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Simply render the task graphs (again, if they've already
		// been rendered.)
		m_notifyOnReady = true;
		renderTaskGraphsAsync();
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
