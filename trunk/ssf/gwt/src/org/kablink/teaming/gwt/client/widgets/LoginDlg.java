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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtOpenIDAuthenticationProvider;
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
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
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
	private FlowPanel m_mainPanel = null;
	private FormPanel m_formPanel = null;
	private FlowPanel m_hiddenInputPanel = null;
	private Element m_userIdLabelElement = null;
	private TextBox m_userIdTxtBox = null;
	private Element m_pwdLabelElement = null;
	private PasswordTextBox m_pwdTxtBox = null;
	private Button m_cancelBtn = null;
	private Label m_loginFailedMsg = null;
	private Label m_authenticatingMsg = null;
	private String m_loginUrl = null;
	private String m_springSecurityRedirect = null;	// This values tells Teaming what url to go to after the user authenticates.
	private GwtSelfRegistrationInfo m_selfRegInfo = null;
	private InlineLabel m_selfRegLink = null;
	private CheckBox m_useOpenIdCkbox = null;
	private FlowPanel m_openIdProvidersPanel = null;
	private Hidden m_openIdProviderInput = null;
	private AsyncCallback<VibeRpcResponse> m_rpcGetLoginInfoCallback = null;
	private boolean m_requestedLoginInfo = false;

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

	/**
	 * 
	 */
	private class OpenIDAuthProviderImg extends Image
	{
		private GwtOpenIDAuthenticationProvider m_provider;
		
		/**
		 * 
		 */
		public OpenIDAuthProviderImg( GwtOpenIDAuthenticationProvider provider )
		{
			super();
			
			ImageResource imgResource;
			String title;
			
			m_provider = provider;
			if ( provider != null )
			{
				switch ( provider.getType() )
				{
				case AOL:
					imgResource = GwtTeaming.getImageBundle().openIdAuthProvider_aol();
					title = GwtTeaming.getMessages().loginDlg_AuthProviderAol();
					break;
				
				case GOOGLE:
					imgResource = GwtTeaming.getImageBundle().openIdAuthProvider_google();
					title = GwtTeaming.getMessages().loginDlg_AuthProviderGoogle();
					break;
				
				case MYOPENID:
					imgResource = GwtTeaming.getImageBundle().openIdAuthProvider_myopenid();
					title = GwtTeaming.getMessages().loginDlg_AuthProviderMyOpenId();
					break;
					
				case VERISIGN:
					imgResource = GwtTeaming.getImageBundle().openIdAuthProvider_verisign();
					title = GwtTeaming.getMessages().loginDlg_AuthProviderVerisign();
					break;
					
				case YAHOO:
					imgResource = GwtTeaming.getImageBundle().openIdAuthProvider_yahoo();
					title = GwtTeaming.getMessages().loginDlg_AuthProviderYahoo();
					break;
					
				case UNKNOWN:
				default:
					title = GwtTeaming.getMessages().loginDlg_AuthProviderUnknown();
					imgResource = GwtTeaming.getImageBundle().openIdAuthProvider_unknown();
					break;
				}
				
				setResource( imgResource );
				setTitle( title );
			}
		}
		
		/**
		 * 
		 */
		public GwtOpenIDAuthenticationProvider getProvider()
		{
			return m_provider;
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
		if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
			headerText = GwtTeaming.getMessages().loginDlgNovellFilrHeader();
		else if ( GwtTeaming.m_requestInfo.isNovellTeaming() )
			headerText = GwtTeaming.getMessages().loginDlgNovellHeader();
		else
			headerText = GwtTeaming.getMessages().loginDlgKablinkHeader();
		
		// Create the callback that will be used when we issue an ajax call to get login information
		m_rpcGetLoginInfoCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
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
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				final GwtLoginInfo loginInfo;
				
				loginInfo = (GwtLoginInfo) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						danceDlg( loginInfo );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
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
	@Override
	public Panel createContent( Object props )
	{
		Element formElement;

		// Hide the close image in the upper-right-hand corner
		hideCloseImg();
		
		m_mainPanel = new FlowPanel();
		
		// Get the <form ...> element that was created by GwtMainPage.jsp
		formElement = Document.get().getElementById( "loginFormId" );
		m_formPanel = new LoginFormPanel( formElement );
		m_formPanel.setVisible( true );
		
		m_formPanel.addSubmitHandler( new FormPanel.SubmitHandler()
		{
			/**
			 * 
			 */
			@Override
			public void onSubmit( SubmitEvent event )
			{
				// Hide the "login failed" message.
				hideLoginFailedMsg();
				
				// Show the the "authenticating..." message.
				showAuthenticatingMsg();
				
				// Are we using OpenID to authenticate
				if ( getUseOpenIDAuthentication() )
				{
					// Yes
					m_formPanel.setAction( "/ssf/j_spring_openid_security_check" );		
					m_formPanel.getElement().setAttribute( "name", "oidf" );
				}
			}
		});
		
		// Add a row for the "user id" controls.
		{
			Element userIdElement;
			
			m_userIdLabelElement = Document.get().getElementById( "userIdLabel" );
			m_userIdLabelElement.setInnerText( GwtTeaming.getMessages().loginDlgUserId() );
			
			userIdElement = Document.get().getElementById( "j_usernameId" );
			m_userIdTxtBox = TextBox.wrap( userIdElement );
		}
		
		// Add a row for the "password" controls.
		{
			Element pwdTxtBoxElement;
			
			m_pwdLabelElement = Document.get().getElementById( "pwdLabel" );
			m_pwdLabelElement.setInnerText( GwtTeaming.getMessages().loginDlgPassword() );
			
			pwdTxtBoxElement = Document.get().getElementById( "j_passwordId" );
			m_pwdTxtBox = PasswordTextBox.wrap( pwdTxtBoxElement );
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
		
		// Create a panel where hidden inputs will live.
		m_hiddenInputPanel = new FlowPanel();
		m_formPanel.add( m_hiddenInputPanel );
		
		// Add a hidden input for the "spring security redirect" which tells Teaming what page to go
		// to after the user authenticates.
		if ( m_springSecurityRedirect != null && m_springSecurityRedirect.length() > 0 )
		{
			Hidden hiddenInput;
			
			hiddenInput = new Hidden();
			hiddenInput.setName( "spring-security-redirect" );
			hiddenInput.setValue( m_springSecurityRedirect );
			m_hiddenInputPanel.add( hiddenInput );
		}
		
		// Create the ok and cancel buttons
		{
			Element okElement;
			Element cancelElement;
			Button okBtn;
			
			okElement = Document.get().getElementById( "loginOkBtn" );
			okBtn = Button.wrap( okElement );
			okBtn.setText( GwtTeaming.getMessages().login() );
			
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
				@Override
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
				@Override
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
				@Override
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
		
		m_mainPanel.add( m_formPanel );

		init( props );

		return m_mainPanel;
	}// end createContent()
	
	
	/*
	 * Override the createFooter() method so we can control what buttons are in the footer.
	 */
	@Override
	public Panel createFooter()
	{
		return null;
	}// end createFooter()
	
	
	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	@Override
	public Panel createHeader( String caption )
	{
		Image img;
		FlowPanel panel;
		
		panel = new FlowPanel();

		if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
		{
			// Create a Novell Filr image that will be used in case there is no branding.
			img = new Image( GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/novell_filr_graphic.png" );
		}
		else if ( GwtTeaming.m_requestInfo.isNovellTeaming() )
		{
			// Create a Novell Teaming image that will be used in case there is no branding.
			img = new Image( GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/novell_graphic.png" );
		}
		else
		{
			// Create a Kablink Teaming image that will be used in case there is no branding.
			img = new Image( GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/kablink_graphic.png" );
		}
	
		img.setHeight( "75" );

		panel.add( img );
		
		return panel;
	}// end createHeader()
	
	/**
	 * Create the controls needed for OpenID authentication
	 */
	private void createOpenIdControls( ArrayList<GwtOpenIDAuthenticationProvider> listOfProviders )
	{
		// Have we already created a "Use OpenID Authentication" checkbox?
		if ( m_useOpenIdCkbox == null )
		{
			FlowPanel panel;
			ValueChangeHandler<java.lang.Boolean> valueChangeHandler;

			// No
			// Add a "Use OpenID Authentication" checkbox.
			m_useOpenIdCkbox = new CheckBox( GwtTeaming.getMessages().loginDlgUseOpenId() );
			panel = new FlowPanel();
			panel.addStyleName( "loginDlg_useOpenIdPanel" );
			panel.add( m_useOpenIdCkbox );
			m_mainPanel.insert( panel, 0 );
			
			valueChangeHandler = new ValueChangeHandler<java.lang.Boolean>()
			{
				@Override
				public void onValueChange( ValueChangeEvent<java.lang.Boolean> event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							danceOpenIdControls();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_useOpenIdCkbox.addValueChangeHandler( valueChangeHandler );

			// Add a hidden input that will hold the url of the selected OpenID authentication provider
			{
				m_openIdProviderInput = new Hidden();
				m_openIdProviderInput.setName( "openid_identifier" );
				m_openIdProviderInput.setValue( "" );
				m_hiddenInputPanel.add( m_openIdProviderInput );
			}
			
			// Create a panel that holds all of the openid authentication providers
			{
				// Add a panel that will hold all of the OpenID authentication providers.
				m_openIdProvidersPanel = new FlowPanel();
				m_openIdProvidersPanel.addStyleName( "loginDlg_openIdProvidersPanel" );
				m_mainPanel.insert( m_openIdProvidersPanel, 1 );
				
				if ( listOfProviders != null && listOfProviders.size() > 0 )
				{
					FlexTable table;
					Label pleaseSelectLabel;
					int row;
					int i;
					
					pleaseSelectLabel = new Label( GwtTeaming.getMessages().loginDlgSelectAuthProviderLabel() );
					pleaseSelectLabel.addStyleName( "loginDlg_pleaseSelectProviderLabel" );
					m_openIdProvidersPanel.add( pleaseSelectLabel );
					
					table = new FlexTable();
					m_openIdProvidersPanel.add( table );
					
					row = 0;
					for (i = 0; i < listOfProviders.size(); ++i)
					{
						OpenIDAuthProviderImg img;
						GwtOpenIDAuthenticationProvider provider;
						int col;
						
						provider = listOfProviders.get( i );
						img = new OpenIDAuthProviderImg( provider );
						img.addStyleName( "loginDlg_openIdProviderImg" );
						
						img.addClickHandler( new ClickHandler() 
						{
							@Override
							public void onClick( ClickEvent event )
							{
								Scheduler.ScheduledCommand cmd;
								OpenIDAuthProviderImg img;
								final GwtOpenIDAuthenticationProvider selectedProvider;
								
								img = (OpenIDAuthProviderImg) event.getSource();
								selectedProvider = img.getProvider();
								
								cmd = new Scheduler.ScheduledCommand()
								{
									@Override
									public void execute() 
									{
										handleOpenIDAuthProviderSelected( selectedProvider );
									}
								};
								Scheduler.get().scheduleDeferred( cmd );
							}
						} );
						
						col = i % 3;
						table.setWidget( row, col, img );
						if ( col == 2 )
							++row;
						
					}// end for()
				}
			}
		}
	}

	/**
	 * Hide/show controls on the dialog based on the given login info
	 */
	private void danceDlg( GwtLoginInfo loginInfo )
	{
		m_selfRegInfo = loginInfo.getSelfRegistrationInfo();
		
		// Hide or show the self registration controls.
		updateSelfRegistrationControls( m_selfRegInfo );

		// Turn auto complete on/off.
		if ( loginInfo.getAllowAutoComplete() == true )
			DOM.removeElementAttribute( m_formPanel.getElement(), "autocomplete" );
		else
			DOM.setElementAttribute( m_formPanel.getElement(), "autocomplete", "off" );
		
		// Is OpenID authentication available?
		if ( loginInfo.getAllowOpenIdAuthentication() )
		{
			// Yes
			// Create the controls needed for openid authentication.
			createOpenIdControls( loginInfo.getOpenIDAuthenticationProviders() );
			
			// Show/hide the openid controls
			danceOpenIdControls();
		}
	}
	
	/**
	 * Show/hide the controls used with OpenId authentication
	 */
	private void danceOpenIdControls()
	{
		if ( m_useOpenIdCkbox != null )
		{
			// Is the "Use OpenId Authentication" checkbox checked.
			if ( m_useOpenIdCkbox.getValue() )
			{
				// Yes
				m_openIdProvidersPanel.setVisible( true );
				
				m_userIdLabelElement.getStyle().setDisplay( Display.NONE );
				m_userIdTxtBox.setVisible( false );
				
				m_pwdLabelElement.getStyle().setDisplay( Display.NONE );
				m_pwdTxtBox.setVisible( false );
			}
			else
			{
				// No
				m_openIdProvidersPanel.setVisible( false );
				
				m_userIdLabelElement.getStyle().setDisplay( Display.BLOCK );
				m_userIdTxtBox.setVisible( true );

				m_pwdLabelElement.getStyle().setDisplay( Display.BLOCK );
				m_pwdTxtBox.setVisible( true );
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Nothing to do.
		return new Object();
	}// end getDataFromDlg()
	
	
	/**
	 *  
	 */
	@Override
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
		
		if ( m_requestedLoginInfo == false )
		{
			m_requestedLoginInfo = true;

			// Issue an ajax request to get login information
			cmd = new GetLoginInfoCmd();
			GwtClientHelper.executeCommand( cmd, m_rpcGetLoginInfoCallback );
		}
	}
	
	/**
	 * Submit the login form using the url of the selecte openid authentication provider.
	 */
	private void handleOpenIDAuthProviderSelected( GwtOpenIDAuthenticationProvider provider )
	{
		if ( provider != null )
		{
			m_openIdProviderInput.setValue( provider.getUrl() );
			m_formPanel.submit();
		}
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
	@Override
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
	@Override
	public void show()
	{
		Scheduler.ScheduledCommand cmd;
		
		super.show();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
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
	
	
	/**
	 * Return whether we should use OpenID authentication 
	 */
	private boolean getUseOpenIDAuthentication()
	{
		if ( m_useOpenIdCkbox != null && m_useOpenIdCkbox.getValue() == true )
			return true;
		
		return false;
	}
}// end LoginDlg
