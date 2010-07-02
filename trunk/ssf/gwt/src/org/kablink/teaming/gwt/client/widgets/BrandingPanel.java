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
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;


/**
 * This widget will display branding data 
 */
public class BrandingPanel extends Composite
	implements ActionRequestor, LoadHandler
{
	public static final String NO_IMAGE = "__no image__";
	public static final String DEFAULT_TEAMING_IMAGE = "__default teaming image__";
	
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private RequestInfo m_requestInfo = null;
	private FlowPanel m_mainPanel = null;
	private FlowPanel m_bgPanel;
	private FlowPanel m_wrapperPanel = null;
	private BrandingContentPanel m_contentPanel = null;
	private Image m_defaultBgImg = null;
	private Image m_bgImg = null;
	private GwtBrandingData m_brandingData = null;
	
	/**
	 * This class displays the image or html defined in the branding
	 */
	private class BrandingContentPanel extends Composite
		implements LoadHandler
	{
		private FlowPanel m_panel;
		private Image m_teamingImg = null;

		/**
		 * 
		 */
		public BrandingContentPanel()
		{
			ImageResource imageResource;
			
			m_panel = new FlowPanel();
			m_panel.addStyleName( "brandingContentPanel" );
	
			// Are we running Novell Teaming?
			if ( m_requestInfo.isNovellTeaming() )
			{
				// Yes
				// Create a Novell Teaming image that will be used in case there is no branding.
				imageResource = GwtTeaming.getImageBundle().mastHeadNovellGraphic();
				m_teamingImg = new Image( imageResource );
				m_teamingImg.setWidth( "500" );
				m_teamingImg.setHeight( "75" );
			}
			else
			{
				// No
				// Create a Kablink Teaming image that will be used in case there is no branding.
				imageResource = GwtTeaming.getImageBundle().mastHeadKablinkGraphic();
				m_teamingImg = new Image( imageResource );
				m_teamingImg.setWidth( "500" );
				m_teamingImg.setHeight( "75" );
			}
		
			// All composites must call initWidget() in their constructors.
			initWidget( m_panel );
		}// end BrandingContentPanel()
		
		
		/**
		 * This method gets called when an image in the branding gets loaded.  We will need
		 * to adjust the height of the BrandingPanel.
		 */
		public void onLoad( LoadEvent event )
		{
			// Adjust the height of the BrandingPanel to take into consideration this new image
			// that has been loaded.
			Command cmd;
			
	        cmd = new Command()
	        {
	        	/**
	        	 * 
	        	 */
	            public void execute()
	            {
	    			adjustBrandingPanelHeight();
	            }
	        };
	        DeferredCommand.addCommand( cmd );
		}// end onLoad()
		
		
		/**
		 * Update this panel with the data found in brandingData. 
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
					String brandingImgName;
					boolean useDefaultTeamingImg = false;

					// Yes
					brandingImgName = brandingData.getBrandingImageName();
					brandingImgUrl = brandingData.getBrandingImageUrl();
					
					// Do we have the name of a branding image to use?
					if ( brandingImgName == null || brandingImgName.length() == 0 )
					{
						// No, use the default teaming image.
						useDefaultTeamingImg = true;
					}
					else
					{
						// Yes
						// Is the branding image name "__default teaming image__"?
						if ( brandingImgName.equalsIgnoreCase( DEFAULT_TEAMING_IMAGE ) )
						{
							// Yes
							useDefaultTeamingImg = true;
						}
						// Is the branding image name "__no image__"?
						else if ( brandingImgName.equalsIgnoreCase( NO_IMAGE ) )
						{
							// Yes, nothing to do
						}
						// Do we have a url to the specified branding image?
						else if ( brandingImgUrl != null && brandingImgUrl.length() > 0 )
						{
							Image img;
							
							// Yes, create the image and add it to the panel.
							img = new Image( brandingImgUrl );
							img.addLoadHandler( this );
							m_panel.add( img );
						}
					}
					
					// Should we use the default Teaming image?
					if ( useDefaultTeamingImg )
					{
						// Yes
						m_panel.add( m_teamingImg );
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
						Timer timer;
						
						// Yes
						// Replace the content of this panel with the branding html.
						m_panel.clear();
						element = m_panel.getElement();
						element.setInnerHTML( html );

						GwtClientHelper.jsExecuteJavaScript( element );

						// The html we just added to the branding may have images in it.
						// We need to wait until the browser has rendered the new html
						// we just added before we adjust the height of the branding panel.
						timer = new Timer()
						{
							/**
							 * 
							 */
							@Override
							public void run()
							{
				    			adjustBrandingPanelHeight();
							}// end run()
						};
						
						timer.schedule( 1000 );
					}
				}
			}
		}// end updatePanel()
	}// end BrandingContentPanel
	
	
	
	
	/**
	 * 
	 */
	public BrandingPanel( RequestInfo requestInfo )
	{
        m_requestInfo = requestInfo;
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "brandingPanel" );
		
		// Create a panel that will hold the background image.
		{
			m_bgPanel = new FlowPanel();
			m_bgPanel.addStyleName( "brandingBgPanel" );
			m_mainPanel.add( m_bgPanel );

			// Get the default background image and add it to the background panel.
			m_defaultBgImg = new Image( m_requestInfo.getImagesPath() + "pics/masthead/mast_head_bg.png" );
			m_defaultBgImg.setWidth( "100%" );
			m_bgImg = m_defaultBgImg;
			m_bgPanel.add( m_defaultBgImg );
		}
		
		// Create a panel that will hold the branding and everything else
		{
			m_wrapperPanel = new FlowPanel();
			m_wrapperPanel.addStyleName( "brandingWrapperPanel" );
			m_mainPanel.add( m_wrapperPanel );
		}
		
		// Create the panel that will hold the content of the branding (an image or html)
		m_contentPanel = new BrandingContentPanel();
		m_wrapperPanel.add( m_contentPanel );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainPanel );

	}// end BrandingPanel()


	/**
	 * Called to add an ActionHandler to this masthead
	 * @param actionHandler
	 */
	public void addActionHandler( ActionHandler actionHandler )
	{
		m_actionHandlers.add( actionHandler );
	}// end addActionHandler()
	

	/**
	 * Set the height of the branding panel to be equal to the content of the panel.  Also,
	 * set the height of the background image so it fills the entire panel.
	 */
	public void adjustBrandingPanelHeight()
	{
		int height;
		int wrapperPanelHeight;
		int contentPanelHeight;
		String heightStr;
		
		// Set the height of the background image to be equal to the height of the content of the branding panel.
		// Have a minimum height of 50 pixels.
		wrapperPanelHeight = m_wrapperPanel.getOffsetHeight();
		contentPanelHeight = m_contentPanel.getOffsetHeight();
		height = Math.max( wrapperPanelHeight, contentPanelHeight );
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
					
					// Is the height of the bg image bigger than the height of the branding content?
					if ( bgImgHeight > height )
					{
						// Yes, use the height of the bg image for the height of the branding panel.
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

		m_mainPanel.setHeight( heightStr );
		//!!! m_wrapperPanel.setHeight( heightStr );
		m_bgPanel.setHeight( heightStr );
		
		// Notify all OnSizeChangeHandler that have registered.
		for (Iterator<ActionHandler> oschIT = m_actionHandlers.iterator(); oschIT.hasNext(); )
		{
			// Calling each OnSizeChangeHandler
			oschIT.next().handleAction( TeamingAction.SIZE_CHANGED, this );
		}
	}// end adjustBrandingPanelHeight()

	
	/**
	 * Return the branding data we are working with.
	 */
	public GwtBrandingData getBrandingData()
	{
		return m_brandingData;
	}// end GwtBrandingData()
	
	
	/**
	 * This method gets called when an image in the branding gets loaded.  We will need
	 * to adjust the height of the branding panel.
	 */
	public void onLoad( LoadEvent event )
	{
		// Adjust the height of the branding panel to take into consideration this new image
		// that has been loaded.
		Command cmd;
		
        cmd = new Command()
        {
        	/**
        	 * 
        	 */
            public void execute()
            {
    			adjustBrandingPanelHeight();
            }
        };
        DeferredCommand.addCommand( cmd );
	}// end onLoad()
	
	
	/**
	 * Update the branding panel with the branding information found in brandingData.
	 */
	public void updateBrandingPanel( GwtBrandingData brandingData )
	{
		m_brandingData = brandingData;
		
		if ( brandingData != null )
		{
			// For the given branding data, adjust the background color or background image.
			{
				String bgImgUrl;
				String bgColor;
				Style bgPanelStyle;
				Element bgPanelElement;
				boolean useDefaultBgImg = true;;

				// Remove any background image that may currently be set on the branding panel
				if ( m_bgImg != null )
				{
					m_bgImg.removeFromParent();
				}

				// Remove any background color that may currently be set on the branding panel.
				bgPanelElement = m_bgPanel.getElement();
				bgPanelStyle = bgPanelElement.getStyle();
				bgPanelStyle.clearBackgroundColor();

				// Is a background image specified in the branding data?
				bgImgUrl = brandingData.getBgImageUrl();
				if ( bgImgUrl != null && bgImgUrl.length() > 0 )
				{
					Image img;
					
					// Yes
					// Add the new background image to the branding panel.
					img = new Image( bgImgUrl );
					img.addLoadHandler( this );
					m_bgPanel.add( img );
					
					// Remember the background image we are using.
					m_bgImg = img;
					
					// No need to use the default background image since a background image was specified.
					useDefaultBgImg = false;
				}
				
				// Is a background color specified in the branding data?
				bgColor = brandingData.getBgColor();
				if ( bgColor != null && bgColor.length() > 0 )
				{
					try
					{
						// Yes, set the color of the branding panel background to the appropriate color.
						bgPanelStyle.setBackgroundColor( bgColor );
					
						// No need to use the default background image since a background color was specified.
						useDefaultBgImg = false;
					}
					catch( Exception ex)
					{
						// Nothing to do
					}
				}
				
				// Do we need to use the default background image?
				if ( useDefaultBgImg )
				{
					// Yes
					// Add the default background image to the branding panel.
					m_bgPanel.add( m_defaultBgImg );
					m_bgImg = m_defaultBgImg;
				}
			}
			
			// Update the actual branding part of the branding panel
			m_contentPanel.updatePanel( brandingData ); 
			
			// Adjust the height of the branding panel to be equal to the height of the branding content panel.
			// Do this in as a DeferredCommand so the browser has a chance to render the new content.
			{
				Command cmd;
				
		        cmd = new Command()
		        {
		        	/**
		        	 * 
		        	 */
		            public void execute()
		            {
		    			adjustBrandingPanelHeight();
		            }
		        };
		        DeferredCommand.addCommand( cmd );
				
			}
		}
	}// end updateBrandingPanel()
	
}// end BrandingPanel
