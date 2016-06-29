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
package org.kablink.teaming.gwt.client.binderviews;

import com.google.gwt.user.client.ui.UIObject;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.rpc.shared.GetPersonalWorkspaceDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PersonalWorkspaceDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

/**
 * This widget is the Personal Workspace view.  It is used to display a
 * personal workspace.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class PersonalWorkspaceView extends WorkspaceViewBase implements ToolPanelReady {
	public final static boolean SHOW_GWT_PERSONAL_WORKSPACE	= false;	//! DRF (20150330):  Leave false on checkin until it's all working.
	
	private PersonalWorkspaceDisplayDataRpcResponseData	m_personalWorkspaceDisplayData;	// The personal workspace display data read from the server.
	private VibeFlowPanel								m_accessoriesPanel;				//
	private VibeFlowPanel								m_dashboardPanel;				//
	private VibeFlowPanel								m_footerPanel;					//
	private VibeFlowPanel								m_headerPanel;					//
	private VibeFlowPanel								m_htmlElementPanel;				//
	private VibeFlowPanel								m_mainPanel;					//
	
	/*
	 * Constructor method.
	 */
	private PersonalWorkspaceView(BinderInfo binderInfo, UIObject parent, ViewReady viewReady) {
		// Initialize the super class...
		super(binderInfo, parent, viewReady);

		// ...initialize the main panel...
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName("vibe-personalWorkspaceView_MainPanel");
		initWidget(m_mainPanel);
		
		// ...and build the rest of the view.
		loadPart1Async();
	}
	
	/*
	 * Build this view.  This view will have the following components:
	 * 1. Title
	 * 2. Description
	 * 3. Accessories
	 * 4. Footer
	 */
	private void buildView() {
		// Add the header.
		m_headerPanel = new VibeFlowPanel();
		m_headerPanel.addStyleName("vibe-personalWorkspaceView_HeaderPanel");
		m_mainPanel.add(m_headerPanel);
//!		...this needs to be implemented...
		m_headerPanel.add(new Label("PersonalWorkspaceView.m_headerPanel:  ...this needs to be implemented..."));
		
		// Add the accessories.
		m_accessoriesPanel = new VibeFlowPanel();
		m_accessoriesPanel.addStyleName("vibe-personalWorkspaceView_AccessoriesPanel");
		m_mainPanel.add(m_accessoriesPanel);
		AccessoriesPanel.createAsync(this, getBinderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase accessories) {
				m_accessoriesPanel.add(accessories);
			}
		});
		
		// Add an HTML element.
		m_htmlElementPanel = new VibeFlowPanel();
		m_htmlElementPanel.addStyleName("vibe-personalWorkspaceView_HtmlElementPanel");
		m_mainPanel.add(m_htmlElementPanel);
		HtmlElementPanel.createAsync(this, getBinderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase htmlElement) {
				m_htmlElementPanel.add(htmlElement);
			}
		});
		
		// Add the dashboard.
		m_dashboardPanel = new VibeFlowPanel();
		m_dashboardPanel.addStyleName("vibe-personalWorkspaceView_DashboardPanel");
		m_mainPanel.add(m_dashboardPanel);
//!		...this needs to be implemented...
		m_dashboardPanel.add(new Label("PersonalWorkspaceView.m_dashboardPanel:  ...this needs to be implemented..."));
		
		// Add the footer.
		m_footerPanel = new VibeFlowPanel();
		m_footerPanel.addStyleName("vibe-personalWorkspaceView_FooterPanel");
		m_mainPanel.add(m_footerPanel);
		FooterPanel.createAsync(this, getBinderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				m_footerPanel.add(tpb);
			}
		});
		
		// Finally, tell the base class that the view is ready.
		viewReady();
	}
	
	/*
	 * Asynchronously loads the personal workspace display data.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the personal workspace display data.
	 */
	private void loadPart1Now() {
		GetPersonalWorkspaceDisplayDataCmd cmd = new GetPersonalWorkspaceDisplayDataCmd(getBinderInfo());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Handle the failure...
				String error = m_messages.rpcFailure_GetPersonalWorkspaceDisplayData();
				GwtClientHelper.handleGwtRPCFailure(caught, error);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_personalWorkspaceDisplayData = ((PersonalWorkspaceDisplayDataRpcResponseData) result.getResponseData());
				populateViewAsync();
			}			
		});
	}

	/*
	 * Asynchronously populates the the personal workspace view.
	 */
	private void populateViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateViewNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the the personal workspace view.
	 */
	private void populateViewNow() {
		buildView();
	}
	
	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 * 
	 * @param tooPanel
	 */
	@Override
	public void toolPanelReady(ToolPanelBase toolPanel) {
		// Nothing to do.  We don't need to know when tool panels are
		// ready.
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the personal workspace binder view and perform some operation */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Loads the PersonalWorkspaceView split point and returns an instance of it via the callback.
	 *
	 * @param binderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo binderInfo, final UIObject parent, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(PersonalWorkspaceView.class, new RunAsyncCallback() {			
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert(m_messages.codeSplitFailure_PersonalWorkspaceView());
				vClient.onUnavailable();
			}

			@Override
			public void onSuccess() {
				PersonalWorkspaceView view = new PersonalWorkspaceView(binderInfo, parent, viewReady);
				vClient.onSuccess(view);
			}
		});
	}
}
