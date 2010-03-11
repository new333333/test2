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

import org.kablink.teaming.gwt.client.ActionHandler;
import org.kablink.teaming.gwt.client.ActionRequestor;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.TeamingAction;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
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
	implements ActionRequestor, ClickHandler, LoadHandler, MouseOutHandler, MouseOverHandler
{
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private RequestInfo m_requestInfo = null;
	private String m_mastheadBinderId = null;
	private FlowPanel m_mainMastheadPanel = null;
	private FlowPanel m_mastheadBgPanel;
	private FlowPanel m_mastheadContentPanel = null;
//!!!	private BrandingPanel m_level1BrandingPanel = null;
	private BrandingPanel m_level2BrandingPanel = null;
	private FlowPanel m_globalActionsPanel;
	private Image m_defaultBgImg = null;
	private Image m_bgImg = null;
	private Image m_adminImg1 = null;
	private Image m_adminImg2 = null;
	private Image m_myWorkspaceImg1 = null;
	private Image m_myWorkspaceImg2 = null;
	private Image m_logoutImg1 = null;
	private Image m_logoutImg2 = null;
	private Image m_helpImg1 = null;
	private Image m_helpImg2 = null;
	private Anchor m_adminLink = null;
	private Anchor m_myWorkspaceLink = null;
	private Anchor m_logoutLink = null;
	private Anchor m_helpLink = null;
	private InlineLabel m_mouseOverHint = null;
	// m_rpcCallback is our callback that gets called when the ajax request to get the branding
	// data completes.
	private AsyncCallback<GwtBrandingData> m_rpcCallback = null;
	private GwtBrandingData m_brandingData = null;
	
	
	
	/**
	 * This class displays branding, either level1(corporate) or level2(sub)
	 */
	public class BrandingPanel extends Composite
		implements LoadHandler
	{
		private FlowPanel m_panel;
		private Image m_novellTeamingImg = null;

		/**
		 * 
		 */
		public BrandingPanel()
		{
			ImageResource imageResource;
			
			m_panel = new FlowPanel();
			m_panel.addStyleName( "mastHeadBrandingPanel" );
	
			// Create a Novell Teaming image that will be used in case there is no branding.
			imageResource = GwtTeaming.getImageBundle().mastHeadNovellGraphic();
			m_novellTeamingImg = new Image( imageResource );
			m_novellTeamingImg.setWidth( "500" );
			m_novellTeamingImg.setHeight( "75" );
		
			// All composites must call initWidget() in their constructors.
			initWidget( m_panel );
		}// end BrandingPanel()
		
		
		/**
		 * This method gets called when an image in the branding gets loaded.  We will need
		 * to adjust the height of the masthead.
		 */
		public void onLoad( LoadEvent event )
		{
			// Adjust the height of the masthead to take into consideration this new image
			// that has been loaded.
			adjustMastheadHeight();
		}// end onLoad()
		
		
		/**
		 * Update this panel with the data found in m_brandingData. 
		 */
		public void updatePanel( GwtBrandingData brandingData )
		{
			// Remove any existing branding from this panel.
			m_panel.clear();
			
			if ( brandingData != null )
			{
				String brandingType;
				
				// Get the type of branding the user wants to do.
				brandingType = brandingData.getBrandingType();
				
				// Should we do branding using an image?  
				if ( brandingType != null && brandingType.equalsIgnoreCase( GwtBrandingDataExt.BRANDING_TYPE_IMAGE ) )
				{
					String brandingImgUrl;

					// Yes
					// Do we have an image to use as the branding?
					brandingImgUrl = brandingData.getBrandingImageUrl();
					if ( brandingImgUrl != null && brandingImgUrl.length() > 0 )
					{
						Image img;
						
						// Yes, create the image and add it to the panel.
						img = new Image( brandingImgUrl );
						img.addLoadHandler( this );
						m_panel.add( img );
					}
					else
					{
						// No, Does the user want to use the default Novell/Kablink Teaming image?
						if ( false )
						{
							// Yes, use the Novell Teaming image for the branding.
							m_panel.add( m_novellTeamingImg );
						}
					}
				}
				else
				{
					String html;

					// Default to advanced branding.  This is the branding that is defined in the "branding" field in the ss_forums table.
					// Get the branding html.
					html = brandingData.getBranding();
					
					// Do we have any branding?
					if ( html != null && html.length() > 0 )
					{
						Element element;
						
						// Yes
						// Replace the content of this panel with the branding html.
						element = m_panel.getElement();
						element.setInnerHTML( html );
					}
				}
			}
		}// end updatePanel()
	}// end BrandingPanel
	
	
	
	
	/**
	 * 
	 */
	public MastHead( RequestInfo requestInfo )
	{
		m_requestInfo = requestInfo;
		
		m_mainMastheadPanel = new FlowPanel();
		m_mainMastheadPanel.addStyleName( "mastHead" );
		
		// Create the callback that will be used when we issue an ajax call to get a GwtBrandingData object.
		m_rpcCallback = new AsyncCallback<GwtBrandingData>()
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
				// Remember the branding data we are working with.
				m_brandingData = brandingData;
				
				// Update the masthead with the branding data we just retrieved.
				updateMasthead( brandingData );
			}// end onSuccess()
		};
		
		// Create a panel that will hold the background image.
		{
			m_mastheadBgPanel = new FlowPanel();
			m_mastheadBgPanel.addStyleName( "mastHeadBgPanel" );
			m_mainMastheadPanel.add( m_mastheadBgPanel );

			// Get the default background image and add it to the masthead.
			m_defaultBgImg = new Image( m_requestInfo.getImagesPath() + "pics/masthead/mast_head_bg.png" );
			m_defaultBgImg.setWidth( "100%" );
			m_bgImg = m_defaultBgImg;
			m_mastheadBgPanel.add( m_defaultBgImg );
		}
		
		// Create a panel that will hold the branding and everything else
		{
			m_mastheadContentPanel = new FlowPanel();
			m_mastheadContentPanel.addStyleName( "mastHeadContentPanel" );
			m_mainMastheadPanel.add( m_mastheadContentPanel );
		}
		
		// Create the panel that will hold the level-1 or corporate branding
//!!!		m_level1BrandingPanel = new BrandingPanel();
//		contentPanel.add( m_level1BrandingPanel );
		
		// Create the panel that will hold the level-2 or sub branding.
		m_level2BrandingPanel = new BrandingPanel();
		m_mastheadContentPanel.add( m_level2BrandingPanel );
		
		// Create the panel that will hold the global actions such as "My workspace", "My Teams" etc
		{
			InlineLabel name;
			ImageResource imgResource;
			Element linkElement;
			
			m_globalActionsPanel = new FlowPanel();
			m_globalActionsPanel.addStyleName( "mastHeadGlobalActionsPanel" );
			
			// Create a label that holds the logged-in user's name.
			name = new InlineLabel( requestInfo.getUserName() );
			name.addStyleName( "mastHeadUserName" );
			m_globalActionsPanel.add( name );
			
			// Create a place to hold the mouse-over hint.
			m_mouseOverHint = new InlineLabel();
			m_mouseOverHint.addStyleName( "mastHeadMouseOverHint" );
			m_globalActionsPanel.add( m_mouseOverHint );
			
			// Add the "My workspace" link.
			{
				m_myWorkspaceLink = new Anchor();
				m_myWorkspaceLink.addStyleName( "mastHeadLink" );
				m_myWorkspaceLink.addClickHandler( this );
				m_myWorkspaceLink.addMouseOutHandler( this );
				m_myWorkspaceLink.addMouseOverHandler( this );
				linkElement = m_myWorkspaceLink.getElement();
				
				// Add the mouse-out image to the link.
				imgResource = GwtTeaming.getImageBundle().myWorkspace1();
				m_myWorkspaceImg1 = new Image( imgResource );
				linkElement.appendChild( m_myWorkspaceImg1.getElement() );

				// Add the mouse-over image to the link.
				imgResource = GwtTeaming.getImageBundle().myWorkspace2();
				m_myWorkspaceImg2 = new Image( imgResource );
				m_myWorkspaceImg2.setVisible( false );
				linkElement.appendChild( m_myWorkspaceImg2.getElement() );
				
				m_globalActionsPanel.add( m_myWorkspaceLink );
			}
			
			// Add the "Administration" link.
			{
				m_adminLink = new Anchor();
				m_adminLink.addStyleName( "mastHeadLink" );
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
				
				m_globalActionsPanel.add( m_adminLink );
			}
			
			// Add the "Logout" link.
			{
				m_logoutLink = new Anchor();
				m_logoutLink.addStyleName( "mastHeadLink" );
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
			
			// Add the "Help" link.
			{
				m_helpLink = new Anchor();
				m_helpLink.addStyleName( "mastHeadLink" );
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
			}
			
			m_mastheadContentPanel.add( m_globalActionsPanel );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainMastheadPanel );

		// Tell the branding panel the binder it is working with.  We can't do this right now
		// because the browser hasn't rendered anything yes.  So set a timer to do the work later.
		{
			Timer timer;
			
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					setBinderId( m_requestInfo.getBinderId() );
				}// end run()
			};
			
			timer.schedule( 250 );
		}
	}// end MastHead()


	/**
	 * Called to add an ActionHandler to this MastHead
	 * @param actionHandler
	 */
	public void addActionHandler( ActionHandler actionHandler )
	{
		m_actionHandlers.add( actionHandler );
	}// end addActionHandler()
	
	
	/**
	 * Set the height of the masthead to be equal to the content of the masthead.  Also,
	 * set the height of the background image so it fills the entire masthead.
	 */
	public void adjustMastheadHeight()
	{
		int height;
		String heightStr;
		
		// Set the height of the background image to be equal to the height of the content of the masthead.
		// Have a minimum height of 50 pixels.
		height = m_mastheadContentPanel.getOffsetHeight();
		if ( height < 50 )
			height = 50;
		heightStr = Integer.toString( height );
		
		// Are we using a background image?
		if ( m_bgImg != null )
		{
			// Yes
			// Are we using the default background image?
			if ( m_bgImg != m_defaultBgImg )
			{
				// No, Should we stretch the background image?
				if ( m_brandingData.getBgImageStretchValue() )
				{
					// Yes
					m_bgImg.setHeight( heightStr );
					m_bgImg.setWidth( "100%" );
				}
				else
				{
					int bgImgHeight;
					
					// No, get the height of the background image.
					bgImgHeight = m_bgImg.getHeight();
					
					// Is the height of the bg image bigger than the height of the masthead content?
					if ( bgImgHeight > height )
					{
						// Yes, use the height of the bg image for the height of the masthead.
						heightStr = Integer.toString( bgImgHeight );
					}
				}
			}
			else
			{
				// Yes
				m_bgImg.setHeight( heightStr );
			}
		}

		m_mainMastheadPanel.setHeight( heightStr );
		m_mastheadBgPanel.setHeight( heightStr );
		
		// Notify all OnSizeChangeHandler that have registered.
		for (Iterator<ActionHandler> oschIT = m_actionHandlers.iterator(); oschIT.hasNext(); )
		{
			// Calling each OnSizeChangeHandler
			oschIT.next().handleAction( TeamingAction.SIZE_CHANGED, this );
		}
	}// end adjustMastheadHeight()

	
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
		else if ( eventSource == m_myWorkspaceLink )
		{
			m_myWorkspaceImg1.setVisible( true );
			m_myWorkspaceImg2.setVisible( false );
		}
		else if ( eventSource == m_logoutLink )
		{
			m_logoutImg1.setVisible( true );
			m_logoutImg2.setVisible( false );
		}
		else if ( eventSource == m_helpLink )
		{
			m_helpImg1.setVisible( true );
			m_helpImg2.setVisible( false );
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
		return m_brandingData;
	}// end GwtBrandingData()
	
	
	/**
	 * Issue an ajax request to get the branding data from the server.  Our AsyncCallback
	 * will be called when this request completes.
	 */
	private void getBrandingDataFromServer()
	{
		GwtRpcServiceAsync rpcService;
		
		rpcService = GwtTeaming.getRpcService();
		
		// Do we have a binder id?
		if ( m_mastheadBinderId != null )
		{
			// Yes, Issue an ajax request to get the branding data for the given binder.
			rpcService.getBinderBrandingData( m_mastheadBinderId, m_rpcCallback );
		}
		else
		{
			// Issue an ajax request to get the corporate branding data.
			rpcService.getCorporateBrandingData( m_rpcCallback );
		}
	}// end getBrandingDataFromServer()
	

	/**
	 * This method gets called when the user clicks on something in the masthead.
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
			else if ( eventSource == m_myWorkspaceLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.MY_WORKSPACE, null );
			}
			else if ( eventSource == m_logoutLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.LOGOUT, null );
			}
			else if ( eventSource == m_helpLink )
			{
				actionHandlerIT.next().handleAction( TeamingAction.HELP, null );
			}
		}
	}// end onClick()
	
	
	/**
	 * This method gets called when an image in the branding gets loaded.  We will need
	 * to adjust the height of the masthead.
	 */
	public void onLoad( LoadEvent event )
	{
		// Adjust the height of the masthead to take into consideration this new image
		// that has been loaded.
		adjustMastheadHeight();
	}// end onLoad()
	
	
	/**
	 * This method gets called when the user mouses out of something in the masthead.
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
	 * This method gets called when the user mouses over something in the masthead.
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
		else if ( eventSource == m_myWorkspaceLink )
		{
			m_myWorkspaceImg1.setVisible( false );
			m_myWorkspaceImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().myWorkspaceHint();
		}
		else if ( eventSource == m_logoutLink )
		{
			m_logoutImg1.setVisible( false );
			m_logoutImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().logoutHint();
		}
		else if ( eventSource == m_helpLink )
		{
			m_helpImg1.setVisible( false );
			m_helpImg2.setVisible( true );
			
			hint = GwtTeaming.getMessages().helpHint();
		}
		
		// Update the mouse-over hint.
		m_mouseOverHint.setText( hint );
	}// onMouseOver()
	

	/**
	 * Refresh the masthead by issuing an ajax request to get the branding data . 
	 */
	public void refreshMasthead()
	{
		// Issue an ajax call to get the branding data for the given binder.  When we get the
		// response to this request our async callback will be called.
		getBrandingDataFromServer();
	}// end refreshMasthead()
	
	
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

			// Issue an ajax call to get the branding data for the given binder.  When we get the
			// response to this request our async callback will be called.
			getBrandingDataFromServer();
		}
	}// end setBinderId()

	
	/**
	 * Update the masthead with the branding information found in m_brandingData.
	 */
	public void updateMasthead( GwtBrandingData brandingData )
	{
		if ( brandingData != null )
		{
			String fontColor;
			
			// For the given branding data, adjust the background color or background image.
			{
				String bgImgUrl;
				String bgColor;
				Style mastheadBgPanelStyle;
				Element mastheadBgPanelElement;
				boolean useDefaultBgImg = true;;

				// Remove any background image that may currently be set on the masthead.
				if ( m_bgImg != null )
				{
					m_bgImg.removeFromParent();
				}

				// Remove any background color that may currently be set on the masthead.
				mastheadBgPanelElement = m_mastheadBgPanel.getElement();
				mastheadBgPanelStyle = mastheadBgPanelElement.getStyle();
				mastheadBgPanelStyle.clearBackgroundColor();

				// Is a background image specified in the branding data?
				bgImgUrl = brandingData.getBgImageUrl();
				if ( bgImgUrl != null && bgImgUrl.length() > 0 )
				{
					Image img;
					
					// Yes
					// Add the new background image to the masthead.
					img = new Image( bgImgUrl );
					img.addLoadHandler( this );
					m_mastheadBgPanel.add( img );
					
					// Remember the background image we are using.
					m_bgImg = img;
					
					// No need to use the default background image since a background image was specified.
					useDefaultBgImg = false;
				}
				
				// Is a background color specified in the branding data?
				bgColor = brandingData.getBgColor();
				if ( bgColor != null && bgColor.length() > 0 )
				{
					// Yes, set the color of the masthead background to the appropriate color.
					mastheadBgPanelStyle.setBackgroundColor( bgColor );

					// No need to use the default background image since a background color was specified.
					useDefaultBgImg = false;
				}
				
				// Do we need to use the default background image?
				if ( useDefaultBgImg )
				{
					// Yes
					// Add the default background image to the masthead.
					m_mastheadBgPanel.add( m_defaultBgImg );
					m_bgImg = m_defaultBgImg;
				}
			}
			
			// For the given branding data, adjust the color of the font used in the "global actions" part of the masthead.
			{
				Element element;
				Style style;
				
				element = m_globalActionsPanel.getElement();
				style = element.getStyle();
				
				// Do we have a font color?
				fontColor = brandingData.getFontColor();
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
			}
			
			// Update the actual branding part of the masthead.
			m_level2BrandingPanel.updatePanel( brandingData ); 
			
			// Adjust the height of the masthead to be equal to the height of the masthead content panel.
			adjustMastheadHeight();
		}
	}// end updateMasthead()
}// end MastHead
