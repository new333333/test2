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

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class LoginDlg extends DlgBox
{
	private FormPanel m_formPanel = null;
	private TextBox m_userIdTxtBox = null;
	private Button m_okBtn = null;
	private Button m_cancelBtn = null;
	private Label m_loginFailedMsg = null;
	private Label m_authenticatingMsg = null;
	private String m_loginUrl = null;
	private String m_springSecurityRedirect = null;	// This values tells Teaming what url to go to after the user authenticates.
	
	/**
	 * 
	 */
	public LoginDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Object properties,
		String loginUrl,	// The url we should use when we post the request to log in.
		String springSecurityRedirect )
	{
		super( autoHide, modal, xPos, yPos );
	
		String headerText;
		
		m_loginUrl = loginUrl;
		m_springSecurityRedirect = springSecurityRedirect;
		
		// Create the header, content and footer of this dialog box.
		if ( GwtMainPage.m_novellTeaming )
			headerText = GwtTeaming.getMessages().loginDlgNovellHeader();
		else
			headerText = GwtTeaming.getMessages().loginDlgKablinkHeader();
		
		createAllDlgContent( headerText, null, null, properties ); 
	}// end LoginDlg()
	

	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent()
	{
	}// end clearContent()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		FlexTable table;
		int row = 0;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_formPanel = new FormPanel( "" );
		m_formPanel.setAction( m_loginUrl );
		m_formPanel.setMethod( FormPanel.METHOD_POST );
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		// Add a row for the "user id" controls.
		{
			table.setText( row, 0, GwtTeaming.getMessages().loginDlgUserId() );
			
			m_userIdTxtBox = new TextBox();
			m_userIdTxtBox.setName( "j_username" );
			m_userIdTxtBox.setVisibleLength( 20 );
			table.setWidget( row, 1, m_userIdTxtBox );
			
			++row;
		}
		
		// Add a row for the "password" controls.
		{
			PasswordTextBox pwdTxtBox;
			
			table.setText( row, 0, GwtTeaming.getMessages().loginDlgPassword() );
			
			pwdTxtBox = new PasswordTextBox();
			pwdTxtBox.setName( "j_password" );
			pwdTxtBox.setVisibleLength( 20 );
			table.setWidget( row, 1, pwdTxtBox );
			
			++row;
		}
		
		// Add a "login failed" label to the dialog.
		{
			FlexTable.FlexCellFormatter cellFormatter;

			m_loginFailedMsg = new Label( GwtTeaming.getMessages().loginDlgLoginFailed() );
			m_loginFailedMsg.addStyleName( "loginFailedMsg" );
			m_loginFailedMsg.setVisible( false );
			
			cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setHorizontalAlignment( row, 0, HasHorizontalAlignment.ALIGN_RIGHT );
			table.setWidget( row, 1, m_loginFailedMsg );
			++row;
		}
		
		// Add a "Authenticating..." label to the dialog.
		{
			FlexTable.FlexCellFormatter cellFormatter;

			m_authenticatingMsg = new Label( GwtTeaming.getMessages().loginDlgAuthenticating() );
			m_authenticatingMsg.addStyleName( "loginAuthenticatingMsg" );
			m_authenticatingMsg.setVisible( false );
			
			cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setHorizontalAlignment( row, 0, HasHorizontalAlignment.ALIGN_RIGHT );
			table.setWidget( row, 1, m_authenticatingMsg );
			++row;
		}
		
		// Add a hidden input for the "spring security redirect" which tells Teaming what page to go
		// to after the user authenticates.
		if ( m_springSecurityRedirect != null && m_springSecurityRedirect.length() > 0 )
		{
			Hidden hiddenInput;
			
			hiddenInput = new Hidden();
			hiddenInput.setName( "spring-security-redirect" );
			hiddenInput.setValue( m_springSecurityRedirect );
			
			table.setWidget( row, 0, hiddenInput );
		}
		
		m_formPanel.add( table );
		
		mainPanel.add( m_formPanel );

		init( props );

		return mainPanel;
	}// end createContent()
	
	
	/*
	 * Override the createFooter() method so we can control what buttons are in the footer.
	 */
	public Panel createFooter()
	{
		FlowPanel panel;
		
		panel = new FlowPanel();
		
		// Associate this panel with its stylesheet.
		panel.setStyleName( "teamingDlgBoxFooter" );
		
		m_okBtn = new Button( GwtTeaming.getMessages().ok() );
		m_okBtn.addClickHandler( this );
		m_okBtn.addStyleName( "teamingButton" );
		panel.add( m_okBtn );
		
		m_cancelBtn = new Button( GwtTeaming.getMessages().cancel() );
		m_cancelBtn.addClickHandler( this );
		m_cancelBtn.addStyleName( "teamingButton" );
		m_cancelBtn.setVisible( false );
		panel.add( m_cancelBtn );
		
		return panel;
	}// end createFooter()
	
	
	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	public Panel createHeader( String caption )
	{
		ImageResource imageResource;
		Image img;
		FlowPanel panel;
		
		panel = new FlowPanel();

		// Are we running Novell Teaming?
		if ( GwtMainPage.m_requestInfo.isNovellTeaming() )
		{
			// Yes
			// Create a Novell Teaming image that will be used in case there is no branding.
			imageResource = GwtTeaming.getImageBundle().mastHeadNovellGraphic();
		}
		else
		{
			// No
			// Create a Kablink Teaming image that will be used in case there is no branding.
			imageResource = GwtTeaming.getImageBundle().mastHeadKablinkGraphic();
		}
	
		img = new Image( imageResource );
		img.setHeight( "75" );

		panel.add( img );
		
		return panel;
	}// end createHeader()
	
	
	/**
	 * 
	 */
	public Object getDataFromDlg()
	{
		// Nothing to do.
		return new Object();
	}// end getDataFromDlg()
	
	
	/**
	 *  
	 */
	public FocusWidget getFocusWidget()
	{
		return m_userIdTxtBox;
	}// end getFocusWidget()
	
	
	/**
	 * 
	 */
	public void hideAuthenticatingMsg()
	{
		m_authenticatingMsg.setVisible( false );
	}// end hideAuthenticatingMsg()

	
	/**
	 * 
	 */
	public void hideLoginFailedMsg()
	{
		m_loginFailedMsg.setVisible( false );
	}// end hideLoginFailedMsg()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given object.
	 * Currently there is nothing to initialize.
	 */
	public void init( Object props )
	{
	}// end init()

	
	/*
	 * This method gets called when the user clicks on a button in the footer.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;
		
		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on ok?
		if ( source == m_okBtn )
		{
			// Yes
			// Hide the "login failed" message.
			hideLoginFailedMsg();
			
			// Show the the "authenticating..." message.
			showAuthenticatingMsg();
			
			// Submit the form requesting authentication.
			m_formPanel.submit();
			
			return;
		}
		
		if ( source == m_cancelBtn )
		{
			hide();
			return;
		}
	}// end onClick()

	
	/**
	 * Should we allow the user to cancel the login dialog.
	 */
	public void setAllowCancel( boolean allowCancel )
	{
		m_cancelBtn.setVisible( allowCancel );
	}// end setAllowCancel()
	
	
	/**
	 * 
	 */
	public void showAuthenticatingMsg()
	{
		m_authenticatingMsg.setVisible( true );
	}// end showAuthenticatingMsg()

	
	/**
	 * 
	 */
	public void showLoginFailedMsg()
	{
		m_loginFailedMsg.setVisible( true );
	}// end hideLoginFailedMsg()
	
}// end LoginDlg
