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

import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent;
import org.kablink.teaming.gwt.client.event.GetManageMenuPopupEvent.ManageMenuPopupCallback;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent.ManageTitleCallback;
import org.kablink.teaming.gwt.client.event.InvokeManageTeamsDlgEvent;
import org.kablink.teaming.gwt.client.event.MenuLoadedEvent.MenuItem;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.HideManageMenuEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageUsersDlgEvent;
import org.kablink.teaming.gwt.client.event.MenuLoadedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeCollapsedEvent;
import org.kablink.teaming.gwt.client.event.TreeNodeExpandedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetTrashUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.TreeMode;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.WorkspaceTreeControlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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
		MenuLoadedEvent.Handler,
		TreeNodeCollapsedEvent.Handler,
		TreeNodeExpandedEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ManageMenuPopup				m_selectorConfigPopup;		//
	private VibeFlowPanel				m_fp;						// The panel holding the content.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.MENU_LOADED,
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
		
		// ...construct the root panel...
		VibeFlowPanel rootContainer = new VibeFlowPanel();
		rootContainer.addStyleName("vibe-binderViewTools vibe-breadCrumbRoot");

		// ...construct the bread crumb tree panel...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-breadCrumbPanel");
		rootContainer.add(m_fp);

		// ...if there's anything on the right...
		VibeFlowPanel rightPanel;
		boolean needsTrashLink    = needsTrashLink();
		boolean needsWhatsNewLink = needsWhatsNewLink();
		if (needsTrashLink || needsWhatsNewLink) {
			// ...add a right panel...
			rightPanel = new VibeFlowPanel();
			rightPanel.addStyleName("vibe-breadCrumbRightPanel");
			rootContainer.add(rightPanel);
		}
		else {
			rightPanel = null;
		}
		
		// ...if required...
		if (needsWhatsNewLink) {
			// ...construct the What's New link...
			InlineLabel whatsNewLabel = new InlineLabel(m_messages.vibeDataTable_WhatsNew());
			whatsNewLabel.addStyleName("vibe-breadCrumbWhatsNewLink");
			whatsNewLabel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showWhatsNewAsync();
				}
			});
			rightPanel.add(whatsNewLabel);
		}
		
		// ...if required...
		if (needsTrashLink) {
			// ...construct the Trash link...
			if (null == rightPanel) {
				rightPanel = new VibeFlowPanel();
				rightPanel.addStyleName("vibe-breadCrumbRightPanel");
			}
			Image i = GwtClientHelper.buildImage(m_images.trashButton().getSafeUri());
			i.addStyleName("vibe-breadCrumbTrashImg");
			Anchor trashAnchor= new Anchor();
			trashAnchor.getElement().setInnerHTML(GwtClientHelper.getWidgetHTML(i));
			trashAnchor.addStyleName("vibe-breadCrumbTrashAnchor");
			trashAnchor.setTitle(m_messages.vibeDataTable_ViewTrash());
			rightPanel.add(trashAnchor);
			trashAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					viewTrashAsync();
				}
			});
		}

		// ...and tie it all together.
		initWidget(rootContainer);
		loadPart1Async();
	}

	/**
	 * Loads the BreadCrumbPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param mvSpec
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(BreadCrumbPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				if (binderInfo.isBinderWorkspace()) {
					GwtClientHelper.consoleLog("BreadCrumbPanel for workspace type: " + binderInfo.getWorkspaceType().name());
				}
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
	 * Adds access to the binder configuration menu.
	 */
	private void addProfileRootConfig(VibeFlowPanel fp) {
		// If the binder doesn't need the configuration menu...
		if (!(needsBinderConfig())) {
			// ...bail.
			return;
		}
		
		// Create an anchor to run the configuration menu on this
		// binder.
		final Anchor  selectorConfigA  = new Anchor();
		final Element selectorConfigAE = selectorConfigA.getElement();
		selectorConfigA.setTitle(m_binderInfo.isBinderFolder() ? m_messages.treeAltConfigureFolder() : m_messages.treeAltConfigureWorkspace());
		Image selectorConfigImg = GwtClientHelper.buildImage(m_images.configOptions());
		selectorConfigImg.addStyleName("breadCrumb_ContentTail_configureImg");
		selectorConfigAE.appendChild(selectorConfigImg.getElement());
		selectorConfigA.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (null == m_selectorConfigPopup)
				     buildAndRunSelectorConfigMenuAsync(selectorConfigA);
				else runSelectorConfigMenuAsync(        selectorConfigA);
			}
		});
		fp.add(selectorConfigA);
		
		// ...and hide the manage menu in the main menu bar.
		HideManageMenuEvent.fireOneAsync();
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
						GwtClientHelper.deferredAlert(m_messages.treeErrorNoManageMenu());
					}
					
					else {
						// Yes, there's stuff in the selector
						// configuration menu!  Complete populating it
						// and run it.
						m_selectorConfigPopup.setCurrentBinder(m_binderInfo);
						m_selectorConfigPopup.populateMenu();
						runSelectorConfigMenuAsync(selectorConfigA);
					}
				}
			}));
	}
	
	/*
	 * Asynchronously handles the panel being resized.
	 */
	private void doResizeAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doResizeNow();
			}
		});
	}
	
	/*
	 * Clears an previous binder configuration panel and menu.
	 */
	private void clearSelectorConfig() {
		// Clear the previous menu.
		if (null != m_selectorConfigPopup) {
			m_selectorConfigPopup.clearItems();
			m_selectorConfigPopup = null;
		}
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
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
		
		// No, we we aren't displaying a bread crumb panel for a
		// collection!  Are we displaying it for the profile, global
		// or team root workspace, or a mobile devices view?
		else if (m_binderInfo.isBinderAdministratorManagement() || m_binderInfo.isBinderLimitUserVisibility() || m_binderInfo.isBinderProfilesRootWS() || m_binderInfo.isBinderGlobalRootWS() || m_binderInfo.isBinderTeamsRootWS() || m_binderInfo.isBinderMobileDevices() || m_binderInfo.isBinderProxyIdentities() || m_binderInfo.isBinderEmailTemplates()) {
			// Yes!  We don't need a tree, just the image and title.
			// Create the panel for it...
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-breadCrumbProfiles-panel");

			// ...create the image...
			ImageResource iRes;
			if (m_binderInfo.isBinderAdministratorManagement()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.adminRoot();        break;
				case MEDIUM:  iRes = m_filrImages.adminRoot_medium(); break;
				case LARGE:   iRes = m_filrImages.adminRoot_large();  break;
				}
			}
			else if (m_binderInfo.isBinderLimitUserVisibility()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.limitedUserVisibility();        break;
				case MEDIUM:  iRes = m_filrImages.limitedUserVisibility_medium(); break;
				case LARGE:   iRes = m_filrImages.limitedUserVisibility_large();  break;
				}
			}
			else if (m_binderInfo.isBinderProfilesRootWS()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.profileRoot();        break;
				case MEDIUM:  iRes = m_filrImages.profileRoot_medium(); break;
				case LARGE:   iRes = m_filrImages.profileRoot_large();  break;
				}
			}
			else if (m_binderInfo.isBinderGlobalRootWS()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.globalRoot();        break;
				case MEDIUM:  iRes = m_filrImages.globalRoot_medium(); break;
				case LARGE:   iRes = m_filrImages.globalRoot_large();  break;
				}
			}
			else if (m_binderInfo.isBinderTeamsRootWS()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.teamRoot();        break;
				case MEDIUM:  iRes = m_filrImages.teamRoot_medium(); break;
				case LARGE:   iRes = m_filrImages.teamRoot_large();  break;
				}
			}
			else if (m_binderInfo.isBinderMobileDevices()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.mobileDevices();        break;
				case MEDIUM:  iRes = m_filrImages.mobileDevices_medium(); break;
				case LARGE:   iRes = m_filrImages.mobileDevices_large();  break;
				}
			}
			else if (m_binderInfo.isBinderProxyIdentities()) {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.proxyIdentities();        break;
				case MEDIUM:  iRes = m_filrImages.proxyIdentities_medium(); break;
				case LARGE:   iRes = m_filrImages.proxyIdentities_large();  break;
				}
			}
			else {
				switch (BinderIconSize.getBreadCrumbIconSize()) {
				default:
				case SMALL:   iRes = m_filrImages.emailTemplates();        break;
				case MEDIUM:  iRes = m_filrImages.emailTemplates_medium(); break;
				case LARGE:   iRes = m_filrImages.emailTemplates_large();  break;
				}
			}
			Image i = GwtClientHelper.buildImage(iRes.getSafeUri().asString());
			i.addStyleName("vibe-breadCrumbProfiles-image");
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
			String txt;
			if      (m_binderInfo.isBinderAdministratorManagement()) txt = m_messages.vibeDataTable_People();
			else if (m_binderInfo.isBinderLimitUserVisibility())     txt = m_messages.vibeDataTable_LimitedUserVisibility();
			else if (m_binderInfo.isBinderProfilesRootWS())          txt = m_messages.vibeDataTable_People();
			else if (m_binderInfo.isBinderGlobalRootWS())            txt = m_messages.vibeDataTable_Globals();
			else if (m_binderInfo.isBinderTeamsRootWS())             txt = m_messages.vibeDataTable_Teams();
			else if (m_binderInfo.isBinderMobileDevices())           txt = m_messages.vibeDataTable_MobileDevices();
			else if (m_binderInfo.isBinderProxyIdentities())         txt = m_messages.vibeDataTable_ProxyIdentities();
			else                                                     txt = m_messages.vibeDataTable_EmailTemplates();
			final InlineLabel il = new InlineLabel(txt);
			il.addStyleName("vibe-breadCrumbProfiles-label");
			fp.add(il);
			if (m_binderInfo.isBinderAdministratorManagement() || m_binderInfo.isBinderLimitUserVisibility() || m_binderInfo.isBinderProfilesRootWSManagement() || m_binderInfo.isBinderTeamsRootWSManagement() || m_binderInfo.isBinderMobileDevices() || m_binderInfo.isBinderProxyIdentities() || m_binderInfo.isBinderEmailTemplates()) {
				GwtTeaming.fireEvent(
					new GetManageTitleEvent(
						m_binderInfo,
						new ManageTitleCallback() {
							@Override
							public void manageTitle(String title) {
								il.setText(title);
							}
						}));
			}
			
			addProfileRootConfig(fp);

			// ...tie it all together and tell our container that we're
			// ...ready.
			m_fp.add(fp);
			toolPanelReady();
		}
		
		else {
			// No, we aren't displaying a bread crumb panel for the
			// profile root workspace either!  We need the full bread
			// crumb tree.
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

	/*
	 * Return true if the binder being viewed requires a binder
	 * configuration menu and false otherwise.
	 */
	private boolean needsBinderConfig() {
		boolean reply = (
			(!(m_binderInfo.isBinderAdministratorManagement()))  &&	// Not on manage administrators...
			(!(m_binderInfo.isBinderLimitUserVisibility()))      &&	// ...or on limited user visibility...
			(!(m_binderInfo.isBinderProfilesRootWSManagement())) &&	// ...or on manage users...
			(!(m_binderInfo.isBinderTeamsRootWSManagement()))    &&	// ...or on manage teams...
			(!(m_binderInfo.isBinderMobileDevices()))            &&	// ...or the mobile devices view...
			(!(m_binderInfo.isBinderProxyIdentities()))          &&	// ...or the proxy identities view...
			(!(m_binderInfo.isBinderEmailTemplates()))           &&	// ...or the email templates view...
			(!(m_binderInfo.isBinderTrash())));						// ...or the trash view.
		
		return reply;
	}
	
	/*
	 * Return true if the binder being viewed requires a Trash link and
	 * false otherwise.
	 */
	private boolean needsTrashLink() {
		boolean reply = (m_binderInfo.isBinderProfilesRootWSManagement() || m_binderInfo.isBinderTeamsRootWSManagement());
		if (!reply) {
			reply = (
				(!(m_binderInfo.isBinderAdministratorManagement())) &&	// Not on view of administrators...
				(!(m_binderInfo.isBinderLimitUserVisibility()))     &&	// ...or limited user visibility...
				(!(m_binderInfo.isBinderProfilesRootWS()))          &&	// ...or view of users...
				(!(m_binderInfo.isBinderTeamsRootWS()))             &&	// ...or view of teams...
				(!(m_binderInfo.isBinderMirroredFolder()))          &&	// ...or any mirrored/net folder...
				(!(m_binderInfo.isBinderMobileDevices()))           &&	// ...or the mobile devices view...
				(!(m_binderInfo.isBinderProxyIdentities()))         &&	// ...or the proxy identities view...
				(!(m_binderInfo.isBinderEmailTemplates()))          &&	// ...or the email templates view...
				(!(m_binderInfo.isBinderTrash())));						// ...or the trash view itself.
			
			if (reply) {
				if (m_binderInfo.isBinderCollection()) {
					switch (m_binderInfo.getCollectionType()) {
					default:        reply = false; break;
					case MY_FILES:  reply = true;  break;
					}
				}
			}
		}
		
		return reply;
	}
	
	/*
	 * Return true if the binder being viewed requires a What's New
	 * link and false otherwise.
	 */
	private boolean needsWhatsNewLink() {
		boolean reply = (
			(!(m_binderInfo.isBinderAdministratorManagement())) &&	// Not on a view of administrators...
			(!(m_binderInfo.isBinderLimitUserVisibility()))     &&	// ...or limited user visibility...
			(!(m_binderInfo.isBinderProfilesRootWS()))          &&	// ...or any view of users...
			(!(m_binderInfo.isBinderTeamsRootWS()))             &&	// ...or any view of teams...
			(!(m_binderInfo.isBinderMobileDevices()))           &&	// ...or the mobile devices view...
			(!(m_binderInfo.isBinderProxyIdentities()))         &&	// ...or the proxy identities view...
			(!(m_binderInfo.isBinderEmailTemplates()))          &&	// ...or the email templates view...
			(!(m_binderInfo.isBinderTrash())));						// ...or the trash view.
		
		return reply;
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
	 * Handles MenuLoadedEvent's received by this class.
	 * 
	 * Implements the MenuLoadedEvent.Handler.onMenuLoaded()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onMenuLoaded(MenuLoadedEvent event) {
		// If we're getting notified that the manage menu has been
		// loaded...
		if (MenuItem.MANAGE_BINDER.equals(event.getMenuItem())) {
			// ...simply null out the selector config popup.  That will
			// ...cause it to get recreated the next time it's needed
			// ...and pull over a new manage menu.
			m_selectorConfigPopup = null;
		}
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
				REGISTERED_EVENTS,
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

	/*
	 * Asynchronously runs What's New on the current binder.
	 */
	private void showWhatsNewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showWhatsNewNow();
			}
		});
	}

	/*
	 * Synchronously runs What's New on the current binder.
	 */
	private void showWhatsNewNow() {
		// Are we viewing a collection?
		ActivityStreamInfo asi = new ActivityStreamInfo();
		asi.setTitle(m_binderInfo.getBinderTitle());
		if (m_binderInfo.isBinderCollection()) {
			// Yes!  Determine the appropriate collection
			// ActivityStream to view.
			ActivityStream as;
			switch (m_binderInfo.getCollectionType()) {
			default:
			case MY_FILES:        as = ActivityStream.MY_FILES;       break;
			case NET_FOLDERS:     as = ActivityStream.NET_FOLDERS;    break;
			case SHARED_BY_ME:    as = ActivityStream.SHARED_BY_ME;   break;
			case SHARED_WITH_ME:  as = ActivityStream.SHARED_WITH_ME; break;
			case SHARED_PUBLIC:   as = ActivityStream.SHARED_PUBLIC;  break;
			}
			asi.setActivityStream(as);
		}
		
		else {
			// No, we are viewing a collection!  We must be viewing
			// a binder.  Determine the appropriate activity stream to
			// view.
			ActivityStream as = (m_binderInfo.isBinderFolder() ? ActivityStream.SPECIFIC_FOLDER : ActivityStream.SPECIFIC_BINDER);
			asi.setActivityStream(as);
			asi.setBinderId(m_binderInfo.getBinderId());
		}
		
		// Finally, fire the appropriate activity stream event.
		GwtTeaming.fireEvent(new ActivityStreamEnterEvent(asi));
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

	/*
	 * Asynchronously runs the trash viewer on the current BinderInfo.
	 */
	private void viewTrashAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				viewTrashNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the trash viewer on the current BinderInfo.
	 */
	private void viewTrashNow() {
		// Are managing users?
		if (m_binderInfo.isBinderProfilesRootWSManagement()) {
			// Yes!  Simply tell the administration console to view the
			// trash on the personal workspaces binder.
			GwtTeaming.fireEventAsync(
				new InvokeManageUsersDlgEvent(
					true));	// true -> Trash view.
		}
		
		// No, we aren't managing users either!  Are we managing teams?
		else if (m_binderInfo.isBinderTeamsRootWSManagement()) {
			// Yes!  Simply tell the administration console to view the
			// trash on the team workspaces binder.
			GwtTeaming.fireEventAsync(
				new InvokeManageTeamsDlgEvent(
					true));	// true -> Trash view.
		}
		
		else {
			// No, we aren't managing teams either!  Get the URL to
			// view the trash on the current BinderInfo...
			GwtClientHelper.executeCommand(new GetTrashUrlCmd(m_binderInfo), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTrashUrl(),
						m_binderInfo.getBinderId());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// ...and navigate to that URL.
					StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
					GwtTeaming.fireEventAsync(new GotoContentUrlEvent(responseData.getStringValue()));
				}
			});
		}
	}
}
