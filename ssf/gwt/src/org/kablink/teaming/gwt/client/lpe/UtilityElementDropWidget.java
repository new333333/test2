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
public class UtilityElementDropWidget extends DropWidget
{
	private static UtilityElementWidgetDlgBox m_utilityDlgBox = null;		// For efficiency sake, we only create one dialog box.
	private UtilityElementProperties	m_properties = null;
	private InlineLabel				m_utilityElementName = null;
	

	/**
	 * 
	 */
	public UtilityElementDropWidget( LandingPageEditor lpe, UtilityElementConfig configData )
	{
		UtilityElementProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}// end UtilityElementDropWidget()
	
	
	/**
	 * 
	 */
	public UtilityElementDropWidget( LandingPageEditor lpe, UtilityElementProperties properties )
	{
		init( lpe, properties );
	}// end UtilityElementDropWidget()
	

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
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorUtilityElement(), GwtTeaming.getMessages().lpeUtilityElement() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public void getPropertiesDlgBox( int xPos, int yPos, DlgBoxClient dBoxClient )
	{
		// Have we already created a dialog?
		if ( m_utilityDlgBox == null )
		{
			// Pass in the object that holds all the properties for a UtilityElementDropWidget.
			m_utilityDlgBox = new UtilityElementWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_utilityDlgBox.init( m_properties );
			m_utilityDlgBox.initHandlers( this, this );
		}
		
		dBoxClient.onSuccess( m_utilityDlgBox );
	}// end getPropertiesDlgBox()
	
	
	/**
	 * 
	 */
	private void init( LandingPageEditor lpe, UtilityElementProperties properties )
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
		
		// Create the controls that will hold the utility element name
		{
			FlowPanel mainPanel;
			InlineLabel label;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "lpeDropWidget" );
			mainPanel.addStyleName( "lpeUtilityElementWidget" );
			
			// Add a label that identifies this widget as a Utility Element.
			label = new InlineLabel( GwtTeaming.getMessages().utilityElementLabel() );
			label.addStyleName( "lpeWidgetIdentifier" );
			mainPanel.add( label );
			
			m_utilityElementName = new InlineLabel();
			m_utilityElementName.addStyleName( "lpeUtilityElementName" );
			mainPanel.add( m_utilityElementName );
			
			wrapperPanel.add( mainPanel );
		}
		
		// Create an object to hold all of the properties that define a "utility element" widget.
		m_properties = new UtilityElementProperties();
		
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
		String utilityElementName;

		// Save the properties that were passed to us.
		if ( props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		
		// Update the utility element name.
		// Get the utility element name.
		utilityElementName = m_properties.getUtilityElementName();
		
		if ( utilityElementName == null || utilityElementName.length() == 0 )
			m_utilityElementName.setText( "" );
		else
			m_utilityElementName.setText( utilityElementName );
	}// end updateWidget()
}// end UtilityElementDropWidget
