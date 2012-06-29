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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.TreeMode;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.WorkspaceTreeControlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class used for the content of the bread crumb tree in the binder
 * views.  
 * 
 * @author drfoster@novell.com
 */
public class BreadCrumbPanel extends ToolPanelBase
	implements
	// Event handlers implemented by this class.
		TreeNodeCollapsedEvent.Handler,
		TreeNodeExpandedEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel				m_fp;						// The panel holding the content.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.TREE_NODE_COLLAPSED,
		TeamingEvents.TREE_NODE_EXPANDED,
	};
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private BreadCrumbPanel(RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-breadCrumbPanel");
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the BreadCrumbPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(BreadCrumbPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				BreadCrumbPanel bcp = new BreadCrumbPanel(containerResizer, binderInfo, toolPanelReady);
				tpClient.onSuccess(bcp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_BreadCrumbPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Asynchronously handles the panel being resized.
	 */
	private void doResizeAsync() {
		ScheduledCommand doResize = new ScheduledCommand() {
			@Override
			public void execute() {
				doResizeNow();
			}
		};
		Scheduler.get().scheduleDeferred(doResize);
	}
	
	/*
	 * Synchronously handles the panel being resized.
	 */
	private void doResizeNow() {
		panelResized();
	}
	
	/*
	 * Asynchronously construct's the contents of the bread crumb
	 * panel.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the panel.
	 */
	private void loadPart1Now() {
		// Are we displaying a bread crumb panel for a collection?
		if (m_binderInfo.isBinderCollection()) {
			// Yes!  We don't need a tree, just the image and title.
			// Create the panel for it...
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-breadCrumbCollection-panel");

			// ...create the image...
			TreeInfo ti = new TreeInfo();
			ti.setBinderInfo(m_binderInfo);
			Image i = GwtClientHelper.buildImage(ti.getBinderImage(BinderIconSize.getBreadCrumbIconSize()).getSafeUri().asString());
			i.addStyleName("vibe-breadCrumbCollection-image");
			int width  = BinderIconSize.getBreadCrumbIconSize().getBinderIconWidth();
			if ((-1) != width) {
				i.setWidth(width + "px");
			}
			int height = BinderIconSize.getBreadCrumbIconSize().getBinderIconHeight();
			if ((-1) != height) {
				i.setHeight(height + "px");
			}
			fp.add(i);

			// ...create the title label...
			InlineLabel il = new InlineLabel(m_binderInfo.getBinderTitle());
			il.addStyleName("vibe-breadCrumbCollection-label");
			fp.add(il);

			// ...tie it all together and tell our container that we're
			// ...ready.
			m_fp.add(fp);
			toolPanelReady();
		}
		
		else {
			// No, we aren't displaying a bread crumb panel for a
			// collection!  We need the full bread crumb tree.
			WorkspaceTreeControl.createAsync(
					GwtTeaming.getMainPage(),
					m_binderInfo,
					m_binderInfo.isBinderTrash(),
					TreeMode.HORIZONTAL_BINDER,
					new WorkspaceTreeControlClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do other than tell our container that
					// we're ready.  The error is handled in the
					// asynchronous provider.
					toolPanelReady();
				}
				
				@Override
				public void onSuccess(WorkspaceTreeControl wsTreeCtrl) {
					// Add the tree to the panel and tell our container
					// that we're ready.
					m_fp.add(wsTreeCtrl);
					toolPanelReady();
				}
			});
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
	 * Handles TreeNodeCollapsedEvent's received by this class.
	 * 
	 * Implements the TreeNodeCollapsedEvent.Handler.onTreeNodeCollapsed()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onTreeNodeCollapsed(TreeNodeCollapsedEvent event) {
		// If this is our bread crumb tree being collapsed...
		Long binderId = event.getBinderInfo().getBinderIdAsLong();
		if ((binderId.equals(m_binderInfo.getBinderIdAsLong())) && event.getTreeMode().isHorizontalBinder()) {
			// ...tell our container about the size change.
			doResizeAsync();
		}
	}
	
	/**
	 * Handles TreeNodeExpandedEvent's received by this class.
	 * 
	 * Implements the TreeNodeExpandedEvent.Handler.onTreeNodeExpanded()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onTreeNodeExpanded(TreeNodeExpandedEvent event) {
		// If this is our bread crumb tree being expanded...
		Long binderId = event.getBinderInfo().getBinderIdAsLong();
		if ((binderId.equals(m_binderInfo.getBinderIdAsLong())) && event.getTreeMode().isHorizontalBinder()) {
			// ...tell our container about the size change.
			doResizeAsync();
		}
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
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}
	
	/**
	 * Called from the binder view to allow the panel to do any work
	 * required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets and reload the bread crumb tree.
		m_fp.clear();
		loadPart1Async();
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
}
