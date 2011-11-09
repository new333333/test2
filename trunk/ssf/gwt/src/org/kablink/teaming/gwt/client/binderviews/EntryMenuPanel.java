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
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;


/**
 * Class used for the content of the entry menus in the binder views.  
 * 
 * @author drfoster@novell.com
 */
public class EntryMenuPanel extends ToolPanelBase {
	private BinderInfo			m_binderInfo;	//
	private List<ToolbarItem>	m_toolbarIems;	//
	private MenuBar				m_menuBar;		//
	private VibeFlowPanel		m_fp;			// The panel holding the AccessoryPanel's contents.
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EntryMenuPanel(BinderInfo binderInfo) {
		// Initialize the super class...
		super(binderInfo);
		
		// ...store the parameters...
		m_binderInfo = binderInfo;

		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-entryMenuPanel");
		m_menuBar = new MenuBar();
		m_fp.add(m_menuBar);
		initWidget(m_fp);
		loadPart1Async();
	}

	/**
	 * Loads the EntryMenuPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param binderInfo
	 * @param tpClient
	 */
	public static void createAsync(final BinderInfo binderInfo, final ToolPanelClient tpClient) {
		GWT.runAsync(EntryMenuPanel.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				EntryMenuPanel emp = new EntryMenuPanel(binderInfo);
				tpClient.onSuccess(emp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EntryMenuPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Returns a named ToolbarItem from a List<ToolbarItem>.  It no
	 * such item exists, returns null.
	 */
	private static ToolbarItem findNamedTBI(List<ToolbarItem> tbiList, String tbiName) {
		for (ToolbarItem tbi:  tbiList) {
			if (tbi.getName().equals(tbiName)) {
				return tbi;
			}
		}
		return null;
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
		// Do we have an entry toolbar?
		ToolbarItem entryTBI = findNamedTBI(m_toolbarIems, "ssEntryToolbar");
		if (null != entryTBI) {
			// Yes!  If it contains an add item...
			List<ToolbarItem> nestedTBIs = entryTBI.getNestedItemsList();
			ToolbarItem addTBI = findNamedTBI(nestedTBIs, "1_add");
			if (null != addTBI) {
				// ...add it to the menu.
				renderAddTBI(addTBI);
			}

			// If it contains a delete selected item...
			ToolbarItem deleteTBI = findNamedTBI(nestedTBIs, "1_deleteSelected");
			if (null != deleteTBI) {
				// ...add it to the menu.
				renderSimpleTBI(deleteTBI);
			}
			
			// If it contains a purge selected item...
			ToolbarItem purgeTBI = findNamedTBI(nestedTBIs, "1_purgeSelected");
			if (null != purgeTBI) {
				// ...add it to the menu.
				renderSimpleTBI(purgeTBI);
			}
			
			// If it contains a drop box...
			ToolbarItem dropBoxTBI = findNamedTBI(nestedTBIs, "dropBox");
			if (null != dropBoxTBI) {
				// ...add it to the menu.
				renderSimpleTBI(dropBoxTBI);
			}
		}
	}

	/*
	 * Renders the 'add an entry' toolbar item.
	 */
	private void renderAddTBI(final ToolbarItem addTBI) {
		// Is there more than one add option?
		List<ToolbarItem>	entryTBIs = addTBI.getNestedItemsList();
		if (entryTBIs.isEmpty()) {
			// No!  Add the single item to the menu.
			m_menuBar.addItem(addTBI.getTitle(), new Command() {
				@Override
				public void execute() {
					// Launch the toolbar item's URL in the content
					// frame.
					OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
						addTBI.getUrl(),
						false,	// false -> Not trash.
						Instigator.GOTO_CONTENT_URL);
					
					if (GwtClientHelper.validateOSBI(osbInfo)) {
						GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
					}
				}
			});
		}
		
		else {
			// Yes, there's more than one add option!  Create a drop
			// down menu for them...
			MenuBar	addMenu = new MenuBar(true);	// true -> Vertical drop down menu.
			m_menuBar.addItem(addTBI.getTitle(), addMenu);
			
			// ...scan the add options...
			for (final ToolbarItem entryTBI:  entryTBIs) {
				// ...and add a single item to the drop down for each
				// ...of them.
				addMenu.addItem(entryTBI.getTitle(), new Command() {
					@Override
					public void execute() {
						// Launch the toolbar item's URL in the content
						// frame.
						OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
							entryTBI.getUrl(),
							false,	// false -> Not trash.
							Instigator.GOTO_CONTENT_URL);
						
						if (GwtClientHelper.validateOSBI(osbInfo)) {
							GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
						}
					}
				});
			}
		}
	}
	
	/*
	 * Renders any of several event base toolbar items.
	 */
	private void renderSimpleTBI(final ToolbarItem simpleTBI) {
		m_menuBar.addItem(simpleTBI.getTitle(), new Command() {
			@Override
			public void execute() {
				TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();
				Long folderId = m_binderInfo.getBinderIdAsLong();
				
				VibeEventBase<?> event;
				switch (simpleEvent) {
				case PURGE_SELECTED_ENTRIES:   event = new PurgeSelectedEntriesEvent( folderId); break;
				case DELETE_SELECTED_ENTRIES:  event = new DeleteSelectedEntriesEvent(folderId); break;
				case INVOKE_DROPBOX:           event = new InvokeDropBoxEvent(        folderId); break;
					
				default:
				case UNDEFINED:
					Window.alert(GwtTeaming.getMessages().eventHandling_NoEntryMenuHandler(simpleEvent.name()));
					return;
				}
				
				GwtTeaming.fireEvent(event);
			}
		});
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
		m_menuBar = new MenuBar();
		m_fp.add(m_menuBar);

		// ...and reload the menu.
		loadPart1Async();
	}
}
