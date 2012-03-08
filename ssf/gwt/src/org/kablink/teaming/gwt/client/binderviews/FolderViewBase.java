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

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.binderviews.BreadCrumbPanel;
import org.kablink.teaming.gwt.client.binderviews.DescriptionPanel;
import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.FilterPanel;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Folder view base calls.  All folder views should be based off this
 * class.
 * 
 * Provides common housekeeping for a folder view including:
 * 1. Managing various tool panels (e.g., bread crumbs,
 *    accessories, ...)
 * 2. Provides access to various pieces of information about the folder
 *    (e.g., BinderInfo, folder display data, ...)
 * 3. Manages load synchronization for the various components that make
 *    up a folder view.
 * 
 * @author drfoster@novell.com
 */
public abstract class FolderViewBase extends ViewBase implements ToolPanelReady {
	private BinderInfo							m_folderInfo;			// A BinderInfo object that describes the folder being viewed.
	private boolean								m_allowColumnSizing;	// true -> Add the column sizing entry menu item.  false -> Don't.
	private boolean								m_viewReady;			// Set true once the view and all its components are ready.
	private FolderDisplayDataRpcResponseData	m_folderDisplayData;	// Various pieces of display information about the folder (sorting, page size, column widths, ...) 
	private int									m_readyComponents;		// Tracks items as they become ready.
	private List<ToolPanelBase>					m_toolPanels;			// List of the various tools panels that appear in the view.
	private String								m_styleBase;			// Base name for the view specific styles to use for this view.
	private VibeFlowPanel						m_flowPanel;			// The flow panel used to hold the view specific content of the view.
	private VibeVerticalPanel					m_verticalPanel;		// The vertical panel that holds all components of the view, both common and view specific.

	// Control whether a FilterPanel can ever be instantiated.  true
	// and they can and false and they can't.
	//
	// Note:  The FilterPanel implements what amounts to same filter
	//    handling as was done in the old JSP code.  Filters are now
	//    integrated in the entry menu so that filter panel is no
	//    loner needed.
	private final static boolean SHOW_LEGACY_FILTERS	= false;
	
	// The following define the indexes into a VibeVerticalPanel of the
	// various panels that make up a folder view.
	public final static int BREADCRUMB_PANEL_INDEX		= 0;
	public final static int ACCESSORY_PANEL_INDEX		= 1;
	public final static int DESCRIPTION_PANEL_INDEX		= 2;
	public final static int FILTER_PANEL_INDEX			= 3;
	public final static int ENTRY_MENU_PANEL_INDEX		= 4;
	public final static int VIEW_CONTENT_PANEL_INDEX	= 5;
	public final static int FOOTER_PANEL_INDEX			= 6;

	public final static int MINIMUM_CONTENT_HEIGHT		= 150;	// The minimum height (in pixels) of a the data table widget.
	public final static int NO_VSCROLL_ADJUST			=  28;	// Height adjustment required so there's no vertical scroll bar by default.

	/*
	 * Enumeration that identifies the various optional panels that
	 * make up a folder view.
	 */
	protected enum FolderPanels {
		BREADCRUMB,
		ACCESSORIES,
		DESCRIPTION,
		FILTER,
		ENTRY_MENU,
		FOOTER,
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public FolderViewBase(BinderInfo folderInfo, ViewReady viewReady, String styleBase, boolean allowColumnSizing) {
		// Initialize the super class...
		super(viewReady);

		// ...store the parameters...
		m_folderInfo        = folderInfo;
		m_styleBase         = ((GwtClientHelper.hasString(styleBase) ? styleBase : "vibe-folderView"));
		m_allowColumnSizing = allowColumnSizing;
		
		// ...create the main content panels and initialize the
		// ...composite...
		initWidget(constructInitialContent());

		// ...and finally, asynchronously load the various parts of the
		// ...view.
		loadPart1Async();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	final public BinderInfo                       getFolderInfo()        {return m_folderInfo;                         }	// The binder being viewed.
	final public boolean                          isProfilesRootWS()     {return m_folderInfo.isBinderProfilesRootWS();}	//
	final public boolean                          isTrash()              {return m_folderInfo.isBinderTrash();         }	//
	final public FolderDisplayDataRpcResponseData getFolderDisplayData() {return m_folderDisplayData;                  }	//
	final public List<ToolPanelBase>              getToolPanels()        {return m_toolPanels;                         }	//
	final public Long                             getFolderId()          {return m_folderInfo.getBinderIdAsLong();     }	//
	final public VibeFlowPanel                    getFlowPanel()         {return m_flowPanel;                          }	// Flow panel holding the view's content (no toolbars, ...)

	/*
	 * Returns a Widget to use for tool panels that aren't used.
	 */
	private Widget buildToolPanelPlaceholder() {
		InlineLabel reply = new InlineLabel();
		reply.removeStyleName("gwt-InlineLabel");
		return reply;
	}
	
	/*
	 * Checks how many items are ready and once everything is, calls
	 * the super class' viewReady() method.
	 */
	private void checkReadyness() {
		// If everything's ready...
		int toolPanels = m_toolPanels.size();
		if ((toolPanels + 1) == m_readyComponents) {	// Count of tool panels plus 1 for the view itself.
			// ...tell the super class.
			m_viewReady = true;
			viewComplete();
			super.viewReady();
		}
	}
	
	/*
	 * Creates the initial content panels, ... required by folder
	 * views.
	 */
	private VibeFlowPanel constructInitialContent() {
		// Initialize various data members of the class...
		initDataMembers();
		
		// ...create the main panel for the content...
		VibeFlowPanel mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName("vibe-folderViewBase " + m_styleBase + " vibe-verticalScroll");

		// ...set the sizing adjustments the account for the padding in
		// ...the vibe-folderViewBase style...
		final int padAdjust = (2 * GwtConstants.PANEL_PADDING);
		setContentHeightAdjust(getContentHeightAdjust() - padAdjust);
		setContentWidthAdjust( getContentWidthAdjust()  - padAdjust);

		// ...create a vertical panel to holds the layout that flows
		// ...down the view...
		m_verticalPanel = new VibeVerticalPanel();
		m_verticalPanel.addStyleName(m_styleBase + "VerticalPanel");
	
		// ...create a flow panel to put the main content...
		m_flowPanel = new VibeFlowPanel();
		m_flowPanel.addStyleName(m_styleBase + "FlowPanel");
		
		// ...and finally, tie everything together.
		m_verticalPanel.add(m_flowPanel);
		m_verticalPanel.addBottomPad();
		mainPanel.add(m_verticalPanel);
		
		return mainPanel;
	}

	/**
	 * Called to construct the view.
	 * 
	 * Implemented by classes that extend this base class so that they
	 * can construct and reset the main content of their view.
	 */
	public abstract void constructView();
	public abstract void resetView();
	
	/*
	 * Asynchronously tells the view to construct itself.
	 */
	private void constructViewAsync() {
		ScheduledCommand doConstructView = new ScheduledCommand() {
			@Override
			public void execute() {
				constructView();
			}
		};
		Scheduler.get().scheduleDeferred(doConstructView);
	}

	/**
	 * Scan the defined tool panels for an entry menu panel and returns
	 * it.
	 * 
	 * @return
	 */
	public EntryMenuPanel getEntryMenuPanel() {
		for (ToolPanelBase tpb:  getToolPanels()) {
			if (tpb instanceof EntryMenuPanel) {
				return ((EntryMenuPanel) tpb);
			}
		}
		return null;
	}
	
	/**
	 * Scan the defined tool panels for a footer panel and returns it.
	 * 
	 * @return
	 */
	public FooterPanel getFooterPanel() {
		for (ToolPanelBase tpb:  getToolPanels()) {
			if (tpb instanceof FooterPanel) {
				return ((FooterPanel) tpb);
			}
		}
		return null;
	}

	/**
	 * Returns true if a panel should be loaded and false otherwise.
	 *
	 * Classes that extend this class can override this method to
	 * inhibit the inclusion of various panels as they see fit.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	protected boolean includePanel(FolderPanels folderPanel) {
		// Unless overridden, all panels are included.
		return true;
	}
	
	/*
	 * Initializes various data members for the class.
	 */
	private void initDataMembers() {
		// Allocate a List<ToolPanelBase> to track the tool panels
		// created for the view.
		m_toolPanels = new ArrayList<ToolPanelBase>();
	}

	/**
	 * Inserts a tool panel into the view.
	 * 
	 * @param tpb
	 * @param tpIndex
	 */
	final public void insertToolPanel(ToolPanelBase tpb, int tpIndex) {
		m_toolPanels.add(tpb);
		m_verticalPanel.insert(tpb, tpIndex);
	}
	
	/**
	 * Inserts a tool panel place holder into the view.
	 * 
	 * @param tpb
	 * @param tpIndex
	 */
	final public void insertToolPanelPlaceholder(int tpIndex) {
		m_verticalPanel.insert(buildToolPanelPlaceholder(), tpIndex);
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart1Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.BREADCRUMB))) {
			// ...we don't show the bread crumbs.
			insertToolPanelPlaceholder(BREADCRUMB_PANEL_INDEX);
			loadPart2Async();
			return;
		}
		
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart1Now() {
		BreadCrumbPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, BREADCRUMB_PANEL_INDEX);
				loadPart2Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart2Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.ACCESSORIES))) {
			// ...we don't show the accessories.
			insertToolPanelPlaceholder(ACCESSORY_PANEL_INDEX);
			loadPart3Async();
			return;
		}
		
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart2Now() {
		AccessoriesPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, ACCESSORY_PANEL_INDEX);
				loadPart3Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart3Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.DESCRIPTION))) {
			// ...we don't show the description.
			insertToolPanelPlaceholder(DESCRIPTION_PANEL_INDEX);
			loadPart4Async();
			return;
		}
		
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart3Now() {
		DescriptionPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, DESCRIPTION_PANEL_INDEX);
				loadPart4Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	@SuppressWarnings("unused")
	private void loadPart4Async() {
		// For classes that don't want it...
		if ((!SHOW_LEGACY_FILTERS) || (!(includePanel(FolderPanels.FILTER)))) {
			// ...we don't show the filter.
			insertToolPanelPlaceholder(FILTER_PANEL_INDEX);
			loadPart5Async();
			return;
		}
		
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart4Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart4Now() {
		FilterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FILTER_PANEL_INDEX);
				loadPart5Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart5Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.ENTRY_MENU))) {
			// ...we don't show the entry menu.
			insertToolPanelPlaceholder(ENTRY_MENU_PANEL_INDEX);
			loadPart6Async();
			return;
		}
		
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart5Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart5Now() {
		EntryMenuPanel.createAsync(this, m_folderInfo, m_allowColumnSizing, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, ENTRY_MENU_PANEL_INDEX);
				loadPart6Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart6Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.FOOTER))) {
			// ...we don't show the footer.
			insertToolPanelPlaceholder(FOOTER_PANEL_INDEX);
			loadPart7Async();
			return;
		}
		
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart6Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart6Now() {
		FooterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FOOTER_PANEL_INDEX);
				loadPart7Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart7Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart7Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}

	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart7Now() {
		GwtClientHelper.executeCommand(
				new GetFolderDisplayDataCmd(m_folderInfo),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderDisplayData(),
					m_folderInfo.getBinderIdAsLong());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the core folder display data and tell the view
				// to construct itself.
				m_folderDisplayData = ((FolderDisplayDataRpcResponseData) response.getResponseData());
				constructViewAsync();
			}
		});
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Overrides the ViewBase.onResize() method.
	 */
	@Override
	public void onResize() {
		// Pass the resize on to the super class and do what we need to
		// do to resize the view.
		super.onResize();
		resizeViewAsync();
	}

	/**
	 * Does what's necessary to reset the view.
	 */
	final public void resetContent() {
		// Clear the flow panel's content...
		m_flowPanel.clear();
		
		// ...tell the tool panels to perform any resetting they need
		// ...to do...
		for (ToolPanelBase tpb:  m_toolPanels) {
			tpb.resetPanel();
		}
		
		// ...and reset any other information.
		m_readyComponents = 0;
		m_viewReady       = false;
	}
	
	/**
	 * Called to resize the view.
	 * 
	 * Implemented by classes that extend this base class so that they
	 * can take whatever action is necessary to resize their view.
	 */
	public abstract void resizeView();
	
	/*
	 * Asynchronously tells the view to resize itself.
	 */
	private void resizeViewAsync() {
		ScheduledCommand doResize = new ScheduledCommand() {
			@Override
			public void execute() {
				resizeView();
			}
		};
		Scheduler.get().scheduleDeferred(doResize);
	}

	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady(ToolPanelBase toolPanel) {
		if (!m_viewReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else if (GwtClientHelper.getRequestInfo().isDebugUI()) {
			Window.alert("FolderViewBase.toolPanelReady( *Internal Error* ):  Unexpected call to toolPanelReady() method.");
		}
	}

	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * This method is defined for classes that extend this class to
	 * override so that they can do any processing that they require
	 * once their a view is complete.
	 */
	public void viewComplete() {
		// Nothing to do.
	}
	
	/**
	 * Called by classes that extend this base class so that it can
	 * inform the world that its view is ready to go.
	 * 
	 * Overrides the ViewBase.viewReady() method.
	 */
	@Override
	public void viewReady() {
		if (!m_viewReady) {
			m_readyComponents += 1;
			checkReadyness();
		}
		
		else if (GwtClientHelper.getRequestInfo().isDebugUI()) {
			Window.alert("FolderViewBase.viewReady( *Internal Error* ):  Unexpected call to viewReady() method.");
		}
	}
}
