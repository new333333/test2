/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetVerticalTreeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TreeInfo;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's bread crumbs.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class FindControlBrowsePopup extends TeamingPopupPanel
	implements OpenHandler<TreeItem>,
	// Event handlers implemented by this class.
		BrowseHierarchyExitEvent.Handler
{
	public final static boolean	SHOW_FIND_BROWSER	= false;	// 20130205 (DRF):  Leave false on checkin until I get this working.
	
	private boolean						m_foldersOnly;				// true -> Only folders are to be returned.  false -> Folders and workspaces can be returned.
	private FindCtrl					m_findControl;				// The FindCtrl widget we're browsing for.
	private GwtFolder					m_findStart;				// The starting point to browse.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Tree						m_browser;					// The Tree containing the hierarchy being browsed.
	private TreeInfo					m_rootTI;					// The TreeInfo containing the root of the tree being browsed.
	
	protected final static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		// Miscellaneous events.
		TeamingEvents.BROWSE_HIERARCHY_EXIT,
	};
	
	/*
	 * Inner class used to wrap items for the find control tree.
	 */
	private static class FindTreeItem extends TreeItem {
		private boolean		m_dummyExpander;	//
		private TreeInfo	m_ti;				// The TreeInfo for this FindTreeItem.

		/**
		 * Constructor method.
		 * 
		 * @param ti
		 * @param dummyExpander
		 */
		public FindTreeItem(TreeInfo ti, boolean dummyExpander) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			setDummyExpander(dummyExpander);
			setTreeInfo(     ti           );
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean  isDummyExpander() {return m_dummyExpander;}
		public TreeInfo getTreeInfo()     {return m_ti;           }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setDummyExpander(boolean  dummyExpander) {m_dummyExpander = dummyExpander;}
		public void setTreeInfo(     TreeInfo ti)            {m_ti            = ti;           }
	}
	
	/*
	 * Creates an empty popup panel, specifying its auto-hide and modal
	 * properties.
	 */
	private FindControlBrowsePopup(FindCtrl findControl, GwtFolder findStart, boolean foldersOnly) {
		// Initialize the super class...
		super(true, false);	// true -> Auto hide.  false -> Not modal.
		addStyleName("vibe-findBrowser-popup");
		GwtClientHelper.scrollUIForPopup(this);
		GwtClientHelper.rollDownPopup(   this);
		
		// ...store the parameters...
		m_findControl = findControl;
		m_findStart   = findStart;
		m_foldersOnly = foldersOnly;
		
		// ...and construct the panel...
		m_browser = new Tree();
		m_browser.addStyleName("vibe-findBrowser-tree");
		m_browser.addOpenHandler(this);
		setWidget(m_browser);
		showRelativeTo(findControl);
		
		// ...and load the tree.
		populateTree();
	}

	/**
	 * Runs the tree browser on behalf of the FindControl.
	 * 
	 * @param findControl
	 * @param findStart
	 */
	public static FindControlBrowsePopup doBrowse(FindCtrl findControl, GwtFolder findStart) {
		boolean		foldersOnly;
		SearchType	st = findControl.getSearchType();
		switch (st) {
		case FOLDERS:  foldersOnly = true;  break;
		case PLACES:   foldersOnly = false; break;
			
		default:
			GwtClientHelper.deferredAlert(m_messages.findControlBrowser_Error_NotSupporter(st.name()));
			return null;
		}
		
		return new FindControlBrowsePopup(findControl, findStart, foldersOnly);
	}

	/*
	 * Reads the root TreeInfo from the server and uses it to populate
	 * the tree.
	 */
	private void populateTree() {
		// Where should we start browsing from?
		final String binderId;
		if (null != m_findStart)
		     binderId = m_findStart.getFolderId();
		else binderId = GwtClientHelper.getRequestInfo().getCurrentUserWorkspaceId();
		
		// Read the tree information we need to start.
		GetVerticalTreeCmd cmd = new GetVerticalTreeCmd(binderId, true);	// true -> Find browser mode.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetTree(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response)  {
				// Asynchronously render the tree so that we can
				// release the AJAX request ASAP.
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
		// Attach the widget and register the event handlers.
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
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	/**
	 * Handles BrowseHierarchyExitEvent's received by this class.
	 * 
	 * Implements the BrowseHierarchyExitEvent.Handler.onBrowseHierarchyExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onBrowseHierarchyExit(final BrowseHierarchyExitEvent event) {
		hide();
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
		FindTreeItem ti = ((FindTreeItem) event.getTarget());
		if (!(ti.isDummyExpander())) {
			return;
		}
		ti.setState(false, false);
		
//!		...this needs to be implemented...
		GwtClientHelper.deferredAlert("FindControlBrowsePopup.onOpen( Read Node Expansion ):  ...this needs to be implemented...");
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
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
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
	 * Renders a node in the tree.
	 */
	private void renderTreeNode(FindTreeItem node, TreeInfo ti) {
		// What do we know about the expansion state of the item?
		List<TreeInfo>	tiList       = ti.getChildBindersList();
		boolean			hasTIList    = GwtClientHelper.hasItems(tiList);
		boolean			showExpander = showExpander(ti);

		// Create the FindTreeItem for this node.
		FindTreeItem childNode = new FindTreeItem(ti, (showExpander && (!hasTIList)));
		childNode.addStyleName("vibe-findBrowser-node");
		childNode.setText(ti.getBinderTitle());
		node.addItem(childNode);

		// Do we have child items for this node?
		if (hasTIList) {
			// Yes!  Scan...
			for (TreeInfo childTI:  tiList) {
				// ...and render them...
				renderTreeNode(childNode, childTI);
			}
			
			// ...and if the item should be expanded...
			if (ti.isBinderExpanded()) {
				// ...expand it.
				childNode.setState(true, false);
			}
		}
		
		// No, we don't have child items for this node!  Does it need a
		// dummy expander anyway?
		else if (showExpander) {
			// Yes!  Simply add a blank text item for it.
			childNode.addTextItem("");
		}
	}
	
	/*
	 * Synchronously renders the tree.
	 */
	private void renderTreeNow() {
		// Create a root FindTreeItem...
		FindTreeItem root = new FindTreeItem(m_rootTI, false);
		root.addStyleName("vibe-findBrowser-node");
		root.setText(m_rootTI.getBinderTitle());
		
		// ...add it to the browse Tree...
		m_browser.addItem(root);

		// ...if the root has any children...
		List<TreeInfo> tiList = m_rootTI.getChildBindersList();
		if (GwtClientHelper.hasItems(tiList)) {
			// ...scan them...
			for (TreeInfo ti:  tiList) {
				// ...and render them into the tree.
				renderTreeNode(root, ti);
			}
		}
		
		// We always show the root expanded.
		root.setState(true, false);
	}

	/*
	 * Returns true if a TreeInfo should have an expander and false
	 * otherwise.
	 */
	private boolean showExpander(TreeInfo ti) {
		return (ti.isBucket() || (0 < ti.getBinderChildren()));
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
