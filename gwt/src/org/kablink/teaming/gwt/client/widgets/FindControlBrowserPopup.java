/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.ExpandVerticalBucketCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetDefaultStorageIdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalNodeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TreeInfo;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A tree browser selection widget used in conjunction with a FindCtrl.
 * 
 * @author drfoster@novell.com
 */
public class FindControlBrowserPopup extends TeamingPopupPanel
	implements OpenHandler<TreeItem>, SelectionHandler<TreeItem>
{
	private boolean						m_foldersOnly;				// true -> Only folders are to be returned.  false -> Folders and workspaces can be returned.
	private FindCtrl					m_findControl;				// The FindCtrl widget we're browsing for.
	private FindTreeNode				m_findStartNode;			// The node corresponding to m_findStart.
	private GwtFolder					m_findStart;				// The starting point to browse.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Long						m_findStartBinderId;		// The ID of the starting binder specified when the tree was loaded.
	private Tree						m_browser;					// The Tree containing the hierarchy being browsed.
	private TreeInfo					m_rootTI;					// The TreeInfo containing the root of the tree being browsed.
	
	protected final static GwtTeamingImageBundle	m_images   = GwtTeaming.getImageBundle();	// Access to Vibe's image resources.
	protected final static GwtTeamingMessages		m_messages = GwtTeaming.getMessages();		// Access to Vibe's localized string resources.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/*
	 * Inner class used to encapsulate the Widget's used for a tree
	 * node.
	 */
	private static class FindNodeWidgets {
		private Image			m_nodeImg;		//
		private InlineLabel		m_nodeTxt;		//
		private VibeFlowPanel	m_nodePanel;	//

		/**
		 * Constructor method.
		 * 
		 * @param nodePanel
		 * @param nodeImg
		 * @param nodeTxt
		 */
		public FindNodeWidgets(VibeFlowPanel nodePanel, Image nodeImg, InlineLabel nodeTxt) {
			// Initialize the super class...
			super();

			// ...and store the parameters.
			setNodePanel(nodePanel);
			setNodeImg(  nodeImg  );
			setNodeTxt(  nodeTxt  );
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public VibeFlowPanel getNodePanel() {return m_nodePanel;}
		@SuppressWarnings("unused")
		public Image         getNodeImg()   {return m_nodeImg;  }
		public InlineLabel   getNodeTxt()   {return m_nodeTxt;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setNodePanel(VibeFlowPanel nodePanel) {m_nodePanel = nodePanel;}
		public void setNodeImg(  Image         nodeImg)   {m_nodeImg   = nodeImg;  }
		public void setNodeTxt(  InlineLabel   nodeTxt)   {m_nodeTxt   = nodeTxt;  }
	}
	
	/*
	 * Inner class used to wrap items for the nodes in the find control
	 * browser.
	 */
	private static class FindTreeNode extends TreeItem {
		private boolean			m_expandPlaceholder;	// true -> This node is a placeholder for an expandable node whose contents have yet to be read.
		private FindNodeWidgets	m_nw;					// The FindNodeWidgets encapsulating the node's widgets.
		private TreeInfo		m_ti;					// The TreeInfo for this FindTreeNode.

		/**
		 * Constructor method.
		 * 
		 * @param nw
		 * @param ti
		 * @param expandPlaceholder
		 */
		public FindTreeNode(FindNodeWidgets nw, TreeInfo ti, boolean expandPlaceholder) {
			// Initialize the super class...
			super(nw.getNodePanel());
			
			// ...and store the parameters.
			setExpandPlaceholder(expandPlaceholder);
			setFindNodeWidgets(  nw               );
			setTreeInfo(         ti               );
			
			addStyleName("vibe-findBrowser-node");
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean         isBucket()            {return m_ti.isBucket();    }
		public boolean         isExpandPlaceholder() {return m_expandPlaceholder;}
		public FindNodeWidgets getFindNodeWidgets()  {return m_nw;               }
		public TreeInfo        getTreeInfo()         {return m_ti;               }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setExpandPlaceholder(boolean         expandPlaceholder) {m_expandPlaceholder = expandPlaceholder;}
		public void setFindNodeWidgets(  FindNodeWidgets nw)                {m_nw                = nw;               }
		public void setTreeInfo(         TreeInfo        ti)                {m_ti                = ti;               }

		/**
		 * Does what's necessary to ensure the node is scrolled into
		 * view.
		 */
		public void scrollIntoView() {
			// If we can access the node's Element...
			Element fsnE = getElement();
			if (null != fsnE) {
				// ...ensure its scrolled into view.
				fsnE.scrollIntoView();
			}
		}
		
		/**
		 * Sets the styles on a text
		 */
		public void setFindStart() {
			getFindNodeWidgets().getNodeTxt().addStyleName("vibe-findBrowser-nodeTxtStart");			
		}
	}

	/*
	 * Creates an empty popup panel, specifying its auto-hide and modal
	 * properties.
	 */
	private FindControlBrowserPopup(FindCtrl findControl, GwtFolder findStart, boolean foldersOnly) {
		// Initialize the super class...
		super(true, false);	// true -> Auto hide.  false -> Not modal.
		addStyleName("vibe-findBrowser-popup");
		GwtClientHelper.scrollUIForPopup(this);
		
		// ...store the parameters...
		m_findControl = findControl;
		m_findStart   = findStart;
		m_foldersOnly = foldersOnly;
		
		// ...construct the panel...
		ScrollPanel sp = new ScrollPanel();
		sp.addStyleName("vibe-findBrowser-scrollPanel");
		m_browser = new Tree();
		m_browser.addStyleName("vibe-findBrowser-tree");
		m_browser.addOpenHandler(     this);
		m_browser.addSelectionHandler(this);
		sp.setWidget(m_browser);
		setWidget(sp);
		showRelativeTo(findControl);
		
		// ...and load the tree.
		loadPart1Async();
	}

	/*
	 * Creates a FindTreeNode that wraps the Widgets that constitute a
	 * browser tree node based on the given TreeInfo.
	 */
	private FindTreeNode createNode(TreeInfo ti, boolean expandPlaceholder) {
		FindTreeNode reply = new FindTreeNode(createNodeWidgets(ti), ti, expandPlaceholder);
		if (isFindStart(reply)) {
			reply.setFindStart();
			m_findStartNode = reply;
		}
		return reply;
	}
	
	/*
	 * Creates the FindNodeWidgets a tree node should contain.
	 */
	private FindNodeWidgets createNodeWidgets(TreeInfo ti) {
		// Create a panel to hold the node's widgets...
		boolean isSelectable = isSelectable(ti);
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-findBrowser-nodePanel");
		if (isSelectable)
		     fp.addStyleName("vibe-findBrowser-nodePanelActive");
		else fp.addStyleName("vibe-findBrowser-nodePanelInert" );

		// ...and the appropriate image...
		Image binderImg = GwtClientHelper.buildImage(((String) null), ti.getBinderHoverImage());
		ti.setBinderUIImage(binderImg);
		setBinderImageResource(ti, BinderIconSize.SMALL);
		binderImg.addStyleName("vibe-findBrowser-nodeImg");
		fp.add(binderImg);

		// ...add the appropriate title...
		InlineLabel il = new InlineLabel((ti.isBucket() ? ti.getBucketInfo().getBucketTitle() : ti.getBinderTitle()));
		il.addStyleName("vibe-findBrowser-nodeTxt");
		if (isSelectable)
		     il.addStyleName("vibe-findBrowser-nodeTxtActive");
		else il.addStyleName("vibe-findBrowser-nodeTxtInert" );
		fp.add(il);

		// ...and return the panel as the node's Widget.
		return new FindNodeWidgets(fp, binderImg, il);
	}
	
	/**
	 * Runs the tree browser on behalf of a FindCtrl.
	 * 
	 * @param findControl
	 * @param findStart
	 * 
	 * @return
	 */
	public static FindControlBrowserPopup doBrowse(FindCtrl findControl, GwtFolder findStart) {
		// What mode is the tree browser running in?
		boolean		foldersOnly;
		SearchType	st = findControl.getSearchType();
		switch (st) {
		case FOLDERS:  foldersOnly = true;  break;
		case PLACES:   foldersOnly = false; break;
			
		default:
			// We only support browsing for folders and places (i.e.,
			// folders and workspace.)  Anything else is rejected.
			GwtClientHelper.deferredAlert(m_messages.findControlBrowser_Error_NotSupporter(st.name()));
			return null;
		}

		// Instantiate and return an instance of this tree browser.
		return
			new FindControlBrowserPopup(
				findControl,
				findStart,
				foldersOnly);
	}

	/*
	 * Called to select the binder in the TreeInfo in the find control.
	 */
	private void doSelect(TreeInfo ti) {
		// Map the TreeInfo to a GwtFolder...
		final String binderId = ti.getBinderInfo().getBinderId();
		GetFolderCmd cmd = new GetFolderCmd(null, binderId, true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolder(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...put the selection into affect...
				GwtFolder selectedItem = ((GwtFolder) response.getResponseData());
				if (null != selectedItem) {
					FindCtrl.setSelectedItem(m_findControl, selectedItem);
				}
				
				// ...and hide the popup.
				hide();
			}
		});
	}

	/*
	 * Asynchronously expands a bucket node.
	 */
	private void expandBucketAsync(final FindTreeNode node) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				expandBucketNow(node);
			}
		});
	}
	
	/*
	 * Synchronously expands a bucket node.
	 */
	private void expandBucketNow(final FindTreeNode node) {
		// Expand the bucket.
		final TreeInfo ti = node.getTreeInfo();
		ExpandVerticalBucketCmd cmd = new ExpandVerticalBucketCmd(ti.getBucketInfo(), true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_ExpandBucket());
				node.getChild(0).remove();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did we get any children from the expansion?
				TreeInfo expandedTI = ((TreeInfo) response.getResponseData());
				List<TreeInfo> expandedChildren = expandedTI.getChildBindersList();
				ti.setChildBindersList(expandedChildren);
				boolean hasExpandedChildren = GwtClientHelper.hasItems(expandedChildren);
				if (hasExpandedChildren) {
					// Yes!  Scan...
					for (TreeInfo childTI:  expandedChildren) {
						// ...and render them...
						renderNode(node, childTI);
					}
				}
				
				// ...remove the dummy expansion node...
				node.getChild(0).remove();
				if (hasExpandedChildren) {
					// ..and expand what we just added.
					ti.setBinderExpanded(true);
					node.setState(true, false);
				}
			}
		});
	}
	
	/*
	 * Asynchronously expands a node.
	 */
	private void expandNodeAsync(final FindTreeNode node) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				expandNodeNow(node);
			}
		});
	}
	
	/*
	 * Synchronously expands a node.
	 */
	private void expandNodeNow(final FindTreeNode node) {
		// Expand the node.
		final TreeInfo ti = node.getTreeInfo();
		GetVerticalNodeCmd cmd = new GetVerticalNodeCmd(ti.getBinderInfo().getBinderId(), true);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTree(),
					ti.getBinderInfo().getBinderId());
				node.getChild(0).remove();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Did we get any children from the expansion?
				TreeInfo expandedTI = ((TreeInfo) response.getResponseData());
				List<TreeInfo> expandedChildren = expandedTI.getChildBindersList();
				ti.setChildBindersList(expandedChildren);
				boolean hasExpandedChildren = GwtClientHelper.hasItems(expandedChildren);
				if (hasExpandedChildren) {
					// Yes!  Scan...
					for (TreeInfo childTI:  expandedChildren) {
						// ...and render them...
						renderNode(node, childTI);
					}
				}
				
				// ...remove the dummy expansion node...
				node.getChild(0).remove();
				if (hasExpandedChildren) {
					// ..and expand what we just added.
					ti.setBinderExpanded(true);
					node.setState(true, false);
				}
			}
		});
	}
	
	/*
	 * Returns true if a TreeInfo should have an expander and false
	 * otherwise.
	 */
	private boolean isExpandable(TreeInfo ti) {
		return (ti.isBucket() || (0 < ti.getBinderChildren()));
	}

	/*
	 * Returns true if a tree node should be marked as the starting
	 * node in the tree browser and false otherwise.
	 */
	private boolean isFindStart(TreeInfo ti) {
		// If the TreeInfo is a bucket...
		if (ti.isBucket()) {
			// ...it can't be selected.
			return false;
		}

		// Return true if the binder ID we're tracking as the starting
		// binder matches the binder ID in the TreeInfo and false
		// otherwise. 
		return (
			(null != m_findStartBinderId) &&
			m_findStartBinderId.equals(
				ti.getBinderInfo().getBinderIdAsLong()));
	}
	
	private boolean isFindStart(FindTreeNode node) {
		// Always use the initial form of the method.
		return isFindStart(node.getTreeInfo());
	}
	
	/*
	 * Returns true if a tree node can be selected for the find control
	 * and false if it can't.
	 */
	private boolean isSelectable(TreeInfo ti) {
		// If the TreeInfo is a bucket...
		if (ti.isBucket()) {
			// ...it can't be selected.
			return false;
		}

		// If only folders can be selected and the TreeInfo is not a
		// folder... 
		if (m_foldersOnly && (!(ti.getBinderInfo().isBinderFolder()))) {
			// ...it can't be selected.
			return false;
		}
		
		// If we get here, the TreeInfo can be selected.
		return true;
	}
	
	private boolean isSelectable(FindTreeNode node) {
		// Always use the initial form of the method.
		return isSelectable(node.getTreeInfo());
	}
	
	/*
	 * Asynchronously loads the next part of the information required
	 * to run the find control browser.
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
	 * Synchronously loads the next part of the information required to
	 * run the find control browser.
	 */
	private void loadPart1Now() {
		// If we were given the starting point for the tree...
		if (null != m_findStart) {
			// ...simply populate it from there.
			loadPart2Async(m_findStart.getFolderId());
		}
		
		else {
			// ...otherwise, get the user's default storage ID from the
			// ...server...
			GwtClientHelper.executeCommand(new GetDefaultStorageIdCmd(), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetDefaultStorageId());
					loadPart2Async(GwtClientHelper.getRequestInfo().getCurrentUserWorkspaceId());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response)  {
					// ...and start from there.
					StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
					String defaultStorageId = responseData.getStringValue();
					loadPart2Async(defaultStorageId);
				}
			});
		}
	}

	/*
	 * Asynchronously loads the next part of the information required
	 * to run the find control browser.
	 */
	private void loadPart2Async(final String startBinderId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now(startBinderId);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the information required to
	 * run the find control browser.
	 */
	private void loadPart2Now(final String startBinderId) {
		// Where should we start browsing from?
		m_findStartBinderId = Long.parseLong(startBinderId);
		m_findStartNode     = null;	// Set when the tree populates.
		
		// Read the tree information...
		GetVerticalTreeCmd cmd = new GetVerticalTreeCmd(startBinderId, true);	// true -> Find browser mode.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTree(),
					startBinderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response)  {
				// ...and render the tree.
				m_rootTI = ((TreeInfo) response.getResponseData());
				renderTreeAsync();
			}
		});
	}
	
	/**
	 * Called when the panel is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget and register any event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the panel is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister any event 
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	/**
	 * Called when a node in the tree is expanded.
	 * 
	 * @param event
	 * 
	 * Implements the OpenHandler.onOpen() method.
	 */
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		// If the node being opened is not an expand placeholder...
		FindTreeNode node = ((FindTreeNode) event.getTarget());
		if (!(node.isExpandPlaceholder())) {
			// ...we've already got its children.  Nothing further
			// ...needs to be done as the tree will manage the
			// ...expansion.
			return;
		}
		
		// Collapse the placeholder node and mark it as no longer being
		// a placeholder...
		node.setState(false,      false);
		node.setExpandPlaceholder(false);

		// ...and expand the node.
		if (node.isBucket())
		     expandBucketAsync(node);
		else expandNodeAsync(  node);
	}
	
	/**
	 * Called when a node in the tree is selected.
	 * 
	 * @param event
	 * 
	 * Implements the SelectionHandler.onSelection() method.
	 */
	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		// If the node can be selected select it, otherwise cancel the
		// event.
		FindTreeNode node = ((FindTreeNode) event.getSelectedItem());
		if (isSelectable(node))
		     doSelect(node.getTreeInfo());
		else DOM.eventCancelBubble(DOM.eventGetCurrentEvent(), true);
		node.setSelected(false);
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we haven't allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}
		
		// If the list of registered events is empty...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Renders a node in the tree.
	 */
	private void renderNode(FindTreeNode node, TreeInfo ti) {
		// What do we know about the expansion state of this item?
		List<TreeInfo>	tiList       = ti.getChildBindersList();
		boolean			hasTIList    = GwtClientHelper.hasItems(tiList);
		boolean			isExpandable = isExpandable(ti);

		// Create the FindTreeNode for this node.
		FindTreeNode childNode = createNode(ti, (isExpandable && (!hasTIList)));
		node.addItem(childNode);

		// Do we have child items for this node?
		if (hasTIList) {
			// Yes!  Scan...
			for (TreeInfo childTI:  tiList) {
				// ...and render them...
				renderNode(childNode, childTI);
			}
			
			// ...and if the item should be expanded...
			if (ti.isBinderExpanded()) {
				// ...expand it.
				childNode.setState(true, false);
			}
		}
		
		// No, we don't have child items for this node!  Does it need a
		// expand placeholder?
		else if (isExpandable) {
			// Yes!  Simply add a blank text item for it.
			childNode.addTextItem("");
		}
	}
	
	/*
	 * Asynchronously renders the tree.
	 */
	private void renderTreeAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				renderTreeNow();
			}
		});
	}
	
	/*
	 * Synchronously renders the tree.
	 */
	private void renderTreeNow() {
		// Create a root FindTreeNode and add it to the browser Tree...
		FindTreeNode rootNode = createNode(m_rootTI, false);
		m_browser.addItem(rootNode);

		// ...if the root has any children...
		List<TreeInfo> tiList = m_rootTI.getChildBindersList();
		if (GwtClientHelper.hasItems(tiList)) {
			// ...scan them...
			for (TreeInfo ti:  tiList) {
				// ...and render them into the tree.
				renderNode(rootNode, ti);
			}
		}
		
		// We always show the root as expanded.
		rootNode.setState(true, false);
		
		// If we have a starting node...
		if (null != m_findStartNode) {
			// ...make sure it's scrolled into view.
			m_findStartNode.scrollIntoView();
		}
	}

	/*
	 * Sets the image resource on a binder image based on its TreeInfo.
	 */
	private void setBinderImageResource(TreeInfo ti, BinderIconSize iconSize, ImageResource defaultImg) {
		// Do we have an Image widget to store the image resource in?
		Image binderImg = ((Image) ti.getBinderUIImage());
		if (null != binderImg) {
			// Yes!  Does the TreeInfo have the name of an icon to use?
			String binderIcon = ti.getBinderIcon(iconSize);
			if ((!(ti.getBinderInfo().isFolderHome())) && GwtClientHelper.hasString(binderIcon)) {
				// Yes!  Set its URL into the Image.
				if (binderIcon.startsWith("/"))
				     binderImg.setUrl(GwtClientHelper.getImagesPath() + binderIcon.substring(1));
				else binderImg.setUrl(GwtClientHelper.getImagesPath() + binderIcon);
			}
			
			else {
				// No, the TreeInfo doesn't have the name of an icon to
				// use!  Does it have an ImageResource to use?
				ImageResource binderImgRes = ti.getBinderImage(iconSize);
				if (null == binderImgRes) {
					// No!  Use the default ImageResource.
					binderImgRes = defaultImg;
				}
				
				// We always display images via their URL so that they
				// can be scaled when necessary. 
				binderImg.setUrl(binderImgRes.getSafeUri());
			}
		}
	}
	
	private void setBinderImageResource(TreeInfo ti, BinderIconSize iconSize) {
		// Always use the initial form of the method.
		setBinderImageResource(ti, iconSize, m_images.spacer1px());
	}
		
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
