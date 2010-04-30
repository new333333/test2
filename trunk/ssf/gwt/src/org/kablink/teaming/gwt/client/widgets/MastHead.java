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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget will display the MastHead 
 */
public class MastHead extends Composite
	implements ActionRequestor, ClickHandler, MouseOutHandler, MouseOverHandler
{
	private BrandingPanel m_siteBrandingPanel = null;
	private BrandingPanel m_binderBrandingPanel = null;
	private RequestInfo m_requestInfo = null;
	private String m_mastheadBinderId = null;
	private FlowPanel m_mainMastheadPanel = null;
	private FlowPanel m_globalActionsPanel = null;
	private InlineLabel m_mouseOverHint = null;
	private Image m_adminImg1 = null;
	private Image m_adminImg2 = null;
	private Image m_personalPrefsImg1 = null;
	private Image m_personalPrefsImg2 = null;
	private Image m_logoutImg1 = null;
	private Image m_logoutImg2 = null;
	private Image m_loginImg1 = null;
	private Image m_loginImg2 = null;
	private Image m_helpImg1 = null;
	private Image m_helpImg2 = null;
	private Anchor m_adminLink = null;
	private Anchor m_personalPrefsLink = null;
	private Anchor m_logoutLink = null;
	private Anchor m_loginLink = null;
	private Anchor m_helpLink = null;
	private InlineLabel m_userName = null;

	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private GwtBrandingData m_siteBrandingData = null;
	private GwtBrandingData m_binderBrandingData = null;
	
	// m_rpcGetSiteBrandingCallback is our callback that gets called when the ajax request to get the site branding data completes.
	private AsyncCallback<GwtBrandingData> m_rpcGetSiteBrandingCallback = null;

	// m_rpcGetBinderBrandingCallback is our callback that gets called when the ajax request to get the binder branding data completes.
	private AsyncCallback<GwtBrandingData> m_rpcGetBinderBrandingCallback = null;
	
	
	
	/**
	 * 
	 */
	public MastHead( RequestInfo requestInfo )
	{
		Command cmd = null;
		
        m_requestInfo = requestInfo;
        m_mastheadBinderId = m_requestInfo.getBinderId();
		
		m_mainMastheadPanel = new FlowPanel();
		m_mainMastheadPanel.addStyleName( "mastHead" );
		
		// Create a branding panel that will display "site" branding if it exists.
		m_siteBrandingPanel = new BrandingPanel( requestInfo );
		m_siteBrandingPanel.setVisible( false );
		m_mainMastheadPanel.add( m_siteBrandingPanel );
		
		// Create a branding panel that will display the binder branding.
		m_binderBrandingPanel = new BrandingPanel( requestInfo );
		m_binderBrandingPanel.setVisible( false );
		m_mainMastheadPanel.add( m_binderBrandingPanel );
		
		// Create the panel that will hold the global actions such as Administration", "Logout" etc
		{
			m_globalActionsPanel = new FlowPanel();
			m_globalActionsPanel.addStyleName( "mastheadGlobalActionsPanel" );
			
			// Create a label that holds the logged-in user's name.
			{
				m_userName = new InlineLabel( requestInfo.getUserName() );
				m_userName.setStylePrimaryName( "mastheadUserName" );
				m_userName.addClickHandler( this );
				m_userName.addMouseOverHandler( this );
				m_userName.addMouseOutHandler( this );
				m_globalActionsPanel.add( m_userName );
			}
			
			
			// Create a place to hold the mouse-over hint.
			m_mouseOverHint = new InlineLabel();
			m_mouseOverHint.addStyleName( "mastheadGlobalActionsMouseOverHint" );
			m_globalActionsPanel.add( m_mouseOverHint );

			// Add the global actions to the masthead.
			addAdministrationAction();
			addPersonalPreferencesAction();
			addLoginLogoutAction();
			addHelpAction();
			
			m_mainMastheadPanel.add( m_globalActionsPanel );
		}
		
		// Create the callback that will be used when we issue an ajax call to get the site branding
		m_rpcGetSiteBrandingCallback = new AsyncCallback<GwtBrandingData>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				String errMsg;
				String cause;
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )
				{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( "Home Workspace" );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( "Home Workspace" );
					else
						cause = messages.errorUnknownException();
				}
				else
				{
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getBrandingRPCFailed( cause );
				Window.alert( errMsg );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtBrandingData brandingData )
			{
				// Update the site branding panel with the branding data
				m_siteBrandingData = brandingData;
				m_siteBrandingPanel.updateBrandingPanel( brandingData );

				// Issue an ajax request to get the binder branding
				getBinderBrandingDataFromServer();
			}// end onSuccess()
		};

		// Create the callback that will be used when we issue an ajax call to get the binder branding
		m_rpcGetBinderBrandingCallback = new AsyncCallback<GwtBrandingData>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				String errMsg;
				String cause;
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )
				{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( m_mastheadBinderId );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( m_mastheadBinderId );
					else
						cause = messages.errorUnknownException();
				}
				else
				{
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getBrandingRPCFailed( cause );
				Window.alert( errMsg );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtBrandingData brandingData )
			{
				// Update the binder branding panel with the branding data
				m_binderBrandingData = brandingData;
				m_binderBrandingPanel.updateBrandingPanel( brandingData );
				
				// Display site and binder branding based on the branding rule found in
				// the site branding.
				showBranding();
			}// end onSuccess()
		};

		// All composites must call initWidget() in their constructors.
		initWidget( m_mainMastheadPanel );

		// Issue an ajax request to get the site branding.
        cmd = new Command()
        {
        	/**
        	 * 
        	 */
            public void execute()
            {
				getSiteBrandingDataFromServer();
            }
        };
        DeferredCommand.addCommand( cmd );
	}// end MastHead()


	/**
	 * Called to add an ActionHandler to this masthead
	 * @param actionHandler
	 */
	public void addActionHandler( ActionHandler actionHandler )
	{
		m_actionHandlers.add( actionHandler );
		
		m_siteBrandingPanel.addActionHandler( actionHandler );
		m_binderBrandingPanel.addActionHandler( actionHandler );
	}// end addActionHandler()
	

	/**
	 * Add the "administration" action to the global actions part of the masthead.
	 */
	private void addAdministrationAction()
	{
		ImageResource imgResource;
		Element linkElement;

		// Currently don't need the url to invoke the "site administration" page because that page is
		// going to be reworked in GWT.  If that work does not get done we can use the url to invoke
		// the jsp-based "site administration" page.
		m_adminLink = new Anchor();
		m_adminLink.addStyleName( "brandingLink" );
		m_adminLink.addClickHandler( this );
		m_adminLink.addMouseOutHandler( this );
		m_adminLink.addMouseOverHandler( this );
		linkElement = m_adminLink.getElement();
		
		// Add the mouse-out image to the link.
		imgResource = GwtTeaming.getImageBundle().administration1();
		m_adminImg1 = new Image( imgResource );
		linkElement.appendChild( m_adminImg1.getElement() );
		
		// Add the mouse-over image to the link.
		imgResource = GwtTeaming.getImageBundle().administration2();
		m_adminImg2 = new Image( imgResource );
		m_adminImg2.setVisible( false );
		linkElement.appendChild( m_adminImg2.getElement() );
		
		// Set the "administration" link to not be visible.
		m_adminLink.setVisible( false );
		
		// Issue an ajax request to see if the user has the rights to run the "site administration" page.
		{
			AsyncCallback<String> rpcCallback;

			// No
			rpcCallback = new AsyncCallback<String>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					// The user does not have the rights to run the "site administration" page.
					m_adminLink.setVisible( false );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( String url )
				{
					// Did we get a url for the "site administration" action?
					if ( url != null && url.length() > 0 )
					{
						// Yes
						m_adminLink.setVisible( true );
					}
				}// end onSuccess()
			};
			
			// Issue an ajax request to get the url for the "site administration" action.
			GwtTeaming.getRpcService().getSiteAdministrationUrl( m_mastheadBinderId, rpcCallback );
			
		}
		
		m_globalActionsPanel.add( m_adminLink );
	}// end addAdministrationAction()
	
	
	/**
	 * Add the "help" action to the global actions part of the masthead.
	 */
	private void addHelpAction()
	{
		ImageResource imgResource;
		Element linkElement;

		m_helpLink = new Anchor();
		m_helpLink.addStyleName( "brandingLink" );
		m_helpLink.addClickHandler( this );
		m_helpLink.addMouseOutHandler( this );
		m_helpLink.addMouseOverHandler( this );
		linkElement = m_helpLink.getElement();
		
		// Add the mouse-out image to the link.
		imgResource = GwtTeaming.getImageBundle().help1();
		m_helpImg1 = new Image( imgResource );
		linkElement.appendChild( m_helpImg1.getElement() );
		
		// Add the mouse-over image to the link.
		imgResource = GwtTeaming.getImageBundle().help2();
		m_helpImg2 = new Image( imgResource );
		m_helpImg2.setVisible( false );
		linkElement.appendChild( m_helpImg2.getElement() );
		
		m_globalActionsPanel.add( m_helpLink );
	}// end addHelpAction()
	
	
	/**
	 * Add the "login" or "logout" action to the global actions part of the masthead.
	 */
	private void addLoginLogoutAction()
	{
		ImageResource imgResource;
		Element linkElement;

		// Is the user logged in?
		if ( m_requestInfo.isUserLoggedIn() )
		{
			// Yes, add the "logout" action.
			m_logoutLink = new Anchor();
			m_logoutLink.addStyleName( "brandingLink" );
			m_logoutLink.addClickHandler( this );
			m_logoutLink.addMouseOutHandler( this );
			m_logoutLink.addMouseOverHandler( this );
			linkElement = m_logoutLink.getElement();
			
			// Add the mouse-out image to the link.
			imgResource = GwtTeaming.getImageBundle().logout1();
			m_logoutImg1 = new Image( imgResource );
			linkElement.appendChild( m_logoutImg1.getElement() );
			
			// Add the mouse-over image to the link.
			imgResource = GwtTeaming.getImageBundle().logout2();
			m_logoutImg2 = new Image( imgResource );
			m_logoutImg2.setVisible( false );
			linkElement.appendChild( m_logoutImg2.getElement() );
			
			m_globalActionsPanel.add( m_logoutLink );
		}
		else
		{
			// No, add the "login" action.
			m_loginLink = new Anchor();
			m_loginLink.addStyleName( "brandingLink" );
			m_loginLink.addClickHandler( this );
			m_loginLink.addMouseOutHandler( this );
			m_loginLink.addMouseOverHandler( this );
			linkElement = m_loginLink.getElement();
			
			// Add the mouse-out image to the link.
			imgResource = GwtTeaming.getImageBundle().login1();
			m_loginImg1 = new Image( imgResource );
			linkElement.appendChild( m_loginImg1.getElement() );
			
			// Add the mouse-over image to the link.
			imgResource = GwtTeaming.getImageBundle().login2();
			m_loginImg2 = new Image( imgResource );
			m_loginImg2.setVisible( false );
			linkElement.appendChild( m_loginImg2.getElement() );
			
			m_globalActionsPanel.add( m_loginLink );
		}
	}// end addLogoutAction()
	
	
	/**
	 * If the user is logged in, add the "personal preferences" link to the global actions panel.
	 */
	private void addPersonalPreferencesAction()
	{
		// Is the user logged in?
		if ( m_requestInfo.isUserLoggedIn() )
		{
			ImageResource imgResource;
			Element linkElement;

			// Yes, add the "personal preferences" action.
			m_personalPrefsLink = new Anchor();
			m_personalPrefsLink.addStyleName( "brandingLink" );
			m_personalPrefsLink.addClickHandler( this );
			m_personalPrefsLink.addMouseOutHandler( this );
			m_personalPrefsLink.addMouseOverHandler( this );
			linkElement = m_personalPrefsLink.getElement();
			
			// Add the mouse-out image to the link.
			imgResource = GwtTeaming.getImageBundle().personalPrefs1();
			m_personalPrefsImg1 = new Image( imgResource );
			linkElement.appendChild( m_personalPrefsImg1.getElement() );
			
			// Add the mouse-over image to the link.
			imgResource = GwtTeaming.getImageBundle().personalPrefs2();
			m_personalPrefsImg2 = new Image( imgResource );
			m_personalPrefsImg2.setVisible( false );
			linkElement.appendChild( m_personalPrefsImg2.getElement() );
			
			m_globalActionsPanel.add( m_personalPrefsLink );
		}
	}// end addPersonalPreferencesAction()
	
	
	/**
	 * Display the mouse-out image for the give widget and remove the mouse-over hint.
	 */
	private void doMouseOutActions( Widget eventSource )
	{
		// Display the mouse-out image for the appropriate link.
		if ( eventSource == m_adminLink )
		{
			m_adminImg1.setVisible( true );
			m_adminImg2.setVisible( false );
		}
		else if ( eventSource == m_personalPrefsLink )
		{
			m_personalPrefsImg1.setVisible( true );
			m_personalPrefsImg2.setVisible( false );
		}
		else if ( eventSource == m_logoutLink )
		{
			m_logoutImg1.setVisible( true );
			m_logoutImg2.setVisible( false );
		}
		else if ( eventSource == m_loginLink )
		{
			m_loginImg1.setVisible( true );
			m_loginImg2.setVisible( false );
		}
		else if ( eventSource == m_helpLink )
		{
			m_helpImg1.setVisible( true );
			m_helpImg2.setVisible( false );
		}
		else if ( eventSource == m_userName )
		{
			m_userName.removeStyleDependentName( "mouseOver" );
			m_userName.addStyleDependentName( "mouseOut" );
		}

		// Remove the mouse-over hint.
		m_mouseOverHint.setText( "" );
	}// end doMouseOutActions()
	
	
	/**
	 * Return the binder id we are working with.
	 */
	public String getBinderId()
	{
		return m_mastheadBinderId;
	}// end getBinderId()
	
	
	/**
	 * Return the branding data we are working with.
	 */
	public GwtBrandingData getBrandingData()
	{
		return m_binderBrandingPanel.getBrandingData();
	}// end GwtBrandingData()
	
	
	/**
	 * Issue an ajax request to get the binder branding data from the server.  Our AsyncCallback
	 * will be called when this request completes.
	 */
	private void getBinderBrandingDataFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		rpcService = GwtTeaming.getRpcService();
		
		// Do we have a binder id?
		if ( m_mastheadBinderId != null )
		{
			// Yes, Issue an ajax request to get the branding data for the given binder.
			rpcService.getBinderBrandingData( m_mastheadBinderId, m_rpcGetBinderBrandingCallback );
		}
	}// end getBinderBrandingDataFromServer()


	/**
	 * Return the site branding data.
	 */
	public GwtBrandingData getSiteBrandingData()
	{
		return m_siteBrandingData;
	}// end getSiteBrandingData()
	
	
	/**
	 * Issue an ajax request to get the site branding data from the server.  Our AsyncCallback
	 * will be called when this request completes.
	 */
	private void getSiteBrandingDataFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		rpcService = GwtTeaming.getRpcService();
		
		// Issue an ajax request to get the site branding data.
		rpcService.getSiteBrandingData( m_rpcGetSiteBrandingCallback );
	}// end getSiteBrandingDataFromServer()
	

	/**
	 * This method gets called when the user clicks on something in the branding panel.
	 */
	public void onClick( ClickEvent event )
	{
		Widget eventSource;
		
		// Get the widget that was clicked on.
		eventSource = (Widget) event.getSource();

		// Display the mouse out-image for the link that was clicked on and hide the hint.
		doMouseOutActions( eventSource );
		
		// Notify all ActionHandler that have registered.
		for (Iterator<ActionHandler> actionHandlerIT = m_actionHandlers.iterator(); actionHandlerIT.hasNext(); )
		{
			// Calling each ActionHandler
			if ( eventSource == m_adminLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.ADMINISTRATION, null );
			}
			else if ( eventSource == m_personalPrefsLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.EDIT_PERSONAL_PREFERENCES, null );
			}
			else if ( eventSource == m_logoutLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.LOGOUT, null );
			}
			else if ( eventSource == m_loginLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.LOGIN, null );
			}
			else if ( eventSource == m_helpLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.HELP, null );
			}
			else if ( eventSource == m_userName )
			{
				actionHandlerIT.next().handleAction( TeamingAction.MY_WORKSPACE, null );
			}
		}
	}// end onClick()
	
	
	/**
	 * This method gets called when the user mouses out of something in the branding panel
	 */
	public void onMouseOut( MouseOutEvent event )
	{
		Widget eventSource;
		
		// Get the widget that was clicked on.
		eventSource = (Widget) event.getSource();
		
		// Display the mouse-out image for the link that mouse left and hide the hint.
		doMouseOutActions( eventSource );
	}// onMouseOut()
	
	
	/**
	 * This method gets called when the user mouses over something in the branding panel
	 */
	public void onMouseOver( MouseOverEvent event )
	{
		Widget eventSource;
		String hint = "";
		
		// Get the widget that was clicked on.
		eventSource = (Widget) event.getSource();
		
		// Display the mouse-over image for the appropriate link.
		if ( eventSource == m_adminLink )
		{
			m_adminImg1.setVisible( false );
			m_adminImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().administrationHint();
		}
		else if ( eventSource == m_personalPrefsLink )
		{
			m_personalPrefsImg1.setVisible( false );
			m_personalPrefsImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().personalPreferencesHint();
		}
		else if ( eventSource == m_logoutLink )
		{
			m_logoutImg1.setVisible( false );
			m_logoutImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().logoutHint();
		}
		else if ( eventSource == m_loginLink )
		{
			m_loginImg1.setVisible( false );
			m_loginImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().loginHint();
		}
		else if ( eventSource == m_helpLink )
		{
			m_helpImg1.setVisible( false );
			m_helpImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().helpHint();
		}
		else if ( eventSource == m_userName )
		{
			m_userName.removeStyleDependentName( "mouseOut" );
			m_userName.addStyleDependentName( "mouseOver" );
			hint = "";
		}
		
		// Update the mouse-over hint.
		m_mouseOverHint.setText( hint );
	}// onMouseOver()
	

	/**
	 * Refresh the binder branding by issuing an ajax request to get the binder branding data . 
	 */
	public void refreshBinderBranding()
	{
		getBinderBrandingDataFromServer();
	}// end refreshBinderBranding()

	
	/**
	 * Refresh the site branding by issuing an ajax request to get the site branding data . 
	 */
	public void refreshSiteBranding()
	{
		getSiteBrandingDataFromServer();
	}// end refreshSiteBranding()

	
	/**
	 * Set the id of the binder the masthead is dealing with.
	 */
	public void setBinderId( String binderId )
	{
		// Did the binder id change?
		if ( m_mastheadBinderId == null || m_mastheadBinderId.equalsIgnoreCase( binderId ) == false )
		{
			// Yes
			m_mastheadBinderId = binderId;
			
			// Issue an ajax request to get the binder branding data for the given binder.
			getBinderBrandingDataFromServer();
		}
	}// end setBinderId()

	
	/**
	 * Set the font color used in the "global actions" panel.
	 */
	private void setGlobalActionsFontColor()
	{
		Element element;
		Style style;
		String fontColor;
		GwtBrandingData brandingData;
		
		// If the site branding is visible get the font color from it.  Otherwise, get the font color
		// from the binder branding.
		{
			if ( m_siteBrandingPanel.isVisible() )
				brandingData = m_siteBrandingData;
			else
				brandingData = m_binderBrandingData;

			fontColor = brandingData.getFontColor();
		}
		
		// For the given branding data, set the color of the font used in the "global actions" part of the branding panel
		element = m_globalActionsPanel.getElement();
		style = element.getStyle();
			
		// Do we have a font color?
		if ( fontColor != null && fontColor.length() > 0 )
		{
			// Yes
			// Change the color of the font used to display the user's name.
			style.setColor( fontColor );
		}
		else
		{
			// Go back to the font color defined in the style sheet.
			style.clearColor();
		}
	}// end setGlobalActionsFontColor()
	
	
	/**
	 * Show the site and binder branding based on the branding rule found in the site branding.
	 */
	private void showBranding()
	{
		// Do we have any site branding?
		if ( m_siteBrandingData.haveBranding() == false )
		{
			// No, just show the binder branding.
			m_siteBrandingPanel.setVisible( false );
			m_binderBrandingPanel.setVisible( true );
			
		}
		else
		{
			String siteBinderId;
			String binderId;
			
			// We have site branding.
			// Did the site branding and the binder branding come from the same binder?
			siteBinderId = m_siteBrandingData.getBinderId();
			binderId = m_binderBrandingData.getBinderId();
			if ( siteBinderId.equalsIgnoreCase( binderId ) )
			{
				// Yes, just display the binder branding.
				m_siteBrandingPanel.setVisible( false );
				m_binderBrandingPanel.setVisible( true );
			}
			else
			{
				GwtBrandingDataExt.BrandingRule brandingRule;
				
				// Display site and binder branding based on the branding rule found
				// in the site branding.
				brandingRule = m_siteBrandingData.getBrandingRule();
				switch ( brandingRule )
				{
				case DISPLAY_SITE_BRANDING_ONLY:
					m_siteBrandingPanel.setVisible( true );
					m_binderBrandingPanel.setVisible( false );
					break;
					
				case DISPLAY_BOTH_SITE_AND_BINDER_BRANDING:
					m_siteBrandingPanel.setVisible( true );
					
					// Do we have binder branding?
					if ( m_binderBrandingData.haveBranding() )
					{
						// Yes
						m_binderBrandingPanel.setVisible( true );
					}
					else
					{
						// No
						m_binderBrandingPanel.setVisible( false );
					}
					break;
					
				case BINDER_BRANDING_OVERRIDES_SITE_BRANDING:
					// Do we have binder branding?
					if ( m_binderBrandingData.haveBranding() )
					{
						// Yes
						m_siteBrandingPanel.setVisible( false );
						m_binderBrandingPanel.setVisible( true );
					}
					else
					{
						m_siteBrandingPanel.setVisible( true );
						m_binderBrandingPanel.setVisible( false );
					}
					break;
					
				case BRANDING_RULE_UNDEFINED:
					m_siteBrandingPanel.setVisible( false );
					m_binderBrandingPanel.setVisible( true );
					break;
					
				default:
					Window.alert( "Unknown branding rule" );
					break;
				}// end switch()
			}
		}
		
		// Set the font color used in the global actions panel.
		setGlobalActionsFontColor();
	}// end showBranding()
}// end MastHead
