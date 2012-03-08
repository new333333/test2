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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedUserWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.InvokeSignGuestbookEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedUserWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TrashPurgeAllEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreAllEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderFiltersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderInfo;
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


/**
 * Class used for the content of the entry menus in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class EntryMenuPanel extends ToolPanelBase {
	private BinderFiltersRpcResponseData	m_binderFilters;			//
	private BinderInfo						m_binderInfo;				//
	private boolean							m_includeColumnResizer;		//
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
	private VibeMenuItem					m_trashPurgeAllMenu;		//
	private VibeMenuItem					m_trashPurgeSelectedMenu;	//
	private VibeMenuItem					m_trashRestoreAllMenu;		//
	private VibeMenuItem					m_trashRestoreSelectedMenu;	//
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EntryMenuPanel(RequiresResize containerResizer, BinderInfo binderInfo, boolean includeColumnResizer, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_binderInfo           = binderInfo;
		m_includeColumnResizer = includeColumnResizer;

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
		m_grid.setWidget(0, 1, rightPanel);
		m_grid.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		
		m_filtersPanel = new VibeFlowPanel();
		m_filtersPanel.addStyleName("vibe-entryMenu-filtersPanel");
		rightPanel.add(m_filtersPanel);
		
		m_quickFilterPanel = new VibeFlowPanel();
		m_quickFilterPanel.addStyleName("vibe-entryMenu-quickFilters-panel");
		rightPanel.add(m_quickFilterPanel);
		
		m_filterOptionsPanel = new VibeFlowPanel();
		m_filterOptionsPanel.addStyleName("vibe-entryMenu-filterOptions-panel");
		rightPanel.add(m_filterOptionsPanel);
		
		m_configPanel = new VibeFlowPanel();
		m_configPanel.addStyleName("vibe-entryMenu-configPanel");
		rightPanel.add(m_configPanel);
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
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final boolean includeColumnResizer, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(EntryMenuPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				EntryMenuPanel emp = new EntryMenuPanel(containerResizer, binderInfo, includeColumnResizer, toolPanelReady);
				tpClient.onSuccess(emp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_EntryMenuPanel());
				tpClient.onUnavailable();
			}
		});
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
					renderStructuredTBI(m_entryMenu, perEntryTBI);
				}
				else if (perEntryTBI.isSeparator()) {
					m_entryMenu.addSeparator();
				}
				else {
					renderSimpleTBI(m_entryMenu, perEntryTBI);
				}
			}
		}
		setEntriesSelected(false);
		
		// Render the various right end capabilities applicable to the
		// current binder.
		renderConfigureTBI();
		renderQuickFilter();
		renderDefinedFiltering();
		
		// Finally, tell who's using this tool panel that it's ready to
		// go.
		toolPanelReady();
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
		Image configureImg = new Image(m_images.configOptions());
		configureImg.addStyleName("vibe-configureMenuImg");
		configureImg.getElement().setAttribute("align", "absmiddle");
		fp.add(configureImg);
		VibeMenuBar  configureMenu         = new VibeMenuBar(      "vibe-configureMenuBar vibe-entryMenuBar");
		VibeMenuBar  configureDropdownMenu = new VibeMenuBar(true, "vibe-configureMenuBarDropDown"          );
		VibeMenuItem configureItem         = new VibeMenuItem(fp.getElement().getInnerHTML(), true, configureDropdownMenu, "vibe-configureMenuBarItem");
		configureItem.setTitle(m_messages.vibeEntryMenu_Alt_ListOptions());
		configureMenu.addItem(configureItem);
		m_configPanel.add(configureMenu);
		
		// ...scan the configure toolbar items...
		for (ToolbarItem configureTBI:  m_configureToolbarItems) {
			// ...adding each to the configure menu.
			if (configureTBI.hasNestedToolbarItems()) {
				renderStructuredTBI(configureDropdownMenu, configureTBI);
			}
			else if (configureTBI.isSeparator()) {
				configureDropdownMenu.addSeparator();
			}
			else {
				renderSimpleTBI(configureDropdownMenu, configureTBI);
			}
		}
	}

	/*
	 * Renders a current filter into the filters panel.
	 */
	private void renderCurrentFilter(String filterName, final String filtersOffUrl, boolean lastCurrentFilter) {
		// Create a panel for the filter...
		VibeFlowPanel perFilterPanel = new VibeFlowPanel();
		perFilterPanel.addStyleName("vibe-filterMenuSavedPanel");
		if (!lastCurrentFilter) {
			perFilterPanel.addStyleName("marginright3px");
		}
		m_filtersPanel.add(perFilterPanel);

		// ...create a label for the filter...
		InlineLabel il = new InlineLabel(filterName);
		il.addStyleName("vibe-filterMenuSavedLabel");
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
		Image filterImg = new Image(m_images.menuButton());
		filterImg.addStyleName("vibe-filterMenuImg");
		filterImg.getElement().setAttribute("align", "absmiddle");
		fp.add(filterImg);
		VibeMenuBar  filterMenu         = new VibeMenuBar(      "vibe-filterMenuBar vibe-entryMenuBar");
		VibeMenuBar  filterDropdownMenu = new VibeMenuBar(true, "vibe-filterMenuBarDropDown"          );
		VibeMenuItem filterItem         = new VibeMenuItem(fp.getElement().getInnerHTML(), true, filterDropdownMenu, "vibe-filterMenuBarItem");
		filterItem.setTitle(m_messages.vibeEntryMenu_Alt_FilterOptions());
		filterMenu.addItem(filterItem);
		m_filterOptionsPanel.add(filterMenu);
		
		// If we have an edit filters URL...
		if (hasFilterEditUrl) {
			// ...add a menu item for it.
			VibeMenuItem mi = new VibeMenuItem(m_messages.vibeEntryMenu_ManageFilters(), new Command() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(filterEditUrl));
				}
			});
			filterDropdownMenu.addItem(mi);
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
			filterDropdownMenu.addItem(mi);
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
				if (bf.isGlobal())
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
						checkImg.addStyleName("vibe-filterMenuBarCheck");
						checkImg.getElement().setAttribute("align", "absmiddle");
					}
					else {
						checkImg = new Image(m_images.spacer1px());
						checkImg.addStyleName("vibe-filterMenuBarCheck");
						checkImg.setWidth("12px");
					}
					html.add(checkImg);
					html.add(new InlineLabel(fName));
					menuText = html.getElement().getInnerHTML();
				}
				else {
					menuText = fName;
				}
				VibeMenuItem mi = new VibeMenuItem(menuText, hasCurrentFilter, new Command() {
					@Override
					public void execute() {
						GwtTeaming.fireEvent(new GotoContentUrlEvent(bf.getFilterAddUrl()));
					}
				});
				filterDropdownMenu.addItem(mi);
				
				// If this is a current filter...
				if (isCurrent) {
					// ...add it to the filters panel.
					currentsShown += 1;
					renderCurrentFilter(
						fName,
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
	private void renderSimpleTBI(VibeMenuBar menuBar, final ToolbarItem simpleTBI) {
		VibeMenuItem menuItem = new VibeMenuItem(simpleTBI.getTitle(), new Command() {
			@Override
			public void execute() {
				// Does the simple toolbar item contain a URL to
				// launch?
				final String simpleUrl = simpleTBI.getUrl();
				if (GwtClientHelper.hasString(simpleUrl)) {
					// Yes!  Launch it in the content frame.
					OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
						simpleUrl,
						false,	// false -> Not trash.
						Instigator.GOTO_CONTENT_URL);
					
					if (GwtClientHelper.validateOSBI(osbInfo)) {
						GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
					}
				}
				
				else {
					// No, the simple toolbar item didn't contain a
					// URL!  The only other option is an event.
					TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();
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
					case INVOKE_COLUMN_RESIZER:               event = new InvokeColumnResizerEvent(           folderId   ); break;
					case INVOKE_DROPBOX:                      event = new InvokeDropBoxEvent(                 folderId   ); break;
					case INVOKE_SIGN_GUESTBOOK:               event = new InvokeSignGuestbookEvent(           folderId   ); break;
					case LOCK_SELECTED_ENTRIES:               event = new LockSelectedEntriesEvent(           folderId   ); break;
					case UNLOCK_SELECTED_ENTRIES:             event = new UnlockSelectedEntriesEvent(         folderId   ); break;
					case MARK_READ_SELECTED_ENTRIES:          event = new MarkReadSelectedEntriesEvent(       folderId   ); break;
					case MOVE_SELECTED_ENTRIES:               event = new MoveSelectedEntriesEvent(           folderId   ); break;
					case PURGE_SELECTED_ENTRIES:              event = new PurgeSelectedEntriesEvent(          folderId   ); break;
					case PURGE_SELECTED_USER_WORKSPACES:      event = new PurgeSelectedUserWorkspacesEvent(   folderId   ); break;
					case PURGE_SELECTED_USERS:                event = new PurgeSelectedUsersEvent(            folderId   ); break;
					case SHARE_SELECTED_ENTRIES:              event = new ShareSelectedEntriesEvent(          folderId   ); break;
					case SUBSCRIBE_SELECTED_ENTRIES:          event = new SubscribeSelectedEntriesEvent(      folderId   ); break;
					case TRASH_PURGE_ALL:                     event = new TrashPurgeAllEvent(                 folderId   ); break;
					case TRASH_PURGE_SELECTED_ENTRIES:        event = new TrashPurgeSelectedEntriesEvent(     folderId   ); break;
					case TRASH_RESTORE_ALL:                   event = new TrashRestoreAllEvent(               folderId   ); break;
					case TRASH_RESTORE_SELECTED_ENTRIES:      event = new TrashRestoreSelectedEntriesEvent(   folderId   ); break;
			        			        					
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
		case TRASH_PURGE_ALL:                 m_trashPurgeAllMenu        = menuItem; break;
		case TRASH_PURGE_SELECTED_ENTRIES:    m_trashPurgeSelectedMenu   = menuItem; break;
		case TRASH_RESTORE_ALL:               m_trashRestoreAllMenu      = menuItem; break;
		case TRASH_RESTORE_SELECTED_ENTRIES:  m_trashRestoreSelectedMenu = menuItem; break;
		}
		menuItem.addStyleName((menuBar == m_entryMenu) ? "vibe-entryMenuBarItem" : "vibe-entryMenuPopupItem");
		menuBar.addItem(menuItem);
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
		if (!(GwtClientHelper.jsIsIE())) {
			dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImgNonIE");
		}
		htmlPanel.add(dropDownImg);
		
		return htmlPanel.getElement().getInnerHTML();
	}
	
	/*
	 * Renders any toolbar item that contains nested toolbar items.
	 */
	private void renderStructuredTBI(VibeMenuBar menuBar, ToolbarItem structuredTBI) {
		// Create a drop down menu for the structured toolbar item...
		VibeMenuBar	structuredMenuBar = new VibeMenuBar(true);	// true -> Vertical drop down menu.
		structuredMenuBar.addStyleName("vibe-entryMenuPopup");
		VibeMenuItem structuredMenuItem = new VibeMenuItem(structuredTBI.getTitle(), structuredMenuBar);
		structuredMenuItem.addStyleName("vibe-entryMenuBarItem");
		structuredMenuItem.setHTML(renderStructuredItemHTML(structuredTBI.getTitle(), true));
		menuBar.addItem(structuredMenuItem);
		
		String structuredName = structuredTBI.getName();
		if (GwtClientHelper.hasString(structuredName) && structuredName.equals("1_more")) {
			m_moreMenu = structuredMenuItem;
		}
		
		// ...scan the nested items...
		for (ToolbarItem nestedTBI:  structuredTBI.getNestedItemsList()) {
			// ...rendering each of them.
			if (nestedTBI.hasNestedToolbarItems()) {
				renderStructuredTBI(structuredMenuBar, nestedTBI);
			}
			else if (nestedTBI.isSeparator()) {
				structuredMenuBar.addSeparator();
			}
			else {
				renderSimpleTBI(structuredMenuBar, nestedTBI);
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

	/**
	 * Called to enable/disable the menu items that require something
	 * to be available (i.e., a data table is not empty, ...)
	 * 
	 * @param enable
	 */
	public void setEntriesAvailable(boolean dataAvailable) {
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
	
	/**
	 * Called to enable/disable the menu items that require something
	 * to be selected.
	 * 
	 * @param enable
	 */
	public void setEntriesSelected(boolean enable) {
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
			case DISCUSSION:
			case FILE:
			case GUESTBOOK:
			case MINIBLOG:
			case MIRROREDFILE:
			case SURVEY:
			case TASK:
				reply = true;
				break;
			}
		}
		return reply;
	}
}
