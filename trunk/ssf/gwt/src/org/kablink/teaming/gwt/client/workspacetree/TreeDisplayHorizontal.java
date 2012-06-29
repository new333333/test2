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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandHorizontalBucketCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetHorizontalNodeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.widgets.EventButton;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
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
 */
public class TreeDisplayHorizontal extends TreeDisplayBase {
	private FlowPanel	m_rootPanel;	//
	
	private final static String GRID_DEPTH_ATTRIBUTE	= "n-depth";
	
	/*
	 * Inner class that implements clicking on the various tree
	 * expansion widgets.
	 */
	private class BinderExpander implements ClickHandler {
		private FlexTable	m_nodeGrid;		//
		private Image		m_expanderImg;	//
		private TreeInfo	m_ti;			//

		/**
		 * Class constructor.
		 * 
		 * @param ti
		 * @param nodeGrid
		 * @param expanderImg
		 */
		BinderExpander(TreeInfo ti, FlexTable nodeGrid, Image expanderImg) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			m_ti          = ti;
			m_nodeGrid    = nodeGrid;
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
			// Are we collapsing the node?
			if (m_ti.isBinderExpanded()) {
				// Yes!  Mark it as being closed and re-render it...
				m_expanderImg.setResource(getImages().tree_opener());
				m_ti.setBinderExpanded(false);
				reRenderNode(m_ti, m_nodeGrid);
				
				// ...and tell everybody that it's been collapsed.
				GwtTeaming.fireEventAsync(
					new TreeNodeCollapsedEvent(
						getSelectedBinderInfo(),
						getTreeMode()));
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
								getMessages().rpcFailure_ExpandBucket());
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
								getMessages().rpcFailure_GetTree(),
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
	 * Class constructor.
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
	 * Removes the widgets from a node's FlexTable.
	 */
	private void clearNode(FlexTable nodeGrid) {
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
		Image img = GwtClientHelper.buildImage(getImages().breadcrumb_close(), getMessages().treeCloseBreadCrumbs());
		img.addStyleName("breadCrumb_CloseImg");
		
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
	 * Creates a FlexTable for use as an individual node in the tree.
	 */
	private FlexTable createGrid(int rows, int columns, String style) {
		FlexTable reply = new FlexTable();
		reply.setCellSpacing(0);
		reply.setCellPadding(0);
		if (GwtClientHelper.hasString(style)) {
			reply.addStyleName(style);
		}
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
	 * Returns the TreeInfo that appears immediately before the given
	 * TreeInfo in the bread crumb list.
	 */
	private TreeInfo getPreviousTI(TreeInfo ti) {
		// Scan the TreeInfo's in the bread crumb list.
		TreeInfo prevTI = null;
		for (TreeInfo tiScan:  getRootTreeInfoList()) {
			// Is this TreeInfo the same instance as the one we were
			// given?
			if (tiScan == ti) {
				// Yes!  Return the one before it.
				return prevTI;
			}
			
			// Store the current one as the most recent previous.
			prevTI = tiScan;
		}
		
		// If we get here, we couldn't find the given TreeInfo in the
		// list.  Return null.
		return null;
	}
	
	/*
	 * Returns the widget to use as a TreeInfo selector's label.
	 */
	private Widget getSelectorLabel(TreeInfo ti, boolean rootNode) {
		Widget reply;
		
		// Is this item a bucket?
		String baseLabelStyle = "breadCrumb_ContentNode_Anchor";
		boolean binderBreadcrumbTail = false;
		if (getTreeMode().isHorizontalBinder()) {
			baseLabelStyle += (" breadCrumb_ContentNode_AnchorBC");
			if (rootNode && ti.isRootTail()) {
				binderBreadcrumbTail = true;
				baseLabelStyle += (" breadCrumb_ContentNode_AnchorTail");
			}
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
			String selectorStart = ((binderBreadcrumbTail && isTrash()) ? (getMessages().treeTrash() + " ") : "");  
			Label selectorLabel = new Label(selectorStart + ti.getBinderTitle());
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
	 * Tells a sidebar tree implementation to refresh itself
	 * maintaining its current context and selected binder.
	 * 
	 * Should never be called in a horizontal tree implementation.
	 * 
	 * Implements the TreeDisplayBase.rerootSidebarTree() method.
	 */
	@Override
	public void refreshSidebarTree() {
		GwtClientHelper.deferredAlert(getMessages().treeInternalErrorRefreshNotSidebar());
	}

	/**
	 * Tells a sidebar tree implementation to re-root itself
	 * to its currently selected binder.
	 * 
	 * Should never be called in a horizontal tree implementation.
	 * 
	 * Implements the TreeDisplayBase.rerootSidebarTree() method.
	 */
	@Override
	public void rerootSidebarTree() {
		GwtClientHelper.deferredAlert(getMessages().treeInternalErrorRerootNotSidebar());
	}

	/**
	 * Called to render the information in a TreeInfo object into a
	 * FlowPanel.
	 * 
	 * Implementation of TreeDisplayBase.render().
	 *
	 * @param selectedBinderInfo
	 * @param rootPanel
	 */
	@Override
	public void render(BinderInfo selectedBinderInfo, FlowPanel rootPanel) {
		m_rootPanel = rootPanel;
		
		// If we're displaying a horizontal popup...
		if (getTreeMode().isHorizontalPopup()) {
			// ...add a close button to the top of the panel...
			m_rootPanel.add(createClosePanel());
		}
		
		// ...create a FlexTable for the content...
		List<TreeInfo> rootTIList = getRootTreeInfoList();
		int count = rootTIList.size();
		FlexTable contentGrid = createGrid(
			1,
			count,
			("breadCrumb_Content " +
				(getTreeMode().isHorizontalBinder() ?
					"breadCrumb_ContentBinder"      :
					"breadCrumb_ContentPopup")));
		m_rootPanel.add(contentGrid);

		// ...scan the TreeInfo's...
		for (int i = 0; i < count; i += 1) {
			// ...and display each into the content FlexTable.
			FlexTable nodeGrid = createGrid(1, 2, "breadCrumb_ContentNode");
			nodeGrid.getElement().setAttribute(GRID_DEPTH_ATTRIBUTE, "0");
			contentGrid.setWidget(0, i, nodeGrid);
			TreeInfo ti = rootTIList.get(i);
			ti.setRootTail((i + 1) == count);
			renderNode(ti, nodeGrid);
		}
	}
	
	/*
	 * Renders a TreeInfo into the next position in a HorizontalPanel.
	 */
	private void renderNode(TreeInfo ti, FlexTable nodeGrid) {
		int depth = Integer.parseInt(nodeGrid.getElement().getAttribute(GRID_DEPTH_ATTRIBUTE));
		Widget selectorLabel      = getSelectorLabel(ti, (0 == depth));
		String selectorLabelStyle = selectorLabel.getStyleName();
		boolean binderBreadcrumbTail = selectorLabelStyle.contains("breadCrumb_ContentNode_AnchorTail");
		
		// Is this Binder expandable?
		boolean showExpander;
		Widget expanderWidget;
		if (binderBreadcrumbTail) {
			showExpander   = false;
			expanderWidget = null;
		}
		else {
			Image expanderImg;
			ImageResource expanderImgRes = ti.getExpanderImage();
			showExpander = (null != expanderImgRes);
			if (showExpander) {
				// Yes!  Put an expander Anchor to allow expanding and
				// collapsing of its contents.
				expanderImg = GwtClientHelper.buildImage(expanderImgRes.getSafeUri());
				expanderImg.getElement().setAttribute("align", "absmiddle");
				expanderImg.addStyleName("breadCrumb_ContentNode_ExpanderImg");
				if (getTreeMode().isHorizontalBinder()) {
					expanderImg.addStyleName("breadCrumb_ContentNode_ExpanderImgSmall");
				}
				Anchor expanderA = new Anchor();
				expanderA.getElement().appendChild(expanderImg.getElement());
				expanderA.addClickHandler(new BinderExpander(ti, nodeGrid, expanderImg));
				expanderWidget = expanderA;
			}
			else {
				// No, it isn't expandable!  Put a 16x16 spacer in place of
				// the expander.
				expanderImgRes = getImages().spacer_1px();
				expanderImg = GwtClientHelper.buildImage(expanderImgRes.getSafeUri());
				expanderImg.setWidth( EXPANDER_WIDTH );
				expanderImg.setHeight(EXPANDER_HEIGHT);
				expanderWidget = expanderImg;
			}
		}
		
		// Generate the widgets to select the Binder.
		Anchor selectorA = new Anchor();
		selectorA.getElement().appendChild(selectorLabel.getElement());
		selectorA.addClickHandler(new BinderSelector(ti));
		selectorA.setWidth("100%");
		if (ti.isBucket()) {
			setWidgetHover(selectorA, getBinderHover(ti));
		}
		
		// Are we rendering the tail node in a binder bread crumb tree?
		if (binderBreadcrumbTail) {
			// Yes!  Make the selector display inline...
			selectorLabel.addStyleName("displayInline");

			// ...create panel to hold the various widgets we'll lay
			// ...down for the tail node...
			FlowPanel fp = new FlowPanel();
			fp.addStyleName("breadCrumb_ContentTail_Panel");

			// ...if the binder about the tail is a folder...
			final TreeInfo prevTI = getPreviousTI(ti);
			if ((null != prevTI) && (BinderType.FOLDER == prevTI.getBinderInfo().getBinderType())) {
				// ...add a back button to navigate to it...
				EventButton backButton = new EventButton(
					getBaseImages().upDisabled16(),
					null,
					getBaseImages().upMouseOver16(),
					true,
					getMessages().treePreviousFolder(),
					new Command() {
						@Override
						public void execute() {
							if (canChangeContext()) {
								selectBinder(prevTI);
								GwtTeaming.fireEvent(
									new ChangeContextEvent(
										buildOnSelectBinderInfo(
											prevTI)));
							}
						}
					});
				backButton.addStyleName("breadCrumb_ContentTail_Back");
				fp.add(backButton);
			}

			// ...add an image for the binder itself...
			Image binderImg = GwtClientHelper.buildImage(((String) null));
			binderImg.addStyleName("breadCrumb_ContentTail_Img");
			binderImg.getElement().setAttribute("align", "absmiddle");
			ti.setBinderUIImage(binderImg);
			setBinderImageResource(ti, BinderIconSize.getBreadCrumbIconSize(), getFilrImages().folder());

			// ...finally, tie it all together and add it to the root
			// ...panel.
			fp.add(binderImg);
			fp.add(selectorA);
		    m_rootPanel.add(fp);
		}
		
		else {
			// No, we aren't rendering the tail node in a binder bread
			// crumb tree!  Simply add the expander and selector to the
			// FlexTable.
			nodeGrid.setWidget(0, 0, expanderWidget);
			nodeGrid.setWidget(0, 1, selectorA     );
		}

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
				FlexTable expansionGrid = createGrid(1, 2, "breadCrumb_ContentNode");
				expansionGrid.getElement().setAttribute(GRID_DEPTH_ATTRIBUTE, String.valueOf(depth + 1));
				vp.add(expansionGrid);
				tii.setRootTail(false);	// Nested node -> Can never be a root tail.
				renderNode(tii, expansionGrid);
			}
		}
	}

	/*
	 * Clears and re-renders a TreeInfo object into a node's FlexTable.
	 */
	private void reRenderNode(TreeInfo ti, FlexTable nodeGrid) {
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
	 * @param selectedBinderInfo
	 * @param targetPanel
	 */
	@Override
	public void setRenderContext(BinderInfo selectedBinderInfo, FlowPanel targetPanel) {
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
