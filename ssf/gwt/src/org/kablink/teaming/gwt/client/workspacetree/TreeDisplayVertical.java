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
package org.kablink.teaming.gwt.client.workspacetree;

import java.util.HashMap;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent.ManageMenuPopupCallback;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent.CollectionCallback;
import org.kablink.teaming.gwt.client.event.MenuLoadedEvent.MenuItem;
import org.kablink.teaming.gwt.client.event.HideManageMenuEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandVerticalBucketCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetRootWorkspaceIdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalActivityStreamsTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalNodeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LongRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.PersistNodeCollapseCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PersistNodeExpandCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UntrackBinderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UntrackPersonCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
 * Class used to drive the display of a vertical WorkspaceTreeControl,
 * typically used for Vibe's sidebar.
 * 
 * @author drfoster@novell.com
 */
public class TreeDisplayVertical extends TreeDisplayBase {
	private ActivityStreamInfo			m_selectedActivityStream;	// When displaying activity streams, the ActivityStream info of the selected activity stream.  null if no activity stream is currently selected.
	private BinderInfo					m_selectedBinderInfo;		// The currently selected binder.
	private BusyInfo					m_busyInfo;					// Stores a BusyInfo while we're busy switching contexts.
	private FlowPanel					m_rootPanel;				// The top level FlowPanel containing the tree's contents.
	private FlowPanel					m_selectorConfig;			//
	private HashMap<String, Integer>	m_renderDepths;				// A map of the depths the Binder's are are displayed at.
	private ManageMenuPopup				m_selectorConfigPopup;		//

	// The following are used for widget IDs assigned to various
	// objects in a running WorkspaceTreeControl.
	private final static String EXTENSION_ID_BASE					= "workspaceTreeBinder_";
	private final static String EXTENSION_ID_ACTIVITY_STREAM_BASE	= (EXTENSION_ID_BASE + "ActivityStream_");
	private final static String EXTENSION_ID_BUCKET_BASE			= (EXTENSION_ID_BASE + "Bucket_");
	private final static String EXTENSION_ID_COLLECTION_BASE		= (EXTENSION_ID_BASE + "Collection_");
	private final static String EXTENSION_ID_SELECTOR_ANCHOR		= "selectorAnchor_";
	private final static String EXTENSION_ID_SELECTOR_CONFIG		= "selectorConfig_";
	private final static String EXTENSION_ID_SELECTOR_BASE			= (EXTENSION_ID_BASE + "Selector_");
	private final static String EXTENSION_ID_SELECTOR_ID			= (EXTENSION_ID_BASE + "SelectorId");
	private final static String EXTENSION_ID_TRASH_BASE				= (EXTENSION_ID_BASE + "Trash_");
	private final static String EXTENSION_ID_TRASH_PERMALINK		= (EXTENSION_ID_BASE + "TrashPermalink");
	private final static String EXTENSION_ID_UNFOLLOW_TAIL			= "_Unfollow";

	// The following controls the grid size and nested offsets for the
	// WorkspaceTreeControl.
	private final static int SELECTOR_GRID_DEPTH_OFFSET	=  18;	// Based on empirical evidence.
	private final static int SELECTOR_GRID_WIDTH_ADJUST	=  22;	// Based on empirical evidence (expander image and spacing.)
	private final static int SELECTOR_GRID_WIDTH        = (GwtConstants.WORKSPACE_TREE_WIDTH - SELECTOR_GRID_WIDTH_ADJUST);

	// The following defines the maximum amount of time we wait to
	// process the completion event for a context switch.  If we exceed
	// this, we simply clear it.
	private final static int MAX_BUSY_DURATION	= 5000;	//	5 seconds.

	// The following provide the adjustments used to position the
	// binder configuration button properly over a selected binder.
	private final static int CONFIG_LEFT_ADJUST			= (-50);
	private final static int CONFIG_TOP_ADJUST_DIV		=    0;
	private final static int CONFIG_TOP_ADJUST_TABLE	=    6;
	
	// The following provide the adjustments used to position the
	// unfollow button properly over a selected followed item.
	private final static int UNFOLLOW_LEFT_ADJUST		= (-50);
	private final static int UNFOLLOW_TOP_ADJUST_DIV	=  (-3);
	private final static int UNFOLLOW_TOP_ADJUST_TABLE	=    3;
	
	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private class BinderExpander implements ClickHandler {
		private Image		m_expanderImg;	//
		private TreeInfo	m_ti;			//

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param expanderImg
		 */
		BinderExpander(TreeInfo ti, Image expanderImg) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			m_ti          = ti;
			m_expanderImg = expanderImg;
		}

		/*
		 * Asynchronously collapses the current row.
		 */
		private void doCollapseRowAsync() {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					doCollapseRowNow();
				}
			});
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
			rerenderRow(
				((FlexTable) m_ti.getRenderedGrid()),
				m_ti.getRenderedGridRow(),
				m_ti,
				true);	// true -> Re-render is because of a collapse.
			m_expanderImg.setResource(getImages().tree_opener());
			
			// ...reset the selector config position in case the
			// ...collapse caused it to move...
			repositionBinderConfig();
			
			// ...and tell everybody that it's been collapsed.
			GwtTeaming.fireEventAsync(
				new TreeNodeCollapsedEvent(
					getSelectedBinderInfo(),
					getTreeMode()));
		}

		/*
		 * Asynchronously expands the current row.
		 */
		private void doExpandRowAsync(final TreeInfo expandedTI) {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					doExpandRowNow(expandedTI);
				}
			});
		}
		
		/*
		 * Synchronously expands the current row.
		 */
		private void doExpandRowNow(TreeInfo expandedTI) {
			// Expand the row...
			m_ti.setBinderExpanded(true);
			m_ti.setChildBindersList(expandedTI.getChildBindersList());
			if (0 < m_ti.getBinderChildren()) {
				rerenderRow(
					((FlexTable) m_ti.getRenderedGrid()),
					m_ti.getRenderedGridRow(),
					m_ti,
					false);	// false -> Re-render is not because of a collapse.
			}
			m_expanderImg.setResource(getImages().tree_closer());
			
			// ...reset the selector config position in case the
			// ...expand caused it to move...
			repositionBinderConfig();
			
			// ...and tell everybody that it's been expanded.
			GwtTeaming.fireEventAsync(
				new TreeNodeExpandedEvent(
					getSelectedBinderInfo(),
					getTreeMode()));
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
							GwtClientHelper.deferCommand(new ScheduledCommand() {
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
							});
						}
					});
				}
			}
		}
	}
	
	/*
	 * Inner class used to handle mouse events for a Binder selector.  
	 */
	private class BinderSelectorMouseHandler implements MouseOverHandler, MouseOutHandler {
		private ActivityStream	m_as;					//
		private boolean			m_isBinderCollection;	//
		private FlowPanel		m_unfollow;				//
		private String			m_selectorGridId;		//
		private TreeInfo		m_ti;					//
		
		/**
		 * Class constructor.
		 * 
		 * @param selectorGridId
		 */
		BinderSelectorMouseHandler(String selectorGridId, TreeInfo ti) {
			// Initialize the super class...
			super();
			
			// ...store the parameters...
			m_selectorGridId = selectorGridId;
			m_ti             = ti;
			
			// ...and initialize everything else that requires it.
			m_isBinderCollection = ti.isBinderCollection();
			m_as                 = (ti.isActivityStream() ? ti.getActivityStreamInfo().getActivityStream() : ActivityStream.UNKNOWN);
		}

		/*
		 * Returns true if the position of the mouse, based on a
		 * MouseEvent, is over an unfollow widget and false otherwise.
		 */
		private boolean isMouseOverUnfollow(MouseEvent<?> me) {
			// Do we have an unfollow widget?
			boolean reply = (null != m_unfollow);
			if (reply) {
				// Yes!  Is the mouse over it?
				int mLeft    = me.getClientX();
				int mTop     = me.getClientY();
				int ufLeft   =  m_unfollow.getAbsoluteLeft();
				int ufRight  = (m_unfollow.getOffsetWidth() + ufLeft);
				int ufTop    =  m_unfollow.getAbsoluteTop();
				int ufBottom = (m_unfollow.getOffsetHeight() + ufTop);
				reply =
					((mLeft >= ufLeft) && (mLeft <= ufRight) &&	// Within left to right...
					 (mTop >= ufTop)   && (mTop  <= ufBottom));	// ...and top  to bottom.
			}
			
			// If we get here, reply is true if the mouse is over this
			// node's unfollow widget and false otherwise.  Return it.
			return reply;
		}
		
		/**
		 * Called when the mouse leaves a Binder selector.
		 * 
		 * @param me
		 */
		@Override
		public void onMouseOut(MouseOutEvent me) {
			// Is the mouse over this node's unfollow widget?
			if (!(isMouseOverUnfollow(me))) {
				// No!  Remove the hover styles...
				Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
				selectorPanel_New.removeClassName("workspaceTreeControlRowHover");
				if (m_isBinderCollection) {
					selectorPanel_New.removeClassName("workspaceTreeControlRowHover_collection");
				}
				
				// ...and if we have an unfollow widget...
				if (null != m_unfollow) {
					// ...hide it.
					m_unfollow.setVisible(false);
				}
			}
		}
		
		/**
		 * Called when the mouse enters a Binder selector.
		 * 
		 * @param me
		 */
		@Override
		public void onMouseOver(MouseOverEvent me) {
			// Add the hover styles.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.addClassName("workspaceTreeControlRowHover");
			if (m_isBinderCollection) {
				selectorPanel_New.addClassName("workspaceTreeControlRowHover_collection");
			}
			
			// Is this node a followed person or place activity stream?
			if (m_as.equals(ActivityStream.FOLLOWED_PERSON) || m_as.equals(ActivityStream.FOLLOWED_PLACE)) {
				// Yes!  Then we need to display a widget so the user
				// can unfollow it.  Have we already created the
				// unfollow widgets for this node?
				if (null != m_unfollow) {
					// Yes!  Simply position and show it.
					positionUnfollow(selectorPanel_New);
					m_unfollow.setVisible(true);
					return;
				}

				// If we get here, we haven't created the unfollow
				// widgets for this node yes!  Create the widgets to do
				// the unfollow...
				final Anchor  unfollowA  = new Anchor();
				final Element unfollowAE = unfollowA.getElement();
				unfollowA.setTitle(getMessages().treeStopFollowing());
				Image unfollowImg = GwtClientHelper.buildImage(getImages().unfollow());
				unfollowImg.addStyleName("workspaceTreeControlRow_unfollowImg");
				unfollowAE.appendChild(unfollowImg.getElement());
				unfollowA.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						VibeRpcCmd cmd = null;
						final Long unfollowId = Long.parseLong(m_ti.getActivityStreamInfo().getBinderIds()[0]);
						switch (m_as) {
						case FOLLOWED_PERSON:  cmd = new UntrackPersonCmd(null, unfollowId); break;
						case FOLLOWED_PLACE:   cmd = new UntrackBinderCmd(      unfollowId); break;
						}
						GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
							@Override
							public void onFailure(Throwable t) {
								String rpcError = null;
								switch (m_as) {
								case FOLLOWED_PERSON:  rpcError = GwtTeaming.getMessages().rpcFailure_UntrackingPerson(); break;
								case FOLLOWED_PLACE:   rpcError = GwtTeaming.getMessages().rpcFailure_UntrackingBinder(); break;
								}
								GwtClientHelper.handleGwtRPCFailure(t, rpcError, unfollowId);
							}
							
							@Override
							public void onSuccess(VibeRpcResponse response) {
								// Detach this node's unfollow widget
								// from the DOM and forget about it...
								m_rootPanel.remove(m_unfollow);
								m_unfollow = null;
								
								// ...remove the unfollowed item from
								// ...its parent TreeInfo...
								ActivityStream parentASI = null;
								switch (m_as) {
								case FOLLOWED_PERSON:  parentASI = ActivityStream.FOLLOWED_PEOPLE; break;
								case FOLLOWED_PLACE:   parentASI = ActivityStream.FOLLOWED_PLACES; break;
								}
								TreeInfo ti = TreeInfo.findFirstActivityStreamTI(getRootTreeInfo(), parentASI);
								List<TreeInfo> tiChildren = ti.getChildBindersList();
								tiChildren.remove(m_ti);
								ti.setBinderChildren(tiChildren.size());
								
								// ...and re-render the parent so
								// ...this item (which is no longer
								// ...being followed) goes away.
								rerenderRow(
									((FlexTable) ti.getRenderedGrid()),
									ti.getRenderedGridRow(),
									ti,
									false);	// false -> Re-render is not because of a collapse.
							}
						});
					}
				});
				m_unfollow = new FlowPanel();
				m_unfollow.addStyleName("workspaceTreeControlRow_unfollowPanel");
				m_unfollow.getElement().setId(m_selectorGridId + EXTENSION_ID_UNFOLLOW_TAIL);
				m_unfollow.add(unfollowA);
				
				// ...and show it.
				positionUnfollow(selectorPanel_New);
				m_rootPanel.add(m_unfollow);
			}
		}

		/*
		 * Positions the unfollow widget based on the current location
		 * of its selector panel Element.
		 */
		private void positionUnfollow(Element selectorPanel) {
			int configTopAdjust;
			if (selectorPanel.getTagName().equalsIgnoreCase("table"))
			     configTopAdjust = UNFOLLOW_TOP_ADJUST_TABLE;
			else configTopAdjust = UNFOLLOW_TOP_ADJUST_DIV;
			double top  = (((selectorPanel.getAbsoluteTop() - m_rootPanel.getAbsoluteTop()) + m_rootPanel.getElement().getScrollTop()) + configTopAdjust     );
			double left = (GwtConstants.SIDEBAR_TREE_WIDTH                                                                             + UNFOLLOW_LEFT_ADJUST);
			Element ufE = m_unfollow.getElement();
			ufE.getStyle().setTop( top,  Unit.PX);
			ufE.getStyle().setLeft(left, Unit.PX);
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
			
			// Setup a timer to wait for the busy state to clear.  If
			// we exceed the timeout, we simply clear the busy state.
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
				if (null != binderImg) {
					binderImg.setUrl(getImages().spacer_1px().getSafeUri());
				}
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
		m_renderDepths = new HashMap<String, Integer>();
	}

	/*
	 * Asynchronously builds and runs the selector configuration menu.
	 */
	private void buildAndRunSelectorConfigMenuAsync(final Anchor selectorConfigA) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				buildAndRunSelectorConfigMenuNow(selectorConfigA);
			}
		});
	}
	
	/*
	 * Synchronously builds and runs the selector configuration menu.
	 */
	private void buildAndRunSelectorConfigMenuNow(final Anchor selectorConfigA) {
		GwtTeaming.fireEvent(
			new GetManageMenuPopupEvent(new ManageMenuPopupCallback() {
				@Override
				public void manageMenuPopup(ManageMenuPopup mmp) {
					// Is there anything in the selector configuration
					// menu?
					m_selectorConfigPopup = mmp;
					if ((null == m_selectorConfigPopup) || (!(m_selectorConfigPopup.shouldShowMenu()))) {
						// No!  Clear the selector widget, tell the
						// user about the problem and bail.
						clearSelectorConfig();
						GwtClientHelper.deferredAlert(getMessages().treeErrorNoManageMenu());
					}
					
					else {
						// Yes, there's stuff in the selector
						// configuration menu!  Complete populating it
						// and run it.
						m_selectorConfigPopup.setCurrentBinder(m_selectedBinderInfo);
						m_selectorConfigPopup.populateMenu();
						runSelectorConfigMenuAsync(selectorConfigA);
					}
				}
			}));
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

	/*
	 * Clears an previous binder configuration panel and menu.
	 */
	private void clearSelectorConfig() {
		// Clear the previous binder configuration panel...
		if (null != m_selectorConfig) {
			m_selectorConfig.removeFromParent();
			m_selectorConfig.clear();
			m_selectorConfig = null;
		}
		
		// ...and menu.
		if (null != m_selectorConfigPopup) {
			m_selectorConfigPopup.clearItems();
			m_selectorConfigPopup = null;
		}
	}
	
	/**
	 * Called when activity stream mode is to be entered on the sidebar
	 * tree.
	 *
	 * Overrides TreeDisplayBase.enterActivityStreamMode().
	 * 
	 * @param defaultASI
	 * @param fromEnterEvent
	 */
	@Override
	public void enterActivityStreamMode(final ActivityStreamInfo defaultASI, final boolean fromEnterEvent) {		
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
			// No, we aren't in activity stream mode!  Clear any busy
			// spinner that might be displayed from a previous
			// context...
			clearBusySpinner();
			
			// ...build a TreeInfo for the activity streams...
			String[] binderIds = defaultASI.getBinderIds();
			String bId;
			if ((null != binderIds) && (0 < binderIds.length))
			     bId = binderIds[0];
			else bId = m_selectedBinderInfo.getBinderId();
			final String selectedBinderId = bId;
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
					enterActivityStreamModeAsync(ti, defaultASI, fromEnterEvent);
				}
			});
		}
	}
	
	/*
	 * Asynchronously loads an activity stream based TreeInfo into the
	 * sidebar.
	 */
	private void enterActivityStreamModeAsync(final TreeInfo asRootTI, final ActivityStreamInfo defaultASI, final boolean fromEnterEvent) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				enterActivityStreamModeNow(asRootTI, defaultASI, fromEnterEvent);
			}
		});
	}
	
	/*
	 * Synchronously loads an activity stream based TreeInfo into the
	 * sidebar.
	 */
	private void enterActivityStreamModeNow(TreeInfo asRootTI, ActivityStreamInfo defaultASI, boolean fromEnterEvent) {
		// Put the activity streams TreeInfo into affect...
		m_selectedActivityStream = defaultASI;
		setRootTreeInfo(asRootTI);
		m_rootPanel.clear();
		render(m_selectedBinderInfo, m_rootPanel);
		
		// ...and if we have a default activity stream to select...
		if (null != defaultASI) {
			// ...put it into affect.
			ActivityStreamEvent asEvent = new ActivityStreamEvent(defaultASI);
			asEvent.setFromEnterEvent(fromEnterEvent);
			GwtTeaming.fireEvent(asEvent);
		}

		// Finally, reset the menu so that it display what's
		// appropriate for activity stream mode.
		resetMenuContext();
		if (!(WorkspaceTreeControl.siteNavigationAvailable())) {
			GwtTeaming.fireEventAsync(new SidebarShowEvent());
		}
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
			// Yes!  Re-root the workspace tree control.
			m_selectedActivityStream = null;
			rerootTree(m_selectedBinderInfo, m_selectedBinderInfo, exitMode);
		}
	}

	/*
	 * Returns the selector grid Element for a given TreeInfo if that
	 * TreeInfo is configurable AND the selector grid can be found.
	 * 
	 * Otherwise, returns null;
	 */
	private static Element findConfigurableSelectorGrid(TreeInfo ti, String selectorGridId) {
		boolean cantConfigure = ti.isActivityStream() || ti.isBinderCollection() || ti.isBucket() || ti.isBinderTrash();
		if (cantConfigure) {
			return null;
		}
		
		Element selectorGrid = Document.get().getElementById(selectorGridId);
		if ((null == selectorGrid) && selectorGridId.startsWith(EXTENSION_ID_SELECTOR_ANCHOR)) {
			selectorGrid = Document.get().getElementById(selectorGridId.substring(EXTENSION_ID_SELECTOR_ANCHOR.length()));
		}
		return selectorGrid;
	}
	
	/**
	 * Called after a new context has been loaded.
	 * 
	 * Overrides TreeDisplayBase.clearBusySpinner().
	 */
	@Override
	public void clearBusySpinner() {
		// Are we tracking a BusyInfo indicating that we're in the
		// middle of a context switch?
		if (null != m_busyInfo) {
			// Yes!  Clear the busy state.
			m_busyInfo.clearBusy();
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
	 * Returns the current sidebar collection type via a callback.
	 * 
	 * Implementation of TreeDisplayBase.getCollectionCallback().
	 * 
	 * @param collectionCallback
	 */
	@Override
	public void getSidebarCollection(CollectionCallback collectionCallback) {
		// We only response to this request when we're not in Filr
		// mode.  When in Filr mode, the mast head will respond as it's
		// responsible for navigation.
		if (!(GwtClientHelper.isLicenseFilr())) {
			// Are we tracking a selected binder?
			TreeInfo selectedTI;
			if (null != m_selectedBinderInfo) {
				// Yes!  Find the matching TreeInfo.
				TreeInfo rootTI = getRootTreeInfo();
				if (m_selectedBinderInfo.isBinderCollection())
				     selectedTI = TreeInfo.findCollectionTI(rootTI, m_selectedBinderInfo.getCollectionType());
				else selectedTI = TreeInfo.findBinderTI(    rootTI, m_selectedBinderInfo.getBinderId());
			}
			else {
				// No, we aren't we tracking a selected binder!  No
				// matching TreeInfo.
				selectedTI = null;
			}
	
			// Return the collection type from the TreeInfo.
			CollectionType ct =
				((null == selectedTI)               ?
					CollectionType.NOT_A_COLLECTION :
					selectedTI.getBinderInfo().getCollectionType());
//			GwtClientHelper.debugAlert("TreeDisplayVertical.getSidebarCollection():  " + ct.name());		
			collectionCallback.collection(ct);
		}
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
			reply = ti.getBinderInfo().isEqual(m_selectedBinderInfo);
		}
		
		return reply;
	}

	/**
	 * Called when a particular menu item is loaded.  If an extender of
	 * this class is interested in these, it should overwrite this
	 * method.
	 * 
	 * Implements the TreeDisplayBase.menuLoaded() abstract method.
	 */
	@Override
	public void menuLoaded(MenuItem menuItem) {
		// If we're getting notified that the manage menu has been
		// loaded...
		if (MenuItem.MANAGE_BINDER.equals(menuItem)) {
			// ...simply null out the selector config popup.  That will
			// ...cause it to get recreated the next time it's needed
			// ...and pull over a new manage menu.
			m_selectorConfigPopup = null;
		}
	}

	/**
	 * Tells a sidebar tree implementation to refresh itself
	 * maintaining its current context and selected binder.
	 * 
	 * Implements the TreeDisplayBase.rerootSidebarTree() method.
	 */
	@Override
	public void refreshSidebarTree() {
		// Simply refresh the tree.
		refreshTree(m_selectedBinderInfo);
	}

	/*
	 * Forces the tree to refresh regardless of the circumstances.
	 */
	private void refreshTree(BinderInfo selectedBinderInfo) {
		// To refresh it, simply re-root it at the same point that it's
		// currently rooted, reselecting the previously selected binder.
		BinderInfo rootBinderInfo;
		if (GwtClientHelper.getRequestInfo().isRerootSidebarTree())
		     rootBinderInfo = selectedBinderInfo;
		else rootBinderInfo = getRootTreeInfo().getBinderInfo();
		rerootTree(rootBinderInfo, selectedBinderInfo, ExitMode.SIMPLE_EXIT);
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
		rerootTree(m_selectedBinderInfo, m_selectedBinderInfo, ExitMode.SIMPLE_EXIT);
	}

	/*
	 * Re-roots the WorkspaceTreeControl to a new Binder and optionally
	 * selects a binder.
	 */
	private void rerootTree(final BinderInfo newRootBinderInfo, final BinderInfo selectedBinderInfo, final ExitMode exitingActivityStreamMode) {
		// Clear any pending sidebar flags.  Re-rooting will take care
		// of any refresh/re-root request.
		clearSidebarFlags();
		
		// Read the TreeInfo for the selected Binder.
		GetVerticalTreeCmd cmd = new GetVerticalTreeCmd(newRootBinderInfo.getBinderId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTree(),
					newRootBinderInfo.getBinderId());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response)  {
				// Re-root the tree asynchronously so that we release
				// the AJAX request ASAP.
				TreeInfo rootTI = ((TreeInfo) response.getResponseData());
				rerootTreeAsync(
					newRootBinderInfo,
					selectedBinderInfo,
					exitingActivityStreamMode,
					rootTI);
			}
		});
	}
	
	private void rerootTree(final BinderInfo newRootBinderInfo) {
		// Always use the initial form of the method.
		rerootTree(newRootBinderInfo, null, ExitMode.SIMPLE_EXIT);
	}
	
	/*
	 * Asynchronously re-roots the WorkspaceTreeControl to a new Binder
	 * and optionally selects a binder.
	 */
	private void rerootTreeAsync(final BinderInfo newRootBinderInfo, final BinderInfo selectedBinderInfo, final ExitMode exitingActivityStreamMode, final TreeInfo rootTI) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				rerootTreeNow(
					newRootBinderInfo,
					selectedBinderInfo,
					exitingActivityStreamMode,
					rootTI);
			}
		});
	}
	
	/*
	 * Synchronously re-roots the WorkspaceTreeControl to a new Binder
	 * and optionally selects a binder.
	 */
	private void rerootTreeNow(BinderInfo newRootBinderInfo, BinderInfo selectedBinderInfo, ExitMode exitingActivityStreamMode, TreeInfo rootTI) {
		// Update the display with the TreeInfo.
		setRootTreeInfo(rootTI);
		clearSelectorConfig();
		m_rootPanel.clear();
		render(newRootBinderInfo, m_rootPanel);
		
		// If we weren't given a binder to select...
		if (null == selectedBinderInfo) {
			// ...select the one we're rooting to...
			selectedBinderInfo = newRootBinderInfo;
		}
		if (null != selectedBinderInfo) {
			// ...and if we can find that binder...
			TreeInfo selectedBinderTI = TreeInfo.findBinderTI(rootTI, selectedBinderInfo.getBinderId());
			if (null != selectedBinderTI) {
				// ...select it.
				selectBinder(selectedBinderTI);
			}
		}
		
		// If we re-rooted the tree to exit activity stream
		// mode...
		if (ExitMode.SIMPLE_EXIT == exitingActivityStreamMode) {
			// ...reset the menu so that it displays what's
			// ...appropriate for navigation mode.
			resetMenuContext();
		}
	}

	/**
	 * Called to render the information in this TreeInfo object into a
	 * FlowPanel.
	 * 
	 * Implementation of TreeDisplayBase.render().
	 *
	 * @param selectedBinderInfo
	 * @param targetPanel
	 */
	@Override
	public void render(BinderInfo selectedBinderInfo, FlowPanel targetPanel) {
		// Track the Binder that's to be initially selected.
		m_rootPanel = targetPanel;
		setSelectedBinderInfo(selectedBinderInfo);
		
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

		List<TreeInfo>	tiList         = rootTI.getCollectionsList();
		boolean			hasCollections = ((null != tiList) && (!(tiList.isEmpty())));
		
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

		// Is site navigation available or are we in an activity
		// stream?
		if (WorkspaceTreeControl.siteNavigationAvailable() || isAS) {
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
			// No!  Display some text saying we know what we're doing.
			Label l = new Label(getMessages().treeIntentionallyLeftBlank());
			l.addStyleName("workspaceTreeControl_blankContent");
			grid.setWidget(0, 0, l);
			
			// Is the tree visible?
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
	private void renderRow(FlexTable grid, int row, TreeInfo ti, int renderDepth, boolean rerenderToCollapse) {
		// Store the grid and row where we're rendering this TreeInfo.
		ti.setRenderedGrid(grid, row);
		
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
			expanderA.addClickHandler(new BinderExpander(ti, expanderImg));
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
		String selectorId = getSelectorId(ti);
		int width = (SELECTOR_GRID_WIDTH - (SELECTOR_GRID_DEPTH_OFFSET * renderDepth));
		if (ti.getBinderIconWidth(BinderIconSize.getSidebarTreeIconSize()) > width) {
			width = SELECTOR_GRID_WIDTH;
		}
		selectorGrid.setWidth(width + "px");
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorGrid.getElement());
		selectorA.addClickHandler(new BinderSelector(ti));
		selectorA.setWidth("100%");
		String selectorGridId = (EXTENSION_ID_SELECTOR_ANCHOR + selectorId);
		selectorGrid.getElement().setId(selectorGridId);
		selectorGrid.addStyleName(buildElementStyle(ti, "workspaceTreeControlRow"));
		if (ti.isBinderCollection()) {
			String paddingStyle;
			if (WorkspaceTreeControl.siteNavigationAvailable())
			     paddingStyle = "padding3b";
			else paddingStyle = "padding5b";
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
		BinderSelectorMouseHandler bsmh = new BinderSelectorMouseHandler(selectorGridId, ti);
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
			if ((!rerenderToCollapse) && shouldBinderBeExpanded(ti)) {
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
	 * Does what's necessary to ensure the binder config selector
	 * is still positioned correctly.
	 * 
	 * This is typically necessary when a an entry containing the one
	 * with the configuration is expanded or collapsed or when one
	 * above the one with the configuration is expanded or collapsed.
	 */
	private void repositionBinderConfig() {
		// Clear the existing configuration item...
		clearSelectorConfig();
		
		// ...and if nothing is selected...
		if (null == m_selectedBinderInfo) {
			// ...bail.
			return;
		}
		
		// ...otherwise, find the selected configuration item...
		TreeInfo rootTI = getRootTreeInfo();
		TreeInfo selectedTI;
		if (m_selectedBinderInfo.isBinderCollection())
		     selectedTI = TreeInfo.findCollectionTI(rootTI, m_selectedBinderInfo.getCollectionType());
		else selectedTI = TreeInfo.findBinderTI(    rootTI, m_selectedBinderInfo.getBinderId());
		
		// ...and show the configuration menu on it.
		showBinderConfig(selectedTI, getSelectorId(selectedTI));
	}
	
	/*
	 * Clears and re-renders a TreeInfo object into a FlexTable row.
	 */
	private void rerenderRow(FlexTable grid, int row, TreeInfo ti, boolean rerenderToCollapse) {
		clearRow(grid, row);
		renderRow(grid, row, ti, getRenderDepth(ti), rerenderToCollapse);
	}

	/*
	 * Asynchronously runs the selector configuration menu.
	 */
	private void runSelectorConfigMenuAsync(final Anchor selectorConfigA) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				runSelectorConfigMenuNow(selectorConfigA);
			}
		});
	}
	
	/*
	 * Synchronously runs the selector configuration menu.
	 */
	private void runSelectorConfigMenuNow(final Anchor selectorConfigA) {
		final PopupMenu configureDropdownMenu = new PopupMenu(true, false, false);
		configureDropdownMenu.addStyleName("vibe-configureMenuBarDropDown");
		configureDropdownMenu.setMenu(m_selectorConfigPopup.getMenuBar());
		configureDropdownMenu.showRelativeToTarget(selectorConfigA);
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
			 setActivityStreamImpl(  asi       );
		else enterActivityStreamMode(asi, false);
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
		// Should we skip the selection?  We skip it under the
		// following conditions:
		// 1. No TreeInfo or a trash TreeInfo is being selected;
		// 2. Site navigation is not available; and
		// 3. We're selecting other than a collection or activity
		//    stream.
		boolean siteNavigationAvailable = WorkspaceTreeControl.siteNavigationAvailable();
		boolean skipSelection           = ((null == ti) || ti.isBinderTrash());
		if ((!skipSelection) && (!siteNavigationAvailable)) {
			skipSelection = ((!(ti.isBinderCollection())) && (!(ti.isActivityStream())));
		}
		if (skipSelection) {
			// Yes!  Is site navigation available?
			if (!siteNavigationAvailable) {
				// No!  Set/clear the selector configuration menu, as
				// appropriate.
				if ((null != ti) && ti.getBinderInfo().isEqual(getRootTreeInfo().getBinderInfo()))
				     selectRootConfig();
				else clearSelectorConfig();
			}
		}
		
		else {
			// Yes, we need to select this item!  Mark it as having
			// been selected...
			if (!(ti.isActivityStream())) {
				setSelectedBinderInfo(ti.getBinderInfo());
			}
			String selectedId_New = getSelectorId(ti);
			Element selectorLabel_New = Document.get().getElementById(selectedId_New);
			selectorLabel_New.addClassName(buildElementStyle(ti, "workspaceTreeBinderSelected"));
			String selectedPanelId_New = (EXTENSION_ID_SELECTOR_ANCHOR + selectedId_New);
			Element selectorPanel_New = Document.get().getElementById(selectedPanelId_New);
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

				String selectedPanelId_Old = (EXTENSION_ID_SELECTOR_ANCHOR + selectedId_Old);
				Element selectorPanel_Old = Document.get().getElementById(selectedPanelId_Old);
				if (null != selectorPanel_Old) {
					selectorPanel_Old.removeClassName("workspaceTreeControlRowSelected"           );
					selectorPanel_Old.removeClassName("workspaceTreeControlRowSelected_collection");
				}
			}
			
			// ...ensure the newly selected item is scrolled into
			// ...view...
			if (null != selectorPanel_New) {
				selectorPanel_New.scrollIntoView();
			}
			
			// ...and store the new ID as having been selected.
			selectorId.setAttribute("value", selectedId_New);
			showBinderConfig(ti, selectedId_New);
		}
	}

	/*
	 * Shows the selector configuration menu on the root TreeInfo.
	 */
	private void selectRootConfig() {
		// Show the selector configuration widget on the root.
		TreeInfo rootTI = getRootTreeInfo();
		showBinderConfig(rootTI, getSelectorId(rootTI));
	}

	/**
	 * Sets the initial context to use for the tree.
	 * 
	 * Implementation of TreeDisplayBase.setRenderContext().
	 * 
	 * @param selectedBinderInfo
	 * @param targetPanel
	 */
	@Override
	public void setRenderContext(BinderInfo selectedBinderInfo, FlowPanel targetPanel) {
		// Simply store the parameter in their appropriate data
		// members.
		m_selectedBinderInfo = selectedBinderInfo;
		m_rootPanel          = targetPanel;
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
	public void setSelectedBinder(OnSelectBinderInfo osbInfo) {
		// If the selection is for a collection...
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		if (osbInfo.isCollection()) {
			// ...select it.
			TreeInfo collectionTI = TreeInfo.findCollectionTI(
				getRootTreeInfo(),
				osbInfo.getBinderInfo().getCollectionType());
			
			if (null != collectionTI) {
				selectBinder(collectionTI);
				if (!(ri.isRefreshSidebarTree())) {
					return;
				}
			}
		}
		
		// Otherwise, if we are in a mode where site navigation is not
		// available...
		else if (!(WorkspaceTreeControl.siteNavigationAvailable())) {
			// ...select the binder.
			selectBinder(
				TreeInfo.findBinderTI(
					getRootTreeInfo(),
					osbInfo.getBinderInfo().getBinderId()));
			
			return;
		}
		
		// If the selection is for a Binder's trash...
		if (osbInfo.isTrash()) {
			// ...we don't change anything.
			return;
		}

		// Is the requested Binder available in those we've already
		// got loaded?
		Instigator		 instigator = osbInfo.getInstigator();
		final BinderInfo binderInfo = osbInfo.getBinderInfo();
		final TreeInfo	 targetTI   = TreeInfo.findBinderTI(getRootTreeInfo(), binderInfo.getBinderId());
		if (null != targetTI) {
			// Yes!  Should the request cause the tree to be refreshed?
			setSelectedBinderInfo(binderInfo);
			final boolean forceRefresh =
				(ri.isRefreshSidebarTree() ||
				 ri.isRerootSidebarTree());
			
			switch (instigator) {
			case SIDEBAR_TREE_SELECT:
				if (forceRefresh)
					 refreshTree( binderInfo);
				else selectBinder(targetTI  );
				break;

			default:
				// Yes, the request should cause the tree to be
				// refreshed!  (It may be coming from the bread crumbs
				// or some other unknown source.)  What's the ID if the
				// selected Binder's root workspace?
				GetRootWorkspaceIdCmd cmd = new GetRootWorkspaceIdCmd(getRootTreeInfo().getBinderInfo().getBinderIdAsLong(), binderInfo.getBinderIdAsLong());
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetRootWorkspaceId(),
							binderInfo.getBinderId());
						selectBinder(targetTI);
					}
					
					@Override
					public void onSuccess(VibeRpcResponse response)  {
						// Asynchronously perform the selection so that
						// we release the AJAX request ASAP.
						LongRpcResponseData responseData = ((LongRpcResponseData) response.getResponseData());
						selectRootWorkspaceIdAsync(
							binderInfo,
							forceRefresh,
							targetTI,
							responseData.getValue());
					}
				});
				break;
			}
		}
		
		else {
			// No, the requested Binder isn't available in those we've
			// already got loaded!  Re-root the tree at the selected
			// Binder...
			rerootTree(binderInfo);
		}
	}

	/*
	 * Asynchronously selects a binder and/or re-roots the tree.
	 */
	private void selectRootWorkspaceIdAsync(final BinderInfo binderInfo, final boolean forceRefresh, final TreeInfo targetTI, final Long rootWorkspaceId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				selectRootWorkspaceIdNow(binderInfo, forceRefresh, targetTI, rootWorkspaceId);
			}
		});
	}
	
	/*
	 * Synchronously selects a binder and/or re-roots the tree.
	 */
	private void selectRootWorkspaceIdNow(BinderInfo binderInfo, boolean forceRefresh, TreeInfo targetTI, Long rootWorkspaceId) {
		// If the selected Binder's workspace is different from the
		// Binder we're currently rooted to, we need to re-root the
		// tree.  Do we need to re-root?
		if (rootWorkspaceId.equals(getRootTreeInfo().getBinderInfo().getBinderIdAsLong())) {
			// No!  Simply select the Binder.
			if (forceRefresh)
				 refreshTree( m_selectedBinderInfo);
			else selectBinder(targetTI            );
		}
		else {
			// Yes, we need to re-root!
			rerootTree(binderInfo);
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
	private void setSelectedBinderInfo(BinderInfo selectedBinderInfo) {
		m_selectedBinderInfo = selectedBinderInfo;
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
			case MY_FILE:
			case MY_TEAM:
			case NET_FOLDER:
			case SHARED_BY_ME_FOLDER:
			case SHARED_WITH_ME_FOLDER:
			case SHARED_PUBLIC_FOLDER:
				// Yes!  Are we looking at it's parent binder?
				ActivityStream parentAS;
				switch (as) {
				default:                     parentAS = ActivityStream.UNKNOWN;         break;
				case FOLLOWED_PERSON:        parentAS = ActivityStream.FOLLOWED_PEOPLE; break;
				case FOLLOWED_PLACE:         parentAS = ActivityStream.FOLLOWED_PLACES; break;
				case MY_FAVORITE:            parentAS = ActivityStream.MY_FAVORITES;    break;
				case MY_FILE:                parentAS = ActivityStream.MY_FILES;        break;
				case MY_TEAM:                parentAS = ActivityStream.MY_TEAMS;        break;
				case NET_FOLDER:             parentAS = ActivityStream.NET_FOLDERS;     break;
				case SHARED_BY_ME_FOLDER:    parentAS = ActivityStream.SHARED_BY_ME;    break;
				case SHARED_WITH_ME_FOLDER:  parentAS = ActivityStream.SHARED_WITH_ME;  break;
				case SHARED_PUBLIC_FOLDER:   parentAS = ActivityStream.SHARED_PUBLIC;   break;
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
					ti = TreeInfo.findCollectionTI(rootTI, osbInfo.getBinderInfo().getCollectionType());
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
				if (null != binderImg) {
					binderImg.setUrl(getImages().busyAnimation_small().getSafeUri());
				}
			}
			else {
				// Yes!  Set the busy animation image for this
				// TreeInfo's binder...
				ImageResource busyAnimation;
				switch (BinderIconSize.getSidebarTreeIconSize()) {
				default:
				case SMALL:   busyAnimation = getImages().busyAnimation();        break;
				case MEDIUM:  busyAnimation = getImages().busyAnimation_medium(); break;
				case LARGE:   busyAnimation = getImages().busyAnimation_large();  break;
				}
				binderImg = ((Image) ti.getBinderUIImage());
				if (null != binderImg) {
					binderImg.setUrl(busyAnimation.getSafeUri());
				}
				
				// ...and keep track of as being the one that we're
				// ...switching to.
				m_busyInfo.setBusyTI(ti);
			}
		}
	}

	/*
	 * Hides/shows the binder configuration widgets for binder
	 * selection.
	 */
	private void showBinderConfig(final TreeInfo selectedTI, final String selectedId) {
		// Are we already tracking a configuration panel?
		String selectorConfigId = (EXTENSION_ID_SELECTOR_CONFIG + selectedId);
		if (null != m_selectorConfig) {
			// Yes!  Is it for this same item?
			if (selectorConfigId.equals(m_selectorConfig.getElement().getId())) {
				// Yes!  Then we don't do anything.  (This will happen
				// if the user simply clicks the selected node in the
				// sidebar.)
				HideManageMenuEvent.fireOneAsync();
				return;
			}
			
			// Clear the previous binder configuration panel...
			clearSelectorConfig();
		}
		
		// Can we find the selector grid for a configurable binder?
		Element selectorGrid = findConfigurableSelectorGrid(selectedTI, (EXTENSION_ID_SELECTOR_ANCHOR + selectedId));
		if (null == selectorGrid) {
			// No!  Then we don't show a binder configuration widget.
			return;
		}

		// Create an anchor to run the configuration menu on this
		// binder.
		final Anchor  selectorConfigA  = new Anchor();
		final Element selectorConfigAE = selectorConfigA.getElement();
		selectorConfigA.setTitle(selectedTI.getBinderInfo().isBinderFolder() ? getMessages().treeAltConfigureFolder() : getMessages().treeAltConfigureWorkspace());
		Image selectorConfigImg = GwtClientHelper.buildImage(getImages().configOptions());
		selectorConfigImg.addStyleName("workspaceTreeControlRow_configureImg");
		selectorConfigAE.appendChild(selectorConfigImg.getElement());
		selectorConfigA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (null == m_selectorConfigPopup)
				     buildAndRunSelectorConfigMenuAsync(selectorConfigA);
				else runSelectorConfigMenuAsync(        selectorConfigA);
			}
		});
		
		// Create a panel to hold the configuration button...
		m_selectorConfig = new FlowPanel();
		m_selectorConfig.addStyleName("workspaceTreeControlRow_configurePanel");
		m_selectorConfig.getElement().setId(selectorConfigId);
		m_selectorConfig.add(selectorConfigA);
		
		// ...show it...
		int configTopAdjust;
		if (selectorGrid.getTagName().equalsIgnoreCase("table"))
		     configTopAdjust = CONFIG_TOP_ADJUST_TABLE;
		else configTopAdjust = CONFIG_TOP_ADJUST_DIV;
		double top  = (((selectorGrid.getAbsoluteTop() - m_rootPanel.getAbsoluteTop()) + m_rootPanel.getElement().getScrollTop()) + configTopAdjust   );
		double left = (GwtConstants.SIDEBAR_TREE_WIDTH                                                                            + CONFIG_LEFT_ADJUST);
		m_selectorConfig.getElement().getStyle().setTop( top,  Unit.PX);
		m_selectorConfig.getElement().getStyle().setLeft(left, Unit.PX);
		m_rootPanel.add(m_selectorConfig);
		
		// ...and hide the manage menu in the main menu bar.
		HideManageMenuEvent.fireOneAsync();
	}
}
