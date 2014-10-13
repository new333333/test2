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

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgBoxClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author jwootton
 *
 */
public class EntryDropWidget extends DropWidget
	implements ClickHandler, MouseOverHandler, MouseOutHandler
{
	private static EntryWidgetDlgBox m_entryDlgBox = null;	// For efficiency sake, we only create one dialog box.
	private EntryProperties	m_properties = null;
	private InlineLabel		m_entryName = null;
	private InlineLabel		m_binderName = null;
	private String				m_viewEntryUrl = null;
	
	/**
	 * 
	 */
	public EntryDropWidget( LandingPageEditor lpe, EntryConfig configData )
	{
		EntryProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
	}// end EntryDropWidget()
	
	
	/**
	 * 
	 */
	public EntryDropWidget( LandingPageEditor lpe, EntryProperties properties )
	{
		init( lpe, properties );
	}// end EntryDropWidget()
	

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
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorEntry(), GwtTeaming.getMessages().lpeEntry() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public void getPropertiesDlgBox( int xPos, int yPos, DlgBoxClient dBoxClient )
	{
		// Have we already created a dialog?
		if ( m_entryDlgBox == null )
		{
			// Pass in the object that holds all the properties for a EntryDropWidget.
			m_entryDlgBox = new EntryWidgetDlgBox( m_lpe, this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_entryDlgBox.init( m_properties );
			m_entryDlgBox.initHandlers( this, this );
		}
		
		dBoxClient.onSuccess( m_entryDlgBox );
	}// end getPropertiesDlgBox()
	
	
	/**
	 * 
	 */
	public void init( LandingPageEditor lpe, EntryProperties properties )
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
		
		// Create the controls that will hold the entry name and binder name.
		{
			FlowPanel mainPanel;
			InlineLabel label;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "lpeDropWidget" );
			mainPanel.addStyleName( "lpeEntryWidget" );
			
			// Add a label that identifies this widget as an entry.
			label = new InlineLabel( GwtTeaming.getMessages().entryLabel() );
			label.addStyleName( "lpeWidgetIdentifier" );
			mainPanel.add( label );
			
			// m_entryName will be updated with the entry's name in updateWidget().
			m_entryName = new InlineLabel();
			m_entryName.setStylePrimaryName( "lpeEntryName" );
			m_entryName.addStyleDependentName( "mouseOut" );
			m_entryName.addClickHandler( this );
			m_entryName.addMouseOverHandler( this );
			m_entryName.addMouseOutHandler( this );
			mainPanel.add( m_entryName );
			
			m_binderName = new InlineLabel();
			m_binderName.addStyleName( "lpeEntryBinderName" );
			mainPanel.add( m_binderName );

			wrapperPanel.add( mainPanel );
		}
		
		// Create an object to hold all of the properties that define an entry widget.
		m_properties = new EntryProperties();
		if ( properties != null )
			m_properties.setZoneUUID( properties.getZoneUUID() );
		
		// Update the dynamic parts of this widget
		updateWidget( properties );
		
		// All composites must call initWidget() in their constructors.
		initWidget( wrapperPanel );
	}// end init()

	
	/*
	 * This method gets called when the user clicks on the entry's name.
	 */
	public void onClick( ClickEvent event )
	{
		Object	source;

		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on the entry name?
		if ( source == m_entryName )
		{
			// Yes
			// Open a window where we will view this entry.
			if ( m_viewEntryUrl != null )
			{
				int height;
				int width;
				
				width = Window.getClientWidth();
				height = Window.getClientHeight();
				Window.open( m_viewEntryUrl, "ViewEntryWnd", "height=" + String.valueOf( height ) + ",resizeable,scrollbars,width=" + String.valueOf( width ) );
			}
		}
	}// end onClick()
	
	
	/**
	 * This method gets called when the mouse was over the entry's name and has now left.
	 */
	public void onMouseOut( MouseOutEvent event )
	{
		m_entryName.removeStyleDependentName( "mouseOver" );
		m_entryName.addStyleDependentName( "mouseOut" );
	}// end onMouseOut()

	
	/**
	 * This method gets called when the mouse is over the entry's name.
	 */
	public void onMouseOver( MouseOverEvent event )
	{
		m_entryName.removeStyleDependentName( "mouseOut" );
		m_entryName.addStyleDependentName( "mouseOver" );
	}// end onMouseOver()

	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( Object props )
	{
		// Save the properties that were passed to us.
		if ( props != null && props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		
		// Get the needed information from the server.
		m_properties.getDataFromServer( new GetterCallback<Boolean>()
		{
			/**
			 * 
			 */
			public void returnValue( Boolean value )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					public void execute()
					{
						updateWidget();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );

	}// end updateWidget()
	
	
	/**
	 * 
	 */
	private void updateWidget()
	{
		String entryName;
		String binderName;

		// Update the entry name
		// Get the entry's name.
		entryName = m_properties.getEntryName();
			
		if ( entryName == null || entryName.length() == 0 )
			m_entryName.setText( "" );
		else
			m_entryName.setText( entryName );
		
		// Update the binder name
		binderName = m_properties.getBinderName();
		if ( binderName == null || binderName.length() == 0 )
			binderName = "";
		m_binderName.setText( " (" + binderName + ")" );
		
		// Update the url that is used to view the entry.
		m_viewEntryUrl =  m_properties.getViewEntryUrl();
		
		// Notify the landing page editor that this widget has been updated.
		m_lpe.notifyWidgetUpdated( this );
	}// end updateWidget()
}// end EntryDropWidget
