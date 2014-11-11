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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtPrincipalMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetPrincipalMobileAppsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SavePrincipalMobileAppsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SavePrincipalMobileAppsConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtMobileOpenInSetting;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PromptDlg.PromptDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This dialog is used to edit the user's mobile applications settings.
 * 
 * @author jwootton
 */
public class ConfigureUserMobileAppsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private boolean m_principalsAreUsers;
	private boolean m_initialAllowPlayWithOtherApps;
	
	private RadioButton m_useGlobalSettingsRB;
	private RadioButton m_useUserSettingsRB;
	private CheckBox m_enableMobileAppsAccessCB;
	private CheckBox m_allowPwdCacheCB;
	private CheckBox m_allowOfflineContentCB;
	
	// Mobile Application Management (MAM) settings.
	private CheckBox m_cutCopyEnabledCB;
	private CheckBox m_screenCaptureEnabledAndroidCB;
	private CheckBox m_disableJailBrokenCB;
	private FlowPanel m_androidApplicationsPanel;
	private FlowPanel m_iosApplicationsPanel;
	private ListBox m_androidApplicationsLB;
	private ListBox m_iosApplicationsLB;
	private ListBox m_openInLB;
	private PromptDlg m_pDlg;
	
	private List<Long> m_principalIds;
	private ArrayList<Long> m_listOfRemainingPrincipalIds;	// This is the list we draw from when we are saving the config.
	private ArrayList<Long> m_nextBatchOfPrincipalIds;
	
	private static int BATCH_SIZE = 10;
	
	/**
	 * Callback interface to interact with the "configure user mobile applications" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureUserMobileAppsDlgClient
	{
		void onSuccess( ConfigureUserMobileAppsDlg cumaDlg );
		void onUnavailable();
	}

	/**
	 * 
	 */
	private ConfigureUserMobileAppsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", this, null, null );	// Caption filled in during the init().  
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlowPanel userPanel;
		FlowPanel ckboxPanel;
		FlowPanel tmpPanel;
		ClickHandler clickHandler;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_useGlobalSettingsRB = new RadioButton( "settingScope", messages.configureMobileAppsDlgUseGlobalSettings() );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginbottom1" );
		tmpPanel.add( m_useGlobalSettingsRB );
		mainPanel.add( tmpPanel );
		
		m_useUserSettingsRB = new RadioButton( "settingScope" );
		setRBText();
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginbottom1" );
		tmpPanel.add( m_useUserSettingsRB );
		mainPanel.add( tmpPanel );
		
		userPanel = new FlowPanel();
		userPanel.addStyleName( "marginleft1" );
		mainPanel.add( userPanel );

		ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName( "marginleft1" );
		ckboxPanel.addStyleName( "marginbottom2" );
		userPanel.add( ckboxPanel );
		
		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				m_useGlobalSettingsRB.setValue( false );
				m_useUserSettingsRB.setValue( true );
			}
		};
		
		// Add the controls for the "allow mobile applications to access Filr"
		{
			String productName;
			
			productName = "Vibe";
			if ( GwtClientHelper.isLicenseFilr() )
				productName = "Filr";
			
			m_enableMobileAppsAccessCB = new CheckBox( messages.configureMobileAppsDlgAllowAccess( productName ) );
			m_enableMobileAppsAccessCB.addClickHandler( clickHandler );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_enableMobileAppsAccessCB );
			ckboxPanel.add( tmpPanel );
		}
		
		// Create the "Allow mobile applications to cache password"
		m_allowPwdCacheCB = new CheckBox( messages.configureMobileAppsDlgAllowCachePwd() );
		m_allowPwdCacheCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowPwdCacheCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Allow mobile applications to cache content offline"
		m_allowOfflineContentCB = new CheckBox( messages.configureMobileAppsDlgAllowCacheContent() );
		m_allowOfflineContentCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowOfflineContentCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the Mobile Application Management (MAM) widgets.
		
		// Create the "Cut/Copy enabled"
		m_cutCopyEnabledCB = new CheckBox( messages.configureMobileAppsDlgCutCopy() );
		m_cutCopyEnabledCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_cutCopyEnabledCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Screen capture enabled (Android only)"
		m_screenCaptureEnabledAndroidCB = new CheckBox( messages.configureMobileAppsDlgScreenCaptureAndroid() );
		m_screenCaptureEnabledAndroidCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_screenCaptureEnabledAndroidCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Disable applications on rooted or jail broken devices"
		m_disableJailBrokenCB = new CheckBox( messages.configureMobileAppsDlgDisableApplicationsOnRootedOrJailBrokenDevices() );
		m_disableJailBrokenCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_disableJailBrokenCB );
		ckboxPanel.add( tmpPanel );		
		
		// Create the controls for open in
		{
			HorizontalPanel hp = new HorizontalPanel();
			hp.addStyleName( "margintop3" );
			
			Label oiLabel = new Label( messages.configureMobileAppsDlgOpenIn() );
			oiLabel.addStyleName( "configMobileAppsDlg_OpenInLabel" );
			hp.add( oiLabel );
			
			m_openInLB = new ListBox( false );	// false -> Not a multi-select ListBox.
			m_openInLB.addStyleName( "configMobileAppsDlg_OpenInSelect" );
			m_openInLB.setVisibleItemCount(1);
			m_openInLB.addItem( messages.configureMobileAppsDlgOpenIn_Disabled(),  "0" );
			m_openInLB.addItem( messages.configureMobileAppsDlgOpenIn_AllApps(),   "1" );
			m_openInLB.addItem( messages.configureMobileAppsDlgOpenIn_WhiteList(), "2" );
			m_openInLB.addChangeHandler( new ChangeHandler()
			{
				@Override
				public void onChange( ChangeEvent event )
				{
					m_useGlobalSettingsRB.setValue( false );
					m_useUserSettingsRB.setValue( true );
					
					danceDlg();
				}
			} );
			hp.add( m_openInLB );
			
			ckboxPanel.add( hp );
			
			// Create the controls for the open in White Lists
			m_androidApplicationsPanel = createAndroidWhiteList( messages );
			ckboxPanel.add( m_androidApplicationsPanel );
			
			m_iosApplicationsPanel = createIosWhiteList( messages );
			ckboxPanel.add( m_iosApplicationsPanel );
		}
		
		return mainPanel;
	}
	
	private void setRBText()
	{
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		String rbText;
		if (m_principalsAreUsers)
		     rbText = messages.configureMobileAppsDlgUseUserSettings();
		else rbText = messages.configureMobileAppsDlgUseGroupSettings();
		m_useUserSettingsRB.setText(rbText);
	}
	
	/*
	 * Creates the widgets for entering Android applications.
	 */
	private FlowPanel createAndroidWhiteList( GwtTeamingMessages messages )
	{
		FlowPanel fp = new FlowPanel();
		fp.addStyleName( "configMobileAppsDlg_AndroidPanel" );

		// Note that the list will be added to the FlowPanel by
		// createWhiteList().
		m_androidApplicationsLB = createWhiteList(
			messages,
			fp,
			messages.configureMobileAppsDlgWhiteListAndroid(),
			messages.configureMobileAppsDlgAddAndroid());
		
		return fp;
	}
	
	/*
	 * Creates the widgets for entering iOS applications.
	 */
	private FlowPanel createIosWhiteList( GwtTeamingMessages messages )
	{
		FlowPanel fp = new FlowPanel();
		fp.addStyleName( "configMobileAppsDlg_IosPanel" );

		// Note that the list will be added to the FlowPanel by
		// createWhiteList().
		m_iosApplicationsLB = createWhiteList(
			messages,
			fp,
			messages.configureMobileAppsDlgWhiteListIos(),
			messages.configureMobileAppsDlgAddIos());
		
		return fp;
	}
	
	/*
	 * Creates the widgets for entering Android or iOS application
	 * white lists.
	 */
	private ListBox createWhiteList( final GwtTeamingMessages messages, final FlowPanel contentPanel, final String listLabel, final String addPrompt )
	{
		// Add a label for the list widgets.
		InlineLabel il = new InlineLabel( listLabel );
		il.addStyleName( "configMobileAppsDlg_ListHeader" );
		contentPanel.add( il );

		// Create a HorizontalPanel to hold the list widgets.
		HorizontalPanel horizontalListPanel = new HorizontalPanel();
		horizontalListPanel.addStyleName( "configMobileAppsDlg_ListPanel" );
		horizontalListPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_TOP );

		// Create the ListBox itself.
		final ListBox listBox = new ListBox( true );	// true -> Multi-select ListBox.
		listBox.setVisibleItemCount( 5 );
		listBox.addStyleName( "configMobileAppsDlg_List" );

		// Create a VerticalPanel to hold buttons for adding to and
		// removing from the list.
		VerticalPanel verticalButtonPanel = new VerticalPanel();
		verticalButtonPanel.addStyleName( "configMobileAppsDlg_ListButtons" );
		verticalButtonPanel.setVerticalAlignment(   HasVerticalAlignment.ALIGN_TOP    );
		verticalButtonPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_LEFT );

		// Create a button for adding to the list.
		Button b = new Button( messages.configureMobileAppsDlgButton_Add(), new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				m_useGlobalSettingsRB.setValue( false );
				m_useUserSettingsRB.setValue( true );
				
				promptForDataAsync( listBox, addPrompt );
			}
		} );
		b.addStyleName( "configMobileAppsDlg_ListButton" );
		verticalButtonPanel.add( b );

		// Create button for removing from the list.
		b = new Button( messages.configureMobileAppsDlgButton_Delete(), new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Scan the list...
						int c = listBox.getItemCount();
						for ( int i = (c - 1); i >= 0; i -= 1 ) 
						{
							if ( listBox.isItemSelected( i ) )
							{
								// ...and remove the selected items.
								listBox.removeItem( i );
							}
						}
					}
				});
			}
		} );
		b.addStyleName( "margintop3pxb configMobileAppsDlg_ListButton" );
		verticalButtonPanel.add( b );
		
		// Connect the panels together.
		horizontalListPanel.add( listBox             );
		horizontalListPanel.add( verticalButtonPanel );
		contentPanel.add(        horizontalListPanel );

		// If we get here, reply refers to the ListBox widget we
		// created.  Return it.
		return listBox;
	}
	
	/*
	 * Hides/shows the application white lists based on what's selected
	 * in the open in list box.
	 */
	private void danceDlg()
	{
		int i = m_openInLB.getSelectedIndex();
		if ( 0 <= i )
		{
			i = Integer.parseInt( m_openInLB.getValue( i ) );
			boolean show = ( 2 == i );
			m_androidApplicationsPanel.setVisible( show );
			m_iosApplicationsPanel.setVisible(     show );
		}
	}
	
	/*
	 * Asynchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataAsync( final ListBox listBox, final String addPrompt, final String addThis )
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute() {
				promptForDataNow( listBox, addPrompt, addThis );
			}
		} );
	}
	
	private void promptForDataAsync( final ListBox listBox, final String addPrompt )
	{
		// Always use the initial form of the method.
		promptForDataAsync( listBox, addPrompt, "" );
	}
	
	/*
	 * Synchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataNow( final ListBox listBox, final String addPrompt, final String addThis )
	{
		// Prompt the user for something to add.
		PromptDlg.initAndShow(
			m_pDlg,
			new PromptCallback() {
				@Override
				public void applied(String addThis) {
					// Did they enter something?
					if (!(GwtClientHelper.hasString(addThis))) {
						// No!  Bail.
						return;
					}
					
					addThis = addThis.trim();
					if (0 < addThis.length()) {
						// If this isn't already in the list...
						if (!(listContains(listBox, addThis))) {
							// ...add it...
							listBox.addItem(addThis);
						}
						
						// ...and bail.  We're done with the add.
						return;
					}
				}

				@Override
				public void canceled() {
					// Nothing to do.
				}
			},
			addPrompt,
			addThis);
	}
	
	/*
	 * Returns true if a ListBox contains a string and false otherwise.
	 */
	private static boolean listContains( ListBox lb, String s )
	{
		if ( null != s )
		{
			s = s.trim();
			if ( 0 < s.length() )
			{
				s = s.toLowerCase();
				for ( int i = 0; i < lb.getItemCount(); i += 1 )
				{
					String v = lb.getValue( i );
					if ( v.toLowerCase().equals( s ) )
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This method gets called when user user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		if ( m_principalIds != null && m_principalIds.size() > 0 )
		{
			final GwtPrincipalMobileAppsConfig mobileAppsConfig;
	
			mobileAppsConfig = (GwtPrincipalMobileAppsConfig) obj;
			
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					clearErrorPanel();
					hideErrorPanel();
					
					// We save the config for users in a batch of 10.  m_listOfRemainingPrincipalIds
					// is the list we work from.
					if ( m_listOfRemainingPrincipalIds == null )
						m_listOfRemainingPrincipalIds = new ArrayList<Long>();
					else
						m_listOfRemainingPrincipalIds.clear();
					for ( Long pId : m_principalIds )
					{
						m_listOfRemainingPrincipalIds.add( pId );
					}
					
					// Disable the Ok button.
					setOkEnabled( false );

					// Issue an rpc request to save the config for the first n users.
					saveConfigForNextBatchOfUsers( mobileAppsConfig );
				}
			} );
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/**
	 * Return whether the value entered by the user for allowing mobile apps to cache passwords
	 */
	private boolean getAllowCachePwd()
	{
		return m_allowPwdCacheCB.getValue();
	}
	
	/**
	 * Return whether the mobile apps can cache content offline
	 */
	private boolean getAllowOfflineContent()
	{
		return m_allowOfflineContentCB.getValue();
	}
	
	/**
	 * Return whether mobile apps can interact with other applications
	 */
	private boolean getAllowPlayWithOtherApps()
	{
		return m_initialAllowPlayWithOtherApps;
	}
	
	/*
	 * Return whether cut/copy is enabled.
	 */
	private boolean getCutCopyEnabled()
	{
		if ( m_cutCopyEnabledCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/*
	 * Return whether Android screen capture is enabled.
	 */
	private boolean getScreenCaptureAndoridEnabled()
	{
		if ( m_screenCaptureEnabledAndroidCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/*
	 * Return whether to disable the device if it's jail broken.
	 */
	private boolean getDisableJailBroken()
	{
		if ( m_disableJailBrokenCB.getValue() == Boolean.TRUE )
			return true;

		return false;
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
		helpData.setPageId( "mobile_user" );
		
		return helpData;
	}

	/*
	 * Returns the open in setting.
	 */
	private GwtMobileOpenInSetting getOpenIn()
	{
		int moi = Integer.parseInt( m_openInLB.getValue( m_openInLB.getSelectedIndex() ) );
		return GwtMobileOpenInSetting.valueOf( moi );
	}
	
	/*
	 * Returns the Android application list.
	 */
	private List<String> getAndroidApplicatons()
	{
		List<String> reply = new ArrayList<String>();
		for ( int i = 0; i < m_androidApplicationsLB.getItemCount(); i += 1 )
		{
			reply.add( m_androidApplicationsLB.getItemText( i ) );
		}
		return reply;
	}
	
	/*
	 * Returns the iOS application list.
	 */
	private List<String> getIosApplicatons()
	{
		List<String> reply = new ArrayList<String>();
		for ( int i = 0; i < m_iosApplicationsLB.getItemCount(); i += 1 )
		{
			reply.add( m_iosApplicationsLB.getItemText( i ) );
		}
		return reply;
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtMobileAppsConfiguration object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtPrincipalMobileAppsConfig mobileAppsConfig;
		
		mobileAppsConfig = new GwtPrincipalMobileAppsConfig();

		// Get whether to use the global settings
		mobileAppsConfig.setUseGlobalSettings( getUseGlobalSettings() );

		// Get whether mobile apps can access Filr
		mobileAppsConfig.setMobileAppsEnabled( getMobileAppsEnabled() );
		
		// Get whether mobile apps can cache the user's password
		mobileAppsConfig.setAllowCachePwd( getAllowCachePwd() );
		
		// Get whether mobile apps can cache content
		mobileAppsConfig.setAllowCacheContent( getAllowOfflineContent() );
		
		// Get whether mobile apps can interact with other apps
		mobileAppsConfig.setAllowPlayWithOtherApps( getAllowPlayWithOtherApps() );
		
		// Get the various values for the Mobile Application Management
		// (MAM) settings.
		mobileAppsConfig.setMobileCutCopyEnabled(                     getCutCopyEnabled()              );
		mobileAppsConfig.setMobileAndroidScreenCaptureEnabled(        getScreenCaptureAndoridEnabled() );
		mobileAppsConfig.setMobileDisableOnRootedOrJailBrokenDevices( getDisableJailBroken()           );
		mobileAppsConfig.setMobileOpenIn(                             getOpenIn()                      );
		mobileAppsConfig.setAndroidApplications(                      getAndroidApplicatons()          );
		mobileAppsConfig.setIosApplications(                          getIosApplicatons()              );
		
		return mobileAppsConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_enableMobileAppsAccessCB;
	}
	
	/**
	 * Return whether mobile apps are enabled.
	 */
	private boolean getMobileAppsEnabled()
	{
		if ( m_enableMobileAppsAccessCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Get the list of the next n principalIds
	 */
	private ArrayList<Long> getNextBatchOfPrincipalIds()
	{
		if ( m_listOfRemainingPrincipalIds != null && m_listOfRemainingPrincipalIds.size() > 0 )
		{
			int cnt;
			
			if ( m_nextBatchOfPrincipalIds == null )
				m_nextBatchOfPrincipalIds = new ArrayList<Long>();
			else
				m_nextBatchOfPrincipalIds.clear();
			
			for ( cnt = 0; cnt < BATCH_SIZE && m_listOfRemainingPrincipalIds.size() > 0; ++cnt )
			{
				m_nextBatchOfPrincipalIds.add( m_listOfRemainingPrincipalIds.get( 0 ) );
				
				// Remove this user id from the working list.
				m_listOfRemainingPrincipalIds.remove( 0 );
			}
			
			return m_nextBatchOfPrincipalIds;
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private boolean getUseGlobalSettings()
	{
		return m_useGlobalSettingsRB.getValue();
	}
	
	/**
	 * 
	 */
	public void init( List<Long> principalIds, boolean principalsAreUsers )
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		if ( principalIds == null )
			return;

		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		m_principalsAreUsers = principalsAreUsers;
		m_principalIds = principalIds;
		
		setRBText();
		
		String caption;
		if ( principalsAreUsers )
		     caption = GwtTeaming.getMessages().configureUserMobileAppsDlgHeaderUsers(  String.valueOf( principalIds.size() ) );
		else caption = GwtTeaming.getMessages().configureUserMobileAppsDlgHeaderGroups( String.valueOf( principalIds.size() ) );
		setCaption( caption );
		
		// Create a callback that will be called when we get the mobile apps configuration.
		rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUserMobileAppsConfiguration() );
				
				// We can't run the dialog without the settings.  Make
				// sure it's hidden.
				hide();
			}
	
			/**
			 * We successfully retrieved the Mobile Apps configuration.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final GwtPrincipalMobileAppsConfig mobileAppsConfig;
				
				mobileAppsConfig = (GwtPrincipalMobileAppsConfig) response.getResponseData();
				
				initPart2Async( mobileAppsConfig );
			}
		};

		// If we are only dealing with 1 user, issue an ajax request to get the
		// User's Mobile Apps configuration.
		if ( principalIds.size() == 1 )
		{
			GetPrincipalMobileAppsConfigCmd cmd;
			
			// Issue an ajax request to get the Mobile Apps configuration from the db.
			cmd = new GetPrincipalMobileAppsConfigCmd();
			cmd.setPrincipalId( principalIds.get( 0 ) );
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
		else
		{
			GwtPrincipalMobileAppsConfig config = null;
			
			initUsingConfigData( config );
		}
	}

	/*
	 * Asynchronously performs the next part of the initializations.
	 */
	private void initPart2Async( final GwtPrincipalMobileAppsConfig mobileAppsConfig )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				initPart2Now( mobileAppsConfig );
			}
		} );
	}
	
	/*
	 * Synchronously performs the next part of the initializations.
	 */
	private void initPart2Now( final GwtPrincipalMobileAppsConfig mobileAppsConfig )
	{
		// Have we created a prompt dialog yet?
		if ( null == m_pDlg ) {
			// No!  Create one now...
			PromptDlg.createAsync(new PromptDlgClient() {
				@Override
				public void onSuccess(PromptDlg pDlg) {
					// ...and continue initializing.
					m_pDlg = pDlg;
					initUsingConfigData( mobileAppsConfig );
				}
	
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
			});
		}
		else {
			// Yes, we've already created a PromptDlg!  Simply
			// continue initializing.
			initUsingConfigData( mobileAppsConfig );
		}
	}
	
	/*
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	private void initUsingConfigData( GwtPrincipalMobileAppsConfig mobileAppsConfig )
	{
		if ( mobileAppsConfig != null )
		{
			m_useGlobalSettingsRB.setValue( mobileAppsConfig.getUseGlobalSettings() );
			m_useUserSettingsRB.setValue( !mobileAppsConfig.getUseGlobalSettings() );
			
			// Initialize whether mobile apps can access Filr
			m_enableMobileAppsAccessCB.setValue( mobileAppsConfig.getMobileAppsEnabled() );
				
			// Initialize the allow pwd cache checkbox
			m_allowPwdCacheCB.setValue( mobileAppsConfig.getAllowCachePwd() );
	
			// Initialize the offline content
			m_allowOfflineContentCB.setValue( mobileAppsConfig.getAllowCacheContent() );
			
			// Initialize the allow mobile apps to play with others
			m_initialAllowPlayWithOtherApps = mobileAppsConfig.getAllowPlayWithOtherApps();
			
			// Initialize the various widgets for the Mobile Application Management
			// (MAM) settings.
			m_cutCopyEnabledCB.setValue( mobileAppsConfig.getMobileCutCopyEnabled() );
			m_screenCaptureEnabledAndroidCB.setValue( mobileAppsConfig.getMobileAndroidScreenCaptureEnabled() );
			m_disableJailBrokenCB.setValue( mobileAppsConfig.getMobileDisableOnRootedOrJailBrokenDevices() );
			GwtMobileOpenInSetting moi = mobileAppsConfig.getMobileOpenIn();
			if ( null == moi )
			{
				moi = GwtMobileOpenInSetting.ALL_APPLICATIONS;
			}
			String moiS = String.valueOf( moi.ordinal() );
			int si = 0;
			for ( int i = 0; i < m_openInLB.getItemCount(); i += 1 )
			{
				if ( m_openInLB.getValue( i ).equals( moiS )) {
					si = i;
					break;
				}
			}
			m_openInLB.setSelectedIndex(si);
			m_androidApplicationsLB.clear();
			m_iosApplicationsLB.clear();
			List<String> apps = mobileAppsConfig.getAndroidApplications();
			if ( null != apps )
			{
				for ( String aApp:  apps )
				{
					m_androidApplicationsLB.addItem( aApp );
				}
			}
			apps = mobileAppsConfig.getIosApplications();
			if ( null != apps )
			{
				for ( String iApp:  apps )
				{
					m_iosApplicationsLB.addItem( iApp );
				}
			}
		}
		else
		{
			m_useGlobalSettingsRB.setValue( true );
			m_useUserSettingsRB.setValue( false );
			m_enableMobileAppsAccessCB.setValue( false );
			m_allowPwdCacheCB.setValue( false );
			m_allowOfflineContentCB.setValue( false );
			m_initialAllowPlayWithOtherApps = false;
			
			// Initialize the various widgets for the Mobile Application Management
			// (MAM) settings.
			m_cutCopyEnabledCB.setValue( false );
			m_screenCaptureEnabledAndroidCB.setValue( false );
			m_disableJailBrokenCB.setValue( false );
			String moiS = String.valueOf( GwtMobileOpenInSetting.ALL_APPLICATIONS.ordinal() );
			int si = 0;
			for ( int i = 0; i < m_openInLB.getItemCount(); i += 1 )
			{
				if ( m_openInLB.getValue( i ).equals( moiS )) {
					si = i;
					break;
				}
			}
			m_openInLB.setSelectedIndex(si);
			m_androidApplicationsLB.clear();
			m_iosApplicationsLB.clear();
		}
		
		// Dance the dialog for the open in setting.
		danceDlg();
		
		hideErrorPanel();
	}
	
	/**
	 * Issue an rpc request to save the config for the next n users.
	 */
	private void saveConfigForNextBatchOfUsers( final GwtPrincipalMobileAppsConfig config )
	{
		ArrayList<Long> principalIds;

		// Get the next batch of user ids
		principalIds = getNextBatchOfPrincipalIds();
		if ( principalIds != null && principalIds.size() > 0 )
		{
			AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
			SavePrincipalMobileAppsConfigCmd cmd;

			// Update the Saving n of nn message
			updateStatusMsg();
			
			// Create the callback that will be used when we issue an ajax request to save the user's mobile apps configuration.
			rpcSaveCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable caught )
				{
					FlowPanel errorPanel;
					Label label;
					String errMsg = null;
					
					hideStatusMsg();
					setOkEnabled( true );

					// Get the panel that holds the errors.
					errorPanel = getErrorPanel();
					
					if ( errMsg == null )
					{
						errMsg = GwtTeaming.getMessages().configureUserMobileAppsDlgOnSaveUnknownException( caught.toString() );
					}
					
					label = new Label( errMsg );
					label.addStyleName( "dlgErrorLabel" );
					errorPanel.add( label );
					
					showErrorPanel();
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					VibeRpcResponseData data;
					
					data = response.getResponseData();
					if ( data instanceof SavePrincipalMobileAppsConfigRpcResponseData )
					{
						SavePrincipalMobileAppsConfigRpcResponseData responseData;
						ArrayList<String> errors;
						
						// Get any errors that may have happened
						responseData = (SavePrincipalMobileAppsConfigRpcResponseData) data;
						errors = responseData.getErrors();
						if ( errors != null && errors.size() > 0 )
						{
							FlowPanel errorPanel;
							
							// Is the error panel already visible?
							errorPanel = getErrorPanel();
							if ( isErrorPanelVisible() == false )
							{
								Label label;
								
								// No, add an error header to it
								label = new Label( GwtTeaming.getMessages().configuerUserMobileAppsDlgErrorHeader() );
								errorPanel.add( label );
							}
							
							for ( String nextErr: errors )
							{
								Label label;
							
								label = new Label( nextErr );
								label.addStyleName( "dlgErrorLabel" );
								label.addStyleName( "marginleft1" );
								errorPanel.add( label );
							}
							
							showErrorPanel();
						}
					}
					
					GwtClientHelper.deferCommand( new ScheduledCommand() 
					{
						@Override
						public void execute() 
						{
							// Call this method again to see if there are any users that we need
							// to save the config for.
							saveConfigForNextBatchOfUsers( config );
						}
					} );
				}
			};

			// Issue an ajax request to save the user's Mobile apps configuration to the db.  rpcSaveCallback will
			// be called when we get the response back.
			cmd = new SavePrincipalMobileAppsConfigCmd( config, principalIds, m_principalsAreUsers );
			GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		}
		else
		{
			// We have saved the config to all the users.

			// Enable the Ok button.
			hideStatusMsg();
			setOkEnabled( true );

			// Are there any errors displayed?
			if ( isErrorPanelVisible() == false )
			{
				// No
				// Close the dialog.
				hide();
			}
		}
	}
	
	/**
	 * Update the status message that displays Saving n of n
	 */
	private void updateStatusMsg()
	{
		if ( m_principalIds != null && m_listOfRemainingPrincipalIds != null )
		{
			String msg;
			int total;
			int remaining;

			total = m_principalIds.size();
			remaining = m_listOfRemainingPrincipalIds.size();
			msg = GwtTeaming.getMessages().configuerUserMobileAppsDlgSaving(
																		String.valueOf( total-remaining ),
																		String.valueOf( total ) );
			showStatusMsg( msg );
		}
	}
	
	/**
	 * Loads the ConfigureUserMobileAppsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final int width,
							final int height,
							final ConfigureUserMobileAppsDlgClient cumaDlgClient )
	{
		GWT.runAsync( ConfigureUserMobileAppsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureUserMobileAppsDlg() );
				if ( cumaDlgClient != null )
				{
					cumaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureUserMobileAppsDlg cumaDlg;
				
				cumaDlg = new ConfigureUserMobileAppsDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cumaDlgClient.onSuccess( cumaDlg );
			}
		});
	}
}
