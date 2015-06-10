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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.event.AddPrincipalAdminRightsEvent;
import org.kablink.teaming.gwt.client.event.CalendarShowEvent;
import org.kablink.teaming.gwt.client.event.CalendarShowHintEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ClearScheduledWipeSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.CopyPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedProxyIdentitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.EmailPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ForceSelectedUsersToChangePasswordEvent;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.HideAccessoriesEvent;
import org.kablink.teaming.gwt.client.event.HideSelectedSharesEvent;
import org.kablink.teaming.gwt.client.event.HideUserListEvent;
import org.kablink.teaming.gwt.client.event.InvokeAddNewFolderEvent;
import org.kablink.teaming.gwt.client.event.InvokeAddNewProxyIdentityEvent;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeCopyFiltersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.InvokeSignGuestbookEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ManageSharesSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ManageUsersFilterEvent;
import org.kablink.teaming.gwt.client.event.ScheduleWipeSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.ManageUsersFilterEvent.ManageUsersFilter;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ResetEntryMenuEvent;
import org.kablink.teaming.gwt.client.event.SetFolderSortEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedBinderShareRightsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedPrincipalsAdminRightsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedPrincipalsLimitedUserVisibilityEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedUserDesktopSettingsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedUserMobileSettingsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedUserShareRightsEvent;
import org.kablink.teaming.gwt.client.event.SharedViewFilterEvent;
import org.kablink.teaming.gwt.client.event.SharedViewFilterEvent.SharedViewFilter;
import org.kablink.teaming.gwt.client.event.ShowAccessoriesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ShowSelectedSharesEvent;
import org.kablink.teaming.gwt.client.event.ShowUserListEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ToggleSharedViewEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeAllEvent;
import org.kablink.teaming.gwt.client.event.TrashPurgeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreAllEvent;
import org.kablink.teaming.gwt.client.event.TrashRestoreSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewPinnedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadSelectedFilesEvent;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderFiltersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageUsersStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSharedViewStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersStateRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SharedViewStateRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarShow;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ManageUsersState;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.SharedViewState;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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
import com.google.gwt.user.client.ui.UIObject;
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
	private BinderFiltersRpcResponseData	m_binderFilters;					//
	private BinderInfo						m_binderInfo;						//
	private boolean							m_includeColumnResizer;				//
	private boolean							m_isIE;								//
	private boolean							m_panelInitialized;					// Set true after the panel has completed initializing.
	private boolean							m_viewingPinnedEntries;				//
	private boolean							m_viewingSharedFiles;				//
	private List<HandlerRegistration>		m_registeredEventHandlers;			// Event handlers that are currently registered.
	private List<ToolbarItem>				m_configureToolbarItems;			//
	private List<ToolbarItem>				m_toolbarItems;						//
	private ManageUserFilterItems			m_manageUserFilters;				//
	private ManageUsersState				m_manageUsersState;					//
	private SharedViewFilterItems			m_sharedViewFilters;				//
	private SharedViewState					m_sharedViewState;					//
	private VibeFlexTable					m_grid;								//
	private VibeFlowPanel					m_configPanel;						//
	private VibeFlowPanel					m_filterOptionsPanel;				//
	private VibeFlowPanel					m_filtersPanel;						//
	private VibeFlowPanel					m_quickFilterPanel;					//
	private VibeMenuBar						m_entryMenu;						//
	private VibeMenuItem					m_addAdminRightsMenu;				//
	private VibeMenuItem					m_addFilesMenu;						//
	private VibeMenuItem					m_addLimitUserVisibilityMenu;		//
	private VibeMenuItem					m_addOverrideUserVisibilityMenu;	//
	private VibeMenuItem					m_deleteMenu;						//
	private VibeMenuItem					m_detailsMenu;						//
	private VibeMenuItem					m_moreMenu;							//
	private VibeMenuItem					m_moreSingleItem;					//
	private VibeMenuItem					m_removeLimitUserVisibilityMenu;	//
	private VibeMenuItem					m_setAdminRightsMenu;				//
	private VibeMenuItem					m_shareMenu;						//
	private VibeMenuItem					m_shareSingleItem;					//
	private VibeMenuItem					m_trashPurgeAllMenu;				//
	private VibeMenuItem					m_trashPurgeSelectedMenu;			//
	private VibeMenuItem					m_trashRestoreAllMenu;				//
	private VibeMenuItem					m_trashRestoreSelectedMenu;			//
	private VibeMenuItem					m_wipeMenu;							//
	private VibeMenuItem					m_wipeSingleItem;					//

	private final static String	TBI_MORE_NAME	= "1_more";
	private final static String	TBI_SHARE_NAME	= "1_share";
	private final static String TBI_WIPE_NAME	= "1_wipe";
	
	// The following controls whether the Manage Users dialog's
	// filtering users checks or show vs. hide options.
	private final static boolean MANAGE_USER_FILTERS_WITH_CHECKS	= true;
	
	/**
	 * Inner class used to encapsulate the manage users filter items.
	 */
	public static class ManageUserFilterItems {
		private VibeMenuItem	m_enabledFilter;		//
		private VibeMenuItem	m_externalFilter;		//
		private VibeMenuItem	m_disabledFilter;		//
		private VibeMenuItem	m_internalFilter;		//
		private VibeMenuItem	m_siteAdminsFilter;		//
		private VibeMenuItem	m_nonSiteAdminsFilter;	//
		
		/**
		 * Constructor method.
		 * 
		 * @param internal
		 * @param external
		 * @param disabled
		 * @param enabled
		 * @param siteAdmins
		 * @param nonSiteAdmins
		 */
		public ManageUserFilterItems(VibeMenuItem internal, VibeMenuItem external, VibeMenuItem disabled, VibeMenuItem enabled, VibeMenuItem siteAdmins, VibeMenuItem nonSiteAdmins) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			setInternalFilter(      internal     );
			setExternalFilter(      external     );
			setDisabledFilter(      disabled     );
			setEnabledFilter(       enabled      );
			setSiteAdminsFilter(    siteAdmins   );
			setNonSiteAdminsFilter( nonSiteAdmins);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public VibeMenuItem getEnabledFilter()       {return m_enabledFilter;      }
		public VibeMenuItem getExternalFilter()      {return m_externalFilter;     }
		public VibeMenuItem getDisabledFilter()      {return m_internalFilter;     }
		public VibeMenuItem getInternalFilter()      {return m_disabledFilter;     }
		public VibeMenuItem getSiteAdminsFilter()    {return m_siteAdminsFilter;   }
		public VibeMenuItem getNonSiteAdminsFilter() {return m_nonSiteAdminsFilter;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setEnabledFilter(      VibeMenuItem enabled)       {m_enabledFilter       = enabled;      }
		public void setExternalFilter(     VibeMenuItem external)      {m_externalFilter      = external;     }
		public void setDisabledFilter(     VibeMenuItem disabled)      {m_disabledFilter      = disabled;     }
		public void setInternalFilter(     VibeMenuItem internal)      {m_internalFilter      = internal;     }
		public void setSiteAdminsFilter(   VibeMenuItem siteAdmins)    {m_siteAdminsFilter    = siteAdmins;   }
		public void setNonSiteAdminsFilter(VibeMenuItem nonSiteAdmins) {m_nonSiteAdminsFilter = nonSiteAdmins;}
	}
	
	/**
	 * Inner class used to encapsulate the shared view filter items.
	 */
	public static class SharedViewFilterItems {
		private VibeMenuItem	m_hiddenFilter;		//
		private VibeMenuItem	m_nonHiddenFilter;	//
		
		/**
		 * Constructor method.
		 * 
		 * @param nonHidden
		 * @param hidden
		 */
		public SharedViewFilterItems(VibeMenuItem nonHidden, VibeMenuItem hidden) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			setNonHiddenFilter(nonHidden);
			setHiddenFilter(   hidden   );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public VibeMenuItem getHiddenFilter()    {return m_hiddenFilter;   }
		public VibeMenuItem getNonHiddenFilter() {return m_nonHiddenFilter;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setHiddenFilter(   VibeMenuItem hidden)    {m_hiddenFilter    = hidden;   }
		public void setNonHiddenFilter(VibeMenuItem nonHidden) {m_nonHiddenFilter = nonHidden;}
	}
	
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
	private EntryMenuPanel(RequiresResize containerResizer, BinderInfo binderInfo, boolean viewingPinnedEntries, boolean viewingSharedFiles, boolean includeColumnResizer, ToolPanelReady toolPanelReady, ToolPanelClient tpClient) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_binderInfo           = binderInfo;
		m_viewingPinnedEntries = viewingPinnedEntries;
		m_viewingSharedFiles   = viewingSharedFiles;
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
		loadPart1Async(tpClient);
	}

	/*
	 * Constructs the panels used by the entry menu.
	 */
	private void constructMenuPanels() {
		m_entryMenu = new VibeMenuBar("vibe-entryMenuBar");
		m_entryMenu.getElement().setId(MenuIds.ENTRY_MENU);
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

	/*
	 * Constructs the filter options menu, adds it to the filter
	 * options panel and returns it.
	 */
	private PopupMenu constructFilterDropdownMenu(boolean canHaveCheckedItems) {
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
		final PopupMenu reply = new PopupMenu(true, false, canHaveCheckedItems);
		reply.addStyleName("vibe-filterMenuBarDropDown");
		a.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reply.showRelativeToTarget(m_filterOptionsPanel);
			}
		});
		fp.add(a);
		m_filterOptionsPanel.add(fp);

		// If we get here, reply refers to the filter options menu.
		// Return it.
		return reply;
	}

	/*
	 * Constructs and returns a manage users filter item.
	 */
	private VibeMenuItem constructManageUsersFilterItem(PopupMenu filterDropdownMenu, ManageUsersFilter muf, boolean mufChecked, boolean separatorBefore) {
		if (separatorBefore) {
			filterDropdownMenu.addSeparator();
		}
		String mufText = null;
		if (MANAGE_USER_FILTERS_WITH_CHECKS) {
			switch (muf) {
			case SHOW_DISABLED_USERS:   mufText = m_messages.vibeEntryMenu_ManageUsers_DisabledFilter_Show();      break;
			case SHOW_ENABLED_USERS:    mufText = m_messages.vibeEntryMenu_ManageUsers_EnabledFilter_Show();       break;
			case SHOW_INTERNAL_USERS:   mufText = m_messages.vibeEntryMenu_ManageUsers_InternalFilter_Show();      break;
			case SHOW_EXTERNAL_USERS:   mufText = m_messages.vibeEntryMenu_ManageUsers_ExternalFilter_Show();      break;
			case SHOW_SITE_ADMINS:      mufText = m_messages.vibeEntryMenu_ManageUsers_SiteAdminsFilter_Show();    break;
			case SHOW_NON_SITE_ADMINS:  mufText = m_messages.vibeEntryMenu_ManageUsers_NonSiteAdminsFilter_Show(); break;
			}
		}
		else {
			switch (muf) {
			case SHOW_DISABLED_USERS:   mufText = (mufChecked ? m_messages.vibeEntryMenu_ManageUsers_DisabledFilter_Hide()      : m_messages.vibeEntryMenu_ManageUsers_DisabledFilter_Show());      break;
			case SHOW_ENABLED_USERS:    mufText = (mufChecked ? m_messages.vibeEntryMenu_ManageUsers_EnabledFilter_Hide()       : m_messages.vibeEntryMenu_ManageUsers_EnabledFilter_Show());       break;
			case SHOW_INTERNAL_USERS:   mufText = (mufChecked ? m_messages.vibeEntryMenu_ManageUsers_InternalFilter_Hide()      : m_messages.vibeEntryMenu_ManageUsers_InternalFilter_Show());      break;
			case SHOW_EXTERNAL_USERS:   mufText = (mufChecked ? m_messages.vibeEntryMenu_ManageUsers_ExternalFilter_Hide()      : m_messages.vibeEntryMenu_ManageUsers_ExternalFilter_Show());      break;
			case SHOW_SITE_ADMINS:      mufText = (mufChecked ? m_messages.vibeEntryMenu_ManageUsers_SiteAdminsFilter_Hide()    : m_messages.vibeEntryMenu_ManageUsers_SiteAdminsFilter_Show());    break;
			case SHOW_NON_SITE_ADMINS:  mufText = (mufChecked ? m_messages.vibeEntryMenu_ManageUsers_NonSiteAdminsFilter_Hide() : m_messages.vibeEntryMenu_ManageUsers_NonSiteAdminsFilter_Show()); break;
			}
		}
		VibeMenuItem reply = filterDropdownMenu.addMenuItem(
			new ManageUsersFilterEvent(muf),
			null,
			mufText);
		reply.getElement().setId(muf.name());
		if (MANAGE_USER_FILTERS_WITH_CHECKS) {
			reply.setCheckedState(mufChecked);
		}
		return reply;
	}
	
	/*
	 * Constructs and returns a shared view filter item.
	 */
	private VibeMenuItem constructSharedViewFilterItem(PopupMenu filterDropdownMenu, SharedViewFilter svf, String svfText, boolean svfChecked) {
		VibeMenuItem reply = filterDropdownMenu.addMenuItem(
			new SharedViewFilterEvent(svf),
			null,
			svfText);
		reply.getElement().setId(svf.name());
		reply.setCheckedState(svfChecked);
		return reply;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ManageUserFilterItems getManageUserFilters()  {return m_manageUserFilters; }
	public ManageUsersState      getManageUsersState()   {return m_manageUsersState;  }
	public SharedViewFilterItems getSharedViewFilters()  {return m_sharedViewFilters; }
	public SharedViewState       getSharedViewState()    {return m_sharedViewState;   }
	public VibeFlowPanel         getConfigPanel()        {return m_configPanel;       }
	public VibeFlowPanel         getFilterOptionsPanel() {return m_filterOptionsPanel;}
	public VibeFlowPanel         getFiltersPanel()       {return m_filtersPanel;      }
	public VibeFlowPanel         getQuickFilterPanel()   {return m_quickFilterPanel;  }
	public VibeMenuItem          getAddFilesMenuItem()   {return m_addFilesMenu;      }
	

	/**
	 * Returns a count of the menu actions that appear on the entry
	 * menu.
	 * 
	 * @return
	 */
	public int getMenuActions() {
		int reply = 0;
		ToolbarItem entryTBI = ToolbarItem.getNestedToolbarItem(m_toolbarItems, "ssEntryToolbar");
		if (null != entryTBI) {
			reply = entryTBI.getNestedItemsList().size();
		}
		return reply;
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart1Async(final ToolPanelClient tpClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(tpClient);
			}
		});
	}
	
	/*
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart1Now(final ToolPanelClient tpClient) {
		final Long folderId = m_binderInfo.getBinderIdAsLong();
		GwtClientHelper.executeCommand(
				new GetFolderToolbarItemsCmd(m_binderInfo),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// Notify the client about the entry menu's load
				// status...
				notifiyClientAsync(tpClient, false);	// false -> Loading failed.
				
				// ...and tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderToolbarItems(),
					folderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Notify the client about the entry menu's load
				// status...
				notifiyClientAsync(tpClient, true);	// true -> Loading was successful.
				
				// ...store the toolbar items and continue loading.
				GetFolderToolbarItemsRpcResponseData responseData = ((GetFolderToolbarItemsRpcResponseData) response.getResponseData());
				m_toolbarItems = responseData.getToolbarItems();
				
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
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
					// Store the filter information and continue
					// loading.
					m_binderFilters = ((BinderFiltersRpcResponseData) response.getResponseData());
					loadPart3Async();
				}
			});
		}

		// No, we aren't working with a non-trash folder!  Are we
		// managing the root profiles binder?
		else if (m_binderInfo.isBinderProfilesRootWSManagement()) {
			// Yes!  Get its current state.
			GwtClientHelper.executeCommand(
					new GetManageUsersStateCmd(),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetManageUsersState());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the state information and continue
					// loading.
					m_manageUsersState = ((ManageUsersStateRpcResponseData) response.getResponseData()).getManageUsersState();
					loadPart3Async();
				}
			});
		}

		// No, we aren't managing the root profiles binder either!
		// Are we showing a 'Shared By/With Me' collection?
		else if (m_binderInfo.getCollectionType().isSharedCollection()) {
			// Yes!  Get its current state.
			GwtClientHelper.executeCommand(
					new GetSharedViewStateCmd(m_binderInfo.getCollectionType()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetSharedViewState());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the state information and continue
					// loading.
					m_sharedViewState = ((SharedViewStateRpcResponseData) response.getResponseData()).getSharedViewState();
					loadPart3Async();
				}
			});
		}
		
		else {
			// No, we aren't showing a 'Shared With Me' collection
			// either!  No filtering.  Simply proceed with the next
			// stop of loading.
			loadPart3Async();
		}
	}
	
	/*
	 * Asynchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart3Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now();
			}
		});
	}
	
	/*
	 * Synchronously construct's the contents of the entry menu panel.
	 */
	private void loadPart3Now() {
		// Do we have an entry toolbar?
		ToolbarItem entryTBI = ToolbarItem.getNestedToolbarItem(m_toolbarItems, "ssEntryToolbar");
		if (null != entryTBI) {
			// Yes!  Scan its nested items..
			for (ToolbarItem perEntryTBI:  entryTBI.getNestedItemsList()) {
				// ...rendering each of them.
				List<ToolbarItem> nestedTBI = perEntryTBI.getNestedItemsList();
				if (GwtClientHelper.hasItems(nestedTBI)) {
					if (nestedTBI.size() == 1) {
						VibeMenuItem simpleTBI = renderSimpleTBI(m_entryMenu, null, nestedTBI.get(0), false);
						String tbiName = perEntryTBI.getName();
						if (null != tbiName) {
							if      (tbiName.equals(TBI_MORE_NAME))  m_moreSingleItem  = simpleTBI;
							else if (tbiName.equals(TBI_SHARE_NAME)) m_shareSingleItem = simpleTBI;
							else if (tbiName.equals(TBI_WIPE_NAME))  m_wipeSingleItem  = simpleTBI;
						}
					}
					else {
						renderStructuredTBI(m_entryMenu, null, perEntryTBI);
					}
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
		setEntrySelectedImpl(  false);
		
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

	/*
	 * Asynchronously notifies the client about the load status of the
	 * entry menu panel.
	 */
	private void notifiyClientAsync(final ToolPanelClient tpClient, final boolean onSuccess) {
		// If we don't have a client to notify...
		if (null == tpClient) {
			// ...simply bail...
			return;
		}
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// ...otherwise, notify them.
				notifiyClientNow(tpClient, onSuccess);
			}
		});
	}
	
	/*
	 * Synchronously notifies the client about the load status of the
	 * entry menu panel.
	 */
	private void notifiyClientNow(final ToolPanelClient tpClient, final boolean onSuccess) {
		// If we don't have a client to notify...
		if (null == tpClient) {
			// ...simply bail...
			return;
		}
		
		// ...otherwise, notify them.
		if (onSuccess)
		     tpClient.onSuccess(this);
		else tpClient.onUnavailable();
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
		reply.getElement().setId(MenuIds.ENTRY_DEFINED_FILTER);
		return reply;
	}
	
	/*
	 * Renders any defined filter capabilities applicable to the
	 * current binder.
	 */
	private void renderDefinedFiltering() {
		// If we're rendering the menu for managing users... 
		if (m_binderInfo.isBinderProfilesRootWSManagement()) {
			// ...there are predefined filters that are specific to
			// ...that.  Construct the filter drop down menu...
			PopupMenu filterDropdownMenu = constructFilterDropdownMenu(true);	// true -> Items may be checked.
			filterDropdownMenu.getElement().setId(MenuIds.ENTRY_FILTER_POPUP);
			
			// ...construct the menu items and store them so they can
			// ...be easily accessed by the manage users dialog.
			boolean disabled      = m_manageUsersState.isShowDisabled();
			boolean enabled       = m_manageUsersState.isShowEnabled();
			boolean external      = m_manageUsersState.isShowExternal();
			boolean internal      = m_manageUsersState.isShowInternal();
			boolean siteAdmins    = m_manageUsersState.isShowSiteAdmins();
			boolean nonSiteAdmins = m_manageUsersState.isShowNonSiteAdmins();
			m_manageUserFilters = new ManageUserFilterItems(
				constructManageUsersFilterItem(filterDropdownMenu, ManageUsersFilter.SHOW_INTERNAL_USERS,  internal,      false),
				constructManageUsersFilterItem(filterDropdownMenu, ManageUsersFilter.SHOW_EXTERNAL_USERS,  external,      false),
				constructManageUsersFilterItem(filterDropdownMenu, ManageUsersFilter.SHOW_DISABLED_USERS,  disabled,      true ),
				constructManageUsersFilterItem(filterDropdownMenu, ManageUsersFilter.SHOW_ENABLED_USERS,   enabled,       false),
				constructManageUsersFilterItem(filterDropdownMenu, ManageUsersFilter.SHOW_SITE_ADMINS,     siteAdmins,    true ),
				constructManageUsersFilterItem(filterDropdownMenu, ManageUsersFilter.SHOW_NON_SITE_ADMINS, nonSiteAdmins, false));

			// If the filtering that's in affect causes the list to be
			// empty...
			String warn;
			if      ((!disabled)   && (!enabled))       warn = m_messages.vibeEntryMenu_ManageUsers_Warning_NoUsers1();
			else if ((!internal)   && (!external))      warn = m_messages.vibeEntryMenu_ManageUsers_Warning_NoUsers2();
			else if ((!siteAdmins) && (!nonSiteAdmins)) warn = m_messages.vibeEntryMenu_ManageUsers_Warning_NoUsers3();
			else                                        warn = null;
			if (null != warn) {
				// ...tell the user about it.
				GwtClientHelper.deferredAlert(warn);
			}
			
			return;
		}
		
		// If we're rendering the menu for a Shared By/With Me view... 
		if (m_binderInfo.getCollectionType().isSharedCollection()) {
			// ...for other than guest...
			if (!(GwtClientHelper.isGuestUser())) {
				// ...there are predefined filters that are specific to
				// ...that.  Construct the filter drop down menu...
				PopupMenu filterDropdownMenu = constructFilterDropdownMenu(true);	// true -> Items may be checked.
				filterDropdownMenu.getElement().setId(MenuIds.ENTRY_FILTER_POPUP);
	
				// ...construct the menu items and store them so they
				// ...can be easily accessed by the view.
				boolean nonHidden   = m_sharedViewState.isShowNonHidden();
				boolean hidden      = m_sharedViewState.isShowHidden();
				m_sharedViewFilters = new SharedViewFilterItems(
					constructSharedViewFilterItem(filterDropdownMenu, SharedViewFilter.SHOW_NON_HIDDEN, m_messages.vibeEntryMenu_SharedView_NonHiddenFilter(), nonHidden),
					constructSharedViewFilterItem(filterDropdownMenu, SharedViewFilter.SHOW_HIDDEN,     m_messages.vibeEntryMenu_SharedView_HiddenFilter(),    hidden)  );
				
				// If the filtering that's in affect causes the list to
				// be empty...
				if ((!nonHidden) && (!hidden)) {
					// ...tell the user about it.
					GwtClientHelper.deferredAlert(m_messages.vibeEntryMenu_SharedView_Warning_NoShares());
				}
			}
			
			return;
		}
		
		// If we don't have any binder filter information...
		if (null == m_binderFilters) {
			// ...there's nothing to render.  Bail.
			return;
		}

		// If there aren't any filters defined and the user can't
		// define any...
		final List<BinderFilter> filtersList   = m_binderFilters.getBinderFilters(); int     filtersCount     = ((null == filtersList) ? 0 : filtersList.size());
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
		
		// Create the filter options menu bar.
		PopupMenu filterDropdownMenu = constructFilterDropdownMenu(false);	// false -> Items aren't checked.

		// Create a menu option for copying filters.
		VibeMenuItem mi = new VibeMenuItem(m_messages.vibeEntryMenu_CopyFilters(), new Command() {
			@Override
			public void execute() {
				GwtTeaming.fireEvent(new InvokeCopyFiltersDlgEvent(m_binderInfo));
			}
		});
		mi.getElement().setId(MenuIds.ENTRY_COPY_FILTERS);
		filterDropdownMenu.addMenuItem(mi);
		if ((!hasFilterEditUrl) && (!hasFiltersOffUrl) && (0 < filtersCount)) {
			filterDropdownMenu.addSeparator();
		}
		
		// If we have an edit filters URL...
		if (hasFilterEditUrl) {
			// ...add a menu item for it.
			mi = new VibeMenuItem(m_messages.vibeEntryMenu_ManageFilters(), new Command() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(filterEditUrl));
				}
			});
			mi.getElement().setId(MenuIds.ENTRY_EDIT_FILTERS);
			filterDropdownMenu.addMenuItem(mi);
			if ((!hasFiltersOffUrl) && (0 < filtersCount)) {
				filterDropdownMenu.addSeparator();
			}
		}

		// If we have a filters off URL...
		if (hasFiltersOffUrl) {
			// ...add a menu item for it.
			mi = new VibeMenuItem(m_messages.vibeEntryMenu_ClearFilters(), new Command() {
				@Override
				public void execute() {
					GwtTeaming.fireEvent(new GotoContentUrlEvent(filtersOffUrl));
				}
			});
			mi.getElement().setId(MenuIds.ENTRY_FILTERS_OFF);
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
			qf.getElement().setId(MenuIds.ENTRY_FILTER_COMPOSITE);
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
	private VibeMenuItem renderSimpleTBI(VibeMenuBar menuBar, PopupMenu popupMenu, final ToolbarItem simpleTBI, boolean contentsSelectable) {
		final String        simpleTitle = simpleTBI.getTitle();
		final TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();

		// Is this a menu item to view pinned entries?
		boolean menuTextIsHTML;
		boolean menuIsSimpleImageButton = false;
		String  menuText;
		if ((null != simpleEvent) && TeamingEvents.VIEW_PINNED_ENTRIES.equals(simpleEvent)) {
			// Yes!  Generate the appropriate HTML for the item.
			Image pinImg = new Image(m_viewingPinnedEntries ? m_images.orangePin() : m_images.grayPin());
			pinImg.addStyleName("vibe-entryMenuBarItemImage vibe-entryMenuBarPin");
			pinImg.getElement().setAttribute("align", "absmiddle");
			pinImg.setTitle(
				m_viewingPinnedEntries                         ?
					m_messages.vibeEntryMenu_Alt_Pin_ShowAll() :
					m_messages.vibeEntryMenu_Alt_Pin_ShowPinned());
			VibeFlowPanel html = new VibeFlowPanel();
			html.add(pinImg);
			menuText                = html.getElement().getInnerHTML();
			menuTextIsHTML          = true;
			menuIsSimpleImageButton = true;
		}
		
		// No, this isn't a view pinned entries item!  Is it a toggle
		// shared view item?
		else if ((null != simpleEvent) && TeamingEvents.TOGGLE_SHARED_VIEW.equals(simpleEvent)) {
			// Yes!  Generate the appropriate HTML for the item.
			Image sharedFilesImg = new Image(m_viewingSharedFiles ? m_images.sharedAll() : m_images.sharedFiles());
			sharedFilesImg.addStyleName("vibe-entryMenuBarItemImage vibe-entryMenuBarSharedFiles");
			sharedFilesImg.getElement().setAttribute("align", "absmiddle");
			sharedFilesImg.setTitle(
				m_viewingSharedFiles                              ?
					m_messages.vibeEntryMenu_Alt_Shared_ShowAll() :
					m_messages.vibeEntryMenu_Alt_Shared_ShowFiles());
			VibeFlowPanel html = new VibeFlowPanel();
			html.add(sharedFilesImg);
			menuText                = html.getElement().getInnerHTML();
			menuTextIsHTML          = true;
			menuIsSimpleImageButton = true;
		}
		
		// No, this isn't toggle shared view item either!  Is it a
		// calendar show hint item?
		else if ((null != simpleEvent) && TeamingEvents.CALENDAR_SHOW_HINT.equals(simpleEvent)) {
			// Yes!  Generate the appropriate HTML for the item.
			Image calendarShowHintImg = new Image(m_menuImages.infoButton());
			calendarShowHintImg.addStyleName("vibe-entryMenuBarItemImage vibe-entryMenuBarCalendarShowHint");
			calendarShowHintImg.getElement().setAttribute("align", "absmiddle");
			calendarShowHintImg.setTitle(simpleTitle);
			VibeFlowPanel html = new VibeFlowPanel();
			html.add(calendarShowHintImg);
			menuText                = html.getElement().getInnerHTML();
			menuTextIsHTML          = true;
			menuIsSimpleImageButton = true;
		}
		
		else {
			// No, this isn't a calendar show hint item either!
			// Generate the text to display for the menu item...
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
		VibeMenuItem reply = new VibeMenuItem(menuText, menuTextIsHTML, new Command() {
			@Override
			public void execute() {
				// Does the simple toolbar item contain a URL to
				// launch?
				final String simpleUrl = simpleTBI.getUrl();
				if (GwtClientHelper.hasString(simpleUrl)) {
					// Yes!  Should we launch it in a popup window?
					String	popupS = simpleTBI.getQualifierValue("popup");
					boolean	popup  = (GwtClientHelper.hasString(popupS) && Boolean.parseBoolean(popupS));
					if (popup) {
						// Yes!  Launch it there.
						int width  = GwtClientHelper.iFromS(simpleTBI.getQualifierValue("popupWidth" ), GwtConstants.DEFAULT_POPUP_WIDTH );
						int	height = GwtClientHelper.iFromS(simpleTBI.getQualifierValue("popupHeight"), GwtConstants.DEFAULT_POPUP_HEIGHT);
						GwtClientHelper.jsLaunchUrlInWindow(simpleUrl, "_blank", height, width);
					}
					
					else {
						// No, it doesn't go in a popup window!  Launch
						// it in the content frame.
						OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
							simpleUrl,
							Instigator.GOTO_CONTENT_URL);
						
						if (GwtClientHelper.validateOSBI(osbInfo)) {
							GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
						}
					}
				}
				
				else {
					// No, the simple toolbar item didn't contain a
					// URL!  The only other option is an event.
					Long folderId = m_binderInfo.getBinderIdAsLong();
					
					VibeEventBase<?> event;
					switch (simpleEvent) {
					default:                                            event = EventHelper.createSimpleEvent(                   simpleEvent   ); break;
					case CHANGE_ENTRY_TYPE_SELECTED_ENTITIES:           event = new ChangeEntryTypeSelectedEntitiesEvent(        folderId      ); break;
					case CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES:  event = new ClearScheduledWipeSelectedMobileDevicesEvent(m_binderInfo  ); break;
					case CLEAR_SELECTED_USERS_ADHOC_FOLDERS:            event = new ClearSelectedUsersAdHocFoldersEvent(         folderId      ); break;
					case CLEAR_SELECTED_USERS_DOWNLOAD:                 event = new ClearSelectedUsersDownloadEvent(             folderId      ); break;
					case CLEAR_SELECTED_USERS_WEBACCESS:                event = new ClearSelectedUsersWebAccessEvent(            folderId      ); break;
					case COPY_PUBLIC_LINK_SELECTED_ENTITIES:            event = new CopyPublicLinkSelectedEntitiesEvent(         folderId      ); break;
					case COPY_SELECTED_ENTITIES:                        event = new CopySelectedEntitiesEvent(                   folderId      ); break;
					case DELETE_SELECTED_ENTITIES:                      event = new DeleteSelectedEntitiesEvent(                 folderId      ); break;
					case DELETE_SELECTED_MOBILE_DEVICES:                event = new DeleteSelectedMobileDevicesEvent(            m_binderInfo  ); break;
					case DELETE_SELECTED_PROXY_IDENTITIES:              event = new DeleteSelectedProxyIdentitiesEvent(          m_binderInfo  ); break;
					case DELETE_SELECTED_USERS:                         event = new DeleteSelectedUsersEvent(                    folderId      ); break;
					case DISABLE_SELECTED_USERS:                        event = new DisableSelectedUsersEvent(                   folderId      ); break;
					case DISABLE_SELECTED_USERS_ADHOC_FOLDERS:          event = new DisableSelectedUsersAdHocFoldersEvent(       folderId      ); break;
					case DISABLE_SELECTED_USERS_DOWNLOAD:               event = new DisableSelectedUsersDownloadEvent(           folderId      ); break;
					case DISABLE_SELECTED_USERS_WEBACCESS:              event = new DisableSelectedUsersWebAccessEvent(          folderId      ); break;
					case EMAIL_PUBLIC_LINK_SELECTED_ENTITIES:           event = new EmailPublicLinkSelectedEntitiesEvent(        folderId      ); break;
					case ENABLE_SELECTED_USERS:                         event = new EnableSelectedUsersEvent(                    folderId      ); break;
					case ENABLE_SELECTED_USERS_ADHOC_FOLDERS:           event = new EnableSelectedUsersAdHocFoldersEvent(        folderId      ); break;
					case ENABLE_SELECTED_USERS_DOWNLOAD:                event = new EnableSelectedUsersDownloadEvent(            folderId      ); break;
					case ENABLE_SELECTED_USERS_WEBACCESS:               event = new EnableSelectedUsersWebAccessEvent(           folderId      ); break;
					case FORCE_SELECTED_USERS_TO_CHANGE_PASSWORD:       event = new ForceSelectedUsersToChangePasswordEvent(     folderId      ); break;
					case HIDE_ACCESSORIES:                              event = new HideAccessoriesEvent(                        folderId      ); break;
					case HIDE_SELECTED_SHARES:                          event = new HideSelectedSharesEvent(                     folderId      ); break;
					case HIDE_USER_LIST:                                event = new HideUserListEvent(                           folderId      ); break;
					case INVOKE_ADD_NEW_PROXY_IDENTITITY:               event = new InvokeAddNewProxyIdentityEvent(                            ); break;
					case INVOKE_COLUMN_RESIZER:                         event = new InvokeColumnResizerEvent(                    m_binderInfo  ); break;
					case INVOKE_DROPBOX:                                event = new InvokeDropBoxEvent(                          folderId      ); break;
					case INVOKE_SIGN_GUESTBOOK:                         event = new InvokeSignGuestbookEvent(                    folderId      ); break;
					case LOCK_SELECTED_ENTITIES:                        event = new LockSelectedEntitiesEvent(                   folderId      ); break;
					case UNLOCK_SELECTED_ENTITIES:                      event = new UnlockSelectedEntitiesEvent(                 folderId      ); break;
					case MANAGE_SHARES_SELECTED_ENTITIES:               event = new ManageSharesSelectedEntitiesEvent(           folderId      ); break;
					case MARK_READ_SELECTED_ENTITIES:                   event = new MarkReadSelectedEntitiesEvent(               folderId      ); break;
					case MARK_UNREAD_SELECTED_ENTITIES:                 event = new MarkUnreadSelectedEntitiesEvent(             folderId      ); break;
					case MOVE_SELECTED_ENTITIES:                        event = new MoveSelectedEntitiesEvent(                   folderId      ); break;
					case SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES:         event = new ScheduleWipeSelectedMobileDevicesEvent(      m_binderInfo  ); break;
					case SET_SELECTED_BINDER_SHARE_RIGHTS:              event = new SetSelectedBinderShareRightsEvent(           folderId      ); break;
					case SET_SELECTED_USER_DESKTOP_SETTINGS:            event = new SetSelectedUserDesktopSettingsEvent(         folderId      ); break;
					case SET_SELECTED_USER_MOBILE_SETTINGS:             event = new SetSelectedUserMobileSettingsEvent(          folderId      ); break;
					case SET_SELECTED_USER_SHARE_RIGHTS:                event = new SetSelectedUserShareRightsEvent(             folderId      ); break;
					case SHOW_ACCESSORIES:                              event = new ShowAccessoriesEvent(                        folderId      ); break;
					case SHOW_USER_LIST:                                event = new ShowUserListEvent(                           folderId      ); break;
					case SHARE_SELECTED_ENTITIES:                       event = new ShareSelectedEntitiesEvent(                  folderId      ); break;
					case SHOW_SELECTED_SHARES:                          event = new ShowSelectedSharesEvent(                     folderId      ); break;
					case SUBSCRIBE_SELECTED_ENTITIES:                   event = new SubscribeSelectedEntitiesEvent(              folderId      ); break;
					case TRASH_PURGE_ALL:                               event = new TrashPurgeAllEvent(                          folderId      ); break;
					case TRASH_PURGE_SELECTED_ENTITIES:                 event = new TrashPurgeSelectedEntitiesEvent(             folderId      ); break;
					case TRASH_RESTORE_ALL:                             event = new TrashRestoreAllEvent(                        folderId      ); break;
					case TRASH_RESTORE_SELECTED_ENTITIES:               event = new TrashRestoreSelectedEntitiesEvent(           folderId      ); break;
					case VIEW_PINNED_ENTRIES:                           event = new ViewPinnedEntriesEvent(                      folderId      ); break;
					case VIEW_SELECTED_ENTRY:                           event = new ViewSelectedEntryEvent(                      folderId      ); break;
					case ZIP_AND_DOWNLOAD_SELECTED_FILES:               event = new ZipAndDownloadSelectedFilesEvent(            folderId, true); break;
					
					case ADD_PRINCIPAL_ADMIN_RIGHTS:
						event = new AddPrincipalAdminRightsEvent(folderId, m_addAdminRightsMenu);
						break;
					
					case CALENDAR_SHOW:
						int calendarShow = Integer.parseInt(simpleTBI.getQualifierValue("calendarShow"));
						event = new CalendarShowEvent(folderId, CalendarShow.getEnum(calendarShow));
						break;

					case CALENDAR_SHOW_HINT:
						int calendarShowHint = Integer.parseInt(simpleTBI.getQualifierValue("calendarShow"));
						event = new CalendarShowHintEvent(folderId, CalendarShow.getEnum(calendarShowHint));
						break;

					case INVOKE_ADD_NEW_FOLDER:
						String	folderTemplateId = simpleTBI.getQualifierValue("folderTemplateId");
						String	folderTargetIdS  = simpleTBI.getQualifierValue("folderTargetId"  );
						Long	folderTargetId;
						if (GwtClientHelper.hasString(folderTargetIdS))
						     folderTargetId = Long.parseLong(folderTargetIdS);
						else folderTargetId = folderId;
						boolean allowCloudFolder =
							(GwtClientHelper.isCloudFoldersEnabled() &&
							 m_binderInfo.isBinderCollection()       &&
							 m_binderInfo.getCollectionType().isMyFiles());
						event = new InvokeAddNewFolderEvent(folderTargetId, Long.parseLong(folderTemplateId), allowCloudFolder);
						break;
						
					case SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS:
						String setRights = simpleTBI.getQualifierValue("setRights");
						event = new SetSelectedPrincipalsAdminRightsEvent(folderId, Boolean.parseBoolean(setRights));
						break;
						
					case SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY:
						String  str             = simpleTBI.getQualifierValue("limited");
						Boolean limited         = (GwtClientHelper.hasString(str) ? Boolean.parseBoolean(str) : null);
						str                     = simpleTBI.getQualifierValue("override");
						Boolean override        = (GwtClientHelper.hasString(str) ? Boolean.parseBoolean(str) : null);
						str                     = simpleTBI.getQualifierValue("selectPrincipal");
						boolean selectPrincipal = (GwtClientHelper.hasString(str) ? Boolean.parseBoolean(str) : false);
						SetSelectedPrincipalsLimitedUserVisibilityEvent sspluvEvent = new SetSelectedPrincipalsLimitedUserVisibilityEvent(null, limited, override, selectPrincipal);
						if (selectPrincipal) {
							UIObject showRelativeTo;
							if      ((null != limited)  && limited)  showRelativeTo = m_addLimitUserVisibilityMenu;
							else if ((null != override) && override) showRelativeTo = m_addOverrideUserVisibilityMenu;
							else                                     showRelativeTo = null;
							sspluvEvent.setShowRelativeTo(showRelativeTo);
						}
						event = sspluvEvent;
						break;
						
					case SET_FOLDER_SORT:
						String sortKey        = simpleTBI.getQualifierValue("sortKey"       );
						String sortDescending = simpleTBI.getQualifierValue("sortDescending");
						event = new SetFolderSortEvent(folderId, sortKey, Boolean.parseBoolean(sortDescending));
						break;
						
					case TOGGLE_SHARED_VIEW:
						event = new ToggleSharedViewEvent(m_binderInfo.getCollectionType());
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
		reply.getElement().setId(simpleTBI.getName());
		switch (simpleTBI.getTeamingEvent()) {
		case ADD_PRINCIPAL_ADMIN_RIGHTS:        m_addAdminRightsMenu       = reply; break;
		case INVOKE_DROPBOX:                    m_addFilesMenu             = reply; break;
		case DELETE_SELECTED_ENTITIES:          m_deleteMenu               = reply; break;
		case DELETE_SELECTED_MOBILE_DEVICES:    m_deleteMenu               = reply; break;
		case DELETE_SELECTED_PROXY_IDENTITIES:  m_deleteMenu               = reply; break;
		case DELETE_SELECTED_USERS:             m_deleteMenu               = reply; break;
		case TRASH_PURGE_ALL:                   m_trashPurgeAllMenu        = reply; break;
		case TRASH_PURGE_SELECTED_ENTITIES:     m_trashPurgeSelectedMenu   = reply; break;
		case TRASH_RESTORE_ALL:                 m_trashRestoreAllMenu      = reply; break;
		case TRASH_RESTORE_SELECTED_ENTITIES:   m_trashRestoreSelectedMenu = reply; break;
		case VIEW_SELECTED_ENTRY:               m_detailsMenu              = reply; break;
		
		case SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS:
			if (m_binderInfo.isBinderAdministratorManagement()) {
				m_setAdminRightsMenu = reply;
			}
			break;
			
		case SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY:
			if (m_binderInfo.isBinderLimitUserVisibility()) {
				String limited  = simpleTBI.getQualifierValue("limited" );
				String override = simpleTBI.getQualifierValue("override");
				String falseS   = String.valueOf(false);
				String trueS    = String.valueOf(true );
				if ((null != limited)  && limited.equals( falseS) &&
				    (null != override) && override.equals(falseS)) {
					m_removeLimitUserVisibilityMenu = reply;
				}
				else if ((null != limited)  && limited.equals( trueS)) {
					m_addLimitUserVisibilityMenu = reply;
				}
				else if ((null != override) && override.equals(trueS)) {
					m_addOverrideUserVisibilityMenu = reply;
				}
			}
			break;
			
		case SHARE_SELECTED_ENTITIES:
			if ((null == m_shareMenu) && (null == m_shareSingleItem)) {
				m_shareSingleItem = reply;
			}
			break;
		}
		
		reply.addStyleName((menuBar == m_entryMenu) ? "vibe-entryMenuBarItem" : "vibe-entryMenuPopupItem");
		if (menuIsSimpleImageButton) {
			reply.addStyleName("vibe-entryMenuBarSimpleImageButton");
		}
		
		if (null != menuBar)
		     menuBar.addItem(      reply);
		else popupMenu.addMenuItem(reply);

		// If we get here, reply contains the simple ToolbarItem's
		// menu item.  Return it.
		return reply;
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
		structuredMenuBar.getElement().setId(structuredTBI.getName());
		structuredMenuBar.addStyleName("vibe-entryMenuPopup");
		VibeMenuItem structuredMenuItem = new VibeMenuItem(structuredTBI.getTitle(), structuredMenuBar);
		structuredMenuItem.getElement().setId(structuredTBI.getName() + "_Item");
		structuredMenuItem.addStyleName("vibe-entryMenuBarItem");
		structuredMenuItem.setHTML(renderStructuredItemHTML(structuredTBI.getTitle(), true));
		if (null != menuBar)
		     menuBar.addItem(      structuredMenuItem);
		else popupMenu.addMenuItem(structuredMenuItem);
		
		String structuredName = structuredTBI.getName();
		if (GwtClientHelper.hasString(structuredName)) {
			if      (structuredName.equals(TBI_MORE_NAME))  m_moreMenu  = structuredMenuItem;
			else if (structuredName.equals(TBI_SHARE_NAME)) m_shareMenu = structuredMenuItem;
			else if (structuredName.equals(TBI_WIPE_NAME))  m_wipeMenu  = structuredMenuItem;
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
		loadPart1Async(null);
	}

	/*
	 * Called to enable/disable the menu items that require something
	 * to be available (i.e., a data table is not empty, ...)
	 */
	private void setEntriesAvailableImpl(boolean dataAvailable) {
		// If we have a trash purge all menu item...
		if (null != m_trashPurgeAllMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_trashPurgeAllMenu, dataAvailable);
		}
		
		// If we have a trash restore all menu item...
		if (null != m_trashRestoreAllMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_trashRestoreAllMenu, dataAvailable);
		}
	}
	
	/*
	 * Called to enable/disable the menu items that require something
	 * to be selected.
	 */
	private void setEntriesSelectedImpl(boolean enable) {
		// If we have a share popup menu item...
		if (null != m_shareMenu) {
			// ...enable disable it...
		    setMenuPopupEnabled(m_shareMenu, enable);
		}

		// If we have a share menu item...
		if (null != m_shareSingleItem) {
			// ...enable disable it...
			setMenuItemEnabled(m_shareSingleItem, enable);
		}

		// If we have a delete menu item...
		if (null != m_deleteMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_deleteMenu, enable);
		}
		
		// If we have a set admin rights menu item...
		if (null != m_setAdminRightsMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_setAdminRightsMenu, enable);
		}

		// If we have a remove limit user visibility menu item...
		if (null != m_removeLimitUserVisibilityMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_removeLimitUserVisibilityMenu, enable);
		}

		// If we have a more popup menu item...
		if (null != m_moreMenu) {
			// ...enable/disable it...
			setMenuPopupEnabled(m_moreMenu, enable);
		}
		
		// If we have a more menu item...
		if (null != m_moreSingleItem) {
			// ...enable disable it...
			setMenuItemEnabled(m_moreSingleItem, enable);
		}

		// If we have a wipe popup menu item...
		if (null != m_wipeMenu) {
			// ...enable/disable it...
			setMenuPopupEnabled(m_wipeMenu, enable);
		}
		
		// If we have a wipe menu item...
		if (null != m_wipeSingleItem) {
			// ...enable disable it...
			setMenuItemEnabled(m_wipeSingleItem, enable);
		}

		// If we have a trash purge selected menu item...
		if (null != m_trashPurgeSelectedMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_trashPurgeSelectedMenu, enable);
		}
		
		// If we have a trash restore selected menu item...
		if (null != m_trashRestoreSelectedMenu) {
			// ...enable disable it.
			setMenuItemEnabled(m_trashRestoreSelectedMenu, enable);
		}
	}
	
	/*
	 * Called to enable/disable the menu items that require a single
	 * entry to be selected.
	 */
	private void setEntrySelectedImpl(boolean enable) {
		// If we have a details menu item...
		if (null != m_detailsMenu) {
			// ...enable disable it.
			m_detailsMenu.setEnabled(enable);
			if (enable)
			     m_detailsMenu.removeStyleName("vibe-menuDisabled");
			else m_detailsMenu.addStyleName(   "vibe-menuDisabled");
		}
	}

	/*
	 * Enables/disables a VibeMenuItem that represents a simple menu
	 * item.
	 */
	private void setMenuItemEnabled(VibeMenuItem mi, boolean enable) {
		// Enabled/disable the menu item.
		mi.setEnabled(enable);
		if (enable)
		     mi.removeStyleName("vibe-menuDisabled");
		else mi.addStyleName(   "vibe-menuDisabled");
	}
	
	/*
	 * Enables/disables a VibeMenuItem that represents a popup menu
	 * item.
	 */
	private void setMenuPopupEnabled(VibeMenuItem mp, boolean enable) {
		// Enabled/disable the menu item...
		setMenuItemEnabled(mp, enable);
		
		// ...and update its display to reflect the state of the
		// ...menu item (in particular, the drop down image on the
		// ...menu.)
		mp.setHTML(
			renderStructuredItemHTML(
				mp.getText(),
				enable));
	}
	
	/*
	 * Returns true if the binder this menu is running against supports
	 * quick filtering and false otherwise.
	 */
	private boolean supportsQuickFilter() {
		boolean reply = false;
		
		switch (m_binderInfo.getBinderType()) {
		case COLLECTION:
			// All collections support the quick filters.
			reply = true;
			break;
			
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
			case PHOTOALBUM:
			case SURVEY:
			case TASK:
			case WIKI:
				reply = true;
				break;
			}
			
			break;
			
		case WORKSPACE:
			switch (m_binderInfo.getWorkspaceType()) {
			case MOBILE_DEVICES:
				reply = m_binderInfo.getMobileDevicesViewSpec().isSystem();
				break;

			case ADMINISTRATOR_MANAGEMENT:
			case GLOBAL_ROOT:
			case LIMIT_USER_VISIBILITY:
			case PROFILE_ROOT:
			case PROFILE_ROOT_MANAGEMENT:
			case TEAM_ROOT:
			case TEAM_ROOT_MANAGEMENT:
				reply = true;
				break;
			}
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
			final boolean			viewingSharedFiles,
			final boolean			includeColumnResizer,
			final ToolPanelReady	toolPanelReady,
			final ToolPanelClient 	tpClient,
			
			// setEntriesAvailable/Selected parameters.
			final EntryMenuPanel	emp,
			final boolean			setAvailable,
			final boolean			setEntries,
			final boolean			setEntry,
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
					// Yes!  Create it.  Note that its construction
					// flow will call the appropriate method off the
					// ToolPanelClient object.
					new EntryMenuPanel(
						containerResizer,
						binderInfo,
						viewingPinnedEntries,
						viewingSharedFiles,
						includeColumnResizer,
						toolPanelReady,
						tpClient);
				}
				
				else {
					// No, it's not a request to create an entry menu
					// panel!  It must be a notification about entries
					// being available or selected.
					if      (setAvailable) emp.setEntriesAvailableImpl(enable);
					else if (setEntries)   emp.setEntriesSelectedImpl( enable);
					else if (setEntry)     emp.setEntrySelectedImpl(   enable);
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
	 * @param viewPinnedEntries
	 * @param viewSharedFiles
	 * @param includeColumnResizer
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final boolean viewingPinnedEntries, final boolean viewingSharedFiles, final boolean includeColumnResizer, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		doAsyncOperation(containerResizer, binderInfo, viewingPinnedEntries, viewingSharedFiles, includeColumnResizer, toolPanelReady, tpClient, null, false, false, false, false);
	}
	
	/**
	 * Called to enable/disable the menu items that require something
	 * to be available (i.e., a data table is not empty, ...)
	 * 
	 * @param emp
	 * @param enable
	 */
	public static void setEntriesAvailable(final EntryMenuPanel emp, final boolean enable) {
		doAsyncOperation(null, null, false, false, false, null, null, emp, true, false, false, enable);
	}
	
	/**
	 * Called to enable/disable the menu items that require something
	 * to be selected.
	 *
	 * @param emp
	 * @param enable
	 */
	public static void setEntriesSelected(final EntryMenuPanel emp, final boolean enable) {
		doAsyncOperation(null, null, false, false, false, null, null, emp, false, true, false, enable);
	}
	
	/**
	 * Called to enable/disable the menu items that require a single
	 * entry to be selected.
	 *
	 * @param emp
	 * @param enable
	 */
	public static void setEntrySelected(final EntryMenuPanel emp, final boolean enable) {
		doAsyncOperation(null, null, false, false, false, null, null, emp, false, false, true, enable);
	}
}
