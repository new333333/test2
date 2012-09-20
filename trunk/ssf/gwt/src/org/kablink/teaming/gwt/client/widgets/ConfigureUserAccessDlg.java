/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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


import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class ConfigureUserAccessDlg extends DlgBox
{
	CheckBox m_allowGuestAccessCkbox;
	CheckBox m_allowSelfRegOfInternalUserAccountCkbox;
	CheckBox m_allowExternalUserAccessCkbox;
	CheckBox m_allowSelfRegOfExternalUserAccountCkbox;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	

	/**
	 * Callback interface to interact with the "configure user access" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureUserAccessDlgClient
	{
		void onSuccess( ConfigureUserAccessDlg mnfDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private ConfigureUserAccessDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().configureUserAccessDlg_Header(), null, null, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add the "Allow Guest access" checkbox;
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_allowGuestAccessCkbox = new CheckBox( messages.configureUserAccessDlg_AllowGuestAccessLabel() );
			panel.add( m_allowGuestAccessCkbox );
			mainPanel.add( panel );
		}
		
		// Add the "Allow external user access" checkbox
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom1" );
			m_allowExternalUserAccessCkbox = new CheckBox( messages.configureUserAccessDlg_AllowExternalUserAccessLabel() );
			panel.add( m_allowExternalUserAccessCkbox );
			mainPanel.add( panel );
		}
		
		// Add the "Allow external user to self register" checkbox
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			panel.addStyleName( "marginleft2" );
			m_allowSelfRegOfExternalUserAccountCkbox = new CheckBox( messages.configureUserAccessDlg_AllowSelfRegExternalUserAccountLabel() );
			panel.add( m_allowSelfRegOfExternalUserAccountCkbox );
			mainPanel.add( panel );
		}
		
		return mainPanel;
	}
	

	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what since we only have a close button.
		return Boolean.TRUE;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_allowGuestAccessCkbox;
	}
	
	/**
	 * Issue an rpc request to get the user access information from the server.
	 */
	private void getUserAccessInfoFromServer()
	{
		
	}
	
	/**
	 * 
	 */
	public void init()
	{
		m_allowExternalUserAccessCkbox.setValue( false );
		m_allowGuestAccessCkbox.setValue( false );
		m_allowSelfRegOfExternalUserAccountCkbox.setValue( false );
		m_allowSelfRegOfInternalUserAccountCkbox.setValue( false );
		
		// Issue an rpc request to get the user access information from the server
		getUserAccessInfoFromServer();
	}
	
	/**
	 * Loads the ConfigureUserAccessDlg split point and returns an instance
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
							final ConfigureUserAccessDlgClient cuaDlgClient )
	{
		GWT.runAsync( ConfigureUserAccessDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureUserAccessDlg() );
				if ( cuaDlgClient != null )
				{
					cuaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureUserAccessDlg cuaDlg;
				
				cuaDlg = new ConfigureUserAccessDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cuaDlgClient.onSuccess( cuaDlg );
			}
		});
	}
}
