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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapSupportsExternalUserImportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This dialog is used to set the zone share rights, ie who can share internal/external/public etc
 * 
 * @author jwootton
 */
public class EditZoneShareSettingsDlg extends DlgBox
	implements
		EditSuccessfulHandler
{
	private boolean						m_supportsExternalUserImport;
	private EditZoneShareTabBase		m_listsTab;
	private EditZoneShareTabBase		m_rightsTab;
	private FlowPanel					m_mainPanel;
	private GwtTeamingMessages			m_messages;
	private List<HandlerRegistration>	m_registeredEventHandlers;
	private TabPanel					m_tabs;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
	};

	
	/**
	 * Callback interface to interact with the "edit zone share rights" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditZoneShareSettingsDlgClient
	{
		void onSuccess( EditZoneShareSettingsDlg cssDlg );
		void onUnavailable();
	}

	/**
	 * 
	 */
	private EditZoneShareSettingsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height,
		EditZoneShareSettingsDlgClient ezsrDlgClient)
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		m_messages = GwtTeaming.getMessages();

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( m_messages.editZoneShareSettingsDlg_Header(), this, null, ezsrDlgClient ); 
	}

	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		m_mainPanel = new FlowPanel();
		m_mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		loadPart1Async(((EditZoneShareSettingsDlgClient) props));
		
		return m_mainPanel;
	}

	/*
	 * Asynchronously determines if external user LDAP imports are
	 * supported and then continues constructing the dialog.
	 */
	private void loadPart1Async(final EditZoneShareSettingsDlgClient ezsrDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(ezsrDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously determines if external user LDAP imports are
	 * supported and then continues constructing the dialog.
	 */
	private void loadPart1Now(final EditZoneShareSettingsDlgClient ezsrDlgClient) {
		GwtClientHelper.executeCommand( new GetLdapSupportsExternalUserImportCmd(), new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetLdapSupportsExternalUserImport() );
				
				m_supportsExternalUserImport = false;
				createTabs( ezsrDlgClient );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				BooleanRpcResponseData reply = ((BooleanRpcResponseData) result.getResponseData());
				m_supportsExternalUserImport = reply.getBooleanValue();
				createTabs( ezsrDlgClient );
			}
		} );
	}
	
	/*
	 * Creates the dialog's tab panel, populates the tabs and tells the
	 * caller the dialog is ready to go.
	 */
	private void createTabs(EditZoneShareSettingsDlgClient ezsrDlgClient) {
		m_tabs = new TabPanel();
		m_tabs.addStyleName( "vibe-tabPanel" );
		
		m_rightsTab = new EditZoneShareRightsTab(this);
		m_tabs.add( m_rightsTab, m_messages.editZoneShareSettingsDlg_Rights() );
		
		m_listsTab = new EditZoneShareListsTab(this);
		m_tabs.add( m_listsTab, m_messages.editZoneShareSettingsDlg_Lists() );
		
		m_mainPanel.add( m_tabs );
		
		m_tabs.selectTab(0);
		
		ezsrDlgClient.onSuccess( this );
	}
	
	/**
	 * Returns whether or not external user imports are supported.
	 * 
	 * @return
	 */
	public boolean isSupportsExternalUserImport() {
		return m_supportsExternalUserImport;
	}

	/*
	 * Called to save the contents of the dialog.
	 */
	private void doSave()
	{
		// Ask the tabs to save.  If they're successful, close the
		// dialog.
		m_rightsTab.save(
			new EditZoneShareTabCallback()
			{
				@Override
				public void success()
				{
					m_listsTab.save(
						new EditZoneShareTabCallback()
						{
							@Override
							public void success()
							{
								hide();
							}

							@Override
							public void failure()
							{
								// Nothing to do.  The lists tab will
								// have told user about the error.
								setOkEnabled( true );
							}
						} );
				}

				@Override
				public void failure()
				{
					// Nothing to do.  The rights tab will have told
					// user about the error.
					setOkEnabled( true );
				}
			} );
	}
	
	/*
	 * Called to validate and save the contents of the dialog.
	 */
	private void doValidateAndSave()
	{
		// Ask the tabs to validate their content.  If they're valid,
		// ask them to save their contents.
		m_rightsTab.validate(
			new EditZoneShareTabCallback()
			{
				@Override
				public void success()
				{
					m_listsTab.validate(
						new EditZoneShareTabCallback()
						{
							@Override
							public void success()
							{
								// The tabs contents are valid.  Ask
								// them to save.
								doSave();
							}

							@Override
							public void failure()
							{
								// Nothing to do.  The lists tab will
								// have told user about the error.
								setOkEnabled( true );
							}
						} );
				}

				@Override
				public void failure()
				{
					// Nothing to do.  The rights tab will have told
					// user about the error.
					setOkEnabled( true );
				}
			} );
	}

	/**
	 * This gets called when the user presses ok.  If we are editing an existing net folder
	 * we will issue an rpc request to save the net folder and then throw a "net folder modified"
	 * event.
	 * If we are creating a new net folder we will issue an rpc request to create the new net folder
	 * and then throw a "net folder created" event.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		clearErrorPanel();
		hideErrorPanel();

		// Disable the Ok button.
		setOkEnabled( false );

		// Validate and save the contents of the dialog.
		doValidateAndSave();
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully create/modify the share settings.
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what because editSuccessful() does the work.
		return Boolean.TRUE;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "share" );
		
		return helpData;
	}

	/**
	 * 
	 */
	private void init()
	{
		hideErrorPanel();
		
		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		m_tabs.selectTab(0);
		m_rightsTab.init();
		m_listsTab.init();
	}

	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
											GwtTeaming.getEventBus(),
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	/**
	 * Loads the EditZoneShareSettingsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createDlg(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final int width,
							final int height,
							final EditZoneShareSettingsDlgClient ezsrDlgClient )
	{
		GWT.runAsync( EditZoneShareSettingsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditZoneShareSettingsDlg() );
				if ( ezsrDlgClient != null )
				{
					ezsrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				new EditZoneShareSettingsDlg(
													autoHide,
													modal,
													left,
													top,
													width,
													height,
													ezsrDlgClient );
			}
		});
	}
	
	/**
	 * Put the initAndShow() method behind a split point
	 */
	public static void initAndShow( final EditZoneShareSettingsDlg dlg )
	{
		GWT.runAsync( EditZoneShareSettingsDlg.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_EditZoneShareSettingsDlg() );
			}

			@Override
			public void onSuccess()
			{
				if ( dlg != null )
				{
					dlg.init();
					dlg.show();
				}
			}
		} );
	}
}
