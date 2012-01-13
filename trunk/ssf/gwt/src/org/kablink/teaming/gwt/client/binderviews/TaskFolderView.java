/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing.TaskListingClient;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Task folder view.
 * 
 * @author drfoster@novell.com
 */
public class TaskFolderView extends FolderViewBase {
	private TaskDisplayDataRpcResponseData	m_taskDisplayData;	// The task display data read from the server.
	private TaskListing						m_taskListing;		// The TaskList composite.
	private VibeFlowPanel					m_gwtTaskFilter;	// The <DIV> that will hold the task filter widget(s). 

	// The following define the indexes into a VibeVerticalPanel of the
	// addition panel that makes up a task folder view.
	private final static int TASK_GRAPHS_PANEL_INDEX	= 3;

	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public TaskFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the super class.
		super(folderInfo, viewReady, "vibe-taskFolder", false);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public TaskDisplayDataRpcResponseData getTaskDisplayData() {return m_taskDisplayData;}
	public VibeFlowPanel                  getGwtTaskFilter()   {return m_gwtTaskFilter;  }
	
	/**
	 * Called to construct the view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Create a DIV for the TaskListing to create task filter
		// widgets in...
		m_gwtTaskFilter = new VibeFlowPanel();
		m_gwtTaskFilter.getElement().setId("gwtTaskFilter");
		m_gwtTaskFilter.addStyleName("vibe-taskFolderFilterPanel");
		getEntryMenuPanel().getFlowPanel().add(m_gwtTaskFilter);
		
		// ...and construct everything else.
		loadPart1Async();
	}
	
	/*
	 * Asynchronously loads the TaskGraphsPanel.
	 */
	private void loadPart1Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the TaskGraphsPanel.
	 */
	private void loadPart1Now() {
		TaskGraphsPanel.createAsync(this, getFolderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, TASK_GRAPHS_PANEL_INDEX);
				loadPart2Async();
			}
		});
	}

	/*
	 * Asynchronously loads the task display data.
	 */
	private void loadPart2Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the task display data.
	 */
	private void loadPart2Now() {
		GetTaskDisplayDataCmd cmd = new GetTaskDisplayDataCmd(getFolderInfo().getBinderIdAsLong());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetTaskDisplayData();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_taskDisplayData = ((TaskDisplayDataRpcResponseData) result.getResponseData());
				loadPart3Async();
			}			
		});
	}

	/*
	 * Asynchronously loads the TaskListing.
	 */
	private void loadPart3Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the TaskListing.
	 */
	private void loadPart3Now() {
		// Yes!  Load the task listing's split point.
		TaskListing.createAsync(
				this,
				new TaskListingClient() {				
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(TaskListing taskListing) {
				m_taskListing = taskListing;
				populateViewAsync();
			}
		});
	}

	/*
	 * Asynchronously populates the the task view.
	 */
	private void populateViewAsync() {
		Scheduler.ScheduledCommand doPopulate = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				populateViewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the the task view.
	 */
	private void populateViewNow() {
		getFlowPanel().add(m_taskListing);
		viewReady();
	}
	
	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		getFlowPanel().clear();
		populateViewAsync();
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		m_taskListing.resize();
	}

	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete() {
		// Tell the task listing to resize itself now that it can
		// determine how big everything is.
		m_taskListing.resize();
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the task folder view and perform some operation on it.        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Loads the TaskFolderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(TaskFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				TaskFolderView dfView = new TaskFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_TaskFolderView());
				vClient.onUnavailable();
			}
		});
	}
}
