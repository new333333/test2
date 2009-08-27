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

import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * 
 * @author jwootton
 *
 */
public class ListDropWidget extends DropWidget
{
	private ListProperties		m_properties = null;
	private FlowPanel			m_mainPanel;
	private FlexTable			m_flexTable = null;
	private LandingPageEditor	m_lpe = null;
	private DropZone			m_dropZone;
	
	/**
	 * 
	 */
	public ListDropWidget( LandingPageEditor lpe, ListProperties properties )
	{
		CellFormatter cellFormatter;

		m_lpe = lpe;
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "lpeListDropWidget" );
		
		// Create an object to hold all of the properties that define a list widget.
		m_properties = new ListProperties();
		
		// If we were passed some properties, make a copy of them.
		if ( properties != null )
			m_properties.copy( properties );
		
		// Create a FlexTable to hold the title and DropZone.
		m_flexTable = new FlexTable();
		m_flexTable.setWidth( "100%" );
		m_flexTable.insertRow( 0 );
		m_flexTable.insertRow( 0 );
		cellFormatter = m_flexTable.getFlexCellFormatter();
		cellFormatter.setWordWrap( 0, 0, false );
		cellFormatter.setWidth( 0, 0, "100%" );
		
		// Add the title.
		m_flexTable.setText( 0, 0, "" );

		// Add a DropZone where the user can drop widgets from the palette.
		m_dropZone = new DropZone( m_lpe, "lpeListDropZone" );
		m_flexTable.setWidget( 1, 0, m_dropZone );
		m_mainPanel.add( m_flexTable );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainPanel );

		setStyleName( "lpeDropWidget" );
	}// end ListDropWidget()
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public DlgBox getPropertiesDlgBox( int xPos, int yPos )
	{
		DlgBox dlgBox;
		
		// Pass in the object that holds all the properties for a ListDropWidget.
		dlgBox = new ListWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		
		return dlgBox;
	}// end getPropertiesDlgBox()
	
	
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( PropertiesObj props )
	{
		String title;
		
		// Save the properties that were passed to us.
		m_properties.copy( props );
		
		// Get the title.
		title = m_properties.getTitle();
		
		if ( title == null || title.length() == 0 )
			m_flexTable.setText( 0, 0, "" );
		else
			m_flexTable.setText( 0, 0, title );
	}// end updateWidget()
}// end ListDropWidget
