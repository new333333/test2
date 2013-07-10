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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgBoxClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author jwootton
 *
 */
public class LinkToUrlDropWidget extends DropWidget
{
	private static LinkToUrlWidgetDlgBox m_linkToUrlDlgBox = null;		// For efficiency sake, we only create one dialog box.
	private LinkToUrlProperties	m_properties = null;
	private InlineLabel			m_title = null;
	private InlineLabel			m_url = null;
	
	/**
	 * 
	 */
	public LinkToUrlDropWidget( LandingPageEditor lpe, LinkToUrlConfig configData )
	{
		LinkToUrlProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}// end LinkToUrlDropWidget()
	
	
	/**
	 * 
	 */
	public LinkToUrlDropWidget( LandingPageEditor lpe, LinkToUrlProperties properties )
	{
		init( lpe, properties );
	}// end LinkToUrlDropWidget()
	

	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	public String createConfigString()
	{
		return m_properties.createConfigString();
	}// end createConfigString()
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	public DragProxy getDragProxy()
	{
		if ( m_dragProxy == null )
		{
			// Create a drag proxy that will be displayed when the user drags this item.
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorLinkUrl(), GwtTeaming.getMessages().lpeLinkURL() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public void getPropertiesDlgBox( int xPos, int yPos, DlgBoxClient dBoxClient )
	{
		// Have we already created a dialog?
		if ( m_linkToUrlDlgBox == null )
		{
			// Pass in the object that holds all the properties for a LinkToUrlDropWidget.
			m_linkToUrlDlgBox = new LinkToUrlWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_linkToUrlDlgBox.init( m_properties );
			m_linkToUrlDlgBox.initHandlers( this, this );
		}
		
		dBoxClient.onSuccess( m_linkToUrlDlgBox );
	}// end getPropertiesDlgBox()
	
	
	/**
	 * 
	 */
	public void init( LandingPageEditor lpe, LinkToUrlProperties properties )
	{
		FlowPanel wrapperPanel;
		
		m_lpe = lpe;
		
		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );
		
		// Create an Edit/Delete control and position it at the top/right of this widget.
		// This control allows the user to edit the properties of this widget and to delete this widget.
		{
			ActionsControl ctrl;
			FlowPanel panel;
			
			ctrl = new ActionsControl( this, this, this );
			ctrl.addStyleName( "upperRight" );
			
			// Wrap the edit/delete control in a panel.  We position the edit/delete control on the right
			// side of the wrapper panel.
			panel = new FlowPanel();
			panel.addStyleName( "editDeleteWrapperPanel" );
			
			panel.add( ctrl );
			wrapperPanel.add( panel );
		}
		
		// Create the controls that will hold the title and url
		{
			FlowPanel mainPanel;
			InlineLabel label;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "lpeDropWidget" );
			mainPanel.addStyleName( "lpeLinkToUrlWidget" );
			
			// Add a label that identifies this widget as a link.
			label = new InlineLabel( GwtTeaming.getMessages().linkToUrlLabel() );
			label.addStyleName( "lpeWidgetIdentifier" );
			mainPanel.add( label );
			
			m_title = new InlineLabel();
			m_title.addStyleName( "lpeLinkToUrlTitle" );
			mainPanel.add( m_title );
			
			m_url = new InlineLabel();
			m_url.addStyleName( "lpeLinkToUrlUrl" );
			mainPanel.add( m_url );

			wrapperPanel.add( mainPanel );
		}
		
		// Create an object to hold all of the properties that define a link-to-url widget.
		m_properties = new LinkToUrlProperties();
		
		// If we were passed some properties, make a copy of them.
		if ( properties != null )
			m_properties.copy( properties );
		
		// Update the dynamic parts of this widget
		updateWidget( m_properties );
		
		// All composites must call initWidget() in their constructors.
		initWidget( wrapperPanel );
	}// end init()

	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( Object props )
	{
		// Save the properties that were passed to us.
		if ( props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		
		// Update the title
		{
			String title;
			
			// Get the title.
			title = m_properties.getTitle();
			
			if ( title == null || title.length() == 0 )
				m_title.setText( "" );
			else
				m_title.setText( title );
		}
		
		// Update the url
		{
			String url;
			
			url = m_properties.getUrl();
			m_url.setText( GwtTeaming.getMessages().linkToUrlUrl( url ) );
		}
	}// end updateWidget()
}// end LinkToUrlDropWidget
