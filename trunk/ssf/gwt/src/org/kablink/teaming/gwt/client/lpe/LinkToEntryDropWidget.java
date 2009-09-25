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
package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditDeleteControl;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author jwootton
 *
 */
public class LinkToEntryDropWidget extends DropWidget
{
	private static LinkToEntryWidgetDlgBox m_entryDlgBox = null;	// For efficiency sake, we only create one dialog box.
	private LinkToEntryProperties	m_properties = null;
	private InlineLabel		m_entryName = null;
	private InlineLabel		m_title = null;
	private Timer				m_timer = null;
	
	/**
	 * 
	 */
	public LinkToEntryDropWidget( LandingPageEditor lpe, LinkToEntryConfig configData )
	{
		LinkToEntryProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}// end LinkToEntryDropWidget()
	
	
	/**
	 * 
	 */
	public LinkToEntryDropWidget( LandingPageEditor lpe, LinkToEntryProperties properties )
	{
		init( lpe, properties );
	}// end LinkToEntryDropWidget()
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public DlgBox getPropertiesDlgBox( int xPos, int yPos )
	{
		// Have we already created a dialog?
		if ( m_entryDlgBox == null )
		{
			// Pass in the object that holds all the properties for a LinkToEntryDropWidget.
			m_entryDlgBox = new LinkToEntryWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_entryDlgBox.init( m_properties );
			m_entryDlgBox.initHandlers( this, this );
		}
		
		return m_entryDlgBox;
	}// end getPropertiesDlgBox()
	
	
	/**
	 * 
	 */
	public void init( LandingPageEditor lpe, LinkToEntryProperties properties )
	{
		FlowPanel wrapperPanel;
		
		m_lpe = lpe;
		
		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );
		
		// Create an Edit/Delete control and position it at the top/right of this widget.
		// This control allows the user to edit the properties of this widget and to delete this widget.
		{
			EditDeleteControl ctrl;
			FlowPanel panel;
			
			ctrl = new EditDeleteControl( this, this );
			ctrl.addStyleName( "upperRight" );
			
			// Wrap the edit/delete control in a panel.  We position the edit/delete control on the right
			// side of the wrapper panel.
			panel = new FlowPanel();
			panel.addStyleName( "editDeleteWrapperPanel" );
			
			panel.add( ctrl );
			wrapperPanel.add( panel );
		}
		
		// Create the controls that will hold the entry name and the user-supplied title.
		{
			FlowPanel mainPanel;
			InlineLabel label;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "lpeDropWidget" );
			mainPanel.addStyleName( "lpeLinkToEntryWidget" );
			
			// Add a label that identifies this widget as an entry.
			label = new InlineLabel( GwtTeaming.getMessages().linkToEntryLabel() );
			label.addStyleName( "lpeWidgetIdentifier" );
			mainPanel.add( label );
			
			m_entryName = new InlineLabel();
			m_entryName.addStyleName( "lpeLinkToEntryName" );
			mainPanel.add( m_entryName );
			
			label = new InlineLabel( GwtTeaming.getMessages().linkToEntryTitleLabel() );
			label.addStyleName( "lpeWidgetIdentifier" );
			label.addStyleName( "lpeMarginLeft1Point5" );
			mainPanel.add( label );
			m_title = new InlineLabel();
			m_title.addStyleName( "lpeLinkToEntryTitle" );
			mainPanel.add( m_title );

			wrapperPanel.add( mainPanel );
		}
		
		// Create an object to hold all of the properties that define a link to entry widget.
		m_properties = new LinkToEntryProperties();
		
		// Update the dynamic parts of this widget
		updateWidget( properties );
		
		// All composites must call initWidget() in their constructors.
		initWidget( wrapperPanel );
	}// end init()

	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( PropertiesObj props )
	{
		// Save the properties that were passed to us.
		if ( props != null )
			m_properties.copy( props );
		
		// Get the needed information from the server.
		m_properties.getDataFromServer();
		
		updateWidget();
	}// end updateWidget()
	
	
	/**
	 * 
	 */
	private void updateWidget()
	{
		String entryName;
		String title;

		// Update the title
		title = m_properties.getTitle();
		if ( title == null || title.length() == 0 )
			title = "";
		m_title.setText( title );

		// Are we waiting for the ajax call to get the entry name to finish?
		if ( m_properties.isRpcInProgress() )
		{
			// Yes
			// Have we already created a timer?
			if ( m_timer == null )
			{
				// No, create one.
				m_timer = new Timer()
				{
					/**
					 * 
					 */
					@Override
					public void run()
					{
						updateWidget();
					}// end run()
				};
			}
			
			m_timer.schedule( 250 );
			return;
		}

		// Get the entry's name.
		entryName = m_properties.getEntryName();
			
		// Update the entry name
		if ( entryName == null || entryName.length() == 0 )
			m_entryName.setText( "" );
		else
			m_entryName.setText( entryName );
		
	}// end updateWidget()
}// end LinkToEntryDropWidget
