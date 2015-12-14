/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 * 
 * @author jwootton
 */
public class PromptForExternalUsersEmailAddressDlg extends DlgBox
{
	private TextBox m_emailAddressesTB;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	

	/**
	 * Callback interface to interact with the "Prompt for external users email address" dialog
	 * asynchronously after it loads. 
	 */
	public interface PromptForExternalUsersEmailAddressDlgClient
	{
		void onSuccess( PromptForExternalUsersEmailAddressDlg dlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private PromptForExternalUsersEmailAddressDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.OkCancel );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().PromptForExternalUsersEmailAddressDlg_Header(),
						editSuccessfulHandler,
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
		FlowPanel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add a hint
		{
			Label label;
			
			label = new Label( messages.PromptForExternalUsersEmailAddressDlg_Hint() );
			label.addStyleName( "promptForExternalUsersEmailAddressDlg_Hint" );
			mainPanel.add( label );
		}
		
		m_emailAddressesTB = new TextBox();
		m_emailAddressesTB.setVisibleLength( 50 );
		mainPanel.add( m_emailAddressesTB );

		return mainPanel;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		String emailAddresses = null;
		
		emailAddresses = m_emailAddressesTB.getValue();
		if ( emailAddresses == null || emailAddresses.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().PromptForExternalUsersEmailAddressDlg_NoEmailAddressesEntered() );
			m_emailAddressesTB.setFocus( true );
			return null;
		}
		
		return emailAddresses;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_emailAddressesTB;
	}
	
	/**
	 * 
	 */
	public void init()
	{
		hideStatusMsg();
		setOkEnabled( true );
		m_emailAddressesTB.setValue( "" );
	}
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final Integer left,
		final Integer top,
		final EditSuccessfulHandler editSuccessfulHandler,
		final PromptForExternalUsersEmailAddressDlgClient dlgClient )
	{
		GWT.runAsync( PromptForExternalUsersEmailAddressDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				if ( dlgClient != null )
				{
					dlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				PromptForExternalUsersEmailAddressDlg dlg;
				
				dlg = new PromptForExternalUsersEmailAddressDlg(
													autoHide,
													modal,
													left,
													top,
													editSuccessfulHandler );
				
				if ( dlgClient != null )
					dlgClient.onSuccess( dlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final PromptForExternalUsersEmailAddressDlg dlg,
		final Integer left,
		final Integer top,
		final PromptForExternalUsersEmailAddressDlgClient dlgClient )
	{
		GWT.runAsync( PromptForExternalUsersEmailAddressDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				if ( dlgClient != null )
				{
					dlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.init();
				
				if ( left != null && top != null )
					dlg.setPopupPosition( left, top );
				
				dlg.show();
			}
		} );
	}
}
