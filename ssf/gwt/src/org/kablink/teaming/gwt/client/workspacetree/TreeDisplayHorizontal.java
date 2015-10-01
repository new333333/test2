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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent.ManageMenuPopupCallback;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent.CollectionCallback;
import org.kablink.teaming.gwt.client.event.MenuLoadedEvent.MenuItem;
import org.kablink.teaming.gwt.client.event.HideManageMenuEvent;
import org.kablink.teaming.gwt.client.event.ShowCollectionEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandHorizontalBucketCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetHorizontalNodeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.widgets.EventButton;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
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
 * Class used to drive the display of a horizontal
 * WorkspaceTreeControl, typically used for Vibe's bread crumbs.
 * 
 * @author drfoster@novell.com
 */
public class TreeDisplayHorizontal extends TreeDisplayBase {
	private Anchor			m_selectorConfigA;		//
	private Boolean			m_mainMenuVisible;		//
	private FlowPanel		m_rootPanel;			// The top level FlowPanel containing the tree's contents.
	private ManageMenuPopup	m_selectorConfigPopup;	//
	
	private final static String GRID_DEPTH_ATTRIBUTE	= "n-depth";

	/*
	 * Enumeration type used to control how binder bread crumbs behave
	 * in Filr mode.
	 */
	@SuppressWarnings("unused")
	private enum FileBreadCrumbMode {
		BINDER_NAMES,	// Only binder names are links.  The expander arrows are not.
		FULL,			// Links in bread crumbs are the same in Filr and Vibe.
		NONE;			// Neither the expander arrows or binder names are links.
		
		boolean isBinderNames() {return this.equals(BINDER_NAMES);}
		boolean isFull()        {return this.equals(FULL        );}
		boolean isNone()        {return this.equals(NONE        );}
	}
	private final static FileBreadCrumbMode	FILR_BC_MODE	= FileBreadCrumbMode.BINDER_NAMES;
	
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
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					doExpandNodeNow(expandedTI);
				}
			});
		}
		
		/*
		 * Synchronously expands the current node.
		 */
		private void doExpandNodeNow(TreeInfo expandedTI) {
			// Expand the node...
			m_expanderImg.setResource(getImages().tree_closer());
			m_ti.setBinderExpanded(true);
			m_ti.setChildBindersList(expandedTI.getChildBindersList());
			rerenderNode(m_ti, m_nodeGrid);
			
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
				rerenderNode(m_ti, m_nodeGrid);
				
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
					cmd = new ExpandHorizontalBucketCmd(m_ti.getBucketInfo());
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
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
					cmd = new GetHorizontalNodeCmd(m_ti.getBinderInfo().getBinderId());
					GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
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
	
	/*
	 * Inner class used to track information about getting the
	 * collection type from the sidebar.
	 */
	private class GetSidebarCollectionHelper {
		private boolean		m_waitingForResponse;	// Coordinates handling the callback.
		private FlowPanel	m_fp;					// The FlowPanel things are being added to.
		private Timer		m_timer;				// A timer used to control how long we wait for a response.
		private TreeInfo	m_ti;					// The TreeInfo we're dealing with.
		private Widget		m_selectorW;			// The Widget for the TreeInfo.
		
		/**
		 * Class constructor.
		 */
		GetSidebarCollectionHelper(FlowPanel fp, TreeInfo ti, Widget selectorW) {
			// Initialize the super class...
			super();
			
			// ...store the parameters...
			m_fp        = fp;
			m_ti        = ti;
			m_selectorW = selectorW;
			
			// ...and initialize everything else.
			setWaitingForResponse(true);
			
			// Setup a timer to wait for the response.  If we exceed
			// the timeout, we simply stop waiting.
			m_timer = new Timer() {
				@Override
				public void run() {
					// Stop waiting...
					clearGetCollection();
					m_waitingForResponse = false;
					
					// ...add the TreeInfo information since we didn't
					// ...get a collection that we'd normally add one
					// ...from.
					addTIImageAndAnchor(m_fp, m_ti, m_selectorW);

					// ...and add the access to the binder
					// ...configuration menu.
					addBinderConfig(m_fp, m_ti);
					
					// If we're in UI debug mode, display an alert
					// about the problem.
					GwtClientHelper.debugAlert(
						getMessages().treeInternalErrorNoCollection());
				}
			};
			m_timer.schedule(1000);	// We'll wait no longer than 1 second for a collection type.
		}

		/**
		 * Called to clear the get collection timer.
		 */
		void clearGetCollection() {
			// If we have a timer...
			if (null != m_timer) {
				// ...cancel and forget about it.
				m_timer.cancel();
				m_timer = null;
			}
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		boolean getWaitingForResponse() {return m_waitingForResponse;}

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		void setWaitingForResponse(boolean waitingForResponse) {m_waitingForResponse = waitingForResponse;}
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

	/*
	 * Adds access to the binder configuration menu.
	 */
	private void addBinderConfig(FlowPanel fp, TreeInfo ti) {
		// For a trash view...
		if (isTrash()) {
			// ...we don't show the binder configuration menu.
			return;
		}

		// If the main menu is visible...
		if (isMainMenuVisible()) {
			// ...create an anchor to run the configuration menu on
			// ...this binder...
			m_selectorConfigA  = new Anchor();
			final Element selectorConfigAE = m_selectorConfigA.getElement();
			m_selectorConfigA.setTitle(ti.getBinderInfo().isBinderFolder() ? getMessages().treeAltConfigureFolder() : getMessages().treeAltConfigureWorkspace());
			Image selectorConfigImg = GwtClientHelper.buildImage(getImages().configOptions());
			selectorConfigImg.addStyleName("breadCrumb_ContentTail_configureImg");
			selectorConfigAE.appendChild(selectorConfigImg.getElement());
			m_selectorConfigA.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (null == m_selectorConfigPopup)
					     buildAndRunSelectorConfigMenuAsync(m_selectorConfigA);
					else runSelectorConfigMenuAsync(        m_selectorConfigA);
				}
			});
			fp.add(m_selectorConfigA);
		}
		
		// ...and hide the manage menu in the main menu bar.
		HideManageMenuEvent.fireOneAsync();
	}
	
	/*
	 * Adds an image and anchor for a TreeInfo to a flow panel.
	 */
	private void addTIImageAndAnchor(FlowPanel fp, TreeInfo ti, Widget selectorW) {
		// Create the image for the TreeInfo...
		Image binderImg = GwtClientHelper.buildImage(((String) null));
		binderImg.addStyleName("breadCrumb_ContentTail_Img");
		binderImg.getElement().setAttribute("align", "absmiddle");
		ti.setBinderUIImage(binderImg);
		setBinderImageResource(ti, BinderIconSize.getBreadCrumbIconSize(), getFilrImages().folder());

		// ...and add it to the flow panel with the anchor.
		fp.add(binderImg);
		fp.add(selectorW);
	}
	
	/*
	 * Adds a button to move up the hierarchy to a flow panel.
	 */
	private void addUpButton(FlowPanel fp, String upAltText, Command upCommand) {
		// Create the up button...
		EventButton upButton = new EventButton(
			getBaseImages().upDisabled16(),		// This is really the enabled image, but it needs to be gray.
			null,								// null -> No disabled image.
			getBaseImages().upMouseOver16(),	// The hover images for the button.
			true,								// true -> Enable the button.
			upAltText,							// The alternate text for the button.
			upCommand);
		upButton.addStyleName("breadCrumb_ContentTail_Up");
		
		// ...and add it to the flow panel.
		fp.add(upButton);
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
						m_selectorConfigPopup.setCurrentBinder(getSelectedBinderInfo());
						m_selectorConfigPopup.populateMenu();
						runSelectorConfigMenuAsync(selectorConfigA);
					}
				}
			}));
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
		// If the TreeInfo is for a personal workspace...
		BinderInfo bi = ti.getBinderInfo();
		if (bi.isBinderWorkspace() && bi.getWorkspaceType().equals(WorkspaceType.USER)) {
			// ...and that workspace is the current user's...
			Long currentUserWorkspaceId = Long.parseLong(GwtClientHelper.getRequestInfo().getCurrentUserWorkspaceId());
			if (currentUserWorkspaceId.equals(bi.getBinderIdAsLong())) {
				// ...navigate to their 'My Files' view instead.
				ti = ti.copyBaseTI();
				bi = ti.getBinderInfo();
				bi.setBinderType(    BinderType.COLLECTION  );
				bi.setCollectionType(CollectionType.MY_FILES);
				ti.setBinderPermalink(
					GwtClientHelper.appendUrlParam(
						ti.getBinderPermalink(),
						"showCollection",
						String.valueOf(CollectionType.MY_FILES.ordinal())));
			}
		}
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
	 * Clears an previous binder configuration panel and menu.
	 */
	private void clearSelectorConfig() {
		// Clear the previous binder configuration panel...
		if (null != m_selectorConfigA) {
			m_selectorConfigA.removeFromParent();
			m_selectorConfigA = null;
		}
		
		// ...and menu.
		if (null != m_selectorConfigPopup) {
			m_selectorConfigPopup.clearItems();
			m_selectorConfigPopup = null;
		}
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
		// If we're in a trash view...
		if (isTrash()) {
			// ...the previous TreeInfo is the one were on.
			return ti;
		}
		
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
	 * Returns the current sidebar context via a callback.  Ignored by
	 * horizontal trees.
	 * 
	 * Implementation of TreeDisplayBase.getCollectionCallback().
	 * 
	 * @param contextCallback
	 */
	@Override
	public void getSidebarCollection(CollectionCallback contextCallback) {
		// Nothing to do.  Ignored by horizontal trees.
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
	
	/*
	 * Returns true if the main menu is visible and false otherwise.
	 */
	@Override
	boolean isMainMenuVisible() {
		boolean reply;
		if (null == m_mainMenuVisible)
		     reply = super.isMainMenuVisible();
		else reply = m_mainMenuVisible.booleanValue();
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
	 * Called when the main menu is hidden.
	 * 
	 * Implements the TreeDisplayBase.menuHide() abstract method.
	 */
	@Override
	public void menuHide() {
		m_mainMenuVisible = Boolean.FALSE;
		clearSelectorConfig();
	}
	
	/**
	 * Called when the main menu is shown.
	 * 
	 * Implements the TreeDisplayBase.menuShow() abstract method.
	 */
	@Override
	public void menuShow() {
		m_mainMenuVisible = Boolean.TRUE;
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
	private void renderNode(final TreeInfo ti, FlexTable nodeGrid) {
		boolean	isFilr               = GwtClientHelper.isLicenseFilr();
		boolean	isHorizontalBinder   = getTreeMode().isHorizontalBinder();
		int		depth                = Integer.parseInt(nodeGrid.getElement().getAttribute(GRID_DEPTH_ATTRIBUTE));
		Widget	selectorLabel        = getSelectorLabel(ti, (0 == depth));
		String	selectorLabelStyle   = selectorLabel.getStyleName();
		boolean	binderBreadcrumbTail = selectorLabelStyle.contains("breadCrumb_ContentNode_AnchorTail");
		
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
				if (isHorizontalBinder) {
					expanderImg.addStyleName("breadCrumb_ContentNode_ExpanderImgSmall");
				}
				if (isHorizontalBinder && isFilr && (FILR_BC_MODE.isBinderNames() || FILR_BC_MODE.isNone())) {
					expanderWidget = expanderImg;
					expanderWidget.addStyleName("breadCrumb_ContentNode_Filr");
				}
				else {
					Anchor expanderA = new Anchor();
					expanderA.getElement().appendChild(expanderImg.getElement());
					expanderA.addClickHandler(new BinderExpander(ti, nodeGrid, expanderImg));
					expanderWidget = expanderA;
				}
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
		Anchor a;
		if (isHorizontalBinder && isFilr && FILR_BC_MODE.isNone() && (!binderBreadcrumbTail)) {
			a = null;
			selectorLabel.addStyleName("breadCrumb_ContentNode_Filr");
		}
		else {
			a = new Anchor();
			a.getElement().appendChild(selectorLabel.getElement());
			a.addClickHandler(new BinderSelector(ti));
			a.setWidth("100%");
			if (ti.isBucket()) {
				setWidgetHover(a, getBinderHover(ti));
			}
		}
		final Widget selectorW = ((null == a) ? selectorLabel : a);
		
		// Are we rendering the tail node in a binder bread crumb tree?
		if (binderBreadcrumbTail) {
			// Yes!  Make the selector display inline...
			selectorLabel.addStyleName("displayInline");

			// ...and create panel to hold the various widgets we'll
			// ...lay down for the tail node.
			final FlowPanel fp = new FlowPanel();
			fp.addStyleName("breadCrumb_ContentTail_Panel");
		    m_rootPanel.add(fp);

			// Is the node above the tail binder a folder?
			final TreeInfo prevTI = getPreviousTI(ti);
			if ((null != prevTI) && (isTrash()  || (BinderType.FOLDER == prevTI.getBinderInfo().getBinderType()))) {
				// Yes!  Is it for a binder other than the profiles
				// or team workspaces root?
				if ((!(ti.getBinderInfo().isBinderProfilesRootWS())) && (!(ti.getBinderInfo().isBinderTeamsRootWS()))) {
					// Yes!  Add an up button to navigate to it...
					addUpButton(
						fp,
						getMessages().treePreviousFolder(),
						new Command() {
							@Override
							public void execute() {
								// If we can change contexts...
								if (canChangeContext()) {
									// ...select the appropriate TreeInfo...
									selectBinder(prevTI);
									
									// ...and change the context.
									GwtTeaming.fireEvent(
										new ChangeContextEvent(
											buildOnSelectBinderInfo(
												prevTI)));
								}
							}
						});
				}
				
				// ...add the image and anchor for the binder...
				addTIImageAndAnchor(fp, ti, selectorW);
				
				// ...and add the access to the binder configuration
				// ...menu.
				addBinderConfig(fp, ti);
			}

			// No the node above the tail binder is not a folder!  Is
			// site navigation available?
			else if (!(WorkspaceTreeControl.siteNavigationAvailable())) {
				// No!  Construct a helper to coordinate handling
				// getting the collection type...
				final GetSidebarCollectionHelper gscHelper = new GetSidebarCollectionHelper(
					fp,			// The FlowPanel we're constructing. 
					ti,			// The TreeInfo  we're constructing from.
					selectorW);	// The Anchor for this TreeInfo.
				
				// ...and fire a get sidebar collection event.
				GwtTeaming.fireEvent(
					new GetSidebarCollectionEvent(
						new CollectionCallback() {
							/**
							 * Callback method for the event.
							 * 
							 * This method will be called by the event
							 * handler(s) with the current collection
							 * type.
							 * 
							 * @param collectionType
							 */
							@Override
							public void collection(final CollectionType collectionType) {
								// Are we still waiting for a response?
								gscHelper.clearGetCollection();
								if (gscHelper.getWaitingForResponse()) {
									// Yes!  We aren't waiting any
									// more.
									gscHelper.setWaitingForResponse(false);
									
									// Did we get a collection type we
									// can generate an up arrow for?
									if (CollectionType.NOT_A_COLLECTION != collectionType) {
										// Perhaps, is it other that
										// the My Files above a Home
										// that servers as the My Files
										// repository?
										boolean isTIMyFilesHome = (ti.getBinderInfo().isFolderHome() && GwtTeaming.getMainPage().getMainPageInfo().isUseHomeAsMyFiles());
										if (!isTIMyFilesHome) {
											// Yes!  Add an up button
											// to navigate to it.
											addUpButton(
												fp,
												getMessages().treePreviousCollection(),
												new Command() {
													/**
													 * The following is
													 * called when the up
													 * is clicked.
													 */
													@Override
													public void execute() {
														// If we can change contexts...
														if (canChangeContext()) {
															// ...fire the event to do so.
															GwtTeaming.fireEventAsync(
																new ShowCollectionEvent(
																	collectionType));
														}
													}
												});
										}
									}
									
									// ...add the image and anchor for
									// ...the binder...
									addTIImageAndAnchor(fp, ti, selectorW);
									
									// ...and add the access to the
									// ...binder configuration menu.
									addBinderConfig(fp, ti);
								}
							}
						}));
			}

			else {
				// No, we must be showing navigation trees!  Add the
				// image and anchor for the binder...
				addTIImageAndAnchor(fp, ti, selectorW);
				
				// ...and add the access to the
				// ...binder configuration menu.
				addBinderConfig(fp, ti);
			}
		}
		
		else {
			// No, we aren't rendering the tail node in a binder bread
			// crumb tree!  Simply add the expander and selector to the
			// FlexTable.
			nodeGrid.setWidget(0, 0, expanderWidget);
			nodeGrid.setWidget(0, 1, selectorW     );
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
	private void rerenderNode(TreeInfo ti, FlexTable nodeGrid) {
		clearNode(nodeGrid);
		renderNode(ti, nodeGrid);
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
