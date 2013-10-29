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
package org.kablink.teaming.gwt.client.datatable;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupActionToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ManageGroupsDlg.GroupInfoPlus;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * Cell that represents an action menu for a group.
 * 
 * @author drfoster@novell.com
 */
public class GroupActionCell extends AbstractCell<GroupInfoPlus> {
	private GwtTeamingDataTableImageBundle	m_images;	// Access to the Vibe image  resources we need for this cell. 
	private GwtTeamingMessages				m_messages;	// Access to the Vibe string resources we need for this cell.

	/**
	 * Constructor method.
	 */
	public GroupActionCell() {
		// Sink the events we need to process this action cell...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
		
		// ...and initialize everything else.
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	}

	/*
	 * Asynchronously builds and shows the action menu for this cell's
	 * group.
	 */
	private void buildAndShowActionMenuAsync(final Element actionMenuImg, final Long groupId, final List<ToolbarItem> tbiList) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				buildAndShowActionMenuNow(actionMenuImg, groupId, tbiList);
			}
		});
	}
	
	/*
	 * Synchronously builds and shows the action menu for this cell's
	 * group.
	 */
	private void buildAndShowActionMenuNow(final Element actionMenuImg, final Long groupId, final List<ToolbarItem> tbiList) {
		// If we don't have any items for the action menu...
		if (tbiList.isEmpty()) {
			// ...tell the user and bail.
			GwtClientHelper.deferredAlert(m_messages.vibeDataTable_Warning_NoEntryActions());
			return;
		}

		// Created the menu for the actions.
		PopupMenu actionMenu = new PopupMenu(true, false, false);
		actionMenu.addStyleName("vibe-dataTableActions-menuDropDown");
		
		// Scan the toolbar items...
		for (ToolbarItem actionTBI:  tbiList) {
			// ...adding each to the action menu...
			if      (actionTBI.hasNestedToolbarItems()) GwtClientHelper.deferredAlert(m_messages.vibeDataTable_InternalError_UnsupportedStructuredToolbar());
			else if (actionTBI.isSeparator())           actionMenu.addSeparator();
			else                                        renderActionTBI(groupId, actionMenu, actionMenuImg, actionTBI);
		}
			
		// ...and then show the action menu.
		showActionMenuNow(actionMenuImg, actionMenu);
	}
	
	/*
	 * Called when the mouse leaves the action menu image.
	 */
	private void handleMouseOut(Element actionMenuImg) {
		actionMenuImg.removeClassName("vibe-dataTableActions-hover");
		actionMenuImg.setAttribute("src", m_images.entryActions1().getSafeUri().asString());
	}
	
	/*
	 * Called when the mouse enters the action menu image.
	 */
	private void handleMouseOver(Element actionMenuImg) {
		actionMenuImg.addClassName("vibe-dataTableActions-hover");
		actionMenuImg.setAttribute("src", m_images.entryActions2().getSafeUri().asString());
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param gip
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
	@Override
    public void onBrowserEvent(Context context, Element parent, GroupInfoPlus gip, NativeEvent event, ValueUpdater<GroupInfoPlus> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, gip, event, valueUpdater);
    	}
    	
    	else {
    		// Something other than a key down!  What type of event are
    		// we processing?
    		Element	eventTarget = Element.as(event.getEventTarget());
	    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
	    		// A click!  Remove the hover and show the action
	    		// menu.
	    		handleMouseOut(eventTarget);
				showActionMenu(eventTarget);
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
	    		// A mouse over!  Add the hover style.
	    		handleMouseOver(eventTarget);
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
	    		// A mouse out!  Remove the hover style.
	    		handleMouseOut(eventTarget);
	    	}
    	}
    }
    
    /**
     * Called when the user presses the ENTER key will the cell is
     * selected.  You are not required to override this method, but
     * it's a common convention that allows your cell to respond to key
     * events.
     * 
     * Overrides AbstractCell.onEnterKeyDown()
     */
	@Override
    protected void onEnterKeyDown(Context context, Element parent, GroupInfoPlus gip, NativeEvent event, ValueUpdater<GroupInfoPlus> valueUpdater) {
    	Element eventTarget = Element.as(event.getEventTarget());
		showActionMenu(eventTarget);
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param gip
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, GroupInfoPlus gip, SafeHtmlBuilder sb) {
		// If we weren't given a GroupInfoPlus...
		if (null == gip) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create a panel to contain the HTML rendering...
		VibeFlowPanel fp = new VibeFlowPanel();
		
		// ...generate the appropriate widgets...
		Image actionMenuImg = GwtClientHelper.buildImage(m_images.entryActions1().getSafeUri().asString());
		actionMenuImg.addStyleName("vibe-dataTableActions-img");
		actionMenuImg.setTitle(m_messages.vibeDataTable_Alt_EntryActions());
		Element amiE = actionMenuImg.getElement();
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, VibeDataTableConstants.CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE);
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_GROUP_ID,  String.valueOf(gip.getGroupInfo().getId())                );
		fp.add(actionMenuImg);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Renders an action (event based) toolbar item.
	 */
	private void renderActionTBI(final Long groupId, final PopupMenu actionMenu, final Element actionMenuImg, final ToolbarItem actionTBI) {
		// Generate a command based menu item.
		VibeMenuItem menuItem = new VibeMenuItem(actionTBI.getTitle(), false, new Command() {
			@Override
			public void execute() {
				TeamingEvents actionEvent = actionTBI.getTeamingEvent();
				switch (actionEvent) {
				case CLEAR_SELECTED_USERS_ADHOC_FOLDERS:    BinderViewsHelper.clearUsersAdHocFolders(  groupId); break;
				case CLEAR_SELECTED_USERS_DOWNLOAD:         BinderViewsHelper.clearUsersDownload(      groupId); break;
				case CLEAR_SELECTED_USERS_WEBACCESS:        BinderViewsHelper.clearUsersWebAccess(     groupId); break;
				case DISABLE_SELECTED_USERS_ADHOC_FOLDERS:  BinderViewsHelper.disableUsersAdHocFolders(groupId); break;
				case DISABLE_SELECTED_USERS_DOWNLOAD:       BinderViewsHelper.disableUsersDownload(    groupId); break;
				case DISABLE_SELECTED_USERS_WEBACCESS:      BinderViewsHelper.disableUsersWebAccess(   groupId); break;
				case ENABLE_SELECTED_USERS_ADHOC_FOLDERS:   BinderViewsHelper.enableUsersAdHocFolders( groupId); break;
				case ENABLE_SELECTED_USERS_DOWNLOAD:        BinderViewsHelper.enableUsersDownload(     groupId); break;
				case ENABLE_SELECTED_USERS_WEBACCESS:       BinderViewsHelper.enableUsersWebAccess(    groupId); break;

				default:
				case UNDEFINED:
					GwtClientHelper.deferredAlert(m_messages.eventHandling_NoActionMenuHandler(actionEvent.name()));
				}
			}
		});
		
		// If we get here, menuItem refers to the VibeMenuItem for the
		// toolbar item.  Style it and add it to the action menu.
		menuItem.addStyleName("vibe-dataTableActions-menuPopupItem");
		actionMenu.addMenuItem(menuItem);
	}

	/*
	 * Shows the action menu for the given group.
	 */
	private void showActionMenu(final Element actionMenuImg) {
		// Load the action menu's toolbar items.
		final String groupIdString = actionMenuImg.getAttribute(VibeDataTableConstants.CELL_WIDGET_GROUP_ID);
		final Long	 groupId       = Long.parseLong(groupIdString);
		GwtClientHelper.executeCommand(
				new GetGroupActionToolbarItemsCmd(groupId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetGroupActionToolbarItems());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the toolbar items...
				GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
				List<ToolbarItem> tbiList = responseData.getToolbarItems();
				
				// ...and use them to build and show the action
				// ...menu. 
				buildAndShowActionMenuAsync(actionMenuImg, groupId, tbiList);
			}
		});
	}

	/*
	 * Asynchronously shows the action menu for this cell's group.
	 */
	@SuppressWarnings("unused")
	private void showActionMenuAsync(final Element actionMenuImg, final PopupMenu actionMenu) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showActionMenuNow(actionMenuImg, actionMenu);
			}
		});
	}
	
	/*
	 * Synchronously shows the action menu for this cell's group.
	 */
	private void showActionMenuNow(final Element actionMenuImg, final PopupMenu actionMenu) {
		actionMenu.showRelativeToTarget(actionMenuImg);
	}
}
