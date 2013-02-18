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
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.SpinnerPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * Folder view base class.  All folder views should be based off this
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
	private BinderInfo							m_folderInfo;					// A BinderInfo object that describes the folder being viewed.
	private boolean								m_allowColumnSizing;			// true -> Add the column sizing entry menu item.  false -> Don't.
	private boolean								m_viewReady;					// Set true once the view and all its components are ready.
	private boolean								m_pinning;						//
	private boolean								m_sharedFiles;					//
	private CalendarDisplayDataProvider			m_calendarDisplayDataProvider;	// A CalendarDisplayDataProvider to use to obtain a CalendarDisplayDataRpcResponseData object.
	private FolderDisplayDataRpcResponseData	m_folderDisplayData;			// Various pieces of display information about the folder (sorting, page size, column widths, ...) 
	private int									m_readyComponents;				// Tracks items as they become ready.
	private List<Widget>						m_verticalPanels;				// Tracks the widgets added as vertical panels.
	private SpinnerPopup						m_busySpinner;					//
	private String								m_styleBase;					// Base name for the view specific styles to use for this view.
	private VibeFlowPanel						m_flowPanel;					// The flow panel used to hold the view specific content of the view.
	private VibeFlowPanel						m_verticalFlowPanel;			// The flow panel that holds all the components of the view, both common and view specific, that flow vertically down the view.

	// Control whether a FilterPanel can ever be instantiated.  true
	// and they can and false and they can't.
	//
	// Note:  The FilterPanel implements what amounts to same filter
	//    handling as was done in the old JSP code.  Filters are now
	//    integrated in the entry menu so that filter panel is no
	//    loner needed.
	private final static boolean SHOW_LEGACY_FILTERS	= false;
	
	// Control whether the FooterPanel is shown when in Filr mode.
	private final static boolean SHOW_FILR_FOOTER	= true;
	
	// Amount of time the initial resizing of a view should delay to
	// wait for things to stabilize.
	public final static int INITIAL_RESIZE_DELAY	= 500;
	
	// Amount of time the a view should delay to wait for things to
	// stabilize before querying the entry menu about its contents.
	public final static int ENTRY_MENU_READY_DELAY	= 500;
	
	// The following define the indexes into a VibeVerticalPanel of the
	// various panels that make up a folder view.
	public final static int BREADCRUMB_PANEL_INDEX			= 0;
	public final static int ACCESSORY_PANEL_INDEX			= 1;
	public final static int DESCRIPTION_PANEL_INDEX			= 2;
	public final static int TASK_GRAPHS_PANEL_INDEX			= 3;
	public final static int FILTER_PANEL_INDEX				= 4;
	public final static int BINDER_OWNER_AVATAR_PANEL_INDEX	= 5;
	public final static int ENTRY_MENU_PANEL_INDEX			= 6;
	public final static int CALENDAR_NAVIGATION_PANEL_INDEX	= 7;
	public final static int VIEW_CONTENT_PANEL_INDEX		= 8;
	public final static int FOOTER_PANEL_INDEX				= 9;

	private final static int MINIMUM_CONTENT_HEIGHT		= 150;	// The minimum height (in pixels) of a the data table widget.
	private final static int NO_VSCROLL_ADJUST			=  20;	// Height adjustment required so there's no vertical scroll bar by default.

	/*
	 * Enumeration that identifies the various optional panels that
	 * make up a folder view.
	 */
	protected enum FolderPanels {
		BREADCRUMB,
		ACCESSORIES,
		DESCRIPTION,
		TASK_GRAPHS,
		FILTER,
		BINDER_OWNER_AVATAR,
		ENTRY_MENU,
		CALENDAR_NAVIGATION,
		FOOTER,
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param styleBase
	 * @param allowColumnSizing
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
	final public boolean                          isPinning()            {return m_pinning;                            }	//
	final public boolean                          isProfilesRootWS()     {return m_folderInfo.isBinderProfilesRootWS();}	//
	final public boolean                          isSharedFiles()        {return m_sharedFiles;                        }	//
	final public boolean                          isTrash()              {return m_folderInfo.isBinderTrash();         }	//
	final public FolderDisplayDataRpcResponseData getFolderDisplayData() {return m_folderDisplayData;                  }	//
	final public Long                             getFolderId()          {return m_folderInfo.getBinderIdAsLong();     }	//
	final public VibeFlowPanel                    getFlowPanel()         {return m_flowPanel;                          }	// Flow panel holding the view's content (no toolbars, ...)

	/**
	 * Returns true if the entry viewer should include next/previous
	 * buttons and false otherwise. 
	 *
	 * Classes that extend this class can override this method to
	 * inhibit the next/previous buttons on an entry view.
	 * 
	 * @return
	 */
	protected boolean allowNextPrevOnEntryView() {
		// By default, all folder views support next/previous on entry
		// views.
		return true;
	}
	
	/*
	 * Returns a Widget to use for tool panels that aren't used.
	 */
	private Widget buildToolPanelPlaceholder() {
		// We don't use a place holder for empty tools.
		return null;
	}
	
	/*
	 * Checks how many items are ready and once everything is, calls
	 * the super class' viewReady() method.
	 */
	private void checkReadyness() {
		// Count the tool panels we've got.
		int toolPanels = 0;
		for (Widget w: m_verticalPanels) {
			if ((null != w) && (w instanceof ToolPanelBase)) {
				toolPanels += 1;
			}
		}
		
		// If everything's ready...
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
		m_verticalFlowPanel = new VibeFlowPanel();
		m_verticalFlowPanel.addStyleName(m_styleBase + "VerticalPanel");
	
		// ...create a flow panel to put the main content...
		m_flowPanel = new VibeFlowPanel();
		m_flowPanel.addStyleName(m_styleBase + "FlowPanel");
		
		// ...and finally, tie everything together.
		trackVerticalPanel(VIEW_CONTENT_PANEL_INDEX, m_flowPanel);
		mainPanel.add(m_verticalFlowPanel);
		
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
		// Scan the widgets that were defined for the vertical flow...
		for (Widget w:  m_verticalPanels) {
			if (null != w) {
				// ...adding each to the vertical flow panel...
				m_verticalFlowPanel.add(w);
			}
		}

		// ...and asynchronously complete the view construction.
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				constructView();
			}
		});
	}

	/**
	 * Scan the defined tool panels for an entry menu panel and returns
	 * it.
	 * 
	 * @return
	 */
	public EntryMenuPanel getEntryMenuPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find an entry menu panel...
			if ((null != w) && (w instanceof EntryMenuPanel)) {
				// ...return it.
				return ((EntryMenuPanel) w);
			}
		}
		
		// If we get here, an entry menu panel isn't defined.  Return
		// null.
		return null;
	}
	
	/**
	 * Scan the defined tool panels for a footer panel and returns it.
	 * 
	 * @return
	 */
	public FooterPanel getFooterPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find a footer panel...
			if ((null != w) && (w instanceof FooterPanel)) {
				// ...return it.
				return ((FooterPanel) w);
			}
		}
		
		// If we get here, a footer panel isn't defined.  Return null.
		return null;
	}
	
	/**
	 * Returns the minimum height to used for a folder view's content.
	 * 
	 * Classes that extend this can override as needed.
	 * 
	 * @return
	 */
	public int getMinimumContentHeight() {
		return MINIMUM_CONTENT_HEIGHT;
	}

	/**
	 * Returns the adjustment to used for a folder view's content so
	 * that it doesn't get a vertical scroll bar.
	 * 
	 * Classes that extend this can override as needed.
	 * 
	 * @return
	 */
	public int getNoVScrollAdjustment() {
		return NO_VSCROLL_ADJUST;
	}

	/**
	 * Scan the defined tool panels for a task graphs panel and returns
	 * it.
	 * 
	 * @return
	 */
	public TaskGraphsPanel getTaskGraphsPanel() {
		// Scan the vertical panels...
		for (Widget w:  m_verticalPanels) {
			// ...and if we find a task graphs panel...
			if ((null != w) && (w instanceof TaskGraphsPanel)) {
				// ...return it.
				return ((TaskGraphsPanel) w);
			}
		}
		
		// If we get here, a task graphs panel isn't defined.  Return
		// null.
		return null;
	}
	
	/**
	 * If a busy spinner exists, hide it.
	 */
	final public void hideBusySpinner() {
		// If we have a busy spinner...
		if (null != m_busySpinner) {
			// ...make sure that it's hidden.
			m_busySpinner.hide();
		}
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
		// Unless overridden, all panels except the binder owner avatar
		// panel, task graphs panel and calendar navigation panel are
		// included.
		boolean reply;
		switch (folderPanel) {
		case BINDER_OWNER_AVATAR:
		case CALENDAR_NAVIGATION:
		case TASK_GRAPHS:  reply = false; break;
		default:           reply = true;  break;
		}
		return reply;
	}
	
	/*
	 * Initializes various data members for the class.
	 */
	private void initDataMembers() {
		// Allocate an ArrayList<Widget> to track the panels we add
		// vertically to the view.
		m_verticalPanels = new ArrayList<Widget>();
	}

	/**
	 * Inserts a tool panel into the view.
	 * 
	 * @param tpb
	 * @param tpIndex
	 */
	final public void insertToolPanel(ToolPanelBase tpb, int tpIndex) {
		trackVerticalPanel(tpIndex, tpb);
	}
	
	/**
	 * Inserts a tool panel place holder into the view.
	 * 
	 * @param tpb
	 * @param tpIndex
	 */
	final public void insertToolPanelPlaceholder(int tpIndex) {
		trackVerticalPanel(tpIndex, buildToolPanelPlaceholder());
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
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
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the display data information for the folder.
	 */
	private void loadPart1Now() {
		// Are we loading a calendar folder?
		if (FolderType.CALENDAR == m_folderInfo.getFolderType()) {
			// Yes!  Then we don't need the folder display data as
			// pertains to data table based views.
			loadPart2Async();
		}
		
		else {
			// No, we aren't loading a calendar folder!  Load the
			// display data for the folder.
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
					m_pinning           = m_folderDisplayData.getViewPinnedEntries();
					m_sharedFiles       = m_folderDisplayData.getViewSharedFiles();
					loadPart2Async();
				}
			});
		}
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart2Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.BREADCRUMB))) {
			// ...we don't show the bread crumbs.
			insertToolPanelPlaceholder(BREADCRUMB_PANEL_INDEX);
			loadPart3Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the BreadCrumbPanel.
	 */
	private void loadPart2Now() {
		BreadCrumbPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, BREADCRUMB_PANEL_INDEX);
				loadPart3Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart3Async() {
		// If we're in Filr mode or a super class doesn't want it...
		if (GwtClientHelper.isLicenseFilr() || (!(includePanel(FolderPanels.ACCESSORIES)))) {
			// ...we don't show the accessories.
			insertToolPanelPlaceholder(ACCESSORY_PANEL_INDEX);
			loadPart4Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 * 
	 * Loads the AccessoriesPanel.
	 */
	private void loadPart3Now() {
		AccessoriesPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, ACCESSORY_PANEL_INDEX);
				loadPart4Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart4Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.DESCRIPTION))) {
			// ...we don't show the description.
			insertToolPanelPlaceholder(DESCRIPTION_PANEL_INDEX);
			loadPart5Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart4Now();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart4Now() {
		DescriptionPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, DESCRIPTION_PANEL_INDEX);
				loadPart5Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart5Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.TASK_GRAPHS))) {
			// ...we don't show the task graphs panel.
			insertToolPanelPlaceholder(TASK_GRAPHS_PANEL_INDEX);
			loadPart6Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart5Now();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the DescriptionPanel.
	 */
	private void loadPart5Now() {
		TaskGraphsPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, TASK_GRAPHS_PANEL_INDEX);
				loadPart6Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	@SuppressWarnings("unused")
	private void loadPart6Async() {
		// For classes that don't want it...
		if ((!SHOW_LEGACY_FILTERS) || (!(includePanel(FolderPanels.FILTER)))) {
			// ...we don't show the filter.
			insertToolPanelPlaceholder(FILTER_PANEL_INDEX);
			loadPart7Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart6Now();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FilterPanel.
	 */
	private void loadPart6Now() {
		FilterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FILTER_PANEL_INDEX);
				loadPart7Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart7Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.ENTRY_MENU))) {
			// ...we don't show the entry menu.
			insertToolPanelPlaceholder(ENTRY_MENU_PANEL_INDEX);
			loadPart8Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart7Now();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the EntryMenuPanel.
	 */
	private void loadPart7Now() {
		EntryMenuPanel.createAsync(this, m_folderInfo, isPinning(), isSharedFiles(), m_allowColumnSizing, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, ENTRY_MENU_PANEL_INDEX);
				loadPart8Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart8Async() {
		// If we need to show the footer panel...
		boolean showFooter = includePanel(FolderPanels.FOOTER);
		if (showFooter) {
			if (!SHOW_FILR_FOOTER) {
				showFooter = (!(GwtClientHelper.isLicenseFilr()));
			}
		}
		
		if (showFooter) {
			// ...load it...
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					loadPart8Now();
				}
			});
		}
		
		else {
			// ...otherwise, insert a place holder for it.
			insertToolPanelPlaceholder(FOOTER_PANEL_INDEX);
			loadPart9Async();
		}
	}

	/*
	 * Asynchronously loads the next part of the view.
	 * 
	 * Loads the FooterPanel.
	 */
	private void loadPart8Now() {
		FooterPanel.createAsync(this, m_folderInfo, this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FOOTER_PANEL_INDEX);
				loadPart9Async();
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart9Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.BINDER_OWNER_AVATAR))) {
			// ...we don't show the binder owner avatar.
			insertToolPanelPlaceholder(BINDER_OWNER_AVATAR_PANEL_INDEX);
			loadPart10Async();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart9Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart9Now() {
		BinderOwnerAvatarPanel.createAsync(this, getFolderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FolderViewBase.BINDER_OWNER_AVATAR_PANEL_INDEX);
				loadPart10Async();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the view.
	 */
	private void loadPart10Async() {
		// For classes that don't want it...
		if (!(includePanel(FolderPanels.CALENDAR_NAVIGATION))) {
			// ...we don't show the calendar navigation panel.
			insertToolPanelPlaceholder(CALENDAR_NAVIGATION_PANEL_INDEX);
			constructViewAsync();
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart10Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the view.
	 */
	private void loadPart10Now() {
		CalendarNavigationPanel.createAsync(this, m_calendarDisplayDataProvider, getFolderInfo(), this, new ToolPanelClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ToolPanelBase tpb) {
				insertToolPanel(tpb, FolderViewBase.CALENDAR_NAVIGATION_PANEL_INDEX);
				constructViewAsync();
			}
		});
	}

	/**
	 * Called when the folder view is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		super.onAttach();
		GwtClientHelper.jsSetAllowNextPrevOnView(allowNextPrevOnEntryView());
	}
	
	/**
	 * Called when the folder view is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		GwtClientHelper.jsSetAllowNextPrevOnView(false);
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
		for (Widget w:  m_verticalPanels) {
			if ((null != w) && (w instanceof ToolPanelBase)) {
				((ToolPanelBase) w).resetPanel();
			}
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resizeView();
			}
		});
	}

	/**
	 * Stores a CalendarDisplayDataProvider for use by folder view.
	 * 
	 * @param calendarDisplayDataProvider
	 */
	public void setCalendarDisplayDataProvider(CalendarDisplayDataProvider calendarDisplayDataProvider) {
		m_calendarDisplayDataProvider = calendarDisplayDataProvider;
	}

	/**
	 * Sets the current pinning state.
	 * 
	 * @param pinning
	 */
	public void setPinning(boolean pinning) {
		m_pinning = pinning;
	}
	
	/**
	 * Sets the current shared files state.
	 * 
	 * @param sharedFiles
	 */
	public void setSharedFiles(boolean sharedFiles) {
		m_sharedFiles = sharedFiles;
	}
	
	/**
	 * Shows a busy spinner animation while an operation is going on.
	 */
	final public void showBusySpinner() {
		// If we haven't created a busy spinner yet...
		if (null == m_busySpinner) {
			// ...create one now...
			m_busySpinner = new SpinnerPopup();
		}

		// ...and show it.
		m_busySpinner.center();
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
		
		else {
			GwtClientHelper.debugAlert("FolderViewBase.toolPanelReady( *Internal Error* ):  Unexpected call to toolPanelReady() method.");
		}
	}

	/*
	 * Ensures m_verticalPanels is large enough to hold an item at
	 * index and sets w as the widget at that index.
	 */
	private void trackVerticalPanel(int index, Widget w) {
		// Ensure the list is big enough...
		int c = m_verticalPanels.size();
		for (int i = c; i <= index; i += 1) {
			m_verticalPanels.add(i, null);
		}
		
		// ...and store the widget.
		m_verticalPanels.set(index, w);
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
		
		else {
			GwtClientHelper.debugAlert("FolderViewBase.viewReady( *Internal Error* ):  Unexpected call to viewReady() method.");
		}
	}
}
