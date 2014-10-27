/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ChangeFavoriteStateEvent;
import org.kablink.teaming.gwt.client.event.ClearScheduledWipeSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.ClearSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.CopyPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.DisableSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.DownloadFolderAsCSVFileEvent;
import org.kablink.teaming.gwt.client.event.EditPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EmailPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersAdHocFoldersEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersDownloadEvent;
import org.kablink.teaming.gwt.client.event.EnableSelectedUsersWebAccessEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeEditInPlaceEvent;
import org.kablink.teaming.gwt.client.event.InvokeRenameEntityEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareBinderEvent;
import org.kablink.teaming.gwt.client.event.InvokeUserPropertiesDlgEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MailToPublicLinkEntityEvent;
import org.kablink.teaming.gwt.client.event.ManageSharesSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsReadEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsUnreadEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ScheduleWipeSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewCurrentBinderTeamMembersEvent;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.event.ViewWhoHasAccessEvent;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadFolderEvent;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadSelectedFilesEvent;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntityActionToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Data table cell that represents an action menu for an entity.
 * 
 * @author drfoster@novell.com
 */
public class ActionMenuCell extends AbstractCell<EntryTitleInfo> {
	private BinderInfo						m_binderInfo;	// The binder hosting this cell.
	private GwtTeamingDataTableImageBundle	m_images;		// Access to the Vibe image  resources we need for this cell. 
	private GwtTeamingMessages				m_messages;		// Access to the Vibe string resources we need for this cell.
	private Map<String, PopupMenu>			m_menuMap;		// Map of entity ID's to PopupMenu.  Added to as the action menus get created for entities in the current data table.

	/**
	 * Constructor method.
	 * 
	 * @param binderInfo
	 */
	public ActionMenuCell(BinderInfo binderInfo) {
		// Sink the events we need to process an action menu...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
		
		// ...store the parameter...
		m_binderInfo = binderInfo;

		// ...and initialize everything else.
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		m_menuMap  = new HashMap<String, PopupMenu>();
	}

	/*
	 * Asynchronously builds and shows the action menu for this cell's
	 * entity.
	 */
	private void buildAndShowActionMenuAsync(final Element actionMenuImg, final EntityId eid, final String entityTitle, final List<ToolbarItem> tbiList) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				buildAndShowActionMenuNow(actionMenuImg, eid, entityTitle, tbiList);
			}
		});
	}
	
	/*
	 * Synchronously builds and shows the action menu for this cell's
	 * entity.
	 */
	private void buildAndShowActionMenuNow(final Element actionMenuImg, final EntityId eid, final String entityTitle, final List<ToolbarItem> tbiList) {
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
				else                                        renderSimpleTBI(eid, entityTitle, actionMenu, actionMenuImg, actionTBI);
			}
			
			// ...and add the action menu to the Map tracking them.
			m_menuMap.put(eidString, actionMenu);
		}

		// ...and then show the action menu.
		showActionMenuNow(actionMenuImg, actionMenu);
	}

	/**
	 * Clears anything in the menu map thereby force all the menus to
	 * be regenerated.
	 */
	public void clearMenuMap() {
		if (null != m_menuMap) {
			m_menuMap.clear();
		}
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
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE,    VibeDataTableConstants.CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE);
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_ID,    eti.getEntityId().getEntityIdString()                     );
		amiE.setAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_TITLE, eti.getTitle()                                            );
		fp.add(actionMenuImg);
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Renders any simple (i.e., URL or event based) toolbar item.
	 */
	private void renderSimpleTBI(final EntityId eid, final String entryTitle, final PopupMenu actionMenu, final Element actionMenuImg, final ToolbarItem simpleTBI) {
		// What do we know about this toolbar item?
		final String        simpleTitle = simpleTBI.getTitle();
		final String		simpleUrl   = simpleTBI.getUrl();
		final TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();

		// Is it a URL for a targeted anchor?
		final boolean	hasSimpleUrl = GwtClientHelper.hasString(simpleUrl);
		String			anchorTarget = (hasSimpleUrl ? simpleTBI.getQualifierValue("anchorTarget") : null);
		VibeMenuItem	menuItem;
		if (GwtClientHelper.hasString(anchorTarget)) {
			// Yes!  Create the anchor..
			Anchor a = new Anchor();
			a.addStyleName("gwt-MenuItem-anchor");
			a.setTarget(anchorTarget);
			a.setHref(simpleUrl);
			Label l = new Label(simpleTitle);
			a.getElement().appendChild(l.getElement());

			// ...and use that to create an HTML only menu item.
			VibeFlowPanel html = new VibeFlowPanel();
			html.add(a);
			menuItem = new VibeMenuItem(
				SafeHtmlUtils.fromTrustedString(
					html.getElement().getInnerHTML()));
		}
		
		else {
			// No, it in't a URL for a targeted anchor!  Generate a
			// command based menu item.
			menuItem = new VibeMenuItem(simpleTitle, false, new Command() {
				@Override
				public void execute() {
					// Does the toolbar item contain a URL to launch?
					if (hasSimpleUrl) {
						// Yes!  Are we supposed to launch it in a
						// popup window?
						String popup = simpleTBI.getQualifierValue("popup");
						if (GwtClientHelper.hasString(popup) && popup.equalsIgnoreCase("true")) {
							// Yes!  Launch it in one.
							int popupHeight;
							try {popupHeight = Integer.parseInt(simpleTBI.getQualifierValue("popupHeight"));}
							catch (Exception e) {popupHeight = Window.getClientHeight();}
							
							int popupWidth;
							try {popupWidth = Integer.parseInt(simpleTBI.getQualifierValue("popupWidth"));}
							catch (Exception e) {popupWidth = Window.getClientWidth();}
							
							GwtClientHelper.jsLaunchUrlInWindow(simpleUrl, "_blank", popupHeight, popupWidth);
						}
						
						else {
							// No, it's not for a popup window.  Launch
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
						// No, the toolbar item didn't contain a URL!
						// The only other option is an event.
						VibeEventBase<?> event;
						Long binderId = m_binderInfo.getBinderIdAsLong();
						switch (simpleEvent) {
						default:                                            event = EventHelper.createSimpleEvent(                   simpleEvent            ); break;
						case CHANGE_ENTRY_TYPE_SELECTED_ENTITIES:           event = new ChangeEntryTypeSelectedEntitiesEvent(        binderId,     eid      ); break;
						case CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES:  event = new ClearScheduledWipeSelectedMobileDevicesEvent(m_binderInfo, eid      ); break;
						case CLEAR_SELECTED_USERS_ADHOC_FOLDERS:            event = new ClearSelectedUsersAdHocFoldersEvent(         binderId,     eid      ); break;
						case CLEAR_SELECTED_USERS_DOWNLOAD:                 event = new ClearSelectedUsersDownloadEvent(             binderId,     eid      ); break;
						case CLEAR_SELECTED_USERS_WEBACCESS:                event = new ClearSelectedUsersWebAccessEvent(            binderId,     eid      ); break;
						case COPY_PUBLIC_LINK_SELECTED_ENTITIES:            event = new CopyPublicLinkSelectedEntitiesEvent(         binderId,     eid      ); break;
						case COPY_SELECTED_ENTITIES:                        event = new CopySelectedEntitiesEvent(                   binderId,     eid      ); break;
						case DELETE_SELECTED_ENTITIES:                      event = new DeleteSelectedEntitiesEvent(                 binderId,     eid      ); break;
						case DELETE_SELECTED_MOBILE_DEVICES:                event = new DeleteSelectedMobileDevicesEvent(            m_binderInfo, eid      ); break;
						case DISABLE_SELECTED_USERS_ADHOC_FOLDERS:          event = new DisableSelectedUsersAdHocFoldersEvent(       binderId,     eid      ); break;
						case DISABLE_SELECTED_USERS_DOWNLOAD:               event = new DisableSelectedUsersDownloadEvent(           binderId,     eid      ); break;
						case DISABLE_SELECTED_USERS_WEBACCESS:              event = new DisableSelectedUsersWebAccessEvent(          binderId,     eid      ); break;
						case EDIT_PUBLIC_LINK_SELECTED_ENTITIES:            event = new EditPublicLinkSelectedEntitiesEvent(         binderId,     eid      ); break;
						case EMAIL_PUBLIC_LINK_SELECTED_ENTITIES:           event = new EmailPublicLinkSelectedEntitiesEvent(        binderId,     eid      ); break;
						case ENABLE_SELECTED_USERS_ADHOC_FOLDERS:           event = new EnableSelectedUsersAdHocFoldersEvent(        binderId,     eid      ); break;
						case ENABLE_SELECTED_USERS_DOWNLOAD:                event = new EnableSelectedUsersDownloadEvent(            binderId,     eid      ); break;
						case ENABLE_SELECTED_USERS_WEBACCESS:               event = new EnableSelectedUsersWebAccessEvent(           binderId,     eid      ); break;
						case LOCK_SELECTED_ENTITIES:                        event = new LockSelectedEntitiesEvent(                   binderId,     eid      ); break;
						case UNLOCK_SELECTED_ENTITIES:                      event = new UnlockSelectedEntitiesEvent(                 binderId,     eid      ); break;
						case MAILTO_PUBLIC_LINK_ENTITY:                     event = new MailToPublicLinkEntityEvent(                 binderId,     eid      ); break;
						case MANAGE_SHARES_SELECTED_ENTITIES:	            event = new ManageSharesSelectedEntitiesEvent(           binderId,     eid      ); break;
						case MARK_READ_SELECTED_ENTITIES:                   event = new MarkReadSelectedEntitiesEvent(               binderId,     eid      ); break;
						case MARK_UNREAD_SELECTED_ENTITIES:                 event = new MarkUnreadSelectedEntitiesEvent(             binderId,     eid      ); break;
						case MOVE_SELECTED_ENTITIES:                        event = new MoveSelectedEntitiesEvent(                   binderId,     eid      ); break;
						case SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES:         event = new ScheduleWipeSelectedMobileDevicesEvent(      m_binderInfo, eid      ); break;
						case SHARE_SELECTED_ENTITIES:                       event = new ShareSelectedEntitiesEvent(                  binderId,     eid      ); break;
						case SUBSCRIBE_SELECTED_ENTITIES:                   event = new SubscribeSelectedEntitiesEvent(              binderId,     eid      ); break;
						case VIEW_SELECTED_ENTRY:                           event = new ViewSelectedEntryEvent(                      binderId,     eid      ); break;
						case VIEW_WHO_HAS_ACCESS:                           event = new ViewWhoHasAccessEvent(                       binderId,     eid      ); break;
						case ZIP_AND_DOWNLOAD_SELECTED_FILES:               event = new ZipAndDownloadSelectedFilesEvent(            binderId,     eid, true); break;
						
						case CHANGE_FAVORITE_STATE:
							event = new ChangeFavoriteStateEvent(
								eid.getEntityId(),
								Boolean.parseBoolean(simpleTBI.getQualifierValue("makeFavorite")));
							break;
						
						case DOWNLOAD_FOLDER_AS_CSV_FILE:
							event = new DownloadFolderAsCSVFileEvent(eid.getEntityId(), binderId);
							break;
						
						case INVOKE_EDIT_IN_PLACE:
							event = new InvokeEditInPlaceEvent(
								binderId,
								eid,
								simpleTBI.getQualifierValue("operatingSystem"),
								simpleTBI.getQualifierValue("openInEditor"   ),
								simpleTBI.getQualifierValue("editorType"     ),
								simpleTBI.getQualifierValue("attachmentId"   ),
								simpleTBI.getQualifierValue("attachmentUrl"  ));
							
							break;

						case INVOKE_SHARE_BINDER:
							event = new InvokeShareBinderEvent(String.valueOf(eid.getEntityId()));
							break;
							
						case INVOKE_RENAME_ENTITY:
							event = new InvokeRenameEntityEvent(eid, entryTitle);
							break;
							
						case INVOKE_USER_PROPERTIES_DLG:
							event = new InvokeUserPropertiesDlgEvent(eid.getEntityId());
							break;
						
						case MARK_FOLDER_CONTENTS_READ:
							event = new MarkFolderContentsReadEvent(eid.getEntityId(), binderId);
							break;
						
						case MARK_FOLDER_CONTENTS_UNREAD:
							event = new MarkFolderContentsUnreadEvent(eid.getEntityId(), binderId);
							break;
							
						case VIEW_CURRENT_BINDER_TEAM_MEMBERS:
							event = new ViewCurrentBinderTeamMembersEvent(eid.getEntityId());
							break;
						
						case ZIP_AND_DOWNLOAD_FOLDER:
							event = new ZipAndDownloadFolderEvent(eid.getEntityId(), binderId, true);
							break;
						
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
		}
		
		// If we get here, menuItem refers to the VibeMenuItem for the
		// toolbar item.  Style it and add it to the action menu.
		menuItem.getElement().setId(simpleTBI.getName());
		menuItem.addStyleName("vibe-dataTableActions-menuPopupItem");
		actionMenu.addMenuItem(menuItem);
	}

	/*
	 * Shows the action menu for the given entity.
	 */
	private void showActionMenu(final Element actionMenuImg) {
		// Have we already built the action menu for this entity?
		final String eidString  = actionMenuImg.getAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_ID   );
		final String entryTitle = actionMenuImg.getAttribute(VibeDataTableConstants.CELL_WIDGET_ENTITY_TITLE);
		PopupMenu	 actionMenu = m_menuMap.get(eidString);
		if (null == actionMenu) {
			// No!  Load the action menu's toolbar items now.
			final EntityId	eid = EntityId.parseEntityIdString(eidString);
			GwtClientHelper.executeCommand(
					new GetEntityActionToolbarItemsCmd(m_binderInfo, eid),
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
					
					// ...and use them to build and show the action
					// ...menu. 
					buildAndShowActionMenuAsync(actionMenuImg, eid, entryTitle, tbiList);
				}
			});
		}
		
		else {
			// Yes, we already built the action menu for this entity!
			// Simply show it. 
			showActionMenuAsync(actionMenuImg, actionMenu);
		}
	}

	/*
	 * Asynchronously shows the action menu for this cell's entity.
	 */
	private void showActionMenuAsync(final Element actionMenuImg, final PopupMenu actionMenu) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showActionMenuNow(actionMenuImg, actionMenu);
			}
		});
	}
	
	/*
	 * Synchronously shows the action menu for this cell's entity.
	 */
	private void showActionMenuNow(final Element actionMenuImg, final PopupMenu actionMenu) {
		actionMenu.showRelativeToTarget(actionMenuImg);
	}
}
