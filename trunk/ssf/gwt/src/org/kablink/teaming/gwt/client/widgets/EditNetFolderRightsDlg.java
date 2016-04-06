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


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserZoneShareSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.PerUserRightsInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * ?
 *  
 * @author jwootton
 */
public class EditNetFolderRightsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_allowAccessCkbox;
	private CheckBox m_canShareExternalCkbox;
	private CheckBox m_canShareInternalCkbox;
	private CheckBox m_canSharePublicCkbox;
	private CheckBox m_canShareLinkCkbox;
	private CheckBox m_canGrantReshareCkbox;
	private CheckBox m_canShareFolderExternalChkbox;
	private CheckBox m_canShareFolderInternalChkbox;
	private CheckBox m_canShareFolderPublicChkbox;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private PerUserRightsInfo m_rightsInfo;
	private PerEntityShareRightsInfo m_zoneShareRights;	// The rights the user has been given at the zone level.
	
	/**
	 * Callback interface to interact with the "Edit Net Folder Rights" dialog asynchronously after it loads. 
	 */
	public interface EditNetFolderRightsDlgClient
	{
		void onSuccess( EditNetFolderRightsDlg esrDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditNetFolderRightsDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editNetFolderRightsDlg_Caption(),
						this,
						null,
						null ); 
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		FlowPanel tmpPanel;
		Label label;
		ClickHandler clickHandler;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editNetFolderRightsDlg_MainPanel" );

		label = new Label( messages.editNetFolderRightsDlg_Instructions() );
		label.addStyleName( "editNetFolderRightsDlg_Instructions" );
		mainPanel.add( label );
		
		// Add a hint that the admin must set the appropriate rights in the Share Settings
		// dialog in order for the recipient to perform the share operations
		label = new Label( messages.editNetFolderRightsDlg_Hint() );
		label.addStyleName( "editNetFolderRightsDlg_Hint" );
		mainPanel.add( label );
		
		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				ScheduledCommand cmd;
				
				cmd = new ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						danceDlg();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};
		
		// Add the "Allow access to the net folder" checkbox
		m_allowAccessCkbox = new CheckBox( messages.editNetFolderRightsDlg_AllowAccessLabel() );
		m_allowAccessCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_allowAccessCkbox.addClickHandler( clickHandler ); 
		mainPanel.add( m_allowAccessCkbox );
		
		// Add the "Allow the recipient to share this item with:" label
		label = new Label( messages.editNetFolderRightsDlg_CanShareLabel() );
		label.addStyleName( "margintop2" );
		mainPanel.add( label );

		// Add the "allow share internal" checkbox.
		m_canShareInternalCkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareInternalLabel() );
		m_canShareInternalCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_canShareInternalCkbox.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareInternalCkbox );
		mainPanel.add( tmpPanel );
		
		// Add the "allow share external" checkbox.
		m_canShareExternalCkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareExternalLabel() );
		m_canShareExternalCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_canShareExternalCkbox.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareExternalCkbox );
		mainPanel.add( tmpPanel );

		// Add the "allow share public" checkbox.
		m_canSharePublicCkbox = new CheckBox( messages.editNetFolderRightsDlg_SharePublicLabel() );
		m_canSharePublicCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_canSharePublicCkbox.addClickHandler( new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						if ( m_canSharePublicCkbox.getValue() == true )
						{
							m_canShareInternalCkbox.setValue( Boolean.TRUE );
							m_canShareExternalCkbox.setValue( Boolean.TRUE );
						}
						
						danceDlg();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canSharePublicCkbox );
		mainPanel.add( tmpPanel );

		// Add the "Share using Filr links" checkbox.
		m_canShareLinkCkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareLinkLabel() );
		m_canShareLinkCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareLinkCkbox );
		mainPanel.add( tmpPanel );

		//Added new UI functionality to support netfolders sharing.
		// Add the "Allow the recipient to share this item with:" label
		label = new Label( messages.editNetFolderRightsDlg_CanShareFolderLabel() );
		label.addStyleName( "margintop2" );
		mainPanel.add( label );

		// Add the "allow share internal" checkbox.
		m_canShareFolderInternalChkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareInternalLabel() );
		m_canShareFolderInternalChkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_canShareFolderInternalChkbox.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareFolderInternalChkbox );
		mainPanel.add( tmpPanel );
		
		// Add the "allow share external" checkbox.
		m_canShareFolderExternalChkbox = new CheckBox( messages.editNetFolderRightsDlg_ShareExternalLabel() );
		m_canShareFolderExternalChkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_canShareFolderExternalChkbox.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareFolderExternalChkbox );
		mainPanel.add( tmpPanel );

		// Add the "allow share public" checkbox.
		m_canShareFolderPublicChkbox = new CheckBox( messages.editNetFolderRightsDlg_SharePublicLabel() );
		m_canShareFolderPublicChkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		m_canShareFolderPublicChkbox.addClickHandler( new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						if ( m_canShareFolderPublicChkbox.getValue() == true )
						{
							m_canShareFolderInternalChkbox.setValue( Boolean.TRUE );
							m_canShareFolderExternalChkbox.setValue( Boolean.TRUE );
						}
						
						danceDlg();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft1" );
		tmpPanel.add( m_canShareFolderPublicChkbox );
		mainPanel.add( tmpPanel );

		// Add the "allow grant re-share" checkbox
		m_canGrantReshareCkbox = new CheckBox( messages.editNetFolderRightsDlg_ReShareLabel() );
		m_canGrantReshareCkbox.addStyleName( "editNetFolderRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "margintop2" );
		tmpPanel.add( m_canGrantReshareCkbox );
		mainPanel.add( tmpPanel );

		return mainPanel;
	}

	/**
	 * 
	 */
	private void danceDlg()
	{
		boolean enable;
		
		enable = m_allowAccessCkbox.getValue();
		
		// Enable/disable the checkboxes depending on whether "allow access" is checked.
		m_canShareExternalCkbox.setEnabled( enable );
		m_canShareInternalCkbox.setEnabled( enable );
		m_canSharePublicCkbox.setEnabled( enable );
		m_canShareLinkCkbox.setEnabled( enable );
		m_canGrantReshareCkbox.setEnabled( enable );
		
		m_canShareFolderExternalChkbox.setEnabled( enable );
		m_canShareFolderInternalChkbox.setEnabled( enable );
		m_canShareFolderPublicChkbox.setEnabled( enable );
		
		// Enable/disable the "Allow the recipient to grant re-share privileges" checkbox 
		// depending on whether sharing is turned on for either internal, external or public.
		if ( enable )
		{
			enable = false;
			
			if ( m_canShareExternalCkbox.getValue() || m_canShareInternalCkbox.getValue() || m_canSharePublicCkbox.getValue() )
				enable = true;
			
			if ( m_canShareFolderExternalChkbox.getValue() || m_canShareFolderInternalChkbox.getValue() || m_canShareFolderPublicChkbox.getValue())
				enable = true;
			
			m_canGrantReshareCkbox.setEnabled( enable );
		}
		
		// If the "public" checkbox is checked, disable the "internal users"
		// and "external users" checkboxes
		if ( m_canSharePublicCkbox.getValue() == true )
		{
			m_canShareInternalCkbox.setEnabled( false );
			m_canShareExternalCkbox.setEnabled( false );
		}
		
		if ( m_canShareFolderPublicChkbox.getValue() == true ){
			m_canShareFolderInternalChkbox.setEnabled(false);
			m_canShareFolderExternalChkbox.setEnabled(false);
		}
		
		// Do we have the rights this user has been given at the zone level?
		if ( m_zoneShareRights != null )
		{
			// Yes.
			// Can the user reshare?
			if ( m_zoneShareRights.isAllowForwarding() == false )
			{
				// No
				m_canGrantReshareCkbox.setValue( Boolean.FALSE );
				m_canGrantReshareCkbox.setEnabled( false );
			}
			
			// Can the user share with internal users?
			if ( m_zoneShareRights.isAllowInternal() == false )
			{
				// No
				m_canShareInternalCkbox.setValue( Boolean.FALSE );
				m_canShareInternalCkbox.setEnabled( false );							
			}
			
			if( m_zoneShareRights.isAllowFolderInternal() == false ){
				m_canShareFolderInternalChkbox.setValue(Boolean.FALSE);
				m_canShareFolderInternalChkbox.setEnabled( false );				
			}
			
			// Can the user share with external users?
			if ( m_zoneShareRights.isAllowExternal() == false )
			{
				// No
				m_canShareExternalCkbox.setValue( Boolean.FALSE );
				m_canShareExternalCkbox.setEnabled( false );							
			}
			
			if ( m_zoneShareRights.isAllowFolderExternal() == false){
				m_canShareFolderExternalChkbox.setValue(Boolean.FALSE);
				m_canShareFolderExternalChkbox.setValue( false );				
			}
			
			// Can the user share with the public?
			if ( m_zoneShareRights.isAllowPublic() == false )
			{
				// No
				m_canSharePublicCkbox.setValue( Boolean.FALSE );
				m_canSharePublicCkbox.setEnabled( false );							
			}
			
			if(m_zoneShareRights.isAllowFolderPublic() == false ){
				m_canShareFolderPublicChkbox.setValue( Boolean.FALSE );
				m_canShareFolderPublicChkbox.setValue( false );				
			}
			
			// Can the user share using Filr links?
			if ( m_zoneShareRights.isAllowPublicLinks() == false )
			{
				// No
				m_canShareLinkCkbox.setValue( Boolean.FALSE );
				m_canShareLinkCkbox.setEnabled( false );
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		// Do we have a PerUserRightsInfo object we are working with?
		if ( m_rightsInfo != null )
		{
			// Yes
			m_rightsInfo.setCanAccess( m_allowAccessCkbox.getValue() );

			if ( m_allowAccessCkbox.getValue() )
			{
				boolean value;
				
				value = m_canShareExternalCkbox.getValue();
				m_rightsInfo.setCanShareExternal( value );
				
				value = m_canShareFolderExternalChkbox.getValue();
				m_rightsInfo.setCanShareFolderExternal( value );
				
				value = m_canShareInternalCkbox.getValue();
				m_rightsInfo.setCanShareInternal( value );
				
				value = m_canShareFolderInternalChkbox.getValue();
				m_rightsInfo.setCanShareFolderInternal( value );
				
				value = m_canSharePublicCkbox.getValue();
				m_rightsInfo.setCanSharePublic( value );
				
				value = m_canShareFolderPublicChkbox.getValue();
				m_rightsInfo.setCanShareFolderPublic( value );

				value = m_canShareLinkCkbox.getValue();
				m_rightsInfo.setCanSharePublicLink( value );
				
				value = false;
				if ( m_canGrantReshareCkbox.isEnabled() )
					value = m_canGrantReshareCkbox.getValue();
				m_rightsInfo.setCanReshare( value );
			}
			else
			{
				m_rightsInfo.setCanReshare( false );
				m_rightsInfo.setCanShareExternal( false );
				m_rightsInfo.setCanShareInternal( false );
				m_rightsInfo.setCanSharePublic( false );
				m_rightsInfo.setCanSharePublicLink( false );
				m_rightsInfo.setCanShareFolderExternal(false);
				m_rightsInfo.setCanShareFolderInternal(false);
				m_rightsInfo.setCanShareFolderPublic(false);
			}

			// Do we have a handler we should call?
			if ( m_editSuccessfulHandler != null )
				m_editSuccessfulHandler.editSuccessful( Boolean.TRUE );
		}

		return true;
	}

	/**
	 * Get the text entered by the user.
	 */
	@Override
	public Object getDataFromDlg()
	{
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
	 * Get the zone share settings for the given user.
	 */
	private void getZoneShareSettings( Long principalId )
	{
		GetUserZoneShareSettingsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				setOkEnabled( false );

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().editNetFolderRightsDlg_ErrorRetrievingZoneShareRights( caught.toString() );
				
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				if ( result.getResponseData() instanceof PerEntityShareRightsInfo )
				{
					ScheduledCommand cmd;

					m_zoneShareRights = (PerEntityShareRightsInfo) result.getResponseData();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							danceDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
	
					setOkEnabled( true );
				}
			}						
		};
		
		// Issue an rpc request to get the share rights this user has been given at the zone level.
		{
			cmd = new GetUserZoneShareSettingsCmd( principalId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		Long principalId,
		PerUserRightsInfo rightsInfo,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		m_rightsInfo = rightsInfo;
		m_editSuccessfulHandler = editSuccessfulHandler;

		m_canShareExternalCkbox.setValue( false );
		m_canShareInternalCkbox.setValue( false );
		m_canSharePublicCkbox.setValue( false );
		m_canShareLinkCkbox.setValue( false );
		m_canGrantReshareCkbox.setValue( false );
		m_allowAccessCkbox.setValue( false );
		
		m_canShareFolderInternalChkbox.setValue( false );
		m_canShareFolderExternalChkbox.setValue( false );
		m_canShareFolderPublicChkbox.setValue( false );

		if ( m_rightsInfo != null )
		{
			m_canShareExternalCkbox.setValue( m_rightsInfo.canShareExternal() );
			m_canShareInternalCkbox.setValue( m_rightsInfo.canShareInternal() );
			m_canSharePublicCkbox.setValue( m_rightsInfo.canSharePublic() );
			m_canShareLinkCkbox.setValue( m_rightsInfo.canSharePublicLink() );
			m_canGrantReshareCkbox.setValue( m_rightsInfo.canReshare() );
			m_allowAccessCkbox.setValue( m_rightsInfo.canAccess() );
			
			m_canShareFolderExternalChkbox.setValue( m_rightsInfo.canShareFolderExternal() );
			m_canShareFolderInternalChkbox.setValue( m_rightsInfo.canShareFolderInternal() );
			m_canShareFolderPublicChkbox.setValue( m_rightsInfo.canShareFolderPublic() );
		}
		
		// Get the zone share settings for the given user
		m_zoneShareRights = null;
		getZoneShareSettings( principalId );
	}
	
	/**
	 * Loads the EditNetFolderRightsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final EditNetFolderRightsDlgClient enfrDlgClient )
	{
		GWT.runAsync( EditNetFolderRightsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditNetFolderRightsDlg() );
				if ( enfrDlgClient != null )
				{
					enfrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditNetFolderRightsDlg enfrDlg;
				
				enfrDlg = new EditNetFolderRightsDlg( autoHide, modal );
				enfrDlgClient.onSuccess( enfrDlg );
			}
		});
	}
}
