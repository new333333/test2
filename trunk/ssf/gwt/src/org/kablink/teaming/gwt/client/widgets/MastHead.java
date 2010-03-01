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


import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


/**
 * This widget will display the MastHead 
 */
public class MastHead extends Composite
{
	private RequestInfo m_requestInfo = null;
//!!!	private BrandingPanel m_level1BrandingPanel = null;
	private BrandingPanel m_level2BrandingPanel = null;
	private Image m_backgroundImg = null;
	private FlowPanel m_mainMastheadPanel = null;
	private FlowPanel m_mastheadContentPanel = null;
	
	/**
	 * This class displays branding, either level1(corporate) or level2(sub)
	 */
	public class BrandingPanel extends Composite
	{
		private FlowPanel m_panel;
		private Image m_novellTeamingImg = null;

		private String m_binderId = null;	// Id of the binder we are displaying branding for.

		private GwtBrandingData m_brandingData = null;	// Branding data for the given binder.

		// m_rpcCallback is our callback that gets called when the ajax request to get the branding
		// data completes.
		private AsyncCallback<GwtBrandingData> m_rpcCallback = null;
		
		
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
							cause = messages.errorAccessToFolderDenied( m_binderId );
						else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
							cause = messages.errorFolderDoesNotExist( m_binderId );
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
					m_brandingData = brandingData;
					
					// Update this panel with the branding data.
					updatePanel();
				}// end onSuccess()
			};
			
			// All composites must call initWidget() in their constructors.
			initWidget( m_panel );
		}// end BrandingPanel()
		
		
		/**
		 * Issue an ajax request to get the branding data from the server.  Our AsyncCallback
		 * will be called when this request completes.
		 */
		public void getDataFromServer()
		{
			GwtRpcServiceAsync rpcService;
			
			rpcService = GwtTeaming.getRpcService();
			
			// Do we have a binder id?
			if ( m_binderId != null )
			{
				// Yes, Issue an ajax request to get the branding data for the given binder.
				rpcService.getBinderBrandingData( m_binderId, m_rpcCallback );
			}
			else
			{
				// Issue an ajax request to get the corporate branding data.
				rpcService.getCorporateBrandingData( m_rpcCallback );
			}
		}// end getDataFromServer()
		
		
		/**
		 * 
		 */
		public void setBinderId( String binderId )
		{
			m_binderId = binderId;
		}// end setBinderId()
		
		
		/**
		 * Update this panel with the data found in m_brandingData. 
		 */
		public void updatePanel()
		{
			// Remove any existing branding from this panel.
			m_panel.clear();
			
			if ( m_brandingData != null )
			{
				String html;
				
				// Get the branding html.
				html = m_brandingData.getBranding();
				
				// Do we have any branding?
				if ( html != null && html.length() > 0 )
				{
					Element element;
					
					// Yes
					// Replace the content of this panel with the branding html.
					element = m_panel.getElement();
					element.setInnerHTML( html );
				}
				else
				{
					// No, use the Novell Teaming image for the branding.
					m_panel.add( m_novellTeamingImg );
				}
			}
			
			// Set the height of the background image to be equal to the height of the masthead.
			// We can't do this right now because the browser hasn't rendered anything yet.  So set a timer to do the work later.
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
						// Adjust the height of the masthead to be equal to the height of the masthead content panel.
						adjustMastheadHeight();
					}// end run()
				};
				
				timer.schedule( 250 );
			}
		}// end updatePanel()
	}// end BrandingPanel
	
	
	/**
	 * 
	 */
	public MastHead( RequestInfo requestInfo )
	{
		FlowPanel panel;
		FlowPanel bgPanel;

		m_requestInfo = requestInfo;
		
		m_mainMastheadPanel = new FlowPanel();
		m_mainMastheadPanel.addStyleName( "mastHead" );
		
		// Create a panel that will hold the background image.
		{
			bgPanel = new FlowPanel();
			bgPanel.addStyleName( "mastHeadBgPanel" );
			m_mainMastheadPanel.add( bgPanel );

			// Get the background image.
			m_backgroundImg = new Image( m_requestInfo.getImagesPath() + "pics/masthead/mast_head_bg.png" );
			m_backgroundImg.setWidth( "100%" );
			bgPanel.add( m_backgroundImg );
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
		m_level2BrandingPanel.setBinderId( m_requestInfo.getBinderId() );
		m_mastheadContentPanel.add( m_level2BrandingPanel );
		
		// Create the panel that will hold the global actions such as "My workspace", "My Teams" etc
		{
			Label name;
			
			panel = new FlowPanel();
			panel.addStyleName( "mastHeadGlobalActionsPanel" );
			
			// Create a label that holds the logged-in user's name.
			name = new Label( requestInfo.getUserName() );
			name.addStyleName( "mastHeadUserName" );
			panel.add( name );
			
			m_mastheadContentPanel.add( panel );
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainMastheadPanel );
		
		// Issue an ajax request to retrieve the corporate and binder branding data.
//!!!		m_level1BrandingPanel.getDataFromServer();
		m_level2BrandingPanel.getDataFromServer();
	}// end MastHead()

	
	/**
	 * Set the height of the masthead to be equal to the content of the masthead.  Also,
	 * set the height of the background image so it fills the entire masthead.
	 */
	public void adjustMastheadHeight()
	{
		int height;
		String heightStr;
		
		// Set the height of the background image to be equal to the height of the content of the masthead.
		height = m_mastheadContentPanel.getOffsetHeight();
		heightStr = Integer.toString( height );
		m_backgroundImg.setHeight( heightStr );
		m_mainMastheadPanel.setHeight( heightStr );
	}// end adjustMastheadHeight()
	
}// end MastHead
