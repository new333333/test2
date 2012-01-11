/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeColumnResizerEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;


/**
 * Class used for the content of the entry menus in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class EntryMenuPanel extends ToolPanelBase {
	private BinderInfo			m_binderInfo;	//
	private boolean				m_includeColumnResizer;	//
	private List<ToolbarItem>	m_toolbarIems;	//
	private VibeFlowPanel		m_fp;			// The panel holding the AccessoryPanel's contents.
	private VibeMenuBar			m_entryMenu;	//
	private VibeMenuItem		m_addFilesMenu;	//
	private VibeMenuItem		m_deleteMenu;	//
	private VibeMenuItem		m_purgeMenu;	//
	
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
		m_binderInfo   = binderInfo;
		m_includeColumnResizer = includeColumnResizer;

		// ...construct and initialize the panel...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-entryMenuPanel");
		m_entryMenu = new VibeMenuBar("vibe-entryMenuBar");
		m_fp.add(m_entryMenu);
		initWidget(m_fp);
		
		// ...and load the menu.
		loadPart1Async();
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
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EntryMenuPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/**
	 * Returns the add files menu item, if displayed on the menu.
	 * 
	 * @return
	 */
	public VibeMenuItem getAddFilesMenuItem() {
		return m_addFilesMenu;
	}
	
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
				new GetFolderToolbarItemsCmd(folderId),
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
				GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
				m_toolbarIems = responseData.getToolbarItems();
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
		// Are we supposed include a column sizing button?
		if(m_includeColumnResizer) {
			// Yes!  Render it.
			renderImageMenuItem(
				m_entryMenu,
				m_images.sizingArrows(),
				m_messages.vibeDataTable_Alt_ColumnResizer(),
				new InvokeColumnResizerEvent(
					m_binderInfo.getBinderIdAsLong()));
		}
		
		// Do we have an entry toolbar?
		ToolbarItem entryTBI = ToolbarItem.getNestedToolbarItem(m_toolbarIems, "ssEntryToolbar");
		if (null != entryTBI) {
			// Yes!  Scan its nested items..
			for (ToolbarItem perEntryTBI:  entryTBI.getNestedItemsList()) {
				// ...rendering each of them.
				if (perEntryTBI.hasNestedToolbarItems())
				     renderStructuredTBI(m_entryMenu, perEntryTBI);
				else renderSimpleTBI(    m_entryMenu, perEntryTBI);
			}
		}
		setDeleteAndPurgeState(false);
		
		// Finally, tell who's using this tool panel that it's ready to
		// go.
		toolPanelReady();
	}

	/*
	 * Renders a menu item containing an image that simply fires an
	 * event when triggered.
	 */
	private void renderImageMenuItem(VibeMenuBar menuBar, ImageResource imgRes, String imgAlt, final VibeEventBase<?> event) {
		FlowPanel fp = new FlowPanel();
		Image img = new Image(imgRes);
		img.setTitle(imgAlt);
		fp.add(img);
		VibeMenuItem menuItem = new VibeMenuItem(fp.getElement().getInnerHTML(), true, new Command() {
			@Override
			public void execute() {
				GwtTeaming.fireEvent(event);
			}
		});
		menuItem.addStyleName((menuBar == m_entryMenu) ? "vibe-entryMenuBarItem" : "vibe-entryMenuPopupItem");
		menuBar.addItem(menuItem);
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
					default:                       event = EventHelper.createSimpleEvent( simpleEvent); break;
					case PURGE_SELECTED_ENTRIES:   event = new PurgeSelectedEntriesEvent( folderId   ); break;
					case DELETE_SELECTED_ENTRIES:  event = new DeleteSelectedEntriesEvent(folderId   ); break;
					case INVOKE_DROPBOX:           event = new InvokeDropBoxEvent(        folderId   ); break;
					case UNDEFINED:
						Window.alert(GwtTeaming.getMessages().eventHandling_NoEntryMenuHandler(simpleEvent.name()));
						event = null;
					}
					
					if (null != event) {
						GwtTeaming.fireEvent(event);
					}
				}
			}
		});
		switch (simpleTBI.getTeamingEvent()) {
		case INVOKE_DROPBOX:           m_addFilesMenu = menuItem; break;
		case DELETE_SELECTED_ENTRIES:  m_deleteMenu   = menuItem; break;
		case PURGE_SELECTED_ENTRIES:   m_purgeMenu    = menuItem; break;
		}
		menuItem.addStyleName((menuBar == m_entryMenu) ? "vibe-entryMenuBarItem" : "vibe-entryMenuPopupItem");
		menuBar.addItem(menuItem);
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
		menuBar.addItem(structuredMenuItem);
		
		// ...scan the nested items...
		for (ToolbarItem nestedTBI:  structuredTBI.getNestedItemsList()) {
			// ...rendering each of them.
			if (nestedTBI.hasNestedToolbarItems())
			     renderStructuredTBI(structuredMenuBar, nestedTBI);
			else renderSimpleTBI(    structuredMenuBar, nestedTBI);
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
		m_fp.clear();
		m_entryMenu = new VibeMenuBar("vibe-entryMenuBar");
		m_fp.add(m_entryMenu);

		// ...and reload the menu.
		loadPart1Async();
	}

	/**
	 * Called to enable/disable the delete an purge menu
	 * items.
	 * 
	 * @param enable
	 */
	public void setDeleteAndPurgeState(boolean enable) {
		if (null != m_deleteMenu) {m_deleteMenu.setEnabled(enable); if (enable) m_deleteMenu.removeStyleName("vibe-menuDisabled"); else m_deleteMenu.addStyleName("vibe-menuDisabled");}
		if (null != m_purgeMenu)  {m_purgeMenu.setEnabled( enable); if (enable) m_purgeMenu.removeStyleName( "vibe-menuDisabled"); else m_purgeMenu.addStyleName( "vibe-menuDisabled");}
	}
}
