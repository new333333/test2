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
package org.kablink.teaming.gwt.client.datatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.LockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntityActionToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Data table cell that represents an action menu for an entity.
 * 
 * @author drfoster@novell.com
 */
public class ActionMenuCell extends AbstractCell<EntryTitleInfo> {
	private GwtTeamingDataTableImageBundle	m_images;	// Access to the Vibe image  resources we need for this cell. 
	private GwtTeamingMessages				m_messages;	// Access to the Vibe string resources we need for this cell.
	private Long							m_binderId;	// The ID of the binder hosting this cell.
	private Map<String, PopupMenu>			m_menuMap;	// Map of entity ID's to PopupMenu.  Added to as the action menus get created for entities in the current data table.

	/*
	 * Inner class used so we can use PopupPanel.showRelativeTo() on an
	 * actionMenuImg Element.
	 */
	private class ElementWrapper extends UIObject {
		public ElementWrapper(Element e) {
			setElement(e);	// setElement() is protected, so we have to subclass and call here
		}
	}
	
	/**
	 * Constructor method.
	 */
	public ActionMenuCell(Long binderId) {
		// Sink the events we need to process an action menu...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
		
		// ...store the parameter...
		m_binderId = binderId;

		// ...and initialize everything else.
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		m_menuMap  = new HashMap<String, PopupMenu>();
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
     * @param eti
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
	@Override
    public void onBrowserEvent(Context context, Element parent, EntryTitleInfo eti, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, eti, event, valueUpdater);
    	}
    	
    	else {
    		// Something other than a key down!  Is it targeted to this
    		// action menu image?
    		Element	eventTarget  = Element.as(event.getEventTarget()                                    );
    		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
    		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE)){
    			// Yes!  What type of event are we processing?
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
    protected void onEnterKeyDown(Context context, Element parent, EntryTitleInfo eti, NativeEvent event, ValueUpdater<EntryTitleInfo> valueUpdater) {
    	Element eventTarget = Element.as(event.getEventTarget());
		String	wt           = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
		if ((null != wt) && wt.equals(VibeDataTableConstants.CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE)){
			showActionMenu(eventTarget);
		}
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param eti
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, EntryTitleInfo eti, SafeHtmlBuilder sb) {
		// If we weren't given a EntryTitleInfo...
		if (null == eti) {
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
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_ID, eti.getEntityId().getEntityIdString()                     );
		fp.add(actionMenuImg);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Renders any simple (i.e., URL or event based) toolbar item.
	 */
	private void renderSimpleTBI(final EntityId eid, PopupMenu popupMenu, final ToolbarItem simpleTBI) {
		final String        simpleTitle = simpleTBI.getTitle();
		final TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();
		
		// Generate the menu item.
		VibeMenuItem menuItem = new VibeMenuItem(simpleTitle, false, new Command() {
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
					VibeEventBase<?> event;
					switch (simpleEvent) {
					default:                                  event = EventHelper.createSimpleEvent(          simpleEvent    ); break;
					case CHANGE_ENTRY_TYPE_SELECTED_ENTRIES:  event = new ChangeEntryTypeSelectedEntriesEvent(m_binderId, eid); break;
					case COPY_SELECTED_ENTRIES:               event = new CopySelectedEntriesEvent(           m_binderId, eid); break;
					case DELETE_SELECTED_ENTRIES:             event = new DeleteSelectedEntriesEvent(         m_binderId, eid); break;
					case LOCK_SELECTED_ENTRIES:               event = new LockSelectedEntriesEvent(           m_binderId, eid); break;
					case UNLOCK_SELECTED_ENTRIES:             event = new UnlockSelectedEntriesEvent(         m_binderId, eid); break;
					case MARK_READ_SELECTED_ENTRIES:          event = new MarkReadSelectedEntriesEvent(       m_binderId, eid); break;
					case MARK_UNREAD_SELECTED_ENTRIES:        event = new MarkUnreadSelectedEntriesEvent(     m_binderId, eid); break;
					case MOVE_SELECTED_ENTRIES:               event = new MoveSelectedEntriesEvent(           m_binderId, eid); break;
					case PURGE_SELECTED_ENTRIES:              event = new PurgeSelectedEntriesEvent(          m_binderId, eid); break;
					case SHARE_SELECTED_ENTRIES:              event = new ShareSelectedEntriesEvent(          m_binderId, eid); break;
					case SUBSCRIBE_SELECTED_ENTRIES:          event = new SubscribeSelectedEntriesEvent(      m_binderId, eid); break;
					case VIEW_SELECTED_ENTRY:                 event = new ViewSelectedEntryEvent(             m_binderId, eid); break;
					
					case UNDEFINED:
						GwtClientHelper.deferredAlert(m_messages.eventHandling_NoActionMenuHandler(simpleEvent.name()));
						event = null;
					}
					
					if (null != event) {
						GwtTeaming.fireEvent(event);
					}
				}
			}
		});
		menuItem.addStyleName("vibe-dataTableActions-menuPopupItem");
		popupMenu.addMenuItem(menuItem);
	}

	/*
	 * Shows the action menu for the given entity.
	 */
	private void showActionMenu(final Element actionMenuImg) {
		// Have we already constructed the action menu for this entity?
		String		eidString  = actionMenuImg.getAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_ID);
		PopupMenu	actionMenu = m_menuMap.get(eidString);
		if (null == actionMenu) {
			// No!  Load the action menu's toolbar items now.
			final EntityId	eid = EntityId.parseEntityIdString(eidString);
			GwtClientHelper.executeCommand(
					new GetEntityActionToolbarItemsCmd(eid),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetEntityActionToolbarItems());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the toolbar items...
					GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
					List<ToolbarItem> tbiList = responseData.getToolbarItems();
					
					// ...and use them to show the action menu. 
					showActionMenuAsync(actionMenuImg, eid, tbiList);
				}
			});
		}
		
		else {
			// Yes, we already constructed the action menu for this
			// entity!  Show it. 
			showActionMenuAsync(actionMenuImg, actionMenu);
		}
	}

	/*
	 * Asynchronously shows the action menu for this cell's entity.
	 */
	private void showActionMenuAsync(final Element actionMenuImg, final EntityId eid, final List<ToolbarItem> tbiList) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				showActionMenuNow(actionMenuImg, eid, tbiList);
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}
	
	private void showActionMenuAsync(final Element actionMenuImg, final PopupMenu actionMenu) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				showActionMenuNow(actionMenuImg, actionMenu);
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}
	
	/*
	 * Synchronously shows the action menu for this cell's entity.
	 */
	private void showActionMenuNow(final Element actionMenuImg, final EntityId eid, final List<ToolbarItem> tbiList) {
		// If we don't have any items for the action menu...
		if (tbiList.isEmpty()) {
			// ...tell the user and bail.
			GwtClientHelper.deferredAlert(m_messages.vibeDataTable_Warning_NoEntryActions());
			return;
		}

		// Have we created the popup menu for the actions yet?
		String eidString = eid.getEntityIdString();
		PopupMenu actionMenu = m_menuMap.get(eidString);
		if (null == actionMenu) {
			// No!  Create one now.
			actionMenu = new PopupMenu(true, false, false);
			actionMenu.addStyleName("vibe-dataTableActions-menuDropDown");
			
			// Scan the toolbar items...
			for (ToolbarItem actionTBI:  tbiList) {
				// ...adding each to the action menu...
				if      (actionTBI.hasNestedToolbarItems()) GwtClientHelper.deferredAlert(m_messages.vibeDataTable_InternalError_UnsupportedStructuredToolbar());
				else if (actionTBI.isSeparator())           actionMenu.addSeparator();
				else                                        renderSimpleTBI(eid, actionMenu, actionTBI);
			}
			
			// ...and add the action menu to the Map tracking them.
			m_menuMap.put(eidString, actionMenu);
		}

		// ...and then show the action menu image.
		showActionMenuNow(actionMenuImg, actionMenu);
	}
	
	/*
	 * Synchronously shows the action menu for this cell's entity.
	 */
	private void showActionMenuNow(final Element actionMenuImg, final PopupMenu actionMenu) {
		actionMenu.showRelativeToTarget(new ElementWrapper(actionMenuImg));
	}
}
