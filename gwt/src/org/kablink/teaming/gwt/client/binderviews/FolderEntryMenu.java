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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.CopyPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EditPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EmailPublicLinkSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ForceFilesUnlockEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditInPlaceEvent;
import org.kablink.teaming.gwt.client.event.InvokeSendEmailToTeamEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MailToPublicLinkEntityEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ShowViewPermalinksEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadSelectedFilesEvent;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Class that holds the folder entry viewer menu.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class FolderEntryMenu extends VibeFlowPanel {
	private boolean							m_isIE;			// true -> We're running in IE.  false -> We're running in another browser.
	private FolderEntryCallback				m_fec;			// Callback to the folder entry composite.
	private GwtTeamingDataTableImageBundle	m_images;		// Access to Vibe's images.
	private GwtTeamingFilrImageBundle		m_filrImages;	// Access to Filr's images.
	private GwtTeamingMessages				m_messages;		// Access to Vibe's messages.
	private List<ToolbarItem>				m_toolbarItems;	// Information about the entry being viewed's menus.
	private VibeMenuBar						m_entryMenu;	// The menu bar that contains all the menu items.

	/**
	 * Constructor method.
	 * 
	 * @param fec
	 * @param toolbarItems
	 */
	public FolderEntryMenu(FolderEntryCallback fec, List<ToolbarItem> toolbarItems) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec          = fec;
		m_toolbarItems = toolbarItems;
		
		// ...initialize the data members requiring it...
		m_isIE       = GwtClientHelper.jsIsIE();
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_images     = GwtTeaming.getDataTableImageBundle();
		m_messages   = GwtTeaming.getMessages();
		
		// ...and construct the menu's content.
		createContent();
	}
	
	/*
	 * Creates the header's content.
	 */
	private void createContent() {
		// Add the panel's style.
		addStyleName("vibe-feView-menuPanel");

		// Create a menu bar widget and add it to the panel.
		m_entryMenu = new VibeMenuBar("vibe-feView-menuBar");
		m_entryMenu.getElement().setId(MenuIds.FEVIEW_MENU);
		add(m_entryMenu);
		
		// Scan the toolbar items..
		for (ToolbarItem entryTBI:  m_toolbarItems) {
			// ...rendering each of them.
			List<ToolbarItem> nestedTBI = entryTBI.getNestedItemsList();
			if (GwtClientHelper.hasItems(nestedTBI)) {
				if (nestedTBI.size() == 1)
				     renderSimpleTBI(    m_entryMenu, null, nestedTBI.get(0), false);
				else renderStructuredTBI(m_entryMenu, null, entryTBI               );
			}
			
			else if (entryTBI.isSeparator()) {
				m_entryMenu.addSeparator();
			}
			
			else {
				renderSimpleTBI(m_entryMenu, null, entryTBI, false);
			}
		}

		// Finally, tell the composite that we're ready.
		m_fec.viewComponentReady();
	}
	
	/*
	 * Renders any simple (i.e., URL or event based) toolbar item.
	 */
	private void renderSimpleTBI(VibeMenuBar menuBar, PopupMenu popupMenu, final ToolbarItem simpleTBI, boolean contentsSelectable) {
		final String        simpleTitle = simpleTBI.getTitle();
		final TeamingEvents simpleEvent = simpleTBI.getTeamingEvent();

		// Generate the text to display for the menu item...
		boolean menuTextIsHTML;
		String  menuText;
		if (contentsSelectable) {
			String contentsCheckedS = simpleTBI.getQualifierValue("selected");
			boolean contentsChecked = (GwtClientHelper.hasString(contentsCheckedS) && contentsCheckedS.equals("true"));
			VibeFlowPanel html = new VibeFlowPanel();
			Image checkImg;
			if (contentsChecked) {
				checkImg = new Image(m_images.check12());
				checkImg.addStyleName("vibe-feView-menuBarCheck");
				checkImg.getElement().setAttribute("align", "absmiddle");
			}
			else {
				checkImg = new Image(m_images.spacer1px());
				checkImg.addStyleName("vibe-feView-menuBarCheck");
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

		// ...and generate the menu item.
		VibeMenuItem menuItem = new VibeMenuItem(menuText, menuTextIsHTML, new Command() {
			@Override
			public void execute() {
				// Does the simple toolbar item contain a URL to
				// launch?
				String simpleUrl = simpleTBI.getUrl();
				if (TeamingEvents.UNDEFINED.equals(simpleEvent) && GwtClientHelper.hasString(simpleUrl)) {
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
					// No, the menu item is not controlled by a URL but
					// by an event!  Most of them contain a
					// binder ID/entry ID for the entry.  If they're
					// there, construct an EntityId for them.
					EntityId eid = simpleTBI.getEntityIdValue("entityId");
					if (null == eid) {
						String binderIdS = simpleTBI.getQualifierValue("binderId");
						String entryIdS  = simpleTBI.getQualifierValue("entryId" );
						if (GwtClientHelper.hasString(binderIdS) && GwtClientHelper.hasString(entryIdS))
						     eid = new EntityId(Long.parseLong(binderIdS), Long.parseLong(entryIdS), EntityId.FOLDER_ENTRY);
						else eid = null;
						GwtClientHelper.debugAlert("FolderEntryMenu.renderSimpleTBI( Manually created EntityID for event '" + simpleEvent.name() + "' ):  Should this event pass an EntityId directly?");
					}

					// Generate the specific event for this menu item...
					VibeEventBase<?> event;
					boolean fireWithSource = true;
					switch (simpleEvent) {
					case CHANGE_ENTRY_TYPE_SELECTED_ENTITIES:  event = new ChangeEntryTypeSelectedEntitiesEvent(eid.getBinderId(), eid      );      break;
					case COPY_PUBLIC_LINK_SELECTED_ENTITIES:   event = new CopyPublicLinkSelectedEntitiesEvent( eid.getBinderId(), eid      );      break;
					case COPY_SELECTED_ENTITIES:               event = new CopySelectedEntitiesEvent(           eid.getBinderId(), eid      );      break;
					case DELETE_SELECTED_ENTITIES:             event = new DeleteSelectedEntitiesEvent(         eid.getBinderId(), eid      );      break;
					case EDIT_PUBLIC_LINK_SELECTED_ENTITIES:   event = new EditPublicLinkSelectedEntitiesEvent( eid.getBinderId(), eid      );      break;
					case EMAIL_PUBLIC_LINK_SELECTED_ENTITIES:  event = new EmailPublicLinkSelectedEntitiesEvent(eid.getBinderId(), eid      );      break;
					case FORCE_FILES_UNLOCK:                   event = new ForceFilesUnlockEvent(               eid.getBinderId(), eid      );      break;
					case INVOKE_SEND_EMAIL_TO_TEAM:            event = new InvokeSendEmailToTeamEvent(          simpleUrl); fireWithSource = false; break;
					case LOCK_SELECTED_ENTITIES:               event = new LockSelectedEntitiesEvent(           eid.getBinderId(), eid      );      break;
					case MAILTO_PUBLIC_LINK_ENTITY:            event = new MailToPublicLinkEntityEvent(         eid.getBinderId(), eid      );      break;
					case MARK_READ_SELECTED_ENTITIES:          event = new MarkReadSelectedEntitiesEvent(       eid.getBinderId(), eid      );      break;
					case MARK_UNREAD_SELECTED_ENTITIES:        event = new MarkUnreadSelectedEntitiesEvent(     eid.getBinderId(), eid      );      break;
					case MOVE_SELECTED_ENTITIES:               event = new MoveSelectedEntitiesEvent(           eid.getBinderId(), eid      );      break;
					case SHARE_SELECTED_ENTITIES:              event = new ShareSelectedEntitiesEvent(          eid.getBinderId(), eid      );      break;
					case SHOW_VIEW_PERMALINKS:                 event = new ShowViewPermalinksEvent();                                               break;
					case SUBSCRIBE_SELECTED_ENTITIES:          event = new SubscribeSelectedEntitiesEvent(      eid.getBinderId(), eid      );      break;
					case UNLOCK_SELECTED_ENTITIES:             event = new UnlockSelectedEntitiesEvent(         eid.getBinderId(), eid      );      break;
					case ZIP_AND_DOWNLOAD_SELECTED_FILES:      event = new ZipAndDownloadSelectedFilesEvent(    eid.getBinderId(), eid, true);      break;
					
					case INVOKE_EDIT_IN_PLACE:
						event = new InvokeEditInPlaceEvent(
							eid,
							simpleTBI.getQualifierValue("operatingSystem"),
							simpleTBI.getQualifierValue("openInEditor"   ),
							simpleTBI.getQualifierValue("editorType"     ),
							simpleTBI.getQualifierValue("attachmentId"   ),
							simpleTBI.getQualifierValue("attachmentUrl"  ));
						
						break;

					default:
					case UNDEFINED:
						GwtClientHelper.deferredAlert(m_messages.eventHandling_NoEntryMenuHandler(simpleEvent.name()));
						event = null;
						break;
					}

					// ...and if we have one...
					if (null != event) {
						// ...fire it.
						GwtTeaming.fireEventAsync(
							event,
								(fireWithSource ?
									m_fec       :	// Source for event.
									null));			// null -> No source for event.
					}
				}
			}
		});

		// Finally, tie it all together.
		menuItem.getElement().setId(simpleTBI.getName());
		menuItem.addStyleName((menuBar == m_entryMenu) ? "vibe-feView-menuBarItem" : "vibe-feView-menuPopupItem");
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
		structuredMenuBar.getElement().setId(structuredTBI.getName());
		structuredMenuBar.addStyleName("vibe-feView-menuPopup");
		VibeMenuItem structuredMenuItem = new VibeMenuItem(structuredTBI.getTitle(), structuredMenuBar);
		structuredMenuItem.getElement().setId(structuredTBI.getName() + "_Item");
		structuredMenuItem.addStyleName("vibe-feView-menuBarItem");
		structuredMenuItem.setHTML(renderStructuredItemHTML(structuredTBI.getTitle(), true));
		if (null != menuBar)
		     menuBar.addItem(      structuredMenuItem);
		else popupMenu.addMenuItem(structuredMenuItem);
		
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
}
