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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent.CollectionCallback;
import org.kablink.teaming.gwt.client.event.MenuHideEvent;
import org.kablink.teaming.gwt.client.event.MenuShowEvent;
import org.kablink.teaming.gwt.client.event.MenuLoadedEvent;
import org.kablink.teaming.gwt.client.event.RefreshSidebarTreeEvent;
import org.kablink.teaming.gwt.client.event.RerootSidebarTreeEvent;
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
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.TreeMode;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayBase;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayHorizontal;
import org.kablink.teaming.gwt.client.workspacetree.TreeDisplayVertical;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This widget will display a workspace tree control.
 * 
 * @author drfoster@novell.com
 */
public class WorkspaceTreeControl extends ResizeComposite
	implements
		// Event handlers implemented by this class.
		ActivityStreamEnterEvent.Handler,
		ActivityStreamEvent.Handler,
		ActivityStreamExitEvent.Handler,
		ChangeContextEvent.Handler,
		ContextChangedEvent.Handler,
		GetSidebarCollectionEvent.Handler,
		MenuHideEvent.Handler,
		MenuShowEvent.Handler,
		MenuLoadedEvent.Handler,
		RefreshSidebarTreeEvent.Handler,
		RerootSidebarTreeEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler
{
	private BinderInfo					m_selectedBinderInfo;		//
	private boolean						m_hiddenByEmptySidebar;		//
	private boolean						m_isTrash;					//
	private GwtMainPage					m_mainPage;					//
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private TreeDisplayBase				m_treeDisplay;				//
	private TreeMode 					m_tm;						//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] HORIZONTAL_BINDER_TREE_REGISTERED_EVENTS = new TeamingEvents[] {
		// Menu events.
		TeamingEvents.MENU_LOADED,
	};
	
	private static final TeamingEvents[] VERTICAL_TREE_REGISTERED_EVENTS = new TeamingEvents[] {
		// Activity stream events.
		TeamingEvents.ACTIVITY_STREAM_ENTER,
		TeamingEvents.ACTIVITY_STREAM,
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		
		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		TeamingEvents.CONTEXT_CHANGED,
		
		// Sidebar events.
		TeamingEvents.GET_SIDEBAR_COLLECTION,
		TeamingEvents.REFRESH_SIDEBAR_TREE,
		TeamingEvents.REROOT_SIDEBAR_TREE,
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
		
		// Menu events.
		TeamingEvents.MENU_HIDE,
		TeamingEvents.MENU_SHOW,
		TeamingEvents.MENU_LOADED,
	};
	
	/*
	 * Constructs a WorkspaceTreeControl based on the information
	 * in the RequestInfo object.
	 *
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private WorkspaceTreeControl(GwtMainPage mainPage, final BinderInfo selectedBinderInfo, final boolean isTrash, TreeMode tm) {
		// Initialize the super class...
		super();
		
		// ...save the parameters...
		m_mainPage           = mainPage;
		m_selectedBinderInfo = selectedBinderInfo;
		m_isTrash            = (isTrash && (TreeMode.HORIZONTAL_BINDER == tm));
		m_tm                 = tm;

		// ...and initialize everything else.
		final WorkspaceTreeControl wsTree = this;
		final VibeFlowPanel mainPanel = new VibeFlowPanel();

		// What type of tree are we constructing?
		switch (m_tm) {
		case HORIZONTAL_BINDER:
		case HORIZONTAL_POPUP:
		{
			boolean isBinder = (TreeMode.HORIZONTAL_BINDER == m_tm);
			mainPanel.addStyleName("breadCrumb_Browser " + (isBinder ? "breadCrumb_BrowserBinder" : "breadCrumb_BrowserPopup"));
			GetHorizontalTreeCmd cmd = new GetHorizontalTreeCmd(selectedBinderInfo.getBinderIdAsLong(), m_tm);
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTree(),
						selectedBinderInfo.getBinderId());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response)  {
					// Asynchronously render the horizontal tree so
					// that we can release the AJAX request ASAP.
					GetHorizontalTreeRpcResponseData responseData = (GetHorizontalTreeRpcResponseData) response.getResponseData();
					List<TreeInfo> tiList = responseData.getTreeInfo();
					renderHTreeAsync(
						mainPanel,
						wsTree,
						selectedBinderInfo,
						tiList);
				}
			});
			
			break;
		}
			
		case VERTICAL:
		{
			mainPanel.addStyleName("workspaceTreeControl workspaceTreeWidth");
			GetVerticalTreeCmd cmd = new GetVerticalTreeCmd(selectedBinderInfo.getBinderId());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTree(),
						selectedBinderInfo.getBinderId());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response)  {
					// Asynchronously render the vertical tree so that
					// we can release the AJAX request ASAP.
					TreeInfo ti = ((TreeInfo) response.getResponseData());
					renderVTreeAsync(
						mainPanel,
						wsTree,
						selectedBinderInfo,
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
	 */
	public void clearBusySpinner() {
		// If we've got a tree display...
		if (null != m_treeDisplay) {
			// ...tell it that a context has been loaded.
			m_treeDisplay.clearBusySpinner();
		}
	}
	
	/*
	 * Called when activity stream mode is to be entered on the sidebar
	 * tree.
	 *
	 * @param defaultASI
	 */
	private void enterActivityStreamMode(ActivityStreamInfo defaultASI, boolean fromEnterEvent) {
		// If we're displaying a sidebar tree...
		if (isSidebarTree() && (null != m_treeDisplay)) {
			// ...tell it to load the activity stream navigation
			// ...points.
			m_treeDisplay.enterActivityStreamMode(defaultASI, fromEnterEvent);
		}
	}
	
	/*
	 * Called when activity stream mode is to be exited on the sidebar
	 * tree
	 */
	private void exitActivityStreamMode(ExitMode exitMode) {
		// If we're displaying a sidebar tree...
		if (isSidebarTree() && (null != m_treeDisplay)) {
			// ...tell it to exit activity stream mode.
			m_treeDisplay.exitActivityStreamMode(exitMode);
			if (!(siteNavigationAvailable())) {
				GwtTeaming.fireEventAsync(new SidebarHideEvent());
			}
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
	 * Returns the binder this tree control was built from.
	 * 
	 * @return
	 */
	public BinderInfo getSelectedBinderInfo() {
		return m_selectedBinderInfo;
	}
	
	/**
	 * Returns the TreeMode of the workspace tree being hosted.
	 * 
	 * @return
	 */
	public TreeMode getTreeMode() {
		return m_tm;
	}
	
	/**
	 * Returns true if the WorkspaceTreeControl is a bread crumb tree
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBreadcrumbTree() {
		return (getTreeMode().isHorizontalPopup());
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
	 * Returns true if the main menu is visible and false otherwise.
	 * 
	 * @return
	 */
	public boolean isMainMenuVisible() {
		return ((null != m_mainPage) && m_mainPage.isMainMenuVisible());
	}
	
	/**
	 * Returns true if the WorkspaceTreeControl is a sidebar tree and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isSidebarTree() {
		return (getTreeMode().isVertical());
	}
	
	/**
	 * Returns true if we're rendering a tree for a trash view and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isTrash() {
		return m_isTrash;
	}
	
	/**
	 * Returns true if the tree is hidden because it had an empty
	 * sidebar and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTreeHiddenByEmptySidebar() {
		return ((!(isVisible()) && m_hiddenByEmptySidebar));
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
		enterActivityStreamMode(event.getActivityStreamInfo(), true);
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
	 * Called when the accessories panel is attached to the document.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the accessories panel is detached from the document.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles ContextChangedEvent's received by this class.
	 * 
	 * Implements the ContextChangedEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanged(final ContextChangedEvent event) {
		// Only sidebar trees care about context changes.
		if (isSidebarTree()) {
			// Is the selection valid?
			OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
			if (GwtClientHelper.validateOSBI(osbInfo, false)) {
				// Yes!  Does the sidebar tree need to react to this?
				// (It doesn't if it came from the sidebar itself AND
				// we don't have to force a refresh.)
				if ((Instigator.SIDEBAR_TREE_SELECT != osbInfo.getInstigator()) ||
					getRequestInfo().isRefreshSidebarTree()) {
					// Yes!  Tell it to change contexts.
					setSelectedBinder(osbInfo);
				}
			}
			
			// The context has been loaded.  Clear any busy spinner on
			// the sidebar.
			clearBusySpinner();
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
	public void onChangeContext(final ChangeContextEvent event) {
		// Only sidebar trees care about context changes.
		if (isSidebarTree()) {
			// Is the selection valid?
			OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
			if (GwtClientHelper.validateOSBI(osbInfo, false)) {
				// Yes!  Tell the sidebar to start the busy spinner.
				showBinderBusy(osbInfo);
			}
		}
	}
	
	/**
	 * Handles GetSidebarCollectionEvent's received by this class.
	 * 
	 * Implements the GetSidebarCollectionEvent.Handler.onGetSidebarCollection() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGetSidebarCollection(GetSidebarCollectionEvent event) {
		// If this is a sidebar tree...
		if (isSidebarTree()) {
			// ...tell it to return its collection.
			CollectionCallback cb = event.getCollectionCallback();
			if (null == m_treeDisplay)
			     cb.collection(CollectionType.NOT_A_COLLECTION);
			else m_treeDisplay.getSidebarCollection(cb);
		}
	}
	
	/**
	 * Handles MenuHideEvent's received by this class.
	 * 
	 * Implements the MenuHideEvent.Handler.onMenuHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMenuHide(MenuHideEvent event) {
		// If we have a tree display...
		if (null != m_treeDisplay) {
			// ...simply tell it the menu was hidden.
			m_treeDisplay.menuHide();
		}
	}
	
	/**
	 * Handles MenuShowEvent's received by this class.
	 * 
	 * Implements the MenuShowEvent.Handler.onMenuShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMenuShow(MenuShowEvent event) {
		// If we have a tree display...
		if (null != m_treeDisplay) {
			// ...simply tell it the menu was shown.
			m_treeDisplay.menuShow();
		}
	}
	
	/**
	 * Handles MenuLoadedEvent's received by this class.
	 * 
	 * Implements the MenuLoadedEvent.Handler.onMenuLoaded()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onMenuLoaded(MenuLoadedEvent event) {
		// If we have a tree display...
		if (null != m_treeDisplay) {
			// ...simply tell it which menu item was loaded.
			m_treeDisplay.menuLoaded(event.getMenuItem());
		}
	}
	
	/**
	 * Handles RefreshSidebarTreeEvent's received by this class.
	 * 
	 * Implements the RefreshSidebarTreeEvent.Handler.onRefreshSidebarTree() method.
	 * 
	 * @param event
	 */
	@Override
	public void onRefreshSidebarTree(RefreshSidebarTreeEvent event) {
		// If this is a sidebar tree...
		if (isSidebarTree()) {
			// ...tell it to refresh.
			m_treeDisplay.refreshSidebarTree();
		}
	}
	
	/**
	 * Handles RerootSidebarTreeEvent's received by this class.
	 * 
	 * Implements the RerootSidebarTreeEvent.Handler.onRerootSidebarTree() method.
	 * 
	 * @param event
	 */
	@Override
	public void onRerootSidebarTree(RerootSidebarTreeEvent event) {
		// If this is a sidebar tree...
		if (isSidebarTree()) {
			// ...tell it to re-root.
			m_treeDisplay.rerootSidebarTree();
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
			m_hiddenByEmptySidebar = event.getHiddenByEmptySidebar();
		}
	}
	
	/**
	 * Called to hide/show the workspace tree control.
	 * 
	 * Overrides the UIObject.setVisible() method.
	 * 
	 * @param visible
	 */
	@Override
	public void setVisible(boolean visible) {
		// Simply call the super class version of the method.  I
		// included this override for setting breakpoints to debug
		// issues with hiding and/or showing the workspace tree
		// control.
		super.setVisible(visible);
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
			m_hiddenByEmptySidebar = false;
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					m_treeDisplay.repositionBinderConfig();
				}
			});
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
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					relayoutPageNow();
				}
			});
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
	private void renderHTreeAsync(final FlowPanel mainPanel, final WorkspaceTreeControl wsTree, final BinderInfo selectedBinderInfo, final List<TreeInfo> tiList) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				renderHTreeNow(mainPanel, wsTree, selectedBinderInfo, tiList);
			}
		});
	}
	
	/*
	 * Synchronously renders a horizontal tree.
	 */
	private void renderHTreeNow(FlowPanel mainPanel, WorkspaceTreeControl wsTree, BinderInfo selectedBinderInfo, List<TreeInfo> tiList) {
		m_treeDisplay = new TreeDisplayHorizontal(wsTree, tiList);
		m_treeDisplay.render(selectedBinderInfo, mainPanel);
	}
	
	/*
	 * Asynchronously renders a vertical tree.
	 */
	private void renderVTreeAsync(final FlowPanel mainPanel, final WorkspaceTreeControl wsTree, final BinderInfo selectedBinderInfo, final TreeInfo ti) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				renderVTreeNow(mainPanel, wsTree, selectedBinderInfo, ti);
			}
		});
	}
	
	/*
	 * Synchronously renders a vertical tree.
	 */
	private void renderVTreeNow(FlowPanel mainPanel, WorkspaceTreeControl wsTree, final BinderInfo selectedBinderInfo, TreeInfo ti) {
		// Construct the vertical tree display.
		m_treeDisplay = new TreeDisplayVertical(wsTree, ti);
		
		// Are we starting up showing what's new?
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		if (ri.isShowWhatsNewOnLogin()) {
			// Yes!  Then we enter activity stream mode by
			// default.  Tell the menu about the context...
			final boolean historyAction = ri.isShowSpecificWhatsNewHistoryAction();
			m_mainPage.setMenuContext(selectedBinderInfo, false, "");
			
			// ...and enter activity stream mode.
			m_treeDisplay.setRenderContext(selectedBinderInfo, mainPanel);
			GetDefaultActivityStreamCmd cmd =
				new GetDefaultActivityStreamCmd(
					selectedBinderInfo.getBinderIdAsLong(),
					ri.getShowSpecificWhatsNew(),
					ri.getShowSpecificWhatsNewId());
			ri.clearShowSpecificWhatsNew();
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
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
					m_treeDisplay.enterActivityStreamMode(asi, historyAction);
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// If the user doesn't have a default
					// saved or the default saved is
					// current binder...
					ActivityStreamInfo asi = ((ActivityStreamInfo) response.getResponseData());
					if ((null == asi) || (ActivityStream.CURRENT_BINDER == asi.getActivityStream())) {
						// ...default to site wide.
						asi = new ActivityStreamInfo();
						asi.setActivityStream(ActivityStream.SITE_WIDE);
						asi.setTitle(GwtTeaming.getMessages().treeSiteWide());
					}
					m_treeDisplay.enterActivityStreamMode(asi, historyAction);
				}
			});
		}
		
		else if (null != selectedBinderInfo) {
			// No, we aren't starting in activity stream
			// mode!  Render the tree.
			m_treeDisplay.render(selectedBinderInfo, mainPanel);
		}
	}
	
	/**
	 * Called to reset the main menu context to that previously loaded.
	 */
	public void resetMenuContext() {
		m_mainPage.resetMenuContext();
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
			boolean			doValidation;
			TeamingEvents[]	registeredEvents;
			switch (m_tm) {
			default:
			case HORIZONTAL_POPUP:   registeredEvents = null;                                     doValidation = false; break;
			case HORIZONTAL_BINDER:  registeredEvents = HORIZONTAL_BINDER_TREE_REGISTERED_EVENTS; doValidation = false; break;
			case VERTICAL:           registeredEvents = VERTICAL_TREE_REGISTERED_EVENTS;          doValidation = true;  break;
			}
			if (null != registeredEvents) {
				EventHelper.registerEventHandlers(
					GwtTeaming.getEventBus(),
					registeredEvents,
					this,
					m_registeredEventHandlers,
					doValidation);
			}
		}
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
	 * Returns true if site navigation is available and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean siteNavigationAvailable() {
		// Site navigation is available in all license modes except
		// Filr.
		return (!(GwtClientHelper.isLicenseFilr()));
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


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the workspace tree control and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
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
	 * @param selectedBinderInfo
	 * @param mode
	 * @param wsTreeCtrlClient
	 */
	public static void createAsync(final GwtMainPage mainPage, final BinderInfo selectedBinderInfo, final boolean isTrash, final TreeMode mode, final WorkspaceTreeControlClient wsTreeCtrlClient) {
		GWT.runAsync(WorkspaceTreeControl.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				WorkspaceTreeControl wsTreeCtrl = new WorkspaceTreeControl(mainPage, selectedBinderInfo, isTrash, mode);
				wsTreeCtrlClient.onSuccess(wsTreeCtrl);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_WorkspaceTreeControl());
				wsTreeCtrlClient.onUnavailable();
			}
		});
	}
}
