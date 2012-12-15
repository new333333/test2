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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserPropertiesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData.ProfileAttribute;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's user properties dialog.
 *  
 * @author drfoster@novell.com
 */
public class UserPropertiesDlg extends DlgBox {
	public static final boolean	SHOW_USER_PROPERTIES_ACTION_MENU	= false;	//! DRF:  Leave this false on checkin until the dialog is complete.
	
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Long							m_userId;					// The user we're dealing with.
	private UserPropertiesRpcResponseData	m_userProperties;			// Information about managing the user, once read from the server.
	private VibeFlowPanel					m_fp;						// The panel holding the dialog's content.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private UserPropertiesDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.userPropertiesDlgHeader(),	// The dialog's header.
			getSimpleSuccessfulHandler(),		// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),			// The dialog's EditCanceledHandler.
			null);								// Create callback data.  Unused.
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create and return a panel to hold the dialog's content.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-userPropertiesDlg-rootPanel");
		return m_fp;
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/*
	 * Ensures a string being used for label ends with a ':'.
	 */
	private String labelizeCaption(String s) {
		if (null != s) s = s.trim();
		int l = ((null == s) ? 0 : s.length());
		if ((0 < l) && (':' != s.charAt(l - 1))) {
			s = m_messages.userPropertiesDlgLabelize(s);
		}
		return s;
	}
	
	/*
	 * Asynchronously loads the entry types the user can select from.
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
	 * Synchronously loads the entry types the user can select from.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(
				new GetUserPropertiesCmd(m_userId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetUserProperties());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the user properties and complete the
				// population of the dialog.
				m_userProperties = ((UserPropertiesRpcResponseData) response.getResponseData());
				populateDlgAsync();
			}
		});
	}
	
	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_fp.clear();
		
		// ...create a grid to hold the dialog's contents...
		FlexTable grid = new FlexTable();
		grid.addStyleName("vibe-userPropertiesDlg-grid");
		m_fp.add(grid);
		FlexCellFormatter fcm = grid.getFlexCellFormatter();

		// ...add the user's title...
		ProfileEntryInfoRpcResponseData profile = m_userProperties.getProfile();
		Map<String, ProfileAttribute> attrMap = profile.getProfileEntryInfo();
		ProfileAttribute pa = attrMap.get("title");
		String title = pa.getAttributeValue();
		InlineLabel il = new InlineLabel(title);
		il.addStyleName("vibe-userPropertiesDlg-title");
		il.setWordWrap(false);
		grid.setWidget(0, 0, il);
		fcm.setColSpan(0, 0, 2);
		
		// ...and add the user's avatar.
		Image avatarImg = new Image();
		avatarImg.addStyleName("vibe-userPropertiesDlg-avatar");
		String avatarUrl = profile.getAvatarUrl();
		if (!(GwtClientHelper.hasString(avatarUrl)))
		     avatarImg.setUrl(GwtTeaming.getDataTableImageBundle().userPhoto().getSafeUri());
		else avatarImg.setUrl(avatarUrl);
		avatarImg.setTitle(title);
		grid.setWidget(1, 0, avatarImg);
		fcm.setColSpan(1, 0, 2);

		// Scan the attributes we have for the user...
		Set<String> attrKeys = attrMap.keySet();
		for (String attrKey:  attrKeys) {
			// ...skipping the title...
			if (attrKey.equals("title")) {
				continue;
			}

			// ...and adding the attribute's caption...
			int row = grid.getRowCount();
			pa = attrMap.get(attrKey);
			il = new InlineLabel(labelizeCaption(pa.getAttributeCaption()));
			il.addStyleName("vibe-userPropertiesDlg-attrCaption");
			il.setWordWrap(false);
			grid.setWidget(row, 0, il);

			// ...and value.
			String v = pa.getAttributeValue();
			il = new InlineLabel((null == v) ? "" : v);
			il.addStyleName("vibe-userPropertiesDlg-attrValue");
			il.setWordWrap(false);
			grid.setWidget(row, 1, il);
		}

		// Do we have a URL for this user to modify this entry?
		VibeFlowPanel buttonPanel = new VibeFlowPanel();
		buttonPanel.addStyleName("vibe-userPropertiesDlg-buttons");
		final String modifyUrl = profile.getModifyUrl();
		boolean hasModify = GwtClientHelper.hasString(modifyUrl);
		if (hasModify) {
			// Yes!  Create a push button so they can.
			Button button = new Button(m_messages.userPropertiesDlgModify());
			button.addStyleName("vibe-userPropertiesDlg-button vibe-userPropertiesDlg-modify");
			buttonPanel.add(button);
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					hide();
					GwtClientHelper.jsLaunchToolbarPopupUrl(modifyUrl, 850, 600);
				}
			});
		}

		// Do we have a URL for this user to delete this entry?
		final String deleteUrl = profile.getDeleteUrl();
		boolean hasDelete = GwtClientHelper.hasString(deleteUrl);
		if (hasDelete) {
			// Yes!  Create a push button so they can.
			Button button = new Button(m_messages.userPropertiesDlgDelete());
			button.addStyleName("vibe-userPropertiesDlg-button vibe-userPropertiesDlg-delete");
			buttonPanel.add(button);
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					hide();
					GwtClientHelper.jsLaunchToolbarPopupUrl(deleteUrl, 900, 150);
				}
			});
		}

		// If we created any push buttons...
		if (hasModify || hasDelete) {
			// ...add the button panel to the grid.
			int row = grid.getRowCount();
			grid.setWidget(row, 0, buttonPanel);
			fcm.setColSpan(row, 0, 2);
		}

		// Finally, show the dialog.
		show(true);
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

	/*
	 * Asynchronously runs the given instance of the user properties
	 * dialog.
	 */
	private static void runDlgAsync(final UserPropertiesDlg upDlg, final Long userId) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				upDlg.runDlgNow(userId);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the user properties
	 * dialog.
	 */
	private void runDlgNow(Long userId) {
		// Store the parameter and populate the dialog.
		m_userId = userId;
		loadPart1Async();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the user properties dialog and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the user properties dialog
	 * asynchronously after it loads. 
	 */
	public interface UserPropertiesDlgClient {
		void onSuccess(UserPropertiesDlg upDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the UserPropertiesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final UserPropertiesDlgClient upDlgClient,
			
			// initAndShow parameters,
			final UserPropertiesDlg upDlg,
			final Long userId) {
		GWT.runAsync(UserPropertiesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_UserPropertiesDlg());
				if (null != upDlgClient) {
					upDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != upDlgClient) {
					// Yes!  Create it and return it via the callback.
					UserPropertiesDlg upDlg = new UserPropertiesDlg();
					upDlgClient.onSuccess(upDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(upDlg, userId);
				}
			}
		});
	}
	
	/**
	 * Loads the UserPropertiesDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param upDlgClient
	 */
	public static void createAsync(UserPropertiesDlgClient upDlgClient) {
		doAsyncOperation(upDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the user properties dialog.
	 * 
	 * @param upDlg
	 * @param userId
	 */
	public static void initAndShow(UserPropertiesDlg upDlg, Long userId) {
		doAsyncOperation(null, upDlg, userId);
	}
}
