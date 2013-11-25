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
package org.kablink.teaming.gwt.client.workspacetree;

import java.util.HashMap;
import java.util.Iterator;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
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
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's sidebar.
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeDisplayVertical extends TreeDisplayBase {
	private ActivityStreamInfo		m_selectedActivityStream;	// When displaying activity streams, the ActivityStream info of the selected activity stream.  null if no activity stream is currently selected.
	private BusyInfo				m_busyInfo;					// Stores a BusyInfo while we're busy switching contexts.
	private FlowPanel				m_rootPanel;				// The top level FlowPanel containing the sidebar tree's contents.
	private HashMap<String,Integer> m_renderDepths;				// A map of the depths the Binder's are are displayed at.
	private long 					m_selectedBinderId;			// The ID of the currently selected binder.

	// The follow controls the height and width of the images displayed
	// by this object.
	private final static int BINDER_HEIGHT_INT = 16; private final static int AS_BINDER_HEIGHT_INT = 1;
	private final static int BINDER_WIDTH_INT  = 16; private final static int AS_BINDER_WIDTH_INT  = 1; 

	// The following are used for widget IDs assigned to various
	// objects in a running WorkspaceTreeControl.
	private final static String EXTENSION_ID_BASE					= "workspaceTreeBinder_";
	private final static String EXTENSION_ID_ACTIVITY_STREAM_BASE	= (EXTENSION_ID_BASE + "ActivityStream_");
	private final static String EXTENSION_ID_BUCKET_BASE			= (EXTENSION_ID_BASE + "Bucket_");
	private final static String EXTENSION_ID_SELECTOR_ANCHOR		= "selectorAnchor_";
	private final static String EXTENSION_ID_SELECTOR_BASE			= (EXTENSION_ID_BASE + "Selector_");
	private final static String EXTENSION_ID_SELECTOR_ID			= (EXTENSION_ID_BASE + "SelectorId");
	private final static String EXTENSION_ID_TRASH_BASE				= (EXTENSION_ID_BASE + "Trash_");
	private final static String EXTENSION_ID_TRASH_PERMALINK		= (EXTENSION_ID_BASE + "TrashPermalink");

	// The following controls the grid size and nested offsets for the
	// WorkspaceTreeControl.
	private final static int SELECTOR_GRID_DEPTH_OFFSET	=  18;	// Based on empirical evidence.
	private final static int SELECTOR_GRID_WIDTH        = 208;	// Based on the width of 230 in the workspaceTreeControl style
	
	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private class BinderExpander implements ClickHandler {
		private Grid m_grid;
		private Image m_expanderImg;
		private int m_gridRow;
		private TreeInfo m_ti;

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param grid
		 * @param gridRow
		 * @param expanderImg
		 */
		BinderExpander(TreeInfo ti, Grid grid, int gridRow, Image expanderImg) {
			// Simply store the parameters.
			m_ti = ti;
			m_grid = grid;
			m_gridRow = gridRow;
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
			if (!m_ti.isActivityStream()) {
				m_ti.clearChildBindersList();
			}
			m_ti.setBinderExpanded(false);
			reRenderRow(m_grid, m_gridRow, m_ti, true);
			m_expanderImg.setResource(getImages().tree_opener());
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
			m_ti.setBinderExpanded(true);
			m_ti.setChildBindersList(expandedTI.getChildBindersList());
			if (0 < m_ti.getBinderChildren()) {
				reRenderRow(m_grid, m_gridRow, m_ti, false);
			}
			m_expanderImg.setResource(getImages().tree_closer());
		}
		
		/**
		 * Called when the expander is clicked.
		 * 
		 * @param event
		 */
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
					PersistNodeCollapseCmd cmd;
					
					// No, we aren't showing an expanded bucket or
					// activity stream!  We must be showing a normal
					// row.  Can we mark the row as being closed?
					cmd = new PersistNodeCollapseCmd( m_ti.getBinderInfo().getBinderId() );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_PersistExpansionState(),
								m_ti.getBinderInfo().getBinderId());
						}
						
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
					ExpandVerticalBucketCmd cmd;
					
					// Yes!  Expand it.
					cmd = new ExpandVerticalBucketCmd( m_ti.getBucketInfo() );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_ExpandBucket());
						}
						
						public void onSuccess(VibeRpcResponse response) {
							TreeInfo expandedTI;
							
							// Yes!  Update the TreeInfo, and if
							// there are any expanded rows, render
							// them and change the row's Anchor
							// Image to a tree_closer.
							expandedTI = (TreeInfo) response.getResponseData();
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
					PersistNodeExpandCmd cmd = new PersistNodeExpandCmd( m_ti.getBinderInfo().getBinderId() );
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_PersistExpansionState(),
								m_ti.getBinderInfo().getBinderId());
						}
						
						public void onSuccess(VibeRpcResponse response) {
							// Run the 'Get Vertical Node' RPC request
							// as a scheduled command so the RPC
							// request that got us here can be
							// terminated.
							ScheduledCommand getVNode = new ScheduledCommand() {
								@Override
								public void execute() {
									GetVerticalNodeCmd cmd;
									
									// Can we get a TreeInfo for the
									// expansion?
									cmd = new GetVerticalNodeCmd( m_ti.getBinderInfo().getBinderId() );
									GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
										public void onFailure(Throwable t) {
											GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetTree(),
												m_ti.getBinderInfo().getBinderId());
										}
										
										public void onSuccess(VibeRpcResponse response) {
											TreeInfo expandedTI;
											
											// Yes!  Update the
											// TreeInfo, and if there
											// are any expanded rows,
											// render them and change
											// the row's Anchor Image
											// to a tree_closer.
											expandedTI = (TreeInfo) response.getResponseData();
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
		private String m_selectorGridId;
		
		/**
		 * Class constructor.
		 * 
		 * @param selectorGridId
		 */
		BinderSelectorMouseHandler(String selectorGridId) {
			// Simply store the parameters.
			m_selectorGridId = selectorGridId;
		}
		
		/**
		 * Called when the mouse leaves a Binder selector.
		 * 
		 * @param me
		 */
		public void onMouseOut(MouseOutEvent me) {
			// Simply remove the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.removeClassName("workspaceTreeControlRowHover");
		}
		
		/**
		 * Called when the mouse enters a Binder selector.
		 * 
		 * @param me
		 */
		public void onMouseOver(MouseOverEvent me) {
			// Simply add the hover style.
			Element selectorPanel_New = Document.get().getElementById(m_selectorGridId);
			selectorPanel_New.addClassName("workspaceTreeControlRowHover");
		}
	}

	/*
	 * Inner class used to track information about the sidebar tree
	 * being in a busy state.
	 */
	private static class BusyInfo {
		private TreeInfo m_busyTI;	// TreeInfo running a busy animation, if there is one.  May be null.

		/**
		 * Class constructor.
		 */
		public BusyInfo() {
			// Nothing to do.
		}
		
		/**
		 * Returns any TreeInfo associated with this BusyInfo object.
		 * 
		 * @return
		 */
		public TreeInfo getBusyTI() {
			return m_busyTI;
		}

		/**
		 * Returns true if this BusyInfo is tracking a TreeInfo and
		 * false otherwise.
		 * 
		 * @return
		 */
		public boolean hasBusyTI() {
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
	 * Constructor method.
	 * 
	 * @param wsTree
	 * @param rootTI
	 */
	public TreeDisplayVertical(WorkspaceTreeControl wsTree, TreeInfo rootTI) {
		// Construct the super class...
		super(wsTree, rootTI);
		
		// ...and initialize everything else.
		m_selectedBinderId = (-1);
		m_renderDepths = new HashMap<String,Integer>();
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
			public void onClick(ClickEvent event) {
				GwtTeaming.fireEvent(new ActivityStreamExitEvent(ExitMode.SIMPLE_EXIT));
			}
		});
		
		// ...and tie it all together.
		closePBAnchor.getElement().appendChild(closeLabel.getElement());
		return closePBAnchor;
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
	OnSelectBinderInfo buildOnSelectBinderInfo(TreeInfo ti) {
		// Construct an OnSelectBinderInfo for this TreeInfo object.
		OnSelectBinderInfo reply = new OnSelectBinderInfo(ti, Instigator.SIDEBAR_TREE_SELECT);
		
		// Is this TreeInfo object the trash Binder?
		if (ti.getBinderInfo().isBinderTrash()) {
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
			GetVerticalActivityStreamsTreeCmd cmd;
			
			cmd = new GetVerticalActivityStreamsTreeCmd( selectedBinderId );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure( t, GwtTeaming.getMessages().rpcFailure_GetActivityStreamsTree(), selectedBinderId);
				}

				public void onSuccess(VibeRpcResponse response) {
					TreeInfo ti;
					
					// ...and put it into effect.
					ti = (TreeInfo) response.getResponseData();
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
			reRootTree(String.valueOf(m_selectedBinderId), m_selectedBinderId, exitMode);
		}
	}
	
	/*
	 * Removes the widgets from a Grid row.
	 */
	private static void clearRow(Grid grid, int row) {
		grid.remove(grid.getWidget(row, 0));
		grid.remove(grid.getWidget(row, 1));
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
			// Yes!  Does it contain a TreeInfo?
			if (m_busyInfo.hasBusyTI()) {
				// Yes!  Restore its default image.
				setBinderImageResource(m_busyInfo.getBusyTI());
			}
			
			// ...and forget about it.
			m_busyInfo = null;
		}
	}

	/*
	 * Returns various height and widths to use from binder images.
	 */
	private int    getBinderImgHeightInt(TreeInfo ti) {return (ti.isActivityStream() ? AS_BINDER_HEIGHT_INT : BINDER_HEIGHT_INT);}
	private String getBinderImgHeight(   TreeInfo ti) {return (getBinderImgHeightInt(ti) + "px");}
	private int    getBinderImgWidthInt( TreeInfo ti) {return (ti.isActivityStream() ? AS_BINDER_WIDTH_INT : BINDER_WIDTH_INT);}
	private String getBinderImgWidth(    TreeInfo ti) {return (getBinderImgWidthInt(ti) + "px");}
	
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
		if      (ti.isBucket())                      idBase = EXTENSION_ID_BUCKET_BASE;
		else if (ti.isActivityStream())              idBase = EXTENSION_ID_ACTIVITY_STREAM_BASE;
		else if (ti.getBinderInfo().isBinderTrash()) idBase = EXTENSION_ID_TRASH_BASE;
		else                                         idBase = EXTENSION_ID_SELECTOR_BASE;
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
			selectorPanel.addStyleName("workspaceTreeBinderAnchor gwtUI_nowrap");
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
			selectorLabel.addStyleName("workspaceTreeBinderAnchor");
			if (boldIt) {
				selectorLabel.addStyleName("bold");
			}
			selectorLabel.getElement().setId(getSelectorId(ti));
			if (!(ti.getBinderInfo().isBinderTrash())) {
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
			reply = (Long.parseLong(ti.getBinderInfo().getBinderId()) == m_selectedBinderId);
		}
		
		return reply;
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
		Grid selectorGrid = new Grid(1, (isAS ? 3 : 2));
		String styles = "workspaceTreeControlHeader workspaceTreeControlHeader_base ";
		if (isAS)
		     styles += "workspaceTreeControlHeader_as";
		else styles += "workspaceTreeControlHeader_nav";
		selectorGrid.addStyleName(styles);
		selectorGrid.setCellSpacing(0);
		selectorGrid.setCellPadding(0);
		String rootTitle = rootTI.getBinderTitle();
		if (GwtClientHelper.hasString(rootTitle)) {
			Label selectorLabel = new Label(rootTitle);
			selectorLabel.setWordWrap(false);
			selectorLabel.getElement().setId(getSelectorId(rootTI));
			selectorLabel.getElement().setAttribute(EXTENSION_ID_TRASH_PERMALINK, rootTI.getBinderTrashPermalink());
			selectorGrid.setWidget(0, 0, selectorLabel);
			selectorGrid.setWidget(0, 1, new Label("\u00A0"));
			selectorGrid.getCellFormatter().setWidth(0, 1, "100%");
			Widget rootWidget;
			if (isAS) {
				selectorGrid.setWidget(0, 2, buildCloseActivityStreamsPB());
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

		// ...its content panel...
		Grid grid = new Grid();
		grid.setCellSpacing(0);
		grid.setCellPadding(0);
		grid.resizeColumns(2);
		grid.addStyleName("workspaceTreeControlBody");
		m_rootPanel.add(grid);
		
		// ...and if there are any rows to display...
		for (Iterator<TreeInfo> tii = rootTI.getChildBindersList().iterator(); tii.hasNext(); ) {
			// ...render them.
			int row = grid.getRowCount();
			grid.insertRow(row);
			renderRow(grid, row, tii.next(), 0);
		}
	}
	
	/*
	 * Called to render an individual row in the WorkspaceTree control.
	 */
	private void renderRow(Grid grid, int row, TreeInfo ti, int renderDepth, boolean reRenderToCollapse) {
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
			expanderImg = new Image(expanderImgRes);
			expanderImg.addStyleName("workspaceTreeBinderExpanderImg");
			Anchor expanderA = new Anchor();
			expanderA.getElement().appendChild(expanderImg.getElement());
			expanderA.addClickHandler(new BinderExpander(ti, grid, row, expanderImg));
			expanderWidget = expanderA;
		}
		else {
			// No, it isn't expandable!  Put a 16x16 spacer in place of
			// the expander.
			expanderImgRes = getImages().spacer_1px();
			expanderImg = new Image(expanderImgRes);
			expanderImg.setWidth(EXPANDER_WIDTH);
			expanderImg.setHeight(EXPANDER_HEIGHT);
			expanderWidget = expanderImg;
		}

		// Generate the widgets to select the Binder.
		Grid selectorGrid = new Grid(1, 3);
		selectorGrid.setCellSpacing(0);
		selectorGrid.setCellPadding(0);
		Image binderImg = new Image();
		ti.setBinderUIImage(binderImg);
		setBinderImageResource(ti);
		binderImg.addStyleName("workspaceTreeBinderImg");
		setWidgetHover(binderImg, ti.getBinderHoverImage());
		selectorGrid.setWidget(0, 0, binderImg);
		Widget selectorLabel = getSelectorLabel(ti, (ti.isActivityStream() && (0 == renderDepth)));
		setWidgetHover(selectorLabel, getBinderHover(ti));
		selectorGrid.setWidget(0, 1, selectorLabel);
		selectorGrid.setWidget(0, 2, new Label("\u00A0"));
		selectorGrid.getCellFormatter().setWidth(0, 2, "100%");
		int width = (SELECTOR_GRID_WIDTH - (SELECTOR_GRID_DEPTH_OFFSET * renderDepth));
		if (getBinderImgWidthInt(ti) > width) {
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
		selectorGrid.addStyleName("workspaceTreeControlRow");
		selectorGrid.addStyleName(getCursorStyle(ti));

		// Add the row to the Grid.
		grid.setWidget(row, 0, expanderWidget);
		grid.setWidget(row, 1, selectorA);

		// If this Binder is supposed to be selected...
		if (isBinderSelected(ti)) {
			// ...mark it as selected.
			selectBinder(ti);
		}
		
		// Install a mouse handler on the selector Anchor so that we
		// can manage hover overs on them.
		BinderSelectorMouseHandler bsmh = new BinderSelectorMouseHandler(selectorGridId);
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
				Grid expansionGrid = new Grid();
				expansionGrid.setCellSpacing(0);
				expansionGrid.setCellPadding(0);
				expansionGrid.resizeColumns(2);
				vp.add(expansionGrid);
				grid.setWidget(row, 1, vp);
				for (Iterator<TreeInfo> tii = ti.getChildBindersList().iterator(); tii.hasNext(); ) {
					int expansionRow = expansionGrid.getRowCount();
					expansionGrid.insertRow(expansionRow);
					renderRow(expansionGrid, expansionRow, tii.next(), (renderDepth + 1));
				}
				expanderImg.setResource(getImages().tree_closer());
			}
		}
	}
	
	private void renderRow(Grid grid, int row, TreeInfo ti, int renderDepth) {
		// Always use the initial form of the method.
		renderRow(grid, row, ti, renderDepth, false);	// false -> Not in process of collapsing a row.
	}

	/*
	 * Clears and re-renders a TreeInfo object into a Grid row.
	 */
	private void reRenderRow(Grid grid, int row, TreeInfo ti, boolean reRenderToCollapse) {
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
	void selectBinder(TreeInfo ti) {
		// If this a trash Binder?
		if (!(ti.getBinderInfo().isBinderTrash())) {
			// No!  Mark it as having been selected.
			if (!(ti.isActivityStream())) {
				setSelectedBinderId(ti.getBinderInfo().getBinderId());
			}
			String selectedId_New = getSelectorId(ti);
			Element selectorLabel_New = Document.get().getElementById(selectedId_New);
			selectorLabel_New.addClassName("workspaceTreeBinderSelected");
			Element selectorPanel_New = Document.get().getElementById(EXTENSION_ID_SELECTOR_ANCHOR + selectedId_New);
			if (null != selectorPanel_New) {
				selectorPanel_New.addClassName("workspaceTreeControlRowSelected");
			}

			// ...mark any previous selection as not being selected...
			Element selectorId = Document.get().getElementById(EXTENSION_ID_SELECTOR_ID);
			String selectedId_Old = selectorId.getAttribute("value");
			if (GwtClientHelper.hasString(selectedId_Old) && (!(selectedId_Old.equals(selectedId_New)))) {
				Element selectorLabel_Old = Document.get().getElementById(selectedId_Old);
				if (null != selectorLabel_Old) {
					selectorLabel_Old.removeClassName("workspaceTreeBinderSelected");
				}
				
				Element selectorPanel_Old = Document.get().getElementById(EXTENSION_ID_SELECTOR_ANCHOR + selectedId_Old);
				if (null != selectorPanel_Old) {
					selectorPanel_Old.removeClassName("workspaceTreeControlRowSelected");
				}
			}
			
			// ...and store the new ID as having been selected.
			selectorId.setAttribute("value", selectedId_New);
		}
	}

	/*
	 * Forces the tree to reload regardless of the circumstances.
	 */
	private void reloadTree() {
		// To reload it, simply re-root it at the same point that it's
		// currently rooted, reselecting the previously selected binder.
		reRootTree(getRootTreeInfo().getBinderInfo().getBinderId(), m_selectedBinderId, ExitMode.SIMPLE_EXIT);
	}
	
	/*
	 * Re-roots the WorkspaceTreeControl to a new Binder and optionally
	 * selects a binder.
	 */
	private void reRootTree(final String newRootBinderId, final Long selectedBinderId, final ExitMode exitingActivityStreamMode) {
		GetVerticalTreeCmd cmd;
		
		// Read the TreeInfo for the selected Binder.
		cmd = new GetVerticalTreeCmd( newRootBinderId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTree(),
					newRootBinderId);
			}
			public void onSuccess(VibeRpcResponse response)  {
				TreeInfo rootTI;
				
				// Re-root the tree asynchronously so that we release
				// the AJAX request ASAP.
				rootTI = (TreeInfo) response.getResponseData();
				reRootTreeAsync(
					newRootBinderId,
					selectedBinderId,
					exitingActivityStreamMode,
					rootTI);
			}
		});
	}
	
	private void reRootTree(final String newRootBinderId) {
		// Always use the initial form of the method.
		reRootTree(newRootBinderId, null, ExitMode.SIMPLE_EXIT);
	}
	
	/*
	 * Asynchronously re-roots the WorkspaceTreeControl to a new Binder
	 * and optionally selects a binder.
	 */
	private void reRootTreeAsync(final String newRootBinderId, final Long selectedBinderId, final ExitMode exitingActivityStreamMode, final TreeInfo rootTI) {
		ScheduledCommand treeRooter = new ScheduledCommand() {
			@Override
			public void execute() {
				reRootTreeNow(
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
	private void reRootTreeNow(String newRootBinderId, Long selectedBinderId, ExitMode exitingActivityStreamMode, TreeInfo rootTI) {
		// Update the display with the TreeInfo.
		setRootTreeInfo(rootTI);
		m_rootPanel.clear();
		render(newRootBinderId, m_rootPanel);
		
		// If we're supposed to select a binder as part of the
		// re-rooting...
		if (null != selectedBinderId) {
			// ...and we can find that binder...
			TreeInfo selectedBinderTI = TreeInfo.findBinderTI(rootTI, String.valueOf(selectedBinderId.longValue()));
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

	/*
	 * Sets the image resource on a binder image based on its TreeInfo.
	 */
	private void setBinderImageResource(TreeInfo ti) {
		Image binderImg = ((Image) ti.getBinderUIImage());
		if (null != binderImg) {
			String binderIconName = ti.getBinderIconName();
			if (GwtClientHelper.hasString(binderIconName)) {
				if (binderIconName.startsWith("/")) {
					//The imagesPath operation always ends in "/", so don't allow two slashes 
					binderImg.setUrl(getImagesPath() + binderIconName.substring(1));
				} else {
					binderImg.setUrl(getImagesPath() + binderIconName);
				}
			}
			else {
				ImageResource binderImgRes = ti.getBinderImage();
				if (null == binderImgRes) {
					binderImgRes = getImages().spacer_1px();
					binderImg.setResource(binderImgRes);
				}
				else {
					binderImg.setResource(binderImgRes);
				}
				binderImg.setVisibleRect(
					0,
					0,
					getBinderImgWidthInt( ti),
					getBinderImgHeightInt(ti));
			}
			binderImg.setWidth( getBinderImgWidth( ti));
			binderImg.setHeight(getBinderImgHeight(ti));
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
	public void setRenderContext(String selectedBinderId, FlowPanel targetPanel) {
		// Simply store the parameter in their appropriate data
		// members.
		m_selectedBinderId = Long.parseLong(selectedBinderId);
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
	public void setSelectedBinder(OnSelectBinderInfo binderInfo) {
		// If the selection is for a Binder's trash...
		if (binderInfo.isTrash()) {
			// ...we don't change anything.
			return;
		}
		
		// Is the requested Binder available in those we've already
		// got loaded?
		Instigator instigator = binderInfo.getInstigator();
		final boolean forceReload = (binderInfo.getForceSidebarReload() || (Instigator.FORCE_SIDEBAR_RELOAD == instigator));
		final String binderId = String.valueOf(binderInfo.getBinderId());
		final TreeInfo targetTI = TreeInfo.findBinderTI(getRootTreeInfo(), binderId);
		if (null != targetTI) {
			// Yes!  Should the request cause the tree to be re-rooted?
			switch (instigator) {
			case CONTENT_AREA_CHANGED:
			case FORCE_SIDEBAR_RELOAD:
			case SIDEBAR_TREE_SELECT:
				if (forceReload)
					 reloadTree();
				else selectBinder(targetTI);
				break;

			default:
				GetRootWorkspaceIdCmd cmd;
				
				// Yes, the request should cause the tree to be
				// re-rooted!  (It may be coming from the bread crumbs
				// or some other unknown source.)  What's the ID if the
				// selected Binder's root workspace?
				cmd = new GetRootWorkspaceIdCmd( binderId );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_GetRootWorkspaceId(),
							binderId);
						selectBinder(targetTI);
					}
					public void onSuccess(VibeRpcResponse response)  {
						String rootWorkspaceId;
						StringRpcResponseData responseData;
						
						// Asynchronously perform the selection so that
						// we release the AJAX request ASAP.
						responseData = (StringRpcResponseData) response.getResponseData();
						rootWorkspaceId = responseData.getStringValue();
						selectRootWorkspaceIdAsync(
							binderId,
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
			reRootTree(binderId);
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
				 reloadTree();
			else selectBinder(targetTI);
		}
		else {
			// Yes, we need to re-root!
			reRootTree(binderId);
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
		m_selectedBinderId = Long.parseLong(selectedBinderId);
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
				Long binderId = osbInfo.getBinderId();
				if (null != binderId) {
					ti = TreeInfo.findBinderTI(rootTI, binderId.toString());
				}
			}
			if (null != ti) {
				// Yes!  Set the busy animation image for this
				// TreeInfo's binder...
				Image binderImg = ((Image) ti.getBinderUIImage());
				if (null != binderImg) {
					binderImg.setResource(getImages().busyAnimation());
				}
				
				// ...and keep track of as being the one that we're
				// ...switching to.
				m_busyInfo.setBusyTI(ti);
			}
		}
	}
}
