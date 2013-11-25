/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetLoginInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;


/**
 * 
 * @author jwootton
 *
 */
@SuppressWarnings("unused")
public class LoginDlg extends DlgBox
{
	private FormPanel m_formPanel = null;
	private TextBox m_userIdTxtBox = null;
	private Button m_cancelBtn = null;
	private Label m_loginFailedMsg = null;
	private Label m_authenticatingMsg = null;
	private String m_loginUrl = null;
	private String m_springSecurityRedirect = null;	// This values tells Teaming what url to go to after the user authenticates.
	private GwtSelfRegistrationInfo m_selfRegInfo = null;
	private InlineLabel m_selfRegLink = null;
	private AsyncCallback<VibeRpcResponse> m_rpcGetLoginInfoCallback = null;

	/**
	 * 
	 */
	private class LoginFormPanel extends FormPanel
	{
		/**
		 * 
		 */
		public LoginFormPanel( Element formElement )
		{
			super( formElement );
		}
	}
	
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private LoginDlg(
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
		
		// Create the callback that will be used when we issue an ajax call to get login information
		m_rpcGetLoginInfoCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				// Don't call GwtClientHelper.handleGwtRPCFailure() like we would normally do.  If the
				// session has expired, handleGwtRPCFailure() will invoke this login dialog again
				// and we will be in an infinite loop.
				// GwtClientHelper.handleGwtRPCFailure(
				//	t,
				//	GwtTeaming.getMessages().rpcFailure_GetSelfRegInfo());
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				GwtLoginInfo loginInfo;
				
				loginInfo = (GwtLoginInfo) response.getResponseData();
				
				m_selfRegInfo = loginInfo.getSelfRegistrationInfo();
				
				// Hide or show the self registration controls.
				updateSelfRegistrationControls( m_selfRegInfo );

				// Turn auto complete on/off.
				if ( loginInfo.getAllowAutoComplete() == true )
					DOM.removeElementAttribute( m_formPanel.getElement(), "autocomplete" );
				else
					DOM.setElementAttribute( m_formPanel.getElement(), "autocomplete", "off" );
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
		Element formElement;
		
		mainPanel = new FlowPanel();
		
		// Get the <form ...> element that was created by GwtMainPage.jsp
		formElement = Document.get().getElementById( "loginFormId" );
		m_formPanel = new LoginFormPanel( formElement );
		m_formPanel.setVisible( true );
		
		m_formPanel.addSubmitHandler( new FormPanel.SubmitHandler()
		{
			/**
			 * 
			 */
			public void onSubmit(SubmitEvent event)
			{
				// Hide the "login failed" message.
				hideLoginFailedMsg();
				
				// Show the the "authenticating..." message.
				showAuthenticatingMsg();
			}
		});
		
		// Add a row for the "user id" controls.
		{
			Element userIdLabelElement;
			Element userIdElement;
			
			userIdLabelElement = Document.get().getElementById( "userIdLabel" );
			userIdLabelElement.setInnerText( GwtTeaming.getMessages().loginDlgUserId() );
			
			userIdElement = Document.get().getElementById( "j_usernameId" );
			m_userIdTxtBox = TextBox.wrap( userIdElement );
		}
		
		// Add a row for the "password" controls.
		{
			Element pwdLabelElement;
			
			pwdLabelElement = Document.get().getElementById( "pwdLabel" );
			pwdLabelElement.setInnerText( GwtTeaming.getMessages().loginDlgPassword() );
		}
		
		// Add a "login failed" label to the dialog.
		{
			Element loginFailedElement;
			
			loginFailedElement = Document.get().getElementById( "loginFailedMsgDiv" );
			loginFailedElement.setInnerText( GwtTeaming.getMessages().loginDlgLoginFailed() );
			m_loginFailedMsg = Label.wrap( loginFailedElement );
			m_loginFailedMsg.setVisible( false );
		}
		
		// Add a "Authenticating..." label to the dialog.
		{
			Element authenticatingElement;
			
			authenticatingElement = Document.get().getElementById( "authenticatingDiv" );
			authenticatingElement.setInnerText( GwtTeaming.getMessages().loginDlgAuthenticating() );
			m_authenticatingMsg = Label.wrap( authenticatingElement );
			m_authenticatingMsg.setVisible( false );
		}
		
		// Add a hidden input for the "spring security redirect" which tells Teaming what page to go
		// to after the user authenticates.
		if ( m_springSecurityRedirect != null && m_springSecurityRedirect.length() > 0 )
		{
			Hidden hiddenInput;
			
			hiddenInput = new Hidden();
			hiddenInput.setName( "spring-security-redirect" );
			hiddenInput.setValue( m_springSecurityRedirect );
			m_formPanel.add( hiddenInput );
		}
		
		// Create the ok and cancel buttons
		{
			Element okElement;
			Element cancelElement;
			Button okBtn;
			
			okElement = Document.get().getElementById( "loginOkBtn" );
			okBtn = Button.wrap( okElement );
			okBtn.setText( GwtTeaming.getMessages().ok() );
			
			cancelElement = Document.get().getElementById( "loginCancelBtn" );
			m_cancelBtn = Button.wrap( cancelElement );
			m_cancelBtn.setText( GwtTeaming.getMessages().cancel() );
			m_cancelBtn.addClickHandler( this );
			m_cancelBtn.setVisible( false );
		}
		
		// Create a "Create new account" link that will initially be hidden.
		// We will show this link later when we get the response to our request to get
		// self registration info and self registration is allowed.
		{
			ClickHandler clickHandler;
			MouseOverHandler mouseOverHandler;
			MouseOutHandler mouseOutHandler;
			Element selfRegElement;
			
			selfRegElement = Document.get().getElementById( "createNewAccountSpan" );
			selfRegElement.setInnerText( GwtTeaming.getMessages().loginDlgCreateNewAccount() );
			m_selfRegLink = InlineLabel.wrap( selfRegElement );
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
		return null;
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
	 * Issue an ajax request to get login info from the server.
	 */
	private void getLoginInfoFromServer()
	{
		GetLoginInfoCmd cmd;
		
		// Issue an ajax request to get login information
		cmd = new GetLoginInfoCmd();
		GwtClientHelper.executeCommand( cmd, m_rpcGetLoginInfoCallback );
	}
	
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
	 * Override this method so we can issue an ajax request to get information about self
	 * registration every time this dialog is invoked.
	 */
	public void show()
	{
		Scheduler.ScheduledCommand cmd;
		
		super.show();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				// Issue an ajax request to get self registration info
				getLoginInfoFromServer();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
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
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface LoginDlgClient
	{
		void onSuccess( LoginDlg dlg );
		void onUnavailable();
	}
	
	/*
	 * Asynchronously loads the LoginDlg and performs some operation
	 * against the code.
	 */
	private static void doAsyncOperation(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final LoginDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,
		final String loginUrl,
		final String springSecurityRedirect,
		
		// initAndShow parameters.
		final LoginDlg show_loginDlg,
		final boolean show_allowCancel,
		final boolean show_showLoginFailedMsg )
	{
		loadControl1(
			// Prefetch parameters.
			dlgClient,
			prefetch,
			
			// Creation parameters.
			autoHide,
			modal,
			xPos,
			yPos,
			properties,
			loginUrl,
			springSecurityRedirect,
			
			// initAndShow parameters.
			show_loginDlg,
			show_allowCancel,
			show_showLoginFailedMsg );
	}// doAsyncOperation

	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls by the LoginDlg object.
	 * 
	 * Loads the split point for the LoginDlg.
	 */
	private static void loadControl1(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final LoginDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,
		final String loginUrl,
		final String springSecurityRedirect,
		
		// initAndShow parameters.
		final LoginDlg show_loginDlg,
		final boolean show_allowCancel,
		final boolean show_showLoginFailedMsg )
	{
		GWT.runAsync( LoginDlg.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				initLoginDlg_Finish(
					// Prefetch parameters.
					dlgClient,
					prefetch,
					
					// Creation parameters.
					autoHide,
					modal,
					xPos,
					yPos,
					properties,
					loginUrl,
					springSecurityRedirect,
					
					// initAndShow parameters.
					show_loginDlg,
					show_allowCancel,
					show_showLoginFailedMsg );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LoginDlg() );
				dlgClient.onUnavailable();
			}// end onFailure()
		});
	}
	
	/*
	 * Finishes the initialization of the LoginDlg object.
	 */
	private static void initLoginDlg_Finish(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final LoginDlgClient dlgClient,
		final boolean prefetch,
		
		// Creation parameters.
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,
		final String loginUrl,
		final String springSecurityRedirect,
		
		// initAndShow parameters.
		final LoginDlg show_loginDlg,
		final boolean show_allowCancel,
		final boolean show_showLoginFailedMsg )
	{
		// On a prefetch...
		if ( prefetch )
		{
			// ...we simply call the success handler with null for the
			// ...parameter.
			dlgClient.onSuccess( null );
		}

		// If we weren't given a LoginDlg...
		else if ( null == show_loginDlg )
		{
			// ...we assume we're to create one.
			LoginDlg dlg = new LoginDlg(
				autoHide,
				modal,
				xPos,
				yPos,
				properties,
				loginUrl,
				springSecurityRedirect );
			
			dlgClient.onSuccess( dlg );
		}
		
		else
		{
			// Otherwise, we assume we're to initialize and show the
			// LoginDlg we were given!  Initialize...
			show_loginDlg.setAllowCancel( show_allowCancel );
			if ( show_showLoginFailedMsg )
			     show_loginDlg.showLoginFailedMsg();
			else show_loginDlg.hideLoginFailedMsg();
			show_loginDlg.hideAuthenticatingMsg();
			
			// ...and show it.
			show_loginDlg.setPopupPositionAndShow( new PopupPanel.PositionCallback()
			{
				@Override
				public void setPosition(int offsetWidth, int offsetHeight)
				{
					int x = ( ( Window.getClientWidth()  - offsetWidth  ) / 2 );
					int y = ( ( Window.getClientHeight() - offsetHeight ) / 3 );
					
					show_loginDlg.setPopupPosition( x, y );
				}// end setPosition()
			} );
		}
	}
	
	/**
	 * Loads the LoginDlg split point and returns an instance of it via
	 * the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param xPos
	 * @param yPos
	 * @param properties
	 * @param loginUrl	The URL we should use when we post the request to log in.
	 * @param springSecurityRedirect
	 * @param dlgClient
	 */
	public static void createAsync(
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final Object properties,
		final String loginUrl,
		final String springSecurityRedirect,
		final LoginDlgClient dlgClient )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			dlgClient,
			false,
			
			// Creation parameters.
			autoHide,
			modal,
			xPos,
			yPos,
			properties,
			loginUrl,
			springSecurityRedirect,
			
			// initAndShow parameters ignored.
			null,
			false,
			false );
	}// end createAsync()

	/**
	 * Initialize and show the dialog.
	 * 
	 * @param loginDlg
	 * @param allowCancel
	 * @param showLoginFailedMsg
	 */
	public static void initAndShow(
		final LoginDlg loginDlg,
		final boolean allowCancel,
		final boolean showLoginFailedMsg )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.
			null,
			false,
			
			// Creation parameters ignored.
			false,
			false,
			-1,
			-1,
			null,
			null,
			null,
			
			// initAndShow parameters.
			loginDlg,
			allowCancel,
			showLoginFailedMsg );
	}// end initAndShow()
	
	/**
	 * Causes the split point for the LoginDlg to be fetched.
	 * 
	 * @param dlgClient
	 */
	public static void prefetch( LoginDlgClient dlgClient )
	{
		// If we weren't give a LoginDlgClient...
		if ( null == dlgClient )
		{
			// ...create a dummy one...
			dlgClient = new LoginDlgClient()
			{				
				@Override
				public void onUnavailable()
				{
					// Unused.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( LoginDlg dlg )
				{
					// Unused.
				}// end onSuccess()
			};
		}

		// ...and perform the prefetch.
		doAsyncOperation(
			// Prefetch parameters.  true -> Prefetch only.
			dlgClient,
			true,
			
			// Creation parameters ignored.
			false,
			false,
			-1,
			-1,
			null,
			null,
			null,
			
			// initAndShow parameters ignored.
			null,
			false,
			false );
	}// end prefetch()
	
	public static void prefetch()
	{
		// Always use the initial form of the method.
		prefetch( null );
	}// end prefetch()
}// end LoginDlg
