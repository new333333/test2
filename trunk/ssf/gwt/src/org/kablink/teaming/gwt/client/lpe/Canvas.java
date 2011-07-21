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
package org.kablink.teaming.gwt.client.lpe;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class represents the canvas that the user can add landing page elements to
 * @author jwootton
 *
 */
public class Canvas extends Composite
{
	DropZone m_dropZone;
	LandingPageProperties m_landingPageProperties;
	LandingPagePropertiesDlgBox m_lpPropertiesDlg;
	String m_binderId;
	
	/**
	 * 
	 */
	public Canvas( LandingPageEditor lpe, String propertiesXML )
	{
		FlowPanel mainPanel;
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "lpeCanvasWrapperPanel" );
		
		m_binderId = lpe.getBinderId();
		
		// Create an "edit" image
		{
			ImageResource imageResource;
			Image editImg;
			Image delImg;
			FlowPanel actionsPanel;
			ClickHandler clickHandler;
			
			actionsPanel = new FlowPanel();
			actionsPanel.addStyleName( "lpeCanvasActionsControl" );

			// Add an edit image
			imageResource = GwtTeaming.getImageBundle().edit10();
			editImg = new Image( imageResource );
			editImg.addStyleName( "lpeEditImg" );
			editImg.setTitle( GwtTeaming.getMessages().lpeAltEditLPProperties() );
			clickHandler = new ClickHandler()
			{
				/**
				 * Invoke the edit landing page properties dialog
				 */
				public void onClick( ClickEvent event )
				{
					Widget img;
					
					// Get the anchor the user clicked on.
					img = (Widget) event.getSource();

					// Invoke the "edit landing page properties" dialog.
					invokeEditLandingPagePropertiesDlg( img.getAbsoluteLeft(), img.getAbsoluteTop() );
				}
			};
			editImg.addClickHandler( clickHandler );
			
			// Create a "delete" image.
			{
				imageResource = GwtTeaming.getImageBundle().delete10();
				delImg = new Image(imageResource);
				delImg.setTitle( GwtTeaming.getMessages().lpeAltDeleteAll() );
				delImg.addStyleName( "lpeDeleteImg" );
				clickHandler = new ClickHandler()
				{
					/**
					 * Remove all elements from the canvas.
					 */
					public void onClick( ClickEvent event )
					{
						// Ask the user if they really want to remove all the elements from the landing page.
						if ( Window.confirm( GwtTeaming.getMessages().lpeDeleteAllWarning() ) )
						{
							m_dropZone.removeAllWidgets();
						}
					}
				};
				delImg.addClickHandler( clickHandler );
			}
			
			actionsPanel.add( editImg );
			actionsPanel.add( delImg );
			mainPanel.add( actionsPanel );
		}

		m_dropZone = new DropZone( lpe, "lpeCanvas" );
		m_dropZone.setParentDropZone( null );
		mainPanel.add( m_dropZone );
		
		m_landingPageProperties = new LandingPageProperties( propertiesXML );
		
		m_lpPropertiesDlg = null;
		
		initWidget( mainPanel );
	}

	/**
	 * Add the given widget to this drop zone.  Use the x and y coordinates to determine where in our list of widgets
	 * we should add the new widget.
	 */
	public void addWidgetToDropZone( DropWidget dropWidget )
	{
		m_dropZone.addWidgetToDropZone( dropWidget );
	}
	
	/**
	 * Adjust the height of all the table widgets so all the DropZones in a table are the same height.
	 */
	public int adjustHeightOfAllTableWidgets()
	{
		return m_dropZone.adjustHeightOfAllTableWidgets();
	}
	
	/**
	 * Return the landing page properties as an xml string.
	 */
	public String getLandingPageProperties()
	{
		if ( m_landingPageProperties != null )
			return m_landingPageProperties.getPropertiesAsXMLString();
		
		return "";
	}
	
	/**
	 * Return how much this drop zone has been scrolled vertically.
	 */
	public int getScrollY()
	{
		return m_dropZone.getScrollY();
	}
	
	/**
	 * Return all the widgets that live in this drop zone.
	 */
	public ArrayList<DropWidget> getWidgets()
	{
		return m_dropZone.getWidgets();
	}
	
	/**
	 * Invoke the "edit landing page properties" dialog.
	 */
	private void invokeEditLandingPagePropertiesDlg( final int x, final int y )
	{
		PopupPanel.PositionCallback posCallback;

		if ( m_lpPropertiesDlg == null )
		{
			EditSuccessfulHandler successHandler;
			
			successHandler = new EditSuccessfulHandler()
			{
				/**
				 * This method gets called when user user presses ok in the "Edit Landing Page Properties" dialog.
				 */
				public boolean editSuccessful( Object obj )
				{
					if ( obj instanceof LandingPageProperties )
					{
						m_landingPageProperties.copy( (LandingPageProperties) obj );
					}
					
					return true;
				}
			};

			m_lpPropertiesDlg = new LandingPagePropertiesDlgBox( successHandler, null, false, true, x, y );
		}
		
		m_lpPropertiesDlg.init( m_landingPageProperties, m_binderId );

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				m_lpPropertiesDlg.setPopupPosition( x - offsetWidth, y );
			}
		};
		m_lpPropertiesDlg.setPopupPositionAndShow( posCallback );
	}
}
