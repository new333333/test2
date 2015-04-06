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

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;

/**
 * 
 * @author jwootton
 *
 */
public class IFrameDropWidget extends DropWidget
{
	private static IFrameWidgetDlgBox m_iframeDlgBox = null;		// For efficiency sake, we only create one dialog box.
	private IFrameProperties	m_properties = null;
	private Frame m_iframe;
	

	/**
	 * 
	 */
	public IFrameDropWidget( LandingPageEditor lpe, IFrameConfig configData )
	{
		IFrameProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}
	
	
	/**
	 * 
	 */
	public IFrameDropWidget( LandingPageEditor lpe, IFrameProperties properties )
	{
		init( lpe, properties );
	}
	

	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	@Override
	public String createConfigString()
	{
		return m_properties.createConfigString();
	}
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	@Override
	public DragProxy getDragProxy()
	{
		if ( m_dragProxy == null )
		{
			// Create a drag proxy that will be displayed when the user drags this item.
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorIFrame(), GwtTeaming.getMessages().lpeIFrame() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	@Override
	public void getPropertiesDlgBox( int x, int y, DlgBoxClient dBoxClient )
	{
		// Have we already created a dialog?
		if ( m_iframeDlgBox == null )
		{
			// Pass in the object that holds all the properties for a IFrameDropWidget.
			m_iframeDlgBox = new IFrameWidgetDlgBox( this, this, false, true, x, y, m_properties );
		}
		else
		{
			m_iframeDlgBox.init( m_properties );
			m_iframeDlgBox.initHandlers( this, this );
		}
		
		dBoxClient.onSuccess( m_iframeDlgBox );
	}
	
	
	/**
	 * 
	 */
	private void init( LandingPageEditor lpe, IFrameProperties properties )
	{
		FlowPanel wrapperPanel;
		FlowPanel iframePanel;
		
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
		
		// Create the controls that will hold the iframe
		{
			iframePanel = new FlowPanel();
			iframePanel.addStyleName( "lpeDropWidget" );
			iframePanel.addStyleName( "lpeIFrameWidget" );
			
			m_iframe = new Frame();
			iframePanel.add( m_iframe );
			
			wrapperPanel.add( iframePanel );
		}
		
		// Create an object to hold all of the properties that define an "IFrame" widget.
		m_properties = new IFrameProperties();
		
		// If we were passed some properties, make a copy of them.
		if ( properties != null )
			m_properties.copy( properties );
		
		// All composites must call initWidget() in their constructors.
		initWidget( wrapperPanel );

		// Update the dynamic parts of this widget
		updateWidget( m_properties );
	}
	
	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	@Override
	public void updateWidget( Object props )
	{
		if ( props instanceof IFrameProperties )
		{
			IFrameProperties iframeProps;
			Element element;
			
			iframeProps = (IFrameProperties) props;

			// Save the properties that were passed to us.
			m_properties.copy( iframeProps );
			
			// Get the iframe's element.
			element = m_iframe.getElement();
			if ( element instanceof IFrameElement )
			{
				IFrameElement iframeElement;
				String value;
				
				iframeElement = (IFrameElement) element;
				
				// Update the frame's properties.
				if ( iframeProps.getShowBorder() == true )
					iframeElement.setFrameBorder( 1 );
				else
					iframeElement.setFrameBorder( 0 );
				
				iframeElement.setName( iframeProps.getName() );
				
				value = iframeProps.getScrollbarValueAsString();
				if ( value != null && value.equalsIgnoreCase( "auto" ) )
					iframeElement.removeAttribute( "scrolling" );
				else
					iframeElement.setScrolling( value );
				
				iframeElement.setAttribute( "height", iframeProps.getHeightAsString() );

				// Set the width on the <iframe> element.
				value = iframeProps.getWidthAsString();
				if ( value != null && value.length() > 0 )
					iframeElement.setAttribute( "width", value );
				else
					iframeElement.setAttribute( "width", "100%" );
				
				iframeElement.setSrc( iframeProps.getUrl() );
			}
		}
	}
}
