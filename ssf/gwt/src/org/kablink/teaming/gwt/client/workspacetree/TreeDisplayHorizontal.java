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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandHorizontalBucketCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetHorizontalNodeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;


/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's bread crumbs.
 * 
 * @author drfoster@novell.com
 *
 */
public class TreeDisplayHorizontal extends TreeDisplayBase {
	private final static String GRID_DEPTH_ATTRIBUTE	= "n-depth";
	
	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private class BinderExpander implements ClickHandler {
		private Grid m_nodeGrid;
		private Image m_expanderImg;
		private TreeInfo m_ti;

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param nodeGrid
		 * @param expanderImg
		 */
		BinderExpander(TreeInfo ti, Grid nodeGrid, Image expanderImg) {
			// Simply store the parameters.
			m_ti = ti;
			m_nodeGrid = nodeGrid;
			m_expanderImg = expanderImg;
		}

		/*
		 * Asynchronously expands the current node.
		 */
		private void doExpandNodeAsync(final TreeInfo expandedTI) {
			ScheduledCommand expander = new ScheduledCommand() {
				@Override
				public void execute() {
					doExpandNodeNow(expandedTI);
				}
			};
			Scheduler.get().scheduleDeferred(expander);
		}
		
		/*
		 * Synchronously expands the current node.
		 */
		private void doExpandNodeNow(TreeInfo expandedTI) {
			// Expand the node...
			m_expanderImg.setResource(getImages().tree_closer());
			m_ti.setBinderExpanded(true);
			m_ti.setChildBindersList(expandedTI.getChildBindersList());
			reRenderNode(m_ti, m_nodeGrid);
			
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
			// Are we collapsing the node?
			if (m_ti.isBinderExpanded()) {
				// Yes!  Mark it as being closed and re-render it...
				m_expanderImg.setResource(getImages().tree_opener());
				m_ti.setBinderExpanded(false);
				reRenderNode(m_ti, m_nodeGrid);
				
				// ...and tell everybody that it's been collapsed.
				GwtTeaming.fireEventAsync(new TreeNodeCollapsedEvent(getSelectedBinderId(), getTreeMode()));
			}
				
			else {
				// No, we aren't collapsing the node!  We must be
				// expanding it.  Are we showing a collapsed bucket?
				if (m_ti.isBucket()) {
					ExpandHorizontalBucketCmd cmd;
					
					// Yes!  Expand it.
					cmd = new ExpandHorizontalBucketCmd( m_ti.getBucketInfo() );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_ExpandBucket());
						}
						
						@Override
						public void onSuccess(VibeRpcResponse response) {
							// Yes!  Mark the node as being opened,
							// save its new child Binder's list and
							// re-render it.  We do this asynchronously
							// so that we release the AJAX request
							// ASAP.
							TreeInfo expandedTI = (TreeInfo) response.getResponseData();
							doExpandNodeAsync(expandedTI);
						}
					});
				}
				
				else {
					GetHorizontalNodeCmd cmd;
					
					// No, we aren't showing a collapsed bucket!  We
					// must be showing a normal node.  Can we get a
					// TreeInfo for the expansion?
					cmd = new GetHorizontalNodeCmd( m_ti.getBinderInfo().getBinderId() );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
						@Override
						public void onFailure(Throwable t) {
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetTree(),
								m_ti.getBinderInfo().getBinderId());
						}
						
						@Override
						public void onSuccess(VibeRpcResponse response) {
							// Yes!  Mark the node as being opened,
							// save its new child Binder's list and
							// re-render it.  We do this asynchronously
							// so that we release the AJAX request
							// ASAP.
							TreeInfo expandedTI = (TreeInfo) response.getResponseData();
							doExpandNodeAsync(expandedTI);
						}
					});
				}
			}
		}
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param wsTree
	 * @param rootTIList
	 */
	public TreeDisplayHorizontal(WorkspaceTreeControl wsTree, List<TreeInfo> rootTIList) {
		// Initialize the super class.
		super(wsTree, rootTIList);
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
		return new OnSelectBinderInfo(ti, Instigator.BREADCRUMB_TREE_SELECT);
	}

	/*
	 * Removes the widgets from a node's Grid.
	 */
	private void clearNode(Grid nodeGrid) {
		nodeGrid.remove(nodeGrid.getWidget(0, 0));
		nodeGrid.remove(nodeGrid.getWidget(0, 1));
	}

	/*
	 * Closes this WorkspaceTreeControl.
	 */
	private void closeTree() {
		BrowseHierarchyExitEvent.fireOne();
	}
	
	/*
	 * Creates a FlowPanel for the close push button.
	 */
	private FlowPanel createClosePanel() {
		// Create the panel...
		FlowPanel panel = new FlowPanel();
		
		// ...create the Image...
		Image img = new Image(getImages().breadcrumb_close());
		img.addStyleName("breadCrumb_CloseImg");
		setWidgetHover(img, getMessages().treeCloseBreadCrumbs());
		
		// ...create the Anchor...
		Anchor a = new Anchor();
		a.addStyleName("breadCrumb_CloseA");
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeTree();
			}
		});
		
		// ...tie things together...
		a.getElement().appendChild(img.getElement());
		
		// ...and add the Anchor to the panel and return it.
		panel.add(a);
		return panel;
	}
	
	/*
	 * Creates a Grid for use as an individual node in the tree.
	 */
	private Grid createGrid(int rows, int columns, String style) {
		Grid reply = new Grid(rows, columns);
		reply.setCellSpacing(0);
		reply.setCellPadding(0);
		reply.addStyleName(style);
		CellFormatter cf = reply.getCellFormatter();
		for (int i = 0; i < rows; i += 1) {
			for (int j = 0; j < columns; j += 1) {
				cf.setAlignment(
					i,
					j,
					HasHorizontalAlignment.ALIGN_LEFT,
					HasVerticalAlignment.ALIGN_TOP);
			}
		}
		return reply;
	}

	/*
	 * Returns the widget to use as a TreeInfo selector's label.
	 */
	private Widget getSelectorLabel(TreeInfo ti, boolean rootNode) {
		Widget reply;
		
		// Is this item a bucket?
		String baseLabelStyle = "breadCrumb_ContentNode_Anchor";
		if (rootNode && ti.isRootTail() && getTreeMode().isHorizontalBinder()) {
			baseLabelStyle += (" breadCrumb_ContentNode_AnchorTail");
		}
		if (ti.isBucket()) {
			// Yes!  Generate the appropriate widgets. 
			FlowPanel selectorPanel = new FlowPanel();
			selectorPanel.addStyleName(baseLabelStyle + " cursorDefault gwtUI_nowrap");
			selectorPanel.add(buildBucketPartLabel(ti.getPreBucketTitle() + "\u00A0"));
			selectorPanel.add(buildBucketRangeImage());
			selectorPanel.add(buildBucketPartLabel("\u00A0" + ti.getPostBucketTitle()));
			reply = selectorPanel;
		}
		
		else {
			// No, it's not a bucket!  Generate a simply Label for it.
			Label selectorLabel = new Label(ti.getBinderTitle());
			selectorLabel.setWordWrap(false);
			selectorLabel.addStyleName(baseLabelStyle + " cursorPointer");
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
		// Return false since a bread crumb tree is never in activity
		// stream mode.
		return false;
	}
	
	/**
	 * Called to render the information in a TreeInfo object into a
	 * FlowPanel.
	 * 
	 * Implementation of TreeDisplayBase.render().
	 *
	 * @param rootPanel
	 */
	@Override
	public void render(String selectedBinderId, FlowPanel rootPanel) {
		// If we're displaying a horizontal popup...
		if (getTreeMode().isHorizontalPopup()) {
			// ...add a close button to the top of the panel...
			rootPanel.add(createClosePanel());
		}
		
		// ...create a Grid for the content...
		List<TreeInfo> rootTIList = getRootTreeInfoList();
		int count = rootTIList.size();;
		Grid contentGrid = createGrid(1, count, "breadCrumb_Content " + (getTreeMode().isHorizontalBinder() ? "breadCrumb_ContentBinder" : "breadCrumb_ContentPopup"));

		// ...scan the TreeInfo's...
		for (int i = 0; i < count; i += 1) {
			// ...display each into the content Grid...
			Grid nodeGrid = createGrid(1, 2, "breadCrumb_ContentNode");
			nodeGrid.getElement().setAttribute(GRID_DEPTH_ATTRIBUTE, "0");
			contentGrid.setWidget(0, i, nodeGrid);
			TreeInfo ti = rootTIList.get(i);
			ti.setRootTail((i + 1) == count);
			renderNode(ti, nodeGrid);
		}
		
		// ...and add the content Grid to the root panel.
		rootPanel.add(contentGrid);
	}
	
	/*
	 * Renders a TreeInfo into the next position in a HorizontalPanel.
	 */
	private void renderNode(TreeInfo ti, Grid nodeGrid) {
		// Is this Binder expandable?
		Widget expanderWidget;
		Image expanderImg;
		ImageResource expanderImgRes = ti.getExpanderImage();
		boolean showExpander = (null != expanderImgRes);
		if (showExpander) {
			// Yes!  Put an expander Anchor to allow expanding and
			// collapsing of its contents.
			expanderImg = new Image(expanderImgRes);
			expanderImg.addStyleName("breadCrumb_ContentNode_ExpanderImg");
			Anchor expanderA = new Anchor();
			expanderA.getElement().appendChild(expanderImg.getElement());
			expanderA.addClickHandler(new BinderExpander(ti, nodeGrid, expanderImg));
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
		int depth = Integer.parseInt(nodeGrid.getElement().getAttribute(GRID_DEPTH_ATTRIBUTE));
		Widget selectorLabel = getSelectorLabel(ti, (0 == depth));
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorLabel.getElement());
		selectorA.addClickHandler(new BinderSelector(ti));
		selectorA.setWidth("100%");
		if (ti.isBucket()) {
			setWidgetHover(selectorA, getBinderHover(ti));
		}
		
		// Add the expander and selector to the Grid.
		nodeGrid.setWidget(0, 0, expanderWidget);
		nodeGrid.setWidget(0, 1, selectorA);

		// Is the node showing an expander and is expanded?
		if (showExpander && ti.isBinderExpanded()) {
			// Yes!  Then we need to render its contents.
			Widget w = nodeGrid.getWidget(0, 1);
			nodeGrid.remove(w);
			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(0);
			vp.add(w);
			nodeGrid.setWidget(0, 1, vp);
			for (TreeInfo tii:  ti.getChildBindersList()) {
				Grid expansionGrid = createGrid(1, 2, "breadCrumb_ContentNode");
				expansionGrid.getElement().setAttribute(GRID_DEPTH_ATTRIBUTE, String.valueOf(depth + 1));
				vp.add(expansionGrid);
				tii.setRootTail(false);	// Nested node -> Can never be a root tail.
				renderNode(tii, expansionGrid);
			}
		}
	}

	/*
	 * Clears and re-renders a TreeInfo object into a node's Grid.
	 */
	private void reRenderNode(TreeInfo ti, Grid nodeGrid) {
		clearNode(nodeGrid);
		renderNode(ti, nodeGrid);
	}
	
	/**
	 * Does whatever is necessary UI wise to select the Binder
	 * represented by TreeInfo.
	 * 
	 * Implementation of TreeDisplayBase.selectBinder().
	 * 
	 * @param ti
	 */
	@Override
	void selectBinder(TreeInfo ti) {
		closeTree();
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
		// These values aren't used by the horizontal tree control.
	}
	
	/**
	 * Called to change the binder being displayed by the
	 * WorkspaceTreeControl.
	 * 
	 * Implementation of TreeDisplayBase.setSelectedBinder().
	 * 
	 * @param binderInfo
	 */
	@Override
	public void setSelectedBinder(OnSelectBinderInfo binderInfo) {
		// The context of the horizontal tree control can only be set
		// during it's instantiation.
	}
}
