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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.CalendarShowEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedUserWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.HideAccessoriesEvent;
import org.kablink.teaming.gwt.client.event.InvokeAddNewFolderEvent;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.InvokeSignGuestbookEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedUserWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.ResetEntryMenuEvent;
import org.kablink.teaming.gwt.client.event.SetFolderSortEvent;
import org.kablink.teaming.gwt.client.event.ShowAccessoriesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TrashPurgeAllEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreAllEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewPinnedEntriesEvent;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderFiltersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarShow;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * Class used for the content of the entry menus in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class EntryMenuPanel extends ToolPanelBase
	implements
	// Event handlers implemented by this class.
		ResetEntryMenuEvent.Handler
{
	private BinderFiltersRpcResponseData	m_binderFilters;			//
	private BinderInfo						m_binderInfo;				//
	private boolean							m_includeColumnResizer;		//
	private boolean							m_isIE;						//
	private boolean							m_panelInitialized;			// Set true after the panel has completed initializing.
	private boolean							m_viewingPinnedEntries;		//
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private List<ToolbarItem>				m_configureToolbarItems;	//
	private List<ToolbarItem>				m_toolbarIems;				//
	private VibeFlexTable					m_grid;						//
	private VibeFlowPanel					m_configPanel;				//
	private VibeFlowPanel					m_filterOptionsPanel;		//
	private VibeFlowPanel					m_filtersPanel;				//
	private VibeFlowPanel					m_quickFilterPanel;			//
	private VibeMenuBar						m_entryMenu;				//
	private VibeMenuItem					m_addFilesMenu;				//
	private VibeMenuItem					m_deleteMenu;				//
	private VibeMenuItem					m_moreMenu;					//
	private VibeMenuItem					m_shareMenu;				//
	private VibeMenuItem					m_trashPurgeAllMenu;		//
	private VibeMenuItem					m_trashPurgeSelectedMenu;	//
	private VibeMenuItem					m_trashRestoreAllMenu;		//
	private VibeMenuItem					m_trashRestoreSelectedMenu;	//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.RESET_ENTRY_MENU,
	};
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EntryMenuPanel(RequiresResize containerResizer, BinderInfo binderInfo, boolean viewingPinnedEntries, boolean includeColumnResizer, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_binderInfo           = binderInfo;
		m_viewingPinnedEntries = viewingPinnedEntries;
		m_includeColumnResizer = includeColumnResizer;

		// ...initialize any other data members...
		m_isIE = GwtClientHelper.jsIsIE();
		
		// ...construct and initialize the panels...
		m_grid = new VibeFlexTable();
		m_grid.addStyleName("vibe-binderViewTools vibe-entryMenu-grid");
		m_grid.setWidth("100%");
		m_grid.setCellPadding(0);
		m_grid.setCellSpacing(0);
		constructMenuPanels();
		initWidget(m_grid);
		
		// ...and load the menu.
		loadPart1Async();
	}

	/*
	 * Constructs the panels used by the entry menu.
	 */
	private void constructMenuPanels() {
		m_entryMenu = new VibeMenuBar("vibe-entryMenuBar");
		m_grid.setWidget(0, 0, m_entryMenu);

		VibeFlowPanel rightPanel = new VibeFlowPanel();
		rightPanel.addStyleName("vibe-entryMenu-rightPanel");
		rightPanel.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		m_grid.setWidget(0, 1, rightPanel);
		m_grid.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		
		m_filtersPanel = new VibeFlowPanel();
		m_filtersPanel.addStyleName("vibe-entryMenu-filtersPanel");
		m_filtersPanel.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		rightPanel.add(m_filtersPanel);
		
		m_quickFilterPanel = new VibeFlowPanel();
		m_quickFilterPanel.addStyleName("vibe-entryMenu-quickFilters-panel");
		m_quickFilterPanel.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		rightPanel.add(m_quickFilterPanel);
		
		m_filterOptionsPanel = new VibeFlowPanel();
		m_filterOptionsPanel.addStyleName("vibe-entryMenu-filterOptions-panel");
		m_filterOptionsPanel.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		rightPanel.add(m_filterOptionsPanel);
		
		m_configPanel = new VibeFlowPanel();
		m_configPanel.addStyleName("vibe-entryMenu-configPanel");
		m_configPanel.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		rightPanel.add(m_configPanel);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public VibeFlowPanel getConfigPanel()        {return m_configPanel;       }
	public VibeFlowPanel getFilterOptionsPanel() {return m_filterOptionsPanel;}
	public VibeFlowPanel getFiltersPanel()       {return m_filtersPanel;      }
	public VibeFlowPanel getQuickFilterPanel()   {return m_quickFilterPanel;  }
	public VibeMenuItem  getAddFilesMenuItem()   {return m_addFilesMenu;      }
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
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
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart1Now() {
		final Long folderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFolderToolbarItemsCmd(m_binderInfo),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderToolbarItems(),
					folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the toolbar items and continue loading.
				GetFolderToolbarItemsRpcResponseData responseData = ((GetFolderToolbarItemsRpcResponseData) response.getResponseData());
				m_toolbarIems = responseData.getToolbarItems();
				
				m_configureToolbarItems = responseData.getConfigureToolbarItems();
				if (m_includeColumnResizer) {
					ToolbarItem rcTBI = new ToolbarItem("resizeColumns");
					rcTBI.setTitle(m_messages.vibeDataTable_ColumnResizer());
					rcTBI.setTeamingEvent(TeamingEvents.INVOKE_COLUMN_RESIZER);
					m_configureToolbarItems.add(rcTBI);
				}
				
				loadPart2Async();
			}
		});
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart2Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart2Now() {
		// Are we working with a non-trash folder?
		if (m_binderInfo.isBinderFolder() && (!(m_binderInfo.isBinderTrash()))) {
			// Yes!  Get the filter information for the binder.
			final Long binderId = m_binderInfo.getBinderIdAsLong();
			GwtClientHelper.executeCommand(
					new GetBinderFiltersCmd(binderId),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetBinderFilters(),
						binderId);
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the filter information and continue loading.
					m_binderFilters = ((BinderFiltersRpcResponseData) response.getResponseData());
					loadPart3Async();
				}
			});
		}
		
		else {
			// No, we either working with a workspace or trash folder!
			// No filtering.  Simply proceed with the next stop of
			// loading.
			loadPart3Async();
		}
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart3Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart3Now() {
		// Do we have an entry toolbar?
		ToolbarItem entryTBI = ToolbarItem.getNestedToolbarItem(m_toolbarIems, "ssEntryToolbar");
		if (null != entryTBI) {
			// Yes!  Scan its nested items..
			for (ToolbarItem perEntryTBI:  entryTBI.getNestedItemsList()) {
				// ...rendering each of them.
				if (perEntryTBI.hasNestedToolbarItems()) {
					renderStructuredTBI(m_entryMenu, null, perEntryTBI);
				}
				
				else if (perEntryTBI.isSeparator()) {
					m_entryMenu.addSeparator();
				}
				
				else {
					renderSimpleTBI(m_entryMenu, null, perEntryTBI, false);
				}
			}
		}
		setEntriesSelectedImpl(false);
		
		// Render the various right end capabilities applicable to the
		// current binder.
		renderConfigureTBI();
		if (m_viewingPinnedEntries) {
			m_quickFilterPanel.setVisible(  false);
			m_filterOptionsPanel.setVisible(false);
		}
		else {
			renderQuickFilter();
			renderDefinedFiltering();
		}
		
		// Finally, if we're not simply resetting the tool panel...
		if (!m_panelInitialized) {
			// ...tell who's using it that it's ready to go.
			toolPanelReady();
			m_panelInitialized = true;
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
	 * Handles ResetEntryMenuEvent's received by this class.
	 * 
	 * Implements the ResetEntryMenuEvent.Handler.onResetEntryMenu()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onResetEntryMenu(ResetEntryMenuEvent event) {
		Long binderId = event.getBinderId();
		if (binderId.equals(m_binderInfo.getBinderIdAsLong())) {
			resetPanel();
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
	
	/*
	 * Renders any configure toolbar items applicable to the current
	 * binder.
	 */
	private void renderConfigureTBI() {
		// If we don't have any configure toolbar items...
		if (m_configureToolbarItems.isEmpty()) {
			// ...there's nothing to render.  Bail.
			return;
		}
		
		// Create the configure menu bar...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-configureMenuBar vibe-entryMenuBar");
		fp.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		final Anchor a = new Anchor();
		a.setTitle(m_messages.vibeEntryMenu_Alt_ListOptions());
		Image configureImg = new Image(m_images.configOptions());
		configureImg.addStyleName("vibe-configureMenuImg");
		configureImg.getElement().setAttribute("align", "absmiddle");
		a.getElement().appendChild(configureImg.getElement());
		final PopupMenu configureDropdownMenu = new PopupMenu(true, false, false);
		configureDropdownMenu.addStyleName("vibe-configureMenuBarDropDown");
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				configureDropdownMenu.showRelativeToTarget(m_configPanel);
			}
		});
		fp.add(a);
		m_configPanel.add(fp);
		
		// ...scan the configure toolbar items...
		for (ToolbarItem configureTBI:  m_configureToolbarItems) {
			// ...adding each to the configure menu.
			if (configureTBI.hasNestedToolbarItems()) {
				renderStructuredTBI(null, configureDropdownMenu, configureTBI);
			}
			else if (configureTBI.isSeparator()) {
				configureDropdownMenu.addSeparator();
			}
			else {
				renderSimpleTBI(null, configureDropdownMenu, configureTBI, false);
			}
		}
	}

	/*
	 * Renders a current filter into the filters panel.
	 */
	private void renderCurrentFilter(String filterName, boolean isGlobal, final String filtersOffUrl, boolean lastCurrentFilter) {
		// Create a panel for the filter...
		VibeFlowPanel perFilterPanel = new VibeFlowPanel();
		perFilterPanel.addStyleName("vibe-filterMenuSavedPanel");
		perFilterPanel.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		if (!lastCurrentFilter) {
			perFilterPanel.addStyleName("marginright3px");
		}
		m_filtersPanel.add(perFilterPanel);

		// ...create a label for the filter...
		InlineLabel il = new InlineLabel(filterName);
		il.addStyleName("vibe-filterMenuSavedLabel");
		if (isGlobal)
		     il.setTitle(m_messages.vibeEntryMenu_Alt_GlobalFilter());
		else il.setTitle(m_messages.vibeEntryMenu_Alt_PersonalFilter());
		perFilterPanel.add(il);

		// ...if we have the URL to remove it...
		if (GwtClientHelper.hasString(filtersOffUrl)) {
			// ...create an Anchor for it...
			final Anchor a = new Anchor();
			a.addStyleName("vibe-filterMenuSavedAnchor");
			Image deleteImg = new Image(m_images.delete());
			deleteImg.addStyleName("vibe-filterMenuSavedImg");
			deleteImg.getElement().setAttribute("align", "absmiddle");
			deleteImg.setTitle(m_messages.vibeEntryMenu_Alt_ClearFilter());
			a.getElement().appendChild(deleteImg.getElement());
			perFilterPanel.add(a);
			
			// ...and add a click handlers to the anchor.
			a.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(filtersOffUrl));
				}
			});
		}
	}

	/*
	 * Renders a defined filter as a VibeMenuItem.
	 */
	private VibeMenuItem renderDefinedFilter(String menuText, boolean isHtml, final BinderFilter bf, final boolean isCurrentFilter) {
		VibeMenuItem reply = new VibeMenuItem(menuText, isHtml, new Command() {
			@Override
			public void execute() {
				GwtTeaming.fireEvent(
					new GotoContentUrlEvent(
						(isCurrentFilter           ?	// If the filter is active...
							bf.getFilterClearUrl() :	// ...the link clears it...
							bf.getFilterAddUrl())));	// ...otherwise, it activates it.
			}
		});
		return reply;
	}
	
	/*
	 * Renders any defined filter capabilities applicable to the
	 * current binder.
	 */
	private void renderDefinedFiltering() {
		// If we don't have any binder filter information...
		if (null == m_binderFilters) {
			// ...there's nothing to render.  Bail.
			return;
		}

		// If there aren't any filters defined and the user can't
		// define any...
		final List<BinderFilter> filtersList   = m_binderFilters.getBinderFilters(); int filtersCount = ((null == filtersList) ? 0 : filtersList.size());
		final String             filterEditUrl = m_binderFilters.getFilterEditUrl(); boolean hasFilterEditUrl = GwtClientHelper.hasString(filterEditUrl);
		if ((0 == filtersCount) && (!hasFilterEditUrl)) {
			// ...there's nothing to render.  Bail.
			return;
		}
		
		List<String> currentFilters = m_binderFilters.getCurrentFilters();
		int     currentFiltersCount = ((null == currentFilters) ? 0 : currentFilters.size());
		boolean hasCurrentFilter    = (0 < currentFiltersCount);
		final String filtersOffUrl;
		boolean hasFiltersOffUrl;
		if (hasCurrentFilter) {
			filtersOffUrl    = m_binderFilters.getFiltersOffUrl();
			hasFiltersOffUrl = GwtClientHelper.hasString(filtersOffUrl);
		}
		else {
			filtersOffUrl    = null;
			hasFiltersOffUrl = false;
		}
		
		// Create the filter options menu bar...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-filterMenuBar vibe-entryMenuBar");
		fp.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
		final Anchor a = new Anchor();
		a.setTitle(m_messages.vibeEntryMenu_Alt_FilterOptions());
		Image filterImg = new Image(m_images.menuButton());
		filterImg.addStyleName("vibe-filterMenuImg");
		filterImg.getElement().setAttribute("align", "absmiddle");
		a.getElement().appendChild(filterImg.getElement());
		final PopupMenu filterDropdownMenu = new PopupMenu(true, false, false);
		filterDropdownMenu.addStyleName("vibe-filterMenuBarDropDown");
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				filterDropdownMenu.showRelativeToTarget(m_filterOptionsPanel);
			}
		});
		fp.add(a);
		m_filterOptionsPanel.add(fp);
		
		// If we have an edit filters URL...
		if (hasFilterEditUrl) {
			// ...add a menu item for it.
			VibeMenuItem mi = new VibeMenuItem(m_messages.vibeEntryMenu_ManageFilters(), new Command() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(filterEditUrl));
				}
			});
			filterDropdownMenu.addMenuItem(mi);
			if ((!hasFiltersOffUrl) && (0 < filtersCount)) {
				filterDropdownMenu.addSeparator();
			}
		}

		// If we have a filters off URL...
		if (hasFiltersOffUrl) {
			// ...add a menu item for it.
			VibeMenuItem mi = new VibeMenuItem(m_messages.vibeEntryMenu_ClearFilters(), new Command() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(filtersOffUrl));
				}
			});
			filterDropdownMenu.addMenuItem(mi);
			if (0 < filtersCount) {
				filterDropdownMenu.addSeparator();
			}
		}

		// If we have any defined filters...
		if (0 < filtersCount) {
			// ...scan them...
			int currentsShown = 0;
			for (final BinderFilter bf:  filtersList) {
				// ...and add a menu item for each.
				String fName = bf.getFilterName();
				boolean isGlobal = bf.isGlobal();
				if (isGlobal)
				     fName = m_messages.vibeEntryMenu_GlobalizeFilter(  fName);
				else fName = m_messages.vibeEntryMenu_PersonalizeFilter(fName);
				String menuText;
				boolean isCurrent = false;
				if (hasCurrentFilter) {
					isCurrent = currentFilters.contains(bf.getFilterSpec());
					VibeFlowPanel html = new VibeFlowPanel();
					Image checkImg;
					if (isCurrent) {
						checkImg = new Image(m_images.check12());
						checkImg.addStyleName("vibe-entryMenuBarCheck");
						checkImg.getElement().setAttribute("align", "absmiddle");
					}
					else {
						checkImg = new Image(m_images.spacer1px());
						checkImg.addStyleName("vibe-entryMenuBarCheck");
						checkImg.setWidth("12px");
					}
					html.add(checkImg);
					html.add(new InlineLabel(fName));
					menuText = html.getElement().getInnerHTML();
				}
				else {
					menuText = fName;
				}
				filterDropdownMenu.addMenuItem(
					renderDefinedFilter(
						menuText,
						hasCurrentFilter,
						bf,
						isCurrent));
				
				// If this is a current filter...
				if (isCurrent) {
					// ...add it to the filters panel.
					currentsShown += 1;
					renderCurrentFilter(
						fName,
						isGlobal,
						bf.getFilterClearUrl(),
						(currentsShown == currentFiltersCount));	// true -> Last current.  false -> Not the last one.
				}
			}
		}
	}
	
	/*
	 * Renders any quick filter capabilities applicable to the current
	 * binder.
	 */
	private void renderQuickFilter() {
		// If the folder supports quick filtering...
		if (supportsQuickFilter()) {
			// ...add a quick filter widget...
			QuickFilter qf = new QuickFilter(m_binderInfo.getBinderIdAsLong());
			qf.addStyleName("vibe-entryMenu-quickFilters-filter");
			qf.addStyleName(m_isIE ? "displayInline" : "displayInlineBlock");
			m_quickFilterPanel.add(qf);
		}
		
		else {
			// ...otherwise, hide the panel.
			m_quickFilterPanel.setVisible(false);
		}
	}
	
	/*
	 * Renders any simple (i.e., URL or event based) toolbar item.
	 */
	private void renderSimpleTBI(VibeMenuBar menuBar, PopupMenu popupMenu, final ToolbarItem simpleTBI, boolean contentsSelectable) {
		final String        simpleTitle = simpleTBI.getTitle();
		final TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();

		// Is this a menu item to view pinned entries?
		boolean menuTextIsHTML;
		String  menuText;
		if ((null != simpleEvent) && TeamingEvents.VIEW_PINNED_ENTRIES.equals(simpleEvent)) {
			// Yes!  Generate the appropriate HTML for the item.
			Image pinImg = new Image(m_viewingPinnedEntries ? m_images.orangePin() : m_images.grayPin());
			pinImg.addStyleName("vibe-entryMenuBarPin");
			pinImg.getElement().setAttribute("align", "absmiddle");
			pinImg.setTitle(
				m_viewingPinnedEntries                         ?
					m_messages.vibeEntryMenu_Alt_Pin_ShowAll() :
					m_messages.vibeEntryMenu_Alt_Pin_ShowPinned());
			VibeFlowPanel html = new VibeFlowPanel();
			html.add(pinImg);
			menuText       = html.getElement().getInnerHTML();
			menuTextIsHTML = true;
		}
		
		else {
			// No, this isn't a view pinned entries item!  Generate the
			// text to display for the menu item...
			if (contentsSelectable) {
				String contentsCheckedS = simpleTBI.getQualifierValue("selected");
				boolean contentsChecked = (GwtClientHelper.hasString(contentsCheckedS) && contentsCheckedS.equals("true"));
				VibeFlowPanel html = new VibeFlowPanel();
				Image checkImg;
				if (contentsChecked) {
					checkImg = new Image(m_images.check12());
					checkImg.addStyleName("vibe-entryMenuBarCheck");
					checkImg.getElement().setAttribute("align", "absmiddle");
				}
				else {
					checkImg = new Image(m_images.spacer1px());
					checkImg.addStyleName("vibe-entryMenuBarCheck");
					checkImg.setWidth("12px");
				}
				html.add(checkImg);
				html.add(new InlineLabel(simpleTitle));
				menuText       = html.getElement().getInnerHTML();
				menuTextIsHTML = true;
			}
			else {
				menuText       = simpleTitle;
				menuTextIsHTML = false;
			}
		}

		// ...and generate the menu item.
		VibeMenuItem menuItem = new VibeMenuItem(menuText, menuTextIsHTML, new Command() {
			@Override
			public void execute() {
				// Does the simple toolbar item contain a URL to
				// launch?
				final String simpleUrl = simpleTBI.getUrl();
				if (GwtClientHelper.hasString(simpleUrl)) {
					// Yes!  Launch it in the content frame.
					OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
						simpleUrl,
						Instigator.GOTO_CONTENT_URL);
					
					if (GwtClientHelper.validateOSBI(osbInfo)) {
						GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
					}
				}
				
				else {
					// No, the simple toolbar item didn't contain a
					// URL!  The only other option is an event.
					Long folderId = m_binderInfo.getBinderIdAsLong();
					
					VibeEventBase<?> event;
					switch (simpleEvent) {
					default:                                  event = EventHelper.createSimpleEvent(          simpleEvent); break;
					case CHANGE_ENTRY_TYPE_SELECTED_ENTRIES:  event = new ChangeEntryTypeSelectedEntriesEvent(folderId   ); break;
					case COPY_SELECTED_ENTRIES:               event = new CopySelectedEntriesEvent(           folderId   ); break;
					case DELETE_SELECTED_ENTRIES:             event = new DeleteSelectedEntriesEvent(         folderId   ); break;
					case DELETE_SELECTED_USER_WORKSPACES:     event = new DeleteSelectedUserWorkspacesEvent(  folderId   ); break;
					case DISABLE_SELECTED_USERS:              event = new DisableSelectedUsersEvent(          folderId   ); break;
					case ENABLE_SELECTED_USERS:               event = new EnableSelectedUsersEvent(           folderId   ); break;
					case HIDE_ACCESSORIES:                    event = new HideAccessoriesEvent(               folderId   ); break;
					case INVOKE_COLUMN_RESIZER:               event = new InvokeColumnResizerEvent(           folderId   ); break;
					case INVOKE_DROPBOX:                      event = new InvokeDropBoxEvent(                 folderId   ); break;
					case INVOKE_SIGN_GUESTBOOK:               event = new InvokeSignGuestbookEvent(           folderId   ); break;
					case LOCK_SELECTED_ENTRIES:               event = new LockSelectedEntriesEvent(           folderId   ); break;
					case UNLOCK_SELECTED_ENTRIES:             event = new UnlockSelectedEntriesEvent(         folderId   ); break;
					case MARK_READ_SELECTED_ENTRIES:          event = new MarkReadSelectedEntriesEvent(       folderId   ); break;
					case MARK_UNREAD_SELECTED_ENTRIES:        event = new MarkUnreadSelectedEntriesEvent(     folderId   ); break;
					case MOVE_SELECTED_ENTRIES:               event = new MoveSelectedEntriesEvent(           folderId   ); break;
					case PURGE_SELECTED_ENTRIES:              event = new PurgeSelectedEntriesEvent(          folderId   ); break;
					case PURGE_SELECTED_USER_WORKSPACES:      event = new PurgeSelectedUserWorkspacesEvent(   folderId   ); break;
					case PURGE_SELECTED_USERS:                event = new PurgeSelectedUsersEvent(            folderId   ); break;
					case SHOW_ACCESSORIES:                    event = new ShowAccessoriesEvent(               folderId   ); break;
					case SHARE_SELECTED_ENTRIES:              event = new ShareSelectedEntriesEvent(          folderId   ); break;
					case SUBSCRIBE_SELECTED_ENTRIES:          event = new SubscribeSelectedEntriesEvent(      folderId   ); break;
					case TRASH_PURGE_ALL:                     event = new TrashPurgeAllEvent(                 folderId   ); break;
					case TRASH_PURGE_SELECTED_ENTRIES:        event = new TrashPurgeSelectedEntriesEvent(     folderId   ); break;
					case TRASH_RESTORE_ALL:                   event = new TrashRestoreAllEvent(               folderId   ); break;
					case TRASH_RESTORE_SELECTED_ENTRIES:      event = new TrashRestoreSelectedEntriesEvent(   folderId   ); break;
					case VIEW_PINNED_ENTRIES:                 event = new ViewPinnedEntriesEvent(             folderId   ); break;
					
					case CALENDAR_SHOW:
						int calendarShow = Integer.parseInt(simpleTBI.getQualifierValue("calendarShow"));
						event = new CalendarShowEvent(folderId, CalendarShow.getEnum(calendarShow));
						break;

					case INVOKE_ADD_NEW_FOLDER:
						String folderTemplateId = simpleTBI.getQualifierValue("folderTemplateId");
						event = new InvokeAddNewFolderEvent(folderId, Long.parseLong(folderTemplateId));
						break;
						
					case SET_FOLDER_SORT:
						String sortKey        = simpleTBI.getQualifierValue("sortKey"       );
						String sortDescending = simpleTBI.getQualifierValue("sortDescending");
						event = new SetFolderSortEvent(folderId, sortKey, Boolean.parseBoolean(sortDescending));
						break;
						
					case UNDEFINED:
						Window.alert(m_messages.eventHandling_NoEntryMenuHandler(simpleEvent.name()));
						event = null;
					}
					
					if (null != event) {
						GwtTeaming.fireEvent(event);
					}
				}
			}
		});
		switch (simpleTBI.getTeamingEvent()) {
		case INVOKE_DROPBOX:                  m_addFilesMenu             = menuItem; break;
		case DELETE_SELECTED_ENTRIES:         m_deleteMenu               = menuItem; break;
		case SHARE_SELECTED_ENTRIES:          m_shareMenu                = menuItem; break;
		case TRASH_PURGE_ALL:                 m_trashPurgeAllMenu        = menuItem; break;
		case TRASH_PURGE_SELECTED_ENTRIES:    m_trashPurgeSelectedMenu   = menuItem; break;
		case TRASH_RESTORE_ALL:               m_trashRestoreAllMenu      = menuItem; break;
		case TRASH_RESTORE_SELECTED_ENTRIES:  m_trashRestoreSelectedMenu = menuItem; break;
		}
		menuItem.addStyleName((menuBar == m_entryMenu) ? "vibe-entryMenuBarItem" : "vibe-entryMenuPopupItem");
		if (null != menuBar)
		     menuBar.addItem(      menuItem);
		else popupMenu.addMenuItem(menuItem);
	}

	/*
	 * Renders the HTML for a structured menu item.
	 */
	private String renderStructuredItemHTML(String itemText, boolean enabled) {
		FlowPanel htmlPanel = new FlowPanel();
		InlineLabel itemLabel = new InlineLabel(itemText);
		itemLabel.addStyleName("vibe-mainMenuBar_BoxText");
		htmlPanel.add(itemLabel);

		Image dropDownImg = new Image(enabled ? GwtTeaming.getMainMenuImageBundle().menuArrow() : GwtTeaming.getMainMenuImageBundle().menuArrowGray());
		dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImg");
		if (!m_isIE) {
			dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImgNonIE");
		}
		htmlPanel.add(dropDownImg);
		
		return htmlPanel.getElement().getInnerHTML();
	}
	
	/*
	 * Renders any toolbar item that contains nested toolbar items.
	 */
	private void renderStructuredTBI(VibeMenuBar menuBar, PopupMenu popupMenu, ToolbarItem structuredTBI) {
		// Create a drop down menu for the structured toolbar item...
		VibeMenuBar	structuredMenuBar = new VibeMenuBar(true);	// true -> Vertical drop down menu.
		structuredMenuBar.addStyleName("vibe-entryMenuPopup");
		VibeMenuItem structuredMenuItem = new VibeMenuItem(structuredTBI.getTitle(), structuredMenuBar);
		structuredMenuItem.addStyleName("vibe-entryMenuBarItem");
		structuredMenuItem.setHTML(renderStructuredItemHTML(structuredTBI.getTitle(), true));
		if (null != menuBar)
		     menuBar.addItem(      structuredMenuItem);
		else popupMenu.addMenuItem(structuredMenuItem);
		
		String structuredName = structuredTBI.getName();
		if (GwtClientHelper.hasString(structuredName) && structuredName.equals("1_more")) {
			m_moreMenu = structuredMenuItem;
		}
		
		// ...scan the nested items...
		String contentsSelectableS = structuredTBI.getQualifierValue("contentsSelectable");
		boolean contentsSelectable = (GwtClientHelper.hasString(contentsSelectableS) && contentsSelectableS.equals("true"));
		for (ToolbarItem nestedTBI:  structuredTBI.getNestedItemsList()) {
			// ...rendering each of them.
			if (nestedTBI.hasNestedToolbarItems()) {
				renderStructuredTBI(structuredMenuBar, popupMenu, nestedTBI);
			}
			else if (nestedTBI.isSeparator()) {
				structuredMenuBar.addSeparator();
			}
			else {
				renderSimpleTBI(structuredMenuBar, null, nestedTBI, contentsSelectable);
			}
		}
	}
	
	/**
	 * Called from the binder view to allow the panel to do any
	 * work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Reset the widgets...
		m_grid.clear();
		constructMenuPanels();

		// ...and reload the menu.
		loadPart1Async();
	}

	/*
	 * Called to enable/disable the menu items that require something
	 * to be available (i.e., a data table is not empty, ...)
	 */
	private void setEntriesAvailableImpl(boolean dataAvailable) {
		// If we have a trash purge all menu item...
		if (null != m_trashPurgeAllMenu) {
			// ...enable disable it.
			m_trashPurgeAllMenu.setEnabled(dataAvailable);
			if (dataAvailable)
			     m_trashPurgeAllMenu.removeStyleName("vibe-menuDisabled");
			else m_trashPurgeAllMenu.addStyleName(   "vibe-menuDisabled");
		}
		
		// If we have a trash restore all menu item...
		if (null != m_trashRestoreAllMenu) {
			// ...enable disable it.
			m_trashRestoreAllMenu.setEnabled(dataAvailable);
			if (dataAvailable)
			     m_trashRestoreAllMenu.removeStyleName("vibe-menuDisabled");
			else m_trashRestoreAllMenu.addStyleName(   "vibe-menuDisabled");
		}
	}
	
	/*
	 * Called to enable/disable the menu items that require something
	 * to be selected.
	 */
	private void setEntriesSelectedImpl(boolean enable) {
		// If we have a share menu item...
		if (null != m_shareMenu) {
			// ...enable disable it.
			m_shareMenu.setEnabled(enable);
			if (enable)
			     m_shareMenu.removeStyleName("vibe-menuDisabled");
			else m_shareMenu.addStyleName(   "vibe-menuDisabled");
		}

		// If we have a delete menu item...
		if (null != m_deleteMenu) {
			// ...enable disable it.
			m_deleteMenu.setEnabled(enable);
			if (enable)
			     m_deleteMenu.removeStyleName("vibe-menuDisabled");
			else m_deleteMenu.addStyleName(   "vibe-menuDisabled");
		}

		// If we have a more menu item...
		if (null != m_moreMenu) {
			// ...enable/disable it...
			m_moreMenu.setEnabled(enable);
			if (enable)
			     m_moreMenu.removeStyleName("vibe-menuDisabled");
			else m_moreMenu.addStyleName(   "vibe-menuDisabled");

			// ...and update its display to reflect the state of the
			// ...menu item (in particular, the drop down image on the
			// ...menu.)
			m_moreMenu.setHTML(
				renderStructuredItemHTML(
					m_moreMenu.getText(),
					enable));
		}
		
		// If we have a trash purge selected menu item...
		if (null != m_trashPurgeSelectedMenu) {
			// ...enable disable it.
			m_trashPurgeSelectedMenu.setEnabled(enable);
			if (enable)
			     m_trashPurgeSelectedMenu.removeStyleName("vibe-menuDisabled");
			else m_trashPurgeSelectedMenu.addStyleName(   "vibe-menuDisabled");
		}
		
		// If we have a trash restore selected menu item...
		if (null != m_trashRestoreSelectedMenu) {
			// ...enable disable it.
			m_trashRestoreSelectedMenu.setEnabled(enable);
			if (enable)
			     m_trashRestoreSelectedMenu.removeStyleName("vibe-menuDisabled");
			else m_trashRestoreSelectedMenu.addStyleName(   "vibe-menuDisabled");
		}
	}
	
	/*
	 * Returns true if the binder this menu is running against supports
	 * quick filtering and false otherwise.
	 */
	private boolean supportsQuickFilter() {
		boolean reply = false;
		
		switch (m_binderInfo.getBinderType()) {
		case FOLDER:
			switch (m_binderInfo.getFolderType()) {
			case BLOG:
			case CALENDAR:
			case DISCUSSION:
			case FILE:
			case GUESTBOOK:
			case MILESTONE:
			case MINIBLOG:
			case MIRROREDFILE:
			case SURVEY:
			case TASK:
				reply = true;
				break;
			}
			break;
			
		case COLLECTION:
			// All collections support the quick filters.
			reply = true;
			break;
		}
		
		return reply;
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
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the entry menu and perform some operation on it.              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/*
	 * Asynchronously loads the EntryMenuPanel and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final RequiresResize	containerResizer,
			final BinderInfo		binderInfo,
			final boolean			viewingPinnedEntries,
			final boolean			includeColumnResizer,
			final ToolPanelReady	toolPanelReady,
			final ToolPanelClient 	tpClient,
			
			// setEntriesAvailable/Selected parameters.
			final EntryMenuPanel	emp,
			final boolean			setAvailable,
			final boolean			enable) {
		GWT.runAsync(EntryMenuPanel.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EntryMenuPanel());
				if (null != tpClient) {
					tpClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create an entry menu panel?
				if (null != tpClient) {
					// Yes!  Create it and return it via the callback.
					EntryMenuPanel emp = new EntryMenuPanel(containerResizer, binderInfo, viewingPinnedEntries, includeColumnResizer, toolPanelReady);
					tpClient.onSuccess(emp);
				}
				
				else {
					// No, it's not a request to create an entry menu
					// panel!  It must be a notification about entries
					// being available or selected.
					if (setAvailable)
					     emp.setEntriesAvailableImpl(enable);
					else emp.setEntriesSelectedImpl( enable);
				}
			}
		});
	}
	
	/**
	 * Loads the EntryMenuPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param includeColumnResizer
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final boolean viewingPinnedEntries, final boolean includeColumnResizer, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		doAsyncOperation(containerResizer, binderInfo, viewingPinnedEntries, includeColumnResizer, toolPanelReady, tpClient, null, false, false);
	}
	
	/**
	 * Called to enable/disable the menu items that require something
	 * to be available (i.e., a data table is not empty, ...)
	 * 
	 * @param emp
	 * @param enable
	 */
	public static void setEntriesAvailable(final EntryMenuPanel emp, final boolean enable) {
		doAsyncOperation(null, null, false, false, null, null, emp, true, enable);
	}
	
	/**
	 * Called to enable/disable the menu items that require something
	 * to be selected.
	 *
	 * @param
	 * @param enable
	 */
	public static void setEntriesSelected(final EntryMenuPanel emp, final boolean enable) {
		doAsyncOperation(null, null, false, false, null, null, emp, false, enable);
	}
}
