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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Class used for the content of the task graphs in the task folder
 * view.  
 * 
 * @author drfoster@novell.com
 */
public class TaskGraphsPanel extends ToolPanelBase {
	private VibeFlowPanel	m_fp;	// The panel holding the content.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private TaskGraphsPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
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
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(TaskGraphsPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				TaskGraphsPanel bcp = new TaskGraphsPanel(containerResizer, binderInfo, toolPanelReady);
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
		m_fp.add(new InlineLabel("TaskGraphsPanel.loadPart1Now():  ...this needs to be implemented..."));
		toolPanelReady();
	}
	
	/**
	 * Called from the binder view to allow the panel to do any work
	 * required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the task graphs.
		m_fp.clear();
		loadPart1Async();
	}
}
