/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;


/**
 * 
 * @author jwootton
 *
 */
public class LoginDlg extends DlgBox
	implements BlurHandler, FocusHandler, KeyUpHandler
{
	private FormPanel m_formPanel = null;
	private TextBox m_userIdTxtBox = null;
	private PasswordTextBox m_pwdTxtBox = null;
	private HandlerRegistration m_userIdKeyUpHandlerReg = null;
	private HandlerRegistration m_pwdKeyUpHandlerReg = null;
	private Button m_okBtn = null;
	private Button m_cancelBtn = null;
	private Label m_loginFailedMsg = null;
	private Label m_authenticatingMsg = null;
	private String m_loginUrl = null;
	private String m_springSecurityRedirect = null;	// This values tells Teaming what url to go to after the user authenticates.
	private GwtSelfRegistrationInfo m_selfRegInfo = null;
	private InlineLabel m_selfRegLink = null;
	private AsyncCallback<GwtSelfRegistrationInfo> m_rpcGetSelfRegInfoCallback = null;
	private int m_submitCount;

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
		
		// Create the callback that will be used when we issue an ajax call to get self-registration information
		m_rpcGetSelfRegInfoCallback = new AsyncCallback<GwtSelfRegistrationInfo>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetSelfRegInfo());
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtSelfRegistrationInfo selfRegInfo )
			{
				m_selfRegInfo = selfRegInfo;
				
				// Hide or show the self registration controls.
				updateSelfRegistrationControls( selfRegInfo );
			}// end onSuccess()
		};

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
		FlexTable.FlexCellFormatter cellFormatter;
		int row = 0;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_formPanel = new FormPanel( "" );
		m_formPanel.setAction( m_loginUrl );
		m_formPanel.setMethod( FormPanel.METHOD_POST );
		DOM.setElementAttribute( m_formPanel.getElement(), "autocomplete", "off" );
		
		// Chrome will submit the form when the user presses enter.  Our key-up handler will also
		// submit the form when we see the enter key.  Add an addSubmitHandler() to prevent
		// the form from being submitted twice.
		m_submitCount = 0;
		m_formPanel.addSubmitHandler( new FormPanel.SubmitHandler()
		{
			public void onSubmit(SubmitEvent event)
			{
				++m_submitCount;
				if ( m_submitCount > 1 )
				{
					event.cancel();
				}
			}
		});
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		cellFormatter = table.getFlexCellFormatter();

		// Add a row for the "user id" controls.
		{
			table.setText( row, 0, GwtTeaming.getMessages().loginDlgUserId() );
			
			m_userIdTxtBox = new TextBox();
			m_userIdTxtBox.setName( "j_username" );
			m_userIdTxtBox.addFocusHandler( this );
			m_userIdTxtBox.addBlurHandler( this );
			m_userIdTxtBox.setVisibleLength( 20 );
			table.setWidget( row, 1, m_userIdTxtBox );
			
			++row;
		}
		
		// Add a row for the "password" controls.
		{
			table.setText( row, 0, GwtTeaming.getMessages().loginDlgPassword() );
			
			m_pwdTxtBox = new PasswordTextBox();
			m_pwdTxtBox.setName( "j_password" );
			m_pwdTxtBox.addFocusHandler( this );
			m_pwdTxtBox.addBlurHandler( this );
			m_pwdTxtBox.setVisibleLength( 20 );
			table.setWidget( row, 1, m_pwdTxtBox );
			
			++row;
		}
		
		// Add a "login failed" label to the dialog.
		{
			m_loginFailedMsg = new Label( GwtTeaming.getMessages().loginDlgLoginFailed() );
			m_loginFailedMsg.addStyleName( "loginFailedMsg" );
			m_loginFailedMsg.setVisible( false );
			
			cellFormatter.setHorizontalAlignment( row, 0, HasHorizontalAlignment.ALIGN_RIGHT );
			table.setWidget( row, 1, m_loginFailedMsg );
			++row;
		}
		
		// Add a "Authenticating..." label to the dialog.
		{
			m_authenticatingMsg = new Label( GwtTeaming.getMessages().loginDlgAuthenticating() );
			m_authenticatingMsg.addStyleName( "loginAuthenticatingMsg" );
			m_authenticatingMsg.setVisible( false );
			
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
			++row;
		}
		
		m_formPanel.add( table );
		
		// Create a "Create new account" link that will initially be hidden.
		// We will show this link later when we get the response to our request to get
		// self registration info and self registration is allowed.
		{
			ClickHandler clickHandler;
			MouseOverHandler mouseOverHandler;
			MouseOutHandler mouseOutHandler;
			
			m_selfRegLink = new InlineLabel( GwtTeaming.getMessages().loginDlgCreateNewAccount() );
			m_selfRegLink.addStyleName( "margintop3" );
			m_selfRegLink.addStyleName( "selfRegLink1" );
			m_selfRegLink.addStyleName( "selfRegLink2" );
			m_selfRegLink.addStyleName( "subhead-control-bg1" );
			m_selfRegLink.addStyleName( "roundcornerSM" );
			m_selfRegLink.setVisible( false );
			
			// Add a clickhandler to the "Create new account" link.  When the user clicks on the link we
			// will invoke the "Create user" page.
			clickHandler = new ClickHandler()
			{
				/**
				 * Clear all branding information.
				 */
				public void onClick( ClickEvent event )
				{
					String url;
					
					// Get the url we need to invoke the "Create User" page.
					url = m_selfRegInfo.getCreateUserUrl();
					
					// Invoke the "Create User" page in a new window.
					Window.open( url, "self_reg_create_new_account", "height=750,resizeable,scrollbars,width=750" );
				}//end onClick()
			};
			m_selfRegLink.addClickHandler( clickHandler );
			
			// Add a mouse-over handler
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				public void onMouseOver( MouseOverEvent event )
				{
					Widget widget;
					
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg1" );
					widget.addStyleName( "subhead-control-bg2" );
				}// end onMouseOver()
			};
			m_selfRegLink.addMouseOverHandler( mouseOverHandler );

			// Add a mouse-out handler
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				public void onMouseOut( MouseOutEvent event )
				{
					Widget widget;
					
					// Remove the background color we added to the anchor when the user moved the mouse over the anchor.
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg2" );
					widget.addStyleName( "subhead-control-bg1" );
				}// end onMouseOut()
			};
			m_selfRegLink.addMouseOutHandler( mouseOutHandler );
			
			cellFormatter.setColSpan( row, 0, 2 );
			table.setWidget( row, 0, m_selfRegLink );
			++row;
		}
		
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
		Image img;
		FlowPanel panel;
		
		panel = new FlowPanel();

		// Are we running Novell Teaming?
		if ( GwtMainPage.m_requestInfo.isNovellTeaming() )
		{
			// Yes
			// Create a Novell Teaming image that will be used in case there is no branding.
			img = new Image( GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/novell_graphic.png" );
		}
		else
		{
			// No
			// Create a Kablink Teaming image that will be used in case there is no branding.
			img = new Image( GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/kablink_graphic.png" );
		}
	
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
	 * Issue an ajax request to get self registration info from the server.
	 */
	private void getSelfRegistrationInfoFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		rpcService = GwtTeaming.getRpcService();
		
		// Issue an ajax request to get self registration information
		rpcService.getSelfRegistrationInfo( HttpRequestInfo.createHttpRequestInfo(), m_rpcGetSelfRegInfoCallback );
	}// end getAdminActionsFromServer()
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

	
	/**
	 * This method gets called when the user id field or the password field loses the focus.
	 * For some browsers if the user types in the browser's address bar and presses enter we
	 * still see the key up.
	 */
	public void onBlur( BlurEvent blurEvent )
	{
		Object src;
		
		src = blurEvent.getSource();
		if ( src == m_userIdTxtBox )
		{
			// Remove the key up handler from the user id field.
			if ( m_userIdKeyUpHandlerReg != null )
			{
				m_userIdKeyUpHandlerReg.removeHandler();
				m_userIdKeyUpHandlerReg = null;
			}
		}
		else if ( src == m_pwdTxtBox )
		{
			// Remove the key up handler from the password field.
			if ( m_pwdKeyUpHandlerReg != null )
			{
				m_pwdKeyUpHandlerReg.removeHandler();
				m_pwdKeyUpHandlerReg = null;
			}
		}
	}
	
	
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
			submitLoginRequest();
			return;
		}
		
		if ( source == m_cancelBtn )
		{
			hide();
			return;
		}
	}// end onClick()
	

	/**
	 * This method gets called when the user id field or the password field get the focus.
	 */
	public void onFocus( FocusEvent focusEvent )
	{
		Object src;
		
		src = focusEvent.getSource();
		if ( src == m_userIdTxtBox )
		{
			// Add the key up handler to the user id field.
			m_userIdKeyUpHandlerReg = m_userIdTxtBox.addKeyUpHandler( this );
		}
		else if ( src == m_pwdTxtBox )
		{
			// Add the key up handler to the password field.
			m_pwdKeyUpHandlerReg = m_pwdTxtBox.addKeyUpHandler( this );
		}
	}
	
	
	/**
	 * This method gets called when the user presses a key in the "user id" field or in the "password" field.
	 * If the user pressed the "enter" key we will submit the form.
	 */
	public void onKeyUp( KeyUpEvent event )
	{
		// Did the user press enter?
		if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER )
		{
			// Yes
			submitLoginRequest();
		}
	}// end onKeyPress()

	
	/**
	 * Should we allow the user to cancel the login dialog.
	 */
	public void setAllowCancel( boolean allowCancel )
	{
		m_cancelBtn.setVisible( allowCancel );
	}// end setAllowCancel()
	
	
	/**
	 * Override this method so we can issue an ajax request to get information about self
	 * registration every time this dialog is invoked.
	 */
	public void show()
	{
		Command cmd;
		
		super.show();
		
		m_submitCount = 0;
		
		cmd = new Command()
		{
			public void execute()
			{
				// Issue an ajax request to get self registration info
				getSelfRegistrationInfoFromServer();
			}
		};
		DeferredCommand.addCommand( cmd );
		
	}// end show()
	
	
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

	
	/**
	 * Submit the form to do the login request.
	 */
	private void submitLoginRequest()
	{
		Command cmd;
		
		cmd = new Command()
		{
			public void execute()
			{
				// Hide the "login failed" message.
				hideLoginFailedMsg();
				
				// Show the the "authenticating..." message.
				showAuthenticatingMsg();
				
				// Submit the form requesting authentication.
				m_formPanel.submit();
			}
		};
		DeferredCommand.addCommand( cmd );
	}// end submitLoginRequest()
	
	
	/**
	 * Show or hide the controls dealing with self registration depending on the values in
	 * the given GwtSelfRegistrationInfo object.
	 */
	private void updateSelfRegistrationControls( GwtSelfRegistrationInfo selfRegInfo )
	{
		String url;
		
		// Is self registration allowed?
		url = selfRegInfo.getCreateUserUrl();
		if ( selfRegInfo.isSelfRegistrationAllowed() && url != null && url.length() > 0 )
		{
			// Yes, show the self registration button.
			m_selfRegLink.setVisible( true );
		}
		else
		{
			// No, hide the self registration button.
			m_selfRegLink.setVisible( false );
		}
	}// end updateSelfRegistrationControls()
}// end LoginDlg
