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
import org.kablink.teaming.gwt.client.util.PerUserZoneShareRightsInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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
public class EditUserZoneShareRightsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_canShareForwardCkbox;
	private CheckBox m_canShareExternalCkbox;
	private CheckBox m_canShareInternalCkbox;
	private CheckBox m_canSharePublicCkbox;
	private CheckBox m_canShareWithAllInternalCkbox;
	private CheckBox m_canShareLinkCkbox;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private PerUserZoneShareRightsInfo m_rightsInfo;
	
	/**
	 * Callback interface to interact with the "Edit Zone Share Rights" dialog asynchronously after it loads. 
	 */
	public interface EditUserZoneShareRightsDlgClient
	{
		void onSuccess( EditUserZoneShareRightsDlg ezsrDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditUserZoneShareRightsDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editUserZoneShareRightsDlg_Caption(),
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
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "editZoneShareRightsDlg_MainPanel" );

		label = new Label( messages.editUserZoneShareRightsDlg_Instructions() );
		label.addStyleName( "editZoneShareRightsDlg_Instructions" );
		mainPanel.add( label );
		
		// Add the "Re-share" checkbox
		m_canShareForwardCkbox = new CheckBox( messages.editUserZoneShareRightsDlg_ReShareLabel() );
		m_canShareForwardCkbox.addStyleName( "editZoneShareRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareForwardCkbox );
		mainPanel.add( tmpPanel );
		
		// Add the "can share internal" checkbox.
		m_canShareInternalCkbox = new CheckBox( messages.editUserZoneShareRightsDlg_ShareInternalLabel() );
		m_canShareInternalCkbox.addStyleName( "editZoneShareRightsDlg_RightsCkbox" );
		m_canShareInternalCkbox.addClickHandler( new ClickHandler()
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
		} );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareInternalCkbox );
		mainPanel.add( tmpPanel );
		
		// Add the "can share with all internal users" checkbox.
		m_canShareWithAllInternalCkbox = new CheckBox( messages.editUserZoneShareRightsDlg_ShareWithAllInternalUsersLabel() );
		m_canShareWithAllInternalCkbox.addStyleName( "editZoneShareRightsDlg_RightsCkbox" );
		m_canShareWithAllInternalCkbox.addStyleName( "marginleft2" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareWithAllInternalCkbox );
		mainPanel.add( tmpPanel );

		// Add the "can share external" checkbox.
		m_canShareExternalCkbox = new CheckBox( messages.editUserZoneShareRightsDlg_ShareExternalLabel() );
		m_canShareExternalCkbox.addStyleName( "editZoneShareRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareExternalCkbox );
		mainPanel.add( tmpPanel );

		// Add the "can share public" checkbox.
		m_canSharePublicCkbox = new CheckBox( messages.editUserZoneShareRightsDlg_SharePublicLabel() );
		m_canSharePublicCkbox.addStyleName( "editZoneShareRightsDlg_RightsCkbox" );
		m_canSharePublicCkbox.addClickHandler( new ClickHandler()
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
		} );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canSharePublicCkbox );
		mainPanel.add( tmpPanel );

		// Add the "Share using links" checkbox.
		m_canShareLinkCkbox = new CheckBox( messages.editUserZoneShareRightsDlg_ShareLinkLabel() );
		m_canShareLinkCkbox.addStyleName( "editZoneShareRightsDlg_RightsCkbox" );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_canShareLinkCkbox );
		mainPanel.add( tmpPanel );

		return mainPanel;
	}

	/**
	 * Enable disable checkboxes depending on whether "share internal" and "share external" is set
	 */
	private void danceDlg()
	{
		// Is the "share with public" checkbox checked?
		if ( m_canSharePublicCkbox.getValue() == true )
		{
			// Yes,
			// Check and disable "share with internal", "share with all internal users group" and "share with external users"
			m_canShareInternalCkbox.setValue( true );
			m_canShareInternalCkbox.setEnabled( false );
			m_canShareWithAllInternalCkbox.setValue( true );
			m_canShareWithAllInternalCkbox.setEnabled( false );
			m_canShareExternalCkbox.setValue( true );
			m_canShareExternalCkbox.setEnabled( false );
		}
		else
		{
			boolean enable;
			
			enable = m_canShareInternalCkbox.getValue();
			if ( enable == false )
				m_canShareWithAllInternalCkbox.setValue( false );
			m_canShareWithAllInternalCkbox.setEnabled( enable );
			
			m_canShareInternalCkbox.setEnabled( true );
			m_canShareExternalCkbox.setEnabled( true );
		}
	}

	/**
	 * 
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		// Do we have a PerUserZoneShareRightsInfo object we are working with?
		if ( m_rightsInfo != null )
		{
			// Yes
			m_rightsInfo.setEnableShareForwarding( m_canShareForwardCkbox.getValue() );
			m_rightsInfo.setEnableShareExternal( m_canShareExternalCkbox.getValue() );
			m_rightsInfo.setEnableShareInternal( m_canShareInternalCkbox.getValue() );
			m_rightsInfo.setEnableSharePublic( m_canSharePublicCkbox.getValue() );
			m_rightsInfo.setEnableShareWithAllInternal( m_canShareWithAllInternalCkbox.getValue() );
			m_rightsInfo.setEnableShareLink( m_canShareLinkCkbox.getValue() );

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
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init(
		PerUserZoneShareRightsInfo rightsInfo,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		m_rightsInfo = rightsInfo;
		m_editSuccessfulHandler = editSuccessfulHandler;

		m_canShareExternalCkbox.setValue( false );
		m_canShareForwardCkbox.setValue( false );
		m_canShareInternalCkbox.setValue( false );
		m_canSharePublicCkbox.setValue( false );
		m_canShareWithAllInternalCkbox.setValue( false );
		m_canShareLinkCkbox.setValue( false );

		if ( m_rightsInfo != null )
		{
			m_canShareExternalCkbox.setValue( m_rightsInfo.getIsEnableShareExternal() );
			m_canShareForwardCkbox.setValue( m_rightsInfo.getIsEnableShareForwarding() );
			m_canShareInternalCkbox.setValue( m_rightsInfo.getIsEnableShareInternal() );
			m_canSharePublicCkbox.setValue( m_rightsInfo.getIsEnableSharePublic() );
			m_canShareWithAllInternalCkbox.setValue( m_rightsInfo.getIsEnableShareWithAllInternal() );
			m_canShareLinkCkbox.setValue( m_rightsInfo.getIsEnableShareLink() );
		}

		danceDlg();
	}
	
	/**
	 * Loads the EditZoneShareRightsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final EditUserZoneShareRightsDlgClient ezsrDlgClient )
	{
		GWT.runAsync( EditUserZoneShareRightsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditUserZoneShareRightsDlg() );
				if ( ezsrDlgClient != null )
				{
					ezsrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditUserZoneShareRightsDlg ezsrDlg;
				
				ezsrDlg = new EditUserZoneShareRightsDlg( autoHide, modal );
				ezsrDlgClient.onSuccess( ezsrDlg );
			}
		});
	}
}
