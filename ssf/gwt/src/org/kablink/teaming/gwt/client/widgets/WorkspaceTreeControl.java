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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetDefaultActivityStreamCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetHorizontalTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetHorizontalTreeRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayBase;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayHorizontal;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayVertical;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * This widget will display the WorkspaceTree control.
 * 
 * @author drfoster@novell.com
 */
public class WorkspaceTreeControl extends Composite
	implements
	// Event handlers implemented by this class.
		ActivityStreamEnterEvent.Handler,
		ActivityStreamEvent.Handler,
		ActivityStreamExitEvent.Handler,
		ChangeContextEvent.Handler,
		ContextChangedEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler
{	
	private GwtMainPage m_mainPage;
	private TreeDisplayBase m_treeDisplay;
	private TreeMode m_tm;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_verticalTreeRegisteredEvents = new TeamingEvents[] {
		// Activity stream events.
		TeamingEvents.ACTIVITY_STREAM_ENTER,
		TeamingEvents.ACTIVITY_STREAM,
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		
		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		TeamingEvents.CONTEXT_CHANGED,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
	};
	
	/**
	 * The mode this WorkspaceTreeControl is running in.
	 * 
	 * HORIZONTAL:  Typically used in the Teaming bread crumbs.
	 * VERTICAL:    Typically used in the Teaming sidebar. 
	 */
	public enum TreeMode {
		HORIZONTAL,
		VERTICAL,
	}
	
	/*
	 * Constructs a WorkspaceTreeControl based on the information
	 * in the RequestInfo object.
	 *
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private WorkspaceTreeControl(GwtMainPage mainPage, final String selectedBinderId, TreeMode tm) {
		// Save the parameters.
		m_mainPage = mainPage;
		m_tm       = tm;

		final WorkspaceTreeControl wsTree = this;
		final FlowPanel mainPanel = new FlowPanel();
		
		switch (m_tm) {
		case HORIZONTAL:
		{
			mainPanel.addStyleName("breadCrumb_Browser");
			GetHorizontalTreeCmd cmd = new GetHorizontalTreeCmd( selectedBinderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTree(),
						selectedBinderId);
				}
				
				public void onSuccess(VibeRpcResponse response)  {
					List<TreeInfo> tiList;
					GetHorizontalTreeRpcResponseData responseData;
					
					// Asynchronously render the horizontal tree so
					// that we can release the AJAX request ASAP.
					responseData = (GetHorizontalTreeRpcResponseData) response.getResponseData();
					tiList = responseData.getTreeInfo();
					renderHTreeAsync(
						mainPanel,
						wsTree,
						selectedBinderId,
						tiList);
				}
			});
			
			break;
		}
			
		case VERTICAL:
		{
			// Register the events to be handled by this class.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_verticalTreeRegisteredEvents,
				this);

			mainPanel.addStyleName("workspaceTreeControl");
			GetVerticalTreeCmd cmd = new GetVerticalTreeCmd( selectedBinderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTree(),
						selectedBinderId);
				}
				public void onSuccess(VibeRpcResponse response)  {
					TreeInfo ti;
					
					// Asynchronously render the vertical tree so that
					// we can release the AJAX request ASAP.
					ti = (TreeInfo) response.getResponseData();
					renderVTreeAsync(
						mainPanel,
						wsTree,
						selectedBinderId,
						ti);
				}
			});
			
			// Set the size of the control.
			relayoutPageAsync();
			break;
		}
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget(mainPanel);
	}

	/**
	 * Called after a new context has been loaded.
	 * 
	 * @param binderId
	 */
	public void contextLoaded(String binderId) {
		if (null != m_treeDisplay) {
			// Simply tell the display that the context has been loaded.
			m_treeDisplay.contextLoaded(binderId);
		}
	}
	
	/**
	 * Called when activity stream mode is to be entered on the sidebar
	 * tree.
	 *
	 * @param defaultASI
	 */
	public void enterActivityStreamMode(ActivityStreamInfo defaultASI) {
		// If we're displaying a sidebar tree...
		if (isSidebarTree() && (null != m_treeDisplay)) {
			// ...tell it to load the activity stream navigation
			// ...points.
			m_treeDisplay.enterActivityStreamMode(defaultASI);
		}
	}
	
	/**
	 * Called when activity stream mode is to be exited on the sidebar
	 * tree
	 */
	public void exitActivityStreamMode(ExitMode exitMode) {
		// If we're displaying a sidebar tree...
		if (isSidebarTree() && (null != m_treeDisplay)) {
			// ...tell it to exit activity stream mode.
			m_treeDisplay.exitActivityStreamMode(exitMode);
		}
	}
	
	/**
	 * Returns the RequestInfo object associated with this
	 * WorkspaceTreeControl.
	 * 
	 * @return
	 */
	public RequestInfo getRequestInfo() {
		return GwtClientHelper.getRequestInfo();
	}

	/**
	 * Returns true if the WorkspaceTreeControl is a bread crumb tree
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBreadcrumbTree() {
		return (TreeMode.HORIZONTAL == m_tm);
	}
	
	/**
	 * Returns true if the workspace tree control is in activity stream
	 * mode and false otherwise.
	 * 
	 * @return
	 */
	public boolean isInActivityStreamMode() {
		return ((null != m_treeDisplay) && m_treeDisplay.isInActivityStreamMode());
	}

	/**
	 * Returns true if the WorkspaceTreeControl is a sidebar tree and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isSidebarTree() {
		return (TreeMode.VERTICAL == m_tm);
	}
	
	/**
	 * Handles ActivityStreamEvent's received by this class.
	 * 
	 * Implements the ActivityStreamEvent.Handler.onActivityStream() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStream(ActivityStreamEvent event) {
		setActivityStream(event.getActivityStreamInfo());
	}

	/**
	 * Handles ActivityStreamEnterEvent's received by this class.
	 *
	 * Note:  If this is passed a default activity stream to load, a
	 *    separate activity stream event will be triggered by the
	 *    workspace tree control AFTER it has entered activity stream
	 *    mode.
	 * 
	 * Implements the ActivityStreamEnterEvent.Handler.onActivityStreamEnter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamEnter(ActivityStreamEnterEvent event) {
		enterActivityStreamMode(event.getActivityStreamInfo());
	}

	/**
	 * Handles ActivityStreamExitEvent's received by this class.
	 *
	 * Implements the ActivityStreamExitEvent.Handler.onActivityStreamExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamExit(ActivityStreamExitEvent event) {
		exitActivityStreamMode(event.getExitMode());
	}

	/**
	 * Called to select an activity stream in the sidebar.
	 *
	 * @param asi
	 */
	public void setActivityStream(ActivityStreamInfo asi) {
		// If we're displaying a sidebar tree...
		if (isSidebarTree() && (null != m_treeDisplay)) {
			// ...tell it to select this activity stream.
			m_treeDisplay.setActivityStream(asi);
		}
	}
	
	/**
	 * Handles ContextChangedEvent's received by this class.
	 * 
	 * Implements the ContextChangedEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanged(final ContextChangedEvent event)
	{
		if (isSidebarTree()) {
			OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
			if (GwtClientHelper.validateOSBI(osbInfo, false)) {
				Instigator instigator = osbInfo.getInstigator();
				if ((Instigator.SIDEBAR_TREE_SELECT  != instigator) ||
				    (Instigator.FORCE_SIDEBAR_RELOAD == instigator) ||
				     osbInfo.getForceSidebarReload())
				{
					// Tell the WorkspaceTreeControl to change contexts.
					setSelectedBinder(osbInfo);
				}
				
				if (Instigator.CONTENT_AREA_CHANGED == instigator) {
					contextLoaded(osbInfo.getBinderId().toString());
				}
			}
		}
	}
	
	/**
	 * Handles ChangeContextEvent's received by this class.
	 * 
	 * Implements the ChangeContextEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeContext(final ChangeContextEvent event)
	{
		if (isSidebarTree()) {
			OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
			if (GwtClientHelper.validateOSBI(osbInfo, false)) {
				showBinderBusy(osbInfo);
			}
		}
	}
	
	/**
	 * Handles SidebarHideEvent's received by this class.
	 * 
	 * Implements the SidebarHideEvent.Handler.onSidebarHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarHide(SidebarHideEvent event) {
		if (isSidebarTree() && (!(m_mainPage.isAdminActive()))) {
			setVisible(false);
		}
	}
	
	/**
	 * Handles SidebarShowEvent's received by this class.
	 * 
	 * Implements the SidebarShowEvent.Handler.onSidebarShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarShow(SidebarShowEvent event) {
		if (isSidebarTree() && (!(m_mainPage.isAdminActive()))) {
			setVisible(true);
		}
	}
	
	/**
	 * Called to change the binder being displayed by this
	 * WorkspaceTreeControl.
	 * 
	 * @param binderInfo
	 */
	public void setSelectedBinder(OnSelectBinderInfo binderInfo) {
		if (null != m_treeDisplay) {
			m_treeDisplay.setSelectedBinder(binderInfo);
		}
	}
	
	/**
	 * Asynchronously forces the workspace tree control to lay itself
	 * out correctly.
	 */
	public void relayoutPageAsync() {
		// We only worry about layout if the tree if it's in vertical
		// mode.  Is it?
		if (isSidebarTree()) {
			// Yes!  Force it to lay itself out again.
			ScheduledCommand layouter = new ScheduledCommand() {
				@Override
				public void execute() {
					relayoutPageNow();
				}
			};
			Scheduler.get().scheduleDeferred(layouter);
		}
	}
		
	/*
	 * Synchronously forces the workspace tree control to lay itself
	 * out correctly.
	 */
	private void relayoutPageNow() {
		int height;
		Style style;

		// Calculate how high the workspace tree should be...
		height = (Window.getClientHeight() - getAbsoluteTop() - 20);
		
		// ...and set it's height.
		style = getElement().getStyle();
		style.setHeight(height, Style.Unit.PX);
	}

	/*
	 * Asynchronously renders a horizontal tree.
	 */
	private void renderHTreeAsync(final FlowPanel mainPanel, final WorkspaceTreeControl wsTree, final String selectedBinderId, final List<TreeInfo> tiList) {
		ScheduledCommand renderHTree = new ScheduledCommand() {
			@Override
			public void execute() {
				renderHTreeNow(mainPanel, wsTree, selectedBinderId, tiList);
			}
		};
		Scheduler.get().scheduleDeferred(renderHTree);
	}
	
	/*
	 * Synchronously renders a horizontal tree.
	 */
	private void renderHTreeNow(FlowPanel mainPanel, WorkspaceTreeControl wsTree, String selectedBinderId, List<TreeInfo> tiList) {
		m_treeDisplay = new TreeDisplayHorizontal(wsTree, tiList);
		m_treeDisplay.render(selectedBinderId, mainPanel);
	}
	
	/*
	 * Asynchronously renders a vertical tree.
	 */
	private void renderVTreeAsync(final FlowPanel mainPanel, final WorkspaceTreeControl wsTree, final String selectedBinderId, final TreeInfo ti) {
		ScheduledCommand renderVTree = new ScheduledCommand() {
			@Override
			public void execute() {
				renderVTreeNow(mainPanel, wsTree, selectedBinderId, ti);
			}
		};
		Scheduler.get().scheduleDeferred(renderVTree);
	}
	
	/*
	 * Synchronously renders a vertical tree.
	 */
	private void renderVTreeNow(FlowPanel mainPanel, WorkspaceTreeControl wsTree, final String selectedBinderId, TreeInfo ti) {
		// Construct the vertical tree display.
		m_treeDisplay = new TreeDisplayVertical(wsTree, ti);
		
		// Are we starting up showing what's new?
		if (GwtClientHelper.getRequestInfo().isShowWhatsNewOnLogin()) {
			GetDefaultActivityStreamCmd cmd;
			
			// Yes!  Then we enter activity stream mode by
			// default.  Tell the menu about the context...
			m_mainPage.setMenuContext(selectedBinderId, false, "");
			
			// ...and enter activity stream mode.
			m_treeDisplay.setRenderContext(selectedBinderId, mainPanel);
			cmd = new GetDefaultActivityStreamCmd( selectedBinderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				public void onFailure(Throwable t) {
					// If we couldn't get it, handle the
					// failure...
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetDefaultActivityStream());
					
					// ...and just go site wide.
					ActivityStreamInfo asi = new ActivityStreamInfo();
					asi.setActivityStream(ActivityStream.SITE_WIDE);
					asi.setTitle(GwtTeaming.getMessages().treeSiteWide());
					m_treeDisplay.enterActivityStreamMode(asi);
				}
				
				public void onSuccess(VibeRpcResponse response) {
					ActivityStreamInfo asi = null;
					
					if ( response.getResponseData() != null )
						asi = (ActivityStreamInfo) response.getResponseData();
					
					// If the user doesn't have a default
					// saved or the default saved is
					// current binder...
					if ((null == asi) || (ActivityStream.CURRENT_BINDER == asi.getActivityStream())) {
						// ...default to site wide.
						asi = new ActivityStreamInfo();
						asi.setActivityStream(ActivityStream.SITE_WIDE);
						asi.setTitle(GwtTeaming.getMessages().treeSiteWide());
					}
					m_treeDisplay.enterActivityStreamMode(asi);
				}
			});
		}
		
		else if (GwtClientHelper.hasString(selectedBinderId)){
			// No, we aren't starting in activity stream
			// mode!  Render the tree.
			m_treeDisplay.render(selectedBinderId, mainPanel);
		}
	}
	
	/**
	 * Called to reset the main menu context to that previously loaded.
	 */
	public void resetMenuContext() {
		m_mainPage.resetMenuContext();
	}
	
	/*
	 * Called when a selection change is in progress.
	 */
	private void showBinderBusy(OnSelectBinderInfo osbInfo) {
		if (null != m_treeDisplay) {
			m_treeDisplay.showBinderBusy(osbInfo);
		}
	}
	
	/**
	 * Callback interface to interact with the workspace tree control
	 * asynchronously after it loads. 
	 */
	public interface WorkspaceTreeControlClient {
		void onSuccess(WorkspaceTreeControl wsTreeCtrl);
		void onUnavailable();
	}

	/**
	 * Loads the WorkspaceTreeControl split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param mainPage
	 * @param selectedBinderId
	 * @param mode
	 * @param wsTreeCtrlClient
	 */
	public static void createAsync(final GwtMainPage mainPage, final String selectedBinderId, final TreeMode mode, final WorkspaceTreeControlClient wsTreeCtrlClient) {
		GWT.runAsync(WorkspaceTreeControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				WorkspaceTreeControl wsTreeCtrl = new WorkspaceTreeControl(mainPage, selectedBinderId, mode);
				wsTreeCtrlClient.onSuccess(wsTreeCtrl);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_WorkspaceTreeControl() );
				wsTreeCtrlClient.onUnavailable();
			}
		});
	}
}
