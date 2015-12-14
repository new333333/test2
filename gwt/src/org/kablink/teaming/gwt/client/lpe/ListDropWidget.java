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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgBoxClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author jwootton
 *
 */
public class ListDropWidget extends DropWidget
	implements HasDropZone
{
	private static ListWidgetDlgBox m_listDlgBox = null;		// For efficiency sake, we only create one dialog box.
	private ListProperties		m_properties = null;
	private FlowPanel			m_mainPanel;
	private FlexTable			m_flexTable = null;
	private Label				m_title = null;
	private DropZone			m_dropZone;
	

	/**
	 * 
	 */
	public ListDropWidget( LandingPageEditor lpe, ListConfig configData )
	{
		ListProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );

		// Create a widget for every child of this list as defined in the ListConfig data.
		addChildWidgetsFromConfig( configData );
	}// end ListDropWidget()
	
	
	/**
	 * 
	 */
	public ListDropWidget( LandingPageEditor lpe, ListProperties properties )
	{
		init( lpe, properties );
	}// end ListDropWidget()
	

	/**
	 * Create a widget for every child defined in ListConfig and add the children to
	 * this ListDropWidget.
	 */
	public void addChildWidgetsFromConfig( ListConfig configData )
	{
		if ( m_dropZone != null )
		{
			int i;

			for (i = 0; i < configData.numItems(); ++i)
			{
				DropWidget dropWidget;
				ConfigItem configItem;
				
				// Get the next piece of configuration information.
				configItem = configData.get( i );
				
				// Create the appropriate DropWidget based on the configuration data.
				dropWidget = configItem.createDropWidget( m_lpe );
				
				// Add the widget to the col's drop zone.
				m_dropZone.addWidgetToDropZone( dropWidget );
			}
		}
	}// end addChildWidgetsFromConfig()
	
	
	/**
	 * Adjust the height of all the table widgets so all the DropZones in a table are the same height.
	 */
	public int adjustHeightOfAllTableWidgets()
	{
		return m_dropZone.adjustHeightOfAllTableWidgets();
	}// end adjustHeightOfAllTableWidgets()
	
	
	/**
	 * Check to see if this widget contains the given DropZone.
	 */
	public boolean containsDropZone( DropZone dropZone )
	{
		if ( m_dropZone == dropZone )
			return true;
		
		// Check all of the widgets found in our drop zone to see if they contain the given DropZone.
		return m_dropZone.containsDropZone( dropZone );
	}
	
	
	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	public String createConfigString()
	{
		String configStr;
		ArrayList<DropWidget> childWidgets;
		DropWidget nextWidget;
		int i;
		
		// Get the configuration string for the properties of this widget.
		configStr = m_properties.createConfigString();
		
		// Get the list of widgets that are children of this widget.
		childWidgets = m_dropZone.getWidgets();
		
		// Spin through the list of child widgets and get the configuration string from each one.
		if ( childWidgets != null )
		{
			for (i = 0; i < childWidgets.size(); ++i)
			{
				String nextConfigStr;
				
				nextWidget = childWidgets.get( i );
				
				nextConfigStr = nextWidget.createConfigString(); 
				configStr += nextConfigStr;
			}
		}

		configStr += "listEnd;";
		
		return configStr;
	}// end createConfigString()
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	public DragProxy getDragProxy()
	{
		if ( m_dragProxy == null )
		{
			// Create a drag proxy that will be displayed when the user drags this item.
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorList(), GwtTeaming.getMessages().lpeList() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public void getPropertiesDlgBox( int xPos, int yPos, DlgBoxClient dBoxClient )
	{
		// Have we already created a dialog?
		if ( m_listDlgBox == null )
		{
			// Pass in the object that holds all the properties for a ListDropWidget.
			m_listDlgBox = new ListWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_listDlgBox.init( m_properties );
			m_listDlgBox.initHandlers( this, this );
		}
		
		dBoxClient.onSuccess(m_listDlgBox);
	}// end getPropertiesDlgBox()
	
	
	/**
	 * 
	 */
	public void init( LandingPageEditor lpe, ListProperties properties )
	{
		FlowPanel wrapperPanel;
		
		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );
		
		m_lpe = lpe;
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "lpeDropWidget" );
		
		// Create an object to hold all of the properties that define a list widget.
		m_properties = new ListProperties();
		
		// If we were passed some properties, make a copy of them.
		if ( properties != null )
			m_properties.copy( properties );
		
		// Create a FlexTable to hold the title and DropZone.
		m_flexTable = new FlexTable();
		m_flexTable.addStyleName( "lpeTable" );
		m_flexTable.setWidth( "100%" );
		m_flexTable.insertRow( 0 );
		
		// Add a DropZone where the user can drop widgets from the palette.
		m_dropZone = new DropZone( m_lpe, "lpeListDropZone" );
		m_dropZone.setParentDropZone( getParentDropZone() );
		m_flexTable.setWidget( 0, 0, m_dropZone );
		m_mainPanel.add( m_flexTable );
		
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
			
			m_title = new Label();
			m_title.addStyleName( "lpeListTitle" );
			panel.add( m_title );
			panel.add( ctrl );
			wrapperPanel.add( panel );
		}
		
		// Update the dynamic parts of this widget
		updateWidget( m_properties );
		
		// All composites must call initWidget() in their constructors.
		wrapperPanel.add( m_mainPanel );
		initWidget( wrapperPanel );
	}// end init()
	

	/**
	 * Set the DropZone this widget lives in.
	 */
	public void setParentDropZone( DropZone dropZone )
	{
		super.setParentDropZone( dropZone );
		
		// Tell our DropZone who its parent DropZone is.
		m_dropZone.setParentDropZone( dropZone );
	}
	
	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( Object props )
	{
		String title;
		
		// Save the properties that were passed to us.
		if ( props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		
		// Get the title.
		title = m_properties.getTitle();
		
		if ( title == null || title.length() == 0 )
			m_title.setText( "" );
		else
			m_title.setText( title );
	}// end updateWidget()
}// end ListDropWidget
