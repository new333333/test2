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

import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DialogClosedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.WindowTitleSetEvent;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtOpenIDAuthenticationProvider;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.RequestResetPwdRpcResponseData;
import org.kablink.teaming.gwt.client.SendForgottenPwdEmailRpcResponseData;
import org.kablink.teaming.gwt.client.ZoneShareTerms;
import org.kablink.teaming.gwt.client.datatable.ApplyColumnWidths;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CompleteExternalUserSelfRegistrationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetLoginInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetZoneShareTermsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.RequestResetPwdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateCaptchaCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.Agent;
import org.kablink.teaming.gwt.client.util.AgentBase;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ChangePasswordDlg.ChangePasswordDlgClient;
import org.kablink.teaming.gwt.client.widgets.ForgottenPwdDlg.ForgottenPwdDlgClient;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderDlg.ModifyNetFolderDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * ?
 * 
 * @author jwootton@novell.com
 */
@SuppressWarnings("unused")
public class LoginDlg extends DlgBox
	implements
		// Event handlers implemented by this class.
		DialogClosedEvent.Handler,
		WindowTitleSetEvent.Handler
{
	private FlowPanel m_mainPanel = null;
	private FormPanel m_formPanel = null;
	private FlowPanel m_hiddenInputPanel = null;
	private FlowPanel m_headerPanel = null;
	private String m_caption = null;
	private Element m_userIdLabelElement = null;
	private TextBox m_userIdTxtBox = null;
	private Element m_pwdLabelElement = null;
	private PasswordTextBox m_pwdTxtBox = null;
	private Button m_okBtn;
	private TouchButton m_okTouchBtn;
	private Button m_cancelBtn = null;
	private TouchButton m_cancelTouchBtn = null;
	private Label m_loginFailedMsg = null;
	private Label m_authenticatingMsg = null;
	private InlineLabel m_forgotPwdLink = null;
	private InlineLabel m_selfRegLink = null;
	private CheckBox m_useOpenIdCkbox = null;
	private FlowPanel m_openIdProvidersPanel = null;
	private Hidden m_openIdProviderInput = null;
	private TextBox m_firstNameTxtBox;
	private TextBox m_lastNameTxtBox;
	private PasswordTextBox m_pwd1TxtBox;
	private PasswordTextBox m_pwd2TxtBox;
	private FlowPanel m_selfRegPanel;
	private Button m_registerBtn;
	private TouchButton m_registerTouchBtn;
	private Button m_pwdResetBtn;
	private TouchButton m_pwdResetTouchBtn;
	private Image m_captchaImg;
	private TextBox m_captchaResponseTxtBox;
	private ForgottenPwdDlg m_forgottenPwdDlg = null;
	private ChangePasswordDlg m_changePwdDlg = null;

	// Elements related to the KeyShield two-stage authentication.
	private Element m_keyShieldErrorMessage;
	private Element m_keyShieldRefererLink;
	private Element m_keyShieldRefererPanel;
	private Element m_keyShieldTwoStageAuthPanel;

	private String m_loginUrl = null;
	private String m_springSecurityRedirect = null;	// This values tells Teaming what URL to go to after the user authenticates.
	private boolean m_redirectIsReadFile = false;	// Set true if m_springSecurityRedirect is set to a readFile URL.
	private GwtSelfRegistrationInfo m_selfRegInfo = null;
	private boolean m_requestedLoginInfo = false;
	private LoginStatus m_loginStatus;
	private Long m_loginUserId;
	private boolean m_initialized = false;
	private List<HandlerRegistration> m_registeredEventHandlers;	// Event handlers that are currently registered.
	private String m_preLoginTitle;
	private boolean m_captchaAlreadyChecked = false;
	
	private ZoneShareTerms m_zoneShareTerms=null;
	
	/**
	 * 
	 */
	public enum LoginStatus
	{
		AuthenticationFailed,
		RegistrationRequired,
		PromptForLogin,
		PromptForPwdReset,
		PwdResetVerified,
		WebAccessRestricted,
		PasswordExpired,
	}
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
	{
		TeamingEvents.DIALOG_CLOSED,
		TeamingEvents.WINDOW_TITLE_SET,
	};
	
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
		if ( GwtClientHelper.hasString( m_springSecurityRedirect ) )
		{
			m_redirectIsReadFile = m_springSecurityRedirect.contains( "/readFile/" );
		}
		
		// Create the header, content and footer of this dialog box.
		if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
			headerText = GwtTeaming.getMessages().loginDlgNovellFilrHeader();
		else if ( GwtTeaming.m_requestInfo.isNovellTeaming() )
			headerText = GwtTeaming.getMessages().loginDlgNovellHeader();
		else
			headerText = GwtTeaming.getMessages().loginDlgKablinkHeader();
		
		createAllDlgContent( headerText, null, null, properties ); 
	}
	

	/**
	 * 
	 */
	private void debugAlert( String msg )
	{
		//Window.alert( msg );
	}
	
	/**
	 * 
	 */
	private void centerAndShow()
	{
		setPopupPositionAndShow( new PopupPanel.PositionCallback()
		{
			@Override
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int x = ( ( Window.getClientWidth()  - offsetWidth  ) / 2 );
				int y = ( ( Window.getClientHeight() - offsetHeight ) / 3 );
				
				setPopupPosition( x, y );
				setGlassStyleName( "loginDlgBox_Glass" );
				
				if ( LoginStatus.PasswordExpired.equals( m_loginStatus ) )
				{
					showChangePasswordDlgAsync( m_loginUserId );
				}
			}
		} );
	}
	
	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent()
	{
	}
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		// Hide the close image in the upper-right-hand corner
		hideCloseImg();
		
		m_mainPanel = new FlowPanel();
		
		// The appropriate ui will be created by showDlg()
		
		init( props );

		return m_mainPanel;
	}
	
	
	/*
	 * Override the createFooter() method so we can control what buttons are in the footer.
	 */
	@Override
	public FlowPanel createFooter()
	{
		return null;
	}

	/**
	 * 
	 */
	private void createFormPanel()
	{
		Element formElement;

		if ( m_formPanel != null )
			return;
		
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
				// Do we need to check the captcha?
				if ( (null != m_captchaImg) && m_captchaImg.isVisible() && (!m_captchaAlreadyChecked) )
				{
					AsyncCallback<VibeRpcResponse> rpcCallback;
					ValidateCaptchaCmd cmd;
					HttpRequestInfo httpRequestInfo;

					// Yes
					rpcCallback = new AsyncCallback<VibeRpcResponse>()
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
							debugAlert( "In ValidateCaptchaCmd / onFailure()" );
						}
				
						/**
						 * 
						 * @param result
						 */
						@Override
						public void onSuccess( VibeRpcResponse response )
						{
							if ( response.getResponseData() != null && response.getResponseData() instanceof BooleanRpcResponseData )
							{
								BooleanRpcResponseData responseData;
								
								responseData = (BooleanRpcResponseData) response.getResponseData();
								if ( responseData.getBooleanValue() == true )
								{
									m_captchaAlreadyChecked = true;
									m_formPanel.submit();
								}
								else
								{
									Window.alert( GwtTeaming.getMessages().loginDlg_InvalidCaptcha() );
								}
							}
						}
					};
					
					// Cancel the form from being submitted.  If the captcha is valid we will submit the form
					event.cancel();
					
					// Issue an ajax request to check the captcha
					cmd = new ValidateCaptchaCmd( getCaptchaResponse() );
					// We want to issue the rpc requests without a user login id so that the GwtRpcController
					// doesn't think the session has timed out.
					httpRequestInfo = HttpRequestInfo.createHttpRequestInfo();
					httpRequestInfo.setUserLoginId( null );
					GwtClientHelper.executeCommand( cmd, httpRequestInfo, rpcCallback );
				}
				else
					doOnSubmitWork();
			}
		});

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

		// Add a hidden input that will hold the url of the selected OpenID authentication provider
		{
			m_openIdProviderInput = new Hidden();
			m_openIdProviderInput.setName( "openid_identifier" );
			m_openIdProviderInput.setValue( "" );
			m_hiddenInputPanel.add( m_openIdProviderInput );
		}
	}
	
	/**
	 * 
	 */
	private void createHeaderNow( GwtBrandingData brandingData )
	{
		boolean useDefaultImg = true;
		String imgUrl = null;
		
		if ( brandingData != null )
		{
			String imgName;
			
			// Get the name of the image to use.
			imgName = brandingData.getLoginDlgImageName();
			
			// Do we have an image name to use
			if ( imgName != null && imgName.length() > 0 )
			{
				// Yes
				// Is the branding image name "__default teaming image__"?
				if ( imgName.equalsIgnoreCase( BrandingPanel.DEFAULT_TEAMING_IMAGE ) )
				{
					// Yes
					useDefaultImg = true;
				}
				// Is the branding image name "__no image__"?
				else if ( imgName.equalsIgnoreCase( BrandingPanel.NO_IMAGE ) )
				{
					// Yes
					useDefaultImg = false;
				}
				else
				{
					imgUrl = brandingData.getLoginDlgImageUrl();
					if ( imgUrl != null && imgUrl.length() > 0 )
						useDefaultImg = false;
				}
			}
		}
	
		if ( useDefaultImg )
		{
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
			{
				// Create a Novell Filr image that will be used in case there is no branding.
				imgUrl = GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/novell_filr_graphic.png";
			}
			else if ( GwtTeaming.m_requestInfo.isNovellTeaming() )
			{
				// Create a Novell Teaming image that will be used in case there is no branding.
				imgUrl = GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/novell_graphic.png";
			}
			else
			{
				// Create a Kablink Teaming image that will be used in case there is no branding.
				imgUrl = GwtMainPage.m_requestInfo.getImagesPath() + "pics/Login/kablink_graphic.png";
			}
		}
		
		// Should we add an image?
		if ( imgUrl != null )
		{
			Image img = null;

			// Yes
			img = new Image( imgUrl );
			img.addLoadHandler( new LoadHandler()
			{
				@Override
				public void onLoad(LoadEvent event)
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							centerAndShow();
						}
					} );
				}
			} );
			img.addErrorHandler( new ErrorHandler()
			{
				@Override
				public void onError( ErrorEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							centerAndShow();
						}
					} );
				}
			} );
			m_headerPanel.add( img );

			// Set the dialog to be hidden and show it.  This will add everything into the DOM
			// and the onLoad() method will be called.  If we don't show the dialog now, the onLoad()
			// method will never get called.
			setVisible( false );
			show();
		}
		else
			centerAndShow();
	}
	
	/**
	 * Override the createHeader() method because we need to make it nicer.
	 */
	@Override
	public Panel createHeader( String caption )
	{
		m_caption = caption;
		m_headerPanel = new FlowPanel();
		
		return m_headerPanel;
	}
	
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							danceOpenIdControls();
						}
					} );
				}
			};
			m_useOpenIdCkbox.addValueChangeHandler( valueChangeHandler );

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
								OpenIDAuthProviderImg img;
								final GwtOpenIDAuthenticationProvider selectedProvider;
								
								img = (OpenIDAuthProviderImg) event.getSource();
								selectedProvider = img.getProvider();
								
								GwtClientHelper.deferCommand( new ScheduledCommand()
								{
									@Override
									public void execute() 
									{
										handleOpenIDAuthProviderSelected( selectedProvider );
									}
								} );
							}
						} );
						
						col = i % 3;
						table.setWidget( row, col, img );
						if ( col == 2 )
							++row;
						
					}
				}
			}
		}
	}

	/*
	 * Hide/show controls on the dialog based on the given login info.
	 */
	private void danceDlg(GwtLoginInfo loginInfo, LoginStatus loginStatus) {
		debugAlert("in danceDlg");
		m_selfRegInfo = loginInfo.getSelfRegistrationInfo();
		
		switch (loginStatus) {
		case AuthenticationFailed:
		case PromptForLogin:
		case PwdResetVerified:
		case WebAccessRestricted:
		case PasswordExpired:
			// Hide or show the self registration controls.
			updateSelfRegistrationControls(m_selfRegInfo);

			// Turn auto complete on/off.
			if ( loginInfo.getAllowAutoComplete() == true )
			     m_formPanel.getElement().removeAttribute("autocomplete"       );
			else m_formPanel.getElement().setAttribute(   "autocomplete", "off");
			
			// Is OpenID authentication available?
			if (loginInfo.getAllowOpenIdAuthentication()) {
				// Yes!  Create the controls needed for openID
				// authentication.
				debugAlert("openid auth is allowed");
				createOpenIdControls(loginInfo.getOpenIDAuthenticationProviders());
				
				// Show/hide the openID controls
				danceOpenIdControls();
			}
			
			// Hide/show the KeyShield two-stage authentication
			// controls. 
			danceKeyShieldControls(
				loginInfo.getKeyShieldHardwareTokenMissing(),
				loginInfo.getKeyShieldErrorMessagesForWeb());
			
		    break;
			
		case RegistrationRequired:
			danceExternalUserRegistrationControls(loginInfo.getAllowOpenIdAuthentication());
			break;

		case PromptForPwdReset:
		default:
			break;
		}
	}
	
	/*
	 */
	private void danceExternalUserRegistrationControls(boolean openIdAuthAllowed) {
		// Nothing to do.
	}
	
	/*
	 * Dances the dialog as necessary for KeyShield two-stage
	 * authentication.
	 */
	private void danceKeyShieldControls(boolean isKeyShieldHardwareTokenMissing, String keyShieldErrorMessagesForWeb) {
		// Show the KeyShield two-stage authentication panel if the
		// hardware token is missing and hide it otherwise.
		UIObject.setVisible(m_keyShieldTwoStageAuthPanel, isKeyShieldHardwareTokenMissing);
		
		// Was the token missing?
		if (isKeyShieldHardwareTokenMissing) {
			// Yes!  Display the appropriate error message...
			if (!(GwtClientHelper.hasString(keyShieldErrorMessagesForWeb))) {
				keyShieldErrorMessagesForWeb = GwtTeaming.getMessages().editKeyShieldConfigDlg_Error_DefaultWeb();
			}
			m_keyShieldErrorMessage.setInnerText(keyShieldErrorMessagesForWeb);

			// ...and if we have the referrer URL...
			boolean hasRefererUrl = GwtClientHelper.hasString(m_springSecurityRedirect);
			UIObject.setVisible(m_keyShieldRefererPanel, hasRefererUrl);
			if (hasRefererUrl) {
				// ...display the link to it.
				String href = "javascript:window.top.ss_doLoginReferal('" + GwtClientHelper.jsEncodeURIComponent(m_springSecurityRedirect) + "');";
				m_keyShieldRefererLink.setAttribute("href", href);
			}
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
	private void doOnSubmitWork()
	{
		m_captchaAlreadyChecked = false;
		
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

		// If we're redirecting the login to a readFile URL...
		if ( m_redirectIsReadFile )
		{
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// ...clear the content of the login
					// ...dialog...
					m_mainPanel.clear();
					
					// ...and display a message about the file
					// ...download.
					FlowPanel dlMsgPanel = new FlowPanel();
					dlMsgPanel.addStyleName( "loginDlg_downloadingFileMsgPanel" );
					m_mainPanel.add( dlMsgPanel );
					
					Label readingLabel = new Label( GwtTeaming.getMessages().loginDlgDownloadingFile1() );
					readingLabel.addStyleName( "loginDlg_downloadingFileMsg1" );
					dlMsgPanel.add( readingLabel );
					readingLabel = new Label( GwtTeaming.getMessages().loginDlgDownloadingFile2() );
					readingLabel.addStyleName( "loginDlg_downloadingFileMsg2" );
					dlMsgPanel.add( readingLabel );
					
				}
			} );
		}
	}
	
	/**
	 * 
	 */
	private String getCaptchaResponse()
	{
		if ( m_captchaResponseTxtBox != null && m_captchaResponseTxtBox.isVisible() )
			return m_captchaResponseTxtBox.getValue();
		
		return "";
	}

	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Nothing to do.
		return new Object();
	}
	
	/**
	 * 
	 */
	private Long getExtUserId()
	{
		return GwtTeaming.getMainPage().getLoginExternalUserId();
	}
	
	/**
	 * 
	 */
	private String getFirstName()
	{
		if ( m_firstNameTxtBox != null )
			return m_firstNameTxtBox.getValue();
		
		return null;
	}
	
	/**
	 *  
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_userIdTxtBox;
	}
	
	
	/**
	 * 
	 */
	private String getLastName()
	{
		if ( m_lastNameTxtBox != null )
			return m_lastNameTxtBox.getValue();
		
		return null;
	}
	
	/**
	 * Issue an ajax request to get login info from the server.
	 */
	private void getLoginInfoFromServer( final LoginStatus loginStatus )
	{
		GetLoginInfoCmd cmd;
		
		debugAlert( "in getLoginInfoFromServer() m_requestedLoginInfo: " + m_requestedLoginInfo );
		if ( m_requestedLoginInfo == false )
		{
			AsyncCallback<VibeRpcResponse> rpcCallback;
			HttpRequestInfo httpRequestInfo;
			
			m_requestedLoginInfo = true;

			rpcCallback = new AsyncCallback<VibeRpcResponse>()
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
					debugAlert( "In m_rpcGetLoginInfoCallback / onFailure()" );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					debugAlert( "in m_rpcGetLoginInfoCallback / onSuccess()" );
					if ( response.getResponseData() != null && response.getResponseData() instanceof GwtLoginInfo )
					{
						final GwtLoginInfo loginInfo;

						debugAlert( "response data is not null" );
						loginInfo = (GwtLoginInfo) response.getResponseData();
						
						danceDlg( loginInfo, loginStatus );
					}
				}
			};
			
			// Issue an ajax request to get login information
			debugAlert( "about to execute GetLoginInfoCmd" );
			cmd = new GetLoginInfoCmd();
			// We want to issue the rpc requests without a user login id so that the GwtRpcController
			// doesn't think the session has timed out.
			httpRequestInfo = HttpRequestInfo.createHttpRequestInfo();
			httpRequestInfo.setUserLoginId( null );
			GwtClientHelper.executeCommand( cmd, httpRequestInfo, rpcCallback );
		}
	}
	
	/**
	 * 
	 */
	private String getPwd1()
	{
		if ( m_pwd1TxtBox != null )
			return m_pwd1TxtBox.getValue();
		
		return null;
	}
	
	/**
	 * 
	 */
	private String getPwd2()
	{
		if ( m_pwd2TxtBox != null )
			return m_pwd2TxtBox.getValue();
		
		return null;
	}

	/**
	 * Get the site branding data and then finish creating the header.
	 */
	private void getSiteBrandingDataThenCreateHeader()
	{
		AsyncCallback<VibeRpcResponse> getSiteBrandingCallback;
		GetSiteBrandingCmd cmd;
		HttpRequestInfo httpRequestInfo;

		// Create the callback that will be used when we issue an ajax call to get the site branding
		getSiteBrandingCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( final Throwable t )
			{
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Don't call GwtClientHelper.handleGwtRPCFailure() like we would normally do.  If the
						// session has expired, handleGwtRPCFailure() will invoke this login dialog again
						// and we will be in an infinite loop.
						createHeaderNow( null );
					}
				} );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final GwtBrandingData brandingData;
				
				brandingData = (GwtBrandingData) response.getResponseData();

				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						createHeaderNow( brandingData );
					}
				} );
				
			}
		};
		
		// Issue an ajax request to get the site branding data.
		cmd = new GetSiteBrandingCmd();
		// We want to issue the rpc requests without a user login id so that the GwtRpcController
		// doesn't think the session has timed out.
		httpRequestInfo = HttpRequestInfo.createHttpRequestInfo();
		httpRequestInfo.setUserLoginId( null );
		GwtClientHelper.executeCommand( cmd, httpRequestInfo, getSiteBrandingCallback );
	}
	
	/**
	 * This method gets called when the user clicks on the Register button
	 */
	private void handleClickOnRegisterBtn()
	{
		CompleteExternalUserSelfRegistrationCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		HttpRequestInfo httpRequestInfo;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
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
				debugAlert( "In CompleteExternalUserSelfRegistrationCmd / onFailure()" );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				debugAlert( "In CompleteExternalUserSelfRegistrationCmd / onSuccess()" );
				if ( response.getResponseData() != null && response.getResponseData() instanceof ErrorListRpcResponseData )
				{
					ErrorListRpcResponseData responseData = ( (ErrorListRpcResponseData) response.getResponseData() );
					List<ErrorInfo> errList = responseData.getErrorList();
					if ( GwtClientHelper.hasItems( errList ) )
					{
						for (ErrorInfo err:  errList) {
							FlowPanel errorPanel = getErrorPanel();
							errorPanel.clear();
							for ( ErrorInfo error:  errList )
							{
								Label label = new Label( error.getMessage() );
								label.addStyleName( "dlgErrorLabel" );
								errorPanel.add( label );
							}
							showErrorPanel();
						}
					}
					else
					{
						GwtClientHelper.deferCommand( new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								clearErrorPanel();
								hideErrorPanel();
								showExternalUserRegConfirmation();
							}
						} );
					}
				}
			}
		};
		
		// Did the user fill out all the necessary information
		if ( isExternalUserSelfRegDataValid() == false )
		{
			// No, just bail.  The user will have already been told what's wrong.
			return;
		}
		
		final Element acceptTermsCheckBox=Document.get().getElementById("acceptTermsCheckBox");
		
		if(m_zoneShareTerms!=null)
		{
			cmd = new CompleteExternalUserSelfRegistrationCmd(
														getExtUserId(),
														getFirstName(),
														getLastName(),
														getPwd1(),
														GwtTeaming.getMainPage().getLoginInvitationUrl(),
														Boolean.TRUE );
		}
		else
		{
			cmd = new CompleteExternalUserSelfRegistrationCmd(
					getExtUserId(),
					getFirstName(),
					getLastName(),
					getPwd1(),
					GwtTeaming.getMainPage().getLoginInvitationUrl(),
					Boolean.FALSE );			
		}
		// We want to issue the rpc requests without a user login id so that the GwtRpcController
		// doesn't think the session has timed out.
		httpRequestInfo = HttpRequestInfo.createHttpRequestInfo();
		httpRequestInfo.setUserLoginId( null );
		GwtClientHelper.executeCommand( cmd, httpRequestInfo, rpcCallback );
	}
	
	/**
	 * This method gets called when the user clicks on the "Reset Password" button
	 */
	private void handleClickOnResetPwdBtn()
	{
		RequestResetPwdCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		HttpRequestInfo httpRequestInfo;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
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
				debugAlert( "In ResetPwdCmd / onFailure()" );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				debugAlert( "In ResetPwdCmd / onSuccess()" );
				if ( response.getResponseData() != null && response.getResponseData() instanceof RequestResetPwdRpcResponseData )
				{
					RequestResetPwdRpcResponseData responseData;
					String[] errors;
					
					responseData = (RequestResetPwdRpcResponseData) response.getResponseData();
					
					// Were there any errors?
					errors = responseData.getErrors();
					if ( errors != null && errors.length > 0 )
					{
						FlowPanel errorPanel;
						Label label;
						
						// Yes
						// Get the panel that holds the errors.
						errorPanel = getErrorPanel();
						errorPanel.clear();

						label = new Label( GwtTeaming.getMessages().shareErrors() );
						label.addStyleName( "dlgErrorLabel" );
						errorPanel.add( label );
						
						for ( String nextErrMsg : errors )
						{
							label = new Label( nextErrMsg );
							label.addStyleName( "bulletListItem" );
							errorPanel.add( label );
						}

						// Make the error panel visible.
						showErrorPanel();

						// Enable the Ok button.
						hideStatusMsg();
						setOkEnabled( true );
					}
					else
					{
						GwtClientHelper.deferCommand( new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								clearErrorPanel();
								hideErrorPanel();
								showExternalUserPasswordResetRequestedUI();
							}
						} );
					}
				}
			}
		};
		
		// Did the user fill out all the necessary information
		if ( isPwdResetDataValid() == false )
		{
			// No, just bail.  The user will have already been told what's wrong.
			return;
		}
		
		cmd = new RequestResetPwdCmd(
							getExtUserId(),
							getPwd1() );
		// We want to issue the rpc requests without a user login id so that the GwtRpcController
		// doesn't think the session has timed out.
		httpRequestInfo = HttpRequestInfo.createHttpRequestInfo();
		httpRequestInfo.setUserLoginId( null );
		GwtClientHelper.executeCommand( cmd, httpRequestInfo, rpcCallback );
	}
	
	/**
	 * Submit the login form using the url of the selected openid authentication provider.
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
	private void hideAuthenticatingMsg()
	{
		if ( m_authenticatingMsg != null )
			m_authenticatingMsg.setVisible( false );
	}

	
	/**
	 * 
	 */
	private void hideLoginFailedMsg()
	{
		if ( m_loginFailedMsg != null )
			m_loginFailedMsg.setVisible( false );
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given object.
	 * Currently there is nothing to initialize.
	 */
	public void init( Object props )
	{
	}

	
	/**
	 * Did the user fill out all the necessary data for external user self registration
	 */
	private boolean isExternalUserSelfRegDataValid()
	{
		String name;
		String pwd1;
		String pwd2;
		
		name = getFirstName();
		if ( name == null || name.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().loginDlg_firstNameRequired() );
			m_firstNameTxtBox.setFocus( true );
			return false;
		}
		
		name = getLastName();
		if ( name == null || name.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().loginDlg_lastNameRequired() );
			m_lastNameTxtBox.setFocus( true );
			return false;
		}
		
		pwd1 = getPwd1();
		if ( pwd1 == null || pwd1.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().loginDlg_pwdRequired() );
			m_pwd1TxtBox.setFocus( true );
			return false;
		}
		
		pwd2 = getPwd2();
		if ( pwd1.equalsIgnoreCase( pwd2 ) == false )
		{
			Window.alert( GwtTeaming.getMessages().loginDlg_pwdDoNotMatch() );
			m_pwd2TxtBox.setFocus( true );
			return false;
		}
		
		// If we get here, everything is good.
		return true;
	}

	/**
	 * Did the user fill out all the necessary data to reset their password
	 */
	private boolean isPwdResetDataValid()
	{
		String pwd1;
		String pwd2;
		
		pwd1 = getPwd1();
		if ( pwd1 == null || pwd1.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().loginDlg_pwdRequired() );
			m_pwd1TxtBox.setFocus( true );
			return false;
		}
		
		pwd2 = getPwd2();
		if ( pwd1.equalsIgnoreCase( pwd2 ) == false )
		{
			Window.alert( GwtTeaming.getMessages().loginDlg_pwdDoNotMatch() );
			m_pwd2TxtBox.setFocus( true );
			return false;
		}
		
		// If we get here, everything is good.
		return true;
	}

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
	}
	

	/**
	 * Should we allow the user to cancel the login dialog.
	 */
	private void setAllowCancel( boolean allowCancel )
	{
		if ( m_cancelBtn != null )
			m_cancelBtn.setVisible( allowCancel );
	}
	

	/**
	 * 
	 */
	public void showDlg( boolean allowCancel, LoginStatus loginStatus, Long loginUserId )
	{
		debugAlert( "In LoginDlg.showDlg()" );

		if ( m_initialized )
		{
			centerAndShow();
			return;
		}

		m_loginStatus = loginStatus;
		m_loginUserId = loginUserId;

		switch ( loginStatus )
		{
		case WebAccessRestricted:
		case AuthenticationFailed:
			showRegularLoginUI();
		    showLoginFailedMsg( loginStatus );
		    break;
		    
		case PasswordExpired:
			showRegularLoginUI();
			break;
			
		case RegistrationRequired:
			showExternalUserRegistrationUI();
			break;
			
		case PromptForLogin:
			showRegularLoginUI();
			hideLoginFailedMsg();
			break;
		
		case PromptForPwdReset:
			showExternalUserPwdResetUI();
			allowCancel = false;
			break;
			
		case PwdResetVerified:
			Window.alert( GwtTeaming.getMessages().loginDlg_PasswordResetComplete() );
			showRegularLoginUI();
			hideLoginFailedMsg();
			break;
		}

		setAllowCancel( allowCancel );

		// Create the header.  getSiteBrandingDataThenCreateHeader() will show the dialog when it is finished.
		m_initialized = true;
		getSiteBrandingDataThenCreateHeader();

		// Issue an ajax request to get self registration info and a list of open id providers
		getLoginInfoFromServer( loginStatus );
	}
	
	/**
	 * 
	 */
	private void showAuthenticatingMsg()
	{
		if ( m_authenticatingMsg != null )
			m_authenticatingMsg.setVisible( true );
	}

	/**
	 * Show the message when the external user has completed the registration
	 */
	private void showExternalUserRegConfirmation()
	{
		FlowPanel panel;
		FlexTable table;
		FlexCellFormatter cellFormatter;
		InlineLabel label;
		Image img;
		
		m_mainPanel.clear();
		
		panel = new FlowPanel();
		panel.addStyleName( "loginDlg_confirmationPanel" );
		
		table = new FlexTable();
		cellFormatter = table.getFlexCellFormatter();
		panel.add( table );
		
		// Add an image
		img = new Image( GwtTeaming.getImageBundle().emailConfirmation() );
		img.addStyleName( "loginDlg_confirmationImg" );
		table.setWidget( 0, 0, img );
		cellFormatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
		
		// Add a message telling the user to check their email.
		label = new InlineLabel( GwtTeaming.getMessages().loginDlg_ConfirmationText() );
		label.addStyleName( "loginDlg_confirmationHint" );
		table.setWidget( 0, 1, label );
		
		m_registerBtn.setVisible( false );
		
		m_mainPanel.add( panel );
	}
	
	/**
	 * An external user needs to complete the registration process.  Show the UI needed for that.
	 */
	private void showExternalUserRegistrationUI()
	{
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				Window.alert(caught.getMessage());
				showExternalUserRegistrationUI(false);
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				Object obj;
				Scheduler.ScheduledCommand cmd;
				obj = result.getResponseData();
				if ( obj != null && obj instanceof ZoneShareTerms && ((ZoneShareTerms)obj).isShowTermsAndConditions())
				{
					m_zoneShareTerms = (ZoneShareTerms) obj;
					showExternalUserRegistrationUI(true);									
				}
				else
				{
					showExternalUserRegistrationUI(false);
				}
			}
		};
		GetZoneShareTermsCmd cmd = new GetZoneShareTermsCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	private void showExternalUserRegistrationUI(boolean showTermsAndConditions)
	{
		final GwtTeamingMessages messages;
		FlowPanel panel;
		
		m_mainPanel.clear();

		messages = GwtTeaming.getMessages();
		
		createFormPanel();
		
		panel = new FlowPanel();
		panel.addStyleName( "loginDlg_RegistrationRequiredPanel" );
		m_mainPanel.add( panel );
		
		// Hide the ok and cancel buttons
		{
			Element okElement;
			Element cancelElement;
			
			okElement = Document.get().getElementById( "loginOkBtn" );
			m_okBtn = Button.wrap( okElement );
			m_okBtn.setVisible( false );
			m_okTouchBtn = new TouchButton( m_okBtn );
			
			cancelElement = Document.get().getElementById( "loginCancelBtn" );
			m_cancelBtn = Button.wrap( cancelElement );
			m_cancelBtn.setText( messages.loginDlg_EnterAsGuest() );
			m_cancelBtn.addClickHandler( this );
			m_cancelBtn.setVisible( false );
			m_cancelTouchBtn = new TouchButton( m_cancelBtn );
		}

		// Show the Register button
		{
			final Element element = Document.get().getElementById( "loginRegisterBtn" );
			m_registerBtn = Button.wrap( element );
			m_registerBtn.setText( messages.loginDlg_Register() );
			m_registerBtn.setVisible( true );
			m_registerTouchBtn = new TouchButton( m_registerBtn );
			
			final Element termsContainerElement=Document.get().getElementById("termsContainer");
			final Element termsLabel=Document.get().getElementById("acceptTermsAnchor");
			final Element acceptTermsCheckBox=Document.get().getElementById("acceptTermsCheckBox");
			
			if(showTermsAndConditions){
				m_registerBtn.removeFromParent();				
				termsContainerElement.getStyle().setDisplay(Display.INLINE_BLOCK);
				Event.sinkEvents(termsLabel, Event.ONCLICK);
				Event.setEventListener(termsLabel, new EventListener() {					
					@Override
					public void onBrowserEvent(Event event) {
						String unescapedHtml=new HTML(m_zoneShareTerms.getTermsAndConditions()).getText();
						openTermsAndConditionsWindow(messages.loginDlg_TermsLabel(),unescapedHtml,messages.loginDlg_TermsPopupBlockMessage());
					}
				});
				
				m_registerBtn.setEnabled(false);
				m_registerBtn.setStyleName("teamingButton");
				Event.sinkEvents(acceptTermsCheckBox, Event.ONCLICK);
				Event.setEventListener(acceptTermsCheckBox, new EventListener() {					
					@Override
					public void onBrowserEvent(Event event) {
						if(((InputElement)acceptTermsCheckBox).isChecked())
						{
							m_registerBtn.setEnabled(true);							
							m_registerBtn.addStyleName("gwt-Button");
						}
						else{
							m_registerBtn.setEnabled(false);
							m_registerBtn.setStyleName("teamingButton");
						}
					}
				});
			}
			
			m_registerBtn.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							handleClickOnRegisterBtn();
						}
					});
				}
			});		
		}

		// Create a panel that holds the self registration controls
		{
			FlexTable table;
			Label label;
			int row;

			m_selfRegPanel = new FlowPanel();
			m_selfRegPanel.addStyleName( "loginDlg_selfRegPanel" );
			
			// Add some instructions
			label = new Label( messages.loginDlg_ExtUserRegistrationHint() );
			label.addStyleName( "loginDlg_extUserSelfRegHint" );
			m_selfRegPanel.add( label );
			
			panel.add( m_selfRegPanel );
			
			row = 0;
			table = new FlexTable();
			table.setWidth("100%");
			
			m_selfRegPanel.add( table );
			
			// Add a read-only control for the user id
			{
				TextBox txtBox;
				
				label = new Label( messages.loginDlgUserId() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
	
				txtBox = new TextBox();
				txtBox.setReadOnly( true );
				txtBox.setValue( GwtTeaming.getMainPage().getLoginExternalUserName() );
				txtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, txtBox );
			
				++row;
			}
			
			// Add the controls for the first name
			{
				label = new Label( messages.loginDlg_FirstNameLabel() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
	
				m_firstNameTxtBox = new TextBox();
				m_firstNameTxtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, m_firstNameTxtBox );
			
				++row;
			}
			
			// Add the controls for the last name
			{
				label = new Label( messages.loginDlg_LastNameLabel() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
	
				m_lastNameTxtBox = new TextBox();
				m_lastNameTxtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, m_lastNameTxtBox );
				
				++row;
			}
			
			// Add the controls for the password
			{
				label = new InlineLabel( messages.loginDlg_PwdLabel() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
				
				m_pwd1TxtBox = new PasswordTextBox();
				m_pwd1TxtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, m_pwd1TxtBox );
				++row;
			}

			// Add the controls for the reenter password
			{
				label = new InlineLabel( messages.loginDlg_ReenterPwdLabel() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
				
				m_pwd2TxtBox = new PasswordTextBox();
				m_pwd2TxtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, m_pwd2TxtBox );
				++row;
			}
			
			//Add the terms and conditions txtarea
			/*if(showTermsAndConditions){
				label=new InlineLabel(messages.loginDlg_TermsLabel());
				table.getFlexCellFormatter().setColSpan(row, 0, 2);
				table.getFlexCellFormatter().addStyleName(row, 0, "loginDlg_TermsLabelHeader");
				table.setHTML(row, 0, label.getElement().getInnerHTML());
				++row;
					
				FlowPanel termsPanel=new FlowPanel();
				termsPanel.getElement().setInnerHTML(m_zoneShareTerms.getTermsAndConditions());
				table.getFlexCellFormatter().setColSpan(row, 0, 2);
				table.getFlexCellFormatter().addStyleName(row, 0, "loginDlg_TermsTxtarea");
				table.setWidget(row, 0, termsPanel);
			}*/
		}
		
		// Hide the user name and password controls that are used for regular authentication
		{
			Element element;
			TextBox txtBox;
			PasswordTextBox pwd;
			InlineLabel label;
			
			element = Document.get().getElementById( "userIdLabel" );
			if ( element != null )
			{
				label = InlineLabel.wrap( element );
				label.setVisible( false );
			}

			element = Document.get().getElementById( "j_usernameId" );
			if ( element != null )
			{
				txtBox = TextBox.wrap( element );
				txtBox.setVisible( false );
			}

			element = Document.get().getElementById( "pwdLabel" );
			if ( element != null )
			{
				label = InlineLabel.wrap( element );
				label.setVisible( false );
			}

			element = Document.get().getElementById( "j_passwordId" );
			if ( element != null )
			{
				pwd = PasswordTextBox.wrap( element );
				pwd.setVisible( false );
			}
		}

		m_mainPanel.add( m_formPanel );
	}
	
	public static native void openTermsAndConditionsWindow(String title,String html,String popuBlockErrorMessage) /*-{		
	    var x=screen.width/2 - 700/2;
	    var y=screen.width/2 - 450/2;
	    if(typeof(termsWindow) != "object")
	    {
			termsWindow=window.open("",title,'width=700, height=485, top=' + y + ', left=' + x);
			if(termsWindow && termsWindow.top)
			{
				termsWindow.document.write(html);
				termsWindow.focus();
			}
			else
			{
				alert(popuBlockErrorMessage);
			}
		}
		else
		{
			termsWindow=window.open("",title,'width=700, height=485, top=' + y + ', left=' + x);
			termsWindow.document.write(html);
			termsWindow.focus();
		}	
	}-*/;
	
	/**
	 * Show the ui needed for an external user to reset their password.
	 */
	private void showExternalUserPwdResetUI()
	{
		GwtTeamingMessages messages;
		FlowPanel panel;
		
		m_mainPanel.clear();

		messages = GwtTeaming.getMessages();
		
		createFormPanel();
		
		panel = new FlowPanel();
		panel.addStyleName( "loginDlg_ExtUserPwdResetPanel" );
		m_mainPanel.add( panel );
		
		// Hide the ok and cancel buttons
		{
			Element okElement;
			Element cancelElement;
			
			okElement = Document.get().getElementById( "loginOkBtn" );
			m_okBtn = Button.wrap( okElement );
			m_okBtn.setVisible( false );
			m_okTouchBtn = new TouchButton( m_okBtn );
			
			cancelElement = Document.get().getElementById( "loginCancelBtn" );
			m_cancelBtn = Button.wrap( cancelElement );
			m_cancelBtn.setText( messages.loginDlg_EnterAsGuest() );
			m_cancelBtn.addClickHandler( this );
			m_cancelBtn.setVisible( false );
			m_cancelTouchBtn = new TouchButton( m_cancelBtn );
		}

		// Show the "Reset password" button
		{
			Element element;
			
			element = Document.get().getElementById( "resetPwdBtn" );
			m_pwdResetBtn = Button.wrap( element );
			m_pwdResetBtn.setText( messages.loginDlg_ResetPwd() );
			m_pwdResetBtn.setVisible( true );
			m_pwdResetTouchBtn = new TouchButton( m_pwdResetBtn );
			
			m_pwdResetBtn.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							handleClickOnResetPwdBtn();
						}
					} );
				}
			} );
		}

		// Create a panel that holds the password reset controls
		{
			FlexTable table;
			FlowPanel pwdResetPanel;
			Label label;
			int row;

			pwdResetPanel = new FlowPanel();
			pwdResetPanel.addStyleName( "loginDlg_ExtUserPwdResetPanel" );
			
			// Add some instructions
			label = new Label( messages.loginDlg_ExtUserPwdResetHint() );
			label.addStyleName( "loginDlg_ExtUserPwdResetHint" );
			pwdResetPanel.add( label );
			
			panel.add( pwdResetPanel );
			
			row = 0;
			table = new FlexTable();
			
			pwdResetPanel.add( table );
			
			// Add a read-only control for the user id
			{
				TextBox txtBox;
				
				label = new Label( messages.loginDlgUserId() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
	
				txtBox = new TextBox();
				txtBox.setReadOnly( true );
				txtBox.setValue( GwtTeaming.getMainPage().getLoginExternalUserName() );
				txtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, txtBox );
			
				++row;
			}
			
			// Add the controls for the password
			{
				label = new InlineLabel( messages.loginDlg_PwdLabel() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
				
				m_pwd1TxtBox = new PasswordTextBox();
				m_pwd1TxtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, m_pwd1TxtBox );
				++row;
			}

			// Add the controls for the reenter password
			{
				label = new InlineLabel( messages.loginDlg_ReenterPwdLabel() );
				table.setHTML( row, 0, label.getElement().getInnerHTML() );
				
				m_pwd2TxtBox = new PasswordTextBox();
				m_pwd2TxtBox.setVisibleLength( 20 );
				table.setWidget( row, 1, m_pwd2TxtBox );
				++row;
			}
		}
		
		// Hide the user name and password controls that are used for regular authentication
		{
			Element element;
			TextBox txtBox;
			PasswordTextBox pwd;
			InlineLabel label;
			
			element = Document.get().getElementById( "userIdLabel" );
			if ( element != null )
			{
				label = InlineLabel.wrap( element );
				label.setVisible( false );
			}

			element = Document.get().getElementById( "j_usernameId" );
			if ( element != null )
			{
				txtBox = TextBox.wrap( element );
				txtBox.setVisible( false );
			}

			element = Document.get().getElementById( "pwdLabel" );
			if ( element != null )
			{
				label = InlineLabel.wrap( element );
				label.setVisible( false );
			}

			element = Document.get().getElementById( "j_passwordId" );
			if ( element != null )
			{
				pwd = PasswordTextBox.wrap( element );
				pwd.setVisible( false );
			}
		}

		m_mainPanel.add( m_formPanel );
	}
	
	/**
	 * Show the message when the external user has requested to reset their password.
	 */
	private void showExternalUserPasswordResetRequestedUI()
	{
		FlowPanel panel;
		FlexTable table;
		FlexCellFormatter cellFormatter;
		InlineLabel label;
		Image img;
		
		m_mainPanel.clear();
		
		panel = new FlowPanel();
		panel.addStyleName( "loginDlg_confirmationPanel" );
		
		table = new FlexTable();
		cellFormatter = table.getFlexCellFormatter();
		panel.add( table );
		
		// Add an image
		img = new Image( GwtTeaming.getImageBundle().emailConfirmation() );
		img.addStyleName( "loginDlg_confirmationImg" );
		table.setWidget( 0, 0, img );
		cellFormatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
		
		// Add a message telling the user to check their email.
		label = new InlineLabel( GwtTeaming.getMessages().loginDlg_PasswordResetRequested() );
		label.addStyleName( "loginDlg_confirmationHint" );
		table.setWidget( 0, 1, label );
		
		m_pwdResetBtn.setVisible( false );
		
		m_mainPanel.add( panel );
	}
	
	/**
	 * Show the UI needed for regular login
	 */
	private void showRegularLoginUI()
	{
		m_mainPanel.clear();
		
		createFormPanel();
		
		// Add a row for the "user id" controls.
		{
			Element userIdElement;
			String userName;
			
			m_userIdLabelElement = Document.get().getElementById( "userIdLabel" );
			m_userIdLabelElement.setInnerText( GwtTeaming.getMessages().loginDlgUserId() );
			
			userIdElement = Document.get().getElementById( "j_usernameId" );
			m_userIdTxtBox = TextBox.wrap( userIdElement );
			
			// Do we have the user name for an external user trying to log in for the first time?
			userName = GwtTeaming.getMainPage().getLoginExternalUserName();
			if ( userName != null && userName.length() > 0 )
				m_userIdTxtBox.setValue( userName );
		}
		
		// Add a row for the "password" controls.
		{
			Element pwdTxtBoxElement;
			
			m_pwdLabelElement = Document.get().getElementById( "pwdLabel" );
			m_pwdLabelElement.setInnerText( GwtTeaming.getMessages().loginDlgPassword() );
			
			pwdTxtBoxElement = Document.get().getElementById( "j_passwordId" );
			m_pwdTxtBox = PasswordTextBox.wrap( pwdTxtBoxElement );
		}
		
		// Add the controls needed for captcha
		{
			Element captchaElement;
			
			captchaElement = Document.get().getElementById( "kaptcha-img" );
			if ( captchaElement != null )
				m_captchaImg = Image.wrap( captchaElement );
			
			captchaElement = Document.get().getElementById( "kaptcha-repsponse" );
			if ( captchaElement != null )
				m_captchaResponseTxtBox = TextBox.wrap( captchaElement );
		}
		
		// Add the controls for KeyShield two-stage authentication.
		m_keyShieldTwoStageAuthPanel = Document.get().getElementById("loginDlgKeyShieldPanel"       );
		m_keyShieldErrorMessage      = Document.get().getElementById("loginDlgKeyShieldErrorMessage");
		m_keyShieldRefererPanel      = Document.get().getElementById("loginDlgKeyShieldRefererPanel");
		m_keyShieldRefererLink       = Document.get().getElementById("loginDlgKeyShieldRefererLink" );
		
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
		
		// Create the ok and cancel buttons
		{
			Element okElement;
			Element cancelElement;
			
			okElement = Document.get().getElementById( "loginOkBtn" );
			m_okBtn = Button.wrap( okElement );
			m_okBtn.setText( GwtTeaming.getMessages().login() );
			m_okTouchBtn = new TouchButton( m_okBtn );
			
			cancelElement = Document.get().getElementById( "loginCancelBtn" );
			m_cancelBtn = Button.wrap( cancelElement );
			m_cancelBtn.setText( GwtTeaming.getMessages().loginDlg_EnterAsGuest() );
			m_cancelBtn.addClickHandler( this );
			m_cancelBtn.setVisible( false );
			m_cancelTouchBtn = new TouchButton( m_cancelBtn );
		}
		
		// Create a "Forgot Your Password?" link.
		{
			ClickHandler clickHandler;
//			MouseOverHandler mouseOverHandler;
//			MouseOutHandler mouseOutHandler;
			Element forgotPwdElement;
			
			forgotPwdElement = Document.get().getElementById( "forgottenPwdSpan" );
			forgotPwdElement.setInnerText( GwtTeaming.getMessages().loginDlg_ForgottenPwd() );
			m_forgotPwdLink = InlineLabel.wrap( forgotPwdElement );
			m_forgotPwdLink.setVisible( true );
			
			// Add a clickhandler to the "Forgot your password?" link.  When the user clicks on the link we
			// will invoke the "Forgotten password" dialog.
			clickHandler = new ClickHandler()
			{
				/**
				 * Clear all branding information.
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeForgottenPwdDlg();
						}
					} );
				}
			};
			m_forgotPwdLink.addClickHandler( clickHandler );
		}

		// Create a "Create new account" link that will initially be hidden.
		// We will show this link later when we get the response to our request to get
		// self registration info and self registration is allowed.
		{
			ClickHandler clickHandler;
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							String url;
							
							// Get the url we need to invoke the "Create User" page.
							url = m_selfRegInfo.getCreateUserUrl();

							// Invoke the "Create User" page in a new window.
							Window.open( url, "self_reg_create_new_account", "height=750,resizeable,scrollbars,width=750" );
						}
					} );
				}
			};
			m_selfRegLink.addClickHandler( clickHandler );
		}
		
		m_mainPanel.add( m_formPanel );
	}
	
	/**
	 * 
	 */
	private void showLoginFailedMsg( LoginStatus loginStatus )
	{
		String msg;
		switch ( loginStatus )
		{
		case WebAccessRestricted:  msg = GwtTeaming.getMessages().loginDlgLoginWebAccessRestricted(); break;
		default:                   msg = GwtTeaming.getMessages().loginDlgLoginFailed();              break;
		}
		m_loginFailedMsg.setText(    msg  );
		m_loginFailedMsg.setVisible( true );
	}


	/*
	 * Asynchronously runs the change password dialog so the given
	 * user can change their password.
	 */
	private void showChangePasswordDlgAsync( final Long userId )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute() {
				showChangePasswordDlgNow( userId );
			}
		} );
	}
	
	/*
	 * Synchronously runs the change password dialog so the given
	 * user can change their password.
	 */
	private void showChangePasswordDlgNow( final Long userId )
	{
		// Have we instantiated the change password dialog before?
		if ( null == m_changePwdDlg )
		{
			// No!  Instantiate one now.
			ChangePasswordDlg.createAsync( new ChangePasswordDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ChangePasswordDlg cpDlg )
				{
					m_changePwdDlg = cpDlg;
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							showChangePasswordDlgNow( userId );
						}
					} );
				}
			});
		}
		else
		{
			// Yes, we've instantiated change password dialog already!
			// Simply show it.
			m_changePwdDlg.init( GwtTeaming.getMessages().loginDlg_PasswordExpiredHint(), userId );
			m_changePwdDlg.show( true                                                            );
		}
	}
	
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
	}
	
	/**
	 * Return whether we should use OpenID authentication 
	 */
	private boolean getUseOpenIDAuthentication()
	{
		if ( m_useOpenIdCkbox != null && m_useOpenIdCkbox.isVisible() && m_useOpenIdCkbox.getValue() == true )
			return true;
		
		return false;
	}

	/**
	 * Invoke the "Forgotten Password" dialog
	 */
	private void invokeForgottenPwdDlg()
	{
		if ( m_forgottenPwdDlg == null )
		{
			ForgottenPwdDlg.createAsync(
										false, 
										true,
										new ForgottenPwdDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ForgottenPwdDlg fpDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_forgottenPwdDlg = fpDlg;
							
							fpDlg.init();
							fpDlg.showRelativeTo( m_forgotPwdLink );
						}
					} );
				}
			} );
		}
		else
		{
			m_forgottenPwdDlg.init();
			m_forgottenPwdDlg.showRelativeTo( m_forgotPwdLink );
		}
	}

	/**
	 * Handles DialogClosedEvent's received by this class.
	 * 
	 * Implements the DialogClosedSetEvent.Handler.onDialogClosed() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDialogClosed(DialogClosedEvent event) {
		// Is this a notification that the ChangePasswordDlg is
		// closing?
		DlgBox eventDialog = event.getDlgBox();
		if ( ( null != m_changePwdDlg ) && ( m_changePwdDlg == eventDialog ) )
		{
			// Yes!  Put the focus in the LoginDlg's focus widget.
			final FocusWidget fw = getFocusWidget();
			if ( null != fw )
			{
				GwtClientHelper.setFocusDelayed( fw );
			}
		}
	}
	
	/**
	 * Handles WindowTitleSetEvent's received by this class.
	 * 
	 * Implements the WindowTitleSetEvent.Handler.onWindowTitleSet() method.
	 * 
	 * @param event
	 */
	@Override
	public void onWindowTitleSet(WindowTitleSetEvent event) {
		final String  productTitle  = GwtClientHelper.getRequestInfo().getProductName(); 
		final String  eventTitle    = event.getWindowTitle();
		final boolean hasEventTitle = GwtClientHelper.hasString( eventTitle );
		if ( ( ! hasEventTitle ) || ( ! eventTitle.equals( productTitle ) ) )
		{
			m_preLoginTitle =
				( hasEventTitle ?
					eventTitle  :
					GwtClientHelper.jsGetMainTitle() );
			
			setWindowTitleAsync( productTitle );
		}
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
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
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ( GwtClientHelper.hasItems( m_registeredEventHandlers ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	/*
	 * Asynchronously sets the window title.
	 */
	private void setWindowTitleAsync( final String windowTitle )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setWindowTitleNow( windowTitle );
			}
		});
	}
	
	/*
	 * Synchronously sets the window title.
	 */
	private void setWindowTitleNow( final String windowTitle )
	{
		// Set the window title we were given...
		GwtClientHelper.jsSetMainTitle( windowTitle );
		
		// ...and if we're in UI debug mode...
		if ( GwtClientHelper.isDebugUI() )
		{
			// ...set the window title to the name of the GWT user
			// ...agent to facilitate debugging incorrect GWT
			// ...permutation issues.
			AgentBase agent = GWT.create(Agent.class);
			String agentTitle = ("GWT user.agent: " + agent.getAgentName());
			GwtClientHelper.jsSetMainTitle( agentTitle, false );	// false -> Don't send the window title changed event.
		}
	}
	
	/**
	 * Called when the login dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event
		// handlers...
		super.onAttach();
		registerEvents();
		
		// ...and set the window title to reflect the login
		// ...dialog.
		setWindowTitleAsync( GwtClientHelper.getRequestInfo().getProductName() );
	}
	
	/**
	 * Called when the login dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
		
		// ...and restore the window title to what it was before the
		// ...login dialog ran.
		if ( null != m_preLoginTitle )
		{
			setWindowTitleNow( m_preLoginTitle );
			m_preLoginTitle = null;
		}
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the login dialog and perform some operation on it.            */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the dialog asynchronously
	 * after it loads. 
	 */
	public interface LoginDlgClient
	{
		void onSuccess( LoginDlg dlg );
		void onUnavailable();
	}
	
	/**
	 * Loads the LoginDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final Object properties,
							final String loginUrl,
							final String springSecurityRedirect,
							final LoginDlgClient loginDlgClient )
	{
		GWT.runAsync( LoginDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LoginDlg() );
				if ( loginDlgClient != null )
				{
					loginDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				LoginDlg loginDlg;
				
				loginDlg = new LoginDlg(
									autoHide,
									modal,
									left,
									top,
									properties,
									loginUrl,
									springSecurityRedirect );
				loginDlgClient.onSuccess( loginDlg );
			}
		});
	}
}
