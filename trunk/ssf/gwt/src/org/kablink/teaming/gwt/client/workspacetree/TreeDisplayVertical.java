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
package org.kablink.teaming.gwt.client.workspacetree;

import java.util.HashMap;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandVerticalBucketCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetRootWorkspaceIdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalActivityStreamsTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalNodeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PersistNodeCollapseCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PersistNodeExpandCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's sidebar.
 * 
 * @author drfoster@novell.com
 */
public class TreeDisplayVertical extends TreeDisplayBase {
	private ActivityStreamInfo		m_selectedActivityStream;	// When displaying activity streams, the ActivityStream info of the selected activity stream.  null if no activity stream is currently selected.
	private BusyInfo				m_busyInfo;					// Stores a BusyInfo while we're busy switching contexts.
	private FlowPanel				m_rootPanel;				// The top level FlowPanel containing the sidebar tree's contents.
	private HashMap<String,Integer> m_renderDepths;				// A map of the depths the Binder's are are displayed at.
	private Long 					m_selectedBinderId;			// The ID of the currently selected binder.

	// The following are used for widget IDs assigned to various
	// objects in a running WorkspaceTreeControl.
	private final static String EXTENSION_ID_BASE					= "workspaceTreeBinder_";
	private final static String EXTENSION_ID_ACTIVITY_STREAM_BASE	= (EXTENSION_ID_BASE + "ActivityStream_");
	private final static String EXTENSION_ID_BUCKET_BASE			= (EXTENSION_ID_BASE + "Bucket_");
	private final static String EXTENSION_ID_COLLECTION_BASE		= (EXTENSION_ID_BASE + "Collection_");
	private final static String EXTENSION_ID_SELECTOR_ANCHOR		= "selectorAnchor_";
	private final static String EXTENSION_ID_SELECTOR_BASE			= (EXTENSION_ID_BASE + "Selector_");
	private final static String EXTENSION_ID_SELECTOR_ID			= (EXTENSION_ID_BASE + "SelectorId");
	private final static String EXTENSION_ID_TRASH_BASE				= (EXTENSION_ID_BASE + "Trash_");
	private final static String EXTENSION_ID_TRASH_PERMALINK		= (EXTENSION_ID_BASE + "TrashPermalink");

	// The following controls the grid size and nested offsets for the
	// WorkspaceTreeControl.
	private final static int SELECTOR_GRID_DEPTH_OFFSET	=  18;	// Based on empirical evidence.
	private final static int SELECTOR_GRID_WIDTH_ADJUST	=  22;	// Based on empirical evidence (expander image and spacing.)
	private final static int SELECTOR_GRID_WIDTH        = (GwtConstants.WORKSPACE_TREE_WIDTH - SELECTOR_GRID_WIDTH_ADJUST);

	// The following defines the maximum amount of time we wait to
	// process the completion event for a context switch.  If we exceed
	// this, we simply clear it.
	private final static int MAX_BUSY_DURATION	= 5000;	//	5 seconds. 
	
	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private class BinderExpander implements ClickHandler {
		private FlexTable	m_grid;			//
		private Image		m_expanderImg;	//
		private int			m_gridRow;		//
		private TreeInfo	m_ti;			//

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param grid
		 * @param gridRow
		 * @param expanderImg
		 */
		BinderExpander(TreeInfo ti, FlexTable grid, int gridRow, Image expanderImg) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			m_ti          = ti;
			m_grid        = grid;
			m_gridRow     = gridRow;
			m_expanderImg = expanderImg;
		}

		/*
		 * Asynchronously collapses the current row.
		 */
		private void doCollapseRowAsync() {
			ScheduledCommand collapser = new ScheduledCommand() {
				@Override
				public void execute() {
					doCollapseRowNow();
				}
			};
			Scheduler.get().scheduleDeferred(collapser);
		}

		/*
		 * Synchronously collapses the current row.
		 */
		private void doCollapseRowNow() {
			// Collapse the row...
			if (!m_ti.isActivityStream()) {
				m_ti.clearChildBindersList();
			}
			m_ti.setBinderExpanded(false);
			reRenderRow(m_grid, m_gridRow, m_ti, true);
			m_expanderImg.setResource(getImages().tree_opener());
			
			// ...and tell everybody that it's been collapsed.
			GwtTeaming.fireEventAsync(new TreeNodeCollapsedEvent(getSelectedBinderId(), getTreeMode()));
		}

		/*
		 * Asynchronously expands the current row.
		 */
		private void doExpandRowAsync(final TreeInfo expandedTI) {
			ScheduledCommand expander = new ScheduledCommand() {
				@Override
				public void execute() {
					doExpandRowNow(expandedTI);
				}
			};
			Scheduler.get().scheduleDeferred(expander);
		}
		
		/*
		 * Synchronously expands the current row.
		 */
		private void doExpandRowNow(TreeInfo expandedTI) {
			// Expand the row...
			m_ti.setBinderExpanded(true);
			m_ti.setChildBindersList(expandedTI.getChildBindersList());
			if (0 < m_ti.getBinderChildren()) {
				reRenderRow(m_grid, m_gridRow, m_ti, false);
			}
			m_expanderImg.setResource(getImages().tree_closer());
			
			// ...and tell everybody that it's been expanded.
			GwtTeaming.fireEventAsync(new TreeNodeExpandedEvent(getSelectedBinderId(), getTreeMode()));
		}
		
		/**
		 * Called when the expander is clicked.
		 * 
		 * @param event
		 */
		@Override
		public void onClick(ClickEvent event) {
			// Are we collapsing the row?
			if (m_ti.isBinderExpanded()) {
				// Yes!  Are we showing an expanded bucket or activity
				// stream?
				if (m_ti.isBucket() || m_ti.isActivityStream()) {
					// Yes!  Collapse it.
					doCollapseRowAsync();
				}
				
				else {
					// No, we aren't showing an expanded bucket or
					// activity stream!  We must be showing a normal
					// row.  Can we mark the row as being closed?
					PersistNodeCollapseCmd cmd = new PersistNodeCollapseCmd(m_ti.getBinderInfo().getBinderId());
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_PersistExpansionState(),
								m_ti.getBinderInfo().getBinderId());
						}
						
						@Override
						public void onSuccess(VibeRpcResponse response) {
							// Yes!  Update the TreeInfo, re-render the
							// row and change the row's Anchor Image to a
							// tree_opener.
							doCollapseRowAsync();
						}
					});
				}
			}
				
			else {
				// No, we aren't collapsing it!  We must be expanding
				// it.  Are we showing a collapsed bucket?
				if (m_ti.isBucket()) {
					// Yes!  Expand it.
					ExpandVerticalBucketCmd cmd = new ExpandVerticalBucketCmd(m_ti.getBucketInfo());
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_ExpandBucket());
						}
						
						@Override
						public void onSuccess(VibeRpcResponse response) {
							// Yes!  Update the TreeInfo, and if
							// there are any expanded rows, render
							// them and change the row's Anchor
							// Image to a tree_closer.
							TreeInfo expandedTI = ((TreeInfo) response.getResponseData());
							doExpandRowAsync(expandedTI);
						}
					});
				}

				// No, we aren't showing a collapsed bucket!  Are we
				// showing a collapsed activity stream?
				else if (m_ti.isActivityStream()) {
					// Yes!  If there are any expanded rows, render
					// them and change the row's Anchor Image to a
					// tree_closer.
					doExpandRowAsync(m_ti);
				}
				
				else {
					// No, we aren't showing a collapsed activity
					// stream either!  We must be showing a normal row.
					// Can we mark the row as being opened?
					PersistNodeExpandCmd cmd = new PersistNodeExpandCmd(m_ti.getBinderInfo().getBinderId());
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_PersistExpansionState(),
								m_ti.getBinderInfo().getBinderId());
						}
						
						@Override
						public void onSuccess(VibeRpcResponse response) {
							// Run the 'Get Vertical Node' RPC request
							// as a scheduled command so the RPC
							// request that got us here can be
							// terminated.
							ScheduledCommand getVNode = new ScheduledCommand() {
								@Override
								public void execute() {
									// Can we get a TreeInfo for the
									// expansion?
									GetVerticalNodeCmd cmd = new GetVerticalNodeCmd(m_ti.getBinderInfo().getBinderId());
									GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
										@Override
										public void onFailure(Throwable t) {
											GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetTree(),
												m_ti.getBinderInfo().getBinderId());
										}
										
										@Override
										public void onSuccess(VibeRpcResponse response) {
											// Yes!  Update the
											// TreeInfo, and if there
											// are any expanded rows,
											// render them and change
											// the row's Anchor Image
											// to a tree_closer.
											TreeInfo expandedTI = ((TreeInfo) response.getResponseData());
											doExpandRowAsync(expandedTI);
										}
									});
								}
							};
							Scheduler.get().scheduleDeferred(getVNode);
						}
					});
				}
			}
		}
	}
	
	/*
	 * Inner class used to handle mouse events for a Binder selector.  
	 */
	private static class BinderSelectorMouseHandler implements MouseOverHandler, MouseOutHandler {
		private boolean	m_isBinderCollection;	//
		private String	m_selectorGridId;		//
		
		/**
		 * Class constructor.
		 * 
		 * @param selectorGridId
		 */
		BinderSelectorMouseHandler(String selectorGridId, boolean isBinderCollection) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			m_selectorGridId      = selectorGridId;
			m_isBinderCollection  = isBinderCollection;
		}
		
		/**
		 * Called when the mouse leaves a Binder selector.
		 * 
		 * @param me
		 */
		@Override
		public void onMouseOut(MouseOutEvent me) {
			// Simply remove the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.removeClassName("workspaceTreeControlRowHover");
			if (m_isBinderCollection) {
				selectorPanel_New.removeClassName("workspaceTreeControlRowHover_collection");
			}
		}
		
		/**
		 * Called when the mouse enters a Binder selector.
		 * 
		 * @param me
		 */
		@Override
		public void onMouseOver(MouseOverEvent me) {
			// Simply add the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.addClassName("workspaceTreeControlRowHover");
			if (m_isBinderCollection) {
				selectorPanel_New.addClassName("workspaceTreeControlRowHover_collection");
			}

		}
	}

	/*
	 * Inner class used to track information about the sidebar tree
	 * being in a busy state.
	 */
	private class BusyInfo {
		private Timer		m_maxBusyDurationTimer;	// A timer used to control the maximum amount of time we'll keep a busy spinner spinning.
		private TreeInfo	m_busyTI;				// TreeInfo running a busy animation, if there is one.  May be null.

		/**
		 * Class constructor.
		 */
		public BusyInfo() {
			// Initialize the super class.
			super();
			
			// Setup a timer to wait for the the busy state to clear.
			// If we exceed the timeout, we simply clear the busy
			// state.
			m_maxBusyDurationTimer = new Timer() {
				@Override
				public void run() {
					// Clear the busy state and...
					clearBusy();

					// ...if we're in UI debug mode, display an alert
					// ...about the problem.
//#					GwtClientHelper.debugAlert(
//#						"Rats!  I hate it when this happens.  We've entered an endless busy state with a sidebar tree spinner.\n\n" +
//#						"That means that somewhere along the way, we failed to process the completion event for a context switch.");
				}
			};
			m_maxBusyDurationTimer.schedule(MAX_BUSY_DURATION);
		}

		/**
		 * Called to clear the busy state.
		 */
		public void clearBusy() {
			// Clear the timer.
			clearTimer();
			m_busyInfo = null;
			
			// Do we have a TreeInfo for the tree node that's busy?
			if (hasBusyTI()) {
				// Yes!  Restore its default image.
				TreeInfo busyTI = getBusyTI();
				setBinderImageResource(busyTI, BinderIconSize.getSidebarTreeIconSize());
			}
			
			else {
				// No, we don't have a TreeInfo for the tree node
				// that's busy!  In that case, we'll have stuck the
				// spinner on the tree's root node.  Restore that.
				Image binderImg = ((Image) getRootTreeInfo().getBinderUIImage());
				binderImg.setUrl(getImages().spacer_1px().getSafeUri());
			}
		}
		/*
		 * Cancels and forgets about the timer waiting for contributors.
		 */
		private void clearTimer() {
			// If we have a busy timer...
			if (null != m_maxBusyDurationTimer) {
				// ...cancel and forget about it.
				m_maxBusyDurationTimer.cancel();
				m_maxBusyDurationTimer = null;
			}
		}
		
		/*
		 * Returns the TreeInfo associated with this BusyInfo object.
		 */
		private TreeInfo getBusyTI() {
			return m_busyTI;
		}

		/*
		 * Returns true if this BusyInfo is tracking a TreeInfo and
		 * false otherwise.
		 */
		private boolean hasBusyTI() {
			return (null != m_busyTI);
		}

		/**
		 * Stores a TreeInfo in this BusyInfo object.
		 * 
		 * @param busyTI
		 */
		public void setBusyTI(TreeInfo busyTI) {
			m_busyTI = busyTI;
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param wsTree
	 * @param rootTI
	 */
	public TreeDisplayVertical(WorkspaceTreeControl wsTree, TreeInfo rootTI) {
		// Initialize the super class...
		super(wsTree, rootTI);
		
		// ...and initialize everything else.
		m_selectedBinderId = (-1L);
		m_renderDepths     = new HashMap<String,Integer>();
	}

	/*
	 * Creates an Anchor containing the close push button for the
	 * sidebar's header when in activity streams mode.
	 */
	private Anchor buildCloseActivityStreamsPB() {
		// Create the Label for the push button...
		Label closeLabel = new Label(getMessages().treeCloseActivityStreams());
		closeLabel.setWordWrap(false);
		closeLabel.addStyleName("workspaceTreeControlHeader_closeButton");
		setWidgetHover(closeLabel, getMessages().treeCloseActivityStreamsHint());
		
		// ...create the Anchor...
		Anchor closePBAnchor = new Anchor();
		closePBAnchor.addStyleName("workspaceTreeControlHeader_closeA");
		closePBAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtTeaming.fireEvent(new ActivityStreamExitEvent(ExitMode.SIMPLE_EXIT));
			}
		});
		
		// ...and tie it all together.
		closePBAnchor.getElement().appendChild(closeLabel.getElement());
		return closePBAnchor;
	}
	
	/*
	 * Constructs a string that can be used for style of an element.
	 */
	private static String buildElementStyle(boolean isBinderCollection, String baseStyle) {
		if (isBinderCollection) {
			if (null == baseStyle) {
				baseStyle = "";
			}
			baseStyle += (" " + baseStyle + "_collection");
		}
		return baseStyle;
	}
	
	private static String buildElementStyle(TreeInfo ti, String baseStyle) {
		// Always use the initial form of the method.
		return buildElementStyle(ti.isBinderCollection(), baseStyle);
	}
	
	/**
	 * Returns an OnSelectBinderInfo object that corresponds to a
	 * TreeInfo object.
	 * 
	 * Implementation of TreeDisplayBase.buildOnSelectBinderInfo().
	 * 
	 * @param ti
	 * 
	 * @return
	 */
	@Override
	OnSelectBinderInfo buildOnSelectBinderInfo(TreeInfo ti) {
		// Construct an OnSelectBinderInfo for this TreeInfo object.
		OnSelectBinderInfo reply = new OnSelectBinderInfo(ti, Instigator.SIDEBAR_TREE_SELECT);
		
		// Is this TreeInfo object the trash Binder?
		BinderInfo bi = ti.getBinderInfo();
		if (bi.isBinderTrash()) {
			// Yes!  Is there another Binder selected?
			String selectedId = Document.get().getElementById(EXTENSION_ID_SELECTOR_ID).getAttribute("value");
			if (GwtClientHelper.hasString(selectedId)) {
				// Yes!  Then we'll return it's trash permalink URL and
				// not the URL from the trash.  This will cause the
				// trash to display on the selected Binder and not on
				// the workspace, unless no Binder is selected.
				Element selectedElement = Document.get().getElementById(selectedId);
				String trashPermalink = selectedElement.getAttribute(EXTENSION_ID_TRASH_PERMALINK);
				reply.setBinderUrl(trashPermalink);
			}
		}

		// Does this TreeInfo object refer to a collection?
		if (ti.isBinderCollection()) {
			// Yes!  Store the collection type in the
			// OnSelectBinderInfo.
			reply.setCollectionType(bi.getCollectionType());
		}
		
		// If we get here, reply refers to the OnSelectBinderInfo
		// object for this TreeInfo.  Return it.
		return reply;
	}

	/**
	 * Returns true if the context can be changed and false otherwise.
	 * 
	 * Overrides TreeDisplayBase.canChangeContext().
	 * 
	 * @return
	 */
	@Override
	boolean canChangeContext() {
		// We can only change contexts if we're not already in the
		// process of changing contexts.
		return (null == m_busyInfo);
	}

	/**
	 * Called when activity stream mode is to be entered on the sidebar
	 * tree.
	 *
	 * Overrides TreeDisplayBase.enterActivityStreamMode().
	 * 
	 * @param defaultASI
	 */
	@Override
	public void enterActivityStreamMode(final ActivityStreamInfo defaultASI) {		
		// Are we currently in activity stream mode?
		if (isInActivityStreamMode()) {
			// Yes!  If we we're given an activity stream to select...
			if (null != defaultASI) {
				// ...select it...
				setActivityStreamImpl(defaultASI);
			}

			// ...otherwise, we don't do anything.
		}
		
		else {
			// No, we aren't in activity stream mode!  Build a TreeInfo
			// for the activity streams...
			final String selectedBinderId = String.valueOf(m_selectedBinderId);
			GetVerticalActivityStreamsTreeCmd cmd = new GetVerticalActivityStreamsTreeCmd(selectedBinderId);
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming.getMessages().rpcFailure_GetActivityStreamsTree(), selectedBinderId);
				}

				@Override
				public void onSuccess(VibeRpcResponse response) {
					// ...and put it into effect.
					TreeInfo ti = ((TreeInfo) response.getResponseData());
					enterActivityStreamModeAsync(ti, defaultASI);
				}
			});
		}
	}
	
	/*
	 * Asynchronously loads an activity stream based TreeInfo into the
	 * sidebar.
	 */
	private void enterActivityStreamModeAsync(final TreeInfo asRootTI, final ActivityStreamInfo defaultASI) {
		ScheduledCommand asLoader = new ScheduledCommand() {
			@Override
			public void execute() {
				enterActivityStreamModeNow(asRootTI, defaultASI);
			}
		};
		Scheduler.get().scheduleDeferred(asLoader);
	}
	
	/*
	 * Synchronously loads an activity stream based TreeInfo into the
	 * sidebar.
	 */
	private void enterActivityStreamModeNow(TreeInfo asRootTI, ActivityStreamInfo defaultASI) {
		// Put the activity streams TreeInfo into affect...
		m_selectedActivityStream = defaultASI;
		setRootTreeInfo(asRootTI);
		m_rootPanel.clear();
		render(String.valueOf(m_selectedBinderId), m_rootPanel);
		
		// ...and if we have a default activity stream to select...
		if (null != defaultASI) {
			// ...put it into affect.
			GwtTeaming.fireEvent(new ActivityStreamEvent(defaultASI));
		}

		// Finally, reset the menu so that it display what's
		// appropriate for activity stream mode.
		resetMenuContext();
	}
	
	/**
	 * Called when activity stream mode is to be exited on the sidebar
	 * tree.
	 *
	 * Overrides TreeDisplayBase.exitActivityStreamMode().
	 * 
	 * @param exitMode
	 */
	@Override
	public void exitActivityStreamMode(ExitMode exitMode) {
		// Are we currently in activity streams mode?
		if (isInActivityStreamMode()) { 
			// Yes!  Reload the workspace tree control.
			m_selectedActivityStream = null;
			rerootTree(m_selectedBinderId, m_selectedBinderId, exitMode);
		}
	}
	
	/*
	 * Removes the widgets from a FlexTable row.
	 */
	private static void clearRow(FlexTable grid, int row) {
		grid.remove(grid.getWidget(row, 0));
		grid.remove(grid.getWidget(row, 1));
	}

	/*
	 * Clears the refresh and re-root sidebar flags.
	 */
	private void clearSidebarFlags() {
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		ri.clearRefreshSidebarTree();
		ri.clearRerootSidebarTree();
	}

	/**
	 * Called after a new context has been loaded.
	 * 
	 * Overrides TreeDisplayBase.contextLoaded().
	 * 
	 * @param binderId
	 */
	@Override
	public void contextLoaded(String binderId) {
		// Are we tracking a BusyInfo indicating that we're in the
		// middle of a context switch?
		if (null != m_busyInfo) {
			// Yes!  Clear the busy state.
			m_busyInfo.clearBusy();
		}
	}

	/*
	 * Returns the style to use for the mouse over cursor for a given
	 * TreeInfo.
	 */
	private String getCursorStyle(TreeInfo ti) {
		String reply;
		
		if (ti.isBucket()) {
			reply = "cursorDefault";
		}
		else {
			reply = "cursorPointer";
			if (ti.isActivityStream()) {
				TeamingEvents te = ti.getActivityStreamEvent();
				if ((null == te) || (TeamingEvents.UNDEFINED == te)) {
					reply = "cursorDefault";
				}
			}
		}
		
		return reply;
	}
	
	/*
	 * Returns the depth that TreeInfo was last rendered at.
	 */
	private int getRenderDepth(TreeInfo ti) {
		int reply;
		Integer depth = m_renderDepths.get(getSelectorIdAppendage(ti));
		if (null == depth)
			 reply = 0;
		else reply = depth.intValue();
		return reply;
	}
	
	/*
	 * Returns the ID to use for the selector for this TreeInfo.
	 */
	private static String getSelectorId(TreeInfo ti) {
		String idBase;
		if      (ti.isBucket())           idBase =  EXTENSION_ID_BUCKET_BASE;
		else if (ti.isActivityStream())   idBase =  EXTENSION_ID_ACTIVITY_STREAM_BASE;
		else if (ti.isBinderTrash())      idBase =  EXTENSION_ID_TRASH_BASE;
		else if (ti.isBinderCollection()) idBase = (EXTENSION_ID_COLLECTION_BASE + ti.getBinderInfo().getCollectionType().name() + "_");
		else                              idBase =  EXTENSION_ID_SELECTOR_BASE;
		return (idBase + getSelectorIdAppendage(ti));
	}

	/*
	 * Returns the part of the ID for a TreeInfo's selector that
	 * appears after the base component.
	 */
	private static String getSelectorIdAppendage(TreeInfo ti) {
		String reply;

		// Is this TreeInfo a bucket?
		if (ti.isBucket()) {
			// Yes!  Generate an appendage based on the range of names
			// the bucket spans.
			BucketInfo bi = ti.getBucketInfo();
			StringBuffer sb = new StringBuffer(String.valueOf(bi.getBucketTuple1()));
			sb.append("_");
			sb.append(bi.getBucketTuple2());
			reply = sb.toString();
		}

		// No, the TreeInfo isn't a bucket!  Is it an activity stream?
		else if (ti.isActivityStream()) {
			// Yes!  Generate an appendage that factors in the activity
			// stream and binder ID.
			ActivityStreamInfo asi = ti.getActivityStreamInfo();
			ActivityStream as = ((null == asi) ? ActivityStream.UNKNOWN : asi.getActivityStream());
			reply = ti.getBinderInfo().getBinderId();
			if (!(GwtClientHelper.hasString(reply))) {
				reply = ti.getBinderTitle();
			}
			reply += ("_" + as.getValue() + "_" + ti.getBinderTitle());
		}

		else {	
			// No, it isn't an activity stream either!  Generate an
			// appendage based on the binder ID.
			reply = ti.getBinderInfo().getBinderId();
			if (!(GwtClientHelper.hasString(reply))) {
				reply = "-unknown-";
			}
		}

		// If we get here, reply refers to the appendage to use for the
		// TreeInfo in question.  Return it.
		return reply;
	}

	/*
	 * Returns the widget to use as the label of a selector in the tree.
	 */
	private Widget getSelectorLabel(TreeInfo ti, boolean boldIt) {
		Widget reply;

		// Is this item a bucket?
		if (ti.isBucket()) {
			// Yes!  Generate the appropriate widgets. 
			FlowPanel selectorPanel = new FlowPanel();
			selectorPanel.addStyleName(buildElementStyle(ti, "workspaceTreeBinderAnchor") + " " + "gwtUI_nowrap");
			if (boldIt) {
				selectorPanel.addStyleName("bold");
			}
			selectorPanel.getElement().setId(getSelectorId(ti));
			selectorPanel.add(buildBucketPartLabel(ti.getPreBucketTitle() + "\u00A0"));
			selectorPanel.add(buildBucketRangeImage());
			selectorPanel.add(buildBucketPartLabel("\u00A0" + ti.getPostBucketTitle()));
			reply = selectorPanel;
		}
		
		else {
			// No, it's not a bucket!  Generate a simple Label for it.
			Label selectorLabel = new Label(ti.getBinderTitle());
			selectorLabel.setWordWrap(false);
			selectorLabel.addStyleName(buildElementStyle(ti, "workspaceTreeBinderAnchor"));
			if (boldIt) {
				selectorLabel.addStyleName("bold");
			}
			selectorLabel.getElement().setId(getSelectorId(ti));
			if (!(ti.isBinderTrash())) {
				selectorLabel.getElement().setAttribute(
					EXTENSION_ID_TRASH_PERMALINK,
					ti.getBinderTrashPermalink());
			}
			reply = selectorLabel;
		}
		
		// If we get here, reply refers to the Widget to use for the
		// selector label.  Return it.
		return reply;
	}

	/**
	 * Returns true if we're displaying activity streams and false
	 * otherwise.
	 * 
	 * Implementation of TreeDisplayBase.isInActivityStreamMode().
	 * 
	 * @return
	 */
	@Override
	public boolean isInActivityStreamMode() {
		TreeInfo rootTI = getRootTreeInfo();
		return ((null != rootTI) && rootTI.isActivityStream());
	}
	
	/*
	 * Returns true if the Binder corresponding to this TreeInfo object
	 * should be selected and false otherwise.
	 */
	private boolean isBinderSelected(TreeInfo ti) {
		boolean reply;
		
		if (ti.isBucket()) {
			reply = false;
		}
		
		else if (ti.isActivityStream()) {
			reply = (null != m_selectedActivityStream);
			if (reply) {
				reply = m_selectedActivityStream.isEqual(ti.getActivityStreamInfo());
			}
		}
		
		else {
			reply = (Long.valueOf(ti.getBinderInfo().getBinderId()).equals(m_selectedBinderId));
		}
		
		return reply;
	}

	/**
	 * Tells a sidebar tree implementation to refresh itself
	 * maintaining its current context and selected binder.
	 * 
	 * Implements the TreeDisplayBase.rerootSidebarTree() method.
	 */
	@Override
	public void refreshSidebarTree() {
		// Simply reload the tree.
		reloadTree(m_selectedBinderId);
	}

	/**
	 * Tells a sidebar tree implementation to re-root itself
	 * to its currently selected binder.
	 * 
	 * Implements the TreeDisplayBase.rerootSidebarTree() method.
	 */
	@Override
	public void rerootSidebarTree() {
		// Re-root the tree at the currently selected binder.
		rerootTree(m_selectedBinderId, m_selectedBinderId, ExitMode.SIMPLE_EXIT);
	}

	/**
	 * Called to render the information in this TreeInfo object into a
	 * FlowPanel.
	 * 
	 * Implementation of TreeDisplayBase.render().
	 *
	 * @param selectedBinderId
	 * @param targetPanel
	 */
	@Override
	public void render(String selectedBinderId, FlowPanel targetPanel) {
		// Track the Binder that's to be initially selected.
		m_rootPanel = targetPanel;
		setSelectedBinderId(selectedBinderId);
		
		// Create a hidden <INPUT> that we'll use to store the ID of
		// the currently selected Binder.
		Hidden selectedId = new Hidden();
		selectedId.getElement().setId(EXTENSION_ID_SELECTOR_ID);
		m_rootPanel.add(selectedId);
		
		// Create the WorkspaceTree control's header...
		TreeInfo rootTI = getRootTreeInfo();
		boolean isAS = rootTI.isActivityStream();
		FlexTable selectorGrid = new FlexTable();
		String styles = "workspaceTreeControlHeader workspaceTreeControlHeader_base ";
		if (isAS)
		     styles += "workspaceTreeControlHeader_as";
		else styles += "workspaceTreeControlHeader_nav";
		selectorGrid.addStyleName(styles);
		selectorGrid.setCellSpacing(0);
		selectorGrid.setCellPadding(0);
		String rootTitle = rootTI.getBinderTitle();
		if (GwtClientHelper.hasString(rootTitle)) {
			Image binderImg = GwtClientHelper.buildImage(getImages().spacer_1px().getSafeUri());
			rootTI.setBinderUIImage(binderImg);
			binderImg.addStyleName("workspaceTreeBinderImg");
			selectorGrid.setWidget(0, 0, binderImg);
			Label selectorLabel = new Label(rootTitle);
			selectorLabel.setWordWrap(false);
			selectorLabel.getElement().setId(getSelectorId(rootTI));
			selectorLabel.getElement().setAttribute(EXTENSION_ID_TRASH_PERMALINK, rootTI.getBinderTrashPermalink());
			selectorGrid.setWidget(0, 1, selectorLabel);
			selectorGrid.setWidget(0, 2, new Label("\u00A0"));
			selectorGrid.getCellFormatter().setWidth(0, 2, "100%");
			Widget rootWidget;
			if (isAS) {
				selectorGrid.setWidget(0, 3, buildCloseActivityStreamsPB());
				rootWidget = selectorGrid;
			}
			else {
				Anchor selectorA = new Anchor();
				selectorA.getElement().appendChild(selectorGrid.getElement());
				selectorA.addClickHandler(new BinderSelector(rootTI));
				setWidgetHover(selectorA, getBinderHover(rootTI));
				rootWidget = selectorA;
			}
			m_rootPanel.add(rootWidget);
		}

		List<TreeInfo> tiList = rootTI.getCollectionsList();
		boolean hasCollections;
		if (ContentControl.SHOW_COLLECTION_VIEW)
		     hasCollections = ((null != tiList) && (!(tiList.isEmpty())));
		else hasCollections = false;
		
		// ...and its initial content grid.
		FlexTable grid = new FlexTable();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		m_rootPanel.add(grid);

		// Are there are any collection rows to display?
		int rowCount = 0;
		if (hasCollections) {
			// Yes!  Count...
			rowCount += tiList.size();
			
			// ...and render them.
			grid.addStyleName("workspaceTreeControlBody_collection workspaceTreeWidth");
			for (TreeInfo ti:  tiList) {
				renderRow(grid, grid.getRowCount(), ti, 0);
			}
		}

		// Are we running in full Vibe mode or in an activity stream?
		if ((!(isVibeLite())) || isAS) {
			// Yes!  Are there are any child binder rows to display?
			tiList = rootTI.getChildBindersList();
			if ((null != tiList) && (!(tiList.isEmpty()))) {
				// Yes!  Count them.
				rowCount += tiList.size();
				
				// Did we display collection rows?
				if (hasCollections) {
					// Yes!  Create a new grid for the child binder rows as
					// they require different styling.
					grid = new FlexTable();
					grid.setCellSpacing(0);
					grid.setCellPadding(0);
					m_rootPanel.add(grid);
				}
				grid.addStyleName("workspaceTreeControlBody");
	
				// If we're not display activity streams...
				if (!isAS) {
					// ...insert a header above the child binders.
					grid.insertRow(0);
					InlineLabel il = new InlineLabel(getMessages().treeWSAndFolders());
					il.addStyleName("workspaceTreeControlTreeHeader");
					grid.setWidget(0, 0, il);
					grid.getFlexCellFormatter().setColSpan(0, 0, 2);
				}
				
				// Finally, render the child binders.
				for (TreeInfo ti:  tiList) {
					renderRow(grid, grid.getRowCount(), ti, 0);
				}
			}
		}

		// Did we display anything in the sidebar?
		if (0 == rowCount) {
			// No!  Is the tree visible?
			if (isTreeVisible()) {
				// Yes!  Then by default, we hide it.
				GwtTeaming.fireEvent(
					new SidebarHideEvent(
						true,	// true -> Resize content immediately.
						true));	// true -> Hidden because of an empty sidebar.
			}
		}
		
		// Yes, we displayed something in the sidebar!  Is it currently
		// being hidden because it was empty?
		else if (isTreeHiddenByEmptySidebar()) {
			// Yes!  Show it.
			SidebarShowEvent.fireOne();
		}
	}
	
	/*
	 * Called to render an individual row in the WorkspaceTree control.
	 */
	private void renderRow(FlexTable grid, int row, TreeInfo ti, int renderDepth, boolean reRenderToCollapse) {
		// Store the depth at which we're rendering this Binder.
		setRenderDepth(ti, renderDepth);
		
		// Is this row expandable?
		Widget expanderWidget;
		Image expanderImg;
		ImageResource expanderImgRes = ti.getExpanderImage();
		boolean showExpander = (null != expanderImgRes);
		if (showExpander) {
			// Yes!  Put an expander Anchor to allow expanding and
			// collapsing of its contents.
			expanderImg = GwtClientHelper.buildImage(expanderImgRes);
			expanderImg.addStyleName(buildElementStyle(ti, "workspaceTreeBinderExpanderImg"));
			Anchor expanderA = new Anchor();
			expanderA.getElement().appendChild(expanderImg.getElement());
			expanderA.addClickHandler(new BinderExpander(ti, grid, row, expanderImg));
			expanderWidget = expanderA;
		}
		else {
			// No, it isn't expandable!  Put a 16x16 spacer in place of
			// the expander.
			expanderImgRes = getImages().spacer_1px();
			expanderImg = GwtClientHelper.buildImage(expanderImgRes);
			expanderImg.setWidth( EXPANDER_WIDTH );
			expanderImg.setHeight(EXPANDER_HEIGHT);
			expanderWidget = expanderImg;
		}

		// Generate the widgets to select the Binder.
		FlexTable selectorGrid = new FlexTable();
		selectorGrid.setCellSpacing(0);
		selectorGrid.setCellPadding(0);
		Image binderImg = GwtClientHelper.buildImage(((String) null), ti.getBinderHoverImage());
		ti.setBinderUIImage(binderImg);
		setBinderImageResource(ti, BinderIconSize.getSidebarTreeIconSize());
		binderImg.addStyleName(buildElementStyle(ti, "workspaceTreeBinderImg"));
		selectorGrid.setWidget(0, 0, binderImg);
		Widget selectorLabel = getSelectorLabel(ti, (ti.isActivityStream() && (0 == renderDepth)));
		setWidgetHover(selectorLabel, getBinderHover(ti));
		selectorGrid.setWidget(0, 1, selectorLabel);
		selectorGrid.setWidget(0, 2, new Label("\u00A0"));
		selectorGrid.getCellFormatter().setWidth(0, 2, "100%");
		int width = (SELECTOR_GRID_WIDTH - (SELECTOR_GRID_DEPTH_OFFSET * renderDepth));
		if (ti.getBinderIconWidth(BinderIconSize.getSidebarTreeIconSize()) > width) {
			width = SELECTOR_GRID_WIDTH;
		}
		selectorGrid.setWidth(String.valueOf(width) + "px");
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorGrid.getElement());
		selectorA.addClickHandler(new BinderSelector(ti));
		selectorA.setWidth("100%");
		String selectorId = getSelectorId(ti);
		String selectorGridId = (EXTENSION_ID_SELECTOR_ANCHOR + selectorId);
		selectorGrid.getElement().setId(selectorGridId);
		selectorGrid.addStyleName(buildElementStyle(ti, "workspaceTreeControlRow"));
		if (ti.isBinderCollection()) {
			String paddingStyle;
			if (isVibeLite())
			     paddingStyle = "padding5b";
			else paddingStyle = "padding3b";
			selectorGrid.addStyleName(paddingStyle);
		}
		if (ti.isBinderBorderTop()) {
			selectorGrid.addStyleName("workspaceTreeControlBorderTop");
		}
		selectorGrid.addStyleName(getCursorStyle(ti));

		// Add the row to the FlexTable.
		grid.setWidget(row, 0, expanderWidget);
		grid.setWidget(row, 1, selectorA);

		// If this Binder is supposed to be selected...
		if (isBinderSelected(ti)) {
			// ...mark it as selected.
			selectBinder(ti);
		}
		
		// Install a mouse handler on the selector Anchor so that we
		// can manage hover overs on them.
		BinderSelectorMouseHandler bsmh = new BinderSelectorMouseHandler(selectorGridId, ti.isBinderCollection());
		selectorA.addMouseOverHandler(bsmh);
		selectorA.addMouseOutHandler( bsmh);
		
		// Is the row showing an expander?
		if (showExpander) {
			// Yes!  Align it to the top of its cell.
			grid.getCellFormatter().setAlignment(
				row,
				0,
				HasHorizontalAlignment.ALIGN_LEFT,
				HasVerticalAlignment.ALIGN_TOP);

			// Is the row expanded?
			if ((!reRenderToCollapse) && shouldBinderBeExpanded(ti)) {
				// Yes!  Then we need to render its contents.
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(0);
				Widget w = grid.getWidget(row, 1);
				grid.remove(w);
				vp.add(w);
				FlexTable expansionGrid = new FlexTable();
				expansionGrid.setCellSpacing(0);
				expansionGrid.setCellPadding(0);
				vp.add(expansionGrid);
				grid.setWidget(row, 1, vp);
				for (TreeInfo tiScan:  ti.getChildBindersList()) {
					renderRow(
						expansionGrid,
						expansionGrid.getRowCount(),
						tiScan,
						(renderDepth + 1));
				}
				expanderImg.setResource(getImages().tree_closer());
			}
		}
	}
	
	private void renderRow(FlexTable grid, int row, TreeInfo ti, int renderDepth) {
		// Always use the initial form of the method.
		renderRow(grid, row, ti, renderDepth, false);	// false -> Not in process of collapsing a row.
	}

	/*
	 * Clears and re-renders a TreeInfo object into a FlexTable row.
	 */
	private void reRenderRow(FlexTable grid, int row, TreeInfo ti, boolean reRenderToCollapse) {
		clearRow(grid, row);
		renderRow(grid, row, ti, getRenderDepth(ti), reRenderToCollapse);
	}
	
	/**
	 * Called to set an activity stream in the sidebar.
	 *
	 * Overrides TreeDisplayBase.setActivityStream().
	 * 
	 * @param asi
	 */
	@Override
	public void setActivityStream(ActivityStreamInfo asi) {
		// Set/load the activity stream, as appropriate. 
		if (isInActivityStreamMode())
			 setActivityStreamImpl(  asi);
		else enterActivityStreamMode(asi);
	}

	/*
	 * Called to set an activity stream in the sidebar.
	 */
	private void setActivityStreamImpl(ActivityStreamInfo asi) {
		// Hide any popup entry IFRAME DIV's.
		GwtClientHelper.jsHideEntryPopupDiv();
		
		// Is this activity stream the one that's already selected?
		if (!(asi.isEqual(m_selectedActivityStream))) {
			// No!  Store it as being selected...
			m_selectedActivityStream = asi;

			// ...and if we can find it in our TreeInfo objects...
			TreeInfo ti = TreeInfo.findActivityStreamTI(getRootTreeInfo(), asi);
			if (null != ti) {
				// ...select it.
				selectBinder(ti);
			}
		}
	}
	
	/**
	 * Does whatever is necessary UI wise to select the Binder
	 * represented by a TreeInfo.
	 * 
	 * Implementation of TreeDisplayBase.selectBinder().
	 * 
	 * @param ti
	 */
	@Override
	void selectBinder(TreeInfo ti) {
		// If this a trash Binder?
		if (!(ti.isBinderTrash())) {
			// No!  Mark it as having been selected.
			if (!(ti.isActivityStream())) {
				setSelectedBinderId(ti.getBinderInfo().getBinderId());
			}
			String selectedId_New = getSelectorId(ti);
			Element selectorLabel_New = Document.get().getElementById(selectedId_New);
			selectorLabel_New.addClassName(buildElementStyle(ti, "workspaceTreeBinderSelected"));
			Element selectorPanel_New = Document.get().getElementById(EXTENSION_ID_SELECTOR_ANCHOR + selectedId_New);
			if (null != selectorPanel_New) {
				selectorPanel_New.addClassName(buildElementStyle(ti, "workspaceTreeControlRowSelected"));
			}

			// ...mark any previous selection as not being selected...
			Element selectorId = Document.get().getElementById(EXTENSION_ID_SELECTOR_ID);
			String selectedId_Old = selectorId.getAttribute("value");
			if (GwtClientHelper.hasString(selectedId_Old) && (!(selectedId_Old.equals(selectedId_New)))) {
				Element selectorLabel_Old = Document.get().getElementById(selectedId_Old);
				if (null != selectorLabel_Old) {
					selectorLabel_Old.removeClassName("workspaceTreeBinderSelected"           );
					selectorLabel_Old.removeClassName("workspaceTreeBinderSelected_collection");
				}
				
				Element selectorPanel_Old = Document.get().getElementById(EXTENSION_ID_SELECTOR_ANCHOR + selectedId_Old);
				if (null != selectorPanel_Old) {
					selectorPanel_Old.removeClassName("workspaceTreeControlRowSelected"           );
					selectorPanel_Old.removeClassName("workspaceTreeControlRowSelected_collection");
				}
			}
			
			// ...and store the new ID as having been selected.
			selectorId.setAttribute("value", selectedId_New);
		}
	}

	/*
	 * Forces the tree to reload regardless of the circumstances.
	 */
	private void reloadTree(Long selectedBinderId) {
		// To reload it, simply re-root it at the same point that it's
		// currently rooted, reselecting the previously selected binder.
		String rootBinderId;
		if (GwtClientHelper.getRequestInfo().isRerootSidebarTree())
		     rootBinderId = String.valueOf(selectedBinderId);
		else rootBinderId = getRootTreeInfo().getBinderInfo().getBinderId();
		rerootTree(rootBinderId, selectedBinderId, ExitMode.SIMPLE_EXIT);
	}
	
	/*
	 * Re-roots the WorkspaceTreeControl to a new Binder and optionally
	 * selects a binder.
	 */
	private void rerootTree(final String newRootBinderId, final Long selectedBinderId, final ExitMode exitingActivityStreamMode) {
		// Clear any pending sidebar flags.  Re-rooting will take care
		// of any refresh/re-root request.
		clearSidebarFlags();
		
		// Read the TreeInfo for the selected Binder.
		GetVerticalTreeCmd cmd = new GetVerticalTreeCmd(newRootBinderId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTree(),
					newRootBinderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response)  {
				// Re-root the tree asynchronously so that we release
				// the AJAX request ASAP.
				TreeInfo rootTI = ((TreeInfo) response.getResponseData());
				rerootTreeAsync(
					newRootBinderId,
					selectedBinderId,
					exitingActivityStreamMode,
					rootTI);
			}
		});
	}
	
	private void rerootTree(final Long newRootBinderId, final Long selectedBinderId, final ExitMode exitingActivityStreamMode) {
		// Always use the initial form of the method.
		rerootTree(String.valueOf(newRootBinderId), selectedBinderId, exitingActivityStreamMode);
	}
	
	private void rerootTree(final String newRootBinderId) {
		// Always use the initial form of the method.
		rerootTree(newRootBinderId, null, ExitMode.SIMPLE_EXIT);
	}
	
	/*
	 * Asynchronously re-roots the WorkspaceTreeControl to a new Binder
	 * and optionally selects a binder.
	 */
	private void rerootTreeAsync(final String newRootBinderId, final Long selectedBinderId, final ExitMode exitingActivityStreamMode, final TreeInfo rootTI) {
		ScheduledCommand treeRooter = new ScheduledCommand() {
			@Override
			public void execute() {
				rerootTreeNow(
					newRootBinderId,
					selectedBinderId,
					exitingActivityStreamMode,
					rootTI);
			}
		};
		Scheduler.get().scheduleDeferred(treeRooter);
	}
	
	/*
	 * Synchronously re-roots the WorkspaceTreeControl to a new Binder
	 * and optionally selects a binder.
	 */
	private void rerootTreeNow(String newRootBinderId, Long selectedBinderId, ExitMode exitingActivityStreamMode, TreeInfo rootTI) {
		// Update the display with the TreeInfo.
		setRootTreeInfo(rootTI);
		m_rootPanel.clear();
		render(newRootBinderId, m_rootPanel);
		
		// If we're supposed to select a binder as part of the
		// re-rooting...
		if (null != selectedBinderId) {
			// ...and we can find that binder...
			TreeInfo selectedBinderTI = TreeInfo.findBinderTI(rootTI, String.valueOf(selectedBinderId));
			if (null != selectedBinderTI) {
				// ...select it.
				selectBinder(selectedBinderTI);
			}
		}
		
		// If we re-rooted the tree to exit activity stream
		// mode...
		if (ExitMode.SIMPLE_EXIT == exitingActivityStreamMode) {
			// ...reset the menu so that it display what's
			// ...appropriate for navigation mode.
			resetMenuContext();
		}
	}

	/**
	 * Sets the initial context to use for the tree.
	 * 
	 * Implementation of TreeDisplayBase.setRenderContext().
	 * 
	 * @param selectedBinderId
	 * @param targetPanel
	 */
	@Override
	public void setRenderContext(String selectedBinderId, FlowPanel targetPanel) {
		// Simply store the parameter in their appropriate data
		// members.
		m_selectedBinderId = Long.valueOf(selectedBinderId);
		m_rootPanel        = targetPanel;
	}
	
	/**
	 * Called to change the binder being displayed by the
	 * WorkspaceTreeControl.
	 * 
	 * Implementation of TreeDisplayBase.setSelectedBinder().
	 * 
	 * @param binderId
	 */
	@Override
	public void setSelectedBinder(OnSelectBinderInfo binderInfo) {
		// If the selection is for a Binder's trash...
		if (binderInfo.isTrash()) {
			// ...we don't change anything.
			return;
		}

		// If the selection is for a collection...
		if (binderInfo.isCollection()) {
			// ...select it.
			TreeInfo targetTI = TreeInfo.findCollectionTI(getRootTreeInfo(), binderInfo.getCollectionType());
			selectBinder(targetTI);
			return;
		}
		
		// Is the requested Binder available in those we've already
		// got loaded?
		Instigator		instigator = binderInfo.getInstigator();
		final Long		binderId   = binderInfo.getBinderId();
		final String	binderIdS  = String.valueOf(binderId);
		final TreeInfo	targetTI   = TreeInfo.findBinderTI(getRootTreeInfo(), binderIdS);
		if (null != targetTI) {
			// Yes!  Should the request cause the tree to be reloaded?
			RequestInfo ri = GwtClientHelper.getRequestInfo();
			final boolean forceReload =
				(ri.isRefreshSidebarTree() ||
				 ri.isRerootSidebarTree());
			
			switch (instigator) {
			case CONTENT_AREA_CHANGED:
			case SIDEBAR_TREE_SELECT:
				if (forceReload)
					 reloadTree(binderId);
				else selectBinder(targetTI);
				break;

			default:
				// Yes, the request should cause the tree to be
				// reloaded!  (It may be coming from the bread crumbs
				// or some other unknown source.)  What's the ID if the
				// selected Binder's root workspace?
				GetRootWorkspaceIdCmd cmd = new GetRootWorkspaceIdCmd(binderIdS);
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetRootWorkspaceId(),
							binderIdS);
						selectBinder(targetTI);
					}
					
					@Override
					public void onSuccess(VibeRpcResponse response)  {
						// Asynchronously perform the selection so that
						// we release the AJAX request ASAP.
						StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
						String rootWorkspaceId = responseData.getStringValue();
						selectRootWorkspaceIdAsync(
							binderIdS,
							forceReload,
							targetTI,
							rootWorkspaceId);
					}
				});
				break;
			}
		}
		
		else {
			// No, the requested Binder isn't available in those we've
			// already got loaded!  Re-root the tree at the selected
			// Binder...
			rerootTree(binderIdS);
		}
	}

	/*
	 * Asynchronously selects a binder and/or re-roots the tree.
	 */
	private void selectRootWorkspaceIdAsync(final String binderId, final boolean forceReload, final TreeInfo targetTI, final String rootWorkspaceId) {
		ScheduledCommand rootWSSelector = new ScheduledCommand() {
			@Override
			public void execute() {
				selectRootWorkspaceIdNow(binderId, forceReload, targetTI, rootWorkspaceId);
			}
		};
		Scheduler.get().scheduleDeferred(rootWSSelector);
	}
	
	/*
	 * Synchronously selects a binder and/or re-roots the tree.
	 */
	private void selectRootWorkspaceIdNow(String binderId, boolean forceReload, TreeInfo targetTI, String rootWorkspaceId) {
		// If the selected Binder's workspace is different from the
		// Binder we're currently rooted to, we need to re-root the
		// tree.  Do we need to re-root?
		if (rootWorkspaceId.equals(getRootTreeInfo().getBinderInfo().getBinderId())) {
			// No!  Simply select the Binder.
			if (forceReload)
				 reloadTree(m_selectedBinderId);
			else selectBinder(targetTI);
		}
		else {
			// Yes, we need to re-root!
			rerootTree(binderId);
		}
	}
	
	/*
	 * Store the depth at which a TreeInfo object was last rendered.
	 */
	private void setRenderDepth(TreeInfo ti, int depth) {
		m_renderDepths.put(getSelectorIdAppendage(ti), new Integer(depth));
	}
	
	/*
	 * Stores which Binder is selected.
	 */
	private void setSelectedBinderId(String selectedBinderId) {
		m_selectedBinderId = Long.valueOf(selectedBinderId);
	}

	/*
	 * Returns true if this binder should be expanded and false
	 * otherwise.
	 */
	private boolean shouldBinderBeExpanded(TreeInfo ti) {
		// If we have a non-expanded activity stream with an activity
		// stream selected, we may need to expand it to ensure that the
		// selection is visible.  Is that the case?
		boolean reply = ti.isBinderExpanded();
		if ((!reply) && ti.isActivityStream() && (null != m_selectedActivityStream)) {
			// Yes!  Is the selected activity stream one that we have
			// to worry about it's parent being expanded to be visible?
			ActivityStream as = m_selectedActivityStream.getActivityStream();
			switch (as) {
			case FOLLOWED_PERSON:
			case FOLLOWED_PLACE:
			case MY_FAVORITE:
			case MY_TEAM:
				// Yes!  Are we looking at it's parent binder?
				ActivityStream parentAS;
				switch (as) {
				default:               parentAS = ActivityStream.UNKNOWN;         break;
				case FOLLOWED_PERSON:  parentAS = ActivityStream.FOLLOWED_PEOPLE; break;
				case FOLLOWED_PLACE:   parentAS = ActivityStream.FOLLOWED_PLACES; break;
				case MY_FAVORITE:      parentAS = ActivityStream.MY_FAVORITES;    break;
				case MY_TEAM:          parentAS = ActivityStream.MY_TEAMS;        break;
				}

				if (ti.getActivityStreamInfo().getActivityStream() == parentAS) {
					// Yes!  Force it to be expanded.
					ti.setBinderExpanded(true);
					reply = true;
				}
				
				break;
				
			default:
				// All other activity stream types top level entities and
				// require no expansion changes.
				break;
			}
		}
		
		// If we get here, reply is true if the binder should be expanded
		// and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Called when a selection change is in progress.
	 *
	 * Shows a busy animation icon while a context switch is in
	 * progress.
	 * 
	 * Overrides TreeDisplayBase.showBusyBinder().
	 * 
	 * @param osbInfo
	 */
	@Override
	public void showBinderBusy(OnSelectBinderInfo osbInfo) {
		// Are we already in the process of changing contexts?
		if (canChangeContext()) {			
			// No!  We will be now.
			m_busyInfo = new BusyInfo();
			
			// Can find the TreeInfo for the binder?
			TreeInfo ti = null;
			TreeInfo rootTI = getRootTreeInfo();
			if (osbInfo.isTrash()) {
				ti = TreeInfo.findBinderTrash(rootTI);
			}
			if (null == ti) {
				if (osbInfo.isCollection()) {
					ti = TreeInfo.findCollectionTI(rootTI, osbInfo.getCollectionType());
				}
				else {
					Long binderId = osbInfo.getBinderId();
					if (null != binderId) {
						ti = TreeInfo.findBinderTI(rootTI, binderId.toString());
					}
				}
			}
			Image binderImg;
			if ((null == ti) || (ti == rootTI)) {
				// No!  Set the busy animation image on the tree's
				// root.
				binderImg = ((Image) rootTI.getBinderUIImage());
				binderImg.setUrl(getImages().busyAnimation().getSafeUri());
			}
			else {
				// Yes!  Set the busy animation image for this
				// TreeInfo's binder...
				binderImg = ((Image) ti.getBinderUIImage());
				if (null != binderImg) {
					binderImg.setUrl(getImages().busyAnimation().getSafeUri());
				}
				
				// ...and keep track of as being the one that we're
				// ...switching to.
				m_busyInfo.setBusyTI(ti);
			}
		}
	}
}
