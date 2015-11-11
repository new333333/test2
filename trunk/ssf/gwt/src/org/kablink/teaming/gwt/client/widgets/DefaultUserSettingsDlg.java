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
package org.kablink.teaming.gwt.client.widgets;

import java.util.Map;
import java.util.TreeMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLocales;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTimeZones;
import org.kablink.teaming.gwt.client.rpc.shared.GetDefaultUserSettingsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DefaultUserSettingsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetLocalesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTimeZonesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetDefaultUserSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements the Default User Settings dialog.
 * 
 * This dialog allows the administrator to define the time zone and
 * locale to use when create new local and external (non-LDAP) users.
 * 
 * @author drfoster@novell.com
 */
public class DefaultUserSettingsDlg extends DlgBox implements EditSuccessfulHandler {
	private DefaultUserSettingsInfoRpcResponseData	m_defaultUserSettingsInfo;	// Information necessary to run the default user settings dialog.
	private GwtLocales								m_locales;					//
	private GwtTeamingMessages						m_messages;					//
	private GwtTimeZones							m_timeZones;				//
	private int										m_showX;					//
	private int										m_showY;					//
	@SuppressWarnings("unused")
	private int										m_showCX;					//
	@SuppressWarnings("unused")
	private int										m_showCY;					//
	private ListBox									m_timeZonesLB;				//
	private ListBox									m_timeZonesExtLB;			//
	private ListBox									m_localesLB;				//
	private ListBox									m_localesExtLB;				//
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DefaultUserSettingsDlg(DefaultUserSettingsDlgClient dusDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		// Initialize the super class...
		super(
			autoHide,
			modal,
			x, y, cx, cy,
			DlgButtonMode.OkCancel);
		
		// ...store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...initialize anything else the needs it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-defaultUserSettingsDlg");
		createAllDlgContent(
			m_messages.defaultUserSettingsDlg_Header(),
			this,
			DlgBox.getSimpleCanceledHandler(),
			dusDlgClient);
	}

	/**
	 * Called when the user selects the dialog's OK button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() method.
	 * 
	 * @param obj (unused)
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		// Save the default user settings...
		SetDefaultUserSettingsCmd cmd =
			new SetDefaultUserSettingsCmd(
				getSelectedTimeZone(),
				getSelectedLocale(),
				getSelectedTimeZoneExt(),
				getSelectedLocaleExt());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetDefaultUserSettings());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// ...and hide the dialog.
				hide();
			}
		});

		// Return false to keep the dialog open.  We'll close it after
		// successfully saving the settings.
		return false;
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create the main panel that will hold the dialog's content...
		FlowPanel mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName("vibe-defaultUserSettingsDlg-rootPanel");

		// Create the widgets for the default settings for internal
		// users.
		Label label = new Label(m_messages.defaultUserSettingsDlg_InternalUsers());
		label.addStyleName("margintop3 fontSize16px");
		mainPanel.add(label);

		label = new Label(m_messages.defaultUserSettingsDlg_TimeZone());
		label.addStyleName("margintop2 marginleft1 fontSize13px");
		mainPanel.add(label);
		
		m_timeZonesLB = new ListBox();
		m_timeZonesLB.setMultipleSelect(false);
		m_timeZonesLB.setVisibleItemCount(1);
		m_timeZonesLB.addStyleName("marginleft1");
		mainPanel.add(m_timeZonesLB);
		
		label = new Label(m_messages.defaultUserSettingsDlg_Locale());
		label.addStyleName("margintop2 marginleft1 fontSize13px");
		mainPanel.add(label);
		
		m_localesLB = new ListBox();
		m_localesLB.setMultipleSelect(false);
		m_localesLB.setVisibleItemCount(1);
		m_localesLB.addStyleName("marginleft1");
		mainPanel.add(m_localesLB);

		// Create the widgets for the default settings for external
		// users.
		String labelTxt;
		if (GwtClientHelper.isLicenseFilr())
		     labelTxt = m_messages.defaultUserSettingsDlg_ExternalUsers_Filr();
		else labelTxt = m_messages.defaultUserSettingsDlg_ExternalUsers_Vibe();
		label = new Label(labelTxt);
		label.addStyleName("margintop5 fontSize16px");
		mainPanel.add(label);

		label = new Label(m_messages.defaultUserSettingsDlg_TimeZone());
		label.addStyleName("margintop2 marginleft1 fontSize13px");
		mainPanel.add(label);
		
		m_timeZonesExtLB = new ListBox();
		m_timeZonesExtLB.setMultipleSelect(false);
		m_timeZonesExtLB.setVisibleItemCount(1);
		m_timeZonesExtLB.addStyleName("marginleft1");
		mainPanel.add(m_timeZonesExtLB);
		
		label = new Label(m_messages.defaultUserSettingsDlg_Locale());
		label.addStyleName("margintop2 marginleft1 fontSize13px");
		mainPanel.add(label);
		
		m_localesExtLB = new ListBox();
		m_localesExtLB.setMultipleSelect(false);
		m_localesExtLB.setVisibleItemCount(1);
		m_localesExtLB.addStyleName("marginleft1");
		mainPanel.add(m_localesExtLB);
		
		// Load the time zones and locales into the appropriate
		// widgets.
		loadPart1Async((DefaultUserSettingsDlgClient) callbackData);

		// Finally, return the main panel.
		return mainPanel;
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
		// Nothing focusable in the dialog.
		return null;
	}
	
	/**
	 * Returns the HelpData for the default user settings dialog.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("defaultUserSettings");
		return helpData;
	}

	/*
	 */
	private String getSelectedLocale() {
		return getSelectedLocaleImpl(m_localesLB);
	}
	
	private String getSelectedLocaleExt() {
		return getSelectedLocaleImpl(m_localesExtLB);
	}
	
	private String getSelectedLocaleImpl(ListBox lb) {
		String timeZoneId;
		
		int selectedIndex = lb.getSelectedIndex();
		if ((-1) == selectedIndex) {
			selectedIndex = 0;
		}
		timeZoneId = lb.getValue(selectedIndex);
		return timeZoneId;
	}
	
	/*
	 */
	private String getSelectedTimeZone() {
		return getSelectedTimeZoneImpl(m_timeZonesLB);
	}
	
	private String getSelectedTimeZoneExt() {
		return getSelectedTimeZoneImpl(m_timeZonesExtLB);
	}
	
	private String getSelectedTimeZoneImpl(ListBox lb) {
		String timeZoneId;
		
		int selectedIndex = lb.getSelectedIndex();
		if ((-1) == selectedIndex) {
			selectedIndex = 0;
		}
		timeZoneId = lb.getValue(selectedIndex);
		return timeZoneId;
	}
	
	/*
	 * Asynchronously gets the data from the server and finishes
	 * populating the dialog.
	 */
	private void loadDataFromServerAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadDataFromServerNow();
			}
		});
	}
	
	/*
	 * Synchronously gets the data from the server and finishes
	 * populating the dialog.
	 */
	private void loadDataFromServerNow() {
		GwtClientHelper.executeCommand(new GetDefaultUserSettingsInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetDefaultUserSettings());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_defaultUserSettingsInfo = ((DefaultUserSettingsInfoRpcResponseData) result.getResponseData());
				populateDlgAsync();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart1Async(final DefaultUserSettingsDlgClient dusDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(dusDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart1Now(final DefaultUserSettingsDlgClient dusDlgClient) {
		// Execute a GWT RPC command to get the time zones
		GwtClientHelper.executeCommand(new GetTimeZonesCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetTimeZones());
				dusDlgClient.onUnavailable();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_timeZones = ((GwtTimeZones) response.getResponseData());
				loadPart2Async(dusDlgClient);
			}
		});
	}
	
	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart2Async(final DefaultUserSettingsDlgClient dusDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now(dusDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart2Now(final DefaultUserSettingsDlgClient dusDlgClient) {
		GwtClientHelper.executeCommand(new GetLocalesCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetLocales() );
				dusDlgClient.onUnavailable();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_locales = ((GwtLocales) response.getResponseData());
				loadPart3Async(dusDlgClient);
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart3Async(final DefaultUserSettingsDlgClient dusDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now(dusDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart3Now(final DefaultUserSettingsDlgClient dusDlgClient) {
		// Populate the time zone...
		TreeMap<String,String> listOfTimeZones = m_timeZones.getListOfTimeZones();
		for (Map.Entry<String, String> mapEntry:  listOfTimeZones.entrySet()) {
			String tzId   = mapEntry.getValue();
			String tzName = mapEntry.getKey();
			
			m_timeZonesLB.addItem(   tzName, tzId);
			m_timeZonesExtLB.addItem(tzName, tzId);
		}
		
		// ...and locale list boxes.
		TreeMap<String,String> listOfLocales = m_locales.getListOfLocales();
		for (Map.Entry<String, String> mapEntry: listOfLocales.entrySet()) {
			String localeDisplayName = mapEntry.getKey();
			String localeId          = mapEntry.getValue();

			m_localesLB.addItem(   localeDisplayName, localeId);
			m_localesExtLB.addItem(localeDisplayName, localeId);
		}
		
		// Finally, tell the caller that we're ready to go.
		dusDlgClient.onSuccess(this);
	}

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					populateDlgNow();
				}
			});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Select the time zones and locales using the server data...
		GwtClientHelper.selectListboxItemByValue(m_timeZonesLB,    m_defaultUserSettingsInfo.getTimeZone()   );
		GwtClientHelper.selectListboxItemByValue(m_localesLB,      m_defaultUserSettingsInfo.getLocale()     );
		GwtClientHelper.selectListboxItemByValue(m_timeZonesExtLB, m_defaultUserSettingsInfo.getTimeZoneExt());
		GwtClientHelper.selectListboxItemByValue(m_localesExtLB,   m_defaultUserSettingsInfo.getLocaleExt()  );
		
		// ...and position and show the dialog.
		setPopupPosition(m_showX, m_showY);
		show();
	}

	/*
	 * Asynchronously runs the given instance of the default user
	 * settings dialog.
	 */
	private static void runDlgAsync(final DefaultUserSettingsDlg dusDlg, final int x, final int y, final int cx, final int cy) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					dusDlg.runDlgNow(x, y, cx, cy);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the default user
	 * settings dialog.
	 */
	private void runDlgNow(int x, int y, int cx, int cy) {
		// Store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...and start populating the dialog.
		loadDataFromServerAsync();
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the default user settings dialog and perform some operation   */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'default user settings'
	 * dialog asynchronously after it loads. 
	 */
	public interface DefaultUserSettingsDlgClient {
		void onSuccess(DefaultUserSettingsDlg dusDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the UserPropertiesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final DefaultUserSettingsDlgClient	dusDlgClient,
			final boolean						autoHide,
			final boolean						modal,
			final int							createX,
			final int							createY,
			final int							createCX,
			final int							createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final DefaultUserSettingsDlg		dusDlg,
			final int							initX,
			final int							initY,
			final int							initCX,
			final int							initCY) {
		GWT.runAsync(DefaultUserSettingsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_DefaultUserSettingsDlg());
				if (null != dusDlgClient) {
					dusDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != dusDlgClient) {
					// Yes!  Create the dialog....
					new DefaultUserSettingsDlg(
						dusDlgClient,	// The client onSuccess()/onUnavailable() will be called from createContent().
						autoHide,
						modal,
						createX,
						createY,
						createCX,
						createCY);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(dusDlg, initX, initY, initCX, initCY);
				}
			}
		});
	}
	
	/**
	 * Creates an instance of the DefaultUserSettingsDlg returns it via
	 * the callback.
	 * 
	 * @param dusDlgClient
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(final DefaultUserSettingsDlgClient dusDlgClient, final boolean autoHide, final boolean modal, final int x, final int y, final int cx, final int cy) {
		doAsyncOperation(
			// Creation parameters.
			dusDlgClient,
			autoHide,
			modal,
			x, y, cx, cy,
			
			// Initialize and show parameters.  Unused.
			null,
			(-1), (-1), (-1), (-1));
	}
	
	/**
	 * Initializes and shows the default user settings dialog.
	 * 
	 * @param dusDlg
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void initAndShow(DefaultUserSettingsDlg dusDlg, int x, int y, int cx, int cy) {
		doAsyncOperation(
			// Creation parameters.  Unused.
			null,
			false,
			false,
			(-1), (-1), (-1), (-1),
			
			// Initialize and show parameters.
			dusDlg,
			x, y, cx, cy);
	}
}
