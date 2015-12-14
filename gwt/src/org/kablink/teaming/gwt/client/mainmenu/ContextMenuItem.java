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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DownloadFolderAsCSVFileEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.GotoPermalinkUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeSendEmailToTeamEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareBinderEvent;
import org.kablink.teaming.gwt.client.event.InvokeWorkspaceShareRightsEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsReadEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsUnreadEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ZipAndDownloadFolderEvent;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem.NameEntityIdPair;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem.NameValuePair;
import org.kablink.teaming.gwt.client.util.ClientEventParameter;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Class used to wrap a context based menu item.
 * 
 * @author drfoster@novell.com
 */
public class ContextMenuItem extends VibeMenuItem {
	/*
	 * Enumeration used to specify the type of content a context menu
	 * item's command contains.
	 */
	private enum CommandType {
		TEAMING_EVENT,
		JAVASCRIPT_STRING,
		URL_IN_POPUP_NO_FORM,
		URL_IN_POPUP_WITH_FORM,
		URL_IN_CONTENT_FRAME,
		
		UNDEFINED,
	}
	
	/*
	 * Inner class that handles selecting the menu items.
	 */
	@SuppressWarnings("unused")
	private static class ContextItemCommand implements Command {
		private boolean					m_hideEntryView;				//
		private ClientEventParameter	m_clientEventParameter;			//
		private CommandType 			m_type = CommandType.UNDEFINED;	//
		private FormPanel 				m_fp;							//
		private int						m_popupHeight;					//
		private int						m_popupWidth;					//
		private List<NameEntityIdPair>	m_eventEntityIds;				//
		private List<NameValuePair>		m_eventQualifiers;				//
		private String					m_onClickJS;					//
		private String					m_url;							//
		private TeamingEvents			m_teamingEvent;					//
		
		/**
		 * Constructor method.
		 *
		 * @param hideEntryView
		 * @param url
		 */
		ContextItemCommand(boolean hideEntryView, String url) {
			// Store the type of command...
			super();
			m_type = CommandType.URL_IN_CONTENT_FRAME;
			
			// ...and the parameters.
			m_hideEntryView = hideEntryView;
			m_url           = url;
		}
		
		/**
		 * Constructor method.
		 *
		 * @param hideEntryView
		 * @param url
		 * @param popupHeight
		 * @param popupWidth
		 */
		ContextItemCommand(boolean hideEntryView, String url, int popupHeight, int popupWidth) {
			// Store the type of command...
			super();
			m_type = CommandType.URL_IN_POPUP_NO_FORM;
			
			// ...and the parameters.
			m_hideEntryView = hideEntryView;
			m_url           = url;
			m_popupHeight   = popupHeight;
			m_popupWidth    = popupWidth;
		}
		
		/**
		 * Constructor method.
		 *
		 * @param hideEntryView
		 * @param fp
		 * @param popupHeight
		 * @param popupWidth
		 */
		ContextItemCommand(boolean hideEntryView, FormPanel fp, int popupHeight, int popupWidth) {
			// Store the type of command...
			super();
			m_type = CommandType.URL_IN_POPUP_WITH_FORM;
			
			// ...and the parameters.
			m_hideEntryView = hideEntryView;
			m_fp            = fp;
			m_popupHeight   = popupHeight;
			m_popupWidth    = popupWidth;
		}
		
		/**
		 * Constructor method.
		 *
		 * @param hideEntryView
		 * @param url
		 * @param onClickJS
		 */
		ContextItemCommand(boolean hideEntryView, String url, String onClickJS) {
			// Store the type of command...
			super();
			m_type = CommandType.JAVASCRIPT_STRING;
			
			// ...and the parameters.
			m_hideEntryView = hideEntryView;
			m_url           = url;
			m_onClickJS     = onClickJS;
		}
		
		/**
		 * Constructor method.
		 *
		 * @param hideEntryView
		 * @param url
		 * @param teamingEvent
		 * @param eventEntityIds
		 * @param eventQualifiers
		 * @param clientEventParameter
		 */
		ContextItemCommand(boolean hideEntryView, String url, TeamingEvents teamingEvent, List<NameEntityIdPair> eventEntityIds, List<NameValuePair> eventQualifiers, ClientEventParameter clientEventParameter) {
			// Store the type of command...
			super();
			m_type = CommandType.TEAMING_EVENT;
			
			// ...and the parameters.
			m_hideEntryView        = hideEntryView;
			m_url                  = url;
			m_teamingEvent         = teamingEvent;
			m_eventEntityIds       = eventEntityIds;
			m_eventQualifiers      = eventQualifiers;
			m_clientEventParameter = clientEventParameter;
		}
		
		/**
		 * Constructor method.
		 *
		 * @param hideEntryView
		 * @param url
		 * @param teamingEvent
		 * @param eventEntityIds
		 * @param eventQualifiers
		 */
		ContextItemCommand(boolean hideEntryView, String url, TeamingEvents teamingEvent, List<NameEntityIdPair> eventEntityIds, List<NameValuePair> eventQualifiers) {
			// Always use one of the initial forms of the constructor.
			this(hideEntryView, url, teamingEvent, eventEntityIds, eventQualifiers, null);
		}
		
		/**
		 * Called when the user selects on a menu item.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			// If requested to do so...
			if (m_hideEntryView) {
				// ...hide any entry view...
				GwtClientHelper.jsHideNewPageEntryViewDIV();
			}

			// ...and perform the request based on the type of command
			// ...constructed.
			switch (m_type) {
			case JAVASCRIPT_STRING:
				GwtClientHelper.jsEvalString(m_url, m_onClickJS);
				break;
				
			case TEAMING_EVENT:
				EntityId eventEID;
				switch (m_teamingEvent) {
				case COPY_SELECTED_ENTITIES:
				case DELETE_SELECTED_ENTITIES:
				case MOVE_SELECTED_ENTITIES:
					// Create the appropriate selected entries event...
					EntityId         eid      = ToolbarItem.getEntityIdValueFromList("entityId", m_eventEntityIds);
					VibeEventBase<?> selEvent = null;
					switch (m_teamingEvent) {
					case COPY_SELECTED_ENTITIES:    selEvent = new CopySelectedEntitiesEvent(  eid.getEntityId(), eid); break;
					case DELETE_SELECTED_ENTITIES:  selEvent = new DeleteSelectedEntitiesEvent(eid.getEntityId(), eid); break;
					case MOVE_SELECTED_ENTITIES:    selEvent = new MoveSelectedEntitiesEvent(  eid.getEntityId(), eid); break;
					}
					
					// ...and fire it.
					GwtTeaming.fireEvent(selEvent);
					break;
					
				case GOTO_PERMALINK_URL:
					GwtTeaming.fireEvent(new GotoPermalinkUrlEvent(m_url));
					break;

				case INVOKE_SEND_EMAIL_TO_TEAM:
					GwtTeaming.fireEvent(new InvokeSendEmailToTeamEvent(m_url));
					break;

				case INVOKE_SHARE_BINDER:
					GwtTeaming.fireEvent(
						new InvokeShareBinderEvent(
							ToolbarItem.getQualifierValueFromList(
								"binderId",
								m_eventQualifiers)));
					break;
					
				case INVOKE_WORKSPACE_SHARE_RIGHTS:
					GwtTeaming.fireEvent(
						new InvokeWorkspaceShareRightsEvent(
							Long.parseLong(ToolbarItem.getQualifierValueFromList(
								"binderId",
								m_eventQualifiers))));
					break;
					
				case EDIT_CURRENT_BINDER_BRANDING:
				case INVOKE_ABOUT:
				case INVOKE_CLIPBOARD:
				case INVOKE_CONFIGURE_COLUMNS:
				case INVOKE_EMAIL_NOTIFICATION:
				case INVOKE_RENAME_ENTITY:
				case TRACK_CURRENT_BINDER:
				case UNTRACK_CURRENT_BINDER:
				case UNTRACK_CURRENT_PERSON:
				case VIEW_CURRENT_BINDER_TEAM_MEMBERS:
				case VIEW_WHATS_NEW_IN_BINDER:
				case VIEW_WHATS_UNSEEN_IN_BINDER:
					EventHelper.fireSimpleEvent(m_teamingEvent);
					break;
					
				case DOWNLOAD_FOLDER_AS_CSV_FILE:
					// Fire the appropriate event.
					eventEID = ToolbarItem.getEntityIdValueFromList("entityId",  m_eventEntityIds);
					GwtTeaming.fireEvent(new DownloadFolderAsCSVFileEvent(eventEID.getEntityId()));
					break;
					
				case MARK_FOLDER_CONTENTS_READ:
					// Fire the appropriate event.
					eventEID = ToolbarItem.getEntityIdValueFromList("entityId",  m_eventEntityIds);
					GwtTeaming.fireEvent(new MarkFolderContentsReadEvent(eventEID.getEntityId()));
					break;
					
				case MARK_FOLDER_CONTENTS_UNREAD:
					// Fire the appropriate event.
					eventEID = ToolbarItem.getEntityIdValueFromList("entityId",  m_eventEntityIds);
					GwtTeaming.fireEvent(new MarkFolderContentsUnreadEvent(eventEID.getEntityId()));
					break;
					
				case ZIP_AND_DOWNLOAD_FOLDER:
					// Fire the appropriate event.
					eventEID = ToolbarItem.getEntityIdValueFromList("entityId",  m_eventEntityIds);
					GwtTeaming.fireEvent(new ZipAndDownloadFolderEvent(
						eventEID.getEntityId(),
						Boolean.parseBoolean(ToolbarItem.getQualifierValueFromList("recursive", m_eventQualifiers))));
					break;
					
				default:
					Window.alert(
						GwtTeaming.getMessages().eventHandling_NoContextMenuEventHandler(
							m_teamingEvent.name()));
					break;
				}				
				break;

			case URL_IN_CONTENT_FRAME:
				GwtTeaming.fireEvent(new GotoContentUrlEvent(m_url));
				break;
				
			case URL_IN_POPUP_NO_FORM:
				GwtClientHelper.jsLaunchUrlInWindow(m_url, "_blank", m_popupHeight, m_popupWidth);
				break;
				
			case URL_IN_POPUP_WITH_FORM:
				GwtClientHelper.jsLaunchUrlInWindow("", m_fp.getTarget(), m_popupHeight, m_popupWidth);
				m_fp.submit();
				break;
			}
		}
	}

	/**
	 * Constructor method.
	 * 
	 * @param contextMenu
	 * @param idBase
	 * @param tbi
	 * @param hideEntryView
	 */
	public ContextMenuItem(MenuBarPopupBase contextMenu, String idBase, ToolbarItem tbi, boolean hideEntryView) {
		super(
			getTBILabelAsHTML(tbi),
			true,	// true -> Text is HTML.
			createCommand(
				hideEntryView,
				getTBIId(idBase, tbi),
				tbi));
		getElement().setId(tbi.getName());
	}
	
	public ContextMenuItem(MenuBarPopupBase contextMenu, String idBase, ToolbarItem tbi) {
		// Always use the initial form of the constructor, defaulting
		// to not hiding an entry view.
		this(contextMenu, idBase, tbi, false);
	}

	/*
	 * Creates a command based on a toolbar item.
	 */
	private static ContextItemCommand createCommand(boolean hideEntryView, String id, ToolbarItem tbi) {
		ContextItemCommand reply;
		String url = tbi.getUrl();
		TeamingEvents te = tbi.getTeamingEvent();
		ClientEventParameter cep = tbi.getClientEventParameter();
		if (TeamingEvents.UNDEFINED.equals(te)) {
			// It's not based on an event!  Is it based on an onClick
			// JavaScript string?
			String jsString = tbi.getQualifierValue("onclick");
			if (GwtClientHelper.hasString(jsString)) {
				// Yes!  Generate the appropriate command for it.
				reply = new ContextItemCommand(hideEntryView, url, jsString);
			}
			
			// No, it isn't based on a JavaScript string!  Is is to
			// open a URL in a popup window?
			else if (GwtClientHelper.bFromS(tbi.getQualifierValue("popup"))) {
				// Yes!  Generate the appropriate command for it.
				reply = createPopupCommand(hideEntryView, id, url, tbi);
			}
			
			else {
				// No, it isn't to open a URL in a popup window either!
				// The only option left is to launch the URL in the content
				// pane.  Generate the appropriate command for it.
				reply = new ContextItemCommand(hideEntryView, url);
			}
		}
		
		else {
			// It's based on an event!  Generate the appropriate
			// command for it.
			reply = new ContextItemCommand(
				hideEntryView,
				url,
				te,
				tbi.getEntityIdsList(),
				tbi.getQualifiersList(),
				cep);
		}

		// If we get here, reply refers to the appropriate command
		// for the toolbar item.  Return it.
		return reply;
	}

	/*
	 * Creates a command that requires opening a popup window
	 * based on a toolbar item.
	 */
	private static ContextItemCommand createPopupCommand(boolean hideEntryView, String id, String url, ToolbarItem tbi) {
		// What's the dimensions for the popup window?
		int popupHeight = GwtClientHelper.iFromS(tbi.getQualifierValue("popupHeight"), Window.getClientHeight());
		int popupWidth  = GwtClientHelper.iFromS(tbi.getQualifierValue("popupWidth"),  Window.getClientWidth());

		// Are we supposed to open the popup window using a submitted form?
		int hiCount = 0;
		boolean popupFromForm = GwtClientHelper.bFromS(tbi.getQualifierValue("popup.fromForm"));
		ContextItemCommand reply;
		if (popupFromForm) {
			// Possibly!  We'll only do so if we're going to have more
			// than one hidden input of data to pass through it.
			hiCount = GwtClientHelper.iFromS(tbi.getQualifierValue("popup.hiddenInput.count"));
			popupFromForm = (0 < hiCount);
		}
		if (popupFromForm) {
			// Yes!  Create the form...
			FormPanel fp = new FormPanel(id + "Wnd");
			fp.setAction(url);
			fp.setMethod(FormPanel.METHOD_POST);
			Element fpE = fp.getElement();
			fpE.setClassName("inline");
			GwtClientHelper.jsAppendDocumentElement(fpE);

			// ...since a FormPanel is a SimplePanel, it can only hold
			// ...a single widget.  If we have more than 1 hidden
			// ...input, we'll use a nested FlowPanel to contain them.
			Panel hiPanel;
			if (1 < hiCount) {
				FlowPanel fpPanel = new FlowPanel();
				fp.add(fpPanel);
				hiPanel = fpPanel;
			}
			else {
				hiPanel = fp;
			}
			
			// ...add the required hidden inputs to it...
			for (int i = 0; i < hiCount; i += 1) {
				String hiBase  = ("popup.hiddenInput." + i + ".");
				Hidden hInput = new Hidden();
				hInput.setName( tbi.getQualifierValue(hiBase + "name"));
				hInput.setValue(tbi.getQualifierValue(hiBase + "value"));
				hiPanel.add(hInput);
			}
			
			// ...and create the command.
			reply = new ContextItemCommand(hideEntryView, fp, popupHeight, popupWidth);
		}
		
		else {
			// No, we don't need to use submitted form!  Generate the
			// appropriate command.
			reply = new ContextItemCommand(hideEntryView, url, popupHeight, popupWidth);
		}
		
		// If we get here, reply refers to the appropriate command for
		// the toolbar item.  Return it.
		return reply;
	}

	/*
	 * Returns the ID to use for the ContextMenuItem based on a
	 * ToolbarItem.
	 */
	private static String getTBIId(String idBase, ToolbarItem tbi) {
		return (idBase + tbi.getName());
	}

	/*
	 * Returns the label for the ContextMenuItem based on a
	 * ToolbarItem.
	 */
	private static String getTBILabelAsHTML(ToolbarItem tbi) {
		// Determine the string for the label...
		String label = tbi.getTitle();
		if (!(GwtClientHelper.hasString(label))) {
			label = tbi.getName();
		}
		
		// ...create a FlowPanel to hold it...
		FlowPanel mpaLabelPanel = new FlowPanel();
		mpaLabelPanel.addStyleName("vibe-mainMenuPopup_Item");
		Label mpaLabel = new Label(label);
		mpaLabel.addStyleName("vibe-mainMenuPopup_ItemText");
		mpaLabelPanel.add(mpaLabel);

		// ...and return its HTML content.
		FlowPanel htmlPanel = new FlowPanel();
		htmlPanel.add(mpaLabelPanel);
		return htmlPanel.getElement().getInnerHTML();
	}
}
